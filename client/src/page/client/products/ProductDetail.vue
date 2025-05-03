<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8" v-if="product">
    <!-- Breadcrumb -->
    <nav class="flex items-center text-sm font-medium text-gray-500 mb-6">
      <router-link to="/home" class="hover:text-blue-600 transition-colors"
        >Trang chủ</router-link
      >
      <span class="mx-2">/</span>
      <router-link to="/products" class="hover:text-blue-600 transition-colors"
        >Sản phẩm</router-link
      >
      <span class="mx-2">/</span>
      <span class="text-gray-800">{{ product.ten }}</span>
    </nav>

    <div class="flex flex-col lg:flex-row gap-8">
      <!-- Left side - Product Images -->
      <div class="w-full lg:w-2/3">
        <div class="flex flex-row-reverse gap-4">
          <!-- Thumbnails -->
          <div class="flex-1">
            <div class="bg-white rounded-lg shadow-sm overflow-hidden relative">
              <!-- Badge giảm giá -->
              <!-- <div 
                v-if="effectivePhanTramGiam" 
                class="absolute top-0 right-0 bg-red-500 text-white px-2 py-1 m-2 rounded-md z-10 font-medium"
              >
                -{{ effectivePhanTramGiam }}%
              </div> -->
              <img
                :src="selectedImage"
                :alt="product.ten"
                class="w-full h-auto object-contain aspect-square"
              />
            </div>
          </div>

          <!-- Thumbnail navigation -->
          <div class="w-20">
            <div class="flex flex-col gap-3 max-h-96 overflow-y-auto pr-2">
              <div
                v-for="(image, index) in allProductImages"
                :key="index"
                class="cursor-pointer rounded-md overflow-hidden border-2 transition-all"
                :class="
                  selectedImage === image
                    ? 'border-blue-500 shadow-md'
                    : 'border-gray-200 hover:border-gray-300'
                "
                @click="selectedImage = image"
              >
                <img
                  :src="image"
                  :alt="`${product.ten} - ảnh ${index + 1}`"
                  class="w-full h-auto aspect-square object-cover"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right side - Product Information -->
      <div class="w-full lg:w-1/3">
        <div class="bg-white rounded-lg p-6 shadow-sm">
          <h1 class="text-2xl font-bold text-gray-800 mb-4">
            {{ product.ten }}
          </h1>

          <!-- Price -->
          <div class="mb-6">
            <div class="flex flex-col gap-1">
              <div class="flex items-baseline gap-2">
                <span
                  v-if="displayedDiscount && displayedDiscount.length > 0"
                  class="line-through text-gray-400 text-lg"
                >
                  {{
                    formatCurrency(
                      displayedPrice && displayedPrice.length > 0
                        ? displayedPrice[0]
                        : 0,
                      "VND",
                      "vi-VN"
                    )
                  }}
                </span>
                <span class="text-2xl font-bold text-red-600">
                  {{
                    displayedDiscount && displayedDiscount.length > 0
                      ? formatCurrency(displayedDiscount[0], "VND", "vi-VN")
                      : formatCurrency(
                          displayedPrice && displayedPrice.length > 0
                            ? displayedPrice[0]
                            : 0,
                          "VND",
                          "vi-VN"
                        )
                  }}
                </span>
                <!-- Hiển thị badge phần trăm giảm giá -->
                <span
                  v-if="effectivePhanTramGiam"
                  class="bg-red-100 text-red-600 px-2 py-1 rounded text-sm font-medium"
                >
                  Giảm {{ effectivePhanTramGiam }}%
                </span>
              </div>

              <!-- Thông báo tiết kiệm -->
              <div
                v-if="
                  displayedDiscount &&
                  displayedDiscount.length > 0 &&
                  displayedPrice &&
                  displayedPrice.length > 0
                "
                class="text-green-600 text-sm"
              >
                Tiết kiệm:
                {{
                  formatCurrency(
                    displayedPrice[0] - displayedDiscount[0],
                    "VND",
                    "vi-VN"
                  )
                }}
              </div>
            </div>
          </div>

          <!-- Divider -->
          <div class="w-full h-px bg-gray-200 my-4"></div>

          <!-- Size selection -->
          <div class="mb-6" v-if="product.kichCo && product.kichCo.length > 0">
            <label class="block text-sm font-medium text-gray-700 mb-2"
              >Kích cỡ:</label
            >
            <div class="flex flex-wrap gap-2">
              <button
                v-for="(size, index) in product.kichCo"
                :key="index"
                class="px-4 py-2 border rounded-md text-sm transition-all"
                :class="
                  selectedSize === size.id
                    ? 'bg-blue-50 border-blue-500 text-blue-700'
                    : 'border-gray-300 text-gray-700 hover:bg-gray-50'
                "
                @click="selectedSize = size.id"
              >
                {{ size.ten }}
              </button>
            </div>
          </div>

          <!-- Color selection -->
          <div class="mb-6" v-if="product.color && product.color.length > 0">
            <label class="block text-sm font-medium text-gray-700 mb-2"
              >Màu sắc:</label
            >
            <div class="flex flex-wrap gap-3">
              <button
                v-for="(mausac, index) in product.color"
                :key="index"
                class="w-10 h-10 rounded-full border-2 transition-transform hover:scale-110 focus:outline-none"
                :style="{ backgroundColor: mausac.code }"
                :class="
                  selectedColor === mausac.id
                    ? 'border-blue-500 shadow-md'
                    : 'border-gray-300'
                "
                @click="selectedColor = mausac.id"
              >
                <span
                  v-if="selectedColor === mausac.id"
                  class="flex items-center justify-center h-full"
                >
                  <svg
                    class="w-5 h-5 text-white"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M5 13l4 4L19 7"
                    ></path>
                  </svg>
                </span>
              </button>
            </div>
          </div>

          <!-- Quantity -->
          <div class="mb-6">
            <label class="block text-sm font-medium text-gray-700 mb-2"
              >Số lượng:</label
            >
            <div class="flex items-center">
              <button
                @click="decreaseQuantity"
                class="w-10 h-10 rounded-l-md border border-gray-300 flex items-center justify-center bg-gray-50 hover:bg-gray-100 transition-colors"
              >
                <svg
                  class="w-4 h-4 text-gray-600"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M20 12H4"
                  ></path>
                </svg>
              </button>
              <input
                type="text"
                v-model="quantity"
                readonly
                class="w-16 h-10 border-t border-b border-gray-300 text-center text-gray-700 font-medium"
              />
              <button
                @click="increaseQuantity"
                class="w-10 h-10 rounded-r-md border border-gray-300 flex items-center justify-center bg-gray-50 hover:bg-gray-100 transition-colors"
              >
                <svg
                  class="w-4 h-4 text-gray-600"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 4v16m8-8H4"
                  ></path>
                </svg>
              </button>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex flex-col sm:flex-row gap-3 mb-6">
            <button
              @click="addToCart"
              class="flex-1 bg-blue-600 hover:bg-blue-700 text-white py-3 px-6 rounded-md font-medium flex items-center justify-center gap-2 transition-colors"
            >
              <svg
                class="w-5 h-5"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"
                ></path>
              </svg>
              Thêm vào giỏ hàng
            </button>
            <button
              @click="buyNow"
              class="flex-1 bg-red-600 hover:bg-red-700 text-white py-3 px-6 rounded-md font-medium transition-colors"
            >
              Mua ngay
            </button>
          </div>

          <!-- Product description -->
          <div class="border-t border-gray-200 pt-4 mt-4">
            <h3 class="text-lg font-medium text-gray-900 mb-2">
              Mô tả sản phẩm
            </h3>
            <p class="text-gray-600">
              {{ product.moTa || "Không có mô tả cho sản phẩm này." }}
            </p>
          </div>

          <!-- Product details -->
          <div class="border-t border-gray-200 pt-4 mt-4">
            <h3 class="text-lg font-medium text-gray-900 mb-2">
              Thông tin chi tiết
            </h3>
            <div class="space-y-2">
              <div class="flex">
                <span class="w-1/3 text-gray-500">Giới tính:</span>
                <span class="w-2/3 text-gray-800">{{
                    product.gioiTinh
                }}</span>
              </div>
              <div class="flex">
                <span class="w-1/3 text-gray-500">Thương hiệu:</span>
                <span class="w-2/3 text-gray-800">{{
                  getAttributeName(product.thuongHieu)
                }}</span>
              </div>
              <div class="flex">
                <span class="w-1/3 text-gray-500">Chất liệu:</span>
                <span class="w-2/3 text-gray-800">{{
                  getAttributeName(product.chatLieu)
                }}</span>
              </div>
              <div class="flex">
                <span class="w-1/3 text-gray-500">Kiểu dáng:</span>
                <span class="w-2/3 text-gray-800">{{
                  getAttributeName(product.kieuDang)
                }}</span>
              </div>
              <div class="flex">
                <span class="w-1/3 text-gray-500">Cổ áo:</span>
                <span class="w-2/3 text-gray-800">{{
                  getAttributeName(product.coAo)
                }}</span>
              </div>
              <div class="flex">
                <span class="w-1/3 text-gray-500">Tay áo:</span>
                <span class="w-2/3 text-gray-800">{{
                  getAttributeName(product.tayAo)
                }}</span>
              </div>
              <div class="flex">
                <span class="w-1/3 text-gray-500">Họa tiết:</span>
                <span class="w-2/3 text-gray-800">{{
                  getAttributeName(product.hoaTiet)
                }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: "client-product-detail",
};
</script>

