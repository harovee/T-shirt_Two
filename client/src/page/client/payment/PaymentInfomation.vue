<template>
  <div class="bg-gray-100 ms-3">
    <div class="p-8">
      <h1 class="text-2xl font-bold">ĐƠN HÀNG</h1>
      <a-form layout="vertical">
        <a-form-item label="MÃ PHIẾU GIẢM GIÁ" class="text-l mt-8">
          <div class="flex items-center space-x-2">
            <a-input
              v-model:value="paymentInfo.voucherCode"
              placeholder="Chọn mã giảm giá ..."
              readonly
            />
            <!-- <a-button
              class="flex justify-between items-center"
              :style="{
                backgroundColor: '#b91c1c',
                borderColor: '#b91c1c',
                color: 'white',
              }"
              @click="getVoucher"
            >
              !
            </a-button> -->
            <a-tooltip title="Không sử dụng phiếu" trigger="hover">
              <a-button
                class="flex justify-between items-center gap-2"
                :style="{
                  backgroundColor: '#b91c1c',
                  borderColor: '#b91c1c',
                  color: 'white',
                }"
                @click="getVoucher"
              >
                <v-icon name="md-donotdisturbon-round" />
              </a-button>
            </a-tooltip>
            <a-tooltip trigger="hover">
              <template #title>Chọn phiếu giảm giá</template>
              <a-button
                :style="{
                  backgroundColor: '#b91c1c',
                  borderColor: '#b91c1c',
                  color: 'white',
                }"
                @click="openVoucherModal"
              >
                <v-icon name="ri-coupon-2-line" />
              </a-button>
            </a-tooltip>
          </div>
          <div
            class="text-xs border border-black-500 rounded-lg p-3 mt-3"
            v-if="voucher"
          >
            <div class="flex items-center">
              <span class="me-3"><v-icon name="ri-coupon-2-fill" /></span>
              <span
                >Bạn đang sử dụng: {{ voucher.ten }} - Số lượng:
                {{ voucher.soLuong }} - Giá trị giảm ({{
                  voucher.loaiGiam ? "Tiền mặt" : "%"
                }}):
                {{
                  voucher.loaiGiam
                    ? formatCurrencyVND(voucher.giaTri)
                    : voucher.giaTri + "%"
                }}</span
              >
            </div>
          </div>
        </a-form-item>
        <voucher-payment-modal
          :open="open"
          :dataCustomer="customer"
          :totalAmount="paymentInfo.totalProductPrice"
          @handleClose="handleClose"
          @cancel="open = false"
          @handleOpenKhachHang="openVoucherModal"
          @selectVoucher="handleGetVoucher"
        />
        <hr class="border-t border-gray-400 border-dashed mb-5" />
        <div class="flex justify-between items-center w-[300px]">
          <p>Tạm tính:</p>
          <p class="font-bold">
            {{ formatCurrencyVND(paymentInfo.totalProductPrice) }}
          </p>
        </div>

        <div class="flex justify-between items-center w-[300px]">
          <p class="inline-flex items-center gap-1 mt-1">
            Phí vận chuyển:
            <img
              src="@/assets/image/logo/Logo-GHN.webp"
              width="50"
              height="24"
            />
          </p>
          <p class="font-bold">
            {{ formatCurrencyVND(paymentInfo.shippingFee) }}
          </p>
        </div>
        <p v-if="paymentInfo.totalProductPrice > 2000000" class="text-red-500">
          Free ship cho đơn hàng từ 2.000.000đ
        </p>
        <div class="flex justify-between items-center w-[300px]">
          <p>Mã giảm giá:</p>
          <p class="text-red-500 font-bold">
            -{{ formatCurrencyVND(paymentInfo.discount) }}
          </p>
        </div>
        <hr class="border-t border-gray-400 border-dashed mb-5" />
        <a-form-item label="PHƯƠNG THỨC THANH TOÁN" class="text-l mt-8">
        <div class="payment-methods">
          <div 
            v-for="method in paymentMethods" 
            :key="method.value"
            :class="['payment-method-item', { 'active': paymentInfo.method === method.value }]"
            @click="paymentInfo.method = method.value"
          >
            <div class="flex items-center p-3 border rounded-md hover:border-red-700 cursor-pointer">
              <div class="flex-shrink-0 mr-3 text-xl text-red-700">
                <v-icon :name="method.icon" />
              </div>
              <div class="flex-grow">
                <div class="font-medium">{{ method.label }}</div>
                <div class="text-xs text-gray-500">{{ method.description }}</div>
              </div>
              <div class="flex-shrink-0 ml-3">
                <a-radio :checked="paymentInfo.method === method.value" />
              </div>
            </div>
          </div>
        </div>
      </a-form-item>
        <hr class="border-t border-gray-400 border-dashed mb-5" />
        <div
          class="flex justify-between items-center w-[300px] text-xxl font-bold"
        >
          <p>Tổng thanh toán:</p>
          <p class="text-3xl font-bold">
            {{ formatCurrencyVND(paymentInfo.total) }}
          </p>
        </div>

        <a-button
          type="primary"
          class="w-full text-xl"
          :style="{
            backgroundColor: '#b91c1c',
            borderColor: '#b91c1c',
            color: 'white',
            height: '50px',
          }"
          @click="handlePayment"
          >ĐẶT HÀNG</a-button
        >
      </a-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {
  ref,
  onMounted,
  watch,
  reactive,
  defineProps,
  computed,
  createVNode,
  nextTick,
} from "vue";
import { Form, message, Modal, Upload } from "ant-design-vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import {
  warningNotiSort,
  successNotiSort,
  errorNotiSort,
} from "@/utils/notification.config";
import { keepPreviousData } from "@tanstack/vue-query";
// import PayMentAddress from "./PayMentAddress.vue";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import { useRouter } from "vue-router";
import {
  useGetListVoucher,
  useGetVoucherById,
  useGetPriceNextVoucher,
  useGetShippingFee,
  useGetServiceId,
  useGetVoucherByCode,
} from "@/infrastructure/services/service/admin/payment.action";
import { useCreateInvoiceOnline, useCreateInvoiceOnlineWithMomo, useCreateInvoiceOnlineWithVnPay } from "@/infrastructure/services/service/client/clientPayment.action";

