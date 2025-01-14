<template>
  <div class="p-4 bg-white rounded-lg shadow">
    <h3 class="text-lg font-bold" v-if="dataSource">
      Thông tin đơn hàng - Đơn hàng {{ dataSource[0]?.loaiHoaDon }}
    </h3>
    <div class="border-t mt-2 mb-4"></div>
    <div class="grid grid-cols-2 gap-4 text-sm">
      <div class="flex items-center">
        <span class="font-medium">Mã:</span>
        <span class="ml-2">{{ dataSource[0]?.maHoaDon }}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Tên khách hàng:</span>
        <span class="ml-2">{{ dataSource[0]?.tenKhachHang }}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">SĐT người nhận:</span>
        <span class="ml-2">{{ dataSource[0]?.soDienThoai }}</span>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Trạng thái:</span>
        <a-tag class="ml-2" color="orange">{{ dataSource[0]?.trangThaiHD }}</a-tag>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Loại:</span>
        <a-tag class="ml-2" color="blue">{{ dataSource[0]?.loaiHoaDon }}</a-tag>
      </div>
      <div class="flex items-center">
        <span class="font-medium">Tên người nhận:</span>
        <span class="ml-2">{{ dataSource[0]?.tenNguoiNhan }}</span>
      </div>
    </div>
    <div class="flex justify-end mt-4">
      <a-button type="primary" class="bg-orange-500 hover:bg-orange-600 text-white">
        Cập nhật
      </a-button>
    </div>
  </div>
  <table-example v-if="props"
    :wrapperClassName="props.wrapperClassName"
    :columns="props.columns"
    :data-source="props.dataSource"
    :loading="props.loading"
    :pagination-params="props.paginationParams"
    :total-pages="props.totalPages"
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

      <div v-else-if="column.key === 'chiTietSanPham'">
        <p>{{ record.tenSanPhamChiTiet }}</p>
        <p>size: {{ record.tenKichCo }}</p>
        <p>{{ record.gia }} đ</p>
      </div>
    </template>
  </table-example>
</template>

<script lang="ts" setup>
import TableExample from "@/components/ui/TableExample.vue";
import { ColumnType } from "ant-design-vue/es/table";
import { watch } from "vue";

const props = defineProps({
  wrapperClassName: {
    type: String,
    required: false,
    default: "min-h-[410px]",
  },
  columns: {
    type: Array as () => ColumnType[],
    required: true,
  },
  dataSource: Array,
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
  () => props?.dataSource,
  (result) => {
    if (result) {
      console.log(result[0]);
    }
  }
);
</script>

<style scoped>

</style>
