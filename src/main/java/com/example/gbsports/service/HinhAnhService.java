package com.example.gbsports.service;

import com.example.gbsports.repository.HinhAnhSanPhamRepo;
import com.example.gbsports.respon.HinhAnhView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;

@Service
public class HinhAnhService {
    @Autowired
    HinhAnhSanPhamRepo hinhAnhSanPhamRepo;
    public ArrayList<HinhAnhView> listAnhTheoCTSP(@PathVariable Integer id, Boolean anhChinh) {
        ArrayList<HinhAnhView> listTam = new ArrayList<>();
        if (anhChinh == null){
            return hinhAnhSanPhamRepo.listHinhAnhTheoSanPham(id);
        }else {
            for (HinhAnhView hinhanh: hinhAnhSanPhamRepo.listHinhAnhTheoSanPham(id)) {
                if (hinhanh.getAnh_chinh()){
                    listTam.add(hinhanh);
                }
            }
            return listTam;
        }
    }
}
