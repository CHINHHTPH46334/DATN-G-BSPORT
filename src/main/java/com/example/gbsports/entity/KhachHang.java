package com.example.gbsports.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Entity
@Table(name = "khach_hang")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khach_hang")
    private Integer idKhachHang;

    @Column(name = "ma_khach_hang")
//    @NotBlank(message = "Mã khách hàng không được để trống")
//    @Size(min = 3, max = 10, message = "Mã khách hàng phải từ 3 đến 10 ký tự")
    private String maKhachHang;

    @Column(name = "ten_khach_hang")
    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(max = 50, message = "Tên khách hàng không được vượt quá 50 ký tự")
    private String tenKhachHang;

    @Column(name = "gioi_tinh")
//    @NotNull(message = "Giới tính không được để trống")
    private Boolean gioiTinh;

    @Column(name = "so_dien_thoai")
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và đúng 10 chữ số")
    private String soDienThoai;

    @Column(name = "ngay_sinh")
    @NotNull(message = "Ngày sinh không được để trống")
    @PastOrPresent(message = "Ngày sinh không được là ngày trong tương lai")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date ngaySinh;

    @Column(name = "email")
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Column(name = "trang_thai")
    @NotBlank(message = "Trạng thái không được để trống")
    private String trangThai;

    @OneToOne
    @JoinColumn(name = "id_tai_khoan")
    private TaiKhoan taiKhoan;


}