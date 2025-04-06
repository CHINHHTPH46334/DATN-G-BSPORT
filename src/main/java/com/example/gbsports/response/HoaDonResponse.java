package com.example.gbsports.response;

import com.example.gbsports.entity.NhanVien;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

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
    String getSdt_nguoi_nhan();
    String getDia_chi();
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
    String getMa_voucher();
    LocalDateTime getNgay_chuyen();
    String getGhi_chu();
    String getLoai_hoa_don();

    String getMa_san_pham();
    String getTen_san_pham();
    Integer getSo_luong();
    BigDecimal getGia_ban();
    String getTrangThaiDonHang();
    Float getTiLeTrangThaiDonHang();
}
