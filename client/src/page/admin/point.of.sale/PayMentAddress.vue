<template>
  <h3 class="text-lg font-semibold mb-4">📍 Nhập địa chỉ người nhận</h3>

  <a-form layout="vertical" class="grid grid-cols-2 gap-4">
    <template v-for="field in formFields" class="col-span-1 md:col-span-1 lg:col-span-1">
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
import {computed, createVNode, defineEmits, reactive, ref, watch} from "vue";
import {Form, Modal, notification} from "ant-design-vue";
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
  useUpdateClientAddress
} from "@/infrastructure/services/service/admin/client.action.ts";
import {keepPreviousData} from "@tanstack/vue-query";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import { ShippingFeeRequest } from "@/infrastructure/services/api/admin/payment.api";
import { useGetShippingFee } from "@/infrastructure/services/service/admin/payment.action";

/*** process data ***/

const props = defineProps({
  dataSource: Object,
  isRefresh: Boolean
});

const emit = defineEmits(["handleResetActiveKey","updateShippingFee"]);

const shippingFee = ref(0);

watch(shippingFee, (newFee) => {
  emit("updateShippingFee", newFee);
});

const isRefetch = ref<boolean>(false);

const {data: provinces, refetch: refetchProvinces} = useGetProvinces({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
  enabled: props.isRefresh
});

const {data: districts, refetch: refetchDistricts} = useGetDistrictsByProvinceIdQuery(props?.dataSource?.province, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
  enabled: props.isRefresh
});

const {data: wards, refetch: refetchWards} = useGetWardsByDistrictIdQuery(props?.dataSource?.district, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
  enabled: props.isRefresh
});
const shippingFeeParams = ref<ShippingFeeRequest>({
  fromDistrictId: 1482, // Set your shop's district ID here
  serviceId: 53320, // Default service ID
  toDistrictId: 0,
  toWardCode: 0,
  weight: 500, // Set default weight in grams
  length: 20, // Set default package dimensions
  width: 20,
  height: 10
});

const { data: shippingFeeData, refetch: refetchShippingFee } = useGetShippingFee(
  shippingFeeParams,
  {
    enabled: false,
    refetchOnWindowFocus: false
  }
);

watch(
  [
    () => modelRef.district,
    () => modelRef.ward
  ],
  ([newDistrict, newWard]) => {
    if (newDistrict && newWard) {
      // Update shipping fee parameters
      shippingFeeParams.value = {
        ...shippingFeeParams.value,
        toDistrictId: parseInt(newDistrict),
        toWardCode: parseInt(newWard)
      };
    
      
      // Calculate new shipping fee
      refetchShippingFee();
    }
  }
);
watch(
  () => shippingFeeData.value,
  (newFeeData) => {
    if (newFeeData?.data) {
      shippingFee.value = newFeeData.data.total || 0;
      console.log(shippingFee);
    }
  }
);
const provincesOptions = ref<{ label: string; value: string }[]>([]);
const districtsOptions = ref<{ label: string; value: string }[]>([]);
const wardsOptions = ref<{ label: string; value: string }[]>([]);

refetchProvinces();
refetchDistricts();
refetchWards();

const {mutate: getDistrictsByProvinceId} = useGetDistrictsByProvinceId();
const {mutate: getWardsByDistrictId} = useGetWardsByDistrictId();
const {mutate: changeClientAddressDefault} = useChangeClientAddressDefault();
const {mutate: updateClientAddress} = useUpdateClientAddress();

watch(provinces, (newProvinces) => {
  const options =
      newProvinces?.data?.map((address: ClientAddressCommonOptionsResponse) => ({
        label: address.name,
        value: address.id,
      })) || [];
  const defaultOption = {label: '-- Chọn tỉnh --', value: ''};

  provincesOptions.value = [defaultOption, ...options];
});

watch(districts, (newDistricts) => {
  const options =
      newDistricts?.data?.map((address: ClientAddressCommonOptionsResponse) => ({
        label: address.name,
        value: address.id,
      })) || [];
  const defaultOption = {label: '-- Chọn thành phố/quận/huyện --', value: ''};

  districtsOptions.value = [defaultOption, ...options];
});

watch(wards, (newWards) => {
  const options =
      newWards?.data?.map((address: ClientAddressCommonOptionsResponse) => ({
        label: address.name,
        value: address.id,
      })) || [];
  const defaultOption = {label: '-- Chọn phường/xã --', value: ''};

  wardsOptions.value = [defaultOption, ...options];
});

const filterOption = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const modelRef = reactive<ClientAddressRequest>({
  name: props?.dataSource?.name,
  phoneNumber: props?.dataSource?.phoneNumber,
  line: props?.dataSource?.line,
  ward: props?.dataSource?.ward.toString(),
  district: props?.dataSource?.district.toString(),
  province: props?.dataSource?.province.toString(),
  isDefault: props?.dataSource?.isDefault,
  clientId: props?.dataSource?.clientId,
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

const {validate, validateInfos} = Form.useForm(
    modelRef,
    rulesRef
);

const formFields = computed(() => [
  {
    label: "Tên khách hàng",
    name: "name",
    component: "a-input",
    placeholder: "Nhâp tên khách hàng",
    type: 'string'
  },
  {
    label: "Số điện thoại",
    name: "phoneNumber",
    component: "a-input",
    placeholder: "Nhâp số điện thoại",
    type: 'number'
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

/*** Handle ***/

const handleChangeOptions = (key: string, value: any) => {
  isRefetch.value = false;

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

</script>