package com.example.gbsports.service;

import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.repository.DanhMucRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DanhMucService {
    @Autowired
    DanhMucRepo danhMucRepo;

    public List<DanhMuc> listFindAllDanhMuc() {
        return danhMucRepo.findAll();
    }
}
