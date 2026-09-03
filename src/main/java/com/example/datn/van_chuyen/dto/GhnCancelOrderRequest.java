package com.example.datn.van_chuyen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GhnCancelOrderRequest {

    @JsonProperty("order_codes")
    private List<String> orderCodes;
}