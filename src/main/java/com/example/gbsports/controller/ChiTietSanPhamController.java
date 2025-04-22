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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:5173",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})

@RequestMapping("/admin/quan_ly_san_pham")
public class ChiTietSanPhamController {
    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @GetMapping("/getAllCTSP")
    public List<ChiTietSanPhamView> getAllCTSP() {
        return chiTietSanPhamService.getAllCTSP();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @GetMapping("/getAllCTSPFindAll")
    public List<ChiTietSanPham> getAllCTSPFindAll() {
        return chiTietSanPhamService.getAllCTSPFindAll();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @GetMapping("/getAllCTSPPhanTrang")
    public List<ChiTietSanPhamView> phanTrang(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return chiTietSanPhamService.getAllCTSPPhanTrang(pageable).getContent();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PostMapping("/saveCTSP")
    public ResponseEntity<?> saveCTSP(@Valid @RequestBody ChiTietSanPhamRequest chiTietSanPhamRequest, BindingResult result) {
        return chiTietSanPhamService.saveChiTietSanPham(chiTietSanPhamRequest, result);
    }

    @PostMapping("/deleteCTSP/{id}")
    public String deleteCTSP(@PathVariable Integer id) {
        return chiTietSanPhamService.deleteChiTietSanPham(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PutMapping("/changeStatusCTSP")
    public ResponseEntity<?> changeStatus(@RequestParam("id") Integer id) {
        System.out.println("Chạy vào đây");
        return chiTietSanPhamService.chuyenTrangThai(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @GetMapping("/searchCTSP")
    public ArrayList<ChiTietSanPhamView> search(@RequestParam(name = "keyword") String keyword) {
        return chiTietSanPhamService.listTimKiem(keyword);
    }

    @GetMapping("/locCTSP")
    public ArrayList<ChiTietSanPhamView> locCTSP(@RequestParam(name = "tenSanPham", required = false) String tenSanPham,
                                                 @RequestParam(name = "giaBanMin", required = false) Float giaBanMin,
                                                 @RequestParam(name = "giaBanMax", required = false) Float giaBanMax,
                                                 @RequestParam(name = "soLuongMin", required = false) Integer soLuongMin,
                                                 @RequestParam(name = "soLuongMax", required = false) Integer soLuongMax,
                                                 @RequestParam(name = "trangThai", required = false) String trangThai,
                                                 @RequestParam(name = "tenMauSac", required = false) String tenMauSac,
                                                 @RequestParam(name = "tenDanhMuc", required = false) String tenDanhMuc,
                                                 @RequestParam(name = "tenThuongHieu", required = false) String tenThuongHieu,
                                                 @RequestParam(name = "tenChatLieu", required = false) String tenChatLieu) {
        System.out.println("Giá max" + giaBanMax);
        if (giaBanMin == null || giaBanMin < 0) {
            giaBanMin = (float) 0;
        }
        if (giaBanMax == null || giaBanMax < 0) {
            giaBanMax = Float.MAX_VALUE;
            System.out.println("Giá max" + giaBanMax);
        }
        if (giaBanMin >= giaBanMax) {
            float giaTrungGian;
            giaTrungGian = giaBanMin;
            giaBanMin = giaBanMax;
            giaBanMax = giaTrungGian;
        }

        return chiTietSanPhamService.listLocCTSP(tenSanPham, giaBanMin, giaBanMax, soLuongMin, soLuongMax,
                trangThai, tenMauSac, tenDanhMuc, tenThuongHieu, tenChatLieu);
    }

    @GetMapping("/sapXepCTSP")
    public List<ChiTietSanPhamView> sapXepCTSP(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", defaultValue = "5") Integer size,
                                               @RequestParam(name = "tieuChi") String tieuChi) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(tieuChi).ascending());
        return chiTietSanPhamService.sapXep(pageable).getContent();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @GetMapping("/CTSPTheoSanPham")
    public List<ChiTietSanPhamView> ctspTheoSanPham(@RequestParam(name = "id") Integer id) {
        return chiTietSanPhamService.listCTSPTheoSanPham(id);
    }
    // public
    @GetMapping("/CTSPBySanPhamFullWeb")
    public List<ChiTietSanPhamView> ctspBySanPhamFull(@RequestParam("idSanPham")Integer idSanPham){
        return chiTietSanPhamService.getCTSPBySanPhamFull(idSanPham);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PutMapping("/changeAllCTSPHoatDong")
    public ResponseEntity<?> allCTSPHoatDong(@RequestParam("id")Integer id){
        return chiTietSanPhamService.changeAllCTSPHoatDong(id);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PutMapping("/changeAllCTSPKhongHoatDong")
    public ResponseEntity<?> allCTSPKhongHoatDong(@RequestParam("id")Integer id){
        return chiTietSanPhamService.changeAllCTSPKhongHoatDong(id);
    }
}
