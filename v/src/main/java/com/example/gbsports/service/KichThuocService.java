package com.example.gbsports.service;

import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.entity.KichThuoc;
import com.example.gbsports.repository.KichThuocRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class KichThuocService {
    @Autowired
    KichThuocRepo kichThuocRepo;

    public List<KichThuoc> listFindAllKichThuoc() {
        return kichThuocRepo.findAll();
    }
    public KichThuoc getKichThuocOrCreateKichThuoc(String giaTri, String donVi){
        Optional<KichThuoc> exitingKichThuoc = kichThuocRepo.findAll().stream()
                .filter(kichThuoc -> giaTri.equalsIgnoreCase(Optional.ofNullable(kichThuoc.getGia_tri()).orElse("")))
                .findFirst();

        if (exitingKichThuoc.isPresent()) {
            return exitingKichThuoc.get();
        }

        // Nếu không tìm thấy, tạo mã mới
        int maxNumber = kichThuocRepo.findAll().stream()
                .map(KichThuoc::getMa_kich_thuoc)
                .filter(ma -> ma.startsWith("KT0"))
                .map(ma -> ma.substring(3))
                .filter(num -> num.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        // Tạo đối tượng mới
        KichThuoc newKichThuoc = new KichThuoc();
        newKichThuoc.setMa_kich_thuoc("KT0" + (maxNumber + 1));
        newKichThuoc.setGia_tri(giaTri);
        newKichThuoc.setDon_vi(donVi);
//        newKichThuoc.setNgay_tao(LocalDateTime.now());
        newKichThuoc.setTrang_thai("Hoạt động");
        kichThuocRepo.save(newKichThuoc);
        return newKichThuoc;
    }
}
