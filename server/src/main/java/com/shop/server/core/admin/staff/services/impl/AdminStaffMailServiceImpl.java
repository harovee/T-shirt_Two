package com.shop.server.core.admin.staff.services.impl;

import com.shop.server.core.admin.staff.services.AdminStaffMailService;
import com.shop.server.entities.main.NhanVien;
import com.shop.server.infrastructure.email.entity.EmailDetails;
import com.shop.server.infrastructure.email.service.EmailService;
import com.shop.server.utils.CodeUtils;
import org.springframework.stereotype.Service;

@Service
public class AdminStaffMailServiceImpl implements AdminStaffMailService {

    private final EmailService emailService;

    public AdminStaffMailServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendMailCreateStaff(NhanVien staff) {
        String emailContent = String.format("""
                        <html>
                            <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                                <div style="max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; padding: 20px; background-color: #f9f9f9;">
                                    <h2 style="text-align: center; color: #4CAF50;">Thông Báo Nhân Viên Mới</h2>
                                    <p>Xin chào <strong>%s</strong>,</p>
                                    <p>Bạn đã được tạo tài khoản trên hệ thống <strong>TShirtsTwo</strong>. Dưới đây là thông tin tài khoản của bạn:</p>
                                    <table style="width: 100%%; border-collapse: collapse; margin-top: 10px;">
                                        <tr>
                                            <td style="padding: 8px; border: 1px solid #ddd;">Mã nhân viên:</td>
                                            <td style="padding: 8px; border: 1px solid #ddd;"><strong>%s</strong></td>
                                        </tr>
                                        <tr>
                                            <td style="padding: 8px; border: 1px solid #ddd;">Số điện thoại:</td>
                                            <td style="padding: 8px; border: 1px solid #ddd;"><strong>%s</strong></td>
                                        </tr>
                                        <tr>
                                            <td style="padding: 8px; border: 1px solid #ddd;">Chức vụ:</td>
                                            <td style="padding: 8px; border: 1px solid #ddd;">Nhân viên</td>
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
                                    <p style="margin-top: 20px;"> 1: Đăng nhập hệ thống kiểm tra thông tin tài khoản của bạn. (Nếu có bất cứ thông tin nào sai vui lòng báo lại cho admin trước khi thao tác hệ thống)</strong></p>
                                    <p style="margin-top: 20px;"> 2: Không cung cấp bất kỳ thông tin nào về thông tin đăng nhập của hệ thống cho bất kỳ ai (Nếu có cung cấp gây ảnh hưởng đến công ty vui lòng chịu toàn bộ trách nhiệm)</strong></p>
                                    <p style="margin-top: 20px;"> 3: Đọc kỹ hướng dẫn sự dụng hệ thống trong tài liệu</strong></p>
                                    <p style="text-align: center; margin-top: 30px; color: #888;">Cảm ơn bạn đã gia nhập đội ngũ của chúng tôi!</p>
                                </div>
                            </body>
                        </html>
                        """,
                staff.getFullName(),
                CodeUtils.convertStringCode(staff.getFullName()) + staff.getCode(),
                staff.getPhoneNumber(),
                staff.getPassword()
        );
        emailService.sendSimpleMail(
                new EmailDetails(
                        staff.getEmail(),
                        emailContent,
                        "Hệ Thống TShirtsTwo Thông Báo",
                        null
                )
        );
    }
}
