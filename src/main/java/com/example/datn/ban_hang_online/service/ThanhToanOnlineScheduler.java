package com.example.datn.ban_hang_online.service;

import com.example.datn.hoa_don.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThanhToanOnlineScheduler {

    private final HoaDonRepository hoaDonRepository;

    private final ThanhToanOnlineService thanhToanOnlineService;
//    15s ktra 1 lần
    @Scheduled(fixedDelay = 15000)
    public void xuLyDonOnlineQuaHan() {

        List<Integer> idList =
                hoaDonRepository
                        .findIdDonOnlineQuaHan(
                                LocalDateTime.now()
                        );

        for (Integer idHoaDon : idList) {

            try {
                thanhToanOnlineService
                        .xuLyDonQuaHan(
                                idHoaDon
                        );

            } catch (Exception e) {

                log.warn(
                        "Không thể xử lý đơn online quá hạn id={}: {}",
                        idHoaDon,
                        e.getMessage()
                );
            }
        }
    }
}