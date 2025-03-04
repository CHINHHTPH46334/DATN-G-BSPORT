create database GBSports
go
use GBSports
go
--Role
create table roles(
id_roles int primary key,
ma_roles varchar(50),
ten_roles nvarchar(200)
);
--Tài khoản
create table tai_khoan(
id_tai_khoan int identity(1,1) primary key,
ma_tai_khoan varchar(50),
ten_dang_nhap varchar(100),
mat_khau varchar(200)
);
--Lịch sử đăng nhập
create table lich_su_dang_nhap(
id_lich_su_dang_nhap int identity(1,1) primary key,
id_tai_khoan int references tai_khoan(id_tai_khoan),
ngay_dang_nhap datetime,
ip_adress varchar(200)
);
--Nhân viên
create table nhan_vien(
id_nhan_vien int identity(1,1) primary key,
id_roles int references roles(id_roles),
id_tai_khoan int references tai_khoan(id_tai_khoan),
ma_nhan_vien varchar(50),
ten_nhan_vien nvarchar(200),
ngay_sinh date,
email varchar(200),
dia_chi_lien_he nvarchar(200),
gioi_tinh bit,
so_dien_thoai varchar(20),
anh_nhan_vien varchar(200),
ngay_tham_gia date,
trang_thai nvarchar(50)
);
--Danh mục sản phẩm
create table danh_muc_san_pham(
id_danh_muc int identity(1,1) primary key,
ma_danh_muc varchar(50),
ten_danh_muc nvarchar(200),
trang_thai nvarchar(50),
ngay_tao datetime,
ngay_sua datetime
);
--Chất liệu
create table chat_lieu(
id_chat_lieu int identity(1,1) primary key,
ma_chat_lieu varchar(50),
ten_chat_lieu nvarchar(200)
);
--Thương hiệu
create table thuong_hieu(
id_thuong_hieu int identity(1,1) primary key,
ma_thuong_hieu varchar(50),
ten_thuong_hieu nvarchar(200),
trang_thai nvarchar(50),
ngay_tao datetime,
ngay_sua datetime
);
--Sản phẩm
create table san_pham(
id_san_pham int identity(1,1) primary key,
id_danh_muc int references danh_muc_san_pham(id_danh_muc),
ma_san_pham varchar(50),
ten_san_pham nvarchar(200),
mo_ta nvarchar(250),
trang_thai nvarchar(50),
gioi_tinh bit,
id_chat_lieu int references chat_lieu(id_chat_lieu),
id_thuong_hieu int references thuong_hieu(id_thuong_hieu)
);
--Khuyến mãi
create table khuyen_mai(
id_khuyen_mai int identity(1,1) primary key,
ma_khuyen_mai varchar(50),
ten_khuyen_mai nvarchar(200),
mo_ta nvarchar(250),
ngay_bat_dau datetime,
ngay_het_han datetime,
gia_tri_toi_da decimal(12,2),
gia_tri_giam float,
kieu_giam_gia nvarchar(50),
trang_thai nvarchar(50)
);
--Kích thước
create table kich_thuoc(
id_kich_thuoc int identity(1,1) primary key,
ma_kich_thuoc varchar(50),
gia_tri varchar(200),
don_vi nvarchar(20)
);
--Màu sắc
create table mau_sac(
id_mau_sac int identity(1,1) primary key,
ma_mau_sac varchar(50),
ten_mau_sac nvarchar(50)
);
--Chi tiết sản phẩm
create table chi_tiet_san_pham(
id_chi_tiet_san_pham int identity(1,1) primary key,
id_san_pham int references san_pham(id_san_pham),
qr_code varchar(200),
gia_ban decimal(12,2),
so_luong int,
trang_thai nvarchar(50),
ngay_tao datetime,
ngay_sua datetime,
gia_nhap decimal(12,2),
id_kich_thuoc int references kich_thuoc(id_kich_thuoc),
id_mau_sac int references mau_sac(id_mau_sac)
);
--Hình ảnh
create table hinh_anh(
id_hinh_anh int identity(1,1) primary key,
id_chi_tiet_san_pham int references chi_tiet_san_pham(id_chi_tiet_san_pham),
anh_chinh bit,
hinh_anh varchar(200)
);
--Chi tiết khuyến mãi
create table chi_tiet_khuyen_mai(
id_khuyen_mai int references khuyen_mai(id_khuyen_mai),
id_chi_tiet_san_pham int references chi_tiet_san_pham(id_chi_tiet_san_pham)
primary key(id_khuyen_mai,id_chi_tiet_san_pham)
);

