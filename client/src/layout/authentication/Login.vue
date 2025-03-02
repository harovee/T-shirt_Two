<template>
  <div class="text-center flex flex-col gap-5">
    <a-form layout="vertical" class="text-left text-xs flex flex-col gap-5">
      <a-form-item
          class="m-0"
          v-for="field in formFields"
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

    <router-link
        :to="ROUTES_CONSTANTS.AUTHENTICATION.children.FORGOT_PASSWORD.path"
        class="text-gray-500 hover:text-gray-500 cursor-pointer text-xs"
    >
      Quên mật khẩu
    </router-link>

    <a-button
        @click="handleSubmitLogin()"
        html-type="submit"
        :disabled="isLoginPass"
        class="h-12 w-full rounded-2xl bg-blue-700 text-white hover:bg-blue-900 hover:text-white cursor-pointer text-xs"
    >
      Đăng nhập
    </a-button>


    <span class="text-gray-500 cursor-default text-xs">Bạn chưa có tài khoản?</span>

    <div class="flex justify-center items-center flex-col">
      <button
          @click="router.push(ROUTES_CONSTANTS.AUTHENTICATION.children.REGISTER.name)"
          type="button"
          class="py-3.5 border-2 rounded-2xl w-full text-xs text-gray-900"
      >
        Đăng ký miễn phí
      </button>
    </div>

    <span class="text-gray-500 cursor-default text-xs">
      Or sign in with
    </span>

    <div class="flex justify-center items-center gap-4">
      <button
          @click="handleLoginGoogle"
          type="button"
          class="p-2 border-2 rounded-2xl text-gray-900"
          :disabled="isLoginGoogle"
      >
        <v-icon width="40" height="40" name="fc-google"/>
      </button>
      <!--      <button-->
      <!--          @click="handleLoginGithub"-->
      <!--          type="button"-->
      <!--          class="p-2 border-2 rounded-2xl text-gray-900"-->
      <!--          :disabled="isLoginGithub"-->
      <!--      >-->
      <!--        <v-icon width="40" height="40" name="vi-folder-type-github-opened"/>-->
      <!--      </button>-->
      <!--      <button-->
      <!--          @click="handleLoginFacebook"-->
      <!--          type="button"-->
      <!--          class="p-2 border-2 rounded-2xl text-gray-900"-->
      <!--          :disabled="isLoginFacebook"-->
      <!--      >-->
      <!--        <v-icon width="40" height="40" size="x-large" name="md-facebook" class="text-blue-800"/>-->
      <!--      </button>-->
    </div>
  </div>
</template>

<script lang="ts" setup>
import {URL_FRONTEND, URL_OAUTH2_FACEBOOK, URL_OAUTH2_GITHUB, URL_OAUTH2_GOOGLE} from "@/infrastructure/constants/url";
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import router from "@/infrastructure/routes/router.ts";
import {computed, reactive, ref} from "vue";
import {Form} from "ant-design-vue";
import {toast} from "vue3-toastify";
import {LoginForm} from "@/infrastructure/services/api/authentication/authentication.api.ts";
import {useLogin} from "@/infrastructure/services/service/authentication/authentication.action.ts";

let isLoginPass = ref(false);
let isLoginGoogle = ref(false);
let isLoginGithub = ref(false);
let isLoginFacebook = ref(false);

const {mutate: login} = useLogin();

const modelRef = reactive<LoginForm>({
  email: "",
  password: "",
})

const rulesRef = reactive({
  email: [
    {required: true, message: "Vui lòng nhập email", trigger: "blur"},
    {
      pattern: /^[a-zA-Z0-9._%+-]+@(gmail\.com)$/,
      message: "Email không hợp lệ (chỉ chấp nhận @gmail.com)",
      trigger: "blur"
    },
    {max: 50, message: "Email không được dài quá 50 ký tự", trigger: "blur"},
  ],
  password: [
    {required: true, message: "Vui lòng nhập mật khẩu", trigger: "blur"},
    {
      pattern: /[A-Z]/,
      message: "Mật khẩu phải có ít nhất 1 ký tự viết hoa",
      trigger: "blur"
    },
    {
      pattern: /\W/,
      message: "Mật khẩu phải có ít nhất 1 ký tự đặc biệt",
      trigger: "blur"
    },
    {min: 8, message: "Mật khẩu phải có độ dài từ 8 ký tự trở lên"},
    {max: 20, message: "Mật khẩu phải có độ dài từ 20 ký tự trở xuống"}
  ],
})

const {validate, validateInfos} = Form.useForm(
    modelRef,
    rulesRef
);

const formFields = computed(() => [
  {
    label: "Email",
    name: "email",
    component: "a-input",
    props: {placeholder: "Email / Số điện thoại"},
  },
  {
    label: "Password",
    name: "password",
    component: "a-input-password",
    props: {placeholder: "Mật khẩu"},
  },
]);

const handleSubmitLogin = async () => {
  try {
    await validate();
    isLoginPass.value = true;

    setTimeout(() => {
      login(modelRef, {
        onSuccess: (result) => {
          isLoginPass.value = false;
          if (result?.data) {
            window.location.href = `${URL_FRONTEND}?state=${result.data}`;
          }
          if (result?.response?.data?.message) {
            toast.warning(result?.response?.data?.message);
          }
        },
        onError: (error) => {
          isLoginPass.value = false;
          console.error("Đã xảy ra lỗi khi đăng nhập:", error);
          toast.error(error?.response?.data?.message || "Đăng nhập thất bại!");
        },
      });
    }, 500);
  } catch (error: any) {
    isLoginPass.value = false;
    console.error("🚀 ~ onFinish login ~ error:", error);
    if (error.errorFields) {
      return;
    }
    toast.error(error?.response?.data?.message || "Đăng nhập thất bại!");
  }
};

const handleLoginGoogle = () => {
  console.log(
      "URL_OAUTH2_GOOGLE + URL_FRONTEND",
      URL_OAUTH2_GOOGLE + URL_FRONTEND
  );
  window.location.href = URL_OAUTH2_GOOGLE + URL_FRONTEND;
};

const handleLoginGithub = () => {
  console.log(
      "URL_OAUTH2_GITHUB + URL_FRONTEND",
      URL_OAUTH2_GITHUB + URL_FRONTEND
  );
  window.location.href = URL_OAUTH2_GITHUB + URL_FRONTEND;
};

const handleLoginFacebook = () => {
  console.log(
      "URL_OAUTH2_FACEBOOK + URL_FRONTEND",
      URL_OAUTH2_FACEBOOK + URL_FRONTEND
  );
  toast.warning("Facebook login is not supported");
  // window.location.href = URL_OAUTH2_FACEBOOK + URL_FRONTEND;
};
</script>
