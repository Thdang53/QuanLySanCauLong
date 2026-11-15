package com.example.androidapp.model;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Kết nối từ Android Emulator (10.0.2.2:8080) tới Backend Server & Microsoft SQL Server (.\SQLEXPRESS -> BadmintonDB).
 */
public class SqlServerHelper {

    private static final String BASE_URL = "http://10.0.2.2:8080/api/auth";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static class UserAccount {
        public final int id;
        public final String username;
        public final String phone;
        public final String fullName;
        public final String role;

        public UserAccount(int id, String username, String phone, String fullName, String role) {
            this.id = id;
            this.username = username;
            this.phone = phone;
            this.fullName = fullName;
            this.role = role;
        }
    }

    public interface AuthCallback {
        void onSuccess(UserAccount account);
        void onError(String message);
    }

    public interface RegisterCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public void authenticateAsync(final String userOrPhone, final String password, final AuthCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String q = "user=" + URLEncoder.encode(userOrPhone.trim(), "UTF-8")
                            + "&pass=" + URLEncoder.encode(password.trim(), "UTF-8");
                    URL url = new URL(BASE_URL + "/login?" + q);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3500);
                    conn.setReadTimeout(3500);

                    int code = conn.getResponseCode();
                    InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    final String resp = reader.readLine();
                    reader.close();

                    if (resp != null && resp.startsWith("OK|")) {
                        String[] parts = resp.split("\\|");
                        int id = Integer.parseInt(parts[1].trim());
                        String username = parts[2].trim();
                        String phone = parts[3].trim();
                        String fullName = normalizeDisplayName(username, parts[4].trim());
                        String role = parts[5].trim();
                        final UserAccount acc = new UserAccount(id, username, phone, fullName, role);
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(acc);
                            }
                        });
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError("Sai tài khoản hoặc mật khẩu trong SQL Server!");
                            }
                        });
                    }
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError("Lỗi kết nối SQL Server Backend (port 8080): " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private String normalizeDisplayName(String username, String rawName) {
        if ("khach".equalsIgnoreCase(username)) return "Khách Hàng";
        if ("nhanvien".equalsIgnoreCase(username)) return "Nhân Viên";
        if ("quanly".equalsIgnoreCase(username)) return "Quản Lý Sân";
        if ("tuanle".equalsIgnoreCase(username)) return "Lê Minh Tuấn";
        if ("baotran".equalsIgnoreCase(username)) return "Trần Quốc Bảo";
        if ("maitran".equalsIgnoreCase(username)) return "Trần Thị Mai";
        if ("namle".equalsIgnoreCase(username)) return "Lê Hoàng Nam";
        return rawName;
    }

    public void registerCustomerAsync(final String username, final String phone, final String fullName,
                                      final String password, final RegisterCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String q = "user=" + URLEncoder.encode(username.trim(), "UTF-8")
                            + "&phone=" + URLEncoder.encode(phone.trim(), "UTF-8")
                            + "&name=" + URLEncoder.encode(fullName.trim(), "UTF-8")
                            + "&pass=" + URLEncoder.encode(password.trim(), "UTF-8");
                    URL url = new URL(BASE_URL + "/register?" + q);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3500);
                    conn.setReadTimeout(3500);

                    int code = conn.getResponseCode();
                    InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    final String resp = reader.readLine();
                    reader.close();

                    if (resp != null && resp.startsWith("OK|")) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess("Đã lưu tài khoản vào SQL Server (BadmintonDB) thành công!");
                            }
                        });
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError("Tài khoản hoặc SĐT đã tồn tại trong SQL Server!");
                            }
                        });
                    }
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError("Lỗi kết nối SQL Server Backend: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }
}
