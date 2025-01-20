<template>
  <div>
    <a-space direction="line" class="flex justify-start items-center">
      <a-input-search
        class="m-2"
        v-model:value="params.keyword"
        placeholder="Tìm kiếm khách hàng"
        style="width: 200px"
        @update:value ="handleSearch"
      />
    </a-space>
   
  </div>
  <a-table
    :row-selection="rowSelection"
    :columns="columns"
    :data-source="dataSource"
    :pagination="false"
    :scroll="{ x: 300, y: 300 }"
  >
  </a-table>
  <a-pagination
      class="m-2"
      v-model:current="params.page"
      v-model:pageSize="params.size"
      :total="data?.data.totalElements"
    />
</template>
<script lang="ts" setup>
import type { TableProps, TableColumnType } from "ant-design-vue";
import {defineProps, computed, defineEmits, ref, watch } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  FindKhachHangRequest
} from "@/infrastructure/services/api/admin/voucher/voucher.api";
import { useGetListKhachHang } from "@/infrastructure/services/service/admin/voucher/voucher.action";

const pageSize = ref(5);
const current1 = ref(1);
defineProps<{
  data: Object,
  idKhachHangs: string[] | undefined
}>();

const emit = defineEmits(['update:idSanPhams']);

interface DataType {
  key: string;
  ten: string;
  sdt: string;
  email: string;
  ngaySinh: string;
}

const columns: TableColumnType<DataType>[] = [
  {
    title: "Tên",
    dataIndex: "ten",
  },
  {
    title: "Số điện thoại",
    dataIndex: "sdt",
  },
  {
    title: "Email",
    dataIndex: "email",
  },
  {
    title: "Ngày sinh",
    dataIndex: "ngaySinh",
  }
];

const params = ref<FindKhachHangRequest>({
  page: 1,
  size: 5,
  keyword: ""
});

const { data } = useGetListKhachHang(params, {
  refetchOnWindowFocus: false
});

const dataSource: DataType[] | any = computed(() => {
  return (
    data?.value?.data?.data.map((e: any) => ({
      key: e.id || "",
      ten: e.ten || "",
      sdt: e.sdt || "",
      email: e.email || "",
      ngaySinh: e.ngaySinh || "",
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


const rowSelection: TableProps["rowSelection"] = {
  onChange: (selectedRowKeys: string[] | any) => {
   if (selectedRowKeys.length == 0 ) {
    emit('update:idSanPhams', []);
   }else {
    const updatedIds = [...selectedRowKeys];
      emit("update:idSanPhams", updatedIds);
   }
  
  },
};

</script>
