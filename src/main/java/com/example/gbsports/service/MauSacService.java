package com.example.gbsports.service;

import com.example.gbsports.entity.MauSac;
import com.example.gbsports.repository.MauSacRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MauSacService {
    @Autowired
    MauSacRepo mauSacRepo;

    public List<MauSac> listFindAllMauSac() {
        return mauSacRepo.findAll();
    }
}
