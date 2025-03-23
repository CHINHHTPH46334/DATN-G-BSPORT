package com.example.gbsports.service;

import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.repository.DanhMucRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        newDanhMuc.setNgay_tao(new Date());
        newDanhMuc.setNgay_sua(new Date());
        danhMucRepo.save(newDanhMuc);
        return newDanhMuc;
    }
}
