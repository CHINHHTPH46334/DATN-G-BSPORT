package com.example.gbsports.controller;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.service.HoaDonService;
import com.example.gbsports.request.TraHangRequest;
import com.example.gbsports.response.*;
import com.example.gbsports.service.TraHangService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
        RequestMethod.PUT, RequestMethod.DELETE })
@RequestMapping("/admin/qlhd")

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
    private DiaChiKhachHangRepo diaChiKhachHangRepo;
    @Autowired
    private HoaDonService hoaDonService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TraHangService traHangService;
    @Autowired
    private ChiTietTraHangRepository chiTietTraHangRepository;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @PutMapping("/updateHTTTHD")
    public ResponseEntity<HoaDon> updateHinhThucTTHoaDon(@RequestParam("idHD") Integer id,
                                                         @RequestParam("hinhThucThanhToan") String httt) {
        HoaDon hoaDon = hoaDonRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        hoaDon.setHinh_thuc_thanh_toan(httt);
        return ResponseEntity.ok(hoaDonRepo.save(hoaDon));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @GetMapping("/all-hoa-don")
    public List<HoaDonResponse> getListHD() {
        return hoaDonRepo.getListHD();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @GetMapping("/tim_kiem")
    public Page<HoaDonResponse> search(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        String searchKeyword = null;
        String trimmedKeyword = keyword.trim();
        searchKeyword = "%" + trimmedKeyword.replaceAll("[^a-zA-Z0-9\\s]", "") + "%";
        return hoaDonRepo.timHoaDon(searchKeyword, pageable);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @GetMapping("/hdct")
    public Map<String, Object> getHDCTBymaHD(@RequestParam("id") String maHoaDon) {
        HoaDonResponse hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon));
        Integer idHoaDon = hoaDon.getId_hoa_don();
        List<HoaDonChiTietResponse> chiTietHoaDons = hoaDonChiTietRepo.findHoaDonChiTietById(idHoaDon);
        List<TheoDoiDonHangResponse> trangThaiHistory = hoaDonRepo.findTrangThaiHistoryByIdHoaDon(idHoaDon);
        List<DiaChiKhachHang> listDC = diaChiKhachHangRepo.findByKhachHangId(hoaDon.getId_khach_hang());
        Map<String, Object> response = new HashMap<>();
        response.put("hoaDon", hoaDon);
        response.put("chiTietHoaDons", chiTietHoaDons);
        response.put("trangThaiHistory", trangThaiHistory);
        response.put("listDC", listDC);
        return response;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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
                        throw new RuntimeException(
                                "Số lượng tồn kho không đủ cho sản phẩm: " + chiTiet.getTen_san_pham());
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
        // Xử lý khi trạng thái là "Hoàn thành"
        if ("Hoàn thành".equals(newTrangThai)) {
            Optional<HoaDon> optHD = hoaDonRepo.findById(idHoaDon);
            if (optHD.isPresent()) {
                HoaDon hoaDon = optHD.get();
                hoaDon.setPhu_thu(BigDecimal.valueOf(0)); // Reset so_tien_thanh_toan_them về 0
                hoaDon.setNgay_sua(LocalDateTime.now());
                hoaDonRepo.save(hoaDon);
            } else {
                throw new RuntimeException("Không tìm thấy hóa đơn với ID: " + idHoaDon);
            }
        }
        // Cập nhật trạng thái đơn hàng với nhan_vien_doi và noi_dung_doi
        hoaDonRepo.insertTrangThaiDonHang(maHoaDon, newTrangThai, ngayChuyen, nhanVienDoi, noiDungDoi);
        return "Cập nhật trạng thái thành công: " + newTrangThai;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @PostMapping("/cancel_order")
    @Transactional
    public String cancelOrder(@RequestParam("maHoaDon") String maHoaDon,
                              @RequestParam(value = "nhanVienDoi", required = false) String nhanVienDoi, // Thêm tham số
                              @RequestParam(value = "noiDungDoi", required = false) String noiDungDoi) { // Thêm tham số
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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
            String noiDungDoiDefault = noiDungDoi != null ? noiDungDoi : "Xóa sản phẩm khỏi hóa đơn"; // Giá trị mặc
            // định
            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen, nhanVienDoi, noiDungDoiDefault);

            return ResponseEntity.ok("Xóa sản phẩm thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa sản phẩm: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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

    // lềnh thay đổi
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_KH')")
    @GetMapping("/khach-hang/{idKhachHang}")
    public ResponseEntity<?> getDonHangByKhachHang(@PathVariable Integer idKhachHang) {
        try {
            List<HoaDonResponse> hoaDons = hoaDonService.getHoaDonByKhachHangId(idKhachHang);
            System.out.println("✅ Số đơn hàng tìm thấy cho idKhachHang " + idKhachHang + ": " + hoaDons.size());
            // Gỡ lỗi: In giá trị ghi_chu của mỗi hóa đơn
            hoaDons.forEach(hd -> System.out.println("Ghi_chu của hóa đơn " + hd.getMa_hoa_don() + ": " + hd.getGhi_chu()));
            return ResponseEntity.ok(hoaDons);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy đơn hàng cho idKhachHang " + idKhachHang + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_KH')")
    @GetMapping("/count/{idKhachHang}")
    public ResponseEntity<Integer> countHoaDonByKhachHang(@PathVariable Integer idKhachHang) {
        int count = hoaDonService.countHoaDonByKhachHangId(idKhachHang);
        return ResponseEntity.ok(count);
    }
    /**
     * API thay thế cho getHDCTBymaHD khi có lỗi với gia_sau_giam
     * Sử dụng truy vấn SQL đơn giản hơn không tham chiếu đến cột gia_sau_giam
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_KH')")
    @GetMapping("/get-products/{maHoaDon}")
    public List<Map<String, Object>> getOrderProducts(@PathVariable String maHoaDon) {
        try {
            // Lấy ID hóa đơn từ mã hóa đơn
            HoaDonResponse hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon));
            Integer idHoaDon = hoaDon.getId_hoa_don();

            // Sử dụng đúng truy vấn SQL như yêu cầu
            String sql = "select s.hinh_anh, ma_san_pham, ten_san_pham, c.so_luong, don_gia, c.so_luong * don_gia as 'Tong_tien' " +
                    "from hoa_don h " +
                    "join hoa_don_chi_tiet c on h.id_hoa_don = c.id_hoa_don " +
                    "join chi_tiet_san_pham t on c.id_chi_tiet_san_pham = t.id_chi_tiet_san_pham " +
                    "join san_pham s on s.id_san_pham = t.id_san_pham " +
                    "where h.id_hoa_don = ?";

            return jdbcTemplate.queryForList(sql, idHoaDon);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy thông tin sản phẩm trong đơn hàng: " + e.getMessage());
        }
    }

    /**
     * API dự phòng cuối cùng chỉ trả về thông tin rất cơ bản của đơn hàng
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_KH')")
    @GetMapping("/basic-order-detail")
    public Map<String, Object> getBasicOrderDetail(@RequestParam("ma_hoa_don") String maHoaDon) {
        try {
            // Lấy thông tin cơ bản của hóa đơn
            HoaDonResponse hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon));
            Integer idHoaDon = hoaDon.getId_hoa_don();

            // Truy vấn SQL đơn giản nhất để lấy thông tin sản phẩm
            String sql = "SELECT sp.ten_san_pham, hdct.so_luong, hdct.don_gia " +
                    "FROM hoa_don_chi_tiet hdct " +
                    "JOIN chi_tiet_san_pham ctsp ON hdct.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham " +
                    "JOIN san_pham sp ON ctsp.id_san_pham = sp.id_san_pham " +
                    "WHERE hdct.id_hoa_don = ?";

            List<Map<String, Object>> products = jdbcTemplate.queryForList(sql, idHoaDon);

            Map<String, Object> result = new HashMap<>();
            result.put("hoaDon", hoaDon);
            result.put("san_pham", products);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy thông tin cơ bản của đơn hàng: " + e.getMessage());
        }
    }

    // Lấy phí vận chuyển theo ID hóa đơn
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_KH')")
    @GetMapping("/phi-van-chuyen/{idHoaDon}")
    public ResponseEntity<?> getPhiVanChuyen(@PathVariable Integer idHoaDon) {
        try {
            String sql = "select phi_van_chuyen from hoa_don where id_hoa_don = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, idHoaDon);

            BigDecimal phiVanChuyen = (BigDecimal) result.get("phi_van_chuyen");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("phi_van_chuyen", phiVanChuyen);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy phí vận chuyển: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoint mới cho trả hàng
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
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

    ///Của lềnh
    // lềnh sửa
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_KH')")
    @PutMapping("/huy-don/{idHoaDon}")
    @Transactional
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Integer idHoaDon) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(idHoaDon);

            if (!hoaDonOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy hóa đơn!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            HoaDon hoaDon = hoaDonOpt.get();

            // Lấy lịch sử trạng thái từ bảng theo_doi_don_hang
            List<TheoDoiDonHangResponse> trangThaiHistory = hoaDonRepo.findTrangThaiHistoryByIdHoaDon(idHoaDon);
            if (trangThaiHistory.isEmpty() || !"Chờ xác nhận".equals(trangThaiHistory.get(trangThaiHistory.size() - 1).getTrang_thai())) {
                response.put("success", false);
                response.put("message", "Chỉ có thể hủy đơn hàng ở trạng thái Chờ xác nhận!");
                return ResponseEntity.badRequest().body(response);
            }

            // Hoàn lại số lượng sản phẩm
            List<HoaDonChiTiet> chiTietList = hoaDon.getDanhSachChiTiet();
            for (HoaDonChiTiet chiTiet : chiTietList) {
                ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
                ctsp.setSo_luong(ctsp.getSo_luong() + chiTiet.getSo_luong());
                chiTietSanPhamRepo.save(ctsp);
            }

            // Hoàn lại voucher nếu có
            if (hoaDon.getVoucher() != null) {
                Voucher voucher = hoaDon.getVoucher();
                voucher.setSoLuong(voucher.getSoLuong() + 1);
                voucherRepo.save(voucher);
            }

            // Cập nhật trạng thái đơn hàng trong hoa_don
            hoaDon.setTrang_thai("Đã hủy");
            hoaDon.setNgay_sua(LocalDateTime.now());
            hoaDonRepo.save(hoaDon);

            // Thêm vào lịch sử theo dõi đơn hàng
            TheoDoiDonHang theoDoiDonHang = new TheoDoiDonHang();
            theoDoiDonHang.setHoaDon(hoaDon);
            theoDoiDonHang.setTrang_thai("Đã hủy");
            theoDoiDonHang.setNgay_chuyen(LocalDateTime.now());
            theoDoiDonHangRepo.save(theoDoiDonHang);

            response.put("success", true);
            response.put("message", "Hủy đơn hàng thành công!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi hủy đơn hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    //// hết

}

