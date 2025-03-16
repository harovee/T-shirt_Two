<template>
  <a-row :gutter="[8, 24]">
    <a-col :span="8">
      <div class="p-4 bg-white rounded-lg border border-gray-300 p-5 me-3">
        <h3 class="text-lg font-bold">
          Thông tin đơn hàng - Đơn hàng {{ copiedBillData?.loaiHD }}
        </h3>
        <div>
          <div class="flex items-center">
            <p class="font-medium">Mã đơn hàng:</p>
            <p class="ml-2">{{ copiedBillData?.ma }}</p>
          </div>
          <div class="flex items-center">
            <p class="font-medium">Hình thức đặt hàng:</p>
            <p class="ml-2">{{ copiedBillData?.loaiHD }}</p>
          </div>
          <div class="flex items-center">
            <p class="font-medium">Trạng thái:</p>
            <a-tag class="ml-2" color="orange">{{
              copiedBillData?.trangThai
            }}</a-tag>
          </div>
          <div class="flex items-center">
            <p class="font-medium">Khách hàng:</p>
            <p class="ml-2">
              {{ copiedBillData?.tenKhachHang || "Khách lẻ" }}
            </p>
          </div>
          <div class="flex items-center">
            <p class="font-medium">Tên người nhận:</p>
            <p class="ml-2">{{ copiedBillData?.tenNguoiNhan || "" }}</p>
          </div>
          <div class="flex items-center">
            <p class="font-medium">SĐT người nhận:</p>
            <p class="ml-2">{{ copiedBillData?.soDienThoai }}</p>
          </div>

          <div class="flex items-center">
            <p class="font-medium">Địa chỉ người nhận:</p>
            <p class="ml-2" color="blue">
              {{ copiedBillData?.diaChiNguoiNhan || "" }}
            </p>
          </div>

          <div class="flex items-center">
            <p class="font-medium">Ghi chú:</p>
            <p class="ml-2">{{ copiedBillData?.ghiChu || "" }}</p>
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
      </div>
      <div
        class="mp-4 bg-white rounded-lg border border-gray-300 p-5 mt-5 me-3"
      >
        <div class="flex flex justify-between block mb-4">
          <span class="text-lg">Tiền hàng:</span>
          <span v-if="copiedDataSource" class="text-lg">{{
            formatCurrencyVND(totalPrice)
          }}</span>
        </div>
        <div class="flex flex justify-between block mb-4">
          <span class="text-lg">Giảm giá:</span>
          <span v-if="copiedDataSource" class="text-lg text-green-500">{{
            copiedDataSource && copiedDataSource.length > 0
              ? `- ${formatCurrencyVND(copiedDataSource[0].tienGiamHD)}`
              : "0 VND"
          }}</span>
        </div>
        <div class="flex flex justify-between block mb-4">
          <span class="text-lg">Phí vận chuyển:</span>
          <span v-if="copiedDataSource" class="text-lg">{{
            copiedDataSource && copiedDataSource.length > 0
              ? `${formatCurrencyVND(copiedDataSource[0].tienShip)}`
              : "0 VND"
          }}</span>
        </div>
        <div class="flex flex justify-between block font-semibold text-xl">
          <span>Tổng tiền:</span>
          <span v-if="copiedDataSource">{{
            copiedDataSource && copiedDataSource.length > 0
              ? formatCurrencyVND(copiedDataSource[0].tongTienHD)
              : "0 VND"
          }}</span>
        </div>
      </div>
    </a-col>
    <a-col :span="16">
      <div class="p-4 bg-white rounded-lg border border-gray-300">
        <div class="flex justify-end mb-4">
          <my-order-step-history
            :data-source="dataHistory || {}"
            :loading="isHistoryLoading || isHistoryFetching"
          />
        </div>
      </div>
      <div class="p-4 bg-white rounded-lg border border-gray-300 mt-5">
        <!-- Nút thêm sản phẩm -->

        <div class="flex justify-between mb-4">
          <div class="flex items-center">
            <h3 class="text-xl mt-2">Sản phẩm đã mua</h3>
            <span class="ms-3">({{ dataSources.length }} sản phẩm)</span>
          </div>
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
          <my-order-add-product-modal
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
              <a-tooltip title="Hoàn trả" trigger="hover">
                <a-button class="bg-purple-100" size="middle" shape="round">
                  <v-icon name="fa-undo-alt" />
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
                  record.gia
                    ? formatCurrencyVND(record.gia)
                    : "Không có dữ liệu"
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
    </a-col>
  </a-row>
  <my-order-update-modal
    :open="isOpenModalUpdateBill"
    :billData="copiedBillData"
    @handleClose="handleCloseModalUpdateBill"
    @onCancel="isOpenModalUpdateBill = false"
    @updated="updateBillData"
    @update:bill="handleUpdateBill"
  />

  <!-- danh sách sản phẩm chi tiết -->
