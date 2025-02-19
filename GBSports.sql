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
ngay_dang_nhap date,
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
trang_thai nvarchar(50)
);
--Danh mục sản phẩm
create table danh_muc_san_pham(
id_danh_muc int identity(1,1) primary key,
ma_danh_muc varchar(50),
ten_danh_muc nvarchar(200),
trang_thai nvarchar(50),
ngay_tao date,
ngay_sua date
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
ngay_tao date,
ngay_sua date
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
ngay_bat_dau date,
ngay_het_han date,
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
ngay_tao date,
ngay_sua date,
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
ngay_sinh date,
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
ngay_binh_luan date,
ngay_sua date
primary key(id_khach_hang, id_chi_tiet_san_pham)
);
--Danh sách yêu thích
create table danh_sach_yeu_thich(
id_khach_hang int references khach_hang(id_khach_hang),
id_chi_tiet_san_pham int references chi_tiet_san_pham(id_chi_tiet_san_pham),
ngay_them date
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
ngay_tao date,
ngay_het_han date,
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
ngay_tao date,
ngay_sua date,
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
ngay_chuyen date
);
--Yêu cầu đổi hàng
create table yeu_cau_doi_hang(
id_yeu_cau int identity(1,1) primary key,
ma_yeu_cau varchar(50),
id_hoa_don int references hoa_don(id_hoa_don),
id_san_pham_moi int references chi_tiet_san_pham(id_chi_tiet_san_pham),
trang_thai_san_pham nvarchar(100),
ngay_yeu_cau date,
ngay_xu_ly date,
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
ngay_xuat_phieu date,
trang_thai nvarchar(50),
gia_tri_chenh_lech decimal
);

-------------------------------------
-- 1. Bảng roles (10 dòng)
-------------------------------------
INSERT INTO roles (id_roles, ma_roles, ten_roles) VALUES
(1, 'ROLE1', N'Quyền quản trị 1'),
(2, 'ROLE2', N'Quyền quản trị 2'),
(3, 'ROLE3', N'Quyền quản trị 3'),
(4, 'ROLE4', N'Quyền quản trị 4'),
(5, 'ROLE5', N'Quyền quản trị 5'),
(6, 'ROLE6', N'Quyền quản trị 6'),
(7, 'ROLE7', N'Quyền quản trị 7'),
(8, 'ROLE8', N'Quyền quản trị 8'),
(9, 'ROLE9', N'Quyền quản trị 9'),
(10, 'ROLE10', N'Quyền quản trị 10');

-------------------------------------
-- 2. Bảng tai_khoan (10 dòng)
-------------------------------------
INSERT INTO tai_khoan (ma_tai_khoan, ten_dang_nhap, mat_khau) VALUES
('TK001', 'user1', 'pass1'),
('TK002', 'user2', 'pass2'),
('TK003', 'user3', 'pass3'),
('TK004', 'user4', 'pass4'),
('TK005', 'user5', 'pass5'),
('TK006', 'user6', 'pass6'),
('TK007', 'user7', 'pass7'),
('TK008', 'user8', 'pass8'),
('TK009', 'user9', 'pass9'),
('TK010', 'user10', 'pass10');

-------------------------------------
-- 3. Bảng lich_su_dang_nhap (10 dòng)
-------------------------------------
INSERT INTO lich_su_dang_nhap (id_tai_khoan, ngay_dang_nhap, ip_adress) VALUES
(1, '2025-02-01', '192.168.1.1'),
(2, '2025-02-02', '192.168.1.2'),
(3, '2025-02-03', '192.168.1.3'),
(4, '2025-02-04', '192.168.1.4'),
(5, '2025-02-05', '192.168.1.5'),
(6, '2025-02-06', '192.168.1.6'),
(7, '2025-02-07', '192.168.1.7'),
(8, '2025-02-08', '192.168.1.8'),
(9, '2025-02-09', '192.168.1.9'),
(10, '2025-02-10', '192.168.1.10');

