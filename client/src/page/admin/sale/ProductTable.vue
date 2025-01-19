<template>
  <div>
    <a-space class="flex justify-start items-center">
        <a-input-search
        class="m-2"
        v-model:value="params.key"
        placeholder="tên sản phẩm"
        style="width: 200px"
        @change="handleChangeKey"
        />
        <a-select
        v-model:value="params.idDanhMuc"
        style="width: 200px;"
        >
            <a-select-option :value="null">Tất cả</a-select-option>
            <a-select-option v-for="item in categories"
             :value="item.id">{{ item.ten }}</a-select-option>
        </a-select>
    </a-space>
   
  </div>
  <a-table
    :loading="isLoading"
    :row-selection="rowSelection"
    :columns="columns"
    :data-source="dataSource"
    :pagination="false"
    :scroll="{ x: 300, y: 300 }"
  >
  </a-table>
  <a-pagination
      class="m-2"
      v-model:current="current1"
      v-model:pageSize="pageSize"
      :total="data?.data.totalElements"
    />
</template>
<script lang="ts" setup>
import type { TableProps, TableColumnType } from "ant-design-vue";
import {defineProps, computed, defineEmits, ref, watch } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  FindProductRequest,
  AttributeResponse
} from "@/infrastructure/services/api/admin/sale.api.ts";
import { useGetProducts } from "@/infrastructure/services/service/admin/sale.action.ts";

const pageSize = ref(5);
const current1 = ref(1);
defineProps<{
  categories: AttributeResponse[] | undefined,
  idSanPhams: string[] | undefined
}>();

const emit = defineEmits(['update:idSanPhams']);

interface DataType {
  key: string;
  ten: string;
  tenDanhMuc: string;
}

const columns: TableColumnType<DataType>[] = [
  {
    title: "Tên",
    dataIndex: "ten",
  },
  {
    title: "Tên Danh Mục",
    dataIndex: "tenDanhMuc",
  },
  {
    title: "Tổng số lượng",
    dataIndex: "tongSoLuong",
    align: 'center',
  }

];

const params = ref<FindProductRequest>({
  page: 1,
  size: 5,
  key: null,
  idDanhMuc: null,
});

const { data, isLoading } = useGetProducts(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataSource: DataType[] | any = computed(() => {
  return (
    data?.value?.data?.data.map((e: any) => ({
      key: e.id || "",
      ten: e.ten || "",
      tenDanhMuc: e.tenDanhMuc || "",
      tongSoLuong: e.tongSoLuong || "",
    })) || []
  );
});

const handleChangeKey = () =>{
  current1.value = 1;
}
watch(current1, () => {
  params.value.page = current1.value == 0 ? 1 : current1.value;
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
