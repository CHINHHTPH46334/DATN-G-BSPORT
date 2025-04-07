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
//    ArrayList<String> mangLoi = new ArrayList<>();
//    private ArrayList<String> checkTrong(NhanVienRequest nhanVienRequest) {
//        mangLoi.clear();
//        // Kiểm tra trống
//        if (nhanVienRequest.getMaNhanVien() == null || nhanVienRequest.getMaNhanVien().trim().isEmpty()) {
//            mangLoi.add("Mã nhân viên không được để trống \n") ;
//        }
//        if (nhanVienRequest.getTenNhanVien() == null || nhanVienRequest.getTenNhanVien().trim().isEmpty()) {
//            mangLoi.add("Tên nhân viên không được để trống \n") ;
//        }
//        if (nhanVienRequest.getEmail() == null || nhanVienRequest.getEmail().trim().isEmpty()) {
//            mangLoi.add("Email không được để trống \n") ;
//        }
//        if (nhanVienRequest.getSoDienThoai() == null || nhanVienRequest.getSoDienThoai().trim().isEmpty()) {
//            mangLoi.add("Số điện thoại không được để trống \n") ;
//        }
//        if (nhanVienRequest.getNgaySinh() == null) {
//            mangLoi.add("Chưa chọn ngày sinh \n");
//        }
//        if (nhanVienRequest.getDiaChiLienHe() == null || nhanVienRequest.getDiaChiLienHe().trim().isEmpty()) {
//            mangLoi.add("Địa chỉ liên hệ không được để trống \n");
//        }
//        if (nhanVienRequest.getGioiTinh() == null ) {
//            mangLoi.add("Chưa chọn giới tính \n");
//        }
//
////        // Kiểm tra trùng mã, email, số điện thoại
////        if (nhanVienRepo.existsByMaNhanVien(nhanVienRequest.getMaNhanVien())) {
////            return "Mã nhân viên đã tồn tại";
////        }
////        if (nhanVienRepo.existsByEmail(nhanVienRequest.getEmail())) {
////            return "Email đã tồn tại";
////        }
////        if (nhanVienRepo.existsBySoDienThoai(nhanVienRequest.getSoDienThoai())) {
////            return "Số điện thoại đã tồn tại";
////        }
//
//        return mangLoi; // Không có lỗi
//    }
//    ArrayList<String> mangTrung = new ArrayList<>();
//    private ArrayList<String> checkTrung(NhanVienRequest nhanVienRequest){
//        mangTrung.clear();
//        for (NhanVienResponse nv: nhanVienRepo.getAll()) {
//            if (nv.getMaNhanVien().trim().equalsIgnoreCase(nhanVienRequest.getMaNhanVien().trim())){
//                mangTrung.add("Trùng mã nhân viên");
//            }
//            if (nv.getEmail().trim().equalsIgnoreCase(nhanVienRequest.getEmail().trim())){
//                mangTrung.add("Trùng Email");
//            }
//            if (nv.getSoDienThoai().trim().equalsIgnoreCase(nhanVienRequest.getSoDienThoai().trim())){
//                mangTrung.add("Trùng số điện thoại");
//            }
//        }
//        return mangTrung;
//    }
//
//    @PostMapping("/quan-ly-nhan-vien/add")
//    public String add(@RequestBody NhanVienRequest nhanVienRequest) {
//       if (checkTrong(nhanVienRequest).isEmpty() && checkTrung(nhanVienRequest).isEmpty()){
//        NhanVien nhanVien = new NhanVien();
//        BeanUtils.copyProperties(nhanVienRequest, nhanVien);
//        nhanVienRepo.save(nhanVien);
//        return "Thêm thành công";
//       }
//        return mangLoi.toString()+mangTrung.toString();
//    }
//private List<String> validateNhanVien(NhanVienRequest nhanVienRequest) {
//    List<String> errors = new ArrayList<>();
//
//    if (nhanVienRequest.getMaNhanVien() == null || nhanVienRequest.getMaNhanVien().trim().isEmpty()) {
//        errors.add("Mã nhân viên không được để trống.");
//    }
//    if (nhanVienRequest.getTenNhanVien() == null || nhanVienRequest.getTenNhanVien().trim().isEmpty()) {
//        errors.add("Tên nhân viên không được để trống.");
//    }
//    if (nhanVienRequest.getNgaySinh() == null) {
//        errors.add("Ngày sinh không được để trống.");
//    }
//    if (nhanVienRequest.getEmail() == null || nhanVienRequest.getEmail().trim().isEmpty()) {
//        errors.add("Email không được để trống.");
//    }
//    if (nhanVienRequest.getDiaChiLienHe() == null || nhanVienRequest.getDiaChiLienHe().trim().isEmpty()) {
//        errors.add("Địa chỉ liên hệ không được để trống.");
//    }
//    if (nhanVienRequest.getGioiTinh() == null) {
//        errors.add("Giới tính không được để trống.");
//    }
//    if (nhanVienRequest.getSoDienThoai() == null || nhanVienRequest.getSoDienThoai().trim().isEmpty()) {
//        errors.add("Số điện thoại không được để trống.");
//    }
//    if (nhanVienRequest.getTrangThai() == null || nhanVienRequest.getTrangThai().trim().isEmpty()) {
//        errors.add("Trạng thái không được để trống.");
//    }
//
//    return errors;
//}
//    @PostMapping("/quan-ly-nhan-vien/add")
//    public ResponseEntity<?> add(@RequestBody NhanVienRequest nhanVienRequest) {
//        List<String> errors = validateNhanVien(nhanVienRequest);
//        if (!errors.isEmpty()) {
//            return ResponseEntity.badRequest().body(errors); // Trả về lỗi dưới dạng JSON
//        }
//        NhanVien nhanVien = new NhanVien();
//        BeanUtils.copyProperties(nhanVienRequest, nhanVien);
//        nhanVienRepo.save(nhanVien);
//        return ResponseEntity.ok("Thêm thành công");
//    }