-------------------------------------
-- 4. Bảng nhan_vien (10 dòng)
-------------------------------------
INSERT INTO nhan_vien (id_roles, id_tai_khoan, ma_nhan_vien, ten_nhan_vien, ngay_sinh, email, dia_chi_lien_he, gioi_tinh, so_dien_thoai, trang_thai) VALUES
(1, 1, 'NV001', N'Nguyễn Văn A', '1980-01-01', 'nv1@example.com', N'Hà Nội', 1, '0901000001', N'Hoạt động'),
(2, 2, 'NV002', N'Trần Thị B', '1981-02-02', 'nv2@example.com', N'Hồ Chí Minh', 0, '0901000002', N'Hoạt động'),
(3, 3, 'NV003', N'Lê Văn C', '1982-03-03', 'nv3@example.com', N'Đà Nẵng', 1, '0901000003', N'Hoạt động'),
(4, 4, 'NV004', N'Phạm Thị D', '1983-04-04', 'nv4@example.com', N'Biên Hòa', 0, '0901000004', N'Hoạt động'),
(5, 5, 'NV005', N'Hồ Văn E', '1984-05-05', 'nv5@example.com', N'Huế', 1, '0901000005', N'Hoạt động'),
(6, 6, 'NV006', N'Đinh Thị F', '1985-06-06', 'nv6@example.com', N'Nha Trang', 0, '0901000006', N'Hoạt động'),
(7, 7, 'NV007', N'Vũ Văn G', '1986-07-07', 'nv7@example.com', N'Vũng Tàu', 1, '0901000007', N'Hoạt động'),
(8, 8, 'NV008', N'Phan Thị H', '1987-08-08', 'nv8@example.com', N'Bến Tre', 0, '0901000008', N'Hoạt động'),
(9, 9, 'NV009', N'Bùi Văn I', '1988-09-09', 'nv9@example.com', N'Cần Thơ', 1, '0901000009', N'Hoạt động'),
(10, 10, 'NV010', N'Ngô Thị K', '1989-10-10', 'nv10@example.com', N'Hải Phòng', 0, '0901000010', N'Hoạt động');

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
INSERT INTO khuyen_mai (ma_khuyen_mai, ten_khuyen_mai, mo_ta, ngay_bat_dau, ngay_het_han, gia_tri_giam, kieu_giam_gia, trang_thai) VALUES
('KM001', N'Khuyến mãi 1', N'Mô tả KM 1', '2025-02-01', '2025-02-28', 10, N'%', N'Hoạt động'),
('KM002', N'Khuyến mãi 2', N'Mô tả KM 2', '2025-02-02', '2025-02-28', 15, N'%', N'Hoạt động'),
('KM003', N'Khuyến mãi 3', N'Mô tả KM 3', '2025-02-03', '2025-02-28', 20, N'%', N'Hoạt động'),
('KM004', N'Khuyến mãi 4', N'Mô tả KM 4', '2025-02-04', '2025-02-28', 25, N'%', N'Hoạt động'),
('KM005', N'Khuyến mãi 5', N'Mô tả KM 5', '2025-02-05', '2025-02-28', 30, N'%', N'Hoạt động'),
('KM006', N'Khuyến mãi 6', N'Mô tả KM 6', '2025-02-06', '2025-02-28', 35, N'%', N'Hoạt động'),
('KM007', N'Khuyến mãi 7', N'Mô tả KM 7', '2025-02-07', '2025-02-28', 40, N'%', N'Hoạt động'),
('KM008', N'Khuyến mãi 8', N'Mô tả KM 8', '2025-02-08', '2025-02-28', 45, N'%', N'Hoạt động'),
('KM009', N'Khuyến mãi 9', N'Mô tả KM 9', '2025-02-09', '2025-02-28', 50, N'%', N'Hoạt động'),
('KM010', N'Khuyến mãi 10', N'Mô tả KM 10', '2025-02-10', '2025-02-28', 55, N'%', N'Hoạt động');

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
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10);

