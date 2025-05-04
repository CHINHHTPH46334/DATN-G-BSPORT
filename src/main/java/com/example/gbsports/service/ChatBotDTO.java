package com.example.gbsports.service;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChatBotDTO {
    private Integer id;
    private String ten;
    private String moTa;
    private BigDecimal gia;
    private String mauSac;
    private String kichThuoc;
    private String chatLieu;
    private String thuongHieu;
    private String danhMuc;
    private Integer soLuong;
    private String trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngaySua;
    private String hinhAnh;
}