<template>
    <a-modal
      v-model:visible="visible"
      title="Chọn Ngân Hàng Thanh Toán"
      :width="700"
      :footer="null"
      @cancel="handleCancel"
    >
      <div class="bank-selection-container">
        <h3 class="text-lg font-bold mb-4">Vui lòng chọn ngân hàng thanh toán</h3>
        
        <div class="grid grid-cols-3 gap-4">
          <div 
            v-for="bank in bankList" 
            :key="bank.code"
            class="bank-option border rounded-lg p-3 cursor-pointer hover:border-red-700 transition-all"
            :class="{ 'border-red-700 border-2': selectedBank === bank.code }"
            @click="selectBank(bank.code)"
          >
            <div class="flex flex-col items-center">
              <img :src="bank.logo" :alt="bank.name" class="h-12 mb-2" />
              <span class="text-sm text-center">{{ bank.name }}</span>
            </div>
          </div>
        </div>
  
        <div class="mt-6">
          <div class="flex justify-between items-center mb-4">
            <p class="text-lg">Số tiền thanh toán:</p>
            <p class="text-xl font-bold text-red-700">{{ formatCurrencyVND(totalAmount) }}</p>
          </div>
  
          <a-button
            type="primary"
            class="w-full text-lg"
            :style="{
              backgroundColor: '#b91c1c',
              borderColor: '#b91c1c',
              color: 'white',
              height: '50px',
            }"
            :disabled="!selectedBank"
            @click="handleConfirm"
          >
            TIẾP TỤC THANH TOÁN
          </a-button>
        </div>
      </div>
    </a-modal>
  </template>
  
  <script lang="ts" setup>
  import { ref, defineProps, defineEmits } from 'vue';
  import { formatCurrencyVND } from '@/utils/common.helper';
  
  const props = defineProps({
    visible: {
      type: Boolean,
      default: false
    },
    totalAmount: {
      type: Number,
      required: true
    }
  });
  
  const emit = defineEmits(['update:visible', 'confirm', 'cancel']);
  
  const selectedBank = ref('');
  
  // Danh sách ngân hàng mà VNPay hỗ trợ
  const bankList = ref([
    {
      code: 'VCB',
      name: 'Vietcombank',
      logo: '/api/placeholder/120/60'
    },
    {
      code: 'TCB',
      name: 'Techcombank',
      logo: '/api/placeholder/120/60'
    },
    {
      code: 'VIB',
      name: 'VIB',
      logo: '/api/placeholder/120/60'
    },
    {
      code: 'VPB',
      name: 'VPBank',
      logo: '/api/placeholder/120/60'
    },
    {
      code: 'BIDV',
      name: 'BIDV',
      logo: '/api/placeholder/120/60'
    },
    {
      code: 'AGRIBANK',
      name: 'Agribank',
      logo: '/api/placeholder/120/60'
    },
    {
      code: 'MB',
      name: 'MB Bank',
      logo: '/api/placeholder/120/60'
    },
    {
      code: 'ACB',
      name: 'ACB',
      logo: '/api/placeholder/120/60'
    },
    {
      code: 'SHB',
      name: 'SHB',
      logo: '/api/placeholder/120/60'
    }
  ]);
  
  const selectBank = (bankCode) => {
    selectedBank.value = bankCode;
  };
  
  const handleConfirm = () => {
    if (selectedBank.value) {
      emit('confirm', selectedBank.value);
      emit('update:visible', false);
    }
  };
  
  const handleCancel = () => {
    selectedBank.value = '';
    emit('cancel');
    emit('update:visible', false);
  };
  </script>
  
  <style scoped>
  .bank-option {
    transition: all 0.2s ease;
  }
  .bank-option:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  </style>