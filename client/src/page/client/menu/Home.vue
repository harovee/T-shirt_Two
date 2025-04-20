<template>
  <div>
    <banner />
  </div>
  <!-- Service features section -->
  <div class="bg-gray-50 py-8 mt-4">
      <div class="container mx-auto">
        <div class="grid grid-cols-4 gap-4">
          <div class="flex flex-col items-center p-4 bg-white rounded-lg shadow-sm">
            <div class="w-16 h-16 mb-4 rounded-full bg-blue-50 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-800">Giao Hàng Nhanh</h3>
            <p class="text-center text-gray-600 text-sm mt-2">Miễn phí giao hàng cho đơn từ 500K</p>
          </div>
          
          <div class="flex flex-col items-center p-4 bg-white rounded-lg shadow-sm">
            <div class="w-16 h-16 mb-4 rounded-full bg-green-50 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-800">Thanh Toán An Toàn</h3>
            <p class="text-center text-gray-600 text-sm mt-2">Nhiều phương thức thanh toán</p>
          </div>
          
          <div class="flex flex-col items-center p-4 bg-white rounded-lg shadow-sm">
            <div class="w-16 h-16 mb-4 rounded-full bg-purple-50 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-purple-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-800">Sản Phẩm Chất Lượng</h3>
            <p class="text-center text-gray-600 text-sm mt-2">Chứng nhận chất lượng cao</p>
          </div>
          
          <div class="flex flex-col items-center p-4 bg-white rounded-lg shadow-sm">
            <div class="w-16 h-16 mb-4 rounded-full bg-red-50 flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-5 0a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-gray-800">Hỗ Trợ 24/7</h3>
            <p class="text-center text-gray-600 text-sm mt-2">Luôn sẵn sàng hỗ trợ khách hàng</p>
          </div>
        </div>
      </div>
    </div>

    <div class="container mx-auto px-4">
      <!-- New products section with better heading -->
      <div class="mt-12 mb-8">
        <div class="flex flex-col items-center">
          <h2 class="text-3xl font-bold text-gray-800">Sản Phẩm Mới Về</h2>
          <div class="w-24 h-1 bg-blue-500 rounded-full mt-2 mb-6"></div>
          <p class="text-gray-600 text-center max-w-2xl mb-8">
            Khám phá những thiết kế mới nhất trong bộ sưu tập của chúng tôi, mang đến phong cách hiện đại và đẳng cấp
          </p>
        </div>
        <cart-product />
      </div>

      <!-- Promotional banner -->
      <div class="relative rounded-lg overflow-hidden my-16">
        <img src="https://file.hstatic.net/200000503583/file/banner_web_-_06.06-03_4f11b0620f1943189a4bf143b8f40d97.jpg" alt="Banner khuyến mãi" class="w-full h-full object-cover" />
        <div class="absolute inset-0 bg-gradient-to-r from-blue-900/70 to-transparent flex items-center">
          <div class="ml-16 max-w-lg">
            <h3 class="text-white text-3xl font-bold mb-4">Bộ Sưu Tập Xuân Hè 2025</h3>
            <p class="text-white text-lg mb-6">Khám phá những xu hướng thời trang mới nhất với ưu đãi lên đến 30%</p>
            <button class="bg-white text-blue-900 font-bold px-6 py-3 rounded-lg hover:bg-blue-50 transition duration-300">
              Khám phá ngay
            </button>
          </div>
        </div>
      </div>

      <!-- Best selling products section -->
      <div class="mt-12 mb-8">
        <div class="flex flex-col items-center">
          <h2 class="text-3xl font-bold text-gray-800">Sản Phẩm Bán Chạy</h2>
          <div class="w-24 h-1 bg-green-500 rounded-full mt-2 mb-6"></div>
          <p class="text-gray-600 text-center max-w-2xl mb-8">
            Những sản phẩm được yêu thích nhất và được nhiều khách hàng lựa chọn
          </p>
        </div>
        <cart-product-best-sale />
      </div>

      <!-- News and certification banner -->
      <div class="bg-gray-50 rounded-lg p-8 my-16">
        <div class="grid grid-cols-2 gap-8">
          <div>
            <h3 class="text-2xl font-bold text-gray-800 mb-4">Chứng Nhận Chất Lượng</h3>
            <div class="flex space-x-6 mt-6">
              <div class="flex flex-col items-center">
                <div class="w-20 h-20 bg-white rounded-full flex items-center justify-center shadow-sm">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
                  </svg>
                </div>
                <span class="text-sm font-medium mt-2">ISO 9001</span>
              </div>
              <div class="flex flex-col items-center">
                <div class="w-20 h-20 bg-white rounded-full flex items-center justify-center shadow-sm">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                  </svg>
                </div>
                <span class="text-sm font-medium mt-2">Eco Friendly</span>
              </div>
              <div class="flex flex-col items-center">
                <div class="w-20 h-20 bg-white rounded-full flex items-center justify-center shadow-sm">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 6l3 1m0 0l-3 9a5.002 5.002 0 006.001 0M6 7l3 9M6 7l6-2m6 2l3-1m-3 1l-3 9a5.002 5.002 0 006.001 0M18 7l3 9m-3-9l-6-2m0-2v2m0 16V5m0 16H9m3 0h3" />
                  </svg>
                </div>
                <span class="text-sm font-medium mt-2">Fair Trade</span>
              </div>
            </div>
          </div>
          
          <div>
            <h3 class="text-2xl font-bold text-gray-800 mb-4">Tin Tức Mới Nhất</h3>
            <div class="space-y-4">
              <div class="bg-white p-4 rounded-lg shadow-sm">
                <div class="text-sm text-blue-600 mb-1">10/04/2025</div>
                <h4 class="font-semibold">Bộ sưu tập mùa hè 2025 đã ra mắt</h4>
              </div>
              <div class="bg-white p-4 rounded-lg shadow-sm">
                <div class="text-sm text-blue-600 mb-1">08/04/2025</div>
                <h4 class="font-semibold">Chương trình khách hàng thân thiết mới</h4>
              </div>
              <div class="bg-white p-4 rounded-lg shadow-sm">
                <div class="text-sm text-blue-600 mb-1">05/04/2025</div>
                <h4 class="font-semibold">Khai trương cửa hàng mới tại TPHCM</h4>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Sale products section -->
      <div class="mt-12 mb-8">
        <div class="flex flex-col items-center">
          <h2 class="text-3xl font-bold text-gray-800">Sản Phẩm Giảm Giá</h2>
          <div class="w-24 h-1 bg-red-500 rounded-full mt-2 mb-6"></div>
          <p class="text-gray-600 text-center max-w-2xl mb-8">
            Cơ hội sở hữu những sản phẩm chất lượng với giá ưu đãi hấp dẫn
          </p>
        </div>
        <cart-sale-product />
      </div>

      <!-- CTA for all products -->
      <div class="flex justify-center mb-16">
        <a-button 
          type="text" 
          class="h-12 w-48 border-2 border-gray-300 hover:border-gray-500 rounded-lg px-4 text-base font-bold text-gray-700 hover:text-gray-900 hover:bg-gray-50 transition-all duration-300"
          @click="handleRedirectAllProduct"
        >
          Xem Tất Cả Sản Phẩm
        </a-button>
      </div>
      <!-- Store News Section -->