-------------------------------------
-- 15. Bảng khach_hang (10 dòng)
-------------------------------------
INSERT INTO khach_hang (ma_khach_hang, ten_khach_hang, gioi_tinh, so_dien_thoai, ngay_sinh, email, id_tai_khoan, trang_thai) VALUES
('KH001', N'Nguyễn Văn M', 1, '0910000001', '1990-01-01', 'kh1@example.com', 1, N'Hoạt động'),
('KH002', N'Phạm Thị N', 0, '0910000002', '1991-02-02', 'kh2@example.com', 2, N'Hoạt động'),
('KH003', N'Lê Văn O', 1, '0910000003', '1992-03-03', 'kh3@example.com', 3, N'Hoạt động'),
('KH004', N'Trần Thị P', 0, '0910000004', '1993-04-04', 'kh4@example.com', 4, N'Hoạt động'),
('KH005', N'Vũ Văn Q', 1, '0910000005', '1994-05-05', 'kh5@example.com', 5, N'Hoạt động'),
('KH006', N'Đỗ Thị R', 0, '0910000006', '1995-06-06', 'kh6@example.com', 6, N'Hoạt động'),
('KH007', N'Phạm Văn S', 1, '0910000007', '1996-07-07', 'kh7@example.com', 7, N'Hoạt động'),
('KH008', N'Nguyễn Thị T', 0, '0910000008', '1997-08-08', 'kh8@example.com', 8, N'Hoạt động'),
('KH009', N'Trần Văn U', 1, '0910000009', '1998-09-09', 'kh9@example.com', 9, N'Hoạt động'),
('KH010', N'Lê Thị V', 0, '0910000010', '1999-10-10', 'kh10@example.com', 10, N'Hoạt động');

-------------------------------------
-- 16. Bảng gio_hang (10 dòng)
-------------------------------------
INSERT INTO gio_hang (id_khach_hang) VALUES
(1),
(2),
(3),
(4),
(5),
(6),
(7),
(8),
(9),
(10);

-------------------------------------
-- 17. Bảng chi_tiet_gio_hang (10 dòng)
-------------------------------------
INSERT INTO chi_tiet_gio_hang (id_gio_hang, id_chi_tiet_san_pham, so_luong) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 1),
(4, 4, 1),
(5, 5, 2),
(6, 6, 1),
(7, 7, 3),
(8, 8, 1),
(9, 9, 2),
(10, 10, 1);

-------------------------------------
-- 18. Bảng binh_luan (10 dòng) - khóa chính (id_khach_hang, id_chi_tiet_san_pham)
-------------------------------------
INSERT INTO binh_luan (id_khach_hang, id_chi_tiet_san_pham, binh_luan, danh_gia, ngay_binh_luan, ngay_sua) VALUES
(1, 1, N'Bình luận của khách 1', 4.5, '2025-02-01', '2025-02-01'),
(2, 2, N'Bình luận của khách 2', 4.0, '2025-02-02', '2025-02-02'),
(3, 3, N'Bình luận của khách 3', 5.0, '2025-02-03', '2025-02-03'),
(4, 4, N'Bình luận của khách 4', 3.5, '2025-02-04', '2025-02-04'),
(5, 5, N'Bình luận của khách 5', 4.2, '2025-02-05', '2025-02-05'),
(6, 6, N'Bình luận của khách 6', 4.8, '2025-02-06', '2025-02-06'),
(7, 7, N'Bình luận của khách 7', 4.0, '2025-02-07', '2025-02-07'),
(8, 8, N'Bình luận của khách 8', 3.8, '2025-02-08', '2025-02-08'),
(9, 9, N'Bình luận của khách 9', 5.0, '2025-02-09', '2025-02-09'),
(10, 10, N'Bình luận của khách 10', 4.7, '2025-02-10', '2025-02-10');

