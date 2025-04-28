package com.example.gbsports.service;

import com.example.gbsports.entity.Voucher;
import com.example.gbsports.repository.VoucherRepository;
import com.example.gbsports.request.VoucherRequetst;
import com.example.gbsports.response.VoucherBHResponse;
import com.example.gbsports.response.VoucherResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
        setVoucherStatus(voucher); // Cập nhật trạng thái trước khi trả về
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
        if (voucher.getSoLuong() != null && voucher.getSoLuong() <= 0) {
            voucher.setTrangThai("Đã kết thúc");
        } else if (voucher.getNgayBatDau().isAfter(now)) {
            voucher.setTrangThai("Sắp diễn ra");
        } else if (voucher.getNgayBatDau().isBefore(now) && voucher.getNgayHetHan().isAfter(now)) {
            voucher.setTrangThai("Đang diễn ra");
        } else if (voucher.getNgayHetHan().isBefore(now)) {
            voucher.setTrangThai("Đã kết thúc");
        }
        voucherRepository.save(voucher);
    }

    // Thêm scheduled task để tự động cập nhật trạng thái
    @Scheduled(fixedRate = 5000) // Chạy mỗi 1 phút
    @Transactional
    public void updateVoucherStatusAutomatically() {
        int pageSize = 100; // Kích thước mỗi trang
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Voucher> voucherPage;

        do {
            voucherPage = voucherRepository.findAll(pageable);
            voucherPage.getContent().forEach(voucher -> {
                String oldStatus = voucher.getTrangThai();
                setVoucherStatus(voucher);
                if (!oldStatus.equals(voucher.getTrangThai())) {
                    logger.info("Updated voucher status: {} from {} to {}",
                            voucher.getMaVoucher(), oldStatus, voucher.getTrangThai());
                }
            });
            pageable = voucherPage.nextPageable(); // Chuyển sang trang tiếp theo
        } while (voucherPage.hasNext()); // Tiếp tục nếu còn trang tiếp theo
    }

    // 1️⃣ Lấy danh sách tất cả voucher (có phân trang)
    public Page<VoucherResponse> getAllVouchers(Pageable pageable) {
        return voucherRepository.findAll(pageable).map(this::toResponse);
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
        if ("Phần trăm".equals(request.getKieuGiamGia()) && request.getGiaTriGiam().compareTo(new BigDecimal("100")) > 0) {
            return "Thêm thất bại: Giá trị giảm không được vượt quá 100 khi chọn Phần trăm!";
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
        if ("Phần trăm".equals(voucher.getKieuGiamGia()) && voucher.getGiaTriGiam().compareTo(new BigDecimal("100")) > 0) {
            return "Cập nhật thất bại: Giá trị giảm không được vượt quá 100 khi chọn Phần trăm!";
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

    // 5️⃣ Lọc voucher theo trạng thái (có phân trang)
    public Page<VoucherResponse> locTheoTrangThai(String trangThai, Pageable pageable) {
        if (trangThai == null || trangThai.equals("Tất cả")) {
            return getAllVouchers(pageable);
        }
        return voucherRepository.findByTrangThai(trangThai, pageable).map(this::toResponse);
    }

    // 6️⃣ Tìm kiếm voucher theo từ khóa (có phân trang)
    public Page<VoucherResponse> timKiemVoucher(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllVouchers(pageable);
        }
        return voucherRepository.searchByKeyword(keyword, pageable).map(this::toResponse);
    }

    // 7️⃣ Tìm kiếm voucher theo khoảng ngày (có phân trang)
    public Page<VoucherResponse> timKiemVoucherByDate(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate == null && endDate == null) {
            return getAllVouchers(pageable);
        } else if (startDate != null && endDate != null) {
            return voucherRepository.searchByDateRange(startDate, endDate, pageable).map(this::toResponse);
        } else if (startDate != null) {
            return voucherRepository.findByNgayBatDauGreaterThanEqual(startDate, pageable).map(this::toResponse);
        } else {
            return voucherRepository.findByNgayHetHanLessThanEqual(endDate, pageable).map(this::toResponse);
        }
    }

    // 8️⃣ Tìm kiếm voucher theo khoảng giá trị giảm (có phân trang)
    public Page<VoucherResponse> timKiemVoucherByPrice(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        BigDecimal min = minPrice != null ? minPrice : BigDecimal.ZERO;
        BigDecimal max = maxPrice != null ? maxPrice : voucherRepository.findMaxPrice();

        if (min.compareTo(max) > 0) {
            BigDecimal temp = min;
            min = max;
            max = temp;
        }

        return voucherRepository.searchByPriceRange(min, max, pageable).map(this::toResponse);
    }

    // 9️⃣ Tắt voucher
    @Transactional
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

    public Page<VoucherResponse> locTheoKieuGiamGia(String kieuGiamGia, Pageable pageable) {
        if (kieuGiamGia == null || kieuGiamGia.equals("Tất cả")) {
            return getAllVouchers(pageable);
        }
        return voucherRepository.findByKieuGiamGia(kieuGiamGia, pageable).map(this::toResponse);
    }

    public List<VoucherBHResponse> listVoucherTheoGiaTruyen(@RequestParam("giaTruyen") BigDecimal giaTruyen) {
        ArrayList<VoucherBHResponse> listVc = new ArrayList<>();
        listVc = (ArrayList<VoucherBHResponse>) voucherRepository.listVoucherHopLeTheoGia(giaTruyen).stream()
                .sorted(Comparator.comparing(VoucherBHResponse::getSo_tien_giam).reversed())
                .collect(Collectors.toList());
        return listVc;
    }
}