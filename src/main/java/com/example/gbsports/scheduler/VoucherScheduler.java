package com.example.gbsports.scheduler;

import com.example.gbsports.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VoucherScheduler {

    @Autowired
    private VoucherRepository voucherRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Chạy vào 0h mỗi ngày
    public void updateVoucherStatus() {
        voucherRepository.capNhatTrangThaiVoucher();
    }
}

