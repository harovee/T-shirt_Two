<template>
  <a-layout>
    <a-layout>
      <a-layout-header class="pl-3 mt-1" style="background-color: white">
        <div class="user-info flex items-center justify-between">

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
        K&Q T-Shirts ©2024 Created by Haove
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script lang="ts" setup>

import {computed, ref} from 'vue';
import {useRouter} from 'vue-router';
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import {useAuthStore} from "@/infrastructure/stores/auth.ts";

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
