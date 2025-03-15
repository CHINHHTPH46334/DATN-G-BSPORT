package com.example.gbsports.controller;

import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.entity.MauSac;
import com.example.gbsports.repository.MauSacRepo;
import com.example.gbsports.service.DanhMucService;
import com.example.gbsports.service.MauSacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RequestMapping("/admin/quan_ly_san_pham")
public class MauSacController {
    @Autowired
    MauSacService mauSacService;
    @GetMapping("/MauSac")
    public List<MauSac> getAllMauSac(){
        return mauSacService.listFindAllMauSac();
    }
}
