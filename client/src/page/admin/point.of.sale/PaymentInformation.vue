<template>
  <div
    :class="
      paymentInfo.shippingOption === 'true'
        ? 'mt-5 grid grid-cols-1 lg:grid-cols-2 gap-6'
        : 'mt-5'
    "
  >
    <div class="bg-white me-5" v-if="paymentInfo.shippingOption === 'true'">
      <pay-ment-address
        :selectedCustomer="selectedCustomerInfo"
        @update:selectedCustomerAddress="handleAddressUpdate"
        :isRefresh="isRefresh"
        @handleGetAddress="handleGetCustomerAddress"
      />
    </div>

    <!-- Thông tin thanh toán -->
    <div class="bg-white">
      <h3 class="text-xl font-semibold mb-4">Thông tin thanh toán</h3>
      <a-form layout="vertical">
        <div class="flex gap-4">
          <a-form-item >
            <a-radio-group
              v-model:value="paymentInfo.shippingOption"
              option-type="button"
              button-style="solid"
            >
              <a-radio value="false" @click="changeShippingOption('false')"
                >Tại cửa hàng</a-radio
              >
              <a-radio value="true" @click="changeShippingOption('true')"
                >Giao hàng</a-radio
              >
            </a-radio-group>
          </a-form-item>
        </div>
        <!-- Mã phiếu giảm giá -->
        <a-form-item label="Mã giảm giá" class="text-xl">
          <div class="flex items-center space-x-2">
            <a-input
              v-model:value="paymentInfo.voucherCode"
              placeholder="Chọn mã giảm giá ..."
              readonly
            />
            <a-tooltip title="Chọn phiếu giảm giá" trigger="hover">
              <a-button
                class="bg-purple-300 flex justify-between items-center gap-2"
                @click="openVoucherModal"
              >
                <v-icon name="ri-coupon-2-line" />
              </a-button>
            </a-tooltip>
            <a-tooltip title="Không sử dụng phiếu giảm giá" trigger="hover">
              <a-button
                class="bg-purple-300 flex justify-between items-center gap-2"
                @click="handleNotVoucher"
              >
                <v-icon name="md-donotdisturbon-round" />
              </a-button>
            </a-tooltip>
          </div>
          <!-- dataNextPriceVouchers > 0 && paymentInfo.value && paymentInfo.value.discount > 0 -->
          <div
            v-if="voucher"
            class="text-xs border rounded-lg p-3 mt-3"
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
            <span class="ms-8" v-if="dataNextPriceVouchers > 0"
              >Mua thêm
              {{ formatCurrencyVND(dataNextPriceVouchers - totalAmount) }} để có
              thể sử dụng phiếu giảm giá tốt hơn.</span
            >

            <span class="ms-8" v-if="dataNextPriceVouchers.length === 0"
              >Không còn phiếu nào phù hợp hơn nữa.</span
            >
          </div>
          <div
            v-if="!voucher && dataNextPriceVouchers.length > 0"
            class="mt-3 text-red-500 text-xs border border-red-400 rounded-lg p-3 flex items-center"
          >
            <span class="me-3"><v-icon name="ri-coupon-2-fill" /></span>
            <span
              >Không có phiếu giảm giá nào phù hợp, mua thêm
              {{ formatCurrencyVND(dataNextPriceVouchers - totalAmount) }} để có
              thể sử dụng phiếu giảm giá tốt hơn.</span
            >
          </div>
          <voucher-payment-table
            :open="open"
            :dataCustomer="selectedCustomerInfo"
            :totalAmount="totalAmount"
            :dataVoucher="dataListVoucher"
            @selectVoucher="handleSelectVoucher"
            @handleClose="handleClose"
            @cancel="open = false"
            class="w-[600px] h-[400px]"
            @handleOpenKhachHang="openVoucherModal"
          />
        </a-form-item>
        <div class="flex justify-between items-center w-[300px]">
          <p>Tiền hàng:</p>
          <p class="font-bold">{{ formatCurrencyVND(totalAmount) }}</p>
        </div>
        <div class="flex justify-between items-center w-[300px]">
          <p>Giảm giá:</p>
          <p class="text-red-500 font-bold">
            {{ formatCurrencyVND(paymentInfo.discount) }}
          </p>
        </div>
        <div
          class="flex justify-between items-center w-[300px]"
          v-if="paymentInfo.shippingOption === 'true'"
        >
          <p class="inline-flex items-center gap-1 mt-1">
            Tiền ship:
            <img
              src="@/assets/image/logo/Logo-GHN.webp"
              width="50"
              height="24"
            />
          </p>
          <a-input-number
            v-model:value="paymentInfo.shippingFee"
            placeholder="Nhập phí vận chuyển"
            class="w-[120px] inline-flex items-center"
            min="0"
            @change="updateTotal"
            :formatter="formatter"
          />
        </div>
        <p v-if="totalAmount >= 2000000 && paymentInfo.shippingOption === 'true'" class="mt-1 ms-16 text-red-500">
          (Free ship cho hóa đơn từ 2.000.000đ.)
        </p>
        <a-form-item label="Phương thức thanh toán" class="text-xl">
          <div class="flex items-center space-x-2">
            <a-tooltip title="Chọn phương thức thanh toán" trigger="hover">
              <a-button
                class="bg-purple-300 flex justify-between items-center gap-2"
                @click="openPaymentMethodModal"
              >
                <v-icon name="ri-bank-card-fill" />
              </a-button>
            </a-tooltip>
          </div>
          <payment-method
            :open="openPaymentMethod"
            :dataCustomer="selectedCustomerInfo"
            :totalAmount="paymentInfo.totalProductPrice"
            :dataVoucher="dataListVoucher"
            :dataSourceInfo="dataSourceInfor"
            @handleClosePaymentMethod="handleClosePaymentMethod"
            @cancel="openPaymentMethod = false"
            @handlePaymented="handleCheckPaymented"
            class="w-[600px] h-[400px]"
          />
        </a-form-item>

        <div
          class="flex justify-between items-center w-[300px] text-xxl font-bold"
        >
          <p>Tổng tiền:</p>
          <p class="text-3xl font-bold">
            {{ formatCurrencyVND(paymentInfo.totalProductPrice) }}
          </p>
        </div>

        <a-button type="primary" class="w-full" @click="handleUpdateBill(1)"
          >Hoàn thành đơn hàng</a-button
        >
      </a-form>
    </div>
  </div>
