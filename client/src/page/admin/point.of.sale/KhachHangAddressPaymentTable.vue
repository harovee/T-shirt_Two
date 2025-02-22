<template>
  <a-modal
    :visible="open"
    @ok="handleClose"
    key=""
    :width="'800px'"
    :okText="'Xác nhận'"
    :cancelText="'Hủy bỏ'"
  >
    <h1 class="text-xl">Danh sách địa chỉ khách hàng</h1>
    <div class="w-400px">
      <a-space class="flex justify-start items-center">
        <a-input-search
          class="m-2"
          v-model:value="params.keyword"
          placeholder="Tìm kiếm khách hàng"
          style="width: 200px"
          @change:value="handleSearch"
        />
      </a-space>
    </div>
    <a-table
      :columns="columns"
      :data-source="dataCustomerAddress"
      :pagination="false"
      :scroll="{ x: 300, y: 300 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'actions'">
          <a-button color="primary" @click="handleSelectCustomerAddress(record)"
            >Chọn</a-button
          >
        </template>
        <template v-if="column.dataIndex === 'address'">
          {{record.line}}
        </template>
      </template>
    </a-table>
    <a-pagination
      class="m-2"
      v-model:current="params.page"
      v-model:pageSize="params.size"
      :total="data?.data.totalElements"
    />
  </a-modal>
</template>
<script lang="ts" setup>
import type { TableProps, TableColumnType } from "ant-design-vue";
import { defineProps, computed, defineEmits, ref, watch, onMounted } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import { FindKhachHangRequest } from "@/infrastructure/services/api/admin/voucher/voucher.api";
import {
  useGetListCustomer,
  useGetListCustomerAddress,
} from "@/infrastructure/services/service/admin/payment.action";
import { FindCustomerAddressRequest } from "@/infrastructure/services/api/admin/payment.api";
import { convertDateFormatTime } from "@/utils/common.helper";
import { useRoute } from "vue-router";

const pageSize = ref(5);
const current1 = ref(1);

const props = defineProps({ open: Boolean,
  dataCustomerWithId: {
    type: Object,
    required: true,
  },
 });

const emit = defineEmits(["handleClose", "selectCustomerAddress"]);

const handleClose = () => emit("handleClose");

interface DataType {
  id: string;

  name: string;

  phoneNumber: string;

  line: string;

  ward: string;

  district: number;

  province: number;

  clientId: string;

  isDefault: boolean;
}

const columns: TableColumnType<DataType>[] = [
  {
    title: "Tên người nhận",
    dataIndex: "name",
  },
  {
    title: "Số điện thoại",
    dataIndex: "phoneNumber",
  },
  {
    title: "Địa chỉ",
    dataIndex: "address",
  },
  { title: "Hành động", dataIndex: "actions" },
];

// const params = computed(() => ({
//   page: 1,
//   size: 5,
//   keyword: "",
//   idKhachHang: props.dataCustomerWithId ? props.dataCustomerWithId.key : null
// }));

const params = ref<FindCustomerAddressRequest>({
  page: 1,
  size: 5,
  keyword: "",
  idKhachHang: props.dataCustomerWithId ? props.dataCustomerWithId.id : null
});

watch(() => props.dataCustomerWithId, (newVal) => {
  if (newVal) {
    params.value.idKhachHang = newVal.id
  }
  
  // params.value.idKhachHang = newVal.key;
}, { immediate: true });

const { data } = useGetListCustomerAddress(params, {
  refetchOnWindowFocus: false
});

const dataCustomerAddress: DataType[] | any = computed(() => {
  return (
    data?.value?.data?.data.map((e: any) => ({
      id: e.id || null,

  name: e.name || null,

  phoneNumber: e.phoneNumber || null,

  line: e.line || null,

  ward: e.ward || null,

  district: e.district || null,

  province: e.province || null,

  clientId: e.clientId || null,

  isDefault: e.isDefault || null
    })) || []
  );
});

// watch(
//   () => data?.value,
//   (newDataSource) => {
//   },
//   { immediate: true, deep: true }
// );

const handleSearch = (newValue: string) => {
  params.value.keyword = newValue;
  params.value.page = 1;
};

watch(current1, () => {
  params.value.page = current1.value === 0 ? 1 : current1.value;
});

const handleSelectCustomerAddress = (customerAddress: DataType) => {
  emit("selectCustomerAddress", customerAddress, dataCustomerAddress.value);
  handleClose();
};
</script>
  