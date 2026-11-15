package com.example.androidapp.model;

import java.util.ArrayList;

/**
 * Dữ liệu đồng bộ với 7 bảng trong SQL Server (.\SQLEXPRESS -> BadmintonDB)
 * phục vụ test đầy đủ các chức năng và giao diện của 3 Role (Khách hàng, Nhân viên, Quản lý).
 */
public class DataStore {
    public static ArrayList<SanCauLong> listSan = new ArrayList<>();
    public static ArrayList<LichDatSan> listLichDat = new ArrayList<>();
    public static ArrayList<DichVuKho> listDichVu = new ArrayList<>();
    public static ArrayList<NhanVienUyTin> listNhanVien = new ArrayList<>();

    public static int doanhThuCocQR = 150000;
    public static int doanhThuTaiQuay = 260000;
    public static int doanhThuDichVu = 85000;
    private static int nextBookingId = 105;

    public static void initIfEmpty() {
        if (!listSan.isEmpty()) return;

        // 1. Bảng SAN_CAU_LONG: 6 sân cầu lông đủ 4 trạng thái màu (Xanh, Vàng, Đỏ, Xám)
        listSan.add(new SanCauLong(1, "Sân 1 • Thảm Yonex VIP", "Ánh sáng chuẩn thi đấu quốc tế", 80000, 4.9, "TRONG"));
        listSan.add(new SanCauLong(2, "Sân 2 • Thảm Yonex Pro", "Gần quầy nước, thoáng mát", 80000, 4.8, "DA_DAT"));
        listSan.add(new SanCauLong(3, "Sân 3 • Sân Thi Đấu Chuẩn", "Thảm mới chống trượt cao cấp", 90000, 5.0, "DANG_CHOI"));
        listSan.add(new SanCauLong(4, "Sân 4 • Sân Tiêu Chuẩn A", "Khu vực yên tĩnh, trần cao", 80000, 4.7, "TRONG"));
        listSan.add(new SanCauLong(5, "Sân 5 • Sân Tiêu Chuẩn B", "Hệ thống đèn LED chống chói", 80000, 4.8, "TRONG"));
        listSan.add(new SanCauLong(6, "Sân 6 • Khu Vực Cuối Sân", "Đang bảo trì thay lưới & vệ sinh thảm", 75000, 4.6, "BAO_TRI"));

        // 2. Bảng DAT_SAN: Đủ 4 trạng thái (CHO_DUYET, DA_COC, DANG_CHOI, HOAN_THANH) để test mọi Role
        // - Đơn 101 (CHO_DUYET): Để Nhân viên bấm duyệt cọc 30%
        listLichDat.add(new LichDatSan(101, 1, "Sân 1 • Thảm Yonex VIP", "Khách Hàng", "0901111111",
                "18:00", "18:00 - 20:00 (2 giờ)", 160000, 48000, 0, "CHO_DUYET", "Nhân Viên", false));

        // - Đơn 102 (DA_COC): Đã duyệt cọc 30%, chờ Nhân viên bấm Check-in mở sân
        listLichDat.add(new LichDatSan(102, 2, "Sân 2 • Thảm Yonex Pro", "Lê Minh Tuấn", "0912345678",
                "19:00", "19:00 - 21:00 (2 giờ)", 160000, 48000, 0, "DA_COC", "Nhân Viên", false));

        // - Đơn 103 (DANG_CHOI): Đang đánh trên sân, để Nhân viên thêm Nước/Cầu hoặc bấm Check-out
        listLichDat.add(new LichDatSan(103, 3, "Sân 3 • Sân Thi Đấu Chuẩn", "Trần Quốc Bảo", "0987654321",
                "17:00", "17:00 - 19:00 (2 giờ)", 180000, 54000, 35000, "DANG_CHOI", "Nhân Viên", false));

        // - Đơn 104 (HOAN_THANH): Đã thanh toán xong, để Khách Hàng bấm vào Chấm Sao Nhân Viên (RatingBar)
        listLichDat.add(new LichDatSan(104, 4, "Sân 4 • Sân Tiêu Chuẩn A", "Khách Hàng", "0901111111",
                "08:00", "08:00 - 10:00 (2 giờ)", 160000, 48000, 20000, "HOAN_THANH", "Nhân Viên", false));

        // 3. Bảng DICH_VU: 5 mặt hàng nước uống & phụ kiện cầu lông trong kho
        listDichVu.add(new DichVuKho(1, "Nước suối Aquafina", 10000, "Chai", 50, "💧"));
        listDichVu.add(new DichVuKho(2, "Nước điện giải Revive", 15000, "Chai", 40, "🍋"));
        listDichVu.add(new DichVuKho(3, "Trà xanh Không Độ", 15000, "Chai", 35, "🍵"));
        listDichVu.add(new DichVuKho(4, "Quả cầu lông Hải Yến", 22000, "Quả", 100, "🏸"));
        listDichVu.add(new DichVuKho(5, "Thuê vợt Yonex Astrox", 30000, "Cây", 15, "🎾"));

        // 4. Bảng DANH_GIA_NHAN_VIEN: Bảng xếp hạng uy tín nhân viên (có cảnh báo đỏ <= 2 sao cho Quản lý)
        listNhanVien.add(new NhanVienUyTin(1, "Nhân Viên", "Thu ngân Ca Sáng", 98, 20,
                "Phục vụ rất nhiệt tình, hỗ trợ nhận sân nhanh!", 5));
        listNhanVien.add(new NhanVienUyTin(2, "Trần Thị Mai", "Thu ngân Ca Chiều", 72, 15,
                "Check-in đúng giờ, quầy nước chu đáo.", 5));
        listNhanVien.add(new NhanVienUyTin(3, "Lê Hoàng Nam", "Nhân viên Hỗ trợ Ca Tối", 42, 10,
                "Thủ tục thanh toán hơi chậm lúc đông khách.", 2));
    }

    public static int nextId() {
        return nextBookingId++;
    }

    public static int getTongDoanhThu() {
        return doanhThuCocQR + doanhThuTaiQuay + doanhThuDichVu;
    }
}
