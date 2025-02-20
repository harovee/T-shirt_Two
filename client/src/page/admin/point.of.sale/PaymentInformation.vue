<template>
  <div
    :class="
      paymentInfo.shippingOption === 'true'
        ? 'mt-5 grid grid-cols-1 lg:grid-cols-2 gap-6'
        : 'mt-5'
    "
  >
    <div
      class="bg-white me-5"
      v-if="paymentInfo.shippingOption === 'true'"
    >
      <pay-ment-address
        :selectedCustomerAddress="selectedCustomerAddress"
        :isRefresh="isRefresh"
      />
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
              disabled
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
          <div v-if="dataNextPriceVouchers" class="text-red-500">
            (Hãy mua hàng thêm {{formatCurrencyVND(dataNextPriceVouchers - totalAmount)}} để có thể sử dụng phiếu giảm giá tốt hơn.)
          </div>
          <voucher-payment-table
            :open="open"
            :dataCustomer="selectedCustomerInfo"
            :totalAmount="totalAmount"
            :dataVoucher="dataListVoucher"
            @handleClose="handleClose"
            @cancel="open = false"
            class="w-[600px] h-[400px]"
            @handleOpenKhachHang="openVoucherModal"
            @selectVoucher="handleVoucherSelected"
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
        <!-- <a-form-item label="Phương thức thanh toán">
          <a-select
            v-model:value="paymentInfo.method"
            placeholder="Chọn phương thức thanh toán"
          >
            <a-select-option value="cash">Tiền mặt</a-select-option>
            <a-select-option value="bank">Chuyển khoản</a-select-option>
          </a-select>
        </a-form-item> -->
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
          <!-- <payment-method-table
            :open="open"
            :dataCustomer="selectedCustomerInfo"
            :totalAmount="totalAmount"
            @handleClose="handleClose"
            @cancel="open = false"
            class="w-[600px] h-[400px]"
            @handleOpenKhachHang="openVoucherModal"
            @selectVoucher="handleVoucherSelected"
          /> -->
          <payment-method
            :open="openPaymentMethod"
            :dataCustomer="selectedCustomerInfo"
            :totalAmount="totalAmount"
            :dataVoucher="dataListVoucher"
            :dataSourceInfo="dataSourceInfor"
            @handleClosePaymentMethod="handleClosePaymentMethod"
            @cancel="openPaymentMethod = false"
            class="w-[600px] h-[400px]"
          />
        </a-form-item>

        <!-- <a-form-item
          v-if="paymentInfo.method === 'cash'"
          label="Nhập tiền khách đưa"
        >
          <a-input
            type="number"
            min="0"
            v-model:value="paymentInfo.bankAccount"
            placeholder="Nhập số tiền"
          />
        </a-form-item> -->

        <!-- Phí vận chuyển -->
        <a-form-item label="Phí vận chuyển" v-if="paymentInfo.isShipping">
          <a-input-number
            v-model:value="paymentInfo.shippingFee"
            placeholder="Nhập phí vận chuyển"
            class="w-full"
            @change="updateTotal"
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

        <a-button type="primary" class="w-full" @click="handleUpdateBill"
          >Hoàn thành đơn hàng</a-button
        >
      </a-form>
    </div>
  </div>
</template>
  
<script lang="ts" setup>
import { ref, watch, reactive, defineProps, computed, createVNode } from "vue";
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
  useGetPriceNextVoucher
} from "@/infrastructure/services/service/admin/payment.action";
import { useUpdateBillWait } from "@/infrastructure/services/service/admin/bill.action";
import VoucherPaymentTable from "./voucher/VoucherPaymentTable.vue";
import PaymentMethod from "./payment-method/PaymentMethod.vue";
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
  nextVoucherRequest
} from "@/infrastructure/services/api/admin/payment.api";

// import { BillWaitResponse } from "@/infrastructure/services/api/admin/bill.api";

const props = defineProps({
  dataSourceInfor: {
    type: Object,
    required: true,
  },
  selectedCustomerInfo: {
    type: Object,
    required: true,
  },
  selectedCustomerAddress: {
    type: Object,
    required: true,
  },
  isRefresh: Boolean,
});

const emit = defineEmits(["handlePaymentInfo"]);

