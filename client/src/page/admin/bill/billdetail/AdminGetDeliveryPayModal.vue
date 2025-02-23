<template>
  <a-modal
    :open="props.open"
    title="Thanh toán"
    @cancel="handleClose"
    @ok="handleCreatePayment"
    ok-text="Lưu"
    cancel-text="Hủy"
    destroyOnClose
    centered
  >
    <a-form layout="vertical" class="pt-3">
      <template v-for="field in formFields" :key="field.name">
        <a-form-item
          :label="field.label"
          :name="field.name"
          v-bind="validateInfos[field.name]"
        >
          <a-radio-group
            v-if="field.component === 'a-radio-group'"
            v-model:value="modelRef.idPhuongThucThanhToan"
            :options="listPaymentMethod"
          />
          <!-- Trường Tổng tiền & Tiền thừa: Chỉ đọc -->
          <a-input
            v-else-if="['tongTien', 'soTienDu'].includes(field.name)"
            v-model:value="formattedValues[field.name]"
            type="text"
            readonly
          />

          <!-- Tiền khách đưa: Người dùng nhập -->
          <!-- <a-input
            v-else-if="field.name === 'tienKhachDua'"
            v-model:value="formattedValues.tienKhachDua"
            type="text"
            @beforeinput="handleBeforeInput"
            @input="handleInput($event, 'tienKhachDua')"
            placeholder="Nhập tiền khách đưa"
          /> -->
          <a-input-number
            v-else-if="field.name === 'tienKhachDua'"
            class="w-full"
            v-model:value="modelRef.tienKhachDua"
            min="0"
            placeholder="Nhập tiền khách đưa"
            :formatter="formatter"
          />

          <!-- Ghi chú (Xử lý nhập an toàn) -->
          <!-- <a-textarea
            v-else-if="field.name === 'ghiChu'"
            v-model:="modelRef.ghiChu"
            placeholder="Nhập ghi chú"
          /> -->
          <component
            v-else
            :is="field.component"
            v-bind="field.props"
            v-model:value="modelRef[field.name]"
          >
          </component>
        </a-form-item>
      </template>
    </a-form>
  </a-modal>
</template>
<script lang="ts" setup>

import { CreateDeliveryPaymentRequest } from "@/infrastructure/services/api/admin/deliverypayment.api";
import { useCreateDeliveryPayment } from "@/infrastructure/services/service/admin/deliverypayment.action";
import { useGetPaymentMethod } from "@/infrastructure/services/service/admin/payhistory.action";
import { formatCurrencyVND } from "@/utils/common.helper";
import { keepPreviousData } from "@tanstack/vue-query";
import { Form, Modal } from "ant-design-vue";
import { computed, createVNode, reactive, ref, watchEffect, watch } from "vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { errorNotiSort, successNotiSort, warningNotiSort } from "@/utils/notification.config";

const props = defineProps({
  open: Boolean,
  totalPrice: Number,
});

const {mutate: createPayment} = useCreateDeliveryPayment();

const isPaymentDisabled = ref(false); // Biến trạng thái để disable nút thanh toán

const handleCreatePayment = () => {
  Modal.confirm({
    content: "Bạn muốn lưu lại thanh toán?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await validate();
        console.log("API", modelRef);

        isPaymentDisabled.value = true; // Disable nút khi bắt đầu thanh toán

        createPayment(modelRef, {
          onSuccess: (result) => {
            successNotiSort(result?.message);
            handleClose();
          },
          onError: (error: any) => {
            errorNotiSort(error?.response?.data?.message);
            isPaymentDisabled.value = false; // Nếu lỗi, bật lại nút
          },
        });
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        warningNotiSort("Vui lòng nhập đầy đủ các trường dữ liệu");
        isPaymentDisabled.value = false; // Nếu lỗi, bật lại nút
      }
    },
    cancelText: "Hủy",
    onCancel() {
      Modal.destroyAll();
    }
  });
};

const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

const emit = defineEmits(["handleClose"]);

const modelRef = reactive<CreateDeliveryPaymentRequest>({
  idHoaDon: getIdHoaDonFromUrl(),
  idPhuongThucThanhToan: null,
  tongTien: 0,
  tienKhachDua: 0,
  soTienDu: 0,
  ghiChu: null,
  maGiaoDich: null,
});

const rulesRef = reactive({
  tienKhachDua: [
    {
      required: true,
      message: "Vui lòng nhập tiền khách đưa",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        if (modelRef.tongTien && value < modelRef.tongTien) {
          return Promise.reject("Tiền khách đưa phải lớn hơn hoặc bằng tổng tiền");
        }
        return Promise.resolve();
      },
      trigger: "blur",
    },
  ],
  ghiChu: [
    {
      max: 255,
      message: "Ghi chú không được vượt quá 255 ký tự",
      trigger: "blur",
    },
  ],
});


const { resetFields, validate, validateInfos } = Form.useForm(
  modelRef,
  rulesRef
);

