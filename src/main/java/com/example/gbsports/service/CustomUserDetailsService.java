package com.example.gbsports.service;

import com.example.gbsports.entity.TaiKhoan;
import com.example.gbsports.repository.TaiKhoanRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TaiKhoanRepo taiKhoanRepo;

    public CustomUserDetailsService(TaiKhoanRepo taiKhoanRepo) {
        this.taiKhoanRepo = taiKhoanRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user: " + username);
        TaiKhoan taiKhoan = taiKhoanRepo.findByTenDangNhap(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        System.out.println("Found user: " + taiKhoan.getTen_dang_nhap() + ", Role: " + taiKhoan.getRoles().getMa_roles());
        return new User(
                taiKhoan.getTen_dang_nhap(),
                taiKhoan.getMat_khau(),
                Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(taiKhoan.getRoles().getMa_roles()))
        );
    }
}