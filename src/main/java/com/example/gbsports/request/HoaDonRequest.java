package com.example.gbsports.request;

import com.example.gbsports.entity.KhachHang;
import com.example.gbsports.entity.NhanVien;
import com.example.gbsports.entity.Voucher;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class HoaDonRequest {
    private Integer id_hoa_don;
    private String ma_hoa_don;
    private NhanVien nhanVien;
    private KhachHang khachHang;
    private String trang_thai;
    private Voucher voucher;
    private String sdt_nguoi_nhan;
    private String dia_chi;
    private String email;
    private float tong_tien_truoc_giam;
    private float phi_van_chuyen;
    private String ho_ten;
    private float tong_tien_sau_giam;
    private String hinh_thuc_thanh_toan;
    private String phuong_thuc_nhan_hang;

    private Integer id_nhan_vien;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngay_tao;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngay_sua;
}
