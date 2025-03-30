<template>
  <a-modal
    :open="open"
    @ok="handlePayment"
    @cancel="handleClose"
    key=""
    :width="'700px'"
    :okText="'Xác nhận'"
    :cancelText="'Hủy bỏ'"
  >
    <h1 class="text-xl">Thanh toán</h1>
    <div class="flex justify-between">
      <p>Số tiền phải thanh toán:</p>
      <p class="text-lg">{{ formatCurrencyVND(paymentInfoData.amountPayable) }}</p>
      <!-- <p v-if="dataPaymentMethodDetails && dataPaymentMethodDetails.length && (totalAmountAfter >= totalAmount)" class="text-lg">{{ formatCurrencyVND(0) }}</p> -->
    </div>
    <a-form :model="params" :rules="rulesRef" layout="vertical">
      <div class="gap-4">
        <a-form-item label="Hình thức thanh toán">
          <a-select
            ref="select"
            v-model:value="params.idPhuongThucThanhToan"
            class="w-full"
            @change="handleChange"
          >
            <a-select-option value="tienmat">Tiền mặt</a-select-option>
            <a-select-option value="chuyenkhoan">Chuyển khoản</a-select-option>
            <a-select-option value="cahai">Cả hai</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          v-if="params.idPhuongThucThanhToan === 'tienmat'"
          label="Tiền khách đưa"
          name="tienKhachDua"
        >
          <a-input-number
            class="w-full"
            v-model:value="params.tienKhachDua"
            min="0"
            :formatter="formatter"
          />
          <p class="mt-3 text-red-500" v-if="params.soTienDu < 0">
            Còn thiếu: {{ formatCurrencyVND(params.soTienDu) }}
          </p>
          <p class="mt-3" v-if="params.soTienDu > 0">
            Tiền thừa: {{ formatCurrencyVND(params.soTienDu) }}
          </p>
        </a-form-item>

        <a-form-item
          v-if="params.idPhuongThucThanhToan === 'cahai'"
          label="Tiền mặt"
          name="tienKhachDua"
        >
          <a-input-number
            class="w-full"
            v-model:value="params.tienKhachDua"
            min="0"
            :formatter="formatter"
          />
          <p class="mt-3" v-if="params.soTienDu > 0">
            Tiền thừa: {{ formatCurrencyVND(params.soTienDu) }}
          </p>
        </a-form-item>

        <a-form-item
          v-if="params.idPhuongThucThanhToan === 'cahai'"
          label="Tiền chuyển khoản"
        >
          <a-input-number
            class="w-full"
            v-model:value="params.tienChuyenKhoan"
            min="0"
            :formatter="formatter"
            readonly
          />
        </a-form-item>

        <a-form-item
          v-if="
            params.idPhuongThucThanhToan === 'chuyenkhoan' ||
            params.idPhuongThucThanhToan === 'cahai'
          "
          label="Mã giao dịch"
          name="maGiaoDich"
        >
          <a-input v-model:value="params.maGiaoDich"> </a-input>
        </a-form-item>
      </div>
    </a-form>
  </a-modal>
</template>
<script lang="ts" setup>
import type { TableProps, TableColumnType } from "ant-design-vue";
import { Form, message, Modal, Upload } from "ant-design-vue";
import {
  formatCurrencyVND,
  getDateFormat,
  getDateTimeMinutesFormat,
} from "@/utils/common.helper";
import {
  defineProps,
  computed,
  defineEmits,
  ref,
  watch,
  onMounted,
  reactive,
  createVNode,
} from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  VoucherResponse,
  FindVoucherRequest,
  PaymentMethodDetailResponse,
  nextVoucherRequest,
  paymentMethodDetailRequest,
} from "@/infrastructure/services/api/admin/payment.api";
import {
  useGetListPaymentMethodDetail,
  useCreatePaymentMethodDetail,
} from "@/infrastructure/services/service/admin/payment.action";
import { convertDateFormatTime } from "@/utils/common.helper";
import { useRoute } from "vue-router";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import {
  warningNotiSort,
  successNotiSort,
  errorNotiSort,
} from "@/utils/notification.config";

const pageSize = ref(5);
const current1 = ref(1);

const props = defineProps({
  open: {
    type: Boolean,
    required: true,
  },
  totalPrice: Number,
  paymentInfoData: Object
});

const emit = defineEmits(["handleClose", "handlePaymented"]);

