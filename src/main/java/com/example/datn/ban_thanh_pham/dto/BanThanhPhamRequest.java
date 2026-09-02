package com.example.datn.ban_thanh_pham.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BanThanhPhamRequest {

    @NotBlank(message = "Tên bán thành phẩm không được để trống")
    @Size(max = 100, message = "Tên bán thành phẩm tối đa 100 ký tự")
    private String tenBanThanhPham;

    @NotBlank(message = "Đơn vị tính không được để trống")
    @Size(max = 20, message = "Đơn vị tính tối đa 20 ký tự")
    private String donViTinh;

    @Min(value = 1, message = "Hạn sử dụng phải lớn hơn 0 giờ")
    private Integer hanSuDungGio;

//    @NotNull(message = "Trạng thái không được để trống")
//    private Integer trangThai;
}