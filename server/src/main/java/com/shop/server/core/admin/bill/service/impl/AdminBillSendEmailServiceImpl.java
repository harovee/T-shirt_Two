package com.shop.server.core.admin.bill.service.impl;

import com.shop.server.core.admin.bill.model.request.AdminSendEmailRequest;
import com.shop.server.core.admin.bill.service.AdminBillSendMailService;
import com.shop.server.infrastructure.email.entity.EmailDetails;
import com.shop.server.infrastructure.email.service.EmailService;
import com.shop.server.utils.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminBillSendEmailServiceImpl implements AdminBillSendMailService {
    private final EmailService emailService;

    @Override
    public void sendMailCreateBill(AdminSendEmailRequest request) {

        try {
            String emailContent = String.format("""
                    <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                            <div
                                                    style="
                                                      max-width: 700px;
                                                      margin: 0 auto;
                                                      border: 1px solid #ddd;
                                                      border-radius: 8px;
                                                      padding: 20px;
                                                      background-color: #f9f9f9;
                                                    "
                                                  >
                                                    <h2 style="text-align: center; color: #4caf50">
                                                      ✅ Thông báo đặt hàng thành công!
                                                    </h2>
                                                    <p style="margin-top: 20px"><strong>Mã đơn hàng:</strong> %s</p>
                                                    <p><strong>Trạng thái:</strong> %s</p>
                                                    <p><strong>Ghi chú:</strong> %s</p>
                    
                                                    <p style="margin-top: 20px">
                                                      🚛 Đơn hàng của bạn đang được xử lý và sẽ sớm được giao đến bạn.
                                                    </p>
                    
                                                    <p
                                                      style="
                                                        text-align: center;
                                                        margin-top: 30px;
                                                        align-items: center;
                                                      "
                                                    >
                                                      Cảm ơn bạn đã mua sắm tại - <strong>TShirtsTwo</strong>! 🎉
                                                    </p>
                                                  </div>
                        </body>
                    </html>
                    """, request.getMaHoaDon(), request.getTrangThai(), request.getGhiChu());
            log.info("sendMail: {}", request.getEmail());
            emailService.sendMailWithAttachment(new
                    EmailDetails(
                    request.getEmail(),
                    emailContent, "🎁 TShirtsTwo - Kính chào quý khách!",
                    null
            ));
        } catch (Exception e) {
            log.error("Error generating voucher email: {}", e.getMessage());
        }
    }

    @Override
    public void sendMailUpdateBill(AdminSendEmailRequest request) {
        try {
            String emailContent = String.format("""
                    <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                            <div
                                                    style="
                                                      max-width: 700px;
                                                      margin: 0 auto;
                                                      border: 1px solid #ddd;
                                                      border-radius: 8px;
                                                      padding: 20px;
                                                      background-color: #f9f9f9;
                                                    "
                                                  >
                                                    <h2 style="text-align: center; color: #4caf50">
                                                      Thông báo thay đổi trạng thái đơn hàng
                                                    </h2>
                                                    <p style="margin-top: 20px"><strong>Mã đơn hàng:</strong> %s</p>
                                                    <p><strong>Trạng thái:</strong> %s</p>
                                                    <p><strong>Nhân viên xử lý:</strong> %s</p>
                                                    <p><strong>Ghi chú:</strong> %s</p>
                    
                                                    <p style="margin-top: 20px">
                                                      🚛 Đơn hàng của bạn đang được xử lý và sẽ sớm được giao đến bạn.
                                                    </p>
                    
                                                    <p
                                                      style="
                                                        text-align: center;
                                                        margin-top: 30px;
                                                        align-items: center;
                                                      "
                                                    >
                                                      Cảm ơn bạn đã mua sắm tại - <strong>TShirtsTwo</strong>! 🎉
                                                    </p>
                                                  </div>
                        </body>
                    </html>
                    """, request.getMaHoaDon(), request.getTrangThai(), request.getEmailNhanVien(), request.getGhiChu());
            log.info("sendMail: {}", request.getEmail());
            emailService.sendMailWithAttachment(new
                    EmailDetails(
                    request.getEmail(),
                    emailContent, "🎁 TShirtsTwo - Kính chào quý khách!",
                    null
            ));
        } catch (Exception e) {
            log.error("Error generating voucher email: {}", e.getMessage());
        }
    }

    @Override
    public void sendMailCancelBill(AdminSendEmailRequest request) {
        try {
            String emailContent = String.format("""
                    <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                            <div
                                                    style="
                                                      max-width: 700px;
                                                      margin: 0 auto;
                                                      border: 1px solid #ddd;
                                                      border-radius: 8px;
                                                      padding: 20px;
                                                      background-color: #f9f9f9;
                                                    "
                                                  >
                                                    <h2 style="text-align: center; color: #4caf50">
                                                      Thông báo thay đổi trạng thái đơn hàng
                                                    </h2>
                                                    <p style="margin-top: 20px"><strong>Mã đơn hàng:</strong> %s</p>
                                                    <p><strong>Trạng thái:</strong> %s</p>
                                                    <p><strong>Nhân viên xử lý:</strong> %s</p>
                                                    <p><strong>Ghi chú:</strong> %s</p>
                    
                                                    <p style="margin-top: 20px">
                                                      🚛 Đơn hàng của bạn đã bị hủy. Trường hợp khách hàng đã thanh toán thì cửa hàng sẽ hoàn trả hoàn trả toàn bộ phí. Xin lỗi vì sự bất tiện này.
                                                    </p>
                    
                                                    <p
                                                      style="
                                                        text-align: center;
                                                        margin-top: 30px;
                                                        align-items: center;
                                                      "
                                                    >
                                                      Cảm ơn bạn đã mua sắm tại - <strong>TShirtsTwo</strong>! 🎉
                                                    </p>
                                                  </div>
                        </body>
                    </html>
                    """, request.getMaHoaDon(), request.getTrangThai(), request.getEmailNhanVien(), request.getGhiChu());
            log.info("sendMail: {}", request.getEmail());
            emailService.sendMailWithAttachment(new
                    EmailDetails(
                    request.getEmail(),
                    emailContent, "🎁 TShirtsTwo - Kính chào quý khách!",
                    null
            ));
        } catch (Exception e) {
            log.error("Error generating voucher email: {}", e.getMessage());
        }
    }
}
