package com.shop.server.core.admin.client.services.impl;

import com.shop.server.core.admin.client.services.AdminClientMailService;
import com.shop.server.entities.main.KhachHang;
import com.shop.server.infrastructure.email.entity.EmailDetails;
import com.shop.server.infrastructure.email.service.EmailService;
import org.springframework.stereotype.Service;

@Service
public class AdminClientMailServiceImpl implements AdminClientMailService {

    private final EmailService emailService;

    public AdminClientMailServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendMailCreateClient(KhachHang client) {
        String emailContent = String.format("""
                        <html>
                            <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                                <div style="max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; padding: 20px; background-color: #f9f9f9;">
                                    <h2 style="text-align: center; color: #4CAF50;">Thông Báo Khách Hàng Mới</h2>
                                    <p>Xin chào <strong>%s</strong>,</p>
                                    <p>Bạn đã được tạo tài khoản trên hệ thống <strong>TShirtsTwo</strong>. Dưới đây là thông tin tài khoản của bạn:</p>
                                    <table style="width: 100%%; border-collapse: collapse; margin-top: 10px;">
                                        <tr>
                                            <td style="padding: 8px; border: 1px solid #ddd;">Mã khách hàng:</td>
                                            <td style="padding: 8px; border: 1px solid #ddd;"><strong>%s</strong></td>
                                        </tr>
                                        <tr>
                                            <td style="padding: 8px; border: 1px solid #ddd;">Số điện thoại:</td>
                                            <td style="padding: 8px; border: 1px solid #ddd;"><strong>%s</strong></td>
                                        </tr>
                                        <tr>
                                            <td style="padding: 8px; border: 1px solid #ddd;">Trạng thái:</td>
                                            <td style="padding: 8px; border: 1px solid #ddd;">Hoạt động</td>
                                        </tr>
                                        <tr>
                                            <td style="padding: 8px; border: 1px solid #ddd;">Mật khẩu:</td>
                                            <td style="padding: 8px; border: 1px solid #ddd;"><strong>%s</strong></td>
                                        </tr>
                                    </table>
                                    <p style="margin-top: 20px;">Người tạo: Admin Hệ Thống</p>
                                    <p style="margin-top: 20px;">Yêu cầu: </strong></p>
                                    <p style="margin-top: 20px;"> 1: Đăng nhập hệ thống kiểm tra thông tin tài khoản của bạn.</strong></p>
                                    <p style="margin-top: 20px;"> 2: Không cung cấp bất kỳ thông tin nào về thông tin đăng nhập của hệ thống cho bất kỳ ai</strong></p>
                                    <p style="text-align: center; margin-top: 30px; color: #888;">Cảm ơn bạn đã đăng ký tài khoản tại TShirt Two!</p>
                                </div>
                            </body>
                        </html>
                        """,
                client.getFullName(),
                "KH" + client.getCode(),
                client.getPhoneNumber(),
                client.getPassword()
        );
        emailService.sendSimpleMail(
                new EmailDetails(
                        client.getEmail(),
                        emailContent,
                        "Hệ Thống TShirtsTwo Thông Báo",
                        null
                )
        );
    }

}
