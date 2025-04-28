package com.example.gbsports.service;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.repository.HoaDonRepo;
import com.itextpdf.text.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class HoaDonService {
    @Autowired
    private HoaDonRepo hoaDonRepo;


    public List<HoaDon> getHoaDonByKhachHangId(Integer idKhachHang) {
        return hoaDonRepo.findByKhachHang_IdKhachHang(idKhachHang);
    }

    public int countHoaDonByKhachHangId(Integer idKhachHang) {
        return hoaDonRepo.countByKhachHangId(idKhachHang);
    }
}

