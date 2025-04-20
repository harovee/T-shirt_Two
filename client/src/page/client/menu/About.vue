<template>
    <div class="about-page container mx-auto py-12 px-4 md:px-8">
      <!-- Banner Section -->
      <div class="relative mb-16">
        <div class="h-64 md:h-80 bg-gray-800 rounded-lg overflow-hidden">
          <img 
            src="@/assets/image/banner/banner3.jpg" 
            alt="Banner About Us" 
            class="w-full h-full object-cover opacity-60"
            onerror="this.src=''; this.onerror=null;"
          />
        </div>
        <div class="absolute inset-0 flex items-center justify-center">
          <h1 class="text-4xl md:text-5xl font-bold text-white text-center drop-shadow-lg">
            GIỚI THIỆU VỀ T-SHIRTS TWO
          </h1>
        </div>
      </div>
  
      <!-- About Us Content -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-12 items-center mb-16">
        <div class="about-content">
          <h2 class="text-3xl font-bold mb-6 text-gray-800">Câu Chuyện Của Chúng Tôi</h2>
          <p class="text-lg text-gray-600 mb-4">
            T-Shirts Two được thành lập vào năm 2020 với sứ mệnh mang đến những sản phẩm áo thun chất lượng cao, thiết kế độc đáo và phù hợp với văn hóa Việt Nam.
          </p>
          <p class="text-lg text-gray-600 mb-4">
            Từ một cửa hàng nhỏ, chúng tôi đã phát triển thành một thương hiệu được yêu thích bởi sự sáng tạo không ngừng và cam kết về chất lượng sản phẩm.
          </p>
          <p class="text-lg text-gray-600">
            Mỗi chiếc áo tại T-Shirts Two đều được thiết kế tỉ mỉ và sản xuất từ những chất liệu tốt nhất, đảm bảo sự thoải mái và bền đẹp theo thời gian.
          </p>
        </div>
  
        <div class="about-image rounded-lg overflow-hidden shadow-xl">
          <img 
            src="https://hoangphucphoto.com/wp-content/uploads/2024/07/14-bi-quyet-chup-anh-chan-dung-theo-nhom13.jpg" 
            alt="Our Team" 
            class="w-full h-full object-cover"
            onerror="this.src=''; this.onerror=null;"
          />
        </div>
      </div>
  
      <!-- Our Values -->
      <div class="mb-16">
        <h2 class="text-3xl font-bold mb-10 text-center text-gray-800">Giá Trị Cốt Lõi</h2>
        
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div class="value-card p-6 bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300">
            <div class="flex justify-center mb-4">
              <div class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center">
                <v-icon name="bi-stars" class="w-8 h-8 text-red-500"></v-icon>
              </div>
            </div>
            <h3 class="text-xl font-bold mb-3 text-center">Chất Lượng</h3>
            <p class="text-gray-600 text-center">
              Chúng tôi không ngừng tìm kiếm những chất liệu vải tốt nhất và quy trình sản xuất hiện đại để đảm bảo mỗi sản phẩm đều đạt chuẩn chất lượng cao.
            </p>
          </div>
  
          <div class="value-card p-6 bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300">
            <div class="flex justify-center mb-4">
              <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
                <v-icon name="bi-palette" class="w-8 h-8 text-green-500"></v-icon>
              </div>
            </div>
            <h3 class="text-xl font-bold mb-3 text-center">Sáng Tạo</h3>
            <p class="text-gray-600 text-center">
              Đội ngũ thiết kế của chúng tôi luôn đổi mới và sáng tạo không ngừng để mang đến những mẫu áo độc đáo, phản ánh xu hướng hiện đại và văn hóa đương đại.
            </p>
          </div>
  
          <div class="value-card p-6 bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300">
            <div class="flex justify-center mb-4">
              <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center">
                <v-icon name="bi-people" class="w-8 h-8 text-blue-500"></v-icon>
              </div>
            </div>
            <h3 class="text-xl font-bold mb-3 text-center">Cộng Đồng</h3>
            <p class="text-gray-600 text-center">
              Chúng tôi tin vào việc xây dựng mối quan hệ bền vững với khách hàng và cộng đồng, luôn lắng nghe phản hồi để cải thiện sản phẩm và dịch vụ.
            </p>
          </div>
        </div>
      </div>
  
      <!-- Our Team -->
      <div class="mb-16">
        <h2 class="text-3xl font-bold mb-10 text-center text-gray-800">Đội Ngũ Của Chúng Tôi</h2>
        
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-6">
          <div class="team-member text-center">
            <div class="mb-4 rounded-full overflow-hidden w-48 h-48 mx-auto shadow-md">
              <img 
                src="https://png.pngtree.com/png-clipart/20210608/ourlarge/pngtree-dark-gray-simple-avatar-png-image_3418404.jpg" 
                alt="Team Member" 
                class="w-full h-full object-cover"
              />
            </div>
            <h3 class="text-xl font-bold mb-1">Bùi Minh Hiếu</h3>
            <p class="text-gray-500 mb-2">Leader</p>
            <div class="flex justify-center space-x-3">
              <a href="#" class="text-gray-400 hover:text-blue-500">
                <v-icon name="bi-facebook" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-blue-400">
                <v-icon name="bi-twitter" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-pink-500">
                <v-icon name="bi-instagram" class="w-5 h-5"></v-icon>
              </a>
            </div>
          </div>
  
          <div class="team-member text-center">
            <div class="mb-4 rounded-full overflow-hidden w-48 h-48 mx-auto shadow-md">
              <img 
                src="https://png.pngtree.com/png-clipart/20210608/ourlarge/pngtree-dark-gray-simple-avatar-png-image_3418404.jpg" 
                alt="Team Member" 
                class="w-full h-full object-cover"
                onerror="this.src=''; this.onerror=null;"
              />
            </div>
            <h3 class="text-xl font-bold mb-1">Trần Thị Ánh Quỳnh</h3>
            <p class="text-gray-500 mb-2">Thiết Kế,Dev</p>
            <div class="flex justify-center space-x-3">
              <a href="#" class="text-gray-400 hover:text-blue-500">
                <v-icon name="bi-facebook" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-blue-400">
                <v-icon name="bi-twitter" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-pink-500">
                <v-icon name="bi-instagram" class="w-5 h-5"></v-icon>
              </a>
            </div>
          </div>
  
          <div class="team-member text-center">
            <div class="mb-4 rounded-full overflow-hidden w-48 h-48 mx-auto shadow-md">
              <img 
                src="https://png.pngtree.com/png-clipart/20210608/ourlarge/pngtree-dark-gray-simple-avatar-png-image_3418404.jpg" 
                alt="Team Member" 
                class="w-full h-full object-cover"
                onerror="this.src=''; this.onerror=null;"
              />
            </div>
            <h3 class="text-xl font-bold mb-1">Phan Quang Hảo</h3>
            <p class="text-gray-500 mb-2">Dev</p>
            <div class="flex justify-center space-x-3">
              <a href="#" class="text-gray-400 hover:text-blue-500">
                <v-icon name="bi-facebook" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-blue-400">
                <v-icon name="bi-twitter" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-pink-500">
                <v-icon name="bi-instagram" class="w-5 h-5"></v-icon>
              </a>
            </div>
          </div>
  
          <div class="team-member text-center">
            <div class="mb-4 rounded-full overflow-hidden w-48 h-48 mx-auto shadow-md">
              <img 
                src="https://png.pngtree.com/png-clipart/20210608/ourlarge/pngtree-dark-gray-simple-avatar-png-image_3418404.jpg" 
                alt="Team Member" 
                class="w-full h-full object-cover"
                onerror="this.src=''; this.onerror=null;"
              />
            </div>
            <h3 class="text-xl font-bold mb-1">Phạm Thị Lan Anh</h3>
            <p class="text-gray-500 mb-2">Tester</p>
            <div class="flex justify-center space-x-3">
              <a href="#" class="text-gray-400 hover:text-blue-500">
                <v-icon name="bi-facebook" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-blue-400">
                <v-icon name="bi-twitter" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-pink-500">
                <v-icon name="bi-instagram" class="w-5 h-5"></v-icon>
              </a>
            </div>
          </div>

          <div class="team-member text-center">
            <div class="mb-4 rounded-full overflow-hidden w-48 h-48 mx-auto shadow-md">
              <img 
                src="https://png.pngtree.com/png-clipart/20210608/ourlarge/pngtree-dark-gray-simple-avatar-png-image_3418404.jpg" 
                alt="Team Member" 
                class="w-full h-full object-cover"
                onerror="this.src=''; this.onerror=null;"
              />
            </div>
            <h3 class="text-xl font-bold mb-1">Nguyễn Thu Thảo</h3>
            <p class="text-gray-500 mb-2">Dev</p>
            <div class="flex justify-center space-x-3">
              <a href="#" class="text-gray-400 hover:text-blue-500">
                <v-icon name="bi-facebook" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-blue-400">
                <v-icon name="bi-twitter" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-pink-500">
                <v-icon name="bi-instagram" class="w-5 h-5"></v-icon>
              </a>
            </div>
          </div>

          <div class="team-member text-center">
            <div class="mb-4 rounded-full overflow-hidden w-48 h-48 mx-auto shadow-md">
              <img 
                src="https://png.pngtree.com/png-clipart/20210608/ourlarge/pngtree-dark-gray-simple-avatar-png-image_3418404.jpg" 
                alt="Team Member" 
                class="w-full h-full object-cover"
                onerror="this.src=''; this.onerror=null;"
              />
            </div>
            <h3 class="text-xl font-bold mb-1">Hoàng Văn Thảnh</h3>
            <p class="text-gray-500 mb-2">Dev</p>
            <div class="flex justify-center space-x-3">
              <a href="#" class="text-gray-400 hover:text-blue-500">
                <v-icon name="bi-facebook" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-blue-400">
                <v-icon name="bi-twitter" class="w-5 h-5"></v-icon>
              </a>
              <a href="#" class="text-gray-400 hover:text-pink-500">
                <v-icon name="bi-instagram" class="w-5 h-5"></v-icon>
              </a>
            </div>
          </div>
        </div>
      </div>
  
      <!-- Call to Action -->
      <div class="bg-gray-100 rounded-lg p-8 md:p-12 text-center">
        <h2 class="text-2xl md:text-3xl font-bold mb-4">Trở Thành Khách Hàng Của T-Shirts Two</h2>
        <p class="text-lg text-gray-600 mb-6 max-w-3xl mx-auto">
          Khám phá bộ sưu tập áo thun mới nhất của chúng tôi và tận hưởng trải nghiệm mua sắm tuyệt vời ngay hôm nay.
        </p>
        <router-link to="/products" class="inline-block bg-red-600 hover:bg-red-700 text-white font-bold py-3 px-8 rounded-lg transition duration-300">
          Khám Phá Ngay
        </router-link>
      </div>
    </div>
  </template>
  
  <script lang="ts" setup>
  // Không cần imports đặc biệt nào
  </script>
  
  <script lang="ts">
  export default {
    name: "AboutPage",
  };
  </script>
  
  <style scoped>
  /* Nếu cần thêm CSS đặc biệt ngoài Tailwind */
  </style>