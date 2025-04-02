package com.example.gbsports.controller;

import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.entity.HoaDonChiTiet;
import com.example.gbsports.repository.ChiTietSanPhamRepo;
import com.example.gbsports.repository.HoaDonChiTietRepo;
import com.example.gbsports.repository.HoaDonRepo;
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
        LocalDateTime ngayChuyen = LocalDateTime.now();
        hoaDonRepo.insertTrangThaiDonHang(maHoaDon, newTrangThai, ngayChuyen);
        return "Cập nhật trạng thái thành công: " + newTrangThai;
    }

    @PostMapping("/quay_lai_trang_thai")
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

        // Thêm bản ghi mới với trạng thái "Chờ xác nhận"
        LocalDateTime ngayChuyen = LocalDateTime.now();
        hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Chờ xác nhận", ngayChuyen);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã quay lại trạng thái 'Chờ xác nhận'!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel_order")
    public String cancelOrder(@RequestParam("maHoaDon") String maHoaDon) {
        Optional<HoaDonResponse> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);
        if (!hoaDonOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon);
        }
        LocalDateTime ngayChuyen = LocalDateTime.now();
        hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã hủy", ngayChuyen);
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

            // Lưu vào database
            hoaDonRepo.save(hoaDon);
            // Insert trạng thái "Đã cập nhật"
            LocalDateTime ngayChuyen = LocalDateTime.now();
            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen);

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
            hoaDonRepo.save(hoaDon);
            // Insert trạng thái "Đã cập nhật"
            LocalDateTime ngayChuyen = LocalDateTime.now();
            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen);

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

                // Gọi addSLGH_HD để xử lý toàn bộ logic
                hoaDonChiTietRepo.addSLGH_HD(idCTSP, idHoaDon, soLuongMua);
                // Insert trạng thái "Đã cập nhật"
                LocalDateTime ngayChuyen = LocalDateTime.now();
                hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen);
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
            if (hoaDon == null) {
                return ResponseEntity.badRequest().body("Hóa đơn không tồn tại");
            }

            Integer idHoaDon = hoaDon.get().getId_hoa_don();
            hoaDonChiTietRepo.removeSPGHinHDCT(idCTSP, idHoaDon, soLuong);
            // Insert trạng thái "Đã cập nhật"
            LocalDateTime ngayChuyen = LocalDateTime.now();
            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen);
            return ResponseEntity.ok("Xóa sản phẩm thành công");
//            if (hoaDon.get().getId_voucher() == null){
//                return ResponseEntity.ok("Xóa sản phẩm thành công");
//            } else {
//                ResponseEntity.ok("Xóa sản phẩm thành công");
//                return ResponseEntity.ok("Hóa đơn đã áp dụng voucher "+ hoaDon.get().getMo_ta());
//            }
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
            if (hoaDon == null) {
                return ResponseEntity.badRequest().body("Hóa đơn không tồn tại");
            }

            Integer idHoaDon = hoaDon.get().getId_hoa_don();
            hoaDonChiTietRepo.updateQuantity(idCTSP, idHoaDon, quantityChange);
            // Insert trạng thái "Đã cập nhật"
            LocalDateTime ngayChuyen = LocalDateTime.now();
            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã cập nhật", ngayChuyen);
            return ResponseEntity.ok("Cập nhật số lượng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật số lượng: " + e.getMessage());
        }
    }

