<template>
  <div>
    <table-example
      class="min-h-[5rem]"
      :columns="columnsBill"
      :data-source="props.dataSource?.data"
      :loading="loading"
      :pagination-params="paginationParams || {}"
      :total-pages="props.dataSource?.totalPages || 1"
      @update:pagination-params="$emit('update:paginationParams', $event)"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'another'" class="text-center"></div>
        <div v-else-if="column.key === 'tongTien'" class="text-center">
          {{ formatCurrencyVND(record.tongTien) }}
        </div>
        <div v-else-if="column.key === 'tienGiam'" class="text-center">
          {{ formatCurrencyVND(record.tienGiam) }}
        </div>
        <div v-else-if="column.key === 'phiShip'" class="text-center">
          {{ formatCurrencyVND(record.phiShip) }}
        </div>
        <div v-else-if="column.key === 'daTra'" class="text-center">
          {{ formatCurrencyVND(record.daTra) }}
        </div>
        <div v-else-if="column.key === 'tienPhaiTra'" class="text-center">
          {{ formatCurrencyVND(record.tienPhaiTra) }}
        </div>
        <div v-else-if="column.key === 'status'" class="text-center">
          <a-tag v-if="record.trangThai === 'Thành công'" color="success"
            >Thành công</a-tag
          >

          <a-tag v-else-if="record.trangThai === 'Chờ xác nhận'" color="warning"
            >Chờ xác nhận</a-tag
          >

          <a-tag
            v-else-if="record.trangThai === 'Chờ giao hàng'"
            color="processing"
            >Chờ giao hàng</a-tag
          >

          <a-tag
            v-else-if="record.trangThai === 'Đang vận chuyển'"
            color="default"
            >Đang vận chuyển</a-tag
          >

          <a-tag v-else-if="record.trangThai === 'Đã giao hàng'" color="default"
            >Đã giao hàng</a-tag
          >

          <a-tag
            v-else-if="record.trangThai === 'Đã thanh toán'"
            color="default"
            >Đã thanh toán</a-tag
          >

          <a-tag v-else-if="record.trangThai === 'Trả hàng'" color="error"
            >Trả hàng</a-tag
          >
        <a-tag v-else-if="record.trangThai === 'Đã hủy'" class="text-red-500 border-red-500"
            >Đã hủy</a-tag
          >
          <a-tag v-else color="secondary">Không xác định</a-tag>
        </div>

        <div v-else-if="column.key === 'loaiHD'" class="text-center">
          <a-tag v-if="record.loaiHD === 'Online'" color="success"
            >Online</a-tag
          >
          <a-tag v-else-if="record.loaiHD === 'Tại quầy'" color="default"
            >Tại quầy</a-tag
          >

          <a-tag v-else color="secondary">Không xác định</a-tag>
        </div>
        <div
          v-else-if="column.key === 'action'"
          class="flex items-center justify-center space-x-2"
        >
          <a-tooltip title="Chi tiết" trigger="hover">
            <a-button
              class="bg-purple-100"
              size="middle"
              shape="round"
              @click="handleRedirectBillDetail(record.id)"
            >
              <v-icon name="fa-eye" />
            </a-button>
          </a-tooltip>
        </div>
      </template>
    </table-example>
  </div>
</template>

<script lang="ts" setup>
import { ColumnType } from "ant-design-vue/es/table";
import { defineEmits } from "vue";
import TableExample from "@/components/ui/TableExample.vue";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import router from "@/infrastructure/routes/router";
import { FilePdfOutlined, FileExcelOutlined } from "@ant-design/icons-vue";
import {
  convertDateFormatTime,
  formatCurrencyVND,
} from "@/utils/common.helper";
import * as XLSX from 'xlsx';

const emit = defineEmits(["update:paginationParams"]);

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
  paginationParams: Object,
});

const handleRedirectBillDetail = (idHoaDon: string) => {
  const detailBillPath = {
    path:
      ROUTES_CONSTANTS.CLIENT.children.MY_ORDER_DETAIL.path,
    query: { idHoaDon },
  };
  router.push(detailBillPath);
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
    title: "Mã hóa đơn",
    dataIndex: "ma",
    key: "ma",
    ellipsis: true,
    width: 100,
    align: "center",
  },
  {
    title: "Tổng giá trị",
    dataIndex: "tongTien",
    key: "tongTien",
    ellipsis: true,
    width: 110,
    align: "center",
  },
  {
    title: "Tiền giảm",
    dataIndex: "tienGiam",
    key: "tienGiam",
    ellipsis: true,
    width: 140,
    align: "center",
  },
  {
    title: "Phí ship",
    dataIndex: "phiShip",
    key: "phiShip",
    ellipsis: true,
    width: 130,
    align: "center",
  },
  {
    title: "Đã trả",
    dataIndex: "daTra",
    key: "daTra",
    ellipsis: true,
    width: 110,
    align: "center",
  },

  {
    title: "Cần thanh toán",
    dataIndex: "tienPhaiTra",
    key: "tienPhaiTra",
    ellipsis: true,
    width: 110,
    align: "center",
  },

  {
    title: "Trạng thái",
    dataIndex: "trangThai",
    key: "status",
    ellipsis: true,
    width: 180,
    align: "center",
  },
  {
    title: "PT nhận",
    dataIndex: "phuongThucNhan",
    key: "phuongThucNhan",
    ellipsis: true,
    width: 180,
    align: "center",
  },
  {
    title: "Loại đơn",
    dataIndex: "loaiDon",
    key: "loaiDon",
    ellipsis: true,
    width: 180,
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
