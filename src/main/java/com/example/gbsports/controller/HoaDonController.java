package com.example.gbsports.controller;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.repository.HoaDonChiTietRepo;
import com.example.gbsports.repository.HoaDonRepo;
import com.example.gbsports.response.HoaDonResponse;
//import com.example.gbsports.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/hoa_don")
public class HoaDonController {

    @Autowired
    private HoaDonRepo hoaDonRepo;
    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepo;
//    @Autowired
//    private HoaDonService hoaDonService;

    @GetMapping("/danh_sach_hoa_don")
    public String getAllHD(Model model,
                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                           @RequestParam(value = "size", defaultValue = "3") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HoaDon> list = hoaDonRepo.findAll(pageable);
        if (page == (list.getTotalPages() - 1)) {
            page = list.getTotalPages() - 1;
            model.addAttribute("pagephai", page);
        } else {
            model.addAttribute("pagephai", page + 1);
        }
        if (page == 0) {
            page = 0;
            model.addAttribute("pagetrai", page);
        } else {
            model.addAttribute("pagetrai", page - 1);
        }
        model.addAttribute("pagemax", list.getTotalPages() - 1);
        model.addAttribute("hd", list.getContent());
        return "hoa_don";
    }

//    @GetMapping("/phan_trang")
//    public String phanTrang(@RequestParam(value = "page", defaultValue = "0") Integer page,
//                            @RequestParam(value = "size", defaultValue = "3") Integer size, Model model) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<HoaDonResponse> list = hoaDonRepo.phanTrang(pageable);
//        if (page == (list.getTotalPages() - 1)) {
//            page = list.getTotalPages() - 1;
//            model.addAttribute("pagephai", page);
//        } else {
//            model.addAttribute("pagephai", page + 1);
//        }
//        if (page == 0) {
//            page = 0;
//            model.addAttribute("pagetrai", page);
//        } else {
//            model.addAttribute("pagetrai", page - 1);
//        }
//        model.addAttribute("hd", list.getContent());
//        return "hoa_don";
//    }

    @GetMapping("/tim_kiem")
    public String search(@RequestParam(name = "keyword", required = false) String keyword, Model model,
                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                         @RequestParam(value = "size", defaultValue = "3") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HoaDon> list;
        if (keyword == null || keyword.isEmpty()) {
            list = hoaDonRepo.findAll(pageable);
        } else {
            list = hoaDonRepo.timHoaDon(keyword, pageable);
        }
        if (page == (list.getTotalPages() - 1)) {
            page = list.getTotalPages() - 1;
            model.addAttribute("pagephai", page);
        } else {
            model.addAttribute("pagephai", page + 1);
        }
        if (page == 0) {
            page = 0;
            model.addAttribute("pagetrai", page);
        } else {
            model.addAttribute("pagetrai", page - 1);
        }
        model.addAttribute("pagemax", list.getTotalPages() - 1);
        model.addAttribute("hd", list.getContent());
        System.out.println("keyword: "+ keyword);
        System.out.println("size"+ list.getContent().size());
        return "hoa_don";
    }

    @GetMapping("/loc_ngay")
    public String locTheoNgay(@RequestParam(name = "tuNgay") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tuNgay,
                              @RequestParam(name = "denNgay") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date denNgay, Model model) {
        model.addAttribute("hd", hoaDonRepo.findHoaDonByNgay(tuNgay, denNgay));
        return "hoa_don";
    }

    @GetMapping("/loc_trang_thai_don_hang")
    public String getHoaDonByTrangThaiGiaoHang(@RequestParam(name = "trangThai", required = false) String trangThai, Model model) {
        if (trangThai == null || trangThai.isEmpty()) {
            model.addAttribute("hd", hoaDonRepo.findAll());
        } else {
            trangThai.trim();
            model.addAttribute("hd", hoaDonRepo.findHoaDonByTrangThaiGiaoHang(trangThai));
        }
        model.addAttribute("selectedTT", trangThai);
        System.out.println("trạng thái: " + trangThai);
        System.out.println("List: " + hoaDonRepo.findHoaDonByTrangThaiGiaoHang(trangThai).size());
        return "hoa_don";
    }

//    @GetMapping("/xuat_hoa_don")
//    public ResponseEntity<byte[]> exportHoaDonPDF(@RequestParam("maHD") Integer maHoaDon) {
//        byte[] pdfBytes = hoaDonService.generateHoaDonPDF(maHoaDon);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDisposition(ContentDisposition.attachment()
//                .filename("HOA_DON_" + maHoaDon + ".pdf")
//                .build());
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(pdfBytes);
//    }
}
