<template>
  <div class="p-4 rounded-xl border-2 shadow-purple-500 shadow-xl">
    <div class="flex justify-between items-center">
      <div>
        <h3 class="text-xl font-semibold text-gray-800">Danh sách đợt giảm giá</h3>
        <p class="text-sm text-gray-500">
          Hiển thị danh sách đợt giảm giá T-Shirts Two
        </p>
      </div>
      <div class="p-2.5">
        <a-tooltip
            title="Thêm đợt giảm giá"
            trigger="hover"
        >
          <a-button
              class="bg-purple-300 flex justify-between items-center gap-2"
              size="large"
              @click="handleRedirectSaleAdd"
          >
            <v-icon name="md-addcircle"/>
          </a-button>
        </a-tooltip>
      </div>
    </div>
    <table-spotify
        wrapperClassName="min-h-[410px]"
        :columns="columnsSale"
        :data-source="props.dataSource?.data"
        :loading="loading"
        :pagination-params="paginationParams || {}"
        :total-pages="props.dataSource?.totalPages || 1"
        @update:pagination-params="$emit('update:paginationParams', $event)"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'another'" class="text-center">
        </div>
        <div v-if="column.key === 'giaTri'">
          <span v-if="record.loai === 'VND'">{{ formatCurrency(record.giaTri, "VND", "vi-VN") }}</span>
          <span v-else="record.loai === 'PHAN_TRAM'">{{ record.giaTri + ' %' }}</span>
        </div>
        <div v-if="column.key === 'ngayBatDau'" class="text-center">
          {{ getDateFormat(record.ngayBatDau, true) }}
        </div>
        <div v-if="column.key === 'ngayKetThuc'" class="text-center">
          {{ getDateFormat(record.ngayKetThuc, true) }}
        </div>
        <div v-else-if="column.key === 'trangThai'" class="text-center">
          <a-tag v-if="record.trangThai === 'IN_PROGRESS'" color="success">Đang diễn ra</a-tag>
          <a-tag v-else-if="record.trangThai === 'INACTIVE'" color="error">Vô hiệu hóa</a-tag>
          <a-tag v-else-if="record.trangThai === 'PENDING'" color="warning">Chưa diễn ra</a-tag>
          <a-tag v-else-if="record.trangThai === 'FINISHED'" color="default">Đã kết thúc</a-tag>
          <a-tag v-else color="secondary">Không xác định</a-tag>
        </div>
        <div v-else-if="column.key === 'action'" class="flex items-center justify-center space-x-2">
          <a-popconfirm
              :title="record.trangThai == 'INACTIVE' ? 'Kích hoạt đợt giảm giá này?' : 'Vô hiệu hóa đợt giảm giá này'"
              ok-text="Có"
              cancel-text="Hủy"
              @confirm="handleChangeStatusSale(record.id, record.trangThai == 'INACTIVE' ? 'ACTIVE': 'INACTIVE')"
              :disabled="record.ngayKetThuc <= Date.now()"
          >
            <a-tooltip
                placement="left"
                :title="record.ngayKetThuc <= Date.now() ? 'Không đổi trạng thái khi đợt giảm giá đã kết thúc' : 'Đổi trạng thái hoạt động'"
                trigger="hover"
            >
              <a-button

                  class="bg-purple-100"
                  size="middle"
                  shape="round"
                  :disabled="record.ngayKetThuc <= Date.now()"
              >
                <v-icon name="fa-exchange-alt"/>
              </a-button>
            </a-tooltip>
          </a-popconfirm>

          <a-tooltip
              title="Chi tiết đợt giảm giá"
              trigger="hover"
          >
            <a-button
                class="bg-purple-100"
                size="middle"
                shape="round"
                @Click="handleRedirectSaleDetail(record.id)"
            >
              <v-icon name="fa-eye"/>
            </a-button>
          </a-tooltip>
        </div>
      </template>
    </table-spotify>
  </div>
</template>

<script setup lang="ts">
import TableSpotify from "@/components/ui/Table.vue";
import {ColumnType} from "ant-design-vue/es/table";
import {toast} from "vue3-toastify";
import {defineEmits} from "vue";
import {useChangeStatusSale} from "@/infrastructure/services/service/admin/sale.action.ts";
import router from "@/infrastructure/routes/router.ts";
import { formatCurrency, getDateFormat} from "@/utils/common.helper.ts";

const emit = defineEmits([
  "update:paginationParams"
]);

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
  paginationParams: Object,
});

const {mutate: changeStatusSale} = useChangeStatusSale();

const handleChangeStatusSale = (id: string, trangThaiMoi: string) => {
  try {
    changeStatusSale(
      {saleId: id, trangThai: trangThaiMoi},
      {onSuccess: (res: any) => {
        toast.success(res.data.message);
      },
      onError: (error: any) => {
        toast.error(
            error?.response?.data?.message
        )
      },
    })
  } catch (error: any) {
    console.error("🚀 ~ handleChangeStatus ~ error:", error);
    toast.error(
        error?.response?.data?.message
    );
  }
}

const handleRedirectSaleDetail = (id: string) => {
    router.push({ name: 'admin-sale-detail', params: { id: id } });
}
const handleRedirectSaleAdd = () => {
    router.push({ name: 'admin-sale-add' });
}

const columnsSale: ColumnType[] = [
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
    dataIndex: "maDotGiamGia",
    key: "maDotGiamGia",
    ellipsis: true,
    width: 60,
    resizable: true
  },
  {
    title: "Tên",
    dataIndex: "ten",
    key: "ten",
    ellipsis: true,
    width: 150,
    resizable: true
  },
  {
    title: "Giá trị giảm",
    dataIndex: "giaTri",
    key: "giaTri",
    ellipsis: true,
    width: 100,
    resizable: true
  },
  {
    title: "Bắt đầu",
    dataIndex: "ngayBatDau",
    key: "ngayBatDau",
    ellipsis: true,
    width: 100,
    resizable: true,
    align: "center"
  },
  {
    title: "Kết thúc",
    dataIndex: "ngayKetThuc",
    key: "ngayKetThuc",
    ellipsis: true,
    width: 100,
    align: "center"
  },
  {
    title: "Trạng thái hiện tại",
    dataIndex: "trangThai",
    key: "trangThai",
    ellipsis: true,
    width: 100,
    align: "center"
  },
  {
    title: "Hành động",
    key: "action",
    align: "center",
    width: 200,
    fixed: "right"
  },
];
</script>