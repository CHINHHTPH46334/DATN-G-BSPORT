package com.example.gbsports.controller;

import com.example.gbsports.entity.KhuyenMai;
import com.example.gbsports.repository.KhuyenMaiRepository;
import com.example.gbsports.request.KhuyenMaiRequest;
import com.example.gbsports.response.KhuyenMaiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class KhuyenMaiController {
    @Autowired
    KhuyenMaiRepository khuyenMaiRepository;

    @GetMapping("/hien-thi-KM")
    public List<KhuyenMai> hienThi() {
        return khuyenMaiRepository.findAll();
    }

    @GetMapping("/phan-trang-KM")
    public List<KhuyenMaiResponse> phanTrang(@RequestParam(value = "page" ,defaultValue = "0") Integer page ){
        Pageable pageable = PageRequest.of(page,5);
        return khuyenMaiRepository.phanTrang(pageable).getContent();
    }

    @PostMapping("/add-KM")
    public String add(@RequestBody KhuyenMaiRequest khuyenMaiRequest){
        KhuyenMai km = new KhuyenMai();
        BeanUtils.copyProperties(khuyenMaiRequest, km);
        if (km.getMaKhuyenMai() == null || km.getMaKhuyenMai().trim().isEmpty() ||
                km.getTenKhuyenMai() == null || km.getTenKhuyenMai().trim().isEmpty() ||
                km.getNgayBatDau() == null || km.getNgayHetHan() == null ||
                km.getGiaTriGiam() == null ||
                km.getKieuGiamGia() == null || km.getKieuGiamGia().trim().isEmpty()) {

            return "Thêm thất bại: Vui lòng nhập đầy đủ thông tin!";
        }
        if (khuyenMaiRepository.existsByMaKhuyenMai(km.getMaKhuyenMai())) {
            return "Thêm thất bại: Mã khuyến mãi đã tồn tại!";
        }
        khuyenMaiRepository.save(km);
        return "Thêm thành công!";
    }

    @PutMapping("/update-KM")
    public String update(@RequestBody KhuyenMaiRequest khuyenMaiRequest) {
        Optional<KhuyenMai> optionalKM = khuyenMaiRepository.findById(khuyenMaiRequest.getId());
        KhuyenMai km = optionalKM.get();
        BeanUtils.copyProperties(khuyenMaiRequest,km, "id");
        if (km.getMaKhuyenMai() == null || km.getMaKhuyenMai().trim().isEmpty() ||
                km.getTenKhuyenMai() == null || km.getTenKhuyenMai().trim().isEmpty() ||
                km.getNgayBatDau() == null || km.getNgayHetHan() == null ||
                km.getGiaTriGiam() == null ||
                km.getKieuGiamGia() == null || km.getKieuGiamGia().trim().isEmpty() ) {

            return "Thêm thất bại: Vui lòng nhập đầy đủ thông tin!";
        }

        khuyenMaiRepository.save(km);
        return "Cập nhật thành công";
    }
    @GetMapping("/detail-KM")
    public KhuyenMai detail(@RequestParam(value = "id", defaultValue = "0") Integer id) {
        Optional<KhuyenMai> bh = khuyenMaiRepository.findById(id);
        return khuyenMaiRepository.findById(id).get();
    }
    @GetMapping("/loc-trang-thai-KM")
    public ResponseEntity<Page<KhuyenMaiResponse>> locKhuyenMai(
            @RequestParam(required = false) String trangThai,
            @PageableDefault(size = 5) Pageable pageable ) {
        Page<KhuyenMaiResponse> result = khuyenMaiRepository.locTheoTrangThai( trangThai, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tim-kiem-KM")
    public ResponseEntity<Page<KhuyenMaiResponse>> timKiemKhuyenMai(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<KhuyenMaiResponse> result = khuyenMaiRepository.timKiemKhuyenMai(search, pageable);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/cap-nhat-trang-thai-KM")
    public ResponseEntity<String> capNhatTrangThai() {
        khuyenMaiRepository.capNhatTrangThaiKhuyenMai();
        return ResponseEntity.ok("Cập nhật trạng thái khuyến mãi thành công!");
    }
    }

