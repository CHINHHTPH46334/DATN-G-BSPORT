package com.example.gbsports.service;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.request.ChiTietSanPhamRequest;
import com.example.gbsports.response.ChiTietSanPhamView;
import com.example.gbsports.response.HinhAnhView;
import com.example.gbsports.response.SanPhamView;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChiTietSanPhamService {
    @Autowired
    ChiTietSanPhamRepo chiTietSanPhamRepo;
    @Autowired
    HinhAnhSanPhamRepo hinhAnhSanPhamRepo;
    @Autowired
    MauSacRepo mauSacRepo;
    @Autowired
    KichThuocRepo kichThuocRepo;
    @Autowired
    SanPhamRepo sanPhamRepo;
    @Autowired
    SanPhamService sanPhamService;

    // @Cacheable(value = "detailProducts")
    public List<ChiTietSanPhamView> getAllCTSP() {
        return chiTietSanPhamRepo.listCTSP();
    }

    public List<ChiTietSanPham> getAllCTSPFindAll() {
        return chiTietSanPhamRepo.findAll();
    }

    public Page<ChiTietSanPhamView> getAllCTSPPhanTrang(Pageable pageable) {
        return chiTietSanPhamRepo.listPhanTrangChiTietSanPham(pageable);
    }

    public static String convertJsDateToUtc7(String jsDateString) {
        // Chuyển chuỗi từ JS (ISO 8601) thành Instant (UTC)
        Instant instant = Instant.parse(jsDateString);

        // Chuyển từ UTC sang UTC+7 (Asia/Bangkok)
        ZonedDateTime utc7Time = instant.atZone(ZoneId.of("Asia/Bangkok"));

        // Định dạng kết quả theo "yyyy-MM-dd HH:mm:ss"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return utc7Time.format(formatter);
    }

    /**
     * API endpoint để lưu thông tin chi tiết sản phẩm
     * Xử lý thêm mới, cập nhật và xử lý trùng lặp chi tiết sản phẩm
     */
    public ResponseEntity<?> saveChiTietSanPham(@Valid @RequestBody ChiTietSanPhamRequest chiTietSanPhamRequest, BindingResult result) {
        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Lấy dữ liệu liên quan (các entity cần thiết)
            KichThuoc kichThuoc = kichThuocRepo.findById(chiTietSanPhamRequest.getId_kich_thuoc())
                    .orElseThrow(() -> new ResourceNotFoundException("KichThuoc", "id", chiTietSanPhamRequest.getId_kich_thuoc()));

            MauSac mauSac = mauSacRepo.findById(chiTietSanPhamRequest.getId_mau_sac())
                    .orElseThrow(() -> new ResourceNotFoundException("MauSac", "id", chiTietSanPhamRequest.getId_mau_sac()));

            SanPham sanPham = sanPhamRepo.findById(chiTietSanPhamRequest.getId_san_pham())
                    .orElseThrow(() -> new ResourceNotFoundException("SanPham", "id", chiTietSanPhamRequest.getId_san_pham()));

            // Biến để lưu trữ chi tiết sản phẩm cần xử lý
            ChiTietSanPham chiTietSanPham = null;
            String message = "";
            boolean isNewDetail = false;
            Integer oldQuantity = 0;
            boolean isUpdateByAttribute = false; // Biến đánh dấu trường hợp update theo thuộc tính

            // CASE 1: Kiểm tra nếu có ID chi tiết sản phẩm (cập nhật)
            if (chiTietSanPhamRequest.getId_chi_tiet_san_pham() != null &&
                    !chiTietSanPhamRequest.getId_chi_tiet_san_pham().toString().isEmpty()) {

                Optional<ChiTietSanPham> existingById = chiTietSanPhamRepo.findById(
                        chiTietSanPhamRequest.getId_chi_tiet_san_pham()
                );

                if (existingById.isPresent()) {
                    chiTietSanPham = existingById.get();
                    message = "Cập nhật chi tiết sản phẩm thành công";
                    // Giữ lại SanPham hiện tại để tránh mất tham chiếu
                    SanPham currentProduct = chiTietSanPham.getSanPham();
                    Date ngayTaoGoc = chiTietSanPham.getNgay_tao();

                    // Sao chép các thuộc tính cơ bản (có thể loại trừ sanPham)
                    chiTietSanPham.setGia_ban(chiTietSanPhamRequest.getGia_ban());
                    chiTietSanPham.setSo_luong(chiTietSanPhamRequest.getSo_luong());
//                    chiTietSanPham.setTrang_thai(chiTietSanPhamRequest.getTrang_thai());
                    chiTietSanPham.setQr_code(chiTietSanPhamRequest.getQr_code());

                    // Giữ ngày tạo gốc
                    chiTietSanPham.setNgay_tao(ngayTaoGoc);
                }
            }

            // CASE 2: Nếu không có ID hoặc ID không tồn tại, kiểm tra trùng lặp thuộc tính
            if (chiTietSanPham == null) {
                // Tìm chi tiết sản phẩm dựa trên ID của các thuộc tính
                Optional<ChiTietSanPham> existingSanPhamByAttributes = chiTietSanPhamRepo.findByIdSanPhamIdMauSacIdKichThuoc(
                        chiTietSanPhamRequest.getId_san_pham(),
                        chiTietSanPhamRequest.getId_mau_sac(),
                        chiTietSanPhamRequest.getId_kich_thuoc()
                );

                // CASE 2.1: Nếu chi tiết sản phẩm với cùng thuộc tính đã tồn tại (cập nhật số lượng)
                if (existingSanPhamByAttributes.isPresent()) {
                    chiTietSanPham = existingSanPhamByAttributes.get();
                    oldQuantity = chiTietSanPham.getSo_luong();
                    chiTietSanPham.setSo_luong(oldQuantity + chiTietSanPhamRequest.getSo_luong());

                    // Đánh dấu đây là trường hợp cập nhật theo thuộc tính
                    isUpdateByAttribute = true;

                    message = "Cập nhật số lượng chi tiết sản phẩm thành công";
                }
                // CASE 3: Tạo mới chi tiết sản phẩm
                else {
                    chiTietSanPham = new ChiTietSanPham();
                    // Sao chép các thuộc tính cơ bản (không bao gồm các đối tượng entity)
                    chiTietSanPham.setGia_ban(chiTietSanPhamRequest.getGia_ban());
                    chiTietSanPham.setSo_luong(chiTietSanPhamRequest.getSo_luong());
                    chiTietSanPham.setTrang_thai("Hoạt động"); // Gán trực tiếp thay vì lấy từ request
                    chiTietSanPham.setQr_code(chiTietSanPhamRequest.getQr_code());
                    chiTietSanPham.setNgay_tao(new Date());
                    message = "Thêm mới chi tiết sản phẩm thành công";
                    isNewDetail = true;
                }
            }

            // Đặt các entity liên quan và ngày sửa
            chiTietSanPham.setMauSac(mauSac);
            chiTietSanPham.setKichThuoc(kichThuoc);
            chiTietSanPham.setSanPham(sanPham);  // Đảm bảo sử dụng SanPham đã tìm thấy từ DB
            chiTietSanPham.setNgay_sua(new Date());

            // Lưu chi tiết sản phẩm
            ChiTietSanPham savedProduct = chiTietSanPhamRepo.save(chiTietSanPham);

            // Xử lý hình ảnh - Đã điều chỉnh logic để giải quyết vấn đề
            if (isNewDetail) {
                // Nếu là chi tiết sản phẩm mới
                if (chiTietSanPhamRequest.getHinh_anh() != null && !chiTietSanPhamRequest.getHinh_anh().isEmpty()) {
                    saveProductImages(savedProduct, chiTietSanPhamRequest.getHinh_anh());
                }
            } else if (isUpdateByAttribute) {
                // Nếu là cập nhật theo thuộc tính (CASE 2.1 - trùng màu sắc và kích thước)
                // Thêm ảnh mới nếu có, nhưng KHÔNG xóa ảnh cũ
                if (chiTietSanPhamRequest.getHinh_anh() != null && !chiTietSanPhamRequest.getHinh_anh().isEmpty()) {
                    addNewProductImagesOnly(savedProduct, chiTietSanPhamRequest.getHinh_anh());
                }
            } else {
                // Nếu là cập nhật theo ID chi tiết sản phẩm (CASE 1)
                if (chiTietSanPhamRequest.getHinh_anh() != null && !chiTietSanPhamRequest.getHinh_anh().isEmpty()) {
                    processProductImages(savedProduct, chiTietSanPhamRequest.getHinh_anh());
                }
            }

            // Chuẩn bị response
            Map<String, Object> response = new HashMap<>();
            response.put("message", message);
            response.put("data", savedProduct);

            // Thêm thông tin về số lượng cũ/mới nếu cập nhật số lượng
            if (oldQuantity > 0) {
                response.put("oldQuantity", oldQuantity);
                response.put("newQuantity", savedProduct.getSo_luong());
            }

            return ResponseEntity.ok().body(response);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    /**
     * Chỉ thêm các ảnh mới mà không xóa ảnh cũ
     * Dùng cho trường hợp cập nhật theo thuộc tính (trùng màu sắc và kích thước)
     */
    private void addNewProductImagesOnly(ChiTietSanPham product, List<String> newImagePaths) {
        if (newImagePaths == null || newImagePaths.isEmpty()) {
            return;
        }

        // Lấy danh sách đường dẫn hình ảnh hiện có
        List<HinhAnhView> existingImages = hinhAnhSanPhamRepo.listHinhAnhTheoSanPham(product.getId_chi_tiet_san_pham());
        List<String> existingImagePaths = existingImages.stream()
                .map(HinhAnhView::getHinh_anh)
                .collect(Collectors.toList());

        // Chỉ thêm các ảnh mới chưa tồn tại, KHÔNG xóa ảnh cũ
        for (String path : newImagePaths) {
            if (!existingImagePaths.contains(path)) {
                HinhAnhSanPham newImage = new HinhAnhSanPham();
                newImage.setChiTietSanPham(product);
                newImage.setHinh_anh(path);
                newImage.setAnh_chinh(false); // Giữ nguyên ảnh chính cũ
                hinhAnhSanPhamRepo.save(newImage);
            }
        }

        // Đảm bảo luôn có một ảnh chính
        ensureMainImageExists(product.getId_chi_tiet_san_pham());
    }

    /**
     * Xử lý hình ảnh sản phẩm khi cập nhật theo ID
     */
    private void processProductImages(ChiTietSanPham product, List<String> newImagePaths) {
        if (newImagePaths == null || newImagePaths.isEmpty()) {
            return;
        }

        // Lấy danh sách đường dẫn hình ảnh hiện có
        List<HinhAnhView> existingImages = hinhAnhSanPhamRepo.listHinhAnhTheoSanPham(product.getId_chi_tiet_san_pham());
        List<String> existingImagePaths = existingImages.stream()
                .map(HinhAnhView::getHinh_anh)
                .collect(Collectors.toList());

        // 1. Xóa ảnh không còn trong danh sách mới
        for (HinhAnhView img : existingImages) {
            if (!newImagePaths.contains(img.getHinh_anh())) {
                hinhAnhSanPhamRepo.deleteById(img.getId_hinh_anh());
            }
        }

        // 2. Thêm ảnh mới
        for (String path : newImagePaths) {
            if (!existingImagePaths.contains(path)) {
                HinhAnhSanPham newImage = new HinhAnhSanPham();
                newImage.setChiTietSanPham(product);
                newImage.setHinh_anh(path);

                // Nếu chưa có ảnh hoặc đây là ảnh đầu tiên, đặt làm ảnh chính
                boolean isMainImage = existingImagePaths.isEmpty() ||
                        (existingImages.stream().noneMatch(HinhAnhView::getAnh_chinh) &&
                                newImagePaths.indexOf(path) == 0);

                newImage.setAnh_chinh(isMainImage);
                hinhAnhSanPhamRepo.save(newImage);
            }
        }

        // 3. Kiểm tra và đảm bảo có ảnh chính
        ensureMainImageExists(product.getId_chi_tiet_san_pham());
    }

    /**
     * Lưu hình ảnh cho sản phẩm mới
     */
    private void saveProductImages(ChiTietSanPham product, List<String> imagePaths) {
        if (imagePaths == null || imagePaths.isEmpty()) {
            return;
        }

        boolean firstImage = true;
        for (String path : imagePaths) {
            HinhAnhSanPham image = new HinhAnhSanPham();
            image.setChiTietSanPham(product);
            image.setHinh_anh(path);
            image.setAnh_chinh(firstImage); // Ảnh đầu tiên là ảnh chính
            hinhAnhSanPhamRepo.save(image);
            firstImage = false;
        }
    }

    /**
     * Đảm bảo luôn có một ảnh chính
     */
    private void ensureMainImageExists(Integer productId) {
        List<HinhAnhView> images = hinhAnhSanPhamRepo.listHinhAnhTheoSanPham(productId);
        boolean hasMainImage = images.stream().anyMatch(HinhAnhView::getAnh_chinh);

        if (!hasMainImage && !images.isEmpty()) {
            HinhAnhSanPham firstImage = hinhAnhSanPhamRepo.findById(images.get(0).getId_hinh_anh())
                    .orElse(null);
            if (firstImage != null) {
                firstImage.setAnh_chinh(true);
                hinhAnhSanPhamRepo.save(firstImage);
            }
        }
    }

    /**
     * Class xử lý ngoại lệ khi tài nguyên không tìm thấy
     */
    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
            super(String.format("%s không tìm thấy với %s: '%s'", resourceName, fieldName, fieldValue));
        }
    }

    public Integer trungMauSacKichThuoc(Integer idMauSac, Integer idKichThuoc, Integer soLuong) {
        int cout = 0;

        return soLuong;
    }

    public String deleteChiTietSanPham(@PathVariable Integer id) {
        ChiTietSanPham ctspDelete = new ChiTietSanPham();
        for (ChiTietSanPham ctsp : chiTietSanPhamRepo.findAll()) {
            if (ctsp.getId_chi_tiet_san_pham() == id) {
                ctspDelete = ctsp;
                ctspDelete.setTrang_thai("Không hoạt động");
            }
        }
        chiTietSanPhamRepo.save(ctspDelete);
        return "Xóa thành công";
    }

    public ResponseEntity<?> chuyenTrangThai(@RequestParam("id") Integer id) {
        ChiTietSanPham ctspDelete = new ChiTietSanPham();
        int countHoatDong = 0;
        ArrayList<ChiTietSanPham> listTam = new ArrayList<>();
        listTam.clear();
        for (ChiTietSanPham ctsp : chiTietSanPhamRepo.findAll()) {
            if (ctsp.getId_chi_tiet_san_pham() == id) {
                ctspDelete = ctsp;
            }
        }
        SanPham sanPham = sanPhamRepo.findById(ctspDelete.getSanPham().getId_san_pham()).get();
        if (sanPham.getTrang_thai().equals("Hoạt động")) {
            if (ctspDelete.getTrang_thai().equalsIgnoreCase("Hoạt động")) {
                ctspDelete.setTrang_thai("Không hoạt động");
                chiTietSanPhamRepo.save(ctspDelete);
            } else {
                ctspDelete.setTrang_thai("Hoạt động");
                chiTietSanPhamRepo.save(ctspDelete);
            }
            for (ChiTietSanPham ctsp : chiTietSanPhamRepo.findAll()) {
                if (ctsp.getSanPham().getId_san_pham().equals(sanPham.getId_san_pham())) {
                    listTam.add(ctsp);
                }
            }
            for (ChiTietSanPham ctspLoad : listTam) {
                if (ctspLoad.getTrang_thai().equals("Hoạt động")) {
                    countHoatDong++;
                }
            }
            if (countHoatDong == 0) {
                sanPhamService.chuyenTrangThai(sanPham.getId_san_pham());
            }
        } else {
            // Nếu người dùng đang cố gắng kích hoạt CTSP
            if (ctspDelete.getTrang_thai().equalsIgnoreCase("Không hoạt động")) {
                // Kích hoạt CTSP và sản phẩm cha
                ctspDelete.setTrang_thai("Hoạt động");
                chiTietSanPhamRepo.save(ctspDelete);
                sanPham.setTrang_thai("Hoạt động");
                sanPhamRepo.save(sanPham);
            }
            // Nếu người dùng đang cố gắng vô hiệu hóa CTSP, giữ nguyên trạng thái sản phẩm
            // cha
            else {
                ctspDelete.setTrang_thai("Không hoạt động");
                chiTietSanPhamRepo.save(ctspDelete);
                // Không thay đổi trạng thái sản phẩm cha
            }
        }

        return ResponseEntity.ok(ctspDelete);
    }

    public ArrayList<ChiTietSanPhamView> listTimKiem(@RequestParam(name = "keywork") String keyword) {
        ArrayList<ChiTietSanPhamView> listTam = new ArrayList<>();
        for (ChiTietSanPhamView ctsp : chiTietSanPhamRepo.listCTSP()) {
            if (ctsp.getTen_san_pham().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)) ||
                    ctsp.getTen_chat_lieu().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)) ||
                    ctsp.getTen_danh_muc().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)) ||
                    ctsp.getTen_thuong_hieu().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))) {
                listTam.add(ctsp);
            }
        }
        return listTam;
    }

    public ArrayList<ChiTietSanPhamView> listLocCTSP(String tenSanPham, float giaBanMin, float giaBanMax,
            Integer soLuongMin, Integer soLuongMax, String trangThai,
            String tenMauSac, String tenDanhMuc, String tenThuongHieu, String tenChatLieu) {
        return chiTietSanPhamRepo.listLocCTSP(tenSanPham, giaBanMin, giaBanMax, soLuongMin, soLuongMax,
                trangThai, tenMauSac, tenDanhMuc, tenThuongHieu, tenChatLieu);
        // Thiếu Kích thước
    }

    public BigDecimal getMaxGiaBan() {
        BigDecimal maxGiaBan = BigDecimal.ZERO;
        for (ChiTietSanPham ctsp : chiTietSanPhamRepo.findAll()) {
            BigDecimal giaBan = ctsp.getGia_ban();
            if (giaBan != null && giaBan.compareTo(maxGiaBan) > 0) {
                maxGiaBan = giaBan;
            }
        }
        return maxGiaBan;
    }

    public Page<ChiTietSanPhamView> sapXep(Pageable pageable) {
        return chiTietSanPhamRepo.listPhanTrangChiTietSanPham(pageable);
    }

    // @Cacheable(value = "ctspBySP", key = "#idSanPham")
    public ArrayList<ChiTietSanPhamView> listCTSPTheoSanPham(@RequestParam("id") Integer id) {
        return chiTietSanPhamRepo.listCTSPFolowSanPham(id);
    }

    public List<ChiTietSanPhamView> getCTSPBySanPhamFull(@RequestParam("idSanPham") Integer idSanPham) {
        return chiTietSanPhamRepo.getCTSPBySanPhamFull(idSanPham);
    }

    // @CacheEvict(value = "ctspBySp", key = "#idSanPham")
    @Caching(evict = {
            @CacheEvict(value = "ctspBySp", allEntries = true),
            @CacheEvict(value = "products", key = "'allSanPham'")
    })
    public ResponseEntity<?> changeAllCTSPKhongHoatDong(@RequestParam("id") Integer id) {
        ArrayList<ChiTietSanPham> listTam = new ArrayList<>();
        int countCTSP = 0;
        listTam.clear();
        System.out.println("Chuyen ctsp khong hoat dong");
        ChiTietSanPham ctsp = chiTietSanPhamRepo.findById(id).get();
        System.out.println("idTatCaCTSPKhongHoatDOng" + ctsp.getId_chi_tiet_san_pham());
        ctsp.setTrang_thai("Không hoạt động");
        chiTietSanPhamRepo.save(ctsp);
        SanPham sanPham = sanPhamRepo.findById(ctsp.getSanPham().getId_san_pham()).get();
        for (ChiTietSanPham ctspXet : chiTietSanPhamRepo.findAll()) {
            if (ctspXet.getSanPham().getId_san_pham().equals(sanPham.getId_san_pham())) {
                listTam.add(ctspXet);
            }
        }
        for (ChiTietSanPham ctspChuyen : listTam) {
            if (ctspChuyen.getTrang_thai().equals("Hoạt động")) {
                countCTSP++;
            }
        }
        if (countCTSP == 0) {
            sanPham.setTrang_thai("Không hoạt động");
            sanPhamRepo.save(sanPham);
        }
        return ResponseEntity.ok(ctsp);
    }

    @Caching(evict = {
            @CacheEvict(value = "ctspBySp", allEntries = true),
            @CacheEvict(value = "products", key = "'allSanPham'")
    })
    public ResponseEntity<?> changeAllCTSPHoatDong(@RequestParam("id") Integer id) {
        ArrayList<ChiTietSanPham> listTam = new ArrayList<>();
        int countCTSP = 0;
        listTam.clear();
        System.out.println("Chuyen ctsp khong hoat dong");
        ChiTietSanPham ctsp = chiTietSanPhamRepo.findById(id).get();
        System.out.println("idTatCaCTSPHoatDOng" + ctsp.getId_chi_tiet_san_pham());
        ctsp.setTrang_thai("Hoạt động");
        chiTietSanPhamRepo.save(ctsp);
        SanPham sanPham = sanPhamRepo.findById(ctsp.getSanPham().getId_san_pham()).get();
        for (ChiTietSanPham ctspXet : chiTietSanPhamRepo.findAll()) {
            if (ctspXet.getSanPham().getId_san_pham().equals(sanPham.getId_san_pham())) {
                listTam.add(ctspXet);
            }
        }
        for (ChiTietSanPham ctspChuyen : listTam) {
            if (ctspChuyen.getTrang_thai().equals("Hoạt động")) {
                countCTSP++;
            }
        }
        if (countCTSP >= 0) {
            sanPham.setTrang_thai("Hoạt động");
            sanPhamRepo.save(sanPham);
        }
        return ResponseEntity.ok(ctsp);
    }

    public ResponseEntity<List<SanPhamView>> timKiemVaLoc(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "tenSanPham", required = false) String tenSanPham,
            @RequestParam(name = "giaBanMin", required = false) Float giaBanMin,
            @RequestParam(name = "giaBanMax", required = false) Float giaBanMax,
            @RequestParam(name = "soLuongMin", required = false) Integer soLuongMin,
            @RequestParam(name = "soLuongMax", required = false) Integer soLuongMax,
            @RequestParam(name = "trangThai", required = false) String trangThai,
            @RequestParam(name = "listMauSac", required = false) List<String> listMauSac,
            @RequestParam(name = "listDanhMuc", required = false) List<String> listDanhMuc,
            @RequestParam(name = "listThuongHieu", required = false) List<String> listThuongHieu,
            @RequestParam(name = "listChatLieu", required = false) List<String> listChatLieu,
            @RequestParam(name = "listKichThuoc", required = false) List<String> listKichThuoc) {
        // Log các tham số để debug
        System.out.println("Tham số nhận được:");
        System.out.println("keyword: " + keyword);
        System.out.println("tenSanPham: " + tenSanPham);
        System.out.println("listMauSac: " + listMauSac);
        System.out.println("listDanhMuc: " + listDanhMuc);
        System.out.println("listThuongHieu: " + listThuongHieu);
        System.out.println("listChatLieu: " + listChatLieu);
        System.out.println("listKichThuoc: " + listKichThuoc);

        List<ChiTietSanPhamView> danhSachSanPham = chiTietSanPhamRepo.listCTSP();
        System.out.println("Tổng số sản phẩm ban đầu: " + danhSachSanPham.size());

        if (isEmpty(keyword) &&
                isEmpty(tenSanPham) &&
                giaBanMin == null &&
                giaBanMax == null &&
                soLuongMin == null &&
                soLuongMax == null &&
                isEmpty(trangThai) &&
                isEmpty(listMauSac) &&
                isEmpty(listDanhMuc) &&
                isEmpty(listThuongHieu) &&
                isEmpty(listChatLieu) &&
                isEmpty(listKichThuoc)) {
            List<Integer> listKhongCoGi = danhSachSanPham.stream()
                    .map(ChiTietSanPhamView::getId_chi_tiet_san_pham)
                    .distinct()
                    .collect(Collectors.toList());
            return ResponseEntity.ok(sanPhamRepo.getSanPhamByListCTSP(listKhongCoGi));
        }

        Stream<ChiTietSanPhamView> stream = danhSachSanPham.stream();
        int countBefore = (int) stream.count(); // Đếm số lượng ban đầu
        stream = danhSachSanPham.stream(); // Reset stream sau khi đã count

        System.out.println("Số lượng trước khi lọc: " + countBefore);

        if (!isEmpty(keyword)) {
            String keywordLowercase = keyword.toLowerCase(Locale.ROOT);
            stream = stream.filter(ctsp -> (ctsp.getTen_san_pham() != null
                    && ctsp.getTen_san_pham().toLowerCase(Locale.ROOT).contains(keywordLowercase)) ||
                    (ctsp.getMa_san_pham() != null
                            && ctsp.getMa_san_pham().toLowerCase(Locale.ROOT).contains(keywordLowercase))
                    ||
                    (ctsp.getTen_chat_lieu() != null
                            && ctsp.getTen_chat_lieu().toLowerCase(Locale.ROOT).contains(keywordLowercase))
                    ||
                    (ctsp.getTen_danh_muc() != null
                            && ctsp.getTen_danh_muc().toLowerCase(Locale.ROOT).contains(keywordLowercase))
                    ||
                    (ctsp.getTen_thuong_hieu() != null
                            && ctsp.getTen_thuong_hieu().toLowerCase(Locale.ROOT).contains(keywordLowercase))
                    ||
                    (ctsp.getTen_mau_sac() != null
                            && ctsp.getTen_mau_sac().toLowerCase(Locale.ROOT).contains(keywordLowercase))
                    ||
                    (ctsp.getGia_tri() != null
                            && ctsp.getGia_tri().toLowerCase(Locale.ROOT).contains(keywordLowercase)));
            System.out.println("Sau khi lọc keyword: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        if (!isEmpty(tenSanPham)) {
            String tenSanPhamLowercase = tenSanPham.toLowerCase(Locale.ROOT);
            stream = stream.filter(ctsp -> ctsp.getTen_san_pham() != null &&
                    ctsp.getTen_san_pham().toLowerCase(Locale.ROOT).contains(tenSanPhamLowercase));
            System.out.println("Sau khi lọc tên sản phẩm: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        if (giaBanMin != null) {
            stream = stream.filter(ctsp -> ctsp.getGia_ban() != null && ctsp.getGia_ban() >= giaBanMin);
            System.out.println("Sau khi lọc giá bán min: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        if (giaBanMax != null) {
            stream = stream.filter(ctsp -> ctsp.getGia_ban() != null && ctsp.getGia_ban() <= giaBanMax);
            System.out.println("Sau khi lọc giá bán max: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        if (soLuongMin != null) {
            stream = stream.filter(ctsp -> ctsp.getSo_luong() != null && ctsp.getSo_luong() >= soLuongMin);
            System.out.println("Sau khi lọc số lượng min: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        if (soLuongMax != null) {
            stream = stream.filter(ctsp -> ctsp.getSo_luong() != null && ctsp.getSo_luong() <= soLuongMax);
            System.out.println("Sau khi lọc số lượng max: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        if (!isEmpty(trangThai)) {
            stream = stream.filter(ctsp -> ctsp.getTrang_thai() != null &&
                    trangThai.equalsIgnoreCase(ctsp.getTrang_thai()));
            System.out.println("Sau khi lọc trạng thái: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        // Sửa lỗi: Thêm debug và xử lý mềm dẻo hơn với màu sắc
        if (!isEmpty(listMauSac)) {
            System.out.println("Lọc theo màu sắc: " + listMauSac);
            // In ra tất cả các giá trị màu sắc trong dữ liệu để kiểm tra
            List<String> allMauSac = danhSachSanPham.stream()
                    .map(ChiTietSanPhamView::getTen_mau_sac)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            System.out.println("Các màu sắc trong dữ liệu: " + allMauSac);

            stream = stream.filter(ctsp -> {
                if (ctsp.getTen_mau_sac() == null)
                    return false;
                String mauSacValue = ctsp.getTen_mau_sac().trim();

                for (String ms : listMauSac) {
                    if (ms != null && ms.trim().equalsIgnoreCase(mauSacValue)) {
                        return true;
                    }
                }
                return false;
            });
            System.out.println("Sau khi lọc màu sắc: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        // Sửa lỗi: Thêm debug và xử lý mềm dẻo hơn với danh mục
        if (!isEmpty(listDanhMuc)) {
            System.out.println("Lọc theo danh mục: " + listDanhMuc);
            // In ra tất cả các giá trị danh mục trong dữ liệu để kiểm tra
            List<String> allDanhMuc = danhSachSanPham.stream()
                    .map(ChiTietSanPhamView::getTen_danh_muc)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            System.out.println("Các danh mục trong dữ liệu: " + allDanhMuc);

            stream = stream.filter(ctsp -> {
                if (ctsp.getTen_danh_muc() == null)
                    return false;
                String danhMucValue = ctsp.getTen_danh_muc().trim();

                for (String dm : listDanhMuc) {
                    if (dm != null && dm.trim().equalsIgnoreCase(danhMucValue)) {
                        return true;
                    }
                }
                return false;
            });
            System.out.println("Sau khi lọc danh mục: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        // Sửa lỗi: Thêm debug và xử lý mềm dẻo hơn với thương hiệu
        if (!isEmpty(listThuongHieu)) {
            System.out.println("Lọc theo thương hiệu: " + listThuongHieu);
            // In ra tất cả các giá trị thương hiệu trong dữ liệu để kiểm tra
            List<String> allThuongHieu = danhSachSanPham.stream()
                    .map(ChiTietSanPhamView::getTen_thuong_hieu)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            System.out.println("Các thương hiệu trong dữ liệu: " + allThuongHieu);

            stream = stream.filter(ctsp -> {
                if (ctsp.getTen_thuong_hieu() == null)
                    return false;
                String thuongHieuValue = ctsp.getTen_thuong_hieu().trim();

                for (String th : listThuongHieu) {
                    if (th != null && th.trim().equalsIgnoreCase(thuongHieuValue)) {
                        return true;
                    }
                }
                return false;
            });
            System.out.println("Sau khi lọc thương hiệu: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        // Sửa lỗi: Thêm debug và xử lý mềm dẻo hơn với chất liệu
        if (!isEmpty(listChatLieu)) {
            System.out.println("Lọc theo chất liệu: " + listChatLieu);
            // In ra tất cả các giá trị chất liệu trong dữ liệu để kiểm tra
            List<String> allChatLieu = danhSachSanPham.stream()
                    .map(ChiTietSanPhamView::getTen_chat_lieu)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            System.out.println("Các chất liệu trong dữ liệu: " + allChatLieu);

            stream = stream.filter(ctsp -> {
                if (ctsp.getTen_chat_lieu() == null)
                    return false;
                String chatLieuValue = ctsp.getTen_chat_lieu().trim();

                for (String cl : listChatLieu) {
                    if (cl != null && cl.trim().equalsIgnoreCase(chatLieuValue)) {
                        return true;
                    }
                }
                return false;
            });
            System.out.println("Sau khi lọc chất liệu: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        // Sửa lỗi: Thêm debug và xử lý mềm dẻo hơn với kích thước
        if (!isEmpty(listKichThuoc)) {
            System.out.println("Lọc theo kích thước: " + listKichThuoc);
            // In ra tất cả các giá trị kích thước trong dữ liệu để kiểm tra
            List<String> allKichThuoc = danhSachSanPham.stream()
                    .map(ChiTietSanPhamView::getGia_tri)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            System.out.println("Các kích thước trong dữ liệu: " + allKichThuoc);

            stream = stream.filter(ctsp -> {
                if (ctsp.getGia_tri() == null)
                    return false;
                String giaTriValue = ctsp.getGia_tri().trim();

                for (String kt : listKichThuoc) {
                    if (kt != null && kt.trim().equalsIgnoreCase(giaTriValue)) {
                        return true;
                    }
                }
                return false;
            });
            System.out.println("Sau khi lọc kích thước: " + stream.count());
            stream = danhSachSanPham.stream(); // Reset stream sau khi đã count
        }

        // Áp dụng tất cả các bộ lọc cho stream một lần cuối
        Stream<ChiTietSanPhamView> finalStream = danhSachSanPham.stream();

        // Áp dụng từng bộ lọc
        if (!isEmpty(keyword)) {
            String keywordLowercase = keyword.toLowerCase(Locale.ROOT);
            finalStream = finalStream.filter(ctsp -> (ctsp.getTen_san_pham() != null
                    && ctsp.getTen_san_pham().toLowerCase(Locale.ROOT).contains(keywordLowercase)) ||
                    (ctsp.getMa_san_pham() != null
                            && ctsp.getMa_san_pham().toLowerCase(Locale.ROOT).contains(keywordLowercase))
                    ||
                    (ctsp.getTen_chat_lieu() != null
                            && ctsp.getTen_chat_lieu().toLowerCase(Locale.ROOT).contains(keywordLowercase))
                    ||
                    (ctsp.getTen_danh_muc() != null
                            && ctsp.getTen_danh_muc().toLowerCase(Locale.ROOT).contains(keywordLowercase))
                    ||
                    (ctsp.getTen_thuong_hieu() != null
                            && ctsp.getTen_thuong_hieu().toLowerCase(Locale.ROOT).contains(keywordLowercase))
                    ||
                    (ctsp.getTen_mau_sac() != null
                            && ctsp.getTen_mau_sac().toLowerCase(Locale.ROOT).contains(keywordLowercase))
                    ||
                    (ctsp.getGia_tri() != null
                            && ctsp.getGia_tri().toLowerCase(Locale.ROOT).contains(keywordLowercase)));
        }

        if (!isEmpty(tenSanPham)) {
            String tenSanPhamLowercase = tenSanPham.toLowerCase(Locale.ROOT);
            finalStream = finalStream.filter(ctsp -> ctsp.getTen_san_pham() != null &&
                    ctsp.getTen_san_pham().toLowerCase(Locale.ROOT).contains(tenSanPhamLowercase));
        }

        if (giaBanMin != null) {
            finalStream = finalStream.filter(ctsp -> ctsp.getGia_ban() != null && ctsp.getGia_ban() >= giaBanMin);
        }

        if (giaBanMax != null) {
            finalStream = finalStream.filter(ctsp -> ctsp.getGia_ban() != null && ctsp.getGia_ban() <= giaBanMax);
        }

        if (soLuongMin != null) {
            finalStream = finalStream.filter(ctsp -> ctsp.getSo_luong() != null && ctsp.getSo_luong() >= soLuongMin);
        }

        if (soLuongMax != null) {
            finalStream = finalStream.filter(ctsp -> ctsp.getSo_luong() != null && ctsp.getSo_luong() <= soLuongMax);
        }

        if (!isEmpty(trangThai)) {
            finalStream = finalStream.filter(ctsp -> ctsp.getTrang_thai() != null &&
                    trangThai.equalsIgnoreCase(ctsp.getTrang_thai()));
        }

        if (!isEmpty(listMauSac)) {
            finalStream = finalStream.filter(ctsp -> {
                if (ctsp.getTen_mau_sac() == null)
                    return false;
                String mauSacValue = ctsp.getTen_mau_sac().trim();

                for (String ms : listMauSac) {
                    if (ms != null && ms.trim().equalsIgnoreCase(mauSacValue)) {
                        return true;
                    }
                }
                return false;
            });
        }

        if (!isEmpty(listDanhMuc)) {
            finalStream = finalStream.filter(ctsp -> {
                if (ctsp.getTen_danh_muc() == null)
                    return false;
                String danhMucValue = ctsp.getTen_danh_muc().trim();

                for (String dm : listDanhMuc) {
                    if (dm != null && dm.trim().equalsIgnoreCase(danhMucValue)) {
                        return true;
                    }
                }
                return false;
            });
        }

        if (!isEmpty(listThuongHieu)) {
            finalStream = finalStream.filter(ctsp -> {
                if (ctsp.getTen_thuong_hieu() == null)
                    return false;
                String thuongHieuValue = ctsp.getTen_thuong_hieu().trim();

                for (String th : listThuongHieu) {
                    if (th != null && th.trim().equalsIgnoreCase(thuongHieuValue)) {
                        return true;
                    }
                }
                return false;
            });
        }

        if (!isEmpty(listChatLieu)) {
            finalStream = finalStream.filter(ctsp -> {
                if (ctsp.getTen_chat_lieu() == null)
                    return false;
                String chatLieuValue = ctsp.getTen_chat_lieu().trim();

                for (String cl : listChatLieu) {
                    if (cl != null && cl.trim().equalsIgnoreCase(chatLieuValue)) {
                        return true;
                    }
                }
                return false;
            });
        }

        if (!isEmpty(listKichThuoc)) {
            finalStream = finalStream.filter(ctsp -> {
                if (ctsp.getGia_tri() == null)
                    return false;
                String giaTriValue = ctsp.getGia_tri().trim();

                for (String kt : listKichThuoc) {
                    if (kt != null && kt.trim().equalsIgnoreCase(giaTriValue)) {
                        return true;
                    }
                }
                return false;
            });
        }

        // Chuyển stream trở lại thành danh sách
        List<ChiTietSanPhamView> ketQua = finalStream.collect(Collectors.toList());
        System.out.println("Kết quả lọc cuối cùng: " + ketQua.size());

        if (ketQua.isEmpty()) {
            System.out.println("Không có kết quả lọc");
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<Integer> listIDCTSP = ketQua.stream()
                .map(ChiTietSanPhamView::getId_chi_tiet_san_pham)
                .collect(Collectors.toList());
        ArrayList<SanPhamView> listReturn = (ArrayList<SanPhamView>) sanPhamRepo.getSanPhamByListCTSP(listIDCTSP);
        System.out.println();
        if (listReturn.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        } else {
            return ResponseEntity.ok(listReturn);
        }
        // Đang chuyển list ctsp -> sản phẩm

    }

    private List<SanPhamView> changeListCTSPToListSp(List<ChiTietSanPhamView> list) {
        return null;
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Phương thức tiện ích để kiểm tra danh sách trống hoặc null
    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
