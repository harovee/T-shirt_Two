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
    <a-form layout="vertical" class="pt-3">
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
  idPhieuGiamGia: null
});

const rulesRef = reactive({
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
    }

    console.log(modelRef);
    
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

const handleUpdateBill = () => {

  emit("update:bill", modelRef);

  // const payload = {
  //   // idKhachHang: modelRef.idKhachHang,
  //   soDienThoai: modelRef.soDienThoai,
  //   diaChiNguoiNhan: modelRef.diaChiNguoiNhan,
  //   tenNguoiNhan: modelRef.tenNguoiNhan,
  //   ghiChu: modelRef.ghiChu,
  // };

  // console.log(payload);

  // Modal.confirm({
  //   content: "Bạn chắc chắn muốn sửa?",
  //   icon: createVNode(ExclamationCircleOutlined),
  //   centered: true,
  //   async onOk() {
  //     try {
  //       await validate();
  //       update(
  //         { idBill: billId, params: payload },
  //         {
  //           onSuccess: (result) => {
  //             successNotiSort("Cập nhật hóa đơn thành công");
  //             emit("updated", result.data);
  //             handleClose();
  //           },
  //           onError: (error: any) => {
  //             errorNotiSort("Cập nhật hóa đơn thất bại");
  //           },
  //         }
  //       );
  //     } catch (error: any) {
  //       console.error("🚀 ~ handleUpdate ~ error:", error);
  //       if (error?.response) {
  //         warningNotiSort(error?.response?.data?.message);
  //       } else if (error?.errorFields) {
  //         warningNotiSort("Vui lòng nhập đúng các trường dữ liệu");
  //       }
  //     }
  //   },
  //   cancelText: "Huỷ",
  //   onCancel() {
  //     Modal.destroyAll();
  //   },
  // });
};

const handleClose = () => {
  emit("handleClose");
  // resetFields();
  if (props.billData) {
    modelRef.soDienThoai = props.billData.soDienThoai || null;
    modelRef.diaChiNguoiNhan = props.billData.diaChiNguoiNhan || null;
    modelRef.tenNguoiNhan = props.billData.tenNguoiNhan || null;
    modelRef.ghiChu = props.billData.ghiChu || null;
  }
};
</script>


