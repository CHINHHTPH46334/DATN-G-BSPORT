package com.example.gbsports.response;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface HoaDonResponse {
    Integer getId_hoa_don();

    String getMa_hoa_don();

    Integer getId_nhan_vien();

    String getTen_nhan_vien();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getNgay_tao();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getNgay_sua();

    String getTrang_thai();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getNgay_chuyen();

    String getSdt_nguoi_nhan();

    String getDia_chi();

    String getMa_voucher();

    String getEmail();

    BigDecimal getTong_tien_truoc_giam();

    BigDecimal getPhi_van_chuyen();

    String getHo_ten();

    BigDecimal getTong_tien_sau_giam();

    String getHinh_thuc_thanh_toan();

    String getPhuong_thuc_nhan_hang();

    Integer getId_khach_hang();

    String getTen_khach_hang();

    Integer getId_voucher();

    String getTen_voucher();

    String getGhi_chu();

    String getLoai_hoa_don();
    String getMa_san_pham();

    String getTen_san_pham();

    Integer getSo_luong();
    BigDecimal getGia_ban();
    String getTrangThaiDonHang();
    Float getTiLeTrangThaiDonHang();

    String getTrang_thai_thanh_toan();

    String getMo_ta();

    BigDecimal getGia_tri_giam();

    String getKieu_giam_gia();
    // Trường mới liên quan đến trả hàng
    String getTrang_thai_tra_hang();
    // New fields for return details
    BigDecimal getTong_tien_hoan();

    List<ChiTietTraHangResponse> getChiTietTraHangs();
}
