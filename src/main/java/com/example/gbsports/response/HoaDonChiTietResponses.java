package com.example.gbsports.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoaDonChiTietResponses {
    private Integer id_chi_tiet_san_pham;
    private String ten_san_pham;
    private Integer so_luong;
    private Integer so_luong_da_tra;
    private String trang_thai_tra_hang;
    private String ly_do_tra_hang;
    private String hinh_anh;
    private String kich_thuoc;
    private String ten_mau_sac;
    private BigDecimal don_gia;

    // Thông tin khách hàng
    private String ho_ten;
    private String dia_chi;
    private String sdt_nguoi_nhan;
    private String ten_khach_hang;
    private String email;
}
