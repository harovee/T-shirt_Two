<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex justify-between items-center">
      <div class="flex items-center gap-2">
        <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48"/>
        <h3 class="text-2xl m-0">Thông tin nhân viên</h3>
      </div>
      <div class="flex items-center gap-2 scale-75 transition-all cursor-pointer
                  hover:scale-100 hover:text-red-500"
           @click="handleRedirectStaff()">
        <v-icon name="gi-fast-backward-button" size="x-large" width="48" height="48"/>
        <h3 class="text-2xl m-0">Quay lại</h3>
      </div>
    </div>
  </div>
  <div class="p-6 grid grid-cols-5 gap-6">
    <div class="col-span-2 md:col-span-5 p-5 lg:col-span-2 rounded-xl border-2 shadow-xl">
      <div class="w-full grid justify-center">
        <div class="w-[20rem] h-[20rem]">
          <a-tooltip
              title="Tải Ảnh Lên"
              trigger="hover"
          >
            <a-avatar
                @click="openWidget()"
                shape="circle"
                :src="modelRef['picture']"
                class="w-full h-full text-center scale-95 hover:scale-100 transition-all cursor-pointer"
            >
            </a-avatar>
          </a-tooltip>
        </div>
      </div>
    </div>
    <div class="col-span-3 md:col-span-5 p-5 lg:col-span-3 rounded-xl border-2 shadow-xl h-fit">
      <a-form layout="vertical" class="grid grid-cols-2 gap-4">
        <template class="col-span-1 md:col-span-1 lg:col-span-1" v-for="field in formFields">
          <a-form-item
              :label="field.label"
              :name="field.name"
              v-bind="validateInfos[field.name]"
          >
            <a-input
                v-if="field.component === 'a-input'"
                v-model:value="modelRef[field.name]"
                :placeholder="field.placeholder"
                :type="field.type"
            ></a-input>

            <a-input-password
                v-if="field.component === 'a-input-password'"
                v-model:value="modelRef[field.name]"
                :placeholder="field.placeholder"
                :type="field.type"
            ></a-input-password>

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
      <div class="flex justify-end gap-4">
        <a-button @click="handleReset()">Đặt lại</a-button>
        <a-button type="primary" @click="handleCreateStaff()">Thêm mới</a-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: 'admin staff create',
};
</script>

<script lang="ts" setup>
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import {computed, createVNode, reactive, ref} from "vue";
import {StaffRequest} from "@/infrastructure/services/api/admin/staff.api.ts";
import {Form, Modal, notification} from "ant-design-vue";
import {
  useCreateStaff
} from "@/infrastructure/services/service/admin/staff.action.ts";
import router from "@/infrastructure/routes/router.ts";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import {CLOUDINARY_CLOUD_NAME, CLOUDINARY_UPLOAD_PRESET} from "@/infrastructure/constants/cloudinary.ts";
import dayjs from "dayjs";

const handleRedirectStaff = () => {
  router.push(ROUTES_CONSTANTS.ADMIN.path + '/' + ROUTES_CONSTANTS.ADMIN.children.STAFF.path);
}

const modelRef = reactive<StaffRequest>({
  name: "",
  email: "",
  password: "",
  birthday: "",
  gender: null,
  phoneNumber: "",
  identity: "",
  picture: "",
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

const formFields = computed(() => [
  {
    label: "Tên nhân viên",
    name: "name",
    type: "string",
    component: "a-input",
    placeholder: "Nhâp tên nhân viên"
  },
  {
    label: "Email",
    name: "email",
    type: "string",
    component: "a-input",
    placeholder: "Nhâp email"
  },
  {
    label: "Mã định danh cá nhân",
    name: "identity",
    type: "number",
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
    type: "number",
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

// * HandleUpload * \\
const myWidget = cloudinary.createUploadWidget(
    {
      cloudName: CLOUDINARY_CLOUD_NAME,
      uploadPreset: CLOUDINARY_UPLOAD_PRESET,
    },
    (error: any, result: any) => {
      if (!error && result && result.event === "success") {
        modelRef.picture = result.info.url;
      }
    }
);

const openWidget = () => {
  myWidget.open();
}

// * HandleForm * \\
const {mutate: create} = useCreateStaff();

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
            handleRedirectStaff();
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
}

const handleReset = () => {
  resetFields();
}

// * Sta * \\
const presets = ref([
  {label: '18 Years Ago', value: dayjs().subtract(18, 'year')},
]);

</script>