//    @GetMapping("/danh_sach_hoa_don")
//    public String getAllHD(Model model,
//                           @RequestParam(value = "page", defaultValue = "0") Integer page,
//                           @RequestParam(value = "size", defaultValue = "3") Integer size) {
//        if (page < 0) {
//            page = 0; // Đặt lại trang về 0 nếu âm
//            model.addAttribute("error", "Số trang không hợp lệ, quay về trang đầu tiên.");
//        }
//        if (size <= 0) {
//            size = 3; // Đặt lại kích thước mặc định nếu nhỏ hơn hoặc bằng 0
//            model.addAttribute("error", "Kích thước trang không hợp lệ, sử dụng giá trị mặc định 5.");
//        }
//        Pageable pageable = PageRequest.of(page, size);
//        try {
//            Page<HoaDonResponse> list = hoaDonRepo.getAllHD(pageable);
//            model.addAttribute("hoaDons", list.getContent()); // Danh sách hóa đơn trên trang hiện tại
//            model.addAttribute("currentPage", page); // Trang hiện tại
//            model.addAttribute("totalPages", list.getTotalPages() - 1); // Trang tối đa (index bắt đầu từ 0)
//            model.addAttribute("hasNext", list.hasNext()); // Có trang tiếp theo không
//            model.addAttribute("hasPrevious", list.hasPrevious()); // Có trang trước không
//            if (list.getContent().isEmpty()){
//                model.addAttribute("error", "Dữ liệu trống. Không có hóa đơn nào trong danh sách.");
//            }
//        } catch (Exception e) {
//            model.addAttribute("error", "Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
//            return "hoa_don";
//        }
//        return "hoa_don";
//    }
//
//    @GetMapping("/tim_kiem")
//    public String search(@RequestParam(name = "keyword", required = false) String keyword, Model model,
//                         @RequestParam(value = "page", defaultValue = "0") Integer page,
//                         @RequestParam(value = "size", defaultValue = "3") Integer size) {
//        // Validate page và size
//        if (page < 0) {
//            page = 0;
//            model.addAttribute("error", "Số trang không hợp lệ, quay về trang đầu tiên.");
//        }
//        if (size <= 0) {
//            size = 3;
//            model.addAttribute("error", "Kích thước trang không hợp lệ, sử dụng giá trị mặc định 5.");
//        }
//        Pageable pageable = PageRequest.of(page, size);
//        // Validate keyword: kiểm tra trống và khoảng trắng
//        String searchKeyword = null;
//        if (keyword != null) {
//            String trimmedKeyword = keyword.trim();
//            if (trimmedKeyword.isEmpty()) {
//                model.addAttribute("error", "Từ khóa tìm kiếm không được để trống hoặc chỉ chứa khoảng trắng.");
//                model.addAttribute("hoaDons", new ArrayList<>()); // Trả về danh sách rỗng nếu không hợp lệ
//                model.addAttribute("currentPage", page);
//                model.addAttribute("totalPages", 0);
//                model.addAttribute("hasNext", false);
//                model.addAttribute("hasPrevious", false);
//                model.addAttribute("keyword", keyword);
//                return "hoa_don";
//            }
//            // Loại bỏ các ký tự đặc biệt không mong muốn và thêm % cho LIKE
//            searchKeyword = "%" + trimmedKeyword.replaceAll("[^a-zA-Z0-9\\s]", "") + "%";
//        }
//        try {
//            Page<HoaDonResponse> hoaDonPage = hoaDonRepo.timHoaDon(searchKeyword, pageable);
//            model.addAttribute("hoaDons", hoaDonPage.getContent());
//            model.addAttribute("currentPage", page);
//            model.addAttribute("totalPages", hoaDonPage.getTotalPages() - 1);
//            model.addAttribute("hasNext", hoaDonPage.hasNext());
//            model.addAttribute("hasPrevious", hoaDonPage.hasPrevious());
//            model.addAttribute("keyword", keyword);
//            if (hoaDonPage.getContent().isEmpty()) {
//                model.addAttribute("error", "Dữ liệu trống. Không tìm thấy hóa đơn nào với từ khóa: " + (keyword != null ? keyword : ""));
//            }
//        } catch (Exception e) {
//            model.addAttribute("error", "Lỗi khi tìm kiếm hóa đơn: " + e.getMessage());
//            return "hoa_don";
//        }
//        return "hoa_don";
//    }
//
//    @GetMapping("/loc_ngay")
//    public String filterHoaDonByDate(Model model,
//                                     @RequestParam(value = "tuNgay", required = false) String tuNgayStr,
//                                     @RequestParam(value = "denNgay", required = false) String denNgayStr,
//                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
//                                     @RequestParam(value = "size", defaultValue = "3") Integer size) {
//        if (page < 0) {
//            page = 0;
//            model.addAttribute("error", "Số trang không hợp lệ, quay về trang đầu tiên.");
//        }
//        if (size <= 0) {
//            size = 3;
//            model.addAttribute("error", "Kích thước trang không hợp lệ, sử dụng giá trị mặc định 5.");
//        }
//        Pageable pageable = PageRequest.of(page, size);
//        LocalDateTime tuNgay = null;
//        LocalDateTime denNgay = null;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//        try {
//            if (tuNgayStr != null && !tuNgayStr.isEmpty()) {
//                tuNgay = LocalDateTime.parse(tuNgayStr, formatter);
//            }
//            if (denNgayStr != null && !denNgayStr.isEmpty()) {
//                denNgay = LocalDateTime.parse(denNgayStr, formatter);
//            }
//            if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
//                model.addAttribute("error", "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.");
//                return "hoa_don";
//            }
//            Page<HoaDonResponse> hoaDonPage = hoaDonRepo.findHoaDonByNgay(tuNgay, denNgay, pageable);
//            // Thêm dữ liệu vào model để hiển thị trên view
//            model.addAttribute("hoaDons", hoaDonPage.getContent());
//            model.addAttribute("currentPage", page);
//            model.addAttribute("totalPages", hoaDonPage.getTotalPages() - 1);
//            model.addAttribute("hasNext", hoaDonPage.hasNext());
//            model.addAttribute("hasPrevious", hoaDonPage.hasPrevious());
//            model.addAttribute("tuNgay", tuNgayStr); // Giữ lại giá trị để hiển thị lại trong input
//            model.addAttribute("denNgay", denNgayStr); // Giữ lại giá trị để hiển thị lại trong input
//            if (hoaDonPage.getContent().isEmpty()) {
//                model.addAttribute("error", "Dữ liệu trống. Không có hóa đơn nào trong khoảng ngày này.");
//            }
//        } catch (DateTimeParseException e) {
//            model.addAttribute("error", "Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng yyyy-MM-ddTHH:mm");
//            return "hoa_don";
//        } catch (Exception e) {
//            model.addAttribute("error", "Lỗi khi lọc hóa đơn theo ngày: " + e.getMessage());
//            return "hoa_don";
//        }
//        return "hoa_don";
//    }
//
//    @GetMapping("/loc_trang_thai_don_hang")
//    public String filterHoaDonByTrangThai(Model model,
//                                          @RequestParam(value = "trangThai", required = false) String trangThai,
//                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
//                                          @RequestParam(value = "size", defaultValue = "3") Integer size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<HoaDonResponse> hoaDonPage;
//        try {
//            if (trangThai == null || trangThai.trim().isEmpty()) {
//                hoaDonPage = hoaDonRepo.getAllHD(pageable);
//            } else {
//                hoaDonPage = hoaDonRepo.findHoaDonByTrangThaiGiaoHang(trangThai, pageable);
//            }
//            model.addAttribute("hoaDons", hoaDonPage.getContent());
//            model.addAttribute("currentPage", page);
//            model.addAttribute("totalPages", hoaDonPage.getTotalPages() - 1);
//            model.addAttribute("hasNext", hoaDonPage.hasNext());
//            model.addAttribute("hasPrevious", hoaDonPage.hasPrevious());
//            model.addAttribute("trangThai", trangThai);
//            if (hoaDonPage.getContent().isEmpty()) {
//                model.addAttribute("error", "Dữ liệu trống. Không có hóa đơn nào với trạng thái: " + (trangThai != null ? trangThai : "Tất cả"));
//            }
//        } catch (Exception e) {
//            model.addAttribute("error", "Lỗi khi lọc hóa đơn theo trạng thái: " + e.getMessage());
//            return "hoa_don";
//        }
//        return "hoa_don";
//    }
//
////    @GetMapping("/xuat_hoa_don")
////    public ResponseEntity<byte[]> exportHoaDonPDF(@RequestParam("maHD") Integer maHoaDon) {
////        byte[] pdfBytes = hoaDonService.generateHoaDonPDF(maHoaDon);
////
////        HttpHeaders headers = new HttpHeaders();
////        headers.setContentType(MediaType.APPLICATION_PDF);
////        headers.setContentDisposition(ContentDisposition.attachment()
////                .filename("HOA_DON_" + maHoaDon + ".pdf")
////                .build());
////
////        return ResponseEntity.ok()
////                .headers(headers)
////                .body(pdfBytes);
////    }

