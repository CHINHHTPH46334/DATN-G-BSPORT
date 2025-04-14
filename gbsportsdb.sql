CREATE DATABASE GBSports
GO
USE GBSports
GO

-- 1. Bảng roles
CREATE TABLE roles (
    id_roles INT PRIMARY KEY,
    ma_roles VARCHAR(50),
    ten_roles NVARCHAR(200)
);

-- 2. Bảng tai_khoan
CREATE TABLE tai_khoan (
    id_tai_khoan INT IDENTITY(1,1) PRIMARY KEY,
    id_roles INT REFERENCES roles(id_roles),
    ten_dang_nhap VARCHAR(100),
    mat_khau VARCHAR(200)
);

-- 3. Bảng lich_su_dang_nhap
CREATE TABLE lich_su_dang_nhap (
    id_lich_su_dang_nhap INT IDENTITY(1,1) PRIMARY KEY,
    id_tai_khoan INT REFERENCES tai_khoan(id_tai_khoan),
    ngay_dang_nhap DATETIME,
    ip_adress VARCHAR(200)
);

-- 4. Bảng nhan_vien
CREATE TABLE nhan_vien (
    id_nhan_vien INT IDENTITY(1,1) PRIMARY KEY,
    id_tai_khoan INT REFERENCES tai_khoan(id_tai_khoan),
    ma_nhan_vien VARCHAR(50),
    ten_nhan_vien NVARCHAR(200),
    ngay_sinh DATE,
    email VARCHAR(200),
    dia_chi_lien_he NVARCHAR(200),
    gioi_tinh BIT,
    so_dien_thoai VARCHAR(20),
    anh_nhan_vien VARCHAR(200),
    ngay_tham_gia DATE,
    trang_thai NVARCHAR(50)
);

-- 5. Bảng danh_muc_san_pham
CREATE TABLE danh_muc_san_pham (
    id_danh_muc INT IDENTITY(1,1) PRIMARY KEY,
    ma_danh_muc VARCHAR(50),
    ten_danh_muc NVARCHAR(200),
    trang_thai NVARCHAR(50),
    ngay_tao DATETIME,
    ngay_sua DATETIME
);

-- 6. Bảng chat_lieu
CREATE TABLE chat_lieu (
    id_chat_lieu INT IDENTITY(1,1) PRIMARY KEY,
    ma_chat_lieu VARCHAR(50),
    ten_chat_lieu NVARCHAR(200),
	ngay_tao DATETIME,
	ngay_sua DATETIME,
	trang_thai NVARCHAR(50),
);

-- 7. Bảng thuong_hieu
CREATE TABLE thuong_hieu (
    id_thuong_hieu INT IDENTITY(1,1) PRIMARY KEY,
    ma_thuong_hieu VARCHAR(50),
    ten_thuong_hieu NVARCHAR(200),
    trang_thai NVARCHAR(50),
    ngay_tao DATETIME,
    ngay_sua DATETIME
);

-- 8. Bảng san_pham
CREATE TABLE san_pham (
    id_san_pham INT IDENTITY(1,1) PRIMARY KEY,
    id_danh_muc INT REFERENCES danh_muc_san_pham(id_danh_muc),
    ma_san_pham VARCHAR(50),
    ten_san_pham NVARCHAR(200),
    mo_ta NVARCHAR(250),
    trang_thai NVARCHAR(50),
    id_chat_lieu INT REFERENCES chat_lieu(id_chat_lieu),
    id_thuong_hieu INT REFERENCES thuong_hieu(id_thuong_hieu),
    hinh_anh VARCHAR(200)
);

-- 9. Bảng khuyen_mai
CREATE TABLE khuyen_mai (
    id_khuyen_mai INT IDENTITY(1,1) PRIMARY KEY,
    ma_khuyen_mai VARCHAR(50),
    ten_khuyen_mai NVARCHAR(200),
    mo_ta NVARCHAR(250),
    ngay_bat_dau DATETIME,
    ngay_het_han DATETIME,
    gia_tri_toi_da DECIMAL(12,2),
    gia_tri_giam FLOAT,
    kieu_giam_gia NVARCHAR(50),
    trang_thai NVARCHAR(50)
);

-- 10. Bảng kich_thuoc
CREATE TABLE kich_thuoc (
    id_kich_thuoc INT IDENTITY(1,1) PRIMARY KEY,
    ma_kich_thuoc VARCHAR(50),
    gia_tri VARCHAR(200),
    don_vi NVARCHAR(20),
	trang_thai NVARCHAR(50)
);

-- 11. Bảng mau_sac
CREATE TABLE mau_sac (
    id_mau_sac INT IDENTITY(1,1) PRIMARY KEY,
    ma_mau_sac VARCHAR(50),
    ten_mau_sac NVARCHAR(50),
	trang_thai NVARCHAR(50)
);

-- 12. Bảng chi_tiet_san_pham
CREATE TABLE chi_tiet_san_pham (
    id_chi_tiet_san_pham INT IDENTITY(1,1) PRIMARY KEY,
    id_san_pham INT REFERENCES san_pham(id_san_pham),
    qr_code VARCHAR(200),
    gia_ban DECIMAL(12,2),
    so_luong INT,
    trang_thai NVARCHAR(50),
    ngay_tao DATETIME,
    ngay_sua DATETIME,
    id_kich_thuoc INT REFERENCES kich_thuoc(id_kich_thuoc),
    id_mau_sac INT REFERENCES mau_sac(id_mau_sac)
);

