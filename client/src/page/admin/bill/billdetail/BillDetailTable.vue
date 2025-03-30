<template>
  <div class="p-4 bg-white rounded-lg shadow">
    <h3 class="text-lg font-bold">
      Thông tin đơn hàng - Đơn hàng {{ copiedBillData?.loaiHD }}
    </h3>
    <div class="border-t mt-2 mb-4"></div>
    <div class="grid grid-cols-2 gap-4 text-sm">
      <div class="flex items-center">
        <span class="font-medium">Mã:</span>
        <span class="ml-2">{{ copiedBillData?.ma }}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Tên khách hàng:</span>
        <span class="ml-2">{{
          copiedBillData?.tenKhachHang || "Khách lẻ"
        }}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">SĐT người nhận:</span>
        <span class="ml-2">{{ copiedBillData?.soDienThoai }}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Trạng thái:</span>
        <a-tag class="ml-2" color="orange">{{
          copiedBillData?.trangThai
        }}</a-tag>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Địa chỉ người nhận:</span>
        <span class="ml-2" color="blue">{{
          copiedBillData?.diaChiNguoiNhan || ""
        }}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Tên người nhận:</span>
        <span class="ml-2">{{ copiedBillData?.tenNguoiNhan || "" }}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Ghi chú:</span>
        <span class="ml-2">{{ copiedBillData?.ghiChu || "" }}</span>
      </div>
    </div>
    <div class="flex justify-end mt-4">
      <a-button
        class="border border-orange-500 bg-transparent text-orange-500 hover:border-orange-300"
        @click="handleOpenModalUpdateBill"
        :disabled="
          [
            'Chờ giao hàng',
            'Đang vận chuyển',
            'Đã giao hàng',
            'Đã thanh toán',
            'Thành công',
            'Đã hủy',
          ].includes(billData?.trangThai)
        "
      >
        Cập nhật
      </a-button>
    </div>
    <update-bill-modal
      :open="isOpenModalUpdateBill"
      :billData="copiedBillData"
      @handleClose="handleCloseModalUpdateBill"
      @onCancel="isOpenModalUpdateBill = false"
      @updated="updateBillData"
      @update:bill="handleUpdateBill"
    />
  </div>

  <!-- Lịch sử thanh toán -->
  <div class="p-4 bg-white rounded-lg shadow">
    <div class="flex justify-between items-center mb-2">
      <h3 class="text-lg font-bold">Lịch sử thanh toán</h3>
      <a-button
        v-if="
          billData?.trangThai === 'Đang vận chuyển' &&
          paymentInfoData.amountPayable > 0
        "
        class="border border-orange-500 bg-transparent text-orange-500 hover:border-orange-300"
        @click="handleOpenModalGetPay"
      >
        Thanh toán
      </a-button>
      <admin-get-delivery-pay-modal
        :open="isOpenModalGetPay"
        :totalPrice="totalPrice"
        :payment-info-data="paymentInfoData"
        @handleClose="handleCloseModalGetPay"
        @onCancel="isOpenModalGetPay = false"
      />
    </div>
    <admin-pay-history
      :isPaymented="isPaymented"
      :billId="billId"
      @get:total-amount="getTotalAmount"
    />
  </div>

  <!-- danh sách sản phẩm chi tiết -->
  <div class="p-4 bg-white rounded-lg shadow">
    <!-- Nút thêm sản phẩm -->
    <div class="flex justify-end mb-4">
      <a-button
        class="border border-orange-500 bg-transparent text-orange-500 hover:border-orange-300"
        @click="handleOpenModalAddProductToOrder"
        :disabled="
          [
            'Chờ giao hàng',
            'Đang vận chuyển',
            'Đã giao hàng',
            'Đã thanh toán',
            'Thành công',
            'Đã hủy',
          ].includes(billData?.trangThai)
        "
      >
        Thêm sản phẩm
      </a-button>

      <!-- Modal thêm sản phẩm -->
      <add-product-detail-modal
        :open="isOpenModalAddProductToOrder"
        @handleClose="handleCloseModalAddProductToOrder"
        @onCancel="isOpenModalAddProductToOrder = false"
        :loadingValue="loadingValue"
      />
    </div>

    <!-- Bảng sản phẩm -->
    <table-example
      class="min-h-[5rem]"
      v-if="props"
      :wrapperClassName="props.wrapperClassName"
      :columns="props.columns"
      :data-source="dataSources"
      :loading="props.loading"
      :pagination-params="props.paginationParams"
      :total-pages="props.totalPages || 1"
      @update:pagination-params="$emit('update:paginationParams', $event)"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'status'" class="text-center">
          <a-tag v-if="record.status === 'false'" color="success">
            Hoạt động
          </a-tag>
          <a-tag v-else-if="record.status === 'true'" color="warning">
            Vô hiệu hóa
          </a-tag>
          <a-tag v-else color="secondary">Không xác định</a-tag>
        </div>
        <div
          v-else-if="column.key === 'action'"
          class="flex items-center justify-center space-x-2"
        >
          <a-tooltip title="Xóa" trigger="hover">
            <a-button
              :disabled="
                [
                  'Chờ giao hàng',
                  'Đang vận chuyển',
                  'Đã giao hàng',
                  'Đã thanh toán',
                  'Thành công',
                  'Đã hủy',
                ].includes(billData?.trangThai)
              "
              class="bg-purple-100"
              size="middle"
              shape="round"
              @click="handleDelete(record)"
            >
              <v-icon name="fa-trash-alt" />
            </a-button>
          </a-tooltip>
        </div>

        <div v-else-if="column.key === 'thanhTien'" style="color: red">
          <!-- <a-input v-model:value="record.thanhTien"></a-input> -->
          {{ formatCurrencyVND(record.thanhTien) }}
        </div>

        <div v-else-if="column.key === 'chiTietSanPham'">
          <p>
            {{ record.tenSanPham }} - {{ record.tenMau }} -
            <a-tag> {{ record.tenKichCo }}</a-tag>
          </p>
          <p style="color: red">
            {{
              record.gia ? formatCurrencyVND(record.gia) : "Không có dữ liệu"
            }}
          </p>
        </div>

        <div v-else-if="column.key === 'anhSanPhamChiTiet'">
          <Image
            :width="60"
            :src="record?.anhSanPhamChiTiet"
            alt="Ảnh SP"
            class="product-image"
          />
        </div>

        <div
          v-if="column.key === 'soLuong'"
          class="flex items-center justify-center space-x-2"
        >
          <input
            type="number"
            min="0"
            v-model="record.soLuong"
            @change="handleChangeQuantity(record)"
            class="w-16 text-center border rounded"
            :disabled="
              [
                'Chờ giao hàng',
                'Đang vận chuyển',
                'Đã giao hàng',
                'Đã thanh toán',
                'Thành công',
                'Đã hủy',
              ].includes(billData?.trangThai)
            "
          />
        </div>
      </template>
    </table-example>
  </div>
