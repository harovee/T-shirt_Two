<template>
  <div class="bg-white rounded-md shadow-md p-6">
    <pay-ment-address v-if="paymentInfo.shippingOption === 'true'"/>
  </div>

  <!-- Thông tin thanh toán -->
  <div class="p-6 bg-white rounded-xl shadow-md">
    <h3 class="text-lg font-semibold mb-4">💳 Thông tin thanh toán</h3>
    <a-form layout="vertical">
      <!-- Phương thức thanh toán -->
      <a-form-item label="Phương thức thanh toán">
        <a-select
          v-model:value="paymentInfo.method"
          placeholder="Chọn phương thức thanh toán"
        >
          <a-select-option value="cash">Tiền mặt</a-select-option>
          <a-select-option value="bank">Chuyển khoản</a-select-option>
        </a-select>
      </a-form-item>

      <!-- Số tài khoản (chỉ hiển thị khi chọn Chuyển khoản) -->
      <a-form-item
        v-if="paymentInfo.method === 'bank'"
        label="Số tài khoản ngân hàng"
      >
        <a-input
          v-model:value="paymentInfo.bankAccount"
          placeholder="Nhập số tài khoản"
        />
      </a-form-item>

      <!-- Mã phiếu giảm giá -->
      <a-form-item label="Mã phiếu giảm giá">
        <a-input
          v-model:value="paymentInfo.voucherCode"
          placeholder="Nhập mã giảm giá"
        />
      </a-form-item>

      <!-- Trả sau & Giao hàng -->
      <div class="flex gap-4">
        <a-form-item label="Giao hàng">
          <a-radio-group v-model:value="paymentInfo.shippingOption" option-type="button" button-style="solid">
          <a-radio value="false">Tại cửa hàng</a-radio>
          <a-radio value="true">Giao hàng</a-radio>
        </a-radio-group>
        </a-form-item>
      </div>

      <!-- Phí vận chuyển -->
      <a-form-item label="Phí vận chuyển" v-if="paymentInfo.isShipping">
        <a-input-number
          v-model:value="paymentInfo.shippingFee"
          placeholder="Nhập phí vận chuyển"
          class="w-full"
          @change="updateTotal"
        />
      </a-form-item>

      <!-- Giảm giá -->
      <a-form-item label="Giảm giá">
        <a-input-number
          v-model:value="paymentInfo.discount"
          placeholder="Nhập số tiền giảm giá"
          class="w-full"
          @change="updateTotal"
        />
      </a-form-item>

      <!-- Tổng tiền -->
      <a-form-item label="Tổng tiền">
        <a-input
          v-model:value="paymentInfo.total"
          disabled
          class="font-semibold text-lg"
        />
      </a-form-item>
    </a-form>
  </div>
</template>
  
<script setup>

import { ref, watch } from "vue";
import PayMentAddress from "./PayMentAddress.vue";
import { useGetListVoucher,useGetVoucherById}  from "@/infrastructure/services/service/admin/payment.action"

const paymentInfo = ref({
  method: null,
  bankAccount: "",
  voucherCode: "",
  shippingOption: "false",
  shippingFee: 0,
  discount: 0,
  total: 0,
});

const updateTotal = () => {
  paymentInfo.value.total =
    (paymentInfo.value.shippingFee || 0) - (paymentInfo.value.discount || 0);
};

// const params = ref<FindVoucherRequest>({
//     page: 1,
//     size: 5,
//     keyword: ""
//   });
  
  // const { data: listVoucher } = useGetListVoucher(params, {
  //   refetchOnWindowFocus: false,
  //   placeholderData:keepPreviousData
  // });

// Theo dõi thay đổi và cập nhật tổng tiền
watch(
  [() => paymentInfo.value.shippingFee, () => paymentInfo.value.discount],
  updateTotal
);
</script>
  