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
            />
            <a-button
              class="flex justify-between items-center"
              :style="{
                backgroundColor: '#b91c1c',
                borderColor: '#b91c1c',
                color: 'white',
              }"
              @click="getVoucher"
            >
              ÁP DỤNG
            </a-button>
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
        <div class="flex justify-between items-center w-[300px]">
          <p>Mã giảm giá:</p>
          <p class="text-red-500 font-bold">
            -{{ formatCurrencyVND(paymentInfo.discount) }}
          </p>
        </div>
        <hr class="border-t border-gray-400 border-dashed mb-5" />
        <a-form-item label="PHƯƠNG THỨC THANH TOÁN" class="text-l mt-8">
          <a-radio-group v-model:value="paymentInfo.method">
            <a-radio value="cod">Thanh toán khi nhận hàng (COD)</a-radio>
            <a-radio value="vnpay">Thanh toán qua VN Pay</a-radio>
          </a-radio-group>
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
import PayMentAddress from "./PayMentAddress.vue";
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
import { useCreateInvoiceOnline, useCreateInvoiceOnlineWithVnPay } from "@/infrastructure/services/service/client/clientPayment.action";

import VoucherPaymentTable from "@/page/admin/point.of.sale/voucher/VoucherPaymentTable.vue";
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
    required: true,
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

const pageSize = ref(5);
const current1 = ref(1);

const selectedAddress = ref({});

const paymentedValue = ref(0);

const voucher = ref<VoucherResponse>();

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

const getVoucher = () => {
  if (paymentInfo.value.voucherCode === "") {
    warningNotiSort("Vui lòng nhập mã phiếu giảm giá!");
    voucher.value = null;
    return;
  }
  voucherRequest.value.keyword = paymentInfo.value.voucherCode;

  refetch()
    .then(() => {
      voucher.value = dataVoucherByCode?.value?.data;
      console.log(voucher.value);

      if (!voucher.value) {
        paymentInfo.value.voucherCode = null;
        paymentInfo.value.voucherId = null;
        warningNotiSort("Không tồn tại phiếu giảm giá này!");
      } else {
        paymentInfo.value.voucherCode = voucher.value.ma;
        paymentInfo.value.voucherId = voucher.value.id;
        if (voucher.value.loaiGiam) {
          paymentInfo.value.discount = Number(voucher.value.giaTri);
        } else {
          paymentInfo.value.discount =
            (paymentInfo.value.totalProductPrice *
              Number(voucher.value.giaTri)) /
            100;
        }
      }
    })
    .catch((error) => {
      console.error("Error fetching voucher:", error);
    });
};

// watch(
//   () => paymentInfo.value,
//   (newData) => {
//     if (dataListVoucher.value) {
//       console.log("ok");

//       voucher.value =
//         dataListVoucher.value.find(
//           (voucherSelected) =>
//             voucherSelected.id === paymentInfo.value.voucherId
//         ) || null;
//       console.log(voucher.value);
//     }
//   },
//   { deep: true }
// );

// const changeShippingOption = (option: string) => {
//   paymentInfo.value.shippingOption = option;
//   emit("handlePaymentInfo", paymentInfo.value);
// };

// const { mutate: updateBillWait } = useUpdateBillWait();

// const handleUpdateBill = () => {
//   const pdfParams = {
//     idKhachHang: props.selectedCustomerInfo
//       ? props.selectedCustomerInfo.id
//       : null,

//     idNhanVien: null,

//     idHoaDon: props.dataSourceInfor.id,

//     products: dataSourcePro.value,

//     tongTien: paymentInfo.value.totalProductPrice,

//     phiVanChuyen: paymentInfo.value.shippingFee,

//     giamGia: paymentInfo.value.discount,
//   };
//   const payload = {
//     trangThai:
//       paymentInfo.value.shippingOption === "true"
//         ? "Chờ giao hàng"
//         : "Thành công",
//     idKhachHang: props.selectedCustomerInfo
//       ? props.selectedCustomerInfo.id
//       : null,
//     idPhieuGiamGia: paymentInfo.value.voucherId || null,
//     idNhanVien: null,
//     diaChiNguoiNhan:
//       paymentInfo.value.shippingOption === "true"
//         ? paymentInfo.value.fullAddress
//         : null,
//     tenNguoiNhan:
//       paymentInfo.value.shippingOption === "true"
//         ? paymentInfo.value.name
//         : null,
//     soDienThoai:
//       paymentInfo.value.shippingOption === "true"
//         ? paymentInfo.value.phoneNumber
//         : null,
//     ngayShip: null,
//     ghiChu: null,
//     tienGiam: paymentInfo.value.discount || null,
//     tienShip:
//       paymentInfo.value.shippingOption === "true"
//         ? paymentInfo.value.shippingFee
//         : null,
//     tongTien: paymentInfo.value.totalProductPrice || null,
//   };
//   if (
//     paymentInfo.value.shippingOption === "true" &&
//     (!paymentInfo.value.name ||
//       !paymentInfo.value.phoneNumber ||
//       !paymentInfo.value.fullAddress)
//   ) {
//     warningNotiSort("Vui lòng điền thông tin người nhận!");
//     return;
//   }
//   if (paymentedValue.value === 0) {
//     warningNotiSort(
//       "Vui lòng chọn phương thức thanh toán và tiến hành thanh toán!"
//     );
//     return;
//   }
//   Modal.confirm({
//     content: "Bạn chắc chắn muốn hoàn thành thanh toán?",
//     icon: createVNode(ExclamationCircleOutlined),
//     centered: true,

