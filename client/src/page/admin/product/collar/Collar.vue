<template>
  <div>
    <h1
      class="flex justify-center items-center text-1000 text-3xl font-semibold"
    >
      <span class="m-3 text-xxl">Quản lý cổ áo</span>
    </h1>
    <search-collar @filter="handleFilter"/>
    <div class="rounded-xl">
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
