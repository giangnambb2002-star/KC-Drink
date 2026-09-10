package com.example.datn.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class DashboardNgayResponse {

    private LocalDate ngay;

    private BigDecimal doanhThu;

    private long donOnline;

    private long donOffline;
}