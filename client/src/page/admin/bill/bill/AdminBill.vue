<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
      <v-icon
        name="md-switchaccount-round"
        size="x-large"
        width="48"
        height="48"
      />
      <h3 class="text-2xl m-0">Quản lý hóa đơn</h3>
    </div>

    <div
      class="p-4 rounded-xl border-2  flex flex-col gap-6"
    >
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24" />
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <admin-bill-filter @filter="handleFilter"/>
    </div>

    <div class="rounded-xl">
      <admin-bill-table
        :data-source="dataSource"
        :loading="isLoading || isFetching"
        :pagination-params="params"
        @update:pagination-params="handlePaginationChange"
      />

    </div>
    
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import AdminBillFilter from "./AdminBillFilter.vue";
import { FindBillRequest } from "@/infrastructure/services/api/admin/bill.api";
import { useGetBills } from "@/infrastructure/services/service/admin/bill.action";
import AdminBillTable from "./AdminBillTable.vue";

/*** Table - Pagination - Filter  ***/

const params = ref<FindBillRequest>({
  page: 1,
  size: 10,
});

const { data, isLoading, isFetching } = useGetBills(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});


const handleFilter = (newParams: FindBillRequest) => {
  params.value = { ...params.value, ...newParams };
};

const dataSource = computed(() => data?.value?.data || []);


const handlePaginationChange = (newParams: FindBillRequest) => {
  params.value = { ...params.value, ...newParams };
};



watch(
  () => data.value,
  (newData) => {
    if (newData) {
        // console.log(newData);   
    }
  },
  { immediate: true }
);
</script>
