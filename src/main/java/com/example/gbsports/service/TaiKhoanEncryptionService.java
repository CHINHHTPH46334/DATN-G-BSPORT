//package com.example.gbsports.service;
//
//import com.example.gbsports.entity.TaiKhoan;
//import com.example.gbsports.repository.TaiKhoanRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class TaiKhoanEncryptionService {
//    @Autowired
//    private TaiKhoanRepo taiKhoanRepo;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public void encryptAllPasswords() {
//        List<TaiKhoan> taiKhoans = taiKhoanRepo.findAll();
//
//        for (TaiKhoan taiKhoan : taiKhoans) {
//            String currentPassword = taiKhoan.getMat_khau();
//            if (!isPasswordEncrypted(currentPassword)) {
//                String encodedPassword = passwordEncoder.encode(currentPassword);
//                taiKhoan.setMat_khau(encodedPassword);
//                taiKhoanRepo.save(taiKhoan);
//                System.out.println("Mã hóa mật khẩu cho " + taiKhoan.getTen_dang_nhap() + ": " + encodedPassword);
//            } else {
//                System.out.println("Mật khẩu cho " + taiKhoan.getTen_dang_nhap() + " đã được mã hóa, bỏ qua.");
//            }
//        }
//    }
//
//    private boolean isPasswordEncrypted(String password) {
//        return password != null && password.startsWith("$2a$");
//    }
//}
