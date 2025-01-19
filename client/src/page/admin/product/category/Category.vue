<template>
  <div>
    <h1
      class="flex justify-center items-center text-1000 text-3xl font-semibold"
    >
      <span class="m-3 text-xxl">Quản lý danh mục</span>
    </h1>
    <search-category @filter="handleFilter"/>
    <div class="rounded-xl">
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
