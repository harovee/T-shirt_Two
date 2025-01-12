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
        redirect: ROUTES_CONSTANTS.ADMIN.children.STATISTIC,
        children: [
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
                path: ROUTES_CONSTANTS.ADMIN.children.SALE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.SALE.name,
                component: () => import('@/page/admin/sale/Sale.vue'),
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
                path: ROUTES_CONSTANTS.ADMIN.children.EMPLOYEE.path,
                name: ROUTES_CONSTANTS.ADMIN.children.EMPLOYEE.name,
                component: () => import('@/page/admin/employee/Employee.vue'),
                meta: {
                    requiresRole: ROLES.ADMIN,
                    requiresAuth: true
                },
            },
        ],
    },
    {
        path: ROUTES_CONSTANTS.EMPLOYEE.path,
        name: ROUTES_CONSTANTS.EMPLOYEE.name,
        redirect: ROUTES_CONSTANTS.EMPLOYEE.children.HOME,
        meta: {
            requiresAuth: false
        },
        children: [
            {
                path: ROUTES_CONSTANTS.EMPLOYEE.children.HOME.path,
                name: ROUTES_CONSTANTS.EMPLOYEE.children.HOME.name,
                component: () => import('@/layout/employee/Employee.vue'),
                meta: {
                    requiresAuth: false
                },
            },
        ],
    },
    {
        path: ROUTES_CONSTANTS.EMPLOYEE.path,
        name: ROUTES_CONSTANTS.EMPLOYEE.name,
        redirect: ROUTES_CONSTANTS.EMPLOYEE.children.HOME,
        meta: {
            requiresAuth: false
        },
        children: [
            {
                path: ROUTES_CONSTANTS.EMPLOYEE.children.HOME.path,
                name: ROUTES_CONSTANTS.EMPLOYEE.children.HOME.name,
                component: () => import('@/layout/employee/Employee.vue'),
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
