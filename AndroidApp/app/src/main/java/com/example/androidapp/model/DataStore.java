package com.example.androidapp.model;

import java.util.ArrayList;

/**
 * Dữ liệu đồng bộ với 7 bảng trong SQL Server (.\SQLEXPRESS -> BadmintonDB)
 * và khớp 100% với bộ giao diện chuẩn 3 Role (Khách hàng, Nhân viên, Chủ sân).
 */
public class DataStore {
    public static ArrayList<SanCauLong> listSan = new ArrayList<>();
    public static ArrayList<LichDatSan> listLichDat = new ArrayList<>();
    public static ArrayList<DichVuKho> listDichVu = new ArrayList<>();
    public static ArrayList<NhanVienUyTin> listNhanVien = new ArrayList<>();
    public static ArrayList<ReviewItem> listReview = new ArrayList<>();

    public static int doanhThuCocQR = 96000;
    public static int doanhThuTaiQuay = 240000;
    public static int doanhThuDichVu = 85000;
    public static int[] doanhThuTuan = {620, 540, 710, 480, 890, 1250, 421};
    public static long startTimeSan3 = System.currentTimeMillis() - (29 * 60 + 20) * 1000L;
    private static int nextBookingId = 105;

    public static class ReviewItem {
        public int staffIndex;
        public String staffName;
        public String customerName;
        public int stars;
        public String comment;
        public String[] chips;
        public boolean resolved = false;
        public String adminNote = "";

        public ReviewItem(int staffIndex, String staffName, String customerName, int stars, String comment, String[] chips) {
            this.staffIndex = staffIndex;
            this.staffName = staffName;
            this.customerName = customerName;
            this.stars = stars;
            this.comment = comment;
            this.chips = chips;
        }
    }

    public static void initIfEmpty() {
        if (!listSan.isEmpty()) return;

        // 1. Bảng SAN_CAU_LONG: 6 sân cầu lông chuẩn 80.000đ/giờ (Sân 2 Đã đặt cọc 30%, Sân 3 Đang chơi, Sân 6 Bảo trì)
        listSan.add(new SanCauLong(1, "Sân 1", "Ánh sáng chuẩn thi đấu quốc tế", 80000, 4.9, "TRONG"));
        listSan.add(new SanCauLong(2, "Sân 2", "Gần quầy nước, thoáng mát", 80000, 4.8, "DA_DAT"));
        listSan.add(new SanCauLong(3, "Sân 3", "Thảm mới chống trượt cao cấp", 80000, 5.0, "DANG_CHOI"));
        listSan.add(new SanCauLong(4, "Sân 4", "Khu vực yên tĩnh, trần cao", 80000, 4.7, "TRONG"));
        listSan.add(new SanCauLong(5, "Sân 5", "Hệ thống đèn LED chống chói", 80000, 4.8, "TRONG"));
        listSan.add(new SanCauLong(6, "Sân 6", "Đang bảo trì thay lưới & vệ sinh thảm", 80000, 4.6, "BAO_TRI"));

        // 2. Bảng DAT_SAN: 3 lịch đặt chuẩn giống ảnh mẫu
        listLichDat.add(new LichDatSan(1, 2, "Sân 2", "Nguyễn Minh Khang", "0901234567",
                "19:00", "2026-11-15 • 19:00 - 21:00 (2 giờ)", 160000, 48000, 0, "DA_COC", "Nguyễn Văn Hùng", false));

        listLichDat.add(new LichDatSan(2, 3, "Sân 3", "Trần Quốc Bảo", "0987654321",
                "17:00", "2026-11-15 • 17:00 - 19:00 (2 giờ)", 160000, 48000, 20000, "DANG_CHOI", "Nguyễn Văn Hùng", false));

        listLichDat.add(new LichDatSan(3, 4, "Sân 4", "Nguyễn Minh Khang", "0901234567",
                "08:00", "2026-11-15 • 08:00 - 10:00 (2 giờ)", 160000, 48000, 15000, "HOAN_THANH", "Nguyễn Văn Hùng", false));

        // 3. Bảng DICH_VU: 4 mặt hàng nước uống & phụ kiện cầu lông trong kho
        listDichVu.add(new DichVuKho(1, "Nước suối Aquafina", 10000, "Chai", 50, "💧"));
        listDichVu.add(new DichVuKho(2, "Nước chanh muối Revive", 15000, "Chai", 40, "🍋"));
        listDichVu.add(new DichVuKho(3, "Quả cầu lông Hải Yến", 20000, "Quả", 100, "🏸"));
        listDichVu.add(new DichVuKho(4, "Thuê vợt Yonex", 30000, "Cây", 15, "🎾"));

        // 4. Bảng DANH_GIA_NHAN_VIEN: 3 nhân viên với điểm sao 4.9, 4.8, 4.2
        listNhanVien.add(new NhanVienUyTin(1, "Nguyễn Văn Hùng", "Thu ngân Ca Sáng", 98, 20,
                "Anh Hùng hỗ trợ rất nhiệt tình, sân sạch.", 5));
        listNhanVien.add(new NhanVienUyTin(2, "Trần Thị Mai", "Thu ngân Ca Chiều", 72, 15,
                "Chị Mai vui vẻ, dễ thương.", 5));
        listNhanVien.add(new NhanVienUyTin(3, "Lê Hoàng Nam", "Nhân viên Hỗ trợ Ca Tối", 42, 10,
                "Nhân viên làm thủ tục chậm, phải chờ 15 phút.", 2));

        // 5. Danh sách lời khen & góp ý chi tiết
        listReview.add(new ReviewItem(0, "Nguyễn Văn Hùng", "Minh Anh", 5,
                "Anh Hùng hỗ trợ rất nhiệt tình, sân sạch.",
                new String[]{"Phục vụ nhiệt tình", "Check-in nhanh"}));
        listReview.add(new ReviewItem(0, "Nguyễn Văn Hùng", "Đức Thịnh", 4,
                "Mọi thứ ổn, sẽ quay lại.",
                new String[]{"Sân sạch đẹp"}));
        listReview.add(new ReviewItem(1, "Trần Thị Mai", "Quốc Bảo", 5,
                "Chị Mai vui vẻ, dễ thương.",
                new String[]{"Thái độ thân thiện"}));
        listReview.add(new ReviewItem(2, "Lê Hoàng Nam", "Thu Hà", 2,
                "Nhân viên làm thủ tục chậm, phải chờ 15 phút.",
                new String[]{}));
    }

    public static int nextId() {
        return nextBookingId++;
    }

    public static int getTongDoanhThu() {
        return doanhThuCocQR + doanhThuTaiQuay + doanhThuDichVu;
    }
}
