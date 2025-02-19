package com.example.gbsports.request;

import com.example.gbsports.entity.NhanVien;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class HoaDonRequest {
    private Integer id_hoa_don;
    private String ma_hoa_don;
    private NhanVien nhanVien;
    private Integer id_nhan_vien;
    //    id_khach_hang int references khach_hang(id_khach_hang),
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngay_tao;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngay_sua;
    private String trang_thai;
    //    id_voucher int references voucher(id_voucher),
    private String sdt_nguoi_nhan;
    private String dia_chi;
    private String email;
    private float tong_tien_truoc_giam;
    private float phi_van_chuyen;
    private String ho_ten;
    private float tong_tien_sau_giam;
    private String hinh_thuc_thanh_toan;
    private String phuong_thuc_nhan_hang;
}