const { data: paymentMethods } = useGetPaymentMethod({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listPaymentMethod = computed(() => {
  return (
    paymentMethods?.value?.data?.map((method) => ({
      value: method.id,
      label: method.tenPhuongThuc,
    })) || []
  );
});

watchEffect(() => {
  if (props.open && listPaymentMethod.value.length > 0) {
    const tienMat = listPaymentMethod.value.find(
      (method) => method.label === "Tiền mặt"
    );
    modelRef.idPhuongThucThanhToan = tienMat
      ? tienMat.value
      : listPaymentMethod.value[0].value;

    modelRef.tongTien = props.totalPrice || 0;
  }
});

const formattedValues = computed(() => ({
  tongTien: formatCurrencyVND(modelRef.tongTien),
  tienKhachDua: formatCurrencyVND(modelRef.tienKhachDua),
  soTienDu: formatCurrencyVND(modelRef.soTienDu),
}));

//xử lý nhập tiền khách đưa

const formatter = (value: any) => {
  if (!value) return "";
  return `${value} ₫`.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const MAX_AMOUNT = 1_000_000_000_000; // Giới hạn số tiền tối đa

const handleBeforeInput = (event: InputEvent) => {
  const target = event.target as HTMLInputElement | null;
  if (!target) return;

  let rawValue = target.value.replace(/\D/g, ""); // Lấy số hiện tại
  let newValue = rawValue + event.data; // Giá trị sau khi nhập thêm

  if (Number(newValue) > MAX_AMOUNT) {
    console.warn(`🚫 Chặn nhập: ${newValue} vượt giới hạn ${MAX_AMOUNT}`);
    event.preventDefault(); // Ngăn không cho nhập tiếp
  }
};

const handleInput = (event: Event, field: string) => {

  const target = event.target as HTMLInputElement | null;
  if (!target) return;

  let rawValue = target.value;
  let cursorPosition = target.selectionStart || 0; // Lưu vị trí con trỏ trước khi format

  // Kiểm tra nếu toàn bộ văn bản bị bôi đen
  let isAllSelected = target.selectionStart === 0 && target.selectionEnd === rawValue.length;

  // Chỉ giữ lại số
  let numericValue = rawValue.replace(/\D/g, "");
  // console.log(numericValue);
  

  // Nếu nhập sai (chỉ toàn chữ) hoặc bôi đen toàn bộ rồi nhập chữ
  if (numericValue === "") {
    // console.log("Nhập ký tự không hợp lệ!");

    if (isAllSelected) {
      target.value = "";
      modelRef[field] = 0;
    } else {
      target.value = formattedValues.value[field];
    }
    return;
  }
  // console.log("✅ modelRef sau khi cập nhật:", { ...modelRef });

  // Kiểm tra nếu số vượt quá giới hạn
  let numberValue = Number(numericValue);
  if (numberValue > MAX_AMOUNT) {
    console.warn(`🚫 Đã đạt giới hạn ${MAX_AMOUNT}, chặn nhập số tiếp theo!`);
    target.value = formattedValues.value[field]; // Không thay đổi giá trị hiển thị
    return;
  }

  // Cập nhật giá trị hợp lệ vào modelRef
  modelRef[field] = numberValue;

  // Cập nhật tiền thừa: soTienDu = tienKhachDua - tongTien
  if (field === "tienKhachDua" && modelRef.tongTien && modelRef.tienKhachDua) {
    modelRef.soTienDu = Math.max(0, modelRef.tienKhachDua - modelRef.tongTien);
  }

  let formattedValue = formatCurrencyVND(modelRef[field]);

  // Cập nhật lại giá trị input
  target.value = formattedValue;

  // Điều chỉnh lại vị trí con trỏ trước chữ "đ"
  setTimeout(() => {
    let positionBeforeCurrencySymbol = formattedValue.length - 2; // Vị trí trước chữ "đ"
    target.selectionStart = target.selectionEnd = Math.min(cursorPosition, positionBeforeCurrencySymbol);
  }, 0);
};

watch (() => modelRef.tienKhachDua, (newValue) => {
  modelRef.soTienDu = Math.max(0,newValue - modelRef.tongTien);
})

const handleClose = () => {
  emit("handleClose");
  resetFields();
};

const formFields = computed(() => [
  {
    label: "Tổng tiền",
    name: "tongTien",
    component: "a-input",
  },
  {
    label: "Tiền khách đưa",
    name: "tienKhachDua",
    component: "a-input",
    props: { placeholder: "Nhập tiền khách đưa", type: "number" },
  },
  {
    label: "Tiền thừa",
    name: "soTienDu",
    component: "a-input",
  },
  {
    label: "Ghi chú",
    name: "ghiChu",
    component: "a-input",
    props: { placeholder: "Nhập ghi chú" },
  },
  {
    label: "Phương thức thanh toán",
    name: "phuongThucThanhToan",
    component: "a-radio-group",
    props: {
      options: listPaymentMethod.value,
    },
  },
  {
    label: "Mã giao dịch",
    name: "maGiaoDich",
    component: "a-input",
    props: { placeholder: "Nhập mã giao dịch" },
  },
]);
</script>
