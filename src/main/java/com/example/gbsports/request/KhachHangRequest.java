package com.example.gbsports.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class KhachHangRequest {
    private Integer idKhachHang;
    private String maKhachHang;
    private String tenKhachHang;
    private Boolean gioiTinh;
    private String soDienThoai;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date ngaySinh;
    private String email;
    private String trangThai;
    private String matKhau;
    private List<DiaChiRequest> diaChiList = new ArrayList<>();
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngayTao;

    // Setter với trim
    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang != null ? maKhachHang.trim() : null;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang != null ? tenKhachHang.replaceAll("\\s+", " ").trim() : null;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai != null ? soDienThoai.replaceAll("\\s+", "").trim() : null;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.replaceAll("\\s+", "").trim() : null;
    }

    @Data
    public static class DiaChiRequest {
        private String soNha;
        private String xaPhuong;
        private String quanHuyen;
        private String tinhThanhPho;
        private Boolean diaChiMacDinh;

        // Setter với trim
        public void setSoNha(String soNha) {
            this.soNha = soNha != null ? soNha.replaceAll("\\s+", " ").trim() : null;
        }

        public void setXaPhuong(String xaPhuong) {
            this.xaPhuong = xaPhuong != null ? xaPhuong.replaceAll("\\s+", " ").trim() : null;
        }

        public void setQuanHuyen(String quanHuyen) {
            this.quanHuyen = quanHuyen != null ? quanHuyen.replaceAll("\\s+", " ").trim() : null;
        }

        public void setTinhThanhPho(String tinhThanhPho) {
            this.tinhThanhPho = tinhThanhPho != null ? tinhThanhPho.replaceAll("\\s+", " ").trim() : null;
        }
    }
}