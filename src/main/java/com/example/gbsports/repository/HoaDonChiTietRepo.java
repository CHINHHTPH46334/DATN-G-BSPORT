package com.example.gbsports.repository;

import com.example.gbsports.entity.HoaDonChiTiet;
import com.example.gbsports.response.HoaDonChiTietResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HoaDonChiTietRepo extends JpaRepository<HoaDonChiTiet, Integer> {
    @Query(value = """
            SELECT hd.id_hoa_don, hd.ma_hoa_don, hd.ngay_tao, hd.ho_ten,hd.sdt_nguoi_nhan, 
                hd.dia_chi, hd.email, hd.tong_tien_truoc_giam, hd.phi_van_chuyen, 
                hd.tong_tien_sau_giam, hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang, 
                hdct.id_hoa_don_chi_tiet, hdct.so_luong, hdct.don_gia, 
                sp.ten_san_pham, sp.ma_san_pham, 
                kt.gia_tri AS kich_thuoc, 
                ms.ten_mau_sac, 
                ha.hinh_anh, ha.anh_chinh
            FROM hoa_don hd
            JOIN hoa_don_chi_tiet hdct ON hd.id_hoa_don = hdct.id_hoa_don
            JOIN chi_tiet_san_pham ctsp ON hdct.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham
            JOIN san_pham sp ON ctsp.id_san_pham = sp.id_san_pham
            JOIN kich_thuoc kt ON ctsp.id_kich_thuoc = kt.id_kich_thuoc
            JOIN mau_sac ms ON ctsp.id_mau_sac = ms.id_mau_sac
            LEFT JOIN hinh_anh ha ON ctsp.id_chi_tiet_san_pham = ha.id_chi_tiet_san_pham AND ha.anh_chinh = 1
            WHERE hd.id_hoa_don = :idHoaDon
        """, nativeQuery = true)
    List<HoaDonChiTietResponse> findHoaDonChiTietById(
            @Param("idHoaDon") Integer idHoaDon
    );

//    @Query(value = """
//    UPDATE hoa_don_chi_tiet
//               SET
//                   so_luong = so_luong + :soLuong,
//                   don_gia = (so_luong + :soLuong) * (SELECT gia_ban FROM chi_tiet_san_pham WHERE id_chi_tiet_san_pham = :idCTSP)
//               WHERE id_chi_tiet_san_pham = :idCTSP
//    """, nativeQuery = true)
//    void addSLGH(@Param("idCTSP") Integer idCTSP, @Param("soLuong") Integer soLuong);
}
