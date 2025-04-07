package com.example.gbsports.repository;

import com.example.gbsports.entity.NhanVien;
import com.example.gbsports.response.NhanVienResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NhanVienRepo extends JpaRepository<NhanVien, Integer> {
    @Query(nativeQuery = true, value = """
        SELECT id_nhan_vien AS idNhanVien,anh_nhan_vien as anhNhanVien, ma_nhan_vien AS maNhanVien, ten_nhan_vien AS tenNhanVien, 
               ngay_sinh AS ngaySinh, email, dia_chi_lien_he AS diaChiLienHe, gioi_tinh AS gioiTinh, 
               so_dien_thoai AS soDienThoai, trang_thai AS trangThai
        FROM nhan_vien nv
    """)
    List<NhanVienResponse> getAll();

    @Query(nativeQuery = true, value = """
        SELECT id_nhan_vien AS idNhanVien, anh_nhan_vien as anhNhanVien, ma_nhan_vien AS maNhanVien, ten_nhan_vien AS tenNhanVien, 
               ngay_sinh AS ngaySinh, email, dia_chi_lien_he AS diaChiLienHe, gioi_tinh AS gioiTinh, 
               so_dien_thoai AS soDienThoai, trang_thai AS trangThai
        FROM nhan_vien nv
        order by id_nhan_vien desc 
    """
//            countQuery = "SELECT COUNT(*) FROM nhan_vien"
    )
    Page<NhanVienResponse> listPT(Pageable pageable);

    @Query(nativeQuery = true, value = """
    SELECT id_nhan_vien AS idNhanVien, anh_nhan_vien as anhNhanVien, ma_nhan_vien AS maNhanVien, ten_nhan_vien AS tenNhanVien, 
           ngay_sinh AS ngaySinh, email, dia_chi_lien_he AS diaChiLienHe, gioi_tinh AS gioiTinh, 
           so_dien_thoai AS soDienThoai, trang_thai AS trangThai
    FROM nhan_vien nv
    WHERE email LIKE %:keyword% 
    OR LOWER(ma_nhan_vien) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(ten_nhan_vien) LIKE LOWER(CONCAT('%', :keyword, '%')) 
    OR so_dien_thoai LIKE %:keyword%
    OR LOWER(dia_chi_lien_he) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(trang_thai) LIKE LOWER(CONCAT('%', :keyword, '%'))
    order by id_nhan_vien desc 
""")
    Page<NhanVienResponse> timNhanVien(@Param("keyword") String keyword, Pageable pageable);

    @Query(nativeQuery = true, value = """
        SELECT id_nhan_vien AS idNhanVien, anh_nhan_vien as anhNhanVien, ma_nhan_vien AS maNhanVien, ten_nhan_vien AS tenNhanVien, 
               ngay_sinh AS ngaySinh, email, dia_chi_lien_he AS diaChiLienHe, gioi_tinh AS gioiTinh, 
               so_dien_thoai AS soDienThoai, trang_thai AS trangThai
        FROM nhan_vien nv
        WHERE (:trangThai IS NULL OR :trangThai = '' OR trang_thai = :trangThai)
        order by id_nhan_vien desc 
    """)
    Page<NhanVienResponse> locNhanVienTheoTrangThai(@Param("trangThai") String trangThai,Pageable pageable);
    boolean existsByMaNhanVien(String maNhanVien);
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);

    @Query("SELECT nv FROM NhanVien nv WHERE nv.taiKhoan.id_tai_khoan = :idTaiKhoan")
    Optional<NhanVien> findByTaiKhoanIdTaiKhoan(Integer idTaiKhoan);

}
