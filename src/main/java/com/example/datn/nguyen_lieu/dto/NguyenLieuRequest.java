package com.example.datn.nguyen_lieu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NguyenLieuRequest {

    @NotBlank(message = "Tên nguyên liệu không được để trống")
    @Size(max = 100, message = "Tên nguyên liệu tối đa 100 ký tự")
    private String tenNguyenLieu;

    @NotBlank(message = "Đơn vị tính không được để trống")
    @Size(max = 50, message = "Đơn vị tính tối đa 50 ký tự")
    private String donViTinh;

    // Để null khi tạo mới (mặc định là 1), truyền vào khi cập nhật trạng thái (0: Khóa, 1: Mở)
    private Integer trangThai;

    @NotNull(message = "Ngưỡng tồn kho không được để trống")
    @PositiveOrZero(message = "Ngưỡng tồn kho phải lớn hơn hoặc bằng 0")
    private BigDecimal nguongTonKho;
}