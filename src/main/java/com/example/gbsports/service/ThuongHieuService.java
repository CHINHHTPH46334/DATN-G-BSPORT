package com.example.gbsports.service;

import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.entity.ThuongHieu;
import com.example.gbsports.repository.ThuongHieuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ThuongHieuService {
    @Autowired
    ThuongHieuRepo thuongHieuRepo;

    public List<ThuongHieu> listFindAllThuongHieu() {
        return thuongHieuRepo.findAll();
    }
    public ThuongHieu getThuongHieuOrCreateThuongHieu(String tenThuongHieu){
        Optional<ThuongHieu> exitingThuongHieu = thuongHieuRepo.findAll().stream()
                .filter(thuongHieu -> thuongHieu.getTen_thuong_hieu().equalsIgnoreCase(tenThuongHieu))
                .findFirst();

        if (exitingThuongHieu.isPresent()) {
            return exitingThuongHieu.get();
        }

        // Nếu không tìm thấy, tạo mã mới
        int maxNumber = thuongHieuRepo.findAll().stream()
                .map(ThuongHieu::getMa_thuong_hieu)
                .filter(ma -> ma.startsWith("TH0"))
                .map(ma -> ma.substring(3))
                .filter(num -> num.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        // Tạo đối tượng mới
        ThuongHieu newThuongHieu = new ThuongHieu();
        newThuongHieu.setMa_thuong_hieu("TH0" + (maxNumber + 1));
        newThuongHieu.setTen_thuong_hieu(tenThuongHieu);
        newThuongHieu.setTrang_thai("Hoạt động");
        newThuongHieu.setNgay_tao(LocalDateTime.now());
        newThuongHieu.setNgay_sua(LocalDateTime.now());
        thuongHieuRepo.save(newThuongHieu);
        return newThuongHieu;
    }

    public ResponseEntity<?> changeTrangThaiThuongHieu(@RequestParam("idThuongHieu") Integer idThuongHieu) {
        if (idThuongHieu != null) {
            ThuongHieu thuongHieu = thuongHieuRepo.findById(idThuongHieu).get();
            if (thuongHieu.getTrang_thai().equals("Hoạt động")) {
                thuongHieu.setTrang_thai("Không hoạt động");
            } else {
                thuongHieu.setTrang_thai("Hoạt động");
            }
            thuongHieu.setNgay_sua(LocalDateTime.now());
            thuongHieuRepo.save(thuongHieu);
            return ResponseEntity.ok(thuongHieu);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("messege", "Lỗi null");
            return ResponseEntity.badRequest().body(error);
        }
    }

    public ResponseEntity<?> updateThuongHieu(@RequestBody ThuongHieu danhMuc){
        if (danhMuc.getId_thuong_hieu() != null){
            ThuongHieu danhMucSua = thuongHieuRepo.findById(danhMuc.getId_thuong_hieu()).get();
            danhMucSua.setTen_thuong_hieu(danhMuc.getTen_thuong_hieu());
            danhMucSua.setNgay_sua(LocalDateTime.now());
            thuongHieuRepo.save(danhMucSua);
            return ResponseEntity.ok(danhMucSua);
        }else {
            Map<String, String> error = new HashMap<>();
            error.put("messege","Lỗi id null");
            return ResponseEntity.badRequest().body(error);
        }
    }
}
