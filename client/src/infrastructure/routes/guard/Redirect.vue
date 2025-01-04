<template>
  <div class="redirect-container">
    <p class="redirect-message">Redirecting...</p>
  </div>
</template>

<script setup lang="ts">
import {useAuthStore} from "@/infrastructure/stores/auth";
import {getUserInformation, isTokenExpired} from "@/utils/token.helper";
import {onMounted} from "vue";
import {useRoute, useRouter} from "vue-router";
import {ROLES} from "@/infrastructure/constants/role.ts";
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import {toast} from "vue3-toastify";

const route = useRoute();

const router = useRouter();

const authStore = useAuthStore();

const {state, error} = route.query;

onMounted(() => {
  if (state) {
    const decodedState = atob(state as string);

    const {accessToken, refreshToken} = JSON.parse(decodedState);

    const user = getUserInformation(accessToken);

    authStore.login({
      user,
      accessToken,
      refreshToken,
    });

    const userRole = user.roleCode;

    switch (userRole) {
      case ROLES.ADMIN:
        router.push({name: ROUTES_CONSTANTS.ADMIN.name});
        break;
      case ROLES.USER:
        router.push({name: ROUTES_CONSTANTS.USER.name});
        break;
      default:
        router.push({name: ROUTES_CONSTANTS.AUTHENTICATION.name});
        break;
    }
    return;
  }

  if (error) {
    toast.warning(error);
    authStore.logout();
  }

  setTimeout(() => {
    router.push({name: ROUTES_CONSTANTS.AUTHENTICATION.name});
  }, 4000);
});
</script>

<style scoped>
.redirect-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f0f0;
}

.redirect-message {
  font-size: 1.5rem;
  color: #333;
  font-weight: bold;
}
</style>