-- 13. Bảng hinh_anh
CREATE TABLE hinh_anh (
    id_hinh_anh INT IDENTITY(1,1) PRIMARY KEY,
    id_chi_tiet_san_pham INT REFERENCES chi_tiet_san_pham(id_chi_tiet_san_pham),
    anh_chinh BIT,
    hinh_anh VARCHAR(200)
);

-- 14. Bảng chi_tiet_khuyen_mai
CREATE TABLE chi_tiet_khuyen_mai (
	id_ctkm INT IDENTITY(1,1) PRIMARY KEY,
    id_khuyen_mai INT REFERENCES khuyen_mai(id_khuyen_mai),
    id_chi_tiet_san_pham INT REFERENCES chi_tiet_san_pham(id_chi_tiet_san_pham),
    gia_sau_giam DECIMAL(12,2)
);

-- 15. Bảng khach_hang
CREATE TABLE khach_hang (
    id_khach_hang INT IDENTITY(1,1) PRIMARY KEY,
    ma_khach_hang VARCHAR(50),
    ten_khach_hang NVARCHAR(200),
    gioi_tinh BIT,
    so_dien_thoai VARCHAR(20),
    ngay_sinh DATE,
    email VARCHAR(200),
    id_tai_khoan INT REFERENCES tai_khoan(id_tai_khoan),
    trang_thai NVARCHAR(50)
);

-- 16. Bảng gio_hang
CREATE TABLE gio_hang (
    id_gio_hang INT IDENTITY(1,1) PRIMARY KEY,
    id_khach_hang INT REFERENCES khach_hang(id_khach_hang)
);

-- 17. Bảng chi_tiet_gio_hang
CREATE TABLE chi_tiet_gio_hang (
	id_chi_tiet_gio_hang INT IDENTITY(1,1) PRIMARY KEY,
    id_gio_hang INT REFERENCES gio_hang(id_gio_hang),
    id_chi_tiet_san_pham INT REFERENCES chi_tiet_san_pham(id_chi_tiet_san_pham),
    so_luong INT
    
);

-- 18. Bảng binh_luan
CREATE TABLE binh_luan (
    id_khach_hang INT REFERENCES khach_hang(id_khach_hang),
    id_chi_tiet_san_pham INT REFERENCES chi_tiet_san_pham(id_chi_tiet_san_pham),
    binh_luan NVARCHAR(250),
    danh_gia FLOAT,
    ngay_binh_luan DATETIME,
    ngay_sua DATETIME,
    PRIMARY KEY (id_khach_hang, id_chi_tiet_san_pham)
);

-- 19. Bảng danh_sach_yeu_thich
CREATE TABLE danh_sach_yeu_thich (
    id_khach_hang INT REFERENCES khach_hang(id_khach_hang),
    id_chi_tiet_san_pham INT REFERENCES chi_tiet_san_pham(id_chi_tiet_san_pham),
    ngay_them DATETIME,
    PRIMARY KEY (id_khach_hang, id_chi_tiet_san_pham)
);

-- 20. Bảng dia_chi_khach_hang
CREATE TABLE dia_chi_khach_hang (
    id_dia_chi_khach_hang INT IDENTITY(1,1) PRIMARY KEY,
    id_khach_hang INT NOT NULL,
	so_nha NVARCHAR(255),
    xa_phuong NVARCHAR(255),
    quan_huyen NVARCHAR(255),
    tinh_thanh_pho NVARCHAR(255),
	dia_chi_mac_dinh BIT DEFAULT 0,  -- Cột mới được thêm, kiểu BIT với giá trị mặc định là 0
    FOREIGN KEY (id_khach_hang) REFERENCES khach_hang(id_khach_hang)
);

-- 21. Bảng voucher
CREATE TABLE voucher (
    id_voucher INT IDENTITY(1,1) PRIMARY KEY,
    ma_voucher VARCHAR(50),
    ten_voucher NVARCHAR(200),
    ngay_tao DATETIME,
    ngay_het_han DATETIME,
    gia_tri_giam DECIMAL(12,2),
    gia_tri_toi_thieu DECIMAL(12,2),
    trang_thai NVARCHAR(50),
    so_luong INT,
    kieu_giam_gia NVARCHAR(20),
    mo_ta NVARCHAR(250),
    gia_tri_toi_da DECIMAL(12,2)
);

-- 22. Bảng hoa_don
CREATE TABLE hoa_don (
    id_hoa_don INT IDENTITY(1,1) PRIMARY KEY,
    ma_hoa_don VARCHAR(50),
    id_nhan_vien INT REFERENCES nhan_vien(id_nhan_vien),
    id_khach_hang INT REFERENCES khach_hang(id_khach_hang),
    ngay_tao DATETIME,
    ngay_sua DATETIME,
    trang_thai NVARCHAR(50),
    id_voucher INT REFERENCES voucher(id_voucher),
    sdt_nguoi_nhan VARCHAR(20),
    dia_chi NVARCHAR(250),
    email VARCHAR(200),
    tong_tien_truoc_giam DECIMAL(12,2),
    phi_van_chuyen DECIMAL(12,2),
    ho_ten NVARCHAR(250),
    tong_tien_sau_giam DECIMAL(12,2),
    hinh_thuc_thanh_toan NVARCHAR(50),
    phuong_thuc_nhan_hang NVARCHAR(100),
	loai_hoa_don NVARCHAR(20),
	ghi_chu NVARCHAR(255)
);

