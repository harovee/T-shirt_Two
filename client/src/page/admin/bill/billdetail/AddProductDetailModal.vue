<template>
  <a-modal
    :open="props.open"
    title="Thêm sản phẩm"
    @cancel="handleClose"
    @ok="handleAddProducts"
    :width="'90vw'"
    :style="{ maxWidth: '1200px' }"
    :bodyStyle="{ maxHeight: '70vh', overflowY: 'auto' }"
    okText="Thêm"
    cancel-text="Hủy"
    destroyOnClose
    centered
  >
    <div class="mb-4">
      <filter-product-to-order @filter="handleFilterProductToOrder" />
    </div>
    <product-detail-table-to-order
      :data-source="dataSource"
      :loading="isLoading || isFetching"
      :pagination-params="paramsAll"
      @update:pagination-params="handlePaginationChange"
      @select-product="handleSelectProduct"
      style="margin-top: 15px"
    />
  </a-modal>
</template>

<script setup lang="ts">
import {
  defineProps,
  defineEmits,
  ref,
  computed,
  reactive,
  onMounted,
  provide,
  watch,
} from "vue";
import ProductDetailTableToOrder from "./ProductDetailTableToOrder.vue";
import FilterProductToOrder from "./FilterProductToOrder.vue";
import {
  useGetAllProductDetail,
  useGetAllProductDetailOverZero,
} from "@/infrastructure/services/service/admin/productdetail.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { FindProductDetailRequest } from "@/infrastructure/services/api/admin/product_detail.api";
import { useCreateBillDetail } from "@/infrastructure/services/service/admin/bill-detail.action";
import { CreateBillDetailRequest } from "@/infrastructure/services/api/admin/bill-detail.api";
import { useGetListMaterial } from "@/infrastructure/services/service/admin/material.action";
import { useGetListCollar } from "@/infrastructure/services/service/admin/collar.action";
import { useGetListColor } from "@/infrastructure/services/service/admin/color.action";
import { useGetListFeature } from "@/infrastructure/services/service/admin/feature.action";
import { useGetListPattern } from "@/infrastructure/services/service/admin/pattern.action";
import { useGetListSize } from "@/infrastructure/services/service/admin/size.action";
import { useGetListSleeve } from "@/infrastructure/services/service/admin/sleeve.action";
import { useGetListStyle } from "@/infrastructure/services/service/admin/style.action";
import { useGetListTrademark } from "@/infrastructure/services/service/admin/trademark.action";
import { useAuthStore } from "@/infrastructure/stores/auth";
import { useCreateBillHistory } from "@/infrastructure/services/service/admin/billhistory.action";
import { useUpdateBillConfirm } from "@/infrastructure/services/service/admin/bill.action";

// Định nghĩa Props
const props = defineProps({
  open: Boolean,
  productList: Object,
  loadingValue: {
    type: Boolean,
    required: true,
  },
  billData: Object,
  dataSource: Array,
});

// Định nghĩa Emits
const emit = defineEmits(["handleClose"]);

// Trong component Modal
const handleClose = () => {
  emit("handleClose"); // Đóng modal
  selectedProducts.value = []; // Reset danh sách sản phẩm đã chọn
};

// Danh sách sản phẩm được chọn
const selectedProducts = ref<any[]>([]);

// Hàm xử lý chọn/bỏ chọn sản phẩm
const handleSelectProduct = (product: any) => {
  const index = selectedProducts.value.findIndex(
    (p) => p.maSanPhamChiTiet === product.maSanPhamChiTiet
  );

  if (index === -1) {
    selectedProducts.value.push(product);
  } else {
    selectedProducts.value.splice(index, 1);
  }

  // console.log("Danh sách sản phẩm đã chọn:", selectedProducts.value);
};

const { mutate: createBillDetail } = useCreateBillDetail();

const { mutate: createBillHistory } = useCreateBillHistory();

const { mutate: update } = useUpdateBillConfirm();

const modelRef = reactive<CreateBillDetailRequest>({
  idHoaDon: null,
  idSanPhamChiTiet: null,
  soLuong: null,
  isClient: null,
});

const payload = ref();

const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

