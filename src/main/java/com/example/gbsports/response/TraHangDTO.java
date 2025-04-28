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
public class TraHangDTO {
    private Integer id;
    private Integer id_hoa_don;
    private String ly_do;
    private String ghi_chu;
    private String nhan_vien_xu_ly;
    private LocalDateTime ngay_tao;
    private String trang_thai;
    private BigDecimal tong_tien_hoan;
}
