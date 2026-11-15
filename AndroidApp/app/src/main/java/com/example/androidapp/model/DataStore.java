package com.example.androidapp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Dữ liệu đồng bộ với 7 bảng trong SQL Server (.\SQLEXPRESS -> BadmintonDB)
 * và có đầy đủ dữ liệu doanh thu + hóa đơn chi tiết xuyên suốt từ đầu tháng (01/11/2026 - 30/11/2026)
 * cùng hệ thống Xếp ca làm việc cho nhân viên.
 */
public class DataStore {
    public static ArrayList<SanCauLong> listSan = new ArrayList<>();
    public static ArrayList<LichDatSan> listLichDat = new ArrayList<>();
    public static ArrayList<DichVuKho> listDichVu = new ArrayList<>();
    public static ArrayList<NhanVienUyTin> listNhanVien = new ArrayList<>();
    public static ArrayList<ReviewItem> listReview = new ArrayList<>();

    // Dữ liệu doanh thu đầy đủ 30 ngày trong tháng 11/2026 (index 0 = ngày 01/11 .. index 29 = ngày 30/11)
    public static ArrayList<DailyRevenueRecord> listDoanhThuThang = new ArrayList<>();
    private static final Map<String, DailyRevenueRecord> customDateMap = new HashMap<>();

    // Dữ liệu phân ca làm việc 7 ngày trong tuần (T2 -> CN)
    public static ArrayList<ShiftDaySchedule> listLichPhanCa = new ArrayList<>();

    public static int doanhThuCocQR = 96000;
    public static int doanhThuTaiQuay = 240000;
    public static int doanhThuDichVu = 85000;
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
     * Lịch phân ca nhân viên theo từng thứ trong tuần (T2..CN)
     */
    public static class ShiftDaySchedule {
        public String thu;          // "T2" .. "CN"
        public String tenThuDayDu;  // "Thứ 2" .. "Chủ Nhật"
        public String nvCaSang;     // 06:00 - 12:00
        public String nvCaChieu;    // 12:00 - 17:00
        public String nvCaToi;      // 17:00 - 22:00
        public String ghiChuCa;

        public ShiftDaySchedule(String thu, String tenThuDayDu, String nvCaSang, String nvCaChieu, String nvCaToi, String ghiChuCa) {
            this.thu = thu;
            this.tenThuDayDu = tenThuDayDu;
            this.nvCaSang = nvCaSang;
            this.nvCaChieu = nvCaChieu;
            this.nvCaToi = nvCaToi;
            this.ghiChuCa = ghiChuCa;
        }
    }

    /**
     * Chi tiết từng hóa đơn phát sinh trong ngày
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
     * Bản ghi doanh thu của 1 ngày cụ thể trong tháng
     */
    public static class DailyRevenueRecord {
        public int dayOfMonth;      // 1..30
        public int month;           // 11
        public int year;            // 2026
        public String thu;          // "T2", "T3", ..., "CN"
        public String ngayNgan;     // "01/11" .. "30/11"
        public String ngayDayDu;    // "01/11/2026" .. "30/11/2026"
        public int tienCocQR;
        public int tienTaiQuay;
        public int tienDichVu;
        public int soDonHoanThanh;
        public ArrayList<HoaDonChiTiet> danhSachHoaDon = new ArrayList<>();

        public DailyRevenueRecord(int dayOfMonth, int month, int year, String thu,
                                  int tienCocQR, int tienTaiQuay, int tienDichVu, int soDonHoanThanh) {
            this.dayOfMonth = dayOfMonth;
            this.month = month;
            this.year = year;
            this.thu = thu;
            this.ngayNgan = String.format(Locale.US, "%02d/%02d", dayOfMonth, month);
            this.ngayDayDu = String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, month, year);
            this.tienCocQR = tienCocQR;
            this.tienTaiQuay = tienTaiQuay;
            this.tienDichVu = tienDichVu;
            this.soDonHoanThanh = soDonHoanThanh;
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