--Khách hàng
create table khach_hang(
id_khach_hang int  identity(1,1) primary key,
ma_khach_hang nvarchar(50),
ten_khach_hang nvarchar(200),
gioi_tinh bit,
so_dien_thoai varchar(20),
ngay_sinh datetime,
email varchar(200),
id_tai_khoan int references tai_khoan(id_tai_khoan),
trang_thai nvarchar(50)
);
--Giỏ hàng
create table gio_hang(
id_gio_hang int identity(1,1) primary key,
id_khach_hang int references khach_hang(id_khach_hang)
);
--Chi tiết giỏ hàng
create table chi_tiet_gio_hang(
id_gio_hang int references gio_hang(id_gio_hang),
id_chi_tiet_san_pham int references chi_tiet_san_pham(id_chi_tiet_san_pham),
so_luong int
primary key(id_gio_hang,id_chi_tiet_san_pham)
);
--Bình luận
create table binh_luan(
id_khach_hang int references khach_hang(id_khach_hang),
id_chi_tiet_san_pham int references chi_tiet_san_pham(id_chi_tiet_san_pham),
binh_luan nvarchar(250),
danh_gia float,
ngay_binh_luan datetime,
ngay_sua datetime
primary key(id_khach_hang, id_chi_tiet_san_pham)
);
--Danh sách yêu thích
create table danh_sach_yeu_thich(
id_khach_hang int references khach_hang(id_khach_hang),
id_chi_tiet_san_pham int references chi_tiet_san_pham(id_chi_tiet_san_pham),
ngay_them datetime
primary key(id_khach_hang, id_chi_tiet_san_pham)
);
--địa chỉ khách hàng
create table dia_chi_khach_hang(
id_dia_chi_khach_hang int identity(1,1) primary key,
id_khach_hang int references khach_hang(id_khach_hang),
dia_chi_khach_hang nvarchar(200)
);
--Voucher
create table voucher(
id_voucher int identity(1,1) primary key,
ma_voucher varchar(50),
ten_voucher nvarchar(200),
ngay_tao datetime,
ngay_het_han datetime,
gia_tri_giam decimal(12,2),
gia_tri_toi_thieu decimal(12,2),
trang_thai nvarchar(50),
so_luong int,
kieu_giam_gia nvarchar(20),
mo_ta nvarchar(250),
gia_tri_toi_da decimal(12,2)
);
--Hóa đơn
create table hoa_don(
id_hoa_don int identity(1,1) primary key,
ma_hoa_don varchar(50),
id_nhan_vien int references nhan_vien(id_nhan_vien),
id_khach_hang int references khach_hang(id_khach_hang),
ngay_tao datetime,
ngay_sua datetime,
trang_thai nvarchar(50),
id_voucher int references voucher(id_voucher),
sdt_nguoi_nhan varchar(20),
dia_chi nvarchar(250),
email varchar(200),
tong_tien_truoc_giam decimal(12,2),
phi_van_chuyen decimal(12,2),
ho_ten nvarchar(250),
tong_tien_sau_giam decimal(12,2),
hinh_thuc_thanh_toan nvarchar(50),
phuong_thuc_nhan_hang nvarchar(100)
);
--Hóa đơn chi tiết
create table hoa_don_chi_tiet(
id_hoa_don_chi_tiet int identity(1,1),
id_hoa_don int references hoa_don(id_hoa_don),
id_chi_tiet_san_pham int references chi_tiet_san_pham(id_chi_tiet_san_pham),
so_luong int,
don_gia decimal(12,2)
primary key(id_hoa_don_chi_tiet, id_hoa_don, id_chi_tiet_san_pham)
);
--Theo dõi đơn hàng
create table theo_doi_don_hang(
id_don_hang int identity(1,1) primary key,
id_hoa_don int references hoa_don(id_hoa_don),
trang_thai nvarchar(50),
ngay_chuyen datetime
);
--Yêu cầu đổi hàng
create table yeu_cau_doi_hang(
id_yeu_cau int identity(1,1) primary key,
ma_yeu_cau varchar(50),
id_hoa_don int references hoa_don(id_hoa_don),
id_san_pham_moi int references chi_tiet_san_pham(id_chi_tiet_san_pham),
trang_thai_san_pham nvarchar(100),
ngay_yeu_cau datetime,
ngay_xu_ly datetime,
ly_do_doi nvarchar(200)
);
--Hình ảnh đổi hàng
create table hinh_anh_doi_hang(
id_hinh_anh_doi int identity(1,1) primary key,
id_yeu_cau int references yeu_cau_doi_hang(id_yeu_cau),
hinh_anh varchar(250)
);
--Phiếu đổi hàng
create table phieu_doi_hang(
id_phieu_doi int identity(1,1) primary key,
id_yeu_cau int references yeu_cau_doi_hang(id_yeu_cau),
ma_phieu_doi varchar(50),
phuong_thuc_thanh_toan nvarchar(50),
ngay_xuat_phieu datetime,
trang_thai nvarchar(50),
gia_tri_chenh_lech decimal
);

