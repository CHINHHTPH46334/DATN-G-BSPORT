package com.example.gbsports.controller;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.request.TraHangRequest;
import com.example.gbsports.response.*;
import com.example.gbsports.service.TraHangService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/qlhd")
public class HoaDonController {

    @Autowired
    private HoaDonRepo hoaDonRepo;
    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepo;
    @Autowired
    private ChiTietSanPhamRepo chiTietSanPhamRepo;
    @Autowired
    private VoucherRepository voucherRepo;
    @Autowired
    private TheoDoiDonHangRepo theoDoiDonHangRepo;
    @Autowired
    private TraHangService traHangService;
    @Autowired
    private ChiTietTraHangRepository chiTietTraHangRepository;

    @PostMapping("/update-status")
    public ResponseEntity<Map<String, Object>> updateInvoiceStatus(
            @RequestBody Map<String, Object> request) {
        Integer idHoaDon = (Integer) request.get("idHoaDon");
        String status = (String) request.get("status");
        System.out.println(status + "hhhhhhhhhhhhhhhhhhhhhhhhhh");
        Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(idHoaDon);
        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            hoaDon.setTrang_thai("Đã thanh toán");
            hoaDonRepo.save(hoaDon);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái hóa đơn thành công!");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không tìm thấy hóa đơn!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/updateHTTTHD")
    public ResponseEntity<HoaDon> updateHinhThucTTHoaDon(@RequestParam("idHD") Integer id,
                                                         @RequestParam("hinhThucThanhToan") String httt) {
        HoaDon hoaDon = hoaDonRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        hoaDon.setHinh_thuc_thanh_toan(httt);
        return ResponseEntity.ok(hoaDonRepo.save(hoaDon));
    }

    @GetMapping("/all-hoa-don")
    public List<HoaDonResponse> getListHD() {
        return hoaDonRepo.getListHD();
    }

    @GetMapping("/danh_sach_hoa_don")
    public Page<HoaDonResponse> getAllHD(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size) {
        if (page < 0) {
            throw new IllegalArgumentException("Số trang không hợp lệ");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Kích thước trang không hợp lệ");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<HoaDonResponse> list = hoaDonRepo.getAllHD(pageable);
        return list;
    }

    @GetMapping("/tim_kiem")
    public Page<HoaDonResponse> search(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page hoặc size không hợp lệ");
        }
        Pageable pageable = PageRequest.of(page, size);
        String searchKeyword = null;
        if (keyword != null) {
            String trimmedKeyword = keyword.trim();
            if (trimmedKeyword.isEmpty()) {
                throw new IllegalArgumentException("Từ khóa tìm kiếm không được để trống");
            }
            searchKeyword = "%" + trimmedKeyword.replaceAll("[^a-zA-Z0-9\\s]", "") + "%";
        }
        return hoaDonRepo.timHoaDon(searchKeyword, pageable);
    }

