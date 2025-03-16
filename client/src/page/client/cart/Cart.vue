<template>
  <div class="mx-auto p-10">
    <h1 class="text-4xl font-bold text-center flex items-center justify-center gap-4 text-red-700">
      <v-icon name="md-shoppingbag-sharp" style="width: 35px; height: 35px" />
      GIỎ HÀNG
    </h1>

    <div v-if="listProducts.length > 0" class="mt-10">
      <a-row :gutter="[24, 24]">
        <!-- Danh sách sản phẩm -->
        <a-col :span="16">
          <a-card class="bg-gray-100">
            <cart-table :dataSource="listProducts"
            @updateCart="reloadCart"
          />
          </a-card>
          
        </a-col>

        <!-- Tổng tiền & Thanh toán -->
        <a-col :span="8">
          <a-card class="bg-gray-100 p-5">
            <h2 class="text-2xl font-bold mb-4 mt-5">ĐƠN HÀNG</h2>
            <div class="flex justify-between items-center text-lg">
              <span>Tổng cộng:</span>
              <span class="text-2xl font-bold text-red-700">
                {{ formatCurrencyVND(tongTien) }}
              </span>
            </div>
            <a-divider />
            <a-button
              type="primary"
              class="w-full h-12 text-xl"
              danger
              @click="redirectPayment"
            >
              TIẾN HÀNH THANH TOÁN
            </a-button>
            <p class="text-center mt-3 text-gray-600">
              Dùng mã giảm giá của <strong>T-shirtTwo</strong> trong bước tiếp theo.
            </p>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- Giỏ hàng trống -->
    <div v-else class="text-center flex flex-col items-center">
      <img src="/src/assets/image/images/empty-cart.webp" width="400" />
      <h4 class="text-lg mt-3">Bạn chưa có sản phẩm nào trong giỏ hàng.</h4>
      <router-link to="/home">
        <a-button class="mt-5 px-6 py-2 text-lg h-12" type="primary" danger>
          TIẾP TỤC MUA SẮM
        </a-button>
      </router-link>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useCartStore } from "@/infrastructure/stores/cart";
import { formatCurrencyVND } from "@/utils/common.helper";
import CartTable from "./CartTable.vue";
import { useCartStorageBL } from "../products/business.logic/useCartLocalStorageBL";
import { getCartFromLocalStorage } from "../products/business.logic/CartLocalStorageBL";

const { cart, totalAmount, addProduct, removeProduct, updateProductQuantity } = useCartStorageBL();

const router = useRouter();
const cartStore = useCartStore();

// Danh sách sản phẩm đã được map lại
const listProducts = computed(() =>
  cart.value.map((item) => ({
    id: item.id,
    // tenHang: `Sản phẩm ${item.id?.slice(0, 5) || "?"}`, // Giả sử không có tên sản phẩm, đặt tạm ID, gọi API lấy thông tin ra nhé
    tenHang: `${item.name} [ ${item.colorName} - ${item.sizeName} ]`,
    anh: `${item.anh}`, // Chưa có ảnh, dùng ảnh placeholder
    gia: item.price || 0,
    soLuong: item.quantity || 1,
    tongTien: (item.price || 0) * (item.quantity || 1),
  }))
);

// Load lấy lại cart từ local
const reloadCart = () => {
   cart.value = [...getCartFromLocalStorage()];
};

const tongTien = computed(() => {
  return listProducts.value.reduce(
    (total, item) => total + item.gia * item.soLuong,
    0
  );
});

const redirectPayment = () => {
  cartStore.setCheckoutData(listProducts);
  router.push({ name: "client-check-out" });
};
</script>

<style scoped>
</style>
