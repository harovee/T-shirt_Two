<template>
    <!-- Bảng danh sách sản phẩm -->
    <table-example
       wrapperClassName="min-h-[35rem]"
      :columns="columns"
      :data-source="props.dataSource?.data"
      :loading="props.loading"
      :pagination-params="paginationParams || {}"
      :total-pages="props.dataSource?.totalPages || 1"
      :showSizeChanger="true"
      @update:pagination-params="$emit('update:paginationParams', $event)"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'select'">
          <a-checkbox @change="() => $emit('select-product', record)" />
        </template>

        <template v-else-if="column.key === 'anhSanPhamChiTiet'">
          <Image
            :src="record.anhSanPhamChiTiet"
            width="40"
            height="40"
            alt="img"
          />
        </template>

        <template v-else-if="column.key === 'gia'">
          <span class="text-red-500">
            {{ formatCurrencyVND(record.gia) }}
          </span>
        </template>
      </template>
    </table-example>
</template>

<script lang="ts" setup>
import TableExample from '@/components/ui/TableExample.vue';
import { formatCurrencyVND } from '@/utils/common.helper';
import { ColumnType } from 'ant-design-vue/es/table';
import { Image } from 'ant-design-vue';

const emit = defineEmits([
  "update:paginationParams",
  "select-product", // Emit sự kiện khi chọn sản phẩm
]);

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
  paginationParams: Object,
});

// Cấu hình cột cho bảng
const columns: ColumnType[] = [
  {
    key: "select",
    align: "center",
    fixed: "left",
    width: 60,
  },
  {
    title: "Mã SP",
    dataIndex: "maSanPhamChiTiet",
    key: "maSanPhamChiTiet",
    ellipsis: true,
    width: 130,
    align: "center",
  },
  {
    title: "Ảnh",
    dataIndex: "anhSanPhamChiTiet",
    key: "anhSanPhamChiTiet",
    ellipsis: true,
    width: 150,
    align: "center",
  },
  {
    title: "Tên",
    dataIndex: "sanPham",
    key: "sanPham",
    ellipsis: true,
    width: 150,
    align: "center",
  },
  {
    title: "Màu sắc",
    dataIndex: "mauSac",
    key: "mauSac",
    ellipsis: true,
    width: 100,
    align: "center",
  },
  {
    title: "Kích cỡ",
    dataIndex: "kichCo",
    key: "kichCo",
    ellipsis: true,
    width: 100,
    align: "center",
  },
  {
    title: "Số lượng",
    dataIndex: "soLuong",
    key: "soLuong",
    ellipsis: true,
    width: 80,
    align: "center",
  },
  {
    title: "Chất liệu",
    dataIndex: "chatLieu",
    key: "chatLieu",
    ellipsis: true,
    width: 100,
    align: "center",
  },
  {
    title: "Thương hiệu",
    dataIndex: "thuongHieu",
    key: "thuongHieu",
    ellipsis: true,
    width: 120,
    align: "center",
  },
  {
    title: "Cổ áo",
    dataIndex: "coAo",
    key: "coAo",
    ellipsis: true,
    width: 100,
    align: "center",
  },
  {
    title: "Tay áo",
    dataIndex: "tayAo",
    key: "tayAo",
    ellipsis: true,
    width: 100,
    align: "center",
  },
  {
    title: "Kiểu dáng",
    dataIndex: "kieuDang",
    key: "kieuDang",
    ellipsis: true,
    width: 150,
    align: "center",
  },
  {
    title: "Họa tiết",
    dataIndex: "hoaTiet",
    key: "hoaTiet",
    ellipsis: true,
    width: 100,
    align: "center",
  },
  {
    title: "Tính năng",
    dataIndex: "tinhNang",
    key: "tinhNang",
    ellipsis: true,
    width: 100,
    align: "center",
  },
  {
    title: "Giá",
    dataIndex: "gia",
    key: "gia",
    ellipsis: true,
    width: 130,
    align: "center",
    fixed: "right",
  },
];
</script>