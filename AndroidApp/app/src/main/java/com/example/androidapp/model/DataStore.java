package com.example.androidapp.model;

import java.util.ArrayList;

/**
 * Dữ liệu đồng bộ với 7 bảng trong SQL Server (.\SQLEXPRESS -> BadmintonDB)
 * và khớp 100% với bộ giao diện chuẩn 3 Role (Khách hàng, Nhân viên, Chủ sân),
 * bổ sung lịch sử doanh thu & sổ cái hóa đơn chi tiết 7 ngày (09/11/2026 - 15/11/2026).
 */
public class DataStore {
    public static ArrayList<SanCauLong> listSan = new ArrayList<>();
    public static ArrayList<LichDatSan> listLichDat = new ArrayList<>();
    public static ArrayList<DichVuKho> listDichVu = new ArrayList<>();
    public static ArrayList<NhanVienUyTin> listNhanVien = new ArrayList<>();
    public static ArrayList<ReviewItem> listReview = new ArrayList<>();
    public static ArrayList<DailyRevenueRecord> listDoanhThu7Ngay = new ArrayList<>();

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

    /**
     * Chi tiết từng hóa đơn / giao dịch phát sinh trong ngày để Chủ sân đối soát
     */
    public static class HoaDonChiTiet {
        public String maHoaDon;
        public String ngay;
        public String tenSan;
        public String khungGio;
        public String tenKhach;
        public String sdtKhach;
        public String nhanVienThu;
        public String caTruc;
        public int tienCocQR;
        public int tienThuTaiQuay;
        public int tienDichVu;
        public String chiTietDichVu;
        public String trangThai; // "HOAN_THANH" hoặc "DA_COC"

        public HoaDonChiTiet(String maHoaDon, String ngay, String tenSan, String khungGio,
                             String tenKhach, String sdtKhach, String nhanVienThu, String caTruc,
                             int tienCocQR, int tienThuTaiQuay, int tienDichVu,
                             String chiTietDichVu, String trangThai) {
            this.maHoaDon = maHoaDon;
            this.ngay = ngay;
            this.tenSan = tenSan;
            this.khungGio = khungGio;
            this.tenKhach = tenKhach;
            this.sdtKhach = sdtKhach;
            this.nhanVienThu = nhanVienThu;
            this.caTruc = caTruc;
            this.tienCocQR = tienCocQR;
            this.tienThuTaiQuay = tienThuTaiQuay;
            this.tienDichVu = tienDichVu;
            this.chiTietDichVu = chiTietDichVu;
            this.trangThai = trangThai;
        }

        public int getTongThu() {
            return tienCocQR + tienThuTaiQuay + tienDichVu;
        }
    }

    /**
     * Bản ghi doanh thu theo từng ngày thực tế (Thứ + Ngày/Tháng/Năm + Danh sách hóa đơn)
     */
    public static class DailyRevenueRecord {
        public int dayIndex;
        public String thu;          // "T2", "T3", ..., "CN"
        public String ngayNgan;     // "09/11", "10/11", ..., "15/11"
        public String ngayDayDu;    // "09/11/2026" .. "15/11/2026"
        public int tienCocQR;
        public int tienTaiQuay;
        public int tienDichVu;
        public int soDonHoanThanh;
        public String tomTatDichVuBanRa;
        public ArrayList<HoaDonChiTiet> danhSachHoaDon = new ArrayList<>();

        public DailyRevenueRecord(int dayIndex, String thu, String ngayNgan, String ngayDayDu,
                                  int tienCocQR, int tienTaiQuay, int tienDichVu,
                                  int soDonHoanThanh, String tomTatDichVuBanRa) {
            this.dayIndex = dayIndex;
            this.thu = thu;
            this.ngayNgan = ngayNgan;
            this.ngayDayDu = ngayDayDu;
            this.tienCocQR = tienCocQR;
            this.tienTaiQuay = tienTaiQuay;
            this.tienDichVu = tienDichVu;
            this.soDonHoanThanh = soDonHoanThanh;
            this.tomTatDichVuBanRa = tomTatDichVuBanRa;
        }

        public int getTongDoanhThu() {
            return tienCocQR + tienTaiQuay + tienDichVu;
        }
    }

    public static void initIfEmpty() {
        if (!listSan.isEmpty()) return;

        // 1. Bảng SAN_CAU_LONG: 6 sân cầu lông chuẩn 80.000đ/giờ
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

        // 6. Khởi tạo lịch sử doanh thu 7 ngày (09/11/2026 - 15/11/2026) khớp 100% biểu đồ
        initRevenueHistory7Days();
    }

