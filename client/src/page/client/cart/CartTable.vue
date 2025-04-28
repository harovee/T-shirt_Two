<!-- CartTable.vue -->
<template>
  <div class="flex items-center">
    <h1 class="text-2xl font-bold m-5">GIỎ HÀNG</h1>
    <p class="mt-4 text-red-700">({{ dataSource.length }} sản phẩm)</p>
  </div>
  <div class="m-5">
    <a-table
      :columns="columns"
      :data-source="localData"
      :pagination="false"
      :scroll="{ y: 400 }"
      :locale="{ emptyText: 'Không có sản phẩm' }"
    >
      <template #bodyCell="{ record, column }">
        <a-image-preview-group>
          <div v-if="column.dataIndex === 'anh'" class="text-center">
            <a-image
              :width="50"
              :src="
                record.anh != 'default-product-detail-image-url.jpg'
                  ? record.anh
                  : defaultProductImageSaleUrl
              "
            />
          </div>
          <div v-if="column.dataIndex === 'tenHang'" class="text-center">
            <a-space>{{ record.tenHang }}</a-space>
          </div>
          <div v-if="column.dataIndex === 'gia'" class="text-center">
            <a-typography-text strong class="cursor-pointer">
              {{ formatCurrency(record.gia, "VND", "vi-VN") }}
            </a-typography-text>
          </div>
          <div v-if="column.dataIndex === 'soLuong'" class="center">
            <a-input
              type="number"
              v-model:value="record.soLuong"
              @focus="getPreQuantity(record.soLuong)"
              @blur="handleQuantityChange(record)"
              min="1"
            >
            </a-input>
          </div>
          <div v-if="column.dataIndex === 'tongTien'" class="center">
            <a-typography-text strong class="cursor-pointer text-xl">
              {{ formatCurrency(record.tongTien, "VND", "vi-VN") }}
            </a-typography-text>
          </div>
          <div
            v-else-if="column.dataIndex === 'hanhDong'"
            class="flex items-center justify-center space-x-2"
          >
            <a-tooltip
              placement="left"
              :title="'Xóa sản phẩm khỏi giỏ hàng này?'"
              trigger="hover"
            >
              <a-button
                class="!bg-transparent !border-none !shadow-none !text-gray-500 hover:!text-red-700"
                size="middle"
                shape="round"
                @click="handleDelete(record)"
              >
                <v-icon name="fa-trash-alt" />
              </a-button>
            </a-tooltip>
          </div>
        </a-image-preview-group>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import type { TableColumnType } from "ant-design-vue";
import {
  POSProductDetailResponse,
  POSUpdateCartRequest,
} from "@/infrastructure/services/api/admin/point-of-sale.api";
import {
  defaultProductImageSaleUrl,
  formatCurrency,
} from "@/utils/common.helper";
import { useCheckQuantityInStockByProductDetail } from "@/infrastructure/services/service/admin/productdetail.action";
import { checkQuantityRequest } from "@/infrastructure/services/api/admin/product_detail.api";
import { useCartStorageBL } from "@/page/client/products/business.logic/useCartLocalStorageBL";
import { warningNotiSort } from "@/utils/notification.config";
import {
  defineProps,
  computed,
  defineEmits,
  ref,
  watch,
  inject
} from "vue";

const { cart, totalAmount, addProduct, removeProduct, updateProductQuantity } =
  useCartStorageBL();
const emitCartUpdate = inject('emitCartUpdate', () => {});

const props = defineProps({
  dataSource: {
    type: Object,
    default: () => ({}),
  },
});

const localData = ref(JSON.parse(JSON.stringify(props.dataSource)));
const prevQuantity = ref(0);

watch(
  () => props.dataSource,
  (newValue) => {
    if (newValue) {
      localData.value = JSON.parse(JSON.stringify(newValue));
    }
  }
);

const emit = defineEmits(["updateCart"]);

interface DataType extends POSProductDetailResponse {
  key: string;
}

const params = ref<checkQuantityRequest>({
  id: null,
  quantity: null,
});

const { data: checkQuantityData, refetch: checkQuantityRefetch } =
  useCheckQuantityInStockByProductDetail(params, {
    refetchOnWindowFocus: false,
    keepPreviousData: false,
    enabled: false,
  });

const getPreQuantity = (quantity) => {
  prevQuantity.value = quantity;
};

const handleQuantityChange = async (record: any) => {
  params.value.id = record.id;
  params.value.quantity = record.soLuong;

  // Check số lương trong kho theo id spct
  await checkQuantityRefetch();
  const checkValue = checkQuantityData?.value?.data;
  if (!checkValue) {
    warningNotiSort("Số lượng trong kho không đủ!");
    record.soLuong = prevQuantity.value; // Restore previous quantity
    emit("updateCart");
    return;
  }
  if (record.soLuong >= 1) {
    updateProductQuantity(record.id, record.soLuong);
    emit("updateCart");
    // Trigger cart update event to update badge
    emitCartUpdate();
  } else {
    record.soLuong = prevQuantity.value; // Restore previous quantity
    emit("updateCart");
    warningNotiSort("Số lượng phải lớn hơn 0!");
  }
};

const handleDelete = (record: any) => {
  removeProduct(record.id);
  emit("updateCart");
  // Trigger cart update event to update badge
  emitCartUpdate();
};

const columns: TableColumnType<DataType>[] = [
  {
    title: "Ảnh",
    dataIndex: "anh",
    width: 80,
    align: "center",
  },
  {
    title: "Tên hàng",
    dataIndex: "tenHang",
    width: 230,
    align: "center",
  },

  {
    title: "Giá",
    dataIndex: "gia",
    width: 100,
    align: "center",
  },
  {
    title: "Số lượng",
    dataIndex: "soLuong",
    width: 100,
    align: "center",
  },
  {
    title: "Tổng tiền",
    dataIndex: "tongTien",
    width: 150,
    align: "center",
  },
  {
    title: "Xóa",
    dataIndex: "hanhDong",
    width: 50,
    align: "center",
  },
];
</script>