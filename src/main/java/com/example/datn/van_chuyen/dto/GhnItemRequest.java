package com.example.datn.van_chuyen.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GhnItemRequest {

    private String name;
    private Integer quantity;
    private Integer price;
}

//test khi chưa có sản phẩm thâth