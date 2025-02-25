package com.example.gbsports.controller;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.repository.HoaDonChiTietRepo;
import com.example.gbsports.repository.HoaDonRepo;
import com.example.gbsports.response.HoaDonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/hoa_don")
public class HoaDonController {

    @Autowired
    private HoaDonRepo hoaDonRepo;
    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepo;
    @Autowired
//    private HoaDonService hoaDonService;

    @GetMapping("/danh_sach_hoa_don")
    public List<HoaDon> getAllHD(){
        return hoaDonRepo.findAll();
    }

    @GetMapping("/phan_trang")
    public List<HoaDonResponse> phanTrang(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "5") Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<HoaDonResponse> list = hoaDonRepo.phanTrang(pageable);
        return list.getContent();
    }
    @GetMapping("/tim_kiem")
    public List<HoaDonResponse> search(@RequestParam(name = "maHoaDon", required = false) String maHoaDon,
                                       @RequestParam(name = "maNhanVien", required = false) String maNhanVien,
                                       @RequestParam(name = "sdtNguoiNhan", required = false) String sdtNguoiNhan){
        return hoaDonRepo.timHoaDon(maHoaDon, maNhanVien, sdtNguoiNhan);
    }

    @GetMapping("/loc_ngay")
    public List<HoaDonResponse> locTheoNgay(@RequestParam(name = "tuNgay") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tuNgay,
                                            @RequestParam(name = "denNgay") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date denNgay){
        return hoaDonRepo.findHoaDonByNgay(tuNgay, denNgay);
    }

    @GetMapping("/loc_trang_thai_don_hang")
    public List<HoaDonResponse> getHoaDonByTrangThaiGiaoHang(@RequestParam(name = "trangThai", required = false) String trangThai) {
        return hoaDonRepo.findHoaDonByTrangThaiGiaoHang(trangThai);
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