</template>
  
<script lang="ts" setup>
import {
  ref,
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
} from "@/infrastructure/services/service/admin/payment.action";
import { useUpdateBillWait } from "@/infrastructure/services/service/admin/bill.action";
import VoucherPaymentTable from "./voucher/VoucherPaymentTable.vue";
import PaymentMethod from "./payment-method/PaymentMethod.vue";
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
import {
  invoices,
  currentInvoice,
  sendCartInfo,
  sendPaymentConfirm,
  mbConnectStatus,
  currentPayloadPaymentInfo,
  currentInvoiceUUID,
  InvoiceData
} from "@/infrastructure/mobile.connect/InvoiceConnect2";

// import { BillWaitResponse } from "@/infrastructure/services/api/admin/bill.api";

const props = defineProps({
  dataSourceInfor: {
    type: Object,
    required: true,
    default: () => ({}),
  },
  selectedCustomerInfo: {
    type: Object,
    required: true,
    default: () => ({}),
  },
  isRefresh: Boolean,
});

const emit = defineEmits(["handlePaymentInfo", "handleChangeMbConnect"]);

const pageSize = ref(5);
const current1 = ref(1);

const selectedAddress = ref({});

const paymentedValue = ref(0);

const calculateProductDimensions = () => {
  const totalWeight = dataSourcePro.value.reduce((sum: any, product: any) => {
    return sum + product.soLuong * 200;
  }, 0);
  const totalHeight = dataSourcePro.value.reduce((sum, product) => {
    return sum + product.soLuong * 3;
  }, 0);
  return {
    weight: totalWeight,
    length: 30,
    width: 20,
    height: totalHeight,
  };
};

interface DataType extends POSProductDetailResponse {
  key: string;
  thanhTien: number;
}

