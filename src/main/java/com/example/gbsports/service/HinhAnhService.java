package com.example.gbsports.service;

import com.example.gbsports.repository.HinhAnhSanPhamRepo;
import com.example.gbsports.response.HinhAnhView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
public class HinhAnhService {
    @Autowired
    HinhAnhSanPhamRepo hinhAnhSanPhamRepo;
    public List<HinhAnhView> listAnhTheoCTSP(@RequestParam("idCTSP") Integer id,
                                             @RequestParam("anhChinh") Boolean anhChinh) {
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
