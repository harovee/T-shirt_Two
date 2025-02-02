<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
        <h3 class="text-2xl m-0">Quản lý tay áo</h3>
      </div>
    <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
      
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24" />
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <search-sleeve @filter="handleFilter"/>
    </div>
    <div class="rounded-xl p-4 rounded-xl border-2">
      <sleeve-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
          @handleOpenModalCreateSleeve="handleOpenModalCreateSleeve"
          @handleOpenModalUpdateSleeve="handleOpenModalUpdateSleeve"
      />
    </div>
    <modal-create-update-sleeve
    :key="id"
    :open="isOpenModalCreateUpdateSleeve"
    @handleClose="handleCloseModalCreateSleeve"
    @onCancel="isOpenModalCreateUpdateSleeve = false"
    :SleeveDetail="sleeveDetail || null"
    :is-loading-detail="isLoadingDetail || false"
    :all-sleeve-data="dataSource?.data"
  />
  </div>
  
</template>

<script lang="ts" setup>
  import SearchSleeve from "@/page/admin/product/sleeve/SearchSleeve.vue";
  import {computed, ref, watch, provide} from "vue";
import {FindSleeveRequest, SleeveResponse} from "@/infrastructure/services/api/admin/sleeve.api";
import {useGetSleeves} from "@/infrastructure/services/service/admin/sleeve.action";
import {keepPreviousData} from "@tanstack/vue-query";
import SleeveTable from "@/page/admin/product/sleeve/SleeveTable.vue";
import ModalCreateUpdateSleeve from "@/page/admin/product/sleeve/ModalCreateUpdateSleeve.vue";
import { useGetSleeve } from "@/infrastructure/services/service/admin/sleeve.action";

const params = ref<FindSleeveRequest>({
  page: 1,
  size: 10
});

const {data: listSleeve, isLoading, isFetching} = useGetSleeves(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetSleeve(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value
});

const isOpenModalCreateUpdateSleeve = ref(false);

const handleOpenModalCreateSleeve = () => {
  isOpenModalCreateUpdateSleeve.value = true;
  id.value = null;
};

const handleOpenModalUpdateSleeve = (record: SleeveResponse) => {
  id.value = record.id;
  isOpenModalCreateUpdateSleeve.value = true;
};

const handleCloseModalCreateSleeve = () => {
  isOpenModalCreateUpdateSleeve.value = false;
};

const handleFilter = (newParams: FindSleeveRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => listSleeve?.value?.data || []);



const handlePaginationChange = (newParams: FindSleeveRequest) => {
  params.value = {...params.value, ...newParams};
};

const sleeveDetail = computed(() =>
  id.value
    ? {
        ...dataDetail.value?.data?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalCreateUpdateSleeve, (newVal) => {
  if (newVal && id && id !== null) {
    console.log(dataDetail);
    refetch();
  }
});
</script>
