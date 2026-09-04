package com.example.datn.khuyen_mai.service;

import com.example.datn.common.PageResponse;
import com.example.datn.khuyen_mai.dto.*;
import com.example.datn.khuyen_mai.entity.KhuyenMai;
import com.example.datn.khuyen_mai.entity.SanPhamKhuyenMai;
import com.example.datn.khuyen_mai.enums.LoaiGiamKhuyenMai;
import com.example.datn.khuyen_mai.enums.TrangThaiKhuyenMai;
import com.example.datn.khuyen_mai.repository.KhuyenMaiRepository;
import com.example.datn.khuyen_mai.repository.SanPhamKhuyenMaiRepository;
import com.example.datn.san_pham.entity.SanPham;
import com.example.datn.san_pham.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KhuyenMaiService {

    private static final Set<String> SORT_FIELDS = Set.of(
            "idKm",
            "tenKm",
            "giaTriGiam",
            "ngayBatDau",
            "ngayKetThuc",
            "trangThai"
    );

    private final KhuyenMaiRepository khuyenMaiRepository;
    private final SanPhamKhuyenMaiRepository sanPhamKhuyenMaiRepository;
    private final SanPhamRepository sanPhamRepository;

    @Transactional(readOnly = true)
    public PageResponse<KhuyenMaiResponse> getAll(
            String keyword,
            Integer trangThai,
            int page,
            int size,
            String sortBy,
            String direction
    ) {
        String keywordChuanHoa = keyword == null
                ? null
                : keyword.trim();

        String sortField = SORT_FIELDS.contains(sortBy)
                ? sortBy
                : "idKm";

        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                sort
        );

        Page<KhuyenMai> pageData = khuyenMaiRepository.search(
                keywordChuanHoa,
                trangThai,
                pageable
        );

        List<KhuyenMaiResponse> content = pageData.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isLast()
        );
    }

    @Transactional(readOnly = true)
    public KhuyenMaiResponse getById(Integer id) {
        KhuyenMai khuyenMai = findById(id);
        return toResponse(khuyenMai);
    }

    @Transactional
    public KhuyenMaiResponse create(KhuyenMaiRequest request) {
        validateRequest(request);

        String tenKm = request.getTenKm().trim();

        if (khuyenMaiRepository.existsByTenKmIgnoreCase(tenKm)) {
            throw new RuntimeException(
                    "Tên chương trình khuyến mãi đã tồn tại"
            );
        }

        List<SanPham> sanPhamList =
                layDanhSachSanPham(request.getIdSanPham());

        KhuyenMai khuyenMai = new KhuyenMai();
        ganDuLieu(khuyenMai, request);
        khuyenMai.setTenKm(tenKm);
        khuyenMai.setTrangThai(1);

        KhuyenMai daLuu =
                khuyenMaiRepository.saveAndFlush(khuyenMai);

        capNhatSanPhamApDung(daLuu, sanPhamList);

        return toResponse(daLuu);
    }

    @Transactional
    public KhuyenMaiResponse update(
            Integer id,
            KhuyenMaiRequest request
    ) {
        validateRequest(request);

        KhuyenMai khuyenMai = findById(id);
        String tenKm = request.getTenKm().trim();

        if (khuyenMaiRepository
                .existsByTenKmIgnoreCaseAndIdKmNot(tenKm, id)) {
            throw new RuntimeException(
                    "Tên chương trình khuyến mãi đã tồn tại"
            );
        }

        List<SanPham> sanPhamList =
                layDanhSachSanPham(request.getIdSanPham());

        ganDuLieu(khuyenMai, request);
        khuyenMai.setTenKm(tenKm);

        KhuyenMai daLuu =
                khuyenMaiRepository.saveAndFlush(khuyenMai);

        sanPhamKhuyenMaiRepository
                .deleteAllByKhuyenMai_IdKm(id);
        sanPhamKhuyenMaiRepository.flush();

        capNhatSanPhamApDung(daLuu, sanPhamList);

        return toResponse(daLuu);
    }

    @Transactional
    public KhuyenMaiResponse lock(Integer id) {
        KhuyenMai khuyenMai = findById(id);
        khuyenMai.setTrangThai(0);

        return toResponse(
                khuyenMaiRepository.saveAndFlush(khuyenMai)
        );
    }

    @Transactional
    public KhuyenMaiResponse unlock(Integer id) {
        KhuyenMai khuyenMai = findById(id);

        if (!khuyenMai.getNgayKetThuc()
                .isAfter(LocalDateTime.now())) {
            throw new RuntimeException(
                    "Không thể mở lại chương trình đã kết thúc"
            );
        }

        khuyenMai.setTrangThai(1);

        return toResponse(
                khuyenMaiRepository.saveAndFlush(khuyenMai)
        );
    }

    /**
     * Trả về CTKM tốt nhất đang áp dụng cho một sản phẩm.
     * Giá trong kết quả chỉ là giá sản phẩm gốc,
     * chưa cộng phụ thu size và topping.
     */
    @Transactional(readOnly = true)
    public KetQuaKhuyenMaiResponse
    tinhKhuyenMaiChoSanPham(Integer idSanPham) {

        SanPham sanPham = sanPhamRepository.findById(idSanPham)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy sản phẩm"
                        )
                );

        BigDecimal giaGoc = sanPham.getGia() == null
                ? BigDecimal.ZERO
                : sanPham.getGia();

        List<SanPhamKhuyenMai> danhSach =
                sanPhamKhuyenMaiRepository
                        .findKhuyenMaiDangApDung(
                                idSanPham,
                                LocalDateTime.now()
                        );

        KhuyenMai khuyenMaiTotNhat = null;
        BigDecimal tienGiamTotNhat = BigDecimal.ZERO;

        for (SanPhamKhuyenMai lienKet : danhSach) {
            KhuyenMai khuyenMai = lienKet.getKhuyenMai();

            BigDecimal tienGiam =
                    tinhTienGiam(khuyenMai, giaGoc);

            if (tienGiam.compareTo(tienGiamTotNhat) > 0) {
                khuyenMaiTotNhat = khuyenMai;
                tienGiamTotNhat = tienGiam;
            }
        }

        if (khuyenMaiTotNhat == null) {
            return new KetQuaKhuyenMaiResponse(
                    null,
                    null,
                    giaGoc,
                    BigDecimal.ZERO,
                    giaGoc
            );
        }

        return new KetQuaKhuyenMaiResponse(
                khuyenMaiTotNhat.getIdKm(),
                khuyenMaiTotNhat.getTenKm(),
                giaGoc,
                tienGiamTotNhat,
                giaGoc.subtract(tienGiamTotNhat)
        );
    }

    private void validateRequest(KhuyenMaiRequest request) {
        if (request == null) {
            throw new RuntimeException(
                    "Dữ liệu khuyến mãi không hợp lệ"
            );
        }

        if (request.getNgayBatDau() == null
                || request.getNgayKetThuc() == null) {
            throw new RuntimeException(
                    "Thời gian khuyến mãi không được để trống"
            );
        }

        if (!request.getNgayKetThuc()
                .isAfter(request.getNgayBatDau())) {
            throw new RuntimeException(
                    "Ngày kết thúc phải sau ngày bắt đầu"
            );
        }

        if (!request.getNgayKetThuc()
                .isAfter(LocalDateTime.now())) {
            throw new RuntimeException(
                    "Ngày kết thúc phải sau thời điểm hiện tại"
            );
        }

        if (request.getGiaTriGiam() == null
                || request.getGiaTriGiam()
                .compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Giá trị giảm phải lớn hơn 0"
            );
        }

        if (request.getLoaiGiam()
                == LoaiGiamKhuyenMai.PHAN_TRAM
                && request.getGiaTriGiam()
                .compareTo(new BigDecimal("100")) > 0) {
            throw new RuntimeException(
                    "Giá trị giảm phần trăm không được vượt quá 100"
            );
        }

        if (request.getIdSanPham() == null
                || request.getIdSanPham().isEmpty()) {
            throw new RuntimeException(
                    "Phải chọn ít nhất một sản phẩm"
            );
        }
    }

    private List<SanPham> layDanhSachSanPham(
            Set<Integer> idSanPhamSet
    ) {
        List<SanPham> sanPhamList =
                sanPhamRepository.findAllById(idSanPhamSet);

        Set<Integer> idTimThay = new HashSet<>();

        for (SanPham sanPham : sanPhamList) {
            idTimThay.add(sanPham.getIdSanPham());
        }

        Set<Integer> idKhongTonTai =
                new HashSet<>(idSanPhamSet);
        idKhongTonTai.removeAll(idTimThay);

        if (!idKhongTonTai.isEmpty()) {
            throw new RuntimeException(
                    "Không tìm thấy sản phẩm: "
                            + idKhongTonTai
            );
        }

        return sanPhamList;
    }

    private void ganDuLieu(
            KhuyenMai khuyenMai,
            KhuyenMaiRequest request
    ) {
        khuyenMai.setLoaiGiam(request.getLoaiGiam());
        khuyenMai.setGiaTriGiam(request.getGiaTriGiam());
        khuyenMai.setNgayBatDau(request.getNgayBatDau());
        khuyenMai.setNgayKetThuc(request.getNgayKetThuc());

        String moTa = request.getMoTa();
        khuyenMai.setMoTa(
                moTa == null || moTa.isBlank()
                        ? null
                        : moTa.trim()
        );
    }

    private void capNhatSanPhamApDung(
            KhuyenMai khuyenMai,
            List<SanPham> sanPhamList
    ) {
        List<SanPhamKhuyenMai> lienKetList =
                sanPhamList.stream()
                        .map(sanPham -> {
                            SanPhamKhuyenMai lienKet =
                                    new SanPhamKhuyenMai();

                            lienKet.setSanPham(sanPham);
                            lienKet.setKhuyenMai(khuyenMai);

                            return lienKet;
                        })
                        .toList();

        sanPhamKhuyenMaiRepository.saveAll(lienKetList);
        sanPhamKhuyenMaiRepository.flush();
    }

    private KhuyenMai findById(Integer id) {
        return khuyenMaiRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy chương trình khuyến mãi"
                        )
                );
    }

    private KhuyenMaiResponse toResponse(
            KhuyenMai khuyenMai
    ) {
        List<SanPhamKhuyenMai> lienKetList =
                sanPhamKhuyenMaiRepository
                        .findAllByKhuyenMai_IdKmOrderBySanPham_TenSanPhamAsc(
                                khuyenMai.getIdKm()
                        );

        List<SanPhamKhuyenMaiResponse> sanPhamResponses =
                lienKetList.stream()
                        .map(lienKet ->
                                toSanPhamResponse(
                                        lienKet.getSanPham(),
                                        khuyenMai
                                )
                        )
                        .toList();

        KhuyenMaiResponse response =
                new KhuyenMaiResponse();

        response.setIdKm(khuyenMai.getIdKm());
        response.setTenKm(khuyenMai.getTenKm());
        response.setLoaiGiam(khuyenMai.getLoaiGiam());
        response.setGiaTriGiam(
                khuyenMai.getGiaTriGiam()
        );
        response.setNgayBatDau(
                khuyenMai.getNgayBatDau()
        );
        response.setNgayKetThuc(
                khuyenMai.getNgayKetThuc()
        );
        response.setTrangThai(
                khuyenMai.getTrangThai()
        );
        response.setTrangThaiHienThi(
                tinhTrangThaiHienThi(khuyenMai)
        );
        response.setMoTa(khuyenMai.getMoTa());
        response.setSoLuongSanPham(
                sanPhamResponses.size()
        );
        response.setSanPhamApDung(sanPhamResponses);

        return response;
    }

    private SanPhamKhuyenMaiResponse toSanPhamResponse(
            SanPham sanPham,
            KhuyenMai khuyenMai
    ) {
        BigDecimal giaGoc = sanPham.getGia() == null
                ? BigDecimal.ZERO
                : sanPham.getGia();

        BigDecimal tienGiam =
                tinhTienGiam(khuyenMai, giaGoc);

        SanPhamKhuyenMaiResponse response =
                new SanPhamKhuyenMaiResponse();

        response.setIdSanPham(sanPham.getIdSanPham());
        response.setTenSanPham(sanPham.getTenSanPham());
        response.setGiaGoc(giaGoc);
        response.setGiaSauKhuyenMai(
                giaGoc.subtract(tienGiam)
        );
        response.setHinhAnh(sanPham.getHinhAnh());

        return response;
    }

    private BigDecimal tinhTienGiam(
            KhuyenMai khuyenMai,
            BigDecimal giaGoc
    ) {
        BigDecimal tienGiam;

        if (khuyenMai.getLoaiGiam()
                == LoaiGiamKhuyenMai.PHAN_TRAM) {
            tienGiam = giaGoc
                    .multiply(khuyenMai.getGiaTriGiam())
                    .divide(
                            new BigDecimal("100"),
                            2,
                            RoundingMode.HALF_UP
                    );
        } else {
            tienGiam = khuyenMai.getGiaTriGiam();
        }

        if (tienGiam.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        if (tienGiam.compareTo(giaGoc) > 0) {
            return giaGoc;
        }

        return tienGiam;
    }

    private TrangThaiKhuyenMai tinhTrangThaiHienThi(
            KhuyenMai khuyenMai
    ) {
        if (!Objects.equals(khuyenMai.getTrangThai(), 1)) {
            return TrangThaiKhuyenMai.NGUNG_HOAT_DONG;
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(khuyenMai.getNgayBatDau())) {
            return TrangThaiKhuyenMai.SAP_DIEN_RA;
        }

        if (now.isAfter(khuyenMai.getNgayKetThuc())) {
            return TrangThaiKhuyenMai.DA_KET_THUC;
        }

        return TrangThaiKhuyenMai.DANG_DIEN_RA;
    }
}