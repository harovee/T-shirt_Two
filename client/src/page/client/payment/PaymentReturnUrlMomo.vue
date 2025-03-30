<template>
    <div class="flex flex-col items-center mt-8 p-8 bg-gray-50 rounded-lg shadow max-w-lg mx-auto">
      <div v-if="loading" class="text-center mb-4">
        <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-red-600 border-t-transparent"></div>
        <p class="mt-4 text-xl">Đang xử lý kết quả thanh toán MoMo...</p>
      </div>
      
      <div v-else class="text-center w-full">
        <div v-if="status === 'success'" class="mb-6">
          <div class="mb-4 text-green-600">
            <svg xmlns="http://www.w3.org/2000/svg" class="mx-auto h-16 w-16" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h1 class="text-2xl font-bold mb-4">Thanh toán thành công!</h1>
          <p class="text-green-600 mb-4">Đơn hàng của bạn đã được thanh toán thành công qua MoMo và đang được xử lý.</p>
          <div class="bg-gray-100 p-4 rounded-lg mb-4 text-left">
            <p class="text-gray-700"><span class="font-semibold">Mã đơn hàng:</span> {{ orderInfo.orderId }}</p>
            <p class="text-gray-700"><span class="font-semibold">Số tiền:</span> {{ formatCurrency(orderInfo.amount) }} VNĐ</p>
            <p class="text-gray-700"><span class="font-semibold">Mã giao dịch:</span> {{ orderInfo.transId }}</p>
          </div>
          <p v-if="showCountdown" class="text-gray-600 text-sm">Tab này sẽ tự động đóng sau <span class="font-bold">{{ countdown }}</span> giây...</p>
        </div>
        
        <div v-else-if="status === 'fail'" class="mb-6">
          <div class="mb-4 text-red-600">
            <svg xmlns="http://www.w3.org/2000/svg" class="mx-auto h-16 w-16" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h1 class="text-2xl font-bold mb-4">Thanh toán thất bại!</h1>
          <p class="text-red-600 mb-4">{{ errorMessage || 'Thanh toán không thành công. Vui lòng thử lại sau.' }}</p>
        </div>
        
        <a-button 
          type="primary" 
          class="mt-4 w-full" 
          :style="{
            backgroundColor: '#ae2070',
            borderColor: '#ae2070',
            color: 'white',
            height: '40px',
          }"
          @click="handleManualRedirect"
        >
          {{ status === 'success' ? 'Xem đơn hàng' : 'Đóng tab' }}
        </a-button>
      </div>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { ref, onMounted, onBeforeUnmount } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import axios from 'axios';
  import { message as antMessage } from 'ant-design-vue';
  import {
    clearCart
  } from "@/page/client/products/business.logic/CartLocalStorageBL";
  
  const route = useRoute();
  const router = useRouter();
  
  const loading = ref(true);
  const status = ref('');
  const errorMessage = ref('');
  const countdown = ref(5);
  const showCountdown = ref(false);
  const orderInfo = ref({
    orderId: '',
    amount: 0,
    transId: '',
  });
  let countdownInterval;
  
  onMounted(async () => {
    loading.value = true;
    
    try {
      const queryParams = route.query;
      console.log('MoMo return params:', queryParams);
      
      if (!queryParams || !queryParams.resultCode) {
        throw new Error('Không nhận được thông tin thanh toán từ MoMo');
      }
  
      // Lưu thông tin đơn hàng
      orderInfo.value = {
        orderId: queryParams.orderId as string || '',
        amount: parseInt(queryParams.amount as string) || 0,
        transId: queryParams.transId as string || '',
      };
      
      // Gọi API để xác nhận thanh toán
      const response = await axios.get('http://localhost:6868/api/v1/client/payment/momo-callback', {
        params: queryParams
      });
      
      console.log('Backend response:', response.data);
      
      // Kiểm tra kết quả từ MoMo qua resultCode
      // resultCode = 0: Thành công
      // resultCode khác 0: Thất bại
      if (queryParams.resultCode === '0' && response.data && response.data.status === 'OK') {
        status.value = 'success';
        antMessage.success('Thanh toán qua MoMo thành công!');
        clearCart();
        showCountdown.value = true;
        startCountdown(() => {
          if (window.opener) {
            window.opener.location.href = '/my-order';
            window.close();
          } else {
            router.push('/my-order');
          }
        });
      } else {
        status.value = 'fail';
        errorMessage.value = queryParams.message as string || response.data?.message || 'Thanh toán thất bại';
        antMessage.error(errorMessage.value);
        showCountdown.value = true;
        startCountdown(() => {
          window.close();
        });
      }
    } catch (error) {
      console.error('Error in MoMo payment return processing:', error);
      status.value = 'fail';
      errorMessage.value = error.response?.data?.message || 'Có lỗi xảy ra khi xử lý kết quả thanh toán';
      antMessage.error(errorMessage.value);
      showCountdown.value = true;
      startCountdown(() => {
        window.close();
      });
    } finally {
      loading.value = false;
    }
  });
  
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('vi-VN').format(amount);
  };
  
  const startCountdown = (callback) => {
    countdownInterval = setInterval(() => {
      countdown.value -= 1;
      if (countdown.value <= 0) {
        clearInterval(countdownInterval);
        if (status.value === 'fail') {
          window.close();
        } else {
          callback();
        }
      }
    }, 1000);
  };
  
  onBeforeUnmount(() => {
    if (countdownInterval) {
      clearInterval(countdownInterval);
    }
  });
  
  const handleManualRedirect = () => {
    if (countdownInterval) {
      clearInterval(countdownInterval);
    }
    
    if (status.value === 'success') {
      if (window.opener) {
        window.opener.location.href = '/my-order';
        window.close();
      } else {
        router.push('/my-order');
      }
    } else {
      window.close();
    }
  };
  </script>