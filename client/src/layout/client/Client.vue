<template>
  <a-layout>
    <a-layout>
      <a-layout-header class="me-4 left-0 right-0 z-10 bg-white shadow-md fixed top-0 w-full header">
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

            <!-- Cập nhật router-link cho trang products -->
            <a
              href="javascript:void(0)"
              class="text-black font-bold text-lg hover:text-gray-400 transition duration-200"
              @click="goToProducts"
            >
              SẢN PHẨM
            </a>
            
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
                v-model:value="searchKey"
                placeholder="Tìm kiếm sản phẩm"
                class="search-input"
                @change="handleSearch"
                allow-clear
              />
          </div>
          <a-tooltip placement="bottom">
              <template #title>
                <span>Giỏ hàng</span>
              </template>
              <a-badge :count="cartItemCount" :offset="[-25, 5]" showZero="true">
                <a-button
                  class="me-6 w-10 h-10 flex justify-center items-center"
                  type="text" @click="redirectCart"
                >
                  <v-icon name="bi-cart3" class="w-8 h-8"></v-icon>
                </a-button>
              </a-badge>
            </a-tooltip>

          <div v-if="userInfo" class="me-10 user-info flex items-center justify-between">
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
                  <a-menu-item @click="handleRedirectMyPage">
                    Thông tin cá nhân
                  </a-menu-item>
                  <a-menu-item @click="handleBought">
                    Đơn hàng đã mua
                  </a-menu-item>
                  <a-menu-item key="logout" @click="handleLogout">
                    Đăng xuất
                  </a-menu-item>
                </a-menu>
                
              </template>
            </a-dropdown>
          </div>
          <div v-if="!userInfo" class="me-10">
            <router-link to="/authentication/login"><a-button>
              Đăng nhập
            </a-button></router-link>
          </div>
      </a-layout-header>
      <a-layout-content class="mt-[80px]">
        <div class="min-h-[calc(100vh-9.5rem)] bg-white">
          <router-view />
        </div>
      </a-layout-content>
      <a-layout-footer>
        <div class="mb-8">
          <footer-view/>
        </div>
        <hr>
        <div class="text-center mt-6">
          T-Shirts Two ©2024 Created by Team 2
        </div>
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script lang="ts" setup>
import { computed, ref, watch,onMounted, provide} from "vue";
import { useRouter, useRoute } from "vue-router";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path.ts";
import { useAuthStore } from "@/infrastructure/stores/auth.ts";
import FooterView from "@/page/client/FooterView.vue"
import { useSearchStore } from '@/infrastructure/stores/search';

import { useCartStorageBL } from "@/page/client/products/business.logic/useCartLocalStorageBL";
import { getCartFromLocalStorage } from "@/page/client/products/business.logic/CartLocalStorageBL";

const { cart } = useCartStorageBL();
const auth = useAuthStore();
const searchStore = useSearchStore();
const router = useRouter();
const route = useRoute();

const userInfo = computed(() => auth.user);
const searchKey = ref("");

// Theo dõi thay đổi route và xóa searchKey khi không ở trang products
watch(() => route.path, (newPath) => {
  if (!newPath.includes('/products')) {
    searchStore.setSearchKey("");
    searchKey.value = "";
  } else {
    searchKey.value = searchStore.searchKey || "";
  }
}, 
{ immediate: true });

const handleSearch = () => {
  searchStore.setSearchKey(searchKey.value);
  router.push({ path: 'products' });
};

const forceCartUpdate = ref(0);
const emitCartUpdate = () => {
  forceCartUpdate.value++;
};

provide('emitCartUpdate', emitCartUpdate);

// theo dõi tìm kiếm
watch(() => route.path, (newPath) => {
  if (!newPath.includes('/products')) {
    searchStore.setSearchKey("");
    searchKey.value = "";
  } else {

    searchKey.value = searchStore.searchKey || "";
  }
}, { immediate: true });

// hiển thị badge cho giỏ hàng
const cartItemCount = computed(() => {
  if (forceCartUpdate.value >= 0) {
    const latestCart = getCartFromLocalStorage();
    return latestCart.length || 0;
  }
  return 0;
});

onMounted(() => {
  emitCartUpdate();
});

const goToProducts = () => {
  searchStore.setSearchKey("");
  searchKey.value = "";
  router.push({ path: '/products' });
};

const handleLogout = () => {
  auth.logout();
  router.push(ROUTES_CONSTANTS.AUTHENTICATION.path);
};

const handleBought = () => {
  router.push(ROUTES_CONSTANTS.CLIENT.children.MY_ORDER.path);
};

const handleRedirectMyPage = () => {
  router.push(ROUTES_CONSTANTS.CLIENT.children.MY_PAGE.path);
};

const redirectCart = () => {
  router.push({
    name: 'client-cart'
  })
}
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