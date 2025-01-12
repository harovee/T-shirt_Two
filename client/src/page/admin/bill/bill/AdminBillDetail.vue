<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex justify-between items-center">
      <div class="flex items-center gap-2">
        <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48"/>
        <h3 class="text-2xl m-0">Chi tiết hóa đơn: </h3>
      </div>
      <div class="flex items-center gap-2 scale-75 transition-all cursor-pointer
                  hover:scale-100 hover:text-red-500"
           @click="handleRedirectBillManager()">
        <v-icon name="gi-fast-backward-button" size="x-large" width="48" height="48"/>
        <h3 class="text-2xl m-0">Quay lại</h3>
      </div>
    </div>
  </div>

</template>

<script lang="ts" setup>
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path";
import router from "@/infrastructure/routes/router";

const handleRedirectBillManager = () => {
  // router.push(ROUTES_CONSTANTS.ADMIN.path + '/' + ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.path);
  router.push({name: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.name})
}


</script>