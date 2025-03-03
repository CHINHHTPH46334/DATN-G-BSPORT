package com.example.gbsports.controller;

import com.example.gbsports.entity.Voucher;
import com.example.gbsports.repository.VoucherRepository;
import com.example.gbsports.request.VoucherRequest;
import com.example.gbsports.response.VoucherResponse;
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
public class VoucherController {
    @Autowired
    private VoucherRepository voucherRepository;

    // 1️⃣ Hiển thị danh sách voucher
    @GetMapping("/hien-thi-VC")
    public List<Voucher> hienThi() {
        return voucherRepository.findAll();
    }

    // 2️⃣ Phân trang danh sách voucher
    @GetMapping("/phan-trang-VC")
    public List<VoucherResponse> phanTrang(@RequestParam(value = "page", defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, 5);
        return voucherRepository.phanTrang(pageable).getContent();
    }

    // 3️⃣ Thêm voucher mới
    @PostMapping("/add-VC")
    public String add(@RequestBody VoucherRequest voucherRequest) {
        Voucher v = new Voucher();
        BeanUtils.copyProperties(voucherRequest, v);

        // Kiểm tra dữ liệu đầu vào
        if (v.getMaVoucher() == null || v.getMaVoucher().trim().isEmpty() ||
                v.getTenVoucher() == null || v.getTenVoucher().trim().isEmpty() ||
                v.getNgayTao() == null || v.getNgayHetHan() == null ||
                v.getGiaTriGiam() == null || v.getGiaTriToiThieu() == null ||
                v.getSoLuong() == null || v.getKieuGiamGia() == null || v.getKieuGiamGia().trim().isEmpty()) {

            return "Thêm thất bại: Vui lòng nhập đầy đủ thông tin!";
        }

        // Kiểm tra mã voucher đã tồn tại chưa
        if (voucherRepository.existsByMaVoucher(v.getMaVoucher())) {
            return "Thêm thất bại: Mã voucher đã tồn tại!";
        }

        // Lưu vào database
        voucherRepository.save(v);
        return "Thêm voucher thành công!";
    }

    // 4️⃣ Cập nhật voucher
    @PutMapping("/update-VC")
    public String update(@RequestBody VoucherRequest voucherRequest) {
        Optional<Voucher> optionalVoucher = voucherRepository.findById(voucherRequest.getId());
        if (optionalVoucher.isEmpty()) {
            return "Cập nhật thất bại: Không tìm thấy voucher!";
        }

        Voucher v = optionalVoucher.get();
        BeanUtils.copyProperties(voucherRequest, v, "id");

        // Kiểm tra dữ liệu đầu vào
        if (v.getMaVoucher() == null || v.getMaVoucher().trim().isEmpty() ||
                v.getTenVoucher() == null || v.getTenVoucher().trim().isEmpty() ||
                v.getNgayTao() == null || v.getNgayHetHan() == null ||
                v.getGiaTriGiam() == null || v.getGiaTriToiThieu() == null ||
                v.getSoLuong() == null || v.getKieuGiamGia() == null || v.getKieuGiamGia().trim().isEmpty()) {

            return "Cập nhật thất bại: Vui lòng nhập đầy đủ thông tin!";
        }

        // Lưu vào database
        voucherRepository.save(v);
        return "Cập nhật voucher thành công!";
    }

    // 5️⃣ Lấy chi tiết voucher
    @GetMapping("/detail-VC")
    public Voucher detail(@RequestParam(value = "id", defaultValue = "0") Integer id) {
        Optional<Voucher> vc = voucherRepository.findById(id);
        return voucherRepository.findById(id).get();
    }

    // 6️⃣ Lọc voucher theo trạng thái
    @GetMapping("/loc-trang-thai-VC")
    public ResponseEntity<Page<VoucherResponse>> locVoucher(
            @RequestParam(required = false) String trangThai,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<VoucherResponse> result = voucherRepository.locTheoTrangThai(trangThai, pageable);
        return ResponseEntity.ok(result);
    }

    // 7️⃣ Tìm kiếm voucher
    @GetMapping("/tim-kiem-VC")
    public ResponseEntity<Page<VoucherResponse>> timKiemVoucher(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<VoucherResponse> result = voucherRepository.timKiemVoucher(search, pageable);
        return ResponseEntity.ok(result);
    }

    // 8️⃣ Cập nhật trạng thái voucher
    @GetMapping("/cap-nhat-trang-thai-VC")
    public ResponseEntity<String> capNhatTrangThai() {
        voucherRepository.capNhatTrangThaiVoucher();
        return ResponseEntity.ok("Cập nhật trạng thái voucher thành công!");
    }
}
