<template>
  <div class="flex justify-between items-center space-x-3 mb-5">
    <h1 class="text-2xl font-bold">
      <v-icon name="md-locationon-round" /> ĐỊA CHỈ GIAO HÀNG
    </h1>
    <a-tooltip title="Chọn địa chỉ" trigger="hover">
      <a-button
        class="bg-purple-300 flex justify-between items-center gap-2"
        @click="handleOpenKhachHangAddress"
        size="large"
      >
        <v-icon name="fa-address-book" />
      </a-button>
    </a-tooltip>
  </div>
  <KhachHangAddressPaymentTable
    :open="openCustomerAddress"
    @handleClose="handleCloseCustomerAddress"
    @cancel="openCustomerAddress = false"
    @selectCustomerAddress="handleCustomerAddressSelected"
    @selectedDefaultAddress="handleSelectedDefaultAddress"
    class="w-[600px] h-[400px]"
  />
  <a-form layout="vertical" class="grid grid-cols-2 gap-4" :key="refreshKey">
    <div class="grid grid-cols-2 gap-4 col-span-2">
      <template
        v-for="(field, index) in formFields.filter((f) =>
          f.component.includes('a-input')
        )"
        :key="field.name"
      >
        <a-form-item
          v-if="index < 2"
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
            @blur="handleGetAddress"
          ></a-input>

          <a-input
            v-else-if="field.component === 'a-input-number'"
            v-model:value="modelRef[field.name]"
            :placeholder="field.placeholder"
            class="w-full"
            @blur="handleGetAddress"
          ></a-input>
        </a-form-item>

        <a-form-item
          v-if="index === 2"
          :label="field.label"
          :name="field.name"
          v-bind="validateInfos[field.name]"
          class="m-0 col-span-2"
        >
          <a-input
            v-if="field.component === 'a-input'"
            v-model:value="modelRef[field.name]"
            :placeholder="field.placeholder"
            :type="field.type"
            @blur="handleGetAddress"
          ></a-input>
        </a-form-item>
      </template>
    </div>

    <div class="grid grid-cols-3 gap-4 col-span-2">
      <template
        v-for="field in formFields.filter((f) => f.component === 'a-select')"
        :key="field.name"
      >
        <a-form-item
          :label="field.label"
          :name="field.name"
          v-bind="validateInfos[field.name]"
          class="m-0 col-span-1"
        >
          <a-select
            v-model:value="modelRef[field.name]"
            :placeholder="field.placeholder"
            :options="field.options"
            show-search
            :filter-option="filterOption"
            @change="handleChangeOptions(field.name, $event)"
            @blur="handleGetAddress"
          ></a-select>
        </a-form-item>
      </template>

      <template
        v-for="field in formFields.filter(
          (f) => f.component === 'a-input' && f.name === 'line' || f.name === 'ghiChu'
        )"
        :key="field.name"
      >
        <a-form-item
          :label="field.label"
          :name="field.name"
          v-bind="validateInfos[field.name]"
          class="m-0 col-span-3"
        >
          <a-input
            v-model:value="modelRef[field.name]"
            :placeholder="field.placeholder"
            :type="field.type"
            @blur="handleGetAddress"
          ></a-input>
        </a-form-item>
      </template>
    </div>
  </a-form>
</template>

<script setup lang="ts">
import { computed, createVNode, defineEmits, reactive, ref, watch } from "vue";
import { Form, Modal, notification } from "ant-design-vue";
import {
  ClientAddressCommonOptionsResponse,
  ClientAddressRequest,
  ClientAddressPaymentRequest
} from "@/infrastructure/services/api/admin/client.api";
import {
  useChangeClientAddressDefault,
  useGetDistrictsByProvinceId,
  useGetDistrictsByProvinceIdQuery,
  useGetProvinces,
  useGetWardsByDistrictId,
  useGetWardsByDistrictIdQuery,
  useUpdateClientAddress,
} from "@/infrastructure/services/service/admin/client.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { log } from "console";
import KhachHangAddressPaymentTable from "@/page/admin/point.of.sale/KhachHangAddressPaymentTable.vue";
import {
  VoucherResponse,
  FindVoucherRequest,
  nextVoucherRequest,
  ShippingFeeRequest,
  getWardByCode,
  getDistrictById,
  getProvinceById,
  ServiceIdRequest,
} from "@/infrastructure/services/api/admin/payment.api";