-------------------------------------
-- 1. Bảng roles (10 dòng)
-------------------------------------
INSERT INTO roles (id_roles, ma_roles, ten_roles) VALUES
(1, N'QL', N'Quản lý'),
(2, N'NV', N'Nhân viên');

-------------------------------------
-- 2. Bảng tai_khoan (10 dòng)
-------------------------------------
INSERT INTO tai_khoan (ma_tai_khoan, ten_dang_nhap, mat_khau) VALUES
(N'TK01', N'admin', N'admin123'),
(N'TK02', N'nhanvien', N'nhanvien123'),
(N'TK03', N'customer', N'customer123');

-------------------------------------
-- 3. Bảng lich_su_dang_nhap (10 dòng)
-------------------------------------
INSERT INTO lich_su_dang_nhap (id_tai_khoan, ngay_dang_nhap, ip_adress) VALUES
(1, '2025-02-01', '192.168.1.1'),
(2, '2025-02-02', '192.168.1.2'),
(3, '2025-02-03', '192.168.1.3');

-------------------------------------
-- 4. Bảng nhan_vien (10 dòng)
-------------------------------------
INSERT INTO nhan_vien (id_roles, id_tai_khoan, ma_nhan_vien, ten_nhan_vien, ngay_sinh, email, dia_chi_lien_he, gioi_tinh, so_dien_thoai, anh_nhan_vien, ngay_tham_gia, trang_thai) VALUES
(1, 1, N'NV01', N'Phạm Thị Quỳnh Thu', CAST(N'2002-07-10' AS Date), N'ptqt10722@gmail.com', N'Phương Canh, Nam Từ Liêm, Hà Nội', 0, N'0359765372', N'avatar1.jpg', CAST(N'2023-01-01' AS Date), N'Đang hoạt động'),
(2, 2, N'NV02', N'Nguyễn Hữu Nghĩa', CAST(N'2000-10-17' AS Date), N'nghianhph46340@fpt.edu.vn', N'Phương Canh, Nam Từ Liêm, Hà Nội', 1, N'0353225292', N'avatar2.jpg', CAST(N'2023-02-01' AS Date), N'Đang hoạt động'),
(2, 2, N'NV03', N'Hoàng Thọ Chính', CAST(N'2004-12-13' AS Date), N'chinhhtph46334@fpt.edu.vn', N'Hoài Đức, Hà Nội', 1, N'0989864737', N'avatar3.jpg', CAST(N'2023-03-01' AS Date), N'Đang hoạt động'),
(2, 2, N'NV04', N'Nguyễn Thị Kiều Anh', CAST(N'2003-04-12' AS Date), N'annv@gmail.com', N'Bắc Từ Liêm, Hà Nội', 0, N'0378854735', N'avatar4.jpg', CAST(N'2023-04-01' AS Date), N'Đã nghỉ việc'),
(2, 2, N'NV05', N'Vũ Tuấn Huy', CAST(N'2004-02-15' AS Date), N'huyvtph46307@fpt.edu.vn', N'Đội Cấn, Ba Đình, Hà Nội', 1, N'0912301363', N'avatar5.jpg', CAST(N'2023-05-01' AS Date), N'Đang hoạt động'),
(2, 2, N'NV06', N'Hồ Bá Dũng', CAST(N'2004-11-14' AS Date), N'dunghbph46428@fpt.edu.vn', N'Xã Đàn, Đống Đa, Hà Nội', 1, N'0397572262', N'avatar6.jpg', CAST(N'2023-06-01' AS Date), N'Đang hoạt động'),
(2, 2, N'NV07', N'Phùn Văn Lềnh', CAST(N'2003-09-10' AS Date), N'lenhpvph46331@fpt.edu.vn', N'Cổ Nhuế 2, Hà Nội', 1, N'0388109763', N'avatar7.jpg', CAST(N'2023-07-01' AS Date), N'Đang hoạt động'),
(2, 2, N'NV08', N'Hà Trung Thành', CAST(N'2001-11-23' AS Date), N'thanhht@gmail.com', N'Hồ Chí Minh', 1, N'0325467832', N'avatar8.jpg', CAST(N'2023-08-01' AS Date), N'Đã nghỉ việc'),
(2, 2, N'NV09', N'Lê Thành Dương', CAST(N'2000-01-17' AS Date), N'duonglt@gmail.com', N'Hồ Chí Minh', 1, N'0987678543', N'avatar9.jpg', CAST(N'2023-09-01' AS Date), N'Đã nghỉ việc'),
(2, 2, N'NV10', N'Lê Thị Nhàn', CAST(N'1999-08-29' AS Date), N'nhanlt@gmail.com', N'Hồ Chí Minh', 0, N'0987678543', N'avatar10.jpg', CAST(N'2023-10-01' AS Date), N'Đã nghỉ việc');

