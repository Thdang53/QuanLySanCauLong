-- ============================================================
-- CƠ SỞ DỮ LIỆU HỆ THỐNG QUẢN LÝ SÂN CẦU LÔNG DAVIES ARENA
-- Đầy đủ 7 bảng chuẩn 3NF và dữ liệu mẫu cho cả 3 Role:
-- 1. Khách hàng (CUSTOMER) : khach    / 123456 (SĐT: 0901111111)
-- 2. Nhân viên  (STAFF)    : nhanvien / 123456 (SĐT: 0902222222)
-- 3. Quản lý    (ADMIN)    : quanly   / 123456 (SĐT: 0903333333)
-- ============================================================

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'BadmintonDB')
BEGIN
    CREATE DATABASE BadmintonDB;
END
GO

USE BadmintonDB;
GO

IF OBJECT_ID('DANH_GIA_NHAN_VIEN', 'U') IS NOT NULL DROP TABLE DANH_GIA_NHAN_VIEN;
IF OBJECT_ID('HOA_DON', 'U') IS NOT NULL DROP TABLE HOA_DON;
IF OBJECT_ID('CHI_TIET_DICH_VU', 'U') IS NOT NULL DROP TABLE CHI_TIET_DICH_VU;
IF OBJECT_ID('DAT_SAN', 'U') IS NOT NULL DROP TABLE DAT_SAN;
IF OBJECT_ID('DICH_VU', 'U') IS NOT NULL DROP TABLE DICH_VU;
IF OBJECT_ID('SAN_CAU_LONG', 'U') IS NOT NULL DROP TABLE SAN_CAU_LONG;
IF OBJECT_ID('NGUOI_DUNG', 'U') IS NOT NULL DROP TABLE NGUOI_DUNG;
GO

-- 1. Bảng NGUOI_DUNG
CREATE TABLE NGUOI_DUNG (
    MaNguoiDung INT IDENTITY(1,1) PRIMARY KEY,
    TaiKhoan NVARCHAR(50) NOT NULL UNIQUE,
    SoDienThoai NVARCHAR(20) NOT NULL UNIQUE,
    HoTen NVARCHAR(100) NOT NULL,
    MatKhau NVARCHAR(100) NOT NULL,
    VaiTro NVARCHAR(20) NOT NULL CHECK (VaiTro IN ('CUSTOMER', 'STAFF', 'ADMIN'))
);

-- 2. Bảng SAN_CAU_LONG
CREATE TABLE SAN_CAU_LONG (
    MaSan INT IDENTITY(1,1) PRIMARY KEY,
    TenSan NVARCHAR(100) NOT NULL,
    MoTa NVARCHAR(200),
    GiaMoiGio INT NOT NULL DEFAULT 80000,
    TrangThai NVARCHAR(20) NOT NULL DEFAULT 'TRONG'
);

-- 3. Bảng DAT_SAN
CREATE TABLE DAT_SAN (
    MaDatSan INT IDENTITY(101,1) PRIMARY KEY,
    MaSan INT NOT NULL FOREIGN KEY REFERENCES SAN_CAU_LONG(MaSan),
    MaNguoiDung INT NOT NULL FOREIGN KEY REFERENCES NGUOI_DUNG(MaNguoiDung),
    KhungGio NVARCHAR(50) NOT NULL,
    TienSanTamTinh INT NOT NULL DEFAULT 0,
    TienCoc30PhanTram INT NOT NULL DEFAULT 0,
    TrangThaiDatSan NVARCHAR(30) NOT NULL DEFAULT 'CHO_DUYET'
);

-- 4. Bảng DICH_VU
CREATE TABLE DICH_VU (
    MaDV INT IDENTITY(1,1) PRIMARY KEY,
    TenDV NVARCHAR(100) NOT NULL,
    DonGia INT NOT NULL,
    DonViTinh NVARCHAR(30) NOT NULL,
    SoLuongTon INT NOT NULL DEFAULT 0
);

-- 5. Bảng CHI_TIET_DICH_VU
CREATE TABLE CHI_TIET_DICH_VU (
    MaChiTiet INT IDENTITY(1,1) PRIMARY KEY,
    MaDatSan INT NOT NULL FOREIGN KEY REFERENCES DAT_SAN(MaDatSan),
    MaDV INT NOT NULL FOREIGN KEY REFERENCES DICH_VU(MaDV),
    SoLuong INT NOT NULL,
    DonGia INT NOT NULL
);

-- 6. Bảng HOA_DON
CREATE TABLE HOA_DON (
    MaHD INT IDENTITY(1,1) PRIMARY KEY,
    MaDatSan INT NOT NULL UNIQUE FOREIGN KEY REFERENCES DAT_SAN(MaDatSan),
    TienSanThucTe INT NOT NULL,
    TienDichVu INT NOT NULL DEFAULT 0,
    TienCocDaTru INT NOT NULL DEFAULT 0,
    TongThanhToan INT NOT NULL,
    ThoiGianLap DATETIME DEFAULT GETDATE(),
    NhanVienThu INT NULL FOREIGN KEY REFERENCES NGUOI_DUNG(MaNguoiDung)
);

