<template>
  <div class="container mx-auto">
    <div class="grid grid-cols-12 gap-6">
      <div class="col-span-2 mt-10">
        <filter-product />
      </div>
      <div class="col-span-10">
        <div class="flex justify-center">
          <h1 class="text-3xl mt-8">Sản phẩm</h1>
        </div>
        <all-cart-product />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import AllCartProduct from "@/page/client/products/AllCartProduct.vue";
import FilterProduct from "@/page/client/products/FilterProduct.vue";
</script>
