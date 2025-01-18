<template>
  <a-modal
      :open="props.open"
      title="Thêm phiếu giảm giá"
      @cancel="handleClose"
      @ok="handleCreateVoucher"
      ok-text="Thêm"
      cancel-text="Hủy"
      destroyOnClose
      centered
  >
    <a-form layout="vertical" class="pt-3">
      <template v-for="field in formFields">
          <a-form-item
            :label="field.label"
            :name="field.name"
            v-bind="validateInfos[field.name]">
          <a-input
              v-if="field.component === 'a-input'"
              v-model:value="modelRef[field.name]"
              @input="formatCurrency"
          ></a-input>
          <a-radio-group
              v-else-if="field.component === 'a-radio-group'"
              :options="field.options"
              v-model:value="modelRef[field.name]"
              :button-style="field.buttonStyle"
              :option-type="field.optionType"
              >
              
          </a-radio-group>
                   <a-date-picker
                       class="w-full"
                       v-else-if="field.component === 'a-date-picker'"
                       v-model:value="modelRef[field.name]"
                       format="YYYY-MM-DD"
                       show-time
                       :placeholder="field.placeholder">
                    </a-date-picker>
                    
        </a-form-item>
      </template>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import {computed, createVNode, defineEmits, defineProps, reactive, watch} from "vue";
import { Form, message, Modal, Upload} from "ant-design-vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import {toast} from "vue3-toastify";
import {useCreateVoucher} from "@/infrastructure/services/service/admin/voucher/voucher.action.ts";
import {VoucherRequest} from "@/infrastructure/services/api/admin/voucher/voucher.api.ts";
import dayjs from "dayjs";

const props = defineProps({
  open: Boolean,
  VoucherDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allVoucher : Object
});

const emit = defineEmits(["handleClose"]);

const {mutate: create} = useCreateVoucher();

const modelRef = reactive<VoucherRequest>({
  ten: "",
  loaiGiam: false,
  soLuong: 0,
  dieuKienGiam: "0",
  giaTriGiam: "0",
  giamToiDa: "0",
  ngayBatDau: null,
  ngayKetThuc: null,
});

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên voucher", trigger: "blur" },
  ],
  loaiGiam: [
    { required: true, message: "Vui lòng chọn loại giảm", trigger: "blur" },
  ],
  soLuong: [
    { 
      required: true, 
      message: "Vui lòng nhập số lượng", 
      trigger: "blur" 
    },
    { 
      validator: (_, value) => 
        value > 0 
          ? Promise.resolve() 
          : Promise.reject("Số lượng phải lớn hơn 0"),
      trigger: "blur"
    }
  ],
  dieuKienGiam: [
    { 
      required: true, 
      message: "Vui lòng nhập điều kiện giảm", 
      trigger: "blur" 
    },
    { 
      validator: (_, value) => 
        !isNaN(Number(value)) 
          ? Promise.resolve() 
          : Promise.reject("Điều kiện giảm phải là số"),
      trigger: "blur"
    }
  ],
  giaTriGiam: [
    { 
      required: true, 
      message: "Vui lòng nhập giá trị giảm", 
      trigger: "blur" 
    },
    { 
      validator: (_, value) => {
        if (modelRef.loaiGiam) { // Loại giảm là tiền mặt
          return !isNaN(Number(value)) 
            ? Promise.resolve() 
            : Promise.reject("Giá trị giảm phải là số");
        } else { // Loại giảm là %
          return value > 0 && value <= 100 
            ? Promise.resolve() 
            : Promise.reject("Giá trị giảm % phải nằm trong khoảng 1-100%");
        }
      },
      trigger: "blur"
    }
  ],
  giamToiDa: [
    { 
      required: true, 
      message: "Vui lòng nhập giảm tối đa", 
      trigger: "blur" 
    },
    { 
      validator: (_, value) => 
        !isNaN(Number(value)) 
          ? Promise.resolve() 
          : Promise.reject("Giảm tối đa phải là số"),
      trigger: "blur"
    }
  ],
  ngayBatDau: [
    { 
      required: true, 
      message: "Vui lòng chọn ngày bắt đầu", 
      trigger: "change" 
    },
    { 
      validator: (_, value) => {
        const now = new Date();
        const startDate = new Date(value);
        return startDate >= now
          ? Promise.resolve()
          : Promise.reject("Ngày bắt đầu phải ở tương lai");
      },
      trigger: "change"
    }
  ],
  ngayKetThuc: [
    { 
      required: true, 
      message: "Vui lòng chọn ngày kết thúc", 
      trigger: "change" 
    },
    { 
      validator: (_, value) => {
        const startDate = new Date(modelRef.ngayBatDau);
        const endDate = new Date(value);
        return endDate >= startDate
          ? Promise.resolve()
          : Promise.reject("Ngày kết thúc phải sau ngày bắt đầu");
      },
      trigger: "change"
    }
  ],
});


