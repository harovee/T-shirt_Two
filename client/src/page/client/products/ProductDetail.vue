
<template>
  <div class="product-detail-container" v-if="product">
    <h4 class="breadcrumb-title">
        <router-link to="/home" class="breadcrumb-link">Trang chủ</router-link> / 
        <router-link to="/products" class="breadcrumb-link">Sản phẩm</router-link> / 
        {{ product.ten }}
      </h4>
    <a-row :gutter="[24, 24]">
      <!-- Left side - Product Images -->
      <a-col :span="12">
        <div class="product-images">
          <div class="main-image-container">
            <img 
              :src="selectedImage" 
              :alt="product.ten" 
              class="main-image" 
            />
          </div>
          <div class="thumbnail-container">
            <img 
              v-for="(image, index) in displayedImages" 
              :key="index" 
              :src="image" 
              :alt="`${product.ten} - ảnh ${index + 1}`" 
              class="thumbnail" 
              :class="{ 'active': selectedImage === image }"
              @click="selectedImage = image"
            />
          </div>
        </div>
      </a-col>
      
      <!-- Right side - Product Information -->
      <a-col :span="12">
        <div class="product-info">
          <h1 class="product-title">{{ product.ten }}</h1>
          
          <div class="product-price">
            <span 
              v-if="displayedDiscount && displayedDiscount.length > 0" 
              class="original-price"
            >
              {{ formatCurrency(displayedPrice && displayedPrice.length > 0 ? displayedPrice[0] : 0, "VND", "vi-VN") }}
            </span>
            <span class="discounted-price">
              {{ 
                displayedDiscount && displayedDiscount.length > 0 
                  ? formatCurrency(displayedDiscount[0], "VND", "vi-VN") 
                  : formatCurrency(displayedPrice && displayedPrice.length > 0 ? displayedPrice[0] : 0, "VND", "vi-VN") 
              }}
            </span>
          </div>
          
          <!-- Size selection -->
          <div class="product-size mb-4" v-if="product.kichCo && product.kichCo.length > 0">
            <p class="attribute-label">Kích cỡ:</p>
            <div class="size-options">
              <a-radio-group v-model:value="selectedSize">
                <a-radio-button 
                  v-for="(size, index) in product.kichCo" 
                  :key="index" 
                  :value="size.id"
                >
                  {{ size.ten }}
                </a-radio-button>
              </a-radio-group>
            </div>
          </div>
          
          <!-- Color selection -->
          <div class="product-color mb-4" v-if="product.color && product.color.length > 0">
                <p class="attribute-label">Màu sắc:</p>
                <div class="color-options">
                  <a-radio-group v-model:value="selectedColor">
                    <a-radio-button v-for="(mausac, index) in product.color" :key="index" :value="mausac.id">
                        <span :style="{ backgroundColor: mausac.code, width: '30px', height: '30px', 
                        display: 'inline-block', borderRadius: '50%' }"></span>
                  </a-radio-button>
              </a-radio-group>
                </div>
              </div>
          
              <div class="product-quantity mb-4">
              <p class="attribute-label">Số lượng:</p>
              <div class="quantity-container">
                <button @click="decreaseQuantity" class="quantity-btn">−</button>
                <input type="text" v-model="quantity" class="quantity-input" readonly />
                <button @click="increaseQuantity" class="quantity-btn">+</button>
              </div>
            </div>
        
          <div class="product-actions">
            <a-button type="primary" size="large" class="mr-4" @click="addToCart">
              <shopping-cart-outlined /> Thêm vào giỏ hàng
            </a-button>
            <a-button type="default" size="large" @click="buyNow">
              Mua ngay
            </a-button>
          </div>
          
          <!-- Product description -->
          <div class="product-description mt-6">
            <a-divider />
            <h3>Mô tả sản phẩm</h3>
            <p>{{ product.moTa || 'Không có mô tả cho sản phẩm này.' }}</p>
          </div>
          
          <!-- Product details -->
          <div class="product-details mt-4">
            <h3>Thông tin chi tiết</h3>
            <a-descriptions :column="1">
              <a-descriptions-item label="Thương hiệu">{{ getAttributeName(product.thuongHieu) }}</a-descriptions-item>
              <a-descriptions-item label="Chất liệu">{{ getAttributeName(product.chatLieu) }}</a-descriptions-item>
              <a-descriptions-item label="Kiểu dáng">{{ getAttributeName(product.kieuDang) }}</a-descriptions-item>
              <a-descriptions-item label="Cổ áo">{{ getAttributeName(product.coAo) }}</a-descriptions-item>
              <a-descriptions-item label="Tay áo">{{ getAttributeName(product.tayAo) }}</a-descriptions-item>
              <a-descriptions-item label="Họa tiết">{{ getAttributeName(product.hoaTiet) }}</a-descriptions-item>
            </a-descriptions>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts">
