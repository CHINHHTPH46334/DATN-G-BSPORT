package com.example.gbsports.request;

import com.example.gbsports.entity.KichThuoc;
import com.example.gbsports.entity.MauSac;
import com.example.gbsports.entity.SanPham;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ChiTietSanPhamRequest {
    private Integer id_chi_tiet_san_pham;
    @NotNull(message = "Không để trống sản phẩm")
    @Valid
    SanPham sanPham;
    private String qr_code;
    @NotNull(message = "Không để trống giá bán")
    private float gia_ban;
    @NotNull(message = "Không để trống số lượng")
    private Integer so_luong;
    @NotBlank(message = "Không để trống trạng thái")
    private String trang_thai;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    @NotNull(message = "Không để trống ngày tạo")
    private Date ngay_tao;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngay_sua;
    @NotNull(message = "Không để trống giá nhập")
    private float gia_nhap;
    @NotNull(message = "Không để trống kích thước")
    @Valid
    KichThuoc kichThuoc;
    @NotNull(message = "Không để trống màu sắc")
    @Valid
    MauSac mauSac;
}
