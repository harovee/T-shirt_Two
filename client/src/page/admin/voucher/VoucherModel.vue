<template>
  <a-modal
      :open="props.open"
      :title="modalTitle"
      @cancel="handleClose"
      @ok="handleCreateOrUpdateVoucher"
      :ok-text="okText"
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
          ></a-input>
          <a-radio-group
              v-else-if="field.component === 'a-radio-group'"
              :options="field.options"
              v-model:value="modelRef[field.name]"
              :button-style="field.buttonStyle"
              :option-type="field.optionType"
              :disabled="field.disabled"
              :size="field.size">
          </a-radio-group>
          <!--          <a-select-->
          <!--              v-else-if="field.component === 'a-select'"-->
          <!--              :max-tag-count="field.maxTagCount"-->
          <!--              :placeholder="field.placeholder"-->
          <!--              :show-search="field.showSearch"-->
          <!--              :filter-option="field.filterOption"-->
          <!--              :allow-clear="field.allowClear"-->
          <!--              :mode="field.mode"-->
          <!--              :options="field.options"-->
          <!--              v-model:value="modelRef[field.name]"-->
          <!--          ></a-select>-->

                   <a-date-picker
                       class="w-full"
                       v-else-if="field.component === 'a-date-picker'"
                       v-model:value="modelRef[field.name]"
                       format="YYYY-MM-DD HH:mm"
                       show-time
                       :placeholder="field.placeholder">
                    </a-date-picker>

          <!--          <a-upload-->
          <!--              v-else-if="field.component === 'a-upload'"-->
          <!--              v-bind="field.customProps || {}"-->
          <!--              :max-count="1"-->
          <!--              v-model:value="modelRef[field.name]"-->
          <!--          >-->
          <!--            <a-button class="flex justify-between items-center gap-1">-->
          <!--              <upload-outlined></upload-outlined>-->
          <!--              Tải tệp âm thanh-->
          <!--            </a-button>-->
          <!--          </a-upload>-->

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
import {useCreateVoucher, useUpdateVoucher} from "@/infrastructure/services/service/admin/voucher/voucher.action.ts";
import {VoucherRequest, VoucherResponse} from "@/infrastructure/services/api/admin/voucher/voucher.api.ts";

const props = defineProps({
  open: Boolean,
  VoucherDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allVoucher : Array<VoucherResponse>
});

const emit = defineEmits(["handleClose"]);

const {mutate: create} = useCreateVoucher();

const {mutate: update} = useUpdateVoucher();

const modalTitle = computed(() =>
{
    props.VoucherDetail ? "Cập nhật phiếu giảm giá" : "Thêm phiếu giảm giá";
}
);

const okText = computed(() =>
    props.VoucherDetail ? "Cập nhật" : "Thêm "
);

watch(
    // () => props.VoucherDetail,
    // (newVal) => {
    //     if (newVal) {
    //         Object.assign(modelRef, {
    //             ten: newVal.ten,
    //             loaiGiam: newVal.loaiGiam,
    //             soLuong: newVal.soLuong,
    //             dieuKienGiam: newVal.dieuKienGiam,
    //             giaTriGiam: newVal.giaTriGiam,
    //             giamToiDa: newVal.giamToiDa,
    //             ngayBatDau: newVal.ngayBatDau,
    //             ngayKetThuc: newVal.ngayKetThuc,
    //         });
    //     } else {
    //         resetFields();
    //     }
    // },
    // { immediate: true }
);
const modelRef = reactive<VoucherRequest>({
  ten: null,
  loaiGiam: false,
  soLuong: null,
  dieuKienGiam: null,
  giaTriGiam: null,
  giamToiDa: null,
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
    // { 
      // validator: (_, value) => {
      //   if (props.modelRef.loaiGiam) { // Loại giảm là tiền mặt
      //     return !isNaN(Number(value)) 
      //       ? Promise.resolve() 
      //       : Promise.reject("Giá trị giảm phải là số");
      //   } else { // Loại giảm là %
      //     return value >= 0 && value <= 40 
      //       ? Promise.resolve() 
      //       : Promise.reject("Giá trị giảm % phải nằm trong khoảng 0-40%");
      //   }
      // },
      // trigger: "blur"
    // }
  ],
  giamToiDa: [
    { 
      required: true, 
      message: "Vui lòng nhập giảm tối đa", 
      trigger: "blur" 
    },
    // { 
    //   validator: (_, value) => 
    //     !isNaN(Number(value)) 
    //       ? Promise.resolve() 
    //       : Promise.reject("Giảm tối đa phải là số"),
    //   trigger: "blur"
    // }
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
    optionType: "button",
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
    placeholder: "Nhâp giá trị giảm"
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

const handleCreateOrUpdateVoucher = () => {
  Modal.confirm({
    icon: createVNode(ExclamationCircleOutlined),
    title: props.VoucherDetail ? "Xác nhận cập nhật phiếu giảm giá" : "Xác nhận thêm phiếu giảm giá",
    content: props.VoucherDetail 
      ? "Bạn có chắc chắn muốn cập nhật thông tin phiếu giảm giá này không?" 
      : "Bạn có chắc chắn muốn thêm phiếu giảm giá mới không?",
    centered: true,
    async onOk() {
      try {
        await validate();
        if (props.VoucherDetail) {
          update(modelRef, {
            onSuccess: (result) => {
              toast.success(result?.message || "Cập nhật phiếu giảm giá thành công!");
              handleClose();
            },
            onError: (error: any) => {
              toast.error(
                error?.response?.data?.message || "Đã xảy ra lỗi khi cập nhật phiếu giảm giá!"
              );
            },
          });
        } else {
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
        }
      } catch (error: any) {
        // Xử lý lỗi khi xác thực form thất bại
        console.error("🚀 ~ handleCreateOrUpdateVoucher ~ error:", error);
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

const handleClose = () => {
  emit("handleClose");
  resetFields();
};
</script>