-- 23. Bảng hoa_don_chi_tiet
CREATE TABLE hoa_don_chi_tiet (
    id_hoa_don_chi_tiet INT IDENTITY(1,1),
    id_hoa_don INT REFERENCES hoa_don(id_hoa_don),
    id_chi_tiet_san_pham INT REFERENCES chi_tiet_san_pham(id_chi_tiet_san_pham),
    so_luong INT,
    don_gia DECIMAL(12,2),
    PRIMARY KEY (id_hoa_don_chi_tiet, id_hoa_don, id_chi_tiet_san_pham)
);

-- 24. Bảng theo_doi_don_hang
CREATE TABLE theo_doi_don_hang (
    id_don_hang INT IDENTITY(1,1) PRIMARY KEY,
    id_hoa_don INT REFERENCES hoa_don(id_hoa_don),
    trang_thai NVARCHAR(50),
    ngay_chuyen DATETIME,
	nhan_vien_doi NVARCHAR(255),
	noi_dung_doi NVARCHAR(255)
);

-- 25. Bảng yeu_cau_doi_hang
CREATE TABLE yeu_cau_doi_hang (
    id_yeu_cau INT IDENTITY(1,1) PRIMARY KEY,
    ma_yeu_cau VARCHAR(50),
    id_hoa_don INT REFERENCES hoa_don(id_hoa_don),
    id_san_pham_moi INT REFERENCES chi_tiet_san_pham(id_chi_tiet_san_pham),
    trang_thai_san_pham NVARCHAR(100),
    ngay_yeu_cau DATETIME,
    ngay_xu_ly DATETIME,
    ly_do_doi NVARCHAR(200)
);

-- 26. Bảng hinh_anh_doi_hang
CREATE TABLE hinh_anh_doi_hang (
    id_hinh_anh_doi INT IDENTITY(1,1) PRIMARY KEY,
    id_yeu_cau INT REFERENCES yeu_cau_doi_hang(id_yeu_cau),
    hinh_anh VARCHAR(250)
);

-- 27. Bảng phieu_doi_hang
CREATE TABLE phieu_doi_hang (
    id_phieu_doi INT IDENTITY(1,1) PRIMARY KEY,
    id_yeu_cau INT REFERENCES yeu_cau_doi_hang(id_yeu_cau),
    ma_phieu_doi VARCHAR(50),
    phuong_thuc_thanh_toan NVARCHAR(50),
    ngay_xuat_phieu DATETIME,
    trang_thai NVARCHAR(50),
    gia_tri_chenh_lech DECIMAL(12,2)
);

-- Chèn dữ liệu mẫu
-- 1. Bảng roles
INSERT INTO roles (id_roles, ma_roles, ten_roles) VALUES
(1, 'ROLE_ADMIN', N'Admin'),
(2, 'ROLE_QL', N'Quản lý'),
(3, 'ROLE_NV', N'Nhân viên'),
(4, 'ROLE_KH', N'Khách hàng');

-- 2. Bảng tai_khoan
INSERT INTO tai_khoan (id_roles, ten_dang_nhap, mat_khau) VALUES
(1, 'thuptqph46287@fpt.edu.vn', '$2a$10$isBXW0aHXbeM4MlGRzBMe.aTZGuBYMM7aVWp0q6ss.RGDf0HDGUN2'), --MK: thu123
(1, 'nghianhph46340@fpt.edu.vn', '$2a$10$YMSAVjBltC6FlCow9fw1muCSCsGVgoE.bi.AEa5C9BBwnmhs5GA56'), --MK: nghia123
(1, 'chinhhtph46334@fpt.edu.vn', '$2a$10$NRzZ3AO62Gf5yVLcumhvyeuWpWKU9nW0s6nNboXD9IKa7.hj6OAJO'), --MK: chinh123
(1, 'dunghbph46428@fpt.edu.vn', '$2a$10$rviQ5PIc4TJj.HBFkjqW/eXwa4kYnOz./tGGSOUaoRD/9roUXfY36'), --MK: dung123
(1, 'Lenhpvph46331@fpt.edu.vn', '$2a$10$kkcLL1T7wjYw.pCEynqgcO5pXLrhYtc1R2O0FnG3GKxWa12k2QJRq'), --MK: lenh123
(1, 'huyvtph46307@fpt.edu.vn', '$2a$10$fCOqAb1zAPLNBmQE9XAoZepHC5sUOsxKDUf.M5QhroFkSUqWpwwvG'), --MK: huy123
(2, 'quanly1', 'ql123'),        
(2, 'quanly2', 'ql456'),        
(3, 'nhanvien1', 'nv123'),         
(3, 'nhanvien2', 'nv456'),        
(3, 'nhanvien3', 'nv789'),         
(3, 'nhanvien4', 'nv101'),         
(3, 'nhanvien5', 'nv112'),        
(3, 'nhanvien6', 'nv131'),        
(3, 'nhanvien7', 'nv415'),         
(4, 'customer1', 'cust123'),
(4, 'customer2', 'cust456'),
(4, 'customer3', 'cust789'),
(4, 'customer4', 'cust101'),
(4, 'customer5', 'cust112'),
(4, 'customer6', 'cust131'),
(4, 'customer7', 'cust415'),
(4, 'customer8', 'cust161'),
(4, 'customer9', 'cust718'),
(4, 'customer10', 'cust192');