<script lang="ts" setup>
import { ref, computed, onMounted, watch } from "vue";
import { formatCurrency } from "@/utils/common.helper";
import { ShoppingCartOutlined } from "@ant-design/icons-vue";
import {
  ClientProductDetailRequest,
  ClientProductRequest,
} from "@/infrastructure/services/api/client/clientproduct.api";
import {
  useGetProductById,
  useGetProductDetailById,
} from "@/infrastructure/services/service/client/productclient.action";
import { useRouter, useRoute } from "vue-router";
import { keepPreviousData } from "@tanstack/vue-query";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import ProductDetail from "@/page/admin/product/product-detail/ProductDetail.vue";
import { useCartStorageBL } from "./business.logic/useCartLocalStorageBL";
import { useCheckQuantityInStockByProductDetail } from "@/infrastructure/services/service/admin/productdetail.action";
import { checkQuantityRequest } from "@/infrastructure/services/api/admin/product_detail.api";
import {
  warningNotiSort,
  successNotiSort,
  errorNotiSort,
  notificationType,
  openNotification,
} from "@/utils/notification.config";

const { cart, totalAmount, addProduct, removeProduct, updateProductQuantity } =
  useCartStorageBL();

const productId = ref<string | null>("");

const router = useRouter();

const paramsProduct = ref<ClientProductRequest>({
  idChatLieu: "",
  idCoAo: "",
  idDanhMuc: "",
  idHoaTiet: "",
  idKieuDang: "",
  idTayAo: "",
  idThuongHieu: "",
  idTinhNang: "",
  gioiTinh: "",
});

