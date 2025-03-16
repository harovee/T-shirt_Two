<template>
  <div class="flex items-center">
    <h1 class="text-2xl font-bold m-5">GIỎ HÀNG</h1>
    <p class="mt-4 text-red-700">({{dataSource.length}} sản phẩm)</p>
  </div>
  <div class="m-5">
    <a-table
      :columns="columns"
      :data-source="dataSource"
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
            <a-space
                >{{ record.tenHang }}</a-space>
          </div>
          <div v-if="column.dataIndex === 'gia'" class="text-center">
            <a-typography-text strong class="cursor-pointer">
              {{
                formatCurrency(
                  record.gia,
                  "VND",
                  "vi-VN"
                )
              }}
            </a-typography-text>
          </div>
          <div v-if="column.dataIndex === 'soLuong'" class="center">
            <!-- <a-space>{{ record.soLuong }}</a-space>
                 -->
            <a-input
              type="number"
              v-model:value="record.soLuong"
              @change="handleQuantityChange(record)"
              min="1"
            >
            </a-input>
          </div>
          <div v-if="column.dataIndex === 'tongTien'" class="center">
            <a-typography-text strong class="cursor-pointer text-xl">
              {{
                formatCurrency(
                  record.tongTien,
                  "VND",
                  "vi-VN"
                )
              }}
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
import type { TableColumnType} from "ant-design-vue";
import {
  POSProductDetailResponse,
  POSUpdateCartRequest,
} from "@/infrastructure/services/api/admin/point-of-sale.api";
import {
  defaultProductImageSaleUrl,
  formatCurrency,
} from "@/utils/common.helper";
import { useCartStorageBL } from '@/page/client/products/business.logic/useCartLocalStorageBL';

const { cart, totalAmount, addProduct, removeProduct, updateProductQuantity } = useCartStorageBL();

const props = defineProps({
    dataSource: {
        type: Object
    }
})

const emit = defineEmits(["updateCart"]);

interface DataType extends POSProductDetailResponse {
  key: string;
}

const handleQuantityChange = (record: any) => {
  updateProductQuantity (record.id, record.soLuong);
  emit("updateCart");
}

const handleDelete = (record: any) => {
  removeProduct(record.id);
  emit("updateCart");
}

const columns: TableColumnType<DataType>[] = [
  {
    title: "Ảnh",
    dataIndex: "anh",
    width:80,
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