<template>
  <div class="flex flex-col items-center mt-8 p-8 bg-gray-50 rounded-lg shadow max-w-lg mx-auto">
    <div v-if="loading" class="text-center mb-4">
      <div class="inline-block animate-spin rounded-full h-12 w-12 border-4 border-red-600 border-t-transparent"></div>
      <p class="mt-4 text-xl">Đang xử lý kết quả thanh toán...</p>
    </div>
    
    <div v-else class="text-center w-full">
      <div v-if="status === 'success'" class="mb-6">
        <div class="mb-4 text-green-600">
          <svg xmlns="http://www.w3.org/2000/svg" class="mx-auto h-16 w-16" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <h1 class="text-2xl font-bold mb-4">Thanh toán thành công!</h1>
        <p class="text-green-600 mb-4">Đơn hàng của bạn đã được thanh toán thành công và đang được xử lý.</p>
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
          backgroundColor: '#b91c1c',
          borderColor: '#b91c1c',
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
let countdownInterval;

onMounted(async () => {
  loading.value = true;
  
  try {
    const queryParams = route.query;
    console.log('VNPay return params:', queryParams);
    
    if (!queryParams || !queryParams.vnp_ResponseCode) {
      throw new Error('Không nhận được thông tin thanh toán từ VNPay');
    }
    
    const response = await axios.get('http://localhost:6868/api/v1/client/payment/vn-pay-callback', {
      params: queryParams
    });
    
    console.log('Backend response:', response.data);
    
    if (response.data && response.data.status === 'OK') {
      if (response.data.message && response.data.message.includes('thành công')) {
        status.value = 'success';
        antMessage.success('Thanh toán thành công!');
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
        errorMessage.value = response.data.message || 'Thanh toán thất bại';
        antMessage.error(errorMessage.value);
        showCountdown.value = true;
        startCountdown(() => {
          window.close();
        });
      }
    } else {
      throw new Error('Backend response format unexpected');
    }
  } catch (error) {
    console.error('Error in payment return processing:', error);
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
