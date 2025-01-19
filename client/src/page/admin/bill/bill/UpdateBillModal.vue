<template>
  <a-modal
    :open="props.open"
    title="Thông tin hóa đơn"
    @cancel="handleClose"
    ok-text="Cập nhật"
    cancel-text="Hủy"
    destroyOnClose
    centered
  >
    <a-form layout="vertical" class="pt-3">
      <template v-for="field in formFields">
        <a-form-item
          :label="field.label"
          :name="field.name"
          v-bind="validateInfos[field.name]"
        >
          <a-input
            v-if="field.component === 'a-input'"
            v-model:value="modelRef[field.name]"
            :placeholder="field.placeholder"
          ></a-input>

        </a-form-item>
      </template>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import { BillRequest } from "@/infrastructure/services/api/admin/bill.api";
import { useUpdateBill } from "@/infrastructure/services/service/admin/bill.action";
import { Form, Modal } from "ant-design-vue";
import { computed, createVNode, defineEmits, reactive } from "vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { toast } from "vue3-toastify";

const props = defineProps({
  open: Boolean,
});

const emit = defineEmits(["handleClose"]);

const { mutate: update } = useUpdateBill();

const modelRef = reactive<BillRequest>({
  soDienThoai: null,
  diaChiNguoiNhan: null,
  idKhachHang: null,
  tenNguoiNhan: null,
  ghiChu: null,
});

const { resetFields, validate, validateInfos } = Form.useForm(modelRef);

const formFields = computed(() => [
  {
    label: "Tên khách hàng",
    name: "name",
    component: "a-input",
    placeholder: "Nhâp tên khách hàng",
  },
  {
    label: "Số điện thoại",
    name: "soDienThoai",
    component: "a-input",
    placeholder: "Nhâp số điện thoại",
  },
  {
    label: "Tên người nhận",
    name: "tenNguoiNhan",
    component: "a-input",
    placeholder: "Nhâp tên người nhận",
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

const handleUpdateBill = () => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn sửa?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await validate();
        update(modelRef, {
          onSuccess: (result) => {
            toast.success(result?.message);
            handleClose();
          },
          onError: (error: any) => {
            toast.error(error?.response?.data?.message);
          },
        });
      } catch (error: any) {
        console.error("🚀 ~ handleUpdate ~ error:", error);
        if (error?.response) {
          toast.warning(error?.response?.data?.message);
        } else if (error?.errorFields) {
          toast.warning("Vui lòng nhập đầy đủ các trường dữ liệu");
        }
      }
    },
    cancelText: "Huỷ",
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
