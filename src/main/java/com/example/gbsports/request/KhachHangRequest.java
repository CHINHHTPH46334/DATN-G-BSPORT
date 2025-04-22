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
    @Size(max = 100, message = "Tên khách hàng không được vượt quá 100 ký tự") // Cập nhật giới hạn
    @Pattern(regexp = "^[a-zA-Z\\s\\u00C0-\\u1EF9]+$", message = "Họ tên chỉ được chứa chữ cái")
    private String tenKhachHang;

    @NotNull(message = "Giới tính không được để trống")
    private Boolean gioiTinh;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và đúng 10 chữ số")
    private String soDienThoai;

    @NotNull(message = "Ngày sinh không được để trống")
    @PastOrPresent(message = "Ngày sinh không được là ngày trong tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date ngaySinh;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;

    @NotBlank(message = "Trạng thái không được để trống")
    private String trangThai;

    // Bắt buộc khi thêm mới, nhưng không bắt buộc khi cập nhật
    @Size(min = 6, max = 20, message = "Mật khẩu phải từ 6 đến 20 ký tự")
    private String matKhau;

    private List<DiaChiRequest> diaChiList = new ArrayList<>();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngayTao;

    // Setter với trim
    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang != null ? maKhachHang.trim() : null;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang != null ? tenKhachHang.trim() : null;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai != null ? soDienThoai.replaceAll("\\s+", "") : null;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    @Data
    public static class DiaChiRequest {
        @NotBlank(message = "Số nhà không được để trống")
        @Size(max = 255, message = "Số nhà, tên đường không được vượt quá 255 ký tự") // Thêm giới hạn
        @Pattern(regexp = "^[a-zA-Z0-9\\s\\u00C0-\\u1EF9]+$", message = "Số nhà, tên đường chỉ được chứa số, chữ cái")
        private String soNha;

        @NotBlank(message = "Xã/Phường không được để trống")
        private String xaPhuong;

        @NotBlank(message = "Quận/Huyện không được để trống")
        private String quanHuyen;

        @NotBlank(message = "Tỉnh/Thành phố không được để trống")
        private String tinhThanhPho;

        private Boolean diaChiMacDinh;

        // Setter với trim
        public void setSoNha(String soNha) {
            this.soNha = soNha != null ? soNha.trim() : null;
        }

        public void setXaPhuong(String xaPhuong) {
            this.xaPhuong = xaPhuong != null ? xaPhuong.trim() : null;
        }

        public void setQuanHuyen(String quanHuyen) {
            this.quanHuyen = quanHuyen != null ? quanHuyen.trim() : null;
        }

        public void setTinhThanhPho(String tinhThanhPho) {
            this.tinhThanhPho = tinhThanhPho != null ? tinhThanhPho.trim() : null;
        }
    }
}