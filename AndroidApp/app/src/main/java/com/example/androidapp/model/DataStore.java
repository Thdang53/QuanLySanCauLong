package com.example.androidapp.model;

import java.util.ArrayList;

/**
 * Kho dữ liệu trạng thái của ứng dụng.
 * Đã xóa toàn bộ dữ liệu test mẫu theo yêu cầu.
 */
public class DataStore {
    public static ArrayList<SanCauLong> listSan = new ArrayList<>();
    public static ArrayList<LichDatSan> listLichDat = new ArrayList<>();
    public static ArrayList<DichVuKho> listDichVu = new ArrayList<>();
    public static ArrayList<NhanVienUyTin> listNhanVien = new ArrayList<>();

    public static int doanhThuCocQR = 0;
    public static int doanhThuTaiQuay = 0;
    public static int doanhThuDichVu = 0;
    private static int nextBookingId = 1;

    public static void initIfEmpty() {
        // Đã xóa toàn bộ dữ liệu test (sân mẫu, lịch đặt mẫu, kho mẫu, đánh giá mẫu).
    }

    public static int nextId() {
        return nextBookingId++;
    }

    public static int getTongDoanhThu() {
        return doanhThuCocQR + doanhThuTaiQuay + doanhThuDichVu;
    }
}
