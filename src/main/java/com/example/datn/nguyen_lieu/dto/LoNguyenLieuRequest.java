package com.example.datn.nguyen_lieu.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LoNguyenLieuRequest {

    @NotNull(message = "Vui lòng chọn nguyên liệu")
    private Integer idNguyenLieu;

    private String maLo;

    @NotNull(message = "Số lượng tồn không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Double soLuongTon;

    @NotNull(message = "Hạn sử dụng không được để trống")
    @FutureOrPresent(message = "Hạn sử dụng phải từ ngày hôm nay trở đi")
    private LocalDate hanSuDung;

    private Integer trangThai;
    private Integer idNhanVien;
}