-------------------------------------
-- 5. Bảng danh_muc_san_pham (10 dòng)
-------------------------------------
INSERT INTO danh_muc_san_pham (ma_danh_muc, ten_danh_muc, trang_thai, ngay_tao, ngay_sua) VALUES
('DM001', N'Thời trang nam', N'Hoạt động', '2025-01-01', '2025-01-01'),
('DM002', N'Thời trang nữ', N'Hoạt động', '2025-01-02', '2025-01-02'),
('DM003', N'Phụ kiện', N'Hoạt động', '2025-01-03', '2025-01-03'),
('DM004', N'Giày dép', N'Hoạt động', '2025-01-04', '2025-01-04'),
('DM005', N'Túi xách', N'Hoạt động', '2025-01-05', '2025-01-05'),
('DM006', N'Đồng hồ', N'Hoạt động', '2025-01-06', '2025-01-06'),
('DM007', N'Mỹ phẩm', N'Hoạt động', '2025-01-07', '2025-01-07'),
('DM008', N'Đồ trẻ em', N'Hoạt động', '2025-01-08', '2025-01-08'),
('DM009', N'Thể thao', N'Hoạt động', '2025-01-09', '2025-01-09'),
('DM010', N'Giải trí', N'Hoạt động', '2025-01-10', '2025-01-10');

