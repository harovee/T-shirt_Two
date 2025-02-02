<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <!-- <div class="flex justify-center items-center gap-2"> -->
      <!-- <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48"/> -->
      <!-- <h3 class="text-2xl m-0 text-center">Quản lý phiếu giảm giá</h3>
    </div> -->
    <div class="flex items-center gap-2">
      <v-icon
        name="io-pricetag-outline"
        size="x-large"
        width="48"
        height="48"
      />
      <h3 class="text-2xl m-0">Quản lý phiếu giảm giá</h3>
    </div>
    <div class="p-4 rounded-xl border-2 shadow-purple-950 flex flex-col gap-6">
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24"/>
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <voucher-filter
          @filter="handleFilter"
      />
    </div>
    <div class="rounded-xl">
      <voucher-table
          :data-source="VoucherData"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>

import {computed, ref, watch} from "vue";
import {FindVoucherRequest, VoucherResponse} from "@/infrastructure/services/api/admin/voucher/voucher.api";
import {useGetListVoucher, useGetVoucherById} from "@/infrastructure/services/service/admin/voucher/voucher.action";
import {keepPreviousData} from "@tanstack/vue-query";
import VoucherFilter from "@/page/admin/voucher/VoucherFilter.vue";
import VoucherTable from "@/page/admin/voucher/VoucherTable.vue";

/*** Table - Pagination - Filter  ***/

const params = ref<FindVoucherRequest>({
  page: 1,
  size: 10
});

const {data, isLoading, isFetching} = useGetListVoucher(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const handleFilter = (newParams: FindVoucherRequest) => {
  params.value = {...params.value, ...newParams};
};

const VoucherData = computed(() => data?.value?.data|| []);


const handlePaginationChange = (newParams: FindVoucherRequest) => {
  params.value = {...params.value, ...newParams};
};

</script>

<script lang="ts">
export default {
  name: 'admin-voucher',
};
</script>
