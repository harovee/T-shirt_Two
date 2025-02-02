<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
        <h3 class="text-2xl m-0">Quản lý kích cỡ</h3>
      </div>
    <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
      
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24" />
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <search-size @filter="handleFilter"/>
    </div>
    
    <div class="rounded-xl p-4 rounded-xl border-2">
      <size-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
          @handleOpenModalCreateSize="handleOpenModalCreateSize"
          @handleOpenModalUpdateSize="handleOpenModalUpdateSize"
      />
    </div>
    <modal-create-update-size
    :key="id"
    :open="isOpenModalCreateUpdateSize"
    @handleClose="handleCloseModalCreateSize"
    @onCancel="isOpenModalCreateUpdateSize = false"
    :SizeDetail="sizeDetail || null"
    :is-loading-detail="isLoadingDetail || false"
    :all-size-data="dataSource?.data"
  />
  </div>
  
</template>

<script lang="ts" setup>
  import SearchSize from "@/page/admin/product/size/SearchSize.vue";
  import {computed, ref, watch, provide} from "vue";
import {FindSizeRequest, SizeResponse} from "@/infrastructure/services/api/admin/size.api";
import {useGetSizes} from "@/infrastructure/services/service/admin/size.action";
import {keepPreviousData} from "@tanstack/vue-query";
import SizeTable from "@/page/admin/product/size/SizeTable.vue";
import ModalCreateUpdateSize from "@/page/admin/product/size/ModalCreateUpdateSize.vue";
import { useGetSize } from "@/infrastructure/services/service/admin/size.action";

const params = ref<FindSizeRequest>({
  page: 1,
  size: 10
});

const {data: listSize, isLoading, isFetching} = useGetSizes(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetSize(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value
});

const isOpenModalCreateUpdateSize = ref(false);

const handleOpenModalCreateSize = () => {
  isOpenModalCreateUpdateSize.value = true;
  id.value = null;
};

const handleOpenModalUpdateSize = (record: SizeResponse) => {
  id.value = record.id;
  isOpenModalCreateUpdateSize.value = true;
};

const handleCloseModalCreateSize = () => {
  isOpenModalCreateUpdateSize.value = false;
};

const handleFilter = (newParams: FindSizeRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => listSize?.value?.data || []);



const handlePaginationChange = (newParams: FindSizeRequest) => {
  params.value = {...params.value, ...newParams};
};

const sizeDetail = computed(() =>
  id.value
    ? {
        ...dataDetail.value?.data?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalCreateUpdateSize, (newVal) => {
  if (newVal && id && id !== null) {
    console.log(dataDetail);
    refetch();
  }
});
</script>