/*** process data ***/

const props = defineProps({
  dataSource: Object,
  isRefresh: Boolean,
  // selectedCustomerAddress: {
  //   type: Object,
  //   required: true,
  // },
});

const emit = defineEmits([
  "handleResetActiveKey",
  "handleGetAddress",
  "update:selectedCustomerAddress",
]);

const emitUpdatedAddress = () => {
  if (modelRef.district && modelRef.ward) {
    emit("update:selectedCustomerAddress", { ...modelRef });
  }
};

const address = ref(null);
const isRefetch = ref<boolean>(false);

const province = ref<string>("");
const district = ref<string>("");
const ward = ref<string>("");

const openCustomerAddress = ref(false);
const addressSelected = ref(null);
const defaultCustomerAddress = ref(null);

watch(
  () => addressSelected.value,
  async (newDataSource) => {
    if (newDataSource) {
      const wardInfo = ref(null);
      const districtInfo = ref(null);
      const provinceInfo = ref(null);
      const fullAddress = ref("");
      try {
        const response = await getWardByCode(newDataSource.ward);
        wardInfo.value = response.data.data;

        const responseDis = await getDistrictById(newDataSource.district);
        districtInfo.value = responseDis.data.data;

        const responsePro = await getProvinceById(newDataSource.province);
        provinceInfo.value = responsePro.data.data;
        fullAddress.value =
          newDataSource.line +
          ", " +
          wardInfo.value +
          ", " +
          districtInfo.value +
          ", " +
          provinceInfo.value;
      } catch (error) {
        console.error("Lỗi khi lấy thông tin Xã, huyện, tỉnh:", error);
      }
      emit("handleGetAddress", newDataSource, fullAddress.value);
    }
  },
  { immediate: true, deep: true }
);

const handleSelectedDefaultAddress = (defaultAddress: any) => {
  defaultCustomerAddress.value = defaultAddress;
  address.value = defaultAddress;
};

watch(
  () => defaultCustomerAddress.value,
  async (newDataSource) => {
    if (newDataSource) {
      const wardInfo = ref(null);
      const districtInfo = ref(null);
      const provinceInfo = ref(null);
      const fullAddress = ref("");
      try {
        const response = await getWardByCode(newDataSource.ward);
        wardInfo.value = response.data.data;

        const responseDis = await getDistrictById(newDataSource.district);
        districtInfo.value = responseDis.data.data;

        const responsePro = await getProvinceById(newDataSource.province);
        provinceInfo.value = responsePro.data.data;
        fullAddress.value =
          newDataSource.line +
          ", " +
          wardInfo.value +
          ", " +
          districtInfo.value +
          ", " +
          provinceInfo.value;
      } catch (error) {
        console.error("Lỗi khi lấy thông tin Xã, huyện, tỉnh:", error);
      }
      emit("handleGetAddress", newDataSource, fullAddress.value);
    }
  },
  { immediate: true, deep: true }
);

const handleCustomerAddressSelected = (selectedAddress: any) => {
  addressSelected.value = selectedAddress;
  address.value = selectedAddress;
};

const handleOpenKhachHangAddress = () => {
  openCustomerAddress.value = true;
};

const handleCloseCustomerAddress = () => {
  openCustomerAddress.value = false;
};