-------------------------------------
-- 19. Bảng danh_sach_yeu_thich (10 dòng) - khóa chính (id_khach_hang, id_chi_tiet_san_pham)
-------------------------------------
INSERT INTO danh_sach_yeu_thich (id_khach_hang, id_chi_tiet_san_pham, ngay_them) VALUES
(1, 1, '2025-02-01'),
(2, 2, '2025-02-02'),
(3, 3, '2025-02-03'),
(4, 4, '2025-02-04'),
(5, 5, '2025-02-05'),
(6, 6, '2025-02-06'),
(7, 7, '2025-02-07'),
(8, 8, '2025-02-08'),
(9, 9, '2025-02-09'),
(10, 10, '2025-02-10');

-------------------------------------
-- 20. Bảng dia_chi_khach_hang (10 dòng)
-------------------------------------
INSERT INTO dia_chi_khach_hang (id_khach_hang, dia_chi_khach_hang) VALUES
(1, N'123 Đường A, Hà Nội'),
(2, N'456 Đường B, Hồ Chí Minh'),
(3, N'789 Đường C, Đà Nẵng'),
(4, N'101 Đường D, Cần Thơ'),
(5, N'202 Đường E, Huế'),
(6, N'303 Đường F, Nha Trang'),
(7, N'404 Đường G, Biên Hòa'),
(8, N'505 Đường H, Hải Phòng'),
(9, N'606 Đường I, Vũng Tàu'),
(10, N'707 Đường J, Bình Dương');

-------------------------------------
-- 21. Bảng voucher (10 dòng)
-------------------------------------
INSERT INTO voucher (ma_voucher, ten_voucher, ngay_tao, ngay_het_han, gia_tri_giam, gia_tri_toi_thieu, trang_thai, so_luong, kieu_giam_gia, mo_ta, gia_tri_toi_da) VALUES
('VC001', N'Voucher 1', '2025-01-01', '2025-12-31', 50000, 200000, N'Hoạt động', 100, N'Giảm cố định', N'Mô tả voucher 1', 300000),
('VC002', N'Voucher 2', '2025-01-02', '2025-12-31', 60000, 200000, N'Hoạt động', 90, N'Giảm cố định', N'Mô tả voucher 2', 300000),
('VC003', N'Voucher 3', '2025-01-03', '2025-12-31', 70000, 200000, N'Hoạt động', 80, N'Giảm cố định', N'Mô tả voucher 3', 300000),
('VC004', N'Voucher 4', '2025-01-04', '2025-12-31', 80000, 200000, N'Hoạt động', 70, N'Giảm cố định', N'Mô tả voucher 4', 300000),
('VC005', N'Voucher 5', '2025-01-05', '2025-12-31', 90000, 200000, N'Hoạt động', 60, N'Giảm cố định', N'Mô tả voucher 5', 300000),
('VC006', N'Voucher 6', '2025-01-06', '2025-12-31', 100000, 200000, N'Hoạt động', 50, N'Giảm cố định', N'Mô tả voucher 6', 300000),
('VC007', N'Voucher 7', '2025-01-07', '2025-12-31', 110000, 200000, N'Hoạt động', 40, N'Giảm cố định', N'Mô tả voucher 7', 300000),
('VC008', N'Voucher 8', '2025-01-08', '2025-12-31', 120000, 200000, N'Hoạt động', 30, N'Giảm cố định', N'Mô tả voucher 8', 300000),
('VC009', N'Voucher 9', '2025-01-09', '2025-12-31', 130000, 200000, N'Hoạt động', 20, N'Giảm cố định', N'Mô tả voucher 9', 300000),
('VC010', N'Voucher 10', '2025-01-10', '2025-12-31', 140000, 200000, N'Hoạt động', 10, N'Giảm cố định', N'Mô tả voucher 10', 300000);

