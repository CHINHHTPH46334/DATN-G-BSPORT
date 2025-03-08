package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "gio_hang")
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_gio_hang;
    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

}
