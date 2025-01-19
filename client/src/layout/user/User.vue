<template>
  <a-layout>
    <a-layout-sider
        theme="light"
        class="w[300px]"
        v-model:collapsed="collapsed"
        :trigger="null"
        collapsible
    >
      <!-- <div class="logo" style="width: 100%; height: 70px">LOGO</div> -->
      <a-menu v-model:selectedKeys="selectedKeys" theme="light" mode="inline">
        <template v-for="item in menuItems">
          <a-menu-item
              :key="item.key"
              v-if="!item.children"
              @click="handleMenuClick(item.key)"
          >
            <component :is="item.icon"/>
            <span>{{ item.label }}</span>
          </a-menu-item>

          <a-sub-menu v-else>
            <template #title>
              <span>
                <component :is="item.icon"/>
                <span>{{ item.label }}</span>
              </span>
            </template>

            <a-menu-item
                v-for="child in item.children"
                :key="child.key"
                @click="handleMenuClick(child.key)"
            >
              {{ child.label }}
            </a-menu-item>
          </a-sub-menu>
        </template>
      </a-menu>
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
         T-Shirts Two ©2024 Created by Haove
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script lang="ts" setup>

import {computed, ref} from 'vue';
import {useRouter} from 'vue-router';
import {MenuFoldOutlined, MenuUnfoldOutlined, UserOutlined,} from '@ant-design/icons-vue';
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import {useAuthStore} from "@/infrastructure/stores/auth.ts";

const auth = useAuthStore();

const userInfo = computed(() => auth.user);

const collapsed = ref<boolean>(false);
const selectedKeys = ref<string[]>(["1"]);
const router = useRouter();

const handleLogout = () => {
  auth.logout();
  router.push(ROUTES_CONSTANTS.AUTHENTICATION.path);
};

const menuItems = ref([
  {
    key: "1",
    icon: UserOutlined,
    label: "FEATURE",
    path: ROUTES_CONSTANTS.USER.children.STATISTIC,
  },
  {
    key: "sub1",
    label: "Thống kê",
    icon: UserOutlined,
    children: [
      {key: '2', label: 'Chi tiết tương tác', path: '/authentication/statistical-interaction'},
      {key: '3', label: 'Theo menu', path: '/authentication/statistical-menu'},
      {key: '4', label: 'Theo nút', path: '/authentication/statistical-button'},
    ],
  },
]);

const handleMenuClick = (key: string) => {
  selectedKeys.value = [key];
  const item = menuItems.value.find(
      (menuItem) =>
          menuItem.key === key ||
          menuItem.children?.some((child) => child.key === key)
  );
  if (item) {
    const target = item.children?.find((child) => child.key === key) || item;
    if (target?.path) {
      router.push(target.path);
    }
  }
};
</script>

<script lang="ts">
export default {
  name: "AdminPage",
};
</script>
