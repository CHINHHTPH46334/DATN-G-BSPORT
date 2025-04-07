package com.example.gbsports.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class RegisterRequest {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, message = "Họ tên phải có ít nhất 2 ký tự")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "(84|0[3|5|7|8|9])([0-9]{8})\\b", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Giới tính không được để trống")
    @Pattern(regexp = "Nam|Nữ|Khác", message = "Giới tính phải là Nam, Nữ hoặc Khác")
    private String gender;

    @NotNull(message = "Ngày sinh không được để trống")
    @PastOrPresent(message = "Ngày sinh không được là ngày trong tương lai")
    private Date birthDate;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}