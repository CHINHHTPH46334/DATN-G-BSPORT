package com.example.gbsports.repository;

import com.example.gbsports.entity.GioHang;
import com.example.gbsports.response.ChiTietGioHangResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GioHangRepo extends JpaRepository<GioHang,Integer> {
    @Query(value = """
    select hdct.id_hoa_don_chi_tiet, ctsp.id_chi_tiet_san_pham, sp.ma_san_pham, sp.ten_san_pham, ha.hinh_anh, hdct.so_luong, ctsp.gia_ban, hdct.don_gia
    from hoa_don_chi_tiet hdct
    left join chi_tiet_san_pham ctsp on ctsp.id_chi_tiet_san_pham = hdct.id_chi_tiet_san_pham
    left join san_pham sp on sp.id_san_pham = ctsp.id_san_pham
    left join hinh_anh ha on ha.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham
    where hdct.id_hoa_don = :idHD
    """, nativeQuery = true)
    List<ChiTietGioHangResponse> getSPGH(Integer idHD);

}
