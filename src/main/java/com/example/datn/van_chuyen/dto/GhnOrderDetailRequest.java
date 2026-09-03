package com.example.datn.van_chuyen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GhnOrderDetailRequest {

    @JsonProperty("order_code")
    private String orderCode;
}