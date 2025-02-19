package com.example.gbsports.service;


import com.example.gbsports.entity.SanPham;
import com.example.gbsports.repository.ChiTietSanPhamRepo;
import com.example.gbsports.repository.SanPhamRepo;
import com.example.gbsports.request.SanPhamRequest;
import com.example.gbsports.response.SanPhamView;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SanPhamService {
    @Autowired
    SanPhamRepo sanPhamRepo;
    @Autowired
    ChiTietSanPhamRepo chiTietSanPhamRepo;

    public ArrayList<SanPhamView> getAll() {
        return sanPhamRepo.getAllSanPham();
    }

    public List<SanPham> getAllFindAll() {
        return sanPhamRepo.findAll();
    }

    public Page<SanPhamView> getAllPhanTrang(Pageable pageable) {
        return sanPhamRepo.getAllSanPhamPhanTrang(pageable);
    }

    public ResponseEntity<?> saveSanPham(@Valid @RequestBody SanPhamRequest sanPhamRequest, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream().map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        } else {
            SanPham sanPham = new SanPham();
            BeanUtils.copyProperties(sanPhamRequest, sanPham);
            sanPhamRepo.save(sanPham);
            return ResponseEntity.ok("Lưu thành công");
        }
    }

    public String deleteSanPham(@PathVariable Integer id) {
        SanPham spDelete = new SanPham();
        for (SanPham sp : sanPhamRepo.findAll()) {
            if (sp.getId_san_pham() == id) {
                spDelete = sp;
                spDelete.setTrang_thai("Không hoạt động");
            }
        }
        sanPhamRepo.save(spDelete);
        return "Xóa thành công";
    }

    public String chuyenTrangThai(@PathVariable Integer id) {
        SanPham spDelete = new SanPham();
        for (SanPham sp : sanPhamRepo.findAll()) {
            if (sp.getId_san_pham() == id) {
                spDelete = sp;
            }
        }
        if (spDelete.getTrang_thai().equalsIgnoreCase("Hoạt động")) {
            spDelete.setTrang_thai("Không hoạt động");
        } else {
            spDelete.setTrang_thai("Hoạt động");
        }
        return "Chuyển trạng thái thành công";
    }

    public ArrayList<SanPham> listTimKiem(String search) {
        ArrayList<SanPham> listTam = new ArrayList<>();
        for (SanPham sp : sanPhamRepo.findAll()) {
            if (sp.getMa_san_pham().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)) ||
                    sp.getTen_san_pham().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)) ||
                    sp.getChatLieu().getTen_chat_lieu().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)) ||
                    sp.getDanhMuc().getTen_danh_muc().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)) ||
                    sp.getThuongHieu().getTen_thuong_hieu().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) {
                listTam.add(sp);
            }
        }
        return listTam;
    }

}