-- 3. Bảng lich_su_dang_nhap
INSERT INTO lich_su_dang_nhap (id_tai_khoan, ngay_dang_nhap, ip_adress) VALUES
(1, '2025-03-01 08:00:00', '192.168.1.1'),
(2, '2025-03-02 09:00:00', '192.168.1.2'),
(3, '2025-03-03 10:00:00', '192.168.1.3'),
(4, '2025-03-04 11:00:00', '192.168.1.4'),
(5, '2025-03-05 12:00:00', '192.168.1.5'),
(6, '2025-03-06 13:00:00', '192.168.1.6'),
(7, '2025-03-07 14:00:00', '192.168.1.7'),
(8, '2025-03-08 15:00:00', '192.168.1.8'),
(9, '2025-03-09 16:00:00', '192.168.1.9'),
(10, '2025-03-10 17:00:00', '192.168.1.10');

-- 4. Bảng nhan_vien
INSERT INTO nhan_vien (id_tai_khoan, ma_nhan_vien, ten_nhan_vien, ngay_sinh, email, dia_chi_lien_he, gioi_tinh, so_dien_thoai, anh_nhan_vien, ngay_tham_gia, trang_thai) VALUES
(1, 'NV02', N'Phạm Thị Quỳnh Thu', '2002-12-13', 'thuptqph46287@fpt.edu.vn', N'Số 5, Xã Vân Canh, Huyện Hoài Đức, Thành phố Hà Nội', 1, '0989864737', 'avatar3.jpg', '2023-03-01', N'Đang hoạt động'),
(2, 'NV01', N'Nguyễn Hữu Nghĩa', '2000-10-17', 'nghianhph46340@fpt.edu.vn', N'Số 7, Phường Phương Canh, Quận Nam Từ Liêm, Thành phố Hà Nội', 1, '0353225292', 'avatar2.jpg', '2023-02-01', N'Đang hoạt động'),
(3, 'NV02', N'Hoàng Thọ Chính', '2004-12-13', 'chinhhtph46334@fpt.edu.vn', N'Số 5, Xã Vân Canh, Huyện Hoài Đức, Thành phố Hà Nội', 1, '0989864737', 'avatar3.jpg', '2023-03-01', N'Đang hoạt động'),
(4, 'NV05', N'Hồ Bá Dũng', '2004-11-14', 'dunghbph46428@fpt.edu.vn', N'Số 7 ngõ 324, Phường Láng Thượng, Quận Đống Đa, Thành phố Hà Nội', 1, '0397572262', 'avatar6.jpg', '2023-06-01', N'Đang hoạt động'),
(5, 'NV06', N'Phùn Văn Lềnh', '2003-09-10', 'lenhpvph46331@fpt.edu.vn', N'Số 4 ngõ 45, Phường Cổ Nhuế 2, Quận Bắc Từ Liêm, Thành phố Hà Nội', 1, '0388109763', 'avatar7.jpg', '2023-07-01', N'Đang hoạt động'),
(6, 'NV04', N'Vũ Tuấn Huy', '2004-02-15', 'huyvtph46307@fpt.edu.vn', N'Số 26, Phường Đội Cấn, Quận Ba Đình, Thành phố Hà Nội', 1, '0912301363', 'avatar5.jpg', '2023-05-01', N'Đang hoạt động');

-- 5. Bảng danh_muc_san_pham
INSERT INTO danh_muc_san_pham (ma_danh_muc, ten_danh_muc, trang_thai, ngay_tao, ngay_sua) VALUES
('DM001', N'Thời trang nam', N'Hoạt động', '2023-01-01', '2023-01-01'),
('DM002', N'Thời trang nữ', N'Hoạt động', '2023-02-01', '2023-02-01'),
('DM003', N'Phụ kiện', N'Hoạt động', '2023-03-01', '2023-03-01'),
('DM004', N'Giày dép', N'Hoạt động', '2023-04-01', '2023-04-01'),
('DM005', N'Túi xách', N'Hoạt động', '2023-05-01', '2023-05-01'),
('DM006', N'Đồng hồ', N'Hoạt động', '2023-06-01', '2023-06-01'),
('DM007', N'Mỹ phẩm', N'Hoạt động', '2023-07-01', '2023-07-01'),
('DM008', N'Đồ trẻ em', N'Hoạt động', '2023-08-01', '2023-08-01'),
('DM009', N'Thể thao', N'Hoạt động', '2023-09-01', '2023-09-01'),
('DM010', N'Giải trí', N'Hoạt động', '2023-10-01', '2023-10-01');

-- 6. Bảng chat_lieu
INSERT INTO chat_lieu (ma_chat_lieu, ten_chat_lieu, ngay_tao, ngay_sua, trang_thai) VALUES
('CL001', N'Cotton', '2023-01-01', '2023-01-01', N'Hoạt động'),
('CL002', N'Len', '2023-01-01', '2023-01-01', N'Hoạt động'),
('CL003', N'Polyester', '2023-01-01', '2023-01-01', N'Hoạt động'),
('CL004', N'Vải Jeans', '2023-01-01', '2023-01-01', N'Hoạt động'),
('CL005', N'Siêu nhẹ', '2023-01-01', '2023-01-01', N'Hoạt động'),
('CL006', N'Chất liệu cao cấp', '2023-01-01', '2023-01-01', N'Hoạt động'),
('CL007', N'Nỉ', '2023-01-01', '2023-01-01', N'Hoạt động'),
('CL008', N'Vải lụa', '2023-01-01', '2023-01-01', N'Hoạt động'),
('CL009', N'Vải dù', '2023-01-01', '2023-01-01', N'Hoạt động'),
('CL010', N'Vải tổng hợp', '2023-01-01', '2023-01-01', N'Hoạt động');