-------------------------------------
-- 22. Bảng hoa_don (10 dòng)
-------------------------------------
INSERT INTO hoa_don (ma_hoa_don, id_nhan_vien, id_khach_hang, ngay_tao, ngay_sua, trang_thai, id_voucher, sdt_nguoi_nhan, dia_chi, email, tong_tien_truoc_giam, phi_van_chuyen, ho_ten, tong_tien_sau_giam, hinh_thuc_thanh_toan, phuong_thuc_nhan_hang) VALUES
('HD001', 1, 1, '2025-02-01', '2025-02-01', N'Chờ xử lý', 1, '0910000001', N'123 Đường A, Hà Nội', 'kh1@example.com', 1000000, 50000, N'Nguyễn Văn M', 950000, N'Tiền mặt', N'Giao hàng'),
('HD002', 2, 2, '2025-02-02', '2025-02-02', N'Chờ xử lý', 2, '0910000002', N'456 Đường B, Hồ Chí Minh', 'kh2@example.com', 2000000, 60000, N'Phạm Thị N', 1940000, N'Chuyển khoản', N'Nhận tại cửa hàng'),
('HD003', 3, 3, '2025-02-03', '2025-02-03', N'Chờ xử lý', 3, '0910000003', N'789 Đường C, Đà Nẵng', 'kh3@example.com', 3000000, 70000, N'Lê Văn O', 2930000, N'Tiền mặt', N'Giao hàng'),
('HD004', 4, 4, '2025-02-04', '2025-02-04', N'Chờ xử lý', 4, '0910000004', N'101 Đường D, Cần Thơ', 'kh4@example.com', 4000000, 80000, N'Trần Thị P', 3920000, N'Chuyển khoản', N'Nhận tại cửa hàng'),
('HD005', 5, 5, '2025-02-05', '2025-02-05', N'Chờ xử lý', 5, '0910000005', N'202 Đường E, Huế', 'kh5@example.com', 5000000, 90000, N'Vũ Văn Q', 4910000, N'Tiền mặt', N'Giao hàng'),
('HD006', 6, 6, '2025-02-06', '2025-02-06', N'Chờ xử lý', 6, '0910000006', N'303 Đường F, Nha Trang', 'kh6@example.com', 6000000, 100000, N'Đỗ Thị R', 5900000, N'Chuyển khoản', N'Nhận tại cửa hàng'),
('HD007', 7, 7, '2025-02-07', '2025-02-07', N'Chờ xử lý', 7, '0910000007', N'404 Đường G, Biên Hòa', 'kh7@example.com', 7000000, 110000, N'Phạm Văn S', 6890000, N'Tiền mặt', N'Giao hàng'),
('HD008', 8, 8, '2025-02-08', '2025-02-08', N'Chờ xử lý', 8, '0910000008', N'505 Đường H, Hải Phòng', 'kh8@example.com', 8000000, 120000, N'Nguyễn Thị T', 7880000, N'Chuyển khoản', N'Nhận tại cửa hàng'),
('HD009', 9, 9, '2025-02-09', '2025-02-09', N'Chờ xử lý', 9, '0910000009', N'606 Đường I, Vũng Tàu', 'kh9@example.com', 9000000, 130000, N'Trần Văn U', 8870000, N'Tiền mặt', N'Giao hàng'),
('HD010', 10, 10, '2025-02-10', '2025-02-10', N'Chờ xử lý', 10, '0910000010', N'707 Đường J, Bình Dương', 'kh10@example.com', 10000000, 140000, N'Lê Thị V', 9860000, N'Chuyển khoản', N'Nhận tại cửa hàng');

-------------------------------------
-- 23. Bảng hoa_don_chi_tiet (10 dòng)
-------------------------------------
INSERT INTO hoa_don_chi_tiet (id_hoa_don, id_chi_tiet_san_pham, so_luong, don_gia) VALUES
(1, 1, 1, 100000),
(2, 2, 2, 200000),
(3, 3, 1, 300000),
(4, 4, 1, 400000),
(5, 5, 2, 500000),
(6, 6, 1, 600000),
(7, 7, 3, 700000),
(8, 8, 1, 800000),
(9, 9, 2, 900000),
(10, 10, 1, 1000000);