const paramsDetail = ref<ClientProductDetailRequest>({
  idChatLieu: "",
  idCoAo: "",
  idDanhMuc: "",
  idHoaTiet: "",
  idKieuDang: "",
  idTayAo: "",
  idThuongHieu: "",
  idTinhNang: "",
  idKichCo: "",
  idMauSac: "",
  gioiTinh: ""
});

onMounted(() => {
  productId.value = useRoute().params.id as string;
  try {
    const storedParams = localStorage.getItem("productDetailParams");
    if (storedParams) {
      paramsProduct.value = JSON.parse(storedParams);
      paramsDetail.value.idChatLieu = paramsProduct.value.idChatLieu;
      paramsDetail.value.idDanhMuc = paramsProduct.value.idDanhMuc;
      paramsDetail.value.idCoAo = paramsProduct.value.idCoAo;
      paramsDetail.value.idHoaTiet = paramsProduct.value.idHoaTiet;
      paramsDetail.value.idKieuDang = paramsProduct.value.idKieuDang;
      paramsDetail.value.idTayAo = paramsProduct.value.idTayAo;
      paramsDetail.value.idThuongHieu = paramsProduct.value.idThuongHieu;
      paramsDetail.value.idTinhNang = paramsProduct.value.idTinhNang;
      paramsDetail.value.gioiTinh = paramsProduct.value.gioiTinh;
    }
  } catch (e) {
    console.error("Error parsing stored params:", e);
  }
});

// Hàm lấy tất cả các biến thể của sản phẩm đó
const {
  data: productResponse,
  isLoading,
  error,
} = useGetProductById(productId, paramsProduct, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
  enabled: !!paramsProduct.value,
});

const params = ref<checkQuantityRequest>({
  id: null,
  quantity: null,
});

const { data: checkQuantityData, refetch: checkQuantityRefetch } =
  useCheckQuantityInStockByProductDetail(params, {
    refetchOnWindowFocus: false,
    keepPreviousData: false,
    enabled: false,
  });

const product = computed(() => {
  return productResponse.value?.data.data;
});

// State for selected options
const selectedImage = ref("");
const selectedSize = ref("");
const selectedColor = ref("");
const quantity = ref(1);

const displayedPrice = ref<number[]>([]);
const displayedDiscount = ref<number[]>([]);
const allProductImages = ref<string[]>([]); // Danh sách tất cả ảnh sản phẩm
const currentVariantImage = ref<string | null>(null); // Ảnh của variant hiện tại

