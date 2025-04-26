<template>
  <div class="p-4 rounded-xl border-2 shadow-purple-950">
    <div class="flex justify-between items-center">
      <div>
        <h3 class="text-xl font-semibold text-gray-800">Danh sách phiếu giảm giá </h3>
        <p class="text-sm text-gray-500">
          Hiển thị danh sách phiếu giảm giá T-Shirts Two
        </p>
      </div>
      <div class="p-2.5">
        <a-tooltip
          title="Thêm phiếu giảm giá"
          trigger="hover"
        >
          <a-button
            class="bg-purple-300 flex justify-between items-center gap-2"
            size="large"
            @click="handleRedirectVoucherAdd"
          >
            <v-icon name="md-addcircle" />
          </a-button>
        </a-tooltip>
      </div>
    </div>
    <table-spotify
      wrapperClassName="min-h-[410px]"
      :columns="columnsVoucher"
      :data-source="props.dataSource?.data"
      :loading="loading"
      :pagination-params="paginationParams || {}"
      :total-pages="props.dataSource?.totalPages || 1"
      @update:pagination-params="$emit('update:paginationParams', $event)"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'another'" class="text-center">
        </div>
        <div v-if="column.key === 'giaTriGiam'" class="text-left">
            <span v-if="record.loaiGiam === true || record.loaiGiam === 'true'">
                {{ formatCurrencyVND(record.giaTriGiam) }}
            </span>
            <span v-else-if="record.loaiGiam === false || record.loaiGiam === 'false'">
                {{ record.giaTriGiam }} %
            </span>
            <span v-else>
                Không xác định
            </span>
        </div>
        <div v-if="column.key === 'dieuKienGiam'" class="text-left">
                {{ formatCurrencyVND(record.dieuKienGiam) }}
        </div>
        <div v-else-if="column.key === 'trangThai'" class="text-center">
            <a-tag v-if="record.trangThai === 'ACTIVE' || record.trangThai === 'ACTIVE'" color="success">Đang áp dụng</a-tag>
            <a-tag v-else-if="record.trangThai === 'NOT_STARTED' || record.trangThai === 'NOT_STARTED'" color="warning">Sắp diễn ra</a-tag>
            <a-tag v-else-if="record.trangThai === 'EXPIRED' || record.trangThai === 'EXPRIXED'" color="error">Hết hạn</a-tag>
            <a-tag v-else-if="record.trangThai === 'INACTIVE' || record.trangThai === 'INACTIVE'" color="default">Vô hiệu hóa</a-tag>
            <a-tag v-else color="secondary">Không xác định</a-tag>
        </div>
        <div v-else-if="column.key === 'kieu'" >
            <a-tag v-if="record.kieu === true || record.kieu === 'true'" color="blue">Cá nhân</a-tag>
            <a-tag v-else-if="record.kieu === false || record.kieu === 'false'" color="green">Công khai</a-tag>
            <span v-else color="secondary">Không xác định</span>
        </div>
        <div v-if="column.key === 'ngayBatDau'" class="text-left">
            <span>{{ convertDateFormatter(record.ngayBatDau) }}</span>
        </div>
        <div v-if="column.key === 'ngayKetThuc'" class="text-left">
            <span>{{ convertDateFormatter(record.ngayKetThuc) }}</span>
        </div>
        <div v-else-if="column.key === 'action'" class="flex items-center justify-center space-x-2">
          <!-- Fix: Add condition to disable the entire popconfirm component -->
          <a-popconfirm
            v-if="!isExpired(record.ngayKetThuc)"
            :title="record.trangThai =='ACTIVE' ? 'Vô hiệu hóa phiếu giảm giá này?': 'Kích hoạt phiếu giảm giá này'"
            ok-text="Có"
            cancel-text="Hủy"
            @confirm="handleChangeStatusVoucher(record.id, record.trangThai == 'INACTIVE' ? 'ACTIVE' : 'INACTIVE')"
          >
            <a-tooltip
              title="Đổi trạng thái phiếu giảm giá"
              trigger="hover"
            >
              <a-button
                class="bg-purple-100"
                size="middle"
                shape="round"
              >
                <v-icon name="fa-exchange-alt" />
              </a-button>
            </a-tooltip>
          </a-popconfirm>
          <!-- Disabled button when expired -->
          <a-tooltip
            v-else
            title="Không thể chuyển trạng thái khi đã kết thúc"
            trigger="hover"
          >
            <a-button
              class="bg-purple-100"
              size="middle"
              shape="round"
              disabled
            >
              <v-icon name="fa-exchange-alt" />
            </a-button>
          </a-tooltip>
          <a-tooltip
            title="Chi tiết phiếu giảm giá"
            trigger="hover">
            <a-button class="bg-blue-100" size="middle" shape="round"
              @click="handleRedirectVoucherDetail(record.id)">
              <v-icon name="fa-eye" />
            </a-button>
          </a-tooltip>
        </div>
      </template>
    </table-spotify>
  </div>