</template>

<script lang="ts" setup>
import TableExample from "@/components/ui/TableExample.vue";
import { ColumnType } from "ant-design-vue/es/table";
import { ref, watch, computed, onMounted, createVNode, nextTick } from "vue";
import AdminPayHistory from "./AdminPayHistory.vue";
import UpdateBillModal from "../bill/UpdateBillModals.vue";
import AddProductDetailModal from "./AddProductDetailModal.vue";
import AdminGetDeliveryPayModal from "./AdminGetDeliveryPayModal.vue";
import { formatCurrencyVND } from "@/utils/common.helper";
import { BillResponse } from "@/infrastructure/services/api/admin/bill.api";
import { BillDetailResponse } from "@/infrastructure/services/api/admin/bill-detail.api";
import { Image, Modal } from "ant-design-vue";
import { FindPayHistoryRequest } from "@/infrastructure/services/api/admin/pay-history.api";
import { useGetPayHistory } from "@/infrastructure/services/service/admin/payhistory.action";
import { useUpdateBill } from "@/infrastructure/services/service/admin/bill.action";
import {
  useGetListVoucher,
  useGetPriceNextVoucher,
  useGetShippingFee,
  useGetServiceId,
} from "@/infrastructure/services/service/admin/payment.action";
import { keepPreviousData } from "@tanstack/vue-query";
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
import { useDeleteCartById } from "@/infrastructure/services/service/admin/point-of-sale";
import {
  warningNotiSort,
  successNotiSort,
  errorNotiSort,
} from "@/utils/notification.config";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";

