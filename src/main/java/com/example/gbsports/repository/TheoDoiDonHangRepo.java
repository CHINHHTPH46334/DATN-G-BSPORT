package com.example.gbsports.repository;

import com.example.gbsports.entity.TheoDoiDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
public interface TheoDoiDonHangRepo extends JpaRepository<TheoDoiDonHang, Integer> {
    @Query(value = """
    select * from theo_doi_don_hang tddh
    full outer join hoa_don hd on hd.id_hoa_don = tddh.id_hoa_don
    where tddh.id_hoa_don = :idHD
    """, nativeQuery = true)
    List<TheoDoiDonHang> getTDDH(@RequestParam("idHoaDon") Integer idHD);

    @Query(value = """
            SELECT tddh.* from theo_doi_don_hang tddh
            JOIN hoa_don hd ON hd.id_hoa_don = tddh.id_hoa_don
            WHERE hd.ma_hoa_don = :maHoaDon AND tddh.trang_thai = N'Đã cập nhật'
            """, nativeQuery = true)
    Optional<TheoDoiDonHang> findByMaHDAndTrangThai(String maHoaDon);
}
