package com.example.datn.nhat_ky_he_thong.controller;

import com.example.datn.common.ApiResponse;
import com.example.datn.common.PageResponse;
import com.example.datn.nhat_ky_he_thong.dto.NhatKyHeThongResponse;
import com.example.datn.nhat_ky_he_thong.service.NhatKyHeThongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/nhat-ky-he-thong")
@RequiredArgsConstructor
public class NhatKyHeThongController {

    private final NhatKyHeThongService service;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String hanhDong,
            @RequestParam(required = false) String doiTuong,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("thoiGian").descending());

        LocalDateTime tuNgayTime = tuNgay != null ? tuNgay.atStartOfDay() : null;
        LocalDateTime denNgayTime = denNgay != null ? denNgay.atTime(LocalTime.MAX) : null;

        PageResponse<NhatKyHeThongResponse> data = service.getAll(
                username,
                hanhDong,
                doiTuong,
                tuNgayTime,
                denNgayTime,
                pageable
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "Lấy danh sách nhật ký thành công",
                        data
                )
        );
    }

}