-------------------------------------
-- 6. Bảng chat_lieu (10 dòng)
-------------------------------------
INSERT INTO chat_lieu (ma_chat_lieu, ten_chat_lieu) VALUES
('CL001', N'Cotton'),
('CL002', N'Len'),
('CL003', N'Polyester'),
('CL004', N'Vải Jeans'),
('CL005', N'Siêu nhẹ'),
('CL006', N'Chất liệu cao cấp'),
('CL007', N'Nỉ'),
('CL008', N'Vải lụa'),
('CL009', N'Vải dù'),
('CL010', N'Vải tổng hợp');

-------------------------------------
-- 7. Bảng thuong_hieu (10 dòng)
-------------------------------------
INSERT INTO thuong_hieu (ma_thuong_hieu, ten_thuong_hieu, trang_thai, ngay_tao, ngay_sua) VALUES
('TH001', N'Adidas', N'Hoạt động', '2025-01-01', '2025-01-01'),
('TH002', N'Nike', N'Hoạt động', '2025-01-02', '2025-01-02'),
('TH003', N'Puma', N'Hoạt động', '2025-01-03', '2025-01-03'),
('TH004', N'Reebok', N'Hoạt động', '2025-01-04', '2025-01-04'),
('TH005', N'Converse', N'Hoạt động', '2025-01-05', '2025-01-05'),
('TH006', N'New Balance', N'Hoạt động', '2025-01-06', '2025-01-06'),
('TH007', N'Under Armour', N'Hoạt động', '2025-01-07', '2025-01-07'),
('TH008', N'Asics', N'Hoạt động', '2025-01-08', '2025-01-08'),
('TH009', N'Vans', N'Hoạt động', '2025-01-09', '2025-01-09'),
('TH010', N'Fila', N'Hoạt động', '2025-01-10', '2025-01-10');

-------------------------------------
-- 8. Bảng san_pham (10 dòng)
-------------------------------------
INSERT INTO san_pham (id_danh_muc, ma_san_pham, ten_san_pham, mo_ta, trang_thai, gioi_tinh, id_chat_lieu, id_thuong_hieu) VALUES
(1, 'SP001', N'Sản phẩm 1', N'Mô tả sản phẩm 1', N'Hoạt động', 1, 1, 1),
(2, 'SP002', N'Sản phẩm 2', N'Mô tả sản phẩm 2', N'Hoạt động', 0, 2, 2),
(3, 'SP003', N'Sản phẩm 3', N'Mô tả sản phẩm 3', N'Hoạt động', 1, 3, 3),
(4, 'SP004', N'Sản phẩm 4', N'Mô tả sản phẩm 4', N'Hoạt động', 0, 4, 4),
(5, 'SP005', N'Sản phẩm 5', N'Mô tả sản phẩm 5', N'Hoạt động', 1, 5, 5),
(6, 'SP006', N'Sản phẩm 6', N'Mô tả sản phẩm 6', N'Hoạt động', 0, 6, 6),
(7, 'SP007', N'Sản phẩm 7', N'Mô tả sản phẩm 7', N'Hoạt động', 1, 7, 7),
(8, 'SP008', N'Sản phẩm 8', N'Mô tả sản phẩm 8', N'Hoạt động', 0, 8, 8),
(9, 'SP009', N'Sản phẩm 9', N'Mô tả sản phẩm 9', N'Hoạt động', 1, 9, 9),
(10, 'SP010', N'Sản phẩm 10', N'Mô tả sản phẩm 10', N'Hoạt động', 0, 10, 10);

