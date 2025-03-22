package com.example.gbsports.service;

import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.entity.MauSac;
import com.example.gbsports.repository.MauSacRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
                .filter(mauSac -> mauSac.getTen_mau_sac().equalsIgnoreCase(tenMauSac))
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
}
