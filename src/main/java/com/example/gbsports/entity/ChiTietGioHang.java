package com.example.gbsports.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chi_tiet_gio_hang")
public class ChiTietGioHang {
    @EmbeddedId
    private ChiTietGioHangId id;

    @ManyToOne
    @MapsId("idGioHang")
    @JoinColumn(name = "id_gio_hang", nullable = false)
    @JsonIgnore // Ngắt vòng lặp
    private GioHang gioHang;

    @ManyToOne
    @MapsId("idChiTietSanPham")
    @JoinColumn(name = "id_chi_tiet_san_pham", nullable = false)
    private ChiTietSanPham chiTietSanPham;

    @Column(name = "so_luong")
    private Integer soLuong;

}
