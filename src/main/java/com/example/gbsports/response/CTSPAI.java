package com.example.gbsports.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CTSPAI {
    // ID và khóa ngoại
    private Integer id_chi_tiet_san_pham;
    private Integer id_san_pham;

    // Thông tin cơ bản sản phẩm
    private String ma_san_pham;
    private String ten_san_pham;
    private String mo_ta;
    private String hinh_anh;

    // Thông tin danh mục, thương hiệu, chất liệu
    private String ten_danh_muc;
    private String ten_thuong_hieu;
    private String ten_chat_lieu;

    // Thông tin giá và số lượng
    private Double gia_ban;
    private Integer so_luong;
    private Integer tong_so_luong;

    // Thông tin thuộc tính
    private String ten_mau;
    private String gia_tri;  // kích thước

    // Thông tin trạng thái và thời gian
    private String trang_thai_sp;  // Trạng thái sản phẩm
    private String trang_thai_ctsp;  // Trạng thái chi tiết sản phẩm
    private LocalDateTime ngay_tao;
    private LocalDateTime ngay_sua;

    // Thông tin khác
    private String qr_code;

    // Danh sách hình ảnh (nếu có)
    private List<String> danh_sach_hinh_anh;
}
