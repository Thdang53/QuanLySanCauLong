package com.example.androidapp.model;

public class LichDatSan {
    private int id;
    private int maSan;
    private String tenSan;
    private String tenKhach;
    private String soDienThoai;
    private String gioMoc;      // VD: "18:00" (hiển thị trục timeline bên trái)
    private String khungGio;    // VD: "18:00 - 20:00 (2 giờ)"
    private int tienSan;
    private int tienCoc30;
    private int tienDichVu;
    private String trangThai;   // CHO_DUYET, DA_COC, DANG_CHOI, HOAN_THANH, DA_HUY
    private String nhanVienThu;
    private boolean daDanhGia;

    public LichDatSan(int id, int maSan, String tenSan, String tenKhach, String soDienThoai,
                      String gioMoc, String khungGio, int tienSan, int tienCoc30,
                      int tienDichVu, String trangThai, String nhanVienThu, boolean daDanhGia) {
        this.id = id;
        this.maSan = maSan;
        this.tenSan = tenSan;
        this.tenKhach = tenKhach;
        this.soDienThoai = soDienThoai;
        this.gioMoc = gioMoc;
        this.khungGio = khungGio;
        this.tienSan = tienSan;
        this.tienCoc30 = tienCoc30;
        this.tienDichVu = tienDichVu;
        this.trangThai = trangThai;
        this.nhanVienThu = nhanVienThu;
        this.daDanhGia = daDanhGia;
    }

    public int getId() { return id; }
    public int getMaSan() { return maSan; }
    public String getTenSan() { return tenSan; }
    public String getTenKhach() { return tenKhach; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getGioMoc() { return gioMoc; }
    public String getKhungGio() { return khungGio; }
    public int getTienSan() { return tienSan; }
    public int getTienCoc30() { return tienCoc30; }
    public int getTienDichVu() { return tienDichVu; }
    public void setTienDichVu(int tienDichVu) { this.tienDichVu = tienDichVu; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getNhanVienThu() { return nhanVienThu; }
    public void setNhanVienThu(String nhanVienThu) { this.nhanVienThu = nhanVienThu; }
    public boolean isDaDanhGia() { return daDanhGia; }
    public void setDaDanhGia(boolean daDanhGia) { this.daDanhGia = daDanhGia; }
}
