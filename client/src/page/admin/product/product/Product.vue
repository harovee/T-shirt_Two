<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
        <v-icon
          name="md-widgets-outlined"
          size="x-large"
          width="48"
          height="48"
        />
        <h3 class="text-2xl m-0">Quản lý sản phẩm</h3>
      </div>
    <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
      
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24" />
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <search-san-pham @filter="handleFilter" />
    </div>
    <!-- <h1
      class="flex justify-center items-center text-1000 text-3xl font-semibold"
    >
      <span class="m-3 text-xxl">Quản lý sản phẩm</span>
    </h1> -->

    <div class="rounded-xl p-4 rounded-xl border-2">
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
import { computed, ref, watch, provide } from "vue";
import { FindProductRequest } from "@/infrastructure/services/api/admin/product.api";
import { useGetProduct } from "@/infrastructure/services/service/admin/product.action";
import { keepPreviousData } from "@tanstack/vue-query";
import ProductTable from "@/page/admin/product/product/ProductTable.vue";

const params = ref<FindProductRequest>({
  page: 1,
  size: 10,
});

const { data, isLoading, isFetching } = useGetProduct(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const handleFilter = (newParams: FindProductRequest) => {
  params.value = { ...params.value, ...newParams };
};

const dataSource = computed(() => data?.value?.data || []);

const handlePaginationChange = (newParams: FindProductRequest) => {
  params.value = { ...params.value, ...newParams };
};

watch(
  () => data.value,
  (newData) => {
    if (newData) {
    }
  },
  { immediate: true }
);
</script>