const {resetFields, validate, validateInfos} = Form.useForm(
    modelRef,
    rulesRef
);

const formFields = computed(() => [
  {
    label: "Tên voucher",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên Voucher"
  },
  {
    label: "Loại giảm",
    name: "loaiGiam",
    component: "a-radio-group",
    options: [
      { label: "Tiền", value: true },
      { label: "%", value: false },
    ],
    buttonStyle: "solid",
    optionType: "radio",
  },
  {
    label: "Số lượng",
    name: "soLuong",
    component: "a-input",
    placeholder: "Nhâp số lượng"
  },
  {
    label: "Đơn tối thiểu",
    name: "dieuKienGiam",
    component: "a-input",
    placeholder: "Nhâp đơn tối thiểu"
  },
  {
    label: "Giá trị giảm",
    name: "giaTriGiam",
    component: "a-input",
    placeholder: "Nhâp giá trị giảm",
  },
  {
    label: "Giảm tối đa",
    name: "giamToiDa",
    component: "a-input",
    placeholder: "Nhâp giá trị giảm tối đa"
  },
  {
    label: "Ngày bắt đầu",
    name: "ngayBatDau",
    component: "a-date-picker",
  },
  {
    label: "Ngày kết thúc",
    name: "ngayKetThuc",
    component: "a-date-picker",
  },
]);

const handleCreateVoucher = () => {
  Modal.confirm({
    icon: createVNode(ExclamationCircleOutlined),
    title:  "Xác nhận thêm phiếu giảm giá",
    content:  "Bạn có chắc chắn muốn thêm phiếu giảm giá mới không?",
    centered: true,
    async onOk() {
      try {
        await validate();
          create(modelRef, {
            onSuccess: (result) => {
              toast.success(result?.message || "Thêm phiếu giảm giá thành công!");
              handleClose();
            },
            onError: (error: any) => {
              toast.error(
                error?.response?.data?.message || "Đã xảy ra lỗi khi thêm phiếu giảm giá!"
              );
            },
          });
      } catch (error: any) {
        // Xử lý lỗi khi xác thực form thất bại
        console.error("🚀 ~ handleCreateVoucher ~ error:", error);
        if (error?.errorFields) {
          toast.warning("Vui lòng nhập đầy đủ các trường dữ liệu bắt buộc!");
        } else {
          toast.warning("Đã xảy ra lỗi không xác định!");
        }
      }
    },
    cancelText: "Hủy",
    onCancel() {
      Modal.destroyAll();
      resetFields();
    },
  });
};
// Hàm định dạng tiền tệ
const formatCurrency = (value: string | number) => {
  const numericValue = value.toString().replace(/[^0-9]/g, ""); // Loại bỏ ký tự không phải số
  return numericValue 
    ? numericValue.replace(/\B(?=(\d{3})+(?!\d))/g, ",") // Thêm dấu phẩy
    : "";
};
    
const handleClose = () => {
  emit("handleClose");
  resetFields();
};
</script>