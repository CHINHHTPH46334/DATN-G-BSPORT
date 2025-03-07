package com.example.gbsports.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class KhachHangRequest {
    private Integer idKhachHang;

    @NotBlank(message = "Mã khách hàng không được để trống")
    @Size(min = 3, max = 10, message = "Mã khách hàng phải từ 3 đến 10 ký tự")
    private String maKhachHang;

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(max = 50, message = "Tên khách hàng không được vượt quá 50 ký tự")
    private String tenKhachHang;

    @NotNull(message = "Giới tính không được để trống")
    private Boolean gioiTinh;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và đúng 10 chữ số")
    private String soDienThoai;

    @NotNull(message = "Ngày sinh không được để trống")
    @PastOrPresent(message = "Ngày sinh không được là ngày trong tương lai")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngaySinh;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Trạng thái không được để trống")
    private String trangThai;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String diaChi;
}