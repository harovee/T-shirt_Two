<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
        <h3 class="text-2xl m-0">Quản lý thương hiệu</h3>
      </div>
    <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
      
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24" />
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <search-trademark @filter="handleFilter"/>
    </div>
    
    <div class="rounded-xl p-4 rounded-xl border-2">
      <trademark-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
          @handleOpenModalCreateTrademark="handleOpenModalCreateTrademark"
          @handleOpenModalUpdateTrademark="handleOpenModalUpdateTrademark"
      />
    </div>
    <modal-create-update-trademark
    :key="id"
    :open="isOpenModalCreateUpdateTrademark"
    @handleClose="handleCloseModalCreateTrademark"
    @onCancel="isOpenModalCreateUpdateTrademark = false"
    :TrademarkDetail="trademarkDetail || null"
    :is-loading-detail="isLoadingDetail || false"
    :all-trademark-data="dataSource?.data"
  />
  </div>
  
</template>

<script lang="ts" setup>
  import SearchTrademark from "@/page/admin/product/trademark/SearchTrademark.vue";
  import {computed, ref, watch, provide} from "vue";
import {FindTrademarkRequest, TrademarkResponse} from "@/infrastructure/services/api/admin/trademark.api";
import {useGetTrademarks} from "@/infrastructure/services/service/admin/trademark.action";
import {keepPreviousData} from "@tanstack/vue-query";
import TrademarkTable from "@/page/admin/product/trademark/TrademarkTable.vue";
import ModalCreateUpdateTrademark from "@/page/admin/product/trademark/ModalCreateUpdateTrademark.vue";
import { useGetTrademark } from "@/infrastructure/services/service/admin/trademark.action";

const params = ref<FindTrademarkRequest>({
  page: 1,
  size: 10
});

const {data: listTrademark, isLoading, isFetching} = useGetTrademarks(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetTrademark(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value
});

const isOpenModalCreateUpdateTrademark = ref(false);

const handleOpenModalCreateTrademark = () => {
  isOpenModalCreateUpdateTrademark.value = true;
  id.value = null;
};

const handleOpenModalUpdateTrademark = (record: TrademarkResponse) => {
  id.value = record.id;
  isOpenModalCreateUpdateTrademark.value = true;
};

const handleCloseModalCreateTrademark = () => {
  isOpenModalCreateUpdateTrademark.value = false;
};

const handleFilter = (newParams: FindTrademarkRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => listTrademark?.value?.data || []);



const handlePaginationChange = (newParams: FindTrademarkRequest) => {
  params.value = {...params.value, ...newParams};
};

const trademarkDetail = computed(() =>
  id.value
    ? {
        ...dataDetail.value?.data?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalCreateUpdateTrademark, (newVal) => {
  if (newVal && id && id !== null) {
    console.log(dataDetail);
    refetch();
  }
});
</script>
