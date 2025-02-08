<template>
  <div class="product-list">
    <a-row>
      <!-- Lặp qua danh sách sản phẩm -->
      <a-col
        :span="6"
        v-for="product in products"
        :key="product.id"
        class="p-3"
      >
        <a-card hoverable class="w-full">
          <template #cover>
            <img :alt="product.name" :src="product.image" />
          </template>
          <a-card-meta :title="product.name">
            <template #description>
              <div>
                <p>
                  <span class="original-price"
                    >{{ formatCurrency(product.originalPrice, 'VND', 'vi-VN') }}</span
                  >
                  <span class="discounted-price"
                    >{{ formatCurrency(product.discountedPrice, 'VND', 'vi-VN') }}</span
                  >
                </p>

                <!-- Size dynamic list -->
                <p class="mb-2">
                  Size:
                  <span
                    v-for="(size, index) in product.sizes"
                    :key="index"
                    class="inline-block mr-2 p-2 w-4 h-4 text-center cursor-pointer"
                  >
                    {{ size }}
                  </span>
                </p>

                <!-- Color dynamic list -->
                <p>
                  Màu:
                  <span
                    v-for="(color, index) in product.colors"
                    :key="index"
                    :style="{ backgroundColor: color.code }"
                    class="inline-block mr-2 p-2 w-4 h-4 rounded-full cursor-pointer"
                  ></span>
                </p>
              </div>
            </template>
          </a-card-meta>
          <div class="flex justify-evenly">
            <a-button type="primary">Thêm vào giỏ</a-button>
            <a-button type="primary">Xem chi tiết</a-button>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
  
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import {formatCurrency} from "@/utils/common.helper"

const products = reactive([
      {
        id: 1,
        name: "Áo Thun Mới",
        originalPrice: 250000,
        discountedPrice: 200000,
        image: "https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png",
        sizes: ["S", "M", "L", "XL"],
        colors: [
          { name: "Đen", code: "#000000" },
          { name: "Đỏ", code: "#FF0000" },
        ],
      },
      {
        id: 2,
        name: "Quần Jean Nam",
        originalPrice: 350000,
        discountedPrice: 200000,
        image: "https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png",
        sizes: ["M", "L", "XL"],
        colors: [
          { name: "Xanh", code: "#0000FF" },
          { name: "Xám", code: "#808080" },
        ],
      },
      {
        id: 3,
        name: "Quần Jean Nam",
        originalPrice: 350000,
        discountedPrice: 200000,
        image: "https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png",
        sizes: ["M", "L", "XL"],
        colors: [
          { name: "Xanh", code: "#0000FF" },
          { name: "Xám", code: "#808080" },
        ],
      },
      {
        id: 4,
        name: "Quần Jean Nam",
        originalPrice: 350000,
        discountedPrice: 200000,
        image: "https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png",
        sizes: ["M", "L", "XL"],
        colors: [
          { name: "Xanh", code: "#0000FF" },
          { name: "Xám", code: "#808080" },
        ],
      },
      {
        id: 5,
        name: "Quần Jean Nam",
        originalPrice: 350000,
        discountedPrice: 200000,
        image: "https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png",
        sizes: ["M", "L", "XL"],
        colors: [
          { name: "Xanh", code: "#0000FF" },
          { name: "Xám", code: "#808080" },
        ],
      },
      {
        id: 6,
        name: "Quần Jean Nam",
        originalPrice: 350000,
        discountedPrice: 200000,
        image: "https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png",
        sizes: ["M", "L", "XL"],
        colors: [
          { name: "Xanh", code: "#0000FF" },
          { name: "Xám", code: "#808080" },
        ],
      },
      {
        id: 7,
        name: "Quần Jean Nam",
        originalPrice: 350000,
        discountedPrice: 200000,
        image: "https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png",
        sizes: ["M", "L", "XL"],
        colors: [
          { name: "Xanh", code: "#0000FF" },
          { name: "Xám", code: "#808080" },
        ],
      },
      {
        id: 8,
        name: "Quần Jean Nam",
        originalPrice: 350000,
        discountedPrice: 200000,
        image: "https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png",
        sizes: ["M", "L", "XL"],
        colors: [
          { name: "Xanh", code: "#0000FF" },
          { name: "Xám", code: "#808080" },
        ],
      },
      // Thêm các sản phẩm khác vào đây
    ]);
</script>

<style scoped>
.product-list {
  padding: 16px;
}

.original-price {
  text-decoration: line-through;
  color: grey;
  margin-right: 10px;
  font-size: 14px;
}

.discounted-price {
  font-weight: bold;
  font-size: 17px;
  color: red;
}

:deep(.ant-card-meta-title) {
  font-size: 18px; /* Đặt kích thước chữ ở đây */
  font-weight: bold;
}
</style>
