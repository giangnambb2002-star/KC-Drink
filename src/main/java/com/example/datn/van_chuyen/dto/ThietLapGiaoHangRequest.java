package com.example.datn.van_chuyen.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThietLapGiaoHangRequest {
    @NotNull(message = "Vui lòng chọn địa chỉ giao hàng")
    private Integer idDiaChi;
    private String ghiChu;
}