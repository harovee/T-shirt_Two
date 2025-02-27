<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex justify-between items-center">
      <div class="flex items-center gap-2">
        <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48"/>
        <h3 class="text-2xl m-0">Thông tin khách hàng</h3>
      </div>
      <div class="flex items-center gap-2 scale-75 transition-all cursor-pointer
                  hover:scale-100 hover:text-red-500"
           @click="handleRedirectClient()">
        <v-icon name="gi-fast-backward-button" size="x-large" width="48" height="48"/>
        <h3 class="text-2xl m-0">Quay lại</h3>
      </div>
    </div>
  </div>
  <div class="p-6 grid grid-cols-5 gap-6">
    <div class="col-span-2 md:col-span-5 lg:col-span-2 bg-gray-100 rounded-xl p-5">
      <div class="w-full grid justify-center">
        <div class="w-[20rem] h-[20rem]">
          <a-avatar
              shape="circle"
              :src="modelRef['picture']"
              class="w-full h-full text-center scale-95 hover:scale-100 transition-all cursor-pointer"
          >
          </a-avatar>
        </div>
      </div>
    </div>
    <div class="col-span-3 md:col-span-5 lg:col-span-3 grid gap-6">
      <div class="rounded-xl border-2 shadow-xl p-5 h-fit grid gap-6">
        <a-form layout="vertical" class="grid grid-cols-2 gap-4">
          <template class="col-span-1 md:col-span-1 lg:col-span-1" v-for="field in formFields">
            <a-form-item
                :label="field.label"
                :name="field.name"
                v-bind="validateInfos[field.name]"
                class="m-0"
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

              <a-select
                  v-else-if="field.component === 'a-select'"
                  v-model:value="modelRef[field.name]"
                  :placeholder="field.placeholder"
                  :options="field.options"
                  show-search
                  :filter-option="filterOption"
                  @change="handleChangeOptions(field.name, $event)"
              >
              </a-select>

            </a-form-item>
          </template>
        </a-form>
        <div class="flex justify-end gap-4">
          <a-button @click="handleReset()">Đặt lại</a-button>
          <a-button type="primary" @click="handleCreateClient()">Thêm mới</a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: 'admin client detail',
};
</script>

<script lang="ts" setup>
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import {computed, createVNode, reactive, ref} from "vue";
import {
  ClientAddressCommonOptionsResponse,
  ClientAddressRequestCreate,
} from "@/infrastructure/services/api/admin/client.api.ts";
import {Form, Modal, notification, SelectProps} from "ant-design-vue";
import {
  useCreateClientAddressMo, useGetDistrictsByProvinceId, useGetProvinces, useGetWardsByDistrictId
} from "@/infrastructure/services/service/admin/client.action.ts";
import router from "@/infrastructure/routes/router.ts";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import dayjs from "dayjs";
import {keepPreviousData} from "@tanstack/vue-query";

const handleRedirectClient = () => {
  router.push({name: ROUTES_CONSTANTS.ADMIN.children.CLIENT.name});
}

const {mutate: getDistrictsByProvinceId} = useGetDistrictsByProvinceId();
const {mutate: getWardsByDistrictId} = useGetWardsByDistrictId();