</template>

<script lang="ts" setup>
import type { UnwrapRef } from "vue";
import TableExample from "@/components/ui/TableExample.vue";
import {
  defaultVoucherDatePickerRules,
  defaultVoucherRequest,
  FormState,
} from "@/page/admin/voucher/base/DefaultConfig";
import { ColumnType } from "ant-design-vue/es/table";
import {
  ref,
  watch,
  computed,
  onMounted,
  reactive,
  createVNode,
  nextTick,
} from "vue";
import MyOrderUpdateModal from "./MyOrderUpdateModal.vue";
import MyOrderAddProductModal from "./MyOrderAddProductModal.vue";
import { formatCurrencyVND } from "@/utils/common.helper";
import { BillResponse } from "@/infrastructure/services/api/admin/bill.api";
import { BillDetailResponse } from "@/infrastructure/services/api/admin/bill-detail.api";
import { Image, Modal } from "ant-design-vue";
import MyOrderStepHistory from "./MyOrderStepHistory.vue";
import { FindBillHistoryRequest } from "@/infrastructure/services/api/admin/billhistory.api";
import { useGetBillHistory } from "@/infrastructure/services/service/admin/billhistory.action";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  FindKhachHangRequest,
  VoucherAndCustomerVoucherRequest,
  PhieuGiamGiaRequest,
} from "@/infrastructure/services/api/admin/voucher/voucher.api";
import { useUpdateBill } from "@/infrastructure/services/service/admin/bill.action";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import {
  errorNotiSort,
  successNotiSort,
  warningNotiSort,
} from "@/utils/notification.config";
import {
  useUpdateVoucher,
  useUpdateCustomerVoucher,
  useGetListKhachHang,
  useGetVoucherById,
  useGetCusTomerByIdPhieuGiamGia,
} from "@/infrastructure/services/service/admin/voucher/voucher.action";
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
import {
  useGetListVoucher,
  useGetPriceNextVoucher,
  useGetShippingFee,
  useGetServiceId,
} from "@/infrastructure/services/service/admin/payment.action";
import { ok } from "assert";

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
  // dataSource: Array,
  dataSource: {
    type: Array as () => BillDetailResponse[], // ✅ Định rõ kiểu mảng chứa các sản phẩm
    required: true,
  },
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
});

const emit = defineEmits(["update:paginationParams", "update-quantity"]);

// Lấy id hóa đơn trên path
const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

const billId = getIdHoaDonFromUrl();

const paramsHistory = ref<FindBillHistoryRequest>({
  page: 1,
  size: 10,
  idHoaDon: "",
});

