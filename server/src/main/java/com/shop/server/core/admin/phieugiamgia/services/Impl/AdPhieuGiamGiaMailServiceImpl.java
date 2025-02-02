package com.shop.server.core.admin.phieugiamgia.services.Impl;

import com.shop.server.core.admin.phieugiamgia.services.AdPhieuGiamGiaMailService;
import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.PhieuGiamGia;
import com.shop.server.infrastructure.email.entity.EmailDetails;
import com.shop.server.infrastructure.email.service.EmailService;
import com.shop.server.utils.DateTimeUtil;
import com.shop.server.utils.GenBarcode;
import com.shop.server.utils.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;

@Service
@Slf4j
public class AdPhieuGiamGiaMailServiceImpl implements AdPhieuGiamGiaMailService {
    private final EmailService emailService;

    public AdPhieuGiamGiaMailServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendMailCreateKhachHangVoucher(KhachHang khachHang, PhieuGiamGia phieuGiamGia) {
        try {
            // Tạo barcode cho mã voucher
            // byte[] barcodeBase64 = GenBarcode.generateBarcode(phieuGiamGia.getMa());
            // String barcodeBase64Str = Base64.getEncoder().encodeToString(barcodeBase64);
            //  String barcodeImgTag = "<img src='data:image/png;base64," + barcodeBase64Str + "' style='display:block; margin:auto;'/>";
            String barCodeUrl = GenBarcode.generateBarcodeImage(phieuGiamGia.getMa());
            String emailContent = String.format("""
                            <html>
                                <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                                    <div style="max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; padding: 20px; background-color: #f9f9f9;">
                                        <h2 style="text-align: center; color: #4CAF50;">🎉 Chúc mừng %s! Bạn nhận được Voucher 🎉</h2>
                                        <p>Xin chào <strong>%s</strong>,</p>
                                        <p>Bạn đã nhận được một mã giảm giá đặc biệt từ <strong>TShirtsTwo</strong>. Dưới đây là chi tiết của voucher:</p>
                            
                                        <table style="width: 100%%; border-collapse: collapse; margin-top: 10px;">
                                            <tr><td style="padding: 8px; border: 1px solid #ddd;">Mã Voucher:</td><td style="padding: 8px; border: 1px solid #ddd;"><strong>%s</strong></td></tr>
                                            <tr><td style="padding: 8px; border: 1px solid #ddd;">Giá trị giảm:</td><td style="padding: 8px; border: 1px solid #ddd;"><strong>%s%%</strong></td></tr>
                                            <tr><td style="padding: 8px; border: 1px solid #ddd;">Ngày hết hạn:</td><td style="padding: 8px; border: 1px solid #ddd;"><strong>%s</strong></td></tr>
                                        </table>
                            
                                        <p style="margin-top: 20px;">📌 Quét mã vạch bên dưới để sử dụng voucher tại cửa hàng:</p>
                                        <img src="%s" style="display:block; margin:auto; width:300px; height:100px;" alt="Mã vạch"/>
                            
                                        <p style="text-align: center; margin-top: 30px; color: #888;">Cảm ơn bạn đã đồng hành cùng <strong>TShirtsTwo</strong>! 🎉</p>
                                    </div>
                                </body>
                            </html>
                            """,
                    khachHang.getFullName(),
                    khachHang.getFullName(),
                    phieuGiamGia.getMa(),
                    phieuGiamGia.getGiaTriGiam(),
                    DateTimeUtil.convertDateToString(DateTimeUtil.convertTimeStampSecondToString(phieuGiamGia.getNgayKetThuc())),
                    barCodeUrl
                    // barcodeImgTag  // Chèn barcode vào email
            );

            log.info("sendMail: {}", khachHang.getEmail());
            String attachmentPath = System.getProperty("user.dir") + "/temp/TShirtsTwoTutorial.pdf";
            emailService.sendMailWithAttachment(new
                    EmailDetails(
                    khachHang.getEmail(),
                    emailContent, "🎁 TShirtsTwo - Voucher Ưu Đãi Dành Cho Bạn!",
                    attachmentPath
            ));
        } catch (Exception e) {
            log.error("Error generating voucher email: {}", e.getMessage());
        }
    }
}
