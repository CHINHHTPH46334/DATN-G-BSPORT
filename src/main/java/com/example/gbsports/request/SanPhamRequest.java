package com.example.gbsports.request;

import com.example.gbsports.entity.ChatLieu;
import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.entity.ThuongHieu;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SanPhamRequest {
    private Integer id_san_pham;
    @NotBlank(message = "Không để trống mã sản phẩm")
    private String ma_san_pham;
    @NotBlank(message = "Không để trống tên sản phẩm")
    private String ten_san_pham;
    @Size(max = 250, message = "Không quá 250 ký tự")
    private String mo_ta;
    @NotBlank(message = "Không được trống trạng thái")
    private String trang_thai;
    @NotNull(message = "Không để trống giới tính")
    private Boolean gioi_tinh;
    private String hinh_anh;
//    @NotNull(message = "Danh mục không được để trống")
    private Integer id_danh_muc;
//    @NotNull(message = "Thương hiệu không được để trống")
    private Integer id_thuong_hieu;
//    @NotNull(message = "Chất liệu không được để trống")
    private Integer id_chat_lieu;
}
