package com.example.datn.van_chuyen.service;

import com.example.datn.van_chuyen.client.GhnClient;
import com.example.datn.van_chuyen.dto.GhnFeeRequest;
import com.example.datn.van_chuyen.dto.GhnLeadTimeRequest;
import com.example.datn.van_chuyen.dto.PhiVanChuyenResponse;
import com.example.datn.van_chuyen.dto.TinhPhiRequest;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GhnService {

    private static final Integer HA_NOI_PROVINCE_ID = 201;

    private static final Integer SERVICE_TYPE_ID = 2;

    private static final Integer FROM_DISTRICT_ID = 1482;

    private final GhnClient ghnClient;

    public JsonNode getDistrictsHaNoi() {

        return ghnClient.getDistricts(
                HA_NOI_PROVINCE_ID
        );

    }

    public JsonNode getWards(Integer districtId) {

        if (districtId == null || districtId <= 0) {

            throw new RuntimeException(
                    "Mã quận/huyện không hợp lệ"
            );

        }

        return ghnClient.getWards(districtId);

    }

    public PhiVanChuyenResponse calculateFee(
            TinhPhiRequest request
    ) {

        GhnFeeRequest ghnRequest =
                new GhnFeeRequest();

        ghnRequest.setServiceTypeId(2);

        ghnRequest.setToDistrictId(
                request.getDistrictId()
        );

        ghnRequest.setToWardCode(
                request.getWardCode()
        );

        ghnRequest.setWeight(
                request.getWeight()
        );

        ghnRequest.setLength(
                request.getLength()
        );

        ghnRequest.setWidth(
                request.getWidth()
        );

        ghnRequest.setHeight(
                request.getHeight()
        );

        ghnRequest.setInsuranceValue(
                request.getInsuranceValue() == null
                        ? 0
                        : request.getInsuranceValue()
        );

        JsonNode json =
                ghnClient.calculateFee(ghnRequest);

        JsonNode fee =
                json.get("data");

        PhiVanChuyenResponse response =
                new PhiVanChuyenResponse();

        response.setPhiVanChuyen(
                fee.get("total").asInt()
        );

        response.setPhiDichVu(
                fee.get("service_fee").asInt()
        );

        response.setPhiBaoHiem(
                fee.get("insurance_fee").asInt()
        );

        return response;

    }
    public JsonNode calculateLeadTime(
            Integer districtId,
            String wardCode
    ) {

        GhnLeadTimeRequest request =
                new GhnLeadTimeRequest();

        request.setFromDistrictId(
                FROM_DISTRICT_ID
        );

        request.setToDistrictId(
                districtId
        );

        request.setToWardCode(
                wardCode
        );

        request.setServiceId(
                SERVICE_TYPE_ID
        );

        JsonNode json =
                ghnClient.calculateLeadTime(request);

        return json.get("data");

    }
}