-- 7. Bảng DANH_GIA_NHAN_VIEN
CREATE TABLE DANH_GIA_NHAN_VIEN (
    MaDanhGia INT IDENTITY(1,1) PRIMARY KEY,
    MaHD INT NOT NULL UNIQUE FOREIGN KEY REFERENCES HOA_DON(MaHD),
    MaKhachHang INT NOT NULL FOREIGN KEY REFERENCES NGUOI_DUNG(MaNguoiDung),
    MaNhanVien INT NOT NULL FOREIGN KEY REFERENCES NGUOI_DUNG(MaNguoiDung),
    SoSao INT NOT NULL CHECK (SoSao BETWEEN 1 AND 5),
    NoiDung NVARCHAR(500),
    ThoiGianDanhGia DATETIME DEFAULT GETDATE()
);
GO

-- NẠP DỮ LIỆU BẢNG 1: NGUOI_DUNG (3 Role chuẩn + tài khoản phụ để hiển thị lịch & xếp hạng)
INSERT INTO NGUOI_DUNG (TaiKhoan, SoDienThoai, HoTen, MatKhau, VaiTro)
VALUES
    (N'khach',    N'0901111111', N'Khách Hàng',    N'123456', N'CUSTOMER'),
    (N'nhanvien', N'0902222222', N'Nhân Viên',     N'123456', N'STAFF'),
    (N'quanly',   N'0903333333', N'Quản Lý Sân',   N'123456', N'ADMIN'),
    (N'tuanle',   N'0912345678', N'Lê Minh Tuấn',  N'123456', N'CUSTOMER'),
    (N'baotran',  N'0987654321', N'Trần Quốc Bảo', N'123456', N'CUSTOMER'),
    (N'maitran',  N'0904444444', N'Trần Thị Mai',  N'123456', N'STAFF'),
    (N'namle',    N'0905555555', N'Lê Hoàng Nam',  N'123456', N'STAFF');

-- NẠP DỮ LIỆU BẢNG 2: SAN_CAU_LONG (6 sân đủ 4 trạng thái màu)
INSERT INTO SAN_CAU_LONG (TenSan, MoTa, GiaMoiGio, TrangThai)
VALUES
    (N'Sân 1 • Thảm Yonex VIP',     N'Ánh sáng chuẩn thi đấu quốc tế',      80000, N'TRONG'),
    (N'Sân 2 • Thảm Yonex Pro',     N'Gần quầy nước, thoáng mát',           80000, N'DA_DAT'),
    (N'Sân 3 • Sân Thi Đấu Chuẩn',  N'Thảm mới chống trượt cao cấp',        90000, N'DANG_CHOI'),
    (N'Sân 4 • Sân Tiêu Chuẩn A',   N'Khu vực yên tĩnh, trần cao',          80000, N'TRONG'),
    (N'Sân 5 • Sân Tiêu Chuẩn B',   N'Hệ thống đèn LED chống chói',         80000, N'TRONG'),
    (N'Sân 6 • Khu Vực Cuối Sân',   N'Đang bảo trì thay lưới & vệ sinh',    75000, N'BAO_TRI');

-- NẠP DỮ LIỆU BẢNG 3: DAT_SAN (4 đơn đặt sân đủ 4 trạng thái vận hành)
INSERT INTO DAT_SAN (MaSan, MaNguoiDung, KhungGio, TienSanTamTinh, TienCoc30PhanTram, TrangThaiDatSan)
VALUES
    (1, 1, N'18:00 - 20:00 (2 giờ)', 160000, 48000, N'CHO_DUYET'),
    (2, 4, N'19:00 - 21:00 (2 giờ)', 160000, 48000, N'DA_COC'),
    (3, 5, N'17:00 - 19:00 (2 giờ)', 180000, 54000, N'DANG_CHOI'),
    (4, 1, N'08:00 - 10:00 (2 giờ)', 160000, 48000, N'HOAN_THANH');

-- NẠP DỮ LIỆU BẢNG 4: DICH_VU (5 mặt hàng kho nước & phụ kiện)
INSERT INTO DICH_VU (TenDV, DonGia, DonViTinh, SoLuongTon)
VALUES
    (N'Nước suối Aquafina',   10000, N'Chai', 50),
    (N'Nước điện giải Revive',15000, N'Chai', 40),
    (N'Trà xanh Không Độ',    15000, N'Chai', 35),
    (N'Quả cầu lông Hải Yến', 22000, N'Quả',  100),
    (N'Thuê vợt Yonex Astrox',30000, N'Cây',  15);

-- NẠP DỮ LIỆU BẢNG 5: CHI_TIET_DICH_VU
INSERT INTO CHI_TIET_DICH_VU (MaDatSan, MaDV, SoLuong, DonGia)
VALUES
    (103, 2, 1, 15000),
    (103, 1, 2, 10000),
    (104, 1, 2, 10000);

-- NẠP DỮ LIỆU BẢNG 6: HOA_DON
INSERT INTO HOA_DON (MaDatSan, TienSanThucTe, TienDichVu, TienCocDaTru, TongThanhToan, NhanVienThu)
VALUES
    (104, 160000, 20000, 48000, 132000, 2);

-- NẠP DỮ LIỆU BẢNG 7: DANH_GIA_NHAN_VIEN
INSERT INTO DANH_GIA_NHAN_VIEN (MaHD, MaKhachHang, MaNhanVien, SoSao, NoiDung)
VALUES
    (1, 1, 2, 5, N'Phục vụ rất nhiệt tình, hỗ trợ nhận sân nhanh!');
GO

SELECT COUNT(*) AS SoNguoiDung FROM NGUOI_DUNG;
SELECT COUNT(*) AS SoSan FROM SAN_CAU_LONG;
SELECT COUNT(*) AS SoDonDatSan FROM DAT_SAN;
SELECT COUNT(*) AS SoMatHangKho FROM DICH_VU;
GO
