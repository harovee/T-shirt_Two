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

    <admin-bill-history
      :data-payment-info="paymentInfo"
      :data-product="detailDataSources"
      :bill-data="billDataById"
      @update:bill="handleUpdateBill"
    />

    <!-- Bill Detail Table -->
    <bill-detail-table
      class="shadow p-4 rounded-lg bg-white"
      :columns="columnsBill"
      :data-source="detailDataSources"
      :payment-info-data="paymentInfo"
      :billId="billId"
      :bill-data="billDataById"
      :loading="isLoadingDetailData || isFetchingDetailData"
      :pagination-params="params"
      :total-pages="totalPage"
      :detail="detail"
      @update:paginationParams="handlePaginationChange"
      @update-quantity="handleChangeTotalPrice"
      @get:total-amount="getTotalAmountPaid"
      @refetch-data="handleRefetchData"
      @get:payment-info="handleGetPaymentInfo"
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
        <span v-if="detailDataSources" class="text-lg text-red-500">{{
          detailDataSources.length > 0
            ? `- ${formatCurrencyVND(detailDataSources[0].tienGiamHD)}`
            : "0 VND"
        }}</span>
      </div>
      <div class="flex justify-between">
        <span class="text-lg">Phí vận chuyển:</span>
        <span v-if="detailDataSources" class="text-lg">{{
          detailDataSources.length > 0
            ? formatCurrencyVND(detailDataSources[0].tienShip)
            : "0 VND"
        }}</span>
      </div>
      <div class="flex justify-between font-semibold text-xl">
        <span>Tổng tiền:</span>
        <span v-if="detailDataSources">{{
          detailDataSources.length > 0
            ? formatCurrencyVND(detailDataSources[0].tongTienHD)
            : "0 VND"
        }}</span>
      </div>
    </div>

    <!-- Cột 2 -->
    <div class="space-y-4">
      <div class="flex justify-between">
        <span class="text-lg">Đã thanh toán:</span>
        <span v-if="detailDataSources" class="text-lg">{{
          detailDataSources.length > 0
            ? `${formatCurrencyVND(paymentInfo.paid)}`
            : "0 VND"
        }}</span>
      </div>
      <div class="flex justify-between">
        <span class="text-lg">Cần thanh toán:</span>
        <span v-if="detailDataSources" class="text-lg text-red-500">{{
          detailDataSources.length > 0
            ? formatCurrencyVND(paymentInfo.amountPayable)
            : "0 VND"
        }}</span>
      </div>
      <div class="flex justify-between text-lg">
        <span>(Phụ phí)</span>
        <span v-if="detailDataSources">{{
          detailDataSources.length > 0
            ? formatCurrencyVND(paymentInfo.surcharge)
            : "0 VND"
        }}</span>
      </div>
      <div class="flex justify-between text-lg">
        <span>(Hoàn trả)</span>
        <span v-if="detailDataSources">{{
          detailDataSources.length > 0
            ? formatCurrencyVND(paymentInfo.refund)
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
import { computed, onMounted, ref, watch, Ref } from "vue";
import AdminBillHistory from "./AdminBillHistory.vue";
import BillDetailTable from "./BillDetailTable.vue";
import { ColumnType } from "ant-design-vue/es/table";
import { useGetBillById } from "@/infrastructure/services/service/admin/bill.action";
import { formatCurrencyVND } from "@/utils/common.helper";
import { useGetPayHistory } from "@/infrastructure/services/service/admin/payhistory.action";
import { BillResponse } from "@/infrastructure/services/api/admin/bill.api";
import { useUpdateBillConfirm } from "@/infrastructure/services/service/admin/bill.action";
import {
  VoucherResponse,
  FindVoucherRequest,
  nextVoucherRequest,
  ShippingFeeRequest,
  getWardByCode,
  getDistrictById,
  getProvinceById,
  ServiceIdRequest,
  createInvoicePdf,
} from "@/infrastructure/services/api/admin/payment.api";
import { toRaw } from "vue";
import {
  useGetListVoucher,
  useGetPriceNextVoucher,
  useGetShippingFee,
  useGetServiceId,
} from "@/infrastructure/services/service/admin/payment.action";
import {
  errorNotiSort,
  successNotiSort,
  warningNotiSort,
} from "@/utils/notification.config";

const emit = defineEmits(["update:paginationParams"]);

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

// Khởi tạo id hóa đơn
const billId = ref<string | null>(null);

// Tạo biến số tiền đã thanh toán
const paid = ref(0);
// Số tiền phải thanh toán
const amountPayable = ref(0);
// Phụ phí
const surcharge = ref(0);
// Tiền hoàn trả
const refund = ref(0);

const detailDataSources = ref<BillDetailResponse[]>([]);

const copiedBillData = ref<BillResponse | null>(null);

const copiedDataSource = ref(null);

const refereshAllProduct = ref(false);

const { mutate: update } = useUpdateBillConfirm();

const { mutate: updateQuantity } = useUpdateBillDetail();

// Biến object lưu giá trị tiền (đã thanh toán, cần thanh toán, phụ phí, hoàn trả)
const paymentInfo = ref({
  paid: 0,
  amountPayable: 0,
  surcharge: 0,
  refund: 0,
});

// Hàm lấy id hóa đơn trên path
const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

onMounted(() => {
  params.value.idHoaDon = getIdHoaDonFromUrl();
  billId.value = getIdHoaDonFromUrl();
});

// Khai báo voucherId
const voucherId = ref(null);
// Voucher detail
const detail = ref(null);

// Lấy danh sách voucher
const { data: dataDetail, refetch: refetchVoucher } = useGetVoucherById(
  voucherId,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled: false,
  }
);

// Lấy dữ liệu hóa đơn chi tiết
const {
  data: detailData,
  isLoading: isLoadingDetailData,
  isFetching: isFetchingDetailData,
  refetch: refetchData,
} = useGetBillDetails(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
  keepPreviousData: false,
});

