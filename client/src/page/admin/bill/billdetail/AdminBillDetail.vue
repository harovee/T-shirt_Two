<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div class="flex items-center gap-2">
        <v-icon
          name="md-switchaccount-round"
          size="x-large"
          width="48"
          height="48"
        />
        <h3 class="text-2xl m-0">Chi tiết hóa đơn</h3>
      </div>
      <div
        class="flex items-center gap-2 scale-75 transition-all cursor-pointer hover:scale-100 hover:text-red-500"
        @click="handleRedirectBillManager()"
      >
        <v-icon
          name="gi-fast-backward-button"
          size="x-large"
          width="48"
          height="48"
        />
        <h3 class="text-2xl m-0">Quay lại</h3>
      </div>
    </div>

    <admin-bill-history
      :data-source="historyData"
      :loading="isHistoryLoading"
    />

    <!-- Bill Detail Table -->
    <bill-detail-table
      class="shadow p-4 rounded-lg bg-white"
      :columns="columnsBill"
      :data-source="detailDataSource?.data?.data"
      :bill-data="billDataById"
      :loading="isLoadingBillData || isFetchingBillData"
      :pagination-params="paginationParams || {}"
      :total-pages="detailDataSource?.data?.totalPages || 1"
      @update:paginationParams="$emit('update:paginationParams', $event)"
    />
  </div>

  <div class="shadow p-4 rounded-lg bg-white mt-6">
    <!-- <div class="flex justify-between mb-4">
        <span class="text-lg">Mã giảm giá:</span>
        <span class="text-lg">{{ detailData?.data?.data?.[0]. || 'Chưa có mã giảm giá' }}</span>
      </div> -->
    <div class="flex justify-between mb-4">
      <span class="text-lg">Giảm giá:</span>
      <span v-if="detailData" class="text-lg text-green-500">{{
        detailData?.data?.data?.[0]
          ? `- ${formatCurrencyVND(detailData?.data?.data?.[0].tienGiamHD)}`
          : "0 VND"
      }}</span>
    </div>
    <div class="flex justify-between mb-4">
      <span class="text-lg">Phí vận chuyển:</span>
      <span v-if="detailData" class="text-lg">{{
        detailData?.data?.data?.[0]
          ? `${formatCurrencyVND(detailData?.data?.data?.[0].tienShip)}`
          : "0 VND"
      }}</span>
    </div>
    <div class="flex justify-between font-semibold text-xl">
      <span>Tổng tiền:</span>
      <span v-if="detailData">{{
        detailData?.data?.data?.[0]
          ? formatCurrencyVND(detailData?.data?.data?.[0].tongTienHD)
          : "0 VND"
      }}</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import router from "@/infrastructure/routes/router";
import { FindBillDetailRequest } from "@/infrastructure/services/api/admin/bill-detail.api";
import { useGetBillDetails } from "@/infrastructure/services/service/admin/bill-detail.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { computed, onMounted, ref, watch } from "vue";
import AdminBillHistory from "./AdminBillHistory.vue";
import BillDetailTable from "./BillDetailTable.vue";
import { ColumnType } from "ant-design-vue/es/table";
import { useGetBillHistory } from "@/infrastructure/services/service/admin/billhistory.action";
import { FindBillHistoryRequest } from "@/infrastructure/services/api/admin/billhistory.api";
import { useGetBillById } from "@/infrastructure/services/service/admin/bill.action";
import { formatCurrencyVND } from "@/utils/common.helper";

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

const paramsHistory = ref<FindBillHistoryRequest>({
  page: 1,
  size: 10,
  idHoaDon: "",
});

const billId = ref<string | null>(null); // Tạo một ref cho billId

const emit = defineEmits(["update:paginationParams"]);

const props = defineProps({
  paginationParams: Object,
});

const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

onMounted(() => {
  params.value.idHoaDon = getIdHoaDonFromUrl();
  paramsHistory.value.idHoaDon = getIdHoaDonFromUrl();
  billId.value = getIdHoaDonFromUrl();
  // console.log(billId.value);
});

const {
  data: detailData,
  isLoading: isLoadingDetailData,
  isFetching: isFetchingDetailData,
} = useGetBillDetails(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const {
  data: historyData,
  isLoading: isHistoryLoading,
  isFetching: isHistoryFetching,
} = useGetBillHistory(paramsHistory, {
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

// watch(historyData, (newData) => {
//   console.log("History data updated:", newData);
// });

watch(billId, (newVal) => {
  if (newVal && newVal !== null) {
    refetch();
  }
});

const detailDataSource = computed(() => detailData.value || []);
console.log(detailDataSource);

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
    dataIndex: "anhSanPham",
    key: "anhSanPham",
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
  /* margin-bottom: 16px; Khoảng cách giữa các component */
}
</style>
