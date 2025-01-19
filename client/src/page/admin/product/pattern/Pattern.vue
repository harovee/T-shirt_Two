<template>
  <div>
    <h1
      class="flex justify-center items-center text-1000 text-3xl font-semibold"
    >
      <span class="m-3 text-xxl">Quản lý họa tiết</span>
    </h1>
    <search-pattern @filter="handleFilter"/>
    <div class="rounded-xl">
      <pattern-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
          @handleOpenModalCreatePattern="handleOpenModalCreatePattern"
          @handleOpenModalUpdatePattern="handleOpenModalUpdatePattern"
      />
    </div>
    <modal-create-update-pattern
    :key="id"
    :open="isOpenModalCreateUpdatePattern"
    @handleClose="handleCloseModalCreatePattern"
    @onCancel="isOpenModalCreateUpdatePattern = false"
    :PatternDetail="patternDetail || null"
    :is-loading-detail="isLoadingDetail || false"
    :all-pattern-data="dataSource?.data"
  />
  </div>
  
</template>

<script lang="ts" setup>
  import SearchPattern from "@/page/admin/product/pattern/SearchPattern.vue";
  import {computed, ref, watch, provide} from "vue";
import {FindPatternRequest, PatternResponse} from "@/infrastructure/services/api/admin/pattern.api";
import {useGetPatterns} from "@/infrastructure/services/service/admin/pattern.action";
import {keepPreviousData} from "@tanstack/vue-query";
import PatternTable from "@/page/admin/product/pattern/PatternTable.vue";
import ModalCreateUpdatePattern from "@/page/admin/product/pattern/ModalCreateUpdatePattern.vue";
import { useGetPattern } from "@/infrastructure/services/service/admin/pattern.action";

const params = ref<FindPatternRequest>({
  page: 1,
  size: 10
});

const {data: listPattern, isLoading, isFetching} = useGetPatterns(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetPattern(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value
});

const isOpenModalCreateUpdatePattern = ref(false);

const handleOpenModalCreatePattern = () => {
  isOpenModalCreateUpdatePattern.value = true;
  id.value = null;
};

const handleOpenModalUpdatePattern = (record: PatternResponse) => {
  id.value = record.id;
  isOpenModalCreateUpdatePattern.value = true;
};

const handleCloseModalCreatePattern = () => {
  isOpenModalCreateUpdatePattern.value = false;
};

const handleFilter = (newParams: FindPatternRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => listPattern?.value?.data || []);



const handlePaginationChange = (newParams: FindPatternRequest) => {
  params.value = {...params.value, ...newParams};
};

const patternDetail = computed(() =>
  id.value
    ? {
        ...dataDetail.value?.data?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalCreateUpdatePattern, (newVal) => {
  if (newVal && id && id !== null) {
    console.log(dataDetail);
    refetch();
  }
});
</script>
