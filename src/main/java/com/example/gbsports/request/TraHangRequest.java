package com.example.gbsports.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TraHangRequest {
    @JsonProperty("ma_hoa_don")
    private String ma_hoa_don;

    @JsonProperty("danh_sach_san_pham")
    private List<ChiTietTraHangRequest> danh_sach_san_pham;

    @JsonProperty("ly_do")
    private String ly_do;

    @JsonProperty("ghi_chu")
    private String ghi_chu;

    @JsonProperty("nhan_vien")
    private String nhan_vien;

    @JsonProperty("tong_tien_hoan")
    private BigDecimal tong_tien_hoan;

    @JsonProperty("tong_tien_sau_giam")
    private BigDecimal tong_tien_sau_giam;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChiTietTraHangRequest {
        @JsonProperty("id_chi_tiet_san_pham")
        private Integer id_chi_tiet_san_pham;

        @JsonProperty("so_luong")
        private Integer so_luong;

        @JsonProperty("so_tien_hoan")
        private BigDecimal so_tien_hoan;
    }
}
