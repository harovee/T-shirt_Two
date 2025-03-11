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
import { computed, ref, watch } from "vue";


const params= ref<FindProductClientRequest>({
    page: 1,
    size: 20,
    tenSanPham: "",
    tenDanhMuc: "",
    tenChatLieu: "",
    tenKieuDang: "",
    tenThuongHieu: "",
    khoangGia: null
});


const { data: allProduct, isFetching, isLoading, refetch} = useGetAllProducts(params,{
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData
});

const products = computed(() => allProduct?.value?.data?.data || []);


const handleFilter = (newParams: FindProductClientRequest) => {
  console.log('Received Filter Params:', newParams);
  params.value = {...params.value, ...newParams};
};


watch(() => params.value, (newdata) => {
  if (newdata) {
    console.log(newdata);
    
  }
}, {deep: true});
</script>
