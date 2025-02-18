package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Entity
@Table(name = "nhan_vien")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_nhan_vien;
    private String ma_nhan_vien;
    private String ten_nhan_vien;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngay_sinh;
    private String email;
    private String dia_chi_lien_he;
    private Boolean gioi_tinh;
    private String so_dien_thoai;
    private String trang_thai;
    @ManyToOne
    @JoinColumn(name = "id_roles")
    private Roles roles;
    @ManyToOne
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan taiKhoan;

}
