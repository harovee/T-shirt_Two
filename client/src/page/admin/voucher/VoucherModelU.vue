<template>
  <a-modal
      :open="props.open"
      title="Cập nhật phiếu giảm giá"
      @cancel="handleClose"
      @ok="handleUpdateVoucher"
      ok-text="Cập nhật"
      cancel-text="Hủy"
      destroyOnClose
      centered
  >
      <div v-if="props.isLoadingDetail" class="flex justify-center items-center">
                <a-spin />
      </div>
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
import {Form, message, Modal, Upload} from "ant-design-vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import {toast} from "vue3-toastify";
import { useUpdateVoucher} from "@/infrastructure/services/service/admin/voucher/voucher.action.ts";
import {VoucherRequest} from "@/infrastructure/services/api/admin/voucher/voucher.api.ts";
import dayjs from "dayjs";

const props = defineProps({
  open: Boolean,
  VoucherDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allVoucher : Object
});

const emit = defineEmits(["handleClose"]);



const {mutate: update} = useUpdateVoucher();

const modelRef = reactive<VoucherRequest>({
  ten: "",
  loaiGiam: false,
  soLuong: 0,
  dieuKienGiam: "",
  giaTriGiam: "",
  giamToiDa: "",
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
    // { 
    //   validator: (_, value) => {
    //     const now = new Date();
    //     const startDate = new Date(value);
    //     return startDate >= now
    //       ? Promise.resolve()
    //       : Promise.reject("Ngày bắt đầu phải ở tương lai");
    //   },
    //   trigger: "change"
    // }
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

// const modalTitle = computed(() => {
//   props.VoucherDetail ?  "Cập nhật phiếu giảm giá" : "Thêm phiếu giảm giá"
// });

// const okText = computed(()=>{
//   props.VoucherDetail ? "Cập nhật" : "Thêm"
// });


const {resetFields, validate, validateInfos} = Form.useForm(
    modelRef,
    rulesRef
);

const formFields = computed(() => [
  {
    label: "Tên phiếu giảm giá",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên phiếu giảm giá"
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

const handleUpdateVoucher = () => {
  // const payload = {
  //   ten: modelRef.ten,
  //   loaiGiam: modelRef.loaiGiam,
  //   soLuong: modelRef.soLuong,
  //   dieuKienGiam: modelRef.dieuKienGiam ,
  //   giaTriGiam: modelRef.giaTriGiam ,
  //   giamToiDa: modelRef.giamToiDa ,
  //   ngayBatDau: modelRef.ngayBatDau,
  //   ngayKetThuc: modelRef.ngayKetThuc
  // };
  Modal.confirm({
    icon: createVNode(ExclamationCircleOutlined),
    title: "Xác nhận cập nhật phiếu giảm giá",
    content: "Bạn có chắc chắn muốn cập nhật thông tin phiếu giảm giá này không?",
    centered: true,
    async onOk() {
      try {
        await validate();
        update(
          { voucherId: props.VoucherDetail.id,
            data : modelRef,
        },
          {
            onSuccess: () => {
              toast.success("Cập nhật phiếu giảm giá thành công!");
              handleClose();
            },
            onError: (error: any) => {
              toast.error(
                error?.response?.data?.message || "Đã xảy ra lỗi khi cập nhật phiếu giảm giá!"
              );
            },
          }
        );
        console.log(modelRef);
        
      } catch (error: any) {
        // Handle form validation errors
        console.error("🚀 ~ handleUpdateVoucher ~ error:", error);

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

watch(
    () => props.VoucherDetail,
    (newVal) => {
        if (newVal) {
          Object.assign(modelRef, {
                ten: newVal.ten ,
                loaiGiam: newVal.loaiGiam,
                soLuong: newVal.soLuong ?? 0,
                dieuKienGiam: newVal.dieuKienGiam ?? 0,
                giaTriGiam: newVal.giaTriGiam ?? 0,
                giamToiDa: newVal.giamToiDa ?? 0,
                ngayBatDau: dayjs(newVal.ngayBatDau) ?? null,
                ngayKetThuc: dayjs(newVal.ngayKetThuc) ?? null,
              });
        } else {
            resetFields();
        }   
    },
    
    
    { immediate: true }
);

const handleClose = () => {
  emit("handleClose");
  resetFields();
};

// export const convertToAntdDatePicker = (timestamp: string | number): dayjs.Dayjs | null => {
//     if (!timestamp) return null;
//     const date = dayjs(Number(timestamp));
//     return date.isValid() ? date : null;
// };
</script>