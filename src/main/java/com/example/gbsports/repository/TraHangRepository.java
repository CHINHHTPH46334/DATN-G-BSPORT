package com.example.gbsports.repository;


import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.entity.TraHang;
//import com.example.gbsports.response.LichSuTraHangResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TraHangRepository extends JpaRepository<TraHang, Integer> {
    @Query("SELECT h FROM HoaDon h WHERE h.id_hoa_don = :id_hoa_don")
    Optional<TraHang> TimIDHoaDon( @Param("id_hoa_don") Integer id_hoa_don);

    @Query(value = """
            SELECT *
            FROM tra_hang th
            WHERE th.id_hoa_don = :idHoaDon
            ORDER BY th.ngay_tao DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<TraHang> findLatestByIdHoaDon(@Param("idHoaDon") Integer idHoaDon);

}