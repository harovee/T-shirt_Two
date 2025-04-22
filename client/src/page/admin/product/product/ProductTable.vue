<template>
  <div class="p-4 rounded-xl">
    <div class="flex justify-between items-center">
      <div>
        <h3 class="text-xl font-semibold text-gray-800">Danh sách sản phẩm</h3>
        <p class="text-sm text-gray-500">
          Hiển thị danh sách sản phẩm T-Shirts Two
        </p>
      </div>
      <div class="p-2.5">
        <a-tooltip title="Thêm sản phẩm" trigger="hover">
          <a-button
            class="bg-purple-300 flex justify-between items-center gap-2"
            size="large"
            @click="redirectToCreateProduct"
          >
            <v-icon name="md-addcircle" />
          </a-button>
        </a-tooltip>
      </div>
    </div>
    <table-spotify
      wrapperClassName="min-h-[410px]"
      :columns="columnsProduct"
      :data-source="props.dataSource?.data"
      :loading="loading"
      :pagination-params="paginationParams || {}"
      :total-pages="props.dataSource?.totalPages || 1"
      @update:pagination-params="$emit('update:paginationParams', $event)"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'trangThai'" class="text-center">
          <a-tag v-if="record.trangThai === 0" color="success"
            >Đang bán</a-tag
          >
          <a-tag v-else-if="record.trangThai === 1" color="warning"
            >Ngừng kinh doanh</a-tag
          >
          <a-tag v-else color="secondary">Không xác định</a-tag>
        </div>
        <div
          v-else-if="column.key === 'action'"
          class="flex items-center justify-center space-x-2"
        >
          <a-tooltip title="Xem chi tiết" trigger="hover">
            <a-button
              class="bg-purple-100"
              size="middle"
              shape="round"
              @click="handleRedirect(record.id)"
            >
              <v-icon name="fa-eye" />
            </a-button>
          </a-tooltip>
          <a-tooltip title="Sửa" trigger="hover">
            <a-button
              class="bg-purple-100"
              size="middle"
              shape="round"
              @click="handleOpenModalCreateProduct(record)"
            >
              <v-icon name="fa-pen"></v-icon>
            </a-button>
          </a-tooltip>
        </div>
      </template>
    </table-spotify>
  </div>
  <modal-update-product
    :open="isOpenModalCreateProduct"
    @handleClose="handleCloseModalCreateProduct"
    @onCancel="isOpenModalCreateProduct = false"
    :ProductDetail="productDetail || null"
    :is-loading-detail="isLoadingDetail || false"
    :all-product-data="props.dataSource?.data"
  />
</template>

<script setup lang="ts">
import TableSpotify from "@/components/ui/Table.vue";
import { ColumnType } from "ant-design-vue/es/table";
import { toast } from "vue3-toastify";
import { defineEmits, computed, watch, ref } from "vue";
import { useRouter } from "vue-router";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import ModalUpdateProduct from "@/page/admin/product/product/ModalUpdateProduct.vue";
import { useGetProductById } from "@/infrastructure/services/service/admin/product.action";

const emit = defineEmits([
  "update:paginationParams",
  "handleOpenModalCreate",
  "handleCloseModalCreate",
]);

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
  paginationParams: Object,
});

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetProductById(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value,
});

// Đóng mở modal
const isOpenModalCreateProduct = ref(false);

const handleOpenModalCreateProduct = (product) => {
  id.value = product.id;
  isOpenModalCreateProduct.value = true;
};

const handleCloseModalCreateProduct = () => {
  isOpenModalCreateProduct.value = false;
};

const router = useRouter(); // Lấy instance router

const redirectToCreateProduct = () => {
  router.push(
    ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.CREATE_PRODUCT.path
  );
};

const handleRedirect = (id) => {
  router.push(
    `${ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.path}/${id}`
  );
};

const productDetail = computed(() =>
  id.value
    ? {
        ...dataDetail.value?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalCreateProduct, (newVal) => {
  if (newVal && id && id !== null) {
    refetch();
  }
});

const columnsProduct: ColumnType[] = [
  {
    title: "#",
    dataIndex: "catalog",
    key: "catalog",
    ellipsis: true,
    width: 50,
    align: "center",
  },
  {
    title: "Mã sản phẩm",
    dataIndex: "maSanPham",
    key: "maSanPham",
    ellipsis: true,
    width: 200,
    resizable: true,
  },
  {
    title: "Tên sản phẩm",
    dataIndex: "ten",
    key: "ten",
    ellipsis: true,
    width: 200,
    resizable: true,
  },
  {
    title: "Danh mục",
    dataIndex: "tenDanhMuc",
    key: "tenDanhMuc",
    ellipsis: true,
    width: 200,
    resizable: true,
  },
  {
    title: "Số lượng",
    dataIndex: "soLuong",
    key: "soLuong",
    ellipsis: true,
    width: 200,
    resizable: true,
  },
  {
    title: "Trạng thái",
    dataIndex: "trangThai",
    key: "trangThai",
    ellipsis: true,
    width: 150,
    align: "center",
  },
  {
    title: "Hành động",
    key: "action",
    align: "center",
    width: 300,
    fixed: "right",
  },
];
</script>