package com.example.gbsports.controller;

import com.example.gbsports.response.HinhAnhView;
import com.example.gbsports.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
//@PreAuthorize("hasAnyRole('ADMIN', 'QL', 'NV')")
@RequestMapping("/admin/quan_ly_san_pham")
public class HinhAnhController {
    @Autowired
    HinhAnhService hinhAnhService;
    @GetMapping("/HinhAnhSanPham")
    public ArrayList<HinhAnhView> listHATheoCTSP(@RequestParam(name = "idCTSP") Integer id,
                                                 @RequestParam(name = "anhChinh", required = false) Boolean anhChinh){
        return hinhAnhService.listAnhTheoCTSP(id,anhChinh);
    }

}
