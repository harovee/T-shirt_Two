<template>
  <div>
    <h1>Quản lý admin statistic</h1>
  </div>
</template>

<script lang="ts">
export default {
  name: 'admin statistic',
};
</script>

<script lang="ts" setup>

import {localStorageAction} from "@/utils/storage.ts";
import {ACCESS_TOKEN_STORAGE_KEY} from "@/infrastructure/constants/storageKey.ts";

const accessToken = localStorageAction.get(ACCESS_TOKEN_STORAGE_KEY);
console.log(accessToken);


</script>