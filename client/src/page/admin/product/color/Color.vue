<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
        <h3 class="text-2xl m-0">Quản lý màu sắc</h3>
      </div>
    <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
      
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24" />
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <search-color @filter="handleFilter"/>
    </div>
    
    <div class="rounded-xl p-4 rounded-xl border-2">
      <color-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
          @handleOpenModalCreateColor="handleOpenModalCreateColor"
          @handleOpenModalUpdateColor="handleOpenModalUpdateColor"
      />
    </div>
    <modal-create-update-color
    :key="id"
    :open="isOpenModalCreateUpdateColor"
    @handleClose="handleCloseModalCreateColor"
    @onCancel="isOpenModalCreateUpdateColor = false"
    :ColorDetail="colorDetail || null"
    :is-loading-detail="isLoadingDetail || false"
    :all-color-data="dataSource?.data"
  />
  </div>
  
</template>

<script lang="ts" setup>
  import SearchColor from "@/page/admin/product/color/SearchColor.vue";
  import {computed, ref, watch, provide} from "vue";
import {FindColorRequest, ColorResponse} from "@/infrastructure/services/api/admin/color.api";
import {useGetColors} from "@/infrastructure/services/service/admin/color.action";
import {keepPreviousData} from "@tanstack/vue-query";
import ColorTable from "@/page/admin/product/color/ColorTable.vue";
import ModalCreateUpdateColor from "@/page/admin/product/color/ModalCreateUpdateColor.vue";
import { useGetColor } from "@/infrastructure/services/service/admin/color.action";

const params = ref<FindColorRequest>({
  page: 1,
  size: 10
});

const {data: listColor, isLoading, isFetching} = useGetColors(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetColor(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value
});

const isOpenModalCreateUpdateColor = ref(false);

const handleOpenModalCreateColor = () => {
  isOpenModalCreateUpdateColor.value = true;
  id.value = null;
};

const handleOpenModalUpdateColor = (record: ColorResponse) => {
  id.value = record.id;
  isOpenModalCreateUpdateColor.value = true;
};

const handleCloseModalCreateColor = () => {
  isOpenModalCreateUpdateColor.value = false;
};

const handleFilter = (newParams: FindColorRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => listColor?.value?.data || []);



const handlePaginationChange = (newParams: FindColorRequest) => {
  params.value = {...params.value, ...newParams};
};

const colorDetail = computed(() =>
  id.value
    ? {
        ...dataDetail.value?.data?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalCreateUpdateColor, (newVal) => {
  if (newVal && id && id !== null) {
    console.log(dataDetail);
    refetch();
  }
});
</script>
