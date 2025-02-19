package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "mau_sac")
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_mau_sac;
    private String ma_mau;
    private String ten_mau;
}
