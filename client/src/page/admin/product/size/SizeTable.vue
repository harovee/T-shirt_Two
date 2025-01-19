<template>
  <div class="p-4 rounded-xl">
    <div class="flex justify-between items-center">
      <div>
        <h3 class="text-xl font-semibold text-gray-800">Danh sách kích cỡ</h3>
      </div>
      <div class="p-2.5">
        <a-tooltip title="Thêm kích cỡ" trigger="hover">
          <a-button
            class="bg-purple-300 flex justify-between items-center gap-2"
            size="large"
            @click="$emit('handleOpenModalCreateSize')"
          >
            <v-icon name="md-addcircle" />
          </a-button>
        </a-tooltip>
      </div>
    </div>
    <table-spotify
      wrapperClassName="min-h-[410px]"
      :columns="columnsSize"
      :data-source="props.dataSource?.data"
      :loading="loading"
      :pagination-params="paginationParams || {}"
      :total-pages="props.dataSource?.totalPages || 1"
      @update:pagination-params="$emit('update:paginationParams', $event)"
    >
      <template #bodyCell="{ column, record }">
        <div
          v-if="column.key === 'ngayTao'"
          class="flex items-center justify-center space-x-2"
        >
        {{ convertDateFormat(record.ngayTao)}}
        </div>
        <div
          v-if="column.key === 'action'"
          class="flex items-center justify-center space-x-2"
        >
          <a-tooltip title="Sửa" trigger="hover">
            <a-button
              class="bg-purple-100"
              size="middle"
              shape="round"
              @click="$emit('handleOpenModalUpdateSize', record)"
            >
              <v-icon name="fa-pen"></v-icon>
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
import { defineEmits, computed, watch, ref } from "vue";
import { useRouter } from "vue-router";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import {convertDateFormat} from "@/utils/common.helper";

const emit = defineEmits([
  "update:paginationParams",
  "handleOpenModalCreateSize",
  "handleOpenModalUpdateSize"
]);

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
  paginationParams: Object,
});

const router = useRouter();

const redirectToCreateSize = () => {
  router.push(
    ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SIZE.path
  );
};

const handleRedirect = (id) => {
  router.push(
    `${ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.SIZE.path}/${id}`
  );
};

const columnsSize: ColumnType[] = [
  {
    title: "STT",
    dataIndex: "catalog",
    key: "catalog",
    ellipsis: true,
    width: 50,
    align: "center",
  },
  {
    title: "Mã kích cỡ",
    dataIndex: "maKichCo",
    key: "maKichCo",
    ellipsis: true,
    width: 100,
    resizable: true,
  },
  {
    title: "Tên kích cỡ",
    dataIndex: "ten",
    key: "ten",
    ellipsis: true,
    width: 200,
    resizable: true,
  },
  {
    title: "Chiều cao tối thiểu",
    dataIndex: "chieuCaoMin",
    key: "chieuCaoMin",
    ellipsis: true,
    resizable: true,
    width: 150,
  },
  {
    title: "Chiều cao tối đa",
    dataIndex: "chieuCaoMax",
    key: "chieuCaoMax",
    ellipsis: true,
    resizable: true,
    width: 150,
  },
  {
    title: "Cân nặng tối thiểu",
    dataIndex: "canNangMin",
    key: "canNangMin",
    ellipsis: true,
    resizable: true,
    width: 150,
  },
  {
    title: "Cân nặng tối đa",
    dataIndex: "canNangMax",
    key: "canNangMax",
    ellipsis: true,
    resizable: true,
    width: 150,
  },
  {
    title: "Ngày tạo",
    dataIndex: "ngayTao",
    key: "ngayTao",
    ellipsis: true,
    width: 150,
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