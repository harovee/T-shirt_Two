<template>
    <div class="contact-page container mx-auto py-12 px-4 md:px-8">
      <!-- Banner Section -->
      <div class="relative mb-16">
        <div class="h-64 md:h-80 bg-gray-800 rounded-lg overflow-hidden">
          <img 
            src="@/assets/image/banner/banner5.jpg" 
            alt="Banner Contact Us" 
            class="w-full h-full object-cover opacity-70"
            onerror="this.src=''; this.onerror=null;"
          />
        </div>
        <div class="absolute inset-0 flex items-center justify-center">
          <h1 class="text-4xl md:text-5xl font-bold text-white text-center drop-shadow-lg">
            LIÊN HỆ VỚI CHÚNG TÔI
          </h1>
        </div>
      </div>
  
      <!-- Content -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-10">
        <!-- Contact Information -->
        <div class="lg:col-span-1">
          <div class="bg-white rounded-lg shadow-md p-6 h-full">
            <h2 class="text-2xl font-bold mb-6 text-gray-800">Thông Tin Liên Hệ</h2>
            
            <div class="space-y-6">
              <div class="flex items-start">
                <div class="mr-4 mt-1">
                  <div class="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center">
                    <v-icon name="bi-geo-alt" class="w-5 h-5 text-red-500"></v-icon>
                  </div>
                </div>
                <div>
                  <h3 class="text-lg font-semibold mb-1">Địa Chỉ</h3>
                  <p class="text-gray-600">
                    Trường cao đẳng FPT Polytechnic, Đường Trịnh Văn Bô<br />
                    Quận Nam Từ Liêm, Thành phố Hà Nội, Việt Nam
                  </p>
                </div>
              </div>
              
              <div class="flex items-start">
                <div class="mr-4 mt-1">
                  <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                    <v-icon name="bi-telephone" class="w-5 h-5 text-blue-500"></v-icon>
                  </div>
                </div>
                <div>
                  <h3 class="text-lg font-semibold mb-1">Điện Thoại</h3>
                  <p class="text-gray-600">
                    Hotline: (028) 3456 7890<br />
                    Hỗ trợ: (028) 3456 7891
                  </p>
                </div>
              </div>
              
              <div class="flex items-start">
                <div class="mr-4 mt-1">
                  <div class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                    <v-icon name="bi-envelope" class="w-5 h-5 text-green-500"></v-icon>
                  </div>
                </div>
                <div>
                  <h3 class="text-lg font-semibold mb-1">Email</h3>
                  <p class="text-gray-600">
                    Hỗ trợ: tshirttwo2@gmail.com
                  </p>
                </div>
              </div>
              
              <div class="flex items-start">
                <div class="mr-4 mt-1">
                  <div class="w-10 h-10 bg-purple-100 rounded-full flex items-center justify-center">
                    <v-icon name="bi-clock" class="w-5 h-5 text-purple-500"></v-icon>
                  </div>
                </div>
                <div>
                  <h3 class="text-lg font-semibold mb-1">Giờ Làm Việc</h3>
                  <p class="text-gray-600">
                    8:30 - 22:00<br />
                    Hàng ngày
                  </p>
                </div>
              </div>
            </div>
            
            <!-- Social Media -->
            <div class="mt-8">
              <h3 class="text-lg font-semibold mb-4">Kết Nối Với Chúng Tôi</h3>
              <div class="flex space-x-4">
                <a href="#" class="w-10 h-10 bg-blue-600 text-white rounded-full flex items-center justify-center hover:bg-blue-700 transition duration-300">
                  <v-icon name="bi-facebook" class="w-5 h-5"></v-icon>
                </a>
                <a href="#" class="w-10 h-10 bg-pink-600 text-white rounded-full flex items-center justify-center hover:bg-pink-700 transition duration-300">
                  <v-icon name="bi-instagram" class="w-5 h-5"></v-icon>
                </a>
                <a href="#" class="w-10 h-10 bg-red-600 text-white rounded-full flex items-center justify-center hover:bg-red-700 transition duration-300">
                  <v-icon name="bi-youtube" class="w-5 h-5"></v-icon>
                </a>
                <a href="#" class="w-10 h-10 bg-blue-400 text-white rounded-full flex items-center justify-center hover:bg-blue-500 transition duration-300">
                  <v-icon name="bi-twitter" class="w-5 h-5"></v-icon>
                </a>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Contact Form -->
        <div class="lg:col-span-2">
          <div class="bg-white rounded-lg shadow-md p-6">
            <h2 class="text-2xl font-bold mb-6 text-gray-800">Gửi Thông Tin Liên Hệ</h2>
            
            <form @submit.prevent="submitForm">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label for="fullName" class="block text-sm font-medium text-gray-700 mb-1">Họ và tên</label>
                  <a-input 
                    id="fullName" 
                    v-model:value="form.fullName" 
                    placeholder="Nhập họ và tên của bạn"
                    class="w-full"
                  />
                </div>
                
                <div>
                  <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
                  <a-input 
                    id="email" 
                    v-model:value="form.email" 
                    placeholder="Nhập email của bạn"
                    class="w-full"
                  />
                </div>
                
                <div>
                  <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">Số điện thoại</label>
                  <a-input 
                    id="phone" 
                    v-model:value="form.phone" 
                    placeholder="Nhập số điện thoại của bạn"
                    class="w-full"
                  />
                </div>
                
                <div>
                  <label for="subject" class="block text-sm font-medium text-gray-700 mb-1">Chủ đề</label>
                  <a-select
                    id="subject"
                    v-model:value="form.subject"
                    placeholder="Chọn chủ đề"
                    class="w-full"
                  >
                    <a-select-option value="general">Thông tin chung</a-select-option>
                    <a-select-option value="products">Thông tin sản phẩm</a-select-option>
                    <a-select-option value="order">Đơn hàng</a-select-option>
                    <a-select-option value="support">Hỗ trợ kỹ thuật</a-select-option>
                    <a-select-option value="other">Khác</a-select-option>
                  </a-select>
                </div>
                
                <div class="md:col-span-2">
                  <label for="message" class="block text-sm font-medium text-gray-700 mb-1">Nội dung</label>
                  <a-textarea
                    id="message"
                    v-model:value="form.message"
                    placeholder="Nhập nội dung tin nhắn của bạn"
                    :rows="6"
                    class="w-full"
                  />
                </div>
                
                <div class="md:col-span-2">
                  <a-checkbox v-model:checked="form.subscribe">
                    Đăng ký nhận thông tin và khuyến mãi từ T-Shirts Two
                  </a-checkbox>
                </div>
                
                <div class="md:col-span-2">
                  <a-button 
                    type="primary" 
                    html-type="submit"
                    class="bg-red-600 hover:bg-red-700 border-red-600 hover:border-red-700 h-10 px-8"
                    :loading="loading"
                  >
                    Gửi Tin Nhắn
                  </a-button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
      
      <!-- Map -->
      <div class="mt-16">
            <h2 class="text-2xl font-bold mb-6 text-gray-800">Địa Điểm Cửa Hàng</h2>
            <div class="h-96 bg-gray-200 rounded-lg overflow-hidden">
                <!-- Google Maps Embed -->
                <iframe 
                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3723.863855881427!2d105.74459841476884!3d21.03813778599332!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x313454b991d80fd5%3A0x53cefc99d6b0bf6f!2zRlBUIFBvbHl0ZWNobmljIEjDoCBO4buZaQ!5e0!3m2!1svi!2s!4v1650000000000!5m2!1svi!2s" 
                width="100%" 
                height="100%" 
                style="border:0;" 
                allowfullscreen 
                loading="lazy"
                referrerpolicy="no-referrer-when-downgrade">
                </iframe>
            </div>
            </div>
  
      <!-- Frequently Asked Questions -->
      <div class="mt-16">
        <h2 class="text-2xl font-bold mb-6 text-gray-800">Câu Hỏi Thường Gặp</h2>
        
        <div class="space-y-4">
          <a-collapse>
            <a-collapse-panel key="1" header="Làm thế nào để theo dõi đơn hàng của tôi?">
              <p class="text-gray-600">
                Bạn có thể theo dõi đơn hàng bằng cách đăng nhập vào tài khoản của mình và truy cập mục "Đơn hàng đã mua". 
                Mỗi đơn hàng sẽ hiển thị trạng thái hiện tại và thông tin vận chuyển nếu có.
              </p>
            </a-collapse-panel>
            
            <a-collapse-panel key="2" header="Chính sách đổi trả hàng như thế nào?">
              <p class="text-gray-600">
                T-Shirts Two chấp nhận đổi trả trong vòng 7 ngày kể từ ngày nhận hàng, với điều kiện sản phẩm còn nguyên tem nhãn, 
                chưa qua sử dụng và còn đầy đủ bao bì. Vui lòng liên hệ với bộ phận Hỗ trợ khách hàng để được hướng dẫn cụ thể.
              </p>
            </a-collapse-panel>
            
            <a-collapse-panel key="3" header="Thời gian giao hàng là bao lâu?">
              <p class="text-gray-600">
                Thời gian giao hàng phụ thuộc vào địa điểm của bạn. Thông thường, đơn hàng sẽ được giao trong vòng 2-3 ngày 
                làm việc đối với các thành phố lớn và 3-5 ngày làm việc đối với các tỉnh thành khác.
              </p>
            </a-collapse-panel>
            
            <a-collapse-panel key="4" header="Làm thế nào để biết size áo phù hợp?">
              <p class="text-gray-600">
                Mỗi sản phẩm đều có bảng thông số kích thước chi tiết. Bạn có thể tham khảo bảng này và đo kích thước cơ thể 
                của mình để chọn size phù hợp nhất. Nếu cần hỗ trợ thêm, vui lòng liên hệ với chúng tôi qua hotline.
              </p>
            </a-collapse-panel>
            
            <!-- <a-collapse-panel key="5" header="T-Shirts Two có cửa hàng thực tế không?">
              <p class="text-gray-600">
                Có, T-Shirts Two có cửa hàng thực tế tại Tòa nhà L  Innovation, 123 Nguyễn Văn Linh, Quận 7, TP. Hồ Chí Minh. 
                Bạn có thể ghé thăm cửa hàng trong giờ làm việc để trải nghiệm trực tiếp các sản phẩm của chúng tôi.
              </p>
            </a-collapse-panel> -->
          </a-collapse>
        </div>
      </div>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { ref, reactive } from 'vue';
  import { message } from 'ant-design-vue';
  
  // Form state
  const form = reactive({
    fullName: '',
    email: '',
    phone: '',
    subject: undefined,
    message: '',
    subscribe: false
  });
  
  const loading = ref(false);
  
  // Form submission handler
  const submitForm = () => {
    // Validation logic would go here
    loading.value = true;
    
    // Simulate API call
    setTimeout(() => {
      loading.value = false;
      message.success('Tin nhắn của bạn đã được gửi thành công!');
      
      // Reset form
      form.fullName = '';
      form.email = '';
      form.phone = '';
      form.subject = undefined;
      form.message = '';
      form.subscribe = false;
    }, 1500);
  };
  </script>
  
  <script lang="ts">
  export default {
    name: "ContactPage",
  };
  </script>
  
  <style scoped>
  /* Nếu cần thêm CSS đặc biệt ngoài Tailwind */
  </style>