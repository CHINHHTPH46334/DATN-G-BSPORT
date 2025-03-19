package com.example.gbsports.service;

import com.example.gbsports.entity.ThuongHieu;
import com.example.gbsports.repository.ThuongHieuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        newThuongHieu.setNgay_tao(new Date());
        newThuongHieu.setNgay_sua(new Date());
        return newThuongHieu;
    }
}
