package com.example.gbsports.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonResponses {
    private Integer id_hoa_don;
    private String ma_hoa_don;
    private Integer id_nhan_vien;
    private String ten_nhan_vien;
    private LocalDateTime ngay_tao;
    private LocalDateTime ngay_sua;
    private String trang_thai;
    private String sdt_nguoi_nhan;
    private String dia_chi;
    private String ma_voucher;
    private String email;
    private BigDecimal tong_tien_truoc_giam;
    private BigDecimal phi_van_chuyen;
    private String ho_ten;
    private BigDecimal tong_tien_sau_giam;
    private String hinh_thuc_thanh_toan;
    private String phuong_thuc_nhan_hang;
    private Integer id_khach_hang;
    private String ten_khach_hang;
    private Integer id_voucher;
    private String ten_voucher;
    private String ghi_chu;
    private String loai_hoa_don;
    private BigDecimal gia_tri_giam;
    private String kieu_giam_gia;
    private String trang_thai_don_hang;
    private String trang_thai_tra_hang;
}
