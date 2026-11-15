package com.example.androidapp.model;

public class SanCauLong {
    private int id;
    private String tenSan;
    private String moTa;
    private int giaMoiGio;
    private double diemSao;
    private String trangThai; // TRONG, DA_DAT, DANG_CHOI, BAO_TRI

    public SanCauLong(int id, String tenSan, String moTa, int giaMoiGio, double diemSao, String trangThai) {
        this.id = id;
        this.tenSan = tenSan;
        this.moTa = moTa;
        this.giaMoiGio = giaMoiGio;
        this.diemSao = diemSao;
        this.trangThai = trangThai;
    }

    public int getId() { return id; }
    public String getTenSan() { return tenSan; }
    public void setTenSan(String tenSan) { this.tenSan = tenSan; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public int getGiaMoiGio() { return giaMoiGio; }
    public void setGiaMoiGio(int giaMoiGio) { this.giaMoiGio = giaMoiGio; }
    public double getDiemSao() { return diemSao; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
