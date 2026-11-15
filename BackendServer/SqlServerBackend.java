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
 * Backend API Server (Java 17) kết nối trực tiếp tới Microsoft SQL Server (.\SQLEXPRESS -> BadmintonDB).
 * Cung cấp RESTful API cho ứng dụng Android Davies Arena qua địa chỉ http://10.0.2.2:8080/api/...
 */
public class SqlServerBackend {

    private static final String SQL_SERVER_INSTANCE = ".\\SQLEXPRESS";
    private static final String DATABASE_NAME = "BadmintonDB";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
        server.createContext("/api/auth/login", new LoginHandler());
        server.createContext("/api/auth/register", new RegisterHandler());
        server.setExecutor(null);
        System.out.println("==============================================================");
        System.out.println("Davies Arena Backend Server đang chạy tại port 8080");
        System.out.println("Kết nối trực tiếp CSDL SQL Server: " + SQL_SERVER_INSTANCE + " -> " + DATABASE_NAME);
        System.out.println("==============================================================");
        server.start();
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> params = parseParams(exchange);
            String user = sanitize(params.getOrDefault("user", ""));
            String pass = sanitize(params.getOrDefault("pass", ""));

            String sql = "SET NOCOUNT ON; "
                    + "SELECT CONCAT(MaNguoiDung, '|', TaiKhoan, '|', SoDienThoai, '|', HoTen, '|', VaiTro) "
                    + "FROM NGUOI_DUNG "
                    + "WHERE (LOWER(TaiKhoan) = LOWER(N'" + user + "') OR SoDienThoai = N'" + user + "') "
                    + "AND MatKhau = N'" + pass + "';";

            String output = runSqlQuery(sql).trim();
            if (!output.isEmpty() && output.contains("|")) {
                sendText(exchange, 200, "OK|" + output);
            } else {
                sendText(exchange, 401, "ERR|Sai tài khoản hoặc mật khẩu trong SQL Server!");
            }
        }
    }

    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> params = parseParams(exchange);
            String user = sanitize(params.getOrDefault("user", ""));
            String phone = sanitize(params.getOrDefault("phone", ""));
            String name = sanitize(params.getOrDefault("name", user));
            String pass = sanitize(params.getOrDefault("pass", ""));

            String checkSql = "SET NOCOUNT ON; SELECT COUNT(*) FROM NGUOI_DUNG "
                    + "WHERE LOWER(TaiKhoan) = LOWER(N'" + user + "') OR SoDienThoai = N'" + phone + "';";
            String countOut = runSqlQuery(checkSql).trim();
            if (!"0".equals(countOut)) {
                sendText(exchange, 409, "ERR|Tài khoản hoặc SĐT đã tồn tại trong SQL Server!");
                return;
            }

            String insertSql = "SET NOCOUNT ON; INSERT INTO NGUOI_DUNG (TaiKhoan, SoDienThoai, HoTen, MatKhau, VaiTro) "
                    + "VALUES (N'" + user + "', N'" + phone + "', N'" + name + "', N'" + pass + "', N'CUSTOMER'); "
                    + "SELECT 'INSERTED';";
            String res = runSqlQuery(insertSql).trim();
            if (res.contains("INSERTED")) {
                sendText(exchange, 200, "OK|Đã lưu tài khoản vào SQL Server (BadmintonDB)!");
            } else {
                sendText(exchange, 500, "ERR|Không thể ghi vào SQL Server!");
            }
        }
    }

    private static String runSqlQuery(String sql) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "sqlcmd", "-S", SQL_SERVER_INSTANCE, "-E", "-d", DATABASE_NAME,
                    "-h", "-1", "-W", "-Q", sql
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        sb.append(line.trim()).append("\n");
                    }
                }
            }
            p.waitFor();
            return sb.toString().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private static String sanitize(String input) {
        return input == null ? "" : input.replace("'", "''").trim();
    }

    private static Map<String, String> parseParams(HttpExchange exchange) throws IOException {
        Map<String, String> map = new HashMap<>();
        String query = exchange.getRequestURI().getRawQuery();
        if (query == null || query.isEmpty()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                query = br.readLine();
            }
        }
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                }
            }
        }
        return map;
    }

    private static void sendText(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
