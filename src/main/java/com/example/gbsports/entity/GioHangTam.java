package com.example.gbsports.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GioHangTam {
    private List<ChiTietGioHangTam> chiTietGioHangs = new ArrayList<>();

    @Data
    public static class ChiTietGioHangTam {
        private Integer idChiTietSanPham;
        private Integer soLuong;
    }
}