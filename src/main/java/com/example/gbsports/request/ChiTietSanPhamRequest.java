package com.example.gbsports.request;

import com.example.gbsports.entity.KichThuoc;
import com.example.gbsports.entity.MauSac;
import com.example.gbsports.entity.SanPham;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ChiTietSanPhamRequest {
    private Integer id_chi_tiet_san_pham;
    @NotNull(message = "Không để trống sản phẩm")
    private Integer id_san_pham;
    private String qr_code;
    @NotNull(message = "Không để trống giá bán")
    private BigDecimal gia_ban;
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
    @NotNull(message = "Không để trống kích thước")
    private Integer id_kich_thuoc;
    @NotNull(message = "Không để trống màu sắc")
    private Integer id_mau_sac;
}
