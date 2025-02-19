package com.example.gbsports.scheduler;

import com.example.gbsports.repository.KhuyenMaiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KhuyenMaiScheduler {

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Chạy lúc 00:00 mỗi ngày
    public void capNhatTrangThaiKhuyenMai() {
        khuyenMaiRepository.capNhatTrangThaiKhuyenMai();
        System.out.println("Đã cập nhật trạng thái khuyến mãi.");
    }
}
