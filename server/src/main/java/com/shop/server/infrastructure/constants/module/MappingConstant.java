package com.shop.server.infrastructure.constants.module;

public class MappingConstant {

    public static final String USER = "/user";
    public static final String ADMIN = "/admin";
    public static final String CLIENT = "/client";

    public static final String EMBED = "/embed";

    public static final String API_VERSION_PREFIX = "/api/v1";
    public static final String API_COMMON = API_VERSION_PREFIX + "/common";

    public static final String API_GUEST_GUARD_ACCOUNT = "/api/guest-guard/account";

    public static final String API_USER_PREFIX = API_VERSION_PREFIX + USER;
    public static final String API_ADMIN_PREFIX = API_VERSION_PREFIX + ADMIN;
    public static final String API_CLIENT_PREFIX = API_VERSION_PREFIX + CLIENT;

    public static final String API_EMBED_PREFIX = API_VERSION_PREFIX + EMBED;

    public static final String API_USER_ANOTHER = API_USER_PREFIX + "/another";
    public static final String API_USER_PRODUCT = API_USER_PREFIX + "/product";

    // admin
    public static final String API_ADMIN_ANOTHER = API_ADMIN_PREFIX + "/another";
    public static final String API_ADMIN_PRODUCT = API_ADMIN_PREFIX + "/product";
    public static final String API_ADMIN_CLIENT = API_ADMIN_PREFIX + "/client";
    public static final String API_ADMIN_STAFF = API_ADMIN_PREFIX + "/staff";
    public static final String API_ADMIN_PRODUCT_DETAIL = API_ADMIN_PREFIX + "/product-detail";
    public static final String API_ADMIN_MATERIAL = API_ADMIN_PREFIX + "/material";
    public static final String API_ADMIN_COLLAR = API_ADMIN_PREFIX + "/collar"; // Cổ áo
    public static final String API_ADMIN_PATTERN = API_ADMIN_PREFIX + "/pattern"; //Họa tiết
    public static final String API_ADMIN_SIZE = API_ADMIN_PREFIX + "/size";
    public static final String API_ADMIN_COLOR = API_ADMIN_PREFIX + "/color";
    public static final String API_ADMIN_STYLE = API_ADMIN_PREFIX + "/style";
    public static final String API_ADMIN_SLEEVE = API_ADMIN_PREFIX + "/sleeve"; // Tay áo
    public static final String API_ADMIN_TRADEMARK = API_ADMIN_PREFIX + "/trademark"; // Thương hiệu
    public static final String API_ADMIN_FEATURE = API_ADMIN_PREFIX + "/feature"; // Tính năng
    public static final String API_ADMIN_CATEGORY = API_ADMIN_PREFIX + "/category"; // DAnh mục
    public static final String API_ADMIN_PROMOTION = API_ADMIN_PREFIX + "/sale";
    public static final String API_ADMIN_BILL = API_ADMIN_PREFIX + "/bill";
    public static final String API_ADMIN_BILL_DETAIL = API_ADMIN_PREFIX + "/bill-detail";
    public static final String API_ADMIN_BILL_HISTORY = API_ADMIN_PREFIX + "/bill-history";
    public static final String API_ADMIN_PAY_HISTORY = API_ADMIN_PREFIX + "/pay-history";
    public static final String API_ADMIN_VOUCHER = API_ADMIN_PREFIX + "/voucher";
    public static final String API_ADMIN_IMAGE = API_ADMIN_PREFIX + "/image";

    // employee
    public static final String API_CLIENT_ANOTHER = API_CLIENT_PREFIX + "/another";

    public static final String API_COMMON_ANOTHER = API_COMMON + "/another";

    /* AUTHENTICATION */
    public static final String API_AUTH_PREFIX = API_VERSION_PREFIX + "/auth";

    public static final String PATH_OAUTH2 = "/oauth2";

}
