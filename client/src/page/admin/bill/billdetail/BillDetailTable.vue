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
        <span class="ml-2">{{ copiedBillData?.tenKhachHang }}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">SĐT người nhận:</span>
        <span class="ml-2">{{ copiedBillData?.soDienThoai }}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Trạng thái:</span>
        <a-tag class="ml-2" color="orange">{{copiedBillData?.trangThaiHD}}</a-tag>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Địa chỉ người nhận:</span>
        <span class="ml-2" color="blue">{{ copiedBillData?.diaChiNguoiNhan || ""}}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Tên người nhận:</span>
        <span class="ml-2">{{ copiedBillData?.tenNguoiNhan || ""}}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Ghi chú:</span>
        <span class="ml-2">{{ copiedBillData?.ghiChu || ""}}</span>
      </div>
    </div>
    <div class="flex justify-end mt-4">
      <a-button
        type="primary"
        class="bg-orange-500 hover:bg-orange-600 text-white"
        @click="handleOpenModalUpdateBill"
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
    <h3 class="text-lg font-bold">
      Lịch sử thanh toán
    </h3>
    <admin-pay-history/>
  </div>

  <!-- danh sách sản phẩm chi tiết -->
  <div class="p-4 bg-white rounded-lg shadow">
    <!-- Nút thêm sản phẩm -->
    <div class="flex justify-end mt-4">
    <a-button
      type="primary"
      class="bg-orange-500 hover:bg-orange-600 text-white"
      style="margin-bottom: 10px;"
    >
      Thêm sản phẩm
    </a-button>
  </div>

  <!-- Bảng sản phẩm -->
  <table-example class="min-h-[15rem]"
    v-if="props"
    :wrapperClassName="props.wrapperClassName"
    :columns="props.columns"
    :data-source="props.dataSource"
    :loading="props.loading"
    :pagination-params="props.paginationParams"
    :total-pages="props.totalPages || 1"
    @update:pagination-params="updatePaginationParams"
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

      <div v-else-if="column.key === 'thanhTien'" style="color:red">
        {{ formatCurrencyVND(record.thanhTien) }}
      </div>

      <div v-else-if="column.key === 'chiTietSanPham'">
        <p>{{ record.tenSanPhamChiTiet }} -  <a-tag> {{ record.tenKichCo }}</a-tag></p>
        <p style="color: red">{{ record.gia ? formatCurrencyVND(record.gia) : "Không có dữ liệu" }}</p>
      </div>

      <div v-if="column.key === 'soLuong'" class="flex items-center justify-center space-x-2">
          <input
            type="number"
            v-model.number="record.soLuong"
            @change="validateQuantity(record)"
            class="w-16 text-center border rounded"
          />
        </div>
      
    </template>
  </table-example>
  </div>
</template>

<script lang="ts" setup>
import TableExample from "@/components/ui/TableExample.vue";
import { ColumnType } from "ant-design-vue/es/table";
import { ref, watch } from "vue";
import AdminPayHistory from "./AdminPayHistory.vue";
import UpdateBillModal from "../bill/UpdateBillModal.vue";
import { formatCurrencyVND } from "@/utils/common.helper";
import { BillResponse, getBillById } from "@/infrastructure/services/api/admin/bill.api";

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


const emit = defineEmits(["update:paginationParams"]);

const updatePaginationParams = (params: any) => {
  emit("update:paginationParams", params);
};

watch(
  () => props?.billData,
  (result) => {
    if (result) {
      // console.log("bill data: ", result);
    }
  }
);

const copiedBillData = ref<BillResponse | null>(null);

watch(() => props?.billData, (newBillData) => {
  copiedBillData.value = JSON.parse(JSON.stringify(newBillData));
  // console.log(copiedBillData.value); 
});

const updateBillData = (updatedBill) => {
  copiedBillData.value = updatedBill; // Cập nhật dữ liệu mới từ API
};

const isOpenModalUpdateBill = ref(false);

const handleOpenModalUpdateBill = () => {
  isOpenModalUpdateBill.value = true;
};

const handleCloseModalUpdateBill = () => {
  isOpenModalUpdateBill.value = false;
};

const increaseQuantity = (record: any) => {
  if (record.soLuong !== undefined) {
    record.soLuong++; // Tăng số lượng
    updateTotal(record);
  }
};

const decreaseQuantity = (record: any) => {
  if (record.soLuong !== undefined && record.soLuong > 1) {
    record.soLuong--; // Giảm số lượng
    updateTotal(record);
  }
};

const validateQuantity = (record: any) => {
  if (record.soLuong < 1 || isNaN(record.soLuong)) {
    record.soLuong = 1; // Đảm bảo số lượng >= 1
  }
  updateTotal(record);
};

const updateTotal = (record: any) => {
  // Cập nhật tổng tiền dựa trên số lượng mới
  if (record.gia) {
    record.thanhTien = record.gia * record.soLuong;
  }
};
</script>

<style scoped>

</style>
