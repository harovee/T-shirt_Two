<template>
  <div class="p-4 rounded-xl border-2">
    <div class="flex justify-between items-center">
      <div>
        <h3 class="text-xl font-semibold text-gray-800">Danh sách hóa đơn</h3>
      </div>
      <div class="ml-auto p-2.5">
        <a-tooltip title="Xuất file Excel" trigger="hover">
          <a-button
            class="bg-purple-300 flex justify-between items-center gap-2"
            size="large"
            @click="exportToExcel"
          >
            <FileExcelOutlined />
          </a-button>
        </a-tooltip>
      </div>
    </div>
    <table-example
      wrapperClassName="min-h-[410px]"
      :columns="columnsBill"
      :data-source="props.dataSource?.data"
      :loading="loading"
      :pagination-params="paginationParams || {}"
      :total-pages="props.dataSource?.totalPages || 1"
      @update:pagination-params="$emit('update:paginationParams', $event)"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'another'" class="text-center"></div>
        <div v-else-if="column.key === 'ngayTao'" class="text-center">
          {{ convertDateFormatTime(record.ngayTao) }}
        </div>
        <div v-else-if="column.key === 'tongTien'" class="text-center">
          {{ formatCurrencyVND(record.tongTien) }}
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
          <a-tooltip title="Chi tiết hóa đơn" trigger="hover">
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

const exportToExcel = () => {
  const data = props.dataSource?.data || [];

  // Định nghĩa các cột và tên cột tương ứng
  const columnsMapping = [
    { key: 'ma', title: 'Mã hóa đơn' },
    { key: 'maNhanVien', title: 'Mã nhân viên' },
    { key: 'tenKhachHang', title: 'Khách hàng' },
    { key: 'tenNguoiNhan', title: 'Tên người nhận' },
    { key: 'diaChiNguoiNhan', title: 'Địa chỉ người nhận' },
    { key: 'soDienThoai', title: 'Số điện thoại' },
    { key: 'loaiHD', title: 'Loại hóa đơn' },
    { key: 'tongTien', title: 'Tổng tiền' },
    { key: 'tienGiam', title: 'Tiền giảm' },
    { key: 'tienShip', title: 'Tiền ship' },
    { key: 'ghiChu', title: 'Ghi chú' },
    { key: 'ngayTao', title: 'Ngày tạo' },
    { key: 'trangThai', title: 'Trạng thái' },
  ];

  // Chuyển đổi dữ liệu và tên cột
  const formattedData = data.map((item: any) => {
    const newItem: any = {};
    columnsMapping.forEach((col) => {
      if (col.key === 'ngayTao') {
        newItem[col.title] = convertDateFormatTime(item[col.key]);
      } else if (col.key === 'tongTien') {
        newItem[col.title] = formatCurrencyVND(item[col.key]);
      } else {
        newItem[col.title] = item[col.key];
      }
    });
    return newItem;
  });

  // Tạo worksheet và workbook
  const ws = XLSX.utils.json_to_sheet(formattedData);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "Danh sách hóa đơn");

  // Xuất file Excel
  XLSX.writeFile(wb, "danh_sach_hoa_don.xlsx");
};

const handleRedirectBillDetail = (idHoaDon: string) => {
  const detailBillPath = {
    path:
      ROUTES_CONSTANTS.ADMIN.path +
      "/" +
      ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_DETAIL.path,
    query: { idHoaDon },
  };
  // console.log(detailBillPath)
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
    title: "Mã nhân viên",
    dataIndex: "maNhanVien",
    key: "maNhanVien",
    ellipsis: true,
    width: 110,
    align: "center",
  },
  {
    title: "Khách hàng",
    dataIndex: "tenKhachHang",
    key: "tenKhachHang",
    ellipsis: true,
    width: 140,
    align: "center",
  },
  {
    title: "Số điện thoại",
    dataIndex: "soDienThoai",
    key: "soDienThoai",
    ellipsis: true,
    width: 130,
    align: "center",
  },
  {
    title: "Loại hóa đơn",
    dataIndex: "loaiHD",
    key: "loaiHD",
    ellipsis: true,
    width: 110,
    align: "center",
  },
  {
    title: "Tổng tiền",
    dataIndex: "tongTien",
    key: "tongTien",
    ellipsis: true,
    width: 110,
    align: "center",
  },

  {
    title: "Ngày tạo",
    dataIndex: "ngayTao",
    key: "ngayTao",
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
    title: "Hành động",
    key: "action",
    align: "center",
    width: 100,
    fixed: "right",
  },
];
</script>
