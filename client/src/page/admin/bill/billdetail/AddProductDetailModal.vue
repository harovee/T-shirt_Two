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
    <product-detail-table-to-order
      :data-source="dataSource"
      :loading="isLoading || isFetching"
      :pagination-params="paramsAll"
      @update:pagination-params="handlePaginationChange"
      @select-product="handleSelectProduct"
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
} from "vue";
import ProductDetailTableToOrder from "./ProductDetailTableToOrder.vue";
import { useGetAllProductDetail } from "@/infrastructure/services/service/admin/productdetail.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { FindProductDetailRequest } from "@/infrastructure/services/api/admin/product_detail.api";
import { useCreateBillDetail } from "@/infrastructure/services/service/admin/bill-detail.action";
import { CreateBillDetailRequest } from "@/infrastructure/services/api/admin/bill-detail.api";

// Định nghĩa Props
const props = defineProps({
  open: Boolean,
  productList: Object,
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

  console.log("Danh sách sản phẩm đã chọn:", selectedProducts.value);
};

const { mutate: createBillDetail } = useCreateBillDetail();

const modelRef = reactive<CreateBillDetailRequest>({
  idHoaDon: null,
  idSanPhamChiTiet: null,
  soLuong: null,
});

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
} = useGetAllProductDetail(paramsAll, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataSource = computed(() => productData?.value?.data || []);

const handlePaginationChange = (newParams: FindProductDetailRequest) => {
  paramsAll.value = { ...paramsAll.value, ...newParams };
};
</script>

<style scoped></style>
