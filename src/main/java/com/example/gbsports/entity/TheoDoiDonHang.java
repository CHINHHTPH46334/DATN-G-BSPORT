package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "theo_doi_don_hang")
public class TheoDoiDonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_don_hang;
    @OneToOne
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;
    private String trang_thai;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngay_chuyen;
}
