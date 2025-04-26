package com.example.gbsports.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinhLuanId implements Serializable {
    private Integer id_khach_hang;
    private Integer id_chi_tiet_san_pham;
}
