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
            >{{ arrange.name  }}</a-select-option
          >
        </a-select>
      </div>
      <a-row>
        <!-- Lặp qua danh sách sản phẩm -->
        <a-col
          :span="6"
          v-for="product in props.dataSource"
          :key="product.id"
          class="p-3"
        >
          <div @click="handleRedirectProductDetail(product)">
            <a-card hoverable class="w-full">
            <template #cover>
              <div class="product-image-container">
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
                <!-- Nếu không có hình thứ 2, dùng hình đầu tiên hoặc ảnh mặc định -->
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
                <div>
                  <p>
                    <span class="price-range">
                      {{
                        getFormattedPriceRange(product)
                      }}
                    </span>
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
            <!-- <div class="flex justify-evenly">
              <a-button type="primary">Thêm vào giỏ</a-button>
              <a-button type="primary" @click="handleRedirectProductDetail(product)">Xem chi tiết</a-button>
            </div> -->
          </a-card>
          </div>
        </a-col>
      </a-row>
      <div class="pagination-container flex justify-center mt-8 mb-4">
        <a-pagination
          v-model:current="currentPage"
          :total="props.dataSource?.totalElements"
          :pageSize="pageSize"
          @change="handlePageChange"
          show-size-changer
          :pageSizeOptions="['12', '20', '40', '60']"
          :showTotal="total => `Tổng ${total} sản phẩm`"
          @showSizeChange="handleSizeChange"
        />
      </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, watch, onMounted, computed } from "vue";
import { formatCurrency } from "@/utils/common.helper";
import { useGetAllProducts } from "@/infrastructure/services/service/client/productclient.action";
import { FindProductClientRequest,ClientProductResponse, ClientProductDetailRequest, ClientProductRequest } from "@/infrastructure/services/api/client/clientproduct.api";
import { keepPreviousData } from "@tanstack/vue-query";
import SPCT from "./ProductDetail.vue"; // Import the ProductDetail component
import { ArrowLeftOutlined } from '@ant-design/icons-vue';
import router from "@/infrastructure/routes/router.ts";

const selectedArrange = ref("default");
const currentPage = ref(1);
const pageSize = ref(20);
const totalItems = ref(0);

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
});

const arrangesData = ref([
  { value: "default", name: "Mặc định" },
  { value: "new", name: "Mới nhất" },
  { value: "asc", name: "Giá từ thấp đến cao" },
  { value: "desc", name: "Giá từ cao đến thấp" },
  { value: "top", name: "Bán chạy nhất" },
]);

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


const handlePageChange = (page: number) => {
  currentPage.value = page;
  updateParams();
};

const handleSizeChange = (current: number, size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  updateParams();
};

const updateParams = () => {
  params.value = {
    ...params.value,
    page: currentPage.value,
    size: pageSize.value
  };
};

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
    };
    localStorage.setItem('productDetailParams', JSON.stringify(detailParams));

    router.push({
        name: 'client-product-detail',
        params: { id: product.id }
    });
}

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
  font-size: 18px;
  font-weight: bold;
}

/* Thêm CSS cho hiệu ứng hover ảnh */
.product-image-container {
  position: relative;
  width: 100%;
  height: 260px; /* Keeping your original fixed height */
  display: flex;
  align-items: center;
  justify-content: center;
  /* Removed overflow: hidden to allow images to extend beyond container */
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain; /* Keeps the original aspect ratio */
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
.price-range {
  font-weight: bold;
  font-size: 17px;
  color: red;
}

</style>

<script lang="ts">
export default {
  name: 'client-products',
};
</script>

