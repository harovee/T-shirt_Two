<template>
  <a-modal :visible="open" @ok="handleClose" key="":width="'800px'">
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
      :data-source="dataCustomer"
      :pagination="false"
      :scroll="{ x: 300, y: 300 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'actions'">
          <a-button color="primary" @click="handleSelectCustomer(record)"
            >Chọn</a-button
          >
        </template>
        <template v-if="column.dataIndex === 'profilePicture'">
          <a-avatar
            shape="circle"
            :src="record.profilePicture"
            class="w-full h-full text-center scale-95 hover:scale-100 transition-all cursor-pointer"
          >
          </a-avatar>
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
import { useGetListCustomer } from "@/infrastructure/services/service/admin/payment.action";
import { convertDateFormatTime } from "@/utils/common.helper";
import { useRoute } from "vue-router";

const pageSize = ref(5);
const current1 = ref(1);

const props = defineProps<{ open: boolean }>();

const emit = defineEmits(["handleClose", "selectCustomer"]);

const handleClose = () => emit("handleClose");

interface DataType {
  key: string;
  profilePicture: string;
  name: string;
  phoneNumber: string;
  email: string;
}

const columns: TableColumnType<DataType>[] = [
  {
    title: "Ảnh",
    dataIndex: "profilePicture",
  },
  {
    title: "Tên",
    dataIndex: "name",
  },
  {
    title: "Số điện thoại",
    dataIndex: "phoneNumber",
  },
  {
    title: "Email",
    dataIndex: "email",
  },
  { title: "Hành động", dataIndex: "actions" },
];

const params = ref<FindKhachHangRequest>({
  page: 1,
  size: 5,
  keyword: "",
});

const { data } = useGetListCustomer(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataCustomer: DataType[] | any = computed(() => {
  return (
    data?.value?.data?.data.map((e: any) => ({
      key: e.id || "",
      name: e.name || "",
      phoneNumber: e.phoneNumber || "",
      email: e.email || "",
      profilePicture: e.profilePicture || "",
    })) || []
  );
});

const handleSearch = (newValue: string) => {
  params.value.keyword = newValue;
  params.value.page = 1;
};

watch(current1, () => {
  params.value.page = current1.value === 0 ? 1 : current1.value;
});

const handleSelectCustomer = (customer: DataType) => {
  emit("selectCustomer", customer, dataCustomer.value);
  handleClose();
};
</script>
  