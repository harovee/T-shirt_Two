<template>
  <h2 class="text-xl font-semibold">Giỏ hàng</h2>
  <div>
    <a-table
      :loading="isLoading"
      :columns="columns"
      :data-source="dataSource"
      :pagination="false"
      :scroll="{ x: 1000, y: 400 }"
      :locale="{ emptyText: 'Không có sản phẩm' }"
    >
      <template #bodyCell="{ record, column }">
        <a-image-preview-group>
          <div v-if="column.dataIndex === 'linkAnh'" class="text-center">
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
          <div v-if="column.dataIndex === 'thongTinChung'" class="text-left">
            <a-space direction="vertical">
              <a-space
                >{{ record.maSanPhamChiTiet }} - {{ record.ten }}</a-space
              >
              <a-space>{{ record.tenSanPham }}</a-space>
              <a-space
                >Giá gốc:
                <a-tag color="#108ee9">
                  {{ formatCurrency(record.gia, "VND", "vi-VN") }}</a-tag
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
          <div v-if="column.dataIndex === 'giaBanHienTai'" class="text-right">
            <a-typography-text
              type="danger"
              strong
              underline
              class="cursor-pointer"
            >
              {{
                formatCurrency(
                  record.giaHienTai ? record.giaHienTai : record.gia,
                  "VND",
                  "vi-VN"
                )
              }}
            </a-typography-text>
          </div>
          <div v-if="column.dataIndex === 'thanhTien'" class="text-right">
            <a-typography-text
              type="danger"
              strong
              underline
              class="cursor-pointer"
            >
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
                <a-button class="bg-purple-100" size="middle" shape="round" @click="handleDelete(record.key)">
                  <v-icon name="fa-trash-alt" />
                </a-button>
              </a-tooltip>
            <!-- <a-popconfirm
              :title="'Bạn chắc chắn muốn loại khỏi danh sách bán?'"
              ok-text="Có"
              cancel-text="Hủy"
              @confirm="handleDelete(record.key)"
            >
              
            </a-popconfirm> -->
          </div>
        </a-image-preview-group>
      </template>
    </a-table>
  </div>
</template>
  <script lang="ts" setup>
import type { TableProps, TableColumnType} from "ant-design-vue";
import { defineProps, computed, defineEmits, ref, watch, createVNode } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  defaultProductImageSaleUrl,
  formatCurrency,
} from "@/utils/common.helper";
import { Modal } from "ant-design-vue";
import {
  POSProductDetailResponse,
  POSUpdateCartRequest,
} from "@/infrastructure/services/api/admin/point-of-sale.api";
import {
  useGetOrderDetails,
  useUpdateQuantityOrderDetails,
  useDeleteCartById
} from "@/infrastructure/services/service/admin/point-of-sale";
import {
  warningNotiSort,
  successNotiSort,
  errorNotiSort,
  notificationType,
  openNotification,
} from "@/utils/notification.config";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";

const props = defineProps<{
  idOrder: string;
}>();

interface DataType extends POSProductDetailResponse {
  key: string;
}

const columns: TableColumnType<DataType>[] = [
  {
    title: "Ảnh",
    dataIndex: "linkAnh",
    fixed: true,
    width: 130,
    align: "center",
  },
  {
    title: "Thông tin chung",
    dataIndex: "thongTinChung",
    width: 230,
    align: "center",
  },
  {
    title: "Chi tiết",
    dataIndex: "chiTiet",
    width: 200,

    align: "center",
  },
  {
    title: "Số lượng bán",
    dataIndex: "soLuong",
    width: 100,
    align: "center",
  },
  {
    title: "Giá bán hiện tại",
    dataIndex: "giaBanHienTai",
    width: 150,
    align: "center",
  },
  {
    title: "Thành tiền",
    dataIndex: "thanhTien",
    width: 150,
    align: "center",
  },
  {
    title: "Hành động",
    dataIndex: "hanhDong",
    width: 100,
    align: "center",
  },
];

//

const { data, isLoading, refetch } = useGetOrderDetails(
  props.idOrder?.valueOf(),
  {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
  }
);

const dataSource: DataType[] | any = computed(() => {
  return (
    data?.value?.data?.map((e: any) => ({
      key: e.id || "",
      maSanPhamChiTiet: e.maSanPhamChiTiet || "",
      ten: e.ten || "",
      soLuong: e.soLuong || "",
      gia: e.gia || 0,
      giaHienTai: e.giaHienTai || 0,
      tenSanPham: e.tenSanPham || "",
      tenThuongHieu: e.tenThuongHieu || "",
      gioiTinh: e.gioiTinh
        ? "Nam"
        : e.gioiTinh == false
        ? "Nữ"
        : "Không xác định",
      kichCo: e.kichCo || "",
      phongCach: e.phongCach || "",
      maMauSac: e.maMauSac || "",
      tenMauSac: e.tenMauSac || "",
      linkAnh: e.linkAnh || "",
    })) || []
  );
});

const { mutate: updateQuantityOrderDetails } = useUpdateQuantityOrderDetails();
const { mutate: deleteOrderDetails } = useDeleteCartById();

const handleQuantityChange = (record: any) => {
  const payload = {
    idHoaDonChiTiet: record.key,
    soLuongBanSau: record.soLuong,
    soLuongBanTruoc: null,
  };
  try {
    updateQuantityOrderDetails(payload);
    // successNotiSort("Sửa thành công")
  } catch (error: any) {
    if (error?.response) {
      openNotification(
        notificationType.error,
        error?.response?.data?.message,
        ""
      );
    } else if (error?.errorFields) {
      openNotification(notificationType.warning, "", "");
    }
  }
};
const handleDelete = (idHdct: string) => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn xóa sản phẩm này ra khỏi giỏ?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await deleteOrderDetails(idHdct);
        successNotiSort("Xóa thành công");
      } catch (error) {
        errorNotiSort("Xóa thất bại");
      }
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
    },
  });
};
</script>
  