-------------------------------------
-- 9. Bảng khuyen_mai (10 dòng)
-------------------------------------
INSERT INTO khuyen_mai (ma_khuyen_mai, ten_khuyen_mai, mo_ta, ngay_bat_dau, ngay_het_han, gia_tri_toi_da, gia_tri_giam, kieu_giam_gia, trang_thai)
VALUES 
('KM001', N'Giảm giá Tết', N'Giảm 20% cho đơn hàng trên 500k', '2025-01-01', '2025-02-15', 100000, 20, N'Phần trăm', N'Đã kết thúc'),
('KM002', N'Flash Sale', N'Giảm 100K cho đơn từ 300K', '2025-03-10', '2025-03-20', 100000, 100000, N'Tiền mặt', N'Đang diễn ra'),
('KM003', N'Mừng sinh nhật', N'Giảm 15% tối đa 50K', '2025-05-01', '2025-05-07', 50000, 15, N'Phần trăm', N'Sắp diễn ra'),
('KM004', N'Khuyến mãi hè', N'Giảm giá 50K đơn từ 400K', '2025-06-01', '2025-06-30', 50000, 50000, N'Tiền mặt', N'Sắp diễn ra'),
('KM005', N'Black Friday', N'Giảm 30% tối đa 200K', '2024-11-20', '2024-11-30', 200000, 30, N'Phần trăm', N'Đã kết thúc');
-------------------------------------
-- 10. Bảng kich_thuoc (10 dòng)
-------------------------------------
INSERT INTO kich_thuoc (ma_kich_thuoc, gia_tri, don_vi) VALUES
('KT001', 'S', N''),
('KT002', 'M', N''),
('KT003', 'L', N''),
('KT004', 'XL', N''),
('KT005', 'XXL', N''),
('KT006', '27', N'inch'),
('KT007', '28', N'inch'),
('KT008', '29', N'inch'),
('KT009', '30', N'inch'),
('KT010', '31', N'inch');

-------------------------------------
-- 11. Bảng mau_sac (10 dòng)
-------------------------------------
INSERT INTO mau_sac (ma_mau_sac, ten_mau_sac) VALUES
('MS001', N'Đỏ'),
('MS002', N'Xanh'),
('MS003', N'Vàng'),
('MS004', N'Đen'),
('MS005', N'Trắng'),
('MS006', N'Hồng'),
('MS007', N'Tím'),
('MS008', N'Cam'),
('MS009', N'Nâu'),
('MS010', N'Xám');

-------------------------------------
-- 12. Bảng chi_tiet_san_pham (10 dòng)
-------------------------------------
INSERT INTO chi_tiet_san_pham (id_san_pham, qr_code, gia_ban, so_luong, trang_thai, ngay_tao, ngay_sua, gia_nhap, id_kich_thuoc, id_mau_sac) VALUES
(1, 'QR001', 100000, 50, N'Còn hàng', '2025-02-01', '2025-02-01', 80000, 1, 1),
(2, 'QR002', 200000, 40, N'Còn hàng', '2025-02-02', '2025-02-02', 160000, 2, 2),
(3, 'QR003', 300000, 30, N'Còn hàng', '2025-02-03', '2025-02-03', 240000, 3, 3),
(4, 'QR004', 400000, 20, N'Hết hàng', '2025-02-04', '2025-02-04', 320000, 4, 4),
(5, 'QR005', 500000, 10, N'Còn hàng', '2025-02-05', '2025-02-05', 400000, 5, 5),
(6, 'QR006', 600000, 60, N'Còn hàng', '2025-02-06', '2025-02-06', 480000, 6, 6),
(7, 'QR007', 700000, 70, N'Còn hàng', '2025-02-07', '2025-02-07', 560000, 7, 7),
(8, 'QR008', 800000, 80, N'Còn hàng', '2025-02-08', '2025-02-08', 640000, 8, 8),
(9, 'QR009', 900000, 90, N'Còn hàng', '2025-02-09', '2025-02-09', 720000, 9, 9),
(10, 'QR010', 1000000, 100, N'Còn hàng', '2025-02-10', '2025-02-10', 800000, 10, 10);

-------------------------------------
-- 13. Bảng hinh_anh (10 dòng)
-------------------------------------
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

-------------------------------------
-- 14. Bảng chi_tiet_khuyen_mai (10 dòng)
-------------------------------------
INSERT INTO chi_tiet_khuyen_mai (id_khuyen_mai, id_chi_tiet_san_pham) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

