package com.example.gbsports.request;

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
public class KhuyenMaiRequetst {
    private Integer id;
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private String moTa;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayHetHan;
    private BigDecimal giaTriGiam;
    private String kieuGiamGia;
    private String trangThai;
    private BigDecimal giaTriToiDa;
}