//     async onOk() {
//       try {
//         await updateBillWait({
//           idBill: props.dataSourceInfor.id,
//           params: payload,
//         });
//         await createInvoicePdf(pdfParams);
//         successNotiSort("Thanh toán thành công!");
//         router.push(
//           ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.path
//         );
//       } catch (error: any) {
//         console.error("🚀 ~ handleCreate ~ error:", error);
//         if (error?.response) {
//           errorNotiSort(error?.response?.data?.message);
//         }
//       }
//     },
//     cancelText: "Huỷ",
//     onCancel() {
//       Modal.destroyAll();
//     },
//   });
// };

// const getCustomerAddress = ref(null);

// const handleGetCustomerAddress = async (modelRef: any, fullAddress: string) => {
//   getCustomerAddress.value = modelRef;
//   paymentInfo.value.name = modelRef.name;
//   paymentInfo.value.phoneNumber = modelRef.phoneNumber;
//   const wardInfo = ref(null);
//   const districtInfo = ref(null);
//   const provinceInfo = ref(null);

//   if (modelRef.ward || modelRef.district || modelRef.province) {
//     try {
//       const response = await getWardByCode(modelRef.ward);
//       wardInfo.value = response.data.data;

//       const responseDis = await getDistrictById(modelRef.district);
//       districtInfo.value = responseDis.data.data;

//       const responsePro = await getProvinceById(modelRef.province);
//       provinceInfo.value = responsePro.data.data;
//       paymentInfo.value.fullAddress =
//         modelRef.line +
//         ", " +
//         wardInfo.value +
//         ", " +
//         districtInfo.value +
//         ", " +
//         provinceInfo.value;
//     } catch (error) {
//       console.error("Lỗi khi lấy thông tin Xã, huyện, tỉnh:", error);
//     }
//   }

//   serviceIdParams.value.formDistrict = shippingParams.value.fromDistrictId;
//   serviceIdParams.value.toDistrict = Number(modelRef.district);

//   if (serviceIdParams.value.toDistrict !== 0) {
//     refetchService().then(() => {
//       shippingParams.value.serviceId = service?.value?.data[0].service_id;

//       shippingParams.value.toDistrictId = modelRef.district;
//       shippingParams.value.toWardCode = modelRef.ward;

//       if (shippingParams.value.toWardCode) {
//         refetchShipping().then(() => {
//           paymentInfo.value.shippingFee = shipping?.value?.data.total;
//         });
//       }
//     });
//   } else {
//     paymentInfo.value.shippingFee = 0;
//   }
//   console.log(paymentInfo.value);
// };

// watch(totalAmount, (newTotal) => {
//   if (newTotal !== 0) {
//     paymentInfo.value.totalProductPrice = newTotal;
//   }
// });

// watch(
//   () => paymentInfo.value.shippingFee,
//   (newTotal) => {
//     if (newTotal && newTotal !== 0) {
//       paymentInfo.value.totalProductPrice =
//         totalAmount.value + newTotal - paymentInfo.value.discount;
//     } else {
//       paymentInfo.value.totalProductPrice = 0;
//     }
//   }
// );

const { mutate: createInvoice } = useCreateInvoiceOnline();
const { mutate: createInvoiceWithVnPay } = useCreateInvoiceOnlineWithVnPay();

const handlePayment = () => {
  if (!props.validateAddress) {
    warningNotiSort("Vui lòng nhập đủ thông tin giao hàng.");
  } else {
    const listSanPhamChiTiets: invoiceDetailRequest[] = listProducts.value.map(
      (product) => ({
        idSanPhamChiTiet: product.id || null,
        soLuong: product.soLuong || null,
        gia: product.tongTien || null,
      })
    );

    const createInvoiceMutation = useCreateInvoiceOnlineWithVnPay();

    const payload = {
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
      amount: paymentInfo.value.total || null,
      bankCode: "VCB"
    };
    console.log(payload);
    if (paymentInfo.value.method === "cod") {
      Modal.confirm({
        content: "Bạn chắc chắn muốn hoàn thành đơn hàng?",
        icon: createVNode(ExclamationCircleOutlined),
        centered: true,

        async onOk() {
          try {
            await createInvoice(payload);
            successNotiSort("Hoàn thành đơn hàng!");
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
    } else {
      Modal.confirm({
        content: "Bạn chắc chắn muốn hoàn thành đơn hàng?",
        icon: createVNode(ExclamationCircleOutlined),
        centered: true,

        async onOk() {
          try {
            const response = await createInvoiceMutation.mutateAsync(payload);
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