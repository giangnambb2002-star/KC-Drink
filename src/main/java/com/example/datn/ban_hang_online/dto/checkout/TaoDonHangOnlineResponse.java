package com.example.datn.ban_hang_online.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoDonHangOnlineResponse {

    /*
     * true:
     * request này vừa tạo đơn mới.
     *
     * false:
     * clientRequestId đã tồn tại,
     * backend trả lại đơn đã tạo trước đó.
     */
    private Boolean taoMoi;

    private Integer idHoaDon;

    private String maHoaDon;

    private String clientRequestId;

    private String loaiHoaDon;

    private LocalDateTime ngayTao;

    private String trangThai;

    private String hinhThucThanhToan;

    private Integer idKhachHang;

    private Integer idVoucher;

    private String maVoucher;

    private BigDecimal tongTien;

    private BigDecimal giamGia;

    private BigDecimal phiVanChuyen;

    private BigDecimal thanhTien;

    private Integer idDiaChi;

    private String tenNguoiNhan;

    private String sdtNguoiNhan;

    private String diaChiGiaoHang;

    private String ghiChu;

    /*
     * PayOS hiện tại sẽ null.
     * Để sẵn field để bước PayOS sau không cần đổi DTO này.
     */
    private Long payosOrderCode;

    private String payosPaymentLinkId;

    private String payosStatus;

    /*
     * GHN hiện tại mới chỉ snapshot thông tin giao hàng.
     * Chưa tạo đơn thật nên maVanDonGhn = null.
     */
    private String trangThaiVanDon;

    private String maVanDonGhn;

    private String trangThaiGhn;
}