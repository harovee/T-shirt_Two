<template>
  <div
    :class="
      paymentInfo.shippingOption === 'true'
        ? 'mt-5 grid grid-cols-1 lg:grid-cols-2 gap-6'
        : 'mt-5'
    "
  >
    <div class="bg-white" v-if="paymentInfo.shippingOption === 'true'">
      <pay-ment-address />
    </div>

    <!-- Thông tin thanh toán -->
    <div class="bg-white">
      <h3 class="text-xl font-semibold mb-4">Thông tin thanh toán</h3>
      <a-form layout="vertical">
        <div class="flex gap-4">
          <a-form-item label="Hình thức mua hàng">
            <a-radio-group
              v-model:value="paymentInfo.shippingOption"
              option-type="button"
              button-style="solid"
            >
              <a-radio value="false">Tại cửa hàng</a-radio>
              <a-radio value="true">Giao hàng</a-radio>
            </a-radio-group>
          </a-form-item>
        </div>
        <!-- Mã phiếu giảm giá -->
        <a-form-item label="Mã giảm giá" class="text-xl font-bold">
          <div class="flex items-center space-x-2">
            <a-input
              v-model:value="paymentInfo.voucherCode"
              placeholder="Nhập mã giảm giá"
            />
            <a-tooltip title="Chọn phiếu giảm giá" trigger="hover">
            <a-button
              class="bg-purple-300 flex justify-between items-center gap-2"
              @click="openVoucherModal"
            >
              <v-icon name="ri-coupon-2-line" />
            </a-button>
          </a-tooltip>
          </div>
          <voucher-payment-table
                  :open="open"
                  @handleClose="handleClose"
                  @cancel="open = false"
                  class="w-[600px] h-[400px]"
                  @handleOpenKhachHang="openVoucherModal"
                  @selectVoucher="handleVoucherSelected"
                />
        </a-form-item>
        <p>Tiền hàng:   <span>{{formatCurrencyVND(paymentInfo.totalProductPrice)}}</span></p>
        <p class="text-red-500">Giảm giá:   <span>{{formatCurrencyVND(paymentInfo.discount)}}</span></p>
        <!-- <a-form-item label="Tiền hàng">
          <a-input-number
            v-model:value="paymentInfo.discount"
            placeholder="Nhập số tiền giảm giá"
            class="w-full"
            @change="updateTotal"
          />
          <p>{{paymentInfo.totalProductPrice}}</p>
        </a-form-item>
        Giảm giá
        <a-form-item label="Giảm giá">
          <a-input-number
            v-model:value="paymentInfo.discount"
            placeholder="Nhập số tiền giảm giá"
            class="w-full"
            @change="updateTotal"
          />
          
        </a-form-item> -->
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

        <a-form-item
          v-if="paymentInfo.method === 'cash'"
          label="Nhập tiền khách đưa"
        >
          <a-input
            v-model:value="paymentInfo.bankAccount"
            placeholder="Nhập số tiền"
          />
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

        <!-- Trả sau & Giao hàng -->
        

        <!-- Phí vận chuyển -->
        <a-form-item label="Phí vận chuyển" v-if="paymentInfo.isShipping">
          <a-input-number
            v-model:value="paymentInfo.shippingFee"
            placeholder="Nhập phí vận chuyển"
            class="w-full"
            @change="updateTotal"
          />
        </a-form-item>

        

        <!-- Tổng tiền -->
        <!-- <a-form-item label="Tổng tiền">
          <a-input
            v-model:value="paymentInfo.total"
            disabled
            class="font-semibold text-lg"
          />
        </a-form-item> -->
        <p class="text-xxl font-bold">Tổng tiền:   <span>{{formatCurrencyVND(paymentInfo.total)}}</span></p>
        <a-button type="primary" class="w-full">Hoàn thành đơn hàng</a-button>
      </a-form>
    </div>
  </div>
</template>
  
<script setup>
import { ref, watch,reactive, defineProps  } from "vue";
import PayMentAddress from "./PayMentAddress.vue";
import {
  useGetListVoucher,
  useGetVoucherById,
} from "@/infrastructure/services/service/admin/payment.action";
import VoucherPaymentTable from "./voucher/VoucherPaymentTable.vue";
import { formatCurrencyVND, getDateFormat, getDateTimeMinutesFormat } from "@/utils/common.helper";

// import { BillWaitResponse } from "@/infrastructure/services/api/admin/bill.api";

const props = defineProps({
  dataSourceInfor: {
    type: Object,
    required: true,
  },
});

const open = ref(false);

const activeTabCustomers = reactive({});

const openVoucherModal = () => {
  open.value = true;
};

const handleClose = () => {
  open.value = false;
};

const paymentInfo = ref({
  method: null,
  bankAccount: "",
  voucherCode: "",
  shippingOption: "false",
  shippingFee: 0,
  discount: 0,
  total: 0,
  totalProductPrice: 0
});

const updateTotal = () => {
  paymentInfo.value.total =
    (paymentInfo.value.shippingFee || 0) - (paymentInfo.value.discount || 0);
};

const handleVoucherSelected = (voucher) => {
  paymentInfo.value.voucherCode = voucher.ma
  console.log(paymentInfo.value.voucherCode);
}

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
  