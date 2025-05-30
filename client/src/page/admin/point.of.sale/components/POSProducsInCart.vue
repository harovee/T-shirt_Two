<template>
  <h2 class="text-xl font-semibold inline-flex">
    Giỏ hàng
    <!-- <a-tooltip v-if="isMBConnected" title="Ngắt kết nối" trigger="hover">
            <a-button
              class="bg-purple-300 flex justify-between items-center gap-2  ms-4"
              size="large"
              @click="openDisConnectOrderModal(maHoaDon)"
            >
              <v-icon name="bi-link" />
            </a-button>
   </a-tooltip> -->
  </h2>

  <div>
    <a-table
      :loading="isLoading"
      :columns="columns"
      :key="tableKey"
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
              :alt="record.linkAnh ? record.ten : 'T-shirt Two'"
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
              <a-space class="text-red-500" v-if="record.gia !== record.giaGoc"
                >Đã có sự thay đổi giá từ
                {{ formatCurrency(record.giaGoc, "VND", "vi-VN") }} ->
                {{ formatCurrency(record.gia, "VND", "vi-VN") }}</a-space
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
                <!-- {{ record.maMauSac }} -->
              </a-space>
            </a-space>
          </div>
          <div v-if="column.dataIndex === 'soLuong'" class="center">
            <!-- <a-space>{{ record.soLuong }}</a-space>
                 -->
            <a-input-number
              v-model:value="record.soLuong"
              :min="1"
              :step="1"
              @focus="focusQuantity(record.soLuong)"
              @change="handleQuantityChange(record)"
            />
          </div>
          <div v-if="column.dataIndex === 'giaBanHienTai'" class="center">
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
</template>
  <script lang="ts" setup>
import type { TableProps, TableColumnType } from "ant-design-vue";
import {
  defineProps,
  computed,
  defineEmits,
  ref,
  watch,
  createVNode,
} from "vue";
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
  useDeleteCartById,
} from "@/infrastructure/services/service/admin/point-of-sale";
import {
  warningNotiSort,
  successNotiSort,
  errorNotiSort,
  notificationType,
  openNotification,
} from "@/utils/notification.config";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import {
  currentInvoice,
  InvoiceData,
  invoices,
  Item,
  sendCartInfo,
} from "@/infrastructure/mobile.connect/InvoiceConnect2";
import { useCheckQuantityInStock } from "@/infrastructure/services/service/admin/productdetail.action";
import { checkQuantityRequest } from "@/infrastructure/services/api/admin/product_detail.api";
import { number } from "echarts/core";

const props = defineProps<{
  idOrder: string;
  maHoaDon: string;
}>();

const emit = defineEmits(["refetchLength"]);

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

// const tableKey = ref(0); // Define tableKey as a ref

const {
  data,
  isLoading,
  refetch: refetchCart,
} = useGetOrderDetails(props.idOrder?.valueOf(), {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataSource: DataType[] | any = computed(() => {
  return (
    data?.value?.data?.map((e: any) => ({
      key: e.id || "",
      maSanPhamChiTiet: e.maSanPhamChiTiet || "",
      ten: e.ten || "",
      soLuong: e.soLuong || "",
      gia: e.gia || 0,
      giaGoc: e.giaGoc || 0,
      giaHienTai: e.giaHienTai || 0,
      tenSanPham: e.tenSanPham || "",
      tenThuongHieu: e.tenThuongHieu || "",
      gioiTinh: e.gioiTinh,
      kichCo: e.kichCo || "",
      phongCach: e.phongCach || "",
      maMauSac: e.maMauSac || "",
      tenMauSac: e.tenMauSac || "",
      linkAnh: e.linkAnh || "",
    })) || []
  );
});

watch(
  () => dataSource.value,
  (newData) => {
    if (newData) {
      console.log(newData);
    }
  }
);

const { mutate: updateQuantityOrderDetails } = useUpdateQuantityOrderDetails();
const { mutate: deleteOrderDetails } = useDeleteCartById();

const preQuantity = ref(null);
const tableKey = ref(0);

const focusQuantity = (quantity: number) => {
  preQuantity.value = quantity;
};

const getTotalQuantity = () => {
  return dataSource.value.reduce((total, item) => {
    return total + (Number(item.soLuong) || 0);
  }, 0);
};

const getTotalPrice = () => {
  return dataSource.value.reduce((total, item) => {
    return total + (item.giaHienTai * item.soLuong || 0);
  }, 0);
};

const params = ref<checkQuantityRequest>({
  id: null,
  quantity: null,
});

const { data: checkQuantityData, refetch: checkQuantityRefetch } =
  useCheckQuantityInStock(params, {
    refetchOnWindowFocus: false,
    keepPreviousData: false,
    enabled: false,
  });

const handleQuantityChange = async (record: any) => {
  params.value.id = record.key;
  params.value.quantity = Math.floor(record.soLuong);

  if (record.soLuong > preQuantity.value && record.giaGoc !== record.gia) {
    warningNotiSort("Sản phẩm này đã thay đổi giá, không thể thêm số lượng!");
    reloadData();
    return;
  }

  if (getTotalQuantity() > 1000) {
    warningNotiSort("Số lượng sản phẩm trong giỏ không được lớn hơn 1000");
    reloadData();
    return;
  }

  if (getTotalPrice() > 100000000) {
    warningNotiSort("Giá trị đơn hàng không được lớn hơn 100.000.000đ");
    reloadData();
    return;
  }

  const payload = {
    idHoaDonChiTiet: record.key,
    soLuongBanSau: params.value.quantity,
    soLuongBanTruoc: null,
  };

  try {
    // Chờ check số lượng xong trước khi tiếp tục
    await checkQuantityRefetch();
    const checkValue = checkQuantityData?.value?.data;

    if (!checkValue) {
      warningNotiSort("Số lượng trong kho không đủ!");
      reloadData();
    } else {
      if (payload.soLuongBanSau <= 0) {
        warningNotiSort("Số lượng không hợp lệ!");
        reloadData();
        return;
      }
      await updateQuantityOrderDetails(payload);
    }
  } catch (error: any) {
    console.log("⛔ Vào catch...");
    console.error("🔥 Lỗi từ backend:", error.response?.data || error);

    if (error.response?.status === 400) {
      warningNotiSort(error.response.data.message);
    } else {
      openNotification(notificationType.error, "Lỗi không xác định!", "");
    }
  }
};

const reloadData = async () => {
  await refetchCart();
  tableKey.value++;
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
        emit("refetchLength");
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

watch(
  () => data.value,
  (newData) => {
    console.log("Data changed:", newData?.data);
    if (props.maHoaDon === currentInvoice.value.id) {
      const products: Item[] =
        newData?.data?.map((e: any) => ({
          id: e.maSanPhamChiTiet,
          name: e.ten,
          quantity: e.soLuong,
          price: e.giaHienTai ? e.giaHienTai : e.gia,
          image: e.linkAnh,
          color: e.tenMauSac,
          size: e.kichCo,
          sku: e.id,
          total: e.giaHienTai ? e.giaHienTai * e.soLuong : e.gia * e.soLuong,
        })) || [];

      invoices.value.forEach((item) => {
        if (item.id === props.maHoaDon) {
          item.items = products;
          item.subtotal = item.items.reduce(
            (total: number, item: any) =>
              total +
              (item.giaHienTai ? item.giaHienTai : item.gia) * item.soLuong,
            0
          );
          currentInvoice.value.items = products;
          currentInvoice.value.subtotal = item.subtotal;
          sendCartInfo(currentInvoice.value);
        }
      });
    }
  },
  { deep: true }
);
</script>
  