    @GetMapping("/loc_ngay")
    public Page<HoaDonResponse> filterHoaDonByDate(
            @RequestParam(value = "tuNgay", required = false) String tuNgayStr,
            @RequestParam(value = "denNgay", required = false) String denNgayStr,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page hoặc size không hợp lệ");
        }
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime tuNgay = null;
        LocalDateTime denNgay = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (tuNgayStr != null && !tuNgayStr.isEmpty()) {
            tuNgay = LocalDateTime.parse(tuNgayStr, formatter);
        }
        if (denNgayStr != null && !denNgayStr.isEmpty()) {
            denNgay = LocalDateTime.parse(denNgayStr, formatter);
        }
        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc");
        }
        return hoaDonRepo.findHoaDonByNgay(tuNgay, denNgay, pageable);
    }

    @GetMapping("/loc_trang_thai_don_hang")
    public Page<HoaDonResponse> filterHoaDonByTrangThai(
            @RequestParam(value = "trangThai", required = false) String trangThai,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page hoặc size không hợp lệ");
        }
        Pageable pageable = PageRequest.of(page, size);
        return (trangThai == null || trangThai.trim().isEmpty())
                ? hoaDonRepo.getAllHD(pageable)
                : hoaDonRepo.findHoaDonByTrangThaiGiaoHang(trangThai, pageable);
    }

    @GetMapping("/hdct")
    public Map<String, Object> getHDCTBymaHD(@RequestParam("id") String maHoaDon) {
        HoaDonResponse hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon));
        Integer idHoaDon = hoaDon.getId_hoa_don();
        List<HoaDonChiTietResponse> chiTietHoaDons = hoaDonChiTietRepo.findHoaDonChiTietById(idHoaDon);
        List<TheoDoiDonHangResponse> trangThaiHistory = hoaDonRepo.findTrangThaiHistoryByIdHoaDon(idHoaDon);
        Map<String, Object> response = new HashMap<>();
        response.put("hoaDon", hoaDon);
        response.put("chiTietHoaDons", chiTietHoaDons);
        response.put("trangThaiHistory", trangThaiHistory);
        return response;
    }


    @PostMapping("/chuyen_trang_thai")
    @Transactional
    public String updateTrangThai(
            @RequestParam("maHoaDon") String maHoaDon,
            @RequestParam("newTrangThai") String newTrangThai,
            @RequestParam(value = "nhanVienDoi", required = false) String nhanVienDoi,
            @RequestParam(value = "noiDungDoi", required = false) String noiDungDoi) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty() ||
                newTrangThai == null || newTrangThai.trim().isEmpty()) {
            throw new IllegalArgumentException("Thông tin không hợp lệ");
        }
        Optional<HoaDonResponse> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);
        if (!hoaDonOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon);
        }
        Integer idHoaDon = hoaDonOpt.get().getId_hoa_don();
        LocalDateTime ngayChuyen = LocalDateTime.now();

        if ("Đã xác nhận".equals(newTrangThai)) {
            List<HoaDonChiTietResponse> chiTietHoaDons = hoaDonChiTietRepo.findHoaDonChiTietById(idHoaDon);
            for (HoaDonChiTietResponse chiTiet : chiTietHoaDons) {
                Integer idCTSP = chiTiet.getId_chi_tiet_san_pham();
                Integer soLuong = chiTiet.getSo_luong();

                Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepo.findById(idCTSP);
                if (chiTietSanPhamOpt.isPresent()) {
                    ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();
                    if (chiTietSanPham.getSo_luong() < soLuong) {
                        throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm: " + chiTiet.getTen_san_pham());
                    }
                    chiTietSanPham.setSo_luong(chiTietSanPham.getSo_luong() - soLuong);
                    chiTietSanPhamRepo.save(chiTietSanPham);
                    Optional<HoaDon> optHD = hoaDonRepo.findById(idHoaDon);
                    HoaDon hoaDon = optHD.get();
                    hoaDon.setNgay_sua(LocalDateTime.now());
                    hoaDonRepo.save(hoaDon);
                } else {
                    throw new RuntimeException("Không tìm thấy sản phẩm chi tiết với ID: " + idCTSP);
                }
            }
        }
        hoaDonRepo.insertTrangThaiDonHang(maHoaDon, newTrangThai, ngayChuyen, nhanVienDoi, noiDungDoi);
        return "Cập nhật trạng thái thành công: " + newTrangThai;
    }

    @PostMapping("/quay_lai_trang_thai")
    @Transactional
    public ResponseEntity<Map<String, Object>> revertToInitialStatus(
            @RequestParam("maHoaDon") String maHoaDon,
            @RequestParam(value = "nhanVienDoi", required = false) String nhanVienDoi,
            @RequestParam(value = "noiDungDoi", required = false) String noiDungDoi) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Mã hóa đơn không hợp lệ!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<HoaDonResponse> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);
        if (!hoaDonOpt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không tìm thấy hóa đơn với mã: " + maHoaDon);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Integer idHoaDon = hoaDonOpt.get().getId_hoa_don();
        List<TheoDoiDonHangResponse> trangThaiHistory = hoaDonRepo.findTrangThaiHistoryByIdHoaDon(idHoaDon);

        if (trangThaiHistory.isEmpty() || !trangThaiHistory.get(0).getTrang_thai().equals("Chờ xác nhận")) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không thể quay lại vì trạng thái ban đầu không phải 'Chờ xác nhận'!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String trangThaiHienTai = trangThaiHistory.get(trangThaiHistory.size() - 1).getTrang_thai();
        if ("Đã xác nhận".equals(trangThaiHienTai)) {
            List<HoaDonChiTietResponse> chiTietHoaDons = hoaDonChiTietRepo.findHoaDonChiTietById(idHoaDon);
            for (HoaDonChiTietResponse chiTiet : chiTietHoaDons) {
                Integer idCTSP = chiTiet.getId_chi_tiet_san_pham();
                Integer soLuong = chiTiet.getSo_luong();
                Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepo.findById(idCTSP);
                if (chiTietSanPhamOpt.isPresent()) {
                    ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();
                    chiTietSanPham.setSo_luong(chiTietSanPham.getSo_luong() + soLuong);
                    chiTietSanPhamRepo.save(chiTietSanPham);
                } else {
                    throw new RuntimeException("Không tìm thấy sản phẩm chi tiết với ID: " + idCTSP);
                }
            }
        }
        Optional<HoaDon> optHD = hoaDonRepo.findById(idHoaDon);
        HoaDon hoaDon = optHD.get();
        hoaDon.setNgay_sua(LocalDateTime.now());
        hoaDonRepo.save(hoaDon);

        LocalDateTime ngayChuyen = LocalDateTime.now();
        hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Chờ xác nhận", ngayChuyen, nhanVienDoi, noiDungDoi);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã quay lại trạng thái 'Chờ xác nhận'!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel_order")
    @Transactional
    public String cancelOrder(@RequestParam("maHoaDon") String maHoaDon,
                              @RequestParam(value = "nhanVienDoi", required = false) String nhanVienDoi,
                              @RequestParam(value = "noiDungDoi", required = false) String noiDungDoi) {
        Optional<HoaDonResponse> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);
        if (!hoaDonOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon);
        }

        Integer idHoaDon = hoaDonOpt.get().getId_hoa_don();
        LocalDateTime ngayChuyen = LocalDateTime.now();

        String trangThaiGanNhat = hoaDonRepo.findLatestNonUpdatedStatusByIdHoaDon(idHoaDon);
        if (trangThaiGanNhat == null) {
            throw new RuntimeException("Không tìm thấy trạng thái phù hợp cho hóa đơn với mã: " + maHoaDon);
        }

        if ("Chờ xác nhận".equals(trangThaiGanNhat)) {
            Optional<HoaDon> hoaDonEntityOpt = hoaDonRepo.findById(idHoaDon);
            if (hoaDonEntityOpt.isPresent()) {
                HoaDon hoaDon = hoaDonEntityOpt.get();
                Integer idVoucher = hoaDon.getVoucher() != null ? hoaDon.getVoucher().getId() : null;
                if (idVoucher != null) {
                    Optional<Voucher> voucherOpt = voucherRepo.findById(idVoucher);
                    if (voucherOpt.isPresent()) {
                        Voucher voucher = voucherOpt.get();
                        voucher.setSoLuong(voucher.getSoLuong() + 1);
                        voucherRepo.save(voucher);
                    } else {
                        throw new RuntimeException("Không tìm thấy voucher với ID: " + idVoucher);
                    }
                }
            }
        } else if ("Đã xác nhận".equals(trangThaiGanNhat) || "Chờ đóng gói".equals(trangThaiGanNhat)) {
            Optional<HoaDon> hoaDonEntityOpt = hoaDonRepo.findById(idHoaDon);
            if (hoaDonEntityOpt.isPresent()) {
                HoaDon hoaDon = hoaDonEntityOpt.get();
                Integer idVoucher = hoaDon.getVoucher() != null ? hoaDon.getVoucher().getId() : null;
                if (idVoucher != null) {
                    Optional<Voucher> voucherOpt = voucherRepo.findById(idVoucher);
                    if (voucherOpt.isPresent()) {
                        Voucher voucher = voucherOpt.get();
                        voucher.setSoLuong(voucher.getSoLuong() + 1);
                        voucherRepo.save(voucher);
                    } else {
                        throw new RuntimeException("Không tìm thấy voucher với ID: " + idVoucher);
                    }
                }
            }

            List<HoaDonChiTietResponse> chiTietHoaDons = hoaDonChiTietRepo.findHoaDonChiTietById(idHoaDon);
            for (HoaDonChiTietResponse chiTiet : chiTietHoaDons) {
                Integer idCTSP = chiTiet.getId_chi_tiet_san_pham();
                Integer soLuong = chiTiet.getSo_luong();
                Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepo.findById(idCTSP);
                if (chiTietSanPhamOpt.isPresent()) {
                    ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();
                    chiTietSanPham.setSo_luong(chiTietSanPham.getSo_luong() + soLuong);
                    chiTietSanPhamRepo.save(chiTietSanPham);
                } else {
                    throw new RuntimeException("Không tìm thấy sản phẩm chi tiết với ID: " + idCTSP);
                }
            }
        } else {
            throw new RuntimeException("Không thể hủy đơn hàng ở trạng thái: " + trangThaiGanNhat);
        }
        hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã hủy", ngayChuyen, nhanVienDoi, noiDungDoi);
        return "Đơn hàng đã được hủy";
    }

    @PostMapping("/update_ttkh")
    public ResponseEntity<Map<String, Object>> updateCustomerInfo(
            @RequestBody Map<String, Object> request) {
        String maHoaDon = (String) request.get("maHoaDon");
        String hoTen = (String) request.get("hoTen");
        String email = (String) request.get("email");
        String sdtNguoiNhan = (String) request.get("sdtNguoiNhan");
        String diaChi = (String) request.get("diaChi");

        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Mã hóa đơn không hợp lệ!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(hoaDonRepo.findByMaHoaDon(maHoaDon)
                .map(HoaDonResponse::getId_hoa_don)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon)));

        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            if (hoTen != null && !hoTen.trim().isEmpty()) {
                hoaDon.setHo_ten(hoTen);
            }
            if (email != null && !email.trim().isEmpty()) {
                hoaDon.setEmail(email);
            }
            if (sdtNguoiNhan != null && !sdtNguoiNhan.trim().isEmpty()) {
                hoaDon.setSdt_nguoi_nhan(sdtNguoiNhan);
            }
            if (diaChi != null && !diaChi.trim().isEmpty()) {
                hoaDon.setDia_chi(diaChi);
            }
            hoaDon.setNgay_sua(LocalDateTime.now());
            hoaDonRepo.save(hoaDon);

            LocalDateTime ngayChuyen = LocalDateTime.now();
            String nhanVienDoi = (String) request.get("nhanVienDoi");
            String noiDungDoi = "Update thông tin khách hàng";
            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen, nhanVienDoi, noiDungDoi);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật thông tin khách hàng thành công!");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không tìm thấy hóa đơn!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/update_note")
    public ResponseEntity<Map<String, Object>> updateNote(
            @RequestBody Map<String, Object> request) {
        String maHoaDon = (String) request.get("maHoaDon");
        String ghiChu = (String) request.get("ghiChu");

        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Mã hóa đơn không hợp lệ!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(hoaDonRepo.findByMaHoaDon(maHoaDon)
                .map(HoaDonResponse::getId_hoa_don)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon)));

        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            hoaDon.setGhi_chu(ghiChu != null ? ghiChu : "");
            hoaDon.setNgay_sua(LocalDateTime.now());
            hoaDonRepo.save(hoaDon);

            LocalDateTime ngayChuyen = LocalDateTime.now();
            String nhanVienDoi = (String) request.get("nhanVienDoi");
            String noiDungDoi = "Update ghi chú";
            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen, nhanVienDoi, noiDungDoi);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật ghi chú thành công!");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không tìm thấy hóa đơn!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/ctsp_hd")
    public Page<ChiTietSanPhamView> getAllCTSP_HD(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "keyword", required = false) String keyword) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page hoặc size không hợp lệ");
        }
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim().replaceAll("[^\\p{L}\\p{N}\\s]", "");
            return chiTietSanPhamRepo.searchCTSP_HD(searchKeyword, pageable);
        }
        return chiTietSanPhamRepo.getAllCTSP_HD(pageable);
    }

    @PostMapping("/addSP_HD")
    @Transactional
    public ResponseEntity<Map<String, Object>> addProductsToInvoice(
            @RequestBody Map<String, Object> request) {
        String maHoaDon = (String) request.get("maHoaDon");
        List<Map<String, Object>> products = (List<Map<String, Object>>) request.get("products");

        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Mã hóa đơn không hợp lệ!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (products == null || products.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Danh sách sản phẩm không được để trống!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<HoaDonResponse> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);
        if (!hoaDonOpt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không tìm thấy hóa đơn với mã: " + maHoaDon);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Integer idHoaDon = hoaDonOpt.get().getId_hoa_don();
        try {
            for (Map<String, Object> product : products) {
                Integer idCTSP = (Integer) product.get("idCTSP");
                Integer soLuongMua = (Integer) product.get("soLuongMua");
                Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepo.findById(idCTSP);
                if (!chiTietSanPhamOpt.isPresent()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Không tìm thấy sản phẩm với ID: " + idCTSP);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
                hoaDonChiTietRepo.addSLGH_HD(idCTSP, idHoaDon, soLuongMua);
            }

            LocalDateTime ngayChuyen = LocalDateTime.now();
            String nhanVienDoi = (String) request.get("nhanVienDoi");
            String noiDungDoi = "Thêm sản phẩm vào hóa đơn";
            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen, nhanVienDoi, noiDungDoi);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thêm sản phẩm vào hóa đơn thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/removeSP_HD")
    @Transactional
    public ResponseEntity<?> removeProductFromInvoice(
            @RequestParam("maHoaDon") String maHoaDon,
            @RequestParam("idCTSP") Integer idCTSP,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam(value = "nhanVienDoi", required = false) String nhanVienDoi,
            @RequestParam(value = "noiDungDoi", required = false) String noiDungDoi) {
        try {
            Optional<HoaDonResponse> hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon);
            if (!hoaDon.isPresent()) {
                return ResponseEntity.badRequest().body("Hóa đơn không tồn tại");
            }
            Integer idHoaDon = hoaDon.get().getId_hoa_don();
            hoaDonChiTietRepo.removeSPGHinHDCT(idCTSP, idHoaDon, soLuong);

            LocalDateTime ngayChuyen = LocalDateTime.now();
            String noiDungDoiDefault = noiDungDoi != null ? noiDungDoi : "Xóa sản phẩm khỏi hóa đơn";
            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen, nhanVienDoi, noiDungDoiDefault);

            return ResponseEntity.ok("Xóa sản phẩm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
    }

    @PostMapping("/update_soLuong")
    @Transactional
    public ResponseEntity<?> updateProductQuantity(
            @RequestParam("maHoaDon") String maHoaDon,
            @RequestParam("idCTSP") Integer idCTSP,
            @RequestParam("quantityChange") Integer quantityChange,
            @RequestParam(value = "nhanVienDoi", required = false) String nhanVienDoi,
            @RequestParam(value = "noiDungDoi", required = false) String noiDungDoi) {
        try {
            Optional<HoaDonResponse> hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon);
            if (!hoaDon.isPresent()) {
                return ResponseEntity.badRequest().body("Hóa đơn không tồn tại");
            }
            Integer idHoaDon = hoaDon.get().getId_hoa_don();
            hoaDonChiTietRepo.updateQuantity(idCTSP, idHoaDon, quantityChange);

            LocalDateTime ngayChuyen = LocalDateTime.now();
            String noiDungDoiDefault = noiDungDoi != null ? noiDungDoi : "Update số lượng sản phẩm";
            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen, nhanVienDoi, noiDungDoiDefault);

            return ResponseEntity.ok("Cập nhật số lượng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật số lượng: " + e.getMessage());
        }
    }

    // Endpoint mới cho trả hàng
    @GetMapping("/{maHoaDon}/chi-tiet-tra-hang")
    public ResponseEntity<Map<String, Object>> layChiTietHoaDonTraHang(@PathVariable String maHoaDon) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Fetch invoice details
            HoaDonResponse hoaDon = hoaDonRepo.getHoaDonWithReturnInfoByMaHoaDon(maHoaDon);
            if (hoaDon == null) {
                response.put("thanh_cong", false);
                response.put("thong_bao", "Không tìm thấy hóa đơn với mã: " + maHoaDon);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Validate order status in TheoDoiDonHang
            List<TheoDoiDonHang> statusList = theoDoiDonHangRepo.findByIdHoaDonOrderByNgayChuyenDesc(hoaDon.getId_hoa_don());
            // Check if the invoice has already been returned
            boolean hasReturn = statusList.stream().anyMatch(status -> "Trả hàng".equals(status.getTrang_thai()));
            if (hasReturn) {
                response.put("thanh_cong", false);
                response.put("thong_bao", "Mỗi hóa đơn chỉ được phép trả hàng một lần duy nhất!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (statusList.isEmpty() || !"Hoàn thành".equals(statusList.get(0).getTrang_thai())) {
                response.put("thanh_cong", false);
                response.put("thong_bao", "Hóa đơn không ở trạng thái Hoàn thành!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            LocalDateTime ngayChuyen = statusList.get(0).getNgay_chuyen();
            LocalDateTime now = LocalDateTime.now();
            long daysBetween = ChronoUnit.DAYS.between(ngayChuyen.toLocalDate(), now.toLocalDate());
            if (daysBetween > 7) {
                response.put("thanh_cong", false);
                response.put("thong_bao", "Hóa đơn đã quá 7 ngày kể từ khi hoàn thành, không thể trả hàng!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
//             Fetch invoice details
            List<HoaDonChiTietResponse> chiTietHoaDons = hoaDonRepo.getChiTietHoaDonByMaHoaDon(maHoaDon);
            if (chiTietHoaDons == null || chiTietHoaDons.isEmpty()) {
                response.put("thanh_cong", false);
                response.put("thong_bao", "Không tìm thấy chi tiết hóa đơn!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Fetch customer information
            HoaDonChiTietResponse thongTinKhachHang = hoaDonRepo.getKhachHangInfoByMaHoaDon(maHoaDon);
            if (thongTinKhachHang == null) {
                response.put("thanh_cong", false);
                response.put("thong_bao", "Không tìm thấy thông tin khách hàng!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Build successful response
            response.put("thanh_cong", true);
            response.put("ma_hoa_don", hoaDon.getMa_hoa_don());
            response.put("trang_thai", hoaDon.getTrang_thai());
            response.put("hinh_thuc_thanh_toan", hoaDon.getHinh_thuc_thanh_toan());
            response.put("ngay_tao", hoaDon.getNgay_tao());
            response.put("ho_ten", hoaDon.getHo_ten());
            response.put("dia_chi", hoaDon.getDia_chi());
            response.put("gia_tri_giam", hoaDon.getGia_tri_giam() != null ? hoaDon.getGia_tri_giam() : BigDecimal.ZERO);
            response.put("tong_tien", hoaDon.getTong_tien_truoc_giam() != null ? hoaDon.getTong_tien_truoc_giam() : BigDecimal.ZERO);
            response.put("tong_tien_thanh_toan", hoaDon.getTong_tien_sau_giam() != null ? hoaDon.getTong_tien_sau_giam() : BigDecimal.ZERO);
            response.put("trang_thai_don_hang", hoaDon.getTrang_thai_thanh_toan());
            response.put("trang_thai_tra_hang", hoaDon.getTrang_thai_tra_hang() != null ? hoaDon.getTrang_thai_tra_hang() : "Chưa yêu cầu");
            response.put("chi_tiet_hoa_don", chiTietHoaDons);
            response.put("thong_tin_khach_hang", thongTinKhachHang);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("thanh_cong", false);
            response.put("thong_bao", "Lỗi khi lấy thông tin chi tiết hóa đơn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/tra-hang")
    public ResponseEntity<TraHangResponse> taoYeuCauTraHang(@RequestBody TraHangRequest request) {
        try {
            TraHangResponse response = traHangService.taoYeuCauTraHang(request);
            if (response.isThanh_cong()) {
                return ResponseEntity.ok(response);

            } else {
                HttpStatus status;
                String thongBao = response.getThong_bao();
                if (thongBao != null && thongBao.contains("Không tìm thấy")) {
                    status = HttpStatus.NOT_FOUND;
                } else if (thongBao != null && thongBao.contains("đã có yêu cầu trả hàng")) {
                    status = HttpStatus.CONFLICT;
                } else {
                    status = HttpStatus.BAD_REQUEST;
                }
                return ResponseEntity.status(status).body(response);
            }
        } catch (Exception e) {
            TraHangResponse response = new TraHangResponse();
            response.setThanh_cong(false);
            response.setThong_bao("Lỗi khi tạo yêu cầu trả hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/ctth")
    public Map<String, Object> getReturnsByMaHD(@RequestParam("id") String maHoaDon) {
        HoaDonResponse hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon));
        Integer idHoaDon = hoaDon.getId_hoa_don();

        // Fetch ChiTietTraHang with product details
        List<ChiTietTraHangDTO> chiTietTraHangs = chiTietTraHangRepository.findChiTietTraHangByIdHoaDon(idHoaDon);

        // Fetch TraHang and map to TraHangDTO
        List<TraHang> traHangsEntities = chiTietTraHangRepository.findTraHangByIdHoaDon(idHoaDon);
        List<TraHangDTO> traHangs = traHangsEntities.stream().map(traHang -> {
            TraHangDTO dto = new TraHangDTO();
            dto.setId(traHang.getId());
            dto.setId_hoa_don(traHang.getId_hoa_don());
            dto.setLy_do(traHang.getLy_do());
            dto.setGhi_chu(traHang.getGhi_chu());
            dto.setNhan_vien_xu_ly(traHang.getNhan_vien_xu_ly());
            dto.setNgay_tao(traHang.getNgay_tao());
            dto.setTrang_thai(traHang.getTrang_thai());
            dto.setTong_tien_hoan(traHang.getTong_tien_hoan());
            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("chiTietTraHangs", chiTietTraHangs);
        response.put("traHangs", traHangs);
        return response;
    }
}

