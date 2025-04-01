package com.example.gbsports.controller;

import com.example.gbsports.entity.NhanVien;
import com.example.gbsports.entity.PasswordGenerator;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public NhanVien findById(@RequestParam("id") Integer id){
        return nhanVienRepo.findById(id).get();
    }


    @PostMapping("/quan-ly-nhan-vien/add")
    public String add(@RequestBody NhanVienRequest nhanVienRequest) {
        System.out.println("Request nhận được: " + nhanVienRequest);
        NhanVien nhanVien = new NhanVien();
        BeanUtils.copyProperties(nhanVienRequest, nhanVien);
        TaiKhoan taiKhoan = new TaiKhoan();
        String generatedPassword = PasswordGenerator.generateRandomPassword();
        taiKhoan.setTen_dang_nhap(nhanVienRequest.getEmail().split("@")[0]);
        taiKhoan.setMat_khau(generatedPassword);
        taiKhoan.setRoles(rolesRepo.findById(3).get());
        taiKhoanRepo.save(taiKhoan);
        nhanVien.setTrangThai("Đang hoạt động");
        nhanVien.setTaiKhoan(taiKhoan);
        nhanVienRepo.save(nhanVien);
        sendEmail(nhanVien.getEmail(), nhanVien.getMaNhanVien(), generatedPassword, nhanVien.getEmail());
        return "Thêm thành công";
    }

    private void sendEmail(String toEmail, String maNhanVien, String password, String email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String tenDN = toEmail.split("@")[0];
            helper.setTo(toEmail);
            helper.setSubject("Thông tin đăng nhập hệ thống của bạn");
            String body = "Chúc mừng bạn đã được tạo tài khoản trên hệ thống của chúng tôi! Dưới đây là thông tin đăng nhập của bạn <br><br>"
                    + "<b> Mã Nhân Viên: " + maNhanVien + "</b><br>"
                    + "<b> Tên tài khoản: " + tenDN + "</b><br>"
                    + "<b> Mật khẩu đăng nhập tạm thời: " + password + "</b><br><br>"
                    + "<b>Vui lòng đổi mật khẩu sau khi đăng nhập.<b>"
                    + "<p>Nếu bạn gặp bất kỳ vấn đề nào, vui lòng liên hệ bộ phận hỗ trợ.</p>"
                    + "<p>Trân trọng,</p>"
                    + "<p><b>[G&B Sport]</b></p>";
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
                taiKhoan.setTen_dang_nhap(nhanVienRequest.getEmail().split("@")[0]);
                taiKhoan.setRoles(rolesRepo.findById(3).get());
                taiKhoanRepo.save(taiKhoan);
                sendEmail(nhanVienRequest.getEmail(),nhanVienRequest.getMaNhanVien(),generatedPassword,nhanVienRequest.getEmail());
                NhanVien nhanVien = new NhanVien();
                BeanUtils.copyProperties(nhanVienRequest, nhanVien);
                nhanVien.setIdNhanVien(nhanVienRequest.getIdNhanVien());
                nhanVien.setTaiKhoan(taiKhoan);
                nhanVienRepo.save(nhanVien);
            }else {
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
    @PutMapping("/quan-ly-nhan-vien/changeStatus")
    public String changeStatus(@RequestParam("id") Integer id) {
        NhanVien nhanVien = nhanVienRepo.findById(id).get();
        if (nhanVien.getTrangThai().equals("Đang hoạt động")) {
            nhanVien.setTrangThai("Đã nghỉ việc");
            nhanVienRepo.save(nhanVien);
        } else {
            nhanVien.setTrangThai("Đang hoạt động");
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


}