onMounted(() => {
  const idHoaDonFromUrl = getIdHoaDonFromUrl();
  if (idHoaDonFromUrl) {
    modelRef.idHoaDon = idHoaDonFromUrl; // Gán idHoaDon lấy từ URL vào modelRef
  } else {
    console.error("idHoaDon không có trong URL!");
  }
});

watch(
  () => props.open,
  (newVal) => {
    if (newVal) {
      refetch(); // Gọi lại API khi mở modal
    }
  }
);

watch(
  () => props.loadingValue,
  (newVal) => {
    if (newVal) {
      refetch(); // Gọi lại API khi mở modal
    }
  }
);

watch(
  () => props.dataSource,
  (newVal) => {
    if (newVal) {
      payload.value = {
          tienShip: newVal[0].tienShip,
          tienGiam: newVal[0].tienGiamHD,
          tongTien: newVal[0].tongTienHD,
        };
    }
  }
);

const handleAddProducts = () => {
  if (selectedProducts.value.length === 0) {
    console.warn("Chưa chọn sản phẩm nào!");
    return;
  }
  // Kiểm tra và đảm bảo idHoaDon hợp lệ trước khi gửi request
  if (!modelRef.idHoaDon) {
    console.error("ID hóa đơn không hợp lệ");
    return;
  }

  selectedProducts.value.forEach((product) => {
    const requestData = {
      idHoaDon: modelRef.idHoaDon, // ID hóa đơn từ URL
      idSanPhamChiTiet: product.id, // Đảm bảo lấy đúng ID sản phẩm
      soLuong: 1, // Mặc định số lượng là 1
      isClient: props.billData.loaiHD === "Online" ? true : false,
    };

    const billHistoryParams = {
      idHoaDon: modelRef.idHoaDon,
      hanhDong: `Thêm sản phẩm`,
      moTa: `Nhân viên "${
        useAuthStore().user?.email
      }" đã thêm sản phẩm vào đơn`,
      trangThai: "Chờ xác nhận",
      nguoiTao: useAuthStore().user?.id || null,
    };

    // console.log("📤 Dữ liệu gửi đi API:", requestData); // Log dữ liệu trước khi gửi

    createBillDetail(requestData, {
      onSuccess: () => {
        // console.log(`✅ Thêm sản phẩm ${product.maSanPhamChiTiet} thành công`);
      },
      onError: (error) => {
        console.error("❌ Lỗi khi thêm sản phẩm:", error);
      },
    });
    //cập nhật lại hóa đơn
    setTimeout(() => {
      // console.log(payload.value);
      update(
      { idBill: modelRef.idHoaDon, params: payload.value },
      {
        onSuccess: (result) => {
          // successNotiSort("Cập nhật thông tin thành công");
        },
        onError: (error: any) => {
          // errorNotiSort("Cập nhật thông tin thất bại");
        },
      }
    );
    }, 3000);

    
    // Thêm lịch sử hóa đơn (Khi khách thêm sản phẩm vào đơn)
    createBillHistory(billHistoryParams);
  });

  handleClose(); // Đóng modal sau khi thêm
};

// Định nghĩa kiểu dữ liệu cho sản phẩm chi tiết
const paramsAll = ref({
  page: 1,
  size: 10,
});

const {
  data: productData,
  isLoading,
  isFetching,
  refetch,
} = useGetAllProductDetailOverZero(paramsAll, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataSource = computed(() => productData?.value?.data || []);

const handlePaginationChange = (newParams: FindProductDetailRequest) => {
  paramsAll.value = { ...paramsAll.value, ...newParams };
};

const handleFilterProductToOrder = (newParams: FindProductDetailRequest) => {
  paramsAll.value = { ...paramsAll.value, ...newParams };
};

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
    sleeves?.value?.data?.map((sleeve: any) => ({
      value: sleeve.id,
      label: sleeve.ten,
    })) || []
  );
});
// console.log(listSleeve);

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

// provide để truyền dữ liệu sang component con
provide("listMaterial", listMaterial);
provide("listCollar", listCollar);
provide("listSleeve", listSleeve);
provide("listPattern", listPattern);
provide("listStyle", listStyle);
provide("listFeature", listFeature);
provide("listColor", listColor);
provide("listSize", listSize);
provide("listTrademark", listTrademark);

</script>

<style scoped></style>
