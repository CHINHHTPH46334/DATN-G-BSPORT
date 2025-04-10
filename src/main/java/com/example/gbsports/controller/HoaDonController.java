package com.example.gbsports.controller;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.response.ChiTietSanPhamView;
import com.example.gbsports.response.HoaDonChiTietResponse;
import com.example.gbsports.response.HoaDonResponse;
import com.example.gbsports.response.TheoDoiDonHangResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

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
        // cập nhật các field khác nếu cần

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
        return list; // Trả về danh sách, kể cả khi rỗng
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
            @RequestParam("newTrangThai") String newTrangThai) {
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
        // Nếu trạng thái mới là "Đã xác nhận", trừ số lượng trong chi_tiet_san_pham
        if ("Đã xác nhận".equals(newTrangThai)) {
            List<HoaDonChiTietResponse> chiTietHoaDons = hoaDonChiTietRepo.findHoaDonChiTietById(idHoaDon);
            for (HoaDonChiTietResponse chiTiet : chiTietHoaDons) {
                Integer idCTSP = chiTiet.getId_chi_tiet_san_pham();
                Integer soLuong = chiTiet.getSo_luong();

                // Kiểm tra số lượng tồn kho trước khi trừ
                Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepo.findById(idCTSP);
                if (chiTietSanPhamOpt.isPresent()) {
                    ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();
                    if (chiTietSanPham.getSo_luong() < soLuong) {
                        throw new RuntimeException("Số lượng tồn kho không đủ cho sản phẩm: " + chiTiet.getTen_san_pham());
                    }
                    // Trừ số lượng
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
        // Cập nhật trạng thái đơn hàng
        hoaDonRepo.insertTrangThaiDonHang(maHoaDon, newTrangThai, ngayChuyen);
        return "Cập nhật trạng thái thành công: " + newTrangThai;
    }

    @PostMapping("/quay_lai_trang_thai")
    @Transactional
    public ResponseEntity<Map<String, Object>> revertToInitialStatus(@RequestParam("maHoaDon") String maHoaDon) {
        // Kiểm tra mã hóa đơn
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Mã hóa đơn không hợp lệ!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        // Tìm hóa đơn
        Optional<HoaDonResponse> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);
        if (!hoaDonOpt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không tìm thấy hóa đơn với mã: " + maHoaDon);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        // Lấy lịch sử trạng thái
        Integer idHoaDon = hoaDonOpt.get().getId_hoa_don();
        List<TheoDoiDonHangResponse> trangThaiHistory = hoaDonRepo.findTrangThaiHistoryByIdHoaDon(idHoaDon);
        // Kiểm tra trạng thái đầu tiên
        if (trangThaiHistory.isEmpty() || !trangThaiHistory.get(0).getTrang_thai().equals("Chờ xác nhận")) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Không thể quay lại vì trạng thái ban đầu không phải 'Chờ xác nhận'!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        // Nếu trạng thái hiện tại là "Đã xác nhận", hoàn lại số lượng
        String trangThaiHienTai = trangThaiHistory.get(trangThaiHistory.size() - 1).getTrang_thai();
        if ("Đã xác nhận".equals(trangThaiHienTai)) {
            List<HoaDonChiTietResponse> chiTietHoaDons = hoaDonChiTietRepo.findHoaDonChiTietById(idHoaDon);
            for (HoaDonChiTietResponse chiTiet : chiTietHoaDons) {
                Integer idCTSP = chiTiet.getId_chi_tiet_san_pham();
                Integer soLuong = chiTiet.getSo_luong();
                // Hoàn lại số lượng
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
        // Thêm bản ghi mới với trạng thái "Chờ xác nhận"
        LocalDateTime ngayChuyen = LocalDateTime.now();
        hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Chờ xác nhận", ngayChuyen);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã quay lại trạng thái 'Chờ xác nhận'!");
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/cancel_order")
//    @Transactional
//    public String cancelOrder(@RequestParam("maHoaDon") String maHoaDon,
//                              @RequestParam(value = "nhanVienDoi", required = false) String nhanVienDoi, // Thêm tham số
//                              @RequestParam(value = "noiDungDoi", required = false) String noiDungDoi) { // Thêm tham số
//        Optional<HoaDonResponse> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);
//        if (!hoaDonOpt.isPresent()) {
//            throw new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon);
//        }
//
//        Integer idHoaDon = hoaDonOpt.get().getId_hoa_don();
//        LocalDateTime ngayChuyen = LocalDateTime.now();
//
//        // Lấy trạng thái gần nhất (bỏ qua "Đã cập nhật")
//        String trangThaiGanNhat = hoaDonRepo.findLatestNonUpdatedStatusByIdHoaDon(idHoaDon);
//        if (trangThaiGanNhat == null) {
//            throw new RuntimeException("Không tìm thấy trạng thái phù hợp cho hóa đơn với mã: " + maHoaDon);
//        }
//
//        // Xử lý theo trạng thái gần nhất
//        if ("Chờ xác nhận".equals(trangThaiGanNhat)) {
//            // Chỉ hoàn lại số lượng voucher (nếu có)
//            Optional<HoaDon> hoaDonEntityOpt = hoaDonRepo.findById(idHoaDon);
//            if (hoaDonEntityOpt.isPresent()) {
//                HoaDon hoaDon = hoaDonEntityOpt.get();
//                Integer idVoucher = hoaDon.getVoucher() != null ? hoaDon.getVoucher().getId() : null;
//                if (idVoucher != null) {
//                    Optional<Voucher> voucherOpt = voucherRepo.findById(idVoucher);
//                    if (voucherOpt.isPresent()) {
//                        Voucher voucher = voucherOpt.get();
//                        voucher.setSoLuong(voucher.getSoLuong() + 1);
//                        voucherRepo.save(voucher);
//                    } else {
//                        throw new RuntimeException("Không tìm thấy voucher với ID: " + idVoucher);
//                    }
//                }
//            }
//        } else if ("Đã xác nhận".equals(trangThaiGanNhat) || "Chờ đóng gói".equals(trangThaiGanNhat)) {
//            // Hoàn lại số lượng voucher (nếu có) và số lượng sản phẩm chi tiết
//            Optional<HoaDon> hoaDonEntityOpt = hoaDonRepo.findById(idHoaDon);
//            if (hoaDonEntityOpt.isPresent()) {
//                HoaDon hoaDon = hoaDonEntityOpt.get();
//                Integer idVoucher = hoaDon.getVoucher() != null ? hoaDon.getVoucher().getId() : null;
//                if (idVoucher != null) {
//                    Optional<Voucher> voucherOpt = voucherRepo.findById(idVoucher);
//                    if (voucherOpt.isPresent()) {
//                        Voucher voucher = voucherOpt.get();
//                        voucher.setSoLuong(voucher.getSoLuong() + 1);
//                        voucherRepo.save(voucher);
//                    } else {
//                        throw new RuntimeException("Không tìm thấy voucher với ID: " + idVoucher);
//                    }
//                }
//            }
//
//            // Hoàn lại số lượng sản phẩm chi tiết
//            List<HoaDonChiTietResponse> chiTietHoaDons = hoaDonChiTietRepo.findHoaDonChiTietById(idHoaDon);
//            for (HoaDonChiTietResponse chiTiet : chiTietHoaDons) {
//                Integer idCTSP = chiTiet.getId_chi_tiet_san_pham();
//                Integer soLuong = chiTiet.getSo_luong();
//                Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepo.findById(idCTSP);
//                if (chiTietSanPhamOpt.isPresent()) {
//                    ChiTietSanPham chiTietSanPham = chiTietSanPhamOpt.get();
//                    chiTietSanPham.setSo_luong(chiTietSanPham.getSo_luong() + soLuong);
//                    chiTietSanPhamRepo.save(chiTietSanPham);
//                } else {
//                    throw new RuntimeException("Không tìm thấy sản phẩm chi tiết với ID: " + idCTSP);
//                }
//            }
//        } else {
//            throw new RuntimeException("Không thể hủy đơn hàng ở trạng thái: " + trangThaiGanNhat);
//        }
////        hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã hủy", ngayChuyen, nhanVienDoi, noiDungDoi);
//        return "Đơn hàng đã được hủy";
//    }


    @PostMapping("/update_ttkh")
    public ResponseEntity<Map<String, Object>> updateCustomerInfo(
            @RequestBody Map<String, Object> request) {
        String maHoaDon = (String) request.get("maHoaDon");
        String hoTen = (String) request.get("hoTen");
        String email = (String) request.get("email");
        String sdtNguoiNhan = (String) request.get("sdtNguoiNhan");
        String diaChi = (String) request.get("diaChi");

        // Kiểm tra đầu vào
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Mã hóa đơn không hợp lệ!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        // Tìm hóa đơn theo mã
        Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(hoaDonRepo.findByMaHoaDon(maHoaDon)
                .map(HoaDonResponse::getId_hoa_don)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon)));

        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            // Cập nhật thông tin khách hàng
            if (hoTen != null && !hoTen.trim().isEmpty()) {
                hoaDon.setHo_ten(hoTen);
            }
            if (email != null && !email.trim().isEmpty()) {
                // Cập nhật email trong bảng khach_hang (nếu cần)
                hoaDon.setEmail(email);
                // Giả sử bảng khach_hang có liên kết với hoa_don qua id_khach_hang
                // Bạn có thể cần thêm repository cho khach_hang để cập nhật email
                // Ở đây tôi chỉ lưu email vào hoaDon để đơn giản hóa
            }
            if (sdtNguoiNhan != null && !sdtNguoiNhan.trim().isEmpty()) {
                hoaDon.setSdt_nguoi_nhan(sdtNguoiNhan);
            }
            if (diaChi != null && !diaChi.trim().isEmpty()) {
                hoaDon.setDia_chi(diaChi);
            }
            hoaDon.setNgay_sua(LocalDateTime.now());
            // Lưu vào database
            hoaDonRepo.save(hoaDon);
            // Insert trạng thái "Đã cập nhật"
            LocalDateTime ngayChuyen = LocalDateTime.now();
//            Optional<TheoDoiDonHang> tddhOpt = theoDoiDonHangRepo.findByMaHDAndTrangThai(maHoaDon);
//            if (tddhOpt.isPresent()){
//                // Nếu trạng thái đã tồn tại, chỉ cập nhật ngày chuyển
//                TheoDoiDonHang theoDoiDonHang = tddhOpt.get();
//                theoDoiDonHang.setNgay_chuyen(ngayChuyen);
//                theoDoiDonHangRepo.save(theoDoiDonHang); // Cập nhật bản ghi hiện có
//            } else {
                // Nếu chưa có, insert mới
                hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen);
//            }

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

        // Kiểm tra đầu vào
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Mã hóa đơn không hợp lệ!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Tìm hóa đơn theo mã
        Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(hoaDonRepo.findByMaHoaDon(maHoaDon)
                .map(HoaDonResponse::getId_hoa_don)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon)));

        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            // Cập nhật ghi chú
            hoaDon.setGhi_chu(ghiChu != null ? ghiChu : ""); // Nếu ghiChu là null, đặt thành chuỗi rỗng
            hoaDon.setNgay_sua(LocalDateTime.now());
            hoaDonRepo.save(hoaDon);
            // Insert trạng thái "Đã cập nhật"
            LocalDateTime ngayChuyen = LocalDateTime.now();
//            Optional<TheoDoiDonHang> tddhOpt = theoDoiDonHangRepo.findByMaHDAndTrangThai(maHoaDon);
//            if (tddhOpt.isPresent()){
//                // Nếu trạng thái đã tồn tại, chỉ cập nhật ngày chuyển
//                TheoDoiDonHang theoDoiDonHang = tddhOpt.get();
//                theoDoiDonHang.setNgay_chuyen(ngayChuyen);
//                theoDoiDonHangRepo.save(theoDoiDonHang); // Cập nhật bản ghi hiện có
//            } else {
                // Nếu chưa có, insert mới
                hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen);
