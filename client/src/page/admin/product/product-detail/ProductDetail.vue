<template>
<div class="p-6 grid grid-cols-1 gap-6">
  <div class="flex items-center gap-2">
        <v-icon
          name="md-widgets-outlined"
          size="x-large"
          width="48"
          height="48"
        />
        <h3 class="text-2xl m-0">Sản phẩm chi tiết</h3>
      </div>
  <div>
    <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
      
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24" />
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <search-product-detail
      @filter="computedFilter"
      @fetch-all-product-detail="handleAllProductDetail"
      @export-to-excel="handleExportToExcel"
    />
    </div>
    
    <div class="rounded-xl p-7 mt-6 rounded-xl border-2">
      <product-detail-table-new
        :data-source="computedDataSource"
        :loading="computedLoading"
        :pagination-params="computedPaginationParams"
        @update:pagination-params="computedPaginationHandler"
        @handleRefetch="refetchData"
        :product-id="productId"
        :change-fill="changeProductDetail"
      />
    </div>
  </div>
</div>
</template>

<script lang="ts" setup>
import SearchProductDetail from "@/page/admin/product/product-detail/SearchProductDetail.vue";
import ProductDetailTableNew from "@/page/admin/product/product-detail/ProductDetailTableNew.vue";
import { computed, ref, watch, provide } from "vue";
import * as XLSX from "xlsx";
import { useRoute } from "vue-router";
import {
  FindProductDetailRequest,
  FindAllProductDetailRequest,
  getListAllProductDetail,
  getListProductDetail,
  getListSanPhamChiTiet,
  getListSanPhamChiTietByIdSanPham,
} from "@/infrastructure/services/api/admin/product_detail.api";
import {
  useGetProductDetail,
  useGetAllProductDetail,
} from "@/infrastructure/services/service/admin/productdetail.action";
import { keepPreviousData } from "@tanstack/vue-query";
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
// Lấy id product trên path
const productId = ref<string>(String(route.params.id));

// Loading dữ liệu danh sách sản phẩm chi tiết có mã sản phẩm
const params = ref<FindProductDetailRequest>({
  page: 1,
  size: 10,
  idSanPham: productId.value,
});

