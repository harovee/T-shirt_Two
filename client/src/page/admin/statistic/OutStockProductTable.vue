<template>

    <div>
        <Table
            :columns="[
                    { title: 'Mã', dataIndex: 'maSanPham', key: 'maSanPham', width: '150px' },
                    {
                    title: 'Tên',
                    dataIndex: 'tenSanPham',
                    key: 'tenSanPham',
                    customRender: ({ text }) => h(Tooltip, { title: text }, () => h('span', { class: 'ellipsis' }, text)),
                    },
                    { title: 'SL tồn', dataIndex: 'soLuong', key: 'soLuong', align: 'center' },
                ]"
            :loading="isLoading|| isFetching "
            :data-source="data?.data?.data"
            :pagination="false" 
            bordered />

            <a-pagination
                class="m-2"
                :pageSize="5"
                v-model:current="outStockProductParams.page"
                :total="data?.data.totalElements"
                />

    </div>

</template>

<script lang="ts" setup>
import { FindOutStockProductRequest } from '@/infrastructure/services/api/admin/statistic.api';
import { useGetOutStockProducts } from '@/infrastructure/services/service/admin/statistic.action';
import { keepPreviousData } from '@tanstack/vue-query';
import { ref, h, watch } from "vue";
import { Table, Tooltip } from "ant-design-vue";


const outStockProductParams = ref<FindOutStockProductRequest>({
  key: null,
  page: 1,
});

const {data, isLoading, isFetching} = useGetOutStockProducts(outStockProductParams, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});
watch(
    () => data.value,
    (newData) => {
      if (newData) {
          // console.log(newData)
      }
    },
    {immediate: true}
);

</script>