-- 7. Bảng thuong_hieu
INSERT INTO thuong_hieu (ma_thuong_hieu, ten_thuong_hieu, trang_thai, ngay_tao, ngay_sua) VALUES
('TH001', N'Adidas', N'Hoạt động', '2023-01-01', '2023-01-01'),
('TH002', N'Nike', N'Hoạt động', '2023-02-01', '2023-02-01'),
('TH003', N'Puma', N'Hoạt động', '2023-03-01', '2023-03-01'),
('TH004', N'Reebok', N'Hoạt động', '2023-04-01', '2023-04-01'),
('TH005', N'Converse', N'Hoạt động', '2023-05-01', '2023-05-01'),
('TH006', N'New Balance', N'Hoạt động', '2023-06-01', '2023-06-01'),
('TH007', N'Under Armour', N'Hoạt động', '2023-07-01', '2023-07-01'),
('TH008', N'Asics', N'Hoạt động', '2023-08-01', '2023-08-01'),
('TH009', N'Vans', N'Hoạt động', '2023-09-01', '2023-09-01'),
('TH010', N'Fila', N'Hoạt động', '2023-10-01', '2023-10-01');

-- 8. Bảng san_pham
INSERT INTO san_pham (id_danh_muc, ma_san_pham, ten_san_pham, mo_ta, trang_thai, id_chat_lieu, id_thuong_hieu, hinh_anh) VALUES
(1, 'SP001', N'Áo thun nam', N'Áo thun nam cao cấp', N'Hoạt động', 1, 1, 'sp1.jpg'),
(2, 'SP002', N'Váy nữ', N'Váy nữ thời trang', N'Hoạt động', 2, 2, 'sp2.jpg'),
(3, 'SP003', N'Mũ lưỡi trai', N'Mũ thời trang', N'Hoạt động', 3, 3, 'sp3.jpg'),
(4, 'SP004', N'Giày thể thao', N'Giày thể thao cao cấp', N'Hoạt động', 4, 4, 'sp4.jpg'),
(5, 'SP005', N'Túi đeo chéo', N'Túi thời trang', N'Hoạt động', 5, 5, 'sp5.jpg'),
(6, 'SP006', N'Đồng hồ nam', N'Đồng hồ cao cấp', N'Hoạt động', 6, 6, 'sp6.jpg'),
(7, 'SP007', N'Son môi', N'Son môi thời trang', N'Hoạt động', 7, 7, 'sp7.jpg'),
(8, 'SP008', N'Áo trẻ em', N'Áo trẻ em xinh xắn', N'Hoạt động', 8, 8, 'sp8.jpg'),
(9, 'SP009', N'Quần thể thao', N'Quần thể thao cao cấp', N'Hoạt động', 9, 9, 'sp9.jpg'),
(10, 'SP010', N'Tai nghe', N'Tai nghe giải trí', N'Hoạt động', 10, 10, 'sp10.jpg');

-- 9. Bảng khuyen_mai
INSERT INTO khuyen_mai (ma_khuyen_mai, ten_khuyen_mai, mo_ta, ngay_bat_dau, ngay_het_han, gia_tri_toi_da, gia_tri_giam, kieu_giam_gia, trang_thai) VALUES
('KM001', N'Giảm giá Tết', N'Giảm 20% cho đơn trên 500K', '2025-01-01', '2025-02-15', 100000, 20, N'Phần trăm', N'Đang diễn ra'),
('KM002', N'Flash Sale', N'Giảm 100K cho đơn từ 300K', '2025-03-10', '2025-03-20', 100000, 100000, N'Tiền mặt', N'Sắp diễn ra'),
('KM003', N'Mừng sinh nhật', N'Giảm 15% tối đa 50K', '2025-05-01', '2025-05-07', 50000, 15, N'Phần trăm', N'Sắp diễn ra'),
('KM004', N'Khuyến mãi hè', N'Giảm giá 50K đơn từ 400K', '2025-06-01', '2025-06-30', 50000, 50000, N'Tiền mặt', N'Sắp diễn ra'),
('KM005', N'Black Friday', N'Giảm 30% tối đa 200K', '2024-11-20', '2024-11-30', 200000, 30, N'Phần trăm', N'Đã kết thúc'),
('KM006', N'Giảm giá mùa đông', N'Giảm 10% đơn trên 300K', '2025-12-01', '2025-12-31', 50000, 10, N'Phần trăm', N'Sắp diễn ra'),
('KM007', N'Khuyến mãi VIP', N'Giảm 200K cho đơn từ 1M', '2025-02-01', '2025-02-28', 200000, 200000, N'Tiền mặt', N'Đang diễn ra'),
('KM008', N'Mua 2 tặng 1', N'Giảm 50% sản phẩm thứ 2', '2025-04-01', '2025-04-15', 100000, 50, N'Phần trăm', N'Sắp diễn ra'),
('KM009', N'Khuyến mãi khai trương', N'Giảm 100K đơn từ 500K', '2025-03-01', '2025-03-31', 100000, 100000, N'Tiền mặt', N'Đang diễn ra'),
('KM010', N'Giảm giá cuối năm', N'Giảm 25% tối đa 150K', '2025-12-15', '2025-12-31', 150000, 25, N'Phần trăm', N'Sắp diễn ra');

-- 10. Bảng kich_thuoc
INSERT INTO kich_thuoc (ma_kich_thuoc, gia_tri, don_vi, trang_thai) VALUES
('KT001', 'S', '', N'Hoạt động'),
('KT002', 'M', '', N'Hoạt động'),
('KT003', 'L', '', N'Hoạt động'),
('KT004', 'XL', '', N'Hoạt động'),
('KT005', 'XXL', '', N'Hoạt động'),
('KT006', '27', 'inch', N'Hoạt động'),
('KT007', '28', 'inch', N'Hoạt động'),
('KT008', '29', 'inch', N'Hoạt động'),
('KT009', '30', 'inch', N'Hoạt động'),
('KT010', '31', 'inch', N'Hoạt động');

