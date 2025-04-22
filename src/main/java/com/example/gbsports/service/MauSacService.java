package com.example.gbsports.service;

import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.entity.MauSac;
import com.example.gbsports.entity.ThuongHieu;
import com.example.gbsports.repository.MauSacRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MauSacService {
    @Autowired
    MauSacRepo mauSacRepo;

    public List<MauSac> listFindAllMauSac() {
        return mauSacRepo.findAll();
    }

    public MauSac getMauSacOrCreateMauSac(String tenMauSac) {
        Optional<MauSac> exitingMauSac = mauSacRepo.findAll().stream()
                .filter(mauSac -> tenMauSac.equalsIgnoreCase(Optional.ofNullable(mauSac.getTen_mau_sac()).orElse("")))
                .findFirst();

        if (exitingMauSac.isPresent()) {
            return exitingMauSac.get();
        }

        // Nếu không tìm thấy, tạo mã mới
        int maxNumber = mauSacRepo.findAll().stream()
                .map(MauSac::getMa_mau_sac)
                .filter(ma -> ma.startsWith("MS0"))
                .map(ma -> ma.substring(3))
                .filter(num -> num.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        // Tạo đối tượng mới
        MauSac newMauSac = new MauSac();
        newMauSac.setMa_mau_sac("MS0" + (maxNumber + 1));
        newMauSac.setTen_mau_sac(tenMauSac);
        newMauSac.setTrang_thai("Hoạt động");
        mauSacRepo.save(newMauSac);
        return newMauSac;
    }

    public ResponseEntity<?> changeTrangThaiMauSac(@RequestParam("idMauSac") Integer idMauSac) {
        if (idMauSac != null) {
            MauSac mauSac = mauSacRepo.findById(idMauSac).get();
            if (mauSac.getTrang_thai().equals("Hoạt động")) {
                mauSac.setTrang_thai("Không hoạt động");
            } else {
                mauSac.setTrang_thai("Hoạt động");
            }
            mauSacRepo.save(mauSac);
            return ResponseEntity.ok(mauSac);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("messege", "Lỗi null");
            return ResponseEntity.badRequest().body(error);
        }
    }

    public ResponseEntity<?> updateMauSac(@RequestBody MauSac mauSac) {
        if (mauSac.getId_mau_sac() != null) {
            MauSac mauSacSua = mauSacRepo.findById(mauSac.getId_mau_sac()).get();
            mauSacSua.setTen_mau_sac(mauSac.getTen_mau_sac());
            mauSacRepo.save(mauSacSua);
            return ResponseEntity.ok(mauSacSua);
        }else {
            Map<String, String> error = new HashMap<>();
            error.put("messege","Lỗi id null");
            return ResponseEntity.badRequest().body(error);
        }
    }
}
