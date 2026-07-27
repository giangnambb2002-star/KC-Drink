package com.example.datn.van_chuyen.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.van_chuyen.dto.PhiVanChuyenResponse;
import com.example.datn.van_chuyen.dto.TinhPhiRequest;
import com.example.datn.van_chuyen.service.GhnService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/van-chuyen")
@RequiredArgsConstructor
public class GhnController {

    private final GhnService service;

    @GetMapping("/districts")
    public ApiResponse<JsonNode> getDistrictsHaNoi() {

        return new ApiResponse<>(
                200,
                "Lấy danh sách quận/huyện Hà Nội thành công",
                service.getDistrictsHaNoi()
        );

    }

    @GetMapping("/wards/{districtId}")
    public ApiResponse<JsonNode> getWards(
            @PathVariable Integer districtId
    ) {

        return new ApiResponse<>(
                200,
                "Lấy danh sách phường/xã thành công",
                service.getWards(districtId)
        );

    }
    @PostMapping("/fee")
    public ApiResponse<PhiVanChuyenResponse> calculateFee(
            @Valid @RequestBody TinhPhiRequest request
    ) {

        // Giá trị mặc định cho đơn đồ uống
        request.setWeight(1000);
        request.setLength(20);
        request.setWidth(20);
        request.setHeight(20);

        if (request.getInsuranceValue() == null) {
            request.setInsuranceValue(0);
        }

        return new ApiResponse<>(
                200,
                "Tính phí vận chuyển thành công",
                service.calculateFee(request)
        );
    }
    @PostMapping("/lead-time")
    public ApiResponse<JsonNode> calculateLeadTime(
            @Valid @RequestBody TinhPhiRequest request
    ) {
        return new ApiResponse<>(
                200,
                "Lấy thời gian giao dự kiến thành công",
                service.calculateLeadTime(
                        request.getDistrictId(),
                        request.getWardCode()
                )
        );
    }
}