const {
  data: dataPro,
  isLoading,
  refetch,
} = useGetOrderDetails(props.dataSourceInfor.id, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const shippingParams = computed<ShippingFeeRequest>(() => ({
  fromDistrictId: 3440,
  fromWardCode: "13007",
  toDistrictId: "",
  toWardCode: "",
  serviceId: 0,
  ...calculateProductDimensions(),
}));

const serviceIdParams = ref<ServiceIdRequest>({
  formDistrict: 0,
  toDistrict: 0,
  shopId: 2509559,
});

const handleAddressUpdate = (newAddress) => {
  selectedAddress.value = newAddress;
  // (shippingParams.value.toDistrictId = newAddress.district),
  //   (shippingParams.value.toWardCode = newAddress.ward);
};

//console.log(shippingParams);

const dataSourcePro: DataType[] | any = computed(() => {
  return (
    dataPro?.value?.data?.map((e: any) => ({
      key: e.id || "",
      maSanPhamChiTiet: e.maSanPhamChiTiet || "",
      ten: e.ten || "",
      soLuong: e.soLuong || "",
      gia: e.gia || 0,
      giaHienTai: e.giaHienTai || 0,
      tenSanPham: e.tenSanPham || "",
      tenThuongHieu: e.tenThuongHieu || "",
      gioiTinh: e.gioiTinh,
        // ? "Nam"
        // : e.gioiTinh == false
        // ? "Nữ"
        // : "Không xác định",
      kichCo: e.kichCo || "",
      phongCach: e.phongCach || "",
      maMauSac: e.maMauSac || "",
      tenMauSac: e.tenMauSac || "",
      linkAnh: e.linkAnh || "",
      thanhTien: e.soLuong * e.gia,
    })) || []
  );
});

// watch(
//   () => dataSourcePro.value,
//   (newData) => {
//     console.log(newData);
//   },
//   { immediate: true }
// );

const paramsVoucher = ref<FindVoucherRequest>({
  page: 1,
  size: 5,
  keyword: "",
  idKhachHang: null,
  tongTien: 0,
});

const paramsNextPriceVoucher = ref<nextVoucherRequest>({
  idKhachHang: null,
  tongTien: 0,
});

const paymentInfo = ref({
  method: "cash",
  bankAccount: formatCurrencyVND(""),
  voucherCode: "",
  voucherId: null,
  shippingOption: "false",
  shippingFee: 0,
  discount: 0,
  total: 0,
  totalProductPrice: 0,
  name: "" || null,
  fullAddress: "" || null,
  phoneNumber: "" || null,
});

// Lấy service ID GHN
const { data: service, refetch: refetchService } = useGetServiceId(
  serviceIdParams,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled:
      paymentInfo.value.shippingOption === "true" &&
      !!serviceIdParams.value.formDistrict &&
      !!serviceIdParams.value.toDistrict,
  }
);

const { data: shipping, refetch: refetchShipping } = useGetShippingFee(
  shippingParams,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled:
      paymentInfo.value.shippingOption === "true" &&
      !!shippingParams.value.toDistrictId &&
      !!shippingParams.value.toWardCode,
  }
);

watch(
  () => props.selectedCustomerInfo,
  (newData) => {
    if (props.selectedCustomerInfo) {
      paramsVoucher.value.idKhachHang = newData.key;
      paramsNextPriceVoucher.value.idKhachHang = newData.key;
    }
  }
);

watch(
  () => dataSourcePro.value,
  (newData) => {
    if (newData) {
      // console.log(newData);selectVoucher

      paramsVoucher.value.tongTien = totalAmount.value;
      paramsNextPriceVoucher.value.tongTien = totalAmount.value;
      if (serviceIdParams.value.toDistrict !== 0) {
        refetchService().then(() => {
          shippingParams.value.serviceId = service?.value?.data[0].service_id;
          // console.log(shippingParams.value);
          shippingParams.value.toDistrictId = getCustomerAddress.value.district;
          shippingParams.value.toWardCode = getCustomerAddress.value.ward;
          if (shippingParams.value.toWardCode) {
            refetchShipping().then(() => {
              if (totalAmount.value <= 2000000) {
                paymentInfo.value.shippingFee = shipping?.value?.data.total;
              } else {
                paymentInfo.value.shippingFee = 0;
              }
            });
          }
        });
      } else {
        paymentInfo.value.shippingFee = 0;
      }
    }
  }
);

