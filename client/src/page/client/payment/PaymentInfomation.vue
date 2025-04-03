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
  
  <!-- QR Code Modal -->
  <a-modal
  v-model:visible="qrCodeModalVisible"
  title="Thanh toán qua VietQR"
  :footer="null"
  width="700px"
  :maskClosable="false"
  @cancel="handleCancelQRModal"
>
  <div class="text-center">
    <div v-if="qrCodeLoading" class="py-10">
      <a-spin tip="Đang tạo mã QR...">
        <div class="content" />
      </a-spin>
    </div>
    <div v-else-if="qrCodeData" class="flex flex-col md:flex-row py-4">
      <!-- QR Code on the left -->
      <div class="md:w-1/2 flex justify-center items-center">
        <img :src="qrCodeData.qrDataURL" class="mx-auto" alt="QR Code" />
      </div>
      
      <!-- Payment info on the right -->
      <div class="md:w-1/2 text-left px-4 md:pl-8 flex flex-col justify-center">
        <!-- <div class="text-lg font-bold mb-4"> Số tiền: {{ formatCurrencyVND(paymentInfo.total) }}</div> -->
        <p>Vui lòng quét mã QR để thanh toán</p>
        <div class="mt-4">
          <p class="font-medium mb-2">Thông tin chuyển khoản:</p>
          <table class="w-full border-collapse">
            <tr>
              <td class="py-1 pr-2 font-medium">Số tài khoản:</td>
              <td>4252420691 </td>
            </tr>
            <tr>
              <td class="py-1 pr-2 font-medium">Chủ tài khoản:</td>
              <td>HOANG VAN THANH</td>
            </tr>
            <tr>
              <td class="py-1 pr-2 font-medium">Ngân hàng:</td>
              <td>BIDV</td>
            </tr>
            <tr>
              <td class="py-1 pr-2 font-medium">Nội dung CK:</td>
              <td>{{ qrPaymentReference }}</td>
            </tr>
            <tr>
              <td class="py-1 pr-2 font-medium">Số tiền:</td>
              <td class="text-lg font-bold">{{ formatCurrencyVND(paymentInfo.total) }}</td>
            </tr>
          </table>
        </div>
      </div>
    </div>
    <div v-else class="py-10 text-red-500">
      Không thể tạo mã QR. Vui lòng thử lại hoặc chọn phương thức thanh toán khác.
    </div>
    
    <!-- Buttons at the bottom -->
    <div v-if="qrCodeData" class="flex justify-center space-x-4 mt-6">
      <a-button 
        type="primary"
        :style="{
          backgroundColor: '#b91c1c',
          borderColor: '#b91c1c',
          color: 'white',
        }"
        @click="handleConfirmPaymentWithVietQR"
      >
        Xác nhận đã thanh toán
      </a-button>
      <a-button @click="handleCancelQRModal">Huỷ</a-button>
    </div>
  </div>
</a-modal>
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
import { Form, message, Modal, Upload, Spin } from "ant-design-vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import {
  warningNotiSort,
  successNotiSort,
  errorNotiSort,
} from "@/utils/notification.config";
import { keepPreviousData } from "@tanstack/vue-query";
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
import { useCreateInvoiceOnline, useCreateInvoiceOnlineWithMomo, useCreateInvoiceOnlineWithVietQR, useCreateInvoiceOnlineWithVnPay, useGetVietQrCode } from "@/infrastructure/services/service/client/clientPayment.action";

import VoucherPaymentModal from "@/page/client/payment/voucher/VoucherPaymentModal.vue";
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
  vietQrRequest,
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
import { useCartStore } from "@/infrastructure/stores/cart";

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

const paymentMethods = [
  {
    value: "cod",
    label: "Thanh toán khi nhận hàng (COD)",
    description: "Thanh toán bằng tiền mặt khi nhận hàng tại nhà",
    icon: "ri-money-dollar-box-fill",
  },
  {
    value: "vnpay",
    label: "Thanh toán qua VN Pay",
    description: "Thanh toán nhanh chóng và an toàn qua cổng VN Pay",
    icon: "ri-bank-card-fill",
  },
  {
    value: "momo",
    label: "Thanh toán qua Momo",
    description: "Thanh toán dễ dàng qua ví điện tử Momo",
    icon: "ri-wallet-3-fill",
  },
  {
    value: "vietQR",
    label: "Thanh toán qua VietQR",
    description: "Chuyển khoản nhanh chóng bằng mã QR",
    icon: "ri-qr-code-fill",
  },
];

const pageSize = ref(5);
const current1 = ref(1);

const selectedAddress = ref({});

const paymentedValue = ref(0);

const voucher = ref<VoucherResponse>();

const customer = ref(null);

// QR Code specific states
const qrCodeModalVisible = ref(false);
const qrCodeLoading = ref(false);
const qrCodeData = ref("");
const qrPaymentReference = ref("");

interface DataType extends POSProductDetailResponse {
  key: string;
  thanhTien: number;
}

