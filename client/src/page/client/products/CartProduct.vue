<template>
  <div class="product-list">
    <!-- Show product list when not viewing product detail -->
      <div class="flex items-center">
        <p class="mr-8 mt-4 text-base">Sắp xếp theo</p>
        <a-select class="w-64" v-model:value="selectedArrange">
          <a-select-option
            v-for="arrange in arrangesData"
            :key="arrange.value"
            :value="arrange.value"
            >{{ arrange.name }}</a-select-option
          >
        </a-select>
      </div>
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
              <div class="product-image-container">
                <img 
                  :alt="product.ten" 
                  :src="product.anh && product.anh.length > 0 ? product.anh[0] : '/default-product-image.jpg'" 
                  class="product-image primary-image"
                />
                <img 
                  v-if="product.anh && product.anh.length > 1" 
                  :alt="product.ten" 
                  :src="product.anh[1]" 
                  class="product-image hover-image"
                />
                <!-- Nếu không có hình thứ 2, dùng hình đầu tiên hoặc ảnh mặc định -->
                <img 
                  v-else
                  :alt="product.ten" 
                  :src="product.anh && product.anh.length > 0 ? product.anh[0] : '/default-product-image.jpg'" 
                  class="product-image hover-image"
                />
              </div>
            </template>
            <a-card-meta :title="product.ten">
              <template #description>
                <div>
                  <p>
                    <span class="original-price">{{
                      formatCurrency(product.gia && product.gia.length > 0 ? product.gia[0] : 0, "VND", "vi-VN")
                    }}</span>
                    <span class="discounted-price">{{
                      formatCurrency(product.gia && product.gia.length > 0 ? product.gia[0] : 0, "VND", "vi-VN")
                    }}</span>
                  </p>

                  <!-- Size dynamic list -->
                  <p class="mb-2">
                    Size:
                    <span
                      v-for="(size, index) in product.kichCo"
                      :key="index"
                      class="inline-block mr-2 p-2 w-4 h-4 text-center cursor-pointer"
                    >
                      {{ size.ten }}
                    </span>
                  </p>

                  <!-- Color dynamic list -->
                  <p>
                    Màu:
                    <span
                      v-for="(c, index) in product.color"
                      :key="index"
                      :style="{ backgroundColor: c.code }"
                      class="inline-block mr-2 p-2 w-4 h-4 rounded-full cursor-pointer"
                    ></span>
                  </p>
                </div>
              </template>
            </a-card-meta>
            <div class="flex justify-evenly">
              <a-button type="primary">Thêm vào giỏ</a-button>
              <a-button type="primary" @click="handleRedirectProductDetail(product)">Xem chi tiết</a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
      </div>
  <div class="flex justify-center">
    <a-button type="text" class="mb-10 h-10 w-48 border-2 border-gray-300 rounded-lg px-4 text-base font-bold">Tất cả sản phẩm</a-button>
  </div>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch } from "vue";
import {formatCurrency} from "@/utils/common.helper"
import { ClientProductDetailRequest, FindProductClientRequest } from "@/infrastructure/services/api/client/clientproduct.api";
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
console.log(products);

const viewProductDetail = (product) => {
  selectedProduct.value = product;
  showProductDetail.value = true;
  // You might want to scroll to top for better UX
  window.scrollTo(0, 0);
};

// Function to go back to product list
const backToList = () => {
  showProductDetail.value = false;
  selectedProduct.value = null;
};

// Watch for changes in sort order to apply sorting
watch(selectedArrange, (newValue) => {
  // Implement your sorting logic here
  console.log("Sorting by:", newValue);
});

const handleRedirectProductDetail = (product) => {
    // Create detail params based on the product
    const detailParams: ClientProductDetailRequest = {
        idChatLieu: product.chatLieu?.id || "",
        idCoAo: product.coAo?.id || "",
        idDanhMuc: product.danhMuc?.id || "",
        idHoaTiet: product.hoaTiet?.id || "",
        idKieuDang: product.kieuDang?.id || "",
        idTayAo: product.tayAo?.id || "",
        idThuongHieu: product.thuongHieu?.id || "",
        idTinhNang: product.tinhNang?.id || "",
        idKichCo: null,
        idMauSac: null
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
.product-image-container {
  position: relative;
  width: 100%;
  height: 260px; /* Thiết lập chiều cao cố định */
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* Đảm bảo ảnh lấp đầy không gian và giữ tỷ lệ */
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

