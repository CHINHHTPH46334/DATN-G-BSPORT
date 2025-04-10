package com.example.gbsports.controller;

import com.example.gbsports.entity.NhanVien;
import com.example.gbsports.entity.PasswordGenerator;
import com.example.gbsports.entity.Roles;
import com.example.gbsports.entity.TaiKhoan;
import com.example.gbsports.repository.NhanVienRepo;
import com.example.gbsports.repository.RolesRepo;
import com.example.gbsports.repository.TaiKhoanRepo;
import com.example.gbsports.request.NhanVienRequest;
import com.example.gbsports.response.NhanVienResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173/", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
@RequestMapping("/admin")
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
//    private PasswordEncoder passwordEncoder;

    @GetMapping("/quan-ly-nhan-vien/findAll")
    public List<NhanVien> findAll() {
        return nhanVienRepo.findAll();
    }


    //    @GetMapping("/quan-ly-nhan-vien")
//    public List<NhanVienResponse> getAll(){
//        return nhanVienRepo.getAll();
//    }
    @GetMapping("/quan-ly-nhan-vien")
    public Page<NhanVienResponse> phanTrang(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "size", defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return nhanVienRepo.listPT(pageable);
    }

    @GetMapping("/quan-ly-nhan-vien/findById")
    public NhanVien findById(@RequestParam("id") Integer id) {
        return nhanVienRepo.findById(id).get();
    }

    @PostMapping("/quan-ly-nhan-vien/add")
    public String add(@RequestBody NhanVienRequest nhanVienRequest) {
        System.out.println("Request nhận được: " + nhanVienRequest);
        NhanVien nhanVien = new NhanVien();
        BeanUtils.copyProperties(nhanVienRequest, nhanVien);
        TaiKhoan taiKhoan = new TaiKhoan();
        String generatedPassword = PasswordGenerator.generateRandomPassword();
        // Mã hóa mật khẩu trước khi lưu
//        String encodedPassword = passwordEncoder.encode(generatedPassword);
        taiKhoan.setTen_dang_nhap(nhanVienRequest.getEmail());
        taiKhoan.setMat_khau(generatedPassword);
        taiKhoan.setRoles(rolesRepo.findById(3).get());
        taiKhoanRepo.save(taiKhoan);
        nhanVien.setTrangThai("Đang hoạt động");
        nhanVien.setTaiKhoan(taiKhoan);
        nhanVienRepo.save(nhanVien);
        String tenDN = nhanVien.getEmail();
        String content =  "<h1>Chào mừng bạn đến với hệ thống G&B SPORTS</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<h3>Chúc mừng bạn đã được tạo tài khoản nhân viên!</h3>" +
                "<p>Dưới đây là thông tin đăng nhập của bạn:</p>" +
                "<div class='info-box'>" +
                "<p><strong>Mã Nhân Viên:</strong> " + nhanVien.getMaNhanVien() + "</p>" +
                "<p><strong>Tên tài khoản:</strong> " + tenDN + "</p>" +
                "<p><strong>Mật khẩu đăng nhập tạm thời:</strong> " + taiKhoan.getMat_khau() + "</p>" +
                "</div>" +
                "<p><strong>Vui lòng đổi mật khẩu sau khi đăng nhập.</strong></p>";

        sendEmail(nhanVien.getEmail(),content);
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

    @PutMapping("/quan-ly-nhan-vien/update")
    public String update(@RequestBody NhanVienRequest nhanVienRequest) {
        System.out.println("Request nhận được: " + nhanVienRequest);
        try {

            // Kiểm tra xem email có thay đổi không
            NhanVien oldNhanVien = nhanVienRepo.findById(nhanVienRequest.getIdNhanVien()).get();
            if (!oldNhanVien.getEmail().equals(nhanVienRequest.getEmail())) {
                // Nếu email thay đổi thì gọi hàm xử lý gửi email đã có sẵn
                // Giả sử hàm gửi email là sendEmailToEmployee
                TaiKhoan taiKhoan = new TaiKhoan();
                String generatedPassword = PasswordGenerator.generateRandomPassword();
                taiKhoan.setId_tai_khoan(nhanVienRequest.getTaiKhoan().getId_tai_khoan());
                taiKhoan.setMat_khau(generatedPassword);
                taiKhoan.setTen_dang_nhap(nhanVienRequest.getEmail());
                taiKhoan.setRoles(rolesRepo.findById(3).get());
                taiKhoanRepo.save(taiKhoan);
                String tenDN = nhanVienRequest.getEmail();
                String content = "<h1>Cập nhật tài khoản thành công</h1>" +
                        "</div>" +
                        "<div class='content'>" +
                        "<h3>Xin chào!</h3>" +
                        "<p>Thông tin tài khoản của bạn trên hệ thống G&B SPORTS đã được cập nhật thành công.</p>" +
                        "<div class='info-box'>" +
                        "<p><strong>Mã nhân viên:</strong> " + nhanVienRequest.getMaNhanVien() + "</p>" +
                        "<p><strong>Tên tài khoản:</strong> " + tenDN + "</p>" +
                        "</div>";
                sendEmail(nhanVienRequest.getEmail(),content);
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

    @PostMapping("/quan-ly-nhan-vien/changeStatus")
    public String changeStatus(@RequestBody NhanVienRequest nhanVienRequest) {
        if (nhanVienRequest.getTrangThai().equals("Đang hoạt động")) {
            nhanVienRequest.setTrangThai("Đã nghỉ việc");
            NhanVien nhanVien = new NhanVien();
            BeanUtils.copyProperties(nhanVienRequest, nhanVien);
            nhanVienRepo.save(nhanVien);
        } else {
            nhanVienRequest.setTrangThai("Đang hoạt động");
            NhanVien nhanVien = new NhanVien();
            BeanUtils.copyProperties(nhanVienRequest, nhanVien);
            nhanVienRepo.save(nhanVien);
        }
        return "Chuyển trạng thái thành công";
    }
//Search NV API

    @GetMapping("/quan-ly-nhan-vien/search")
    public Page<NhanVienResponse> timNhanVien(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size,
                                              @RequestParam(name = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        return nhanVienRepo.timNhanVien(keyword, pageable);
    }

    @GetMapping("/quan-ly-nhan-vien/locTrangThai")
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
}