-------------------------------------
-- 15. Bảng khach_hang (10 dòng)
-------------------------------------
INSERT INTO khach_hang (ma_khach_hang, ten_khach_hang, gioi_tinh, so_dien_thoai, ngay_sinh, email, id_tai_khoan, trang_thai) VALUES
('KH001', N'Nguyễn Văn M', 1, '0910000001', '1990-01-01', 'kh1@example.com', 1, N'Hoạt động'),
('KH002', N'Phạm Thị N', 0, '0910000002', '1991-02-02', 'kh2@example.com', 2, N'Hoạt động'),
('KH003', N'Lê Văn O', 1, '0910000003', '1992-03-03', 'kh3@example.com', 3, N'Hoạt động');

-------------------------------------
-- 16. Bảng gio_hang (10 dòng)
-------------------------------------
INSERT INTO gio_hang (id_khach_hang) VALUES
(1),
(2),
(3);

-------------------------------------
-- 17. Bảng chi_tiet_gio_hang (10 dòng)
-------------------------------------
INSERT INTO chi_tiet_gio_hang (id_gio_hang, id_chi_tiet_san_pham, so_luong) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 1);

-------------------------------------
-- 18. Bảng binh_luan (10 dòng) - khóa chính (id_khach_hang, id_chi_tiet_san_pham)
-------------------------------------
INSERT INTO binh_luan (id_khach_hang, id_chi_tiet_san_pham, binh_luan, danh_gia, ngay_binh_luan, ngay_sua) VALUES
(1, 1, N'Bình luận của khách 1', 4.5, '2025-02-01', '2025-02-01'),
(2, 2, N'Bình luận của khách 2', 4.0, '2025-02-02', '2025-02-02'),
(3, 3, N'Bình luận của khách 3', 5.0, '2025-02-03', '2025-02-03');

-------------------------------------
-- 19. Bảng danh_sach_yeu_thich (10 dòng) - khóa chính (id_khach_hang, id_chi_tiet_san_pham)
-------------------------------------
INSERT INTO danh_sach_yeu_thich (id_khach_hang, id_chi_tiet_san_pham, ngay_them) VALUES
(1, 1, '2025-02-01'),
(2, 2, '2025-02-02'),
(3, 3, '2025-02-03');

-------------------------------------
-- 20. Bảng dia_chi_khach_hang (10 dòng)
-------------------------------------
INSERT INTO dia_chi_khach_hang (id_khach_hang, dia_chi_khach_hang) VALUES
(1, N'123 Đường A, Hà Nội'),
(2, N'456 Đường B, Hồ Chí Minh'),
(3, N'789 Đường C, Đà Nẵng');

