<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex justify-between items-center">
      <div class="flex items-center gap-2">
        <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48"/>
        <h3 class="text-2xl m-0">Thông tin nhân viên: </h3>
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
    <div class="col-span-2 md:col-span-5 lg:col-span-2 bg-gray-100 rounded-xl p-5 grid justify-center items-">
      <div class="w-[20rem] h-[20rem]">
        <a-avatar
            shape="vertical"
            :src="userInfo?.profilePicture"
            class="w-full h-full text-center"
        >
        </a-avatar>
      </div>
      <div class="text-center">
        aaaaaaaaaaaaa
      </div>
    </div>
    <div class="col-span-3 md:col-span-5 lg:col-span-3 rounded-xl border-2 shadow-purple-500 shadow-xl">
      <a-form layout="vertical" class="p-5">
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
                format="YYYY-MM-DD HH:mm"
                show-time
                :placeholder="field.placeholder"
            ></a-date-picker>

          </a-form-item>
        </template>
      </a-form>
    </div>

  </div>
</template>

<script lang="ts">
export default {
  name: 'admin staff detail',
};
</script>

<script lang="ts" setup>
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import router from "@/infrastructure/routes/router.ts";
import {computed, reactive} from "vue";
import {useAuthStore} from "@/infrastructure/stores/auth.ts";
import {StaffRequest} from "@/infrastructure/services/api/admin/staff.api.ts";
import {Form} from "ant-design-vue";

const auth = useAuthStore();
const userInfo = computed(() => auth.user);

const handleRedirectStaff = () => {
  router.push(ROUTES_CONSTANTS.ADMIN.path + '/' + ROUTES_CONSTANTS.ADMIN.children.STAFF.path);
}

const modelRef = reactive<StaffRequest>({
  name: null,
  email: null,
  username: null,
  password: null,
  birthday: null,
  gender: null,
  phoneNumber: null,
});

const rulesRef = reactive({
  name: [{required: true, message: "Vui lòng nhập tên nhân viên", trigger: "blur"}],
  email: [{required: true, message: "Vui lòng nhập tên nhân viên", trigger: "blur"}],
  username: [{required: true, message: "Vui lòng nhập tên tài khoản", trigger: "blur"}],
  password: [{required: true, message: "Vui lòng nhập mật khẩu", trigger: "blur"}],
  birthday: [{required: true, message: "Vui lòng nhập ngày sinh", trigger: "blur"}],
  gender: [{required: true, message: "Vui lòng chọn giới tính", trigger: "blur"}],
  phoneNumber: [{required: true, message: "Vui lòng nhập số điện thoại", trigger: "blur"}],
});

const {resetFields, validate, validateInfos} = Form.useForm(
    modelRef,
    rulesRef
);

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
    label: "Tên tài khoản",
    name: "username",
    component: "a-input",
    placeholder: "Nhâp tên tài khoản"
  },
  {
    label: "Mật khẩu",
    name: "password",
    component: "a-input",
    placeholder: "Nhâp mật khẩu"
  },
  {
    label: "Ngày sinh",
    name: "birthday",
    component: "a-date-picker",
    placeholder: "Nhâp ngày sinh"
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
  {
    label: "Số điện thoại",
    name: "phoneNumber",
    component: "a-input",
    placeholder: "Nhâp số điện thoại"
  },
]);


</script>