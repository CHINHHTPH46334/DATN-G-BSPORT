package com.example.gbsports.controller;

import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.request.ChiTietSanPhamRequest;
import com.example.gbsports.response.ChiTietSanPhamView;
import com.example.gbsports.service.ChiTietSanPhamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/admin/quan_ly_san_pham")
public class ChiTietSanPhamController {
    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;

    @GetMapping("/getAllCTSP")
    public List<ChiTietSanPhamView> getAllCTSP() {
        return chiTietSanPhamService.getAllCTSP();
    }

    @GetMapping("/getAllCTSPFindAll")
    public List<ChiTietSanPham> getAllCTSPFindAll() {
        return chiTietSanPhamService.getAllCTSPFindAll();
    }

    @GetMapping("/getAllCTSPPhanTrang")
    public List<ChiTietSanPhamView> phanTrang(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return chiTietSanPhamService.getAllCTSPPhanTrang(pageable).getContent();
    }

    @PostMapping("/saveCTSP")
    public ResponseEntity<?> saveCTSP(@Valid @RequestBody ChiTietSanPhamRequest chiTietSanPhamRequest, BindingResult result) {
        return chiTietSanPhamService.saveChiTietSanPham(chiTietSanPhamRequest, result);
    }

    @PostMapping("/deleteCTSP/{id}")
    public String deleteCTSP(@PathVariable Integer id) {
        return chiTietSanPhamService.deleteChiTietSanPham(id);
    }

    @PutMapping("/changeStatusCTSP")
    public ResponseEntity<?> changeStatus(@RequestParam("id") Integer id) {
        System.out.println("Chạy vào đây");
        return chiTietSanPhamService.chuyenTrangThai(id);
    }

    @GetMapping("/searchCTSP")
    public ArrayList<ChiTietSanPhamView> search(@RequestParam(name = "keyword") String keyword) {
        return chiTietSanPhamService.listTimKiem(keyword);
    }

    @GetMapping("/locCTSP")
    public ResponseEntity<List<ChiTietSanPhamView>>  locCTSP(@RequestParam(name = "keyword", required = false) String keyword,
                                                            @RequestParam(name = "tenSanPham", required = false) String tenSanPham,
                                                            @RequestParam(name = "giaBanMin", required = false) Float giaBanMin,
                                                            @RequestParam(name = "giaBanMax", required = false) Float giaBanMax,
                                                            @RequestParam(name = "soLuongMin", required = false) Integer soLuongMin,
                                                            @RequestParam(name = "soLuongMax", required = false) Integer soLuongMax,
                                                            @RequestParam(name = "trangThai", required = false) String trangThai,
                                                            @RequestParam(name = "listMauSac", required = false) List<String> listMauSac,
                                                            @RequestParam(name = "listDanhMuc", required = false) List<String> listDanhMuc,
                                                            @RequestParam(name = "listThuongHieu", required = false) List<String> listThuongHieu,
                                                            @RequestParam(name = "listChatLieu", required = false) List<String> listChatLieu,
                                                            @RequestParam(name = "listKichThuoc", required = false) List<String> listKichThuoc) {
        return chiTietSanPhamService.timKiemVaLoc(keyword, tenSanPham, giaBanMin, giaBanMax, soLuongMin, soLuongMax, trangThai, listMauSac, listDanhMuc, listThuongHieu
                , listChatLieu, listKichThuoc);
    }

    @GetMapping("/sapXepCTSP")
    public List<ChiTietSanPhamView> sapXepCTSP(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", defaultValue = "5") Integer size,
                                               @RequestParam(name = "tieuChi") String tieuChi) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(tieuChi).ascending());
        return chiTietSanPhamService.sapXep(pageable).getContent();
    }

    @GetMapping("/CTSPTheoSanPham")
    public List<ChiTietSanPhamView> ctspTheoSanPham(@RequestParam(name = "id") Integer id) {
        return chiTietSanPhamService.listCTSPTheoSanPham(id);
    }

    @GetMapping("/CTSPBySanPhamFullWeb")
    public List<ChiTietSanPhamView> ctspBySanPhamFull(@RequestParam("idSanPham") Integer idSanPham) {
        return chiTietSanPhamService.getCTSPBySanPhamFull(idSanPham);
    }

    @PutMapping("/changeAllCTSPHoatDong")
    public ResponseEntity<?> allCTSPHoatDong(@RequestParam("id") Integer id) {
        return chiTietSanPhamService.changeAllCTSPHoatDong(id);
    }

    @PutMapping("/changeAllCTSPKhongHoatDong")
    public ResponseEntity<?> allCTSPKhongHoatDong(@RequestParam("id") Integer id) {
        return chiTietSanPhamService.changeAllCTSPKhongHoatDong(id);
    }

    @GetMapping("/giaLonNhat")
    public BigDecimal getGiaLonNhat() {
        return chiTietSanPhamService.getMaxGiaBan();
    }
}
