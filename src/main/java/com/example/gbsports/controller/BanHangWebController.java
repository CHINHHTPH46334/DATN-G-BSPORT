package com.example.gbsports.controller;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.request.HoaDonRequest;
import com.example.gbsports.request.VoucherRequetst;
import com.example.gbsports.response.HoaDonChiTietResponse;
import com.example.gbsports.response.HoaDonResponse;
import com.example.gbsports.response.VoucherBHResponse;
import com.example.gbsports.service.GioHangService;
import com.example.gbsports.service.VoucherService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private KhachHangRepo khachHangRepo;
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private GioHangWebRepo gioHangWebRepo;
    @Autowired
    private GioHangService gioHangService;
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
    Integer idKhachHang = 0;
    Boolean xacNhan = false;

//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')") public
    @PostMapping("/taoHoaDonWeb")
    public ResponseEntity<?> taoHoaDonWeb(@RequestBody HoaDonRequest hoaDon) {
        HoaDon hoaDonAdd = new HoaDon();
        BeanUtils.copyProperties(hoaDon, hoaDonAdd);
        hoaDonAdd.setMa_hoa_don(generateUniqueMaHoaDon());
        hoaDonAdd.setLoai_hoa_don("Online");
        hoaDonAdd.setNgay_tao(LocalDateTime.now());
        hoaDonAdd.setNgay_sua(LocalDateTime.now());
        hoaDonAdd.setPhuong_thuc_nhan_hang("Giao hàng");
        hoaDonAdd.setVoucher(hoaDon.getVoucher().getId() != 0 ? voucherRepository.findById(hoaDon.getVoucher().getId()).get() : null);
        hoaDonAdd.setKhachHang(hoaDon.getId_khach_hang() == 0 ? null : khachHangRepo.findById(hoaDon.getId_khach_hang()).get());
        hoaDonRepo.save(hoaDonAdd);
        idHoaDon = hoaDonAdd.getId_hoa_don();
        idKhachHang = hoaDonAdd.getKhachHang() == null || hoaDonAdd.getKhachHang().getIdKhachHang() == null? 0: hoaDonAdd.getKhachHang().getIdKhachHang();
        xacNhan = hoaDon.getIsChuyen();
        TheoDoiDonHang theoDoiDonHang = new TheoDoiDonHang();
        theoDoiDonHang.setHoaDon(hoaDonAdd);
        theoDoiDonHang.setTrang_thai("Chờ xác nhận");
        theoDoiDonHang.setNgay_chuyen(LocalDateTime.now());
        theoDoiDonHangRepo.save(theoDoiDonHang);
        if (hoaDon.getVoucher().getId() != 0) {
            updateVoucherSoLuong(hoaDonAdd.getVoucher().getId());
        }
        sendEmail(hoaDonAdd.getEmail(), hoaDonAdd.getMa_hoa_don());
        if (hoaDon.getIsChuyen()) {
            TheoDoiDonHang theoDoiDonHang1 = new TheoDoiDonHang();
            theoDoiDonHang1.setHoaDon(hoaDonAdd);
            theoDoiDonHang1.setTrang_thai("Đã xác nhận");
            theoDoiDonHang1.setNgay_chuyen(LocalDateTime.now());
            theoDoiDonHangRepo.save(theoDoiDonHang1);
        }
        return ResponseEntity.ok(hoaDonAdd);
    }

    private void updateVoucherSoLuong(Integer idVoucher) {
        Voucher vc = voucherRepository.findById(idVoucher).get();
        vc.setSoLuong(vc.getSoLuong() - 1);
        voucherRepository.save(vc);
    }

    //        public void deleteGioHangByKh(List<HoaDonChiTiet> list){
