<template>
  <div>
    <a-space class="flex justify-start items-center">
      <a-input-search
        class="m-2"
        v-model:value="params.keyword"
        placeholder="Tìm kiếm khách hàng"
        style="width: 200px"
        @change:value ="handleSearch"
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
import {defineProps, computed, defineEmits, ref, watch, onMounted } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  FindKhachHangRequest
} from "@/infrastructure/services/api/admin/voucher/voucher.api";
import { useGetListKhachHang } from "@/infrastructure/services/service/admin/voucher/voucher.action";
import { convertDateFormatTime } from "@/utils/common.helper";
import { Key } from "ant-design-vue/es/_util/type";

const pageSize = ref(5);
const current1 = ref(1);
const props = defineProps<{
  data: Object,
  idKhachHangs: string[] | undefined,
  disable: boolean | false,
}>();

const emit = defineEmits(['update:idKhachHangs']);

interface DataType {
  key: string;
  name: string;
  phone: string;
  email: string;
  ngaySinh: number;
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
  placeholderData:keepPreviousData
});

const dataSource: DataType[] | any = computed(() => {
  return (
    data?.value?.data?.data.map((e: any) => ({
      key: e.id || "",
      name: e.name || "",
      phone: e.phone || "",
      email: e.email || "",
      ngaySinh: e.ngaySinh ? convertDateFormatTime(e.ngaySinh) : "" ,
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

const selectedRowKeys = ref<Key[]>(props.idKhachHangs || []);

const rowSelection: TableProps["rowSelection"] = {
  selectedRowKeys: selectedRowKeys.value, // Gán danh sách đã chọn
  onChange: (selectedKeys: Key[]) => {
    selectedRowKeys.value = selectedKeys;
    emit("update:idKhachHangs", selectedKeys.map(String));
  },
};

watch(() => props.idKhachHangs, (newVal) => {
  selectedRowKeys.value = newVal || [];
}, { deep: true });

</script>
