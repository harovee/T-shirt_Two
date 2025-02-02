<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
        <h3 class="text-2xl m-0">Quản lý cổ áo</h3>
      </div>
    <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
      
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24" />
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <search-collar @filter="handleFilter"/>
    </div>
    
    <div class="rounded-xl p-4 rounded-xl border-2">
      <collar-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
          @handleOpenModalCreateCollar="handleOpenModalCreateCollar"
          @handleOpenModalUpdateCollar="handleOpenModalUpdateCollar"
      />
    </div>
    <modal-create-update-collar
    :key="id"
    :open="isOpenModalCreateUpdateCollar"
    @handleClose="handleCloseModalCreateCollar"
    @onCancel="isOpenModalCreateUpdateCollar = false"
    :CollarDetail="collarDetail || null"
    :is-loading-detail="isLoadingDetail || false"
    :all-collar-data="dataSource?.data"
  />
  </div>
  
</template>

<script lang="ts" setup>
  import SearchCollar from "@/page/admin/product/collar/SearchCollar.vue";
  import {computed, ref, watch, provide} from "vue";
import {FindCollarRequest, CollarResponse} from "@/infrastructure/services/api/admin/collar.api";
import {useGetCollars} from "@/infrastructure/services/service/admin/collar.action";
import {keepPreviousData} from "@tanstack/vue-query";
import CollarTable from "@/page/admin/product/collar/CollarTable.vue";
import ModalCreateUpdateCollar from "@/page/admin/product/collar/ModalCreateUpdateCollar.vue";
import { useGetCollar } from "@/infrastructure/services/service/admin/collar.action";

const params = ref<FindCollarRequest>({
  page: 1,
  size: 10
});

const {data: listCollar, isLoading, isFetching} = useGetCollars(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetCollar(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value
});

const isOpenModalCreateUpdateCollar = ref(false);

const handleOpenModalCreateCollar = () => {
  isOpenModalCreateUpdateCollar.value = true;
  id.value = null;
};

const handleOpenModalUpdateCollar = (record: CollarResponse) => {
  id.value = record.id;
  isOpenModalCreateUpdateCollar.value = true;
};

const handleCloseModalCreateCollar = () => {
  isOpenModalCreateUpdateCollar.value = false;
};

const handleFilter = (newParams: FindCollarRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => listCollar?.value?.data || []);



const handlePaginationChange = (newParams: FindCollarRequest) => {
  params.value = {...params.value, ...newParams};
};

const collarDetail = computed(() =>
  id.value
    ? {
        ...dataDetail.value?.data?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalCreateUpdateCollar, (newVal) => {
  if (newVal && id && id !== null) {
    console.log(dataDetail);
    refetch();
  }
});
</script>
