package com.example.gbsports.controller;

import com.example.gbsports.entity.DiaChiKhachHang;
import com.example.gbsports.entity.KhachHang;
import com.example.gbsports.entity.TaiKhoan;
import com.example.gbsports.repository.DiaChiKhachHangRepo;
import com.example.gbsports.repository.KhachHangRepo;
import com.example.gbsports.repository.TaiKhoanRepo;
import com.example.gbsports.request.KhachHangRequest;
import com.example.gbsports.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173/", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
@RequestMapping("/api/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangRepo khachHangRepo;

    @Autowired
    private DiaChiKhachHangRepo diaChiKhachHangRepo;

    @Autowired
    private TaiKhoanRepo taiKhoanRepo;

    @Autowired
    private EmailService emailService;

    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>>   getKhachHang(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "trangThai", required = false) String trangThai) {

        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHang> danhSachKhachHang;

        if (trangThai != null && !trangThai.isEmpty()) {
            danhSachKhachHang = khachHangRepo.locKhachHangTheoTrangThai(trangThai, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            String trimmedKeyword = keyword.trim()+"";
            danhSachKhachHang = khachHangRepo.timKhachHang(trimmedKeyword, pageable);
        } else {
            danhSachKhachHang = khachHangRepo.findAllSortedByIdDesc(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        if (danhSachKhachHang.isEmpty() && (keyword != null || trangThai != null)) {
            response.put("message", "Không tìm thấy khách hàng nào phù hợp!");
        }

        Map<Integer, String> diaChiMap = new HashMap<>();
        for (KhachHang kh : danhSachKhachHang) {
            var diaChiList = diaChiKhachHangRepo.findByKhachHangId(kh.getIdKhachHang());
            String diaChiString = diaChiList.stream()
                    .map(DiaChiKhachHang::getDiaChiKhachHang)
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("");
            diaChiMap.put(kh.getIdKhachHang(), diaChiString);
        }

        response.put("danhSachKhachHang", danhSachKhachHang.getContent());
        response.put("diaChiMap", diaChiMap);
        response.put("currentPage", page);
        response.put("totalPages", danhSachKhachHang.getTotalPages());
        response.put("trangThai", trangThai);
        response.put("keyword", keyword);

        return ResponseEntity.ok(response);
    }

//    @PostMapping("/add")
//    public ResponseEntity<Map<String, Object>> addKhachHang(
//            @Valid @RequestBody KhachHangRequest khachHangRequest,
//            BindingResult result) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        if (result.hasErrors()) {
//            Map<String, String> fieldErrors = new HashMap<>();
//            for (FieldError error : result.getFieldErrors()) {
//                fieldErrors.put(error.getField(), error.getDefaultMessage());
//            }
//            response.put("fieldErrors", fieldErrors);
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        if (khachHangRequest.getNgaySinh() == null) {
//            response.put("error", "Ngày sinh không được để trống!");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        LocalDate ngaySinh = khachHangRequest.getNgaySinh().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        LocalDate now = LocalDate.now();
//        int tuoi = Period.between(ngaySinh, now).getYears();
//        if (tuoi < 13) {
//            Map<String, String> fieldErrors = new HashMap<>();
//            fieldErrors.put("ngaySinh", "Khách hàng phải đủ 13 tuổi!");
//            response.put("fieldErrors", fieldErrors);
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        Optional<KhachHang> existingKhachHang = khachHangRepo.findByMaKhachHang(khachHangRequest.getMaKhachHang());
//        if (existingKhachHang.isPresent()) {
//            response.put("error", "Mã khách hàng đã tồn tại!");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        try {
//            TaiKhoan taiKhoan = new TaiKhoan();
//            taiKhoan.setTen_dang_nhap(khachHangRequest.getEmail());
//            taiKhoan.setMat_khau(khachHangRequest.getMatKhau());
//            taiKhoan = taiKhoanRepo.save(taiKhoan);
//
//            KhachHang khachHang = new KhachHang();
//            BeanUtils.copyProperties(khachHangRequest, khachHang);
//            khachHang.setTaiKhoan(taiKhoan);
//            khachHang = khachHangRepo.save(khachHang);
//
//            if (khachHangRequest.getDiaChiList() != null && !khachHangRequest.getDiaChiList().isEmpty()) {
//                List<KhachHangRequest.DiaChiRequest> validDiaChiList = khachHangRequest.getDiaChiList().stream()
//                        .filter(this::isValidDiaChi)
//                        .collect(Collectors.toList());
//
//                for (KhachHangRequest.DiaChiRequest diaChiReq : validDiaChiList) {
//                    DiaChiKhachHang diaChiKhachHang = new DiaChiKhachHang();
//                    diaChiKhachHang.setKhachHang(khachHang);
//                    diaChiKhachHang.setSoNha(diaChiReq.getSoNha());
//                    diaChiKhachHang.setXaPhuong(diaChiReq.getXaPhuong());
//                    diaChiKhachHang.setQuanHuyen(diaChiReq.getQuanHuyen());
//                    diaChiKhachHang.setTinhThanhPho(diaChiReq.getTinhThanhPho());
//                    diaChiKhachHangRepo.save(diaChiKhachHang);
//                }
//            }
//
//            String subject = "Chào mừng bạn đến với GB Sports!";
//            String body = "<h3>Xin chào " + khachHang.getTenKhachHang() + ",</h3>" +
//                    "<p>Cảm ơn bạn đã đăng ký tài khoản tại GB Sports. Tài khoản của bạn đã được tạo thành công!</p>" +
//                    "<p>Dưới đây là thông tin đăng nhập của bạn:</p>" +
//                    "<ul>" +
//                    "<li><strong>Tên đăng nhập</strong>: " + taiKhoan.getTen_dang_nhap() + "</li>" +
//                    "<li><strong>Mật khẩu</strong>: " + khachHangRequest.getMatKhau() + "</li>" +
//                    "</ul>" +
//                    "<p>Vui lòng đăng nhập để sử dụng dịch vụ.</p>" +
//                    "<p>Trân trọng,<br>Đội ngũ GB Sports</p>";
//            try {
//                emailService.sendEmail(khachHang.getEmail(), subject, body);
//            } catch (MessagingException e) {
//                response.put("error", "Lưu khách hàng thành công nhưng gửi email thất bại: " + e.getMessage());
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//            }
//
//            response.put("successMessage", "Thêm khách hàng thành công!");
//            response.put("khachHang", khachHang);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("error", "Có lỗi xảy ra khi thêm khách hàng: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addKhachHang(
            @Valid @RequestBody KhachHangRequest khachHangRequest,
            BindingResult result) {

        Map<String, Object> response = new HashMap<>();

        // Kiểm tra validation
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            }
            response.put("fieldErrors", fieldErrors);
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra ngày sinh
        if (khachHangRequest.getNgaySinh() == null) {
            response.put("error", "Ngày sinh không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        LocalDate ngaySinh = khachHangRequest.getNgaySinh().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        int tuoi = Period.between(ngaySinh, now).getYears();
        if (tuoi < 13) {
            Map<String, String> fieldErrors = new HashMap<>();
            fieldErrors.put("ngaySinh", "Khách hàng phải đủ 13 tuổi!");
            response.put("fieldErrors", fieldErrors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Sinh mã khách hàng tự động nếu không có trong request
            String maKhachHang = khachHangRequest.getMaKhachHang();
            if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
                maKhachHang = generateMaKhachHang();
            } else {
                // Kiểm tra nếu mã đã tồn tại
                Optional<KhachHang> existingKhachHang = khachHangRepo.findByMaKhachHang(maKhachHang);
                if (existingKhachHang.isPresent()) {
                    response.put("error", "Mã khách hàng đã tồn tại!");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            khachHangRequest.setMaKhachHang(maKhachHang);

            // Lưu tài khoản
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTen_dang_nhap(khachHangRequest.getEmail());
            taiKhoan.setMat_khau(khachHangRequest.getMatKhau());
            taiKhoan = taiKhoanRepo.save(taiKhoan);

            // Lưu khách hàng
            KhachHang khachHang = new KhachHang();
            BeanUtils.copyProperties(khachHangRequest, khachHang);
            khachHang.setTaiKhoan(taiKhoan);
            khachHang = khachHangRepo.save(khachHang);

            // Lưu địa chỉ
            if (khachHangRequest.getDiaChiList() != null && !khachHangRequest.getDiaChiList().isEmpty()) {
                List<KhachHangRequest.DiaChiRequest> validDiaChiList = khachHangRequest.getDiaChiList().stream()
                        .filter(this::isValidDiaChi)
                        .collect(Collectors.toList());

                for (KhachHangRequest.DiaChiRequest diaChiReq : validDiaChiList) {
                    DiaChiKhachHang diaChiKhachHang = new DiaChiKhachHang();
                    diaChiKhachHang.setKhachHang(khachHang);
                    diaChiKhachHang.setSoNha(diaChiReq.getSoNha());
                    diaChiKhachHang.setXaPhuong(diaChiReq.getXaPhuong());
                    diaChiKhachHang.setQuanHuyen(diaChiReq.getQuanHuyen());
                    diaChiKhachHang.setTinhThanhPho(diaChiReq.getTinhThanhPho());
                    diaChiKhachHangRepo.save(diaChiKhachHang);
                }
            }

            // Gửi email (không làm thất bại request nếu lỗi)
            String subject = "Chào mừng bạn đến với GB Sports!";
            String body = "<h3>Xin chào " + khachHang.getTenKhachHang() + ",</h3>" +
                    "<p>Cảm ơn bạn đã đăng ký tài khoản tại GB Sports. Tài khoản của bạn đã được tạo thành công!</p>" +
                    "<p>Dưới đây là thông tin đăng nhập của bạn:</p>" +
                    "<ul>" +
                    "<li><strong>Tên đăng nhập</strong>: " + taiKhoan.getTen_dang_nhap() + "</li>" +
                    "<li><strong>Mật khẩu</strong>: " + khachHangRequest.getMatKhau() + "</li>" +
                    "</ul>" +
                    "<p>Vui lòng đăng nhập để sử dụng dịch vụ.</p>" +
                    "<p>Trân trọng,<br>Đội ngũ GB Sports</p>";

            try {
                emailService.sendEmail(khachHang.getEmail(), subject, body);
                response.put("emailMessage", "Email chào mừng đã được gửi thành công!");
            } catch (MessagingException e) {
                response.put("warning", "Lưu khách hàng thành công nhưng gửi email thất bại: " + e.getMessage());
            }

            response.put("successMessage", "Thêm khách hàng thành công!");
            response.put("khachHang", khachHang);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Có lỗi xảy ra khi thêm khách hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Phương thức sinh mã khách hàng tự động
    private String generateMaKhachHang() {
        long count = khachHangRepo.count();
        return String.format("KH%03d", count + 1); // Ví dụ: KH011, KH012,...
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> getKhachHangForEdit(@PathVariable("id") Integer id) {
        Map<String, Object> response = new HashMap<>();

        KhachHang khachHang = khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        KhachHangRequest request = new KhachHangRequest();
        BeanUtils.copyProperties(khachHang, request);

        var diaChiList = diaChiKhachHangRepo.findByKhachHangId(khachHang.getIdKhachHang());
        for (DiaChiKhachHang diaChi : diaChiList) {
            KhachHangRequest.DiaChiRequest diaChiReq = new KhachHangRequest.DiaChiRequest();
            diaChiReq.setSoNha(diaChi.getSoNha());
            diaChiReq.setXaPhuong(diaChi.getXaPhuong());
            diaChiReq.setQuanHuyen(diaChi.getQuanHuyen());
            diaChiReq.setTinhThanhPho(diaChi.getTinhThanhPho());
            request.getDiaChiList().add(diaChiReq);
        }

        if (khachHang.getTaiKhoan() != null) {
            request.setMatKhau(khachHang.getTaiKhoan().getMat_khau());
        }

        response.put("khachHang", request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateKhachHang(
            @Valid @RequestBody KhachHangRequest khachHangRequest,
            BindingResult result) {

        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            }
            response.put("fieldErrors", fieldErrors);
            return ResponseEntity.badRequest().body(response);
        }

        if (khachHangRequest.getNgaySinh() == null) {
            response.put("error", "Ngày sinh không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        LocalDate ngaySinh = khachHangRequest.getNgaySinh().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        int tuoi = Period.between(ngaySinh, now).getYears();
        if (tuoi < 13) {
            Map<String, String> fieldErrors = new HashMap<>();
            fieldErrors.put("ngaySinh", "Khách hàng phải đủ 13 tuổi!");
            response.put("fieldErrors", fieldErrors);
            return ResponseEntity.badRequest().body(response);
        }

        KhachHang khachHang = khachHangRepo.findById(khachHangRequest.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        // Kiểm tra mã khách hàng mới nếu thay đổi
        if (!khachHang.getMaKhachHang().equals(khachHangRequest.getMaKhachHang())) {
            Optional<KhachHang> existingKhachHang = khachHangRepo.findByMaKhachHang(khachHangRequest.getMaKhachHang());
            if (existingKhachHang.isPresent()) {
                response.put("error", "Mã khách hàng mới đã tồn tại!");
                return ResponseEntity.badRequest().body(response);
            }
        }

        try {
            BeanUtils.copyProperties(khachHangRequest, khachHang);

            if (khachHangRequest.getMatKhau() != null && !khachHangRequest.getMatKhau().isEmpty()) {
                TaiKhoan taiKhoan = khachHang.getTaiKhoan();
                if (taiKhoan != null) {
                    taiKhoan.setMat_khau(khachHangRequest.getMatKhau());
                    taiKhoanRepo.save(taiKhoan);
                }
            }

            khachHangRepo.save(khachHang);

            var existingDiaChiList = diaChiKhachHangRepo.findByKhachHangId(khachHang.getIdKhachHang());
            diaChiKhachHangRepo.deleteAll(existingDiaChiList);

            if (khachHangRequest.getDiaChiList() != null && !khachHangRequest.getDiaChiList().isEmpty()) {
                List<KhachHangRequest.DiaChiRequest> validDiaChiList = khachHangRequest.getDiaChiList().stream()
                        .filter(this::isValidDiaChi)
                        .collect(Collectors.toList());

                for (KhachHangRequest.DiaChiRequest diaChiReq : validDiaChiList) {
                    DiaChiKhachHang diaChiKhachHang = new DiaChiKhachHang();
                    diaChiKhachHang.setKhachHang(khachHang);
                    diaChiKhachHang.setSoNha(diaChiReq.getSoNha());
                    diaChiKhachHang.setXaPhuong(diaChiReq.getXaPhuong());
                    diaChiKhachHang.setQuanHuyen(diaChiReq.getQuanHuyen());
                    diaChiKhachHang.setTinhThanhPho(diaChiReq.getTinhThanhPho());
                    diaChiKhachHangRepo.save(diaChiKhachHang);
                }
            }

            response.put("successMessage", "Cập nhật khách hàng thành công!");
            response.put("khachHang", khachHang);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Có lỗi xảy ra khi cập nhật khách hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getKhachHangDetail(@PathVariable("id") Integer id) {
        Map<String, Object> response = new HashMap<>();

        KhachHang khachHang = khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        var diaChiList = diaChiKhachHangRepo.findByKhachHangId(khachHang.getIdKhachHang());
        String diaChiString = diaChiList.stream()
                .map(DiaChiKhachHang::getDiaChiKhachHang)
                .reduce((a, b) -> a + "; " + b)
                .orElse("Chưa có địa chỉ");

        response.put("khachHang", khachHang);
        response.put("diaChi", diaChiString);
        response.put("matKhau", khachHang.getTaiKhoan() != null ? khachHang.getTaiKhoan().getMat_khau() : "Không có mật khẩu");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chuyen-trang-thai")
    public ResponseEntity<Map<String, Object>> changeTrangThai(@RequestParam("idKhachHang") Integer idKhachHang) {
        Map<String, Object> response = new HashMap<>();

        KhachHang khachHang = khachHangRepo.findById(idKhachHang)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        if ("Đang hoạt động".equals(khachHang.getTrangThai())) {
            khachHang.setTrangThai("Không hoạt động");
        } else {
            khachHang.setTrangThai("Đang hoạt động");
        }
        khachHangRepo.save(khachHang);

        response.put("successMessage", "Thay đổi trạng thái thành công!");
        response.put("khachHang", khachHang);
        return ResponseEntity.ok(response);
    }

    private boolean isValidDiaChi(KhachHangRequest.DiaChiRequest diaChi) {
        return diaChi.getSoNha() != null && !diaChi.getSoNha().trim().isEmpty() &&
                diaChi.getTinhThanhPho() != null && !diaChi.getTinhThanhPho().trim().isEmpty() &&
                diaChi.getQuanHuyen() != null && !diaChi.getQuanHuyen().trim().isEmpty() &&
                diaChi.getXaPhuong() != null && !diaChi.getXaPhuong().trim().isEmpty();
    }
}