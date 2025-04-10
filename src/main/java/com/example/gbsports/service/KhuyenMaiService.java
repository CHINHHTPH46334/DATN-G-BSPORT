package com.example.gbsports.service;

import com.example.gbsports.entity.ChiTietKhuyenMai;
import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.entity.KhuyenMai;
import com.example.gbsports.entity.SanPham;
import com.example.gbsports.repository.ChiTietKhuyenMaiRepo;
import com.example.gbsports.repository.ChiTietSanPhamRepo;
import com.example.gbsports.repository.KhuyenMaiRepository;
import com.example.gbsports.repository.SanPhamRepo;
import com.example.gbsports.request.KhuyenMaiRequest;
import com.example.gbsports.response.KhuyenMaiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KhuyenMaiService {

    private static final Logger logger = LoggerFactory.getLogger(KhuyenMaiService.class);

    private final KhuyenMaiRepository khuyenMaiRepository;
    private final SanPhamRepo sanPhamRepository;
    private final ChiTietSanPhamRepo chiTietSanPhamRepository;
    private final ChiTietKhuyenMaiRepo chiTietKhuyenMaiRepository;

    // Chuyển đổi từ KhuyenMai sang KhuyenMaiResponse
    private KhuyenMaiResponse toResponse(KhuyenMai khuyenMai) {
        KhuyenMaiResponse response = new KhuyenMaiResponse();
        response.setId(khuyenMai.getId());
        response.setMaKhuyenMai(khuyenMai.getMaKhuyenMai());
        response.setTenKhuyenMai(khuyenMai.getTenKhuyenMai());
        response.setMoTa(khuyenMai.getMoTa());
        response.setNgayBatDau(khuyenMai.getNgayBatDau());
        response.setNgayHetHan(khuyenMai.getNgayHetHan());
        response.setGiaTriGiam(khuyenMai.getGiaTriGiam());
        response.setKieuGiamGia(khuyenMai.getKieuGiamGia());
        response.setGiaTriToiDa(khuyenMai.getGiaTriToiDa());
        setKhuyenMaiStatus(khuyenMai); // Cập nhật trạng thái trước khi trả về
        response.setTrangThai(khuyenMai.getTrangThai());

        // Lấy danh sách chi tiết sản phẩm liên kết với khuyến mãi
        List<ChiTietKhuyenMai> chiTietKhuyenMais = chiTietKhuyenMaiRepository.findByKhuyenMaiId(khuyenMai.getId());
        List<ChiTietSanPham> chiTietSanPhams = chiTietKhuyenMais.stream()
                .map(ChiTietKhuyenMai::getChiTietSanPham)
                .collect(Collectors.toList());
        response.setChiTietSanPhams(chiTietSanPhams);

        return response;
    }

    // Chuyển đổi từ KhuyenMaiRequest sang KhuyenMai
    private KhuyenMai toEntity(KhuyenMaiRequest request) {
        KhuyenMai khuyenMai = new KhuyenMai();
        khuyenMai.setId(request.getId());
        khuyenMai.setMaKhuyenMai(request.getMaKhuyenMai());
        khuyenMai.setTenKhuyenMai(request.getTenKhuyenMai());
        khuyenMai.setMoTa(request.getMoTa());
        khuyenMai.setNgayBatDau(request.getNgayBatDau());
        khuyenMai.setNgayHetHan(request.getNgayHetHan());
        khuyenMai.setGiaTriGiam(request.getGiaTriGiam());
        khuyenMai.setKieuGiamGia(request.getKieuGiamGia());
        khuyenMai.setTrangThai(request.getTrangThai());
        khuyenMai.setGiaTriToiDa(request.getGiaTriToiDa());
        return khuyenMai;
    }

    // Set trạng thái khuyến mãi
    private void setKhuyenMaiStatus(KhuyenMai khuyenMai) {
        OffsetDateTime now = OffsetDateTime.now();
        if (khuyenMai.getNgayBatDau().isAfter(now)) {
            khuyenMai.setTrangThai("Sắp diễn ra");
        } else if (khuyenMai.getNgayBatDau().isBefore(now) && khuyenMai.getNgayHetHan().isAfter(now)) {
            khuyenMai.setTrangThai("Đang diễn ra");
        } else if (khuyenMai.getNgayHetHan().isBefore(now)) {
            khuyenMai.setTrangThai("Đã kết thúc");
        }
        khuyenMaiRepository.save(khuyenMai); // Lưu trạng thái mới
    }

    // Cập nhật scheduled task để xử lý tất cả các trang
    @Scheduled(fixedRate = 5000) // Chạy mỗi 1 phút
    @Transactional
    public void updateKhuyenMaiStatusAutomatically() {
        int pageSize = 100; // Kích thước mỗi trang
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<KhuyenMai> khuyenMaiPage;

        do {
            khuyenMaiPage = khuyenMaiRepository.findAll(pageable);
            khuyenMaiPage.getContent().forEach(khuyenMai -> {
                String oldStatus = khuyenMai.getTrangThai();
                setKhuyenMaiStatus(khuyenMai);
                if (!oldStatus.equals(khuyenMai.getTrangThai())) {
                    logger.info("Updated khuyen mai status: {} from {} to {}",
                            khuyenMai.getMaKhuyenMai(), oldStatus, khuyenMai.getTrangThai());
                }
            });
            pageable = khuyenMaiPage.nextPageable(); // Chuyển sang trang tiếp theo
        } while (khuyenMaiPage.hasNext()); // Tiếp tục nếu còn trang tiếp theo
    }

    // 1️⃣ Lấy danh sách tất cả khuyến mãi (có phân trang)
    public Page<KhuyenMaiResponse> getAllKhuyenMai(Pageable pageable) {
        return khuyenMaiRepository.findAll(pageable).map(this::toResponse);
    }

    // 2️⃣ Thêm khuyến mãi mới
    @Transactional
    public String addKhuyenMai(KhuyenMaiRequest request, List<Integer> selectedChiTietSanPhamIds) {
        // Kiểm tra dữ liệu đầu vào
        if (request.getMaKhuyenMai() == null || request.getMaKhuyenMai().trim().isEmpty()) {
            return "Thêm thất bại: Mã khuyến mãi không được để trống!";
        }
        if (request.getTenKhuyenMai() == null || request.getTenKhuyenMai().trim().isEmpty()) {
            return "Thêm thất bại: Tên khuyến mãi không được để trống!";
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
        if (selectedChiTietSanPhamIds == null || selectedChiTietSanPhamIds.isEmpty()) {
            return "Thêm thất bại: Vui lòng chọn ít nhất một chi tiết sản phẩm!";
        }

        // Kiểm tra mã khuyến mãi đã tồn tại chưa
        if (khuyenMaiRepository.existsByMaKhuyenMai(request.getMaKhuyenMai())) {
            return "Thêm thất bại: Mã khuyến mãi đã tồn tại!";
        }

        KhuyenMai khuyenMai = toEntity(request);
        if ("Tiền mặt".equals(khuyenMai.getKieuGiamGia())) {
            khuyenMai.setGiaTriToiDa(khuyenMai.getGiaTriGiam());
        }
        setKhuyenMaiStatus(khuyenMai);
        KhuyenMai savedKhuyenMai = khuyenMaiRepository.save(khuyenMai);

        // Lưu chi tiết khuyến mãi
        for (Integer chiTietSanPhamId : selectedChiTietSanPhamIds) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(chiTietSanPhamId)
                    .orElseThrow(() -> new IllegalArgumentException("Chi tiết sản phẩm không tồn tại: " + chiTietSanPhamId));
            ChiTietKhuyenMai chiTietKhuyenMai = new ChiTietKhuyenMai();
            chiTietKhuyenMai.setKhuyenMai(savedKhuyenMai);
            chiTietKhuyenMai.setChiTietSanPham(chiTietSanPham);
            chiTietKhuyenMaiRepository.save(chiTietKhuyenMai);
        }

        logger.info("Thêm khuyến mãi thành công: {}", savedKhuyenMai.getMaKhuyenMai());
        return "Thêm khuyến mãi thành công!";
    }

    // 3️⃣ Cập nhật khuyến mãi
    @Transactional
    public String updateKhuyenMai(KhuyenMaiRequest request, List<Integer> selectedChiTietSanPhamIds) {
        Optional<KhuyenMai> optionalKhuyenMai = khuyenMaiRepository.findById(request.getId());
        if (optionalKhuyenMai.isEmpty()) {
            return "Cập nhật thất bại: Không tìm thấy khuyến mãi!";
        }

        KhuyenMai khuyenMai = optionalKhuyenMai.get();
        khuyenMai.setMaKhuyenMai(request.getMaKhuyenMai());
        khuyenMai.setTenKhuyenMai(request.getTenKhuyenMai());
        khuyenMai.setMoTa(request.getMoTa());
        khuyenMai.setNgayBatDau(request.getNgayBatDau());
        khuyenMai.setNgayHetHan(request.getNgayHetHan());
        khuyenMai.setGiaTriGiam(request.getGiaTriGiam());
        khuyenMai.setKieuGiamGia(request.getKieuGiamGia());
        khuyenMai.setGiaTriToiDa(request.getGiaTriToiDa());

        // Kiểm tra dữ liệu đầu vào
        if (khuyenMai.getMaKhuyenMai() == null || khuyenMai.getMaKhuyenMai().trim().isEmpty()) {
            return "Cập nhật thất bại: Mã khuyến mãi không được để trống!";
        }
        if (khuyenMai.getTenKhuyenMai() == null || khuyenMai.getTenKhuyenMai().trim().isEmpty()) {
            return "Cập nhật thất bại: Tên khuyến mãi không được để trống!";
        }
        if (khuyenMai.getGiaTriGiam() == null || khuyenMai.getGiaTriGiam().compareTo(BigDecimal.ZERO) <= 0) {
            return "Cập nhật thất bại: Giá trị giảm phải lớn hơn 0!";
        }
        if ("Phần trăm".equals(khuyenMai.getKieuGiamGia()) && khuyenMai.getGiaTriGiam().compareTo(new BigDecimal("100")) > 0) {
            return "Cập nhật thất bại: Giá trị giảm không được vượt quá 100 khi chọn Phần trăm!";
        }
        if (khuyenMai.getKieuGiamGia() == null || khuyenMai.getKieuGiamGia().trim().isEmpty()) {
            return "Cập nhật thất bại: Kiểu giảm giá không được để trống!";
        }
        if (khuyenMai.getNgayBatDau() == null || khuyenMai.getNgayHetHan() == null) {
            return "Cập nhật thất bại: Ngày bắt đầu và ngày kết thúc không được để trống!";
        }
        if (khuyenMai.getNgayHetHan().isBefore(khuyenMai.getNgayBatDau())) {
            return "Cập nhật thất bại: Ngày kết thúc phải sau ngày bắt đầu!";
        }
        if (selectedChiTietSanPhamIds == null || selectedChiTietSanPhamIds.isEmpty()) {
            return "Cập nhật thất bại: Vui lòng chọn ít nhất một chi tiết sản phẩm!";
        }

        // Kiểm tra mã khuyến mãi đã tồn tại chưa (trừ chính khuyến mãi đang cập nhật)
        Optional<KhuyenMai> existingKhuyenMai = khuyenMaiRepository.findByMaKhuyenMai(khuyenMai.getMaKhuyenMai());
        if (existingKhuyenMai.isPresent() && !existingKhuyenMai.get().getId().equals(khuyenMai.getId())) {
            return "Cập nhật thất bại: Mã khuyến mãi đã tồn tại!";
        }

        if ("Tiền mặt".equals(khuyenMai.getKieuGiamGia())) {
            khuyenMai.setGiaTriToiDa(khuyenMai.getGiaTriGiam());
        }
        setKhuyenMaiStatus(khuyenMai);
        khuyenMaiRepository.save(khuyenMai);

        // Xóa chi tiết khuyến mãi cũ và thêm mới
        chiTietKhuyenMaiRepository.deleteByKhuyenMaiId(khuyenMai.getId());
        for (Integer chiTietSanPhamId : selectedChiTietSanPhamIds) {
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(chiTietSanPhamId)
                    .orElseThrow(() -> new IllegalArgumentException("Chi tiết sản phẩm không tồn tại: " + chiTietSanPhamId));
            ChiTietKhuyenMai chiTietKhuyenMai = new ChiTietKhuyenMai();
            chiTietKhuyenMai.setKhuyenMai(khuyenMai);
            chiTietKhuyenMai.setChiTietSanPham(chiTietSanPham);
            chiTietKhuyenMaiRepository.save(chiTietKhuyenMai);
        }

        logger.info("Cập nhật khuyến mãi thành công: {}", khuyenMai.getMaKhuyenMai());
        return "Cập nhật khuyến mãi thành công!";
    }

    // 4️⃣ Lấy chi tiết khuyến mãi
    public KhuyenMaiResponse getKhuyenMaiById(Integer id) {
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + id));
        return toResponse(khuyenMai); // Sử dụng toResponse đã cập nhật
    }

    // 5️⃣ Lọc khuyến mãi theo trạng thái (có phân trang)
    public Page<KhuyenMaiResponse> locTheoTrangThai(String trangThai, Pageable pageable) {
        if (trangThai == null || trangThai.equals("Tất cả")) {
            return getAllKhuyenMai(pageable);
        }
        return khuyenMaiRepository.findByTrangThai(trangThai, pageable).map(this::toResponse);
    }

    // 6️⃣ Tìm kiếm khuyến mãi theo từ khóa (có phân trang)
    public Page<KhuyenMaiResponse> timKiemKhuyenMai(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllKhuyenMai(pageable);
        }
        return khuyenMaiRepository.searchByKeyword(keyword, pageable).map(this::toResponse);
    }

    // 7️⃣ Tìm kiếm khuyến mãi theo khoảng ngày (có phân trang)
    public Page<KhuyenMaiResponse> timKiemKhuyenMaiByDate(OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable) {
        System.out.println("Searching by date - Start: " + startDate + ", End: " + endDate); // Debug
        return khuyenMaiRepository.searchByDateRange(startDate, endDate, pageable).map(this::toResponse);
    }

    // 8️⃣ Tìm kiếm khuyến mãi theo khoảng giá trị giảm (có phân trang)
    public Page<KhuyenMaiResponse> timKiemKhuyenMaiByPrice(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        BigDecimal min = minPrice != null ? minPrice : BigDecimal.ZERO;
        BigDecimal max = maxPrice != null ? maxPrice : khuyenMaiRepository.findMaxPrice();

        if (min.compareTo(max) > 0) {
            BigDecimal temp = min;
            min = max;
            max = temp;
        }

        return khuyenMaiRepository.searchByPriceRange(min, max, pageable).map(this::toResponse);
    }

    // 9️⃣ Tắt khuyến mãi
    @Transactional
    public String offKhuyenMai(Integer id) {
        Optional<KhuyenMai> optionalKhuyenMai = khuyenMaiRepository.findById(id);
        if (optionalKhuyenMai.isEmpty()) {
            return "Tắt thất bại: Không tìm thấy khuyến mãi!";
        }

        KhuyenMai khuyenMai = optionalKhuyenMai.get();
        khuyenMai.setNgayHetHan(OffsetDateTime.now());
        setKhuyenMaiStatus(khuyenMai);
        khuyenMaiRepository.save(khuyenMai);

        logger.info("Tắt khuyến mãi thành công: {}", khuyenMai.getMaKhuyenMai());
        return "Tắt khuyến mãi thành công!";
    }

    // 10️⃣ Tìm kiếm sản phẩm với phân trang
    public Page<SanPham> searchSanPham(String keywordSanPham, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (keywordSanPham != null && !keywordSanPham.trim().isEmpty()) {
            return sanPhamRepository.findByMaSanPhamOrTenSanPhamContainingIgnoreCase(keywordSanPham, pageable);
        } else {
            return sanPhamRepository.findAllSortedByIdSanPham(pageable);
        }
    }

    // 11️⃣ Lấy chi tiết sản phẩm theo sản phẩm
    public List<ChiTietSanPham> getChiTietSanPhamBySanPham(Integer idSanPham) {
        if (idSanPham == null || idSanPham <= 0) {
            return List.of();
        }
        return chiTietSanPhamRepository.findBySanPhamIdSanPham(idSanPham);
    }

    public Page<KhuyenMaiResponse> locTheoKieuGiamGia(String kieuGiamGia, Pageable pageable) {
        if (kieuGiamGia == null || kieuGiamGia.equals("Tất cả")) {
            return getAllKhuyenMai(pageable);
        }
        return khuyenMaiRepository.findByKieuGiamGia(kieuGiamGia, pageable).map(this::toResponse);
    }
}