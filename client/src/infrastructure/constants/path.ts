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
            STATISTIC: {
                path: "statistic",
                name: "admin-statistic",
            },
            PRODUCTS: {
                name: "admin-products",
                children: {
                    PRODUCT_DETAIL: {
                        path: "product-detail",
                        name: "admin-product-detail",
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
                }
            },
            SALE: {
                path: "sale",
                name: "admin-sale",
            },
            STAFF: {
                path: "staff",
                name: "admin-staff",
            },
            BILL: {
                path: "bill",
                name: "admin-bill",
            },
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
    EMPLOYEE: {
        path: "/",
        name: "employee",
        children: {
            HOME: {
                path: "home",
                name: "employee-home",
            },
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
};