        // 4. Bảng DANH_GIA_NHAN_VIEN: 3 nhân viên
        listNhanVien.add(new NhanVienUyTin(1, "Nguyễn Văn Hùng", "Ca Sáng (06:00 - 12:00)", 98, 20,
                "Anh Hùng hỗ trợ rất nhiệt tình, sân sạch.", 5));
        listNhanVien.add(new NhanVienUyTin(2, "Trần Thị Mai", "Ca Chiều (12:00 - 17:00)", 72, 15,
                "Chị Mai vui vẻ, dễ thương.", 5));
        listNhanVien.add(new NhanVienUyTin(3, "Lê Hoàng Nam", "Ca Tối (17:00 - 22:00)", 42, 10,
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

        // 6. Khởi tạo lịch phân ca nhân viên 7 ngày trong tuần
        initShiftSchedules();

        // 7. Khởi tạo dữ liệu doanh thu & hóa đơn từ đầu tháng 01/11/2026 -> 30/11/2026
        initFullMonthRevenue();
    }

    private static void initShiftSchedules() {
        listLichPhanCa.clear();
        listLichPhanCa.add(new ShiftDaySchedule("T2", "Thứ Hai", "Nguyễn Văn Hùng", "Trần Thị Mai", "Lê Hoàng Nam", "Kiểm kê kho nước đầu tuần"));
        listLichPhanCa.add(new ShiftDaySchedule("T3", "Thứ Ba", "Nguyễn Văn Hùng", "Trần Thị Mai", "Lê Hoàng Nam", "Trực chuẩn giờ cao điểm tối"));
        listLichPhanCa.add(new ShiftDaySchedule("T4", "Thứ Tư", "Trần Thị Mai", "Nguyễn Văn Hùng", "Lê Hoàng Nam", "Đổi ca sáng - chiều định kỳ"));
        listLichPhanCa.add(new ShiftDaySchedule("T5", "Thứ Năm", "Nguyễn Văn Hùng", "Lê Hoàng Nam", "Trần Thị Mai", "Vệ sinh thảm Sân 1 - Sân 3"));
        listLichPhanCa.add(new ShiftDaySchedule("T6", "Thứ Sáu", "Nguyễn Văn Hùng", "Trần Thị Mai", "Lê Hoàng Nam", "Tăng cường hỗ trợ khách đặt cọc"));
        listLichPhanCa.add(new ShiftDaySchedule("T7", "Thứ Bảy", "Trần Thị Mai", "Nguyễn Văn Hùng", "Lê Hoàng Nam", "Cuối tuần đông khách - trực 100%"));
        listLichPhanCa.add(new ShiftDaySchedule("CN", "Chủ Nhật", "Nguyễn Văn Hùng", "Trần Thị Mai", "Lê Hoàng Nam", "Chốt sổ doanh thu tuần lúc 22:00"));
    }

    private static void initFullMonthRevenue() {
        listDoanhThuThang.clear();

        // Tổng doanh thu từng ngày (nghìn đồng) từ 01/11/2026 -> 30/11/2026
        // Đặc biệt ngày 09/11 -> 15/11 khớp 100% với mẫu: 620, 540, 710, 480, 890, 1250, 421
        int[] dailyTotalsK = {
                950,  // 01/11 (CN)
                580,  // 02/11 (T2)
                510,  // 03/11 (T3)
                640,  // 04/11 (T4)
                490,  // 05/11 (T5)
                780,  // 06/11 (T6)
                1120, // 07/11 (T7)
                980,  // 08/11 (CN)
                620,  // 09/11 (T2)
                540,  // 10/11 (T3)
                710,  // 11/11 (T4)
                480,  // 12/11 (T5)
                890,  // 13/11 (T6)
                1250, // 14/11 (T7)
                421,  // 15/11 (CN - Hôm nay)
                660,  // 16/11 (T2)
                590,  // 17/11 (T3)
                730,  // 18/11 (T4)
                520,  // 19/11 (T5)
                840,  // 20/11 (T6)
                1180, // 21/11 (T7)
                920,  // 22/11 (CN)
                610,  // 23/11 (T2)
                560,  // 24/11 (T3)
                690,  // 25/11 (T4)
                500,  // 26/11 (T5)
                870,  // 27/11 (T6)
                1210, // 28/11 (T7)
                960,  // 29/11 (CN)
                640   // 30/11 (T2)
        };

        String[] thuCycle = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"}; // 01/11/2026 là Chủ Nhật
        String[] sampleCustomers = {
                "Nguyễn Minh Khang", "Trần Quốc Bảo", "Lê Minh Tuấn", "Phạm Gia Huy",
                "Hoàng Đức Thịnh", "Võ Thành Đạt", "Ngô Bảo Châu", "Bùi Anh Quân",
                "Đặng Thu Hà", "Phan Thanh Hải", "Đỗ Hoàng Phúc", "Lương Thế Vinh"
        };
        String[] samplePhones = {
                "0901234567", "0987654321", "0912345678", "0938112233",
                "0977889900", "0919223344", "0908776655", "0944556677",
                "0966554433", "0933445566", "0918765432", "0972334455"
        };
        String[] sampleServices = {
                "2× Nước suối Aquafina, 1× Cầu Hải Yến",
                "2× Nước chanh muối Revive",
                "3× Quả cầu lông Hải Yến",
                "1× Thuê vợt Yonex, 2× Nước suối Aquafina",
                "4× Nước suối Aquafina"
        };

        for (int day = 1; day <= 30; day++) {
            String thu = thuCycle[(day - 1) % 7];
            int total = dailyTotalsK[day - 1] * 1000;

            if (day == 15) {
                // Ngày 15/11/2026 (Hôm nay): Khớp chính xác 96.000đ + 240.000đ + 85.000đ = 421.000đ
                DailyRevenueRecord d15 = new DailyRevenueRecord(15, 11, 2026, "CN",
                        doanhThuCocQR, doanhThuTaiQuay, doanhThuDichVu, 2);
                d15.danhSachHoaDon.add(new HoaDonChiTiet("HD-1511-01", "15/11/2026", "Sân 4", "08:00 - 10:00 (2 giờ)",
                        "Nguyễn Minh Khang", "0901234567", "Nguyễn Văn Hùng", "Ca Sáng",
                        48000, 112000, 15000, "1× Nước chanh muối Revive", "HOAN_THANH"));
                d15.danhSachHoaDon.add(new HoaDonChiTiet("HD-1511-02", "15/11/2026", "Sân 1", "10:00 - 12:00 (2 giờ)",
                        "Lê Minh Tuấn", "0912345678", "Nguyễn Văn Hùng", "Ca Sáng",
                        0, 128000, 70000, "2× Nước suối, 1× Cầu Hải Yến, 1× Thuê vợt", "HOAN_THANH"));
                d15.danhSachHoaDon.add(new HoaDonChiTiet("HD-1511-03", "15/11/2026", "Sân 2", "19:00 - 21:00 (2 giờ)",
                        "Nguyễn Minh Khang", "0901234567", "Trần Thị Mai", "Ca Chiều",
                        48000, 0, 0, "Đã cọc 30% qua VietQR", "DA_COC"));
                listDoanhThuThang.add(d15);
                continue;
            }

            // Tính toán 3 khoản thu khớp 100% tổng tiền ngày đó
            int numInvoices = (total >= 900000) ? 4 : 3;
            int cocTotal = (numInvoices * 48000);
            int quayTotal = (numInvoices * 112000);
            int dvTotal = Math.max(20000, total - cocTotal - quayTotal);
            // Điều chỉnh tiền quầy để cocTotal + quayTotal + dvTotal == total tuyệt đối
            quayTotal = total - cocTotal - dvTotal;

            DailyRevenueRecord rec = new DailyRevenueRecord(day, 11, 2026, thu,
                    cocTotal, quayTotal, dvTotal, numInvoices);

            String dateStr = String.format(Locale.US, "%02d/11/2026", day);
            String codePrefix = String.format(Locale.US, "HD-%02d11-", day);

            int remCoc = cocTotal;
            int remQuay = quayTotal;
            int remDv = dvTotal;

            for (int idx = 1; idx <= numInvoices; idx++) {
                int c = (idx == numInvoices) ? remCoc : 48000;
                int q = (idx == numInvoices) ? remQuay : (quayTotal / numInvoices);
                int dv = (idx == numInvoices) ? remDv : (dvTotal / numInvoices);
                remCoc -= c;
                remQuay -= q;
                remDv -= dv;

                int custIdx = (day * 3 + idx) % sampleCustomers.length;
                String sanName = "Sân " + (((day + idx) % 5) + 1);
                String timeSlot = (idx == 1) ? "07:00 - 09:00 (2 giờ)"
                        : (idx == 2 ? "16:00 - 18:00 (2 giờ)"
                        : (idx == 3 ? "18:00 - 20:00 (2 giờ)" : "20:00 - 22:00 (2 giờ)"));
                String cashier = (idx == 1) ? "Nguyễn Văn Hùng"
                        : (idx == 2 ? "Trần Thị Mai" : "Lê Hoàng Nam");
                String shift = (idx == 1) ? "Ca Sáng" : (idx == 2 ? "Ca Chiều" : "Ca Tối");
                String svcDesc = sampleServices[(day + idx) % sampleServices.length];

                rec.danhSachHoaDon.add(new HoaDonChiTiet(
                        codePrefix + "0" + idx,
                        dateStr,
                        sanName,
                        timeSlot,
                        sampleCustomers[custIdx],
                        samplePhones[custIdx],
                        cashier,
                        shift,
                        c, q, dv, svcDesc, "HOAN_THANH"
                ));
            }

            listDoanhThuThang.add(rec);
        }
    }

    /**
     * Lấy đúng bản ghi doanh thu của bất kỳ ngày/tháng/năm nào mà người dùng chọn (không bao giờ nhảy sai ngày)
     */
    public static DailyRevenueRecord getRevenueRecordForDate(int day, int month, int year) {
        initIfEmpty();
        // Đồng bộ ngày hôm nay 15/11/2026
        DailyRevenueRecord d15 = listDoanhThuThang.get(14);
        d15.tienCocQR = doanhThuCocQR;
        d15.tienTaiQuay = doanhThuTaiQuay;
        d15.tienDichVu = doanhThuDichVu;

        if (month == 11 && year == 2026 && day >= 1 && day <= listDoanhThuThang.size()) {
            return listDoanhThuThang.get(day - 1);
        }

        String key = String.format(Locale.US, "%02d/%02d/%04d", day, month, year);
        if (customDateMap.containsKey(key)) {
            return customDateMap.get(key);
        }

        String[] thuCycle = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        String thu = thuCycle[Math.abs(day) % 7];
        DailyRevenueRecord custom = new DailyRevenueRecord(day, month, year, thu, 96000, 224000, 80000, 2);
        String codePrefix = String.format(Locale.US, "HD-%02d%02d-", day, month);
        custom.danhSachHoaDon.add(new HoaDonChiTiet(codePrefix + "01", key, "Sân 1", "08:00 - 10:00 (2 giờ)",
                "Nguyễn Minh Khang", "0901234567", "Nguyễn Văn Hùng", "Ca Sáng",
                48000, 112000, 40000, "4× Nước suối Aquafina", "HOAN_THANH"));
        custom.danhSachHoaDon.add(new HoaDonChiTiet(codePrefix + "02", key, "Sân 3", "18:00 - 20:00 (2 giờ)",
                "Trần Quốc Bảo", "0987654321", "Trần Thị Mai", "Ca Chiều",
                48000, 112000, 40000, "2× Quả cầu lông Hải Yến", "HOAN_THANH"));
        customDateMap.put(key, custom);
        return custom;
    }

    public static DailyRevenueRecord getTodayRevenueRecord() {
        return getRevenueRecordForDate(15, 11, 2026);
    }

    public static int nextId() {
        return nextBookingId++;
    }

    public static int getTongDoanhThu() {
        return doanhThuCocQR + doanhThuTaiQuay + doanhThuDichVu;
    }
}
