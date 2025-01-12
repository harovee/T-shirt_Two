package com.shop.server.infrastructure.constants.module;

public class MappingConstant {

    public static final String USER = "/user";
    public static final String ADMIN = "/admin";
    public static final String EMPLOYEE = "/employee";

    public static final String EMBED = "/embed";

    public static final String API_VERSION_PREFIX = "/api/v1";
    public static final String API_COMMON = API_VERSION_PREFIX + "/common";

    public static final String API_GUEST_GUARD_ACCOUNT = "/api/guest-guard/account";

    public static final String API_USER_PREFIX = API_VERSION_PREFIX + USER;
    public static final String API_ADMIN_PREFIX = API_VERSION_PREFIX + ADMIN;
    public static final String API_EMPLOYEE_PREFIX = API_VERSION_PREFIX + EMPLOYEE;

    public static final String API_EMBED_PREFIX = API_VERSION_PREFIX + EMBED;

    public static final String API_USER_ANOTHER = API_USER_PREFIX + "/another";
    public static final String API_USER_PRODUCT = API_USER_PREFIX + "/product";

    // admin
    public static final String API_ADMIN_ANOTHER = API_ADMIN_PREFIX + "/another";
    public static final String API_ADMIN_PRODUCT = API_ADMIN_PREFIX + "/product";
    public static final String API_ADMIN_EMPLOYEE = API_ADMIN_PREFIX + "/employee";
    public static final String API_ADMIN_VOUCHER = API_ADMIN_PREFIX + "/voucher";

    // employee
    public static final String API_EMPLOYEE_ANOTHER = API_EMPLOYEE_PREFIX + "/another";

    public static final String API_COMMON_ANOTHER = API_COMMON + "/another";

    /* AUTHENTICATION */
    public static final String API_AUTH_PREFIX = API_VERSION_PREFIX + "/auth";

    public static final String PATH_OAUTH2 = "/oauth2";

}