export default {
name: 'client-product-detail',
};
</script>

<script lang="ts" setup>
import { ref, computed, onMounted, watch } from 'vue';
import { formatCurrency } from '@/utils/common.helper';
import { ShoppingCartOutlined } from '@ant-design/icons-vue';
import { ClientProductDetailRequest, ClientProductRequest } from '@/infrastructure/services/api/client/clientproduct.api';
import { useGetProductById, useGetProductDetailById } from "@/infrastructure/services/service/client/productclient.action";
import { useRoute } from 'vue-router';
import { keepPreviousData } from '@tanstack/vue-query';

const productId = ref<string | null>("");

const paramsProduct = ref<ClientProductRequest>({
  idChatLieu: "",
  idCoAo: "",
  idDanhMuc: "",
  idHoaTiet: "",
  idKieuDang: "",
  idTayAo: "",
  idThuongHieu: "",
  idTinhNang: ""
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
  idMauSac: ""
});

onMounted(() => {
  productId.value = useRoute().params.id as string;
  try {
    const storedParams = localStorage.getItem('productDetailParams');
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
    }
  } catch (e) {
    console.error('Error parsing stored params:', e);
  }
});

const { data: productResponse, isLoading, error } = useGetProductById(
  productId,
  paramsProduct,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled: !!paramsProduct.value
  }
);

const product = computed(() => {
  return productResponse.value?.data.data;
});

// State for selected options
const selectedImage = ref('');
const selectedSize = ref('');
const selectedColor = ref('');
const quantity = ref(1);

// Create refs for the displayed values that may change with variant selection
const displayedPrice = ref<number[]>([]);
const displayedDiscount = ref<number[]>([]);
const displayedImages = ref<string[]>([]);

// Initial setup when product data loads
watch(product, (newProduct) => {
  if (newProduct) {
    // Initialize displayed values with product defaults
    displayedPrice.value = newProduct.gia || [];
    displayedDiscount.value = newProduct.discount || [];
    displayedImages.value = newProduct.anh || [];
    
    // Set initial image
    if (newProduct.anh && newProduct.anh.length > 0) {

      selectedImage.value = newProduct.anh[0];
    } else {
      selectedImage.value = '/default-product-image.jpg';
    }
    
    // Set initial size and color
    if (newProduct.kichCo && newProduct.kichCo.length > 0) {
      selectedSize.value = newProduct.kichCo[0].id;
      paramsDetail.value.idKichCo = selectedSize.value;
    }
    
    if (newProduct.color && newProduct.color.length > 0) {
      selectedColor.value = newProduct.color[0].id;
      paramsDetail.value.idMauSac = selectedColor.value;
    }
  }
}, { immediate: true });

// Watch for size changes to update params and trigger refetch
watch(selectedSize, (newSize) => {
  if (newSize) {
    paramsDetail.value.idKichCo = newSize;
    // Force refetch with the new parameter
    refetchDetail();
  }
});

watch(selectedColor, (newColor) => {
  if (newColor) {
    paramsDetail.value.idMauSac = newColor;
    refetchDetail();
  }
});

const { data: dataDetail, refetch: refetchDetail } = useGetProductDetailById(
  productId,
  paramsDetail,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled: !!productId.value && (!!paramsDetail.value.idMauSac || !!paramsDetail.value.idKichCo)
  }
);

