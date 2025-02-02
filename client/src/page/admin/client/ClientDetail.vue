<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex justify-between items-center">
      <div class="flex items-center gap-2">
        <v-icon name="pr-users" size="x-large" width="48" height="48"/>
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
    <div class="col-span-2 md:col-span-5 lg:col-span-2 bg-gray-100 rounded-xl p-5 h-fit">
      <div class="flex justify-end">
        <a-tag v-if="detailRef.status === false" color="success">Hoạt động</a-tag>
        <a-tag v-else-if="detailRef.status === true" color="warning">Vô hiệu hóa</a-tag>
        <a-tag v-else color="violet">Không xác định</a-tag>
      </div>
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
      <div class="mt-5 p-5 w-full h-full bg-white rounded-xl">
        <h4 class="text-center text-xl font-semibold mb-5 text-gray-700">{{ detailRef.fullName }}</h4>
        <p class="text-gray-500">Mã khách hàng: KH{{ detailRef.code }}</p>
        <p class="text-gray-500">Người tạo: {{ detailRef.createdBy || 'Chưa xác định' }} lúc
          {{ convertDateFormat(detailRef.createdDate) }}</p>
        <p class="text-gray-500">Người chỉnh sửa lần cuối: {{ detailRef.lastModifiedBy || 'Chưa xác định' }} lúc
          {{ convertDateFormat(detailRef.lastModifiedDate) }}</p>
      </div>
    </div>
    <div class="col-span-3 md:col-span-5 lg:col-span-3">
      <div class="rounded-xl border-2 shadow-xl p-5 h-fit">
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

            </a-form-item>
          </template>
        </a-form>
        <div class="flex justify-end gap-4">
          <a-button @click="handleReset()">Đặt lại</a-button>
          <a-button type="primary" @click="handleUpdate()">Cập nhật</a-button>
        </div>
      </div>
      <div class="rounded-xl border-2 shadow-xl h-fit mt-10 p-5">
        <div class="flex justify-between items-center mb-5">
          <div>
            <h3 class="text-xl font-semibold text-gray-800 m-0">Địa chỉ khách hàng</h3>
          </div>
          <a-tooltip
              title="Thêm địa chỉ"
              trigger="hover"
          >
            <a-button
                class="bg-purple-300 flex justify-between items-center gap-2"
                size="large"
                @click="handleOpenModalCreateClientAddress()"
            >
              <v-icon name="md-addcircle"/>
            </a-button>
          </a-tooltip>
        </div>
        <div class="grid gap-3" :key="refreshKey">
          <a-collapse
              v-for="(clientAddress, index) in clientAddresses"
              v-model:activeKey="activeKey"
              accordion
              expand-icon-position="end"
              @change="handleChangeCollapse"
          >
            <a-collapse-panel
                :key="index"
                :header="clientAddress.isDefault ? `Địa chỉ mặc định: ${clientAddress.line}` : `Địa chỉ thường: ${clientAddress.line}`"
            >
              <client-address
                  :data-source="clientAddress"
                  :isRefresh="isRefresh"
                  @handleResetActiveKey="handleResetActiveKey"
              />
            </a-collapse-panel>
          </a-collapse>
        </div>
      </div>
    </div>
  </div>
  <client-address-modal-c
      :open="isOpenModalCreateClientAddress"
      :clientId="clientId"
      @handleClose="handleCloseModalCreateClientAddress"
  />
</template>

<script lang="ts">
export default {
  name: 'admin client detail',
};
</script>

<script lang="ts" setup>
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import {computed, createVNode, reactive, ref, watch} from "vue";
import {
  DetailClientResponse,
  ClientRequest,
  ClientAddressResponse,
} from "@/infrastructure/services/api/admin/client.api.ts";
import {Form, Modal, notification} from "ant-design-vue";
import {
  useGetClientAddressesByClientId,
  useGetClientById,
  useUpdateClient
} from "@/infrastructure/services/service/admin/client.action.ts";
import {keepPreviousData} from "@tanstack/vue-query";
import router from "@/infrastructure/routes/router.ts";
import {
  convertDateFormat, convertToAntdDatePicker
} from "@/utils/common.helper.ts";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import ClientAddress from "@/page/admin/client/ClientAddress.vue";
import dayjs from "dayjs";
import ClientAddressModalC from "@/page/admin/client/ClientAddressModalC.vue";

