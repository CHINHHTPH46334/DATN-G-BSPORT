package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "binh_luan")
@IdClass(BinhLuanId.class)
public class BinhLuan {

    @Id
    @Column(name = "id_khach_hang")
    private Integer id_khach_hang;

    @Id
    @Column(name = "id_chi_tiet_san_pham")
    private Integer id_chi_tiet_san_pham;

    @Column(name = "binh_luan")
    private String binh_luan;

    @Column(name = "danh_gia")
    private Float danh_gia;

    @Column(name = "ngay_binh_luan")
    private Date ngay_binh_luan;

    @Column(name = "ngay_sua")
    private Date ngay_sua;

    @Column(name = "da_chinh_sua")
    private Boolean da_chinh_sua;
}