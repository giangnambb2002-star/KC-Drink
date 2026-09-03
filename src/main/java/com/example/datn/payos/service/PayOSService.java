package com.example.datn.payos.service;
import com.example.datn.payos.dto.PayOSCreateRequest;
import com.example.datn.payos.dto.PayOSCreateResponse;
import com.example.datn.payos.dto.PayOSPaymentStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import java.time.Instant;
@Service
@RequiredArgsConstructor
public class PayOSService {
    private final PayOS payOS;
    private static final long QR_EXPIRATION_SECONDS = 10 * 60;
    public PayOSCreateResponse createPayment(PayOSCreateRequest request) {
        return createPayment(request.getAmount(), request.getDescription());
    }
    public PayOSCreateResponse createPayment(Long amount, String description) {
        try {
            if (amount == null || amount <= 0) {
                throw new RuntimeException("Số tiền thanh toán không hợp lệ");
            }
            long orderCode = System.currentTimeMillis();
            CreatePaymentLinkRequest paymentRequest =
                    CreatePaymentLinkRequest.builder()
                            .orderCode(orderCode)
                            .amount(amount)
                            .description(description)
                            .cancelUrl("http://localhost:5173/payos/cancel")
                            .returnUrl("http://localhost:5173/payos/success")
                            .expiredAt(
                                    Instant.now()
                                            .plusSeconds(QR_EXPIRATION_SECONDS)
                                            .getEpochSecond()
                            )
                            .build();
            var paymentLink = payOS.paymentRequests().create(paymentRequest);
            return new PayOSCreateResponse(
                    orderCode,
                    amount,
                    description,
                    paymentLink.getCheckoutUrl(),
                    paymentLink.getQrCode(),
                    paymentLink.getPaymentLinkId(),
                    paymentLink.getStatus().toString()
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Không thể tạo link thanh toán PayOS: " + e.getMessage()
            );
        }
    }
    public Object getPaymentInfo(Long orderCode) {
        try {
            return payOS.paymentRequests().get(orderCode);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Không thể lấy trạng thái thanh toán PayOS: " + e.getMessage()
            );
        }
    }
    public PayOSPaymentStatusResponse getPaymentStatusInfo(Long orderCode) {
        try {
            var paymentInfo = payOS.paymentRequests().get(orderCode);
            return new PayOSPaymentStatusResponse(
                    paymentInfo.getOrderCode(),
                    paymentInfo.getAmount(),
                    paymentInfo.getStatus().toString()
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Không thể kiểm tra trạng thái thanh toán PayOS: " + e.getMessage()
            );
        }
    }
    public PayOSPaymentStatusResponse cancelPayment(Long orderCode, String cancellationReason) {
        try {
            if (orderCode == null) {
                throw new RuntimeException("Mã giao dịch PayOS không hợp lệ");
            }
            var paymentInfo = payOS.paymentRequests().cancel(orderCode, cancellationReason);
            return new PayOSPaymentStatusResponse(
                    paymentInfo.getOrderCode(),
                    paymentInfo.getAmount(),
                    paymentInfo.getStatus().toString()
            );
        } catch (Exception e) {
            throw new RuntimeException("Không thể hủy thanh toán PayOS: " + e.getMessage());
        }
    }
    public String getPaymentStatus(Long orderCode) {
        return getPaymentStatusInfo(orderCode).getStatus();
    }

}