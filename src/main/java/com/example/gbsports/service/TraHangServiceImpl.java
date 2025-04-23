package com.example.gbsports.service;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.request.TraHangRequest;

import com.example.gbsports.response.TraHangResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class TraHangServiceImpl implements TraHangService {

    private static final Logger logger = LoggerFactory.getLogger(TraHangServiceImpl.class);

    @Autowired
    private TraHangRepository traHangRepository;

    @Autowired
    private HoaDonRepo hoaDonRepository;

    @Autowired
    private TheoDoiDonHangRepo theoDoiDonHangRepo;
    @Autowired
    private ChiTietSanPhamRepo chiTietSanPhamRepo;

    @Override
    @Transactional
    public TraHangResponse taoYeuCauTraHang(TraHangRequest request) {
        logger.info("Tạo yêu cầu trả hàng cho hóa đơn: {}", request.getMa_hoa_don());
        TraHangResponse response = new TraHangResponse();

        // Validate invoice
        HoaDon hoaDon = hoaDonRepository.TimMaHoaDon(request.getMa_hoa_don())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với mã: " + request.getMa_hoa_don()));



        // Validate product list
        if (request.getDanh_sach_san_pham() == null || request.getDanh_sach_san_pham().isEmpty()) {
            response.setThanh_cong(false);
            response.setThong_bao("Danh sách sản phẩm trả không hợp lệ!");
            return response;
        }

        for (TraHangRequest.ChiTietTraHangRequest sp : request.getDanh_sach_san_pham()) {
            if (sp.getId_chi_tiet_san_pham() == null) {
                response.setThanh_cong(false);
                response.setThong_bao("ID chi tiết sản phẩm không được để trống!");
                return response;
            }
            if (sp.getSo_luong() == null || sp.getSo_luong() <= 0) {
                response.setThanh_cong(false);
                response.setThong_bao("Số lượng trả phải lớn hơn 0!");
                return response;
            }

        }


        BigDecimal calculatedTotal = request.getDanh_sach_san_pham().stream()
                .map(TraHangRequest.ChiTietTraHangRequest::getSo_tien_hoan)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        // Create TraHang entity
        TheoDoiDonHang theoDoiDonHang = new TheoDoiDonHang();
        TraHang traHang = new TraHang();
        traHang.setId_hoa_don(hoaDon.getId_hoa_don());
        traHang.setLy_do(request.getLy_do());
        traHang.setGhi_chu(request.getGhi_chu());
        traHang.setNhan_vien_xu_ly(request.getNhan_vien());
        traHang.setNgay_tao(LocalDateTime.now());
        traHang.setTrang_thai("Trả hàng");
        traHang.setTong_tien_hoan(request.getTong_tien_hoan());
        theoDoiDonHang.setTrang_thai("Trả hàng");
        theoDoiDonHang.setNhan_vien_doi(request.getNhan_vien());
        theoDoiDonHang.setNoi_dung_doi(request.getLy_do());
        theoDoiDonHang.setNgay_chuyen(LocalDateTime.now());
        theoDoiDonHang.setHoaDon(hoaDon);
//        hoaDon.setTong_tien_sau_giam(BigDecimal.valueOf(100000));
//        hoaDonRepository.save(hoaDon);
        List<ChiTietTraHang> chiTietTraHangList = new ArrayList<>();
        for (TraHangRequest.ChiTietTraHangRequest sp : request.getDanh_sach_san_pham()) {
            ChiTietTraHang chiTietTraHang = new ChiTietTraHang();
            chiTietTraHang.setTra_hang(traHang);
            chiTietTraHang.setId_chi_tiet_san_pham(sp.getId_chi_tiet_san_pham());
            chiTietTraHang.setSo_luong(sp.getSo_luong());
            chiTietTraHang.setTien_hoan(sp.getSo_tien_hoan());
            chiTietTraHangList.add(chiTietTraHang);

            // Update ChiTietSanPham quantity if reason is not "Sản phẩm lỗi"
            if (!"Sản phẩm lỗi".equals(request.getLy_do())) {
                ChiTietSanPham chiTietSanPham = chiTietSanPhamRepo.findById(sp.getId_chi_tiet_san_pham())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm với ID: " + sp.getId_chi_tiet_san_pham()));
                chiTietSanPham.setSo_luong(chiTietSanPham.getSo_luong() + sp.getSo_luong());
                chiTietSanPhamRepo.save(chiTietSanPham);
                logger.info("Cộng lại {} sản phẩm cho chi tiết sản phẩm ID: {} (Lý do: {})",
                        sp.getSo_luong(), sp.getId_chi_tiet_san_pham(), request.getLy_do());
            } else {
                logger.info("Không cộng lại số lượng cho chi tiết sản phẩm ID: {} vì lý do trả hàng là 'Sản phẩm lỗi'",
                        sp.getId_chi_tiet_san_pham());
            }
        }
        traHang.setDanh_sach_chi_tiet_tra_hang(chiTietTraHangList);


        traHangRepository.save(traHang);
        theoDoiDonHangRepo.save(theoDoiDonHang);

        response.setThanh_cong(true);
        response.setThong_bao("Tạo yêu cầu trả hàng thành công!");
        response.setId_tra_hang(traHang.getId());
        return response;
    }

}