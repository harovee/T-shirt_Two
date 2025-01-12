<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
      <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48"/>
      <h3 class="text-2xl m-0">Quản lý đợt giảm giá</h3>
    </div>
    <div class="p-4 rounded-xl border-2 shadow-purple-500 shadow-xl flex flex-col gap-6">
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24"/>
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <SaleFilter
          @filter="handleFilter"
      />
    </div>
    <div class="rounded-xl">
      <SaleTableTable
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          @handleOpenModalCreate="handleOpenModalCreateClient"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>

import {computed, ref, watch} from "vue";
import {FindSaleRequest} from "@/infrastructure/services/api/admin/sale.api.ts";
import {useGetSales} from "@/infrastructure/services/service/admin/sale.action.ts";
import {keepPreviousData} from "@tanstack/vue-query";
import SaleFilter from "@/page/admin/sale/SaleFilter.vue";
import SaleTableTable from "@/page/admin/sale/SaleTable.vue";

/*** Table - Pagination - Filter  ***/

const params = ref<FindSaleRequest>({
  page: 1,
  size: 10
});

const {data, isLoading, isFetching} = useGetSales(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const handleFilter = (newParams: FindSaleRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => data?.value?.data || []);

const handlePaginationChange = (newParams: FindSaleRequest) => {
  params.value = {...params.value, ...newParams};
};

const handleOpenModalCreateClient = () => {
  console.log("Chuyển trang thêm sale")
};

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

<script lang="ts">
export default {
  name: 'admin-sale',
};
</script>