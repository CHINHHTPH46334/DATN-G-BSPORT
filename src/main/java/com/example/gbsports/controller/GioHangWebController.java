package com.example.gbsports.controller;

import com.example.gbsports.repository.GioHangRepository;
import com.example.gbsports.repository.GioHangWebRepo;
import com.example.gbsports.response.GioHangWebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173/", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
@PreAuthorize("hasRole('ROLE_KH')")
@RequestMapping("/gioHangWeb")
public class GioHangWebController {
    @Autowired
    private GioHangWebRepo gioHangWebRepo;

    @GetMapping("/gioHangByKH")
    public List<GioHangWebResponse> listGHByKH(@RequestParam("idKhachHang") Integer idKhachHang) {
        return gioHangWebRepo.listGioHangByKhachHang(idKhachHang);
    }

    @GetMapping("/danhSachDiaChi")
    public List<GioHangWebResponse> listDiaChiByKH(@RequestParam("idKhachHang") Integer idKhachHang) {
        return gioHangWebRepo.listDiaChiByKH(idKhachHang);
    }
}
