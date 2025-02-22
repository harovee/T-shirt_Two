<template>
  <div>
    <div class="flex justify-between items-center mb-5">
      <div>
        <h3 class="text-xl font-semibold text-gray-800">
          Danh sách sản phẩm chi tiết {{ changeFill ? "" : " - " + findTenSanPham(productId) }}
        </h3>
      </div>
      <div v-if="selectedRowKeyNews.length > 0">
        <a-button type="primary" class="ml-auto" @click="handleEdit">
          Sửa nhiều
        </a-button>
      </div>
    </div>
    <table-spotify
      wrapperClassName="min-h-[410px]"
      :columns="columnsProduct"
      :data-source="dataSourceProduct"
      :loading="loading"
      :pagination-params="paginationParams || {}"
      :total-pages="props.dataSource?.totalPages || 1"
      @update:pagination-params="$emit('update:paginationParams', $event)"
      :row-selection="rowSelection"
      :row-key="(record) => record.id"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'soLuong'" class="text-center">
          <a-input type="number" v-model:value="record.soLuong" min="0">
          </a-input>
        </div>
        <div v-if="column.key === 'gia'" class="text-center">
          <a-input-number
            v-model:value="record.gia"
            min="0"
            :formatter="formatter"
          />
        </div>

        <div v-if="column.key === 'trangThai'" class="text-center">
          <a-tag v-if="record.trangThai === 0" color="success"
            >Đang bán</a-tag
          >
          <a-tag v-else-if="record.trangThai === 1" color="warning"
            >Ngừng kinh doanh</a-tag
          >
          <a-tag v-else color="error">Không xác định</a-tag>
        </div>
        <div
          v-else-if="column.key === 'action'"
          class="flex items-center justify-center space-x-2"
        >
          <a-tooltip title="Sửa" trigger="hover">
            <a-button
              class="bg-purple-100"
              size="middle"
              shape="round"
              @click="handleOpenModalUpdateProductDetail(record)"
            >
              <v-icon name="fa-pen"></v-icon>
            </a-button>
          </a-tooltip>
        </div>
      </template>
    </table-spotify>
  </div>
  <modal-update-product-detail
    :open="isOpenModalUpdateProductDetail"
    @handleClose="handleCloseModalUpdateProductDetail"
    @onCancel="isOpenModalUpdateProductDetail = false"
    :ProductDetail="productDetail || null"
    :is-loading-detail="isLoadingDetail || false"
  />
</template>

<script setup lang="ts">
import type { TableProps } from "ant-design-vue";
import TableSpotify from "@/components/ui/Table.vue";
import { ColumnType } from "ant-design-vue/es/table";
import { toast } from "vue3-toastify";
import {
  defineEmits,
  computed,
  watch,
  ref,
  reactive,
  onMounted,
  createVNode,
  inject,
} from "vue";
import { useRouter } from "vue-router";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import { formatCurrency } from "@/utils/common.helper";
import ModalUpdateProductDetail from "@/page/admin/product/product-detail/ModalUpdateProductDetail.vue";
import {
  useGetProductDetailById,
  useUpdateProductDetail,
} from "@/infrastructure/services/service/admin/productdetail.action";
import { Form, message, Modal, Upload } from "ant-design-vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { successNotiSort, warningNotiSort } from "@/utils/notification.config";

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
  paginationParams: Object,
  productId: String,
  changeFill: Boolean,
});

const emit = defineEmits([
  "update:paginationParams",
  "handleOpenModalCreate",
  "handleCloseModalCreate",
]);

// Interface dùng để inject các thuộc tính
interface Trademark {
  value: string;
  label: string;
}
interface Material {
  value: string;
  label: string;
}
interface Collar {
  value: string;
  label: string;
}
interface Sleeve {
  value: string;
  label: string;
}
interface Pattern {
  value: string;
  label: string;
}
interface Style {
  value: string;
  label: string;
}
interface Color {
  value: string;
  label: string;
}
interface Size {
  value: string;
  label: string;
}
interface Feature {
  value: string;
  label: string;
}
interface Product {
  maSanPham: string;
  ten: string;
}

