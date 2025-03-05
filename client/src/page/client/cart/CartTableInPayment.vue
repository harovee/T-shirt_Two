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
            <a-space
                >{{ record.soLuong }}</a-space>
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
        </a-image-preview-group>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import type { TableProps, TableColumnType} from "ant-design-vue";
import {
  POSProductDetailResponse,
  POSUpdateCartRequest,
} from "@/infrastructure/services/api/admin/point-of-sale.api";
import {
  defaultProductImageSaleUrl,
  formatCurrency,
} from "@/utils/common.helper";

const props = defineProps({
    dataSource: {
        type: Object
    }
})

interface DataType extends POSProductDetailResponse {
  key: string;
}

const columns: TableColumnType<DataType>[] = [
  {
    title: "",
    dataIndex: "anh",
    width: 70,
    align: "center",
  },
  {
    title: "Tên hàng",
    dataIndex: "tenHang",
    width: 200,
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
    width: 100,
    align: "center",
  },
];
</script>