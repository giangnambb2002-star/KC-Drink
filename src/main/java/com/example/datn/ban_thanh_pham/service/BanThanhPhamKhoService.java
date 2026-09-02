package com.example.datn.ban_thanh_pham.service;

import com.example.datn.ban_thanh_pham.entity.BanThanhPham;
import com.example.datn.ban_thanh_pham.entity.MePhaChe;
import com.example.datn.ban_thanh_pham.repository.BanThanhPhamRepository;
import com.example.datn.ban_thanh_pham.repository.MePhaCheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BanThanhPhamKhoService {

    private final MePhaCheRepository mePhaCheRepository;
    private final BanThanhPhamRepository banThanhPhamRepository;

    public BigDecimal getTongTonKhaDung(Integer idBanThanhPham) {
        banThanhPhamRepository.findById(idBanThanhPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bán thành phẩm"));

        LocalDateTime now = LocalDateTime.now();

        return mePhaCheRepository
                .findByBanThanhPham_IdBanThanhPhamAndTrangThai(idBanThanhPham, 1)
                .stream()
                .filter(me -> me.getSoLuongConLai() != null)
                .filter(me -> me.getSoLuongConLai().compareTo(BigDecimal.ZERO) > 0)
                .filter(me -> me.getHanSuDung() == null || me.getHanSuDung().isAfter(now))
                .map(MePhaChe::getSoLuongConLai)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void kiemTraDuTon(Integer idBanThanhPham, BigDecimal soLuongCanDung) {
        if (soLuongCanDung == null || soLuongCanDung.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số lượng bán thành phẩm cần dùng phải lớn hơn 0");
        }
        BanThanhPham banThanhPham = banThanhPhamRepository.findById(idBanThanhPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bán thành phẩm"));
        if (banThanhPham.getTrangThai() == null || banThanhPham.getTrangThai() != 1) {
            throw new RuntimeException(
                    "Bán thành phẩm \"" + banThanhPham.getTenBanThanhPham()
                            + "\" đang ngừng sử dụng."
            );
        }
        BigDecimal tongTon = getTongTonKhaDung(idBanThanhPham);
        if (tongTon.compareTo(soLuongCanDung) < 0) {
            throw new RuntimeException(
                    "Không đủ bán thành phẩm \"" + banThanhPham.getTenBanThanhPham() + "\"."
                            + " Cần " + soLuongCanDung + " " + banThanhPham.getDonViTinh()
                            + ", tồn khả dụng " + tongTon + " " + banThanhPham.getDonViTinh() + "."
            );
        }
    }
    @Transactional
    public void truBanThanhPham(Integer idBanThanhPham, BigDecimal soLuongCanTru) {
        kiemTraDuTon(idBanThanhPham, soLuongCanTru);

        LocalDateTime now = LocalDateTime.now();

        List<MePhaChe> danhSachMe = mePhaCheRepository
                .findByBanThanhPham_IdBanThanhPhamAndTrangThai(idBanThanhPham, 1)
                .stream()
                .filter(me -> me.getSoLuongConLai() != null)
                .filter(me -> me.getSoLuongConLai().compareTo(BigDecimal.ZERO) > 0)
                .filter(me -> me.getHanSuDung() == null || me.getHanSuDung().isAfter(now))
                .sorted(
                        Comparator.comparing(
                                MePhaChe::getHanSuDung,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        )
                )
                .toList();

        BigDecimal conPhaiTru = soLuongCanTru;

        for (MePhaChe me : danhSachMe) {
            if (conPhaiTru.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal tonMe = me.getSoLuongConLai();

            if (tonMe.compareTo(conPhaiTru) >= 0) {
                me.setSoLuongConLai(tonMe.subtract(conPhaiTru));
                conPhaiTru = BigDecimal.ZERO;
            } else {
                conPhaiTru = conPhaiTru.subtract(tonMe);
                me.setSoLuongConLai(BigDecimal.ZERO);
            }

            if (me.getSoLuongConLai().compareTo(BigDecimal.ZERO) == 0) {
                me.setTrangThai(0);
            }

            mePhaCheRepository.save(me);
        }
    }
}