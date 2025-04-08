package com.example.gbsports.controller;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.entity.HoaDonChiTiet;
import com.example.gbsports.entity.TheoDoiDonHang;
import com.example.gbsports.entity.Voucher;
import com.example.gbsports.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/banhangweb")
public class BanHangWebController {
    @Autowired
    HoaDonRepo hoaDonRepo;
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    HoaDonChiTietRepo hoaDonChiTietRepo;
    @Autowired
    ChiTietSanPhamRepo chiTietSanPhamRepo;
    @Autowired
    TheoDoiDonHangRepo theoDoiDonHangRepo;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String generateUniqueMaHoaDon() {
        Random random = new Random();
        String maHoaDon;
        boolean isDuplicate;
        do {
            StringBuilder code = new StringBuilder("HD");
            for (int i = 0; i < 6; i++) {
                code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            maHoaDon = code.toString();

            final String finalMaHoaDon = maHoaDon;
            isDuplicate = hoaDonRepo.findAll().stream()
                    .anyMatch(hd -> finalMaHoaDon.equalsIgnoreCase(hd.getMa_hoa_don()));

        } while (isDuplicate);

        return maHoaDon;
    }
    Integer idHoaDon = 0;
    @PostMapping("/taoHoaDonWeb")
    public ResponseEntity<?> taoHoaDonWeb(@RequestBody HoaDon hoaDon) {
        HoaDon hoaDonAdd = new HoaDon();
        BeanUtils.copyProperties(hoaDon, hoaDonAdd);
        hoaDonAdd.setMa_hoa_don(generateUniqueMaHoaDon());
        hoaDonAdd.setLoai_hoa_don("Online");
        hoaDonAdd.setNgay_tao(LocalDateTime.now());
        hoaDonAdd.setNgay_sua(LocalDateTime.now());
        hoaDonAdd.setPhuong_thuc_nhan_hang("Giao hàng");
        hoaDonAdd.setVoucher(hoaDon.getVoucher().getId() != null ? voucherRepository.findById(hoaDon.getVoucher().getId()).get() : null);
        hoaDonRepo.save(hoaDonAdd);
        idHoaDon = hoaDonAdd.getId_hoa_don();
        TheoDoiDonHang theoDoiDonHang = new TheoDoiDonHang();
        theoDoiDonHang.setHoaDon(hoaDonAdd);
        theoDoiDonHang.setTrang_thai("Chờ xác nhận");
        theoDoiDonHang.setNgay_chuyen(LocalDateTime.now());
        theoDoiDonHangRepo.save(theoDoiDonHang);
        return ResponseEntity.ok(hoaDonAdd);
    }
    @PostMapping("/taoHoaDonChiTiet")
    public ResponseEntity<?> taoHoaDonChiTiet(@RequestBody List<HoaDonChiTiet> hoaDonChiTiets){
        ArrayList<HoaDonChiTiet> listHdct = new ArrayList<>();
        for (HoaDonChiTiet hdct : hoaDonChiTiets) {
            HoaDonChiTiet hoaDonChiTietAdd = new HoaDonChiTiet();
            hoaDonChiTietAdd.setHoaDon(hoaDonRepo.findById(idHoaDon).get());
            hoaDonChiTietAdd.setChiTietSanPham(
                    chiTietSanPhamRepo.findById(hdct.getChiTietSanPham().getId_chi_tiet_san_pham()).orElseThrow()
            );
            hoaDonChiTietAdd.setSo_luong(hdct.getSo_luong());
            hoaDonChiTietAdd.setDon_gia(hdct.getDon_gia());

            hoaDonChiTietRepo.save(hoaDonChiTietAdd);
            listHdct.add(hoaDonChiTietAdd);
        }
        return ResponseEntity.ok(listHdct);
    }
}
