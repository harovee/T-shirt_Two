<template>
  <a-layout>
    <a-layout-sider
      theme="light"
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      :width="250"
    >
      <div class="w-full flex items-center justify-center">
        <img class="w-full" :src="logo" alt="logo" />
      </div>
      <a-menu
        v-model:openKeys="openKeys"
        v-model:selectedKeys="selectedKeys"
        mode="inline"
        :items="items"
        @click="handleClick"
      ></a-menu>
    </a-layout-sider>

    <a-layout>
      <a-layout-header class="pl-3 mt-1" style="background-color: white">
        <div class="user-info flex items-center justify-between">
          <!-- Nút thu gọn -->
          <div class="cursor-pointer" @click="collapsed = !collapsed">
            <component
              :is="collapsed ? MenuUnfoldOutlined : MenuFoldOutlined"
              class="text-xl"
            />
          </div>

          <div class="flex items-center gap-4 justify-end">
            <!-- chuông thông báo -->

            <a-popover
              trigger="click"
              placement="bottomRight"
              overlay-class-name="custom-popover"
            >
              <template #content>
                <div class="w-[350px] max-h-[400px] overflow-auto rounded-lg">
                  <div
                    class="flex items-center justify-between px-4 py-2 border-b"
                  >
                    <span class="font-medium text-gray-700">Thông báo</span>
                    <button
                      class="text-sm text-gray-400 hover:text-red-500"
                      @click="handleDeleteAllNotification"
                    >
                      Xóa tất cả
                    </button>
                  </div>
                  <notification-list :messages="messages" />
                </div>
              </template>
              <a-badge :count="messages.length" :offset="[-5, 5]">
                <BellOutlined class="text-xl cursor-pointer" />
              </a-badge>
            </a-popover>

            <!-- user info -->
            <a-dropdown placement="bottomRight" arrow>
              <div class="flex items-center cursor-pointer">
                <a-avatar
                  v-if="userInfo?.profilePicture"
                  :src="userInfo?.profilePicture"
                  size="large"
                >
                  {{ userInfo?.userName }}
                </a-avatar>
                <span class="ml-2 truncate">
                  {{ userInfo?.userName }}
                </span>
              </div>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="logout" @click="handleLogout">
                    Đăng xuất
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </div>
      </a-layout-header>

      <a-layout-content class="mx-4">
        <div class="min-h-[calc(100vh-9.5rem)] bg-white">
          <router-view />
        </div>
      </a-layout-content>
      <a-layout-footer class="text-center">
        T-Shirts Two ©2024 Created by Team 2
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script lang="ts" setup>
import logo from "@/assets/image/logo/T-ShirtTwo.png";
import { computed, h, reactive, ref, VueElement } from "vue";
import { useRouter } from "vue-router";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path.ts";
import { ROLES } from "@/infrastructure/constants/role.ts";
import { useAuthStore } from "@/infrastructure/stores/auth.ts";
import {
  AppstoreOutlined,
  MailOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  BarChartOutlined,
  FileTextOutlined,
  PercentageOutlined,
  TagOutlined,
  TeamOutlined,
  UserOutlined,
  ShoppingCartOutlined,
  BellOutlined,
} from "@ant-design/icons-vue";
import { ItemType, MenuProps } from "ant-design-vue";
import { useDeleteAllNotification } from "@/websocket/services/notification.action";
import NotificationList from "@/websocket/view/NotificationList.vue";

const openKeys = ref(["sub1"]);

function getItem(
  label: VueElement | string,
  key: string,
  icon?: any,
  children?: ItemType[],
  type?: "group"
): ItemType {
  return {
    key,
    icon,
    children,
    label,
    type,
  } as ItemType;
}

const auth = useAuthStore();
const isAdmin = auth?.user?.roleCode === ROLES.ADMIN;
const userInfo = computed(() => auth.user);
const collapsed = ref<boolean>(false);
const router = useRouter();

const selectedKeys = ref(
  isAdmin
    ? [ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name]
    : [ROUTES_CONSTANTS.ADMIN.children.POINT_OF_SALE.name]
);

const { mutate: deleteAllNotification } = useDeleteAllNotification();

const handleLogout = () => {
  auth.logout();
  router.push(ROUTES_CONSTANTS.AUTHENTICATION.path);
};