-------------------------------------
-- 21. Bảng voucher (10 dòng)
-------------------------------------
INSERT INTO voucher (ma_voucher, ten_voucher, ngay_tao, ngay_het_han, gia_tri_giam, gia_tri_toi_thieu, trang_thai, so_luong, kieu_giam_gia, mo_ta, gia_tri_toi_da)
VALUES 
('VC001', N'Voucher 50K', '2025-01-05', '2025-03-01', 50000, 300000, N'Đã kết thúc', 0, N'Tiền mặt', N'Giảm 50K cho đơn hàng từ 300K', 50000),
('VC002', N'Voucher 10%', '2025-02-10', '2025-04-01', 10, 500000, N'Đang diễn ra', 50, N'Phần trăm', N'Giảm 10% cho đơn từ 500K', 100000),
('VC003', N'Voucher sinh nhật', '2025-03-01', '2025-03-31', 20, 400000, N'Đang diễn ra', 30, N'Phần trăm', N'Ưu đãi sinh nhật 20% đơn từ 400K', 80000),
('VC004', N'Voucher 150K', '2025-06-05', '2025-07-01', 150000, 700000, N'Sắp diễn ra', 30, N'Tiền mặt', N'Giảm ngay 150K cho đơn từ 700K', 150000),
('VC005', N'Voucher mùa hè', '2025-07-10', '2025-08-10', 15, 350000, N'Sắp diễn ra', 20, N'Phần trăm', N'Giảm 15% tối đa 75K', 75000);
-------------------------------------
-- 22. Bảng hoa_don (10 dòng)
-------------------------------------
INSERT INTO hoa_don (ma_hoa_don, id_nhan_vien, id_khach_hang, ngay_tao, ngay_sua, trang_thai, id_voucher, sdt_nguoi_nhan, dia_chi, email, tong_tien_truoc_giam, phi_van_chuyen, ho_ten, tong_tien_sau_giam, hinh_thuc_thanh_toan, phuong_thuc_nhan_hang) VALUES
('HD001', 1, 1, '2025-02-01', '2025-02-01', N'Chờ xử lý', 1, '0910000001', N'123 Đường A, Hà Nội', 'kh1@example.com', 1000000, 50000, N'Nguyễn Văn M', 950000, N'Tiền mặt', N'Giao hàng'),
('HD002', 2, 2, '2025-02-02', '2025-02-02', N'Chờ xử lý', 2, '0910000002', N'456 Đường B, Hồ Chí Minh', 'kh2@example.com', 2000000, 60000, N'Phạm Thị N', 1940000, N'Chuyển khoản', N'Nhận tại cửa hàng'),
('HD003', 3, 3, '2025-02-03', '2025-02-03', N'Chờ xử lý', 3, '0910000003', N'789 Đường C, Đà Nẵng', 'kh3@example.com', 3000000, 70000, N'Lê Văn O', 2930000, N'Tiền mặt', N'Giao hàng');

-------------------------------------
-- 23. Bảng hoa_don_chi_tiet (10 dòng)
-------------------------------------
INSERT INTO hoa_don_chi_tiet (id_hoa_don, id_chi_tiet_san_pham, so_luong, don_gia) VALUES
(1, 1, 1, 100000),
(2, 2, 2, 200000),
(3, 3, 1, 300000);

-------------------------------------
-- 24. Bảng theo_doi_don_hang (10 dòng)
-------------------------------------
INSERT INTO theo_doi_don_hang (id_hoa_don, trang_thai, ngay_chuyen) VALUES
(1, N'Đang xử lý', '2025-02-02'),
(2, N'Đang xử lý', '2025-02-03'),
(3, N'Đang xử lý', '2025-02-04');

-------------------------------------
-- 25. Bảng yeu_cau_doi_hang (10 dòng)
-------------------------------------
INSERT INTO yeu_cau_doi_hang (ma_yeu_cau, id_hoa_don, id_san_pham_moi, trang_thai_san_pham, ngay_yeu_cau, ngay_xu_ly, ly_do_doi) VALUES
('YC001', 1, 1, N'Mới', '2025-02-03', '2025-02-04', N'Lỗi sản phẩm'),
('YC002', 2, 2, N'Mới', '2025-02-04', '2025-02-05', N'Lỗi sản phẩm'),
('YC003', 3, 3, N'Mới', '2025-02-05', '2025-02-06', N'Lỗi sản phẩm');

-------------------------------------
-- 26. Bảng hinh_anh_doi_hang (10 dòng)
-------------------------------------
INSERT INTO hinh_anh_doi_hang (id_yeu_cau, hinh_anh) VALUES
(1, 'doi_img1.jpg'),
(2, 'doi_img2.jpg'),
(3, 'doi_img3.jpg');

-------------------------------------
-- 27. Bảng phieu_doi_hang (10 dòng)
-------------------------------------
INSERT INTO phieu_doi_hang (id_yeu_cau, ma_phieu_doi, phuong_thuc_thanh_toan, ngay_xuat_phieu, trang_thai, gia_tri_chenh_lech) VALUES
(1, 'PDH001', N'Tiền mặt', '2025-02-05', N'Đã xuất', 10000),
(2, 'PDH002', N'Chuyển khoản', '2025-02-06', N'Đã xuất', 20000),
(3, 'PDH003', N'Tiền mặt', '2025-02-07', N'Đã xuất', 30000);

