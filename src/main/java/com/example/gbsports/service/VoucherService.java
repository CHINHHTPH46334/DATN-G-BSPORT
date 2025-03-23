package com.example.gbsports.service;

import com.example.gbsports.entity.Voucher;
import com.example.gbsports.repository.VoucherRepository;
import com.example.gbsports.request.VoucherRequetst;
import com.example.gbsports.response.VoucherResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private static final Logger logger = LoggerFactory.getLogger(VoucherService.class);

    private final VoucherRepository voucherRepository;

    // Chuyển đổi từ Voucher sang VoucherResponse
    private VoucherResponse toResponse(Voucher voucher) {
        VoucherResponse response = new VoucherResponse();
        response.setId(voucher.getId());
        response.setMaVoucher(voucher.getMaVoucher());
        response.setTenVoucher(voucher.getTenVoucher());
        response.setMoTa(voucher.getMoTa());
        response.setNgayBatDau(voucher.getNgayBatDau());
        response.setNgayHetHan(voucher.getNgayHetHan());
        response.setGiaTriGiam(voucher.getGiaTriGiam());
        response.setKieuGiamGia(voucher.getKieuGiamGia());
        response.setTrangThai(voucher.getTrangThai());
        response.setGiaTriToiDa(voucher.getGiaTriToiDa());
        response.setSoLuong(voucher.getSoLuong());
        response.setGiaTriToiThieu(voucher.getGiaTriToiThieu());
        setVoucherStatus(voucher);
        response.setTrangThai(voucher.getTrangThai());
        return response;
    }

    // Chuyển đổi từ VoucherRequest sang Voucher
    private Voucher toEntity(VoucherRequetst request) {
        Voucher voucher = new Voucher();
        voucher.setId(request.getId());
        voucher.setMaVoucher(request.getMaVoucher());
        voucher.setTenVoucher(request.getTenVoucher());
        voucher.setMoTa(request.getMoTa());
        voucher.setNgayBatDau(request.getNgayBatDau());
        voucher.setNgayHetHan(request.getNgayHetHan());
        voucher.setGiaTriGiam(request.getGiaTriGiam());
        voucher.setKieuGiamGia(request.getKieuGiamGia());
        voucher.setTrangThai(request.getTrangThai());
        voucher.setGiaTriToiDa(request.getGiaTriToiDa());
        voucher.setSoLuong(request.getSoLuong());
        voucher.setGiaTriToiThieu(request.getGiaTriToiThieu());
        return voucher;
    }

    // Set trạng thái voucher
    private void setVoucherStatus(Voucher voucher) {
        LocalDateTime now = LocalDateTime.now();
        if (voucher.getNgayBatDau().isAfter(now)) {
            voucher.setTrangThai("Sắp diễn ra");
            voucherRepository.save(voucher);
        } else if (voucher.getNgayBatDau().isBefore(now) && voucher.getNgayHetHan().isAfter(now)) {
            voucher.setTrangThai("Đang diễn ra");
            voucherRepository.save(voucher);
        } else if (voucher.getNgayHetHan().isBefore(now)) {
            voucher.setTrangThai("Đã kết thúc");
            voucherRepository.save(voucher);
        }
    }

    // 1️⃣ Lấy danh sách tất cả voucher
    public List<VoucherResponse> getAllVouchers() {
        List<Voucher> vouchers = voucherRepository.findAll();
        vouchers.forEach(this::setVoucherStatus);

        return vouchers.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 2️⃣ Thêm voucher mới
    @Transactional
    public String addVoucher(VoucherRequetst request) {
        // Kiểm tra dữ liệu đầu vào
        if (request.getMaVoucher() == null || request.getMaVoucher().trim().isEmpty()) {
            return "Thêm thất bại: Mã voucher không được để trống!";
        }
        if (request.getTenVoucher() == null || request.getTenVoucher().trim().isEmpty()) {
            return "Thêm thất bại: Tên voucher không được để trống!";
        }
        if (request.getGiaTriGiam() == null || request.getGiaTriGiam().compareTo(BigDecimal.ZERO) <= 0) {
            return "Thêm thất bại: Giá trị giảm phải lớn hơn 0!";
        }
        if ("Phần trăm".equals(request.getKieuGiamGia()) && request.getGiaTriGiam().compareTo(new BigDecimal("95")) > 0) {
            return "Thêm thất bại: Giá trị giảm không được vượt quá 95 khi chọn Phần trăm!";
        }
        if (request.getKieuGiamGia() == null || request.getKieuGiamGia().trim().isEmpty()) {
            return "Thêm thất bại: Kiểu giảm giá không được để trống!";
        }
        if (request.getNgayBatDau() == null || request.getNgayHetHan() == null) {
            return "Thêm thất bại: Ngày bắt đầu và ngày kết thúc không được để trống!";
        }
        if (request.getNgayHetHan().isBefore(request.getNgayBatDau())) {
            return "Thêm thất bại: Ngày kết thúc phải sau ngày bắt đầu!";
        }
        if (request.getSoLuong() == null || request.getSoLuong() < 0) {
            return "Thêm thất bại: Số lượng không được nhỏ hơn 0!";
        }
        if (request.getGiaTriToiThieu() != null && request.getGiaTriToiThieu().compareTo(BigDecimal.ZERO) < 0) {
            return "Thêm thất bại: Giá trị tối thiểu không được nhỏ hơn 0!";
        }

        // Kiểm tra mã voucher đã tồn tại chưa
        if (voucherRepository.existsByMaVoucher(request.getMaVoucher())) {
            return "Thêm thất bại: Mã voucher đã tồn tại!";
        }

        // Chuyển đổi request thành entity và lưu
        Voucher voucher = toEntity(request);
        if ("Tiền mặt".equals(voucher.getKieuGiamGia())) {
            voucher.setGiaTriToiDa(voucher.getGiaTriGiam());
        }
        setVoucherStatus(voucher);
        voucherRepository.save(voucher);

        return "Thêm voucher thành công!";
    }

    // 3️⃣ Cập nhật voucher
    @Transactional
    public String updateVoucher(VoucherRequetst request) {
        Optional<Voucher> optionalVoucher = voucherRepository.findById(request.getId());
        if (optionalVoucher.isEmpty()) {
            return "Cập nhật thất bại: Không tìm thấy voucher!";
        }

        Voucher voucher = optionalVoucher.get();
        voucher.setMaVoucher(request.getMaVoucher());
        voucher.setTenVoucher(request.getTenVoucher());
        voucher.setMoTa(request.getMoTa());
        voucher.setNgayBatDau(request.getNgayBatDau());
        voucher.setNgayHetHan(request.getNgayHetHan());
        voucher.setGiaTriGiam(request.getGiaTriGiam());
        voucher.setKieuGiamGia(request.getKieuGiamGia());
        voucher.setGiaTriToiDa(request.getGiaTriToiDa());
        voucher.setSoLuong(request.getSoLuong());
        voucher.setGiaTriToiThieu(request.getGiaTriToiThieu());

        // Kiểm tra dữ liệu đầu vào
        if (voucher.getMaVoucher() == null || voucher.getMaVoucher().trim().isEmpty()) {
            return "Cập nhật thất bại: Mã voucher không được để trống!";
        }
        if (voucher.getTenVoucher() == null || voucher.getTenVoucher().trim().isEmpty()) {
            return "Cập nhật thất bại: Tên voucher không được để trống!";
        }
        if (voucher.getGiaTriGiam() == null || voucher.getGiaTriGiam().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cập nhật thất bại: Giá trị giảm phải lớn hơn 0!";
        }
        if ("Phần trăm".equals(voucher.getKieuGiamGia()) && voucher.getGiaTriGiam().compareTo(new BigDecimal("95")) > 0) {
            return "Cập nhật thất bại: Giá trị giảm không được vượt quá 95 khi chọn Phần trăm!";
        }
        if (voucher.getKieuGiamGia() == null || voucher.getKieuGiamGia().trim().isEmpty()) {
            return "Cập nhật thất bại: Kiểu giảm giá không được để trống!";
        }
        if (voucher.getNgayBatDau() == null || voucher.getNgayHetHan() == null) {
            return "Cập nhật thất bại: Ngày bắt đầu và ngày kết thúc không được để trống!";
        }
        if (voucher.getNgayHetHan().isBefore(voucher.getNgayBatDau())) {
            return "Cập nhật thất bại: Ngày kết thúc phải sau ngày bắt đầu!";
        }
        if (voucher.getSoLuong() == null || voucher.getSoLuong() < 0) {
            return "Cập nhật thất bại: Số lượng không được nhỏ hơn 0!";
        }
        if (voucher.getGiaTriToiThieu() != null && voucher.getGiaTriToiThieu().compareTo(BigDecimal.ZERO) < 0) {
            return "Cập nhật thất bại: Giá trị tối thiểu không được nhỏ hơn 0!";
        }

        // Kiểm tra mã voucher đã tồn tại chưa (trừ chính voucher đang cập nhật)
        Optional<Voucher> existingVoucher = voucherRepository.findByMaVoucher(voucher.getMaVoucher());
        if (existingVoucher.isPresent() && !existingVoucher.get().getId().equals(voucher.getId())) {
            return "Cập nhật thất bại: Mã voucher đã tồn tại!";
        }

        // Set trạng thái và lưu
        if ("Tiền mặt".equals(voucher.getKieuGiamGia())) {
            voucher.setGiaTriToiDa(voucher.getGiaTriGiam());
        }
        setVoucherStatus(voucher);
        voucherRepository.save(voucher);

        return "Cập nhật voucher thành công!";
    }

    // 4️⃣ Lấy chi tiết voucher
    public VoucherResponse getVoucherById(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher với ID: " + id));
        return toResponse(voucher);
    }

    // 5️⃣ Lọc voucher theo trạng thái
    public List<VoucherResponse> locTheoTrangThai(String trangThai) {
        List<Voucher> vouchers;
        if (trangThai != null && !trangThai.equals("Tất cả")) {
            vouchers = voucherRepository.findByTrangThai(trangThai);
        } else {
            vouchers = voucherRepository.findAll();
        }
        return vouchers.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 6️⃣ Tìm kiếm voucher theo từ khóa
    public List<VoucherResponse> timKiemVoucher(String keyword) {
        List<Voucher> vouchers;
        if (keyword != null && !keyword.trim().isEmpty()) {
            vouchers = voucherRepository.searchByKeyword(keyword);
        } else {
            vouchers = voucherRepository.findAll();
        }
        return vouchers.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 7️⃣ Tìm kiếm voucher theo khoảng ngày
    public List<VoucherResponse> timKiemVoucherByDate(LocalDateTime startDate, LocalDateTime endDate) {
        List<Voucher> vouchers = voucherRepository.searchByDateRange(startDate, endDate);
        return vouchers.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 8️⃣ Tìm kiếm voucher theo khoảng giá trị giảm
    public List<VoucherResponse> timKiemVoucherByPrice(Integer minPrice, Integer maxPrice) {
        Integer min = minPrice != null ? minPrice : voucherRepository.findMinPrice();
        Integer max = maxPrice != null ? maxPrice : voucherRepository.findMaxPrice();

        if (min == null || max == null) {
            min = 0;
            max = Integer.MAX_VALUE;
        }

        if (min > max) {
            Integer temp = min;
            min = max;
            max = temp;
        }

        List<Voucher> vouchers = voucherRepository.searchByPriceRange(min, max);
        return vouchers.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 9️⃣ Tắt voucher
    public String offVoucher(Integer id) {
        Optional<Voucher> optionalVoucher = voucherRepository.findById(id);
        if (optionalVoucher.isEmpty()) {
            return "Tắt thất bại: Không tìm thấy voucher!";
        }

        Voucher voucher = optionalVoucher.get();
        voucher.setNgayHetHan(LocalDateTime.now());
        setVoucherStatus(voucher);
        voucherRepository.save(voucher);
        return "Tắt voucher thành công!";
    }
}