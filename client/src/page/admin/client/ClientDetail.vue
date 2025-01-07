<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex justify-between items-center">
      <div class="flex items-center gap-2">
        <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48"/>
        <h3 class="text-2xl m-0">Thông tin khách hàng: </h3>
      </div>
      <div class="flex items-center gap-2 scale-75 transition-all cursor-pointer
                  hover:scale-100 hover:text-red-500"
           @click="handleRedirectClient()">
        <v-icon name="gi-fast-backward-button" size="x-large" width="48" height="48"/>
        <h3 class="text-2xl m-0">Quay lại</h3>
      </div>
    </div>
  </div>
  <div class="p-6 grid grid-cols-5 gap-6">
    <div class="col-span-2 md:col-span-5 lg:col-span-2 w-full h-screen bg-gray-300 flex justify-center">
      <div class="w-[20rem] h-[20rem]">
        <a-avatar
            shape="vertical"
            :src="userInfo?.profilePicture"
            class="w-full h-full text-center"
        >
        </a-avatar>
      </div>
    </div>
    <div class="col-span-3 md:col-span-5 lg:col-span-3 h-screen bg-violet-400">

    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: 'admin client detail',
};
</script>

<script lang="ts" setup>
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import router from "@/infrastructure/routes/router.ts";
import {computed} from "vue";
import {useAuthStore} from "@/infrastructure/stores/auth.ts";

const auth = useAuthStore();
const userInfo = computed(() => auth.user);

const handleRedirectClient = () => {
  router.push(ROUTES_CONSTANTS.ADMIN.path + '/' + ROUTES_CONSTANTS.ADMIN.children.CLIENT.path);
}

</script>