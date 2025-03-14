package com.example.gbsports.controller;

import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.entity.HoaDonChiTiet;
import com.example.gbsports.entity.NhanVien;
import com.example.gbsports.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;


@Controller
@RequestMapping("/admin/ban-hang")
public class BanHangController {

    @Autowired
    private HoaDonRepo hoaDonRepo;

    @Autowired
    private GioHangRepo gioHangRepo;

    @Autowired
    private ChiTietSanPhamRepo chiTietSanPhamRepo;

    @Autowired
    private KhachHangRepo khachHangRepo;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private NhanVienRepo nhanVienRepo;

    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepo;

    Integer idHD = null;
    Integer idCTSP = null;
    Integer idNV = null;

    public void viewALl(Model model) {
        model.addAttribute("listHoaDon", hoaDonRepo.getAllHoaDonCTT());
        model.addAttribute("listCTSP", chiTietSanPhamRepo.listCTSP());
        model.addAttribute("listKH", khachHangRepo.findAll());
        model.addAttribute("listVC", voucherRepository.findAll());
        model.addAttribute("listNV", nhanVienRepo.findAll());
        if (idHD == null) {
            model.addAttribute("hdbh", null);
        } else {
            model.addAttribute("hdbh", hoaDonRepo.findHoaDonById(idHD).get(0));
            model.addAttribute("listGH", gioHangRepo.getSPGH(idHD));
        }
        if (idCTSP == null) {
            model.addAttribute("slgh", null);
        } else {
            ChiTietSanPham ct = new ChiTietSanPham();
            for (ChiTietSanPham ctsp: chiTietSanPhamRepo.findAll()) {
                if (idCTSP == ctsp.getId_chi_tiet_san_pham()) {
                    ct = ctsp;
                }
            }
            model.addAttribute("slgh", ct);
        }
    }

    @GetMapping("/view")
    public String viewBanHang(Model model) {
        viewALl(model);
        return "banhang";
    }

    @PostMapping("/view/add-hoa-don")
    public String addHoaDon() {
        HoaDon hoaDon = new HoaDon();
        idNV = 1;
        Optional<NhanVien> nv = nhanVienRepo.findById(idNV);
        hoaDon.setMa_hoa_don(generateUniqueMaHoaDon());
        hoaDon.setNhanVien(nv.get());
        hoaDon.setNgay_tao(LocalDateTime.now());
        hoaDon.setTrang_thai("Chưa thanh toán");
        hoaDon.setTong_tien_truoc_giam(BigDecimal.ZERO);
        hoaDon.setPhi_van_chuyen(BigDecimal.ZERO);
        hoaDon.setTong_tien_sau_giam(BigDecimal.ZERO);

        hoaDonRepo.save(hoaDon);
        return "redirect:/admin/ban-hang/view";
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    private String generateUniqueMaHoaDon() {
        Random random = new Random();
        String maHoaDon;
        Boolean check = false;
        do {
            StringBuilder code = new StringBuilder("HD");
            for (int i = 0; i < 6; i++) {
                code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            maHoaDon = code.toString();

            for (HoaDon hd: hoaDonRepo.findAll()) {
                if (maHoaDon.equalsIgnoreCase(hd.getMa_hoa_don())) {
                    check = true;
                }
            }
        } while (check);
        return maHoaDon;
    }

    @GetMapping("/view/{idHd}")
    public String detail(@RequestParam("idHd") Integer id) {
        idHD = id;
        return "redirect:/admin/ban-hang/view";
    }

    @GetMapping("/view/addAndUdateSPGH")
    public String addAndUpdateSPGH(@RequestParam("idCTSP") Integer id_chi_tiet_san_pham,
                                   @RequestParam("idHoaDon") Integer id_hoa_don,
                                   @RequestParam("soLuong") Integer so_luong) {
        for (HoaDonChiTiet hdcts: hoaDonChiTietRepo.findAll()) {
            if (hdcts.getChiTietSanPham().getId_chi_tiet_san_pham() == id_chi_tiet_san_pham && hdcts.getHoaDon().getId_hoa_don() == id_hoa_don) {
                hoaDonChiTietRepo.addSLGH(id_chi_tiet_san_pham,id_hoa_don,so_luong);
            } else if (hdcts.getChiTietSanPham().getId_chi_tiet_san_pham() != id_chi_tiet_san_pham && hdcts.getHoaDon().getId_hoa_don() == id_hoa_don) {
                HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
                BigDecimal giaSP = null;
                for (ChiTietSanPham chiTietSanPham: chiTietSanPhamRepo.findAll()) {
                    if (chiTietSanPham.getId_chi_tiet_san_pham() == id_chi_tiet_san_pham) {
                        hoaDonChiTiet.setChiTietSanPham(chiTietSanPham);
                        hoaDonChiTiet.setSo_luong(so_luong);
                        giaSP = BigDecimal.valueOf(chiTietSanPham.getGia_ban());
                    }
                }
                for (HoaDon hoaDon: hoaDonRepo.findAll()) {
                    if (hoaDon.getId_hoa_don() == id_hoa_don) {
                        hoaDonChiTiet.setHoaDon(hoaDon);
                    }
                }
                BigDecimal slhdct = BigDecimal.valueOf(so_luong);
                BigDecimal don_giahdct;
                don_giahdct = slhdct.multiply(giaSP);
                hoaDonChiTiet.setDon_gia(don_giahdct);
                hoaDonChiTietRepo.save(hoaDonChiTiet);
            }
        }


        return "redirect:/admin/ban-hang/view";
    }
}