-- 11. Bảng mau_sac
INSERT INTO mau_sac (ma_mau_sac, ten_mau_sac, trang_thai) VALUES
('MS001', N'Đỏ', N'Hoạt động'),
('MS002', N'Xanh', N'Hoạt động'),
('MS003', N'Vàng', N'Hoạt động'),
('MS004', N'Đen', N'Hoạt động'),
('MS005', N'Trắng', N'Hoạt động'),
('MS006', N'Hồng', N'Hoạt động'),
('MS007', N'Tím', N'Hoạt động'),
('MS008', N'Cam', N'Hoạt động'),
('MS009', N'Nâu', N'Hoạt động'),
('MS010', N'Xám', N'Hoạt động');

-- 12. Bảng chi_tiet_san_pham
INSERT INTO chi_tiet_san_pham (id_san_pham, qr_code, gia_ban, so_luong, trang_thai, ngay_tao, ngay_sua, id_kich_thuoc, id_mau_sac) VALUES
(1, 'QR001', 100000, 50, N'Hoạt động', '2023-01-01', '2023-01-01', 1, 1),
(2, 'QR002', 200000, 40, N'Hoạt động', '2023-02-01', '2023-02-01', 2, 2),
(3, 'QR003', 300000, 30, N'Hoạt động', '2023-03-01', '2023-03-01', 3, 3),
(4, 'QR004', 400000, 20, N'Hoạt động', '2023-04-01', '2023-04-01', 4, 4),
(5, 'QR005', 500000, 10, N'Hoạt động', '2023-05-01', '2023-05-01', 5, 5),
(6, 'QR006', 600000, 60, N'Hoạt động', '2023-06-01', '2023-06-01', 6, 6),
(7, 'QR007', 700000, 70, N'Hoạt động', '2023-07-01', '2023-07-01', 7, 7),
(8, 'QR008', 800000, 80, N'Hoạt động', '2023-08-01', '2023-08-01', 8, 8),
(9, 'QR009', 900000, 90, N'Hoạt động', '2023-09-01', '2023-09-01', 9, 9),
(10, 'QR010', 1000000, 100, N'Hoạt động', '2023-10-01', '2023-10-01', 10, 10);

-- 13. Bảng hinh_anh
INSERT INTO hinh_anh (id_chi_tiet_san_pham, anh_chinh, hinh_anh) VALUES
(1, 1, 'img1.jpg'),
(2, 0, 'img2.jpg'),
(3, 1, 'img3.jpg'),
(4, 0, 'img4.jpg'),
(5, 1, 'img5.jpg'),
(6, 0, 'img6.jpg'),
(7, 1, 'img7.jpg'),
(8, 0, 'img8.jpg'),
(9, 1, 'img9.jpg'),
(10, 0, 'img10.jpg');

-- 14. Bảng chi_tiet_khuyen_mai
INSERT INTO chi_tiet_khuyen_mai (id_khuyen_mai, id_chi_tiet_san_pham) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10);

-- 15. Bảng khach_hang
INSERT INTO khach_hang (ma_khach_hang, ten_khach_hang, gioi_tinh, so_dien_thoai, ngay_sinh, email, id_tai_khoan, trang_thai) VALUES
( 'KH001', N'Nguyễn Văn Khách 1', 1, '0910000001', '1990-01-01', 'kh1@example.com', 11, N'Đang hoạt động'), -- Khách hàng
( 'KH002', N'Phạm Thị Khách 2', 0, '0910000002', '1991-02-02', 'kh2@example.com', 12, N'Đang hoạt động'), -- Khách hàng
( 'KH003', N'Lê Văn Khách 3', 1, '0910000003', '1992-03-03', 'kh3@example.com', 13, N'Đang hoạt động'), -- Khách hàng
( 'KH004', N'Trần Thị Khách 4', 0, '0910000004', '1993-04-04', 'kh4@example.com', 14, N'Đang hoạt động'), -- Khách hàng
( 'KH005', N'Hoàng Văn Khách 5', 1, '0910000005', '1994-05-05', 'kh5@example.com', 15, N'Đang hoạt động'), -- Khách hàng
( 'KH006', N'Vũ Thị Khách 6', 0, '0910000006', '1995-06-06', 'kh6@example.com', 16, N'Đang hoạt động'), -- Khách hàng
( 'KH007', N'Đặng Văn Khách 7', 1, '0910000007', '1996-07-07', 'kh7@example.com', 17, N'Đang hoạt động'), -- Khách hàng
( 'KH008', N'Bùi Thị Khách 8', 0, '0910000008', '1997-08-08', 'kh8@example.com', 18, N'Đang hoạt động'), -- Khách hàng
( 'KH009', N'Phan Văn Khách 9', 1, '0910000009', '1998-09-09', 'kh9@example.com', 19, N'Đang hoạt động'), -- Khách hàng
('KH010', N'Ngô Thị Khách 10', 0, '0910000010', '1999-10-10', 'kh10@example.com', 20, N'Đang hoạt động'); -- Khách hàng

-- 16. Bảng gio_hang
INSERT INTO gio_hang (id_khach_hang) VALUES
(1), (2), (3), (4), (5), (6), (7), (8), (9), (10);

