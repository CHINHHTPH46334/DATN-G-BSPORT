package com.example.gbsports.service;

import com.example.gbsports.entity.KichThuoc;
import com.example.gbsports.repository.KichThuocRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KichThuocService {
    @Autowired
    KichThuocRepo kichThuocRepo;

    public List<KichThuoc> listFindAllKichThuoc() {
        return kichThuocRepo.findAll();
    }
}