const paramsVoucher = ref<FindVoucherRequest>({
  page: 1,
  size: 5,
  keyword: "",
  idKhachHang: null,
  tongTien: 0,
});

const voucherRequestParams = ref<voucherRequest>({
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
  maGiaoDich: "" || null,
});

const { data: dataVouchers } = useGetListVoucher(paramsVoucher, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataListVoucher = computed(() => dataVouchers?.value?.data?.data || []);

const { data: dataVoucherByCode, refetch } = useGetVoucherByCode(
  voucherRequestParams,
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

const open = ref(false);
const router = useRouter();
const activeTabCustomers = reactive({});

const openVoucherModal = () => {
  open.value = true;
};

const handleClose = () => {
  open.value = false;
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
const { mutate: createInvoiceWithVietQr } = useCreateInvoiceOnlineWithVietQR();
const createInvoiceMutation = useCreateInvoiceOnlineWithVnPay();
const getVietQrcode = useGetVietQrCode();
const createInvoiceMutationMomo = useCreateInvoiceOnlineWithMomo();

// Function to generate a unique reference for the payment
const generatePaymentReference = () => {
  const userId = useAuthStore().user?.id || "guest";
  const timestamp = new Date().getTime();
  return `ORDER-${userId}-${timestamp}`;
};

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

// Create payload for order creation
const createOrderPayload = () => {
  const listSanPhamChiTiets: invoiceDetailRequest[] = listProducts.value.map(
    (product) => ({
      idSanPhamChiTiet: product.id || null,
      soLuong: product.soLuong || null,
      gia: product.gia || null,
    })
  );

  return {
    diaChiNguoiNhan: paymentInfo.value.fullAddress || null,
    ghiChu: props.memo || null,
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
    email: props.dataAddress.email,
    maGiaoDich : ""
  };
};

// Handle showing the QR code modal
const showQRCodeModal = async () => {
  qrCodeModalVisible.value = true;
  qrCodeLoading.value = true;
  qrPaymentReference.value = generatePaymentReference();
  
  try {
    const vietQRRequest = {
      accountNo: 4252420691,
      accountName: "HOANG VAN THANH",
      acqId: 970418,
      addInfo: qrPaymentReference.value,
      format: "text",
      template: "compact2",
      amount: paymentInfo.value.total
    };
    
    const response = await getVietQrcode.mutateAsync(vietQRRequest);
    if (response && response.data) {
      qrCodeData.value = response.data;
    } else {
      throw new Error("Không nhận được dữ liệu QR");
    }
  } catch (error) {
    console.error("Error generating QR code:", error);
    errorNotiSort("Không thể tạo mã QR. Vui lòng thử lại.");
  } finally {
    qrCodeLoading.value = false;
  }
};

// Handle confirming payment and creating order
const handleConfirmPaymentWithVietQR = async () => {
  try {
    const payload = createOrderPayload();
    payload.maGiaoDich = qrPaymentReference.value
    await createInvoiceWithVietQr(payload);
    clearCart();
    successNotiSort("Hoàn thành đơn hàng!");
    qrCodeModalVisible.value = false;
    router.push({ name: "client-complete-payment" });
  } catch (error: any) {
    console.error("Error creating order:", error);
    if (error?.response) {
      errorNotiSort(error?.response?.data?.message);
    } else {
      errorNotiSort("Đã có lỗi xảy ra khi tạo đơn hàng");
    }
  }
};

// Handle cancelling the QR modal
const handleCancelQRModal = () => {
  Modal.confirm({
    title: "Huỷ thanh toán",
    content: "Bạn có chắc muốn huỷ thanh toán qua VietQR không?",
    okText: "Đồng ý",
    cancelText: "Không",
    onOk() {
      qrCodeModalVisible.value = false;
      qrCodeData.value = "";
    },
  });
};

const handlePayment = () => {
  if (!props.validateAddress) {
    warningNotiSort("Vui lòng nhập đủ thông tin giao hàng.");
    return;
  }
  
  const payload = createOrderPayload();
  
  if (paymentInfo.value.method === "vietQR") {
    showQRCodeModal();
  } else if (paymentInfo.value.method === "cod") {
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
          console.error("Error creating order:", error);
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
        } catch (error: any) {
          console.error("Error with VNPay:", error);
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
  } else if (paymentInfo.value.method === "momo") {
    Modal.confirm({
      content: "Bạn chắc chắn muốn thanh toán qua MoMo?",
      icon: createVNode(ExclamationCircleOutlined),
      centered: true,
      async onOk() {
        try {
          const response = await createInvoiceMutationMomo.mutateAsync(payload);
          if (response?.data?.payUrl) {
            window.open(response?.data?.payUrl, "_blank");
          }
        } catch (error: any) {
          console.error("Error with MoMo:", error);
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

.payment-method-item.active {
  border-color: #b91c1c;
  background-color: #fee2e2;
}
</style>  