const {
  data,
  isLoading: productDetailsLoading,
  isFetching: productDetailsFetching,
  refetch
} = useGetProductDetail(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const handleFilter = (newParams: FindProductDetailRequest) => {
  params.value = { ...params.value, ...newParams };
};

const dataSource = computed(() => data?.value?.data || []);

const handlePaginationChange = (newParams: FindProductDetailRequest) => {
  params.value = { ...params.value, ...newParams };
};

// Loading dữ liệu danh sách toàn bộ sản phẩm chi tiết của cửa hàng
const paramsAll = ref<FindAllProductDetailRequest>({
  page: 1,
  size: 10,
});

const {
  data: allProductDetail,
  isLoading: allProductDetailsLoading,
  isFetching: allProductDetailsFetching
} = useGetAllProductDetail(paramsAll, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const handleFilterAll = (newParams: FindAllProductDetailRequest) => {
  paramsAll.value = { ...paramsAll.value, ...newParams };
};

const dataSourceAll = computed(() => allProductDetail?.value?.data || []);

const handlePaginationChangeAll = (newParams: FindAllProductDetailRequest) => {
  paramsAll.value = { ...paramsAll.value, ...newParams };
};
// --------------------------------------------------------------------

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
    features?.value?.data.map((feature: any) => ({
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

const changeProductDetail = ref(false);

const handleAllProductDetail = (changeProductDetails) => {
  changeProductDetail.value = changeProductDetails;
  // console.log(changeProductDetail.value);
};

const computedFilter = computed(() => {
  return changeProductDetail.value ? handleFilterAll : handleFilter;
});

const computedDataSource = computed(() => {
  return changeProductDetail.value ? dataSourceAll.value : dataSource.value;
});

const computedLoading = computed(() => {
  return changeProductDetail.value
    ? allProductDetailsLoading.value || allProductDetailsFetching.value
    : productDetailsLoading.value || productDetailsFetching.value;
});

const computedPaginationParams = computed(() => {
  return changeProductDetail.value ? paramsAll.value : params.value;
});

const computedPaginationHandler = computed(() => {
  return changeProductDetail.value
    ? handlePaginationChangeAll
    : handlePaginationChange;
});

// const handleExportToExcel = () => {

//   const dataExcel = computedDataSource.value?.data;
//   console.log(dataExcel);
  
//   const filteredData = dataExcel?.map((item: any) => {
//     return {
//       STT: item.catalog,
//       "Mã sản phẩm": item.maSanPhamChiTiet,
//       "Tên Sản Phẩm": item.sanPham,
//       "Chất liệu": item.chatLieu,
//       "Thương hiệu": item.thuongHieu,
//       "Cổ áo": item.coAo,
//       "Tay áo": item.tayAo,
//       "Kiểu dáng": item.kieuDang,
//       "Họa tiết": item.hoaTiet,
//       "Kích cỡ" : item.kichCo,
//       "Màu sắc" : item.mauSac,
//       "Tính năng": item.tinhNang,
//       Giá: formatter(item.gia) || 0,
//       "Số lượng": item.soLuong || 0,
//       "Trạng thái": item.trangThai ? "Ngừng áp dụng" : "Đang áp dụng",
//     };
//   });
//   const ws = XLSX.utils.json_to_sheet(filteredData);
//   const wb = XLSX.utils.book_new();
//   XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
//   XLSX.writeFile(wb, 'danh_sach.xlsx');
//   // console.log(allData);
// };

const handleExportToExcel = async () => {
  try {
    let allData: any[] = [];
    
    if (changeProductDetail.value) {
      // Khi chọn tất cả sản phẩm - lấy tất cả sản phẩm không phân trang
      const response = await getListSanPhamChiTiet();
      allData = response?.data || [];
    } else {
      // Khi chọn sản phẩm cụ thể - lấy tất cả sản phẩm chi tiết theo sản phẩm không phân trang
      const response = await getListSanPhamChiTietByIdSanPham(productId.value);
      allData = response?.data || [];
    }
    
    const filteredData = allData.map((item: any) => {
      return {
        STT: item.catalog,
        "Mã sản phẩm": item.maSanPhamChiTiet,
        "Tên Sản Phẩm": item.sanPham,
        "Chất liệu": item.chatLieu,
        "Thương hiệu": item.thuongHieu,
        "Cổ áo": item.coAo,
        "Tay áo": item.tayAo,
        "Kiểu dáng": item.kieuDang,
        "Họa tiết": item.hoaTiet,
        "Kích cỡ": item.kichCo,
        "Màu sắc": item.mauSac,
        "Tính năng": item.tinhNang,
        Giá: formatter(item.gia) || 0,
        "Số lượng": item.soLuong || 0,
        "Trạng thái": item.trangThai ? "Ngưng bán" : "Đang bán",
      };
    });
    
    const ws = XLSX.utils.json_to_sheet(filteredData);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
    XLSX.writeFile(wb, 'danh_sach.xlsx');
  } catch (error) {
    console.error("Lỗi khi xuất Excel:", error);
    // Có thể thêm thông báo lỗi cho người dùng
  }
};

const formatter = (value: any) => {
  if (!value) return "";
  return `${Math.round(value)} ₫`.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

const refetchData = () => {
  refetch();
}

// provide để truyền dữ liệu sang component con
provide("listProduct", dataProduct);
provide("listMaterial", listMaterial);
provide("listCollar", listCollar);
provide("listSleeve", listSleeve);
provide("listPattern", listPattern);
provide("listStyle", listStyle);
provide("listFeature", listFeature);
provide("listColor", listColor);
provide("listSize", listSize);
provide("listTrademark", listTrademark);

watch(
  () => data.value,
  (newData) => {
    if (newData) {
    }
  },
  { immediate: true }
);
</script>