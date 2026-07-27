package com.example.datn.van_chuyen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GhnFeeRequest {

    @JsonProperty("service_type_id")
    private Integer serviceTypeId;

    @JsonProperty("to_district_id")
    private Integer toDistrictId;

    @JsonProperty("to_ward_code")
    private String toWardCode;

    private Integer weight;

    private Integer length;

    private Integer width;

    private Integer height;

    @JsonProperty("insurance_value")
    private Integer insuranceValue;
}