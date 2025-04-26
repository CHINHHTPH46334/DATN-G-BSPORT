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
public class ChiTietTraHangDTO {
    private Integer id;
    private Integer id_tra_hang;
    private Integer id_chi_tiet_san_pham;
    private Integer so_luong;
    private BigDecimal tien_hoan;
    private BigDecimal don_gia;
    private String ten_san_pham;
    private String hinh_anh;
    private String kich_thuoc;
    private String ten_mau_sac;
}
