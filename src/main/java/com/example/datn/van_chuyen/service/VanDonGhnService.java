package com.example.datn.van_chuyen.service;
import com.example.datn.dia_chi.entity.DiaChiKhachHang;
import com.example.datn.dia_chi.repository.DiaChiRepository;
import com.example.datn.hoa_don.entity.HoaDon;
import com.example.datn.hoa_don.repository.HoaDonRepository;
import com.example.datn.van_chuyen.dto.PhiVanChuyenResponse;
import com.example.datn.van_chuyen.dto.ThietLapGiaoHangRequest;
import com.example.datn.van_chuyen.dto.TinhPhiRequest;
import com.example.datn.van_chuyen.dto.VanDonGhnResponse;
import com.example.datn.van_chuyen.entity.VanDonGhn;
import com.example.datn.van_chuyen.dto.TaoDonGhnRequest;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import com.example.datn.van_chuyen.repository.VanDonGhnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class VanDonGhnService {
    private final VanDonGhnRepository vanDonRepository;
    private final HoaDonRepository hoaDonRepository;
    private final DiaChiRepository diaChiRepository;
    private final GhnService ghnService;
    @Transactional(readOnly = true)
    public VanDonGhnResponse getByHoaDon(Integer idHoaDon) {
        VanDonGhn vanDon = vanDonRepository.findByHoaDon_IdHoaDon(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không có thông tin giao hàng"));
        return toResponse(vanDon);
    }
    @Transactional
    public VanDonGhnResponse thietLapGiaoHang(
            Integer idHoaDon,
            ThietLapGiaoHangRequest request
    ) {
        HoaDon hoaDon = getHoaDonChoSua(idHoaDon);
        if (hoaDon.getKhachHang() == null) {
            throw new RuntimeException("Vui lòng chọn khách hàng trước khi giao hàng");
        }
        DiaChiKhachHang diaChi = diaChiRepository.findById(request.getIdDiaChi())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng"));
        if (diaChi.getTrangThai() == null || diaChi.getTrangThai() != 1) {
            throw new RuntimeException("Địa chỉ giao hàng đang bị khóa");
        }
        Integer idKhachHangHoaDon = hoaDon.getKhachHang().getIdKhachHang();
        Integer idKhachHangDiaChi = diaChi.getKhachHang().getIdKhachHang();
        if (!idKhachHangHoaDon.equals(idKhachHangDiaChi)) {
            throw new RuntimeException("Địa chỉ không thuộc khách hàng của hóa đơn");
        }
        if (diaChi.getDistrictId() == null || diaChi.getDistrictId() <= 0
                || diaChi.getWardCode() == null || diaChi.getWardCode().isBlank()) {
            throw new RuntimeException("Địa chỉ chưa có đủ mã quận/huyện và phường/xã");
        }
        if (diaChi.getTenNguoiNhan() == null || diaChi.getTenNguoiNhan().isBlank()
                || diaChi.getSdtNguoiNhan() == null || diaChi.getSdtNguoiNhan().isBlank()) {
            throw new RuntimeException("Địa chỉ chưa có đủ tên và số điện thoại người nhận");
        }
        TinhPhiRequest tinhPhiRequest = new TinhPhiRequest();
        tinhPhiRequest.setDistrictId(diaChi.getDistrictId());
        tinhPhiRequest.setWardCode(diaChi.getWardCode());
        tinhPhiRequest.setWeight(1000);
        tinhPhiRequest.setLength(20);
        tinhPhiRequest.setWidth(20);
        tinhPhiRequest.setHeight(20);
        tinhPhiRequest.setInsuranceValue(0);
        PhiVanChuyenResponse phiResponse = ghnService.calculateFee(tinhPhiRequest);
        if (phiResponse.getPhiVanChuyen() == null
                || phiResponse.getPhiVanChuyen() < 0) {
            throw new RuntimeException("Phí vận chuyển GHN không hợp lệ");
        }
        VanDonGhn vanDon = vanDonRepository.findByHoaDon_IdHoaDon(idHoaDon)
                .orElseGet(VanDonGhn::new);
        if (vanDon.getMaVanDonGhn() != null && !vanDon.getMaVanDonGhn().isBlank()) {
            throw new RuntimeException("Hóa đơn đã tạo vận đơn GHN, không thể đổi địa chỉ");
        }
        BigDecimal phiVanChuyen =
                BigDecimal.valueOf(phiResponse.getPhiVanChuyen());
        vanDon.setHoaDon(hoaDon);
        vanDon.setDiaChiKhachHang(diaChi);
        vanDon.setTenNguoiNhan(diaChi.getTenNguoiNhan().trim());
        vanDon.setSdtNguoiNhan(diaChi.getSdtNguoiNhan().trim());
        vanDon.setDiaChiGiaoHang(taoDiaChiDayDu(diaChi));
        vanDon.setProvinceId(diaChi.getProvinceId());
        vanDon.setDistrictId(diaChi.getDistrictId());
        vanDon.setWardCode(diaChi.getWardCode().trim());
        vanDon.setTenTinhThanh(diaChi.getTenTinhThanh());
        vanDon.setTenQuanHuyen(diaChi.getTenQuanHuyen());
        vanDon.setTenPhuongXa(diaChi.getTenPhuongXa());
        vanDon.setPhiVanChuyen(phiVanChuyen);
        vanDon.setCodAmount(0);
        vanDon.setInsuranceValue(0);
        vanDon.setTrangThai("CHO_THANH_TOAN");
        vanDon.setTrangThaiGhn(null);
        vanDon.setGhiChu(chuanHoaGhiChu(request.getGhiChu()));
        hoaDon.setPhiVanChuyen(phiVanChuyen);
        capNhatThanhTien(hoaDon);
        hoaDonRepository.save(hoaDon);
        return toResponse(vanDonRepository.save(vanDon));
    }
    @Transactional
    public void boGiaoHang(Integer idHoaDon) {
        HoaDon hoaDon = getHoaDonChoSua(idHoaDon);
        VanDonGhn vanDon = vanDonRepository.findByHoaDon_IdHoaDon(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Hóa đơn chưa thiết lập giao hàng"));
        if (vanDon.getMaVanDonGhn() != null && !vanDon.getMaVanDonGhn().isBlank()) {
            throw new RuntimeException("Hóa đơn đã tạo vận đơn GHN, không thể bỏ giao hàng");
        }
        vanDonRepository.delete(vanDon);
        hoaDon.setPhiVanChuyen(BigDecimal.ZERO);
        capNhatThanhTien(hoaDon);
        hoaDonRepository.save(hoaDon);
    }
    @Transactional
    public void capNhatSauThanhToan(Integer idHoaDon) {
        vanDonRepository.findByHoaDon_IdHoaDon(idHoaDon).ifPresent(vanDon -> {
            if (vanDon.getMaVanDonGhn() == null
                    || vanDon.getMaVanDonGhn().isBlank()) {
                vanDon.setTrangThai("CHO_TAO_DON");
                vanDonRepository.save(vanDon);
            }
        });
    }
    private HoaDon getHoaDonChoSua(Integer idHoaDon) {
        HoaDon hoaDon = hoaDonRepository.findByIdForUpdate(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        if (!"CHO_THANH_TOAN".equals(hoaDon.getTrangThai())) {
            throw new RuntimeException("Hóa đơn không còn ở trạng thái chờ thanh toán");
        }
        String payosStatus = hoaDon.getPayosStatus();
        if (payosStatus != null && !payosStatus.isBlank()
                && !"CANCELLED".equalsIgnoreCase(payosStatus)
                && !"EXPIRED".equalsIgnoreCase(payosStatus)) {
            throw new RuntimeException(
                    "Hóa đơn đang có giao dịch PayOS chờ thanh toán, không thể sửa giao hàng"
            );
        }
        return hoaDon;
    }
    private void capNhatThanhTien(HoaDon hoaDon) {
        BigDecimal tongTien = hoaDon.getTongTien() == null
                ? BigDecimal.ZERO : hoaDon.getTongTien();
        BigDecimal giamGia = hoaDon.getGiamGia() == null
                ? BigDecimal.ZERO : hoaDon.getGiamGia();
        BigDecimal phiVanChuyen = hoaDon.getPhiVanChuyen() == null
                ? BigDecimal.ZERO : hoaDon.getPhiVanChuyen();
        hoaDon.setThanhTien(
                tongTien.subtract(giamGia)
                        .add(phiVanChuyen)
                        .max(BigDecimal.ZERO)
                        .setScale(0, RoundingMode.HALF_UP)
        );
    }
    private String taoDiaChiDayDu(DiaChiKhachHang diaChi) {
        List<String> parts = new ArrayList<>();
        themPhanDiaChi(parts, diaChi.getDiaChi());
        themPhanDiaChi(parts, diaChi.getTenPhuongXa());
        themPhanDiaChi(parts, diaChi.getTenQuanHuyen());
        themPhanDiaChi(parts, diaChi.getTenTinhThanh());
        if (parts.isEmpty()) {
            throw new RuntimeException("Địa chỉ giao hàng không hợp lệ");
        }
        return String.join(", ", parts);
    }
    private void themPhanDiaChi(List<String> parts, String value) {
        if (value != null && !value.isBlank()) {
            parts.add(value.trim());
        }
    }
    private String chuanHoaGhiChu(String ghiChu) {
        return ghiChu == null || ghiChu.trim().isEmpty()
                ? null : ghiChu.trim();
    }
    private VanDonGhn getVanDonDaTao(Integer idHoaDon) {
        VanDonGhn vanDon = vanDonRepository.findByHoaDon_IdHoaDon(idHoaDon)
                .orElseThrow(() -> new RuntimeException(
                        "Hóa đơn chưa có thông tin giao hàng"
                ));
        if (vanDon.getMaVanDonGhn() == null
                || vanDon.getMaVanDonGhn().isBlank()) {
            throw new RuntimeException("Hóa đơn chưa tạo đơn GHN");
        }
        return vanDon;
    }

    private String chuyenTrangThaiNoiBo(String ghnStatus) {
        return switch (ghnStatus.toLowerCase()) {
            case "ready_to_pick" -> "DA_TAO_DON";
            case "delivered" -> "GIAO_THANH_CONG";
            case "cancel" -> "DA_HUY";
            case "return", "returned", "returning",
                    "return_transporting", "return_sorting",
                    "waiting_to_return" -> "HOAN_HANG";
            default -> "DANG_GIAO";
        };
    }
    private LocalDateTime parseGhnDateTime(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return OffsetDateTime.parse(value)
                    .atZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(value);
            } catch (DateTimeParseException ignoredAgain) {
                return null;
            }
        }
    }
    private VanDonGhnResponse toResponse(VanDonGhn vanDon) {
        VanDonGhnResponse response = new VanDonGhnResponse();
        response.setIdVanDon(vanDon.getIdVanDon());
        response.setIdHoaDon(vanDon.getHoaDon().getIdHoaDon());
        response.setIdDiaChi(vanDon.getDiaChiKhachHang() == null
                ? null : vanDon.getDiaChiKhachHang().getIdDiaChi());
        response.setTenNguoiNhan(vanDon.getTenNguoiNhan());
        response.setSdtNguoiNhan(vanDon.getSdtNguoiNhan());
        response.setDiaChiGiaoHang(vanDon.getDiaChiGiaoHang());
        response.setProvinceId(vanDon.getProvinceId());
        response.setDistrictId(vanDon.getDistrictId());
        response.setWardCode(vanDon.getWardCode());
        response.setTenTinhThanh(vanDon.getTenTinhThanh());
        response.setTenQuanHuyen(vanDon.getTenQuanHuyen());
        response.setTenPhuongXa(vanDon.getTenPhuongXa());
        response.setPhiVanChuyen(vanDon.getPhiVanChuyen());
        response.setCodAmount(vanDon.getCodAmount());
        response.setInsuranceValue(vanDon.getInsuranceValue());
        response.setMaVanDonGhn(vanDon.getMaVanDonGhn());
        response.setTrangThai(vanDon.getTrangThai());
        response.setTrangThaiGhn(vanDon.getTrangThaiGhn());
        response.setThoiGianGiaoDuKien(vanDon.getThoiGianGiaoDuKien());
        response.setGhiChu(vanDon.getGhiChu());
        response.setNgayTao(vanDon.getNgayTao());
        response.setNgayCapNhat(vanDon.getNgayCapNhat());
        return response;
    }
    @Transactional(readOnly = true)
    public boolean coThongTinGiaoHang(Integer idHoaDon) {
        return vanDonRepository.existsByHoaDon_IdHoaDon(idHoaDon);
    }
    @Transactional
    public void capNhatSauHuyHoaDon(Integer idHoaDon) {
        vanDonRepository.findByHoaDon_IdHoaDon(idHoaDon).ifPresent(vanDon -> {
            vanDon.setTrangThai("DA_HUY");
            vanDonRepository.save(vanDon);
        });
    }
    @Transactional
    public VanDonGhnResponse taoDonGhn(Integer idHoaDon) {
        HoaDon hoaDon = hoaDonRepository.findByIdForUpdate(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        if (!"DA_THANH_TOAN".equals(hoaDon.getTrangThai())) {
            throw new RuntimeException(
                    "Chỉ được tạo đơn GHN sau khi hóa đơn đã thanh toán"
            );
        }
        VanDonGhn vanDon = vanDonRepository.findByHoaDon_IdHoaDon(idHoaDon)
                .orElseThrow(() -> new RuntimeException(
                        "Hóa đơn chưa có thông tin giao hàng"
                ));
        if (vanDon.getMaVanDonGhn() != null
                && !vanDon.getMaVanDonGhn().isBlank()) {
            return toResponse(vanDon);
        }
        if (!"CHO_TAO_DON".equals(vanDon.getTrangThai())) {
            throw new RuntimeException("Vận đơn chưa sẵn sàng để tạo trên GHN");
        }
        TaoDonGhnRequest request = new TaoDonGhnRequest();
        request.setToName(vanDon.getTenNguoiNhan());
        request.setToPhone(vanDon.getSdtNguoiNhan());
        request.setToAddress(vanDon.getDiaChiGiaoHang());
        request.setToDistrictId(vanDon.getDistrictId());
        request.setToWardCode(vanDon.getWardCode());
        request.setToWardName(vanDon.getTenPhuongXa());
        request.setToDistrictName(vanDon.getTenQuanHuyen());
        request.setToProvinceName(vanDon.getTenTinhThanh());
        request.setCodAmount(vanDon.getCodAmount());
        request.setInsuranceValue(vanDon.getInsuranceValue());
        request.setNote(vanDon.getGhiChu());
        JsonNode ghnData = ghnService.createOrder(request);
        String orderCode = ghnData == null
                ? null : ghnData.path("order_code").asText(null);
        if (orderCode == null || orderCode.isBlank()) {
            throw new RuntimeException("GHN không trả về mã vận đơn");
        }
        vanDon.setMaVanDonGhn(orderCode);
        vanDon.setTrangThai("DA_TAO_DON");
        vanDon.setTrangThaiGhn("ready_to_pick");
        vanDon.setThoiGianGiaoDuKien(
                parseGhnDateTime(
                        ghnData.path("expected_delivery_time").asText(null)
                )
        );
        return toResponse(vanDonRepository.save(vanDon));
    }
    @Transactional
    public VanDonGhnResponse lamMoiTrangThaiGhn(Integer idHoaDon) {
        hoaDonRepository.findByIdForUpdate(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        VanDonGhn vanDon = getVanDonDaTao(idHoaDon);
        JsonNode ghnData = ghnService.getOrderDetail(vanDon.getMaVanDonGhn());
        String ghnStatus = ghnData == null
                ? null : ghnData.path("status").asText(null);
        if (ghnStatus == null || ghnStatus.isBlank()) {
            throw new RuntimeException("GHN không trả về trạng thái vận đơn");
        }
        vanDon.setTrangThaiGhn(ghnStatus);
        vanDon.setTrangThai(chuyenTrangThaiNoiBo(ghnStatus));
        String leadtime = ghnData.path("leadtime").asText(null);
        if (leadtime != null && !leadtime.isBlank()) {
            vanDon.setThoiGianGiaoDuKien(parseGhnDateTime(leadtime));
        }
        return toResponse(vanDonRepository.saveAndFlush(vanDon));
    }

    @Transactional
    public VanDonGhnResponse huyDonGhn(Integer idHoaDon) {
        hoaDonRepository.findByIdForUpdate(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        VanDonGhn vanDon = getVanDonDaTao(idHoaDon);
        JsonNode detail = ghnService.getOrderDetail(vanDon.getMaVanDonGhn());
        String currentStatus = detail == null
                ? null : detail.path("status").asText(null);
        if ("cancel".equalsIgnoreCase(currentStatus)) {
            vanDon.setTrangThaiGhn("cancel");
            vanDon.setTrangThai("DA_HUY");
            return toResponse(vanDonRepository.saveAndFlush(vanDon));
        }
        if ("delivered".equalsIgnoreCase(currentStatus)) {
            throw new RuntimeException(
                    "Đơn GHN đã giao thành công, không thể hủy"
            );
        }

        JsonNode cancelData = ghnService.cancelOrder(vanDon.getMaVanDonGhn());
        JsonNode result = cancelData != null
                && cancelData.isArray()
                && cancelData.size() > 0
                ? cancelData.get(0) : null;
        if (result == null || !result.path("result").asBoolean(false)) {
            String message = result == null
                    ? "GHN không xác nhận hủy đơn"
                    : result.path("message").asText("GHN không xác nhận hủy đơn");
            throw new RuntimeException(message);
        }

        vanDon.setTrangThaiGhn("cancel");
        vanDon.setTrangThai("DA_HUY");
        return toResponse(vanDonRepository.saveAndFlush(vanDon));
    }
}