// Lấy dữ liệu hóa đơn theo id
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

// Hàm tính cân nặng và chiều dài của đơn hàng
const calculateProductDimensions = () => {
  const totalWeight = detailDataSources?.value.reduce(
    (sum: any, product: any) => {
      return sum + (Number(product.soLuong) || 0) * 200;
    },
    0
  );
  const totalHeight = detailDataSources?.value.reduce(
    (sum: any, product: any) => {
      return sum + (Number(product.soLuong) || 0) * 3;
    },
    0
  );
  return {
    weight: Number(totalWeight) || 0,
    length: 30,
    width: 20,
    height: Number(totalHeight) || 0,
  };
};

// Param tính phí ship
const shippingParams = computed<ShippingFeeRequest>(() => ({
  fromDistrictId: 3440,
  fromWardCode: "13007",
  toDistrictId: "",
  toWardCode: "",
  serviceId: 0,
  ...calculateProductDimensions(),
}));

// Param tìm serviceId để tính phí ship liên tỉnh
const serviceIdParams = ref<ServiceIdRequest>({
  formDistrict: 0,
  toDistrict: 0,
  shopId: 2509559,
});

// API lấy serviceID để dùng cho shipping param
const { data: service, refetch: refetchService } = useGetServiceId(
  serviceIdParams,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled:
      !!serviceIdParams.value.formDistrict &&
      !!serviceIdParams.value.toDistrict,
  }
);

// API tính số tiền ship
const { data: shipping, refetch: refetchShipping } = useGetShippingFee(
  shippingParams,
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
    enabled:
      !!shippingParams.value.toDistrictId && !!shippingParams.value.toWardCode,
  }
);

watch(billId, (newVal) => {
  if (newVal && newVal !== null) {
    refetch();
  }
});

// Hàm tính tổng tiền các sản phẩm có trong giỏ
const totalPrice = computed(() => {
  return detailDataSources.value.reduce((sum, item) => sum + item.thanhTien, 0);
});