const {data: provinces} = useGetProvinces({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const provincesOptions = computed(() => {
  const options = provinces?.value?.data?.map((province: ClientAddressCommonOptionsResponse) => ({
    label: province.name,
    value: province.id,
  })) || [];
  const defaultOptions = {
    label: '-- Chọn tỉnh --',
    value: '',
  }
  if (options === undefined || options.length === 0) {
    return [defaultOptions];
  }
  options.unshift(defaultOptions);
  return options;
})

const filterOption = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const districtsOptions = ref<SelectProps['options']>([
  {
    label: '-- Chọn thành phố/quận/huyện --',
    value: '',
  },
]);

const wardsOptions = ref<SelectProps['options']>([
  {
    label: '-- Chọn phường/xã --',
    value: '',
  },
]);

const handleChangeOptions = (key: string, value: any) => {

  const defaultDistrictOption = {
    label: '-- Chọn thành phố/quận/huyện --',
    value: '',
  };
  const defaultWardOption = {
    label: '-- Chọn phường/xã --',
    value: '',
  };

  switch (key) {
    case 'province':
      districtsOptions.value = [defaultDistrictOption];
      wardsOptions.value = [defaultWardOption];
      modelRef.district = '';
      modelRef.ward = '';
      getDistrictsByProvinceId(value, {
        onSuccess: (data) => {
          const options = data?.data?.map((district: ClientAddressCommonOptionsResponse) => ({
            label: district.name,
            value: district.id,
          })) || [];
          districtsOptions.value = [defaultDistrictOption, ...options];
        },
        onError: (error) => {
          console.error("Đã xảy ra lỗi khi lấy danh sách quận/huyện:", error);
        },
      });
      break;
    case 'district':
      wardsOptions.value = [defaultWardOption];
      modelRef.ward = '';
      console.log(modelRef);
      getWardsByDistrictId(value, {
        onSuccess: (data) => {
          const options = data?.data?.map((ward: ClientAddressCommonOptionsResponse) => ({
            label: ward.name,
            value: ward.id,
          })) || [];
          wardsOptions.value = [defaultWardOption, ...options];
        },
        onError: (error) => {
          console.error("Đã xảy ra lỗi khi lấy danh sách phường/xã:", error);
        },
      });
      break;
    default:
      return;
  }
}

const modelRef = reactive<ClientAddressRequestCreate>({
  name: "",
  phoneNumber: "",
  email: "",
  birthday: "",
  gender: "",
  line: "",
  ward: "",
  district: "",
  province: "",
  picture: "https://res.cloudinary.com/tshirtstwo/image/upload/v1737466633/user-icon-trendy-flat-style-600nw-1697898655_jrflvi.webp",
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
    {
      required: true,
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
  province: [
    {required: true, message: "Vui lòng chọn tỉnh", trigger: "blur"},
  ],
  district: [
    {required: true, message: "Vui lòng chon thành phố/quận/huyện", trigger: "blur"},
  ],
  ward: [
    {required: true, message: "Vui lòng chọn phường xã", trigger: "blur"},
  ],
  line: [
    {
      required: true,
      validator: (_, value) => value !== null && value.trim() !== "" ? Promise.resolve() : Promise.reject("Vui lòng điền số nhà/ngõ/đường"),
      trigger: "blur"
    },
    {max: 120, message: "Tên không được dài quá 120 ký tự", trigger: "blur"},
  ]
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
    label: "Tên khách hàng",
    name: "name",
    type: "string",
    component: "a-input",
    placeholder: "Nhâp tên khách hàng"
  },
  {
    label: "Email",
    name: "email",
    type: "string",
    component: "a-input",
    placeholder: "Nhâp email"
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
  {
    label: "Tỉnh",
    name: "province",
    component: "a-select",
    placeholder: "-- Chọn tỉnh --",
    options: provincesOptions.value
  },
  {
    label: "Thành phố/Quận/Huyện",
    name: "district",
    component: "a-select",
    placeholder: "-- Chọn thành phố/quận/huyện --",
    options: districtsOptions.value,
  },
  {
    label: "Phường/Xã",
    name: "ward",
    component: "a-select",
    placeholder: "-- Chọn phường/xã --",
    options: wardsOptions.value,
  },
  {
    label: "Số nhà/Ngõ/Đường",
    name: "line",
    component: "a-input",
    placeholder: "Nhập số nhà/ngõ/đường",
    type: "string",
  }
]);

/*** HandleForm ***/
const {mutate: create} = useCreateClientAddressMo();

const handleCreateClient = () => {
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
            handleRedirectClient();
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

const handleReset = () => {
  resetFields();
}

</script>