import VoucherPaymentModal from "@/page/client/payment/voucher/VoucherPaymentModal.vue";
import PaymentMethod from "@/page/admin/point.of.sale/payment-method/PaymentMethod.vue";
import { useAuthStore } from "@/infrastructure/stores/auth";
import {
  formatCurrencyVND,
  getDateFormat,
  getDateTimeMinutesFormat,
} from "@/utils/common.helper";
import {
  POSProductDetailResponse,
  POSUpdateCartRequest,
} from "@/infrastructure/services/api/admin/point-of-sale.api";
import {
  clientPaymentRequest,
  invoiceDetailRequest,
} from "@/infrastructure/services/api/client/clientpayment.api";
import {
  useGetOrderDetails,
  useUpdateQuantityOrderDetails,
  useDeleteCartById,
} from "@/infrastructure/services/service/admin/point-of-sale";
import { clearCart } from "@/page/client/products/business.logic/CartLocalStorageBL";
import {
  VoucherResponse,
  FindVoucherRequest,
  nextVoucherRequest,
  ShippingFeeRequest,
  getWardByCode,
  getDistrictById,
  getProvinceById,
  ServiceIdRequest,
  createInvoicePdf,
  voucherRequest,
} from "@/infrastructure/services/api/admin/payment.api";
import {
  ClientAddressCommonOptionsResponse,
  ClientAddressRequest,
} from "@/infrastructure/services/api/admin/client.api";
import {
  useChangeClientAddressDefault,
  useGetDistrictsByProvinceId,
  useGetDistrictsByProvinceIdQuery,
  useGetProvinces,
  useGetWardsByDistrictId,
  useGetWardsByDistrictIdQuery,
  useUpdateClientAddress,
} from "@/infrastructure/services/service/admin/client.action";
import { log } from "console";
import { useCartStore } from "@/infrastructure/stores/cart";

// import { BillWaitResponse } from "@/infrastructure/services/api/admin/bill.api";

const props = defineProps({
  dataAddress: {
    type: Object,
    required: false,
    default: () => ({}),
  },
  totalPrice: Number,
  shippingFee: Number,
  isRefresh: Boolean,
  fullAddressCustomer: String,
  validateAddress: Boolean,
  memo: String,
});

const cartStore = useCartStore();

const listProducts = computed(() => cartStore.checkoutData);

// const emit = defineEmits(["handlePaymentInfo"]);

