<template>
  <div>
    <h1
      class="flex justify-center items-center text-1000 text-3xl font-semibold"
    >
      <span class="m-3 text-xxl">Quản lý kiểu dáng</span>
    </h1>
    <search-style @filter="handleFilter"/>
    <div class="rounded-xl">
      <style-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
          @handleOpenModalCreateStyle="handleOpenModalCreateStyle"
          @handleOpenModalUpdateStyle="handleOpenModalUpdateStyle"
      />
    </div>
    <modal-create-update-style
    :key="id"
    :open="isOpenModalCreateUpdateStyle"
    @handleClose="handleCloseModalCreateStyle"
    @onCancel="isOpenModalCreateUpdateStyle = false"
    :StyleDetail="styleDetail || null"
    :is-loading-detail="isLoadingDetail || false"
    :all-style-data="dataSource?.data"
  />
  </div>
  
</template>

<script lang="ts" setup>
  import SearchStyle from "@/page/admin/product/style/SearchStyle.vue";
  import {computed, ref, watch, provide} from "vue";
import {FindStyleRequest, StyleResponse} from "@/infrastructure/services/api/admin/style.api";
import {useGetStyles} from "@/infrastructure/services/service/admin/style.action";
import {keepPreviousData} from "@tanstack/vue-query";
import StyleTable from "@/page/admin/product/style/StyleTable.vue";
import ModalCreateUpdateStyle from "@/page/admin/product/style/ModalCreateUpdateStyle.vue";
import { useGetStyle } from "@/infrastructure/services/service/admin/style.action";

const params = ref<FindStyleRequest>({
  page: 1,
  size: 10
});

const {data: listStyle, isLoading, isFetching} = useGetStyles(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetStyle(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value
});

const isOpenModalCreateUpdateStyle = ref(false);

const handleOpenModalCreateStyle = () => {
  isOpenModalCreateUpdateStyle.value = true;
  id.value = null;
};

const handleOpenModalUpdateStyle = (record: StyleResponse) => {
  id.value = record.id;
  isOpenModalCreateUpdateStyle.value = true;
};

const handleCloseModalCreateStyle = () => {
  isOpenModalCreateUpdateStyle.value = false;
};

const handleFilter = (newParams: FindStyleRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => listStyle?.value?.data || []);



const handlePaginationChange = (newParams: FindStyleRequest) => {
  params.value = {...params.value, ...newParams};
};

const styleDetail = computed(() =>
  id.value
    ? {
        ...dataDetail.value?.data?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalCreateUpdateStyle, (newVal) => {
  if (newVal && id && id !== null) {
    console.log(dataDetail);
    refetch();
  }
});
</script>
