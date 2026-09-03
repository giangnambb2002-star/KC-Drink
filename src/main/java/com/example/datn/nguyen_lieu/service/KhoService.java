package com.example.datn.nguyen_lieu.service;
import com.example.datn.nguyen_lieu.entity.LoNguyenLieu;
import com.example.datn.nguyen_lieu.entity.NguyenLieu;
import com.example.datn.nguyen_lieu.repository.LoNguyenLieuRepository;
import com.example.datn.nguyen_lieu.repository.NguyenLieuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
@Service
@RequiredArgsConstructor
public class KhoService {
    private final LoNguyenLieuRepository loNguyenLieuRepository;
    private final NguyenLieuRepository nguyenLieuRepository;
    /**
     * THUẬT TOÁN FEFO (First-Expired, First-Out)
     * Trừ số lượng nguyên liệu từ các lô còn hạn, ưu tiên lô sắp hết hạn trước.
     */
    public void kiemTraDuTon(Integer idNguyenLieu, Double soLuongCanDung) {
        if (soLuongCanDung == null || soLuongCanDung <= 0) {
            throw new RuntimeException("Số lượng nguyên liệu cần dùng phải lớn hơn 0");
        }
        NguyenLieu nguyenLieu = nguyenLieuRepository.findById(idNguyenLieu)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));
        if (nguyenLieu.getTrangThai() == null || nguyenLieu.getTrangThai() != 1) {
            throw new RuntimeException(
                    "Nguyên liệu \"" + nguyenLieu.getTenNguyenLieu()
                            + "\" đang ngừng sử dụng."
            );
        }
        BigDecimal tongTon = loNguyenLieuRepository
                .getTongTonKhoConHan(idNguyenLieu);
        BigDecimal soLuongCan = BigDecimal.valueOf(soLuongCanDung);
        if (tongTon.compareTo(soLuongCan) < 0) {
            throw new RuntimeException(
                    "Không đủ nguyên liệu \"" + nguyenLieu.getTenNguyenLieu()
                            + "\". Cần " + soLuongCanDung + " " + nguyenLieu.getDonViTinh()
                            + ", tồn khả dụng " + tongTon + " " + nguyenLieu.getDonViTinh() + "."
            );
        }
    }
    @Transactional
    public void truKhoNguyenLieu(Integer idNguyenLieu, Double soLuongCanTru) {
        NguyenLieu nguyenLieu = nguyenLieuRepository.findById(idNguyenLieu)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu"));
        if (nguyenLieu.getTrangThai() == null || nguyenLieu.getTrangThai() != 1) {
            throw new RuntimeException(
                    "Nguyên liệu \"" + nguyenLieu.getTenNguyenLieu()
                            + "\" đang ngừng sử dụng."
            );
        }
        // 1. Kéo toàn bộ các lô còn hàng, còn hạn sử dụng...
        List<LoNguyenLieu> cacLoHopLe =
                loNguyenLieuRepository.findLoToUseFEFO(idNguyenLieu);
        Double soLuongConThieu = soLuongCanTru;
        // phần dưới giữ nguyên
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
            String ten = nguyenLieu.getTenNguyenLieu();
            String donVi = nguyenLieu.getDonViTinh();
            Double tongTon = soLuongCanTru - soLuongConThieu;
            throw new RuntimeException("Không đủ nguyên liệu \"" + ten + "\". Cần " + soLuongCanTru + " " + donVi + ", tồn khả dụng " + tongTon + " " + donVi + ".");
        }
        // 4. Lưu lại toàn bộ sự thay đổi xuống Database
        loNguyenLieuRepository.saveAll(cacLoHopLe);
    }
}