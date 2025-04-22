export const ROUTES_CONSTANTS = {
    AUTHENTICATION: {
        path: "/authentication",
        name: "T-Shirt Two",
        children: {
            LOGIN: {
                path: "login",
                name: "login",
            },
            REGISTER: {
                path: "register",
                name: "register",
            },
            FORGOT_PASSWORD: {
                path: "forgot-password",
                name: "forgot-password",
            },
        }
    },
    ADMIN: {
        path: "/admin",
        name: "admin",
        children: {
            DASHBOARD: {
                path: "dashboard",
                name: "dashboard",
            },
            STATISTIC: {
                path: "statistic",
                name: "admin-statistic",
            },
            POINT_OF_SALE: {
                path: "point-of-sale",
                name: "admin-point-of-sale",
            },
            PRODUCTS: {
                name: "admin-products",
                children: {
                    PRODUCT_DETAIL: {
                        path: "product-detail",
                        name: "admin-product-detail",
                    },
                    CREATE_PRODUCT: {
                        path: "create-product",
                        name: "admin-create-product",
                    },
                    CATEGORY: {
                        path: "category",
                        name: "admin-category",
                    },
                    PRODUCT: {
                        path: "product",
                        name: "admin-product",
                    },
                    TRADEMARK: {
                        path: "trademark",
                        name: "admin-trademark",
                    },
                    MATERIAL: {
                        path: "material",
                        name: "admin-material",
                    },
                    COLLAR: {
                        path: "collar",
                        name: "admin-collar",
                    },
                    SIZE: {
                        path: "size",
                        name: "admin-size",
                    },
                    COLOR: {
                        path: "color",
                        name: "admin-color",
                    },
                    FEATURE: {
                        path: "feature",
                        name: "admin-feature",
                    },
                    STYLE: {
                        path: "style",
                        name: "admin-style",
                    },
                    SLEEVE: {
                        path: "sleeve",
                        name: "admin-sleeve",
                    },
                    PATTERN: {
                        path: "pattern",
                        name: "admin-pattern",
                    }
                }
            },
            SALE: {
                path: "sale",
                name: "admin-sale",
            },
            SALE_ADD: {
                path: "sale/add",
                name: "admin-sale-add",
            },
            SALE_DETAIL: {
                path: "sale/:id",
                name: "admin-sale-detail",
            },
            STAFF: {
                path: "staff",
                name: "admin-staff",
            },
            STAFF_CREATE: {
                path: "staff/create",
                name: "admin-staff-create",
            },
            STAFF_DETAIL: {
                path: "staff/:id",
                name: "admin-staff-detail",
            },
            BILL: {
                path: "bill",
                name: "admin-bill",
                children: {
                    BILL_MANAGEMENT: {
                        path: "bill-management",
                        name: "admin-bill-management",
                    },
                    BILL_DETAIL: {
                        path: "bill-detail",
                        name: "admin-bill-detail",
                    },
                    // BILL_REFUND: {
                    //     path: "refund",
                    //     name: "admin-bill-refund",
                    // },
                    // BILL_REFUND_DETAIL: {
                    //     path: "refund/:maHoaDon",
                    //     name: "admin-bill-refund-detail",
                    // },
                }
            },
            PAYMENT: {
                path: "/payment",
                name: "admin-payment",
                children: {
                    CUSTOMER: {
                        path: "khach-hang",
                        name: "admin-khach-hang",
                    },
                    CUSTOMER_DETAIL: {
                        path: "khach-hang/:id",
                        name: "admin-khach-hang-detail",
                    },
                    VOUCHER_PAY: {
                        path: "voucher",
                        name: "admin-voucherpay",
                    },
                    VOUCHER_PAY_DETAIL: {
                        path: "voucher/:id",
                        name: "admin-voucherpay-detail",
                    }
                }
            },
            VOUCHER: {
                path: "voucher",
                name: "admin-voucher",
            },
            VOUCHER_ADD: {
                path: "voucher/add",
                name: "admin-voucher-add",   
            },
            VOUCHER_DETAIL: {
                path: "voucher/:id",
                name: "admin-voucher-detail",   
            },
            CLIENT: {
                path: "client",
                name: "admin-client",
            },
            CLIENT_CREATE: {
                path: "client/create",
                name: "admin-client-create",
            },
            CLIENT_DETAIL: {
                path: "client/:id",
                name: "admin-client-detail",
            }
        },
    },
    USER: {
        path: "/user",
        name: "user",
        children: {
            STATISTIC: {
                path: "statistic",
                name: "user-statistic",
            },
            PRODUCT: {
                path: "product",
                name: "user-product",
            },
        },
    },
    CLIENT: {
        path: "/",
        name: "client",
        children: {
            HOME: {
                path: "home",
                name: "client-home",
            },
            PRODUCTS: {
                path: "products",
                name: "client-products"
            },
            PRODUCTS_DETAIL: {
                path: "products/:id",
                name: "client-product-detail"
            },
            ABOUT: {
                path: "about",
                name: "client-about"
            },
            CONTACT: {
                path: "contact",
                name: "client-contact"
            },
            CART: {
                path: "cart",
                name: "client-cart"
            },
            CHECKOUT: {
                path: "check-out",
                name: "client-check-out"
            },
            MY_ORDER: {
                path:"my-order",
                name: "client-my-order"
            },
            MY_ORDER_DETAIL: {
                path: "my-order-detail",
                name: "client-my-order-detail",
            },
            COMPLETE_PAYMENT: {
                path: "complete-payment",
                name: "client-complete-payment"
            }
        },
    },
    NOT_FOUND: {
        path: "/:pathMatch(.*)*",
        name: "NotFound",
    },
    REDIRECT: {
        path: "/redirect",
        name: "redirect",
    },
    COMMON: {
        children: {
            STATISTIC: {
                path: "/cm-statistic",
                name: "cm-statistic",
            },
            MB_EXAMPLE: {
                path: "/mb-example",
                name: "mb-example"
            }
        }
    },
    PAYMENT: {
        children: {
            VNPay: {    
                path:"/payment/vn-pay-callback",
                name: "payment-vnpay"
            },
            MoMo: {    
                path:"/payment/momo-callback",
                name: "payment-momo"
            },
    }
    }
};