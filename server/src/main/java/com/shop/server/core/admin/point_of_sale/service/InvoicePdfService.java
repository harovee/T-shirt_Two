package com.shop.server.core.admin.point_of_sale.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.UnitValue;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.shop.server.core.admin.point_of_sale.model.request.AdPOSInvoicePdfRequest;
import com.shop.server.core.admin.point_of_sale.model.response.AdOrderDetailResponse;
import com.shop.server.core.admin.point_of_sale.repository.PointOfSaleRepository;
import com.shop.server.entities.main.HoaDon;
import com.shop.server.repositories.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.itextpdf.layout.property.TextAlignment;

@Service
@RequiredArgsConstructor
public class InvoicePdfService {

    private final PointOfSaleRepository pointOfSaleRepository;

    private final HoaDonRepository hoaDonRepository;

    public byte[] generateInvoicePdf(AdPOSInvoicePdfRequest request) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String fontPath = "src/main/resources/fonts/timesbd.ttf"; //
            PdfFont vietnameseFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, true);
            DecimalFormat df = new DecimalFormat("#,###.## VND");

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            Paragraph title1 = new Paragraph("Cửa hàng T-shirt Two")
                    .setFont(vietnameseFont)
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            Paragraph diaChi = new Paragraph("Địa chỉ: Số 13 - Xuân Phương - Nam Từ Liêm - TP Hà Nội")
                    .setFont(vietnameseFont)
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER);
            Paragraph sdt = new Paragraph("SĐT: 0987654321")
                    .setFont(vietnameseFont)
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER);

            Paragraph camOn = new Paragraph("Cảm ơn quý khách ! Hẹn gặp lại.")
                    .setFont(vietnameseFont)
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER);
            // Tiêu đề hóa đơn
            Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG")
                    .setFont(vietnameseFont)
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER); // Đặt căn giữa trước khi add vào document
            SolidLine solidLine = new SolidLine(1); // Độ dày đường kẻ là 1
            LineSeparator lineSeparator = new LineSeparator(solidLine);
            lineSeparator.setWidth(UnitValue.createPercentValue(100)); // Độ dài 100% trang

            LocalDateTime now = LocalDateTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDateTime = now.format(formatter);
            document.add(title1);
            document.add(diaChi);
            document.add(sdt);
            document.add(new Paragraph(""));
            document.add(lineSeparator);
            document.add(new Paragraph(""));
            document.add(title);
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            // Thông tin khách hàng
            float[] columnWidths = {1, 1}; // Chia bảng thành 2 cột bằng nhau
            Table table1 = new Table(columnWidths);
            table1.setWidth(UnitValue.createPercentValue(100));
            String tenKhachHang = request.getIdKhachHang() != null ? pointOfSaleRepository.getCustomerNameById(request.getIdKhachHang()) : "Khách lẻ";
            String maHoaDon = pointOfSaleRepository.getInvoiceCodeById(request.getIdHoaDon());
            String tenNhanVien = request.getIdNhanVien() != null ? pointOfSaleRepository.getStaffNameById(request.getIdNhanVien()): "Nhân viên không xác định";
            table1.addCell(new Cell().add(new Paragraph("Khách hàng: " + tenKhachHang))
                    .setFont(vietnameseFont)
                    .setTextAlignment(TextAlignment.LEFT) // Căn trái
                    .setBorder(null)); // Xóa viền ô

            table1.addCell(new Cell().add(new Paragraph("Mã HĐ: " + maHoaDon))
                    .setFont(vietnameseFont)
                    .setTextAlignment(TextAlignment.RIGHT) // Căn phải
                    .setBorder(null));

            table1.addCell(new Cell().add(new Paragraph("Nhân viên: " + tenNhanVien)) // Đổi thành getTenNhanVien()
                    .setFont(vietnameseFont)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBorder(null));

            table1.addCell(new Cell().add(new Paragraph("Ngày tạo: " + formattedDateTime))
                    .setFont(vietnameseFont)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(null));

            document.add(table1);

            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
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
                                                           " - " +product.getKichCo() + " ]").setFont(vietnameseFont)));
                table.addCell(new Cell().add(new Paragraph(product.getSoLuong()).setFont(vietnameseFont)));
                table.addCell(new Cell().add(new Paragraph(df.format(product.getGiaHienTai())).setFont(vietnameseFont)));
                table.addCell(new Cell().add(new Paragraph(df.format(total)).setFont(vietnameseFont)));
            }
            BigDecimal tongThanhToan = request.getTongTien()
                    .add(request.getPhiVanChuyen())
                    .subtract(request.getGiamGia());
            document.add(table);
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));

            Table summaryTable = new Table(new float[]{2, 1});
            summaryTable.setWidth(UnitValue.createPercentValue(100));

            // Thêm các dòng vào bảng
            addRow(summaryTable, "Tiền hàng:", df.format(request.getTongTien()), vietnameseFont);
            addRow(summaryTable, "Phí vận chuyển:", df.format(request.getPhiVanChuyen()), vietnameseFont);
            addRow(summaryTable, "Giảm giá:", df.format(request.getGiamGia()), vietnameseFont);

            // Thêm dấu gạch ngang
            summaryTable.addCell(new Cell(1, 2).add(new Paragraph("-----------------------------------------------------"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(null));


            // Thêm tổng tiền và số tiền thanh toán
            addRow(summaryTable, "Tổng:", df.format(tongThanhToan), vietnameseFont);
            addRow(summaryTable, "Đã thanh toán:", df.format(tongThanhToan), vietnameseFont);

            document.add(summaryTable);
            document.add(new Paragraph(""));
            document.add(camOn);
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF", e);
        }
    }

    public byte[] generateInvoicePdfWithId(String id, AdPOSInvoicePdfRequest request) {

        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với id: " + id));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String fontPath = "src/main/resources/fonts/timesbd.ttf"; //
            PdfFont vietnameseFont = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, true);
            DecimalFormat df = new DecimalFormat("#,###.## VND");

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            Paragraph title1 = new Paragraph("Cửa hàng T-shirt Two")
                    .setFont(vietnameseFont)
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            Paragraph diaChi = new Paragraph("Địa chỉ: Số 13 - Xuân Phương - Nam Từ Liêm - TP Hà Nội")
                    .setFont(vietnameseFont)
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER);
            Paragraph sdt = new Paragraph("SĐT: 0987654321")
                    .setFont(vietnameseFont)
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER);

            Paragraph camOn = new Paragraph("Cảm ơn quý khách ! Hẹn gặp lại.")
                    .setFont(vietnameseFont)
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER);
            // Tiêu đề hóa đơn
            Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG")
                    .setFont(vietnameseFont)
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER); // Đặt căn giữa trước khi add vào document
            SolidLine solidLine = new SolidLine(1); // Độ dày đường kẻ là 1
            LineSeparator lineSeparator = new LineSeparator(solidLine);
            lineSeparator.setWidth(UnitValue.createPercentValue(100)); // Độ dài 100% trang

            LocalDateTime now = LocalDateTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDateTime = now.format(formatter);
            document.add(title1);
            document.add(diaChi);
            document.add(sdt);
            document.add(new Paragraph(""));
            document.add(lineSeparator);
            document.add(new Paragraph(""));
            document.add(title);
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            // Thông tin khách hàng
            float[] columnWidths = {1, 1}; // Chia bảng thành 2 cột bằng nhau
            Table table1 = new Table(columnWidths);
            table1.setWidth(UnitValue.createPercentValue(100));
            String tenKhachHang = hoaDon.getKhachHang().getId() != null ? pointOfSaleRepository.getCustomerNameById(hoaDon.getKhachHang().getId()) : "Khách lẻ";
            String maHoaDon = pointOfSaleRepository.getInvoiceCodeById(hoaDon.getId());
            String tenNhanVien = request.getIdNhanVien() != null ? pointOfSaleRepository.getStaffNameById(request.getIdNhanVien()): "Nhân viên không xác định";
            table1.addCell(new Cell().add(new Paragraph("Khách hàng: " + tenKhachHang))
                    .setFont(vietnameseFont)
                    .setTextAlignment(TextAlignment.LEFT) // Căn trái
                    .setBorder(null)); // Xóa viền ô

            table1.addCell(new Cell().add(new Paragraph("Mã HĐ: " + maHoaDon))
                    .setFont(vietnameseFont)
                    .setTextAlignment(TextAlignment.RIGHT) // Căn phải
                    .setBorder(null));

            table1.addCell(new Cell().add(new Paragraph("Nhân viên: " + tenNhanVien)) // Đổi thành getTenNhanVien()
                    .setFont(vietnameseFont)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBorder(null));

            table1.addCell(new Cell().add(new Paragraph("Ngày tạo: " + formattedDateTime))
                    .setFont(vietnameseFont)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(null));

            document.add(table1);

            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
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
                                                           " - " +product.getKichCo() + " ]").setFont(vietnameseFont)));
                table.addCell(new Cell().add(new Paragraph(product.getSoLuong()).setFont(vietnameseFont)));
                table.addCell(new Cell().add(new Paragraph(df.format(product.getGiaHienTai())).setFont(vietnameseFont)));
                table.addCell(new Cell().add(new Paragraph(df.format(total)).setFont(vietnameseFont)));
            }
            BigDecimal tongThanhToan = request.getTongTien()
                    .add(hoaDon.getTienShip())
                    .subtract(hoaDon.getTienGiam());
            document.add(table);
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));

            Table summaryTable = new Table(new float[]{2, 1});
            summaryTable.setWidth(UnitValue.createPercentValue(100));

            // Thêm các dòng vào bảng
            addRow(summaryTable, "Tiền hàng:", df.format(hoaDon.getTongTien()), vietnameseFont);
            addRow(summaryTable, "Phí vận chuyển:", df.format(hoaDon.getTienShip()), vietnameseFont);
            addRow(summaryTable, "Giảm giá:", df.format(hoaDon.getTienGiam()), vietnameseFont);

            // Thêm dấu gạch ngang
            summaryTable.addCell(new Cell(1, 2).add(new Paragraph("-----------------------------------------------------"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(null));


            // Thêm tổng tiền và số tiền thanh toán
            addRow(summaryTable, "Tổng:", df.format(tongThanhToan), vietnameseFont);
            addRow(summaryTable, "Đã thanh toán:", df.format(tongThanhToan), vietnameseFont);

            document.add(summaryTable);
            document.add(new Paragraph(""));
            document.add(camOn);
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF", e);
        }
    }

    private void addRow(Table table, String title, String value, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(title).setFont(font))
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(null));

        table.addCell(new Cell().add(new Paragraph(value).setFont(font).setBold())
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(null));
    }
}
