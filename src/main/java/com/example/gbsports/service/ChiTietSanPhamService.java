package com.example.gbsports.service;

import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.entity.KichThuoc;
import com.example.gbsports.entity.MauSac;
import com.example.gbsports.entity.SanPham;
import com.example.gbsports.repository.*;
import com.example.gbsports.request.ChiTietSanPhamRequest;
import com.example.gbsports.request.SanPhamRequest;
import com.example.gbsports.response.HinhAnhView;
import com.example.gbsports.response.ChiTietSanPhamView;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<ChiTietSanPhamView> getAllCTSP() {
        return chiTietSanPhamRepo.listCTSP();
    }

    public List<ChiTietSanPham> getAllCTSPFindAll() {
        return chiTietSanPhamRepo.findAll();
    }

    public Page<ChiTietSanPhamView> getAllCTSPPhanTrang(Pageable pageable) {
        return chiTietSanPhamRepo.listPhanTrangChiTietSanPham(pageable);
    }

    public ResponseEntity<?> saveChiTietSanPham(@Valid @RequestBody ChiTietSanPhamRequest chiTietSanPhamRequest, BindingResult result) {
        int count = 0;
        Integer slCu = 0;
        Integer id = 0;
        Integer count2 = 0;
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream().map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        } else {
            for (ChiTietSanPham ctspCheckTrung : chiTietSanPhamRepo.findAll()) {
                if (ctspCheckTrung.getId_chi_tiet_san_pham() == chiTietSanPhamRequest.getId_chi_tiet_san_pham()) {
                    count++;
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
                chiTietSanPhamRepo.save(chiTietSanPham);
                return ResponseEntity.ok("Lưu thành công");
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
                    if (ctspSua.getSo_luong() == (slCu + chiTietSanPhamRequest.getSo_luong())) {
                        chiTietSanPhamRepo.save(ctspSua);
                        return ResponseEntity.ok("cập nhật số lượng");
                    } else {
                        return ResponseEntity.badRequest().body("KHông ổn");
                    }
                }else {
                    ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
//                    chiTietSanPham.setId_chi_tiet_san_pham(chiTietSanPhamRequest.getId_chi_tiet_san_pham());
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
                    chiTietSanPhamRepo.save(chiTietSanPham);
                    return ResponseEntity.ok("Lưu thành công");
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

    public String chuyenTrangThai(@PathVariable Integer id) {
        ChiTietSanPham ctspDelete = new ChiTietSanPham();
        for (ChiTietSanPham ctsp : chiTietSanPhamRepo.findAll()) {
            if (ctsp.getId_chi_tiet_san_pham() == id) {
                ctspDelete = ctsp;
            }
        }
        if (ctspDelete.getTrang_thai().equalsIgnoreCase("Hoạt động")) {
            ctspDelete.setTrang_thai("Không hoạt động");
            chiTietSanPhamRepo.save(ctspDelete);
        } else {
            ctspDelete.setTrang_thai("Hoạt động");
            chiTietSanPhamRepo.save(ctspDelete);
        }
        return "Chuyển trạng thái thành công";
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
        //Thiếu Kích thước
    }

    public Page<ChiTietSanPhamView> sapXep(Pageable pageable) {
        return chiTietSanPhamRepo.listPhanTrangChiTietSanPham(pageable);
    }

    public ArrayList<ChiTietSanPhamView> listCTSPTheoSanPham(@PathVariable Integer id) {
        return chiTietSanPhamRepo.listCTSPFolowSanPham(id);
    }


}