const { data: provinces, refetch: refetchProvinces } = useGetProvinces({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const { data: districts, refetch: refetchDistricts } =
  useGetDistrictsByProvinceIdQuery(address?.value?.province, {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled: false,
  });

const { data: wards, refetch: refetchWards } = useGetWardsByDistrictIdQuery(
  address?.value?.district,
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
  5;
  wardsOptions.value = [defaultOption, ...options];
});

const filterOption = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const modelRef = reactive<ClientAddressPaymentRequest>({
  name: address?.value?.name,
  email: address?.value?.name,
  phoneNumber: address?.value?.phoneNumber,
  line: address?.value?.line || null,
  ward: address?.value?.ward.toString() || null,
  district: address?.value?.district.toString() || null,
  province: address?.value?.province.toString() || null,
  isDefault: address?.value?.isDefault || false,
  clientId: address?.value?.clientId,
  ghiChu: address?.value?.ghiChu,
});

// watch(
//   () => props.selectedCustomer,
//   (newCustomer) => {
//     if (newCustomer) {
//       Object.assign(modelRef, {
//         name: "",
//         phoneNumber: "",
//         line: "",
//         ward: "",
//         district: "",
//         province: "",
//         isDefault: false,
//         clientId: null,
//       });
//     }
//   }
// );
const refreshKey = ref(0);

const rulesRef = reactive({
  name: [
    {
      required: true,
      validator: (_, value) =>
        value && typeof value === "string" && value.trim() !== ""
          ? Promise.resolve()
          : Promise.reject("Tên không được để trống"),
      trigger: "blur",
    },
    { max: 50, message: "Tên không được dài quá 50 ký tự", trigger: "blur" },
  ],
  email: [
  {
    required: true,
    validator: (_, value) =>
      value && typeof value === "string" && value.trim() !== ""
        ? Promise.resolve()
        : Promise.reject("Email không được để trống"),
    trigger: "blur",
  },
  {
    pattern: /^[a-zA-Z0-9._%+-]+@(gmail\.com)$/,
    message: "Email không hợp lệ (chỉ chấp nhận @gmail.com)",
    trigger: "blur",
  }
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
    label: "HỌ TÊN",
    name: "name",
    component: "a-input",
    placeholder: "Nhập họ tên của bạn",
    type: "string",
  },
  {
    label: "SĐT",
    name: "phoneNumber",
    component: "a-input",
    placeholder: "Nhập số điện thoại",
    min: 0,
    type: "number",
  },
  {
    label: "EMAIL",
    name: "email",
    component: "a-input",
    placeholder: "Nhập email",
    type: "string",
  },
  {
    label: "TỈNH",
    name: "province",
    component: "a-select",
    placeholder: "-- Chọn tỉnh --",
    options: provincesOptions.value,
  },
  {
    label: "THÀNH PHỐ / QUẬN / HUYỆN",
    name: "district",
    component: "a-select",
    placeholder: "-- Chọn thành phố/quận/huyện --",
    options: districtsOptions.value,
  },
  {
    label: "PHƯỜNG / XÃ",
    name: "ward",
    component: "a-select",
    placeholder: "-- Chọn phường/xã --",
    options: wardsOptions.value,
  },
  {
    label: "SỐ NHÀ / NGÕ / ĐƯỜNG",
    name: "line",
    component: "a-input",
    placeholder: "Nhập số nhà/ngõ/đường",
    type: "string",
  },
  {
    label: "GHI CHÚ",
    name: "ghiChu",
    component: "a-input",
    placeholder: "Nhập ghi chú của bạn",
    type: "string",
  },
]);

const fullAddressRef = ref("");
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
      updateFullAddress();
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
      updateFullAddress();
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
    case "ward":
      modelRef.ward = value;
      updateFullAddress(); // Cập nhật địa chỉ mới
      break;
    default:
      return;
  }
  emitUpdatedAddress();
  // validate();
  const fullAddress = modelRef.line + ", " + fullAddressRef.value;
  emit("handleGetAddress", modelRef, fullAddress);
};

const updateFullAddress = () => {
  const wardName =
    wardsOptions.value.find((w) => w.value === modelRef.ward)?.label || "";
  const districtName =
    districtsOptions.value.find((d) => d.value === modelRef.district)?.label ||
    "";
  const provinceName =
    provincesOptions.value.find((p) => p.value === modelRef.province)?.label ||
    "";
  fullAddressRef.value = `${wardName}, ${districtName}, ${provinceName}`.trim();
};

const handleGetAddress = (name: string) => {
  validate();
  const fullAddress = modelRef.line + ", " + fullAddressRef.value;
  emit("handleGetAddress", modelRef, fullAddress);
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

watch(
  () => address.value,
  (newDataSource) => {
    if (newDataSource) {
      if (newDataSource?.province && newDataSource?.district) {
        handleChangeOptions("province", newDataSource?.province);
        handleChangeOptions("district", newDataSource?.district);
      }
      Object.assign(modelRef, {
        name: newDataSource.name || "",
        email: newDataSource.email || "",
        phoneNumber: newDataSource.phoneNumber || "",
        line: newDataSource.line || "",
        ward: newDataSource.ward || "",
        district: newDataSource.district?.toString() || "",
        province: newDataSource.province?.toString() || "",
        isDefault: newDataSource.isDefault ?? false,
        clientId: newDataSource.clientId || "",
        ghiChu: newDataSource.ghiChu || ""
      });
    }
  },
  { immediate: true, deep: true }
);
</script>