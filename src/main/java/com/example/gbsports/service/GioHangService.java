package com.example.gbsports.service;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.ChiTietGioHangRepository;
import com.example.gbsports.repository.ChiTietSanPhamRepo;
import com.example.gbsports.repository.GioHangRepository;
import com.example.gbsports.repository.KhachHangRepo;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class GioHangService {
    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private ChiTietSanPhamRepo chiTietSanPhamRepository;

    @Autowired
    private ChiTietGioHangRepository chiTietGioHangRepository;

    @Autowired
    private KhachHangRepo khachHangRepository;

    @Autowired
    private HttpSession session;

    @Transactional
    public void themSanPhamVaoGioHang(Integer idKhachHang, Integer idChiTietSanPham, Integer soLuong) {
        if (idKhachHang != null) {
            // Người dùng đã đăng nhập
            KhachHang khachHang = khachHangRepository.findById(idKhachHang)
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

            GioHang gioHang = gioHangRepository.findByKhachHangId(idKhachHang)
                    .orElseGet(() -> {
                        GioHang newGioHang = new GioHang();
                        newGioHang.setKhachHang(khachHang);
                        return gioHangRepository.save(newGioHang);
                    });

            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(idChiTietSanPham)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            if (chiTietSanPham.getSo_luong() < soLuong) {
                throw new RuntimeException("Số lượng tồn kho không đủ");
            }

            ChiTietGioHangId chiTietGioHangId = new ChiTietGioHangId();
            chiTietGioHangId.setIdGioHang(gioHang.getId_gio_hang());
            chiTietGioHangId.setIdChiTietSanPham(idChiTietSanPham);

            Optional<ChiTietGioHang> existingItem = chiTietGioHangRepository.findById(chiTietGioHangId);

            if (existingItem.isPresent()) {
                ChiTietGioHang chiTietGioHang = existingItem.get();
                chiTietGioHang.setSoLuong(chiTietGioHang.getSoLuong() + soLuong);
                chiTietGioHangRepository.save(chiTietGioHang);
            } else {
                ChiTietGioHang chiTietGioHang = new ChiTietGioHang();
                chiTietGioHang.setId(chiTietGioHangId);
                chiTietGioHang.setGioHang(gioHang);
                chiTietGioHang.setChiTietSanPham(chiTietSanPham);
                chiTietGioHang.setSoLuong(soLuong);
                chiTietGioHangRepository.save(chiTietGioHang);
            }
        } else {
            // Người dùng chưa đăng nhập
            GioHangTam gioHangTam = (GioHangTam) session.getAttribute("gioHangTam");
            if (gioHangTam == null) {
                gioHangTam = new GioHangTam();
                session.setAttribute("gioHangTam", gioHangTam);
            }

            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(idChiTietSanPham)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            if (chiTietSanPham.getSo_luong() < soLuong) {
                throw new RuntimeException("Số lượng tồn kho không đủ");
            }

            GioHangTam.ChiTietGioHangTam chiTiet = gioHangTam.getChiTietGioHangs().stream()
                    .filter(item -> item.getIdChiTietSanPham().equals(idChiTietSanPham))
                    .findFirst()
                    .orElse(null);

            if (chiTiet != null) {
                chiTiet.setSoLuong(chiTiet.getSoLuong() + soLuong);
            } else {
                GioHangTam.ChiTietGioHangTam newChiTiet = new GioHangTam.ChiTietGioHangTam();
                newChiTiet.setIdChiTietSanPham(idChiTietSanPham);
                newChiTiet.setSoLuong(soLuong);
                gioHangTam.getChiTietGioHangs().add(newChiTiet);
            }
        }
    }

    public Object xemGioHang(Integer idKhachHang) {
        if (idKhachHang != null) {
            return gioHangRepository.findByKhachHangIdWithDetails(idKhachHang)
                    .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
        } else {
            GioHangTam gioHangTam = (GioHangTam) session.getAttribute("gioHangTam");
            if (gioHangTam == null) {
                return new GioHangTam(); // Trả về giỏ rỗng nếu chưa có
            }
            return gioHangTam;
        }
    }

    @Transactional
    public void capNhatSoLuong(Integer idKhachHang, Integer idChiTietSanPham, Integer soLuongMoi) {
        if (idKhachHang != null) {
            GioHang gioHang = gioHangRepository.findByKhachHangId(idKhachHang)
                    .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));

            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(idChiTietSanPham)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            if (chiTietSanPham.getSo_luong() < soLuongMoi) {
                throw new RuntimeException("Số lượng tồn kho không đủ");
            }

            ChiTietGioHangId chiTietGioHangId = new ChiTietGioHangId();
            chiTietGioHangId.setIdGioHang(gioHang.getId_gio_hang());
            chiTietGioHangId.setIdChiTietSanPham(idChiTietSanPham);

            ChiTietGioHang chiTietGioHang = chiTietGioHangRepository.findById(chiTietGioHangId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng"));

            chiTietGioHang.setSoLuong(soLuongMoi);
            chiTietGioHangRepository.save(chiTietGioHang);
        } else {
            GioHangTam gioHangTam = (GioHangTam) session.getAttribute("gioHangTam");
            if (gioHangTam == null) {
                throw new RuntimeException("Giỏ hàng tạm không tồn tại");
            }

            GioHangTam.ChiTietGioHangTam chiTiet = gioHangTam.getChiTietGioHangs().stream()
                    .filter(item -> item.getIdChiTietSanPham().equals(idChiTietSanPham))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng tạm"));

            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(idChiTietSanPham)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            if (chiTietSanPham.getSo_luong() < soLuongMoi) {
                throw new RuntimeException("Số lượng tồn kho không đủ");
            }

            chiTiet.setSoLuong(soLuongMoi);
        }
    }

    @Transactional
    public void xoaSanPhamKhoiGioHang(Integer idKhachHang, Integer idChiTietSanPham) {
        if (idKhachHang != null) {
            Optional<GioHang> gioHangOP = gioHangRepository.findByKhachHangId(idKhachHang);
            if (gioHangOP.isEmpty()) {
                return;
            }

            GioHang gioHang = gioHangOP.get();
            ChiTietGioHangId chiTietGioHangId = new ChiTietGioHangId();
            chiTietGioHangId.setIdGioHang(gioHang.getId_gio_hang());
            chiTietGioHangId.setIdChiTietSanPham(idChiTietSanPham);

            Optional<ChiTietGioHang> chiTietGioHangOptional = chiTietGioHangRepository.findById(chiTietGioHangId);
            if (chiTietGioHangOptional.isEmpty()) {
                return;
            }
            ChiTietGioHang chiTietGioHang = chiTietGioHangOptional.get();
            chiTietGioHangRepository.delete(chiTietGioHang);
        } else {
            GioHangTam gioHangTam = (GioHangTam) session.getAttribute("gioHangTam");
            if (gioHangTam == null) {
                throw new RuntimeException("Giỏ hàng tạm không tồn tại");
            }

            GioHangTam.ChiTietGioHangTam chiTiet = gioHangTam.getChiTietGioHangs().stream()
                    .filter(item -> item.getIdChiTietSanPham().equals(idChiTietSanPham))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng tạm"));

            gioHangTam.getChiTietGioHangs().remove(chiTiet);
        }
    }

    @Transactional
    public void xoaToanBoGioHang(Integer idKhachHang) {
        if (idKhachHang != null) {
            GioHang gioHang = gioHangRepository.findByKhachHangIdWithDetails(idKhachHang)
                    .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));

            gioHang.getChiTietGioHangs().clear();
            gioHangRepository.save(gioHang);
            chiTietGioHangRepository.deleteAll(gioHang.getChiTietGioHangs());
        } else {
            GioHangTam gioHangTam = (GioHangTam) session.getAttribute("gioHangTam");
            if (gioHangTam != null) {
                gioHangTam.getChiTietGioHangs().clear();
            }
        }
    }

    public BigDecimal tinhTongTien(Integer idKhachHang) {
        if (idKhachHang != null) {
            GioHang gioHang = gioHangRepository.findByKhachHangIdWithDetails(idKhachHang)
                    .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
            return gioHang.getChiTietGioHangs().stream()
                    .map(item -> item.getChiTietSanPham().getGia_ban()
                            .multiply(BigDecimal.valueOf(item.getSoLuong())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            GioHangTam gioHangTam = (GioHangTam) session.getAttribute("gioHangTam");
            if (gioHangTam == null || gioHangTam.getChiTietGioHangs().isEmpty()) {
                return BigDecimal.ZERO;
            }
            return gioHangTam.getChiTietGioHangs().stream()
                    .map(item -> {
                        ChiTietSanPham sp = chiTietSanPhamRepository.findById(item.getIdChiTietSanPham())
                                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
                        return sp.getGia_ban().multiply(BigDecimal.valueOf(item.getSoLuong()));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    // Đồng bộ giỏ hàng tạm sang database khi đăng nhập
    @Transactional
    public void syncGioHangTamToDatabase(Integer idKhachHang) {
        GioHangTam gioHangTam = (GioHangTam) session.getAttribute("gioHangTam");
        if (gioHangTam != null && !gioHangTam.getChiTietGioHangs().isEmpty()) {
            for (GioHangTam.ChiTietGioHangTam item : gioHangTam.getChiTietGioHangs()) {
                themSanPhamVaoGioHang(idKhachHang, item.getIdChiTietSanPham(), item.getSoLuong());
            }
            session.removeAttribute("gioHangTam"); // Xóa giỏ tạm sau khi đồng bộ
        }
    }


}