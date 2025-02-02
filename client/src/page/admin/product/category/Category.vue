<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
        <h3 class="text-2xl m-0">Quản lý danh mục</h3>
      </div>
    <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
      
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24" />
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <search-category @filter="handleFilter"/>
    </div>
    <div class="rounded-xl p-4 rounded-xl border-2">
      <category-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
          @handleOpenModalCreateCategory="handleOpenModalCreateCategory"
          @handleOpenModalUpdateCategory="handleOpenModalUpdateCategory"
      />
    </div>
    <modal-create-update-category
    :key="id"
    :open="isOpenModalCreateUpdateCategory"
    @handleClose="handleCloseModalCreateCategory"
    @onCancel="isOpenModalCreateUpdateCategory = false"
    :CategoryDetail="categoryDetail || null"
    :is-loading-detail="isLoadingDetail || false"
    :all-category-data="dataSource?.data"
  />
  </div>
  
</template>

<script lang="ts" setup>
  import SearchCategory from "@/page/admin/product/category/SearchCategory.vue";
  import {computed, ref, watch, provide} from "vue";
import {FindCategoryRequest, CategoryResponse} from "@/infrastructure/services/api/admin/category.api";
import {useGetCategorys} from "@/infrastructure/services/service/admin/category.action";
import {keepPreviousData} from "@tanstack/vue-query";
import CategoryTable from "@/page/admin/product/category/CategoryTable.vue";
import ModalCreateUpdateCategory from "@/page/admin/product/category/ModalCreateUpdateCategory.vue";
import { useGetCategory } from "@/infrastructure/services/service/admin/category.action";

const params = ref<FindCategoryRequest>({
  page: 1,
  size: 10
});

const {data: listCategory, isLoading, isFetching} = useGetCategorys(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetCategory(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value
});

const isOpenModalCreateUpdateCategory = ref(false);

const handleOpenModalCreateCategory = () => {
  isOpenModalCreateUpdateCategory.value = true;
  id.value = null;
};

const handleOpenModalUpdateCategory = (record: CategoryResponse) => {
  id.value = record.id;
  isOpenModalCreateUpdateCategory.value = true;
};

const handleCloseModalCreateCategory = () => {
  isOpenModalCreateUpdateCategory.value = false;
};

const handleFilter = (newParams: FindCategoryRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => listCategory?.value?.data || []);



const handlePaginationChange = (newParams: FindCategoryRequest) => {
  params.value = {...params.value, ...newParams};
};

const categoryDetail = computed(() =>
  id.value
    ? {
        ...dataDetail.value?.data?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalCreateUpdateCategory, (newVal) => {
  if (newVal && id && id !== null) {
    console.log(dataDetail);
    refetch();
  }
});
</script>
