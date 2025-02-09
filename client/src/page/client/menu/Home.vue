<template>
  <div>
    <banner/>
  </div>
  <div class="container mx-auto">
    <div class="flex justify-center mt-5 mb-5"><h1 class="text-3xl">Sản phẩm mới về</h1></div>
    <div>
      <cart-product/>
    </div>
    <div class="flex justify-center">
    <a-button type="text" class="mb-10 h-10 w-48 border-2 border-gray-300 rounded-lg px-4 text-base font-bold">Tất cả sản phẩm</a-button>
  </div>
</template>

<script lang="ts" setup>
  import Banner from "@/page/client/Banner.vue";
  import CartProduct from "@/page/client/products/CartProduct.vue";
</script>

<style scoped>
</style>