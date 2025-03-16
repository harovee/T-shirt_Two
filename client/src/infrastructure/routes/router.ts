import {createRouter, createWebHistory, RouteRecordRaw} from "vue-router";
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import {ROLES} from "@/infrastructure/constants/role.ts";
import {useAuthStore} from "@/infrastructure/stores/auth.ts";

const routes: Array<RouteRecordRaw> = [
    {
        path: ROUTES_CONSTANTS.AUTHENTICATION.path,
        name: ROUTES_CONSTANTS.AUTHENTICATION.name,
        component: () => import("@/layout/authentication/TShirts.vue"),
        redirect: ROUTES_CONSTANTS.AUTHENTICATION.children.LOGIN,
        meta: {
            requiresAuth: false
        },
        children: [
            {
                path: ROUTES_CONSTANTS.AUTHENTICATION.children.LOGIN.path,
                name: ROUTES_CONSTANTS.AUTHENTICATION.children.LOGIN.name,
                component: () => import('@/layout/authentication/Login.vue'),
                meta: {
                    requiresAuth: false
                },
            },
            {
                path: ROUTES_CONSTANTS.AUTHENTICATION.children.REGISTER.path,
                name: ROUTES_CONSTANTS.AUTHENTICATION.children.REGISTER.name,
                component: () => import('@/layout/authentication/Register.vue'),
                meta: {
                    requiresAuth: false
                },
            },
        ],
    },
    {
        path: ROUTES_CONSTANTS.USER.path,
        name: ROUTES_CONSTANTS.USER.name,
        component: () => import("@/layout/user/User.vue"),
        meta: {
            requiresRole: ROLES.USER,
            requiresAuth: true
        },
        redirect: ROUTES_CONSTANTS.USER.children.STATISTIC,
        children: [
            {
                path: ROUTES_CONSTANTS.USER.children.STATISTIC.path,
                name: ROUTES_CONSTANTS.USER.children.STATISTIC.name,
                component: () => import('@/page/user/statistic/Statistic.vue'),
                meta: {
                    requiresRole: ROLES.USER,
                    requiresAuth: true
                },
            },
        ],
    },
    {
        path: ROUTES_CONSTANTS.ADMIN.path,
        name: ROUTES_CONSTANTS.ADMIN.name,
        component: () => import("@/layout/admin/Admin.vue"),
        meta: {
            requiresRole: ROLES.ADMIN,
            requiresAuth: true
        },
        redirect: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD,
        children: [
            {
                path: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.path,
                name: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name,
                component: () => import('@/page/admin/statistic/Dashboard.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.STATISTIC.path,
                name: ROUTES_CONSTANTS.ADMIN.children.STATISTIC.name,
                component: () => import('@/page/admin/statistic/Statistic.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.POINT_OF_SALE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.POINT_OF_SALE.name,
                component: () => import('@/page/admin/point.of.sale/PointOfSale.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.path,
                name: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.name,
                component: () => import('@/page/admin/bill/bill/AdminBill.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_DETAIL.path,
                name: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_DETAIL.name,
                component: () => import('@/page/admin/bill/billdetail/AdminBillDetail.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_REFUND.path,
                name: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_REFUND.name,
                component: () => import('@/page/admin/bill/refund/AdminBillRefund.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_REFUND_DETAIL.path,
                name: "admin-bill-refund-detail",
                component: () => import('@/page/admin/bill/refund/RefundDetail.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT_DETAIL.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT_DETAIL.name,
                component: () => import('@/page/admin/product/product-detail/ProductDetail.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.path + '/:id',
                name: 'product-detail-id',
                component: () => import('@/page/admin/product/product-detail/ProductDetail.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.CATEGORY.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.CATEGORY.name,
                component: () => import('@/page/admin/product/category/Category.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.name,
                component: () => import('@/page/admin/product/product/Product.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.CREATE_PRODUCT.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.CREATE_PRODUCT.name,
                component: () => import('@/page/admin/product/product/CreateProduct.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.TRADEMARK.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.TRADEMARK.name,
                component: () => import('@/page/admin/product/trademark/Trademark.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.MATERIAL.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.MATERIAL.name,
                component: () => import('@/page/admin/product/material/Material.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLLAR.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLLAR.name,
                component: () => import('@/page/admin/product/collar/Collar.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SIZE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SIZE.name,
                component: () => import('@/page/admin/product/size/Size.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLOR.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLOR.name,
                component: () => import('@/page/admin/product/color/Color.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.STYLE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.STYLE.name,
                component: () => import('@/page/admin/product/style/Style.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SLEEVE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SLEEVE.name,
                component: () => import('@/page/admin/product/sleeve/Sleeve.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.FEATURE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.FEATURE.name,
                component: () => import('@/page/admin/product/feature/Feature.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PATTERN.path,
                name: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PATTERN.name,
                component: () => import('@/page/admin/product/pattern/Pattern.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.SALE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.SALE.name,
                component: () => import('@/page/admin/sale/Sale.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.VOUCHER.path,
                name: ROUTES_CONSTANTS.ADMIN.children.VOUCHER.name,
                component: () => import('@/page/admin/voucher/Voucher.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.VOUCHER_ADD.path,
                name: ROUTES_CONSTANTS.ADMIN.children.VOUCHER_ADD.name,
                component: () => import('@/page/admin/voucher/AddVoucher.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.VOUCHER_DETAIL.path,
                name: ROUTES_CONSTANTS.ADMIN.children.VOUCHER_DETAIL.name,
                component: () => import('@/page/admin/voucher/VoucherDetail.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.SALE_DETAIL.path,
                name: ROUTES_CONSTANTS.ADMIN.children.SALE_DETAIL.name,
                component: () => import('@/page/admin/sale/SaleDetail.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.SALE_ADD.path,
                name: ROUTES_CONSTANTS.ADMIN.children.SALE_ADD.name,
                component: () => import('@/page/admin/sale/AddSale.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.STAFF.path,
                name: ROUTES_CONSTANTS.ADMIN.children.STAFF.name,
                component: () => import('@/page/admin/staff/Staff.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.STAFF_CREATE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.STAFF_CREATE.name,
                component: () => import('@/page/admin/staff/StaffCreate.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.STAFF_DETAIL.path,
                name: ROUTES_CONSTANTS.ADMIN.children.STAFF_DETAIL.name,
                component: () => import('@/page/admin/staff/StaffDetail.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.CLIENT.path,
                name: ROUTES_CONSTANTS.ADMIN.children.CLIENT.name,
                component: () => import('@/page/admin/client/Client.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.CLIENT_CREATE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.CLIENT_CREATE.name,
                component: () => import('@/page/admin/client/ClientCreate.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
            {
                path: ROUTES_CONSTANTS.ADMIN.children.CLIENT_DETAIL.path,
                name: ROUTES_CONSTANTS.ADMIN.children.CLIENT_DETAIL.name,
                component: () => import('@/page/admin/client/ClientDetail.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
        ],
    },
    {
        path: ROUTES_CONSTANTS.CLIENT.path,
        name: ROUTES_CONSTANTS.CLIENT.name,
        component: () => import('@/layout/client/Client.vue'),
        redirect: ROUTES_CONSTANTS.CLIENT.children.HOME.path,
        meta: {
            requiresAuth: false
        },
        children: [
            {
                path: ROUTES_CONSTANTS.CLIENT.children.HOME.path,
                name: ROUTES_CONSTANTS.CLIENT.children.HOME.name,
                component: () => import('@/page/client/menu/Home.vue'),
                meta: {
                    requiresAuth: false
                },
            },
            {
                path: ROUTES_CONSTANTS.CLIENT.children.PRODUCTS.path,
                name: ROUTES_CONSTANTS.CLIENT.children.PRODUCTS.name,
                component: () => import('@/page/client/menu/Products.vue'),
                meta: {
                    requiresAuth: false
                },
            },
            {
                path: ROUTES_CONSTANTS.CLIENT.children.PRODUCTS_DETAIL.path,
                name: ROUTES_CONSTANTS.CLIENT.children.PRODUCTS_DETAIL.name,
                component: () => import('@/page/client/products/ProductDetail.vue'),
                meta: {
                    requiresAuth: false
                },
            },
            {
                path: ROUTES_CONSTANTS.CLIENT.children.CHECKOUT.path,
                name: ROUTES_CONSTANTS.CLIENT.children.CHECKOUT.name,
                component: () => import('@/page/client/payment/Checkout.vue'),
                meta: {
                    requiresAuth: false
                },
            },
            {
                path: ROUTES_CONSTANTS.CLIENT.children.ABOUT.path,
                name: ROUTES_CONSTANTS.CLIENT.children.ABOUT.name,
                component: () => import('@/page/client/menu/About.vue'),
                meta: {
                    requiresAuth: false
                },
            },
            {
                path: ROUTES_CONSTANTS.CLIENT.children.CONTACT.path,
                name: ROUTES_CONSTANTS.CLIENT.children.CONTACT.name,
                component: () => import('@/page/client/menu/Contact.vue'),
                meta: {
                    requiresAuth: false
                },
            },
            {
                path: ROUTES_CONSTANTS.CLIENT.children.CART.path,
                name: ROUTES_CONSTANTS.CLIENT.children.CART.name,
                component: () => import('@/page/client/cart/Cart.vue'),
                meta: {
                    requiresAuth: false
                },
            },
            {
                path: ROUTES_CONSTANTS.CLIENT.children.CHECKOUT.path,
                name: ROUTES_CONSTANTS.CLIENT.children.CHECKOUT.name,
                component: () => import('@/page/client/payment/Checkout.vue'),
                meta: {
                    requiresAuth: false
                },
            },
            {
                path: ROUTES_CONSTANTS.CLIENT.children.MY_ORDER.path,
                name: ROUTES_CONSTANTS.CLIENT.children.MY_ORDER.name,
                component: () => import('@/page/client/my-order/MyOrder.vue'),
                meta: {
                    requiresAuth: false
                },
            },
            {
                path: ROUTES_CONSTANTS.CLIENT.children.MY_ORDER_DETAIL.path,
                name: ROUTES_CONSTANTS.CLIENT.children.MY_ORDER_DETAIL.name,
                component: () => import('@/page/client/my-order/MyOrderDetail.vue'),
                meta: {
                    requiresAuth: false
                },
            },
            {
                path: ROUTES_CONSTANTS.CLIENT.children.COMPLETE_PAYMENT.path,
                name: ROUTES_CONSTANTS.CLIENT.children.COMPLETE_PAYMENT.name,
                component: () => import('@/page/client/payment/CompletePayment.vue'),
                meta: {
                    requiresAuth: false
                },
            },
        ],
    },
    {
        path: ROUTES_CONSTANTS.REDIRECT.path,
        name: ROUTES_CONSTANTS.REDIRECT.name,
        component: () => import("@/infrastructure/routes/guard/Redirect.vue"),
        meta: {requiresAuth: false},
    },
    {
        path: ROUTES_CONSTANTS.NOT_FOUND.path,
        name: ROUTES_CONSTANTS.NOT_FOUND.name,
        component: () => import("@/page/exception/404/NotFound.vue"),
        meta: {requiresAuth: false},
    },
    {
        path: ROUTES_CONSTANTS.COMMON.children.STATISTIC.path,
        name: ROUTES_CONSTANTS.COMMON.children.STATISTIC.name,
        component: () => import('@/page/admin/statistic/Statistic.vue'),
        meta: {
            requiresAuth: false
        },
    },
    {
        path: ROUTES_CONSTANTS.PAYMENT.children.path,
        name: ROUTES_CONSTANTS.PAYMENT.children.name,
        component: () => import('@/page/client/payment/PaymentReturnUrl.vue'),
        meta: {requiresAuth: false},
    },
];

const route = createRouter({
    history: createWebHistory(),
    routes,
});

route.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    const requiresAuth = to.matched && Array.isArray(to.matched) && to.matched.some((record) => record?.meta?.requiresAuth);
    const requiresRole = to.matched && Array.isArray(to.matched) && to.matched.some((record) => record?.meta?.requiresRole);
    const userRole = authStore?.user?.roleCode;
    if (userRole === null && requiresAuth) {
        next({name: ROUTES_CONSTANTS.AUTHENTICATION.children.LOGIN.name});
    } else if (requiresAuth && !authStore.isAuthenticated) {
        next({name: ROUTES_CONSTANTS.AUTHENTICATION.children.LOGIN.name});
    } else if (requiresRole && (!userRole || userRole !== to.meta.requiresRole)) {
        next({name: ROUTES_CONSTANTS.AUTHENTICATION.children.LOGIN.name});
    } else {
        next();
    }
});

export default route;
