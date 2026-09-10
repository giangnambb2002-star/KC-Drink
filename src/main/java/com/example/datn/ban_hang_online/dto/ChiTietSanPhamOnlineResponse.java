package com.example.datn.ban_hang_online.dto;

import com.example.datn.san_pham.dto.SanPhamResponse;
import com.example.datn.san_pham.dto.SanPhamSizeResponse;
import com.example.datn.topping.dto.ToppingResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietSanPhamOnlineResponse {

    private SanPhamResponse sanPham;

    private List<SanPhamSizeResponse> sizes;

    private List<ToppingResponse> toppings;
}