const { data: dataVouchers } = useGetListVoucher(paramsVoucher, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataListVoucher = computed(() => dataVouchers?.value?.data?.data || []);

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

const voucher = ref(null);

watch(
  () => dataListVoucher.value,
  (newData) => {
    console.log(newData);
    
    if (newData && newData.length > 0) {
      paymentInfo.value.voucherCode = newData[0].ma;
      paymentInfo.value.voucherId = newData[0].id;
      paymentInfo.value.discount = newData[0].loaiGiam ? parseFloat(newData[0].giaTri) : (totalAmount.value * parseFloat(newData[0].giaTri) / 100);
      paymentInfo.value.totalProductPrice =
        totalAmount.value - paymentInfo.value.discount;
      voucher.value =
        newData.find((voucher) => voucher.id === paymentInfo.value.voucherId) ||
        null;
      // console.log(dataNextPriceVouchers.value);

      // Thảo
      invoices.value.forEach((item) => {
        if (item.id === currentInvoice.value.id) {
          item.vouchers = [
            {
              id: newData[0].id,
              code: newData[0].ma,
              name: newData[0].ten,
              discount: Number(
                newData[0].loaiGiam ? newData[0].giaTri : newData[0].giaTri
              ),
              type: newData[0].loaiGiam ? "fixed" : "percent",
            },
          ];
          currentInvoice.value = item;
          sendCartInfo(item);
        }
      });
    } else {
      paymentInfo.value.voucherCode = "";
      paymentInfo.value.voucherId = null;
      paymentInfo.value.discount = 0;
      paymentInfo.value.totalProductPrice =
        totalAmount.value - paymentInfo.value.discount;
    }
  }
);

// Hàm format tiền sang VNĐ
const formatter = (value: any) => {
  if (!value) return "";
  return `${value} ₫`.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const totalAmount = computed(() => {
  // console.log(dataSourcePro.value);
  const total =
    dataSourcePro.value.reduce((total, e) => {
      return total + (e.giaHienTai * e.soLuong || 0);
    }, 0) || 0;
  paramsVoucher.value.tongTien = total;
  return total;
});

const discounted = computed(() => {
  const total =
    dataSourcePro.value.reduce((total, e) => {
      return total + (e.gia * e.soLuong || 0);
    }, 0) || 0;

  paymentInfo.value.totalProductPrice = total;
  return total;
});

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
    (paymentInfo.value.shippingFee || 0) - (paymentInfo.value.discount || 0);

    console.log(paymentInfo.value.shippingFee);
    invoices.value.forEach((item) => {
      if (item.id === currentInvoice.value.id) {
        if (item.shipping) {
          item.shipping.cost = paymentInfo.value.shippingFee || 0;
        }
        currentInvoice.value = item;
        sendCartInfo(item);
      }
    });
    sendCartInfo
};

const handleCheckPaymented = (totalAmountAfter: number) => {
  paymentedValue.value = totalAmountAfter;
};

const handleSelectVoucher = (voucher) => {
  //Loai giảm = true: tiền, false: %
  // Kiểu true: công khai, false: cá nhân
  if (paymentedValue.value !== 0) {
    warningNotiSort("Bạn đã thanh toán không thể thay đổi phiếu giảm giá!");
    return;
  }
  paymentInfo.value.voucherCode = voucher.ma;
  paymentInfo.value.voucherId = voucher.id;
  if (!voucher.loaiGiam) {
    paymentInfo.value.discount = totalAmount.value * (voucher.giaTri  / 100);
    paymentInfo.value.totalProductPrice =
      totalAmount.value - paymentInfo.value.discount;
  } else {
    paymentInfo.value.discount = voucher.giaTri;
    paymentInfo.value.totalProductPrice =
      totalAmount.value - paymentInfo.value.discount;
  }
};

watch(
  () => paymentInfo.value,
  (newData) => {
    if (dataListVoucher.value) {
      voucher.value =
        dataListVoucher.value.find(
          (voucherSelected) =>
            voucherSelected.id === paymentInfo.value.voucherId
        ) || null;
      // console.log(voucher.value);
    }
  },
  { deep: true }
);

const handleNotVoucher = () => {
  paymentInfo.value.voucherCode = "";
  paymentInfo.value.voucherId = null;
  paymentInfo.value.discount = 0;
  paymentInfo.value.totalProductPrice = totalAmount.value;

  // Thảo
  invoices.value.forEach((item) => {
    if (item.id === currentInvoice.value.id) {
      item.vouchers = [];
      currentInvoice.value = item;
      sendCartInfo(item);
    }
  });
};

const changeShippingOption = (option: string) => {
  paymentInfo.value.shippingOption = option;
   // Thảo
   invoices.value.forEach((item) => {
    if (item.id === currentInvoice.value.id) {
      if (option === 'true') {
          item.customerInfo.address = {
            province: getCustomerAddress.value ? getCustomerAddress.value.province || '' : '',
            district: getCustomerAddress.value ? getCustomerAddress.value.district ?? '' : '',
            ward: getCustomerAddress.value ? getCustomerAddress.value.ward || '' : '',
            detail: getCustomerAddress.value ? getCustomerAddress.value.line : '',
            phone: getCustomerAddress.value ? getCustomerAddress.value.phoneNumber || '' : '',
            note: ''
          };
      } else {
          item.customerInfo.address = null;
      }
      currentInvoice.value = item;
      sendCartInfo(item);
    }
  });
  emit("handleChangeMbConnect", paymentInfo.value);
};

const openLocalPdf = (maHoaDon) => {
  const filePath = "/hoa_don/HD.pdf"; // Đường dẫn file PDF trong thư mục public hoặc được serve bởi server
  window.open(filePath, "_blank");
};

const { mutate: updateBillWait } = useUpdateBillWait();

const handleUpdateBill = (x: number) => {
  const pdfParams = {
    idKhachHang: props.selectedCustomerInfo
      ? props.selectedCustomerInfo.id
      : null,

    idNhanVien: useAuthStore().user?.id || null,

    idHoaDon: props.dataSourceInfor.id,

    products: dataSourcePro.value,

    tongTien: totalAmount.value,

    phiVanChuyen: paymentInfo.value.shippingFee,

    giamGia: paymentInfo.value.discount,
  };
  const payload = {
    trangThai:
      paymentInfo.value.shippingOption === "true"
        ? "Chờ giao hàng"
        : "Thành công",
    idKhachHang: props.selectedCustomerInfo
      ? props.selectedCustomerInfo.id
      : null,
    idPhieuGiamGia: paymentInfo.value.voucherId || null,
    idNhanVien: useAuthStore().user?.id || null,
    diaChiNguoiNhan:
      paymentInfo.value.shippingOption === "true"
        ? paymentInfo.value.fullAddress
        : null,
    tenNguoiNhan:
      paymentInfo.value.shippingOption === "true"
        ? paymentInfo.value.name
        : null,
    soDienThoai:
      paymentInfo.value.shippingOption === "true"
        ? paymentInfo.value.phoneNumber
        : null,
    ngayShip: null,
    ghiChu: null,
    tienGiam: paymentInfo.value.discount || null,
    tienShip:
      paymentInfo.value.shippingOption === "true"
        ? paymentInfo.value.shippingFee
        : null,
    tongTien: paymentInfo.value.totalProductPrice || null,
    xa: getCustomerAddress?.value?.ward || null,
    huyen: getCustomerAddress?.value?.district || null,
    tinh: getCustomerAddress?.value?.province || null,
    phuongThucNhan:
      paymentInfo.value.shippingOption === "true" ? "Giao hàng" : "Tại quầy",
  };
  if (
    paymentInfo.value.shippingOption === "true" &&
    (!paymentInfo.value.name ||
      !paymentInfo.value.phoneNumber ||
      !paymentInfo.value.fullAddress)
  ) {
    warningNotiSort("Vui lòng điền thông tin người nhận!");
    return;
  }
  if (dataSourcePro.value.length <= 0) {
    warningNotiSort("Không có sản phẩm nào trong giỏ hàng!");
    return;
  }
  if (
    paymentedValue.value === 0 ||
    Math.floor(paymentedValue.value) <
      Math.floor(paymentInfo.value.totalProductPrice)
  ) {
    warningNotiSort(
      "Vui lòng chọn phương thức thanh toán và tiến hành thanh toán!"
    );
    return;
  }

  if (getTotalQuantity() > 1000) {
    warningNotiSort("Số lượng sản phẩm trong giỏ không được quá 1000!");
    return;
  }

  currentPayloadPaymentInfo.value = payload;
  currentInvoiceUUID.value = props.dataSourceInfor.id;

  if (x === 1) {
    Modal.confirm({
      content: "Bạn chắc chắn muốn hoàn thành thanh toán?",
      icon: createVNode(ExclamationCircleOutlined),
      centered: true,
      onOk() {
        if (mbConnectStatus.value) {
          console.log("Thao tác thanh toán trên app MB Connect");
          sendPaymentConfirm(currentInvoice.value.id);
          // hoanThanhDonHang(payload);
        } else {
          hoanThanhDonHang(payload);
        }
      },
      cancelText: "Huỷ",
      onCancel() {
        Modal.destroyAll();
      },
    });
  } else if (x === 2) {

  }
};

const hoanThanhDonHang = async (payload: any) => {
      try {
        await updateBillWait({
          idBill: props.dataSourceInfor.id,
          params: payload,
        });
        successNotiSort("Thanh toán thành công!");
        router.push(
          ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.path
        );
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          errorNotiSort(error?.response?.data?.message);
        }
      }
};

