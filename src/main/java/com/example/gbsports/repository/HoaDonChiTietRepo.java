package com.example.gbsports.repository;

import com.example.gbsports.entity.HoaDonChiTiet;
import com.example.gbsports.response.HoaDonChiTietResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HoaDonChiTietRepo extends JpaRepository<HoaDonChiTiet, Integer> {
    @Query(value = """
            SELECT DISTINCT hd.id_hoa_don, hd.ma_hoa_don, hd.ngay_tao, hd.ho_ten,hd.sdt_nguoi_nhan, 
                hd.dia_chi, hd.email, hd.tong_tien_truoc_giam, hd.phi_van_chuyen, 
                hd.tong_tien_sau_giam, hd.hinh_thuc_thanh_toan, hd.phuong_thuc_nhan_hang, 
                tdh.trang_thai, hdct.id_hoa_don_chi_tiet, hdct.so_luong, hdct.don_gia, 
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
            LEFT JOIN (SELECT t.id_hoa_don, t.trang_thai
                        FROM theo_doi_don_hang t
                        WHERE t.ngay_chuyen = (SELECT MAX(ngay_chuyen)
                                                FROM theo_doi_don_hang t2
                                                WHERE t2.id_hoa_don = t.id_hoa_don
                                                )
                        ) tdh ON hd.id_hoa_don = tdh.id_hoa_don
            LEFT JOIN hinh_anh ha ON ctsp.id_chi_tiet_san_pham = ha.id_chi_tiet_san_pham AND ha.anh_chinh = 1
            WHERE hd.id_hoa_don = :idHoaDon
        """, nativeQuery = true)
    List<HoaDonChiTietResponse> findHoaDonChiTietById(
            @Param("idHoaDon") Integer idHoaDon
    );
}
