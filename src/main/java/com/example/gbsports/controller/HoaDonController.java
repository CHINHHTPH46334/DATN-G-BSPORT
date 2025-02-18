package com.example.gbsports.controller;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.repository.HoaDonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hoa_don")
public class HoaDonController {

    @Autowired
    private HoaDonRepo hoaDonRepo;

    @GetMapping
    public List<HoaDon> getAllHD(){
        return hoaDonRepo.findAll();
    }
}
