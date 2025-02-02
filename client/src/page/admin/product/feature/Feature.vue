<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
        <h3 class="text-2xl m-0">Quản lý tính năng</h3>
      </div>
    <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
      
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24" />
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div><search-feature @filter="handleFilter"/>
    </div>
    
    <div class="rounded-xl p-4 rounded-xl border-2">
      <feature-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
          @handleOpenModalCreateFeature="handleOpenModalCreateFeature"
          @handleOpenModalUpdateFeature="handleOpenModalUpdateFeature"
      />
    </div>
    <modal-create-update-feature
    :key="id"
    :open="isOpenModalCreateUpdateFeature"
    @handleClose="handleCloseModalCreateFeature"
    @onCancel="isOpenModalCreateUpdateFeature = false"
    :FeatureDetail="featureDetail || null"
    :is-loading-detail="isLoadingDetail || false"
    :all-feature-data="dataSource?.data"
  />
  </div>
  
</template>

<script lang="ts" setup>
  import SearchFeature from "@/page/admin/product/feature/SearchFeature.vue";
  import {computed, ref, watch, provide} from "vue";
import {FindFeatureRequest, FeatureResponse} from "@/infrastructure/services/api/admin/feature.api";
import {useGetFeatures} from "@/infrastructure/services/service/admin/feature.action";
import {keepPreviousData} from "@tanstack/vue-query";
import FeatureTable from "@/page/admin/product/feature/FeatureTable.vue";
import ModalCreateUpdateFeature from "@/page/admin/product/feature/ModalCreateUpdateFeature.vue";
import { useGetFeature } from "@/infrastructure/services/service/admin/feature.action";

const params = ref<FindFeatureRequest>({
  page: 1,
  size: 10
});

const {data: listFeature, isLoading, isFetching} = useGetFeatures(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetFeature(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value
});

const isOpenModalCreateUpdateFeature = ref(false);

const handleOpenModalCreateFeature = () => {
  isOpenModalCreateUpdateFeature.value = true;
  id.value = null;
};

const handleOpenModalUpdateFeature = (record: FeatureResponse) => {
  id.value = record.id;
  isOpenModalCreateUpdateFeature.value = true;
};

const handleCloseModalCreateFeature = () => {
  isOpenModalCreateUpdateFeature.value = false;
};

const handleFilter = (newParams: FindFeatureRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => listFeature?.value?.data || []);



const handlePaginationChange = (newParams: FindFeatureRequest) => {
  params.value = {...params.value, ...newParams};
};

const featureDetail = computed(() =>
  id.value
    ? {
        ...dataDetail.value?.data?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalCreateUpdateFeature, (newVal) => {
  if (newVal && id && id !== null) {
    console.log(dataDetail);
    refetch();
  }
});
</script>
