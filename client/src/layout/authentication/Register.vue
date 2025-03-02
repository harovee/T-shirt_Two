<template>
  <div class="text-center flex flex-col gap-5">
    <span class="text-base text-gray-500">Đăng ký tài khoản mới</span>
    <a-form layout="vertical" class="text-left text-xs flex flex-col gap-5">
      <a-form-item
        class="m-0"
        v-for="field in formFields"
        :key="field.name"
        :name="field.name"
        v-bind="validateInfos[field.name]"
      >
        <component
          class="h-12 rounded-2 text-xs"
          :is="field.component"
          v-bind="field.props"
          v-model:value="modelRef[field.name]"
        />
      </a-form-item>
    </a-form>

    <a-button
      @click="handleSubmitRegister()"
      html-type="submit"
      :disabled="isRegisterPass"
      class="h-12 w-full rounded-2xl bg-blue-700 text-white hover:bg-blue-900 hover:text-white cursor-pointer text-xs"
    >
      Đăng ký
    </a-button>

    <span class="text-gray-500 cursor-default text-xs">Bạn đã có tài khoản?</span>

<div class="flex justify-center items-center flex-col">
  <button
      @click="router.push(ROUTES_CONSTANTS.AUTHENTICATION.children.LOGIN.name)"
      type="button"
      class="py-3.5 border-2 rounded-2xl w-full text-xs text-gray-900"
  >
    Trở về trang đăng nhập
  </button>
</div>
  </div>
</template>

<script lang="ts" setup>
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import router from "@/infrastructure/routes/router.ts";
import { URL_FRONTEND } from "@/infrastructure/constants/url";
import { RegisterForm } from "@/infrastructure/services/api/authentication/authentication.api";
import { useRegister } from "@/infrastructure/services/service/authentication/authentication.action";
import {
  errorNotiSort,
  successNotiSort,
  warningNotiSort,
} from "@/utils/notification.config";
import { Form, Modal } from "ant-design-vue";
import { computed, reactive, ref } from "vue";
import { toast } from "vue3-toastify";

let isRegisterPass = ref(false);

const { mutate: register } = useRegister();

const modelRef = reactive<RegisterForm>({
  fullName: "",
  email: "",
  phoneNumber: "",
  password: "",
  rePassword: "",
});

const rulesRef = reactive({
  fullName: [
    {
      required: true,
      validator: (_, value) =>
        value !== null && value.trim() !== ""
          ? Promise.resolve()
          : Promise.reject("Tên không được để trống"),
      trigger: ["blur", "change"],
    },
    { max: 50, message: "Tên không được dài quá 50 ký tự", trigger: "blur" },
  ],
  email: [
    { required: true, message: "Vui lòng nhập email", trigger: "blur" },
    {
      pattern: /^[a-zA-Z0-9._%+-]+@(gmail\.com)$/,
      message: "Email không hợp lệ (chỉ chấp nhận @gmail.com)",
      trigger: ["blur", "change"],
    },
    { max: 50, message: "Email không được dài quá 50 ký tự", trigger: "blur" },
  ],
  phoneNumber: [
    { required: true, message: "Vui lòng nhập số điện thoại", trigger: "blur" },
    {
      pattern: /^0[1-9]\d{8,9}$/,
      message: "Số điện thoại phải bắt đầu bằng số 0 và có 10-11 chữ số.",
      trigger: ["blur", "change"],
    },
  ],
  password: [
    { required: true, message: "Vui lòng nhập mật khẩu", trigger: "blur" },
    {
      pattern: /[A-Z]/,
      message: "Mật khẩu phải có ít nhất 1 ký tự viết hoa",
      trigger: ["blur", "change"],
    },
    {
      pattern: /\W/,
      message: "Mật khẩu phải có ít nhất 1 ký tự đặc biệt",
      trigger: ["blur", "change"],
    },
    { min: 8, message: "Mật khẩu phải có độ dài từ 8 ký tự trở lên" },
    { max: 20, message: "Mật khẩu phải có độ dài từ 20 ký tự trở xuống" },
  ],
  rePassword: [
    { required: true, message: "Vui lòng nhập mật khẩu", trigger: ["blur", "change"] },
    {
      validator: (_, value) => {
        if (value !== modelRef.password) {
          return Promise.reject("Mật khẩu nhập lại không khớp");
        }
        return Promise.resolve();
      },
      trigger: "blur",
    },
  ],
});

const { validate, validateInfos } = Form.useForm(modelRef, rulesRef);

const handleSubmitRegister = async () => {
  try {
    await validate();
    isRegisterPass.value = true;
    console.log(modelRef);

    setTimeout(() => {
      register(modelRef, {
        onSuccess: (result) => {
          isRegisterPass.value = false;
          if (result?.data) {
            successNotiSort("Đăng ký thành công");

            // Hiển thị hộp thoại xác nhận
            Modal.confirm({
              title: "Xác nhận",
              content: "Bạn có muốn đăng nhập bằng tài khoản vừa tạo không?",
              okText: "Đồng ý",
              cancelText: "Hủy",
              onOk() {
                window.location.href = `${URL_FRONTEND}?state=${result.data}`;
              },
            });
          }
          if (result?.response?.data?.message) {
            warningNotiSort(result?.response?.data?.message);
          }
        },
        onError: (error) => {
          isRegisterPass.value = false;
          console.error("Đã xảy ra lỗi khi đăng ký:", error);
          errorNotiSort(error?.response?.data?.message || "Đăng ký thất bại!");
        },
      });
    }, 500);
  } catch (error: any) {
    isRegisterPass.value = false;
    console.error("🚀 ~ onFinish register ~ error:", error);
    if (error.errorFields) {
      return;
    }
    toast.error(error?.response?.data?.message || "Đăng ký thất bại!");
  }
};

const formFields = computed(() => [
  {
    label: "Tên người dùng",
    name: "fullName",
    type: "string",
    component: "a-input",
    props: { placeholder: "Họ tên người dùng" },
  },
  {
    label: "Email",
    name: "email",
    type: "string",
    component: "a-input",
    props: { placeholder: "Email (đuôi gmail).." },
  },
  {
    label: "Số điện thoại",
    name: "phoneNumber",
    type: "number",
    component: "a-input",
    props: { placeholder: "Số điện thoại" },
  },
  {
    label: "Mật khẩu",
    name: "password",
    type: "string",
    component: "a-input-password",
    props: { placeholder: "Mật khẩu" },
  },
  {
    label: "Xác nhận mật khẩu",
    name: "rePassword",
    type: "string",
    component: "a-input-password",
    props: { placeholder: "Xác nhận mật khẩu" },
  },
]);
</script>
