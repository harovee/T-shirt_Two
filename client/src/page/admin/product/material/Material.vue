<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
        <h3 class="text-2xl m-0">Quản lý chất liệu</h3>
      </div>
    <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
      
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24" />
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <search-material @filter="handleFilter" />
    </div>
    
    <div class="rounded-xl p-4 rounded-xl border-2">
      <material-table
        :data-source="dataSource"
        :loading="isLoading || isFetching"
        :pagination-params="params"
        @update:pagination-params="handlePaginationChange"
        @handleOpenModalCreateMaterial="handleOpenModalCreateMaterial"
        @handleOpenModalUpdateMaterial="handleOpenModalUpdateMaterial"
      />
    </div>
    <modal-create-update-material
      :key="id"
      :open="isOpenModalCreateUpdateMaterial"
      @handleClose="handleCloseModalCreateMaterial"
      @onCancel="isOpenModalCreateUpdateMaterial = false"
      :MaterialDetail="materialDetail || null"
      :is-loading-detail="isLoadingDetail || false"
      :all-material-data="dataSource?.data"
    />
  </div>
</template>

<script lang="ts" setup>
import SearchMaterial from "@/page/admin/product/material/SearchMaterial.vue";
import { computed, ref, watch, provide } from "vue";
import {
  FindMaterialRequest,
  MaterialResponse,
} from "@/infrastructure/services/api/admin/material.api";
import { useGetMaterials } from "@/infrastructure/services/service/admin/material.action";
import { keepPreviousData } from "@tanstack/vue-query";
import MaterialTable from "@/page/admin/product/material/MaterialTable.vue";
import ModalCreateUpdateMaterial from "@/page/admin/product/material/ModalCreateUpdateMaterial.vue";
import { useGetMaterial } from "@/infrastructure/services/service/admin/material.action";
import { log } from "console";

const params = ref<FindMaterialRequest>({
  page: 1,
  size: 10,
});

const {
  data: listMaterial,
  isLoading,
  isFetching,
} = useGetMaterials(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetMaterial(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value,
});

const isOpenModalCreateUpdateMaterial = ref(false);

const handleOpenModalCreateMaterial = () => {
  isOpenModalCreateUpdateMaterial.value = true;
  id.value = null;
};

const handleOpenModalUpdateMaterial = (record: MaterialResponse) => {
  id.value = record.id;
  isOpenModalCreateUpdateMaterial.value = true;
};

const handleCloseModalCreateMaterial = () => {
  isOpenModalCreateUpdateMaterial.value = false;
};

const handleFilter = (newParams: FindMaterialRequest) => {
  params.value = { ...params.value, ...newParams };
};

const dataSource = computed(() => listMaterial?.value?.data || []);

const handlePaginationChange = (newParams: FindMaterialRequest) => {
  params.value = { ...params.value, ...newParams };
};

const materialDetail = computed(() =>
  id.value
    ? {
        ...dataDetail.value?.data?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalCreateUpdateMaterial, (newVal) => {
  console.log("id", id);
  console.log("newVal", newVal);
  console.log("id && id !== null", id && id !== null);
  if (id && id !== null) {
    console.log("dataDetail", dataDetail);
    refetch();
  }
});
</script>
