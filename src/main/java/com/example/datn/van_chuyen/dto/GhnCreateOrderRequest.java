package com.example.datn.van_chuyen.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GhnCreateOrderRequest {

    @JsonProperty("payment_type_id")
    private Integer paymentTypeId;

    @JsonProperty("note")
    private String note;

    @JsonProperty("required_note")
    private String requiredNote;

    @JsonProperty("to_name")
    private String toName;

    @JsonProperty("to_phone")
    private String toPhone;

    @JsonProperty("to_address")
    private String toAddress;

    @JsonProperty("to_ward_code")
    private String toWardCode;

    @JsonProperty("to_district_id")
    private Integer toDistrictId;

    @JsonProperty("cod_amount")
    private Integer codAmount;

    @JsonProperty("weight")
    private Integer weight;

    @JsonProperty("length")
    private Integer length;

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("service_type_id")
    private Integer serviceTypeId;

    @JsonProperty("insurance_value")
    private Integer insuranceValue;

//test
    private java.util.List<GhnItemRequest> items;
    @JsonProperty("from_name")
    private String fromName;

    @JsonProperty("from_phone")
    private String fromPhone;

    @JsonProperty("from_address")
    private String fromAddress;

    @JsonProperty("from_ward_name")
    private String fromWardName;

    @JsonProperty("from_district_name")
    private String fromDistrictName;

    @JsonProperty("from_province_name")
    private String fromProvinceName;

    @JsonProperty("to_ward_name")
    private String toWardName;

    @JsonProperty("to_district_name")
    private String toDistrictName;

    @JsonProperty("to_province_name")
    private String toProvinceName;
}