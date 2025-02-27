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
        <span class="ml-2">{{ copiedBillData?.tenKhachHang || "Khách lẻ" }}</span>
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
    />
  </div>

  <!-- Lịch sử thanh toán -->
  <div class="p-4 bg-white rounded-lg shadow">
    <div class="flex justify-between items-center mb-2">
      <h3 class="text-lg font-bold">Lịch sử thanh toán</h3>
      <a-button
        v-if="billData?.trangThai === 'Đã giao hàng'"
        class="border border-orange-500 bg-transparent text-orange-500 hover:border-orange-300"
        @click="handleOpenModalGetPay"
      >
        Thanh toán
      </a-button>
      <admin-get-delivery-pay-modal
        :open="isOpenModalGetPay"
        :totalPrice="totalPrice"
        @handleClose="handleCloseModalGetPay"
        @onCancel="isOpenModalGetPay = false"
      />
    </div>
    <admin-pay-history />
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
      />
    </div>

    <!-- Bảng sản phẩm -->
    <table-example
      class="min-h-[15rem]"
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
import { ref, watch, computed } from "vue";
import AdminPayHistory from "./AdminPayHistory.vue";
import UpdateBillModal from "../bill/UpdateBillModal.vue";
import AddProductDetailModal from "./AddProductDetailModal.vue";
import AdminGetDeliveryPayModal from "./AdminGetDeliveryPayModal.vue";
import { formatCurrencyVND } from "@/utils/common.helper";
import { BillResponse } from "@/infrastructure/services/api/admin/bill.api";
import { BillDetailResponse } from "@/infrastructure/services/api/admin/bill-detail.api";
import { Image, Modal } from "ant-design-vue";

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
});

const emit = defineEmits(["update:paginationParams", "update-quantity"]);

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

const updateBillData = (updatedBill) => {
  // console.log("Dữ liệu cập nhật từ API:", updatedBill);
  copiedBillData.value = updatedBill; // Cập nhật dữ liệu mới từ API
};

//modal update thông tin hóa đơn: người nhận, sđt,. .
const isOpenModalUpdateBill = ref(false);

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
    }
  }
);

const totalPrice = computed(() => props.billData?.tongTien);
// console.log(totalPrice.value);

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