// import { toRaw } from 'vue';
watch(
  () => detailData.value?.data?.data,
  (newData) => {
    let newObject = [];
    newObject = newData;
    if (newData) {
      detailDataSources.value = JSON.parse(JSON.stringify(newData || []));
    }

    // if (newData && detailDataSources.value) {
    //   for (let i = 0; i < newObject.length; i++) {
    //     const product = newObject[i];
    //     const copiedProduct: BillDetailResponse = {
    //       catalog: null,
    //       id: null,
    //       maHoaDon: null,
    //       tenSanPhamChiTiet: null,
    //       tenSanPham: null,
    //       imgUrl: null,
    //       soLuong: 0,
    //       gia: null,
    //       thanhTien: 0,
    //       tienGiamHD: 0,
    //       tienShip: 0,
    //       tongTienHD: null,
    //       loaiGiam: null,
    //       giamToiDa: null,
    //       giaTriGiam: 0,
    //       dieuKienGiam: null,
    //       tenPhieuGiam: null,
    //     };

    //     // Set từng trường một
    //     copiedProduct.catalog = product.catalog;
    //     copiedProduct.id = product.id;
    //     copiedProduct.maHoaDon = product.maHoaDon;
    //     copiedProduct.tenSanPhamChiTiet = product.tenSanPhamChiTiet;
    //     copiedProduct.tenSanPham = product.tenSanPham;
    //     copiedProduct.imgUrl = product.imgUrl;
    //     copiedProduct.soLuong = product.soLuong;
    //     copiedProduct.gia = product.gia;
    //     copiedProduct.thanhTien = product.thanhTien;
    //     copiedProduct.tienGiamHD = product.tienGiamHD;
    //     copiedProduct.tienShip = product.tienShip;
    //     copiedProduct.tongTienHD = product.tongTienHD;
    //     copiedProduct.loaiGiam = product.loaiGiam;
    //     copiedProduct.giamToiDa = product.giamToiDa;
    //     copiedProduct.giaTriGiam = product.giaTriGiam;
    //     copiedProduct.dieuKienGiam = product.dieuKienGiam;
    //     copiedProduct.tenPhieuGiam = product.tenPhieuGiam;

    //     // Thêm đối tượng vào mảng sao chép
    //     detailDataSources[i].value = copiedProduct;
    //     // console.log(detailDataSources.value[i]);
    //   }
    //   // console.log(detailDataSources.value);
    // }
    console.log(detailDataSources.value);

    serviceIdParams.value.formDistrict = shippingParams.value.fromDistrictId;
    if (copiedBillData.value && copiedBillData.value.huyen) {
      serviceIdParams.value.toDistrict = Number(copiedBillData.value.huyen);
      console.log(serviceIdParams.value.toDistrict);
      
      if (serviceIdParams.value.toDistrict !== 0) {
        refetchService().then(() => {
          shippingParams.value.serviceId = service?.value?.data[0].service_id;
          shippingParams.value.toDistrictId = copiedBillData.value.huyen;
          shippingParams.value.toWardCode = copiedBillData.value.xa;
          if (shippingParams.value.toWardCode) {
            refetchShipping().then(() => {
              // Miễn phí ship cho hóa đơn từ 2.000.000đ
              detailDataSources.value[0].tienShip = shipping?.value?.data.total;
              if (totalPrice.value < 2000000) {
                detailDataSources.value[0].tienShip =
                  shipping?.value?.data.total;
              } else {
                detailDataSources.value[0].tienShip = 0;
              }
              console.log(totalPrice.value);
              console.log(detailDataSources.value[0].tienShip);
              
            });
          }
        });
      } else {
        detailDataSources.value[0].tienShip = 0;
      }
    }
    console.log(detailDataSources.value);
    
  },
  { immediate: true }
);

// Theo dõi cái bản sao của detail data
watch(
  () => detailDataSources.value,
  (newData) => {
    // console.log(newData);
    console.log(newData);
    if (newData[0]) {
      // Tính toán lại phí giảm sau khi thêm sản phẩm hoặc thay đổi số lượng
      if (detail.value) {
        // Loại giảm = true (tiền mặt)
        if (detail.value.loaiGiam) {
          newData[0] = detail?.value?.giaTriGiam;
          newData[0].tongTienHD =
            totalPrice.value + newData[0].tienShip - newData[0].tienGiamHD;
        } else {
          // Loại giảm = flase (%)
          newData[0].tienGiamHD =
            (totalPrice.value * Number(detail?.value?.giaTriGiam)) / 100;

          newData[0].tongTienHD =
            totalPrice.value + newData[0].tienShip - newData[0].tienGiamHD;
          // console.log(newData[0].tongTienHD);
        }
      } else {
        newData[0].tienGiamHD = 0;
        // newData[0].tienShip = 0;
        newData[0].tongTienHD =
          totalPrice.value + newData[0].tienShip - newData[0].tienGiamHD;
      }

      // Tính toán lại phụ phí/ hoàn trả
      if (newData[0].tongTienHD > paymentInfo.value.paid) {
        paymentInfo.value.amountPayable =
          detailDataSources.value[0].tongTienHD - paymentInfo.value.paid;
        paymentInfo.value.surcharge =
          newData[0].tongTienHD - paymentInfo.value.paid;
        paymentInfo.value.refund = 0;
      } else {
        paymentInfo.value.amountPayable = 0;
        paymentInfo.value.surcharge = 0;
        paymentInfo.value.refund =
          paymentInfo.value.paid - newData[0].tongTienHD;
      }
    }
  },
  { deep: true, immediate: true }
);

