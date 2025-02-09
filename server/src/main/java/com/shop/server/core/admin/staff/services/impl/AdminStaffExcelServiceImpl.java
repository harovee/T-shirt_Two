package com.shop.server.core.admin.staff.services.impl;

import com.shop.server.core.admin.staff.models.responses.AdminStaffExcelResponse;
import com.shop.server.core.admin.staff.repositories.AdminStaffRepository;
import com.shop.server.core.admin.staff.services.AdminStaffExcelService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.NhanVien;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.infrastructure.constants.module.Status;
import com.shop.server.utils.AESPasswordCryptoUtil;
import com.shop.server.utils.DefaultImageUtil;
import com.shop.server.utils.FileHelper;
import com.shop.server.utils.Helper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class AdminStaffExcelServiceImpl implements AdminStaffExcelService {

    private static final String[] HEADERS = {"Họ và Tên", "Ngày Sinh", "Giới Tính", "CCCD", "SĐT", "Email"};
    private static final String SHEET = "Nhân Viên";
    private static final List<String> GENDERS = List.of("Nam", "Nữ");

    private final AdminStaffRepository adminStaffRepository;

    public AdminStaffExcelServiceImpl(AdminStaffRepository adminStaffRepository) {
        this.adminStaffRepository = adminStaffRepository;
    }

    @Override
    public ResponseObject<?> exportExcel(List<AdminStaffExcelResponse> staffs) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Nhân Viên");
            String[] headers = {"STT", "Mã NV", "Họ và Tên", "Ngày Sinh", "Giới Tính", "CCCD", "SĐT", "Email", "Trạng Thái"};

            createHeaderRow(sheet, headers, createHeaderCellStyle(workbook));
            createRowBorder(workbook, sheet, headers.length - 1);

            CellStyle cellStyle = createCellStyleWithBorder(workbook);
            int rowIdx = 1;
            for (AdminStaffExcelResponse staff : staffs) {
                Row row = sheet.getRow(rowIdx++);
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(staff.getCatalog());
                cell0.setCellStyle(cellStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(staff.getCode());
                cell1.setCellStyle(cellStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(staff.getName());
                cell2.setCellStyle(cellStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(staff.getAge());
                cell3.setCellStyle(cellStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(staff.getGender());
                cell4.setCellStyle(cellStyle);

                Cell cell5 = row.createCell(5);
                cell5.setCellValue(staff.getIdentity());
                cell5.setCellStyle(cellStyle);

                Cell cell6 = row.createCell(6);
                cell6.setCellValue(staff.getPhone());
                cell6.setCellStyle(cellStyle);

                Cell cell7 = row.createCell(7);
                cell7.setCellValue(staff.getEmail());
                cell7.setCellStyle(cellStyle);

                Cell cell8 = row.createCell(8);
                cell8.setCellValue(staff.getStatus());
                cell8.setCellStyle(cellStyle);
            }

            autoSizeColumns(workbook);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ResponseObject<>(new ByteArrayInputStream(outputStream.toByteArray()), HttpStatus.OK, "Download template successfully");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xuất Excel: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseObject<?> exportTemplateExcel() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(SHEET);
            createHeaderRow(sheet, HEADERS, createHeaderCellStyle(workbook));
            createRowBorder(workbook, sheet, HEADERS.length - 1);
            applyTextFormatToColumns(sheet);
            applyDataFormatToColumns(sheet, workbook);
            addDataValidation(sheet);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ResponseObject<>(new ByteArrayInputStream(outputStream.toByteArray()), HttpStatus.OK, "Download template successfully");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xuất Excel: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseObject<?> importExcel(MultipartFile file) {
        if (FileHelper.hasExcelFormat(file)) {
            try {
                Workbook workbook = new XSSFWorkbook(file.getInputStream());

                Sheet sheet = workbook.getSheet(SHEET);
                Iterator<Row> rows = sheet.iterator();

                List<NhanVien> staffs = new ArrayList<>();

                int rowNumber = 0;
                long count = adminStaffRepository.countNhanVienByRole(Role.USER) + 1;


                while (rows.hasNext()) {
                    Row currentRow = rows.next();

                    if (rowNumber == 0) {
                        rowNumber++;
                        continue;
                    }

                    if (FileHelper.isRowEmpty(currentRow)) {
                        continue;
                    }

                    Iterator<Cell> cellsInRow = currentRow.iterator();

                    NhanVien staff = new NhanVien();

                    int cellIdx = 0;

                    while (cellsInRow.hasNext()) {
                        Cell currentCell = cellsInRow.next();
                        switch (cellIdx) {
                            case 0:
                                String name = currentCell.getStringCellValue();
                                if (FileHelper.isHasValue(name)) {
                                    staff.setFullName(name);
                                } else {
                                    return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, Message.Response.FORMAT_NOT_VALID + ", tên: " + name + ". Định dạng không được null");
                                }
                                break;
                            case 1:
                                Double birthDay = (Double) currentCell.getNumericCellValue();
                                if (FileHelper.isExcelNumericDate(birthDay)) {
                                    long timestamp = FileHelper.convertExcelDateToTimestamp(birthDay);
                                    if (!FileHelper.isOver18(timestamp / 1000L)) {
                                        return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, "Người dùng chưa đủ 18 tuổi.");
                                    }
                                    staff.setBirthday(timestamp);
                                } else {
                                    return ResponseObject.errorForward(
                                            HttpStatus.BAD_REQUEST,
                                            Message.Response.FORMAT_NOT_VALID + ", Ngày sinh không hợp lệ: " + birthDay
                                    );
                                }
                                break;
                            case 2:
                                String gender = currentCell.getStringCellValue();
                                if (FileHelper.isHasValue(gender) && GENDERS.contains(gender)) {
                                    staff.setGender(gender.equalsIgnoreCase("Nam"));
                                } else {
                                    return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, Message.Response.FORMAT_NOT_VALID + ", giới tính: " + gender + ". Định dạng sai option template");
                                }
                                break;
                            case 3:
                                String identity = currentCell.getStringCellValue();
                                if (FileHelper.isHasValue(identity)) {
                                    staff.setIdentity(identity);
                                } else {
                                    return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, Message.Response.FORMAT_NOT_VALID + ", mã căn cước công dân: " + identity + ". Định dạng đúng option cccd");
                                }
                                break;
                            case 4:
                                String phone = currentCell.getStringCellValue();
                                if (FileHelper.isHasValue(phone)) {
                                    staff.setPhoneNumber(phone);
                                } else {
                                    return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, Message.Response.FORMAT_NOT_VALID + ", số điện thoại: " + phone + ". Định dạng đúng option phone");
                                }
                                break;
                            case 5:
                                String email = currentCell.getStringCellValue();
                                if (FileHelper.isHasValue(email)) {
                                    staff.setEmail(email);
                                } else {
                                    return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, Message.Response.FORMAT_NOT_VALID + ", email: " + email + ". Định dạng đúng option email");
                                }
                                break;
                            default:
                                break;
                        }
                        cellIdx++;
                    }

                    if (adminStaffRepository.existsStaffByEmail(staff.getEmail())) {
                        return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, Message.Response.DUPLICATE + ", email: " + staff.getEmail());
                    }
                    if (adminStaffRepository.existsStaffByIdentity(staff.getIdentity())) {
                        return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, Message.Response.DUPLICATE + ", mã định danh cá nhân: " + staff.getIdentity());
                    }
                    if (adminStaffRepository.existsStaffByPhoneNumber(staff.getPhoneNumber())) {
                        return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, Message.Response.DUPLICATE + ", số điện thoại: " + staff.getPhoneNumber());
                    }

                    String pass = AESPasswordCryptoUtil.genPassword(8L);
                    staff.setPassword(pass);
                    staff.setSubCode(Helper.getSubCodeFromName(staff.getFullName()));
                    staff.setCode(String.valueOf(count++));
                    staff.setProfilePicture(DefaultImageUtil.IMAGE);
                    staff.setRole(Role.USER);
                    staff.setStatus(Status.ACTIVE);
                    staff.setDeleted(false);
                    staffs.add(staff);
                }

                workbook.close();

                adminStaffRepository.saveAll(staffs);
                return ResponseObject.successForward(HttpStatus.OK, Message.Success.UPDATE_SUCCESS);
            } catch (IOException e) {
                throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
            }
        }
        return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, "File không đúng định dạng");
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle headerCellStyle = workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        headerCellStyle.setFont(font);
        headerCellStyle.setLocked(true);
        headerCellStyle.setWrapText(true);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        return headerCellStyle;
    }

    private CellStyle createCellStyleWithBorder(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        short format = workbook.createDataFormat().getFormat("@");
        cellStyle.setDataFormat(format);
        return cellStyle;
    }

    private CellStyle createCellStyleWithBorder(Workbook workbook, String dataFormat) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        short format = workbook.createDataFormat().getFormat(dataFormat);
        cellStyle.setDataFormat(format);
        return cellStyle;
    }

    private void applyDataFormatToColumns(XSSFSheet sheet, XSSFWorkbook workbook) {
        CellStyle generalStyle = createCellStyleWithBorder(workbook);
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setBorderTop(BorderStyle.THIN);
        dateStyle.setBorderBottom(BorderStyle.THIN);
        dateStyle.setBorderLeft(BorderStyle.THIN);
        dateStyle.setBorderRight(BorderStyle.THIN);
        short dateFormat = workbook.createDataFormat().getFormat("yyyy-mm-dd"); // Date format
        dateStyle.setDataFormat(dateFormat);

        int[] dateColumns = {1}; // "Ngày Sinh" column (index 1)
        int[] textColumns = {0, 2, 3, 4, 5}; // "Họ và Tên", "Giới Tính", "CCCD", "SĐT", "Email"

        // Apply general text format to specific columns
        for (int colIdx : textColumns) {
            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row != null) {
                    Cell cell = row.createCell(colIdx);
                    cell.setCellStyle(generalStyle);
                }
            }
        }

        for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row != null) {
                Cell cell = row.createCell(1);
                cell.setCellStyle(dateStyle);
            }
        }
    }

    private void createHeaderRow(Sheet sheet, String[] headers, CellStyle headerCellStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }
        sheet.autoSizeColumn(0);
    }

    private void createRowBorder(Workbook workbook, Sheet sheet, int cols) {
        CellStyle cellStyleWithBorder = createCellStyleWithBorder(workbook);
        for (int rowIndex = 1; rowIndex <= 500; rowIndex++) {
            Row row = sheet.createRow(rowIndex);
            for (int colIndex = 0; colIndex <= cols; colIndex++) {
                Cell cell = row.createCell(colIndex);
                cell.setCellStyle(cellStyleWithBorder);
            }
        }
    }

    private void applyTextFormatToColumns(XSSFSheet sheet) {
        XSSFCellStyle textStyle = sheet.getWorkbook().createCellStyle();
        textStyle.setDataFormat(sheet.getWorkbook().getCreationHelper().createDataFormat().getFormat("@"));

        int[] textColumns = {0, 1, 3, 6, 7}; // Họ và Tên (2), Giới Tính (4), CCCD (5), SĐT (6), Email (7)

        for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row != null) {
                for (int colIdx : textColumns) {
                    Cell cell = row.getCell(colIdx);
                    if (cell == null) {
                        cell = row.createCell(colIdx);
                    }
                    cell.setCellStyle(textStyle);
                }
            }
        }
    }

    private void addDataValidation(Sheet sheet) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        createAndAddValidationString(sheet, validationHelper, 1, 500, 0);
        createAndAddValidationDate(sheet, validationHelper, 1, 500, 1);
        createAndAddValidationOption(sheet, validationHelper, GENDERS, 1, 500, 2);
        createAndAddValidationCCCD(sheet, validationHelper, 1, 500, 3);
        createAndAddValidationPhoneNumber(sheet, validationHelper, 1, 500, 4);
        createAndAddValidationEmail(sheet, validationHelper, 1, 500, 5);
    }

    private void createAndAddValidationOption(Sheet sheet, DataValidationHelper validationHelper, List<String> validValues, int startRow, int endRow, int column) {
        CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, column, column);
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(validValues.toArray(new String[0]));
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(true);
        validation.createErrorBox("Sai dữ liệu", "Hãy chọn dữ liệu cho sẵn");
        validation.createPromptBox("Chọn dữ liệu", "Hãy chọn dữ liệu cho sẵn");
        sheet.addValidationData(validation);
    }

    private void createAndAddValidationDate(Sheet sheet, DataValidationHelper validationHelper, int startRow, int endRow, int column) {
        CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, column, column);
        String startDate = "dd-mm-yyyy";
        String endDate = LocalDate.now().minusYears(18).toString();
        DataValidationConstraint dateConstraint = validationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, startDate, endDate, "dd-mm-yyyy");
        DataValidation validation = validationHelper.createValidation(dateConstraint, addressList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Lỗi nhập ngày sinh", "Vui lòng nhập ngày hợp lệ (từ 01/01/1900 đến ngày đủ 18 tuổi).");
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(true);
        sheet.addValidationData(validation);
    }

    private void createAndAddValidationPhoneNumber(Sheet sheet, DataValidationHelper validationHelper, int startRow, int endRow, int column) {
        CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, column, column);

        DataValidationConstraint phoneConstraint = validationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.BETWEEN, "10", "11");

        DataValidation validation = validationHelper.createValidation(phoneConstraint, addressList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Lỗi nhập số điện thoại", "Số điện thoại phải có từ 10 - 11 chữ số.");
        validation.setSuppressDropDownArrow(true);

        sheet.addValidationData(validation);
    }

    private void createAndAddValidationCCCD(Sheet sheet, DataValidationHelper validationHelper, int startRow, int endRow, int column) {
        CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, column, column);

        DataValidationConstraint cccdConstraint = validationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.EQUAL, "12", null);

        DataValidation validation = validationHelper.createValidation(cccdConstraint, addressList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Lỗi nhập CCCD", "CCCD phải có đúng 12 chữ số.");
        validation.setSuppressDropDownArrow(true);

        sheet.addValidationData(validation);
    }

    private void createAndAddValidationNumber(Sheet sheet, DataValidationHelper validationHelper, int startRow, int endRow, int column) {
        CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, column, column);

        DataValidationConstraint numberConstraint = validationHelper.createIntegerConstraint(DataValidationConstraint.OperatorType.BETWEEN, "1", "1000");

        DataValidation validation = validationHelper.createValidation(numberConstraint, addressList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Lỗi nhập số", "Vui lòng nhập số từ 1 đến 1000.");
        validation.setSuppressDropDownArrow(true);

        sheet.addValidationData(validation);
    }

    private void createAndAddValidationString(Sheet sheet, DataValidationHelper validationHelper, int startRow, int endRow, int column) {
        CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, column, column);

        DataValidationConstraint textConstraint = validationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.GREATER_THAN, "0", null);

        DataValidation validation = validationHelper.createValidation(textConstraint, addressList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Lỗi nhập dữ liệu", "Vui lòng nhập nội dung không được để trống.");
        validation.setSuppressDropDownArrow(true);

        sheet.addValidationData(validation);
    }

    private void createAndAddValidationEmail(Sheet sheet, DataValidationHelper validationHelper, int startRow, int endRow, int column) {
        CellRangeAddressList addressList = new CellRangeAddressList(startRow, endRow, column, column);

        String formula = "AND(ISNUMBER(SEARCH(\"@gmail.com\", INDIRECT(ADDRESS(ROW(), COLUMN())))), LEN(INDIRECT(ADDRESS(ROW(), COLUMN()))) > 10)";
        DataValidationConstraint emailConstraint = validationHelper.createCustomConstraint(formula);

        DataValidation validation = validationHelper.createValidation(emailConstraint, addressList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Lỗi nhập Email", "Email phải có đuôi @gmail.com.");
        validation.setSuppressDropDownArrow(true);

        sheet.addValidationData(validation);
    }

    public void autoSizeColumns(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                Row row = sheet.getRow(sheet.getFirstRowNum());
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    sheet.autoSizeColumn(columnIndex);
                }
            }
        }
    }

}
