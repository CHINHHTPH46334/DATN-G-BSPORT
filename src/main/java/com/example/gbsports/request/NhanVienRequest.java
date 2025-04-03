package com.example.gbsports.request;

import com.example.gbsports.entity.TaiKhoan;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NhanVienRequest {
    private Integer idNhanVien;
    private String maNhanVien;
    private String tenNhanVien;
    private Date ngaySinh;
    private String email;
    private String diaChiLienHe;
    private Boolean gioiTinh;
    private String anhNhanVien;
    private Date ngayThamGia;
    private String soDienThoai;
    private String trangThai;
    private TaiKhoan taiKhoan;

}
