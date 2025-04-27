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
public class TraHangResponse {
    private boolean thanh_cong;
    private String thong_bao;
    private Integer id_tra_hang;

}
