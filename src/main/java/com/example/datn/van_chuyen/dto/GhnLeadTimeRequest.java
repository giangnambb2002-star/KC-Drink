package com.example.datn.van_chuyen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GhnLeadTimeRequest {

    @JsonProperty("from_district_id")
    private Integer fromDistrictId;

    @JsonProperty("to_district_id")
    private Integer toDistrictId;

    @JsonProperty("to_ward_code")
    private String toWardCode;

    @JsonProperty("service_id")
    private Integer serviceId;
}