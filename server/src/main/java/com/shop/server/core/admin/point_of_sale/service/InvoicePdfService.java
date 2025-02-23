package com.shop.server.core.admin.point_of_sale.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.UnitValue;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.shop.server.core.admin.point_of_sale.model.request.AdPOSInvoicePdfRequest;
import com.shop.server.core.admin.point_of_sale.model.response.AdOrderDetailResponse;
import org.springframework.stereotype.Service;

@Service
public class InvoicePdfService {
    public byte[] generateInvoicePdf(AdPOSInvoicePdfRequest request) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String fontPath = "src/main/resources/fonts/timesbd.ttf"; //
            PdfFont vietnameseFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, true);
            DecimalFormat df = new DecimalFormat("#,###.## VND");

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Tiêu đề hóa đơn
            document.add(new Paragraph("HÓA ĐƠN BÁN HÀNG")
                    .setFont(vietnameseFont)
                    .setBold().setFontSize(18));

            // Thông tin khách hàng
            document.add(new Paragraph("Khách hàng: " + request.getTenKhachHang())).setFont(vietnameseFont);

            // Bảng sản phẩm
            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 20, 20, 20}))
                    .useAllAvailableWidth();

            // Thêm tiêu đề cột
            table.addHeaderCell(new Cell().add(new Paragraph("Sản phẩm").setFont(vietnameseFont).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Số lượng").setFont(vietnameseFont).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Đơn giá").setFont(vietnameseFont).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Thành tiền").setFont(vietnameseFont).setBold()));

            // Thêm dữ liệu sản phẩm
            for (AdOrderDetailResponse product : request.getProducts()) {
                BigDecimal total = BigDecimal.valueOf(Long.valueOf(product.getSoLuong()) * product.getGiaHienTai());
                table.addCell(new Cell().add(new Paragraph(product.getTenSanPham() + " [ " + product.getTenMauSac() +
                                                           " - " +product.getKichCo() + " ]")));
                table.addCell(new Cell().add(new Paragraph(product.getSoLuong())));
                table.addCell(new Cell().add(new Paragraph(df.format(product.getGiaHienTai()))));
                table.addCell(new Cell().add(new Paragraph(df.format(total))));
            }

            document.add(table);

            // Tổng tiền

            document.add(new Paragraph("Tổng tiền: " + df.format(request.getTongTien())).setFont(vietnameseFont)
                    .setBold().setFontSize(14));
            document.add(new Paragraph("Phí vận chuyển: " + df.format(request.getPhiVanChuyen())).setFont(vietnameseFont)
                    .setBold().setFontSize(14));
            document.add(new Paragraph("Giảm giá: " + df.format(request.getGiamGia())).setFont(vietnameseFont)
                    .setBold().setFontSize(14));
            BigDecimal tongThanhToan = request.getTongTien()
                    .add(request.getPhiVanChuyen())
                    .subtract(request.getGiamGia());
            document.add(new Paragraph("Tổng thanh toán: " + df.format(tongThanhToan)).setFont(vietnameseFont)
                    .setBold().setFontSize(14));
            document.add(new Paragraph("Đã thanh toán: " + df.format(tongThanhToan)).setFont(vietnameseFont)
                    .setBold().setFontSize(14));

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF", e);
        }
    }
}