const { mutate: createPaymentMethodDetail } = useCreatePaymentMethodDetail();

const rulesRef = reactive({
  tienKhachDua: [
    {
      required: true,
      message: "Vui lòng nhập tiền khách đưa",
      trigger: ["blur", "change"],
    },
    {
      validator: (_, value) => {
        if (value === 0) {
          return Promise.reject(new Error("Tiền khách đưa phải lớn hơn 0"));
        }
        return Promise.resolve();
      },
      trigger: ["blur", "change"],
    },
  ],
  maGiaoDich: [
    {
      validator: (_, value) => {
        if (
          (params.value.idPhuongThucThanhToan === "chuyenkhoan" ||
            params.value.idPhuongThucThanhToan === "cahai") &&
          !value
        ) {
          return Promise.reject(new Error("Vui lòng nhập mã giao dịch"));
        }
        return Promise.resolve();
      },
      trigger: ["blur", "change"],
    },
  ],
});

const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || null;
};

const params = ref<paymentMethodDetailRequest>({
  idHoaDon: getIdHoaDonFromUrl(),
  idPhuongThucThanhToan: "tienmat",
  tienKhachDua: 0,
  maGiaoDich: null,
  soTienDu: null,
  tienChuyenKhoan: 0,
});

const { resetFields, validate, validateInfos } = Form.useForm(params, rulesRef);

const handleChange = (value: string) => {
  if (value === "tienmat") {
    params.value.tienKhachDua = 0;
    params.value.soTienDu = 0;
  } else if (value === "cahai") {
    params.value.tienKhachDua = 0;
    params.value.soTienDu = 0;
  } else {
    params.value.soTienDu = 0;
  }
  params.value.idPhuongThucThanhToan = value;
};

const { data: dataPaymentMethodDetail } = useGetListPaymentMethodDetail(
  params,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
  }
);

const dataPaymentMethodDetails = computed(
  () => dataPaymentMethodDetail?.value?.data || []
);

const totalAmountAfter = ref(0);

watch(
  () => props.open,
  (newData) => {
    resetFields();
  }
);

watch(
  () => params.value,
  (newData) => {
    if (params.value.tienKhachDua === null) {
      params.value.soTienDu = 0;
    }
    if (params.value.idPhuongThucThanhToan === "tienmat") {
      if (params.value.tienKhachDua > 0) {
        params.value.soTienDu = params.value.tienKhachDua - props.paymentInfoData.amountPayable;
      }
    } else if (params.value.idPhuongThucThanhToan === "chuyenkhoan") {
      params.value.tienKhachDua = props.paymentInfoData.amountPayable;
    } else {
      if (params.value.tienKhachDua > 0 && params.value.tienChuyenKhoan === 0) {
        params.value.soTienDu = params.value.tienKhachDua - props.paymentInfoData.amountPayable;
      }
      params.value.tienChuyenKhoan =
        props.paymentInfoData.amountPayable - params.value.tienKhachDua;
      if (params.value.tienChuyenKhoan < 0) {
        params.value.tienChuyenKhoan = 0;
      }
    }
  },
  { deep: true, immediate: true }
);

const formatter = (value: any) => {
  if (!value) return "";
  return `${value} ₫`.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const handlePayment = () => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn thanh toán?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await validate();

        if (props.paymentInfoData.amountPayable === 0) {
          warningNotiSort(
            "Bạn đã thanh toán đủ số tiền cần thanh toán !"
          );
          return;
        }
        if (params.value.idPhuongThucThanhToan === 'tienmat' && params.value.tienKhachDua < props.paymentInfoData.amountPayable) {
          warningNotiSort("Tiền khách đưa chưa đủ!");
          return;
        }
        if (params.value.tienKhachDua > props.paymentInfoData.amountPayable) {
          params.value.tienKhachDua = props.paymentInfoData.amountPayable
        }
        createPaymentMethodDetail(params.value, {
          onSuccess: (result) => {
            successNotiSort(result?.message);
            handleClose();
          },
          onError: (error: any) => {
            errorNotiSort(error?.response?.data?.message);
          },
        });
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          warningNotiSort(error?.response?.data?.message);
        } else if (error?.errorFields) {
          warningNotiSort("Vui lòng nhập đầy đủ các trường dữ liệu");
        }
      }
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
    },
  });
};

const handleClose = () => {
  emit("handleClose");
  resetFields();
};
</script>
  