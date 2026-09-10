package com.example.datn.ban_hang_online.dto.checkout;

import lombok.Data;

@Data
public class ToppingGioHangRequest {

    private Integer idTopping;

    /**
     * Số PHẦN topping của cả dòng món.
     *
     * Ví dụ:
     * - 2 ly Matcha
     * - Thạch dừa x1
     *
     * => chỉ tính 1 phần thạch dừa,
     * KHÔNG nhân topping theo số lượng ly.
     */
    private Integer soLuong;
}