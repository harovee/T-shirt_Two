package com.shop.server.infrastructure.constants.module;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    public static class Success {
        public static final String GET_SUCCESS = "Lấy dữ liệu thành công";
        public static final String CREATE_SUCCESS = "Tạo dữ liệu thành công";
        public static final String UPDATE_SUCCESS = "Cập nhật dữ liệu thành công";
        public static final String DELETE_SUCCESS = "Xóa dữ liệu thành công";
    }

    public static class Error {
        public static final String GET_ERROR = "Lấy dữ liệu thất bại";
        public static final String CREATE_ERROR = "Tạo dữ liệu thất bại";
        public static final String UPDATE_ERROR = "Cập nhật dữ liệu thất bại";
        public static final String DELETE_ERROR = "Xóa dữ liệu thất bại";
    }

    public static class Exception {
        public static final String CLIENT_NOT_FOUND = "Không tìm thấy khách hàng";
        public static final String CALL_API_FAIL = "Gọi API thất bại";
        public static final String CALL_API_SUCCESS = "Gọi API thành công";
    }

    public static class Response {
        public static final String INTERNAL_SERVER_ERROR = "Có lỗi xảy ra, vui lòng thử lại sau";
        public static final String NOT_FOUND = "Không tìm thấy dữ liệu";
        public static final String INVALID_REQUEST = "Yêu cầu không hợp lệ";
        public static final String UNAUTHORIZED = "Không có quyền truy cập";
        public static final String FORBIDDEN = "Không được phép truy cập";
        public static final String BAD_REQUEST = "Yêu cầu không hợp lệ";
        public static final String DUPLICATE = "Dữ liệu đã tồn tại";
        public static final String NOT_VALID = "Dữ liệu không hợp lệ";
        public static final String NOT_FOUND_USER = "Không tìm thấy người dùng";
        public static final String NOT_FOUND_ROLE = "Không tìm thấy quyền";
        public static final String STRING_TOO_LONG = "Dữ liệu quá dài";
        public static final String CODE_EXIST = "Mã đã tồn tại";
        public static final String OVERLAPPING_SEMESTERS = "Thời gian học kỳ đang trùng lặp với học kỳ khác";
    }

    public static class Validate {
        public static final String NOT_NULL = "không được để trống";
        public static final String NOT_BLANK = "không được để trống và ký tự khoảng trắng";
        public static final String IS_EMAIL = "trường dữ liệu phải có định dạng là email";
        public static final String IS_NUMBER = "trường dữ liệu phải có định dạng số";
        public static final String SIZE50 = "trường dữ liệu phải có từ 8 đến 50 ký tự";
        public static final String SIZE100 = "trường dữ liệu phải có từ 8 đến 100 ký tự";
    }
}