// Inject thuộc tính
const listMaterial = inject<Material[]>("listMaterial");
const listCollar = inject<Collar[]>("listCollar");
const listSleeve = inject<Sleeve[]>("listSleeve");
const listTrademark = inject<Trademark[]>("listTrademark");
const listColor = inject<Color[]>("listColor");
const listFeature = inject<Feature[]>("listFeature");
const listPattern = inject<Pattern[]>("listPattern");
const listSize = inject<Size[]>("listSize");
const listStyle = inject<Style[]>("listStyle");
const listProduct = inject<Product[]>("listProduct");

// Tạo 1 dataSource khác để có thể sửa số lượng và giá vì k thể sửa trực tiếp của dataSource truyền từ component cha
const dataSourceProduct = reactive([]);

// Theo dõi khi props.dataSource?.data thay đổi, đợi cho đến khi có dữ liệu hiển thị được lên bảng ...
watch(
  () => props.dataSource?.data,
  (newData) => {
    if (newData) {
      // Dùng JSON.parse và JSON.stringify để sao chép sâu
      dataSourceProduct.length = 0; // Xóa dữ liệu cũ
      dataSourceProduct.push(...JSON.parse(JSON.stringify(newData))); // Thêm dữ liệu mới (sao chép sâu)
    }
  },
  { immediate: true }
);
// -----------------------------------------

const id = ref<string | null>(null);

const {
  data: dataDetail,
  isLoading: isLoadingDetail,
  refetch,
} = useGetProductDetailById(id, {
  refetchOnWindowFocus: false,
  enabled: () => !!id.value,
});

// Đóng mở modal -----------------------
const isOpenModalUpdateProductDetail = ref(false);

const handleOpenModalUpdateProductDetail = (productDetail) => {
  id.value = productDetail.id;
  isOpenModalUpdateProductDetail.value = true;
};

const handleCloseModalUpdateProductDetail = () => {
  isOpenModalUpdateProductDetail.value = false;
};
// ---------------------------------------

// Theo dõi để load lại modal khi id không thay đổi vẫn fill dữ liệu lên modal
const productDetail = computed(() =>
  id.value
    ? {
        ...dataDetail?.value?.data,
        id: id.value,
      }
    : null
);

watch(isOpenModalUpdateProductDetail, (newVal) => {
  if (newVal && id && id !== null) {
    refetch();
  }
});
// ------------------------------------------

// convert tiền sang VND
const formatter = (value: any) => {
      if (!value) return '';
      return `${value} ₫`.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    };

    // Hàm để phân tích dữ liệu khi người dùng nhập vào
//     const parser = (value: any) => {
//   return value.replace(/₫\s?|(,*)|đ$/g, '');
// };


// Hàm tìm id theo tên thuộc tính. Tại vì list là 1 [] nên .value sẽ báo đỏ nhưng vẫn chạy được
const findChatLieu = (ten: string) => {
  const cl = listMaterial.value.find((cl1: any) => cl1.label === ten);
  return cl ? cl.value : null;
};
const findCoAo = (ten: string) => {
  const ca = listCollar.value.find((ca1: any) => ca1.label === ten);
  return ca ? ca.value : null;
};
const findTayAo = (ten: string) => {
  const ta = listSleeve.value.find((ta1: any) => ta1.label === ten);
  return ta ? ta.value : null;
};
const findHoaTiet = (ten: string) => {
  const ht = listPattern.value.find((ht1: any) => ht1.label === ten);
  return ht ? ht.value : null;
};
const findKieuDang = (ten: string) => {
  const kd = listStyle.value.find((kd1: any) => kd1.label === ten);
  return kd ? kd.value : null;
};
const findTinhNang = (ten: string) => {
  const tn = listFeature.value.find((tn1: any) => tn1.label === ten);
  return tn ? tn.value : null;
};
const findMauSac = (ten: string) => {
  const ms = listColor.value.find((ms1: any) => ms1.label === ten);
  return ms ? ms.value : null;
};
const findKichCo = (ten: string) => {
  const kc = listSize.value.find((kc1: any) => kc1.label === ten);
  return kc ? kc.value : null;
};
const findThuongHieu = (ten: string) => {
  const th = listTrademark.value.find((th1: any) => th1.label === ten);
  return th ? th.value : null;
};
const findSanPham = (ten: string) => {
  const sp = listProduct.value.find((sp1: any) => sp1.ten === ten);
  return sp ? sp.id : null;
};

