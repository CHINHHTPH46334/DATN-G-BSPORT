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
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        Instant instant = Instant.parse(jsDateString);
        ZonedDateTime utc7Time = instant.atZone(ZoneId.of("Asia/Bangkok"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return utc7Time.format(formatter);
    }

    public ResponseEntity<?> saveChiTietSanPham(@Valid @RequestBody ChiTietSanPhamRequest chiTietSanPhamRequest, BindingResult result) {
        Integer count = 0;
        Integer slCu = 0;
        Integer id = 0;
        Integer count2 = 0;
        Integer count3 = 0;
        Date ngay_sua_lo = null;

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream().map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        } else {
            if (chiTietSanPhamRequest.getId_chi_tiet_san_pham() == null || chiTietSanPhamRequest.getId_chi_tiet_san_pham().equals("")) {
                for (ChiTietSanPham ctsp : chiTietSanPhamRepo.findAll()) {
                    if (ctsp.getMauSac().getId_mau_sac() == chiTietSanPhamRequest.getId_mau_sac()
                            && ctsp.getKichThuoc().getId_kich_thuoc() == chiTietSanPhamRequest.getId_kich_thuoc()
                            && ctsp.getSanPham().getId_san_pham() == chiTietSanPhamRequest.getId_san_pham()
                            && ctsp.getId_chi_tiet_san_pham() != chiTietSanPhamRequest.getId_chi_tiet_san_pham()) {
                        count3++;
                        id = ctsp.getId_chi_tiet_san_pham();
                    }
                }
                if (count3 > 0) {
                    ChiTietSanPham ctspSua = chiTietSanPhamRepo.findById(id).get();
                    slCu = ctspSua.getSo_luong();
                    ctspSua.setId_chi_tiet_san_pham(id);
                    ctspSua.setSo_luong(ctspSua.getSo_luong() + chiTietSanPhamRequest.getSo_luong());
                    ctspSua.setNgay_sua(new Date());
                    if (ctspSua.getSo_luong() == (slCu + chiTietSanPhamRequest.getSo_luong())) {
                        chiTietSanPhamRepo.save(ctspSua);
                        return ResponseEntity.ok("cập nhật số lượng");
                    } else {
                        return ResponseEntity.badRequest().body("KHông ổn");
                    }
                } else {
                    ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
                    Optional<KichThuoc> kichThuocOptional = kichThuocRepo.findById(chiTietSanPhamRequest.getId_kich_thuoc());
                    Optional<MauSac> mauSacOptional = mauSacRepo.findById(chiTietSanPhamRequest.getId_mau_sac());
                    Optional<SanPham> sanPhamOptional = sanPhamRepo.findById(chiTietSanPhamRequest.getId_san_pham());
                    KichThuoc kichThuoc = kichThuocOptional.orElse(new KichThuoc());
                    MauSac mauSac = mauSacOptional.orElse(new MauSac());
                    SanPham sanPham = sanPhamOptional.orElse(new SanPham());
                    BeanUtils.copyProperties(chiTietSanPhamRequest, chiTietSanPham);
                    chiTietSanPham.setMauSac(mauSac);
                    chiTietSanPham.setKichThuoc(kichThuoc);
                    chiTietSanPham.setSanPham(sanPham);
                    chiTietSanPham.setTrang_thai("Hoạt động");
                    chiTietSanPham.setNgay_tao(new Date());
                    chiTietSanPham.setNgay_sua(new Date());
                    chiTietSanPhamRepo.save(chiTietSanPham);
                    for (String ha : chiTietSanPhamRequest.getHinh_anh()) {
                        HinhAnhSanPham hinhAnhSanPham = new HinhAnhSanPham();
                        hinhAnhSanPham.setChiTietSanPham(chiTietSanPham);
                        hinhAnhSanPham.setHinh_anh(ha);
                        hinhAnhSanPham.setAnh_chinh(true);
                        hinhAnhSanPhamRepo.save(hinhAnhSanPham);
                    }
                    return ResponseEntity.ok("Lưu thành công");
                }
            } else {
                for (ChiTietSanPham ctspCheckTrung : chiTietSanPhamRepo.findAll()) {
                    if (ctspCheckTrung.getId_chi_tiet_san_pham().equals(chiTietSanPhamRequest.getId_chi_tiet_san_pham())) {
                        count++;
                        ngay_sua_lo = (ctspCheckTrung.getNgay_tao());
                    }
                }
                if (count > 0) {
                    ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
                    chiTietSanPham.setId_chi_tiet_san_pham(chiTietSanPhamRequest.getId_chi_tiet_san_pham());
                    Optional<KichThuoc> kichThuocOptional = kichThuocRepo.findById(chiTietSanPhamRequest.getId_kich_thuoc());
                    Optional<MauSac> mauSacOptional = mauSacRepo.findById(chiTietSanPhamRequest.getId_mau_sac());
                    Optional<SanPham> sanPhamOptional = sanPhamRepo.findById(chiTietSanPhamRequest.getId_san_pham());

                    KichThuoc kichThuoc = kichThuocOptional.orElse(new KichThuoc());
                    MauSac mauSac = mauSacOptional.orElse(new MauSac());
                    SanPham sanPham = sanPhamOptional.orElse(new SanPham());
                    BeanUtils.copyProperties(chiTietSanPhamRequest, chiTietSanPham);

                    chiTietSanPham.setMauSac(mauSac);
                    chiTietSanPham.setKichThuoc(kichThuoc);
                    chiTietSanPham.setSanPham(sanPham);
                    chiTietSanPham.setNgay_sua(new Date());
                    chiTietSanPham.setNgay_tao(ngay_sua_lo);
                    chiTietSanPham.toString();
                    chiTietSanPhamRepo.save(chiTietSanPham);

                    List<HinhAnhView> anhHienCo = hinhAnhSanPhamRepo.listHinhAnhTheoSanPham(chiTietSanPham.getId_chi_tiet_san_pham());
                    List<String> duongDanAnhHienCo = anhHienCo.stream()
                            .map(HinhAnhView::getHinh_anh)
                            .collect(Collectors.toList());

                    List<String> duongDanAnhMoi = chiTietSanPhamRequest.getHinh_anh();

                    for (HinhAnhView anhHienTai : anhHienCo) {
                        if (!duongDanAnhMoi.contains(anhHienTai.getHinh_anh())) {
                            hinhAnhSanPhamRepo.delete(hinhAnhSanPhamRepo.findById(anhHienTai.getId_hinh_anh()).get());
                        }
                    }

                    for (String duongDanAnh : duongDanAnhMoi) {
                        if (!duongDanAnhHienCo.contains(duongDanAnh)) {
                            HinhAnhSanPham hinhAnhSanPham = new HinhAnhSanPham();
                            hinhAnhSanPham.setChiTietSanPham(chiTietSanPhamRepo.findById(chiTietSanPham.getId_chi_tiet_san_pham()).get());
                            hinhAnhSanPham.setHinh_anh(duongDanAnh);

                            boolean isAnhChinh = duongDanAnhHienCo.isEmpty() ||
                                    (anhHienCo.stream().noneMatch(a -> a.getAnh_chinh()) && duongDanAnhMoi.indexOf(duongDanAnh) == 0);

                            hinhAnhSanPham.setAnh_chinh(isAnhChinh);
                            hinhAnhSanPhamRepo.save(hinhAnhSanPham);
                        }
                    }

                    boolean hasMainImage = hinhAnhSanPhamRepo.listHinhAnhTheoSanPham(chiTietSanPham.getId_chi_tiet_san_pham())
                            .stream().anyMatch(a -> a.getAnh_chinh());

                    if (!hasMainImage && !duongDanAnhMoi.isEmpty()) {
                        List<HinhAnhSanPham> allImages = new ArrayList<>();
                        for (HinhAnhView haview : hinhAnhSanPhamRepo.listHinhAnhTheoSanPham(chiTietSanPham.getId_chi_tiet_san_pham())) {
                            allImages.add(hinhAnhSanPhamRepo.findById(haview.getId_hinh_anh()).get());
                        }
                        if (!allImages.isEmpty()) {
                            HinhAnhSanPham firstImage = allImages.get(0);
                            firstImage.setAnh_chinh(true);
                            hinhAnhSanPhamRepo.save(firstImage);
                        }
                    }

                    return ResponseEntity.ok("Lưu thành công" + chiTietSanPham);
                } else {
                    for (ChiTietSanPham ctsp : chiTietSanPhamRepo.findAll()) {
                        if (ctsp.getMauSac().getId_mau_sac() == chiTietSanPhamRequest.getId_mau_sac()
                                && ctsp.getKichThuoc().getId_kich_thuoc() == chiTietSanPhamRequest.getId_kich_thuoc()
                                && ctsp.getSanPham().getId_san_pham() == chiTietSanPhamRequest.getId_san_pham()
                                && ctsp.getId_chi_tiet_san_pham() != chiTietSanPhamRequest.getId_chi_tiet_san_pham()) {
                            count2++;
                            id = ctsp.getId_chi_tiet_san_pham();
                        }
                    }
                    if (count2 > 0) {
                        ChiTietSanPham ctspSua = chiTietSanPhamRepo.findById(id).get();
                        slCu = ctspSua.getSo_luong();
                        ctspSua.setId_chi_tiet_san_pham(id);
                        ctspSua.setSo_luong(ctspSua.getSo_luong() + chiTietSanPhamRequest.getSo_luong());
                        ctspSua.setNgay_sua(new Date());
                        if (ctspSua.getSo_luong() == (slCu + chiTietSanPhamRequest.getSo_luong())) {
                            ctspSua.setNgay_sua(new Date());
                            chiTietSanPhamRepo.save(ctspSua);
                            return ResponseEntity.ok("cập nhật số lượng" + ctspSua);
                        } else {
                            return ResponseEntity.badRequest().body("KHông ổn");
                        }
                    } else {
                        ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
                        Optional<KichThuoc> kichThuocOptional = kichThuocRepo.findById(chiTietSanPhamRequest.getId_kich_thuoc());
                        Optional<MauSac> mauSacOptional = mauSacRepo.findById(chiTietSanPhamRequest.getId_mau_sac());
                        Optional<SanPham> sanPhamOptional = sanPhamRepo.findById(chiTietSanPhamRequest.getId_san_pham());
                        KichThuoc kichThuoc = kichThuocOptional.orElse(new KichThuoc());
                        MauSac mauSac = mauSacOptional.orElse(new MauSac());
                        SanPham sanPham = sanPhamOptional.orElse(new SanPham());
                        BeanUtils.copyProperties(chiTietSanPhamRequest, chiTietSanPham);
                        chiTietSanPham.setMauSac(mauSac);
                        chiTietSanPham.setKichThuoc(kichThuoc);
                        chiTietSanPham.setSanPham(sanPham);
                        chiTietSanPham.setTrang_thai("Hoạt động");
                        chiTietSanPham.setNgay_tao(new Date());
                        chiTietSanPham.setNgay_sua(new Date());
                        chiTietSanPhamRepo.save(chiTietSanPham);
                        for (String ha : chiTietSanPhamRequest.getHinh_anh()) {
                            HinhAnhSanPham hinhAnhSanPham = new HinhAnhSanPham();
                            hinhAnhSanPham.setChiTietSanPham(chiTietSanPham);
                            hinhAnhSanPham.setHinh_anh(ha);
                            hinhAnhSanPham.setAnh_chinh(true);
                            hinhAnhSanPhamRepo.save(hinhAnhSanPham);
                        }
                        return ResponseEntity.ok("Lưu thành công" + chiTietSanPham);
                    }
                }
            }
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
            if (ctspDelete.getTrang_thai().equalsIgnoreCase("Không hoạt động")) {
                ctspDelete.setTrang_thai("Hoạt động");
                chiTietSanPhamRepo.save(ctspDelete);
                sanPham.setTrang_thai("Hoạt động");
                sanPhamRepo.save(sanPham);
            } else {
                ctspDelete.setTrang_thai("Không hoạt động");
                chiTietSanPhamRepo.save(ctspDelete);
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

    public ArrayList<ChiTietSanPhamView> listCTSPTheoSanPham(@RequestParam("id") Integer id) {
        return chiTietSanPhamRepo.listCTSPFolowSanPham(id);
    }

    public List<ChiTietSanPhamView> getCTSPBySanPhamFull(@RequestParam("idSanPham") Integer idSanPham) {
        return chiTietSanPhamRepo.getCTSPBySanPhamFull(idSanPham);
    }

    @Caching(evict = {
            @CacheEvict(value = "ctspBySp", allEntries = true),
            @CacheEvict(value = "products", key = "'allSanPham'")
    })
    public ResponseEntity<?> changeAllCTSPKhongHoatDong(@RequestParam("id") Integer id) {
        ArrayList<ChiTietSanPham> listTam = new ArrayList<>();
        int countCTSP = 0;
        listTam.clear();
        ChiTietSanPham ctsp = chiTietSanPhamRepo.findById(id).get();
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
        ChiTietSanPham ctsp = chiTietSanPhamRepo.findById(id).get();
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
        System.out.println("listMauSac: " + listMauSac);
        System.out.println("listDanhMuc: " + listDanhMuc);
        System.out.println("listThuongHieu: " + listThuongHieu);
        System.out.println("listChatLieu: " + listChatLieu);
        System.out.println("listKichThuoc: " + listKichThuoc);

        List<ChiTietSanPhamView> danhSachSanPham = chiTietSanPhamRepo.listCTSP();
        System.out.println("Tổng số sản phẩm ban đầu: " + danhSachSanPham.size());

        if (isEmpty(keyword) &&
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
                    .collect(Collectors.toList());
            return ResponseEntity.ok(sanPhamRepo.getSanPhamByListCTSP(listKhongCoGi));
        }

        List<ChiTietSanPhamView> ketQua = new ArrayList<>(danhSachSanPham);

        // Áp dụng các bộ lọc
        if (!isEmpty(keyword)) {
            String keywordLowercase = keyword.toLowerCase(Locale.ROOT);
            String[] keywords = keywordLowercase.split("\\s+");
            ketQua = ketQua.stream()
                .filter(ctsp -> {
                    String content = Stream.of(
                        ctsp.getTen_san_pham(),
                        ctsp.getMa_san_pham(),
                        ctsp.getTen_chat_lieu(),
                        ctsp.getTen_danh_muc(),
                        ctsp.getTen_thuong_hieu(),
                        ctsp.getTen_mau_sac(),
                        ctsp.getGia_tri()
                    )
                    .filter(Objects::nonNull)
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining(" "));
                    
                    return Arrays.stream(keywords)
                               .filter(kw -> !kw.equals("không"))
                               .allMatch(content::contains);
                })
                .collect(Collectors.toList());
            System.out.println("Sau khi lọc keyword: " + ketQua.size());
        }

        if (giaBanMin != null) {
            ketQua = ketQua.stream()
                .filter(ctsp -> ctsp.getGia_ban() != null && ctsp.getGia_ban() >= giaBanMin)
                .collect(Collectors.toList());
            System.out.println("Sau khi lọc giá bán min: " + ketQua.size());
        }

        if (giaBanMax != null) {
            ketQua = ketQua.stream()
                .filter(ctsp -> ctsp.getGia_ban() != null && ctsp.getGia_ban() <= giaBanMax)
                .collect(Collectors.toList());
            System.out.println("Sau khi lọc giá bán max: " + ketQua.size());
        }

        if (soLuongMin != null) {
            ketQua = ketQua.stream()
                .filter(ctsp -> ctsp.getSo_luong() != null && ctsp.getSo_luong() >= soLuongMin)
                .collect(Collectors.toList());
            System.out.println("Sau khi lọc số lượng min: " + ketQua.size());
        }

        if (soLuongMax != null) {
            ketQua = ketQua.stream()
                .filter(ctsp -> ctsp.getSo_luong() != null && ctsp.getSo_luong() <= soLuongMax)
                .collect(Collectors.toList());
            System.out.println("Sau khi lọc số lượng max: " + ketQua.size());
        }

        if (!isEmpty(trangThai)) {
            ketQua = ketQua.stream()
                .filter(ctsp -> ctsp.getTrang_thai() != null &&
                        trangThai.equalsIgnoreCase(ctsp.getTrang_thai()))
                .collect(Collectors.toList());
            System.out.println("Sau khi lọc trạng thái: " + ketQua.size());
        }

        if (!isEmpty(listMauSac)) {
            System.out.println("Lọc theo màu sắc: " + listMauSac);
            ketQua = ketQua.stream()
                .filter(ctsp -> {
                    if (ctsp.getTen_mau_sac() == null)
                        return false;
                    String mauSacValue = ctsp.getTen_mau_sac().toLowerCase().trim();
                    return listMauSac.stream()
                            .filter(Objects::nonNull)
                            .map(ms -> ms.toLowerCase().trim())
                            .anyMatch(mauSacValue::contains);
                })
                .collect(Collectors.toList());
            System.out.println("Sau khi lọc màu sắc: " + ketQua.size());
        }

        if (!isEmpty(listDanhMuc)) {
            System.out.println("Lọc theo danh mục: " + listDanhMuc);
            ketQua = ketQua.stream()
                .filter(ctsp -> {
                    if (ctsp.getTen_danh_muc() == null)
                        return false;
                    String danhMucValue = ctsp.getTen_danh_muc().toLowerCase().trim();
                    return listDanhMuc.stream()
                            .filter(Objects::nonNull)
                            .map(dm -> dm.toLowerCase().trim())
                            .anyMatch(dm -> danhMucValue.contains(dm) || dm.contains(danhMucValue));
                })
                .collect(Collectors.toList());
            System.out.println("Sau khi lọc danh mục: " + ketQua.size());
        }

        if (!isEmpty(listThuongHieu)) {
            ketQua = ketQua.stream()
                .filter(ctsp -> {
                    if (ctsp.getTen_thuong_hieu() == null)
                        return false;
                    String thuongHieuValue = ctsp.getTen_thuong_hieu().toLowerCase().trim();
                    return listThuongHieu.stream()
                            .filter(Objects::nonNull)
                            .map(th -> th.toLowerCase().trim())
                            .anyMatch(thuongHieuValue::contains);
                })
                .collect(Collectors.toList());
            System.out.println("Sau khi lọc thương hiệu: " + ketQua.size());
        }

        if (!isEmpty(listChatLieu)) {
            ketQua = ketQua.stream()
                .filter(ctsp -> {
                    if (ctsp.getTen_chat_lieu() == null)
                        return false;
                    String chatLieuValue = ctsp.getTen_chat_lieu().toLowerCase().trim();
                    return listChatLieu.stream()
                            .filter(Objects::nonNull)
                            .map(cl -> cl.toLowerCase().trim())
                            .anyMatch(chatLieuValue::contains);
                })
                .collect(Collectors.toList());
            System.out.println("Sau khi lọc chất liệu: " + ketQua.size());
        }

        if (!isEmpty(listKichThuoc)) {
            ketQua = ketQua.stream()
                .filter(ctsp -> {
                    if (ctsp.getGia_tri() == null)
                        return false;
                    String giaTriValue = ctsp.getGia_tri().toLowerCase().trim();
                    return listKichThuoc.stream()
                            .filter(Objects::nonNull)
                            .map(kt -> kt.toLowerCase().trim())
                            .anyMatch(giaTriValue::contains);
                })
                .collect(Collectors.toList());
            System.out.println("Sau khi lọc kích thước: " + ketQua.size());
        }

        System.out.println("Kết quả lọc cuối cùng: " + ketQua.size());

        if (ketQua.isEmpty()) {
            System.out.println("Không có kết quả lọc");
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<Integer> listIDCTSP = ketQua.stream()
                .map(ChiTietSanPhamView::getId_chi_tiet_san_pham)
                .collect(Collectors.toList());

        ArrayList<SanPhamView> listReturn = (ArrayList<SanPhamView>) sanPhamRepo.getSanPhamByListCTSP(listIDCTSP);
        
        return ResponseEntity.ok(listReturn.isEmpty() ? new ArrayList<>() : listReturn);
    }

    private List<SanPhamView> changeListCTSPToListSp(List<ChiTietSanPhamView> list) {
        return null;
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
