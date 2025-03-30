package com.example.gbsports.service;

import com.example.gbsports.entity.ChiTietKhuyenMai;
import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.entity.KhuyenMai;
import com.example.gbsports.entity.SanPham;
import com.example.gbsports.repository.ChiTietKhuyenMaiRepo;
import com.example.gbsports.repository.ChiTietSanPhamRepo;
import com.example.gbsports.repository.KhuyenMaiRepository;
import com.example.gbsports.repository.SanPhamRepo;
import com.example.gbsports.request.KhuyenMaiRequetst;
import com.example.gbsports.response.KhuyenMaiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KhuyenMaiService {

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
        response.setNgayBatDau((khuyenMai.getNgayBatDau()));
        response.setNgayHetHan((khuyenMai.getNgayHetHan()));
        response.setGiaTriGiam(khuyenMai.getGiaTriGiam());
        response.setKieuGiamGia(khuyenMai.getKieuGiamGia());
        response.setTrangThai(khuyenMai.getTrangThai());
        response.setGiaTriToiDa(khuyenMai.getGiaTriToiDa());
        setKhuyenMaiStatus(khuyenMai);
        response.setTrangThai(khuyenMai.getTrangThai());
        return response;
    }

    // Chuyển đổi từ KhuyenMaiRequest sang KhuyenMai
    private KhuyenMai toEntity(KhuyenMaiRequetst request) {
        KhuyenMai khuyenMai = new KhuyenMai();
        khuyenMai.setId(request.getId());
        khuyenMai.setMaKhuyenMai(request.getMaKhuyenMai());
        khuyenMai.setTenKhuyenMai(request.getTenKhuyenMai());
        khuyenMai.setMoTa(request.getMoTa());
        khuyenMai.setNgayBatDau((request.getNgayBatDau()));
        khuyenMai.setNgayHetHan((request.getNgayHetHan()));
        khuyenMai.setGiaTriGiam(request.getGiaTriGiam());
        khuyenMai.setKieuGiamGia(request.getKieuGiamGia());
        khuyenMai.setTrangThai(request.getTrangThai());
        khuyenMai.setGiaTriToiDa(request.getGiaTriToiDa());
        return khuyenMai;
    }




    // Set trạng thái khuyến mãi
    private void setKhuyenMaiStatus(KhuyenMai khuyenMai) {
        LocalDateTime now = LocalDateTime.now();
        if (khuyenMai.getNgayBatDau().isAfter(now)) {
            khuyenMai.setTrangThai("Sắp diễn ra");
        } else if (khuyenMai.getNgayBatDau().isBefore(now) && khuyenMai.getNgayHetHan().isAfter(now)) {
            khuyenMai.setTrangThai("Đang diễn ra");
        } else if (khuyenMai.getNgayHetHan().isBefore(now)) {
            khuyenMai.setTrangThai("Đã kết thúc");
        }
    }

    // 1️⃣ Lấy danh sách tất cả khuyến mãi
    public List<KhuyenMaiResponse> getAllKhuyenMai() {
        List<KhuyenMai> khuyenMais = khuyenMaiRepository.findAll();
        return khuyenMais.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 2️⃣ Thêm khuyến mãi
    @Transactional
    public String addKhuyenMai(KhuyenMaiRequetst request, List<Integer> selectedChiTietSanPhamIds) {
        // Kiểm tra dữ liệu đầu vào
        if (request.getMaKhuyenMai() == null || request.getMaKhuyenMai().trim().isEmpty() ||
                request.getTenKhuyenMai() == null || request.getTenKhuyenMai().trim().isEmpty() ||
                request.getKieuGiamGia() == null || request.getKieuGiamGia().trim().isEmpty() ||
                request.getGiaTriGiam() == null || request.getNgayBatDau() == null ||
                request.getNgayHetHan() == null) {
            return "Thêm thất bại: Vui lòng nhập đầy đủ thông tin!";
        }

        // Kiểm tra mã khuyến mãi đã tồn tại chưa
        if (khuyenMaiRepository.existsByMaKhuyenMai(request.getMaKhuyenMai())) {
            return "Thêm thất bại: Mã khuyến mãi đã tồn tại!";
        }

        // Kiểm tra ngày kết thúc phải sau ngày bắt đầu
        LocalDateTime ngayBatDau = (request.getNgayBatDau());
        LocalDateTime ngayHetHan = (request.getNgayHetHan());
        if (ngayHetHan.isBefore(ngayBatDau)) {
            return "Thêm thất bại: Ngày kết thúc phải sau ngày bắt đầu!";
        }

        // Kiểm tra chi tiết sản phẩm được chọn
        if (selectedChiTietSanPhamIds == null || selectedChiTietSanPhamIds.isEmpty()) {
            return "Thêm thất bại: Vui lòng chọn ít nhất một chi tiết sản phẩm!";
        }

        // Chuyển đổi request thành entity và lưu
        KhuyenMai khuyenMai = toEntity(request);
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

        return "Thêm khuyến mãi thành công!";
    }

    // 3️⃣ Cập nhật khuyến mãi
    @Transactional
    public String updateKhuyenMai(KhuyenMaiRequetst request, List<Integer> selectedChiTietSanPhamIds) {
        Optional<KhuyenMai> optionalKhuyenMai = khuyenMaiRepository.findById(request.getId());
        if (optionalKhuyenMai.isEmpty()) {
            return "Cập nhật thất bại: Không tìm thấy khuyến mãi!";
        }

        KhuyenMai khuyenMai = optionalKhuyenMai.get();
        khuyenMai.setMaKhuyenMai(request.getMaKhuyenMai());
        khuyenMai.setTenKhuyenMai(request.getTenKhuyenMai());
        khuyenMai.setMoTa(request.getMoTa());
        khuyenMai.setNgayBatDau((request.getNgayBatDau()));
        khuyenMai.setNgayHetHan((request.getNgayHetHan()));
        khuyenMai.setGiaTriGiam(request.getGiaTriGiam());
        khuyenMai.setKieuGiamGia(request.getKieuGiamGia());
        khuyenMai.setGiaTriToiDa(request.getGiaTriToiDa());

        // Kiểm tra dữ liệu đầu vào
        if (khuyenMai.getMaKhuyenMai() == null || khuyenMai.getMaKhuyenMai().trim().isEmpty() ||
                khuyenMai.getTenKhuyenMai() == null || khuyenMai.getTenKhuyenMai().trim().isEmpty() ||
                khuyenMai.getKieuGiamGia() == null || khuyenMai.getKieuGiamGia().trim().isEmpty() ||
                khuyenMai.getGiaTriGiam() == null || khuyenMai.getNgayBatDau() == null ||
                khuyenMai.getNgayHetHan() == null) {
            return "Cập nhật thất bại: Vui lòng nhập đầy đủ thông tin!";
        }

        // Kiểm tra mã khuyến mãi đã tồn tại chưa (trừ chính khuyến mãi đang cập nhật)
        Optional<KhuyenMai> existingKhuyenMai = khuyenMaiRepository.findByMaKhuyenMai(khuyenMai.getMaKhuyenMai());
        if (existingKhuyenMai.isPresent() && !existingKhuyenMai.get().getId().equals(khuyenMai.getId())) {
            return "Cập nhật thất bại: Mã khuyến mãi đã tồn tại!";
        }

        // Kiểm tra ngày kết thúc phải sau ngày bắt đầu
        if (khuyenMai.getNgayHetHan().isBefore(khuyenMai.getNgayBatDau())) {
            return "Cập nhật thất bại: Ngày kết thúc phải sau ngày bắt đầu!";
        }

        // Kiểm tra chi tiết sản phẩm được chọn
        if (selectedChiTietSanPhamIds == null || selectedChiTietSanPhamIds.isEmpty()) {
            return "Cập nhật thất bại: Vui lòng chọn ít nhất một chi tiết sản phẩm!";
        }

        // Set trạng thái và lưu
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

        return "Cập nhật khuyến mãi thành công!";
    }

    // 4️⃣ Lấy chi tiết khuyến mãi
    public KhuyenMaiResponse getKhuyenMaiById(Integer id) {
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + id));
        return toResponse(khuyenMai);
    }

    // 5️⃣ Lọc khuyến mãi theo trạng thái
    public List<KhuyenMaiResponse> locTheoTrangThai(String trangThai) {
        List<KhuyenMai> khuyenMais;
        if (trangThai != null && !trangThai.equals("Tất cả")) {
            khuyenMais = khuyenMaiRepository.findByTrangThai(trangThai);
        } else {
            khuyenMais = khuyenMaiRepository.findAll();
        }
        return khuyenMais.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 6️⃣ Tìm kiếm khuyến mãi
    public List<KhuyenMaiResponse> timKiemKhuyenMai(String keyword) {
        List<KhuyenMai> khuyenMais;
        if (keyword != null && !keyword.trim().isEmpty()) {
            khuyenMais = khuyenMaiRepository.searchByKeyword(keyword);
        } else {
            khuyenMais = khuyenMaiRepository.findAll();
        }
        return khuyenMais.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // 7️⃣ Tắt khuyến mãi
    public String offKhuyenMai(Integer id) {
        Optional<KhuyenMai> optionalKhuyenMai = khuyenMaiRepository.findById(id);
        if (optionalKhuyenMai.isEmpty()) {
            return "Tắt thất bại: Không tìm thấy khuyến mãi!";
        }

        KhuyenMai khuyenMai = optionalKhuyenMai.get();
        khuyenMai.setNgayBatDau(LocalDateTime.now().minusDays(2));
        khuyenMai.setNgayHetHan(LocalDateTime.now().minusDays(1));
        setKhuyenMaiStatus(khuyenMai);
        khuyenMaiRepository.save(khuyenMai);
        return "Tắt khuyến mãi thành công!";
    }

    // 8️⃣ Tìm kiếm sản phẩm
    public List<SanPham> searchSanPham(String keywordSanPham) {
        if (keywordSanPham != null && !keywordSanPham.trim().isEmpty()) {
            return sanPhamRepository.findByMaSanPhamOrTenSanPhamContainingIgnoreCase(keywordSanPham);
        } else {
            return sanPhamRepository.findAllSortedByIdSanPham();
        }
    }

    // 9️⃣ Lấy chi tiết sản phẩm theo sản phẩm
    public List<ChiTietSanPham> getChiTietSanPhamBySanPham(Integer idSanPham) {
        if (idSanPham == null || idSanPham <= 0) {
            return List.of();
        }
        return chiTietSanPhamRepository.findBySanPhamIdSanPham(idSanPham);
    }
    // Tìm kiếm khuyến mãi theo khoảng ngày (mới)
    public List<KhuyenMaiResponse> timKiemKhuyenMaiByDate(LocalDateTime startDate, LocalDateTime endDate) {
        List<KhuyenMai> khuyenMais = khuyenMaiRepository.searchByDateRange(startDate, endDate);
        return khuyenMais.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Tìm kiếm khuyến mãi theo khoảng giá trị giảm (mới)
    public List<KhuyenMaiResponse> timKiemKhuyenMaiByPrice(Integer minPrice, Integer maxPrice) {
        Integer min = minPrice != null ? minPrice : khuyenMaiRepository.findMinGiaTriToiDa();
        Integer max = maxPrice != null ? maxPrice : khuyenMaiRepository.findMaxGiaTriToiDa();

        if (min == null || max == null) {
            min = 0;
            max = Integer.MAX_VALUE;
        }

        if (min > max) {
            Integer temp = min;
            min = max;
            max = temp;
        }

        List<KhuyenMai> khuyenMais = khuyenMaiRepository.searchByGiaTriToiDaRange(min, max);
        return khuyenMais.stream().map(this::toResponse).collect(Collectors.toList());
    }
}