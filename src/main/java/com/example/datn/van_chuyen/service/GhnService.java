package com.example.datn.van_chuyen.service;

import com.example.datn.van_chuyen.client.GhnClient;
import com.example.datn.van_chuyen.dto.GhnCreateOrderRequest;
import com.example.datn.van_chuyen.dto.GhnFeeRequest;
import com.example.datn.van_chuyen.dto.GhnItemRequest;
import com.example.datn.van_chuyen.dto.GhnLeadTimeRequest;
import com.example.datn.van_chuyen.dto.PhiVanChuyenResponse;
import com.example.datn.van_chuyen.dto.TaoDonGhnRequest;
import com.example.datn.van_chuyen.dto.TinhPhiRequest;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GhnService {

    private static final Integer HA_NOI_PROVINCE_ID = 201;
    private static final Integer SERVICE_TYPE_ID = 2;
    private static final Integer FROM_DISTRICT_ID = 1492;

    private final GhnClient ghnClient;

    public JsonNode getDistrictsHaNoi() {
        return ghnClient.getDistricts(HA_NOI_PROVINCE_ID);
    }

    public JsonNode getWards(Integer districtId) {
        if (districtId == null || districtId <= 0) {
            throw new RuntimeException("Mã quận/huyện không hợp lệ");
        }
        return ghnClient.getWards(districtId);
    }

    public PhiVanChuyenResponse calculateFee(TinhPhiRequest request) {
        GhnFeeRequest ghnRequest = new GhnFeeRequest();
        ghnRequest.setServiceTypeId(2);
        ghnRequest.setToDistrictId(request.getDistrictId());
        ghnRequest.setToWardCode(request.getWardCode());
        ghnRequest.setWeight(request.getWeight());
        ghnRequest.setLength(request.getLength());
        ghnRequest.setWidth(request.getWidth());
        ghnRequest.setHeight(request.getHeight());
        ghnRequest.setInsuranceValue(
                request.getInsuranceValue() == null ? 0 : request.getInsuranceValue()
        );

        JsonNode json = ghnClient.calculateFee(ghnRequest);
        JsonNode fee = json.get("data");

        PhiVanChuyenResponse response = new PhiVanChuyenResponse();
        response.setPhiVanChuyen(fee.get("total").asInt());
        response.setPhiDichVu(fee.get("service_fee").asInt());
        response.setPhiBaoHiem(fee.get("insurance_fee").asInt());

        return response;
    }

    public JsonNode calculateLeadTime(Integer districtId, String wardCode) {
        JsonNode services = ghnClient.getAvailableServices(
                FROM_DISTRICT_ID,
                districtId
        ).get("data");

        if (services == null || !services.isArray() || services.isEmpty()) {
            throw new RuntimeException("GHN không có dịch vụ giao hàng cho tuyến này");
        }

        Integer serviceId = null;

        for (JsonNode service : services) {
            if (service.get("service_type_id").asInt() == SERVICE_TYPE_ID) {
                serviceId = service.get("service_id").asInt();
                break;
            }
        }

        if (serviceId == null) {
            serviceId = services.get(0).get("service_id").asInt();
        }

        GhnLeadTimeRequest request = new GhnLeadTimeRequest();
        request.setFromDistrictId(FROM_DISTRICT_ID);
        request.setToDistrictId(districtId);
        request.setToWardCode(wardCode);
        request.setServiceId(serviceId);

        JsonNode json = ghnClient.calculateLeadTime(request);
        return json.get("data");
    }

    private GhnCreateOrderRequest buildCreateOrderRequest(TaoDonGhnRequest request) {
        GhnCreateOrderRequest ghnRequest = new GhnCreateOrderRequest();
        ghnRequest.setPaymentTypeId(1);
        ghnRequest.setRequiredNote("KHONGCHOXEMHANG");

        ghnRequest.setToName(request.getToName());
        ghnRequest.setToPhone(request.getToPhone());
        ghnRequest.setToAddress(request.getToAddress());
        ghnRequest.setToDistrictId(request.getToDistrictId());
        ghnRequest.setToWardCode(request.getToWardCode());
        ghnRequest.setToWardName(request.getToWardName());
        ghnRequest.setToDistrictName(request.getToDistrictName());
        ghnRequest.setToProvinceName(request.getToProvinceName());

        ghnRequest.setCodAmount(request.getCodAmount() == null ? 0 : request.getCodAmount());
        ghnRequest.setInsuranceValue(
                request.getInsuranceValue() == null ? 0 : request.getInsuranceValue()
        );
        ghnRequest.setNote(request.getNote());

        ghnRequest.setWeight(1000);
        ghnRequest.setLength(20);
        ghnRequest.setWidth(20);
        ghnRequest.setHeight(20);
        ghnRequest.setServiceTypeId(SERVICE_TYPE_ID);

        GhnItemRequest item = new GhnItemRequest(
                "Đồ uống KC Drink",
                1,
                request.getCodAmount() == null ? 0 : request.getCodAmount()
        );
        ghnRequest.setItems(java.util.List.of(item));

        return ghnRequest;
    }

    public JsonNode createOrder(TaoDonGhnRequest request) {
        GhnCreateOrderRequest ghnRequest = buildCreateOrderRequest(request);
        JsonNode json = ghnClient.createOrder(ghnRequest);
        return json.get("data");
    }

    public JsonNode previewOrder(TaoDonGhnRequest request) {
        GhnCreateOrderRequest ghnRequest = buildCreateOrderRequest(request);
        JsonNode json = ghnClient.previewOrder(ghnRequest);
        return json.get("data");
    }

    public JsonNode getAvailableServices(Integer districtId) {
        JsonNode json = ghnClient.getAvailableServices(
                FROM_DISTRICT_ID,
                districtId
        );
        return json.get("data");
    }

    public JsonNode getOrderDetail(String orderCode) {
        if (orderCode == null || orderCode.isBlank()) {
            throw new RuntimeException("Mã vận đơn GHN không được để trống");
        }

        JsonNode json = ghnClient.getOrderDetail(orderCode.trim());
        return json.get("data");
    }
}