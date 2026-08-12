package com.example.datn.voucher.service;

import com.example.datn.khach_hang.entity.KhachHang;
import com.example.datn.khach_hang.repository.KhachHangRepository;
import com.example.datn.nhat_ky_he_thong.service.NhatKyHeThongService;
import com.example.datn.voucher.entity.Voucher;
import com.example.datn.voucher.repository.VoucherRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VoucherMailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private VoucherRepository voucherRepository; // Nhớ tạo interface JpaRepository cho Voucher nhé

    @Autowired
    private NhatKyHeThongService nhatKyHeThongService;

    @Transactional
    public void tangVoucherSinhNhat() {
        int currentMonth = LocalDate.now().getMonthValue();
        int currentDay = LocalDate.now().getDayOfMonth();
        int currentYear = LocalDate.now().getYear();
        int soLuongDaTang = 0;

        List<KhachHang> danhSachSinhNhat =
                khachHangRepository.findKhachHangSinhNhat(currentDay, currentMonth);

        for (KhachHang kh : danhSachSinhNhat) {
            if (kh.getNamNhanVoucherSn() == null || kh.getNamNhanVoucherSn() < currentYear) {

                String maCode = "SN" + currentYear + "-"
                        + UUID.randomUUID()
                        .toString()
                        .substring(0, 5)
                        .toUpperCase();

                Voucher v = new Voucher();
                v.setIdKhachHang(kh.getIdKhachHang());
                v.setMaVoucher(maCode);
                v.setTenVoucher("Quà tặng sinh nhật " + currentYear);
                v.setLoaiVoucher("PERCENT");
                v.setGiaTriGiam(new BigDecimal("30"));
                v.setGiamToiDa(new BigDecimal("100000"));
                v.setDieuKien(BigDecimal.ZERO);
                v.setNgayBatDau(LocalDateTime.now());
                v.setNgayKetThuc(LocalDateTime.now().plusDays(7));
                v.setSoLuong(1);
                v.setTrangThai(1);

                voucherRepository.save(v);
                soLuongDaTang++;

                kh.setNamNhanVoucherSn(currentYear);
                khachHangRepository.save(kh);

                guiMailHtml(
                        kh.getEmail(),
                        kh.getTenKhachHang(),
                        maCode
                );
            }
        }

        if (soLuongDaTang > 0) {
            nhatKyHeThongService.ghiLogHeThong(
                    "TẶNG SINH NHẬT",
                    "VOUCHER",
                    null,
                    "Hệ thống đã tự động tạo "
                            + soLuongDaTang
                            + " voucher sinh nhật cho khách hàng"
            );
        }
    }
    private void guiMailHtml(String email, String tenKh, String maCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("🎉 Chúc Mừng Sinh Nhật! Tặng bạn mã giảm giá 30%");

            String htmlMsg = "<h3>Chào " + tenKh + ",</h3>"
                    + "<p>Chúc bạn một sinh nhật thật vui vẻ và hạnh phúc!</p>"
                    + "<p>Chúng mình tặng bạn 1 mã Voucher giảm <b>30%</b> (tối đa 100.000đ) khi đặt hàng.</p>"
                    + "<p>Mã của bạn là: <b style='color:red; font-size:18px'>" + maCode + "</b></p>"
                    + "<p><i>Mã có hạn sử dụng trong 7 ngày nhé.</i></p>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.out.println("Lỗi gửi mail sinh nhật: " + e.getMessage());
        }
    }

    // ================ THÊM HÀM MỚI NÀY VÀO DƯỚI CÙNG ================
    public void guiMailTangVoucherRieng(String email, String tenKh, Voucher voucher) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("🎁 KC Drink tặng riêng bạn một mã giảm giá đặc biệt!");

            // Format hiển thị mức giảm (30% hoặc 50,000đ)
            String mucGiam = "PERCENT".equals(voucher.getLoaiVoucher())
                    ? voucher.getGiaTriGiam() + "%"
                    : String.format("%,.0f", voucher.getGiaTriGiam()) + "đ";

            String htmlMsg = "<h3>Chào " + tenKh + ",</h3>"
                    + "<p>KC Drink vừa tặng riêng cho bạn một mã giảm giá siêu xịn!</p>"
                    + "<p>Mã của bạn là: <b style='color:#1890ff; font-size:22px'>" + voucher.getMaVoucher() + "</b></p>"
                    + "<ul>"
                    + "<li><b>Mức giảm:</b> " + mucGiam + "</li>"
                    + "<li><b>Đơn tối thiểu:</b> " + String.format("%,.0f", voucher.getDieuKien()) + "đ</li>"
                    + "</ul>"
                    + "<p>Hãy nhanh tay sử dụng trước khi hết hạn nhé!</p>"
                    + "<p><i>Cảm ơn bạn đã luôn ủng hộ KC Drink!</i></p>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);

            System.out.println("Đã gửi mail tặng voucher thành công tới: " + email);
        } catch (Exception e) {
            System.out.println("Lỗi gửi mail tặng voucher: " + e.getMessage());
        }
    }
    // ====================================================================
    // Vừa hẹn giờ 8h sáng (để chuẩn lý thuyết đồ án khi lên báo cáo)
    // Vừa tự động chạy NGAY LẬP TỨC khi bạn bật Server Local lên
    // ====================================================================
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 7 * * ?")
    @PostConstruct
    public void tuDongQuetSinhNhatHangNgay() {
        System.out.println("============== [STARTUP LOG] ==============");
        System.out.println("👉 SERVER ĐÃ BẬT: ĐANG KHỞI ĐỘNG LUỒNG QUÉT QUÀ SINH NHẬT TỰ ĐỘNG...");
        try {
            this.tangVoucherSinhNhat();
            System.out.println("✅ [SUCCESS]: ĐÃ HOÀN TẤT QUÉT VÀ GỬI MAIL SINH NHẬT KHÁCH HÀNG!");
        } catch (Exception e) {
            System.out.println("❌ [ERROR]: QUÉT TỰ ĐỘNG THẤT BẠI: " + e.getMessage());
        }
        System.out.println("===========================================");
    }


}