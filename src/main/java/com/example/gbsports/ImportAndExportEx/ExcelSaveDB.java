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
    public Boolean saveToDB(List<ChiTietSanPham> list) {
        for (ChiTietSanPham request : list) {


            DanhMuc danhMuc = request.getSanPham().getDanhMuc().getId_danh_muc() != null ?
                    danhMucRepo.findById(request.getSanPham().getDanhMuc().getId_danh_muc())
                            .orElseGet(() -> danhMucRepo.save(request.getSanPham().getDanhMuc())) :
                    danhMucRepo.save(request.getSanPham().getDanhMuc());


            ThuongHieu thuongHieu = request.getSanPham().getThuongHieu().getId_thuong_hieu() != null ?
                    thuongHieuRepo.findById(request.getSanPham().getThuongHieu().getId_thuong_hieu())
                            .orElseGet(() -> thuongHieuRepo.save(request.getSanPham().getThuongHieu())) :
                    thuongHieuRepo.save(request.getSanPham().getThuongHieu());


            ChatLieu chatLieu = request.getSanPham().getChatLieu().getId_chat_lieu() != null ?
                    chatLieuRepo.findById(request.getSanPham().getChatLieu().getId_chat_lieu())
                            .orElseGet(() -> chatLieuRepo.save(request.getSanPham().getChatLieu())) :
                    chatLieuRepo.save(request.getSanPham().getChatLieu());


            SanPham sanPham = request.getSanPham().getId_san_pham() != null ?
                    sanPhamRepo.findById(request.getSanPham().getId_san_pham())
                            .orElseGet(() -> sanPhamRepo.save(request.getSanPham())) :
                    sanPhamRepo.save(request.getSanPham());


            MauSac mauSac = request.getMauSac().getId_mau_sac() != null ?
                    mauSacRepo.findById(request.getMauSac().getId_mau_sac())
                            .orElseGet(() -> mauSacRepo.save(request.getMauSac())) :
                    mauSacRepo.save(request.getMauSac());


            KichThuoc kichThuoc = request.getKichThuoc().getId_kich_thuoc() != null ?
                    kichThuocRepo.findById(request.getKichThuoc().getId_kich_thuoc())
                            .orElseGet(() -> kichThuocRepo.save(request.getKichThuoc())) :
                    kichThuocRepo.save(request.getKichThuoc());


            ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
            chiTietSanPham.setSanPham(sanPham);
            chiTietSanPham.setGia_ban(request.getGia_ban());
            chiTietSanPham.setGia_nhap(request.getGia_nhap());
            chiTietSanPham.setSo_luong(request.getSo_luong());
            chiTietSanPham.setTrang_thai(request.getTrang_thai());
            chiTietSanPham.setMauSac(mauSac);
            chiTietSanPham.setKichThuoc(kichThuoc);
            chiTietSanPham.setNgay_tao(request.getNgay_tao());
            chiTietSanPham.setNgay_sua(request.getNgay_sua());
            chiTietSanPham.setQr_code(request.getQr_code());
            chiTietSanPhamRepo.save(chiTietSanPham);
        }
        return true;
    }
}
