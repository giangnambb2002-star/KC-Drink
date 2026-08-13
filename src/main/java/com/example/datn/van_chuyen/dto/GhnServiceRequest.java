package com.example.datn.van_chuyen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GhnServiceRequest {

    @JsonProperty("shop_id")
    private Integer shopId;

    @JsonProperty("from_district")
    private Integer fromDistrict;

    @JsonProperty("to_district")
    private Integer toDistrict;
}