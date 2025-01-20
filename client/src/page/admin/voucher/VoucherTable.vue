<template>
  <div class="p-4 rounded-xl border-2 shadow-purple-950 shadow-xl">
    <div class="flex justify-between items-center">
      <div>
        <h3 class="text-xl font-semibold text-gray-800">Danh sách phiếu giảm giá</h3>
        <p class="text-sm text-gray-500">
          Hiển thị danh sách phiếu giảm giá
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
      {{ record.giaTriGiam }} VNĐ
    </span>
    <span v-else-if="record.loaiGiam === false || record.loaiGiam === 'false'">
      {{ record.giaTriGiam }} %
    </span>
    <span v-else>
      Không xác định
    </span>
  </div>
        <div v-else-if="column.key === 'trangThai'" class="text-center">
            <a-tag v-if="record.trangThai === 'ACTIVE' || record.trangThai === 'ACTIVE'" color="success">Đang áp dụng</a-tag>
            <a-tag v-else-if="record.trangThai === 'NOT_STARTED' || record.trangThai === 'NOT_STARTED'" color="warning">Sắp diễn ra</a-tag>
            <a-tag v-else-if="record.trangThai === 'EXPIRED' || record.trangThai === 'EXPIRED'" color="rgb(231, 147, 164)">Hết hạn</a-tag>
            <a-tag v-else color="secondary">Không xác định</a-tag>
        </div>
        <div v-else-if="column.key === 'action'" class="flex items-center justify-center space-x-2">
          <a-tooltip
            :title="record.trangThai === 'ACTIVE' || record.trangThai === 'EXPIRED' ? 'Không thể cập nhật voucher này' : 'Cập nhật'"
            trigger="hover"
             
          >
            <a-button  class="bg-blue-100"  size="middle" shape="round"
            :disabled="record.trangThai === 'ACTIVE' || record.trangThai === 'EXPIRED'"
              @click="handleRedirectVoucherDetail(record.id)"
            >
              <v-icon name="fa-edit" />
            </a-button>
          </a-tooltip>
          <a-popconfirm
            title="Bạn có chắc chắn muốn xóa phiếu giảm giá này không?"
            ok-text="Có"
            cancel-text="Hủy"
            @confirm="handleDeleteVoucher(record.id)"
          >
            <a-tooltip
              title="Xóa"
              trigger="hover"
            >
              <a-button
                class="bg-purple-100"
                size="middle"
                shape="round"
              >
                <v-icon name="fa-trash" />
              </a-button>
            </a-tooltip>
          </a-popconfirm>
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
import { useDeleteVoucher } from "@/infrastructure/services/service/admin/voucher/voucher.action";
import router from "@/infrastructure/routes/router.ts";

const emit = defineEmits([
  "update:paginationParams"
]);

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
  paginationParams: Object,
});

// watch(
//   () => props.dataSource,
//   (newData) => {
//     console.log(newData);
//   }
// );
 const { mutate : deleteVoucher} = useDeleteVoucher();

const handleDeleteVoucher = async (id: string) => {
  try {
    await deleteVoucher(id);
    console.log("Deleting voucher with ID:", id);
    toast.success("Voucher deleted successfully!");
  } catch (error: any) {
    console.error("🚀 ~ DeleteVoucher ~ error:", error);
    toast.error("Failed to delete voucher");
  }
};

const handleRedirectVoucherAdd = () => {
    router.push({ name: 'admin-voucher-add' });
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
    width: 50,
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
    width: 55,
    resizable: true,
  },
  {
    title: "Điều kiện giảm",
    dataIndex: "dieuKienGiam",
    key: "dieuKienGiam",
    ellipsis: true,
    width: 100,
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
    title: "Giảm tối đa",
    dataIndex: "giamToiDa",
    key: "giamToiDa",
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
