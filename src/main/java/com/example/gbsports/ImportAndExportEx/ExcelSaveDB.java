package com.example.gbsports.ImportAndExportEx;

import com.example.gbsports.repository.*;
import com.example.gbsports.request.ChiTietSanPhamRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final KichThuocRepo kichThuocRepo;
    @Transactional
    public void saveToDB(List<ChiTietSanPham> list) {
        for (ChiTietSanPham request : list) {
            SanPham sanPham = sanPhamRepo.findById(request.getSanPham().getId_san_pham())
                    .orElseGet(() -> sanPhamRepo.save(request.getSanPham()));

            DanhMuc danhMuc = danhMucRepo.findById(request.getSanPham().getDanhMuc().getId_danh_muc())
                    .orElseGet(() -> danhMucRepo.save(request.getSanPham().getDanhMuc()));

            ThuongHieu thuongHieu = thuongHieuRepo.findById(request.getSanPham().getThuongHieu().getId_thuong_hieu())
                    .orElseGet(() -> thuongHieuRepo.save(request.getSanPham().getThuongHieu()));

            ChatLieu chatLieu = chatLieuRepo.findById(request.getSanPham().getChatLieu().getId_chat_lieu())
                    .orElseGet(() -> chatLieuRepo.save(request.getSanPham().getChatLieu()));

            MauSac mauSac = mauSacRepo.findById(request.getMauSac().getId_mau_sac())
                    .orElseGet(() -> mauSacRepo.save(request.getMauSac()));

            KichThuoc kichThuoc = kichThuocRepo.findById(request.getKichThuoc().getId_kich_thuoc())
                    .orElseGet(() -> kichThuocRepo.save(request.getKichThuoc()));

            ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
            chiTietSanPham.setSanPham(sanPham);
            chiTietSanPham.setGia_ban(request.getGia_ban());
            chiTietSanPham.setGia_nhap(request.getGia_nhap());
            chiTietSanPham.setSo_luong(request.getSo_luong());
            chiTietSanPham.setTrang_thai(request.getTrang_thai());
            chiTietSanPham.setMauSac(mauSac);
            chiTietSanPham.setKichThuoc(kichThuoc);

            chiTietSanPhamRepo.save(chiTietSanPham);
        }
    }
}