watch(
  product,
  (newProduct) => {
    if (newProduct) {
      console.log(newProduct);
      
      displayedPrice.value = newProduct.gia || [];
      displayedDiscount.value = newProduct.discount || [];

      // Lưu tất cả ảnh sản phẩm vào biến riêng
      if (newProduct.anh && Array.isArray(newProduct.anh)) {
        allProductImages.value = newProduct.anh.map((img) => img.url);

        if (allProductImages.value.length > 0) {
          selectedImage.value = allProductImages.value[0];
        } else {
          selectedImage.value = "/default-product-image.jpg";
        }
      } else {
        allProductImages.value = [];
        selectedImage.value = "/default-product-image.jpg";
      }

      if (newProduct.kichCo && newProduct.kichCo.length > 0) {
        selectedSize.value = newProduct.kichCo[0].id;
        paramsDetail.value.idKichCo = selectedSize.value;
      }

      if (newProduct.color && newProduct.color.length > 0) {
        selectedColor.value = newProduct.color[0].id;
        paramsDetail.value.idMauSac = selectedColor.value;
      }
    }
  },
  { immediate: true }
);

// Hàm lấy sản phẩm detail dựa vào kích thước màu sắc
const { data: dataDetail, refetch: refetchDetail } = useGetProductDetailById(
  productId,
  paramsDetail,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled:
      !!productId.value &&
      (!!paramsDetail.value.idMauSac || !!paramsDetail.value.idKichCo),
  }
);

watch(selectedSize, (newSize) => {
  if (newSize) {
    paramsDetail.value.idKichCo = newSize;
    refetchDetail();
  }
});

watch(selectedColor, (newColor) => {
  if (newColor) {
    paramsDetail.value.idMauSac = newColor;
    refetchDetail();
  }
});

// Computed property lấy giá trị phanTramGiam từ dataDetail nếu có
const effectivePhanTramGiam = computed(() => {
  if (
    dataDetail.value &&
    dataDetail.value.data &&
    dataDetail.value.data.data &&
    dataDetail.value.data.data.phanTramGiam &&
    dataDetail.value.data.data.phanTramGiam.length > 0
  ) {
    return dataDetail.value.data.data.phanTramGiam[0];
  }
  return null;
});

watch(dataDetail, (newDetail) => {
  //console.log(dataDetail.value);
  
  if (newDetail && newDetail.data && newDetail?.data?.data) {
    const detailData = newDetail?.data?.data;
    if (detailData.gia && detailData.gia.length > 0) {
      displayedPrice.value = detailData.gia;
    }
    displayedDiscount.value = detailData.discount;
    if (
      detailData.anh &&
      Array.isArray(detailData.anh) &&
      detailData.anh.length > 0
    ) {
      const variantImage = detailData.anh[0].url;
      if (!allProductImages.value.includes(variantImage)) {
        allProductImages.value.unshift(variantImage);
      }
      selectedImage.value = variantImage;
    }
  }
});

const getAttributeName = (attribute) => {
  if (!attribute) return "N/A";
  return Array.isArray(attribute)
    ? attribute.map((item) => item.ten).join(", ")
    : attribute.ten || "N/A";
};

const addToCart = () => {
  if (!product.value) return;

  addProduct({
    id: dataDetail?.value?.data?.data?.maSPCTs[0],
    name: dataDetail?.value?.data?.data?.ten,
    anh:
      dataDetail?.value?.data?.data?.anh.length > 0
        ? dataDetail?.value?.data?.data?.anh[0].url
        : "/default-product-image.jpg",
    sizeName: dataDetail?.value?.data?.data?.kichCo[0].ten,
    colorName: dataDetail?.value?.data?.data?.color[0].name,
    size: selectedSize.value,
    color: selectedColor.value,
    quantity: quantity.value,
    price:
      displayedDiscount.value && displayedDiscount.value.length > 0
        ? displayedDiscount.value[0]
        : displayedPrice.value[0],
  });
  successNotiSort("Thêm vào giỏ thành công.");
};

const buyNow = () => {
  router.push({
    name: "client-cart",
  });
  addToCart();
};

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
  }
};

const increaseQuantity = async () => {
  
  params.value.id = dataDetail?.value?.data?.data?.maSPCTs[0];
  params.value.quantity = quantity.value + 1;
  console.log(params.value);
  
  await checkQuantityRefetch();
  const checkValue = checkQuantityData?.value?.data;
  if (!checkValue) {
    warningNotiSort("Số lượng trong kho không đủ!");
  } else {
    quantity.value++;
  }
};
</script>