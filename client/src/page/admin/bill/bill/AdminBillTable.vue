<template>
  <div class="p-4 rounded-xl border-2 shadow-purple-950 shadow-xl">
    <div class="flex justify-between items-center">
      <div>
        <h3 class="text-xl font-semibold text-gray-800">Danh sách hóa đơn</h3>
      </div>
      <div class="p-2.5"></div>
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
import { convertDateFormatTime, formatCurrencyVND } from "@/utils/common.helper";


const emit = defineEmits(["update:paginationParams",]);

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
  paginationParams: Object,
});

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