//            }

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
        // Kiểm tra đầu vào
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
        // Tìm hóa đơn
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
                // Kiểm tra sản phẩm có tồn tại không
                Optional<ChiTietSanPham> chiTietSanPhamOpt = chiTietSanPhamRepo.findById(idCTSP);
                if (!chiTietSanPhamOpt.isPresent()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Không tìm thấy sản phẩm với ID: " + idCTSP);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
                // Gọi hàm thêm sản phẩm mà không trừ số lượng tồn kho
                hoaDonChiTietRepo.addSLGH_HD(idCTSP, idHoaDon, soLuongMua);
                // Insert trạng thái "Đã cập nhật"
                LocalDateTime ngayChuyen = LocalDateTime.now();
//                Optional<TheoDoiDonHang> tddhOpt = theoDoiDonHangRepo.findByMaHDAndTrangThai(maHoaDon);
//                if (tddhOpt.isPresent()){
//                    // Nếu trạng thái đã tồn tại, chỉ cập nhật ngày chuyển
//                    TheoDoiDonHang theoDoiDonHang = tddhOpt.get();
//                    theoDoiDonHang.setNgay_chuyen(ngayChuyen);
//                    theoDoiDonHangRepo.save(theoDoiDonHang); // Cập nhật bản ghi hiện có
//                } else {
                    // Nếu chưa có, insert mới
                    hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen);
