<template>
  <a-layout>
    <a-layout>
      <a-layout-header class="left-0 right-0 z-10 bg-white shadow-md header">
          <router-link to="/home" class="logo-link ms-10">
            <img
              src="@/assets/image/logo/T-ShirtTwo.png"
              alt="Logo"
              class="logo"
            />
          </router-link>
          <div class="ms-10 hidden md:flex space-x-10">
            <router-link
              to="/home"
              class="text-black font-bold text-lg hover:text-gray-400 transition duration-200"
            >
              TRANG CHỦ
            </router-link>

            <router-link
              to="/products"
              class="text-black font-bold text-lg hover:text-gray-400 transition duration-200"
            >
              SẢN PHẨM
            </router-link>
            <router-link
              to="/about"
              class="text-black font-bold text-lg hover:text-gray-400 transition duration-200"
            >
              GIỚI THIỆU
            </router-link>
            <router-link
              to="/contact"
              class="text-black font-bold text-lg hover:text-gray-400 transition duration-200"
            >
              LIÊN HỆ
            </router-link>
          </div>
          <div class="search-container me-6">
            <a-input-search
              placeholder="Tìm kiếm sản phẩm"
              class="search-input"
            />
          </div>
          <a-tooltip placement="bottom">
            <template #title>
              <span>Giỏ hàng</span>
            </template>
            <a-button
              class="me-6 w-10 h-10 flex justify-center items-center"
              type="text"
            >
              <v-icon name="bi-cart3" class="w-8 h-8"></v-icon>
            </a-button>
          </a-tooltip>

          <div class="me-10 user-info flex items-center justify-between">
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
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path.ts";
import { useAuthStore } from "@/infrastructure/stores/auth.ts";

const auth = useAuthStore();

const userInfo = computed(() => auth.user);

const router = useRouter();

const handleLogout = () => {
  auth.logout();
  router.push(ROUTES_CONSTANTS.AUTHENTICATION.path);
};
</script>

<script lang="ts">
export default {
  name: "HomePage",
};
</script>

<style scoped>
.header {
  display: flex;
  height: 80px;
  background-color: white;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo-link {
  display: inline-block;
}

.logo {
  height: 80px;
  width: 80px;
  vertical-align: middle;
}

.search-container {
  flex-grow: 1;
  display: flex;
  justify-content: right;
}

.search-input {
  width: 500px;
}

.search-input:hover {
  border-color: red !important;
  background-color: #f3f4f6 !important;
}

.menu {
  margin-left: 20px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-info .flex {
  display: flex;
  align-items: center;
}

.user-info .cursor-pointer {
  cursor: pointer;
}

.user-info .ml-2 {
  margin-left: 8px;
}
</style>
