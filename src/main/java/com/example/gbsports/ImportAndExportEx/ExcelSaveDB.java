package com.example.gbsports.ImportAndExportEx;

import com.example.gbsports.repository.*;
import com.example.gbsports.request.ChiTietSanPhamRequest;
import com.example.gbsports.service.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.gbsports.entity.*;

@Data
@Service
public class ExcelSaveDB {
    private final SanPhamRepo sanPhamRepo;
    private final ChiTietSanPhamRepo chiTietSanPhamRepo;
    private final DanhMucRepo danhMucRepo;
    private final ThuongHieuRepo thuongHieuRepo;
    private final ChatLieuRepo chatLieuRepo;
    private final MauSacRepo mauSacRepo;
    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;
    private final ChatLieuService chatLieuService;
    private final ThuongHieuService thuongHieuService;
    private final KichThuocService kichThuocService;
    private final MauSacService mauSacService;

    //    ArrayList<ChiTietSanPham> listSoSanh = new ArrayList<>();
    @Transactional
    public void saveToDB(List<ChiTietSanPham> list) {
        Map<String, ChiTietSanPham> mapChiTietSanPham = new HashMap<>();

        // Đọc tất cả các bản ghi hiện có từ cơ sở dữ liệu
        for (ChiTietSanPham ctsp : chiTietSanPhamRepo.findAll()) {
            String key = ctsp.getSanPham().getId_san_pham() + "-"
                    + ctsp.getMauSac().getId_mau_sac() + "-"
                    + ctsp.getKichThuoc().getId_kich_thuoc(); // Key không bao gồm giới tính
            mapChiTietSanPham.put(key, ctsp);
        }

        // Xử lý từng ChiTietSanPham trong danh sách nhập từ file Excel
        for (ChiTietSanPham ctspss : list) {
            // ✅ Đảm bảo các đối tượng liên quan tồn tại trong DB
            DanhMuc danhMuc = danhMucService.getDanhMucOrCreateDanhMuc(ctspss.getSanPham().getDanhMuc().getTen_danh_muc());
            ThuongHieu thuongHieu = thuongHieuService.getThuongHieuOrCreateThuongHieu(ctspss.getSanPham().getThuongHieu().getTen_thuong_hieu());
            ChatLieu chatLieu = chatLieuService.getChatLieuOrCreateChatLieu(ctspss.getSanPham().getChatLieu().getTen_chat_lieu());
            SanPham sanPham = sanPhamService.getSanPhamOrCreateSanPham(
                    ctspss.getSanPham().getTen_san_pham(),
                    thuongHieu,
                    danhMuc,
                    chatLieu
            );
            MauSac mauSac = mauSacService.getMauSacOrCreateMauSac(ctspss.getMauSac().getTen_mau_sac());
            KichThuoc kichThuoc = kichThuocService.getKichThuocOrCreateKichThuoc(
                    ctspss.getKichThuoc().getGia_tri(),
                    ctspss.getKichThuoc().getDon_vi()
            );

            // ✅ Cập nhật đối tượng ChiTietSanPham với các đối tượng liên quan
            ctspss.setSanPham(sanPham);
            ctspss.setMauSac(mauSac);
            ctspss.setKichThuoc(kichThuoc);

            // ✅ Tạo key để kiểm tra trùng lặp
            String key = ctspss.getSanPham().getId_san_pham() + "-"
                    + ctspss.getMauSac().getId_mau_sac() + "-"
                    + ctspss.getKichThuoc().getId_kich_thuoc(); // Key không bao gồm giới tính

            if (mapChiTietSanPham.containsKey(key)) {
                // Nếu đã tồn tại, cộng dồn số lượng
                ChiTietSanPham existingCtsp = mapChiTietSanPham.get(key);
                existingCtsp.setSo_luong(existingCtsp.getSo_luong() + ctspss.getSo_luong()); // Cộng dồn số lượng
                chiTietSanPhamRepo.save(existingCtsp);
            } else {
                // Nếu chưa tồn tại, thêm mới vào cơ sở dữ liệu
                chiTietSanPhamRepo.save(ctspss);
            }
        }
    }
}