-- 17. Bảng chi_tiet_gio_hang
INSERT INTO chi_tiet_gio_hang (id_gio_hang, id_chi_tiet_san_pham, so_luong) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 1),
(4, 4, 3),
(5, 5, 2),
(6, 6, 1),
(7, 7, 4),
(8, 8, 2),
(9, 9, 1),
(10, 10, 3);

-- 18. Bảng binh_luan
INSERT INTO binh_luan (id_khach_hang, id_chi_tiet_san_pham, binh_luan, danh_gia, ngay_binh_luan, ngay_sua) VALUES
(1, 1, N'Sản phẩm tốt', 4.5, '2025-03-01', '2025-03-01'),
(2, 2, N'Chất lượng ổn', 4.0, '2025-03-02', '2025-03-02'),
(3, 3, N'Rất hài lòng', 5.0, '2025-03-03', '2025-03-03'),
(4, 4, N'Tạm được', 3.5, '2025-03-04', '2025-03-04'),
(5, 5, N'Không tệ', 4.0, '2025-03-05', '2025-03-05'),
(6, 6, N'Đẹp lắm', 4.8, '2025-03-06', '2025-03-06'),
(7, 7, N'Ổn áp', 4.2, '2025-03-07', '2025-03-07'),
(8, 8, N'Bình thường', 3.0, '2025-03-08', '2025-03-08'),
(9, 9, N'Tuyệt vời', 5.0, '2025-03-09', '2025-03-09'),
(10, 10, N'Hài lòng', 4.5, '2025-03-10', '2025-03-10');

-- 19. Bảng danh_sach_yeu_thich
INSERT INTO danh_sach_yeu_thich (id_khach_hang, id_chi_tiet_san_pham, ngay_them) VALUES
(1, 1, '2025-03-01'),
(2, 2, '2025-03-02'),
(3, 3, '2025-03-03'),
(4, 4, '2025-03-04'),
(5, 5, '2025-03-05'),
(6, 6, '2025-03-06'),
(7, 7, '2025-03-07'),
(8, 8, '2025-03-08'),
(9, 9, '2025-03-09'),
(10, 10, '2025-03-10');

-- 20. Bảng dia_chi_khach_hang
INSERT INTO dia_chi_khach_hang (id_khach_hang, so_nha, xa_phuong, quan_huyen, tinh_thanh_pho, dia_chi_mac_dinh) VALUES
(1, N'Số nhà 25', N'Phường Cổ Nhuế 1', N'Quận Bắc Từ Liêm', N'Hà Nội', 1),
(2, N'Số nhà 225', N'Phường Phương Canh', N'Quận Nam Từ Liêm', N'Hà Nội', 1),
(3, N'Số nhà 125', N'Phường 15', N'Quận Tân Bình', N'Hồ Chí Minh', 1),
(4, N'Số nhà 2', N'Phường Bến Nghé', N'Quận 1', N'Hồ Chí Minh', 1),
(5, N'Số nhà 5', N'Phường Hải Châu 1', N'Quận Hải Châu', N'Đà Nẵng', 1),
(6, N'Số nhà 3', N'Phường Thọ Quang', N'Quận Sơn Trà', N'Đà Nẵng', 1),
(7, N'Số nhà 45', N'Phường Hòa Khánh', N'Quận Liên Chiểu', N'Đà Nẵng', 1),
(8, N'Số nhà 5', N'Phường 10', N'Quận 3', N'Hồ Chí Minh', 1),
(9, N'Số nhà 11', N'Phường Xuân Đỉnh', N'Quận Bắc Từ Liêm', N'Hà Nội', 1),
(10, N'Số nhà 41', N'Phường Tân Phú', N'Quận 7', N'Hồ Chí Minh', 1);

-- 21. Bảng voucher
INSERT INTO voucher (ma_voucher, ten_voucher, ngay_tao, ngay_het_han, gia_tri_giam, gia_tri_toi_thieu, trang_thai, so_luong, kieu_giam_gia, mo_ta, gia_tri_toi_da) VALUES
('VC001', N'Voucher 50K', '2025-01-05', '2025-03-01', 50000, 300000, N'Đang diễn ra', 100, N'Tiền mặt', N'Giảm 50K cho đơn từ 300K', 50000),
('VC002', N'Voucher 10%', '2025-02-10', '2025-04-01', 10, 500000, N'Đang diễn ra', 50, N'Phần trăm', N'Giảm 10% cho đơn từ 500K', 100000),
('VC003', N'Voucher sinh nhật', '2025-03-01', '2025-03-31', 20, 400000, N'Đang diễn ra', 30, N'Phần trăm', N'Ưu đãi sinh nhật 20% đơn từ 400K', 80000),
('VC004', N'Voucher 150K', '2025-06-05', '2025-07-01', 150000, 700000, N'Sắp diễn ra', 30, N'Tiền mặt', N'Giảm ngay 150K cho đơn từ 700K', 150000),
('VC005', N'Voucher mùa hè', '2025-07-10', '2025-08-10', 15, 350000, N'Sắp diễn ra', 20, N'Phần trăm', N'Giảm 15% tối đa 75K', 75000),
('VC006', N'Voucher 20K', '2025-01-01', '2025-02-01', 20000, 200000, N'Đã kết thúc', 0, N'Tiền mặt', N'Giảm 20K cho đơn từ 200K', 20000),
('VC007', N'Voucher 25%', '2025-04-01', '2025-05-01', 25, 600000, N'Sắp diễn ra', 40, N'Phần trăm', N'Giảm 25% tối đa 150K', 150000),
('VC008', N'Voucher 100K', '2025-03-15', '2025-04-15', 100000, 500000, N'Đang diễn ra', 60, N'Tiền mặt', N'Giảm 100K cho đơn từ 500K', 100000),
('VC009', N'Voucher khai trương', '2025-02-01', '2025-03-01', 30, 800000, N'Đang diễn ra', 25, N'Phần trăm', N'Giảm 30% tối đa 200K', 200000),
('VC010', N'Voucher cuối năm', '2025-12-01', '2025-12-31', 50000, 400000, N'Sắp diễn ra', 50, N'Tiền mặt', N'Giảm 50K cho đơn từ 400K', 50000);

