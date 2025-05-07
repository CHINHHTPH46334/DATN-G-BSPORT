package com.example.gbsports.repository;

import com.example.gbsports.entity.ChiTietTraHang;
import com.example.gbsports.entity.TraHang;
import com.example.gbsports.response.ChiTietTraHangDTO;
import com.example.gbsports.response.ChiTietTraHangResponse;
import com.example.gbsports.response.TraHangResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ChiTietTraHangRepository extends JpaRepository<ChiTietTraHang, Integer> {
    @Query(value = "SELECT DISTINCT " +
            "ctth.id, " +
            "ctth.id_tra_hang AS id_tra_hang, " +
            "ctth.id_chi_tiet_san_pham AS id_chi_tiet_san_pham, " +
            "ctth.so_luong AS so_luong, " +
            "ctth.tien_hoan AS tien_hoan, " +
            "(ctth.tien_hoan / NULLIF(ctth.so_luong, 0)) AS don_gia, " +
            "sp.ten_san_pham AS ten_san_pham, " +
            "sp.hinh_anh AS hinh_anh, " +
            "kt.gia_tri AS kich_thuoc, " +
            "ms.ten_mau_sac AS ten_mau_sac " +
            "FROM chi_tiet_tra_hang ctth " +
            "INNER JOIN tra_hang th ON ctth.id_tra_hang = th.id " +
            "INNER JOIN chi_tiet_san_pham ctsp ON ctth.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham " +
            "INNER JOIN san_pham sp ON ctsp.id_san_pham = sp.id_san_pham " +
            "LEFT JOIN mau_sac ms ON ctsp.id_mau_sac = ms.id_mau_sac " +
            "LEFT JOIN kich_thuoc kt ON ctsp.id_kich_thuoc = kt.id_kich_thuoc " +
            "LEFT JOIN hinh_anh ha ON ctsp.id_chi_tiet_san_pham = ha.id_chi_tiet_san_pham AND ha.anh_chinh = 1 " +
            "WHERE th.id_hoa_don = :idHoaDon", nativeQuery = true)
    List<ChiTietTraHangDTO> findChiTietTraHangByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);

    @Query(value = """
     SELECT\s
            th.id,
            th.id_hoa_don AS id_hoa_don,
            th.tong_tien_hoan AS tong_tien_hoan,
            th.ly_do AS ly_do,
            th.ghi_chu AS ghi_chu,
            th.nhan_vien_xu_ly AS nhan_vien_xu_ly,
            th.ngay_tao AS ngay_tao,
            th.trang_thai AS trang_thai
        FROM tra_hang th
        WHERE th.id_hoa_don = :idHoaDon
""", nativeQuery = true)
    List<TraHang> findTraHangByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);
}
