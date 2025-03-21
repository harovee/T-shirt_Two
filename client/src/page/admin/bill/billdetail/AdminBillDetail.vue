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
      @get:total-amount="getTotalAmountPaid"
      :loadingValue="refereshAllProduct"
    />
  </div>

  <div class="shadow p-6 rounded-lg bg-white mt-6 grid grid-cols-2 gap-48">
  <!-- Cột 1 -->
  <div class="space-y-4">
    <div class="flex justify-between">
      <span class="text-lg">Tiền hàng:</span>
      <span v-if="detailDataSources" class="text-lg">{{
        formatCurrencyVND(totalPrice)
      }}</span>
    </div>
    <div class="flex justify-between">
      <span class="text-lg">Giảm giá:</span>
      <span v-if="detailDataSources" class="text-lg text-green-500">{{
        detailDataSources.length > 0 ? `- ${formatCurrencyVND(detailDataSources[0].tienGiamHD)}` : "0 VND"
      }}</span>
    </div>
    <div class="flex justify-between">
      <span class="text-lg">Phí vận chuyển:</span>
      <span v-if="detailDataSources" class="text-lg">{{
        detailDataSources.length > 0 ? formatCurrencyVND(detailDataSources[0].tienShip) : "0 VND"
      }}</span>
    </div>
    <div class="flex justify-between font-semibold text-xl">
      <span>Tổng tiền:</span>
      <span v-if="detailDataSources">{{
        detailDataSources.length > 0 ? formatCurrencyVND(detailDataSources[0].tongTienHD) : "0 VND"
      }}</span>
    </div>
  </div>

  <!-- Cột 2 -->
  <div class="space-y-4">
    <div class="flex justify-between">
      <span class="text-lg">Đã thanh toán:</span>
      <span v-if="detailDataSources" class="text-lg">{{
        detailDataSources.length > 0 ? `${formatCurrencyVND(paid)}` : "0 VND"
      }}</span>
    </div>
    <div class="flex justify-between">
      <span class="text-lg">Cần thanh toán:</span>
      <span v-if="detailDataSources" class="text-lg text-red-500">{{
        detailDataSources.length > 0 ? formatCurrencyVND(amountPayable) : "0 VND"
      }}</span>
    </div>
    <div class="flex justify-between text-lg">
      <span>(Phụ phí)</span>
      <span v-if="detailDataSources">{{
        detailDataSources.length > 0 ? formatCurrencyVND(detailDataSources[0].tongTienHD) : "0 VND"
      }}</span>
    </div>
    <div class="flex justify-between text-lg">
      <span>(Hoàn trả)</span>
      <span v-if="detailDataSources">{{
        detailDataSources.length > 0 ? formatCurrencyVND(detailDataSources[0].tongTienHD) : "0 VND"
      }}</span>
    </div>
  </div>
</div>

</template>

<script lang="ts" setup>
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import router from "@/infrastructure/routes/router";
import {
  useUpdateVoucher,
  useUpdateCustomerVoucher,
  useGetListKhachHang,
  useGetVoucherById,
  useGetCusTomerByIdPhieuGiamGia,
} from "@/infrastructure/services/service/admin/voucher/voucher.action";
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

// Tạo biến số tiền đã thanh toán
const paid = ref(0);
// Số tiền phải thanh toán
const amountPayable = ref(0);

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

// Khai báo voucherId
const voucherId = ref(null);
// Voucher detail
const detail = ref(null);

const { data: dataDetail, refetch: refetchVoucher } = useGetVoucherById(
  voucherId,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled: false,
  }
);

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

const totalPrice = computed(() => {
  return detailDataSources.value.reduce((sum, item) => sum + item.thanhTien, 0);
});

const detailDataSources = ref<BillDetailResponse[]>([]);

// Theo dõi sự thay đổi từ API và cập nhật lại `detailDataSource` khi dữ liệu thay đổi
watch(
  () => detailData.value?.data?.data,
  (newData) => {
    // Tạo bản sao dữ liệu mới từ API để tránh readonly
    detailDataSources.value = JSON.parse(JSON.stringify(newData || []));
  },
  { immediate: true }
);

