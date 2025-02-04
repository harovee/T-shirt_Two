package com.shop.server.core.admin.staff.services.impl;

import com.shop.server.core.admin.staff.models.responses.AdminStaffExcelResponse;
import com.shop.server.core.admin.staff.services.AdminStaffExcelService;
import com.shop.server.core.common.base.ResponseObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class AdminStaffExcelServiceImpl implements AdminStaffExcelService {

    @Override
    public ResponseObject<?> exportExcel(List<AdminStaffExcelResponse> staffs) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Nhân Viên");
            String[] headers = {"STT", "Mã NV", "Họ và Tên", "Tuổi", "Giới Tính", "CCCD", "SĐT", "Email", "Trạng Thái"};
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(getHeaderCellStyle(workbook));
            }

            int rowIdx = 1;
            for (AdminStaffExcelResponse staff : staffs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(staff.getCatalog());
                row.createCell(1).setCellValue(staff.getCode());
                row.createCell(2).setCellValue(staff.getName());
                row.createCell(3).setCellValue(staff.getAge());
                row.createCell(4).setCellValue(staff.getGender());
                row.createCell(5).setCellValue(staff.getIdentity());
                row.createCell(6).setCellValue(staff.getPhone());
                row.createCell(7).setCellValue(staff.getEmail());
                row.createCell(8).setCellValue(staff.getStatus());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ResponseObject<>(
                    new ByteArrayInputStream(outputStream.toByteArray()),
                    HttpStatus.OK,
                    "Download template successfully"
            );
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xuất Excel: " + e.getMessage(), e);
        }
    }

    private CellStyle getHeaderCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setLocked(true);
        style.setFont(font);
        style.setWrapText(true);

        return style;
    }

}
