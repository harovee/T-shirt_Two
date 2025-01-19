<template>
  <div>
    <h1
      class="flex justify-center items-center text-1000 text-3xl font-semibold"
    >
      <span class="m-3 text-xxl">Quản lý chất liệu</span>
    </h1>
    <search-material @filter="handleFilter" />
    <div class="rounded-xl">
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
