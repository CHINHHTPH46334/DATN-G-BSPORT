package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "chi_tiet_tra_hang")
public class ChiTietTraHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tra_hang", nullable = false)
    private TraHang tra_hang;

    @Column(name = "id_chi_tiet_san_pham", nullable = false)
    private Integer id_chi_tiet_san_pham;

    @Column(name = "so_luong", nullable = false)
    private Integer so_luong;

    @Column(name = "tien_hoan", nullable = false)
    private BigDecimal tien_hoan;
}