watch(dataDetail, (newDetail) => {
  if (newDetail && newDetail.data && newDetail.data.data) {
    const detailData = newDetail.data.data;
    
    if (detailData.gia && detailData.gia.length > 0) {
      displayedPrice.value = detailData.gia;
    }
    
    if (detailData.discount && detailData.discount.length > 0) {
      displayedDiscount.value = detailData.discount;
    }
    
    if (detailData.anh && detailData.anh.length > 0) {
      displayedImages.value = detailData.anh;
      selectedImage.value = detailData.anh[0];
    }
    
    // console.log('Updated product details with variant data:', detailData);
  }
});

const getAttributeName = (attribute) => {
  if (!attribute) return 'N/A';
  return Array.isArray(attribute) ? attribute.map(item => item.ten).join(', ') : attribute.ten || 'N/A';
};

const addToCart = () => {
  if (!product.value) return;
  
  console.log('Adding to cart:', {
    product: product.value,
    size: selectedSize.value,
    color: selectedColor.value,
    quantity: quantity.value,
    price: displayedDiscount.value.length > 0 ? displayedDiscount.value[0] : displayedPrice.value[0]
  });
};

const buyNow = () => {
  addToCart();
  console.log('Buy now clicked');
};
const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
  }
};

const increaseQuantity = () => {
  quantity.value++;
};
</script>

<style scoped>
.product-detail-container {
  padding: 24px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.main-image-container {
  width: 100%;
  height: 400px;
  overflow: hidden;
  margin-bottom: 16px;
  border: 1px solid #eee;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-container {
  display: flex;
  gap: 8px;
  overflow-x: auto;
}

.thumbnail {
  width: 80px;
  height: 80px;
  object-fit: cover;
  cursor: pointer;
  border: 2px solid transparent;
}

.thumbnail.active {
  border-color: #1890ff;
}

.product-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
}

.product-price {
  margin-bottom: 20px;
}

.original-price {
  text-decoration: line-through;
  color: grey;
  margin-right: 10px;
  font-size: 16px;
}

.discounted-price {
  font-weight: bold;
  font-size: 24px;
  color: red;
}

.attribute-label {
  font-weight: bold;
  margin-bottom: 8px;
}

.color-options {
  display: flex;
  gap: 8px;
}

.color-option {
  display: inline-block;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  border: 2px solid transparent;
}

.color-option.active {
  border-color: #1890ff;
}

.product-actions {
  margin-top: 24px;
}

.product-description, .product-details {
  margin-top: 24px;
}
.color-options {
display: flex;
gap: 10px;
align-items: center;
flex-wrap: wrap;
}

.color-option {
width: 40px;
height: 40px;
border-radius: 50%;
cursor: pointer;
border: 2px solid transparent;
transition: transform 0.2s ease, border-color 0.2s ease;
display: flex;
align-items: center;
justify-content: center;
position: relative;
}

.color-option:hover {
transform: scale(1.1);
}

.color-option.active {
border-color: #1890ff;
box-shadow: 0 0 5px rgba(24, 144, 255, 0.5);
}

.color-option.active::after {
content: '✔';
color: white;
font-size: 20px;
font-weight: bold;
position: absolute;
top: 50%;
left: 50%;
transform: translate(-50%, -50%);
}
.breadcrumb-title {
font-size: 16px;
font-weight: 500;
color: #555;
margin-bottom: 20px;
margin-top: 15px;
}
.breadcrumb-title {
font-size: 16px;
font-weight: 500;
color: #555;
margin-bottom: 16px;
}

.breadcrumb-link {
color: #1890ff;
text-decoration: none;
transition: color 0.2s ease-in-out;
}

.breadcrumb-link:hover {
color: #0056b3;
text-decoration: underline;
}
.quantity-container {
display: flex;
align-items: center;
gap: 8px;
}

.quantity-btn {
width: 32px;
height: 32px;
font-size: 20px;
font-weight: bold;
border: none;
background-color: #ffff;
color: rgb(30, 28, 28);
border-radius: 20px;
cursor: pointer;
transition: 0.2s ease;
}

.quantity-btn:hover {
background-color: #96999a;
}

.quantity-input {
width: 50px;
height: 32px;
text-align: center;
border: 1px solid #ccc;
border-radius: 4px;
font-size: 16px;
font-weight: bold;
}
</style>