//        GioHang gh = gioHangRepository.findByKhachHangId(idKhachHang).get();
//
//
//        ArrayList<>
//    }
    private void updateSoLuongSanPham(List<HoaDonChiTiet> list) {
        for (HoaDonChiTiet hdct : list) {
            ChiTietSanPham ctsp = chiTietSanPhamRepo.findById(hdct.getChiTietSanPham().getId_chi_tiet_san_pham()).get();
            if (ctsp.getSo_luong() <= hdct.getSo_luong()) {
                ctsp.setSo_luong(0);
                ctsp.setTrang_thai("Không hoạt động");
                chiTietSanPhamRepo.save(ctsp);
            } else {
                ctsp.setSo_luong(ctsp.getSo_luong() - hdct.getSo_luong());
                chiTietSanPhamRepo.save(ctsp);
            }

        }
    }

    @PostMapping("/taoHoaDonWeb1")
    public ResponseEntity<?> taoHoaDonWeb1(@RequestBody HoaDon hoaDon) {
        HoaDon hoaDonAdd = new HoaDon();
        BeanUtils.copyProperties(hoaDon, hoaDonAdd);
        hoaDonAdd.setMa_hoa_don(generateUniqueMaHoaDon());
        hoaDonAdd.setLoai_hoa_don("Online");
        hoaDonAdd.setNgay_tao(LocalDateTime.now());
        hoaDonAdd.setNgay_sua(LocalDateTime.now());
        hoaDonAdd.setPhuong_thuc_nhan_hang("Giao hàng");
        hoaDonAdd.setVoucher(hoaDon.getVoucher().getId() != null ? voucherRepository.findById(hoaDon.getVoucher().getId()).get() : null);
        hoaDonAdd.setKhachHang(hoaDon.getKhachHang().getIdKhachHang() == 0 ? null : khachHangRepo.findById(hoaDon.getKhachHang().getIdKhachHang()).get());
        hoaDonRepo.save(hoaDonAdd);
        idHoaDon = hoaDonAdd.getId_hoa_don();
        TheoDoiDonHang theoDoiDonHang = new TheoDoiDonHang();
        theoDoiDonHang.setHoaDon(hoaDonAdd);
        theoDoiDonHang.setTrang_thai("Đã xác nhận");
        theoDoiDonHang.setNgay_chuyen(LocalDateTime.now());
        theoDoiDonHangRepo.save(theoDoiDonHang);
        sendEmail(hoaDonAdd.getEmail(), hoaDonAdd.getMa_hoa_don());
        return ResponseEntity.ok(hoaDonAdd);
    }


    @PostMapping("/taoHoaDonChiTiet")
    public ResponseEntity<?> taoHoaDonChiTiet(@RequestBody List<HoaDonChiTiet> hoaDonChiTiets) {
        ArrayList<HoaDonChiTiet> listHdct = new ArrayList<>();
        for (HoaDonChiTiet hdct : hoaDonChiTiets) {
            HoaDonChiTiet hoaDonChiTietAdd = new HoaDonChiTiet();
            hoaDonChiTietAdd.setHoaDon(hoaDonRepo.findById(idHoaDon).get());
            System.out.println("id Hoá đơn: fdfdfd: " + idHoaDon);
            hoaDonChiTietAdd.setChiTietSanPham(
                    chiTietSanPhamRepo.findById(hdct.getChiTietSanPham().getId_chi_tiet_san_pham()).orElseThrow()
            );
            hoaDonChiTietAdd.setSo_luong(hdct.getSo_luong());
            hoaDonChiTietAdd.setDon_gia(hdct.getDon_gia());

            hoaDonChiTietRepo.save(hoaDonChiTietAdd);
            listHdct.add(hoaDonChiTietAdd);
        }
        if (xacNhan) {
            updateSoLuongSanPham(listHdct);
        }
        if (idKhachHang != 0) {
            for (HoaDonChiTiet hdct : listHdct) {
                gioHangService.xoaSanPhamKhoiGioHang(idKhachHang, hdct.getChiTietSanPham().getId_chi_tiet_san_pham());
            }
        }
        return ResponseEntity.ok(listHdct);
    }

    //
    @PostMapping("/suaHoaDon")
    public ResponseEntity<?> suaHoaDon(@RequestBody HoaDon hoaDon) {
        System.out.println("idHoaDonSua" + hoaDon.getId_hoa_don());
        HoaDon hoaDonAdd = new HoaDon();
        BeanUtils.copyProperties(hoaDon, hoaDonAdd);
        hoaDonAdd.setMa_hoa_don(generateUniqueMaHoaDon());
        hoaDonAdd.setLoai_hoa_don("Online");
        hoaDonAdd.setNgay_sua(LocalDateTime.now());
        hoaDonAdd.setPhuong_thuc_nhan_hang("Giao hàng");
        hoaDonAdd.setVoucher(hoaDon.getVoucher().getId() != null ? voucherRepository.findById(hoaDon.getVoucher().getId()).get() : null);
        hoaDonRepo.save(hoaDonAdd);
        idHoaDon = hoaDonAdd.getId_hoa_don();
        TheoDoiDonHang theoDoiDonHang = new TheoDoiDonHang();
        theoDoiDonHang.setHoaDon(hoaDonAdd);
        theoDoiDonHang.setTrang_thai("Đã xác nhận");
        theoDoiDonHang.setNgay_chuyen(LocalDateTime.now());
        theoDoiDonHangRepo.save(theoDoiDonHang);
        sendEmail(hoaDonAdd.getEmail(), hoaDonAdd.getMa_hoa_don());

        return ResponseEntity.ok(hoaDonAdd);
    }

    private void sendEmail(String toEmail, String maHoaDon) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String tenDN = toEmail.split("@")[0];
            helper.setTo(toEmail);
            helper.setSubject("Thông tin đơn hàng của bạn");
            String body = "Cảm ơn vì đã tin tưởng chúng tôi <br><br>"
                    + "<b> Mã hóa đơn của bạn là: " + maHoaDon + "</b><br>"
                    + "<b>Tra cứu đơn hàng tại: http://localhost:5173/tracuudonhang-banhang theo mã hóa đơn đã gửi.<b>"
                    + "<p>Nếu bạn gặp bất kỳ vấn đề nào, vui lòng liên hệ bộ phận hỗ trợ.</p>"
                    + "<p>Trân trọng,</p>"
                    + "<p><b>[G&B Sport]</b></p>";
            helper.setText(body, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/thongTinHoaDonChiTiet")
    public List<HoaDonChiTietResponse> getTraCuuDonHang(@RequestParam("maHoaDon") String maHoaDon) {
        return hoaDonRepo.listThongTinHoaDon(maHoaDon);
    }

    @GetMapping("/thongTinTimeLine")
    public List<HoaDonChiTietResponse> getThongTinDonHang(@RequestParam("maHoaDon") String maHoaDon) {
        return hoaDonRepo.listTrangThaiTimeLineBanHangWeb(maHoaDon);
    }

    @GetMapping("/thongTinKhachHang")
    public List<HoaDonChiTietResponse> getThongTinKhachHang(@RequestParam("maHoaDon") String maHoaDon) {
        return hoaDonRepo.listThongTinKhachHang(maHoaDon);
    }

    @GetMapping("/thongTinHoaDon")
    public HoaDonResponse getHoaDonByMaHoaDon(@RequestParam("maHoaDon") String maHoaDon) {
        return hoaDonRepo.getHoaDonByMaHoaDon(maHoaDon);
    }

    @GetMapping("/voucherTheoGiaTruyen")
    public List<VoucherBHResponse> voucherTheoGiaTruyen(@RequestParam("giaTruyen") BigDecimal giaTruyen) {
        return voucherService.listVoucherTheoGiaTruyen(giaTruyen);
    }
}