const props = defineProps({
  wrapperClassName: {
    type: String,
    required: false,
    default: "min-h-/[35rem/]",
  },
  columns: {
    type: Array as () => ColumnType[],
    required: true,
  },
  class: String,
  dataSource: Array,
  billData: Object,
  loading: {
    type: Boolean,
    required: true,
  },
  paginationParams: {
    type: Object,
    required: true,
  },
  totalPages: {
    type: Number,
    required: true,
  },
  loadingValue: {
    type: Boolean,
    required: true,
  },
  billId: {
    type: String,
  },
  detail: {
    type: Object,
  },
  paymentInfoData: {
    type: Object,
  },
});

const emit = defineEmits([
  "update:paginationParams",
  "get:payment-info",
  "update-quantity",
  "get:total-amount",
  "refetch-data",
]);

watch(
  () => props?.billData,
  (result) => {
    if (result) {
      // console.log("bill data: ", result);
    }
  }
);

const copiedBillData = ref<BillResponse | null>(null);

watch(
  () => props?.billData,
  (newBillData) => {
    copiedBillData.value = JSON.parse(JSON.stringify(newBillData));
    // console.log(copiedBillData.value);
  }
);

const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

const params = ref<FindPayHistoryRequest>({
  idHoaDon: "",
});

onMounted(() => {
  params.value.idHoaDon = getIdHoaDonFromUrl();
});

