<template>
  <div>
    <a-space class="flex justify-start items-center">
      <a-input-search
        class="m-2"
        v-model:value="params.keyword"
        placeholder="Tìm kiếm khách hàng"
        style="width: 200px"
        @search="handleSearch"
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
    :total="data?.data?.totalElements"
  />
</template>

<script lang="ts" setup>
import type { TableProps, TableColumnType } from "ant-design-vue";
import type { TableRowSelection, Key } from "ant-design-vue/es/table/interface";
import { defineProps, computed, defineEmits, ref, watch } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import { FindKhachHangRequest } from "@/infrastructure/services/api/admin/voucher/voucher.api";
import { useGetListKhachHang } from "@/infrastructure/services/service/admin/voucher/voucher.action";
import { convertDateFormat, convertDateFormatTime, getDateFormat } from "@/utils/common.helper";

const props = defineProps<{
  data: any;
  idKhachHangs: string[];
}>();

const emit = defineEmits<{
  'update:idKhachHangs': [ids: string[]];
}>();

interface DataType {
  key: string;
  name: string;
  phone: string;
  email: string;
  ngaySinh: string;
}

const columns: TableColumnType<DataType>[] = [
  {
    title: "Tên",
    dataIndex: "name",
  },
  {
    title: "Số điện thoại",
    dataIndex: "phone",
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
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData
});

const dataSource = computed(() => {
  return (
    data?.value?.data?.data.map((e: any) => ({
      key: e.id || "",
      name: e.name || "",
      phone: e.phone || "",
      email: e.email || "",
      ngaySinh: e.ngaySinh ? getDateFormat(e.ngaySinh) : "",
    })) || []
  );
});

const handleSearch = (value: string) => {
  params.value.keyword = value;
  params.value.page = 1;
};

watch(() => params.value.page, (newPage) => {
  params.value.page = newPage === 0 ? 1 : newPage;
});

const rowSelection = computed<TableRowSelection<DataType>>(() => {
  return {
    selectedRowKeys: props.idKhachHangs as Key[],
    onChange: (selectedRowKeys: Key[], selectedRows: DataType[]) => {
      emit('update:idKhachHangs', selectedRowKeys as string[]);
    },
    preserveSelectedRowKeys: true
  };
});
</script>