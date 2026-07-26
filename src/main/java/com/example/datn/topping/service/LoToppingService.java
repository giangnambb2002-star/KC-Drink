package com.example.datn.topping.service;

import com.example.datn.common.PageResponse;
import com.example.datn.topping.dto.LoToppingRequest;
import com.example.datn.topping.dto.LoToppingResponse;
import com.example.datn.topping.entity.LoTopping;
import com.example.datn.topping.entity.Topping;
import com.example.datn.topping.repository.LoToppingRepository;
import com.example.datn.topping.repository.ToppingRepository;
import com.example.datn.nhan_vien.entity.NhanVien;
import com.example.datn.nhan_vien.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import com.example.datn.common.ExcelHelper;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoToppingService {

    private final LoToppingRepository repository;
    private final ToppingRepository toppingRepository;
    private final NhanVienRepository nhanVienRepository;

    

    public PageResponse<LoToppingResponse> getAll(
            Integer idTopping, Integer trangThai, int page, int size, String sortBy, String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LoTopping> pageData = repository.searchLoTopping(idTopping, trangThai, pageable);

        return new PageResponse<>(
                pageData.getContent().stream().map(this::toResponse).toList(),
                pageData.getNumber(), pageData.getSize(), pageData.getTotalElements(),
                pageData.getTotalPages(), pageData.isLast()
        );
    }

    @Transactional // Đảm bảo đồng bộ: Lỗi 1 cái là rollback cả 2 bảng
    public LoToppingResponse create(LoToppingRequest request) {
        // 1. Check Topping & Nhân viên
        Topping topping = toppingRepository.findById(request.getIdTopping())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Topping gốc"));

        NhanVien nhanVien = nhanVienRepository.findById(request.getIdNhanVien())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên nhập kho"));

        // 2. Tạo lô mới
        LoTopping lo = new LoTopping();
        lo.setTopping(topping);
        lo.setMaLo(request.getMaLo());
        lo.setNhanVien(nhanVien);
        lo.setSoLuongNhap(request.getSoLuongNhap());
        lo.setSoLuongTon(request.getSoLuongNhap()); // Mới nhập thì Tồn = Nhập
        lo.setHanSuDung(request.getHanSuDung());
        lo.setNgayNhap(LocalDateTime.now());
        lo.setTrangThai(1);

        LoTopping savedLo = repository.save(lo);

        // 3. LOGIC QUAN TRỌNG: Cộng dồn vào bảng Topping gốc
        int currentTonKho = topping.getTongTonKho() != null ? topping.getTongTonKho() : 0;
        topping.setTongTonKho(currentTonKho + request.getSoLuongNhap());
        toppingRepository.save(topping);

        return toResponse(savedLo);
    }

    @Transactional
    public LoToppingResponse lock(Integer id) {
        LoTopping lo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lô Topping với ID: " + id));

        // Kiểm tra nếu lô đã bị khóa trước đó thì không trừ trùng lặp
        if (lo.getTrangThai() != null && lo.getTrangThai() == 0) {
            return toResponse(lo);
        }

        // 1. Đổi trạng thái lô về 0 (Đã khóa)
        lo.setTrangThai(0);
        LoTopping savedLo = repository.save(lo);

        // 2. Trừ số lượng tồn của lô này khỏi Tổng tồn kho Topping gốc
        Topping topping = lo.getTopping();
        if (topping != null) {
            int currentTon = topping.getTongTonKho() != null ? topping.getTongTonKho() : 0;
            int loTon = lo.getSoLuongTon() != null ? lo.getSoLuongTon() : 0;

            topping.setTongTonKho(Math.max(0, currentTon - loTon)); // Đảm bảo không âm
            toppingRepository.save(topping);
        }

        return toResponse(savedLo);
    }

    // 🔓 MỞ KHÓA LÔ TOPPING (Cộng trả lại tổng tồn kho)
    @Transactional
    public LoToppingResponse unlock(Integer id) {
        LoTopping lo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lô Topping với ID: " + id));

        // Kiểm tra nếu lô đã ở trạng thái mở khóa thì không cộng trùng lặp
        if (lo.getTrangThai() != null && lo.getTrangThai() == 1) {
            return toResponse(lo);
        }

        // 1. Đổi trạng thái lô về 1 (Khả dụng)
        lo.setTrangThai(1);
        LoTopping savedLo = repository.save(lo);

        // 2. Cộng trả lại số lượng tồn của lô này vào Tổng tồn kho Topping gốc
        Topping topping = lo.getTopping();
        if (topping != null) {
            int currentTon = topping.getTongTonKho() != null ? topping.getTongTonKho() : 0;
            int loTon = lo.getSoLuongTon() != null ? lo.getSoLuongTon() : 0;

            topping.setTongTonKho(currentTon + loTon);
            toppingRepository.save(topping);
        }

        return toResponse(savedLo);
    }

    private LoToppingResponse toResponse(LoTopping lo) {
        LoToppingResponse response = new LoToppingResponse();
        response.setIdLoTopping(lo.getIdLoTopping());
        response.setMaLo(lo.getMaLo());
        response.setIdTopping(lo.getTopping().getIdTopping());
        response.setTenTopping(lo.getTopping().getTenTopping());
        response.setSoLuongNhap(lo.getSoLuongNhap());
        response.setSoLuongTon(lo.getSoLuongTon());
        response.setHanSuDung(lo.getHanSuDung());
        response.setNgayNhap(lo.getNgayNhap());
        response.setTrangThai(lo.getTrangThai());

        if (lo.getNhanVien() != null) {
            response.setTenNhanVien(lo.getNhanVien().getTenNhanVien());
        }

        return response;
    }

    // 📥 IMPORT BATCH LÔ TOPPING TỪ FILE EXCEL (CÓ CỘNG DỒN NẾU TRÙNG MÃ LÔ)
    @Transactional(rollbackFor = Exception.class)
    public void importExcelLoTopping(MultipartFile file, Integer idNhanVien) {
        // 1. Kiểm tra nhân viên thực hiện Import (nếu có)
        NhanVien nhanVien = null;
        if (idNhanVien != null) {
            nhanVien = nhanVienRepository.findById(idNhanVien)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên thực hiện import!"));
        }
        final NhanVien nvImport = nhanVien;

        // 2. Đọc dữ liệu từ file Excel thông qua ExcelHelper
        List<LoTopping> dtoList = ExcelHelper.readExcel(file, row -> {
            LoTopping lo = new LoTopping();

            // Cột 0: Mã lô (String)
            lo.setMaLo(ExcelHelper.getStringValue(row.getCell(0)));

            // Cột 1: ID Topping gốc (Gán tạm ID vào entity Topping)
            Integer idTopping = ExcelHelper.getIntegerValue(row.getCell(1));
            Topping tempTopping = new Topping();
            tempTopping.setIdTopping(idTopping);
            lo.setTopping(tempTopping);

            // Cột 2: Số lượng nhập
            Integer soLuong = ExcelHelper.getIntegerValue(row.getCell(2));
            lo.setSoLuongNhap(soLuong);
            lo.setSoLuongTon(soLuong); // Lô mới nhập: Tồn = Nhập

            // Cột 3: Hạn sử dụng (LocalDate)
            lo.setHanSuDung(ExcelHelper.getLocalDateValue(row.getCell(3)));

            // Set các thuộc tính mặc định
            lo.setNgayNhap(LocalDateTime.now());
            lo.setTrangThai(1); // 1: Khả dụng
            lo.setNhanVien(nvImport);

            return lo;
        });
        // 3. Kiểm tra nghiệp vụ & Lưu/Cập nhật vào DB
        // 3. Kiểm tra nghiệp vụ & Lưu/Cập nhật vào DB
        for (LoTopping lo : dtoList) {
            // Validate: Bỏ qua dòng trống nếu không nhập mã lô
            if (lo.getMaLo() == null || lo.getMaLo().trim().isEmpty()) {
                continue;
            }

            // Check Topping gốc có tồn tại không
            Integer idTopping = lo.getTopping().getIdTopping();
            Topping topping = toppingRepository.findById(idTopping)
                    .orElseThrow(() -> new RuntimeException("Lỗi file Excel: Không tìm thấy Topping gốc có ID = " + idTopping));

            // 🔄 XỬ LÝ: KIỂM TRA TRÙNG MÃ LÔ
            Optional<LoTopping> existingLoOpt = repository.findByMaLo(lo.getMaLo());

            if (existingLoOpt.isPresent()) {
                // 👉 TH 1: ĐÃ TỒN TẠI MÃ LÔ -> BỎ QUA (KHÔNG CỘNG DỒN, KHÔNG CẬP NHẬT GÌ CẢ)
                LoTopping existingLo = existingLoOpt.get();

                // Kiểm tra an toàn: Báo lỗi nếu mã lô trùng nhưng thuộc Topping khác
                if (!existingLo.getTopping().getIdTopping().equals(idTopping)) {
                    throw new RuntimeException("Lỗi file Excel: Mã lô [" + lo.getMaLo() + "] đã tồn tại nhưng thuộc về Topping khác!");
                }

                // ⛔ Bỏ qua dòng này, chuyển sang dòng tiếp theo luôn!
                continue;

            } else {
                // 👉 TH 2: CHƯA TỒN TẠI MÃ LÔ -> THÊM LÔ MỚI
                lo.setTopping(topping);
                repository.save(lo);

                // ➕ CHỈ CỘNG DỒN VÀO TỔNG TỒN KHO CỦA TOPPING KHI LÀ LÔ MỚI
                int currentTonKho = topping.getTongTonKho() != null ? topping.getTongTonKho() : 0;
                int importSoLuong = lo.getSoLuongNhap() != null ? lo.getSoLuongNhap() : 0;
                topping.setTongTonKho(currentTonKho + importSoLuong);
                toppingRepository.save(topping);
            }
        }
    }
}