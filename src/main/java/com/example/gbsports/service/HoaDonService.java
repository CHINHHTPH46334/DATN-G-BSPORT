package com.example.gbsports.service;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.repository.HoaDonRepo;
import com.example.gbsports.response.HoaDonResponse;
import com.itextpdf.text.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class HoaDonService {
    @Autowired
    private HoaDonRepo hoaDonRepo;


    // lềnh thay đổi
    public List<HoaDonResponse> getHoaDonByKhachHangId(Integer idKhachHang) {
        return hoaDonRepo.findHoaDonWithLatestStatusByKhachHangId(idKhachHang);
    }

    public int countHoaDonByKhachHangId(Integer idKhachHang) {
        return hoaDonRepo.countByKhachHangId(idKhachHang);
    }
}

