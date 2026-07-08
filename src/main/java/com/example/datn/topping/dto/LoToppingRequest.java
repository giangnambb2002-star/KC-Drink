package com.example.datn.topping.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LoToppingRequest {

    @NotNull(message = "Vui lòng chọn Topping")
    private Integer idTopping;

    private String maLo;

    @NotNull(message = "Số lượng nhập không được để trống")
    @Min(value = 1, message = "Số lượng nhập phải lớn hơn 0")
    private Integer soLuongNhap;

    @NotNull(message = "Hạn sử dụng không được để trống")
    @FutureOrPresent(message = "Hạn sử dụng phải từ ngày hôm nay trở đi")
    private LocalDate hanSuDung;

    private Integer trangThai;

    @NotNull(message = "Không tìm thấy người nhập kho")
    private Integer idNhanVien;
}