const paymentMethods = [
  {
    value: "cod",
    label: "Thanh toán khi nhận hàng (COD)",
    description: "Thanh toán bằng tiền mặt khi nhận hàng tại nhà",
    icon: "ri-money-dollar-box-fill", // Thay thế với đường dẫn thực của bạn
  },
  {
    value: "vnpay",
    label: "Thanh toán qua VN Pay",
    description: "Thanh toán nhanh chóng và an toàn qua cổng VN Pay",
    icon: "ri-bank-card-fill", // Thay thế với đường dẫn thực của bạn
  },
  {
    value: "momo",
    label: "Thanh toán qua Momo",
    description: "Thanh toán dễ dàng qua ví điện tử Momo",
    icon: "ri-wallet-3-fill", // Thay thế với đường dẫn thực của bạn
  },
  {
    value: "vietQR",
    label: "Thanh toán qua VietQR",
    description: "Chuyển khoản nhanh chóng bằng mã QR",
    icon: "ri-qr-code-fill", // Thay thế với đường dẫn thực của bạn
  },
];
const pageSize = ref(5);
const current1 = ref(1);

const selectedAddress = ref({});

const paymentedValue = ref(0);

const voucher = ref<VoucherResponse>();

const customer = ref(null);

interface DataType extends POSProductDetailResponse {
  key: string;
  thanhTien: number;
}

const handleAddressUpdate = (newAddress) => {
  selectedAddress.value = newAddress;
};

const paramsVoucher = ref<FindVoucherRequest>({
  page: 1,
  size: 5,
  keyword: "",
  idKhachHang: null,
  tongTien: 0,
});

const voucherRequest = ref<voucherRequest>({
  keyword: "",
  idKhachHang: useAuthStore().user?.id || null,
});

const paramsNextPriceVoucher = ref<nextVoucherRequest>({
  idKhachHang: null,
  tongTien: 0,
});

const paymentInfo = ref({
  method: "cod",
  bankAccount: formatCurrencyVND(""),
  voucherCode: "",
  voucherId: null,
  shippingOption: "false",
  shippingFee: props.shippingFee || 0,
  discount: 0,
  total: 0,
  totalProductPrice: props.totalPrice || 0,
  name: "" || null,
  fullAddress: props.fullAddressCustomer || null,
  phoneNumber: "" || null,
});

const { data: dataVouchers } = useGetListVoucher(paramsVoucher, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataListVoucher = computed(() => dataVouchers?.value?.data?.data || []);

const { data: dataVoucherByCode, refetch } = useGetVoucherByCode(
  voucherRequest,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
  }
);

const { data: dataNextPriceVoucher } = useGetPriceNextVoucher(
  paramsNextPriceVoucher,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
  }
);

const dataNextPriceVouchers = computed(
  () => dataNextPriceVoucher?.value?.data || []
);

// Hàm format tiền sang VNĐ
const formatter = (value: any) => {
  if (!value) return "";
  return `${value} ₫`.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};
const open = ref(false);

const openPaymentMethod = ref(false);

const router = useRouter();

const activeTabCustomers = reactive({});

const openVoucherModal = () => {
  open.value = true;
};

const openPaymentMethodModal = () => {
  openPaymentMethod.value = true;
};

const handleClose = () => {
  open.value = false;
};

const handleClosePaymentMethod = () => {
  openPaymentMethod.value = false;
};

const updateTotal = () => {
  paymentInfo.value.total =
    paymentInfo.value.totalProductPrice +
    (paymentInfo.value.shippingFee || 0) -
    (paymentInfo.value.discount || 0);
};

const handleCheckPaymented = (totalAmountAfter: number) => {
  paymentedValue.value = totalAmountAfter;
};

onMounted(() => {
  customer.value = useAuthStore().user;
  updateTotal();
});

const getVoucher = () => {
  voucher.value = null;
  paymentInfo.value.voucherCode = null;
  paymentInfo.value.voucherId = null;
  paymentInfo.value.discount = 0;
};

const { mutate: createInvoice } = useCreateInvoiceOnline();
// const { mutate: createInvoiceWithVnPay } = useCreateInvoiceOnlineWithVnPay();

const createInvoiceMutation = useCreateInvoiceOnlineWithVnPay();


const handleGetVoucher = (voucherDetail: any) => {
  voucher.value = voucherDetail;
  if (!voucher.value) {
    paymentInfo.value.voucherCode = null;
    paymentInfo.value.voucherId = null;
  } else {
    paymentInfo.value.voucherCode = voucher.value.ma;
    paymentInfo.value.voucherId = voucher.value.id;
    if (voucher.value.loaiGiam) {
      paymentInfo.value.discount = Number(voucher.value.giaTri);
    } else {
      paymentInfo.value.discount =
        (paymentInfo.value.totalProductPrice * Number(voucher.value.giaTri)) /
        100;
    }
  }
};

const createInvoiceMutationMomo = useCreateInvoiceOnlineWithMomo();

