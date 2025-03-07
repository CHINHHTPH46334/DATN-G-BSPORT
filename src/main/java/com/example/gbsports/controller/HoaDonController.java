package com.example.gbsports.controller;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.repository.HoaDonChiTietRepo;
import com.example.gbsports.repository.HoaDonRepo;
import com.example.gbsports.response.HoaDonChiTietResponse;
import com.example.gbsports.response.HoaDonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/hoa_don")
public class HoaDonController {

    @Autowired
    private HoaDonRepo hoaDonRepo;
    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepo;
//    @Autowired
//    private HoaDonService hoaDonService;

    @GetMapping("/danh_sach_hoa_don")
    public String getAllHD(Model model,
                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                           @RequestParam(value = "size", defaultValue = "3") Integer size) {
        if (page < 0) {
            page = 0; // Đặt lại trang về 0 nếu âm
            model.addAttribute("error", "Số trang không hợp lệ, quay về trang đầu tiên.");
        }
        if (size <= 0) {
            size = 3; // Đặt lại kích thước mặc định nếu nhỏ hơn hoặc bằng 0
            model.addAttribute("error", "Kích thước trang không hợp lệ, sử dụng giá trị mặc định 5.");
        }
        Pageable pageable = PageRequest.of(page, size);
        try {
            Page<HoaDonResponse> list = hoaDonRepo.getAllHD(pageable);
            model.addAttribute("hoaDons", list.getContent()); // Danh sách hóa đơn trên trang hiện tại
            model.addAttribute("currentPage", page); // Trang hiện tại
            model.addAttribute("totalPages", list.getTotalPages() - 1); // Trang tối đa (index bắt đầu từ 0)
            model.addAttribute("hasNext", list.hasNext()); // Có trang tiếp theo không
            model.addAttribute("hasPrevious", list.hasPrevious()); // Có trang trước không
            if (list.getContent().isEmpty()){
                model.addAttribute("error", "Dữ liệu trống. Không có hóa đơn nào trong danh sách.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
            return "hoa_don";
        }
        return "hoa_don";
    }

    @GetMapping("/tim_kiem")
    public String search(@RequestParam(name = "keyword", required = false) String keyword, Model model,
                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                         @RequestParam(value = "size", defaultValue = "3") Integer size) {
        // Validate page và size
        if (page < 0) {
            page = 0;
            model.addAttribute("error", "Số trang không hợp lệ, quay về trang đầu tiên.");
        }
        if (size <= 0) {
            size = 3;
            model.addAttribute("error", "Kích thước trang không hợp lệ, sử dụng giá trị mặc định 5.");
        }
        Pageable pageable = PageRequest.of(page, size);
        // Validate keyword: kiểm tra trống và khoảng trắng
        String searchKeyword = null;
        if (keyword != null) {
            String trimmedKeyword = keyword.trim();
            if (trimmedKeyword.isEmpty()) {
                model.addAttribute("error", "Từ khóa tìm kiếm không được để trống hoặc chỉ chứa khoảng trắng.");
                model.addAttribute("hoaDons", new ArrayList<>()); // Trả về danh sách rỗng nếu không hợp lệ
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", 0);
                model.addAttribute("hasNext", false);
                model.addAttribute("hasPrevious", false);
                model.addAttribute("keyword", keyword);
                return "hoa_don";
            }
            // Loại bỏ các ký tự đặc biệt không mong muốn và thêm % cho LIKE
            searchKeyword = "%" + trimmedKeyword.replaceAll("[^a-zA-Z0-9\\s]", "") + "%";
        }
        try {
            Page<HoaDonResponse> hoaDonPage = hoaDonRepo.timHoaDon(searchKeyword, pageable);
            model.addAttribute("hoaDons", hoaDonPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", hoaDonPage.getTotalPages() - 1);
            model.addAttribute("hasNext", hoaDonPage.hasNext());
            model.addAttribute("hasPrevious", hoaDonPage.hasPrevious());
            model.addAttribute("keyword", keyword);
            if (hoaDonPage.getContent().isEmpty()) {
                model.addAttribute("error", "Dữ liệu trống. Không tìm thấy hóa đơn nào với từ khóa: " + (keyword != null ? keyword : ""));
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tìm kiếm hóa đơn: " + e.getMessage());
            return "hoa_don";
        }
        return "hoa_don";
    }

    @GetMapping("/loc_ngay")
    public String filterHoaDonByDate(Model model,
                                     @RequestParam(value = "tuNgay", required = false) String tuNgayStr,
                                     @RequestParam(value = "denNgay", required = false) String denNgayStr,
                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                     @RequestParam(value = "size", defaultValue = "3") Integer size) {
        if (page < 0) {
            page = 0;
            model.addAttribute("error", "Số trang không hợp lệ, quay về trang đầu tiên.");
        }
        if (size <= 0) {
            size = 3;
            model.addAttribute("error", "Kích thước trang không hợp lệ, sử dụng giá trị mặc định 5.");
        }
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime tuNgay = null;
        LocalDateTime denNgay = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        try {
            if (tuNgayStr != null && !tuNgayStr.isEmpty()) {
                tuNgay = LocalDateTime.parse(tuNgayStr, formatter);
            }
            if (denNgayStr != null && !denNgayStr.isEmpty()) {
                denNgay = LocalDateTime.parse(denNgayStr, formatter);
            }
            if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
                model.addAttribute("error", "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.");
                return "hoa_don";
            }
            Page<HoaDonResponse> hoaDonPage = hoaDonRepo.findHoaDonByNgay(tuNgay, denNgay, pageable);
            // Thêm dữ liệu vào model để hiển thị trên view
            model.addAttribute("hoaDons", hoaDonPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", hoaDonPage.getTotalPages() - 1);
            model.addAttribute("hasNext", hoaDonPage.hasNext());
            model.addAttribute("hasPrevious", hoaDonPage.hasPrevious());
            model.addAttribute("tuNgay", tuNgayStr); // Giữ lại giá trị để hiển thị lại trong input
            model.addAttribute("denNgay", denNgayStr); // Giữ lại giá trị để hiển thị lại trong input
            if (hoaDonPage.getContent().isEmpty()) {
                model.addAttribute("error", "Dữ liệu trống. Không có hóa đơn nào trong khoảng ngày này.");
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng yyyy-MM-ddTHH:mm");
            return "hoa_don";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lọc hóa đơn theo ngày: " + e.getMessage());
            return "hoa_don";
        }
        return "hoa_don";
    }

    @GetMapping("/loc_trang_thai_don_hang")
    public String filterHoaDonByTrangThai(Model model,
                                          @RequestParam(value = "trangThai", required = false) String trangThai,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "3") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HoaDonResponse> hoaDonPage;
        try {
            if (trangThai == null || trangThai.trim().isEmpty()) {
                hoaDonPage = hoaDonRepo.getAllHD(pageable);
            } else {
                hoaDonPage = hoaDonRepo.findHoaDonByTrangThaiGiaoHang(trangThai, pageable);
            }
            model.addAttribute("hoaDons", hoaDonPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", hoaDonPage.getTotalPages() - 1);
            model.addAttribute("hasNext", hoaDonPage.hasNext());
            model.addAttribute("hasPrevious", hoaDonPage.hasPrevious());
            model.addAttribute("trangThai", trangThai);
            if (hoaDonPage.getContent().isEmpty()) {
                model.addAttribute("error", "Dữ liệu trống. Không có hóa đơn nào với trạng thái: " + (trangThai != null ? trangThai : "Tất cả"));
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lọc hóa đơn theo trạng thái: " + e.getMessage());
            return "hoa_don";
        }
        return "hoa_don";
    }

//    @GetMapping("/xuat_hoa_don")
//    public ResponseEntity<byte[]> exportHoaDonPDF(@RequestParam("maHD") Integer maHoaDon) {
//        byte[] pdfBytes = hoaDonService.generateHoaDonPDF(maHoaDon);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDisposition(ContentDisposition.attachment()
//                .filename("HOA_DON_" + maHoaDon + ".pdf")
//                .build());
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(pdfBytes);
//    }

    //    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@--HÓA ĐƠN CHI TIẾT--@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @GetMapping("/hdct")
    public String getHDCTBymaHD(@RequestParam("id") String maHoaDon, Model model) {
        try {
            // Tìm id_hoa_don dựa trên ma_hoa_don
            HoaDon hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + maHoaDon));
            Integer idHoaDon = hoaDon.getId_hoa_don();
            // Lấy chi tiết hóa đơn (dạng List)
            List<HoaDonChiTietResponse> chiTietHoaDons = hoaDonChiTietRepo.findHoaDonChiTietById(idHoaDon);
            // Kiểm tra nếu danh sách chi tiết rỗng
            if (chiTietHoaDons.isEmpty()) {
                model.addAttribute("error", "Không có chi tiết hóa đơn nào cho hóa đơn này.");
                model.addAttribute("hoaDon", hoaDon); // Truyền thông tin hóa đơn cơ bản nếu không có chi tiết
                model.addAttribute("chiTietHoaDons", chiTietHoaDons);
            } else {
                // Thêm vào model
                model.addAttribute("hoaDon", chiTietHoaDons.get(0)); // Lấy thông tin hóa đơn từ response đầu tiên
                model.addAttribute("chiTietHoaDons", chiTietHoaDons);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage());
            return "hdct";
        }
        return "hdct";
    }
}
