package com.example.datn.dashboard.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.dashboard.dto.DashboardResponse;
import com.example.datn.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<?> getDashboard() {
        DashboardResponse data = dashboardService.getDashboard();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Lấy dữ liệu dashboard thành công",
                        data
                )
        );
    }
}