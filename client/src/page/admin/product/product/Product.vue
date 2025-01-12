<template>
  <div>
    <h1
      class="flex justify-center items-center text-1000 text-3xl font-semibold"
    >
      <span class="m-3 text-xxl">Quản lý sản phẩm</span>
    </h1>
    <search-san-pham @filter="handleFilter"/>
    <div class="rounded-xl">
      <product-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
      />
    </div>
  </div>
  
</template>

<script lang="ts" setup>
  import SearchSanPham from "@/page/admin/product/product/SearchSanPham.vue";
  import {computed, ref, watch, provide} from "vue";
import {FindProductRequest} from "@/infrastructure/services/api/admin/product.api";
import {useGetProduct} from "@/infrastructure/services/service/admin/product.action";
import {keepPreviousData} from "@tanstack/vue-query";
import ProductTable from "@/page/admin/product/product/ProductTable.vue";

const params = ref<FindProductRequest>({
  page: 1,
  size: 10
});

const {data, isLoading, isFetching} = useGetProduct(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const handleFilter = (newParams: FindProductRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => data?.value?.data || []);

const handlePaginationChange = (newParams: FindProductRequest) => {
  params.value = {...params.value, ...newParams};
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
