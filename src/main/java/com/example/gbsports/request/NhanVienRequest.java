package com.example.gbsports.request;

import com.example.gbsports.entity.Roles;
import com.example.gbsports.entity.TaiKhoan;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class NhanVienRequest {
    private Integer id_nhan_vien;
    private Integer id_roles;
    private Integer id_tai_khoan;
    private String ma_nhan_vien;
    private String ten_nhan_vien;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngay_sinh;
    private String email;
    private String dia_chi_lien_he;
    private boolean gioi_tinh;
    private String so_dien_thoai;
    private String trang_thai;
    private Roles roles;
    private TaiKhoan taiKhoan;
}
