package com.example.gbsports.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "hoa_don")
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_hoa_don;
    private String ma_hoa_don;
    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    @JsonIgnore
    private NhanVien nhanVien;
    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    @JsonIgnore
    private KhachHang khachHang;
    @Column(name = "ngay_tao")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngay_tao;
    @Column(name = "ngay_sua")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngay_sua;
    private String trang_thai;
    @ManyToOne
    @JoinColumn(name = "id_voucher")
    @JsonIgnore
    private Voucher voucher;
    private String sdt_nguoi_nhan;
    private String dia_chi;
    private String email;
    private BigDecimal tong_tien_truoc_giam;
    private BigDecimal phi_van_chuyen;
    private String ho_ten;
    private BigDecimal tong_tien_sau_giam;
    private String hinh_thuc_thanh_toan;
    private String phuong_thuc_nhan_hang;
    @Column(name = "loai_hoa_don")
    private String loai_hoa_don;
    private String ghi_chu;
    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TheoDoiDonHang> lichSuTrangThai = new ArrayList<>();
    private String loai_hoa_don;
    private String ghi_chu;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<HoaDonChiTiet> danhSachChiTiet = new ArrayList<>();

}
