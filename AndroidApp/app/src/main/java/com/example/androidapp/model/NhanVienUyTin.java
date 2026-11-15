package com.example.androidapp.model;

public class NhanVienUyTin {
    private int id;
    private String hoTen;
    private String chucVu;
    private int tongSao;
    private int soLuotDanhGia;
    private String nhanXetGanNhat;
    private int saoGanNhat;

    public NhanVienUyTin(int id, String hoTen, String chucVu, int tongSao, int soLuotDanhGia, String nhanXetGanNhat, int saoGanNhat) {
        this.id = id;
        this.hoTen = hoTen;
        this.chucVu = chucVu;
        this.tongSao = tongSao;
        this.soLuotDanhGia = soLuotDanhGia;
        this.nhanXetGanNhat = nhanXetGanNhat;
        this.saoGanNhat = saoGanNhat;
    }

    public int getId() { return id; }
    public String getHoTen() { return hoTen; }
    public String getChucVu() { return chucVu; }
    public int getTongSao() { return tongSao; }
    public int getSoLuotDanhGia() { return soLuotDanhGia; }
    public String getNhanXetGanNhat() { return nhanXetGanNhat; }
    public int getSaoGanNhat() { return saoGanNhat; }

    public double getDiemTrungBinh() {
        if (soLuotDanhGia == 0) return 5.0;
        return (double) tongSao / soLuotDanhGia;
    }

    public void themDanhGia(int soSao, String nhanXet) {
        this.tongSao += soSao;
        this.soLuotDanhGia += 1;
        this.saoGanNhat = soSao;
        if (nhanXet != null && !nhanXet.isEmpty()) {
            this.nhanXetGanNhat = nhanXet;
        }
    }
}