//    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@--HÓA ĐƠN CHI TIẾT--@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//    @GetMapping("/hdct")
//    public String getHDCTBymaHD(@RequestParam("id") String maHoaDon, Model model) {
//        try {
//            // Tìm id_hoa_don dựa trên ma_hoa_don
//            HoaDonResponse hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon)
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon));
//            Integer idHoaDon = hoaDon.getId_hoa_don();
//            // Lấy chi tiết hóa đơn (dạng List)
//            List<HoaDonChiTietResponse> chiTietHoaDons = hoaDonChiTietRepo.findHoaDonChiTietById(idHoaDon);
//            List<TheoDoiDonHangResponse> trangThaiHistory = hoaDonRepo.findTrangThaiHistoryByIdHoaDon(idHoaDon);
//            // Kiểm tra nếu danh sách chi tiết rỗng
//            if (chiTietHoaDons.isEmpty()) {
//                model.addAttribute("error", "Không có chi tiết hóa đơn nào cho hóa đơn này.");
//                model.addAttribute("hoaDon", hoaDon); // Truyền thông tin hóa đơn cơ bản nếu không có chi tiết
//                model.addAttribute("chiTietHoaDons", chiTietHoaDons);
//            } else {
//                // Thêm vào model
//                model.addAttribute("hoaDon", chiTietHoaDons.get(0)); // Lấy thông tin hóa đơn từ response đầu tiên
//                model.addAttribute("chiTietHoaDons", chiTietHoaDons);
//                model.addAttribute("trangThaiHistory", trangThaiHistory);
//            }
//        } catch (Exception e) {
//            model.addAttribute("error", "Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage());
//            return "hdct2";
//        }
//        return "hdct2";
//    }
//
//    @PostMapping("/chuyen-trang-thai")
//    public String updateTrangThai(@RequestParam("maHoaDon") String maHoaDon,
//                                  @RequestParam("newTrangThai") String newTrangThai,
//                                  RedirectAttributes redirectAttributes) {
//        Optional<HoaDonResponse> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);
//        Integer idHoaDon = hoaDonOpt.get().getId_hoa_don();
//        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
//            redirectAttributes.addFlashAttribute("message", "Mã hóa đơn không hợp lệ.");
//            return "redirect:/hoa_don/hdct?id=" + maHoaDon;
//        }
//        if (!hoaDonOpt.isPresent()) {
//            redirectAttributes.addFlashAttribute("message", "Không tìm thấy hóa đơn với mã: " + maHoaDon);
//            return "redirect:/hoa_don/hdct?id=" + maHoaDon;
//        }
//
//        if (newTrangThai == null || newTrangThai.trim().isEmpty()) {
//            redirectAttributes.addFlashAttribute("message", "Trạng thái mới không hợp lệ.");
//            return "redirect:/hoa_don/hdct?id=" + maHoaDon;
//        }
//
//        try {
//            LocalDateTime ngayChuyen = LocalDateTime.now();
//            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, newTrangThai, ngayChuyen);
//            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thành công: " + newTrangThai);
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("message", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
//            // Log lỗi chi tiết để debug
//            e.printStackTrace();
//        }
//        // Chuyển hướng về trang chi tiết hóa đơn
//        return "redirect:/hoa_don/hdct?id=" + maHoaDon;
//    }
//
//    @PostMapping("/cancel-order")
//    public String cancelOrder(@RequestParam("maHoaDon") String maHoaDon, RedirectAttributes redirectAttributes) {
//        // Kiểm tra hóa đơn tồn tại
//        Optional<HoaDonResponse> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);
//        if (!hoaDonOpt.isPresent()) {
//            redirectAttributes.addFlashAttribute("message", "Không tìm thấy hóa đơn với mã: " + maHoaDon);
//            return "redirect:/hoa_don/hdct?id=" + maHoaDon;
//        }
//        try {
//            // Thêm trạng thái "Đã hủy" vào bảng theo_doi_don_hang
//            LocalDateTime ngayChuyen = LocalDateTime.now();
//            hoaDonRepo.insertTrangThaiDonHang(maHoaDon, "Đã hủy", ngayChuyen);
//            redirectAttributes.addFlashAttribute("message", "Đơn hàng đã được hủy!");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("message", "Lỗi khi hủy đơn: " + e.getMessage());
//        }
//        // Chuyển hướng về trang chi tiết hóa đơn
//        return "redirect:/hoa_don/hdct?id=" + maHoaDon;
//    }
}
