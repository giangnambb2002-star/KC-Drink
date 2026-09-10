package com.example.datn.ban_hang_online.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrangThaiThanhToanOnlineResponse {

    private Integer idHoaDon;

    private String maHoaDon;

    private String trangThaiDonHang;

    private Integer doUuTien;

    private String maLyDoCho;

    private String lyDoCho;

    private Long payosOrderCode;

    private Long amount;

    private String payosStatus;

    /*
     * true khi hệ thống đã hoàn tất:
     * - trừ kho
     * - trừ voucher
     * - cập nhật hóa đơn
     */
    private Boolean daThanhToan;

    private String trangThaiVanDon;

    private String maVanDonGhn;

    private String trangThaiGhn;

    private LocalDateTime payosExpiresAt;
}