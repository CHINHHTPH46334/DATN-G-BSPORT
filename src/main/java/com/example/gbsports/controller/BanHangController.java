package com.example.gbsports.controller;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.response.HoaDonChiTietResponse;
import com.example.gbsports.response.HoaDonResponse;
import com.example.gbsports.response.VoucherBHResponse;
import com.example.gbsports.service.ZaloPayService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/banhang")
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
    private TheoDoiDonHangRepo theoDoiDonHangRepo;

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

    @GetMapping("/getAllHoaDonCTT")
    public List<HoaDonResponse> getAllHDCTT() {
        return hoaDonRepo.getAllHoaDonCTT();
    }

    @GetMapping("/getHoaDonByIdHoaDon")
    public HoaDon getHoaDonByIdHoaDon(@RequestParam("idHD") Integer idHD) {
        return hoaDonRepo.findById(idHD).get();
    }

    @GetMapping("/createHoaDon")
    public ResponseEntity<?> createHoaDon(@RequestParam(value = "idNhanVien") Integer idNhanVien) {
        try {
            // 1. Validate input
            if (idNhanVien == null || idNhanVien <= 0) {
                return ResponseEntity.badRequest().body("ID nhân viên không hợp lệ");
            }

            // 2. Find employee
            NhanVien nv = nhanVienRepo.findById(idNhanVien)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Không tìm thấy nhân viên với ID: " + idNhanVien
                    ));

            // 3. Create new invoice
            HoaDon newHoaDon = new HoaDon();
            newHoaDon.setMa_hoa_don(generateUniqueMaHoaDon());
            newHoaDon.setNgay_tao(LocalDateTime.now());
            newHoaDon.setTrang_thai("Chưa thanh toán");
            newHoaDon.setLoai_hoa_don("Offline");
            newHoaDon.setNhanVien(nv);
            newHoaDon.setHinh_thuc_thanh_toan("Tiền mặt");
            newHoaDon.setPhuong_thuc_nhan_hang("Nhận tại cửa hàng");

            // 4. Set default values
            newHoaDon.setTong_tien_truoc_giam(BigDecimal.ZERO);
            newHoaDon.setTong_tien_sau_giam(BigDecimal.ZERO);
            newHoaDon.setPhi_van_chuyen(BigDecimal.ZERO);

            // 5. Save to database
            HoaDon savedHoaDon = hoaDonRepo.save(newHoaDon);
            TheoDoiDonHang theoDoiDonHang = new TheoDoiDonHang();
            theoDoiDonHang.setTrang_thai("Chờ xác nhận");
            theoDoiDonHang.setHoaDon(savedHoaDon);
            theoDoiDonHang.setNgay_chuyen(LocalDateTime.now());
            theoDoiDonHangRepo.save(theoDoiDonHang);
            // 6. Create response DTO
            Map<String, Object> response = new HashMap<>();
            response.put("id_hoa_don", savedHoaDon.getId_hoa_don());
            response.put("ma_hoa_don", savedHoaDon.getMa_hoa_don());
            response.put("ngay_tao", savedHoaDon.getNgay_tao().format(DateTimeFormatter.ISO_DATE_TIME));
            response.put("trang_thai", savedHoaDon.getTrang_thai());
            response.put("id_nhan_vien", savedHoaDon.getNhanVien().getIdNhanVien());
            response.put("ten_nhan_vien", savedHoaDon.getNhanVien().getTenNhanVien());

            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(
                    Map.of(
                            "error", true,
                            "message", e.getReason()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "error", true,
                            "message", "Lỗi hệ thống: " + e.getMessage()
                    )
            );
        }
    }

    @DeleteMapping("/deleteHoaDon")
    @Transactional
    public ResponseEntity<?> deleteHoaDon(@RequestParam(value = "idHoaDon") Integer id) {
        try {
            Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(id);
            if (hoaDonOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Không tìm thấy hóa đơn với ID: " + id));
            }

            HoaDon hoaDon = hoaDonOpt.get();

            // Cập nhật lại số lượng tồn sản phẩm
            for (HoaDonChiTiet chiTiet : hoaDon.getDanhSachChiTiet()) {
                Integer idChiTietSanPham = chiTiet.getChiTietSanPham().getId_chi_tiet_san_pham();
                Integer soLuong = chiTiet.getSo_luong();
                chiTietSanPhamRepo.updateSLCTSPByIdCTSP(idChiTietSanPham, soLuong);
            }

            // Chỉ cần gọi delete, Hibernate sẽ tự cascade xóa danh sách chi tiết
            hoaDonRepo.delete(hoaDon);

            return ResponseEntity.ok(Map.of("success", true, "message", "Đã xóa hóa đơn và cập nhật số lượng tồn thành công"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Lỗi khi xóa hóa đơn: " + e.getMessage()));
        }
    }


    @PutMapping("/updateHoaDon")
    public ResponseEntity<String> updateHoaDon(
            @RequestParam("idHoaDon") Integer idHD,
            @RequestParam(value = "idKhachHang", required = false) Integer idKH,
            @RequestParam(value = "trangThai", required = false) String trangThai,
            @RequestParam(value = "idVoucher", required = false) Integer idVoucher,
            @RequestParam(value = "sdtNguoiNhan", required = false) String sdtNguoiNhan,
            @RequestParam(value = "diaChi", required = false) String diaChi,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "tongTienTruocGiam", required = false) BigDecimal tongTienTruocGiam,
            @RequestParam(value = "phiVanChuyen", required = false) BigDecimal phiVanChuyen,
            @RequestParam(value = "tongTienSauGiam", required = false) BigDecimal tongTienSauGiam,
            @RequestParam(value = "hinhThucThanhToan", required = false) String hinhThucThanhToan,
            @RequestParam(value = "phuongThucNhanHang", required = false) String phuongThucNhanHang,
            @RequestParam(value = "loaiHoaDon", required = false) String loaiHoaDon,
            @RequestParam(value = "ghiChu", required = false) String ghiChu
    ) {
        try {
            Optional<HoaDon> hoaDonOptional = hoaDonRepo.findById(idHD);
            if (!hoaDonOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy hóa đơn");
            }

            HoaDon hoaDon = hoaDonOptional.get();

            // Cập nhật từng trường nếu có giá trị
            if (idKH != null) {
                Optional<KhachHang> khachHang = khachHangRepo.findById(idKH);
                khachHang.ifPresent(hoaDon::setKhachHang);
            }

            if (trangThai != null) hoaDon.setTrang_thai(trangThai);
            if (sdtNguoiNhan != null) hoaDon.setSdt_nguoi_nhan(sdtNguoiNhan);
            if (diaChi != null) hoaDon.setDia_chi(diaChi);
            if (email != null) hoaDon.setEmail(email);
            if (tongTienTruocGiam != null) hoaDon.setTong_tien_truoc_giam(tongTienTruocGiam);
            if (phiVanChuyen != null) hoaDon.setPhi_van_chuyen(phiVanChuyen);
            if (tongTienSauGiam != null) hoaDon.setTong_tien_sau_giam(tongTienSauGiam);
            if (hinhThucThanhToan != null) hoaDon.setHinh_thuc_thanh_toan(hinhThucThanhToan);
            if (phuongThucNhanHang != null) hoaDon.setPhuong_thuc_nhan_hang(phuongThucNhanHang);
            if (loaiHoaDon != null) hoaDon.setLoai_hoa_don(loaiHoaDon);
            if (ghiChu != null) hoaDon.setGhi_chu(ghiChu);

            // Xử lý voucher
            if (idVoucher != null) {
                if (idVoucher.describeConstable().isEmpty()) {
                    hoaDon.setVoucher(null);
                } else {
                    Optional<Voucher> voucher = voucherRepository.findById(idVoucher);
                    voucher.ifPresent(hoaDon::setVoucher);
                }
            }

            hoaDonRepo.save(hoaDon);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật");
        }
    }

    @GetMapping("/getSPHD")
    public List<HoaDonChiTietResponse> getAllSPHD(@RequestParam(value = "idHoaDon") Integer idHD) {
        return hoaDonChiTietRepo.getSPGH(idHD);
    }

    @PostMapping("/themSPHDMoi")
    public ResponseEntity<?> themSPHDMoi(
            @RequestParam("idHoaDon") Integer idHD,
            @RequestParam("idCTSP") Integer idCTSP,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam("giaBan") Integer giaBan) {
        try {
            // Kiểm tra hóa đơn
            HoaDon hoaDon = hoaDonRepo.findById(idHD)
                    .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại!"));

            // Kiểm tra số lượng tồn kho
            ChiTietSanPham ctsp = chiTietSanPhamRepo.findById(idCTSP)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));
            if (ctsp.getSo_luong() < soLuong) {
                return ResponseEntity.badRequest()
                        .body("Số lượng tồn kho không đủ!");
            }

            // Thêm sản phẩm
            hoaDonChiTietRepo.addSPHD(idHD, idCTSP, soLuong, giaBan);

            // Cập nhật voucher (nếu có)
            capNhatVoucher(idHD);

            return ResponseEntity.ok("Thêm sản phẩm mới vào hóa đơn thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
    }

    @PostMapping("/addSPHD")
    public ResponseEntity<?> addSPHD(
            @RequestParam(value = "idHoaDon") Integer idHD,
            @RequestParam(value = "idCTSP") Integer idCTSP,
            @RequestParam(value = "soLuong") Integer soLuong,
            @RequestParam(value = "giaBan") Integer giaBan) {
        try {
            HoaDon hoaDon = hoaDonRepo.findById(idHD)
                    .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại!"));
            hoaDonChiTietRepo.addSPHD(idHD, idCTSP, soLuong, giaBan);

            capNhatVoucher(idHD);
            return ResponseEntity.ok("Thêm sản phẩm vào hóa đơn thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
    }

    @PostMapping("/giamSPHD")
    public ResponseEntity<?> giamSPHD(
            @RequestParam(value = "idHoaDon") Integer idHD,
            @RequestParam(value = "idCTSP") Integer idCTSP,
            @RequestParam(value = "soLuong") Integer soLuong,
            @RequestParam(value = "giaBan") Integer giaBan) {
        try {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepo.findById(idCTSP)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

            if (chiTietSanPham.getSo_luong() < soLuong) {
                return ResponseEntity.badRequest()
                        .body("Số lượng không đủ để giảm sản phẩm khỏi hóa đơn!");
            }

            hoaDonChiTietRepo.giamSPHD(idHD, idCTSP, soLuong, giaBan);

            HoaDon hoaDon = hoaDonRepo.findById(idHD)
                    .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại!"));

            capNhatVoucher(idHD);

            return ResponseEntity.ok("Giảm sản phẩm trong hóa đơn thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi giảm sản phẩm: " + e.getMessage());
        }
    }


    @DeleteMapping("/xoaSPHD")
    public ResponseEntity<?> xoaSanPhamKhoiHoaDon(
            @RequestParam("idHoaDon") Integer idHoaDon,
            @RequestParam("idChiTietSanPham") Integer idChiTietSanPham) {
        try {
            HoaDon hoaDon = hoaDonRepo.findById(idHoaDon)
                    .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại!"));

            if ("Đã thanh toán".equalsIgnoreCase(hoaDon.getTrang_thai())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Không thể xóa sản phẩm từ hóa đơn đã thanh toán!"));
            }

            hoaDonChiTietRepo.xoaSPKhoiHD(idHoaDon, idChiTietSanPham);

            capNhatVoucher(idHoaDon);

            hoaDon = hoaDonRepo.findById(idHoaDon).orElseThrow(); // Cập nhật lại hóa đơn sau thay đổi
            return ResponseEntity.ok(Map.of("success", true, "data", hoaDon));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Lỗi khi xóa sản phẩm: " + e.getMessage()));
        }
    }

    private void capNhatVoucher(Integer idHD) {
        List<VoucherBHResponse> voucherBHResponse = voucherRepository.giaTriGiamThucTeByIDHD(idHD);
        if (!voucherBHResponse.isEmpty()) {
            Voucher voucher = voucherRepository.findById(voucherBHResponse.get(0).getId_voucher())
                    .orElseThrow(() -> new RuntimeException("Voucher không tồn tại!"));
            HoaDon hoaDon = hoaDonRepo.findById(idHD).get();
            hoaDon.setTong_tien_sau_giam(hoaDon.getTong_tien_truoc_giam().subtract(voucherBHResponse.get(0).getGia_tri_giam_thuc_te()));
            hoaDon.setVoucher(voucher);
            hoaDonRepo.save(hoaDon);
        } else {
            HoaDon hoaDon = hoaDonRepo.findById(idHD).get();
            hoaDon.setVoucher(null);
            hoaDonRepo.save(hoaDon);
        }
    }


    @GetMapping("/trangThaiDonHang")
    public ResponseEntity<?> chuyenTrangThaiHoaDon(@RequestParam("idHoaDon") Integer idHD) {
        HoaDon hoaDon = hoaDonRepo.getReferenceById(idHD);
        hoaDon.setTrang_thai("Đã thanh toán");
        hoaDonRepo.insertTrangThaiDonHang(hoaDon.getMa_hoa_don(), "Hoàn thành", LocalDateTime.now(), null, null);
        hoaDonRepo.save(hoaDon);
        System.out.println(hoaDon);
        return ResponseEntity.ok("Thanh toán hóa đơn thành công");
    }

    @GetMapping("/phuongThucNhanHang")
    public ResponseEntity<?> phuongThucNhanHang(
            @RequestParam("idHoaDon") Integer idHD,
            @RequestParam("phuongThucNhanHang") String phuongThuc
    ) {
        Optional<HoaDon> hoaDon = hoaDonRepo.findById(idHD);
        HoaDon hd = hoaDon.get();
        if (phuongThuc.equalsIgnoreCase("Giao hàng")) {
            hd.setPhuong_thuc_nhan_hang("Giao hàng");
            hd.setPhi_van_chuyen(BigDecimal.valueOf(30000));
            hoaDonRepo.save(hd);
            return ResponseEntity.ok("oke");
        } else {
            hd.setPhuong_thuc_nhan_hang("Nhận tại cửa hàng");
            hd.setPhi_van_chuyen(BigDecimal.ZERO);
            return ResponseEntity.ok("oke");
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
