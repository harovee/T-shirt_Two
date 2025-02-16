<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <!-- Header -->
    <div class="flex justify-between items-center">
      <div class="flex items-center gap-2">
        <v-icon
          name="la-file-invoice-solid"
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

    <admin-bill-history />

    <!-- Bill Detail Table -->
    <bill-detail-table
      class="shadow p-4 rounded-lg bg-white"
      :columns="columnsBill"
      :data-source="detailDataSources"
      :bill-data="billDataById"
      :loading="isLoadingBillData || isFetchingBillData"
      :pagination-params="params"
      :total-pages="totalPage"
      @update:paginationParams="handlePaginationChange"
      @update-quantity="handleChangeTotalPrice"
    />
  </div>

  <div class="shadow p-4 rounded-lg bg-white mt-6">
    <div class="mt-4 max-w-lg ml-auto">
      <!-- <div class="flex justify-between mb-4">
        <span class="text-lg">Mã giảm giá:</span>
        <span class="text-lg">{{ detailData?.data?.data?.[0]. || 'Chưa có mã giảm giá' }}</span>
      </div> -->
      <div class="flex flex justify-between block mb-4">
        <span class="text-lg">Tiền hàng:</span>
        <span v-if="detailDataSources" class="text-lg">{{
          formatCurrencyVND(totalPrice)
        }}</span>
      </div>
      <div class="flex flex justify-between block mb-4">
        <span class="text-lg">Giảm giá:</span>
        <span v-if="detailDataSources" class="text-lg text-green-500">{{
          detailDataSources && detailDataSources.length > 0
            ? `- ${formatCurrencyVND(detailDataSources[0].tienGiamHD)}`
            : "0 VND"
        }}</span>
      </div>
      <div class="flex flex justify-between block mb-4">
        <span class="text-lg">Phí vận chuyển:</span>
        <span v-if="detailDataSources" class="text-lg">{{
          detailDataSources && detailDataSources.length > 0
            ? `${formatCurrencyVND(detailDataSources[0].tienShip)}`
            : "0 VND"
        }}</span>
      </div>
      <div class="flex flex justify-between block font-semibold text-xl">
        <span>Tổng tiền:</span>
        <span v-if="detailDataSources">{{
          detailDataSources && detailDataSources.length > 0
            ? formatCurrencyVND(detailDataSources[0].tongTienHD)
            : "0 VND"
        }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import router from "@/infrastructure/routes/router";
import {
  FindBillDetailRequest,
  BillDetailResponse,
} from "@/infrastructure/services/api/admin/bill-detail.api";
import { useGetBillDetails, useUpdateBillDetail } from "@/infrastructure/services/service/admin/bill-detail.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { computed, onMounted, ref, watch } from "vue";
import AdminBillHistory from "./AdminBillHistory.vue";
import BillDetailTable from "./BillDetailTable.vue";
import { ColumnType } from "ant-design-vue/es/table";
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

const billId = ref<string | null>(null); // Tạo một ref cho billId

const emit = defineEmits(["update:paginationParams"]);

const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

onMounted(() => {
  params.value.idHoaDon = getIdHoaDonFromUrl();
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
// console.log(detailData);

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
// console.log(detailData);

const totalPrice = computed(() => {
  return detailDataSources.value.reduce((sum, item) => sum + item.thanhTien, 0);
});

const handlePaginationChange = (newParams: FindBillDetailRequest) => {
  params.value = { ...params.value, ...newParams };
};

const {mutate: updateQuantity} = useUpdateBillDetail();

// Hàm cập nhật giá trị thanhTien
const handleChangeTotalPrice = (record) => {
  const item = detailDataSources.value.find((item) => item.id === record.id);
  if (item) {
    // Cập nhật số lượng và thành tiền trước khi gửi request
    item.soLuong = record.soLuong;
    
    // Gửi request cập nhật dữ liệu lên server
    updateQuantity(
      {
        idBillDetail: record.id,
        data: { soLuong: record.soLuong }
      },
      {
        onSuccess: () => {
          // console.log("Cập nhật thành công!");
          
          refetch(); // Refresh dữ liệu sau khi cập nhật thành công
        },
        onError: (error) => {
          console.error("Lỗi khi cập nhật:", error);
        },
      }
    );
  }

  // if (item) {
  //   // Cập nhật số lượng và thành tiền trước
  //   item.soLuong = record.soLuong;
  //   item.thanhTien = record.thanhTien;
  //   item.dieuKienGiam = record.dieuKienGiam;
  //   item.giamToiDa = record.giamToiDa;
  //   item.anhSanPhamChiTiet = record.anhSanPhamChiTiet;
  //   item.tienGiamHD = record.tienGiamHD;
  //   item.tienShip = record.tienShip;
  //   console.log(record.tienShip);
  // }

  // Tính tổng tiền sau khi cập nhật giá trị
  // const totalPrice = detailDataSources.value.reduce(
  //   (sum, item) => sum + item.thanhTien,
  //   0
  // );

  // if (totalPrice === 0) {
  //   detailDataSources.value.forEach((ds) => {
  //     ds.tienGiamHD = 0;
  //     ds.tienShip = 0; // Đặt phí vận chuyển bằng 0
  //   });
  // } else {
  //   if (item) {
  //     detailDataSources.value.forEach((ds) => {
  //       ds.tienShip = item.tienShip;
  //       ds.tienGiamHD = item.tienGiamHD;
  //     });
  //     if (item?.loaiGiam) {
  //       detailDataSources.value.forEach((ds) => {
  //         ds.tienGiamHD = record.giaTriGiam;
  //       });
  //     } else if (
  //       totalPrice >= Number(item?.dieuKienGiam) &&
  //       item?.loaiGiam == false
  //     ) {
  //       // item.tienGiamHD = (record.giaTriGiam / 100) * totalPrice;
  //       detailDataSources.value.forEach((ds) => {
  //         ds.tienGiamHD = (record.giaTriGiam / 100) * totalPrice;
  //       });
  //     }
  //   }
  // }

  // // Cập nhật tổng tiền hóa đơn
  // detailDataSources.value.forEach((ds) => {
  //   // ds.tienGiamHD = item?.tienGiamHD ? item?.tienGiamHD : 0;
  //   if (ds.tienGiamHD >= Number(item?.giamToiDa) && item?.loaiGiam == false) {
  //     ds.tienGiamHD = Number(item?.giamToiDa);
  //   }
  //   ds.tongTienHD = totalPrice - ds.tienGiamHD + ds.tienShip;
  // });

  // console.log("Updated totalPrice:", totalPrice);
};

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
    dataIndex: "anhSanPhamChiTiet",
    key: "anhSanPhamChiTiet",
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