interface CustomerAddress {
  line: string;
  province?: string;
  district?: string;
  ward?: string;
  name?: string;
  phoneNumber?: string;
}

const getCustomerAddress = ref<CustomerAddress | null>(null);

const handleGetCustomerAddress = async (modelRef: any, fullAddress: string) => {
  getCustomerAddress.value = modelRef;
  paymentInfo.value.name = modelRef.name;
  paymentInfo.value.phoneNumber = modelRef.phoneNumber;
  const wardInfo = ref(null);
  const districtInfo = ref(null);
  const provinceInfo = ref(null);

  if (modelRef.ward || modelRef.district || modelRef.province) {
    try {
      const response = await getWardByCode(modelRef.ward);
      wardInfo.value = response.data.data;

      const responseDis = await getDistrictById(modelRef.district);
      districtInfo.value = responseDis.data.data;

      const responsePro = await getProvinceById(modelRef.province);
      provinceInfo.value = responsePro.data.data;
      paymentInfo.value.fullAddress =
        modelRef.line +
        ", " +
        wardInfo.value +
        ", " +
        districtInfo.value +
        ", " +
        provinceInfo.value;
    } catch (error) {
      console.error("Lỗi khi lấy thông tin Xã, huyện, tỉnh:", error);
    }

    invoices.value.forEach((invoice: InvoiceData) => {
    if (invoice.id === currentInvoice.value.id) {
      invoice.customerInfo.address = {
        province: provinceInfo.value ?? '',
        district: districtInfo.value ?? '',
        ward: wardInfo.value ?? '',
        detail: modelRef.line,
        receiver: modelRef.name,
        phone: modelRef.phoneNumber,
        note: '',
      };
      currentInvoice.value = invoice;
      sendCartInfo(currentInvoice.value);
    }
    });
  }

  serviceIdParams.value.formDistrict = shippingParams.value.fromDistrictId;
  serviceIdParams.value.toDistrict = Number(modelRef.district);

  if (serviceIdParams.value.toDistrict !== 0) {
    refetchService().then(() => {
      shippingParams.value.serviceId = service?.value?.data[0].service_id;

      shippingParams.value.toDistrictId = modelRef.district;
      shippingParams.value.toWardCode = modelRef.ward;

      if (shippingParams.value.toWardCode) {
        refetchShipping().then(() => {
          if (totalAmount.value <= 2000000) {
            paymentInfo.value.shippingFee = shipping?.value?.data.total;
          } else {
            paymentInfo.value.shippingFee = 0;
          }
        });
      }
    });
  } else {
    paymentInfo.value.shippingFee = 0;
  }
  // console.log(paymentInfo.value);
};

