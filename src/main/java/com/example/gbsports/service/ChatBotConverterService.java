package com.example.gbsports.service;

import com.example.gbsports.response.CTSPAI;
import com.example.gbsports.response.ChiTietSanPhamView;
import com.example.gbsports.entity.ChiTietSanPham;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatBotConverterService {

    /**
     * Chuyển đổi Date sang LocalDateTime
     */
    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) return null;
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Chuyển đổi từ ChiTietSanPhamView sang CTSPAI
     */
    public CTSPAI convertToCTSPAI(ChiTietSanPhamView view) {
        if (view == null) return null;
        
        CTSPAI ctspai = new CTSPAI();
        
        // ID và khóa ngoại
        ctspai.setId_chi_tiet_san_pham(view.getId_chi_tiet_san_pham());
        ctspai.setId_san_pham(view.getId_san_pham());

        // Thông tin cơ bản sản phẩm
        ctspai.setMa_san_pham(view.getMa_san_pham());
        ctspai.setTen_san_pham(view.getTen_san_pham());
        ctspai.setMo_ta(view.getMo_ta());
        ctspai.setHinh_anh(view.getHinh_anh());

        // Thông tin danh mục, thương hiệu, chất liệu
        ctspai.setTen_danh_muc(view.getTen_danh_muc());
        ctspai.setTen_thuong_hieu(view.getTen_thuong_hieu());
        ctspai.setTen_chat_lieu(view.getTen_chat_lieu());

        // Thông tin giá và số lượng
        ctspai.setGia_ban(view.getGia_ban() != null ? view.getGia_ban().doubleValue() : null);
        ctspai.setSo_luong(view.getSo_luong());

        // Thông tin thuộc tính
        ctspai.setTen_mau(view.getTen_mau_sac());
        ctspai.setGia_tri(view.getGia_tri());  // kích thước

        // Thông tin trạng thái và thời gian
        ctspai.setTrang_thai_ctsp(view.getTrang_thai());
        // Chuyển đổi Date sang LocalDateTime
        ctspai.setNgay_tao(view.getNgay_tao());
        ctspai.setNgay_sua(view.getNgay_sua());

        // Thông tin QR code
        ctspai.setQr_code(view.getQr_code());

        return ctspai;
    }

    /**
     * Chuyển đổi danh sách ChiTietSanPhamView sang danh sách CTSPAI
     */
    public List<CTSPAI> convertToCTSPAIList(List<ChiTietSanPhamView> views) {
        if (views == null) return new ArrayList<>();
        
        List<CTSPAI> result = new ArrayList<>();
        for (ChiTietSanPhamView view : views) {
            CTSPAI ctspai = convertToCTSPAI(view);
            if (ctspai != null) {
                result.add(ctspai);
            }
        }
        return result;
    }

    /**
     * Chuyển đổi từ ChiTietSanPham sang CTSPAI
     */
    public CTSPAI convertFromEntity(ChiTietSanPham entity) {
        if (entity == null) return null;
        
        CTSPAI ctspai = new CTSPAI();
        
        // ID và khóa ngoại
        ctspai.setId_chi_tiet_san_pham(entity.getId_chi_tiet_san_pham());
        ctspai.setId_san_pham(entity.getSanPham().getId_san_pham());

        // Thông tin cơ bản sản phẩm
        ctspai.setMa_san_pham(entity.getSanPham().getMa_san_pham());
        ctspai.setTen_san_pham(entity.getSanPham().getTen_san_pham());
        ctspai.setMo_ta(entity.getSanPham().getMo_ta());
        ctspai.setHinh_anh(entity.getSanPham().getHinh_anh());

        // Thông tin danh mục, thương hiệu, chất liệu
        ctspai.setTen_danh_muc(entity.getSanPham().getDanhMuc().getTen_danh_muc());
        ctspai.setTen_thuong_hieu(entity.getSanPham().getThuongHieu().getTen_thuong_hieu());
        ctspai.setTen_chat_lieu(entity.getSanPham().getChatLieu().getTen_chat_lieu());

        // Thông tin giá và số lượng
        ctspai.setGia_ban(entity.getGia_ban().doubleValue());
        ctspai.setSo_luong(entity.getSo_luong());

        // Thông tin thuộc tính
        ctspai.setTen_mau(entity.getMauSac().getTen_mau_sac());
        ctspai.setGia_tri(entity.getKichThuoc().getGia_tri());

        // Thông tin trạng thái và thời gian
        ctspai.setTrang_thai_ctsp(entity.getTrang_thai());
        ctspai.setNgay_tao(entity.getNgay_tao().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime());
        ctspai.setNgay_sua(entity.getNgay_sua() != null ? 
            entity.getNgay_sua().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime() : null);

        // Thông tin QR code
        ctspai.setQr_code(entity.getQr_code());

        return ctspai;
    }

    /**
     * Chuyển đổi danh sách ChiTietSanPham sang danh sách CTSPAI
     */
    public List<CTSPAI> convertFromEntities(List<ChiTietSanPham> entities) {
        if (entities == null) return new ArrayList<>();
        
        List<CTSPAI> result = new ArrayList<>();
        for (ChiTietSanPham entity : entities) {
            CTSPAI ctspai = convertFromEntity(entity);
            if (ctspai != null) {
                result.add(ctspai);
            }
        }
        return result;
    }
}