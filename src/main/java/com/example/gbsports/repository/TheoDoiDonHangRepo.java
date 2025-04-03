package com.example.gbsports.repository;

import com.example.gbsports.entity.TheoDoiDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface TheoDoiDonHangRepo extends JpaRepository<TheoDoiDonHang, Integer> {
    @Query(value = """
    select * from theo_doi_don_hang tddh
    full outer join hoa_don hd on hd.id_hoa_don = tddh.id_hoa_don
    where tddh.id_hoa_don = :idHD
    """, nativeQuery = true)
    List<TheoDoiDonHang> getTDDH(@RequestParam("idHoaDon") Integer idHD);
}
