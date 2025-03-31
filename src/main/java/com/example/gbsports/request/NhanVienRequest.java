package com.example.gbsports.request;

import com.example.gbsports.entity.Roles;
import com.example.gbsports.entity.TaiKhoan;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class NhanVienRequest {
    private Integer idNhanVien;
//
////    @NotBlank(message = "Không được để trống mã")
//    private String maNhanVien;
//
////    @NotBlank(message = "Không được để trống tên")
//    private String tenNhanVien;
//
//    @Temporal(TemporalType.DATE)
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
////    @NotNull(message = "Không được để trống ngày sinh")
//    private Date ngaySinh;
//
////    @NotBlank(message = "Không được để trống email")
////    @Email(message = "Email không hợp lệ")
//    private String email;
//
////    @NotBlank(message = "Không được để trống địa chỉ liên hệ")
//    private String diaChiLienHe;
//
////    @NotNull(message = "Chưa chọn giới tính")
//    private Boolean gioiTinh;
//
////    @NotBlank(message = "Không được để trống số điện thoại")
//    private String soDienThoai;
//
////    @NotBlank(message = "Không được để trống trạng thái")
//    private String trangThai;

private String maNhanVien;

<<<<<<< HEAD
    private String tenNhanVien;

=======

    private String tenNhanVien;


>>>>>>> d4962f0821ddabcd00b5fd0db0f3d793bcc2dd66
    private Date ngaySinh;

    private String email;

<<<<<<< HEAD
    private String diaChiLienHe;

    private Boolean gioiTinh;
    private String anhNhanVien;
    private Date ngayThamGia;
    private String soDienThoai;

=======

    private String diaChiLienHe;


    private Boolean gioiTinh;

    private String anhNhanVien;

    private Date ngayThamGia;

    private String soDienThoai;


>>>>>>> d4962f0821ddabcd00b5fd0db0f3d793bcc2dd66
    private String trangThai;

//    private Roles roles;
    private TaiKhoan taiKhoan;

}
