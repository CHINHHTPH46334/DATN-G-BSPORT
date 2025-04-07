package com.example.gbsports.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class KhachHangRequest {
    private Integer idKhachHang;

    // Không bắt buộc vì controller sẽ sinh mã tự động nếu trống
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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // Chấp nhận định dạng ISO từ frontend
    private Date ngaySinh;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Trạng thái không được để trống")
    private String trangThai;

    // Bắt buộc khi thêm mới, nhưng không bắt buộc khi cập nhật
    @Size(min = 6, max = 20, message = "Mật khẩu phải từ 6 đến 20 ký tự")
    private String matKhau;

    private List<DiaChiRequest> diaChiList = new ArrayList<>();

    @Data
    public static class DiaChiRequest {
        @NotBlank(message = "Số nhà không được để trống")
        private String soNha;

        @NotBlank(message = "Xã/Phường không được để trống")
        private String xaPhuong;

        @NotBlank(message = "Quận/Huyện không được để trống")
        private String quanHuyen;

        @NotBlank(message = "Tỉnh/Thành phố không được để trống")
        private String tinhThanhPho;

        private Boolean diaChiMacDinh; // Thêm trường này để khớp với logic trong controller
    }
}