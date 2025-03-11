<template>
  <div class="container mx-auto pt-10">
    <h1
      class="flex items-center justify-center text-center gap-4 text-4xl font-bold mb-10 text-red-700"
    >
      <v-icon name="md-shoppingbag-sharp" style="width: 35px; height: 35px" />
      GIỎ HÀNG
    </h1>
    <div class="m-5">
      <a-row :gutter="[8, 24]">
        <a-col :span="16">
          <div class="flex items-center">
            <h1 class="text-2xl font-bold m-5">GIỎ HÀNG</h1>
            <p class="mt-4 text-red-400">(1 sản phẩm)</p>
          </div>
          <div class="m-5">
            <a-table
              :loading="isLoading"
              :columns="columns"
              :data-source="dataSource"
              :pagination="false"
              :scroll="{ y: 400 }"
              :locale="{ emptyText: 'Không có sản phẩm' }"
            >
              <template #bodyCell="{ record, column }">
                <a-image-preview-group>
                  <div
                    v-if="column.dataIndex === 'linkAnh'"
                    class="text-center"
                  >
                    <a-image
                      :width="100"
                      :alt="record.linkAnh ? record.ten : 'K&Q T-Shirts'"
                      :src="
                        record.linkAnh != 'default-product-detail-image-url.jpg'
                          ? record.linkAnh
                          : defaultProductImageSaleUrl
                      "
                    />
                  </div>
                  <div
                    v-if="column.dataIndex === 'thongTinChung'"
                    class="text-left"
                  >
                    <a-space direction="vertical">
                      <a-space
                        >{{ record.maSanPhamChiTiet }} -
                        {{ record.ten }}</a-space
                      >
                      <a-space>{{ record.tenSanPham }}</a-space>
                      <a-space
                        >Giá gốc:
                        <a-tag color="#108ee9">
                          {{
                            formatCurrency(record.gia, "VND", "vi-VN")
                          }}</a-tag
                        ></a-space
                      >
                    </a-space>
                  </div>
                  <div v-if="column.dataIndex === 'chiTiet'" class="text-left">
                    <a-space direction="vertical">
                      <a-space>Giới tính: {{ record.gioiTinh }}</a-space>
                      <a-space>Kích cỡ: {{ record.kichCo }}</a-space>
                      <a-space>
                        Màu: {{ record.tenMauSac }}
                        <div
                          :style="{
                            width: '25px',
                            height: '25px',
                            background: record.maMauSac,
                            'border-radius': '5px',
                          }"
                          class=""
                        ></div>
                        {{ record.maMauSac }}
                      </a-space>
                    </a-space>
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
                  <div
                    v-if="column.dataIndex === 'giaBanHienTai'"
                    class="center"
                  >
                    <a-typography-text strong class="cursor-pointer">
                      {{
                        formatCurrency(
                          record.giaHienTai ? record.giaHienTai : record.gia,
                          "VND",
                          "vi-VN"
                        )
                      }}
                    </a-typography-text>
                  </div>
                  <div v-if="column.dataIndex === 'thanhTien'" class="center">
                    <a-typography-text strong class="cursor-pointer text-xl">
                      {{
                        formatCurrency(
                          record.giaHienTai
                            ? record.giaHienTai * record.soLuong
                            : record.gia * record.soLuong,
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
                        class="bg-purple-100"
                        size="middle"
                        shape="round"
                        @click="handleDelete(record.key)"
                      >
                        <v-icon name="fa-trash-alt" />
                      </a-button>
                    </a-tooltip>
                  </div>
                </a-image-preview-group>
              </template>
            </a-table>
          </div>
        </a-col>
        <a-col :span="8">
          <h1 class="text-2xl font-bold m-5">ĐƠN HÀNG</h1>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { TableProps, TableColumnType} from "ant-design-vue";
import {
  POSProductDetailResponse,
  POSUpdateCartRequest,
} from "@/infrastructure/services/api/admin/point-of-sale.api";

interface DataType extends POSProductDetailResponse {
  key: string;
}

const columns: TableColumnType<DataType>[] = [
  {
    title: "Tên hàng",
    dataIndex: "tenHang",
    width: 230,
    align: "center",
  },
  {
    title: "Giá",
    dataIndex: "chiTiet",
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
    dataIndex: "thanhTien",
    width: 100,
    align: "center",
  },
  {
    title: "Xóa",
    dataIndex: "hanhDong",
    width: 100,
    align: "center",
  },
];
</script>

<style scoped>
</style>
