package com.example.datn.ban_hang_online.service;

import com.example.datn.hoa_don.entity.HoaDon;
import com.example.datn.hoa_don.repository.HoaDonRepository;
import com.example.datn.van_chuyen.entity.VanDonGhn;
import com.example.datn.van_chuyen.repository.VanDonGhnRepository;
import com.example.datn.voucher.entity.Voucher;
import com.example.datn.voucher.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CodOnlineService {

    private final HoaDonRepository hoaDonRepository;
    private final VanDonGhnRepository vanDonGhnRepository;
    private final VoucherRepository voucherRepository;
    private final DatChoKhoService datChoKhoService;

    /*
     * ==========================================
     * NHÂN VIÊN TIẾP NHẬN COD
     *
     * Đây là thời điểm quán bắt đầu làm đồ uống:
     * - validate voucher
     * - trừ kho thật
     * - dùng voucher
     * - reservation -> CONSUMED
     *
     * HOA_DON vẫn CHO_THANH_TOAN.
     * ==========================================
     */
    @Transactional
    public void xuatKhoKhiTiepNhan(
            Integer idHoaDon
    ) {

        HoaDon hoaDon =
                hoaDonRepository
                        .findByIdForUpdate(idHoaDon)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy hóa đơn"
                                )
                        );

        if (!"ONLINE".equalsIgnoreCase(
                hoaDon.getLoaiHoaDon()
        )) {
            throw new RuntimeException(
                    "Hóa đơn không phải đơn online"
            );
        }

        if (!"TIEN_MAT".equalsIgnoreCase(
                hoaDon.getHinhThucThanhToan()
        )) {
            return;
        }

        if (!"CHO_THANH_TOAN".equals(
                hoaDon.getTrangThai()
        )) {
            throw new RuntimeException(
                    "Đơn COD không còn ở trạng thái chờ thanh toán"
            );
        }

        /*
         * Validate + lock voucher trước khi
         * bắt đầu xuất kho.
         */
        Voucher voucher = hoaDon.getVoucher();

        if (voucher != null) {

            voucher =
                    voucherRepository
                            .findByIdForUpdate(
                                    voucher.getIdVoucher()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Voucher không còn tồn tại"
                                    )
                            );

            LocalDateTime now =
                    LocalDateTime.now();

            if (voucher.getTrangThai() == null
                    || voucher.getTrangThai() != 1) {
                throw new RuntimeException(
                        "Voucher không còn khả dụng"
                );
            }

            if (voucher.getSoLuong() != null
                    && voucher.getSoLuong() <= 0) {
                throw new RuntimeException(
                        "Voucher đã hết lượt sử dụng"
                );
            }

            if (voucher.getNgayBatDau() != null
                    && now.isBefore(
                    voucher.getNgayBatDau()
            )) {
                throw new RuntimeException(
                        "Voucher chưa đến thời gian sử dụng"
                );
            }

            if (voucher.getNgayKetThuc() != null
                    && now.isAfter(
                    voucher.getNgayKetThuc()
            )) {
                throw new RuntimeException(
                        "Voucher đã hết hạn"
                );
            }

            if (voucher.getIdKhachHang() != null
                    && (
                    hoaDon.getKhachHang() == null
                            || !voucher.getIdKhachHang()
                            .equals(
                                    hoaDon.getKhachHang()
                                            .getIdKhachHang()
                            )
            )) {
                throw new RuntimeException(
                        "Voucher không thuộc về khách hàng của hóa đơn"
                );
            }

            if (voucher.getDieuKien() != null
                    && hoaDon.getTongTien()
                    .compareTo(
                            voucher.getDieuKien()
                    ) < 0) {
                throw new RuntimeException(
                        "Hóa đơn chưa đạt điều kiện sử dụng voucher"
                );
            }
        }

        /*
         * Trừ BTP / nguyên liệu / topping
         * đúng theo reservation đã tạo.
         *
         * Sau khi thành công:
         * ACTIVE -> CONSUMED.
         */
        datChoKhoService.xuatKhoTuDatCho(
                idHoaDon
        );

        /*
         * Voucher chỉ thực sự tiêu thụ
         * khi cửa hàng nhận đơn và bắt đầu làm.
         */
        if (voucher != null
                && voucher.getSoLuong() != null) {

            int soLuongConLai =
                    voucher.getSoLuong() - 1;

            voucher.setSoLuong(
                    Math.max(
                            soLuongConLai,
                            0
                    )
            );

            if (soLuongConLai <= 0) {
                voucher.setTrangThai(0);
            }

            voucherRepository.save(
                    voucher
            );
        }
    }

    /*
     * ==========================================
     * GHN DELIVERED
     *
     * Kho đã trừ từ lúc nhận đơn.
     * Tại đây TUYỆT ĐỐI không trừ kho/voucher nữa.
     *
     * delivered COD = khách đã nhận và trả tiền.
     * ==========================================
     */
    @Transactional
    public void xacNhanDaThuTien(
            Integer idHoaDon
    ) {

        HoaDon hoaDon =
                hoaDonRepository
                        .findByIdForUpdate(idHoaDon)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy hóa đơn"
                                )
                        );

        if (!"ONLINE".equalsIgnoreCase(
                hoaDon.getLoaiHoaDon()
        )
                || !"TIEN_MAT".equalsIgnoreCase(
                hoaDon.getHinhThucThanhToan()
        )) {
            return;
        }

        /*
         * Gọi lại delivered nhiều lần vẫn an toàn.
         */
        if ("DA_THANH_TOAN".equals(
                hoaDon.getTrangThai()
        )) {
            return;
        }

        if (!"CHO_THANH_TOAN".equals(
                hoaDon.getTrangThai()
        )) {
            throw new RuntimeException(
                    "Trạng thái hóa đơn COD không hợp lệ"
            );
        }

        VanDonGhn vanDon =
                vanDonGhnRepository
                        .findByHoaDon_IdHoaDon(idHoaDon)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy vận đơn"
                                )
                        );

        boolean daGiao =
                "delivered".equalsIgnoreCase(
                        vanDon.getTrangThaiGhn()
                )
                        || "GIAO_THANH_CONG".equals(
                        vanDon.getTrangThai()
                );

        if (!daGiao) {
            throw new RuntimeException(
                    "Đơn COD chưa giao thành công"
            );
        }

        /*
         * Chỉ ghi nhận thanh toán.
         *
         * KHÔNG gọi HoaDonService.thanhToanHoaDon()
         * vì kho + voucher đã xử lý lúc tiếp nhận.
         */
        hoaDon.setTrangThai(
                "DA_THANH_TOAN"
        );

        hoaDonRepository.save(
                hoaDon
        );
    }
}