<template>
  <a-modal
      :open="props.open"
      title="Thông tin nhân viên"
      @cancel="handleClose"
      @ok="handleCreateStaff"
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
            v-bind="validateInfos[field.name]"
        >
          <a-input
              v-if="field.component === 'a-input'"
              v-model:value="modelRef[field.name]"
              :placeholder="field.placeholder"
          ></a-input>

          <a-radio-group
              v-if="field.component === 'a-radio-group'"
              v-for="option in field.options"
              v-model:value="modelRef[field.name]"
          >
            <a-radio :value="option.value">
              {{ option.name }}
            </a-radio>
          </a-radio-group>

          <a-date-picker
              class="w-full"
              v-else-if="field.component === 'a-date-picker'"
              v-model:value="modelRef[field.name]"
              :format="field.format"
              :presets="field.presets"
              show-time
              :placeholder="field.placeholder"
          ></a-date-picker>

        </a-form-item>
      </template>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import {computed, createVNode, defineEmits, defineProps, reactive, ref} from "vue";
import {Form, Modal, notification} from "ant-design-vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import {useCreateStaff} from "@/infrastructure/services/service/admin/staff.action.ts";
import {StaffRequest} from "@/infrastructure/services/api/admin/staff.api.ts";
import dayjs from "dayjs";

const props = defineProps({
  open: Boolean,
});

const emit = defineEmits(["handleClose"]);

const {mutate: create} = useCreateStaff();

const modelRef = reactive<StaffRequest>({
  name: null,
  email: null,
  password: null,
  birthday: null,
  gender: null,
  phoneNumber: null,
  identity: null,
  picture: null,
});

const rulesRef = reactive({
  name: [
    {
      required: true,
      validator: (_, value) => value !== null && value.trim() !== "" ? Promise.resolve() : Promise.reject("Tên không được để trống"),
      trigger: "blur"
    },
    {max: 50, message: "Tên không được dài quá 50 ký tự", trigger: "blur"},
  ],
  email: [
    {required: true, message: "Vui lòng nhập email", trigger: "blur"},
    {
      pattern: /^[a-zA-Z0-9._%+-]+@(gmail\.com)$/,
      message: "Email không hợp lệ (chỉ chấp nhận @gmail.com)",
      trigger: "blur"
    },
    {max: 50, message: "Email không được dài quá 50 ký tự", trigger: "blur"},
  ],
  birthday: [
    {required: true, message: "Vui lòng nhập ngày sinh", trigger: "blur"},
    {
      validator: (_, value) => {
        const birthDate = new Date(value);
        const today = new Date();
        const age = today.getFullYear() - birthDate.getFullYear();

        const hasHadBirthdayThisYear =
            today.getMonth() > birthDate.getMonth() ||
            (today.getMonth() === birthDate.getMonth() &&
                today.getDate() >= birthDate.getDate());

        const actualAge = hasHadBirthdayThisYear ? age : age - 1;

        return actualAge >= 18
            ? Promise.resolve()
            : Promise.reject("Ngày sinh phải đủ 18 tuổi");
      },
      trigger: "blur",
    },
  ],
  gender: [
    {required: true, message: "Vui lòng chọn giới tính", trigger: "blur"},
  ],
  phoneNumber: [
    {required: true, message: "Vui lòng nhập số điện thoại", trigger: "blur"},
    {
      pattern: /^0[1-9]\d{8,9}$/,
      message: "Số điện thoại phải bắt đầu bằng số 0 và có 10-11 chữ số.",
      trigger: "blur"
    },
  ],
  identity: [
    {required: true, message: "Vui lòng nhập mã căn cước công dân", trigger: "blur"},
    {
      pattern: /^\d{12}$/,
      message: "Mã căn cước công dân không hợp lệ. CCCD phải bao gồm 12 chữ số.",
      trigger: "blur"
    },
  ],
});

const {resetFields, validate, validateInfos} = Form.useForm(
    modelRef,
    rulesRef
);

const presets = ref([
  {label: '18 Years Ago', value: dayjs().subtract(18, 'year')},
]);

const formFields = computed(() => [
  {
    label: "Tên nhân viên",
    name: "name",
    component: "a-input",
    placeholder: "Nhâp tên nhân viên"
  },
  {
    label: "Email",
    name: "email",
    component: "a-input",
    placeholder: "Nhâp email"
  },
  {
    label: "Mã định danh cá nhân",
    name: "identity",
    component: "a-input",
    placeholder: "Nhâp mã căn cước công dân"
  },
  {
    label: "Ngày sinh",
    name: "birthday",
    component: "a-date-picker",
    placeholder: "Nhâp ngày sinh",
    format: 'DD-MM-YYYY',
    presets: presets.value,
  },
  {
    label: "Số điện thoại",
    name: "phoneNumber",
    component: "a-input",
    placeholder: "Nhâp số điện thoại"
  },
  {
    label: "Giới tính",
    name: "gender",
    component: "a-radio-group",
    options: [
      {
        name: "Nam",
        value: true,
      },
      {
        name: "Nữ",
        value: false,
      }
    ]
  },
]);

const handleCreateStaff = () => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn thêm?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await validate();
        create(modelRef, {
          onSuccess: (res) => {
            notification.success({
              message: 'Thông báo',
              description: res?.message,
              duration: 4,
            });
            handleClose();
          },
          onError: (error: any) => {
            notification.error({
              message: 'Thông báo',
              description: error?.response?.data?.message,
              duration: 4,
            });
          },
        });
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          notification.warning({
            message: 'Thông báo',
            description: error?.response?.data?.message,
            duration: 4,
          });
        } else if (error?.errorFields) {
          notification.warning({
            message: 'Thông báo',
            description: 'Vui lòng nhập đúng đủ các trường dữ liệu',
            duration: 4,
          });
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