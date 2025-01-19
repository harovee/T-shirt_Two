<template>
  <div>
    <search-product-detail @filter="handleFilter"/>
    <div class="rounded-xl">
      <product-detail-table-new
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
  import SearchProductDetail from "@/page/admin/product/product-detail/SearchProductDetail.vue";
  import ProductDetailTableNew from "@/page/admin/product/product-detail/ProductDetailTableNew.vue";
  import {computed, ref, watch, provide} from "vue";
  import {useRoute} from "vue-router";
import {FindProductDetailRequest} from "@/infrastructure/services/api/admin/product_detail.api";
import {useGetProductDetail} from "@/infrastructure/services/service/admin/productdetail.action";
import {keepPreviousData} from "@tanstack/vue-query";
import {
  useGetListMaterial,
  useCreateMaterial,
} from "@/infrastructure/services/service/admin/material.action";
import {
  useGetListCollar,
  useCreateCollar,
} from "@/infrastructure/services/service/admin/collar.action";
import {
  useGetListColor,
  useCreateColor,
} from "@/infrastructure/services/service/admin/color.action";
import {
  useGetListFeature,
  useCreateFeature,
} from "@/infrastructure/services/service/admin/feature.action";
import {
  useGetListPattern,
  useCreatePattern,
} from "@/infrastructure/services/service/admin/pattern.action";
import {
  useGetListSize,
  useCreateSize,
} from "@/infrastructure/services/service/admin/size.action";
import {
  useGetListSleeve,
  useCreateSleeve,
} from "@/infrastructure/services/service/admin/sleeve.action";
import {
  useGetListStyle,
  useCreateStyle,
} from "@/infrastructure/services/service/admin/style.action";
import {
  useGetListTrademark,
  useCreateTrademark,
} from "@/infrastructure/services/service/admin/trademark.action";
import { useGetListProduct } from "@/infrastructure/services/service/admin/product.action";

const route = useRoute();

const productId = ref<string>(String(route.params.id));

  const params = ref<FindProductDetailRequest>({
  page: 1,
  size: 10,
  idSanPham: productId.value
});

const {data, isLoading, isFetching} = useGetProductDetail(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const handleFilter = (newParams: FindProductDetailRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => data?.value?.data || []);

const handlePaginationChange = (newParams: FindProductDetailRequest) => {
  params.value = {...params.value, ...newParams};
};

const colorItem = ref([]);

const sizeItem = ref([]);

//Lấy list id và tên product fill lên select option
const { data: products } = useGetListProduct({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataProduct = computed(() => products?.value?.data || []);

// lấy danh sách chất liệu
const { data: materials } = useGetListMaterial({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listMaterial = computed(() => {
  return (
    materials?.value?.data?.map((mate: any) => ({
      value: mate.id,
      label: mate.ten,
    })) || []
  );
});

// lấy danh sách cổ áo
const { data: collars } = useGetListCollar({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listCollar = computed(() => {
  return (
    collars?.value?.data.map((collar: any) => ({
      value: collar.id,
      label: collar.ten,
    })) || []
  );
});

// lấy danh sách màu sắc
const { data: colors } = useGetListColor({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listColor = computed(() => {
  return (
    colors?.value?.data.map((color: any) => ({
      value: color.id,
      label: color.ten,
    })) || []
  );
});

//Tìm tên màu theo id
const getColorNameById = (id: string) => {
  // Tìm màu theo id
  const color = listColor.value.find((item: any) => item.value === id);
  return color ? color.label : "Màu không tìm thấy";
};

// lấy danh sách tính năng
const { data: features } = useGetListFeature({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listFeature = computed(() => {
  return (
    features?.value?.data.map((feature:any) => ({
      value: feature.id,
      label: feature.ten,
    })) || []
  );
});

// lấy danh sách họa tiết
const { data: patterns } = useGetListPattern({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listPattern = computed(() => {
  return (
    patterns?.value?.data.map((pattern: any) => ({
      value: pattern.id,
      label: pattern.ten,
    })) || []
  );
});

// lấy danh sách kich cỡ
const { data: sizes } = useGetListSize({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listSize = computed(() => {
  return (
    sizes?.value?.data.map((size: any) => ({
      value: size.id,
      label: size.ten,
    })) || []
  );
});

// lấy danh sách tay áo
const { data: sleeves } = useGetListSleeve({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listSleeve = computed(() => {
  return (
    sleeves?.value?.data.map((sleeve: any) => ({
      value: sleeve.id,
      label: sleeve.ten,
    })) || []
  );
});

// lấy danh sách kiểu dáng
const { data: styles } = useGetListStyle({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listStyle = computed(() => {
  return (
    styles?.value?.data.map((style: any) => ({
      value: style.id,
      label: style.ten,
    })) || []
  );
});

// lấy danh sách thương hiệu
const { data: trademarks } = useGetListTrademark({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listTrademark = computed(() => {
  return (
    trademarks?.value?.data.map((tra: any) => ({
      value: tra.id,
      label: tra.ten,
    })) || []
  );
});

provide('listProduct', dataProduct);
provide('listMaterial', listMaterial);
provide('listCollar', listCollar);
provide('listSleeve', listSleeve);
provide('listPattern', listPattern);
provide('listStyle', listStyle);
provide('listFeature', listFeature);
provide('listColor', listColor);
provide('listSize', listSize);
provide('listTrademark', listTrademark);

watch(
    () => data.value,
    (newData) => {
      if (newData) {

      }
    },
    {immediate: true}
);
</script>