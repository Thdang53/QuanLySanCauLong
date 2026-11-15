package com.example.androidapp.model;

public class DichVuKho {
    private int id;
    private String tenMon;
    private int donGia;
    private String donVi;
    private int soLuongTon;
    private String bieuTuong;

    public DichVuKho(int id, String tenMon, int donGia, String donVi, int soLuongTon, String bieuTuong) {
        this.id = id;
        this.tenMon = tenMon;
        this.donGia = donGia;
        this.donVi = donVi;
        this.soLuongTon = soLuongTon;
        this.bieuTuong = bieuTuong;
    }

    public int getId() { return id; }
    public String getTenMon() { return tenMon; }
    public int getDonGia() { return donGia; }
    public void setDonGia(int donGia) { this.donGia = donGia; }
    public String getDonVi() { return donVi; }
    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }
    public String getBieuTuong() { return bieuTuong; }
}
