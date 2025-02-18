package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "kich_thuoc")
public class KichThuoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_kich_thuoc;
    private String ma_kich_thuoc;
    private float gia_tri;
    private String don_vi;
}
