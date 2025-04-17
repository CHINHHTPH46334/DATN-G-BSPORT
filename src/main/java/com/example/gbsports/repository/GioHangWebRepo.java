package com.example.gbsports.repository;

import com.example.gbsports.entity.ChiTietGioHang;
import com.example.gbsports.response.GioHangWebResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GioHangWebRepo extends JpaRepository<ChiTietGioHang, Integer> {
    @Query(nativeQuery = true, value = """
            select gh.id_gio_hang,ctsp.id_chi_tiet_san_pham ,gh.id_khach_hang, sp.hinh_anh, sp.ten_san_pham, ms.ten_mau_sac, kt.gia_tri, kt.don_vi, ctgh.so_luong, ctsp.gia_ban from gio_hang gh
            join chi_tiet_gio_hang ctgh on ctgh.id_gio_hang = gh.id_gio_hang
            join chi_tiet_san_pham ctsp on ctsp.id_chi_tiet_san_pham = ctgh.id_chi_tiet_san_pham
            join khach_hang kh on kh.id_khach_hang = gh.id_khach_hang
            join san_pham sp on sp.id_san_pham = ctsp.id_san_pham
            join mau_sac ms on ms.id_mau_sac = ctsp.id_mau_sac
            join kich_thuoc kt on kt.id_kich_thuoc = ctsp.id_kich_thuoc
            where gh.id_khach_hang = :idKhachHang
            """)
    List<GioHangWebResponse> listGioHangByKhachHang(Integer idKhachHang);

    @Query(nativeQuery = true, value = """
            select kh.id_khach_hang, dckh.so_nha, dckh.xa_phuong, dckh.quan_huyen, dckh.tinh_thanh_pho, dckh.dia_chi_mac_dinh from khach_hang kh
            join dia_chi_khach_hang dckh on dckh.id_khach_hang = kh.id_khach_hang
            where kh.id_khach_hang = :idKhachHang
            """)
    List<GioHangWebResponse> listDiaChiByKH(Integer idKhachHang);
}