-------------------------------------
-- 24. Bảng theo_doi_don_hang (10 dòng)
-------------------------------------
INSERT INTO theo_doi_don_hang (id_hoa_don, trang_thai, ngay_chuyen) VALUES
(1, N'Đang xử lý', '2025-02-02'),
(2, N'Đang xử lý', '2025-02-03'),
(3, N'Đang xử lý', '2025-02-04'),
(4, N'Đang xử lý', '2025-02-05'),
(5, N'Đang xử lý', '2025-02-06'),
(6, N'Đang xử lý', '2025-02-07'),
(7, N'Đang xử lý', '2025-02-08'),
(8, N'Đang xử lý', '2025-02-09'),
(9, N'Đang xử lý', '2025-02-10'),
(10, N'Đang xử lý', '2025-02-11');

-------------------------------------
-- 25. Bảng yeu_cau_doi_hang (10 dòng)
-------------------------------------
INSERT INTO yeu_cau_doi_hang (ma_yeu_cau, id_hoa_don, id_san_pham_moi, trang_thai_san_pham, ngay_yeu_cau, ngay_xu_ly, ly_do_doi) VALUES
('YC001', 1, 1, N'Mới', '2025-02-03', '2025-02-04', N'Lỗi sản phẩm'),
('YC002', 2, 2, N'Mới', '2025-02-04', '2025-02-05', N'Lỗi sản phẩm'),
('YC003', 3, 3, N'Mới', '2025-02-05', '2025-02-06', N'Lỗi sản phẩm'),
('YC004', 4, 4, N'Mới', '2025-02-06', '2025-02-07', N'Lỗi sản phẩm'),
('YC005', 5, 5, N'Mới', '2025-02-07', '2025-02-08', N'Lỗi sản phẩm'),
('YC006', 6, 6, N'Mới', '2025-02-08', '2025-02-09', N'Lỗi sản phẩm'),
('YC007', 7, 7, N'Mới', '2025-02-09', '2025-02-10', N'Lỗi sản phẩm'),
('YC008', 8, 8, N'Mới', '2025-02-10', '2025-02-11', N'Lỗi sản phẩm'),
('YC009', 9, 9, N'Mới', '2025-02-11', '2025-02-12', N'Lỗi sản phẩm'),
('YC010', 10, 10, N'Mới', '2025-02-12', '2025-02-13', N'Lỗi sản phẩm');

-------------------------------------
-- 26. Bảng hinh_anh_doi_hang (10 dòng)
-------------------------------------
INSERT INTO hinh_anh_doi_hang (id_yeu_cau, hinh_anh) VALUES
(1, 'doi_img1.jpg'),
(2, 'doi_img2.jpg'),
(3, 'doi_img3.jpg'),
(4, 'doi_img4.jpg'),
(5, 'doi_img5.jpg'),
(6, 'doi_img6.jpg'),
(7, 'doi_img7.jpg'),
(8, 'doi_img8.jpg'),
(9, 'doi_img9.jpg'),
(10, 'doi_img10.jpg');

-------------------------------------
-- 27. Bảng phieu_doi_hang (10 dòng)
-------------------------------------
INSERT INTO phieu_doi_hang (id_yeu_cau, ma_phieu_doi, phuong_thuc_thanh_toan, ngay_xuat_phieu, trang_thai, gia_tri_chenh_lech) VALUES
(1, 'PDH001', N'Tiền mặt', '2025-02-05', N'Đã xuất', 10000),
(2, 'PDH002', N'Chuyển khoản', '2025-02-06', N'Đã xuất', 20000),
(3, 'PDH003', N'Tiền mặt', '2025-02-07', N'Đã xuất', 30000),
(4, 'PDH004', N'Chuyển khoản', '2025-02-08', N'Đã xuất', 40000),
(5, 'PDH005', N'Tiền mặt', '2025-02-09', N'Đã xuất', 50000),
(6, 'PDH006', N'Chuyển khoản', '2025-02-10', N'Đã xuất', 60000),
(7, 'PDH007', N'Tiền mặt', '2025-02-11', N'Đã xuất', 70000),
(8, 'PDH008', N'Chuyển khoản', '2025-02-12', N'Đã xuất', 80000),
(9, 'PDH009', N'Tiền mặt', '2025-02-13', N'Đã xuất', 90000),
(10, 'PDH010', N'Chuyển khoản', '2025-02-14', N'Đã xuất', 100000);

