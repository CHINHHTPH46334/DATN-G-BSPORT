package com.example.gbsports.controller;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.request.*;
import com.example.gbsports.response.HoaDonResponse;
import com.example.gbsports.service.EmailService;
import com.example.gbsports.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173/", allowedHeaders = "*", methods = { RequestMethod.GET,
        RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT })
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
    private HoaDonRepo hoaDonRepo;

    @Autowired
    private EmailService emailServiceDK_DN;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LichSuDangNhapRepo lichSuDangNhapRepo;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV', 'ROLE_KH')")
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
    public ResponseEntity<Map<String, Object>> addKhachHang(@RequestBody KhachHangRequest khachHangRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Kiểm tra email đã tồn tại
            Optional<TaiKhoan> existingTaiKhoan = taiKhoanRepo.findByTenDangNhap(khachHangRequest.getEmail());
            if (existingTaiKhoan.isPresent()) {
                response.put("error", "Email đã được sử dụng!");
                return ResponseEntity.badRequest().body(response);
            }

            // Sinh mã khách hàng tự động nếu không có
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

            // Lưu tài khoản
            String matKhau = generateRandomPassword();
            khachHangRequest.setMatKhau(matKhau);

            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTen_dang_nhap(khachHangRequest.getEmail());
            taiKhoan.setMat_khau(passwordEncoder.encode(matKhau));
            taiKhoan.setRoles(rolesRepo.findById(4).orElseThrow(() -> new RuntimeException("Role không tồn tại")));
            taiKhoan = taiKhoanRepo.save(taiKhoan);

            // Lưu khách hàng
            KhachHang khachHang = new KhachHang();
            BeanUtils.copyProperties(khachHangRequest, khachHang);
            khachHang.setNgayTao(LocalDateTime.now());
            khachHang.setTaiKhoan(taiKhoan);
            khachHang = khachHangRepo.save(khachHang);

            // Lưu địa chỉ
            if (khachHangRequest.getDiaChiList() != null && !khachHangRequest.getDiaChiList().isEmpty()) {
                for (KhachHangRequest.DiaChiRequest diaChiReq : khachHangRequest.getDiaChiList()) {
                    DiaChiKhachHang diaChiKhachHang = new DiaChiKhachHang();
                    diaChiKhachHang.setKhachHang(khachHang);
                    BeanUtils.copyProperties(diaChiReq, diaChiKhachHang);
                    diaChiKhachHangRepo.save(diaChiKhachHang);
                }
            }

            // Gửi email chào mừng
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

    @PostMapping("/addKHMoi")
    public ResponseEntity<Map<String, Object>> addKhachHangNhanh(
            @Valid @RequestBody KhachHangRequest khachHangRequest,
            BindingResult result) {

        Map<String, Object> response = new HashMap<>();

        khachHangRequest.setTrangThai("Đang hoạt động");
        khachHangRequest.setGioiTinh(true);
        khachHangRequest.setNgaySinh(new Date());
        try {
            // Kiểm tra email đã tồn tại
            Optional<TaiKhoan> existingTaiKhoan = taiKhoanRepo.findByTenDangNhap(khachHangRequest.getEmail());
            if (existingTaiKhoan.isPresent()) {
                response.put("error", "Email đã được sử dụng!");
                return ResponseEntity.badRequest().body(response);
            }

            // Sinh mã khách hàng tự động nếu không có
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

            // Lưu tài khoản
            String matKhau = generateRandomPassword();
            khachHangRequest.setMatKhau(matKhau);

            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTen_dang_nhap(khachHangRequest.getEmail());
            taiKhoan.setMat_khau(passwordEncoder.encode(matKhau));
            taiKhoan.setRoles(rolesRepo.findById(4).orElseThrow(() -> new RuntimeException("Role không tồn tại")));
            taiKhoan = taiKhoanRepo.save(taiKhoan);

            // Lưu khách hàng
            KhachHang khachHang = new KhachHang();
            BeanUtils.copyProperties(khachHangRequest, khachHang);
            khachHang.setNgayTao(LocalDateTime.now());
            khachHang.setTaiKhoan(taiKhoan);
            khachHang = khachHangRepo.save(khachHang);

            // Lưu địa chỉ
            if (khachHangRequest.getDiaChiList() != null && !khachHangRequest.getDiaChiList().isEmpty()) {
                for (KhachHangRequest.DiaChiRequest diaChiReq : khachHangRequest.getDiaChiList()) {
                    DiaChiKhachHang diaChiKhachHang = new DiaChiKhachHang();
                    diaChiKhachHang.setKhachHang(khachHang);
                    BeanUtils.copyProperties(diaChiReq, diaChiKhachHang);
                    diaChiKhachHangRepo.save(diaChiKhachHang);
                }
            }

            // Gửi email chào mừng
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

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_KH')")
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateKhachHang(@RequestBody KhachHangRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Kiểm tra khách hàng tồn tại
            KhachHang khachHang = khachHangRepo.findById(request.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

            // Kiểm tra mã khách hàng trùng lặp (nếu thay đổi)
            if (!khachHang.getMaKhachHang().equals(request.getMaKhachHang())) {
                Optional<KhachHang> existing = khachHangRepo.findByMaKhachHang(request.getMaKhachHang());
                if (existing.isPresent()) {
                    response.put("error", "Mã khách hàng đã tồn tại!");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // Cập nhật thông tin khách hàng
            BeanUtils.copyProperties(request, khachHang);
            khachHang = khachHangRepo.save(khachHang);

            // Xóa địa chỉ cũ
            var existingDiaChiList = diaChiKhachHangRepo.findByKhachHangId(khachHang.getIdKhachHang());
            diaChiKhachHangRepo.deleteAll(existingDiaChiList);

            // Lưu địa chỉ mới
            if (request.getDiaChiList() != null && !request.getDiaChiList().isEmpty()) {
                for (KhachHangRequest.DiaChiRequest diaChiReq : request.getDiaChiList()) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getKhachHangDetail(@PathVariable("id") Integer id) {
        Map<String, Object> response = new HashMap<>();

        KhachHang khachHang = khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        System.out.println("🔍 Ngày tạo gửi về JSON: " + khachHang.getNgayTao());
        // Lấy toàn bộ danh sách địa chỉ của khách hàng
        List<DiaChiKhachHang> diaChiList = diaChiKhachHangRepo.findByKhachHangId(khachHang.getIdKhachHang());

        response.put("khachHang", khachHang);
        response.put("diaChiList", diaChiList); // Trả về danh sách địa chỉ đầy đủ
        response.put("matKhau",
                khachHang.getTaiKhoan() != null ? khachHang.getTaiKhoan().getMat_khau() : "Không có mật khẩu");
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
            Optional<TaiKhoan> existingTaiKhoan = taiKhoanRepo
                    .findByTenDangNhapAndKhachHangRole(registerRequest.getEmail());
            if (existingTaiKhoan.isPresent()) {
                response.put("error", "Email đã được sử dụng!");
                return ResponseEntity.badRequest().body(response);
            }
            // Tạo tài khoản
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTen_dang_nhap(registerRequest.getEmail());
            taiKhoan.setMat_khau(passwordEncoder.encode(registerRequest.getPassword())); // Mã hóa mật khẩu
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
                    ".container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #f9f9f9; }"
                    +
                    ".header { background-color: #4CAF50; color: white; padding: 15px; text-align: center; border-radius: 10px 10px 0 0; }"
                    +
                    ".content { padding: 20px; background-color: white; border-radius: 0 0 10px 10px; }" +
                    ".highlight { color: #4CAF50; font-weight: bold; }" +
                    ".info-box { background-color: #e8f5e9; padding: 15px; border-left: 5px solid #4CAF50; margin: 15px 0; }"
                    +
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
                    "<p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>G&B SPORTS</strong>. Tài khoản của bạn đã được tạo thành công!</p>"
                    +
                    "<div class='info-box'>" +
                    "<h4>Thông tin đăng nhập của bạn:</h4>" +
                    "<ul>" +
                    "<li>Tên đăng nhập: <strong>" + taiKhoan.getTen_dang_nhap() + "</strong></li>" +
                    "<li>Mật khẩu: <strong>" + registerRequest.getPassword() + "</strong></li>" +
                    "</ul>" +
                    "</div>" +
                    "<p>Vui lòng <a href='http://localhost:5173/login-register/login'>đăng nhập</a> để bắt đầu sử dụng dịch vụ và khám phá các ưu đãi hấp dẫn.</p>"
                    +
                    "</div>" +
                    "<div class='footer'>" +
                    "<p>Trân trọng,<br>Đội ngũ G&B SPORTS</p>" +
                    "<p><a href='http://localhost:5173/home'>Ghé thăm website của chúng tôi</a> | <a href='mailto:support@gbsports.com'>Liên hệ hỗ trợ</a></p>"
                    +
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
            BindingResult result,
            HttpServletRequest request) {

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
            // Tìm tài khoản trước để kiểm tra trạng thái
            TaiKhoan taiKhoan = taiKhoanRepo.findByTenDangNhapAndKhachHangRole(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            // Tài khoản khách hàng
            Optional<KhachHang> khachHangOpt = khachHangRepo.findByTaiKhoanIdTaiKhoan(taiKhoan.getId_tai_khoan());
            if (khachHangOpt.isPresent()) {
                KhachHang khachHang = khachHangOpt.get();
                if ("Ngừng hoạt động".equals(khachHang.getTrangThai())) {
                    response.put("error", "Tài khoản của bạn đã bị ngừng hoạt động!");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            // Xác thực người dùng bằng AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // Tạo JWT token
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            String token = jwtUtil.generateToken(userDetails);
            List<String> roles = jwtUtil.extractRoles(token);

            // Lấy địa chỉ IP từ request
            String ipAddress = request.getRemoteAddr();
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = "Unknown";
            }

            // Lưu lịch sử đăng nhập
            LichSuDangNhap lichSuDangNhap = new LichSuDangNhap();
            lichSuDangNhap.setTaiKhoan(taiKhoan);
            lichSuDangNhap.setNgay_dang_nhap(LocalDateTime.now());
            lichSuDangNhap.setIp_adress(ipAddress);
            lichSuDangNhapRepo.save(lichSuDangNhap);

            // Trả về thông tin đăng nhập
            response.put("successMessage", "Đăng nhập thành công!");
            response.put("token", token);
            response.put("taiKhoan", taiKhoan);
            response.put("id_roles", taiKhoan.getRoles().getId_roles());
            response.put("roles", roles);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Tên đăng nhập hoặc mật khẩu không đúng! ");
            return ResponseEntity.badRequest().body(response);
        }
    }

//    @GetMapping("/details")
//    public ResponseEntity<KhachHang> getKhachHangDetails(@RequestParam String tenDangNhap) {
//        Optional<KhachHang> khachHang = taiKhoanRepo.findKhachHangByTenDangNhap(tenDangNhap);
//        if (khachHang.isPresent()) {
//            System.out.println("Thông tin khách hàng tìm được: " + khachHang.get());
//        } else {
//            System.out.println("Không tìm thấy khách hàng với ten_dang_nhap: " + tenDangNhap);
//        }
//        return khachHang.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DoiMKRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Lấy username từ token
        String token = authHeader.substring(7); // Bỏ "Bearer "
        String username = jwtUtil.extractUsername(token);

        // Lấy vai trò từ token
        List<String> rolesFromToken = jwtUtil.extractRoles(token);

        // Tìm tất cả tài khoản liên quan đến email (cả khách hàng và nhân viên)
        List<TaiKhoan> taiKhoanList = taiKhoanRepo.findAllByTenDangNhap(username);
        if (taiKhoanList.isEmpty()) {
            response.put("error", "Tài khoản không tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }

        // Xác định tài khoản cần đổi mật khẩu dựa trên vai trò từ token
        TaiKhoan taiKhoanToUpdate = null;
        for (TaiKhoan taiKhoan : taiKhoanList) {
            String roleFromTaiKhoan = taiKhoan.getRoles().getMa_roles();
            if (rolesFromToken.contains(roleFromTaiKhoan)) {
                taiKhoanToUpdate = taiKhoan;
                break;
            }
        }

        if (taiKhoanToUpdate == null) {
            response.put("error", "Không tìm thấy tài khoản phù hợp với vai trò của bạn!");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra trạng thái tài khoản
        boolean isActive = false;
        if (taiKhoanToUpdate.getRoles().getId_roles() == 4) { // Khách hàng
            Optional<KhachHang> khachHangOpt = khachHangRepo
                    .findByTaiKhoanIdTaiKhoan(taiKhoanToUpdate.getId_tai_khoan());
            if (khachHangOpt.isPresent() && "Đang hoạt động".equals(khachHangOpt.get().getTrangThai())) {
                isActive = true;
            }
        } else { // Nhân viên (id_roles = 1, 2, 3)
            Optional<NhanVien> nhanVienOpt = nhanVienRepo.findByTaiKhoanIdTaiKhoan(taiKhoanToUpdate.getId_tai_khoan());
            if (nhanVienOpt.isPresent() && "Đang hoạt động".equals(nhanVienOpt.get().getTrangThai())) {
                isActive = true;
            }
        }

        if (!isActive) {
            response.put("error", "Tài khoản của bạn đã bị ngừng hoạt động!");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), taiKhoanToUpdate.getMat_khau())) {
            response.put("error", "Mật khẩu cũ không đúng!");
            return ResponseEntity.badRequest().body(response);
        }

        // Cập nhật mật khẩu mới cho tài khoản đã chọn
        taiKhoanToUpdate.setMat_khau(passwordEncoder.encode(request.getNewPassword()));
        taiKhoanRepo.save(taiKhoanToUpdate);

        response.put("successMessage", "Đổi mật khẩu thành công!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody QuenMKRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Tìm tài khoản khách hàng theo email
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepo.findByTenDangNhapAndKhachHangRole(request.getEmail());
        if (!taiKhoanOpt.isPresent()) {
            response.put("error", "Tài khoản không tồn tại trong hệ thống!");
            return ResponseEntity.badRequest().body(response);
        }

        TaiKhoan taiKhoan = taiKhoanOpt.get();
        Optional<KhachHang> khachHangOpt = khachHangRepo.findByTaiKhoanIdTaiKhoan(taiKhoan.getId_tai_khoan());
        if (khachHangOpt.isPresent() && !"Đang hoạt động".equals(khachHangOpt.get().getTrangThai())) {
            response.put("error", "Tài khoản của bạn đã bị ngừng hoạt động!");
            return ResponseEntity.badRequest().body(response);
        }
        // Tạo reset token
        String resetToken = jwtUtil.generateResetToken(request.getEmail());
        // Tạo liên kết đặt lại mật khẩu
        String resetLink = "http://localhost:5173/login-register/login?token=" + resetToken;
        // Gửi email
        String emailContent = "<!DOCTYPE html>" +
                "<html lang='vi'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }" +
                ".header { background-color: #d02c39; color: white; padding: 20px; text-align: center; border-top-left-radius: 10px; border-top-right-radius: 10px; }" +
                ".header h1 { margin: 0; font-size: 24px; }" +
                ".content { padding: 20px; }" +
                ".content h3 { margin: 0 0 10px; font-size: 20px; }" +
                ".info-box { background-color: #fff5f5; border-left: 5px solid #d02c39; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                ".info-box p { margin: 5px 0; }" +
                ".footer { text-align: center; padding: 10px; font-size: 14px; color: #666; }" +
                ".footer a { color: #d02c39; text-decoration: none; }" +
                ".footer a:hover { text-decoration: underline; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>Đặt lại mật khẩu - G&B SPORTS</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<h3>Xin chào,</h3>" +
                "<p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản khách hàng tại G&B SPORTS.</p>" +
                "<p>Vui lòng nhấp vào liên kết sau để đặt lại mật khẩu:</p>" +
                "<div class='info-box'>" +
                "<p><a href='" + resetLink + "'>Đặt lại mật khẩu</a></p>" +
                "</div>" +
                "<p>Liên kết này có hiệu lực trong 1 giờ. Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Trân trọng,<br>Đội ngũ G&B SPORTS</p>" +
                "<p><a href='http://localhost:5173/home'>Ghé thăm website</a> | <a href='mailto:support@gbsports.com'>Liên hệ hỗ trợ</a></p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        try {
            emailService.sendEmail(request.getEmail(), "Đặt lại mật khẩu - G&B SPORTS", emailContent);
            response.put("successMessage", "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn!");
        } catch (MessagingException e) {
            response.put("warning", "Yêu cầu đặt lại mật khẩu thành công nhưng gửi email thất bại: " + e.getMessage());
        }
//        response.put("successMessage", "Email hợp lệ, vui lòng nhập mật khẩu mới.");
//        response.put("resetToken", resetToken); // Thêm token vào phản hồi
        return ResponseEntity.ok(response);
    }

    // Kiểm tra token (GET)
    @GetMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> validateResetToken(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = jwtUtil.validateResetTokenAndGetEmail(token);
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepo.findByTenDangNhapAndKhachHangRole(email);
            if (!taiKhoanOpt.isPresent()) {
                response.put("error", "Tài khoản không tồn tại!");
                return ResponseEntity.badRequest().body(response);
            }

            TaiKhoan taiKhoan = taiKhoanOpt.get();
            Optional<KhachHang> khachHangOpt = khachHangRepo.findByTaiKhoanIdTaiKhoan(taiKhoan.getId_tai_khoan());
            if (khachHangOpt.isPresent() && !"Đang hoạt động".equals(khachHangOpt.get().getTrangThai())) {
                response.put("error", "Tài khoản của bạn đã bị ngừng hoạt động!");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("successMessage", "Token hợp lệ, vui lòng nhập mật khẩu mới.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", "Token không hợp lệ: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody ResetMKRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Xác thực reset token và lấy email
        String email;
        try {
            email = jwtUtil.validateResetTokenAndGetEmail(request.getToken());
        } catch (Exception e) {
            response.put("error", "Token không hợp lệ hoặc đã hết hạn! " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        // Tìm tài khoản khách hàng theo email
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepo.findByTenDangNhapAndKhachHangRole(email);
        if (!taiKhoanOpt.isPresent()) {
            response.put("error", "Tài khoản không tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }

        TaiKhoan taiKhoan = taiKhoanOpt.get();
        Optional<KhachHang> khachHangOpt = khachHangRepo.findByTaiKhoanIdTaiKhoan(taiKhoan.getId_tai_khoan());
        if (khachHangOpt.isPresent() && !"Đang hoạt động".equals(khachHangOpt.get().getTrangThai())) {
            response.put("error", "Tài khoản của bạn đã bị ngừng hoạt động!");
            return ResponseEntity.badRequest().body(response);
        }

        // Cập nhật mật khẩu cho tài khoản khách hàng
        taiKhoan.setMat_khau(passwordEncoder.encode(request.getNewPassword()));
        taiKhoanRepo.save(taiKhoan);

        response.put("successMessage", "Đặt lại mật khẩu thành công!");
        return ResponseEntity.ok(response);
    }


    ////dia chi cua lenh
    @GetMapping("/details")
    public ResponseEntity<KhachHang> getKhachHangDetails(@RequestParam String tenDangNhap) {
        Optional<KhachHang> khachHang = taiKhoanRepo.findKhachHangByTenDangNhap(tenDangNhap);
        if (khachHang.isPresent()) {
            KhachHang kh = khachHang.get();
            System.out.println("Thông tin khách hàng tìm được: " + kh);
            System.out.println("🔍 Khách hàng tìm được:");
            System.out.println(" - Ngày tạo (ngayTao): " + kh.getNgayTao());
        } else {
            System.out.println("Không tìm thấy khách hàng với ten_dang_nhap: " + tenDangNhap);
        }
        return khachHang.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/dia-chi/add")
    public ResponseEntity<Map<String, Object>> addDiaChi(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer idKhachHang = Integer.parseInt(request.get("idKhachHang").toString());
            String soNha = (String) request.get("soNha");
            String xaPhuong = (String) request.get("xaPhuong");
            String quanHuyen = (String) request.get("quanHuyen");
            String tinhThanhPho = (String) request.get("tinhThanhPho");
            Boolean diaChiMacDinh = (Boolean) request.getOrDefault("diaChiMacDinh", false);

            // Validate input
            if (soNha == null || xaPhuong == null || quanHuyen == null || tinhThanhPho == null) {
                response.put("error", true);
                response.put("message", "Vui lòng điền đầy đủ thông tin địa chỉ");
                return ResponseEntity.badRequest().body(response);
            }

            // Find customer
            Optional<KhachHang> khachHangOpt = khachHangRepo.findById(idKhachHang);
            if (khachHangOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Không tìm thấy thông tin khách hàng");
                return ResponseEntity.status(404).body(response);
            }

            KhachHang khachHang = khachHangOpt.get();

            // Lấy danh sách địa chỉ hiện tại của khách
            List<DiaChiKhachHang> existingAddresses = diaChiKhachHangRepo.findByKhachHangId(idKhachHang);

            if (existingAddresses.isEmpty()) {
                // Nếu là địa chỉ đầu tiên => luôn đặt là mặc định
                diaChiMacDinh = true;
            } else if (diaChiMacDinh) {
                // Nếu user chọn đặt mặc định thì unset tất cả địa chỉ cũ
                for (DiaChiKhachHang addr : existingAddresses) {
                    addr.setDiaChiMacDinh(false);
                    diaChiKhachHangRepo.save(addr);
                }
            }

            // Tạo mới địa chỉ
            DiaChiKhachHang diaChi = new DiaChiKhachHang();
            diaChi.setKhachHang(khachHang);
            diaChi.setSoNha(soNha);
            diaChi.setXaPhuong(xaPhuong);
            diaChi.setQuanHuyen(quanHuyen);
            diaChi.setTinhThanhPho(tinhThanhPho);
            diaChi.setDiaChiMacDinh(diaChiMacDinh);

            // Lưu vào DB
            diaChi = diaChiKhachHangRepo.save(diaChi);

            response.put("success", true);
            response.put("message", "Thêm địa chỉ thành công");
            response.put("diaChi", diaChi);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Cập nhật địa chỉ
    @PutMapping("/dia-chi/update")
    public ResponseEntity<Map<String, Object>> updateDiaChi(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer idDiaChi = Integer.parseInt(request.get("idDiaChi").toString());
            String soNha = (String) request.get("soNha");
            String xaPhuong = (String) request.get("xaPhuong");
            String quanHuyen = (String) request.get("quanHuyen");
            String tinhThanhPho = (String) request.get("tinhThanhPho");
            Boolean diaChiMacDinh = (Boolean) request.getOrDefault("diaChiMacDinh", false);

            // Validate input
            if (soNha == null || xaPhuong == null || quanHuyen == null || tinhThanhPho == null) {
                response.put("error", true);
                response.put("message", "Vui lòng điền đầy đủ thông tin địa chỉ");
                return ResponseEntity.badRequest().body(response);
            }

            // Find address
            Optional<DiaChiKhachHang> diaChiOpt = diaChiKhachHangRepo.findById(idDiaChi);
            if (diaChiOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Không tìm thấy địa chỉ");
                return ResponseEntity.status(404).body(response);
            }

            DiaChiKhachHang diaChi = diaChiOpt.get();
            KhachHang khachHang = diaChi.getKhachHang();

            // If this is set as default, update all other addresses
            if (diaChiMacDinh && !diaChi.getDiaChiMacDinh()) {
                List<DiaChiKhachHang> existingAddresses = diaChiKhachHangRepo
                        .findByKhachHangId(khachHang.getIdKhachHang());
                for (DiaChiKhachHang addr : existingAddresses) {
                    if (!addr.getIdDiaChiKhachHang().equals(idDiaChi)) {
                        addr.setDiaChiMacDinh(false);
                        diaChiKhachHangRepo.save(addr);
                    }
                }
            }

            // Update address
            diaChi.setSoNha(soNha);
            diaChi.setXaPhuong(xaPhuong);
            diaChi.setQuanHuyen(quanHuyen);
            diaChi.setTinhThanhPho(tinhThanhPho);
            diaChi.setDiaChiMacDinh(diaChiMacDinh);

            // Save updated address
            diaChi = diaChiKhachHangRepo.save(diaChi);

            response.put("success", true);
            response.put("message", "Cập nhật địa chỉ thành công");
            response.put("diaChi", diaChi);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Xóa địa chỉ
    @DeleteMapping("/dia-chi/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteDiaChi(@PathVariable("id") Integer idDiaChi) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Tìm địa chỉ cần xóa
            Optional<DiaChiKhachHang> diaChiOpt = diaChiKhachHangRepo.findById(idDiaChi);
            if (diaChiOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Không tìm thấy địa chỉ");
                return ResponseEntity.status(404).body(response);
            }

            DiaChiKhachHang diaChi = diaChiOpt.get();
            Integer idKhachHang = diaChi.getKhachHang().getIdKhachHang();

            // Lấy toàn bộ địa chỉ của khách hàng
            List<DiaChiKhachHang> allAddresses = diaChiKhachHangRepo.findByKhachHangId(idKhachHang);

            // Nếu chỉ có 1 địa chỉ thì không được xóa
            if (allAddresses.size() <= 1) {
                response.put("error", true);
                response.put("message", "Phải có ít nhất một địa chỉ. Không thể xóa.");
                return ResponseEntity.badRequest().body(response);
            }

            // Nếu địa chỉ bị xóa là mặc định thì gán địa chỉ khác làm mặc định
            if (diaChi.getDiaChiMacDinh()) {
                List<DiaChiKhachHang> otherAddresses = allAddresses.stream()
                        .filter(addr -> !addr.getIdDiaChiKhachHang().equals(idDiaChi))
                        .collect(Collectors.toList());

                if (!otherAddresses.isEmpty()) {
                    DiaChiKhachHang newDefault = otherAddresses.get(0);
                    newDefault.setDiaChiMacDinh(true);
                    diaChiKhachHangRepo.save(newDefault);
                }
            }

            // Xóa địa chỉ
            diaChiKhachHangRepo.delete(diaChi);

            response.put("success", true);
            response.put("message", "Xóa địa chỉ thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/hd_kh")
    public Page<HoaDonResponse> getAllHDbyidKH(
            @RequestParam(name = "idKH", required = false) Integer idKH,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return hoaDonRepo.getAllHDByidKH(idKH, pageable);
    }
    @GetMapping("/hd_kh_tt")
    public Page<HoaDonResponse> getAllHDbyidKHandTT(
            @RequestParam(name = "idKH", required = false) Integer idKH,
            @RequestParam(name = "trangThai", required = false) String trangThai,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return (trangThai == null || trangThai.trim().isEmpty())
                ? hoaDonRepo.getAllHDByidKH(idKH, pageable)
                : hoaDonRepo.getAllHDByidKHandTT(idKH, trangThai, pageable);
    }

    // lềnh thêm
    @PostMapping("/update-order-info")
    @PreAuthorize("hasRole('ROLE_KH')")
    public ResponseEntity<Map<String, Object>> updateOrderCustomerInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateOrderCustomerInfoDTO request,
            @RequestParam(value = "phiVanChuyen", required = false, defaultValue = "0") BigDecimal phiVanChuyen) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Lấy email từ UserDetails (giả sử username là email)
            String email = userDetails.getUsername();
            Optional<KhachHang> khachHangOpt = khachHangRepo.findByEmail(email);
            if (!khachHangOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin khách hàng!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            Integer idKhachHang = khachHangOpt.get().getIdKhachHang();

            // Kiểm tra mã hóa đơn và đảm bảo thuộc về khách hàng
            Optional<HoaDon> hoaDonOpt = hoaDonRepo.findByMaHoaDonAndIdKhachHang(request.getMaHoaDon(), idKhachHang);
            if (!hoaDonOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy hóa đơn hoặc bạn không có quyền cập nhật!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            HoaDon hoaDon = hoaDonOpt.get();

            // Kiểm tra trạng thái đơn hàng (phải là Chờ xác nhận, Đã xác nhận, hoặc Chờ đóng gói)
            String currentStatus = hoaDonRepo.findLatestStatusByIdHoaDon(hoaDon.getId_hoa_don());
            List<String> allowedStatuses = Arrays.asList("Chờ xác nhận", "Đã xác nhận", "Chờ đóng gói");
            if (!allowedStatuses.contains(currentStatus)) {
                response.put("success", false);
                response.put("message", "Không thể cập nhật thông tin khách hàng cho đơn hàng này!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Kiểm tra dữ liệu đầu vào
            if (request.getHoTen() == null || request.getHoTen().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Họ tên không được để trống!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (request.getSdtNguoiNhan() == null || request.getSdtNguoiNhan().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Số điện thoại không được để trống!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (request.getDiaChi() == null || request.getDiaChi().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Địa chỉ không được để trống!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Cập nhật thông tin
            hoaDon.setHo_ten(request.getHoTen().trim());
            hoaDon.setSdt_nguoi_nhan(request.getSdtNguoiNhan().trim());
            hoaDon.setDia_chi(request.getDiaChi().trim());
            hoaDon.setNgay_sua(LocalDateTime.now());
            BigDecimal pvcCu = hoaDon.getPhi_van_chuyen() != null ? hoaDon.getPhi_van_chuyen() : BigDecimal.ZERO;
            hoaDon.setTong_tien_sau_giam(hoaDon.getTong_tien_sau_giam().subtract(pvcCu).add(phiVanChuyen));
            System.out.println("Phí vận chuyển: " + phiVanChuyen);
            System.out.println("Phí vận chuyển: " + hoaDon.getTong_tien_sau_giam());
            hoaDon.setPhi_van_chuyen(phiVanChuyen);
            hoaDonRepo.save(hoaDon);

            // Ghi lại lịch sử cập nhật trong theo_doi_don_hang
            LocalDateTime ngayChuyen = LocalDateTime.now();
            String noiDungDoi = "Khách hàng tự cập nhật thông tin";
            hoaDonRepo.insertTrangThaiDonHang(request.getMaHoaDon(), "Đã cập nhật", ngayChuyen, null, noiDungDoi);

            response.put("success", true);
            response.put("message", "Cập nhật thông tin khách hàng thành công!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    ///
    @PostMapping("/send-support-request")
    @PreAuthorize("hasRole('ROLE_KH')") // Chỉ cho phép khách hàng (ROLE_KH) truy cập
    public ResponseEntity<Map<String, Object>> sendSupportRequest(
            @AuthenticationPrincipal UserDetails userDetails, // Lấy thông tin người dùng từ token
            @RequestBody SupportRequestDTO request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Lấy email từ token (username chính là email)
            String email = userDetails.getUsername();

            // Tìm khách hàng dựa trên email từ token
            Optional<KhachHang> khachHangOpt = khachHangRepo.findByEmail(email);
            if (!khachHangOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin khách hàng!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            KhachHang khachHang = khachHangOpt.get();

            // Kiểm tra trạng thái tài khoản khách hàng
            if (!"Đang hoạt động".equals(khachHang.getTrangThai())) {
                response.put("success", false);
                response.put("message", "Tài khoản của bạn đã bị ngừng hoạt động!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Tạo nội dung email
            String subject = "Yêu cầu hỗ trợ mới từ khách hàng - " + request.getChuDe();
            String body = "<!DOCTYPE html>" +
                    "<html lang='vi'>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                    ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }" +
                    ".header { background-color: #e53935; color: #ffffff; padding: 20px; text-align: center; border-top-left-radius: 10px; border-top-right-radius: 10px; }" +
                    ".header h1 { margin: 0; font-size: 24px; }" +
                    ".content { padding: 20px; }" +
                    ".content h3 { margin: 0 0 10px; font-size: 20px; }" +
                    ".info-box { background-color: #fff5f5; border-left: 5px solid #e53935; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                    ".info-box p { margin: 5px 0; }" +
                    ".footer { text-align: center; padding: 10px; font-size: 14px; color: #666; }" +
                    ".footer a { color: #e53935; text-decoration: none; }" +
                    ".footer a:hover { text-decoration: underline; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<h1>Yêu cầu hỗ trợ từ khách hàng</h1>" +
                    "</div>" +
                    "<div class='content'>" +
                    "<h3>Thông tin yêu cầu:</h3>" +
                    "<div class='info-box'>" +
                    "<p><strong>Họ và tên:</strong> " + request.getHoTen() + "</p>" +
                    "<p><strong>Số điện thoại:</strong> " + request.getSoDienThoai() + "</p>" +
                    "<p><strong>Email:</strong> " + request.getEmail() + "</p>" +
                    "<p><strong>Chủ đề:</strong> " + request.getChuDe() + "</p>" +
                    "<p><strong>Nội dung:</strong> " + request.getNoiDung() + "</p>" +
                    "</div>" +
                    "<p>Vui lòng xem xét và phản hồi yêu cầu của khách hàng trong thời gian sớm nhất.</p>" +
                    "</div>" +
                    "<div class='footer'>" +
                    "<p>Trân trọng,<br>Đội ngũ G&B SPORTS</p>" +
                    "<p><a href='http://localhost:5173/home'>Ghé thăm website</a></p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            // Gửi email đến lenhphun919@gmail.com
            emailService.sendEmail("chinhhtph46334@gmail.com", subject, body);

            response.put("success", true);
            response.put("message", "Yêu cầu hỗ trợ đã được gửi thành công!");
            return ResponseEntity.ok(response);

        } catch (MessagingException e) {
            response.put("success", false);
            response.put("message", "Gửi yêu cầu thất bại: Không thể gửi email - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}