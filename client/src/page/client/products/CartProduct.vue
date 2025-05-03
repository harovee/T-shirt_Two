<template>
  <div class="product-list px-4">
    <!-- Show product list when not viewing product detail -->
    <div class="flex items-center mb-6">
      <p class="mr-8 text-base font-medium text-gray-700">Sắp xếp theo</p>
      <a-select class="w-64" v-model:value="selectedArrange">
        <!-- <a-select-option
          v-for="arrange in arrangesData"
          :key="arrange.value"
          :value="arrange.value"
          >{{ arrange.name }}</a-select-option
        > -->
      </a-select>
    </div>
    
    <a-row class="mb-8">
      <!-- Lặp qua danh sách sản phẩm -->
      <a-col
        :span="6"
        v-for="product in products"
        :key="product.id"
        class="p-3"
      >
        <div @click="handleRedirectProductDetail(product)" class="cursor-pointer transition-transform duration-300 hover:scale-105">
          <a-card hoverable class="w-full shadow-sm rounded-lg overflow-hidden border border-gray-100">
            <template #cover>
              <div class="product-image-container bg-gray-50">
                <div 
                  v-if="product.phanTramGiam && product.phanTramGiam.length > 0" 
                  class="absolute top-0 right-0 bg-red-500 text-white px-2 py-1 m-2 rounded-md z-10 font-medium"
                >
                  -{{ product.phanTramGiam[0] }}%
                </div>
                <img
                  :alt="product.ten"
                  :src="product.anh && product.anh.length > 0 ? product.anh[0].url : '/default-product-image.jpg'"
                  class="product-image primary-image"
                />
                <img
                  v-if="product.anh && product.anh.length > 1"
                  :alt="product.ten"
                  :src="product.anh[1].url"
                  class="product-image hover-image"
                />
                <img
                  v-else
                  :alt="product.ten"
                  :src="product.anh && product.anh.length > 0 ? product.anh[0].url : '/default-product-image.jpg'"
                  class="product-image hover-image"
                />
              </div>
            </template>
            <a-card-meta :title="product.ten">
              <template #description>
                <div class="py-2">
                  <p class="mb-3">
                    <span class="price-range text-red-600 font-bold text-lg">
                      {{ getFormattedPriceRange(product) }}
                    </span>
                  </p>

                  <!-- Size dynamic list -->
                  <p class="mb-3 flex items-center flex-wrap">
                    <span class="text-gray-700 font-medium mr-2">Size:</span>
                    <span
                      v-for="(size, index) in product.kichCo"
                      :key="index"
                      class="inline-flex items-center justify-center mr-2 mb-1 w-6 h-6 text-xs text-center text-gray-700 border border-gray-300 rounded hover:bg-gray-100 cursor-pointer transition-colors"
                    >
                      {{ size.ten }}
                    </span>
                  </p>

                  <!-- Color dynamic list -->
                  <p class="flex items-center flex-wrap">
                    <span class="text-gray-700 font-medium mr-2">Màu:</span>
                    <span
                      v-for="(c, index) in product.color"
                      :key="index"
                      :style="{ backgroundColor: c.code }"
                      class="inline-block mr-2 mb-1 w-5 h-5 rounded-full cursor-pointer border border-gray-200 shadow-sm hover:shadow-md transition-shadow"
                    ></span>
                  </p>
                  <p class="text-gray-700 font-medium mr-2">Giới tính:  
                    <span>  {{ product.gioiTinh }} </span>
                  </p>
                </div>
              </template>
            </a-card-meta>
          </a-card>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch } from "vue";
import {formatCurrency} from "@/utils/common.helper"
import { ClientProductDetailRequest, ClientProductRequest, FindProductClientRequest } from "@/infrastructure/services/api/client/clientproduct.api";
import { useGetTop8ProductsMoiNhat } from "@/infrastructure/services/service/client/productclient.action";
import { keepPreviousData } from "@tanstack/vue-query";
import router from "@/infrastructure/routes/router.ts";

const selectedArrange = ref("default");
const showProductDetail = ref(false);
const selectedProduct = ref(null);

const params= ref<FindProductClientRequest>({
    page: 1,
    size: 20,
    tenSanPham: "",
    tenDanhMuc: "",
    tenChatLieu: "",
    tenTayAo: "",
    tenTinhNang: "",
    tenKieuDang: "",
    tenCoAo: "",
    tenThuongHieu: "",
    tenHoaTiet: ""
});

const { data: top8Product } = useGetTop8ProductsMoiNhat(params,{
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData
});

const products = computed(() => top8Product?.value?.data || []);

watch(selectedArrange, (newValue) => {
  console.log("Sorting by:", newValue);
});

const getPriceRange = (product) => {
  let prices = [];

  if (product.gia && product.gia.length > 0) {
    prices.push(...product.gia);
  }

  if (product.discount && product.discount.length > 0) {
    prices.push(...product.discount);
  }

  return prices.length > 0 ? prices : [0]; // Nếu không có giá, mặc định là 0
};

const getFormattedPriceRange = (product) => {
  const prices = getPriceRange(product);
  const minPrice = Math.min(...prices);
  const maxPrice = Math.max(...prices);

  return minPrice === maxPrice
    ? formatCurrency(minPrice, "VND", "vi-VN")
    : `${formatCurrency(minPrice, "VND", "vi-VN")} - ${formatCurrency(maxPrice, "VND", "vi-VN")}`;
};


const handleRedirectProductDetail = (product) => {
    const detailParams: ClientProductRequest = {
        idChatLieu: product.chatLieu?.id || "",
        idCoAo: product.coAo?.id || "",
        idDanhMuc: product.danhMuc?.id || "",
        idHoaTiet: product.hoaTiet?.id || "",
        idKieuDang: product.kieuDang?.id || "",
        idTayAo: product.tayAo?.id || "",
        idThuongHieu: product.thuongHieu?.id || "",
        idTinhNang: product.tinhNang?.id || "",
        gioiTinh: product.gioiTinh || "",
    };
    localStorage.setItem('productDetailParams', JSON.stringify(detailParams));

    router.push({
        name: 'client-product-detail',
        params: { id: product.id }
    });
}

</script>

<script lang="ts">
export default {
  name: 'client-home',
};
</script>

<style scoped>
.product-image-container {
  position: relative;
  width: 100%;
  height: 260px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  position: absolute;
  top: 0;
  left: 0;
  transition: opacity 0.3s ease;
}

.primary-image {
  opacity: 1;
}

.hover-image {
  opacity: 0;
}

.product-image-container:hover .primary-image {
  opacity: 0;
}

.product-image-container:hover .hover-image {
  opacity: 1;
}
</style>