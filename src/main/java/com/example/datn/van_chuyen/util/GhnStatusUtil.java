package com.example.datn.van_chuyen.util;

public class GhnStatusUtil {

    private GhnStatusUtil() {
    }

    public static String toVietnamese(String status) {
        if (status == null || status.isBlank()) {
            return "Không xác định";
        }

        return switch (status) {
            case "ready_to_pick" -> "Chờ lấy hàng";
            case "picking" -> "Đang lấy hàng";
            case "money_collect_picking" -> "Đang thu tiền người gửi";
            case "picked" -> "Đã lấy hàng";
            case "storing" -> "Đang lưu kho";
            case "transporting" -> "Đang vận chuyển";
            case "sorting" -> "Đang phân loại";
            case "delivering" -> "Đang giao hàng";
            case "money_collect_delivering" -> "Đang thu tiền người nhận";
            case "delivered" -> "Giao hàng thành công";
            case "delivery_fail" -> "Giao hàng thất bại";
            case "waiting_to_return" -> "Chờ trả hàng";
            case "return" -> "Đang trả hàng";
            case "return_transporting" -> "Đang vận chuyển trả hàng";
            case "return_sorting" -> "Đang phân loại trả hàng";
            case "returning" -> "Đang hoàn hàng";
            case "return_fail" -> "Hoàn hàng thất bại";
            case "returned" -> "Đã hoàn hàng";
            case "cancel" -> "Đã hủy";
            case "exception" -> "Đơn hàng ngoại lệ";
            case "damage" -> "Hàng bị hư hỏng";
            case "lost" -> "Hàng bị thất lạc";
            default -> status;
        };
    }
}