//                }
            }
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
            @RequestParam("soLuong") Integer soLuong) {
        try {
            Optional<HoaDonResponse> hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon);
            if (!hoaDon.isPresent()) {
                return ResponseEntity.badRequest().body("Hóa đơn không tồn tại");
            }
            Integer idHoaDon = hoaDon.get().getId_hoa_don();
            hoaDonChiTietRepo.removeSPGHinHDCT(idCTSP, idHoaDon, soLuong);
            // Insert trạng thái "Đã cập nhật"
            LocalDateTime ngayChuyen = LocalDateTime.now();
//            Optional<TheoDoiDonHang> tddhOpt = theoDoiDonHangRepo.findByMaHDAndTrangThai(maHoaDon);
//            if (tddhOpt.isPresent()){
//                // Nếu trạng thái đã tồn tại, chỉ cập nhật ngày chuyển
//                TheoDoiDonHang theoDoiDonHang = tddhOpt.get();
//                theoDoiDonHang.setNgay_chuyen(ngayChuyen);
//                theoDoiDonHangRepo.save(theoDoiDonHang); // Cập nhật bản ghi hiện có
//            } else {
                // Nếu chưa có, insert mới
                hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen);
//            }
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
            @RequestParam("quantityChange") Integer quantityChange) {
        try {
            Optional<HoaDonResponse> hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon);
            if (!hoaDon.isPresent()) {
                return ResponseEntity.badRequest().body("Hóa đơn không tồn tại");
            }
            Integer idHoaDon = hoaDon.get().getId_hoa_don();
            hoaDonChiTietRepo.updateQuantity(idCTSP, idHoaDon, quantityChange);
            // Insert trạng thái "Đã cập nhật"
            LocalDateTime ngayChuyen = LocalDateTime.now();
//            Optional<TheoDoiDonHang> tddhOpt = theoDoiDonHangRepo.findByMaHDAndTrangThai(maHoaDon);
//            if (tddhOpt.isPresent()){
//                // Nếu trạng thái đã tồn tại, chỉ cập nhật ngày chuyển
//                TheoDoiDonHang theoDoiDonHang = tddhOpt.get();
//                theoDoiDonHang.setNgay_chuyen(ngayChuyen);
//                theoDoiDonHangRepo.save(theoDoiDonHang); // Cập nhật bản ghi hiện có
//            } else {
                // Nếu chưa có, insert mới
                hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen);
//            }
            return ResponseEntity.ok("Cập nhật số lượng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật số lượng: " + e.getMessage());
        }
    }
}
