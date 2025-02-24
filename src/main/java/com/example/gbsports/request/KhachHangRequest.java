package com.example.gbsports.request;

import com.example.gbsports.entity.TaiKhoan;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class KhachHangRequest {
    private Integer idKhachHang;
    private Integer idTaiKhoan;
    private String maKhachHang;
    private String tenKhachHang;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySinh;

    private String email;
    private Boolean gioiTinh;
    private String soDienThoai;
    private String trangThai;
    private TaiKhoan taiKhoan;
}