const menuItems = ref([
  {
    key: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name,
    path: ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.STATISTIC.name,
    path: ROUTES_CONSTANTS.ADMIN.children.STATISTIC.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.name,
    path: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT_DETAIL.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT_DETAIL.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.CATEGORY.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.CATEGORY.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.TRADEMARK.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.TRADEMARK.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.MATERIAL.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.MATERIAL.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLLAR.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLLAR.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SIZE.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SIZE.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLOR.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLOR.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.FEATURE.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.FEATURE.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PATTERN.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PATTERN.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.STYLE.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.STYLE.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SLEEVE.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SLEEVE.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.SALE.name,
    path: ROUTES_CONSTANTS.ADMIN.children.SALE.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.STAFF.name,
    path: ROUTES_CONSTANTS.ADMIN.children.STAFF.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.CLIENT.name,
    path: ROUTES_CONSTANTS.ADMIN.children.CLIENT.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.VOUCHER.name,
    path: ROUTES_CONSTANTS.ADMIN.children.VOUCHER.path,
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.POINT_OF_SALE.name,
    path: ROUTES_CONSTANTS.ADMIN.children.POINT_OF_SALE.path,
  },
]);

const items: ItemType[] = reactive([
  isAdmin &&
    getItem("Tổng quan", ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name, () =>
      h(BarChartOutlined)
    ),
  getItem(
    "Sản Phẩm",
    ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.name,
    () => h(AppstoreOutlined),
    [
      // getItem('Sản phẩm chi tiết', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT_DETAIL.name),
      getItem(
        "Sản phẩm",
        ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.name
      ),
      getItem(
        "Danh mục",
        ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.CATEGORY.name
      ),
      getItem(
        "Thương hiệu",
        ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.TRADEMARK.name
      ),
      getItem(
        "Chất liệu",
        ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.MATERIAL.name
      ),
      getItem(
        "Cổ áo",
        ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLLAR.name
      ),
      getItem(
        "Tay áo",
        ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SLEEVE.name
      ),
      getItem(
        "Kích cỡ",
        ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SIZE.name
      ),
      getItem(
        "Màu sắc",
        ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLOR.name
      ),
      getItem(
        "Tính năng",
        ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.FEATURE.name
      ),
      getItem(
        "Kiểu dáng",
        ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.STYLE.name
      ),
      getItem(
        "Họa tiết",
        ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PATTERN.name
      ),
    ]
  ),

  getItem(
    "Bán tại quầy",
    ROUTES_CONSTANTS.ADMIN.children.POINT_OF_SALE.name,
    () => h(ShoppingCartOutlined)
  ),

  getItem(
    "Hóa đơn",
    ROUTES_CONSTANTS.ADMIN.children.BILL.name,
    () => h(FileTextOutlined),
    [
      // getItem('Bán hàng', ROUTES_CONSTANTS.ADMIN.children.STATISTIC.name, null),
      getItem(
        "Quản lý hóa đơn",
        ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.name,
        null
      ),
      // getItem('Trả hàng', ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_REFUND.name, null),
    ]
  ),

  getItem("Đợt giảm giá", ROUTES_CONSTANTS.ADMIN.children.SALE.name, () =>
    h(PercentageOutlined)
  ),

  getItem("Phiếu giảm giá", ROUTES_CONSTANTS.ADMIN.children.VOUCHER.name, () =>
    h(TagOutlined)
  ),

  isAdmin &&
    getItem("Nhân viên", ROUTES_CONSTANTS.ADMIN.children.STAFF.name, () =>
      h(UserOutlined)
    ),

  getItem("Khách hàng", ROUTES_CONSTANTS.ADMIN.children.CLIENT.name, () =>
    h(TeamOutlined)
  ),
  isAdmin &&
    getItem("Thống kê", ROUTES_CONSTANTS.ADMIN.children.STATISTIC.name, () =>
      h(BarChartOutlined)
    ),
]);

const handleClick: MenuProps["onClick"] = (e) => {
  selectedKeys.value = [String.fromCharCode(e.value)];
  const targetItem = menuItems.value.find((item) => item.key === e.key);
  if (targetItem && targetItem.path) {
    const path = ROUTES_CONSTANTS.ADMIN.path + "/" + targetItem.path;
    router.push(path);
  }
};

const handleDeleteAllNotification = async () => {
  await deleteAllNotification();
};

import { useNotificationSocket } from "@/websocket/config/useNotificationSocket";
const { messages } = useNotificationSocket();
// console.log(messages);
</script>

<style scoped>
/* .custom-dropdown-notification {
  width: 320px;
  max-height: 400px;
  overflow-y: auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  padding: 12px;
} */

/* Trong component notification-list.vue */
/* .notification-item {
  padding: 10px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.3s;
}

.notification-item:hover {
  background-color: #f5f5f5;
}

.notification-title {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 4px;
}

.notification-content {
  font-size: 13px;
  color: #666;
} */
</style>