watch(
  () => detailDataSources.value,
  (newData) => {
    if (newData[0]) {
      // Tính toán lại phí giảm sau khi thêm sản phẩm hoặc thay đổi số lượng
      if (detail.value && detail.value.loaiGiam) {
        // Loại giảm = true (tiền mặt)
        newData[0] = detail?.value?.giaTriGiam;
        newData[0].tongTienHD =
          totalPrice.value +
          newData[0].tienShip -
          newData[0].tienGiamHD;
        amountPayable.value = detailDataSources.value[0].tongTienHD - paid.value;
      } else {
        // Loại giảm = flase (%)
        newData[0].tienGiamHD =
          (totalPrice.value * Number(detail?.value?.giaTriGiam)) / 100;

        newData[0].tongTienHD =
          totalPrice.value +
          newData[0].tienShip -
          newData[0].tienGiamHD;
        amountPayable.value = detailDataSources.value[0].tongTienHD - paid.value;
      }
      
      
    }
  },

  { immediate: true, deep: true }
);

watch(
  () => billDataById.value,
  (newBillData) => {
    voucherId.value = newBillData.idPhieuGiamGia;

    // if (copiedBillData.value && copiedBillData.value.huyen) {
    //   serviceIdParams.value.toDistrict = Number(copiedBillData.value.huyen);
    //   if (serviceIdParams.value.toDistrict !== 0) {
    //     refetchService().then(() => {
    //       shippingParams.value.serviceId = service?.value?.data[0].service_id;
    //       // console.log(shippingParams.value);
    //       shippingParams.value.toDistrictId = copiedBillData.value.huyen;
    //       shippingParams.value.toWardCode = copiedBillData.value.xa;
    //       if (shippingParams.value.toWardCode) {
    //         refetchShipping().then(() => {
    //           copiedDataSource.value[0].tienShip = shipping?.value?.data.total;
    //           copiedBillData.value.tienShip =
    //             copiedDataSource.value[0].tienShip;
    //         });
    //       }
    //     });
    //   } else {
    //     copiedDataSource.value[0].tienShip = 0;
    //   }
    // }
  }
);

// Theo dõi nếu hóa đơn có voucherId thì sẽ tính tiền giảm
watch(
  () => voucherId.value,
  (newValue) => {
    if (newValue) {
      console.log(newValue);

      refetchVoucher().then(() => {
        detail.value = dataDetail?.value?.data?.data;

        if (detail.value.loaiGiam) {
          // Loại giảm = true (tiền mặt)
          detailDataSources.value[0].tienGiamHD = detail.value.giaTriGiam;
          detailDataSources.value[0].tongTienHD =
            totalPrice.value +
            detailDataSources.value[0].tienShip -
            detailDataSources.value[0].tienGiamHD;
        } else {
          // Loại giảm = flase (%)
          detailDataSources.value[0].tienGiamHD =
            (totalPrice.value * Number(detail.value.giaTriGiam)) / 100;

          detailDataSources.value[0].tongTienHD =
            totalPrice.value +
            detailDataSources.value[0].tienShip -
            detailDataSources.value[0].tienGiamHD;
        }
      });
    }
  },
  { immediate: true }
);

// Tính toán totalPages
const totalPage = computed(() => detailData?.value?.data?.totalPages || 1);

const handlePaginationChange = (newParams: FindBillDetailRequest) => {
  params.value = { ...params.value, ...newParams };
};

const { mutate: updateQuantity } = useUpdateBillDetail();

const refereshAllProduct = ref(false);

// Hàm cập nhật giá trị thanhTien
const handleChangeTotalPrice = (record) => {
  const item = detailDataSources.value.find((item) => item.id === record.id);

  if (item) {
    // Cập nhật số lượng và thành tiền trước khi gửi request
    item.soLuong = record.soLuong;
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
          refetch(); // Refresh dữ liệu sau khi cập nhật thành công
        },
        onError: (error) => {
          console.error("Lỗi khi cập nhật:", error);
        },
      }
    );
  }
};

// Hàm tính tiền đã thanh toán
const getTotalAmountPaid = (totalPaid: number) => {
  paid.value = totalPaid;
  amountPayable.value = detailDataSources.value[0].tongTienHD - totalPaid;
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
