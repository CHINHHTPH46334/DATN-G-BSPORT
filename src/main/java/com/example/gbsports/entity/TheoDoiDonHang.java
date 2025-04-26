package com.example.gbsports.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "theo_doi_don_hang")
public class TheoDoiDonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_don_hang;
    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    @JsonBackReference
    private HoaDon hoaDon;
    private String trang_thai;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ngay_chuyen;
    private String nhan_vien_doi;
    private String noi_dung_doi;
}
