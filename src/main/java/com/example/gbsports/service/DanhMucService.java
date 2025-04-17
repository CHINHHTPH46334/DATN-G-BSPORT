package com.example.gbsports.service;

import com.example.gbsports.entity.ChatLieu;
import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.repository.DanhMucRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DanhMucService {
    @Autowired
    DanhMucRepo danhMucRepo;

    public List<DanhMuc> listFindAllDanhMuc() {
        return danhMucRepo.findAll();
    }
    public DanhMuc getDanhMucOrCreateDanhMuc(String tenDanhMuc){
        Optional<DanhMuc> exitingDanhMuc = danhMucRepo.findAll().stream()
                .filter(danhMuc -> tenDanhMuc.equalsIgnoreCase(Optional.ofNullable(danhMuc.getTen_danh_muc()).orElse("")))
                .findFirst();

        if (exitingDanhMuc.isPresent()) {
            return exitingDanhMuc.get();
        }

        // Nếu không tìm thấy, tạo mã mới
        int maxNumber = danhMucRepo.findAll().stream()
                .map(DanhMuc::getMa_danh_muc)
                .filter(ma -> ma.startsWith("DM0"))
                .map(ma -> ma.substring(3))
                .filter(num -> num.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        // Tạo đối tượng mới
        DanhMuc newDanhMuc = new DanhMuc();
        newDanhMuc.setMa_danh_muc("DM0" + (maxNumber + 1));
        newDanhMuc.setTen_danh_muc(tenDanhMuc);
        newDanhMuc.setTrang_thai("Hoạt động");
        newDanhMuc.setNgay_tao(LocalDateTime.now());
        newDanhMuc.setNgay_sua(LocalDateTime.now());
        danhMucRepo.save(newDanhMuc);
        return newDanhMuc;
    }

    public ResponseEntity<?> changeTrangThaiDanhMuc(@RequestParam("idDanhMuc") Integer idDanhMuc) {
        if (idDanhMuc != null) {
            DanhMuc danhMuc = danhMucRepo.findById(idDanhMuc).get();
            if (danhMuc.getTrang_thai().equals("Hoạt động")) {
                danhMuc.setTrang_thai("Không hoạt động");
            } else {
                danhMuc.setTrang_thai("Hoạt động");
            }
            danhMuc.setNgay_sua(LocalDateTime.now());
            danhMucRepo.save(danhMuc);
            return ResponseEntity.ok(danhMuc);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("messege", "Lỗi null");
            return ResponseEntity.badRequest().body(error);
        }
    }

    public ResponseEntity<?> updateDanhMuc(@RequestBody DanhMuc danhMuc){
        if (danhMuc.getId_danh_muc() != null){
            DanhMuc danhMucSua = danhMucRepo.findById(danhMuc.getId_danh_muc()).get();
            danhMucSua.setTen_danh_muc(danhMuc.getTen_danh_muc());
            danhMucSua.setNgay_sua(LocalDateTime.now());
            danhMucRepo.save(danhMucSua);
            return ResponseEntity.ok(danhMucSua);
        }else {
            Map<String, String> error = new HashMap<>();
            error.put("messege","Lỗi id null");
            return ResponseEntity.badRequest().body(error);
        }
    }
}
