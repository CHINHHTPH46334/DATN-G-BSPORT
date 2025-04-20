package com.example.gbsports.service;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.entity.HoaDonChiTiet;
import com.example.gbsports.entity.Voucher;
import com.example.gbsports.repository.ChiTietSanPhamRepo;
import com.example.gbsports.repository.HoaDonRepo;
import com.example.gbsports.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class HoaDonScheduler {

    @Autowired
    private HoaDonRepo hoaDonRepo;

    @Autowired
    private ChiTietSanPhamRepo chiTietSanPhamRepo;

    @Autowired
    private VoucherRepository voucherRepo;

    @Transactional
    public int xoaHoaDonChoQuaNgay() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        // Chỉ lấy các hóa đơn "Đang chờ" và đã quá ngày
        List<HoaDon> hoaDons = hoaDonRepo.findExpiredChoHoaDons(startOfToday);

        for (HoaDon hd : hoaDons) {
            // ✅ Cập nhật lại số lượng tồn sản phẩm
            for (HoaDonChiTiet chiTiet : hd.getDanhSachChiTiet()) {
                Integer idChiTietSanPham = chiTiet.getChiTietSanPham().getId_chi_tiet_san_pham();
                Integer soLuong = chiTiet.getSo_luong();
                chiTietSanPhamRepo.updateSLCTSPByIdCTSP(idChiTietSanPham, soLuong);
            }

            // ✅ Cập nhật lại số lượng voucher nếu có và đang diễn ra
            Voucher voucher = hd.getVoucher();
            if (voucher != null && "Đang diễn ra".equalsIgnoreCase(voucher.getTrangThai())) {
                voucher.setSoLuong(voucher.getSoLuong() + 1);
                voucherRepo.save(voucher);
            }
        }

        // ✅ Xoá toàn bộ hóa đơn sau khi đã xử lý
        if (!hoaDons.isEmpty()) {
            hoaDonRepo.deleteAll(hoaDons);
        }

        return hoaDons.size();
    }



    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void tuDongXoaHoaDonChoQuaNgay() {
        int soLuong = xoaHoaDonChoQuaNgay();
        System.out.println("[Scheduler] ✅ Đã tự động xoá " + soLuong + " hoá đơn 'Đang chờ' quá ngày.");
        System.out.println("Đang chạy");
    }

}
