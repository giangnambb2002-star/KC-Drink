package com.example.datn.nguyen_lieu.service;

import com.example.datn.nguyen_lieu.entity.LoNguyenLieu;
import com.example.datn.nguyen_lieu.repository.LoNguyenLieuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KhoService {

    private final LoNguyenLieuRepository loNguyenLieuRepository;

    /**
     * THUẬT TOÁN FEFO (First-Expired, First-Out)
     * Trừ số lượng nguyên liệu từ các lô còn hạn, ưu tiên lô sắp hết hạn trước.
     */
    @Transactional
    public void truKhoNguyenLieu(Integer idNguyenLieu, Double soLuongCanTru) {

        // 1. Kéo toàn bộ các lô còn hàng, còn hạn sử dụng của nguyên liệu này lên, xếp theo Date gần nhất
        List<LoNguyenLieu> cacLoHopLe = loNguyenLieuRepository.findLoToUseFEFO(idNguyenLieu);

        Double soLuongConThieu = soLuongCanTru;

        // 2. Chạy vòng lặp trừ lùi từng lô
        for (LoNguyenLieu lo : cacLoHopLe) {
            if (soLuongConThieu <= 0) break; // Đã trừ đủ số lượng thì dừng luôn

            if (lo.getSoLuongTon() >= soLuongConThieu) {
                // Nếu lô này đủ hàng để trừ
                lo.setSoLuongTon(lo.getSoLuongTon() - soLuongConThieu);
                soLuongConThieu = 0.0;
            } else {
                // Nếu lô này không đủ hàng, lấy hết sạch lô này và trừ tiếp ở lô sau
                soLuongConThieu -= lo.getSoLuongTon();
                lo.setSoLuongTon(0.0);
                lo.setTrangThai(0); // Cho lô này về trạng thái hết hàng (hoặc khóa)
            }
        }

        // 3. Chốt hạ: Nếu vét cạn các lô rồi mà vẫn thiếu hàng -> Báo lỗi cho Frontend
        if (soLuongConThieu > 0) {
            throw new RuntimeException("Kho không đủ nguyên liệu (ID: " + idNguyenLieu + "). Thiếu: " + soLuongConThieu);
        }

        // 4. Lưu lại toàn bộ sự thay đổi xuống Database
        loNguyenLieuRepository.saveAll(cacLoHopLe);
    }
}