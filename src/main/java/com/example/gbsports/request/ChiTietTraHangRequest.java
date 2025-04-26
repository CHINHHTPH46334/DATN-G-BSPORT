package com.example.gbsports.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietTraHangRequest {
    private Integer id_chi_tiet_san_pham;
    private Integer so_luong;
    private BigDecimal so_tien_hoan;
}
