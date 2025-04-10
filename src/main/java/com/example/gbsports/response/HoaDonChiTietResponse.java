package com.example.gbsports.response;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface HoaDonChiTietResponse {
    Integer getId_hoa_don();
    String getMa_hoa_don();
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getNgay_tao();
    String getHo_ten();
    String getSdt_nguoi_nhan();
    String getDia_chi();
    String getEmail();
    BigDecimal getTong_tien_truoc_giam();
    BigDecimal getPhi_van_chuyen();
    BigDecimal getTong_tien_sau_giam();
    String getHinh_thuc_thanh_toan();
    String getPhuong_thuc_nhan_hang();

    // Chi tiết sản phẩm
    Integer getId_hoa_don_chi_tiet();
    Integer getId_chi_tiet_san_pham();
    Integer getSo_luong();
    Integer getSo_luong_con_lai();
    BigDecimal getDon_gia();
    String getTen_san_pham();
    String getMa_san_pham();
    String getGia_tri();
    String getTen_mau_sac();
    String getHinh_anh();
    Boolean getAnh_chinh();
    Integer getId_nhan_vien();
    String getTen_nhan_vien();
    Integer getId_khach_hang();
    String getTen_khach_hang();
    Integer getId_voucher();
    String getTen_voucher();
    String getMa_voucher();
    String getTrang_thai();
    BigDecimal getGia_ban();
    Integer getSo_luong_ton();
    String getLoai_hoa_don();
    String getTrang_thai_thanh_toan();
    String getGhi_chu();
    String getKich_thuoc();

    String getDon_vi();
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getNgay_chuyen();



}