</template>

<script setup lang="ts">
import TableSpotify from "@/components/ui/Table.vue";
import { ColumnType } from "ant-design-vue/es/table";
import { toast } from "vue3-toastify";
import { defineEmits, watch } from "vue";
import { useChangeStatusVoucher } from "@/infrastructure/services/service/admin/voucher/voucher.action";
import { formatCurrencyVND, getDateFormat, convertDateFormat, convertDateFormatter } from "@/utils/common.helper";
import { useRouter } from "vue-router";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";

const emit = defineEmits([
  "update:paginationParams"
]);

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
  paginationParams: Object,
});

const { mutate: changeStatusVoucher } = useChangeStatusVoucher();

// Helper function to check if a voucher is expired
const isExpired = (endDate: string | number) => {
  if (!endDate) return false;
  
  // If endDate is a string, convert it to a Date object
  const endDateTime = typeof endDate === 'string' ? new Date(endDate).getTime() : endDate;
  return endDateTime <= Date.now();
};

const handleChangeStatusVoucher = async (id: string, trangThaiMoi: string) => {
  try {
    changeStatusVoucher(
      {voucherId: id, trangThai: trangThaiMoi},
      {
        onSuccess: (res: any) => {
          toast.success(res.data.message);
        },
        onError: (error: any) => {
          toast.error(
            error?.response?.data?.message
          )
        },
      }
    )
  } catch (error: any) {
    console.error("🚀 ~ handleChangeStatus ~ error:", error);
    toast.error(
      error?.response?.data?.message
    );
  }
}

const router = useRouter(); 

const handleRedirectVoucherAdd = () => {
  router.push(ROUTES_CONSTANTS.ADMIN.children.VOUCHER_ADD.path);
}

const handleRedirectVoucherDetail = (id: string) => {
  router.push({ name: 'admin-voucher-detail', params: { id: id } });
}

const columnsVoucher: ColumnType[] = [
  {
    title: "#",
    dataIndex: "catalog",
    key: "catalog",
    ellipsis: true,
    width: 30,
    align: "center"
  },
  {
    title: "Mã",
    dataIndex: "ma",
    key: "ma",
    ellipsis: true,
    width: 70,
    resizable: true,
  },
  {
    title: "Tên voucher",
    dataIndex: "ten",
    key: "ten",
    ellipsis: true,
    width: 100,
    resizable: true,
  },
  {
    title: "Số lượng",
    dataIndex: "soLuong",
    key: "soLuong",
    ellipsis: true,
    width: 70,
    resizable: true,
  },
  {
    title: "Loại giảm",
    dataIndex: "kieu",
    key: "kieu",
    ellipsis: true,
    width: 80,
    resizable: true,
  },
  {
    title: "Giá trị giảm",
    dataIndex: "giaTriGiam",
    key: "giaTriGiam",
    ellipsis: true,
    width: 100,
    resizable: true,
  },
  {
    title: "Đơn tối thiểu",
    dataIndex: "dieuKienGiam",
    key: "dieuKienGiam",
    ellipsis: true,
    width: 100,
    resizable: true,
  },
  {
    title: "Ngày bắt đầu",
    dataIndex: "ngayBatDau",
    key: "ngayBatDau",
    ellipsis: true,
    width: 100,
    resizable: true,
  },
  {
    title: "Ngày kết thúc",
    dataIndex: "ngayKetThuc",
    key: "ngayKetThuc",
    ellipsis: true,
    width: 100,
    resizable: true,
  },
  {
    title: "Trạng thái",
    dataIndex: "trangThai",
    key: "trangThai",
    ellipsis: true,
    width: 100,
    align: "center",
  },
  {
    title: "Hành động",
    key: "action",
    align: "center",
    width: 150,
    fixed: "right",
  },
];
</script>