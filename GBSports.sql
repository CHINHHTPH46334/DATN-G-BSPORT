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
id_tai_khoan int primary key,
ma_tai_khoan varchar(50),
ten_dang_nhap varchar(100),
mat_khau varchar(200)
);
--Lịch sử đăng nhập
create table lich_su_dang_nhap(
id_lich_su_dang_nhap int primary key,
id_tai_khoan int references tai_khoan(id_tai_khoan),
ngay_dang_nhap date,
ip_adress varchar(200)
);
--Nhân viên
create table nhan_vien(
id_nhan_vien int primary key,
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
id_danh_muc int primary key,
ma_danh_muc varchar(50),
ten_danh_muc nvarchar(200),
trang_thai nvarchar(50),
ngay_tao date,
ngay_sua date
);
--Chất liệu
create table chat_lieu(
id_chat_lieu int primary key,
ma_chat_lieu varchar(50),
ten_chat_lieu nvarchar(200)
);
--Thương hiệu
create table thuong_hieu(
id_thuong_hieu int primary key,
ma_thuong_hieu varchar(50),
ten_thuong_hieu nvarchar(200),
trang_thai nvarchar(50),
ngay_tao date,
ngay_sua date
);
--Sản phẩm
create table san_pham(
id_san_pham int primary key,
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
id_khuyen_mai int primary key,
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
id_kich_thuoc int primary key,
ma_kich_thuoc varchar(50),
gia_tri varchar(200),
don_vi nvarchar(50)
);
--Màu sắc
create table mau_sac(
id_mau_sac int primary key,
ma_mau_sac varchar(50),
ten_mau_sac nvarchar(50)
);
--Chi tiết sản phẩm
create table chi_tiet_san_pham(
id_chi_tiet_san_pham int primary key,
id_san_pham int references san_pham(id_san_pham),
qr_code varchar(200),
gia_ban decimal,
so_luong int,
trang_thai nvarchar(50),
ngay_tao date,
ngay_sua date,
gia_nhap decimal,
id_kich_thuoc int references kich_thuoc(id_kich_thuoc),
id_mau_sac int references mau_sac(id_mau_sac)
);
--Hình ảnh
create table hinh_anh(
id_hinh_anh int primary key,
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
id_khach_hang int primary key,
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
id_gio_hang int primary key,
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
id_dia_chi_khach_hang int primary key,
id_khach_hang int references khach_hang(id_khach_hang)
);
--Voucher
create table voucher(
id_voucher int primary key,
ma_voucher varchar(50),
ten_voucher nvarchar(200),
ngay_tao date,
ngay_het_han date,
gia_tri_giam decimal,
gia_tri_toi_thieu decimal,
trang_thai nvarchar(50),
so_luong int,
kieu_giam_gia nvarchar(200),
mo_ta nvarchar(250),
gia_tri_toi_da decimal
);
--Hóa đơn
create table hoa_don(
id_hoa_don int primary key,
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
tong_tien_truoc_giam decimal,
phi_van_chuyen decimal,
ho_ten nvarchar(250),
tong_tien_sau_giam decimal,
hinh_thuc_thanh_toan nvarchar(50)
);
--Hóa đơn chi tiết
create table hoa_don_chi_tiet(
id_hoa_don_chi_tiet int,
id_hoa_don int references hoa_don(id_hoa_don),
id_chi_tiet_san_pham int references chi_tiet_san_pham(id_chi_tiet_san_pham),
so_luong int,
don_gia decimal
primary key(id_hoa_don_chi_tiet, id_hoa_don, id_chi_tiet_san_pham)
);
--Theo dõi đơn hàng
create table theo_doi_don_hang(
id_don_hang int primary key,
id_hoa_don int references hoa_don(id_hoa_don),
trang_thai nvarchar(50)
);
--Yêu cầu đổi hàng
create table yeu_cau_doi_hang(
id_yeu_cau int primary key,
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
id_hinh_anh_doi int primary key,
id_yeu_cau int references yeu_cau_doi_hang(id_yeu_cau),
hinh_anh varchar(250)
);
--Phiếu đổi hàng
create table phieu_doi_hang(
id_phieu_doi int primary key,
ma_phieu_doi varchar(50),
phuong_thuc_thanh_toan nvarchar(50),
ngay_xuat_phieu date,
trang_thai nvarchar(50),
gia_tri_chenh_lech decimal
);