const handlePayment = () => {
  if (!props.validateAddress) {
    warningNotiSort("Vui lòng nhập đủ thông tin giao hàng.");
  } else {
    const listSanPhamChiTiets: invoiceDetailRequest[] = listProducts.value.map(
      (product) => ({
        idSanPhamChiTiet: product.id || null,
        soLuong: product.soLuong || null,
        gia: product.gia || null,
      })
    );

    const payload = {
      diaChiNguoiNhan: paymentInfo.value.fullAddress || null,
      ghiChu: props.memo || "",
      soDienThoai: paymentInfo.value.phoneNumber || null,
      tenNguoiNhan: paymentInfo.value.name || null,
      tienGiam: paymentInfo.value.discount || null,
      tienShip: paymentInfo.value.shippingFee || null,
      tongTien: paymentInfo.value.total || null,
      idKhachHang: useAuthStore().user?.id || null,
      idNhanVien: null,
      idPhieuGiamGia: paymentInfo.value.voucherId || null,
      listSanPhamChiTiets: listSanPhamChiTiets || null,
      paymentMethod: paymentInfo.value.method,
      tinh: props.dataAddress.province,
      huyen: props.dataAddress.district,
      xa: props.dataAddress.ward,
      amount: paymentInfo.value.total + "" || null,
      bankCode: "",
      email:props.dataAddress.email
    };
    // console.log(payload);
    if (paymentInfo.value.method === "cod") {
      Modal.confirm({
        content: "Bạn chắc chắn muốn hoàn thành đơn hàng?",
        icon: createVNode(ExclamationCircleOutlined),
        centered: true,

        async onOk() {
          try {
            await createInvoice(payload);
            clearCart();
            successNotiSort("Hoàn thành đơn hàng!");
            router.push({ name: "client-complete-payment" });
          } catch (error: any) {
            console.error("🚀 ~ handleCreate ~ error:", error);
            if (error?.response) {
              errorNotiSort(error?.response?.data?.message);
            }
          }
        },
        cancelText: "Huỷ",
        onCancel() {
          Modal.destroyAll();
        },
      });
    } else if (paymentInfo.value.method === "vnpay") {
      Modal.confirm({
        content: "Bạn chắc chắn muốn thanh toán qua VNPay?",
        icon: createVNode(ExclamationCircleOutlined),
        centered: true,
        async onOk() {
          try {
            const response = await createInvoiceMutation.mutateAsync(payload);

            if (response?.data?.paymentUrl) {
              window.open(response?.data?.paymentUrl, "_blank");
            }
            console.log(response);
          } catch (error: any) {
            console.error("🚀 ~ handleCreate ~ error:", error);
            if (error?.response) {
              errorNotiSort(error?.response?.data?.message);
            }
          }
        },
        cancelText: "Huỷ",
        onCancel() {
          Modal.destroyAll();
        },
      });
    } 
    else{
      Modal.confirm({
        content: "Bạn chắc chắn muốn thanh toán qua MoMo?",
        icon: createVNode(ExclamationCircleOutlined),
        centered: true,
        async onOk() {
          try {
            const response = await createInvoiceMutationMomo.mutateAsync(payload);
            console.log(response);
            
            if (response?.data?.payUrl) {
            window.open(response?.data?.payUrl, "_blank");
          } 
          } catch (error: any) {
            console.error("🚀 ~ handleCreate ~ error:", error); 
            if (error?.response) {
              errorNotiSort(error?.response?.data?.message);
            }
          }
        },
        cancelText: "Huỷ",
        onCancel() {
          Modal.destroyAll();
        },
      });
    }
  }
};

watch(
  [() => props.dataAddress, () => props.shippingFee],
  (newTotal) => {
    if (newTotal) {
      paymentInfo.value.name = props.dataAddress.name;
      paymentInfo.value.phoneNumber = props.dataAddress.phoneNumber;
      paymentInfo.value.shippingFee = props.shippingFee;
    }
  },
  { deep: true }
);

watch(
  () => props.fullAddressCustomer,
  (newVal) => {
    paymentInfo.value.fullAddress = props.fullAddressCustomer;
  }
);

watch(
  [() => paymentInfo.value.shippingFee, () => paymentInfo.value.discount],
  updateTotal
);
</script>

<style scoped>
.custom-radio-group .ant-radio-button-wrapper {
  color: #b91c1c;
  border-color: #b91c1c;
}

.custom-radio-group .ant-radio-button-wrapper:hover {
  background: #b91c1c;
  color: white;
}

.custom-radio-group .ant-radio-button-wrapper-checked {
  background: #b91c1c !important;
  border-color: #b91c1c !important;
  color: white !important;
}

.custom-radio-group .ant-radio-button-wrapper-checked::before {
  background-color: #b91c1c !important;
}
</style>