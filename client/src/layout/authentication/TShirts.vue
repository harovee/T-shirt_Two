<template>
  <a-row type="flex">
    <a-col flex="600px" class="h-full p-10">
      <div class="flex justify-center items-center h-full font-normal">
        <div class="w-full max-w-md">
          <div class="text-center mb-12 no-underline">
            <div class="flex items-center justify-center">
              <img :src="logoSpotifyFirst" alt="Logo" class="mb-2 w-48"/>
            </div>
            <span class="text-2xl text-gray-500">Chào mừng đến với T-Shirts Two</span>
          </div>
          <router-view/>
        </div>
      </div>
    </a-col>
    <a-col flex="auto" class="bg-authentication"></a-col>
  </a-row>
</template>

<script lang="ts" setup>
import logoSpotifyFirst from "@/assets/image/logo/T-ShirtTwo.png";
</script>

<style scoped>
.bg-authentication {
  background-image: url(@/assets/image/background/bg-default.jpg);
  background-size: cover;
  background-position: center;
  height: 100vh;
}
</style>