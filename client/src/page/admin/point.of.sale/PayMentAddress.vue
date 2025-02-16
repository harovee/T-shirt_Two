<template>
  <h3 class="text-lg font-semibold mb-4">📍 Nhập địa chỉ người nhận</h3>

  <a-form layout="vertical" class="grid grid-cols-2 gap-4" :key="refreshKey">
    <template
      v-for="field in formFields"
      class="col-span-1 md:col-span-1 lg:col-span-1"
    >
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
  <!-- <div class="flex justify-end gap-4 mt-4">
    <a-button v-if="props?.dataSource?.isDefault === false" @click="handleChangeDefault(props?.dataSource?.id)">
      Đặt làm mặc định
    </a-button>
    <a-button @click="handleReset()">Đặt lại</a-button>
    <a-button type="primary" @click="handleUpdate(props?.dataSource?.id)">Cập nhật</a-button>
  </div> -->
</template>

<script setup lang="ts">
import { computed, createVNode, defineEmits, reactive, ref, watch } from "vue";
import { Form, Modal, notification } from "ant-design-vue";
import {
  ClientAddressCommonOptionsResponse,
  ClientAddressRequest,
} from "@/infrastructure/services/api/admin/client.api.ts";
import {
  useChangeClientAddressDefault,
  useGetDistrictsByProvinceId,
  useGetDistrictsByProvinceIdQuery,
  useGetProvinces,
  useGetWardsByDistrictId,
  useGetWardsByDistrictIdQuery,
  useUpdateClientAddress,
} from "@/infrastructure/services/service/admin/client.action.ts";
import { keepPreviousData } from "@tanstack/vue-query";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { log } from "console";

/*** process data ***/

