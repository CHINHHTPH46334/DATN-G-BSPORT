package com.example.gbsports.controller;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.service.ZaloPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin/ban-hang")
public class BanHangController {

    @Autowired
    private HoaDonRepo hoaDonRepo;

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

    @Autowired
    private ZaloPayService zaloPayService;

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
            model.addAttribute("listGH", hoaDonChiTietRepo.getSPGH(idHD));
        }
        if (idCTSP == null) {
            model.addAttribute("slgh", null);
        } else {
            ChiTietSanPham ct = new ChiTietSanPham();
            for (ChiTietSanPham ctsp : chiTietSanPhamRepo.findAll()) {
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

    @GetMapping("/view/{idHd}")
    public String detail(@RequestParam("idHd") Integer id) {
        idHD = id;
        return "redirect:/admin/ban-hang/view";
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

        HoaDon savedHoaDon = hoaDonRepo.save(hoaDon);
        idHD = savedHoaDon.getId_hoa_don();

        return "redirect:/admin/ban-hang/view";
    }

    @RequestMapping(value = "/update-khach-hang", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> updateKhachHang(
            @RequestParam("idHoaDonUDKH") String idHoaDonStr,
            @RequestParam("idKhachHangUDKH") String idKhachHangStr) {

        Map<String, Object> response = new HashMap<>();

        System.out.println("idHoaDon: " + idHoaDonStr);
        System.out.println("idKhachHang: " + idKhachHangStr);

        Integer idHoaDon = null;
        Integer idKhachHang = null;

        try {
            if (idHoaDonStr != null && !idHoaDonStr.trim().isEmpty()) {
                idHoaDon = Integer.parseInt(idHoaDonStr);
            }

            if (idKhachHangStr != null && !idKhachHangStr.trim().isEmpty()) {
                idKhachHang = Integer.parseInt(idKhachHangStr);
            }
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "ID không phải là số hợp lệ");
            return response;
        }

        if (idHoaDon == null || idKhachHang == null) {
            response.put("success", false);
            response.put("message", "ID hóa đơn hoặc ID khách hàng không hợp lệ");
            return response;
        }

        try {
            Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(idHoaDon);
            Optional<KhachHang> khachHangOpt = khachHangRepo.findById(idKhachHang);

            if (hoaDonOpt.isPresent() && khachHangOpt.isPresent()) {
                HoaDon hoaDon = hoaDonOpt.get();
                KhachHang khachHang = khachHangOpt.get();
                hoaDon.setKhachHang(khachHang);
                hoaDonRepo.save(hoaDon);

                response.put("success", true);
                response.put("message", "Cập nhật khách hàng thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy hóa đơn hoặc khách hàng");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/admin/khach-hang/them-moi")
    @ResponseBody
    public Map<String, Object> themKhachHang(
            @RequestBody KhachHang khachHang) {
        Map<String, Object> response = new HashMap<>();
        try {
            KhachHang newKhachHang = khachHangRepo.save(khachHang);
            response.put("success", true);
            response.put("idKhachHang", newKhachHang.getIdKhachHang());
            response.put("message", "Thêm khách hàng thành công");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi thêm khách hàng: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/admin/ban-hang/update-khach-hang")
    @ResponseBody
    public Map<String, Object> updateKhachHang(
            @RequestParam("idHoaDonUDKH") Integer idHoaDon,
            @RequestParam("idKhachHangUDKH") Integer idKhachHang) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(idHoaDon);
            Optional<KhachHang> khachHangOpt = khachHangRepo.findById(idKhachHang);

            if (hoaDonOpt.isPresent() && khachHangOpt.isPresent()) {
                HoaDon hoaDon = hoaDonOpt.get();
                KhachHang khachHang = khachHangOpt.get();
                hoaDon.setKhachHang(khachHang);
                hoaDonRepo.save(hoaDon);

                response.put("success", true);
                response.put("message", "Cập nhật khách hàng thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy hóa đơn hoặc khách hàng");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/update-voucher")
    @ResponseBody
    public ResponseEntity<?> updateVoucher(
            @RequestParam("idHoaDon") Integer idHoaDon,
            @RequestParam("idVoucher") Integer idVoucher) {

        if (idHoaDon == null) {
            return ResponseEntity.badRequest().body("ID hóa đơn không hợp lệ");
        }

        try {
            Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(idHoaDon);

            if (hoaDonOpt.isPresent()) {
                HoaDon hoaDon = hoaDonOpt.get();

                if (idVoucher != null && idVoucher > 0) {
                    Optional<Voucher> voucherOpt = voucherRepository.findById(idVoucher);
                    if (voucherOpt.isPresent()) {
                        hoaDon.setVoucher(voucherOpt.get());
                    }
                } else {
                    hoaDon.setVoucher(null);
                }

                hoaDonRepo.save(hoaDon);
                updateTongTienHoaDon(idHoaDon);

                HoaDon updatedHoaDon = hoaDonRepo.findById(idHoaDon).get();
                Map<String, Object> response = new HashMap<>();
                response.put("tongTienTruocGiam", updatedHoaDon.getTong_tien_truoc_giam());
                response.put("tongTienSauGiam", updatedHoaDon.getTong_tien_sau_giam());
                response.put("message", "Cập nhật voucher thành công");

                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy hóa đơn");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/get-applicable-vouchers")
    @ResponseBody
    public ResponseEntity<?> getApplicableVouchers(@RequestParam("idHoaDon") Integer idHoaDon) {
        if (idHoaDon == null) {
            return ResponseEntity.badRequest().body("ID hóa đơn không hợp lệ");
        }

        try {
            Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(idHoaDon);

            if (hoaDonOpt.isPresent()) {
                HoaDon hoaDon = hoaDonOpt.get();
                BigDecimal tongTienTruocGiam = hoaDon.getTong_tien_truoc_giam();

                List<Voucher> applicableVouchers = voucherRepository.findAll().stream()
                        .filter(v -> tongTienTruocGiam.compareTo(v.getGiaTriToiThieu()) >= 0)
                        .collect(Collectors.toList());

                return ResponseEntity.ok().body(applicableVouchers);
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy hóa đơn");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/thanh-toan")
    public String thanhToan(
            @RequestParam(value = "id_hoa_don", required = true) String idHoaDonStr,
            @RequestParam("idKhachHang") Integer idKhachHang,
            @RequestParam("idNhanVien") Integer idNhanVien,
            @RequestParam("hinhThucThanhToan") String hinhThucThanhToan,
            @RequestParam("phuongThucNhanHang") String phuongThucNhanHang,
            @RequestParam(value = "phiVanChuyen", required = false, defaultValue = "0") BigDecimal phiVanChuyen,
            @RequestParam(value = "idVoucher", required = false) Integer idVoucher,
            @RequestParam(value = "tienKhachDua", required = false) BigDecimal tienKhachDua,
            Model model) {
        Integer idHoaDon = (idHoaDonStr != null && !idHoaDonStr.isEmpty()) ? Integer.parseInt(idHoaDonStr) : null;

        if (idHoaDon == null) {
            model.addAttribute("error", "ID hóa đơn không hợp lệ!");
            return "redirect:/admin/ban-hang/view";
        }

        Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(idHoaDon);
        if (!hoaDonOpt.isPresent()) {
            model.addAttribute("error", "Không tìm thấy hóa đơn!");
            return "redirect:/admin/ban-hang/view";
        }

        HoaDon hoaDon = hoaDonOpt.get();

        Optional<KhachHang> khachHangOpt = khachHangRepo.findById(idKhachHang);
        if (khachHangOpt.isPresent()) {
            hoaDon.setKhachHang(khachHangOpt.get());
        }

        Optional<NhanVien> nhanVienOpt = nhanVienRepo.findById(idNhanVien);
        if (nhanVienOpt.isPresent()) {
            hoaDon.setNhanVien(nhanVienOpt.get());
        }

        if (idVoucher != null && idVoucher > 0) {
            Optional<Voucher> voucherOpt = voucherRepository.findById(idVoucher);
            if (voucherOpt.isPresent()) {
                hoaDon.setVoucher(voucherOpt.get());
            }
        } else {
            hoaDon.setVoucher(null);
        }

        hoaDon.setPhuong_thuc_nhan_hang(phuongThucNhanHang);
        hoaDon.setHinh_thuc_thanh_toan(hinhThucThanhToan);
        hoaDon.setPhi_van_chuyen(phiVanChuyen);
        hoaDon.setTrang_thai("Đã thanh toán");
        updateTongTienHoaDon(idHoaDon);

        hoaDon = hoaDonRepo.findById(idHoaDon).get();

        if ("Tiền mặt".equals(hinhThucThanhToan)) {
            if (tienKhachDua == null) {
                model.addAttribute("error", "Vui lòng nhập số tiền khách đưa!");
                return "redirect:/admin/ban-hang/view";
            } else if (tienKhachDua.compareTo(hoaDon.getTong_tien_sau_giam()) >= 0) {
                System.out.println("nhảy vào thanh toán ----------------------------------------");
                hoaDon.setTrang_thai("Đã thanh toán");
                hoaDonRepo.save(hoaDon);
                model.addAttribute("message", "Thanh toán thành công!");
                return "redirect:/admin/ban-hang/view";
            } else {
                model.addAttribute("error", "Số tiền khách đưa không đủ!");
                return "redirect:/admin/ban-hang/view";
            }
        } else if ("Chuyển khoản".equals(hinhThucThanhToan)) {
            try {
                // Lấy mã QR từ ZaloPay
                Map<String, Object> qrCodeResponse = zaloPayService.createQRCode(
                        hoaDon.getTong_tien_sau_giam().longValue(),
                        idHoaDon.longValue()
                );
                String qrCodeUrl = (String) qrCodeResponse.get("qr_code_url");

                if (qrCodeUrl != null && !qrCodeUrl.isEmpty()) {
                    model.addAttribute("qrCodeUrl", qrCodeUrl);
                    model.addAttribute("message", "Vui lòng quét mã QR để thanh toán.");
                    hoaDon.setTrang_thai("Đã thanh toán");
                    hoaDonRepo.save(hoaDon);
                    return "payment-qr"; // Trả về view hiển thị mã QR
                } else {
                    model.addAttribute("error", "Không thể tạo mã QR. Vui lòng thử lại.");
                    return "redirect:/admin/ban-hang/view";
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Có lỗi xảy ra khi tạo mã QR. Vui lòng thử lại.");
                return "redirect:/admin/ban-hang/view";
            }
        } else {
            model.addAttribute("error", "Hình thức thanh toán không hợp lệ!");
            return "redirect:/admin/ban-hang/view";
        }
    }

    @PostMapping("/zalopay/callback")
    public ResponseEntity<String> handleZaloPayCallback(@RequestBody Map<String, Object> callbackData) {
        try {
            // Kiểm tra tính hợp lệ của callback
            if (isValidCallback(callbackData)) {
                // Lấy thông tin từ callback
                Long appTransId = Long.parseLong(callbackData.get("app_trans_id").toString());
                String status = callbackData.get("status").toString();

                if ("1".equals(status)) { // Thanh toán thành công
                    // Cập nhật trạng thái hóa đơn
                    Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(appTransId.intValue());
                    if (hoaDonOpt.isPresent()) {
                        HoaDon hoaDon = hoaDonOpt.get();
                        hoaDon.setTrang_thai("Đã thanh toán");
                        hoaDonRepo.save(hoaDon);
                    }
                }
                return ResponseEntity.ok("Callback processed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid callback data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing callback");
        }
    }

    private boolean isValidCallback(Map<String, Object> callbackData) {
        // Kiểm tra tính hợp lệ của callback (ví dụ: chữ ký HMAC)
        return true; // Thay thế bằng logic thực tế
    }

    @GetMapping("/check-so-luong")
    public ResponseEntity<Map<String, Integer>> checkSoLuong(
            @RequestParam("idCTSP") Integer idCTSP) {
        Optional<ChiTietSanPham> ctspOpt = chiTietSanPhamRepo.findById(idCTSP);

        if (!ctspOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        int soLuongTon = ctspOpt.get().getSo_luong();

        Map<String, Integer> response = new HashMap<>();
        response.put("soLuongTon", soLuongTon);

        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/view/addAndUdateSPGH")
    public String addAndUpdateSPGH(
            @RequestParam("idCTSP") Integer idChiTietSanPham,
            @RequestParam(value = "idHoaDonADGH", required = false) Integer idHoaDon,
            @RequestParam("soLuong") Integer soLuong,
            Model model) {

        if (idHoaDon == null) {
            if (idHD != null) {
                idHoaDon = idHD;
            } else {
                model.addAttribute("error", "Không có hóa đơn được chọn!");
                return "redirect:/admin/ban-hang/view";
            }
        }

        Optional<HoaDonChiTiet> existingHdct = hoaDonChiTietRepo.findByChiTietSanPhamIdAndHoaDonId(idChiTietSanPham, idHoaDon);

        Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepo.findById(idChiTietSanPham);
        if (!chiTietSanPhamOpt.isPresent()) {
            model.addAttribute("error", "Không tìm thấy sản phẩm!");
            return "redirect:/admin/ban-hang/view";
        }

        ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();

        if (chiTietSanPham.getSo_luong() < soLuong) {
            model.addAttribute("error", "Số lượng không đủ!");
            return "redirect:/admin/ban-hang/view";
        }

        Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(idHoaDon);
        if (!hoaDonOpt.isPresent()) {
            model.addAttribute("error", "Không tìm thấy hóa đơn!");
            return "redirect:/admin/ban-hang/view";
        }

        HoaDon hoaDon = hoaDonOpt.get();

        if (existingHdct.isPresent()) {
            HoaDonChiTiet hdct = existingHdct.get();
            int newSoLuong = hdct.getSo_luong() + soLuong;
            if (chiTietSanPham.getSo_luong() < newSoLuong) {
                model.addAttribute("error", "Số lượng không đủ!");
                return "redirect:/admin/ban-hang/view";
            }
            hdct.setSo_luong(newSoLuong);
            hdct.setDon_gia(BigDecimal.valueOf(newSoLuong).multiply(chiTietSanPham.getGia_ban()));
            hoaDonChiTietRepo.save(hdct);
        } else {
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setChiTietSanPham(chiTietSanPham);
            hoaDonChiTiet.setHoaDon(hoaDon);
            hoaDonChiTiet.setSo_luong(soLuong);
            hoaDonChiTiet.setDon_gia(BigDecimal.valueOf(soLuong).multiply(chiTietSanPham.getGia_ban()));
            hoaDonChiTietRepo.save(hoaDonChiTiet);
        }

        chiTietSanPham.setSo_luong(chiTietSanPham.getSo_luong() - soLuong);
        chiTietSanPhamRepo.save(chiTietSanPham);

        updateTongTienHoaDon(idHoaDon);

        return "redirect:/admin/ban-hang/view";
    }

    private void updateTongTienHoaDon(Integer idHoaDon) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(idHoaDon);
        if (!hoaDonOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy hóa đơn");
        }

        HoaDon hoaDon = hoaDonOpt.get();

        BigDecimal tongDonGia = hoaDonChiTietRepo.sumDonGiaByHoaDonId(idHoaDon);
        if (tongDonGia == null) tongDonGia = BigDecimal.ZERO;

        BigDecimal phiVanChuyen = hoaDon.getPhi_van_chuyen() != null ? hoaDon.getPhi_van_chuyen() : BigDecimal.ZERO;
        BigDecimal tongTienTruocGiam = tongDonGia.add(phiVanChuyen);

        BigDecimal giamGia = BigDecimal.ZERO;
        if (hoaDon.getVoucher() != null) {
            Voucher voucher = hoaDon.getVoucher();
            if (tongTienTruocGiam.compareTo(voucher.getGiaTriToiThieu()) >= 0) {
                giamGia = voucher.getGiaTriGiam();
                if (giamGia.compareTo(tongTienTruocGiam) > 0) {
                    giamGia = tongTienTruocGiam;
                }
            }
        }

        BigDecimal tongTienSauGiam = tongTienTruocGiam.subtract(giamGia);
        if (tongTienSauGiam.compareTo(BigDecimal.ZERO) < 0) {
            tongTienSauGiam = BigDecimal.ZERO;
        }

        hoaDon.setTong_tien_truoc_giam(tongTienTruocGiam);
        hoaDon.setTong_tien_sau_giam(tongTienSauGiam);
        hoaDonRepo.save(hoaDon);
    }
}
