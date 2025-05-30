<template>
  <a-modal
    :open="props.open"
    title="Cập nhật hóa đơn "
    @cancel="handleClose"
    ok-text="Cập nhật"
    @ok="handleUpdateBill"
    cancel-text="Hủy"
    destroyOnClose
    centered
    width="700px"
  >
  <a-form layout="vertical" class="pt-3" :model="modelRef" :rules="rulesRef" validateTrigger="blur">
      <template v-for="field in formFields" :key="field.name">
        <a-form-item
          v-if="field.name === 'diaChiNguoiNhan'"
          :label="field.label"
          :name="field.name"
          v-bind="validateInfos[field.name]"
        >
          <div class="flex items-center space-x-2">
            <a-input
              v-model:value="modelRef[field.name]"
              :placeholder="field.placeholder"
              readonly
            />
            <a-tooltip title="Chọn phiếu giảm giá" trigger="hover">
              <a-button
                class="bg-purple-300 flex items-center gap-2"
                @click="openAddressModal"
              >
                <v-icon name="md-modeeditoutline" />
              </a-button>
            </a-tooltip>
          </div>
          <customer-address-modal
            :open="isOpenModalAddress"
            :dataBill="modelRef"
            @onCancel="isOpenModalAddress = false"
            @handleClose="handleCloseModalAddress"
            @handleGetAddress="handleChangeAddress"
        />
        </a-form-item>
        

        <!-- Các field khác vẫn bình thường -->
        <a-form-item
          v-else
          :label="field.label"
          :name="field.name"
          v-bind="validateInfos[field.name]"
        >
          <a-input
            v-if="field.component === 'a-input'"
            v-model:value="modelRef[field.name]"
            :placeholder="field.placeholder"
          />
        </a-form-item>
      </template>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import { BillRequest } from "@/infrastructure/services/api/admin/bill.api";
import { useUpdateBill } from "@/infrastructure/services/service/admin/bill.action";
import { Form, Modal } from "ant-design-vue";
import { computed, createVNode, defineEmits, reactive, watch, ref } from "vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import {
  errorNotiSort,
  successNotiSort,
  warningNotiSort,
} from "@/utils/notification.config";
import 
  CustomerAddressModal
 from "./CustomerAddressModal.vue";

const props = defineProps({
  open: Boolean,
  billData: {
    type: Object as () => Record<string, any> | null,
    required: false,
    default: null,
  },
});

const isOpenModalAddress = ref(false);

const openAddressModal = () => {
  isOpenModalAddress.value = true;
};

const handleCloseModalAddress = () => {
  isOpenModalAddress.value = false;
};

const emit = defineEmits(["handleClose", "updated", "update:bill"]);

const { mutate: update } = useUpdateBill();

const modelRef = reactive<BillRequest>({
  soDienThoai: null,
  diaChiNguoiNhan: null,
  // idKhachHang: null,
  tenNguoiNhan: null,
  ghiChu: null,
  tinh: null,
  huyen: null,
  xa: null,
  idPhieuGiamGia: null,
  nhanVien: null
});

const rulesRef = reactive({
  ghiChu: [
    {
      max: 255,
      message: "Ghi chú không được vượt quá 255 ký tự",
      trigger: "blur",
    },
  ],
  soDienThoai: [
    { 
      required: true, 
      message: "Vui lòng nhập số điện thoại", 
      trigger: ["blur", "change"] // Thêm trigger 'change'
    },
    {
      pattern: /^0[1-9]\d{8,9}$/,
      message: "Số điện thoại phải bắt đầu bằng số 0 và có 10-11 chữ số.",
      trigger: ["blur", "change"] // Thêm trigger 'change'
    },
  ],
  tenNguoiNhan: [
    {
      required: true,
      validator: (_, value) => value !== null && value.trim() !== "" 
        ? Promise.resolve() 
        : Promise.reject("Tên không được để trống"),
      trigger: ["blur", "change"] // Thêm trigger 'change'
    },
    {
      max: 50, 
      message: "Tên không được dài quá 50 ký tự", 
      trigger: ["blur", "change"] // Thêm trigger 'change'
    },
  ],
});

const { resetFields, validate, validateInfos, clearValidate } = Form.useForm(
  modelRef,
  rulesRef
);

watch(
  () => props.billData,
  (newBillData) => {
    if (newBillData) {
      modelRef.soDienThoai = newBillData.soDienThoai || null;
      modelRef.diaChiNguoiNhan = newBillData.diaChiNguoiNhan || null;
      // modelRef.idKhachHang = newBillData.idKhachHang || null;
      modelRef.tenNguoiNhan = newBillData.tenNguoiNhan || null;
      modelRef.ghiChu = newBillData.ghiChu || null;
      modelRef.tinh = newBillData.tinh || null;
      modelRef.huyen = newBillData.huyen || null;
      modelRef.xa = newBillData.xa || null;
      clearValidate();

    }

    // console.log(modelRef);
    
  },
  // { immediate: true } // Theo dõi ngay khi component mount
);

const formFields = computed(() => [
  {
    label: "Tên người nhận",
    name: "tenNguoiNhan",
    component: "a-input",
    placeholder: "Nhâp tên người nhận",
  },
  {
    label: "Số điện thoại",
    name: "soDienThoai",
    component: "a-input",
    placeholder: "Nhâp số điện thoại",
  },
  {
    label: "Địa chỉ người nhận",
    name: "diaChiNguoiNhan",
    component: "a-input",
    placeholder: "Nhâp địa chỉ người nhận",
  },
  {
    label: "Ghi chú",
    name: "ghiChu",
    component: "a-input",
    placeholder: "Nhâp ghi chú",
  },
]);

const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

const handleChangeAddress = (fullAddress: string, modelRefAdd: any) => {
    modelRef.diaChiNguoiNhan = fullAddress;
    modelRef.tinh = modelRefAdd.province;
    modelRef.huyen = modelRefAdd.district;
    modelRef.xa = modelRefAdd.ward;
}

const billId = getIdHoaDonFromUrl();
// console.log(billId);

const handleUpdateBill = async () => {
  try {
    await validate();
    emit("update:bill", modelRef);
    console.log('updated');
    
  } catch (err) {
    console.warn("Validation failed", err);
  }
};

const handleClose = () => {
  emit("handleClose");
  clearValidate();
  if (props.billData) {
    modelRef.soDienThoai = props.billData.soDienThoai || null;
    modelRef.diaChiNguoiNhan = props.billData.diaChiNguoiNhan || null;
    modelRef.tenNguoiNhan = props.billData.tenNguoiNhan || null;
    modelRef.ghiChu = props.billData.ghiChu || null;
  }
};
</script>


