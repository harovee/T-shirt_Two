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
        <p class="text-green-600 mb-4">Đơn hàng của bạn đã được thanh toán và xác nhận thành công.</p>
        <div class="bg-gray-100 p-4 rounded-lg mb-4 text-left">
          <p class="text-gray-700"><span class="font-semibold">Mã đơn hàng:</span> {{ orderInfo.orderId }}</p>
          <p class="text-gray-700"><span class="font-semibold">Số tiền:</span> {{ formatCurrency(orderInfo.amount) }} VNĐ</p>
          <p class="text-gray-700"><span class="font-semibold">Mã giao dịch:</span> {{ orderInfo.transId }}</p>
        </div>
        <p v-if="showCountdown" class="text-gray-600 text-sm">
          Tab này sẽ tự động đóng sau <span class="font-bold">{{ countdown }}</span> giây...
        </p>
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
import { message as antMessage } from 'ant-design-vue';
import { clearCart } from '@/page/client/products/business.logic/CartLocalStorageBL';
import { useCreateUrlMomoCallBack } from '@/infrastructure/services/service/client/clientPayment.action';

const route = useRoute();
const router = useRouter();

const loading       = ref(true);
const status        = ref<'success' | 'fail'>('fail');
const errorMessage  = ref('');
const countdown     = ref(20);
const showCountdown = ref(false);

const orderInfo = ref({
  orderId: '',
  amount: 0,
  transId: '',
});

 const createUrlMomoCallBack = useCreateUrlMomoCallBack();
let countdownInterval: number;

onMounted(async () => {
  loading.value = true;
  try {
    const qp = route.query;
    if (qp.resultCode !== '0') {
      throw new Error(qp.message as string || 'Thanh toán thất bại từ MoMo');
    }

    const pending = JSON.parse(localStorage.getItem('pendingVnPayOrder') || 'null');
    if (!pending) {
      throw new Error('Không tìm thấy thông tin đơn hàng đã đặt trước');
    }

    const payload = {
      diaChiNguoiNhan: pending.fullAddress || null,
      ghiChu: pending.ghiChu || null,
      soDienThoai: pending.soDienThoai || null,
      tenNguoiNhan: pending.tenNguoiNhan || null,
      tienGiam: pending.tienGiam || null,
      tienShip: pending.tienShip || null,
      tongTien: pending.tongTien || null,
      idKhachHang: pending.idKhachHang || null,
      idPhieuGiamGia: pending.idPhieuGiamGia || null,
      paymentMethod: pending.paymentMethod,
      tinh: pending.tinh,
      huyen: pending.huyen,
      xa: pending.xa,
      email: pending.email,
      listSanPhamChiTiets: pending.listSanPhamChiTiets,
      maGiaoDich: String(qp.transId),
      bankCode: '',
      amount: null,
      idNhanVien: null,
    };

    // Gọi API và chờ backend xác nhận
    const apiRes = await createUrlMomoCallBack.mutateAsync(payload);
    if (apiRes.status === 'OK') {
      status.value = 'success';
      antMessage.success('Đơn hàng đã được xác nhận thành công!');

      clearCart();
      localStorage.removeItem('pendingVnPayOrder');

      orderInfo.value = {
        orderId: pending.orderId || '',
        amount: pending.tongTien || 0,
        transId: String(qp.transId),
      };

      showCountdown.value = true;
      startCountdown(() => {
        if (window.opener) {
          window.opener.location.href = '/complete-payment';
          window.close();
        } else {
          router.push({ name: "client-complete-payment" });
        }
      });
    } else {
      throw new Error(apiRes.message || 'Lỗi xác nhận đơn hàng');
    }
  } catch (err: any) {
    status.value = 'fail';
    errorMessage.value = err.message || 'Có lỗi khi xử lý thanh toán';
    antMessage.error(errorMessage.value);
  } finally {
    loading.value = false;
  }
});

function startCountdown(cb: () => void) {
  countdownInterval = window.setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(countdownInterval);
      cb();
    }
  }, 1000);
}

onBeforeUnmount(() => {
  clearInterval(countdownInterval);
});

const handleManualRedirect = () => {
  if (countdownInterval) clearInterval(countdownInterval);
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

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('vi-VN').format(amount);
};
</script>
