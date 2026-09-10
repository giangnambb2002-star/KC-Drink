package com.example.datn.ban_hang_online.service;

import com.example.datn.ban_hang_online.dto.address.DiaChiOnlineRequest;
import com.example.datn.dia_chi.dto.DiaChiResponse;
import com.example.datn.dia_chi.entity.DiaChiKhachHang;
import com.example.datn.dia_chi.repository.DiaChiRepository;
import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaChiOnlineService {

    private final KhachHangOnlineService khachHangOnlineService;
    private final DiaChiRepository diaChiRepository;

    /**
     * Lấy toàn bộ địa chỉ đang hoạt động
     * của khách hàng hiện tại.
     *
     * Địa chỉ mặc định được đưa lên đầu.
     */
    @Transactional(readOnly = true)
    public List<DiaChiResponse> getMyAddresses(
            TaiKhoan taiKhoan
    ) {

        KhachHang khachHang =
                khachHangOnlineService
                        .getCurrentCustomer(taiKhoan);

        return diaChiRepository
                .findAllByKhachHang_IdKhachHang(
                        khachHang.getIdKhachHang()
                )
                .stream()
                .filter(diaChi ->
                        diaChi.getTrangThai() != null
                                && diaChi.getTrangThai() == 1
                )
                .sorted(
                        Comparator
                                .comparing(
                                        DiaChiKhachHang::getMacDinh,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                                .thenComparing(
                                        DiaChiKhachHang::getIdDiaChi
                                )
                )
                .map(this::toResponse)
                .toList();
    }

    /**
     * Thêm địa chỉ mới cho chính khách
     * đang đăng nhập.
     */
    @Transactional
    public DiaChiResponse createMyAddress(
            TaiKhoan taiKhoan,
            DiaChiOnlineRequest request
    ) {

        KhachHang khachHang =
                khachHangOnlineService
                        .getCurrentCustomer(taiKhoan);

        List<DiaChiKhachHang> danhSach =
                diaChiRepository
                        .findAllByKhachHang_IdKhachHang(
                                khachHang.getIdKhachHang()
                        );

        boolean coDiaChiHoatDong =
                danhSach.stream()
                        .anyMatch(item ->
                                item.getTrangThai() != null
                                        && item.getTrangThai() == 1
                        );

        /*
         * Nếu đây là địa chỉ đầu tiên:
         * tự động đặt mặc định.
         *
         * Nếu đã có địa chỉ:
         * chỉ mặc định khi request.macDinh = true.
         */
        boolean macDinh =
                !coDiaChiHoatDong
                        || Boolean.TRUE.equals(
                        request.getMacDinh()
                );

        if (macDinh) {
            boMacDinhCacDiaChiKhac(
                    danhSach,
                    null
            );
        }

        DiaChiKhachHang diaChi =
                new DiaChiKhachHang();

        diaChi.setKhachHang(khachHang);

        ganThongTinDiaChi(
                diaChi,
                request
        );

        diaChi.setMacDinh(macDinh);
        diaChi.setTrangThai(1);

        DiaChiKhachHang saved =
                diaChiRepository.save(diaChi);

        return toResponse(saved);
    }

    /**
     * Cập nhật địa chỉ.
     *
     * Chỉ sửa được địa chỉ thuộc
     * chính khách đang đăng nhập.
     */
    @Transactional
    public DiaChiResponse updateMyAddress(
            TaiKhoan taiKhoan,
            Integer idDiaChi,
            DiaChiOnlineRequest request
    ) {

        KhachHang khachHang =
                khachHangOnlineService
                        .getCurrentCustomer(taiKhoan);

        DiaChiKhachHang diaChi =
                layDiaChiThuocKhachHang(
                        khachHang,
                        idDiaChi
                );

        if (diaChi.getTrangThai() == null
                || diaChi.getTrangThai() != 1) {

            throw new RuntimeException(
                    "Địa chỉ đang bị khóa"
            );
        }

        boolean dangLaMacDinh =
                Boolean.TRUE.equals(
                        diaChi.getMacDinh()
                );

        boolean yeuCauMacDinh =
                Boolean.TRUE.equals(
                        request.getMacDinh()
                );

        /*
         * Không cho việc sửa thông tin vô tình
         * làm mất địa chỉ mặc định hiện tại.
         *
         * Muốn đổi mặc định sang địa chỉ khác
         * thì dùng API /default.
         */
        boolean macDinhSauCapNhat =
                dangLaMacDinh || yeuCauMacDinh;

        if (yeuCauMacDinh) {

            List<DiaChiKhachHang> danhSach =
                    diaChiRepository
                            .findAllByKhachHang_IdKhachHang(
                                    khachHang.getIdKhachHang()
                            );

            boMacDinhCacDiaChiKhac(
                    danhSach,
                    idDiaChi
            );
        }

        ganThongTinDiaChi(
                diaChi,
                request
        );

        diaChi.setMacDinh(
                macDinhSauCapNhat
        );

        DiaChiKhachHang saved =
                diaChiRepository.save(diaChi);

        return toResponse(saved);
    }

    /**
     * Đặt một địa chỉ làm mặc định.
     */
    @Transactional
    public DiaChiResponse setMyDefaultAddress(
            TaiKhoan taiKhoan,
            Integer idDiaChi
    ) {

        KhachHang khachHang =
                khachHangOnlineService
                        .getCurrentCustomer(taiKhoan);

        DiaChiKhachHang diaChi =
                layDiaChiThuocKhachHang(
                        khachHang,
                        idDiaChi
                );

        if (diaChi.getTrangThai() == null
                || diaChi.getTrangThai() != 1) {

            throw new RuntimeException(
                    "Địa chỉ đang bị khóa"
            );
        }

        List<DiaChiKhachHang> danhSach =
                diaChiRepository
                        .findAllByKhachHang_IdKhachHang(
                                khachHang.getIdKhachHang()
                        );

        boMacDinhCacDiaChiKhac(
                danhSach,
                idDiaChi
        );

        diaChi.setMacDinh(true);

        DiaChiKhachHang saved =
                diaChiRepository.save(diaChi);

        return toResponse(saved);
    }

    /**
     * Method đang được checkout/order sử dụng.
     *
     * Kiểm tra:
     * - địa chỉ tồn tại
     * - đang hoạt động
     * - thuộc khách đang login
     */
    @Transactional(readOnly = true)
    public DiaChiKhachHang layDiaChiHopLeCuaKhachHang(
            TaiKhoan taiKhoan,
            Integer idDiaChi
    ) {

        if (idDiaChi == null) {
            throw new RuntimeException(
                    "Vui lòng chọn địa chỉ nhận hàng"
            );
        }

        KhachHang khachHang =
                khachHangOnlineService
                        .getCurrentCustomer(taiKhoan);

        DiaChiKhachHang diaChi =
                layDiaChiThuocKhachHang(
                        khachHang,
                        idDiaChi
                );

        if (diaChi.getTrangThai() == null
                || diaChi.getTrangThai() != 1) {

            throw new RuntimeException(
                    "Địa chỉ nhận hàng đang bị khóa"
            );
        }

        return diaChi;
    }

    /**
     * Kiểm tra ownership.
     */
    private DiaChiKhachHang layDiaChiThuocKhachHang(
            KhachHang khachHang,
            Integer idDiaChi
    ) {

        if (idDiaChi == null) {
            throw new RuntimeException(
                    "Địa chỉ không hợp lệ"
            );
        }

        DiaChiKhachHang diaChi =
                diaChiRepository
                        .findById(idDiaChi)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Không tìm thấy địa chỉ"
                                )
                        );

        if (diaChi.getKhachHang() == null
                || !khachHang
                .getIdKhachHang()
                .equals(
                        diaChi
                                .getKhachHang()
                                .getIdKhachHang()
                )) {

            throw new RuntimeException(
                    "Địa chỉ không thuộc khách hàng đang đăng nhập"
            );
        }

        return diaChi;
    }

    /**
     * Bỏ mặc định các địa chỉ khác.
     *
     * idBoQua:
     * - null: bỏ tất cả
     * - có giá trị: giữ nguyên địa chỉ đó
     */
    private void boMacDinhCacDiaChiKhac(
            List<DiaChiKhachHang> danhSach,
            Integer idBoQua
    ) {

        for (DiaChiKhachHang item : danhSach) {

            if (idBoQua != null
                    && idBoQua.equals(
                    item.getIdDiaChi()
            )) {

                continue;
            }

            if (Boolean.TRUE.equals(
                    item.getMacDinh()
            )) {

                item.setMacDinh(false);
            }
        }
    }

    /**
     * Gán dữ liệu request vào entity.
     */
    private void ganThongTinDiaChi(
            DiaChiKhachHang diaChi,
            DiaChiOnlineRequest request
    ) {

        diaChi.setTenNguoiNhan(
                request.getTenNguoiNhan().trim()
        );

        diaChi.setSdtNguoiNhan(
                request.getSdtNguoiNhan().trim()
        );

        diaChi.setDiaChi(
                request.getDiaChi().trim()
        );

        diaChi.setProvinceId(
                request.getProvinceId()
        );

        diaChi.setDistrictId(
                request.getDistrictId()
        );

        diaChi.setWardCode(
                request.getWardCode().trim()
        );

        diaChi.setTenTinhThanh(
                request.getTenTinhThanh().trim()
        );

        diaChi.setTenQuanHuyen(
                request.getTenQuanHuyen().trim()
        );

        diaChi.setTenPhuongXa(
                request.getTenPhuongXa().trim()
        );
    }

    private DiaChiResponse toResponse(
            DiaChiKhachHang diaChi
    ) {

        DiaChiResponse response =
                new DiaChiResponse();

        response.setIdDiaChi(
                diaChi.getIdDiaChi()
        );

        response.setIdKhachHang(
                diaChi.getKhachHang()
                        .getIdKhachHang()
        );

        response.setTenNguoiNhan(
                diaChi.getTenNguoiNhan()
        );

        response.setSdtNguoiNhan(
                diaChi.getSdtNguoiNhan()
        );

        response.setDiaChi(
                diaChi.getDiaChi()
        );

        response.setMacDinh(
                diaChi.getMacDinh()
        );

        response.setTrangThai(
                diaChi.getTrangThai()
        );

        response.setProvinceId(
                diaChi.getProvinceId()
        );

        response.setDistrictId(
                diaChi.getDistrictId()
        );

        response.setWardCode(
                diaChi.getWardCode()
        );

        response.setTenTinhThanh(
                diaChi.getTenTinhThanh()
        );

        response.setTenQuanHuyen(
                diaChi.getTenQuanHuyen()
        );

        response.setTenPhuongXa(
                diaChi.getTenPhuongXa()
        );

        return response;
    }
}