<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <my-order-detail-table
      class="shadow p-4 rounded-lg bg-white"
      :columns="columnsBill"
      :data-source="detailDataSources"
      :bill-data="billDataById"
      :loading="isLoadingBillData || isFetchingBillData"
      :pagination-params="params"
      :total-pages="totalPage"
      @update:paginationParams="handlePaginationChange"
      @update-quantity="handleChangeTotalPrice"
      @refetch-data="handleRefetchData"
      @reload-data="handleReloadData"
      :loadingValue="refereshAllProduct"
    />
  </div>
</template>

<script lang="ts" setup>
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import router from "@/infrastructure/routes/router";
import {
  FindBillDetailRequest,
  BillDetailResponse,
} from "@/infrastructure/services/api/admin/bill-detail.api";
import {
  useGetBillDetails,
  useUpdateBillDetail,
} from "@/infrastructure/services/service/admin/bill-detail.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { computed, onMounted, ref, watch } from "vue";
import MyOrderDetailTable from "./my-order-detail/MyOrderDetailTable.vue";
import { ColumnType } from "ant-design-vue/es/table";
import { useGetBillById } from "@/infrastructure/services/service/admin/bill.action";
import { formatCurrencyVND } from "@/utils/common.helper";
import { useCreateBillHistory } from "@/infrastructure/services/service/admin/billhistory.action";
import { useAuthStore } from "@/infrastructure/stores/auth";

const handleRedirectBillManager = () => {
  router.push({
    name: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.name,
  });
};

const params = ref<FindBillDetailRequest>({
  page: 1,
  size: 10,
  idHoaDon: "",
});

const billId = ref<string | null>(null); // Tạo một ref cho billId

const emit = defineEmits(["update:paginationParams"]);

const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

onMounted(() => {
  params.value.idHoaDon = getIdHoaDonFromUrl();
  billId.value = getIdHoaDonFromUrl();
});

const {
  data: detailData,
  isLoading: isLoadingDetailData,
  isFetching: isFetchingDetailData,
  refetch: refetchDetailData
} = useGetBillDetails(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const {
  data: billData,
  isLoading: isLoadingBillData,
  isFetching: isFetchingBillData,
  refetch,
} = useGetBillById(billId, {
  refetchOnWindowFocus: false,
  enabled: () => !!billId.value,
});

const billDataById = computed(() => billData?.value?.data?.data);

watch(billId, (newVal) => {
  if (newVal && newVal !== null) {
    refetch();
  }
});

const detailDataSources = ref<BillDetailResponse[]>([]);

// Theo dõi sự thay đổi từ API và cập nhật lại `detailDataSource` khi dữ liệu thay đổi
watch(
  () => detailData.value?.data?.data,
  (newData) => {
    // Tạo bản sao dữ liệu mới từ API để tránh readonly
    detailDataSources.value = JSON.parse(JSON.stringify(newData || []));
    // console.log(detailDataSources.value);
  },
  { immediate: true }
);

// Tính toán totalPages
const totalPage = computed(() => detailData?.value?.data?.totalPages || 1);

const totalPrice = computed(() => {
  return detailDataSources.value.reduce((sum, item) => sum + item.thanhTien, 0);
});

const handlePaginationChange = (newParams: FindBillDetailRequest) => {
  params.value = { ...params.value, ...newParams };
};

const { mutate: updateQuantity } = useUpdateBillDetail();

const { mutate: createBillHistory } = useCreateBillHistory();

const refereshAllProduct = ref(false)

// Hàm cập nhật giá trị thanhTien
const handleChangeTotalPrice = (record) => {
  const item = detailDataSources.value.find((item) => item.id === record.id);
  
  if (item) {
    // Cập nhật số lượng và thành tiền trước khi gửi request
    item.soLuong = record.soLuong;
    const params = { idHoaDonChiTiet: item.id, soLuong: item.soLuong };

    const billHistoryParams = {
          idHoaDon: billId.value,
          hanhDong: `Sửa số lượng`,
          moTa: `Khách hàng đã sửa số lượng sản phẩm đơn hàng`,
          trangThai: "Chờ xác nhận",
          nguoiTao: useAuthStore().user?.id || null
        }
    // Gửi request cập nhật dữ liệu lên server
    updateQuantity(
      {
        idBillDetail: record.id,
        data: params,
      },
      {
        onSuccess: () => {
          refereshAllProduct.value = !refereshAllProduct.value
          refetch(); // Refresh dữ liệu sau khi cập nhật thành công
        },
        onError: (error) => {
          console.error("Lỗi khi cập nhật:", error);
        },
      }
    );
    // Thêm lịch sử hóa đơn (Khi khách sửa số lượng sản phẩm trong đơn)
    createBillHistory(billHistoryParams);
  }
};

// Hàm load lại danh sách sản phẩm trong giỏ
const handleRefetchData = (record) => {
  const item = detailDataSources.value.find((item) => item.id === record.id);

  const billHistoryParams = {
          idHoaDon: billId.value,
          hanhDong: `Xóa sản phẩm`,
          moTa: `Khách hàng đã xóa sản phẩm khỏi đơn hàng`,
          trangThai: "Chờ xác nhận",
          nguoiTao: useAuthStore().user?.id || null
        }

  if (item) {
    // Cập nhật số lượng và thành tiền trước khi gửi request
    item.soLuong = 0;
    const params = { idHoaDonChiTiet: item.id, soLuong: item.soLuong };
    // Gửi request cập nhật dữ liệu lên server
    updateQuantity(
      {
        idBillDetail: record.id,
        data: params,
      },
      {
        onSuccess: () => {
          refereshAllProduct.value = !refereshAllProduct.value;
          refetch();
        },
        onError: (error) => {
          console.error("Lỗi khi cập nhật:", error);
        },
      }
    );
    // Thêm lịch sử hóa đơn (Khi khách xóa sản phẩm khỏi đơn)
    createBillHistory(billHistoryParams);
  }
};

const handleReloadData = async (done) => {
  await refetchDetailData();
  done();
}

const columnsBill: ColumnType[] = [
  {
    title: "#",
    dataIndex: "catalog",
    key: "catalog",
    ellipsis: true,
    width: 50,
    align: "center",
  },
  {
    title: "Ảnh",
    dataIndex: "imgUrl",
    key: "imgUrl",
    ellipsis: true,
    width: 150,
    align: "center",
  },
  {
    title: "Sản phẩm",
    dataIndex: "chiTietSanPham",
    key: "chiTietSanPham",
    ellipsis: true,
    width: 150,
    align: "center",
  },
  {
    title: "Số lượng",
    dataIndex: "soLuong",
    key: "soLuong",
    ellipsis: true,
    width: 150,
    align: "center",
  },
  {
    title: "Thành tiền",
    dataIndex: "thanhTien",
    key: "thanhTien",
    ellipsis: true,
    width: 200,
    align: "center",
  },
  {
    title: "Hành động",
    key: "action",
    align: "center",
    width: 100,
    fixed: "right",
  },
];
</script>

<style scoped>
.component-container {
  padding: 16px;
  border-radius: 8px;
  background-color: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>
