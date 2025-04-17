package com.example.gbsports.controller;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.LichSuDangNhapRepo;
import com.example.gbsports.repository.NhanVienRepo;
import com.example.gbsports.repository.RolesRepo;
import com.example.gbsports.repository.TaiKhoanRepo;
import com.example.gbsports.request.*;
import com.example.gbsports.response.NhanVienResponse;
import com.example.gbsports.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173/", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
@RequestMapping("/admin/quan-ly-nhan-vien")
public class NhanVienController {
    @Autowired
    NhanVienRepo nhanVienRepo;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordGenerator passwordGenerator;
    @Autowired
    private RolesRepo rolesRepo;
    @Autowired
    private TaiKhoanRepo taiKhoanRepo;
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

    @GetMapping("/findAll")
    public List<NhanVien> findAll() {
        return nhanVienRepo.findAll();
    }


    //    @GetMapping("/quan-ly-nhan-vien")
//    public List<NhanVienResponse> getAll(){
//        return nhanVienRepo.getAll();
//    }
    @GetMapping("/phanTrang")
    public Page<NhanVienResponse> phanTrang(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "size", defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return nhanVienRepo.listPT(pageable);
    }

    @GetMapping("/findById")
    public NhanVien findById(@RequestParam("id") Integer id) {
        return nhanVienRepo.findById(id).get();
    }

    @PostMapping("/add")
    public String add(@RequestBody NhanVienRequest nhanVienRequest) {
        System.out.println("Request nhận được: " + nhanVienRequest);
        NhanVien nhanVien = new NhanVien();
        BeanUtils.copyProperties(nhanVienRequest, nhanVien);
        TaiKhoan taiKhoan = new TaiKhoan();
        String generatedPassword = PasswordGenerator.generateRandomPassword();
        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(generatedPassword);
        taiKhoan.setTen_dang_nhap(nhanVienRequest.getEmail().split("@")[0]);
        taiKhoan.setMat_khau(encodedPassword);
        taiKhoan.setRoles(rolesRepo.findById(3).get());
        taiKhoanRepo.save(taiKhoan);
        nhanVien.setTrangThai("Đang hoạt động");
        nhanVien.setTaiKhoan(taiKhoan);
        nhanVien.setNgayThamGia(LocalDate.now());
        nhanVienRepo.save(nhanVien);
        String tenDN = nhanVien.getEmail().split("@")[0];
        String content = "<h1>Chào mừng bạn đến với hệ thống G&B SPORTS</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<h3>Chúc mừng bạn đã được tạo tài khoản nhân viên!</h3>" +
                "<p>Dưới đây là thông tin đăng nhập của bạn:</p>" +
                "<div class='info-box'>" +
                "<p><strong>Mã Nhân Viên:</strong> " + nhanVien.getMaNhanVien() + "</p>" +
                "<p><strong>Tên tài khoản:</strong> " + tenDN + "</p>" +
                "<p><strong>Mật khẩu đăng nhập tạm thời:</strong> " + generatedPassword + "</p>" +
                "</div>" +
                "<p><strong>Vui lòng đổi mật khẩu sau khi đăng nhập.</strong></p>";

        sendEmail(nhanVien.getEmail(), content);
        return "Thêm thành công";
    }