const pageSize = ref(5);
const current1 = ref(1);



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
      gioiTinh: e.gioiTinh
        ? "Nam"
        : e.gioiTinh == false
        ? "Nữ"
        : "Không xác định",
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
  tongTien: 0
});

const paramsNextPriceVoucher = ref<nextVoucherRequest>({
  idKhachHang: null,
  tongTien: 0
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
});

watch(() => props.selectedCustomerInfo, (newData) => {
  if (props.selectedCustomerInfo) {
    paramsVoucher.value.idKhachHang = newData.key
    paramsNextPriceVoucher.value.idKhachHang = newData.key
  }
});

watch(() => dataSourcePro.value, (newData) => {
  if (newData) {
    paramsVoucher.value.tongTien = totalAmount.value
    paramsNextPriceVoucher.value.tongTien = totalAmount.value
  }
});

const { data: dataVouchers } = useGetListVoucher(paramsVoucher, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataListVoucher = computed(() => dataVouchers?.value?.data?.data || []);

const { data: dataNextPriceVoucher } = useGetPriceNextVoucher(paramsNextPriceVoucher, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataNextPriceVouchers = computed(() => dataNextPriceVoucher?.value?.data || []);

watch(() => dataListVoucher.value, (newData) => {
  if (newData && newData.length > 0) {
    paymentInfo.value.voucherCode = newData[0].ma;
    paymentInfo.value.voucherId = newData[0].id;
    paymentInfo.value.discount = parseFloat(newData[0].giaTriGiam);
    paymentInfo.value.totalProductPrice = totalAmount.value - paymentInfo.value.discount;
  } else {
    paymentInfo.value.voucherCode = "";
    paymentInfo.value.voucherId = null;
    paymentInfo.value.discount = 0;
    paymentInfo.value.totalProductPrice = totalAmount.value - paymentInfo.value.discount;
  }
});

watch(() => dataNextPriceVouchers.value, (newData) => {
  console.log(newData);

});

const totalAmount = computed(() => {
  const total =
    dataSourcePro.value.reduce((total, e) => {
      return total + (e.gia * e.soLuong || 0);
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

const openPaymentMethod = ref (false);

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
};

const handleVoucherSelected = (voucher) => {
  paymentInfo.value.voucherCode = voucher.ma;
  paymentInfo.value.voucherId = voucher.id;
  if (!voucher.loaiGiam) {
    paymentInfo.value.discount = voucher.giaTriGiam;
    paymentInfo.value.totalProductPrice =
      totalAmount.value - paymentInfo.value.discount;
  } else {
    paymentInfo.value.discount = voucher.giaTriGiam;
    paymentInfo.value.totalProductPrice =
      totalAmount.value - paymentInfo.value.discount;
  }
};

const handleNotVoucher = () => {
  paymentInfo.value.voucherCode = "";
  paymentInfo.value.voucherId = null;
  paymentInfo.value.discount = 0;
  paymentInfo.value.totalProductPrice = totalAmount.value
}

const changeShippingOption = (option: string) => {
  paymentInfo.value.shippingOption = option;
  emit("handlePaymentInfo", paymentInfo.value);
};

const { mutate: updateBillWait } = useUpdateBillWait();

const handleUpdateBill = () => {
  const payload = {
    trangThai: "Thành công" || null,
    idKhachHang: props.selectedCustomerInfo
      ? props.selectedCustomerInfo.key
      : null,
    idPhieuGiamGia: paymentInfo.value.voucherId || null,
    idNhanVien: null,
    diaChiNguoiNhan: null,
    tenNguoiNhan: null,
    soDienThoai: null,
    ngayShip: null,
    ghiChu: null,
    tienGiam: paymentInfo.value.discount || null,
    tienShip: paymentInfo.value.shippingFee || null,
    tongTien: paymentInfo.value.totalProductPrice || null,
  };
  Modal.confirm({
    content: "Bạn chắc chắn muốn hoàn thành thanh toán?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,

    async onOk() {
      // console.log(props.dataSourceInfor);

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
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
    },
  });
};

watch(totalAmount, (newTotal) => {
  if (newTotal !== 0) {
    paymentInfo.value.totalProductPrice = newTotal;
  }
});

// Theo dõi thay đổi và cập nhật tổng tiền
watch(
  [() => paymentInfo.value.shippingFee, () => paymentInfo.value.discount],
  updateTotal
);

</script>
  