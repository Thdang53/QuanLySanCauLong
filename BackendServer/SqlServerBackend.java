import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Backend HTTP Server (Port 8080) kết nối trực tiếp Microsoft SQL Server (.\SQLEXPRESS - BadmintonDB).
 * Hỗ trợ xác thực 3 Role (CUSTOMER, STAFF, ADMIN) và đồng bộ trực tiếp các thao tác Quản lý (Sân, Giá, Kho, Bảo trì).
 */
public class SqlServerBackend {

    private static final String SQL_SERVER = ".\\SQLEXPRESS";
    private static final String DB_NAME = "BadmintonDB";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);

        // API 1: Đăng nhập kiểm tra tài khoản trong bảng NGUOI_DUNG (SQL Server)
        server.createContext("/api/auth/login", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());
                String user = escapeSql(params.getOrDefault("user", ""));
                String pass = escapeSql(params.getOrDefault("pass", ""));

                String sql = "SET NOCOUNT ON; SELECT TOP 1 CONCAT('OK|', MaNguoiDung, '|', TaiKhoan, '|', SoDienThoai, '|', HoTen, '|', VaiTro) "
                        + "FROM NGUOI_DUNG WHERE (TaiKhoan = N'" + user + "' OR SoDienThoai = N'" + user + "') AND MatKhau = N'" + pass + "';";

                String result = runSqlCmd(sql);
                if (result.startsWith("OK|")) {
                    sendResponse(exchange, 200, result);
                } else {
                    sendResponse(exchange, 401, "ERR|Invalid credentials");
                }
            }
        });

        // API 2: Đăng ký tài khoản Khách hàng mới -> INSERT trực tiếp vào bảng NGUOI_DUNG
        server.createContext("/api/auth/register", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());
                String user = escapeSql(params.getOrDefault("user", ""));
                String phone = escapeSql(params.getOrDefault("phone", ""));
                String name = escapeSql(params.getOrDefault("name", ""));
                String pass = escapeSql(params.getOrDefault("pass", ""));

                if (user.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                    sendResponse(exchange, 400, "ERR|Missing fields");
                    return;
                }

                String checkSql = "SET NOCOUNT ON; SELECT COUNT(*) FROM NGUOI_DUNG WHERE TaiKhoan = N'" + user + "' OR SoDienThoai = N'" + phone + "';";
                String countStr = runSqlCmd(checkSql);
                if (!"0".equals(countStr)) {
                    sendResponse(exchange, 409, "ERR|Exists");
                    return;
                }

                String insertSql = "SET NOCOUNT ON; INSERT INTO NGUOI_DUNG (TaiKhoan, SoDienThoai, HoTen, MatKhau, VaiTro) "
                        + "VALUES (N'" + user + "', N'" + phone + "', N'" + name + "', N'" + pass + "', N'CUSTOMER'); SELECT 'OK|Registered';";
                String insResult = runSqlCmd(insertSql);
                if (insResult.contains("OK|Registered")) {
                    sendResponse(exchange, 200, "OK|Registered");
                } else {
                    sendResponse(exchange, 500, "ERR|Insert failed");
                }
            }
        });

        // API 3: Admin cập nhật giá thuê hoặc trạng thái bảo trì sân (Bảng SAN_CAU_LONG)
        server.createContext("/api/admin/update-court", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());
                int id = parseIntSafe(params.getOrDefault("id", "0"), 0);
                int price = parseIntSafe(params.getOrDefault("price", "80000"), 80000);
                String status = escapeSql(params.getOrDefault("status", "TRONG"));

                if (id <= 0) {
                    sendResponse(exchange, 400, "ERR|Invalid court id");
                    return;
                }

                String sql = "SET NOCOUNT ON; UPDATE SAN_CAU_LONG SET GiaMoiGio = " + price
                        + ", TrangThai = N'" + status + "' WHERE MaSan = " + id + "; SELECT 'OK|UpdatedCourt';";
                String res = runSqlCmd(sql);
                sendResponse(exchange, 200, res.contains("OK|") ? "OK|UpdatedCourt" : "ERR|UpdateFailed");
            }
        });

        // API 4: Admin thêm sân mới (Bảng SAN_CAU_LONG)
        server.createContext("/api/admin/add-court", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());
                String name = escapeSql(params.getOrDefault("name", "Sân mới"));
                int price = parseIntSafe(params.getOrDefault("price", "80000"), 80000);

                String sql = "SET NOCOUNT ON; INSERT INTO SAN_CAU_LONG (TenSan, MoTa, GiaMoiGio, TrangThai) "
                        + "VALUES (N'" + name + "', N'Sân tiêu chuẩn mới', " + price + ", N'TRONG'); SELECT 'OK|AddedCourt';";
                String res = runSqlCmd(sql);
                sendResponse(exchange, 200, res.contains("OK|") ? "OK|AddedCourt" : "ERR|AddFailed");
            }
        });

        // API 5: Admin cập nhật đơn giá & số lượng tồn kho dịch vụ (Bảng DICH_VU)
        server.createContext("/api/admin/update-service", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());
                int id = parseIntSafe(params.getOrDefault("id", "0"), 0);
                int price = parseIntSafe(params.getOrDefault("price", "10000"), 10000);
                int stock = parseIntSafe(params.getOrDefault("stock", "0"), 0);

                if (id <= 0) {
                    sendResponse(exchange, 400, "ERR|Invalid service id");
                    return;
                }

                String sql = "SET NOCOUNT ON; UPDATE DICH_VU SET DonGia = " + price
                        + ", SoLuongTon = " + stock + " WHERE MaDV = " + id + "; SELECT 'OK|UpdatedService';";
                String res = runSqlCmd(sql);
                sendResponse(exchange, 200, res.contains("OK|") ? "OK|UpdatedService" : "ERR|UpdateFailed");
            }
        });

        // API 6: Admin thêm mặt hàng mới vào kho dịch vụ (Bảng DICH_VU)
        server.createContext("/api/admin/add-service", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());
                String name = escapeSql(params.getOrDefault("name", "Mặt hàng mới"));
                int price = parseIntSafe(params.getOrDefault("price", "15000"), 15000);
                String unit = escapeSql(params.getOrDefault("unit", "Chai"));
                int stock = parseIntSafe(params.getOrDefault("stock", "20"), 20);

                String sql = "SET NOCOUNT ON; INSERT INTO DICH_VU (TenDV, DonGia, DonViTinh, SoLuongTon) "
                        + "VALUES (N'" + name + "', " + price + ", N'" + unit + "', " + stock + "); SELECT 'OK|AddedService';";
                String res = runSqlCmd(sql);
                sendResponse(exchange, 200, res.contains("OK|") ? "OK|AddedService" : "ERR|AddFailed");
            }
        });

        server.setExecutor(null);
        System.out.println("==================================================");
        System.out.println("SQL Server Backend is running at http://0.0.0.0:8080");
        System.out.println("Connected to: " + SQL_SERVER + " -> " + DB_NAME);
        System.out.println("==================================================");
        server.start();
    }

    private static String runSqlCmd(String query) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "sqlcmd", "-S", SQL_SERVER, "-d", DB_NAME, "-h", "-1", "-W", "-f", "65001", "-Q", query
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
            String line;
            String firstLine = "";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && firstLine.isEmpty()) {
                    firstLine = line;
                }
            }
            p.waitFor();
            return firstLine;
        } catch (Exception e) {
            return "ERR|" + e.getMessage();
        }
    }

    private static int parseIntSafe(String val, int fallback) {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return fallback;
        }
    }

    private static String escapeSql(String s) {
        return s == null ? "" : s.replace("'", "''").trim();
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String k = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String v = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                map.put(k, v);
            }
        }
        return map;
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
