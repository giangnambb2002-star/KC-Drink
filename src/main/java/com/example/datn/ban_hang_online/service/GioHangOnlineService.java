package com.example.datn.ban_hang_online.service;
import com.example.datn.dia_chi.entity.DiaChiKhachHang;
import com.example.datn.van_chuyen.dto.PhiVanChuyenResponse;
import com.example.datn.van_chuyen.dto.TinhPhiRequest;
import com.example.datn.van_chuyen.service.GhnService;
import com.example.datn.voucher.dto.VoucherValidationResponse;
import com.example.datn.voucher.entity.Voucher;
import com.example.datn.voucher.repository.VoucherRepository;
import com.example.datn.voucher.service.VoucherService;
import com.example.datn.ban_hang_online.dto.checkout.ChiTietGioHangRequest;
import com.example.datn.ban_hang_online.dto.checkout.ChiTietGioHangResponse;
import com.example.datn.ban_hang_online.dto.checkout.XemTruocGioHangRequest;
import com.example.datn.ban_hang_online.dto.checkout.XemTruocGioHangResponse;
import com.example.datn.ban_hang_online.dto.checkout.ToppingGioHangRequest;
import com.example.datn.ban_hang_online.dto.checkout.ToppingGioHangResponse;
import com.example.datn.khuyen_mai.dto.KetQuaKhuyenMaiResponse;
import com.example.datn.khuyen_mai.service.KhuyenMaiService;
import com.example.datn.san_pham.entity.SanPham;
import com.example.datn.san_pham.entity.SanPhamSize;
import com.example.datn.san_pham.repository.SanPhamRepository;
import com.example.datn.san_pham.repository.SanPhamSizeRepository;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.topping.dto.ToppingResponse;
import com.example.datn.topping.service.ToppingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class GioHangOnlineService {
    private final SanPhamRepository sanPhamRepository;
    private final SanPhamSizeRepository sanPhamSizeRepository;
    private final KhuyenMaiService khuyenMaiService;
    private final ToppingService toppingService;
    private final KiemTraTonKhoOnlineService kiemTraTonKhoOnlineService;
    private final DiaChiOnlineService diaChiOnlineService;
    private final GhnService ghnService;
    private final VoucherService voucherService;
    private final VoucherRepository voucherRepository;
    @Transactional(readOnly = true)
    public XemTruocGioHangResponse preview(
            XemTruocGioHangRequest request,
            TaiKhoan taiKhoan
    ) {
        validateRequest(request);
        /*
         * 1. Xác minh địa chỉ:
         *
         * - có tồn tại
         * - đang hoạt động
         * - thuộc đúng USER đăng nhập
         */
        DiaChiKhachHang diaChi =
                diaChiOnlineService.layDiaChiHopLeCuaKhachHang(
                        taiKhoan,
                        request.getIdDiaChi()
                );
        List<ChiTietGioHangResponse> itemResponses =
                new ArrayList<>();
        BigDecimal tamTinhTruocKhuyenMai =
                BigDecimal.ZERO;
        BigDecimal tongTienGiamKhuyenMai =
                BigDecimal.ZERO;
        BigDecimal tamTinhSauKhuyenMai =
                BigDecimal.ZERO;
        for (ChiTietGioHangRequest item : request.getItems()) {
            ChiTietGioHangResponse itemResponse =
                    tinhMotDong(item);
            itemResponses.add(itemResponse);
            tamTinhTruocKhuyenMai =
                    tamTinhTruocKhuyenMai
                            .add(
                                    itemResponse
                                            .getTienSanPhamTruocKhuyenMai()
                            )
                            .add(
                                    itemResponse.getTienTopping()
                            );
            tongTienGiamKhuyenMai =
                    tongTienGiamKhuyenMai.add(
                            itemResponse.getTienGiamKhuyenMai()
                    );
            tamTinhSauKhuyenMai =
                    tamTinhSauKhuyenMai.add(
                            itemResponse.getThanhTienDong()
                    );
        }
        /*
         * 3. Kiểm tra tồn kho theo toàn bộ giỏ.
         *
         * Preview chỉ CHECK.
         * Không trừ kho.
         */
        kiemTraTonKhoOnlineService.kiemTraDuTon(request);
        /*
         * 4. Voucher.
         *
         * Không chọn:
         * tienGiamVoucher = 0
         *
         * Có chọn:
         * backend kiểm tra:
         * - voucher chung / voucher riêng
         * - khách hàng
         * - trạng thái
         * - số lượng
         * - thời gian
         * - điều kiện đơn
         * - PERCENT / FIXED
         * - giảm tối đa
         *
         * Preview KHÔNG trừ số lượng voucher.
         */
        Integer idVoucher = null;
        String maVoucher = null;
        String tenVoucher = null;
        String loaiVoucher = null;
        BigDecimal tienGiamVoucher =
                BigDecimal.ZERO;
        if (request.getIdVoucher() != null) {
            if (diaChi.getKhachHang() == null
                    || diaChi.getKhachHang().getIdKhachHang() == null) {
                throw new RuntimeException(
                        "Không xác định được khách hàng đang đăng nhập"
                );
            }
            Integer idKhachHang =
                    diaChi.getKhachHang()
                            .getIdKhachHang();
            VoucherValidationResponse ketQuaVoucher =
                    voucherService.kiemTraVoucherTheoId(
                            request.getIdVoucher(),
                            idKhachHang,
                            tamTinhSauKhuyenMai
                    );
            if (!ketQuaVoucher.isHopLe()) {
                throw new RuntimeException(
                        ketQuaVoucher.getThongBao()
                );
            }
            Voucher voucher =
                    voucherRepository
                            .findById(request.getIdVoucher())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Voucher không tồn tại"
                                    )
                            );
            idVoucher = voucher.getIdVoucher();
            maVoucher = voucher.getMaVoucher();
            tenVoucher = voucher.getTenVoucher();
            loaiVoucher = voucher.getLoaiVoucher();
            tienGiamVoucher =
                    ketQuaVoucher.getSoTienGiam() == null
                            ? BigDecimal.ZERO
                            : ketQuaVoucher.getSoTienGiam();
        }
        /*
         * Không bao giờ để voucher
         * giảm tiền hàng xuống âm.
         */
        BigDecimal tienHangSauVoucher =
                tamTinhSauKhuyenMai
                        .subtract(tienGiamVoucher)
                        .max(BigDecimal.ZERO);
        /*
         * 5. Tính phí GHN.
         *
         * Chỉ calculate fee.
         * Không tạo vận đơn.
         */
        BigDecimal phiVanChuyen =
                tinhPhiVanChuyen(diaChi);
        /*
         * 6. Tổng thanh toán:
         *
         * tiền hàng sau CTKM
         * - voucher
         * + phí vận chuyển
         */
        BigDecimal tongThanhToan =
                tienHangSauVoucher
                        .add(phiVanChuyen);
        return new XemTruocGioHangResponse(
                itemResponses,
                tamTinhTruocKhuyenMai,
                tongTienGiamKhuyenMai,
                tamTinhSauKhuyenMai,
                idVoucher,
                maVoucher,
                tenVoucher,
                loaiVoucher,
                tienGiamVoucher,
                phiVanChuyen,
                tongThanhToan
        );
    }
    private ChiTietGioHangResponse tinhMotDong(
            ChiTietGioHangRequest item
    ) {
        validateItem(item);
        // =========================
        // SẢN PHẨM
        // =========================
        SanPham sanPham =
                sanPhamRepository
                        .findById(item.getIdSanPham())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy sản phẩm"
                                )
                        );
        if (sanPham.getTrangThai() == null
                || sanPham.getTrangThai() != 1) {
            throw new RuntimeException(
                    "Sản phẩm \""
                            + sanPham.getTenSanPham()
                            + "\" đang ngừng bán"
            );
        }
        // =========================
        // SIZE
        // =========================
        SanPhamSize sanPhamSize =
                sanPhamSizeRepository
                        .findByIdSanPhamAndSize_IdSize(
                                item.getIdSanPham(),
                                item.getIdSize()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Sản phẩm không hỗ trợ size này"
                                )
                        );
        BigDecimal phuThuSize =
                sanPhamSize.getPhuThu() == null
                        ? BigDecimal.ZERO
                        : sanPhamSize.getPhuThu();
        // =========================
        // CTKM
        // =========================
        KetQuaKhuyenMaiResponse khuyenMai =
                khuyenMaiService
                        .tinhKhuyenMaiChoSanPham(
                                item.getIdSanPham()
                        );
        BigDecimal giaSanPhamGoc =
                khuyenMai.getGiaGoc();
        BigDecimal giaSauKhuyenMai =
                khuyenMai.getGiaSauKhuyenMai();
        BigDecimal soLuong =
                BigDecimal.valueOf(item.getSoLuong());
        /*
         * CTKM CHỈ tính trên giá sản phẩm.
         *
         * Size được cộng sau CTKM.
         */
        BigDecimal tienSanPhamTruocKhuyenMai =
                giaSanPhamGoc
                        .add(phuThuSize)
                        .multiply(soLuong);
        BigDecimal tienGiamKhuyenMai =
                khuyenMai
                        .getTienGiam()
                        .multiply(soLuong);
        BigDecimal donGiaSauKhuyenMai =
                giaSauKhuyenMai
                        .add(phuThuSize);
        BigDecimal tienSanPhamSauKhuyenMai =
                donGiaSauKhuyenMai
                        .multiply(soLuong);
        // =========================
        // TOPPING
        // =========================
        List<ToppingGioHangResponse>
                toppingResponses =
                new ArrayList<>();
        BigDecimal tienTopping =
                BigDecimal.ZERO;
        Set<Integer> toppingDaXuLy =
                new HashSet<>();
        if (item.getToppings() != null) {
            for (ToppingGioHangRequest toppingRequest
                    : item.getToppings()) {
                validateToppingRequest(toppingRequest);
                /*
                 * Một topping không được xuất hiện 2 lần
                 * trong cùng một dòng món.
                 */
                if (!toppingDaXuLy.add(
                        toppingRequest.getIdTopping()
                )) {
                    throw new RuntimeException(
                            "Topping bị trùng trong cùng một sản phẩm"
                    );
                }
                /*
                 * Reuse ToppingService hiện có:
                 *
                 * - lấy tên
                 * - lấy giá hiện tại từ DB
                 * - tính tổng tồn kho còn hạn
                 */
                ToppingResponse topping =
                        toppingService.getById(
                                toppingRequest.getIdTopping()
                        );
                if (topping.getTrangThai() == null
                        || topping.getTrangThai() != 1) {
                    throw new RuntimeException(
                            "Topping \""
                                    + topping.getTenTopping()
                                    + "\" đang ngừng bán"
                    );
                }
                if (topping.getGiaTopping() == null
                        || topping.getGiaTopping()
                        .compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException(
                            "Giá topping không hợp lệ"
                    );
                }
                int tonKho =
                        topping.getTongTonKho() == null
                                ? 0
                                : topping.getTongTonKho();
                /*
                 * Lưu ý:
                 * topping x1 là 1 phần cho cả dòng,
                 * KHÔNG nhân thêm theo số lượng ly.
                 */
                if (toppingRequest.getSoLuong() > tonKho) {
                    throw new RuntimeException(
                            "Topping \""
                                    + topping.getTenTopping()
                                    + "\" chỉ còn "
                                    + tonKho
                                    + " phần"
                    );
                }
                BigDecimal thanhTienTopping =
                        topping.getGiaTopping()
                                .multiply(
                                        BigDecimal.valueOf(
                                                toppingRequest
                                                        .getSoLuong()
                                        )
                                );
                tienTopping =
                        tienTopping.add(
                                thanhTienTopping
                        );
                toppingResponses.add(
                        new ToppingGioHangResponse(
                                topping.getIdTopping(),
                                topping.getTenTopping(),
                                toppingRequest.getSoLuong(),
                                topping.getGiaTopping(),
                                thanhTienTopping
                        )
                );
            }
        }
        BigDecimal thanhTienDong =
                tienSanPhamSauKhuyenMai
                        .add(tienTopping);
        int mucDuong =
                item.getMucDuong() == null
                        ? 100
                        : item.getMucDuong();
        int mucDa =
                item.getMucDa() == null
                        ? 100
                        : item.getMucDa();
        String ghiChu =
                item.getGhiChu() != null
                        && !item.getGhiChu()
                        .trim()
                        .isEmpty()
                        ? item.getGhiChu().trim()
                        : null;
        return new ChiTietGioHangResponse(
                sanPham.getIdSanPham(),
                sanPham.getTenSanPham(),
                sanPhamSize.getSize().getIdSize(),
                sanPhamSize.getSize().getTenSize(),
                mucDuong,
                mucDa,
                item.getSoLuong(),
                ghiChu,
                giaSanPhamGoc,
                phuThuSize,
                khuyenMai.getIdKm(),
                khuyenMai.getTenKm(),
                tienGiamKhuyenMai,
                donGiaSauKhuyenMai,
                tienSanPhamTruocKhuyenMai,
                tienSanPhamSauKhuyenMai,
                toppingResponses,
                tienTopping,
                thanhTienDong
        );
    }
    private void validateRequest(
            XemTruocGioHangRequest request
    ) {
        if (request == null
                || request.getItems() == null
                || request.getItems().isEmpty()) {
            throw new RuntimeException(
                    "Giỏ hàng không có sản phẩm"
            );
        }
        if (request.getItems().size() > 50) {
            throw new RuntimeException(
                    "Giỏ hàng có quá nhiều dòng sản phẩm"
            );
        }
    }
    private void validateItem(
            ChiTietGioHangRequest item
    ) {
        if (item == null) {
            throw new RuntimeException(
                    "Dữ liệu sản phẩm không hợp lệ"
            );
        }
        if (item.getIdSanPham() == null) {
            throw new RuntimeException(
                    "Sản phẩm không được để trống"
            );
        }
        if (item.getIdSize() == null) {
            throw new RuntimeException(
                    "Size không được để trống"
            );
        }
        if (item.getSoLuong() == null
                || item.getSoLuong() <= 0) {
            throw new RuntimeException(
                    "Số lượng sản phẩm phải lớn hơn 0"
            );
        }
        if (item.getSoLuong() > 100) {
            throw new RuntimeException(
                    "Số lượng sản phẩm quá lớn"
            );
        }
        validateMucDuong(item.getMucDuong());
        validateMucDa(item.getMucDa());
    }
    private void validateMucDuong(Integer mucDuong) {
        if (mucDuong == null) {
            return;
        }
        if (mucDuong != 0
                && mucDuong != 30
                && mucDuong != 50
                && mucDuong != 70
                && mucDuong != 100) {
            throw new RuntimeException(
                    "Mức đường không hợp lệ"
            );
        }
    }
    private void validateMucDa(Integer mucDa) {
        if (mucDa == null) {
            return;
        }
        if (mucDa != 0
                && mucDa != 50
                && mucDa != 100) {
            throw new RuntimeException(
                    "Mức đá không hợp lệ"
            );
        }
    }
    private void validateToppingRequest(
            ToppingGioHangRequest topping
    ) {
        if (topping == null
                || topping.getIdTopping() == null) {
            throw new RuntimeException(
                    "Topping không hợp lệ"
            );
        }
        if (topping.getSoLuong() == null
                || topping.getSoLuong() <= 0) {
            throw new RuntimeException(
                    "Số lượng topping phải lớn hơn 0"
            );
        }
        if (topping.getSoLuong() > 100) {
            throw new RuntimeException(
                    "Số lượng topping quá lớn"
            );
        }
    }
    private BigDecimal tinhPhiVanChuyen(
            DiaChiKhachHang diaChi
    ) {
        if (diaChi.getDistrictId() == null
                || diaChi.getWardCode() == null
                || diaChi.getWardCode().trim().isEmpty()) {
            throw new RuntimeException(
                    "Địa chỉ chưa đủ thông tin để tính phí vận chuyển"
            );
        }
        TinhPhiRequest tinhPhiRequest =
                new TinhPhiRequest();
        /*
         * Reuse đúng cấu hình hiện tại
         * của VanDonGhnService.
         */
        tinhPhiRequest.setDistrictId(
                diaChi.getDistrictId()
        );
        tinhPhiRequest.setWardCode(
                diaChi.getWardCode()
        );
        tinhPhiRequest.setWeight(1000);
        tinhPhiRequest.setLength(20);
        tinhPhiRequest.setWidth(20);
        tinhPhiRequest.setHeight(20);
        tinhPhiRequest.setInsuranceValue(0);
        PhiVanChuyenResponse phiResponse =
                ghnService.calculateFee(
                        tinhPhiRequest
                );
        if (phiResponse == null
                || phiResponse.getPhiVanChuyen() == null) {
            throw new RuntimeException(
                    "Không lấy được phí vận chuyển từ GHN"
            );
        }
        return BigDecimal.valueOf(
                phiResponse.getPhiVanChuyen()
        );
    }
}