package com.shop.server.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class FileHelper {

    private static final String[] HEADERS = {"Họ và Tên", "Ngày Sinh", "Giới Tính", "CCCD", "SĐT", "Email"};

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@gmail\\.com$");
    private static final Pattern IDENTITY_PATTERN = Pattern.compile("^[0-9]{12}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{2}-\\d{2}-\\d{4}$");

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static boolean isPhoneFormat(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isEmailFormat(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isIdentityFormat(String identity) {
        return IDENTITY_PATTERN.matcher(identity).matches();
    }

    public static boolean isHasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isDateFormat(String date) {
        if (!DATE_PATTERN.matcher(date).matches()) {
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {
            LocalDate.parse(date, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isExcelNumericDate(Double numericDate) {
        if (numericDate == null || numericDate < 1) {
            return false;
        }
        try {
            LocalDate date = LocalDate.of(1900, 1, 1).plusDays(numericDate.longValue() - 2);
            return date.getYear() > 1900;
        } catch (Exception e) {
            return false;
        }
    }

    public static long convertExcelDateToTimestamp(double excelDate) {
        LocalDate date = LocalDate.of(1900, 1, 1).plusDays((long) excelDate - 2);
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond() * 1000;
    }

    public static boolean isOver18(Long birthDateTimestamp) {
        if (birthDateTimestamp == null || birthDateTimestamp <= 0) {
            return false;
        }

        LocalDate birthDate = Instant.ofEpochSecond(birthDateTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
    }

    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        // * Date * \\
        double birthDay = 38169.0;
        long timestamp = convertExcelDateToTimestamp(birthDay);

        System.out.println("Timestamp: " + timestamp);
        System.out.println("Timestamp TimeZone: " + DateTimeUtil.convertTimeStampSecondToStringTimeZone(timestamp / 1000L));
        System.out.println("Timestamp 18 + : " + isOver18(timestamp));

        System.out.println(isDateFormat("25-12-2024")); // ✅ true
        System.out.println(isDateFormat("32-13-2024")); // ❌ false
        System.out.println(isDateFormat("00-00-2024")); // ❌ false
        System.out.println(isDateFormat("10/12/2024")); // ❌ false
    }

}