<div class="bg-gray-100 py-8 mt-12 mb-0">
  <div class="container mx-auto px-4">
    <div class="flex flex-col items-center mb-6">
      <h2 class="text-2xl font-bold text-gray-800">Tin Tức Cửa Hàng</h2>
      <div class="w-20 h-1 bg-indigo-500 rounded-full mt-2"></div>
    </div>
    
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow duration-300">
        <img src="https://file.hstatic.net/200000503583/file/banner_web_-_06.06-03_4f11b0620f1943189a4bf143b8f40d97.jpg" alt="Tin tức cửa hàng" class="w-full h-48 object-cover" />
        <div class="p-4">
          <div class="text-sm text-indigo-600 mb-1">12/04/2025</div>
          <h3 class="font-semibold text-lg mb-2">Chương trình khuyến mãi mùa hè 2025</h3>
          <p class="text-gray-600 text-sm mb-3">Giảm giá lên đến 50% cho tất cả sản phẩm mùa hè trong tháng 4 và tháng 5.</p>
          <a href="#" class="text-indigo-600 font-medium text-sm hover:text-indigo-800">Xem chi tiết →</a>
        </div>
      </div>
      
      <div class="bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow duration-300">
        <img src="https://channel.mediacdn.vn/428462621602512896/2022/4/13/photo-1-1649815481112327252855.jpg" alt="Tin tức cửa hàng" class="w-full h-48 object-cover" />
        <div class="p-4">
          <div class="text-sm text-indigo-600 mb-1">10/04/2025</div>
          <h3 class="font-semibold text-lg mb-2">Khai trương chi nhánh mới tại Hà Nội</h3>
          <p class="text-gray-600 text-sm mb-3">Chi nhánh mới tại 123 Nguyễn Trãi, Thanh Xuân, Hà Nội sẽ chính thức khai trương vào ngày 20/04/2025.</p>
          <a href="#" class="text-indigo-600 font-medium text-sm hover:text-indigo-800">Xem chi tiết →</a>
        </div>
      </div>
      
      <div class="bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-shadow duration-300">
        <img src="https://inkythuatso.com/uploads/thumbnails/800/2022/01/banner-quan-ao-inkythuatso-13-10-19-37.jpg" alt="Tin tức cửa hàng" class="w-full h-48 object-cover" />
        <div class="p-4">
          <div class="text-sm text-indigo-600 mb-1">05/04/2025</div>
          <h3 class="font-semibold text-lg mb-2">Bộ sưu tập mới nhất đã lên kệ</h3>
          <p class="text-gray-600 text-sm mb-3">Bộ sưu tập Xuân-Hè 2025 với nhiều thiết kế độc đáo và phong cách hiện đại đã có mặt tại tất cả cửa hàng.</p>
          <a href="#" class="text-indigo-600 font-medium text-sm hover:text-indigo-800">Xem chi tiết →</a>
        </div>
      </div>
    </div>
    
    <div class="flex justify-center mt-6">
      <a-button 
        type="text" 
        class="h-10 border border-indigo-500 rounded-lg px-4 text-sm font-medium text-indigo-600 hover:bg-indigo-50 transition-all duration-300"
        @click="handleRedirectToNews"
      >
        Xem tất cả tin tức
      </a-button>
    </div>
  </div>
</div>
      </div>
</template>

<script lang="ts" setup>
import Banner from "@/page/client/Banner.vue";
import CartProduct from "@/page/client/products/CartProduct.vue";
import CartProductBestSale from "@/page/client/products/CartProductBestSale.vue";
import CartSaleProduct from "@/page/client/products/CartSaleProduct.vue";
import router from "@/infrastructure/routes/router.ts";

const handleRedirectAllProduct = () => {
  router.push({ name: 'client-products' });
}

const handleRedirectToNews = () => {
  router.push({ name: 'client-news' }); // Thay 'client-news' bằng tên route của trang tin tức của bạn
}
</script>

<style scoped>
</style>