    private void sendEmail(String toEmail, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Thông tin đăng nhập hệ thống của bạn");
            String body = "<!DOCTYPE html>" +
                    "<html lang='vi'>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                    ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }" +
                    ".header { background-color: rgb(217, 27, 27); color: white; padding: 20px; text-align: center; border-top-left-radius: 10px; border-top-right-radius: 10px; }" +
                    ".header h1 { margin: 0; font-size: 24px; color: white; }" +
                    ".content { padding: 20px; }" +
                    ".content h3 { margin: 0 0 10px; font-size: 20px; }" +
                    ".info-box { background-color: rgb(255, 239, 239); border-left: 5px solid #d02c39; padding: 15px; margin: 20px 0; border-radius: 5px; }" +
                    ".info-box p { margin: 5px 0; }" +
                    ".footer { text-align: center; padding: 10px; font-size: 14px; color: #666; }" +
                    ".footer a { color: rgb(232, 78, 78); text-decoration: none; }" +
                    ".footer a:hover { text-decoration: underline; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    content +
                    "<p>Nếu bạn gặp bất kỳ vấn đề nào, vui lòng liên hệ bộ phận hỗ trợ.</p>" +
                    "</div>" +
                    "<div class='footer'>" +
                    "<p>Trân trọng,<br>Đội ngũ G&B SPORTS</p>" +
                    "<p><a href='http://localhost:5173/home'>Ghé thăm website</a> | <a href='mailto:support@gbsports.com'>Liên hệ hỗ trợ</a></p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";
            helper.setText(body, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @PutMapping("/update")
    public String update(@RequestBody NhanVienRequest nhanVienRequest) {
        System.out.println("Request nhận được: " + nhanVienRequest);
        try {

            // Kiểm tra xem email có thay đổi không
            NhanVien oldNhanVien = nhanVienRepo.findById(nhanVienRequest.getIdNhanVien()).get();
            if (!oldNhanVien.getEmail().equals(nhanVienRequest.getEmail())) {
                // Nếu email thay đổi thì gọi hàm xử lý gửi email đã có sẵn
                // Giả sử hàm gửi email là sendEmailToEmployee
                TaiKhoan taiKhoan = new TaiKhoan();
                taiKhoan.setId_tai_khoan(nhanVienRequest.getTaiKhoan().getId_tai_khoan());
                taiKhoan.setTen_dang_nhap(nhanVienRequest.getEmail().split("@")[0]);
                taiKhoan.setRoles(rolesRepo.findById(3).get());
                taiKhoanRepo.save(taiKhoan);
                String tenDN = nhanVienRequest.getEmail().split("@")[0];
                String content = "<h1>Cập nhật tài khoản thành công</h1>" +
                        "</div>" +
                        "<div class='content'>" +
                        "<h3>Xin chào!</h3>" +
                        "<p>Thông tin tài khoản của bạn trên hệ thống G&B SPORTS đã được cập nhật thành công.</p>" +
                        "<div class='info-box'>" +
                        "<p><strong>Mã nhân viên:</strong> " + nhanVienRequest.getMaNhanVien() + "</p>" +
                        "<p><strong>Tên tài khoản:</strong> " + tenDN + "</p>" +
                        "</div>";
                sendEmail(nhanVienRequest.getEmail(), content);
                NhanVien nhanVien = new NhanVien();
                BeanUtils.copyProperties(nhanVienRequest, nhanVien);
                nhanVien.setIdNhanVien(nhanVienRequest.getIdNhanVien());
                nhanVien.setTaiKhoan(taiKhoan);
                nhanVienRepo.save(nhanVien);
            } else {
                NhanVien nhanVien = new NhanVien();
                BeanUtils.copyProperties(nhanVienRequest, nhanVien);
                nhanVien.setIdNhanVien(nhanVienRequest.getIdNhanVien());
                nhanVien.setTaiKhoan(taiKhoanRepo.findById(nhanVienRequest.getTaiKhoan().getId_tai_khoan()).get());
                nhanVienRepo.save(nhanVien);
            }
            return "Sửa thành công";
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật nhân viên: " + e.getMessage());
            return "Có lỗi xảy ra khi cập nhật: " + e.getMessage();
        }
    }

    @PutMapping("/changeStatus")
    public String changeStatus(@RequestParam("id") Integer id) {
        NhanVien nhanVien = nhanVienRepo.findById(id).get();
        if (nhanVien.getTrangThai().equals("Đang hoạt động")) {
            nhanVien.setTrangThai("Ngừng hoạt động");

            nhanVienRepo.save(nhanVien);
        } else {
            nhanVien.setTrangThai("Đang hoạt động");


            nhanVienRepo.save(nhanVien);
        }
        return "Chuyển trạng thái thành công";
    }
//Search NV API

    @GetMapping("/search")
    public Page<NhanVienResponse> timNhanVien(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size,
                                              @RequestParam(name = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        return nhanVienRepo.timNhanVien(keyword, pageable);
    }

    @GetMapping("/locTrangThai")
    public Page<NhanVienResponse> locNhanVien(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size,
                                              @RequestParam(name = "trangThai", required = false) String trangThai) {
        Pageable pageable = PageRequest.of(page, size);
        return nhanVienRepo.locNhanVienTheoTrangThai(trangThai, pageable);
    }

    //    @PostMapping("addNhanVien")
//    public ResponseEntity<?> saveNhanVien(@Valid @RequestBody NhanVienRequest nhanVienRequest, BindingResult result) {
//        if (result.hasErrors()) {
//            List<String> errors = result.getAllErrors().stream().map(error -> error.getDefaultMessage())
//                    .collect(Collectors.toList());
//            return ResponseEntity.badRequest().body(errors);
//        } else {
//            NhanVien nv = new NhanVien();
//            BeanUtils.copyProperties(nhanVienRequest, nv);
//            nhanVienRepo.save(nv);
//            return ResponseEntity.ok("Lưu thành công");
//
//        }
//    }
    @PostMapping("/login_admin")
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
            TaiKhoan taiKhoan = taiKhoanRepo.findByTenDangNhapAndNhanVienRoles(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            // Tài khoản người dùng (Admin, Quản lý, Nhân viên)
            Optional<NhanVien> nhanVienOpt = nhanVienRepo.findByTaiKhoanIdTaiKhoan(taiKhoan.getId_tai_khoan());
            if (nhanVienOpt.isPresent()) {
                NhanVien nhanVien = nhanVienOpt.get();
                if ("Ngừng hoạt động".equals(nhanVien.getTrangThai())) {
                    response.put("error", "Tài khoản của bạn đã bị ngừng hoạt động!");
                    return ResponseEntity.badRequest().body(response);
                }
            }
            // Xác thực người dùng bằng AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
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

    @GetMapping("/details")
    public ResponseEntity<NhanVien> getNhanVienDetails(@RequestParam String tenDangNhap) {
        Optional<NhanVien> nhanVien = taiKhoanRepo.findNhanVienByTenDangNhap(tenDangNhap);
        if (nhanVien.isPresent()) {
            System.out.println("Thông tin nhân viên tìm được: " + nhanVien.get());
        } else {
            System.out.println("Không tìm thấy nhân viên với ten_dang_nhap: " + tenDangNhap);
        }
        return nhanVien.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody QuenMKRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Tìm tài khoản nhân viên theo email
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepo.findByTenDangNhapAndNhanVienRoles(request.getEmail());
        if (!taiKhoanOpt.isPresent()) {
            response.put("error", "Email không tồn tại trong hệ thống!");
            return ResponseEntity.badRequest().body(response);
        }

        TaiKhoan taiKhoan = taiKhoanOpt.get();
        Optional<NhanVien> nhanVienOpt = nhanVienRepo.findByTaiKhoanIdTaiKhoan(taiKhoan.getId_tai_khoan());
        if (nhanVienOpt.isPresent() && !"Đang hoạt động".equals(nhanVienOpt.get().getTrangThai())) {
            response.put("error", "Tài khoản của bạn đã bị ngừng hoạt động!");
            return ResponseEntity.badRequest().body(response);
        }
        // Tạo reset token
        String resetToken = jwtUtil.generateResetToken(request.getEmail());
//        // Tạo liên kết đặt lại mật khẩu
//        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;
//
//        // Gửi email
//        String content = "<h1>Đặt lại mật khẩu - G&B SPORTS</h1>" +
//                "</div>" +
//                "<div class='content'>" +
//                "<h3>Xin chào,</h3>" +
//                "<p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản nhân viên tại G&B SPORTS.</p>" +
//                "<p>Vui lòng nhấp vào liên kết sau để đặt lại mật khẩu:</p>" +
//                "<div class='info-box'>" +
//                "<p><a href='" + resetLink + "'>Đặt lại mật khẩu</a></p>" +
//                "</div>" +
//                "<p>Liên kết này có hiệu lực trong 1 giờ. Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>";
//
//        try {
//            sendEmail(request.getEmail(), content);
//            response.put("successMessage", "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn!");
//        } catch (Exception e) {
//            response.put("warning", "Yêu cầu đặt lại mật khẩu thành công nhưng gửi email thất bại: " + e.getMessage());
//        }
        response.put("successMessage", "Tên đăng nhập hợp lệ, vui lòng nhập mật khẩu mới.");
        response.put("resetToken", resetToken); // Thêm token vào phản hồi
        return ResponseEntity.ok(response);
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

        // Tìm tài khoản nhân viên theo email
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepo.findByTenDangNhapAndNhanVienRoles(email);
        if (!taiKhoanOpt.isPresent()) {
            response.put("error", "Tài khoản không tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }

        TaiKhoan taiKhoan = taiKhoanOpt.get();
        Optional<NhanVien> nhanVienOpt = nhanVienRepo.findByTaiKhoanIdTaiKhoan(taiKhoan.getId_tai_khoan());
        if (nhanVienOpt.isPresent() && !"Đang hoạt động".equals(nhanVienOpt.get().getTrangThai())) {
            response.put("error", "Tài khoản của bạn đã bị ngừng hoạt động!");
            return ResponseEntity.badRequest().body(response);
        }

        // Cập nhật mật khẩu cho tài khoản nhân viên
        taiKhoan.setMat_khau(passwordEncoder.encode(request.getNewPassword()));
        taiKhoanRepo.save(taiKhoan);

        response.put("successMessage", "Đặt lại mật khẩu thành công!");
        return ResponseEntity.ok(response);
    }
    @GetMapping("/listTrangAdmin")
    public List<NhanVienResponse> listTrangAdmin(){
        return nhanVienRepo.listTrangAdmin();
    }
}