watch(totalAmount, (newTotal) => {
  if (newTotal !== 0) {
    // console.log(newTotal);
    paymentInfo.value.totalProductPrice = newTotal;
  }
});

watch(
  () => paymentInfo.value.shippingFee,
  (newTotal) => {
    if (
      newTotal &&
      newTotal !== 0 &&
      paymentInfo.value.shippingOption === "true"
    ) {
      paymentInfo.value.totalProductPrice =
        totalAmount.value + newTotal - paymentInfo.value.discount;
    } else {
      paymentInfo.value.totalProductPrice =
        totalAmount.value + 0 - paymentInfo.value.discount;
    }
  }
);

// Theo dõi nếu thay đổi hình thức mua hàng thì sẽ thay đổi tổng tiền
watch(
  () => paymentInfo.value.shippingOption,
  (newData) => {
    if (newData) {
      if (
        paymentInfo.value.shippingFee &&
        paymentInfo.value.shippingFee !== 0 &&
        paymentInfo.value.shippingOption === "true"
      ) {
        paymentInfo.value.totalProductPrice =
          totalAmount.value +
          paymentInfo.value.shippingFee -
          paymentInfo.value.discount;
      } else {
        paymentInfo.value.shippingFee = 0;
        paymentInfo.value.totalProductPrice =
          totalAmount.value +
          paymentInfo.value.shippingFee -
          paymentInfo.value.discount;
      }
    }
  }
);

// Theo dõi thay đổi và cập nhật tổng tiền
watch(
  [() => paymentInfo.value.shippingFee, () => paymentInfo.value.discount],
  updateTotal
);
</script>
  