package com.example.gbsports.controller;


import com.example.gbsports.entity.NhanVien;
import com.example.gbsports.repository.NhanVienRepo;
import com.example.gbsports.request.NhanVienRequest;
import com.example.gbsports.response.NhanVienResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class NhanVienController {
    @Autowired
    NhanVienRepo nhanVienRepo;

    @GetMapping("/findAll")
    public List<NhanVien> findAll() {
        return nhanVienRepo.findAll();
    }


    //    @GetMapping("/quan-ly-nhan-vien")
//    public List<NhanVienResponse> getAll(){
//        return nhanVienRepo.getAll();
//    }
    @GetMapping("/quan-ly-nhan-vien")
    public List<NhanVienResponse> phanTrang(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "size", defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NhanVienResponse> list = nhanVienRepo.listPT(pageable);
        return list.getContent();
    }

    @PostMapping("/add")
    public String add(@RequestBody NhanVienRequest nhanVienRequest) {
        NhanVien nhanVien = new NhanVien();
        BeanUtils.copyProperties(nhanVienRequest, nhanVien);
//        if (nhanVien.getMa_nhan_vien() == null
//                || nhanVien.getTen_nhan_vien() == null
//                || nhanVien.getNgay_sinh() == null
//                || nhanVien.getSo_dien_thoai() == null
//                || nhanVien.getEmail() == null
//                || nhanVien.getDia_chi_lien_he() == null
//                || nhanVien.getGioi_tinh() == null
//                || nhanVien.getTrang_thai() == null
//        ) {
//            return "Không được để trống";
//        } else {
        nhanVienRepo.save(nhanVien);
        return "Thêm thành công";
//        }
    }

    @PostMapping("/update")
    public String update(@RequestBody NhanVienRequest nhanVienRequest) {
        NhanVien nhanVien = new NhanVien();
        BeanUtils.copyProperties(nhanVienRequest, nhanVien);
        nhanVienRepo.save(nhanVien);
        return "Sửa thành công";
    }
    @PostMapping("/changeStatus")
    public String changeStatus(@RequestBody NhanVienRequest nhanVienRequest){
        if (nhanVienRequest.getTrang_thai().equals("Đang hoạt động")){
            nhanVienRequest.setTrang_thai("Đã nghỉ việc");
            NhanVien nhanVien = new NhanVien();
            BeanUtils.copyProperties(nhanVienRequest, nhanVien);
            nhanVienRepo.save(nhanVien);
        }
        else {
            nhanVienRequest.setTrang_thai("Đang hoạt động");
            NhanVien nhanVien = new NhanVien();
            BeanUtils.copyProperties(nhanVienRequest, nhanVien);
            nhanVienRepo.save(nhanVien);
        }
        return "Chuyển trạng thái thành công";
    }

    @GetMapping("/search")
    public List<NhanVienResponse> timNhanVien(@RequestParam(name = "keyword", required = false) String keyword) {
        return nhanVienRepo.timNhanVien(keyword);
    }

    @GetMapping("/locTrangThai")
    public List<NhanVienResponse> locNhanVien(@RequestParam(name = "trangThai", required = false) String trangThai) {
        return nhanVienRepo.locNhanVienTheoTrangThai(trangThai);
    }

}