onMounted(() => {
  paramsHistory.value.idHoaDon = getIdHoaDonFromUrl();
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
    weight: totalWeight,
    length: 30,
    width: 20,
    height: totalHeight,
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

// Hàm gọi api lấy lịch sử hóa đơn
const {
  data: historyData,
  isLoading: isHistoryLoading,
  isFetching: isHistoryFetching,
} = useGetBillHistory(paramsHistory, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

watch(
  () => props.dataSource,
  (newData) => {
    if (newData) {
      serviceIdParams.value.formDistrict = shippingParams.value.fromDistrictId;
    }
  }
);

const dataHistory = computed(() => historyData?.value);

const voucherId = ref(null);

const {
  data: dataDetail,
  isLoading,
  isFetching,
  refetch,
} = useGetVoucherById(voucherId, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
  enabled: false,
});

const formState: UnwrapRef<FormState> = reactive({
  ten: "",
  loaiGiam: false,
  giaTriGiam: "",
  giamToiDa: "",
  soLuong: 0,
  dieuKienGiam: "",
  ngayBatDauVaKetThuc: [],
  kieu: false,
});

const detail = ref(null);

watch(
  () => voucherId.value,
  (newValue) => {
    if (newValue) {
      refetch().then(() => {
        detail.value = dataDetail?.value?.data?.data;
        if (detail.value.loaiGiam) {
          // Loại giảm = true (tiền mặt)
          copiedDataSource.value[0].tienGiamHD = detail.value.giaTriGiam;
          copiedDataSource.value[0].tongTienHD =
            totalProductPrice.value +
            copiedDataSource.value[0].tienShip -
            copiedDataSource.value[0].tienGiamHD;
          copiedBillData.value.tienGiam = copiedDataSource.value[0].tienGiamHD;
          copiedBillData.value.tongTien = copiedDataSource.value[0].tongTienHD;
        } else {
          // Loại giảm = flase (%)
          copiedDataSource.value[0].tienGiamHD =
            (totalProductPrice.value * Number(detail.value.giaTriGiam)) / 100;
          // if (copiedBillData.value) {
          //   copiedBillData.value.tienGiam = copiedDataSource.value[0].tienGiamHD;
          //   copiedBillData.value.tongTien = copiedDataSource.value[0].tongTienHD;
          // }

          copiedDataSource.value[0].tongTienHD =
            totalProductPrice.value +
            copiedDataSource.value[0].tienShip -
            copiedDataSource.value[0].tienGiamHD;
          // console.log(copiedBillData.value);
        }
      });
    }
  },
  { immediate: true }
);

watch(
  () => props?.billData,
  (result) => {
    if (result) {
      // console.log("bill data: ", result);
    }
  }
);

const copiedBillData = ref<BillResponse | null>(null);

const copiedDataSource = ref(null);

watch(
  () => props?.billData,
  (newBillData) => {
    copiedBillData.value = JSON.parse(JSON.stringify(newBillData));
    voucherId.value = newBillData.idPhieuGiamGia;
    if (copiedBillData.value && copiedBillData.value.huyen) {
      serviceIdParams.value.toDistrict = Number(copiedBillData.value.huyen);
      if (serviceIdParams.value.toDistrict !== 0) {
        refetchService().then(() => {
          shippingParams.value.serviceId = service?.value?.data[0].service_id;
          // console.log(shippingParams.value);
          shippingParams.value.toDistrictId = copiedBillData.value.huyen;
          shippingParams.value.toWardCode = copiedBillData.value.xa;
          if (shippingParams.value.toWardCode) {
            refetchShipping().then(() => {
              copiedDataSource.value[0].tienShip = shipping?.value?.data.total;
              copiedBillData.value.tienShip =
                copiedDataSource.value[0].tienShip;
            });
          }
        });
      } else {
        copiedDataSource.value[0].tienShip = 0;
      }
    }
  }
);

const updateBillData = (updatedBill) => {
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

const totalProductPrice = ref(0);

watch(
  () => dataSources.value,
  (newData) => {
    if (newData) {
      copiedDataSource.value = JSON.parse(JSON.stringify(newData));

      totalProductPrice.value = newData.reduce(
        (total, item) => total + item.thanhTien,
        0
      );

      // Tính phí ship
      if (copiedBillData.value && copiedBillData.value.huyen) {
        serviceIdParams.value.toDistrict = Number(copiedBillData.value.huyen);
        if (serviceIdParams.value.toDistrict !== 0) {
          refetchService().then(() => {
            shippingParams.value.serviceId = service?.value?.data[0].service_id;
            shippingParams.value.toDistrictId = copiedBillData.value.huyen;
            shippingParams.value.toWardCode = copiedBillData.value.xa;
            if (shippingParams.value.toWardCode) {
              refetchShipping().then(() => {
                copiedDataSource.value[0].tienShip =
                  shipping?.value?.data.total;
                copiedBillData.value.tienShip =
                  copiedDataSource.value[0].tienShip;
              });
            }
          });
        } else {
          copiedDataSource.value[0].tienShip = 0;
        }
      }

      if (detail.value && detail.value.loaiGiam) {
        // Loại giảm = true (tiền mặt)
        copiedDataSource.value[0].tienGiamHD = detail.value.giaTriGiam;
        copiedDataSource.value[0].tongTienHD =
          totalProductPrice.value +
          copiedDataSource.value[0].tienShip -
          copiedDataSource.value[0].tienGiamHD;
        copiedBillData.value.tienGiam = copiedDataSource.value[0].tienGiamHD;
        copiedBillData.value.tongTien = copiedDataSource.value[0].tongTienHD;
      } else {
        // Loại giảm = flase (%)
        if (detail.value && detail.value.giaTriGiam) {
          copiedDataSource.value[0].tienGiamHD =
            (totalProductPrice.value * Number(detail.value.giaTriGiam)) / 100;
        }
        copiedDataSource.value[0].tongTienHD =
          totalProductPrice.value +
          copiedDataSource.value[0].tienShip -
          copiedDataSource.value[0].tienGiamHD;
      }
    }
  }
);

const { mutate: update } = useUpdateBill();

const modelRefTmp = ref(null);

const handleUpdateBill = async (modelRef: any) => {
  modelRefTmp.value = modelRef;

  Modal.confirm({
    content: "Bạn chắc chắn muốn sửa?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await nextTick();
        const payload = {
          soDienThoai: modelRef.soDienThoai,
          diaChiNguoiNhan: modelRef.diaChiNguoiNhan,
          tenNguoiNhan: modelRef.tenNguoiNhan,
          ghiChu: modelRef.ghiChu,
          tinh: modelRef.tinh,
          huyen: modelRef.huyen,
          xa: modelRef.xa,
          idPhieuGiamGia: detail.value ? detail.value.id : null,
          tienShip: copiedDataSource.value[0].tienShip,
          tienGiam: copiedDataSource.value[0].tienGiamHD,
          tongTien: copiedDataSource.value[0].tongTienHD,
        };
        update(
          { idBill: billId, params: payload },
          {
            onSuccess: (result) => {
              successNotiSort("Cập nhật thông tin thành công");
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

const loadTotalPrice = () => {};

const totalPrice = computed(() => totalProductPrice.value);

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
  await nextTick();

  setTimeout(() => {
    const payload = {
      soDienThoai: copiedBillData.value.soDienThoai,
      diaChiNguoiNhan: copiedBillData.value.diaChiNguoiNhan,
      tenNguoiNhan: copiedBillData.value.tenNguoiNhan,
      ghiChu: copiedBillData.value.ghiChu,
      tinh: copiedBillData.value.tinh,
      huyen: copiedBillData.value.huyen,
      xa: copiedBillData.value.xa,
      idPhieuGiamGia: detail.value ? detail.value.id : null,
      tienShip: copiedDataSource.value[0]?.tienShip || 0,
      tienGiam: copiedDataSource.value[0]?.tienGiamHD || 0,
      tongTien: copiedDataSource.value[0]?.tongTienHD || 0,
    };
    try {
      // await validate();
      update(
        { idBill: billId, params: payload },
        {
          onSuccess: (result) => {
            successNotiSort("Cập nhật thông tin thành công");
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
      }
    }
  }, 1000);
}
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
