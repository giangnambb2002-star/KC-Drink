package com.example.datn.topping.service;

import com.example.datn.topping.entity.LoTopping;
import com.example.datn.topping.entity.Topping;
import com.example.datn.topping.repository.LoToppingRepository;
import com.example.datn.topping.repository.ToppingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ToppingKhoService {

    private final LoToppingRepository loToppingRepository;
    private final ToppingRepository toppingRepository;

    public void kiemTraDuTon(Integer idTopping, Integer soLuongCanDung) {
        if (soLuongCanDung == null || soLuongCanDung <= 0) {
            throw new RuntimeException("Số lượng topping cần dùng phải lớn hơn 0");
        }

        Topping topping = toppingRepository.findById(idTopping)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy topping"));

        if (topping.getTrangThai() == null || topping.getTrangThai() != 1) {
            throw new RuntimeException(
                    "Topping \"" + topping.getTenTopping()
                            + "\" đang ngừng sử dụng."
            );
        }

        BigDecimal tongTon = loToppingRepository
                .getTongTonKhoConHan(idTopping);

        if (tongTon.compareTo(BigDecimal.valueOf(soLuongCanDung)) < 0) {
            throw new RuntimeException(
                    "Không đủ topping \"" + topping.getTenTopping()
                            + "\". Cần " + soLuongCanDung
                            + ", tồn khả dụng " + tongTon + "."
            );
        }
    }
    @Transactional
    public void truKhoTopping(Integer idTopping, Integer soLuongCanTru) {
        kiemTraDuTon(idTopping, soLuongCanTru);
        if (soLuongCanTru == null || soLuongCanTru <= 0) {
            throw new RuntimeException("Số lượng topping cần trừ phải lớn hơn 0");
        }

        List<LoTopping> cacLoHopLe = loToppingRepository.findLoToUseFEFO(idTopping);
        int soLuongConThieu = soLuongCanTru;

        for (LoTopping lo : cacLoHopLe) {
            if (soLuongConThieu <= 0) break;

            if (lo.getSoLuongTon() >= soLuongConThieu) {
                lo.setSoLuongTon(lo.getSoLuongTon() - soLuongConThieu);
                soLuongConThieu = 0;

                if (lo.getSoLuongTon() == 0) {
                    lo.setTrangThai(0);
                }
            } else {
                soLuongConThieu -= lo.getSoLuongTon();
                lo.setSoLuongTon(0);
                lo.setTrangThai(0);
            }
        }

        if (soLuongConThieu > 0) {
            com.example.datn.topping.entity.Topping tp = null;
            if (!cacLoHopLe.isEmpty()) {
                tp = cacLoHopLe.get(0).getTopping();
            } else {
                tp = toppingRepository.findById(idTopping).orElse(null);
            }
            String ten = tp != null ? tp.getTenTopping() : "ID " + idTopping;
            int tongTon = soLuongCanTru - soLuongConThieu;
            throw new RuntimeException("Không đủ topping \"" + ten + "\". Cần " + soLuongCanTru + ", tồn khả dụng " + tongTon + ".");
        }

        loToppingRepository.saveAll(cacLoHopLe);
    }

}