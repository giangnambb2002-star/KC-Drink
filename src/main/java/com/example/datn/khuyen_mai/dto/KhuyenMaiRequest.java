package com.example.datn.khuyen_mai.dto;

import com.example.datn.khuyen_mai.enums.LoaiGiamKhuyenMai;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class KhuyenMaiRequest {

    @NotBlank(message = "Tên chương trình khuyến mãi không được để trống")
    @Size(max = 100, message = "Tên chương trình tối đa 100 ký tự")
    private String tenKm;

    @NotNull(message = "Loại giảm không được để trống")
    private LoaiGiamKhuyenMai loaiGiam;

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(
            value = "0.01",
            message = "Giá trị giảm phải lớn hơn 0"
    )
    private BigDecimal giaTriGiam;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime ngayKetThuc;

    @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
    private String moTa;

    @NotEmpty(message = "Phải chọn ít nhất một sản phẩm")
    private Set<@NotNull Integer> idSanPham;
}