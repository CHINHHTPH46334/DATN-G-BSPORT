package com.example.gbsports.controller;

import com.example.gbsports.ImportAndExportEx.ChiTietSanPhamValidate;
import com.example.gbsports.ImportAndExportEx.ExcelExport;
import com.example.gbsports.ImportAndExportEx.ExcelSaveDB;
import com.example.gbsports.ImportAndExportEx.Excelmport;
import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.entity.SanPham;
import com.example.gbsports.request.ChiTietSanPhamRequest;
import com.example.gbsports.request.SanPhamRequest;
import com.example.gbsports.response.SanPhamView;
import com.example.gbsports.response.ChiTietSanPhamView;
import com.example.gbsports.service.ChiTietSanPhamService;
import com.example.gbsports.service.SanPhamService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RequestMapping("/admin/quan_ly_san_pham/")
public class SanPhamController {
    @Autowired
    SanPhamService sanPhamService;
    @Autowired
    ChiTietSanPhamService chiTietSanPhamService;
    @Autowired
    Excelmport excelmport;

    @GetMapping("/SanPham")
    public List<SanPhamView> getAll() {
        return sanPhamService.getAll();
    }

    @GetMapping("/SanPhamFindAll")
    public List<SanPham> getAllfindAll() {
        return sanPhamService.getAllFindAll();
    }
    @GetMapping("/sanPhamTheoNgaySua")
    public List<SanPhamView> getAllSPTheoNgay(){
        return sanPhamService.getAllSPNgaySua();
    }
    @GetMapping("/allSanPham")
    public List<SanPhamView> getAll(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                    @RequestParam(name = "size", defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamService.getAllPhanTrang(pageable).getContent();
    }
    @GetMapping("/sanPhamDetail")
    public SanPham spDetail(@RequestParam ("id") Integer id){
        return sanPhamService.detailSP(id);
    }
    @PostMapping("/saveSanPham2")
    public ResponseEntity<?> addSanPham2(@Valid @RequestBody SanPhamRequest sanPhamRequest, BindingResult bindingResult) {
        return sanPhamService.saveSanPham2(sanPhamRequest, bindingResult);
    }

    @PostMapping("/saveSanPham")
    public ResponseEntity<?> addSanPham(@RequestBody SanPhamRequest sanPhamRequest) {
        return sanPhamService.saveSanPham(sanPhamRequest);
    }

    @PostMapping("/xoaSanPham")
    public String xoaSanPham(@RequestParam("id") Integer id) {
        return sanPhamService.deleteSanPham(id);
    }

    @GetMapping("/timKiemSanPham")
    public List<SanPham> searchSanPham(@RequestParam("search") String search) {
        return sanPhamService.listTimKiem(search);
    }

    @PutMapping("/chuyenTrangThaiSanPham")
    public String chuyenTrangThaiSanPham(@RequestParam("id") Integer id) {
        return sanPhamService.chuyenTrangThai(id);
    }

    @GetMapping("/locSanPham")
    public List<SanPhamView> locSanPham(
            @RequestParam(value = "danhMuc", required = false) String danhMuc,
            @RequestParam(value = "thuongHieu", required = false) String thuongHieu,
            @RequestParam(value = "chatLieu", required = false) String chatLieu
    ) {
        return sanPhamService.locSanPham(danhMuc, thuongHieu, chatLieu);
    }

    @GetMapping("/sapXep")
    public List<SanPhamView> sapXep(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size,
            @RequestParam(name = "tieuChi", required = false) String tieuChi
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(tieuChi).ascending());
        return sanPhamService.sapXep(pageable).getContent();
    }

    @GetMapping("/exportExcel")
    public ResponseEntity<byte[]> exportExcel() {
        List<ChiTietSanPhamView> list = chiTietSanPhamService.getAllCTSP();
        ByteArrayInputStream in = ExcelExport.sanPhamToExcel(list);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sanpham.xlsx")
                .body(in.readAllBytes());
    }

    @PostMapping("/listImport")
    public ResponseEntity<?> getListImport(@RequestParam("file") MultipartFile file) throws IOException {
        ArrayList<ChiTietSanPham> list = excelmport.readExcel(file);
        return ResponseEntity.ok(list);
    }

    @Autowired
    ChiTietSanPhamValidate chiTietSanPhamValidate;

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestBody List<ChiTietSanPham> list) {
        List<String> errors = chiTietSanPhamValidate.validate(list);
        return errors.isEmpty() ? ResponseEntity.ok("Hợp lệ") : ResponseEntity.badRequest().body(errors);
    }

    @Autowired
    ExcelSaveDB excelSaveDB;

    @PostMapping("/save")
    public ResponseEntity<?> saveToDB(@RequestBody List<ChiTietSanPham> list) {
        excelSaveDB.saveToDB(list);
        return ResponseEntity.ok("ok");
    }
    @GetMapping("/getSanPhamByTenSanPham")
    public List<SanPhamView> getSanPhamBySP(@RequestParam("tenSanPham")String tenSanPham){
        return sanPhamService.getSanPhamTheoTen(tenSanPham);
    }
    @GetMapping("/getAllCTSPKM")
    public List<ChiTietSanPhamView> getAllCTSPKM() {
        return sanPhamService.getAllCTSPKM();
    }
}
