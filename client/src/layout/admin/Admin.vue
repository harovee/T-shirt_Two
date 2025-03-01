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
        <img class="w-full" :src="logo" alt="logo"/>
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
          <div class="cursor-pointer" @click="collapsed = !collapsed">
            <component
                :is="collapsed ? MenuUnfoldOutlined : MenuFoldOutlined"
                class="text-xl"
            />
          </div>
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
      </a-layout-header>
      <a-layout-content class="mx-4">
        <div class="min-h-[calc(100vh-9.5rem)] bg-white">
          <router-view/>
        </div>
      </a-layout-content>
      <a-layout-footer class="text-center">
        T-Shirts Two ©2024 Created by Team 2
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script lang="ts" setup>
import logo from "@/assets/image/logo/T-ShirtTwo.png"
import {computed, h, reactive, ref, VueElement} from 'vue';
import {useRouter} from 'vue-router';
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import {useAuthStore} from "@/infrastructure/stores/auth.ts";
import {AppstoreOutlined, MailOutlined, MenuFoldOutlined, MenuUnfoldOutlined, BarChartOutlined,FileTextOutlined, PercentageOutlined, TagOutlined, TeamOutlined, UserOutlined, ShoppingCartOutlined} from '@ant-design/icons-vue';
import {ItemType, MenuProps} from "ant-design-vue";


const selectedKeys = ref([ROUTES_CONSTANTS.ADMIN.children.STATISTIC.name]);
const openKeys = ref(['sub1']);

function getItem(
    label: VueElement | string,
    key: string,
    icon?: any,
    children?: ItemType[],
    type?: 'group',
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
const userInfo = computed(() => auth.user);
const collapsed = ref<boolean>(false);
const router = useRouter();

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
    key: ROUTES_CONSTANTS.ADMIN.children.STATISTIC.name,
    path: ROUTES_CONSTANTS.ADMIN.children.STATISTIC.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.STATISTIC.name,
    path: ROUTES_CONSTANTS.ADMIN.children.STATISTIC.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.name,
    path: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT_DETAIL.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT_DETAIL.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.CATEGORY.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.CATEGORY.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.TRADEMARK.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.TRADEMARK.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.MATERIAL.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.MATERIAL.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLLAR.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLLAR.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SIZE.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SIZE.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLOR.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLOR.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.FEATURE.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.FEATURE.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PATTERN.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PATTERN.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.STYLE.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.STYLE.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SLEEVE.name,
    path: ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SLEEVE.path
  },
  {
    key: ROUTES_CONSTANTS.ADMIN.children.SALE.name,
    path: ROUTES_CONSTANTS.ADMIN.children.SALE.path,
  }
  ,
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
    path: ROUTES_CONSTANTS.ADMIN.children.POINT_OF_SALE.path
  },
]);

const items: ItemType[] = reactive([
  getItem('Tổng quan', ROUTES_CONSTANTS.ADMIN.children.DASHBOARD.name, () => h(BarChartOutlined)),
  getItem('Thống kê', ROUTES_CONSTANTS.ADMIN.children.STATISTIC.name, () => h(BarChartOutlined)),

  getItem('Bán tại quầy', ROUTES_CONSTANTS.ADMIN.children.POINT_OF_SALE.name, () => h(ShoppingCartOutlined)),

  getItem('Hóa đơn', ROUTES_CONSTANTS.ADMIN.children.BILL.name, () => h(FileTextOutlined), [
    // getItem('Bán hàng', ROUTES_CONSTANTS.ADMIN.children.STATISTIC.name, null),
    getItem('Quản lý hóa đơn', ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.name, null),
  ]),

  getItem('Sản Phẩm', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.name, () => h(AppstoreOutlined), [
    // getItem('Sản phẩm chi tiết', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT_DETAIL.name),
    getItem('Sản phẩm', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.name),
    getItem('Danh mục', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.CATEGORY.name),
    getItem('Thương hiệu', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.TRADEMARK.name),
    getItem('Chất liệu', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.MATERIAL.name),
    getItem('Cổ áo', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLLAR.name),
    getItem('Tay áo', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SLEEVE.name),
    getItem('Kích cỡ', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SIZE.name),
    getItem('Màu sắc', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.COLOR.name),
    getItem('Tính năng', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.FEATURE.name),
    getItem('Kiểu dáng', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.STYLE.name),
    getItem('Họa tiết', ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PATTERN.name)
  ]),

  getItem('Đợt giảm giá', ROUTES_CONSTANTS.ADMIN.children.SALE.name, () => h(PercentageOutlined)),

  getItem('Phiếu giảm giá', ROUTES_CONSTANTS.ADMIN.children.VOUCHER.name, () => h(TagOutlined)),

  getItem('Nhân viên', ROUTES_CONSTANTS.ADMIN.children.STAFF.name, () => h(UserOutlined)),

  getItem('Khách hàng', ROUTES_CONSTANTS.ADMIN.children.CLIENT.name, () => h(TeamOutlined)),

  

]);

const handleClick: MenuProps['onClick'] = e => {
  selectedKeys.value = [String.fromCharCode(e.value)];
  const targetItem = menuItems.value.find(item => item.key === e.key);
  if (targetItem && targetItem.path) {
    const path = ROUTES_CONSTANTS.ADMIN.path + '/' + targetItem.path;
    router.push(path);
  }
};

</script>

<script lang="ts">
export default {
  name: "AdminPage",
};
</script>
