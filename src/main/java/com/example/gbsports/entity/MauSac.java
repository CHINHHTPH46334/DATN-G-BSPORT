package com.example.gbsports.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "mau_sac")
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_mau_sac;
    @NotBlank(message = "Không để trống mã màu")
    private String ma_mau_sac;
    @NotBlank(message = "Không để trống nên màu")
    private String ten_mau_sac;
    private String trang_thai;
}