const clientId = computed(() => {
  const currentUrl = window.location.href;
  const match = currentUrl.match(/\/admin\/client\/([a-f0-9-]+)/);
  if (match) {
    return match[1];
  } else {
    router.push({name: ROUTES_CONSTANTS.NOT_FOUND.name})
    notification.warning({
      message: 'Thông báo',
      description: 'Không tìm thấy người dùng trên',
      duration: 4
    });
    return "idNotFound";
  }
});

const handleRedirectClient = () => {
  router.push({name: ROUTES_CONSTANTS.ADMIN.children.CLIENT.name});
}

const {data, refetch} = useGetClientById(clientId.value, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const clientDetail = computed(() => data?.value?.data?.data || null);

const detailRef = reactive<DetailClientResponse>({
  id: clientId.value,
  code: null,
  fullName: null,
  birthday: null,
  gender: null,
  phoneNumber: null,
  email: null,
  password: null,
  status: null,
  picture: null,
  createdBy: null,
  lastModifiedBy: null,
  createdDate: null,
  lastModifiedDate: null,
})

const modelRef = reactive<ClientRequest>({
  name: null,
  email: null,
  password: null,
  birthday: null,
  gender: null,
  phoneNumber: null,
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
});

const {validate, validateInfos} = Form.useForm(
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
    label: "Mật khẩu",
    name: "password",
    type: "string",
    component: "a-input-password",
    placeholder: "Nhâp mật khẩu"
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

/*** HandleForm ***/
const {mutate: updateClient} = useUpdateClient();

const handleUpdate = () => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn cập nhật?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await validate();
        const request = {
          clientId: detailRef.id,
          data: modelRef
        };
        updateClient(request, {
          onSuccess: (res: any) => {
            notification.success({
              message: 'Thông báo',
              description: res.data.message,
              duration: 4,
            });
            refetch();
          },
          onError: (error: any) => {
            notification.error({
              message: 'Thông báo',
              description: error?.response?.data?.message,
              duration: 4,
            });
          },
        })
      } catch (error: any) {
        console.error("🚀 ~ handleUpdate ~ error:", error);
        if (error?.response) {
          notification.error({
            message: 'Thông báo',
            description: error?.response?.data?.message,
            duration: 4,
          });
        } else if (error?.errorFields) {
          notification.warning({
            message: 'Thông báo',
            description: "Vui lòng nhập đúng đủ các trường dữ liệu",
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
  if (clientDetail.value != null) {
    assignData(clientDetail.value);
  }
}

const assignData = (client: DetailClientResponse) => {
  Object.assign(modelRef, {
    name: client.fullName,
    email: client.email,
    password: client.password,
    birthday: convertToAntdDatePicker(client.birthday),
    gender: client.gender,
    phoneNumber: client.phoneNumber,
    picture: client.picture,
  });

  Object.assign(detailRef, {
    id: client.id,
    code: client.code,
    fullName: client.fullName,
    birthday: client.birthday,
    gender: client.gender,
    phoneNumber: client.phoneNumber,
    email: client.email,
    password: client.password,
    status: client.status,
    picture: client.picture,
    createdBy: client.createdBy,
    lastModifiedBy: client.lastModifiedBy,
    createdDate: client.createdDate,
    lastModifiedDate: client.lastModifiedDate,
  })
}

/*** GetClientAddress  ***/
const {data: clientAddressesData} = useGetClientAddressesByClientId(clientId.value, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const clientAddresses = computed(() =>
    clientAddressesData?.value?.data || Array<ClientAddressResponse>
);

const activeKey = ref(['0']);

const handleResetActiveKey = () => {
  activeKey.value = ['0'];
}

const isRefresh = ref(false);

const handleChangeCollapse = () => {
  isRefresh.value = !isRefresh.value;
}

const refreshKey = ref(0);

watch(
    clientAddresses,
    (newVal) => {
      console.log("Danh sách clientAddresses đã thay đổi:", newVal);
      refreshKey.value++;
    },
    {deep: true, immediate: true}
);

isRefresh.value = !isRefresh.value;

/*** Create Client Address ***/
const isOpenModalCreateClientAddress = ref(false);

const handleOpenModalCreateClientAddress = () => {
  isOpenModalCreateClientAddress.value = true;
};

const handleCloseModalCreateClientAddress = () => {
  isOpenModalCreateClientAddress.value = false;
};

watch(
    clientDetail,
    (client) => {
      if (client) {
        assignData(client);
      }
    },
    {immediate: true}
);

</script>