    private static void initRevenueHistory7Days() {
        listDoanhThu7Ngay.clear();

        // Ngày 0: T2 - 09/11/2026 (Tổng 620.000đ = Cọc 144k + Quầy 336k + Dịch vụ 140k)
        DailyRevenueRecord d0 = new DailyRevenueRecord(0, "T2", "09/11", "09/11/2026",
                144000, 336000, 140000, 3,
                "💧 5 Nước suối (50k) • 🏸 3 Cầu Hải Yến (60k) • 🎾 1 Thuê vợt (30k)");
        d0.danhSachHoaDon.add(new HoaDonChiTiet("HD-0911-01", "09/11/2026", "Sân 1", "07:00 - 09:00 (2 giờ)",
                "Lê Minh Tuấn", "0912345678", "Nguyễn Văn Hùng", "Ca Sáng",
                48000, 112000, 50000, "2× Nước suối Aquafina, 1× Thuê vợt Yonex", "HOAN_THANH"));
        d0.danhSachHoaDon.add(new HoaDonChiTiet("HD-0911-02", "09/11/2026", "Sân 3", "17:00 - 19:00 (2 giờ)",
                "Phạm Gia Huy", "0938112233", "Trần Thị Mai", "Ca Chiều",
                48000, 112000, 60000, "3× Quả cầu lông Hải Yến", "HOAN_THANH"));
        d0.danhSachHoaDon.add(new HoaDonChiTiet("HD-0911-03", "09/11/2026", "Sân 4", "19:00 - 21:00 (2 giờ)",
                "Hoàng Đức Thịnh", "0977889900", "Lê Hoàng Nam", "Ca Tối",
                48000, 112000, 30000, "3× Nước suối Aquafina", "HOAN_THANH"));
        listDoanhThu7Ngay.add(d0);

        // Ngày 1: T3 - 10/11/2026 (Tổng 540.000đ = Cọc 120k + Quầy 280k + Dịch vụ 140k)
        DailyRevenueRecord d1 = new DailyRevenueRecord(1, "T3", "10/11", "10/11/2026",
                120000, 280000, 140000, 3,
                "🍋 4 Revive (60k) • 🏸 4 Cầu Hải Yến (80k)");
        d1.danhSachHoaDon.add(new HoaDonChiTiet("HD-1011-01", "10/11/2026", "Sân 2", "08:00 - 10:00 (2 giờ)",
                "Nguyễn Minh Anh", "0905123456", "Nguyễn Văn Hùng", "Ca Sáng",
                48000, 112000, 60000, "4× Nước chanh muối Revive", "HOAN_THANH"));
        d1.danhSachHoaDon.add(new HoaDonChiTiet("HD-1011-02", "10/11/2026", "Sân 5", "18:00 - 20:00 (2 giờ)",
                "Trần Quốc Bảo", "0987654321", "Trần Thị Mai", "Ca Chiều",
                48000, 112000, 40000, "2× Quả cầu lông Hải Yến", "HOAN_THANH"));
        d1.danhSachHoaDon.add(new HoaDonChiTiet("HD-1011-03", "10/11/2026", "Sân 1", "20:00 - 21:00 (1 giờ)",
                "Đặng Thu Hà", "0966554433", "Lê Hoàng Nam", "Ca Tối",
                24000, 56000, 40000, "2× Quả cầu lông Hải Yến", "HOAN_THANH"));
        listDoanhThu7Ngay.add(d1);

        // Ngày 2: T4 - 11/11/2026 (Tổng 710.000đ = Cọc 168k + Quầy 392k + Dịch vụ 150k)
        DailyRevenueRecord d2 = new DailyRevenueRecord(2, "T4", "11/11", "11/11/2026",
                168000, 392000, 150000, 4,
                "💧 6 Nước suối (60k) • 🍋 2 Revive (30k) • 🎾 2 Thuê vợt (60k)");
        d2.danhSachHoaDon.add(new HoaDonChiTiet("HD-1111-01", "11/11/2026", "Sân 1", "06:00 - 08:00 (2 giờ)",
                "Võ Thành Đạt", "0919223344", "Nguyễn Văn Hùng", "Ca Sáng",
                48000, 112000, 40000, "4× Nước suối Aquafina", "HOAN_THANH"));
        d2.danhSachHoaDon.add(new HoaDonChiTiet("HD-1111-02", "11/11/2026", "Sân 2", "09:00 - 11:00 (2 giờ)",
                "Ngô Bảo Châu", "0908776655", "Nguyễn Văn Hùng", "Ca Sáng",
                48000, 112000, 30000, "2× Nước chanh muối Revive", "HOAN_THANH"));
        d2.danhSachHoaDon.add(new HoaDonChiTiet("HD-1111-03", "11/11/2026", "Sân 3", "17:00 - 19:00 (2 giờ)",
                "Bùi Anh Quân", "0944556677", "Trần Thị Mai", "Ca Chiều",
                48000, 112000, 60000, "2× Thuê vợt Yonex", "HOAN_THANH"));
        d2.danhSachHoaDon.add(new HoaDonChiTiet("HD-1111-04", "11/11/2026", "Sân 4", "19:00 - 20:00 (1 giờ)",
                "Phan Thanh Hải", "0933445566", "Lê Hoàng Nam", "Ca Tối",
                24000, 56000, 20000, "2× Nước suối Aquafina", "HOAN_THANH"));
        listDoanhThu7Ngay.add(d2);

        // Ngày 3: T5 - 12/11/2026 (Tổng 480.000đ = Cọc 120k + Quầy 280k + Dịch vụ 80k)
        DailyRevenueRecord d3 = new DailyRevenueRecord(3, "T5", "12/11", "12/11/2026",
                120000, 280000, 80000, 2,
                "💧 4 Nước suối (40k) • 🏸 2 Cầu Hải Yến (40k)");
        d3.danhSachHoaDon.add(new HoaDonChiTiet("HD-1211-01", "12/11/2026", "Sân 2", "08:00 - 11:00 (3 giờ)",
                "CLB Cầu Lông Sao Mai", "0901998877", "Nguyễn Văn Hùng", "Ca Sáng",
                72000, 168000, 40000, "4× Nước suối Aquafina", "HOAN_THANH"));
        d3.danhSachHoaDon.add(new HoaDonChiTiet("HD-1211-02", "12/11/2026", "Sân 5", "18:00 - 20:00 (2 giờ)",
                "Lương Thế Vinh", "0972334455", "Trần Thị Mai", "Ca Chiều",
                48000, 112000, 40000, "2× Quả cầu lông Hải Yến", "HOAN_THANH"));
        listDoanhThu7Ngay.add(d3);

        // Ngày 4: T6 - 13/11/2026 (Tổng 890.000đ = Cọc 216k + Quầy 504k + Dịch vụ 170k)
        DailyRevenueRecord d4 = new DailyRevenueRecord(4, "T6", "13/11", "13/11/2026",
                216000, 504000, 170000, 4,
                "🍋 6 Revive (90k) • 🏸 4 Cầu Hải Yến (80k)");
        d4.danhSachHoaDon.add(new HoaDonChiTiet("HD-1311-01", "13/11/2026", "Sân 1", "07:00 - 09:00 (2 giờ)",
                "Nguyễn Minh Khang", "0901234567", "Nguyễn Văn Hùng", "Ca Sáng",
                48000, 112000, 45000, "3× Nước chanh muối Revive", "HOAN_THANH"));
        d4.danhSachHoaDon.add(new HoaDonChiTiet("HD-1311-02", "13/11/2026", "Sân 2", "16:00 - 18:00 (2 giờ)",
                "Đỗ Hoàng Phúc", "0918765432", "Trần Thị Mai", "Ca Chiều",
                48000, 112000, 45000, "3× Nước chanh muối Revive", "HOAN_THANH"));
        d4.danhSachHoaDon.add(new HoaDonChiTiet("HD-1311-03", "13/11/2026", "Sân 3", "18:00 - 21:00 (3 giờ)",
                "Nhóm Giao Lưu Quận 7", "0909112233", "Trần Thị Mai", "Ca Chiều",
                72000, 168000, 60000, "3× Quả cầu lông Hải Yến", "HOAN_THANH"));
        d4.danhSachHoaDon.add(new HoaDonChiTiet("HD-1311-04", "13/11/2026", "Sân 4", "19:00 - 21:00 (2 giờ)",
                "Trương Tuấn Kiệt", "0988776611", "Lê Hoàng Nam", "Ca Tối",
                48000, 112000, 20000, "1× Quả cầu lông Hải Yến", "HOAN_THANH"));
        listDoanhThu7Ngay.add(d4);

        // Ngày 5: T7 - 14/11/2026 (Tổng 1.250.000đ = Cọc 288k + Quầy 672k + Dịch vụ 290k)
        DailyRevenueRecord d5 = new DailyRevenueRecord(5, "T7", "14/11", "14/11/2026",
                288000, 672000, 290000, 5,
                "💧 8 Nước suối (80k) • 🍋 6 Revive (90k) • 🏸 6 Cầu Hải Yến (120k)");
        d5.danhSachHoaDon.add(new HoaDonChiTiet("HD-1411-01", "14/11/2026", "Sân 1", "07:00 - 10:00 (3 giờ)",
                "Giải Nội Bộ Công Ty FPT", "0903445566", "Nguyễn Văn Hùng", "Ca Sáng",
                72000, 168000, 80000, "8× Nước suối Aquafina", "HOAN_THANH"));
        d5.danhSachHoaDon.add(new HoaDonChiTiet("HD-1411-02", "14/11/2026", "Sân 2", "08:00 - 11:00 (3 giờ)",
                "Giải Nội Bộ Công Ty FPT", "0903445566", "Nguyễn Văn Hùng", "Ca Sáng",
                72000, 168000, 90000, "6× Nước chanh muối Revive", "HOAN_THANH"));
        d5.danhSachHoaDon.add(new HoaDonChiTiet("HD-1411-03", "14/11/2026", "Sân 3", "15:00 - 17:00 (2 giờ)",
                "Lê Minh Tuấn", "0912345678", "Trần Thị Mai", "Ca Chiều",
                48000, 112000, 40000, "2× Quả cầu lông Hải Yến", "HOAN_THANH"));
        d5.danhSachHoaDon.add(new HoaDonChiTiet("HD-1411-04", "14/11/2026", "Sân 4", "17:00 - 19:00 (2 giờ)",
                "Trần Quốc Bảo", "0987654321", "Trần Thị Mai", "Ca Chiều",
                48000, 112000, 40000, "2× Quả cầu lông Hải Yến", "HOAN_THANH"));
        d5.danhSachHoaDon.add(new HoaDonChiTiet("HD-1411-05", "14/11/2026", "Sân 5", "19:00 - 21:00 (2 giờ)",
                "Vũ Hoàng Long", "0971223344", "Lê Hoàng Nam", "Ca Tối",
                48000, 112000, 40000, "2× Quả cầu lông Hải Yến", "HOAN_THANH"));
        listDoanhThu7Ngay.add(d5);

        // Ngày 6 (Hôm nay): CN - 15/11/2026 (Tổng 421.000đ = Cọc 96k + Quầy 240k + Dịch vụ 85k)
        DailyRevenueRecord d6 = new DailyRevenueRecord(6, "CN", "15/11", "15/11/2026",
                doanhThuCocQR, doanhThuTaiQuay, doanhThuDichVu, 1,
                "💧 2 Nước suối (20k) • 🍋 1 Revive (15k) • 🏸 1 Cầu Hải Yến (20k) • 🎾 1 Thuê vợt (30k)");
        d6.danhSachHoaDon.add(new HoaDonChiTiet("HD-1511-01", "15/11/2026", "Sân 4", "08:00 - 10:00 (2 giờ)",
                "Nguyễn Minh Khang", "0901234567", "Nguyễn Văn Hùng", "Ca Sáng",
                48000, 112000, 15000, "1× Nước chanh muối Revive", "HOAN_THANH"));
        d6.danhSachHoaDon.add(new HoaDonChiTiet("HD-1511-02", "15/11/2026", "Sân 1", "10:00 - 12:00 (2 giờ)",
                "Khách vãng lai Ca Sáng", "0915889900", "Nguyễn Văn Hùng", "Ca Sáng",
                0, 128000, 70000, "2× Nước suối, 1× Cầu Hải Yến, 1× Thuê vợt", "HOAN_THANH"));
        d6.danhSachHoaDon.add(new HoaDonChiTiet("HD-1511-03", "15/11/2026", "Sân 2", "19:00 - 21:00 (2 giờ)",
                "Nguyễn Minh Khang", "0901234567", "Nguyễn Văn Hùng", "Ca Sáng",
                48000, 0, 0, "Chưa phát sinh dịch vụ (Đã cọc 30%)", "DA_COC"));
        listDoanhThu7Ngay.add(d6);
    }

    /**
     * Đồng bộ doanh thu hôm nay (15/11/2026) mỗi khi có giao dịch mới từ Quầy hoặc QR
     */
    public static DailyRevenueRecord getTodayRevenueRecord() {
        initIfEmpty();
        DailyRevenueRecord today = listDoanhThu7Ngay.get(6);
        today.tienCocQR = doanhThuCocQR;
        today.tienTaiQuay = doanhThuTaiQuay;
        today.tienDichVu = doanhThuDichVu;
        int done = 0;
        for (LichDatSan b : listLichDat) {
            if ("HOAN_THANH".equals(b.getTrangThai())) done++;
        }
        today.soDonHoanThanh = Math.max(done, 1);
        doanhThuTuan[6] = today.getTongDoanhThu() / 1000;
        return today;
    }

    public static int nextId() {
        return nextBookingId++;
    }

    public static int getTongDoanhThu() {
        return doanhThuCocQR + doanhThuTaiQuay + doanhThuDichVu;
    }
}
