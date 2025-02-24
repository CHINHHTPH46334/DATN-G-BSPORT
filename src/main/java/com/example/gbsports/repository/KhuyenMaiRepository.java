package com.example.gbsports.repository;

import com.example.gbsports.entity.KhuyenMai;
import com.example.gbsports.response.KhuyenMaiResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai,Integer> {
    @Query("""
        SELECT new com.example.gbsports.response.KhuyenMaiResponse
        (km.id, km.maKhuyenMai, km.tenKhuyenMai, km.moTa, km.ngayBatDau, km.ngayHetHan, 
         km.giaTriGiam, km.kieuGiamGia, km.trangThai, km.giaTriToiDa) 
        FROM KhuyenMai km
        """)
    Page<KhuyenMaiResponse> phanTrang(Pageable pageable);

    boolean existsByMaKhuyenMai(String maKhuyenMai);

    @Query("""
        SELECT new com.example.gbsports.response.KhuyenMaiResponse(
            km.id, km.maKhuyenMai, km.tenKhuyenMai, km.moTa, 
            km.ngayBatDau, km.ngayHetHan, km.giaTriGiam, 
            km.kieuGiamGia, km.trangThai, km.giaTriToiDa)
        FROM KhuyenMai km
        WHERE km.trangThai = :trangThai
    """)
    Page<KhuyenMaiResponse> locTheoTrangThai(@Param("trangThai") String trangThai, Pageable pageable);
    @Query("""
        SELECT new com.example.gbsports.response.KhuyenMaiResponse(
            km.id, km.maKhuyenMai, km.tenKhuyenMai, km.moTa, 
            km.ngayBatDau, km.ngayHetHan, km.giaTriGiam, 
            km.kieuGiamGia, km.trangThai, km.giaTriToiDa)
        FROM KhuyenMai km
        WHERE (:search IS NULL OR km.maKhuyenMai LIKE %:search% OR km.tenKhuyenMai LIKE %:search%) 
    """)
    Page<KhuyenMaiResponse> timKiemKhuyenMai(
            @Param("search") String search,
            Pageable pageable);
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE khuyen_mai 
        SET trang_thai = 
            CASE 
                WHEN ngay_bat_dau > GETDATE() THEN N'Sắp diễn ra'
                WHEN ngay_bat_dau <= GETDATE() AND ngay_het_han >= GETDATE() THEN N'Đang diễn ra'
                WHEN ngay_het_han < GETDATE() THEN N'Đã kết thúc'
                ELSE trang_thai
            END
        """, nativeQuery = true)
    void capNhatTrangThaiKhuyenMai();
}

