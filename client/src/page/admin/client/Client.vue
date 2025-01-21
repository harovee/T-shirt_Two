<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
      <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48"/>
      <h3 class="text-2xl m-0">Quản lý khách hàng</h3>
    </div>
    <div class="p-4 rounded-xl border-2  flex flex-col gap-6">
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24"/>
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <client-filter
          @filter="handleFilter"
      />
    </div>
    <div class="rounded-xl">
      <client-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          @handleOpenModalCreate="handleOpenModalCreateClient"
          @handleCloseModalCreate="handleCloseModalCreateClient"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
      />
    </div>
  </div>
  <client-modal-c
      :open="isOpenModalCreateClient"
      @handleClose="handleCloseModalCreateClient"
      @onCancel="isOpenModalCreateClient = false"
  />
</template>

<script lang="ts" setup>

import {computed, ref, watch} from "vue";
import {FindClientRequest} from "@/infrastructure/services/api/admin/client.api.ts";
import {useGetClients} from "@/infrastructure/services/service/admin/client.action.ts";
import {keepPreviousData} from "@tanstack/vue-query";
import ClientFilter from "@/page/admin/client/ClientFilter.vue";
import ClientTable from "@/page/admin/client/ClientTable.vue";
import ClientModalC from "@/page/admin/client/ClientModalC.vue";

/*** Table - Pagination - Filter  ***/

const params = ref<FindClientRequest>({
  page: 1,
  size: 10
});

const {data, isLoading, isFetching} = useGetClients(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const handleFilter = (newParams: FindClientRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => data?.value?.data || []);

const handlePaginationChange = (newParams: FindClientRequest) => {
  params.value = {...params.value, ...newParams};
};

/*** Create Client ***/
const isOpenModalCreateClient = ref(false);

const handleOpenModalCreateClient = () => {
  isOpenModalCreateClient.value = true;
};

const handleCloseModalCreateClient = () => {
  isOpenModalCreateClient.value = false;
};

watch(
    () => data.value,
    (newData) => {
      if (newData) {

      }
    },
    {immediate: true}
);
</script>

<script lang="ts">
export default {
  name: 'admin client',
};
</script>