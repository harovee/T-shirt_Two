const {VITE_BASE_URL_SERVER} = import.meta.env || {};

const {VITE_BASE_URL_CLIENT} = import.meta.env || {};

// API URL
export const API_URL = `${VITE_BASE_URL_SERVER}/api/v1` as string;

// DOMAIN
export const DOMAIN_BACKEND = `${VITE_BASE_URL_SERVER}` as string;

export const DOMAIN_FRONTEND = `${VITE_BASE_URL_CLIENT}` as string;

export const URL_OAUTH2_GOOGLE = `${DOMAIN_BACKEND}/oauth2/authorize/google?redirect_uri=` as string;
export const URL_OAUTH2_GITHUB = `${DOMAIN_BACKEND}/oauth2/authorize/github?redirect_uri=` as string;
export const URL_OAUTH2_FACEBOOK = `${DOMAIN_BACKEND}/oauth2/authorize/facebook?redirect_uri=` as string;

export const URL_FRONTEND = `${DOMAIN_FRONTEND}/redirect`;

// COMMON API
export const PREFIX_API_COMMON = `${API_URL}/common` as string;

// AUTH API
export const PREFIX_API_AUTH = `${API_URL}/auth` as string;
export const PREFIX_API_LOGIN = PREFIX_API_AUTH + `/login` as string;
export const PREFIX_API_LOGOUT = PREFIX_API_AUTH + `/logout` as string;
export const PREFIX_API_REGISTER = PREFIX_API_AUTH + `/register` as string;
export const PREFIX_API_FORGOT_PASSWORD = PREFIX_API_AUTH + `/forgot-password` as string;
export const PREFIX_API_REFRESH = PREFIX_API_AUTH + `/refresh` as string;


// ADMIN API
export const PREFIX_API_ADMIN_FEATURE = `${API_URL}/admin/feature` as string;
export const PREFIX_API_ADMIN_VOUCHER = `${API_URL}/admin/voucher` as string;
export const PREFIX_API_ADMIN_PRODUCT = `${API_URL}/admin/product` as string;
export const PREFIX_API_ADMIN_PRODUCT_DETAIL = `${API_URL}/admin/product-detail` as string;
export const PREFIX_API_ADMIN_MATERIAL = `${API_URL}/admin/material` as string;
export const PREFIX_API_ADMIN_SIZE = `${API_URL}/admin/size` as string;
export const PREFIX_API_ADMIN_COLLAR = `${API_URL}/admin/collar` as string;
export const PREFIX_API_ADMIN_PATTERN = `${API_URL}/admin/pattern` as string;
export const PREFIX_API_ADMIN_COLOR = `${API_URL}/admin/color` as string;
export const PREFIX_API_ADMIN_STYLE = `${API_URL}/admin/style` as string;
export const PREFIX_API_ADMIN_SLEEVE = `${API_URL}/admin/sleeve` as string;
export const PREFIX_API_ADMIN_TRADEMARK = `${API_URL}/admin/trademark` as string;
export const PREFIX_API_ADMIN_CATEGORY = `${API_URL}/admin/category` as string;
export const PREFIX_API_ADMIN_CLIENT = `${API_URL}/admin/client` as string;
export const PREFIX_API_ADMIN_SALE = `${API_URL}/admin/sale` as string;
export const API_ADMIN_BILL = `${API_URL}/admin/bill` as string;
export const API_ADMIN_COUNT_BILL = `${API_URL}/admin/bill/count-by-status` as string;
export const API_ADMIN_BILL_DETAIL = `${API_URL}/admin/bill-detail` as string;
export const API_ADMIN_BILL_HISTORY = `${API_URL}/admin/bill-history` as string;
export const API_ADMIN_PAY_HISTORY = `${API_URL}/admin/pay-history` as string;
export const PREFIX_API_ADMIN_POINT_OF_SALE = `${API_URL}/admin/point-of-sale` as string;
export const API_ADMIN_POINT_OF_SALE = `${API_URL}/admin/point-of-sale` as string;
export const API_ADMIN_PAYMENT = `${API_URL}/admin/payment` as string;
export const API_ADMIN_PAYMENT_METHOD = `${API_URL}/admin/payment-method` as string;
export const API_ADMIN_DELIVERY_PAYMENT = `${API_URL}/admin/delivery-payment` as string;
export const GHN_API_URL = "https://dev-online-gateway.ghn.vn/shiip/public-api";
export const GHN_TOKEN = "0c1a0015-d88e-11ef-98e4-42c0f54d535b";

export const PREFIX_API_ADMIN_STAFF = `${API_URL}/admin/staff` as string;

// Example
export const PREFIX_API_ADMIN_USER = `${API_URL}/admin/user` as string;
export const PREFIX_API_ADMIN_SONG = `${API_URL}/admin/song` as string;

// USER API
export const PREFIX_API_USER_FEATURE = `${API_URL}/admin/feature` as string;

// CLIENT API
export const PREFIX_API_CLIENT_FEATURE = `${API_URL}/client/feature` as string;

