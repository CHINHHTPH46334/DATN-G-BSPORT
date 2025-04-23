package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tra_hang")
public class TraHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_hoa_don", nullable = false)
    private Integer id_hoa_don;

    @Column(name = "ly_do", nullable = false)
    private String ly_do;

    @Column(name = "ghi_chu")
    private String ghi_chu;

    @Column(name = "nhan_vien_xu_ly")
    private String nhan_vien_xu_ly;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngay_tao;

    @Column(name = "trang_thai", nullable = false)
    private String trang_thai;

    @Column(name = "tong_tien_hoan", nullable = false)
    private BigDecimal tong_tien_hoan;

    @OneToMany(mappedBy = "tra_hang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietTraHang> danh_sach_chi_tiet_tra_hang;

}