const findTenSanPham = (id: string) => {
  const sp = listProduct.value.find((sp1: any) => sp1.id === id);
  return sp ? sp.ten : null;
}

// Mảng chứa các id được chọn
const selectedRowKeyNews = ref<(string | number)[]>([]);

// Hàm onChange khi chọn các dòng ở table
const rowSelection: TableProps["rowSelection"] = {
  checkStrictly: true,
  onChange: (selectedRowKeys: (string | number)[]) => {
    selectedRowKeyNews.value = selectedRowKeys;
  },
};

const { mutate: updateProductDetail } = useUpdateProductDetail();

// Hàm sửa nhiều dòng đã được chọn
const handleEdit = () => {
  const selectedProducts = computed(() => {
    return dataSourceProduct.filter((product) =>
      selectedRowKeyNews.value.includes(product.id)
    );
  });

  Modal.confirm({
    content: "Bạn chắc chắn muốn sửa các sản phẩm đã chọn?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,

    async onOk() {
      try {
        await Promise.all(
          selectedProducts.value.map((product) => {
            const payload = {
              gia: product.gia,
              soLuong: product.soLuong,
              idChatLieu: findChatLieu(product.chatLieu),
              idCoAo: findCoAo(product.coAo),
              idHoaTiet: findHoaTiet(product.hoaTiet),
              idMauSac: findMauSac(product.mauSac),
              idKichCo: findKichCo(product.kichCo),
              idKieuDang: findKieuDang(product.kieuDang),
              idTayAo: findTayAo(product.tayAo),
              idThuongHieu: findThuongHieu(product.thuongHieu),
              idTinhNang: findTinhNang(product.tinhNang),
              idSanPham: findSanPham(product.sanPham),
              trangThai: 0,
            };

            return updateProductDetail({
              id: product.id,
              params: payload,
            });
          })
        );
        successNotiSort("Cập nhật sản phẩm chi tiết thành công");
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          warningNotiSort(error?.response?.data?.message);
        }
      }
    },
  });
};

const columnsProduct: ColumnType[] = [
  {
    title: "STT",
    dataIndex: "catalog",
    key: "catalog",
    ellipsis: true,
    align: "center",
    width: 50,
  },
  {
    title: "Mã sản phẩm chi tiết",
    dataIndex: "maSanPhamChiTiet",
    key: "maSanPhamChiTiet",
    ellipsis: true,
    width: 100,
  },
  {
    title: "Chất liệu",
    dataIndex: "chatLieu",
    key: "chatLieu",
    ellipsis: true,
    width: 100,
  },
  {
    title: "Thương hiệu",
    dataIndex: "thuongHieu",
    key: "thuongHieu",
    ellipsis: true,
    width: 100,
  },
  {
    title: "Cổ áo",
    dataIndex: "coAo",
    key: "coAo",
    ellipsis: true,
    width: 100,
  },
  {
    title: "Tay áo",
    dataIndex: "tayAo",
    key: "tayAo",
    ellipsis: true,
    width: 100,
  },
  {
    title: "Kiểu dáng",
    dataIndex: "kieuDang",
    key: "kieuDang",
    ellipsis: true,
    width: 100,
  },
  {
    title: "Họa tiết",
    dataIndex: "hoaTiet",
    key: "hoaTiet",
    ellipsis: true,
    width: 100,
  },
  {
    title: "Tính năng",
    dataIndex: "tinhNang",
    key: "tinhNang",
    ellipsis: true,
    width: 100,
  },
  {
    title: "Màu sắc",
    dataIndex: "mauSac",
    key: "mauSac",
    ellipsis: true,
    width: 100,
  },
  {
    title: "Kích cỡ",
    dataIndex: "kichCo",
    key: "kichCo",
    ellipsis: true,
    width: 70,
  },
  {
    title: "Số lượng",
    dataIndex: "soLuong",
    key: "soLuong",
    ellipsis: true,
    width: 100,
  },
  {
    title: "Giá",
    dataIndex: "gia",
    key: "gia",
    ellipsis: true,
    width: 130,
  },
  {
    title: "Trạng thái",
    dataIndex: "trangThai",
    key: "trangThai",
    ellipsis: true,
    align: "center",
  },
  {
    title: "Hành động",
    key: "action",
    align: "center",
    fixed: "right",
  },
];
</script>