// Hàm tính cân nặng và chiều dài của đơn hàng
const calculateProductDimensions = () => {
  const totalWeight = props.dataSource.reduce((sum: any, product: any) => {
    return sum + (Number(product.soLuong) || 0) * 200;
  }, 0);
  const totalHeight = props.dataSource.reduce((sum: any, product: any) => {
    return sum + (Number(product.soLuong) || 0) * 3;
  }, 0);
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

const copiedDataSource = ref(null);

// watch(
//   () => props?.billData,
//   (newBillData) => {
//     copiedBillData.value = JSON.parse(JSON.stringify(newBillData));
//     // voucherId.value = newBillData.idPhieuGiamGia;
//     if (copiedBillData.value && copiedBillData.value.huyen) {
//       serviceIdParams.value.toDistrict = Number(copiedBillData.value.huyen);
//       if (serviceIdParams.value.toDistrict !== 0) {
//         refetchService().then(() => {
//           shippingParams.value.serviceId = service?.value?.data[0].service_id;
//           // console.log(shippingParams.value);
//           shippingParams.value.toDistrictId = copiedBillData.value.huyen;
//           shippingParams.value.toWardCode = copiedBillData.value.xa;
//           if (shippingParams.value.toWardCode) {
//             refetchShipping().then(() => {
//               copiedDataSource.value[0].tienShip = shipping?.value?.data.total;
//               copiedBillData.value.tienShip =
//                 copiedDataSource.value[0].tienShip;
//             });
//           }
//         });
//       } else {
//         copiedDataSource.value[0].tienShip = 0;
//       }
//     }
//   }
// );

const { mutate: deleteOrderDetails } = useDeleteCartById();

const { data: PaymentData } = useGetPayHistory(params, {
  refetchOnWindowClose: false,
  placeholderData: keepPreviousData,
});

const updateBillData = (updatedBill) => {
  // console.log("Dữ liệu cập nhật từ API:", updatedBill);
  copiedBillData.value = updatedBill; // Cập nhật dữ liệu mới từ API
};

//modal update thông tin hóa đơn: người nhận, sđt,. .
const isOpenModalUpdateBill = ref(false);

const isPaymented = ref(false);

const handleOpenModalUpdateBill = () => {
  isOpenModalUpdateBill.value = true;
};

const handleCloseModalUpdateBill = () => {
  isOpenModalUpdateBill.value = false;
};

//modal thêm sản phẩm vào đơn
const isOpenModalAddProductToOrder = ref(false);

const handleOpenModalAddProductToOrder = () => {
  isOpenModalAddProductToOrder.value = true;
};

const handleCloseModalAddProductToOrder = () => {
  isOpenModalAddProductToOrder.value = false;
};

//modal thanh toán sau giao hàng
const isOpenModalGetPay = ref(false);

const handleOpenModalGetPay = () => {
  isOpenModalGetPay.value = true;
};

const handleCloseModalGetPay = () => {
  isOpenModalGetPay.value = false;
  isPaymented.value = !isPaymented.value;
};

const dataSources: BillDetailResponse[] | any = computed(() => {
  return (
    props.dataSource?.map((e: any) => ({
      catalog: e.catalog || null,
      id: e.id || null,
      maHoaDon: e.maHoaDon || null,
      tenSanPhamChiTiet: e.tenSanPhamChiTiet || null,
      tenSanPham: e.tenSanPham || null,
      anhSanPhamChiTiet: e.anhSanPhamChiTiet || null,
      tenKichCo: e.tenKichCo || null,
      tenMau: e.tenMau || null,
      soLuong: e.soLuong || 0,
      gia: e.gia || null,
      thanhTien: e.thanhTien || null,
      tienGiamHD: e.tienGiamHD || null,
      tienShip: e.tienShip || null,
      tongTienHD: e.tongTienHD || null,
      loaiGiam: e.loaiGiam,
      giamToiDa: e.giamToiDa || null,
      giaTriGiam: e.giaTriGiam || null,
      dieuKienGiam: e.dieuKienGiam || null,
      tenPhieuGiam: e.tenPhieuGiam || null,
    })) || []
  );
});

watch(
  () => dataSources.value,
  (newData) => {
    if (newData) {
      // console.log(newData);
      copiedDataSource.value = JSON.parse(JSON.stringify(newData));

      // totalProductPrice.value = newData.reduce(
      //   (total, item) => total + item.thanhTien,
      //   0
      // );

      // Tính phí ship
      // if (copiedBillData.value && copiedBillData.value.huyen) {
      //   serviceIdParams.value.toDistrict = Number(copiedBillData.value.huyen);
      //   if (serviceIdParams.value.toDistrict !== 0) {
      //     refetchService().then(() => {
      //       shippingParams.value.serviceId = service?.value?.data[0].service_id;
      //       shippingParams.value.toDistrictId = copiedBillData.value.huyen;
      //       shippingParams.value.toWardCode = copiedBillData.value.xa;
      //       if (shippingParams.value.toWardCode) {
      //         refetchShipping().then(() => {
      //           copiedDataSource.value[0].tienShip =
      //             shipping?.value?.data.total;
      //           copiedBillData.value.tienShip =
      //             copiedDataSource.value[0].tienShip;
      //         });
      //       }
      //     });
      //   } else {
      //     copiedDataSource.value[0].tienShip = 0;
      //   }
      // }

      // if (detail.value && detail.value.loaiGiam) {
      //   // Loại giảm = true (tiền mặt)
      //   copiedDataSource.value[0].tienGiamHD = detail.value.giaTriGiam;
      //   copiedDataSource.value[0].tongTienHD =
      //     totalProductPrice.value +
      //     copiedDataSource.value[0].tienShip -
      //     copiedDataSource.value[0].tienGiamHD;
      //   copiedBillData.value.tienGiam = copiedDataSource.value[0].tienGiamHD;
      //   copiedBillData.value.tongTien = copiedDataSource.value[0].tongTienHD;
      // } else {
      //   // Loại giảm = flase (%)
      //   if (detail.value && detail.value.giaTriGiam) {
      //     copiedDataSource.value[0].tienGiamHD =
      //       (totalProductPrice.value * Number(detail.value.giaTriGiam)) / 100;
      //   }
      //   copiedDataSource.value[0].tongTienHD =
      //     totalProductPrice.value +
      //     copiedDataSource.value[0].tienShip -
      //     copiedDataSource.value[0].tienGiamHD;
      // }
      // console.log(copiedDataSource.value);
    }
  }
);

const totalPrice = computed(() => props.billData?.tongTien);
// console.log(totalPrice.value);

const { mutate: update } = useUpdateBill();

const modelRefTmp = ref(null);

// Hàm thay update bill khi thay đổi thông tin giao hàng
const handleUpdateBill = async (modelRef: any) => {
  modelRefTmp.value = modelRef;

  Modal.confirm({
    content: "Bạn chắc chắn muốn sửa?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await nextTick();
        // console.log(modelRefTmp.value);

        const payload = {
          soDienThoai: modelRef.soDienThoai,
          diaChiNguoiNhan: modelRef.diaChiNguoiNhan,
          tenNguoiNhan: modelRef.tenNguoiNhan,
          ghiChu: modelRef.ghiChu,
          tinh: modelRef.tinh,
          huyen: modelRef.huyen,
          xa: modelRef.xa,
          idPhieuGiamGia: props.detail ? props.detail.id : null,
        };
        update(
          { idBill: params.value.idHoaDon, params: payload },
          {
            onSuccess: (result) => {
              successNotiSort("Cập nhật thông tin thành công");
              emit("get:payment-info", modelRef);
              isOpenModalUpdateBill.value = false;
            },
            onError: (error: any) => {
              errorNotiSort("Cập nhật thông tin thất bại");
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
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
    },
  });
};

const getTotalAmount = (totalPaid: number) => {
  emit("get:total-amount", totalPaid);
};

const handleChangeQuantity = async (record: any) => {
  if (!record.previousQuantity && record.soLuong !== 0) {
    record.previousQuantity = record.soLuong; // Lưu giá trị cũ nếu chưa có
  }

  if (record.soLuong === 0) {
    Modal.confirm({
      title: "Xác nhận",
      content: "Bạn có chắc chắn muốn xóa sản phẩm này khỏi đơn hàng?",
      okText: "Xóa",
      okType: "danger",
      cancelText: "Hủy",
      onOk() {
        record.thanhTien = 0;
        emit("update-quantity", record);
      },
      onCancel() {
        record.soLuong = record.previousQuantity || 1; // Trả về giá trị cũ
        setTimeout(() => {
          emit("update-quantity", record);
        }, 0);
      },
    });
  } else {
    record.thanhTien = record.soLuong * record.gia;
    emit("update-quantity", record);
    record.previousQuantity = record.soLuong; // Cập nhật lại giá trị trước đó
  }
};

// Xóa sản phẩm trong giỏ hàng
const handleDelete = (productDetail: any) => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn xóa sản phẩm này ra khỏi giỏ?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        // await deleteOrderDetails(productDetail.id);
        emit("refetch-data", productDetail);
        successNotiSort("Xóa thành công");
      } catch (error) {
        errorNotiSort("Xóa thất bại");
      }
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
    },
  });
};
</script>

<style scoped>
.product-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease-in-out;
}

.product-image:hover {
  transform: scale(1.1);
}
</style>
