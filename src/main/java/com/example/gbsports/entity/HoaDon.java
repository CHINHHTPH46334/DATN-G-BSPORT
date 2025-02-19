package com.example.gbsports.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_hoa_don;
    private String ma_hoa_don;
    //    id_nhan_vien int references nhan_vien(id_nhan_vien),
//    id_khach_hang int references khach_hang(id_khach_hang),
    private Date ngay_tao;
    private Date ngay_sua;
    private String trang_thai;
    //    id_voucher int references voucher(id_voucher),
    private String sdt_nguoi_nhan;
    private String dia_chi;
    private String email;
    private Double tong_tien_truoc_giam;
    private Double phi_van_chuyen;
    private String ho_ten;
    private Double tong_tien_sau_giam;
    private String hinh_thuc_thanh_toan;
    private String phuong_thuc_nhan_hang;

}