-- 22. Bảng hoa_don
INSERT INTO hoa_don (ma_hoa_don, id_nhan_vien, id_khach_hang, ngay_tao, ngay_sua, trang_thai, id_voucher, sdt_nguoi_nhan, dia_chi, email, tong_tien_truoc_giam, phi_van_chuyen, ho_ten, tong_tien_sau_giam, hinh_thuc_thanh_toan, phuong_thuc_nhan_hang, loai_hoa_don) VALUES
('HD001', 1, 1, '2025-02-01', '2025-02-01', N'Chưa thanh toán', 1, '0910000001', N'123 Đường A, Hà Nội', 'kh1@example.com', 100000, 30000, N'Nguyễn Văn M', 130000, N'Tiền mặt', N'Giao hàng', N'Online'),
('HD002', 2, 2, '2025-02-02', '2025-02-02', N'Chưa thanh toán', 2, '0910000002', N'456 Đường B, Hồ Chí Minh', 'kh2@example.com', 400000, 50000, N'Phạm Thị N', 450000, N'Chuyển khoản', N'Nhận tại cửa hàng', N'Offline'),
('HD003', 3, 3, '2025-02-03', '2025-02-03', N'Đã thanh toán', 3, '0910000003', N'789 Đường C, Đà Nẵng', 'kh3@example.com', 300000, 50000, N'Lê Văn O', 350000, N'Tiền mặt', N'Giao hàng', N'Offline'),
('HD004', 3, 3, '2025-02-03', '2025-02-03', N'Đã thanh toán', 3, '0910000003', N'789 Đường C, Đà Nẵng', 'kh3@example.com', 900000, 50000, N'Lê Văn O', 950000, N'Tiền mặt', N'Nhận tại cửa hàng', N'Offline'),
('HD005', 3, 3, '2025-02-03', '2025-02-03', N'Đã thanh toán', 3, '0910000003', N'789 Đường C, Đà Nẵng', 'kh3@example.com', 4900000, 100000, N'Lê Văn O', 5000000, N'Chuyển khoản', N'Giao hàng', N'Online'),
('HD006', 3, 3, '2025-02-03', '2025-02-03', N'Đã thanh toán', 3, '0910000003', N'789 Đường C, Đà Nẵng', 'kh3@example.com', 4700000, 100000, N'Lê Văn O', 4800000, N'Tiền mặt', N'Giao hàng', N'Online');

-- 23. Bảng hoa_don_chi_tiet
INSERT INTO hoa_don_chi_tiet (id_hoa_don, id_chi_tiet_san_pham, so_luong, don_gia) VALUES
(1, 1, 1, 100000),
(2, 2, 2, 200000),
(3, 3, 1, 300000),
(4, 1, 3, 100000),
(4, 3, 2, 300000),
(5, 5, 1, 500000),
(5, 4, 3, 400000),
(5, 8, 4, 800000),
(6, 2, 5, 200000),
(6, 9, 3, 900000),
(6, 10, 1, 1000000);

-- 24. Bảng theo_doi_don_hang
INSERT INTO theo_doi_don_hang (id_hoa_don, trang_thai, ngay_chuyen) VALUES
(1, N'Đã hủy', '2025-02-02'),
(2, N'Chờ xác nhận', '2025-02-03'),
(3, N'Đang giao', '2025-02-03'),
(4, N'Hoàn thành', '2025-02-03'),
(5, N'Đã xác nhận', '2025-02-03'),
(6, N'Hoàn thành', '2025-02-04');

-- 25. Bảng yeu_cau_doi_hang
INSERT INTO yeu_cau_doi_hang (ma_yeu_cau, id_hoa_don, id_san_pham_moi, trang_thai_san_pham, ngay_yeu_cau, ngay_xu_ly, ly_do_doi) VALUES
('YC001', 1, 1, N'Mới', '2025-02-03', '2025-02-04', N'Lỗi sản phẩm'),
('YC002', 2, 2, N'Mới', '2025-02-04', '2025-02-05', N'Lỗi sản phẩm'),
('YC003', 3, 3, N'Mới', '2025-02-05', '2025-02-06', N'Lỗi sản phẩm');

-- 26. Bảng hinh_anh_doi_hang
INSERT INTO hinh_anh_doi_hang (id_yeu_cau, hinh_anh) VALUES
(1, 'doi_img1.jpg'),
(2, 'doi_img2.jpg'),
(3, 'doi_img3.jpg');


-- 27. Bảng phieu_doi_hang
INSERT INTO phieu_doi_hang (id_yeu_cau, ma_phieu_doi, phuong_thuc_thanh_toan, ngay_xuat_phieu, trang_thai, gia_tri_chenh_lech) VALUES
(1, 'PDH001', N'Tiền mặt', '2025-02-05', N'Đã xuất', 10000),
(2, 'PDH002', N'Chuyển khoản', '2025-02-06', N'Đã xuất', 20000),
(3, 'PDH003', N'Tiền mặt', '2025-02-07', N'Đã xuất', 30000);
