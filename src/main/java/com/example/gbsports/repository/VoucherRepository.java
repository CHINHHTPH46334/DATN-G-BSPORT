package com.example.gbsports.repository;

import com.example.gbsports.entity.Voucher;
import com.example.gbsports.response.VoucherResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoucherRepository extends JpaRepository<Voucher,Integer> {
    @Query("""
        SELECT new com.example.gbsports.response.VoucherResponse(
            v.id, v.maVoucher, v.tenVoucher, v.ngayTao, v.ngayHetHan,
            v.giaTriGiam, v.giaTriToiThieu, v.trangThai, v.soLuong,
            v.kieuGiamGia, v.moTa, v.giaTriToiDa)
        FROM Voucher v
    """)
    Page<VoucherResponse> phanTrang(Pageable pageable);

    // Kiểm tra voucher đã tồn tại theo mã
    boolean existsByMaVoucher(String maVoucher);

    // Lọc voucher theo trạng thái
    @Query("""
        SELECT new com.example.gbsports.response.VoucherResponse(
            v.id, v.maVoucher, v.tenVoucher, v.ngayTao, v.ngayHetHan,
            v.giaTriGiam, v.giaTriToiThieu, v.trangThai, v.soLuong,
            v.kieuGiamGia, v.moTa, v.giaTriToiDa)
        FROM Voucher v
        WHERE v.trangThai = :trangThai
    """)
    Page<VoucherResponse> locTheoTrangThai(@Param("trangThai") String trangThai, Pageable pageable);

    // Tìm kiếm voucher theo mã hoặc tên
    @Query("""
        SELECT new com.example.gbsports.response.VoucherResponse(
            v.id, v.maVoucher, v.tenVoucher, v.ngayTao, v.ngayHetHan,
            v.giaTriGiam, v.giaTriToiThieu, v.trangThai, v.soLuong,
            v.kieuGiamGia, v.moTa, v.giaTriToiDa)
        FROM Voucher v
        WHERE (:search IS NULL OR v.maVoucher LIKE %:search% OR v.tenVoucher LIKE %:search%)
    """)
    Page<VoucherResponse> timKiemVoucher(@Param("search") String search, Pageable pageable);

    // Cập nhật trạng thái voucher theo ngày
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE voucher
        SET trang_thai =
            CASE
                WHEN ngay_tao > GETDATE() THEN N'Sắp diễn ra'
                WHEN ngay_tao <= GETDATE() AND ngay_het_han >= GETDATE() THEN N'Đang diễn ra'
                WHEN ngay_het_han < GETDATE() THEN N'Đã kết thúc'
                ELSE trang_thai
            END
    """, nativeQuery = true)
    void capNhatTrangThaiVoucher();
}
