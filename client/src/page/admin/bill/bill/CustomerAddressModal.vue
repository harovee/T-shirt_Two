<template>
  <a-modal
    :open="props.open"
    title="Cập nhật địa chỉ"
    @cancel="handleClose"
    ok-text="Xác nhận"
    @ok="handleUpdateBill"
    cancel-text="Hủy"
    destroyOnClose
    centered
  >
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
  </a-modal>
</template>

<script lang="ts" setup>
import { BillRequest } from "@/infrastructure/services/api/admin/bill.api";
import { useUpdateBill } from "@/infrastructure/services/service/admin/bill.action";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { errorNotiSort, successNotiSort, warningNotiSort } from "@/utils/notification.config";
import { computed, createVNode, defineEmits, reactive, ref, watch } from "vue";
import { Form, Modal, notification } from "ant-design-vue";
import {
  ClientAddressCommonOptionsResponse,
  ClientAddressRequest,
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
import { log } from "console";
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

const props = defineProps({
  open: Boolean
});

const emit = defineEmits(["handleClose",
  "handleGetAddress"]);

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
      const fullAddress = ref('');
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
    }
  },
  { immediate: true, deep: true }
);

watch(
  () => defaultCustomerAddress.value,
  async (newDataSource) => {
    if (newDataSource) {
      const wardInfo = ref(null);
      const districtInfo = ref(null);
      const provinceInfo = ref(null);
      const fullAddress = ref('');
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
    }
  },
  { immediate: true, deep: true }
);



const handleCustomerAddressSelected = (selectedAddress: any) => {
  addressSelected.value = selectedAddress
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

const modelRef = reactive<any>({
  line: address?.value?.line || null,
  ward: address?.value?.ward.toString() || null,
  district: address?.value?.district.toString() || null,
  province: address?.value?.province.toString() || null
});

const refreshKey = ref(0);

const rulesRef = reactive({
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

const { validate, validateInfos, resetFields } = Form.useForm(modelRef, rulesRef);

const formFields = computed(() => [
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
  }
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
      updateFullAddress();
      break;
    default:
      return;
  }
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

const handleUpdateBill = async () => {
  try {
    await validate(); // Chờ validate hoàn tất
    const fullAddress = modelRef.line + ', ' + fullAddressRef.value;
    emit("handleGetAddress", fullAddress);
    handleClose();
  } catch (error) {
    console.error("Validation failed:", error);
    // Không đóng modal hoặc emit nếu validate thất bại
  }
};

const handleClose = () => {
  emit("handleClose");
};
</script>


