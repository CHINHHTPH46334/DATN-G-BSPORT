package com.example.gbsports.controller;

import com.example.gbsports.entity.DiaChiKhachHang;
import com.example.gbsports.entity.KhachHang;
import com.example.gbsports.entity.NhanVien;
import com.example.gbsports.entity.TaiKhoan;
import com.example.gbsports.repository.*;
import com.example.gbsports.request.KhachHangRequest;
import com.example.gbsports.request.LoginRequest;
import com.example.gbsports.request.RegisterRequest;
import com.example.gbsports.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
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

    @Autowired
    private RolesRepo rolesRepo;

    @Autowired
    private NhanVienRepo nhanVienRepo;

    @Autowired
    private EmailService emailServiceDK_DN;

    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> getKhachHang(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "trangThai", required = false) String trangThai,
            @RequestParam(value = "updatedId", required = false) Integer updatedId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHang> danhSachKhachHang;

        if (trangThai != null && !trangThai.isEmpty()) {
            danhSachKhachHang = khachHangRepo.locKhachHangTheoTrangThai(trangThai, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            String trimmedKeyword = keyword.trim();
            danhSachKhachHang = khachHangRepo.timKhachHang(trimmedKeyword, pageable);
        } else {
            danhSachKhachHang = khachHangRepo.findAllSortedByIdDesc(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        List<KhachHang> khachHangList = danhSachKhachHang.getContent();

        // Nếu có updatedId và đang ở trang đầu tiên, đưa khách hàng đó lên đầu
        if (updatedId != null && page == 0) {
            Optional<KhachHang> updatedKhachHangOpt = khachHangRepo.findById(updatedId);
            if (updatedKhachHangOpt.isPresent()) {
                KhachHang updatedKhachHang = updatedKhachHangOpt.get();
                khachHangList.removeIf(kh -> kh.getIdKhachHang().equals(updatedId));
                khachHangList.add(0, updatedKhachHang);
            }
        }

        if (khachHangList.isEmpty() && (keyword != null || trangThai != null)) {
            response.put("message", "Không tìm thấy khách hàng nào phù hợp!");
        }

        Map<Integer, String> diaChiMap = new HashMap<>();
        for (KhachHang kh : khachHangList) {
            var diaChiList = diaChiKhachHangRepo.findByKhachHangId(kh.getIdKhachHang());
            String diaChiMacDinh = diaChiList.stream()
                    .filter(DiaChiKhachHang::getDiaChiMacDinh)
                    .map(DiaChiKhachHang::getDiaChiKhachHang)
                    .findFirst()
                    .orElse("Chưa có địa chỉ mặc định");
            diaChiMap.put(kh.getIdKhachHang(), diaChiMacDinh);
        }

        response.put("danhSachKhachHang", khachHangList);
        response.put("diaChiMap", diaChiMap);
        response.put("currentPage", page);
        response.put("totalPages", danhSachKhachHang.getTotalPages());
        response.put("totalElements", danhSachKhachHang.getTotalElements());
        response.put("trangThai", trangThai);
        response.put("keyword", keyword);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllKH")
    public ResponseEntity<Map<String, Object>> getAllKhachHang() {
        List<KhachHang> khachHangList = khachHangRepo.findAll(Sort.by(Sort.Direction.DESC, "idKhachHang"));

        // Map để lưu địa chỉ mặc định của từng khách hàng
        Map<Integer, String> diaChiMap = new HashMap<>();
        for (KhachHang kh : khachHangList) {
            var diaChiList = diaChiKhachHangRepo.findByKhachHangId(kh.getIdKhachHang());
            String diaChiMacDinh = diaChiList.stream()
                    .filter(DiaChiKhachHang::getDiaChiMacDinh)
                    .map(DiaChiKhachHang::getDiaChiKhachHang)
                    .findFirst()
                    .orElse("Chưa có địa chỉ mặc định");
            diaChiMap.put(kh.getIdKhachHang(), diaChiMacDinh);
        }

        // Trả về response gồm danh sách khách hàng và map địa chỉ
        Map<String, Object> response = new HashMap<>();
        response.put("danhSachKhachHang", khachHangList);
        response.put("diaChiMap", diaChiMap);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addKhachHang(
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

        try {
            String maKhachHang = khachHangRequest.getMaKhachHang();
            if (maKhachHang == null || maKhachHang.trim().isEmpty()) {
                maKhachHang = generateMaKhachHang();
            } else {
                Optional<KhachHang> existingKhachHang = khachHangRepo.findByMaKhachHang(maKhachHang);
                if (existingKhachHang.isPresent()) {
                    response.put("error", "Mã khách hàng đã tồn tại!");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            khachHangRequest.setMaKhachHang(maKhachHang);

            String matKhau = generateRandomPassword();
            khachHangRequest.setMatKhau(matKhau);

            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTen_dang_nhap(khachHangRequest.getEmail());
            taiKhoan.setMat_khau(matKhau);
            taiKhoan = taiKhoanRepo.save(taiKhoan);

            KhachHang khachHang = new KhachHang();
            BeanUtils.copyProperties(khachHangRequest, khachHang);
            khachHang.setTaiKhoan(taiKhoan);
            khachHang = khachHangRepo.save(khachHang);

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
                    diaChiKhachHang.setDiaChiMacDinh(diaChiReq.getDiaChiMacDinh() != null && diaChiReq.getDiaChiMacDinh());
                    diaChiKhachHangRepo.save(diaChiKhachHang);
                }
            }

            String subject = "Chào mừng bạn đến với GB Sports!";
            String body = "<!DOCTYPE html>" +
                    "<html lang='vi'>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                    ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }" +
                    ".header { background-color: #28a745; color: #ffffff; padding: 20px; text-align: center; border-top-left-radius: 10px; border-top-right-radius: 10px; }" +
                    ".header h1 { margin: 0; font-size: 24px; }" +
                    ".content { padding: 20px; }" +
                    ".content h3 { margin: 0 0 10px; font-size: 20px; }" +
                    ".info-box { background-color: #e6f4ea; border-left: 5px solid #28a745; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                    ".info-box p { margin: 5px 0; }" +
                    ".footer { text-align: center; padding: 10px; font-size: 14px; color: #666; }" +
                    ".footer a { color: #007bff; text-decoration: none; }" +
                    ".footer a:hover { text-decoration: underline; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<h1>Chào mừng bạn đến với G&B SPORTS</h1>" +
                    "</div>" +
                    "<div class='content'>" +
                    "<h3>Xin chào " + khachHang.getTenKhachHang() + ",</h3>" +
                    "<p>Cảm ơn bạn đã đăng ký tài khoản tại G&B SPORTS. Tài khoản của bạn đã được tạo thành công!</p>" +
                    "<div class='info-box'>" +
                    "<p><strong>Thông tin đăng nhập của bạn:</strong></p>" +
                    "<p><strong>Tên đăng nhập:</strong> " + taiKhoan.getTen_dang_nhap() + "</p>" +
                    "<p><strong>Mật khẩu:</strong> " + matKhau + "</p>" +
                    "</div>" +
                    "<p>Vui lòng đăng nhập để bắt đầu sử dụng dịch vụ và khám phá các ưu đãi hấp dẫn.</p>" +
                    "</div>" +
                    "<div class='footer'>" +
                    "<p>Trân trọng,<br>Đội ngũ G&B SPORTS</p>" +
                    "<p><a href='http://localhost:5173/home'>Ghé thăm website của chúng tôi</a> | <a href='mailto:support@gbsports.com'>Liên hệ hỗ trợ</a></p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

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
    @GetMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> getKhachHangForEdit(@PathVariable("id") Integer id) {
        Map<String, Object> response = new HashMap<>();

        Optional<KhachHang> khachHangOpt = khachHangRepo.findById(id);
        if (!khachHangOpt.isPresent()) {
            response.put("error", "Không tìm thấy khách hàng với ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        KhachHang khachHang = khachHangOpt.get();
        KhachHangRequest request = new KhachHangRequest();
        BeanUtils.copyProperties(khachHang, request);

        var diaChiList = diaChiKhachHangRepo.findByKhachHangId(khachHang.getIdKhachHang());
        for (DiaChiKhachHang diaChi : diaChiList) {
            KhachHangRequest.DiaChiRequest diaChiReq = new KhachHangRequest.DiaChiRequest();
            diaChiReq.setSoNha(diaChi.getSoNha());
            diaChiReq.setXaPhuong(diaChi.getXaPhuong());
            diaChiReq.setQuanHuyen(diaChi.getQuanHuyen());
            diaChiReq.setTinhThanhPho(diaChi.getTinhThanhPho());
            diaChiReq.setDiaChiMacDinh(diaChi.getDiaChiMacDinh());
            request.getDiaChiList().add(diaChiReq);
        }

        if (khachHang.getTaiKhoan() != null) {
            request.setMatKhau(khachHang.getTaiKhoan().getMat_khau());
        }

        response.put("khachHang", request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateKhachHang(@RequestBody KhachHangRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (request.getNgaySinh() == null) {
            response.put("error", "Ngày sinh không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        LocalDate ngaySinh = request.getNgaySinh().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (Period.between(ngaySinh, LocalDate.now()).getYears() < 13) {
            response.put("error", "Khách hàng phải từ 13 tuổi trở lên!");
            return ResponseEntity.badRequest().body(response);
        }

        KhachHang khachHang = khachHangRepo.findById(request.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        if (!khachHang.getMaKhachHang().equals(request.getMaKhachHang())) {
            Optional<KhachHang> existing = khachHangRepo.findByMaKhachHang(request.getMaKhachHang());
            if (existing.isPresent()) {
                response.put("error", "Mã khách hàng đã tồn tại!");
                return ResponseEntity.badRequest().body(response);
            }
        }

        try {
            BeanUtils.copyProperties(request, khachHang);
            khachHang = khachHangRepo.save(khachHang);

            var existingDiaChiList = diaChiKhachHangRepo.findByKhachHangId(khachHang.getIdKhachHang());
            diaChiKhachHangRepo.deleteAll(existingDiaChiList);

            if (request.getDiaChiList() != null && !request.getDiaChiList().isEmpty()) {
                List<KhachHangRequest.DiaChiRequest> validDiaChiList = request.getDiaChiList().stream()
                        .filter(this::isValidDiaChi)
                        .collect(Collectors.toList());
                for (KhachHangRequest.DiaChiRequest diaChiReq : validDiaChiList) {
                    DiaChiKhachHang diaChi = new DiaChiKhachHang();
                    diaChi.setKhachHang(khachHang);
                    BeanUtils.copyProperties(diaChiReq, diaChi);
                    diaChiKhachHangRepo.save(diaChi);
                }
            }

            response.put("message", "Cập nhật khách hàng thành công!");
            response.put("khachHang", khachHang);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Lỗi khi cập nhật khách hàng: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getKhachHangDetail(@PathVariable("id") Integer id) {
        Map<String, Object> response = new HashMap<>();

        KhachHang khachHang = khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Lấy toàn bộ danh sách địa chỉ của khách hàng
        List<DiaChiKhachHang> diaChiList = diaChiKhachHangRepo.findByKhachHangId(khachHang.getIdKhachHang());

        response.put("khachHang", khachHang);
        response.put("diaChiList", diaChiList); // Trả về danh sách địa chỉ đầy đủ
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

    private String generateMaKhachHang() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; // Tập hợp ký tự: chữ và số
        Random random = new Random();
        StringBuilder maKhachHang = new StringBuilder("KH"); // Tiền tố KH

        // Sinh 6 ký tự ngẫu nhiên (chữ hoặc số xen lẫn)
        for (int i = 0; i < 6; i++) {
            maKhachHang.append(chars.charAt(random.nextInt(chars.length())));
        }

        // Kiểm tra trùng lặp, nếu trùng thì sinh lại
        String newMaKhachHang = maKhachHang.toString();
        while (khachHangRepo.findByMaKhachHang(newMaKhachHang).isPresent()) {
            maKhachHang = new StringBuilder("KH");
            for (int i = 0; i < 6; i++) {
                maKhachHang.append(chars.charAt(random.nextInt(chars.length())));
            }
            newMaKhachHang = maKhachHang.toString();
        }

        return newMaKhachHang;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    private boolean isValidDiaChi(KhachHangRequest.DiaChiRequest diaChi) {
        return diaChi.getSoNha() != null && !diaChi.getSoNha().trim().isEmpty() &&
                diaChi.getTinhThanhPho() != null && !diaChi.getTinhThanhPho().trim().isEmpty() &&
                diaChi.getQuanHuyen() != null && !diaChi.getQuanHuyen().trim().isEmpty() &&
                diaChi.getXaPhuong() != null && !diaChi.getXaPhuong().trim().isEmpty();
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerKhachHang(
            @Valid @RequestBody RegisterRequest registerRequest,
            BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        // Kiểm tra validation từ DTO
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            }
            response.put("fieldErrors", fieldErrors);
            return ResponseEntity.badRequest().body(response);
        }
        // Kiểm tra xác nhận mật khẩu
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            Map<String, String> fieldErrors = new HashMap<>();
            fieldErrors.put("confirmPassword", "Mật khẩu xác nhận không khớp");
            response.put("fieldErrors", fieldErrors);
            return ResponseEntity.badRequest().body(response);
        }
        // Kiểm tra tuổi >= 14
        LocalDate ngaySinh = registerRequest.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        int tuoi = Period.between(ngaySinh, now).getYears();
        if (tuoi < 14) {
            Map<String, String> fieldErrors = new HashMap<>();
            fieldErrors.put("birthDate", "Bạn phải đủ 14 tuổi để đăng ký");
            response.put("fieldErrors", fieldErrors);
            return ResponseEntity.badRequest().body(response);
        }
        try {
            // Kiểm tra email đã tồn tại
            Optional<TaiKhoan> existingTaiKhoan = taiKhoanRepo.findByTenDangNhapAndKhachHangRole(registerRequest.getEmail());
            if (existingTaiKhoan.isPresent()) {
                response.put("error", "Email đã được sử dụng!");
                return ResponseEntity.badRequest().body(response);
            }
            // Tạo tài khoản
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTen_dang_nhap(registerRequest.getEmail());
            taiKhoan.setMat_khau(registerRequest.getPassword());
            taiKhoan.setRoles(rolesRepo.findById(4).get()); // Gán id_roles = 4 cho khách hàng
            taiKhoan = taiKhoanRepo.save(taiKhoan);

            // Tạo mã khách hàng tự động
            String maKhachHang = generateMaKhachHang();

            // Tạo khách hàng
            KhachHang khachHang = new KhachHang();
            khachHang.setMaKhachHang(maKhachHang);
            khachHang.setTenKhachHang(registerRequest.getFullName());
            khachHang.setSoDienThoai(registerRequest.getPhone());
            khachHang.setEmail(registerRequest.getEmail());
            khachHang.setNgaySinh(registerRequest.getBirthDate());
            khachHang.setTrangThai("Đang hoạt động");

            // Xử lý giới tính
            if ("Nam".equals(registerRequest.getGender())) {
                khachHang.setGioiTinh(true);
            } else if ("Nữ".equals(registerRequest.getGender())) {
                khachHang.setGioiTinh(false);
            } else {
                khachHang.setGioiTinh(null); // "Khác" sẽ để null
            }

            khachHang.setTaiKhoan(taiKhoan);
            khachHang = khachHangRepo.save(khachHang);

            // Gửi email chào mừng
            String subject = "Chào mừng bạn đến với G&B SPORTS 🎉";
            String body = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                    ".container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #f9f9f9; }" +
                    ".header { background-color: #4CAF50; color: white; padding: 15px; text-align: center; border-radius: 10px 10px 0 0; }" +
                    ".content { padding: 20px; background-color: white; border-radius: 0 0 10px 10px; }" +
                    ".highlight { color: #4CAF50; font-weight: bold; }" +
                    ".info-box { background-color: #e8f5e9; padding: 15px; border-left: 5px solid #4CAF50; margin: 15px 0; }" +
                    ".footer { text-align: center; margin-top: 20px; font-size: 14px; color: #777; }" +
                    "a { color: #4CAF50; text-decoration: none; }" +
                    "a:hover { text-decoration: underline; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<h2>Chào mừng bạn đến với G&B SPORTS 🎉</h2>" +
                    "</div>" +
                    "<div class='content'>" +
                    "<h3>Xin chào <span class='highlight'>" + khachHang.getTenKhachHang() + "</span>,</h3>" +
                    "<p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>G&B SPORTS</strong>. Tài khoản của bạn đã được tạo thành công!</p>" +
                    "<div class='info-box'>" +
                    "<h4>Thông tin đăng nhập của bạn:</h4>" +
                    "<ul>" +
                    "<li>Tên đăng nhập: <strong>" + taiKhoan.getTen_dang_nhap() + "</strong></li>" +
                    "<li>Mật khẩu: <strong>" + registerRequest.getPassword() + "</strong></li>" +
                    "</ul>" +
                    "</div>" +
//                    "<p style='color: #d32f2f; font-weight: bold;'>🎁 ƯU ĐÃI ĐẶC BIỆT: GIẢM 20% CHO ĐƠN HÀNG ĐẦU TIÊN!</p>" +
                    "<p>Vui lòng <a href='http://localhost:5173/login-register/login'>đăng nhập</a> để bắt đầu sử dụng dịch vụ và khám phá các ưu đãi hấp dẫn.</p>" +
                    "</div>" +
                    "<div class='footer'>" +
                    "<p>Trân trọng,<br>Đội ngũ G&B SPORTS</p>" +
                    "<p><a href='http://localhost:5173/home'>Ghé thăm website của chúng tôi</a> | <a href='mailto:support@gbsports.com'>Liên hệ hỗ trợ</a></p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            try {
                emailServiceDK_DN.sendEmail(khachHang.getEmail(), subject, body);
                response.put("emailMessage", "Email chào mừng đã được gửi thành công!");
            } catch (MessagingException e) {
                response.put("warning", "Đăng ký thành công nhưng gửi email thất bại: " + e.getMessage());
            }

            response.put("successMessage", "Đăng ký thành công!");
            response.put("khachHang", khachHang);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Có lỗi xảy ra khi đăng ký: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            BindingResult result) {

        Map<String, Object> response = new HashMap<>();

        // Kiểm tra validation từ Request
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            }
            response.put("fieldErrors", fieldErrors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Tìm tất cả tài khoản theo email (ten_dang_nhap)
            List<TaiKhoan> taiKhoanList = taiKhoanRepo.findAllByTenDangNhap(loginRequest.getEmail());
            if (taiKhoanList.isEmpty()) {
                response.put("error", "Email không tồn tại!");
                return ResponseEntity.badRequest().body(response);
            }

            // Duyệt qua danh sách tài khoản để tìm tài khoản hợp lệ
            TaiKhoan taiKhoanDangNhap = null;

            for (TaiKhoan taiKhoan : taiKhoanList) {
                // Kiểm tra mật khẩu
                if (taiKhoan.getMat_khau().equals(loginRequest.getPassword())) {
                    // Kiểm tra trạng thái tài khoản
                    if (taiKhoan.getRoles().getId_roles() == 4) {
                        // Tài khoản khách hàng
                        Optional<KhachHang> khachHangOpt = khachHangRepo.findByTaiKhoanIdTaiKhoan(taiKhoan.getId_tai_khoan());
                        if (khachHangOpt.isPresent()) {
                            KhachHang khachHang = khachHangOpt.get();
                            if ("Ngừng hoạt động".equals(khachHang.getTrangThai())) {
                                response.put("error", "Tài khoản của bạn đã bị ngừng hoạt động!");
                                return ResponseEntity.badRequest().body(response);
                            }
                            taiKhoanDangNhap = taiKhoan; // Tài khoản hợp lệ
                            break;
                        }
                    } else {
                        // Tài khoản người dùng (Admin, Quản lý, Nhân viên)
                        Optional<NhanVien> nhanVienOpt = nhanVienRepo.findByTaiKhoanIdTaiKhoan(taiKhoan.getId_tai_khoan());
                        if (nhanVienOpt.isPresent()) {
                            NhanVien nhanVien = nhanVienOpt.get();
                            if ("Ngừng hoạt động".equals(nhanVien.getTrangThai())) {
                                response.put("error", "Tài khoản của bạn đã bị ngừng hoạt động!");
                                return ResponseEntity.badRequest().body(response);
                            }
                            taiKhoanDangNhap = taiKhoan; // Tài khoản hợp lệ
                            break;
                        }
                    }
                }
            }
            // Kiểm tra nếu không tìm thấy tài khoản hợp lệ
            if (taiKhoanDangNhap == null) {
                response.put("error", "Mật khẩu không đúng !");
                return ResponseEntity.badRequest().body(response);
            }
            // Đăng nhập thành công, trả về thông tin tài khoản
            response.put("successMessage", "Đăng nhập thành công!");
            response.put("taiKhoan", taiKhoanDangNhap);
            response.put("id_roles", taiKhoanDangNhap.getRoles().getId_roles());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Có lỗi xảy ra khi đăng nhập: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/details")
    public ResponseEntity<KhachHang> getKhachHangDetails(@RequestParam String tenDangNhap) {
        Optional<KhachHang> khachHang = taiKhoanRepo.findKhachHangByTenDangNhap(tenDangNhap);
        if (khachHang.isPresent()) {
            System.out.println("Thông tin khách hàng tìm được: " + khachHang.get());
        } else {
            System.out.println("Không tìm thấy khách hàng với ten_dang_nhap: " + tenDangNhap);
        }
        return khachHang.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}