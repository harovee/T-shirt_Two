<template>
  <!-- Bảng danh sách sản phẩm -->
  <table-example
    wrapperClassName="min-h-[35rem]"
    :columns="columnsFilter"
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

      <template v-else-if="column.key === 'imgUrl'">
        <a-image
          :src="
            record.imgUrl != 'default-product-detail-image-url.jpg'
              ? record.imgUrl
              : defaultProductImageSaleUrl
          "
          width="40"
          height="40"
          alt="img"
        />
      </template>

      <template v-else-if="column.key === 'giaHienTai'">
        <span class="text-red-500">
          {{ formatCurrencyVND(record.giaHienTai) }}
        </span>
      </template>

      <template v-else-if="column.key === 'sanPham'">
        <span style="color: black">{{ record.sanPham }}</span
        ><br />

        <span>
          <template v-if="record.idSanPhamGiamGia">
            <del class="text-gray-500">{{ formatCurrencyVND(record.gia) }}</del> - 
            <span class="text-red-500" style="color: red">{{ formatCurrencyVND(record.giaSauGiam) }}</span>
          </template>
          <template v-else>
            {{ formatCurrencyVND(record.giaSauGiam) }}
          </template>
        </span>
      </template>
    </template>
  </table-example>
</template>

<script lang="ts" setup>
import TableExample from "@/components/ui/TableExample.vue";
import {
  formatCurrencyVND,
  defaultProductImageSaleUrl,
} from "@/utils/common.helper";
import { ColumnType } from "ant-design-vue/es/table";
import { Image } from "ant-design-vue";
import { useAuthStore } from "@/infrastructure/stores/auth";
import { computed } from "vue";

const authStore = useAuthStore();
const userRole = computed(() => authStore?.user?.roleName);
const isClient = computed(() => userRole.value === "CLIENT");

const emit = defineEmits([
  "update:paginationParams",
  "select-product", // Emit sự kiện khi chọn sản phẩm
]);

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
  paginationParams: Object,
});

// console.log(props.dataSource);

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
    dataIndex: "imgUrl",
    key: "imgUrl",
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
    title: "Giá bán",
    dataIndex: "giaHienTai",
    key: "giaHienTai",
    ellipsis: true,
    width: 130,
    align: "center",
    fixed: "right",
  },
];

const columnsFilter = computed(() =>
  columns.filter(col => !(isClient.value && col.key === "soLuong"))
)

</script>
