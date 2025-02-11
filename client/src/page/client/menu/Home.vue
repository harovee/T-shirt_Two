<template>
  <div>
    <banner />
  </div>
  <div class="container mx-auto">
    <div class="flex justify-center mt-5 mb-5">
      <h1 class="text-3xl">Sản phẩm mới về</h1>
    </div>
    <div>
      <cart-product />
    </div>
  </div>
</template>

<script lang="ts" setup>
import Banner from "@/page/client/Banner.vue";
import CartProduct from "@/page/client/products/CartProduct.vue";
</script>

<style scoped>
</style>