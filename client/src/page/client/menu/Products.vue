<template>
  <div class="container mx-auto">
    <div class="grid grid-cols-12 gap-6">
      <div class="col-span-2 mt-10">
        <filter-product
          @filter="handleFilter"
        />
      </div>
      <div class="col-span-10">
        <div class="flex justify-center">
          <h1 class="text-3xl mt-8">Sản phẩm</h1>
        </div>
        <all-cart-product
          :data-source="products"
          :loading="isLoading || isFetching"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { FindProductClientRequest } from "@/infrastructure/services/api/client/clientproduct.api";
import { useGetAllProducts } from "@/infrastructure/services/service/client/productclient.action";
import AllCartProduct from "@/page/client/products/AllCartProduct.vue";
import FilterProduct from "@/page/client/products/FilterProduct.vue";
import { keepPreviousData } from "@tanstack/vue-query";
import { computed, ref, watch, onMounted } from "vue";
import { useSearchStore } from '@/infrastructure/stores/search';

const searchStore = useSearchStore();

const searchKey = computed(() => searchStore.searchKey || "");

const params = ref<FindProductClientRequest>({
  page: 1,
  size: 20,
  tenSanPham: searchKey.value,
  tenDanhMuc: "",
  tenChatLieu: "",
  tenKieuDang: "",
  tenThuongHieu: "",
  min: 0,
  max: null
});

const { data: allProduct, isFetching, isLoading, refetch } = useGetAllProducts(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData
});

const products = computed(() => allProduct?.value?.data?.data || []);

const handleFilter = (newParams: FindProductClientRequest) => {
  console.log('Received Filter Params:', newParams);
  
  // Ensure price range values are properly handled
  params.value = {
    ...params.value,
    ...newParams,
    min: newParams.min !== undefined ? newParams.min : params.value.min,
    max: newParams.max !== undefined ? newParams.max : params.value.max
  };
};

watch(() => searchStore.searchKey, (newSearchKey) => {
  if (params.value.tenSanPham !== newSearchKey) {
    params.value.tenSanPham = newSearchKey || "";
    refetch();
  }
}, { immediate: true });

watch(() => params.value, (newParams) => {
  console.log('Params changed:', newParams);
  refetch();
}, { deep: true });

onMounted(() => {
  if (searchKey.value) {
    params.value.tenSanPham = searchKey.value || "";
    refetch();
  }
});
</script>