// Theo dõi nếu id hóa đơn thay đổi
watch(
  () => billDataById.value,
  (newBillData) => {
    if (newBillData) {
      copiedBillData.value = newBillData;
    }
    voucherId.value = newBillData.idPhieuGiamGia;
    serviceIdParams.value.formDistrict = shippingParams.value.fromDistrictId;
    if (copiedBillData.value && copiedBillData.value.huyen) {
      serviceIdParams.value.toDistrict = Number(copiedBillData.value.huyen);
      if (serviceIdParams.value.toDistrict !== 0) {
        refetchService().then(() => {
          shippingParams.value.serviceId = service?.value?.data[0].service_id;
          shippingParams.value.toDistrictId = copiedBillData.value.huyen;
          shippingParams.value.toWardCode = copiedBillData.value.xa;
          if (shippingParams.value.toWardCode) {
            refetchShipping().then(() => {
              // Miễn phí ship cho hóa đơn từ 2.000.000đ
              if (totalPrice.value < 2000000) {
                detailDataSources.value[0].tienShip =
                  shipping?.value?.data.total;
              } else {
                detailDataSources.value[0].tienShip = 0;
              }
            });
          }
        });
      } else {
        detailDataSources.value[0].tienShip = 0;
      }
    }
  }
);

// Theo dõi nếu hóa đơn có voucherId thì sẽ tính tiền giảm
watch(
  () => voucherId.value,
  (newValue) => {
    if (newValue) {
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

// watch(
//   () => paid.value,
//   (data) => {
//     paymentInfo.value.paid = data;
//   }
// );

// Tính toán totalPages
const totalPage = computed(() => detailData?.value?.data?.totalPages || 1);

// Hàm load lại table khi phân trang
const handlePaginationChange = (newParams: FindBillDetailRequest) => {
  params.value = { ...params.value, ...newParams };
};

const handleGetPaymentInfo = (info) => {
  copiedBillData.value = info;
};

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
          refetch();
        },
        onError: (error) => {
          console.error("Lỗi khi cập nhật:", error);
        },
      }
    );
  }
};

// Hàm load lại danh sách sản phẩm trong giỏ
const handleRefetchData = (record) => {
  const item = detailDataSources.value.find((item) => item.id === record.id);

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
  }
};

// const { data, isLoading, isFetching } = useGetPayHistory(params, {
//   refetchOnWindowClose: false,
//   placeholderData: keepPreviousData,
//   keepPreviousData: false,
// });

// Hàm tính tổng tiền đã thanh toán
const totalAmountPaid = (listPay: any) => {
  return listPay.reduce((total, item) => total + (item.tienKhachDua || 0), 0);
};

// Hàm emit tính tiền đã thanh toán
const getTotalAmountPaid = (totalPaid: number) => {
  paid.value = totalPaid;
  paymentInfo.value.paid = totalPaid;
  // amountPayable.value = detailDataSources.value[0].tongTienHD - totalPaid;
};

// Cập nhật lại hóa đơn sau khi xác nhận đơn hàng
// Ở đây chỉ cập nhật lại tiền vì trc đó đã update hết các thông tin rồi.
const handleUpdateBill = () => {
  try {
    const payload = {
      tienShip: detailDataSources.value[0].tienShip,
      tienGiam: detailDataSources.value[0].tienGiamHD,
      tongTien: detailDataSources.value[0].tongTienHD,
    };

    update(
      { idBill: billId.value, params: payload },
      {
        onSuccess: (result) => {
          // successNotiSort("Cập nhật thông tin thành công");
        },
        onError: (error: any) => {
          // errorNotiSort("Cập nhật thông tin thất bại");
        },
      }
    );
  } catch (error: any) {
    console.error("🚀 ~ handleUpdate ~ error:", error);
    if (error?.response) {
      warningNotiSort(error?.response?.data?.message);
    } else if (error?.errorFields) {
      warningNotiSort("Vui lòng nhập đúng các trường dữ liệu");
    }
  }
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