const props = defineProps({
  dataSource: Object,
  isRefresh: Boolean,
  selectedCustomerAddress: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["handleResetActiveKey"]);

const isRefetch = ref<boolean>(false);

const province = ref<string>("");
const district = ref<string>("");
const ward = ref<string>("");

const { data: provinces, refetch: refetchProvinces } = useGetProvinces({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const { data: districts, refetch: refetchDistricts } =
  useGetDistrictsByProvinceIdQuery(props?.selectedCustomerAddress?.province, {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled: false,
  });

const { data: wards, refetch: refetchWards } = useGetWardsByDistrictIdQuery(
  props?.selectedCustomerAddress?.district,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled: false,
  }
);

const provincesOptions = ref<{ label: string; value: string }[]>([]);
const districtsOptions = ref<{ label: string; value: string }[]>([]);
const wardsOptions = ref<{ label: string; value: string }[]>([]);

refetchProvinces();

const { mutate: getDistrictsByProvinceId } = useGetDistrictsByProvinceId();
const { mutate: getWardsByDistrictId } = useGetWardsByDistrictId();
const { mutate: changeClientAddressDefault } = useChangeClientAddressDefault();
const { mutate: updateClientAddress } = useUpdateClientAddress();

watch(provinces, (newProvinces) => {
  const options =
    newProvinces?.data?.map((address: ClientAddressCommonOptionsResponse) => ({
      label: address.name,
      value: address.id,
    })) || [];
  const defaultOption = { label: "-- Chọn tỉnh --", value: "" };

  provincesOptions.value = [defaultOption, ...options];
});

watch(districts, (newDistricts) => {
  const options =
    newDistricts?.data?.map((address: ClientAddressCommonOptionsResponse) => ({
      label: address.name,
      value: address.id,
    })) || [];
  const defaultOption = { label: "-- Chọn thành phố/quận/huyện --", value: "" };

  districtsOptions.value = [defaultOption, ...options];
});

watch(wards, (newWards) => {
  const options =
    newWards?.data?.map((address: ClientAddressCommonOptionsResponse) => ({
      label: address.name,
      value: address.id,
    })) || [];
  const defaultOption = { label: "-- Chọn phường/xã --", value: "" };

  wardsOptions.value = [defaultOption, ...options];
});

const filterOption = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const modelRef = reactive<ClientAddressRequest>({
  name: props?.selectedCustomerAddress?.name,
  phoneNumber: props?.selectedCustomerAddress?.phoneNumber,
  line: props?.selectedCustomerAddress?.line,
  ward: props?.selectedCustomerAddress?.ward.toString(),
  district: props?.selectedCustomerAddress?.district.toString(),
  province: props?.selectedCustomerAddress?.province.toString(),
  isDefault: props?.selectedCustomerAddress?.isDefault,
  clientId: props?.selectedCustomerAddress?.clientId,
});
const refreshKey = ref(0);

const rulesRef = reactive({
  name: [
    {
      required: true,
      validator: (_, value) =>
        value !== null && value.trim() !== ""
          ? Promise.resolve()
          : Promise.reject("Tên không được để trống"),
      trigger: "blur",
    },
    { max: 50, message: "Tên không được dài quá 50 ký tự", trigger: "blur" },
  ],
  phoneNumber: [
    { required: true, message: "Vui lòng nhập số điện thoại", trigger: "blur" },
    {
      pattern: /^0[1-9]\d{8,9}$/,
      message: "Số điện thoại phải bắt đầu bằng số 0 và có 10-11 chữ số.",
      trigger: "blur",
    },
  ],
  province: [
    { required: true, message: "Vui lòng chọn tỉnh", trigger: "blur" },
  ],
  district: [
    {
      required: true,
      message: "Vui lòng chon thành phố/quận/huyện",
      trigger: "blur",
    },
  ],
  ward: [
    { required: true, message: "Vui lòng chọn phường xã", trigger: "blur" },
  ],
  line: [
    {
      required: true,
      validator: (_, value) =>
        value !== null && value.trim() !== ""
          ? Promise.resolve()
          : Promise.reject("Vui lòng điền số nhà/ngõ/đường"),
      trigger: "blur",
    },
  ],
});

const { validate, validateInfos } = Form.useForm(modelRef, rulesRef);

const formFields = computed(() => [
  {
    label: "Tên người nhận",
    name: "name",
    component: "a-input",
    placeholder: "Nhâp tên khách hàng",
    type: "string",
  },
  {
    label: "Số điện thoại người nhận",
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
    options: provincesOptions.value,
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
  },
]);

/*** Handle ***/

const handleChangeOptions = (key: string, value: any) => {
  isRefetch.value = false;

  const defaultDistrictOption = {
    label: "-- Chọn thành phố/quận/huyện --",
    value: "",
  };
  const defaultWardOption = {
    label: "-- Chọn phường/xã --",
    value: "",
  };

  switch (key) {
    case "province":
      districtsOptions.value = [defaultDistrictOption];
      wardsOptions.value = [defaultWardOption];
      modelRef.district = "";
      modelRef.ward = "";
      getDistrictsByProvinceId(value, {
        onSuccess: (data) => {
          const options =
            data?.data?.map((district: ClientAddressCommonOptionsResponse) => ({
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
    case "district":
      wardsOptions.value = [defaultWardOption];
      modelRef.ward = "";
      getWardsByDistrictId(value, {
        onSuccess: (data) => {
          const options =
            data?.data?.map((ward: ClientAddressCommonOptionsResponse) => ({
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
};

const handleChangeDefault = (id: string) => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn đặt địa chỉ này làm địa chỉ mặc định?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await validate();
        changeClientAddressDefault(id, {
          onSuccess: (res: any) => {
            notification.success({
              message: "Thông báo",
              description: res.message,
              duration: 4,
            });
            emit("handleResetActiveKey");
          },
          onError: (error: any) => {
            notification.error({
              message: "Thông báo",
              description: error?.response?.data?.message,
              duration: 4,
            });
          },
        });
      } catch (error: any) {
        console.error("🚀 ~ handleUpdate ~ error:", error);
        if (error?.response) {
          notification.error({
            message: "Thông báo",
            description: error?.response?.data?.message,
            duration: 4,
          });
        } else if (error?.errorFields) {
          notification.warning({
            message: "Thông báo",
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
};

const handleReset = () => {
  modelRef.name = props?.selectedCustomerAddress?.name;
  modelRef.phoneNumber = props?.selectedCustomerAddress?.phoneNumber;
  modelRef.line = props?.selectedCustomerAddress?.line;
  modelRef.province = props?.selectedCustomerAddress?.province.toString();
  modelRef.district = props?.selectedCustomerAddress?.district.toString();
  modelRef.ward = props?.selectedCustomerAddress?.ward;
  refetchProvinces();
  refetchDistricts();
  refetchWards();
};

watch(
  () => props.selectedCustomerAddress,
  (newDataSource) => {
    if (newDataSource) {
      if (newDataSource?.province && newDataSource?.district) {
        handleChangeOptions("province", newDataSource?.province);
        handleChangeOptions("district", newDataSource?.district);
      }
      Object.assign(modelRef, {
        name: newDataSource.name || "",
        phoneNumber: newDataSource.phoneNumber || "",
        line: newDataSource.line || "",
        ward: newDataSource.ward || "",
        district: newDataSource.district?.toString() || "",
        province: newDataSource.province?.toString() || "",
        isDefault: newDataSource.isDefault ?? false,
        clientId: newDataSource.clientId || "",
      });
    }
  },
  { immediate: true, deep: true }
);

</script>