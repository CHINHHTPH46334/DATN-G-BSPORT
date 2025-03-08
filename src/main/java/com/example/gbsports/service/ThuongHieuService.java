package com.example.gbsports.service;

import com.example.gbsports.entity.ThuongHieu;
import com.example.gbsports.repository.ThuongHieuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThuongHieuService {
    @Autowired
    ThuongHieuRepo thuongHieuRepo;

    public List<ThuongHieu> listFindAllThuongHieu() {
        return thuongHieuRepo.findAll();
    }
}
