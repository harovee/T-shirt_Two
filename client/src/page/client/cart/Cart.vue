<template>
  <div class="container mx-auto pt-10">
    <h1
      class="flex items-center justify-center text-center gap-4 text-4xl font-bold mb-10 text-red-700"
    >
      <v-icon name="md-shoppingbag-sharp" style="width: 35px; height: 35px" />
      GIỎ HÀNG
    </h1>
    <div v-if="listProducts.length > 0" class="m-5">
      <a-row :gutter="[8, 24]">
        <a-col :span="16" class="bg-gray-100">
          <cart-table
            :dataSource="listProducts"
          />
        </a-col>
        <a-col :span="7" :offset="1">
          <div class="mt-5">
            <h1 class="text-2xl font-bold">ĐƠN HÀNG</h1>
            <div class="mt-8 flex justify-between items-center">
              <p>Tổng giá trị đơn hàng: </p>
              <p class="text-2xl font-bold text-red-700">{{formatCurrencyVND(tongTien)}}</p>
            </div>
            <hr class="border-t border-gray-400 border-dashed mt-2">
            <a-button class="mt-10 mb-4 w-full h-12 text-xl" :style="{ backgroundColor: '#b91c1c', borderColor: '#b91c1c', color: 'white' }" type="primary" danger @click="redirectPayment">TIẾN HÀNH THANH TOÁN</a-button>
            <p>
              Dùng mã giảm giá của <strong>T-shirtTwo</strong> trong bước tiếp theo.
            </p>
          </div>
        </a-col>
      </a-row>
    </div>
    <div v-else class="flex flex-col items-center justify-center text-center">
      <img
        :width="500"
        src="/src/assets/image/images/empty-cart.webp"
      />
      <h4 class="text-l">Bạn chưa có sản phẩm nào trong giỏ hàng.</h4>
      <router-link to="/home"><a-button class="mt-5" type="primary" danger>TIẾP TỤC MUA SẮM</a-button></router-link>
    </div>
  </div>
</template>

<script lang="ts" setup>
import CartTable from "./CartTable.vue";
import { ref, computed } from "vue";
import {
  formatCurrencyVND
} from "@/utils/common.helper";
import { useRouter } from "vue-router";
import { useCartStore } from '@/infrastructure/stores/cart';

const router = useRouter();

const cartStore = useCartStore();

const listProducts = ref([
  {
    tenHang: 'Sản phẩm 1',
    anh: 'http://res.cloudinary.com/tshirtstwo/image/upload/v1740921453/t%E1%BA%A3i_xu%E1%BB%91ng_clpitw.jpg',
    gia: 250000,
    soLuong: 2,
    tongTien: 500000,
  },
  {
    tenHang: 'Sản phẩm 2',
    anh: 'http://res.cloudinary.com/tshirtstwo/image/upload/v1740922472/images_1_jarwau.jpg',
    gia: 200000,
    soLuong: 3,
    tongTien: 600000,
  },
  {
    tenHang: 'Sản phẩm 3',
    anh: 'http://res.cloudinary.com/tshirtstwo/image/upload/v1740921453/t%E1%BA%A3i_xu%E1%BB%91ng_3_vwlnzd.jpg',
    gia: 100000,
    soLuong: 10,
    tongTien: 1000000,
  }
]);

const tongTien = computed(() => {
  return listProducts.value.reduce((total, item) => total + item.gia * item.soLuong, 0);
});

const redirectPayment = () => {
  cartStore.setCheckoutData(listProducts);
  router.push({
    name: 'client-check-out'
  })
}

</script>

<style scoped>
</style>
