<template>
  <a-modal
      :open="props.open"
      title="Thông tin địa chỉ"
      @cancel="handleClose"
      @ok="handleCreateClientAddress"
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
              :type="field.type"
          ></a-input>

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
  </a-modal>
</template>

<script setup lang="ts">
import {computed, createVNode, defineEmits, defineProps, reactive, ref} from "vue";
import {Form, Modal, notification, SelectProps} from "ant-design-vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import {
  useCreateClientAddress,
  useGetDistrictsByProvinceId,
  useGetProvinces,
  useGetWardsByDistrictId
} from "@/infrastructure/services/service/admin/client.action.ts";
import {
  ClientAddressCommonOptionsResponse,
  ClientAddressRequest
} from "@/infrastructure/services/api/admin/client.api.ts";
import {keepPreviousData} from "@tanstack/vue-query";

const props = defineProps({
  open: Boolean,
  clientId: String
});

const emit = defineEmits(["handleClose"]);

const {mutate: getDistrictsByProvinceId} = useGetDistrictsByProvinceId();
const {mutate: getWardsByDistrictId} = useGetWardsByDistrictId();
const {mutate: createClientAddress} = useCreateClientAddress();

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

const modelRef = reactive<ClientAddressRequest>({
  name: "",
  phoneNumber: "",
  line: "",
  ward: "",
  district: "",
  province: "",
  isDefault: "",
  clientId: props.clientId ? props.clientId : "",
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
  ]
});

const {resetFields, validate, validateInfos} = Form.useForm(
    modelRef,
    rulesRef
);

const formFields = computed(() => [
  {
    label: "Tên khách hàng",
    name: "name",
    component: "a-input",
    placeholder: "Nhâp tên khách hàng",
    type: "string",
  },
  {
    label: "Số điện thoại",
    name: "phoneNumber",
    component: "a-input",
    placeholder: "Nhâp số điện thoại",
    type: "number",
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

const handleCreateClientAddress = () => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn thêm?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await validate();
        createClientAddress(modelRef, {
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