//    private String validateNhanVien(NhanVienRequest nhanVienRequest) {
//        StringBuilder errors = new StringBuilder();
//
//        if (nhanVienRequest.getMaNhanVien() == null || nhanVienRequest.getMaNhanVien().trim().isEmpty()) {
//            errors.append("Mã nhân viên không được để trống.\n");
//        } else if (nhanVienRepo.existsByMaNhanVien(nhanVienRequest.getMaNhanVien())) {
//            errors.append("Mã nhân viên đã tồn tại.\n");
//        }
//
//        if (nhanVienRequest.getTenNhanVien() == null || nhanVienRequest.getTenNhanVien().trim().isEmpty()) {
//            errors.append("Tên nhân viên không được để trống.\n");
//        }
//
//        if (nhanVienRequest.getNgaySinh() == null) {
//            errors.append("Ngày sinh không được để trống.\n");
//        } else {
//            LocalDate birthDate = nhanVienRequest.getNgaySinh().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//            int age = Period.between(birthDate, LocalDate.now()).getYears();
//            if (age < 18 || age > 50) {
//                errors.append("Tuổi nhân viên phải từ 18 đến 50\n");
//            }
//        }
//
//        if (nhanVienRequest.getEmail() == null || nhanVienRequest.getEmail().trim().isEmpty()) {
//            errors.append("Email không được để trống.\n");
//        } else if (!nhanVienRequest.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
//            errors.append("Email không đúng định dạng.\n");
//        } else if (nhanVienRepo.existsByEmail(nhanVienRequest.getEmail())) {
//            errors.append("Email đã tồn tại.\n");
//        }
//
//        if (nhanVienRequest.getDiaChiLienHe() == null || nhanVienRequest.getDiaChiLienHe().trim().isEmpty()) {
//            errors.append("Địa chỉ liên hệ không được để trống.\n");
//        }
//
//        if (nhanVienRequest.getGioiTinh() == null) {
//            errors.append("Giới tính không được để trống.\n");
//        }
//
//        if (nhanVienRequest.getSoDienThoai() == null || nhanVienRequest.getSoDienThoai().trim().isEmpty()) {
//            errors.append("Số điện thoại không được để trống.\n");
//        } else if (!nhanVienRequest.getSoDienThoai().matches("^(0[3|5|7|8|9])[0-9]{8,9}$")) {
//            errors.append("Số điện thoại không đúng định dạng Việt Nam.\n");
//        } else if (nhanVienRepo.existsBySoDienThoai(nhanVienRequest.getSoDienThoai())) {
//            errors.append("Số điện thoại đã tồn tại.\n");
//        }
//
//        if (nhanVienRequest.getTrangThai() == null || nhanVienRequest.getTrangThai().trim().isEmpty()) {
//            errors.append("Trạng thái không được để trống.\n");
//        }
//
//        return errors.toString().trim();
//    }

    @PostMapping("/quan-ly-nhan-vien/add")
    public String add(@RequestBody NhanVienRequest nhanVienRequest) {
        System.out.println("Request nhận được: " + nhanVienRequest);
//
//        String errorMessage = validateNhanVien(nhanVienRequest);
//        if (!errorMessage.isEmpty()) {
//            return errorMessage; // Trả về lỗi nếu có
//        }
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
                sendEmail(nhanVienRequest.getEmail(), nhanVienRequest.getMaNhanVien(), generatedPassword, nhanVienRequest.getEmail());
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
