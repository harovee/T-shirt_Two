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
    <div class="col-span-2 md:col-span-5 lg:col-span-2 bg-gray-100 rounded-xl p-5 h-fit">
      <div class="flex justify-end">
        <a-tag v-if="detailRef.status === false" color="success">Hoạt động</a-tag>
        <a-tag v-else-if="detailRef.status === true" color="warning">Vô hiệu hóa</a-tag>
        <a-tag v-else color="violet">Không xác định</a-tag>
      </div>
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
      <div class="mt-5 p-5 w-full h-full bg-white rounded-xl">
        <h4 class="text-center text-xl font-semibold text-gray-700">{{ detailRef.fullName }}</h4>
        <p class="text-gray-500">Mã khách hàng: KH{{ detailRef.code }}</p>
        <p class="text-gray-500">Người tạo: {{ detailRef.createdBy || 'Chưa xác định' }} lúc
          {{ convertDateFormat(detailRef.createdDate) }}</p>
        <p class="text-gray-500">Người chỉnh sửa lần cuối: {{ detailRef.lastModifiedBy || 'Chưa xác định' }} lúc
          {{ convertDateFormat(detailRef.lastModifiedDate) }}</p>
      </div>
    </div>
    <div class="col-span-3 md:col-span-5 p-5 lg:col-span-3 rounded-xl border-2 shadow-purple-500 shadow-xl h-fit">
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
                format="YYYY-MM-DD"
                show-time
                :placeholder="field.placeholder"
            ></a-date-picker>

          </a-form-item>
        </template>
        <div class="flex justify-end items-center gap-4">
          <a-button @click="handleReset()">Đặt lại</a-button>
          <a-button type="primary" @click="handleUpdate()">Cập nhật</a-button>
        </div>
      </a-form>
      <client-address/>
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
import {computed, createVNode, reactive, watch} from "vue";
import {DetailClientResponse, ClientRequest} from "@/infrastructure/services/api/admin/client.api.ts";
import {Form, Modal} from "ant-design-vue";
import {
  useGetClientById,
  useUpdateClient,
  useUpdateClientAvatar
} from "@/infrastructure/services/service/admin/client.action.ts";
import {keepPreviousData} from "@tanstack/vue-query";
import router from "@/infrastructure/routes/router.ts";
import {CLOUDINARY_CLOUD_NAME, CLOUDINARY_UPLOAD_PRESET} from "@/infrastructure/constants/cloudinary.ts";
import {
  convertDateFormat, convertToAntdDatePicker
} from "@/utils/common.helper.ts";
import {toast} from "vue3-toastify";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import ClientAddress from "@/page/admin/client/ClientAddress.vue";

const clientId = computed(() => {
  const currentUrl = window.location.href;
  const match = currentUrl.match(/\/admin\/client\/([a-f0-9-]+)/);
  if (match) {
    return match[1];
  } else {
    router.push({name: ROUTES_CONSTANTS.NOT_FOUND.name})
    toast.warning("Không tìm thấy người dùng trên");
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
      validator: (_, value) => value !== null && value.trim() !== "" ? Promise.resolve() : Promise.reject("Tên không được để trống"),
      trigger: "blur"
    },
    {max: 50, message: "Tên không được dài quá 50 ký tự", trigger: "blur"},
  ],
  email: [
    {required: true, message: "Vui lòng nhập email", trigger: "blur"},
    {
      pattern: /^[a-zA-Z0-9._%+-]+@(gmail\.com|fpt\.edu\.vn)$/,
      message: "Email không hợp lệ (chỉ chấp nhận @gmail.com hoặc @fpt.edu.vn)",
      trigger: "blur"
    },
    {max: 50, message: "Email không được dài quá 50 ký tự", trigger: "blur"},
  ],
  password: [
    {required: true, message: "Vui lòng nhập mật khẩu", trigger: "blur"},
    {
      pattern: /^(?=.*[A-Z])(?=.*\W).{8,50}$/,
      message: "Mật khẩu phải có ít nhất 1 ký tự viết hoa, 1 ký tự đặc biệt, và dài từ 8 đến 50 ký tự",
      trigger: "blur"
    },
  ],
  birthday: [
    {required: true, message: "Vui lòng nhập ngày sinh", trigger: "blur"},
    {
      validator: (_, value) => new Date(value) < new Date() ? Promise.resolve() : Promise.reject("Ngày sinh phải là ngày trong quá khứ"),
      trigger: "blur"
    },
  ],
  gender: [
    {required: true, message: "Vui lòng chọn giới tính", trigger: "blur"},
  ],
  phoneNumber: [
    {required: true, message: "Vui lòng nhập số điện thoại", trigger: "blur"},
    {
      pattern: /^\+?[1-9]\d{1,14}$/,
      message: "Số điện thoại không hợp lệ (bao gồm mã quốc gia nếu có) ví dụ: 84",
      trigger: "blur"
    },
  ],
});


const {validate, validateInfos} = Form.useForm(
    modelRef,
    rulesRef
);

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
    placeholder: "Nhâp ngày sinh"
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
const {mutate: updateClientAvatar} = useUpdateClientAvatar();

const myWidget = cloudinary.createUploadWidget(
    {
      cloudName: CLOUDINARY_CLOUD_NAME,
      uploadPreset: CLOUDINARY_UPLOAD_PRESET,
    },
    (error: any, result: any) => {
      if (!error && result && result.event === "success") {
        modelRef.picture = result.info.url;
        const id = clientId.value;
        uploadAvatarClient(id, modelRef);
      }
    }
);

const uploadAvatarClient = (clientId: string, data: ClientRequest) => {
  try {
    updateClientAvatar({clientId, data}, {
      onSuccess: (res: any) => {
        toast.success(res.data.message);
        refetch();
      },
      onError: (error: any) => {
        toast.error(
            error?.response?.data?.message
        )
      },
    })
  } catch (error: any) {
    console.error("🚀 ~ handleChangeStatus ~ error:", error);
    toast.error(
        error?.response?.data?.message
    );
  }
}

const openWidget = () => {
  myWidget.open();
}

// * HandleForm * \\
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
            toast.success(res.data.message);
            refetch();
          },
          onError: (error: any) => {
            toast.error(
                error?.response?.data?.message
            )
          },
        })
      } catch (error: any) {
        console.error("🚀 ~ handleUpdate ~ error:", error);
        if (error?.response) {
          toast.warning(
              error?.response?.data?.message
          );
        } else if (error?.errorFields) {
          toast.warning("Vui lòng nhập đúng đủ các trường dữ liệu");
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