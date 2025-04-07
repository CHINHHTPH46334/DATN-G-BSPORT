package com.example.gbsports.response;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface VoucherBHResponse {
    Integer getId_hoa_don();

    Integer getId_voucher();

    String getMa_voucher();

    String getTen_voucher();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getNgay_tao();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getNgay_het_han();

    BigDecimal getGia_tri_giam();

    BigDecimal getGia_tri_toi_thieu();

    String getTrang_thai();

    Integer getSo_luong();

    String getKieu_giam_gia();

    String getMo_ta();

    BigDecimal getGia_tri_toi_da();

    BigDecimal getGia_tri_giam_thuc_te();

}
