package com.example.gbsports.repository;

import com.example.gbsports.entity.NhanVien;
import com.example.gbsports.request.NhanVienRequest;
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
            SELECT id_nhan_vien AS idNhanVien, anh_nhan_vien as anhNhanVien, ma_nhan_vien AS maNhanVien, ten_nhan_vien AS tenNhanVien,\s
                   ngay_sinh AS ngaySinh, email, dia_chi_lien_he AS diaChiLienHe, gioi_tinh AS gioiTinh,\s
                   so_dien_thoai AS soDienThoai, trang_thai AS trangThai
                FROM nhan_vien nv
            	join tai_khoan tk on tk.id_tai_khoan = nv.id_tai_khoan
            	join roles r on r.id_roles = tk.id_roles
            	where tk.id_roles = 3
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
                join tai_khoan tk on tk.id_tai_khoan = nv.id_tai_khoan
            	join roles r on r.id_roles = tk.id_roles 
                WHERE tk.id_roles = 3 and email LIKE %:keyword% 
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
                join tai_khoan tk on tk.id_tai_khoan = nv.id_tai_khoan
            	join roles r on r.id_roles = tk.id_roles 
                WHERE tk.id_roles = 3 and (:trangThai IS NULL OR :trangThai = '' OR trang_thai = :trangThai)
                order by id_nhan_vien desc 
            """)
    Page<NhanVienResponse> locNhanVienTheoTrangThai(@Param("trangThai") String trangThai, Pageable pageable);

    @Query(nativeQuery = true, value = """
              SELECT id_nhan_vien AS idNhanVien, anh_nhan_vien as anhNhanVien, ma_nhan_vien AS maNhanVien, ten_nhan_vien AS tenNhanVien, ngay_sinh AS ngaySinh, email, dia_chi_lien_he AS diaChiLienHe, gioi_tinh AS gioiTinh,
                        so_dien_thoai AS soDienThoai, trang_thai AS trangThai
                        FROM nhan_vien nv
                        join tai_khoan tk on tk.id_tai_khoan = nv.id_tai_khoan
                        join roles r on r.id_roles = tk.id_roles
                        where r.id_roles not in (select r1.id_roles from roles r1 where r1.id_roles = 4)
            """)
    List<NhanVienResponse> listTrangAdmin();

    @Query("SELECT nv FROM NhanVien nv WHERE nv.taiKhoan.id_tai_khoan = :idTaiKhoan")
    Optional<NhanVien> findByTaiKhoanIdTaiKhoan(Integer idTaiKhoan);

}
