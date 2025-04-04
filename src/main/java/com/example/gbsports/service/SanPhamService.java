package com.example.gbsports.service;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.request.SanPhamRequest;
import com.example.gbsports.response.ChiTietSanPhamView;
import com.example.gbsports.response.SanPhamView;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SanPhamService {
    @Autowired
    SanPhamRepo sanPhamRepo;
    @Autowired
    ChiTietSanPhamRepo chiTietSanPhamRepo;
    @Autowired
    DanhMucRepo danhMucRepo;
    @Autowired
    ThuongHieuRepo thuongHieuRepo;
    @Autowired
    ChatLieuRepo chatLieuRepo;

    public ArrayList<SanPhamView> getAll() {
        return sanPhamRepo.getAllSanPham();
    }

    public List<SanPham> getAllFindAll() {
        return sanPhamRepo.findAll();
    }

    public Page<SanPhamView> getAllPhanTrang(Pageable pageable) {
        return sanPhamRepo.getAllSanPhamPhanTrang(pageable);
    }
    public SanPham detailSP(@RequestParam("id") Integer id){
        return sanPhamRepo.findById(id).get();
    }
    public ArrayList<SanPhamView> getAllSPNgaySua(){return sanPhamRepo.getAllSanPhamSapXepTheoNgaySua();}
    public ResponseEntity<?> saveSanPham2(@Valid @RequestBody SanPhamRequest sanPhamRequest, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("success", false);
            response.put("message", "Validation failed");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            SanPham sanPham = new SanPham();
            Optional<DanhMuc> danhMucOp = danhMucRepo.findById(sanPhamRequest.getId_danh_muc());
            Optional<ThuongHieu> thuongHieuOp = thuongHieuRepo.findById(sanPhamRequest.getId_thuong_hieu());
            Optional<ChatLieu> chatLieuOp = chatLieuRepo.findById(sanPhamRequest.getId_chat_lieu());

            ChatLieu chatLieu = chatLieuOp.orElse(new ChatLieu());
            ThuongHieu thuongHieu = thuongHieuOp.orElse(new ThuongHieu());
            DanhMuc danhMuc = danhMucOp.orElse(new DanhMuc());

            BeanUtils.copyProperties(sanPhamRequest, sanPham);
            sanPham.setChatLieu(chatLieu);
            sanPham.setDanhMuc(danhMuc);
            sanPham.setThuongHieu(thuongHieu);

            SanPham savedSanPham = sanPhamRepo.save(sanPham);

            response.put("success", true);
            response.put("message", "Lưu thành công");
            response.put("data", savedSanPham);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lưu sản phẩm");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<?> saveSanPham(@RequestBody SanPhamRequest sanPhamRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            SanPham sanPham = new SanPham();
            Optional<DanhMuc> danhMucOp = danhMucRepo.findById(sanPhamRequest.getId_danh_muc());
            Optional<ThuongHieu> thuongHieuOp = thuongHieuRepo.findById(sanPhamRequest.getId_thuong_hieu());
            Optional<ChatLieu> chatLieuOp = chatLieuRepo.findById(sanPhamRequest.getId_chat_lieu());

            ChatLieu chatLieu = chatLieuOp.orElse(new ChatLieu());
            ThuongHieu thuongHieu = thuongHieuOp.orElse(new ThuongHieu());
            DanhMuc danhMuc = danhMucOp.orElse(new DanhMuc());

            BeanUtils.copyProperties(sanPhamRequest, sanPham);
            sanPham.setChatLieu(chatLieu);
            sanPham.setDanhMuc(danhMuc);
            sanPham.setThuongHieu(thuongHieu);

            SanPham savedSanPham = sanPhamRepo.save(sanPham);

            response.put("success", true);
            response.put("message", "Lưu thành công");
            response.put("data", savedSanPham);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lưu sản phẩm");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public String deleteSanPham(@PathVariable Integer id) {
        ArrayList<ChiTietSanPham> list = new ArrayList<>();
        SanPham spDelete = new SanPham();
        for (SanPham sp : sanPhamRepo.findAll()) {
            if (sp.getId_san_pham() == id) {
                spDelete = sp;
                spDelete.setTrang_thai("Không hoạt động");
            }
        }
        for (ChiTietSanPham ctsp : chiTietSanPhamRepo.findAll()) {
            if (ctsp.getSanPham().getId_san_pham() == id) {
                list.add(ctsp);
            }
        }
        if (list.isEmpty()) {
            return "Không có chi tiết sản phẩm cho sản phẩm này";
        } else {
            for (ChiTietSanPham ctspXoa : list) {
                ctspXoa.setTrang_thai("Không hoạt động");
                chiTietSanPhamRepo.save(ctspXoa);
            }
            sanPhamRepo.save(spDelete);
            return "Xóa thành công";
        }

    }

    public String chuyenTrangThai(@PathVariable Integer id) {
        ArrayList<ChiTietSanPham> list = new ArrayList<>();
        SanPham spDelete = new SanPham();
        for (SanPham sp : sanPhamRepo.findAll()) {
            if (sp.getId_san_pham() == id) {
                spDelete = sp;
            }
        }
        for (ChiTietSanPham ctsp : chiTietSanPhamRepo.findAll()) {
            if (ctsp.getSanPham().getId_san_pham() == id) {
                list.add(ctsp);
            }
        }
        if (list.isEmpty()) {
            return "Không có chi tiết sản phẩm cho sản phẩm này";
        } else {
            if (spDelete.getTrang_thai().equalsIgnoreCase("Hoạt động")) {
                for (ChiTietSanPham ctspXoa : list) {
                    ctspXoa.setTrang_thai("Hết hàng");
                    chiTietSanPhamRepo.save(ctspXoa);
                }
                spDelete.setTrang_thai("Không hoạt động");
                sanPhamRepo.save(spDelete);
            } else {
                for (ChiTietSanPham ctspXoa : list) {
                    ctspXoa.setTrang_thai("Còn hàng");
                    chiTietSanPhamRepo.save(ctspXoa);
                }
                spDelete.setTrang_thai("Hoạt động");
                sanPhamRepo.save(spDelete);
            }
        }
        return "Chuyển trạng thái thành công";
    }

    public ArrayList<SanPham> listTimKiem(String search) {
        ArrayList<SanPham> listTam = new ArrayList<>();
        for (SanPham sp : sanPhamRepo.findAll()) {
            if (sp.getMa_san_pham().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)) ||
                    sp.getTen_san_pham().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)) ||
                    sp.getChatLieu().getTen_chat_lieu().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)) ||
                    sp.getDanhMuc().getTen_danh_muc().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)) ||
                    sp.getThuongHieu().getTen_thuong_hieu().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) {
                listTam.add(sp);
            }
            Integer tongsoluong = tongSoLuongSanPham(sp.getId_san_pham());
            sp.setTong_so_luong(tongsoluong);
        }
        return listTam;
    }
    public Integer tongSoLuongSanPham(Integer idSanPham){
        Integer soLuong = 0;
        for (ChiTietSanPham ctsp: chiTietSanPhamRepo.findAll()) {
            if (ctsp.getSanPham().getId_san_pham()==idSanPham){
                soLuong+=ctsp.getSo_luong();
            }
        }
        return soLuong;
    }

    public List<SanPhamView> locSanPham(String tenDanhMuc, String tenThuongHieu, String tenChatLieu) {
        return sanPhamRepo.locSanPham(tenDanhMuc, tenThuongHieu, tenChatLieu);
    }

    public Page<SanPhamView> sapXep(Pageable pageable) {
        return sanPhamRepo.getAllSanPhamPhanTrang(pageable);
    }
    public SanPham getSanPhamOrCreateSanPham(String tenSanPham, ThuongHieu thuongHieu, DanhMuc danhMuc, ChatLieu chatLieu){
        Optional<SanPham> exitingSanPham = sanPhamRepo.findAll().stream()
                .filter(sanPham -> tenSanPham.equalsIgnoreCase(Optional.ofNullable(sanPham.getTen_san_pham()).orElse("")))
                .findFirst();

        if (exitingSanPham.isPresent()) {
            return exitingSanPham.get();
        }else {
            int maxNumber = sanPhamRepo.findAll().stream()
                    .map(SanPham::getMa_san_pham)
                    .filter(ma -> ma.startsWith("SP0"))
                    .map(ma -> ma.substring(3))
                    .filter(num -> num.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0);

            // Tạo đối tượng mới
            SanPham newSanPham = new SanPham();
            newSanPham.setMa_san_pham("SP0" + (maxNumber + 1));
            newSanPham.setTen_san_pham(tenSanPham);
            newSanPham.setTrang_thai("Hoạt động");
//            newSanPham.setGioi_tinh(gioiTinh);
            newSanPham.setThuongHieu(thuongHieu);
            newSanPham.setDanhMuc(danhMuc);
            newSanPham.setChatLieu(chatLieu);
            //Còn có thể thêm hình ảnh và mô tả
            sanPhamRepo.save(newSanPham);
            return newSanPham;
        }

        // Nếu không tìm thấy, tạo mã mới

    }

    public List<SanPhamView> getSanPhamTheoTen(@RequestParam("tenSanPham") String tenSanPham){
        return sanPhamRepo.listSanPhamBanHangWebTheoSP(tenSanPham);
    }

    public List<ChiTietSanPhamView> getAllCTSPKM() {
        return sanPhamRepo.getAllCTSPKM();
    }

}
