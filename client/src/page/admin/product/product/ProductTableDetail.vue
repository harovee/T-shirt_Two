<template>
  <div
    v-if="selectedRowKeyNews.length > 0 && dataSource.length > 0"
    class="mb-2"
    style="display: flex; justify-content: flex-end"
  >
    <div class="me-10">
      <a-input
        class="me-1"
        v-model:value="newPrice"
        type="number"
        min="0"
        style="width: 100px"
      />
      <a-button type="primary" @click="updateValuesGia">Sửa giá</a-button>
    </div>
    <div>
      <a-input
        class="me-1"
        v-model:value="newQuantity"
        type="number"
        min="0"
        style="width: 100px"
      />
      <a-button type="primary" @click="updateValuesSoLuong"
        >Sửa số lượng</a-button
      >
    </div>
  </div>
  <a-table
    v-if="dataSource.length > 0"
    :loading="isLoading"
    :row-selection="rowSelection"
    :row-key="(record) => record.id"
    :columns="columns"
    :data-source="dataSource"
    :pagination="false"
    :scroll="{ x: 300, y: 300 }"
  >
    <template #bodyCell="{ column, record, index }">
      <div v-if="column.key === 'stt'" class="text-center">
        {{ index + 1 }}
      </div>
      <div v-if="column.key === 'ten'" class="text-center">
        {{
          findSanPham(record.idSanPham) +
          " [ " +
          findMau(record.idMauSac) +
          " - " +
          findKichCo(record.idKichCo) +
          " ]"
        }}
      </div>
      <div v-if="column.key === 'soLuong'" class="text-center">
        <a-input
          type="number"
          min="0"
          v-model:value="record.soLuong"
          @blur="handleInputChangeSoLuong(record, index)"
        >
        </a-input>
      </div>
      <div v-if="column.key === 'gia'" class="text-center">
        <a-input
          type="number"
          min="0"
          v-model:value="record.gia"
        >
        </a-input>
      </div>
      <div
        v-else-if="column.key === 'action'"
        class="flex items-center justify-center space-x-2"
      >
        <a-tooltip title="Xóa" trigger="hover">
          <a-button
            class="bg-purple-100"
            size="middle"
            shape="round"
            @click="handleDelete(record)"
          >
            <v-icon name="fa-trash"></v-icon>
          </a-button>
        </a-tooltip>
      </div>
      <!-- Upload ảnh -->
      <div v-if="column.key === 'anh'" class="text-center">
        <template v-if="getRowSpan(record) > 1 && isFirstInGroup(record)">
          <div style="display: flex; align-items: center; gap: 10px">
            <a-button
              @click="openWidget(record)"
              style="width: 80px; height: 80px; border: 1px dashed #D3D3D3;"
              v-if="
                (fileLists[record.idMauSac] &&
                  fileLists[record.idMauSac].length <= 2) ||
                !fileLists[record.idMauSac]
              "
            >
            <v-icon name="co-plus" style="font-size: 14px"></v-icon
              >
              <div>Ảnh</div>
            </a-button>
            <div
              v-for="(file, fileIndex) in fileLists[record.idMauSac]"
              :key="fileIndex"
              class="uploaded-image"
              style="display: inline-block; position: relative"
            >
              <img
                :src="file.url"
                alt="Uploaded image"
                style="
                  width: 80px;
                  height: 80px;
                  object-fit: cover;
                  border-radius: 5px;
                "
              />
              <a-button
                type="primary"
                danger
                style="
                  position: absolute;
                  top: 5px;
                  right: 5px;
                  padding: 5px 10px;
                  font-size: 12px;
                  z-index: 10;
                  width: 20px;
                  height: 20px;
                  font-size: 10px;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                "
                @click="removeImage(fileIndex, record.idMauSac)"
                ><v-icon name="fa-trash" style="font-size: 14px"></v-icon
              ></a-button>
            </div>
          </div>
        </template>
      </div>
      <!-- ------------- -->
    </template>
  </a-table>
  <!-- <a-pagination
      v-if="dataSource.length > 0"
      class="m-2"
      v-model:current="current1"
      v-model:pageSize="pageSize"
      :total="dataSource.length"
    /> -->
  <div class="mt-10" v-if="dataSource.length > 0">
    <a-button type="primary" class="w-full" @click="handleCreateProduct()">
      Hoàn thành
    </a-button>
  </div>
</template>

<script lang="ts" setup>
import type { TableProps, TableColumnType } from "ant-design-vue";
import { ColumnType } from "ant-design-vue/es/table";
import {
  defineProps,
  computed,
  defineEmits,
  ref,
  watch,
  createVNode,
  reactive,
  nextTick,
} from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  FindProductRequest,
  AttributeResponse,
} from "@/infrastructure/services/api/admin/sale.api";
import { useGetProducts } from "@/infrastructure/services/service/admin/sale.action";
import {
  ProductDetailRequest,
  RenProductDetailResponse,
} from "@/infrastructure/services/api/admin/product_detail.api";
import type { UploadProps } from "ant-design-vue";
import { ListProductResponse } from "@/infrastructure/services/api/admin/product.api";
import { ListSizeResponse } from "@/infrastructure/services/api/admin/size.api";
import { ListColorResponse } from "@/infrastructure/services/api/admin/color.api";
import { warningNotiSort, successNotiSort, errorNotiSort } from "@/utils/notification.config";
import { useCreateProductDetail } from "@/infrastructure/services/service/admin/productdetail.action";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { Form, message, Modal, Upload } from "ant-design-vue";
import { useRouter } from "vue-router";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import {
  CLOUDINARY_CLOUD_NAME,
  CLOUDINARY_UPLOAD_PRESET,
} from "@/infrastructure/constants/cloudinary";
import {
  PlusOutlined,
  LoadingOutlined,
  DeleteOutlined,
} from "@ant-design/icons-vue";
import { error } from "console";

const emit = defineEmits([
  "update-data",
  "updateDataProductDetail",
  "updatePrice",
  "updateQuantity",
]);

// ------------------------------------
// Upload ảnh

const currentIdMauSac = ref<string | null>(null);

const myWidget = cloudinary.createUploadWidget(
  {
    cloudName: CLOUDINARY_CLOUD_NAME,
    uploadPreset: CLOUDINARY_UPLOAD_PRESET,
  },
  (error: any, result: any) => {
    if (!error && result && result.event === "success") {
      const imageUrl = result.info.url; // Lấy URL ảnh từ Cloudinary
      if (currentIdMauSac.value) {
        if (!fileLists[currentIdMauSac.value]) {
          fileLists[currentIdMauSac.value] = [];
        }
        fileLists[currentIdMauSac.value].push({
          url: imageUrl,
          name: result.info.original_filename,
        });
      }
    }
  }
);

// Mở widget Cloudinary khi người dùng nhấn nút
const openWidget = (record: any) => {
  currentIdMauSac.value = record.idMauSac;
  myWidget.open();
  // console.log(fileLists);
};

const fileLists = reactive<Record<string, any[]>>({});

watch(
  fileLists,
  (newValue, oldValue) => {
    console.log("fileLists đã thay đổi:", newValue);
  },
  { deep: true }
);
// Loại bỏ ảnh khỏi mảng fileLists dựa trên index
const removeImage = (fileIndex: number, idMauSac: string) => {
  if (fileLists && fileLists[idMauSac] && Array.isArray(fileLists[idMauSac])) {
    fileLists[idMauSac].splice(fileIndex, 1);
    successNotiSort("Ảnh đã được xóa");
  } else {
    errorNotiSort("Không tìm thấy ảnh để xóa hoặc danh sách ảnh không hợp lệ");
  }
};
// ---------------------------------------------

const router = useRouter();

const pageSize = ref(3);
const current1 = ref(1);
const props = defineProps<{
  categories: AttributeResponse[] | undefined;
  idSanPhams: string[] | undefined;
  dataProductDetail: RenProductDetailResponse[];
  products: Array<ListProductResponse>;
  colors: Array<ListColorResponse>;
  sizes: Array<ListSizeResponse>;
}>();

const formatter = (value: any) => {
      if (!value) return '';
      return `${value} ₫`.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    };

interface DataType {
  stt: number;
  id: string;
  trangThai: 0;
  gia: string | 0;
  soLuong: string | 0;
  idChatLieu: string | null;
  idCoAo: string | null;
  idHoaTiet: string | null;
  idMauSac: string | null;
  idKichCo: string | null;
  idKieuDang: string | null;
  idTayAo: string | null;
  idThuongHieu: string | null;
  idTinhNang: string | null;
  idSanPham: string | null;
}


// TableColumnType<RenProductDetailResponse>[]
const columns: TableColumnType<RenProductDetailResponse>[] = [
  {
    title: "STT",
    dataIndex: "stt",
    key: "stt",
    align: "center",
    width: 100,
  },
  {
    title: "Tên sản phẩm",
    dataIndex: "ten",
    key: "ten",
    align: "center",
  },
  {
    title: "Giá",
    dataIndex: "gia",
    key: "gia",
    align: "center",
    width: 200,
  },
  {
    title: "Số lượng",
    dataIndex: "soLuong",
    key: "soLuong",
    align: "center",
    ellipsis: true,
    width: 150,
  },
  {
    title: "Hành động",
    dataIndex: "action",
    key: "action",
    align: "center",
    width: 100,
  },
  {
    title: "Ảnh",
    dataIndex: "anh",
    key: "anh",
    align: "center",
    width: 500,
  },
];

const params = ref<FindProductRequest>({
  page: 1,
  size: 5,
  key: null,
  idDanhMuc: null,
});

const newQuantity = ref(0);
const newPrice = ref(0);

// Cập nhật giá trị trong copiedData cho tất cả các dòng được chọn
const updateValuesGia = (field: string) => {
  if (dataSource.value.length > 0) {
    selectedRowKeyNews.value.forEach((selectedKey) => {
      dataSource.value.forEach((item) => {
        if (item.id === selectedKey) {
          if (newPrice.value !== null) {
            item.gia = parseFloat(newPrice.value + "");
          }
        }
      });
    });
  }
  newPrice.value = 0;
  emit("updatePrice", dataSource.value);
  // console.log(dataSource.value);
};

const updateValuesSoLuong = (field: string) => {
  if (dataSource.value.length > 0) {
    selectedRowKeyNews.value.forEach((selectedKey) => {
      dataSource.value.forEach((item) => {
        if (item.id === selectedKey) {
          if (newQuantity.value !== null) {
            item.soLuong = parseFloat(newQuantity.value + "");
          }
        }
      });
    });
  }
  newQuantity.value = 0;
  emit("updateQuantity", dataSource.value);
  // console.log(dataSource.value);
};

// Blur giá, số lượng thì sẽ thay đổi
const handleInputChangeSoLuong = (
  record: RenProductDetailResponse,
  index: number
) => {
  let value = record["soLuong"].toString();
  value = value.replace(/[^0-9.]/g, "");
  if (value.split(".").length > 2) {
    value = value.substring(0, value.lastIndexOf("."));
  }
  if (value === "") {
    value = "0";
  }
  record["soLuong"] = parseFloat(value);
  dataSource.value[index] = { ...dataSource.value[index], ...record };
  // console.log(dataSource.value);
};

const handleInputChangeGia = (
  record: RenProductDetailResponse,
  index: number
) => {
  let value = record["gia"].toString();
  value = value.replace(/[^0-9.]/g, "");
  if (value.split(".").length > 2) {
    value = value.substring(0, value.lastIndexOf("."));
  }
  if (value === "") {
    value = "0";
  }
  record["gia"] = parseFloat(value);
  dataSource.value[index] = { ...dataSource.value[index], ...record };
  // console.log(dataSource.value);
};
// -----------------------------------------------------



const findMau = (id: string) => {
  const mau = props.colors.find((mau) => mau.value === id);
  return mau ? mau.label : null;
};

const findKichCo = (id: string) => {
  const kt = props.sizes.find((size) => size.value === id);
  return kt ? kt.label : null;
};

const findSanPham = (id: string) => {
  const sp = props.products.find((sp1) => sp1.value === id);
  return sp ? sp.label : null;
};

const getRowSpan = (record) => {
  return dataSource.value.filter((item) => item.idMauSac === record.idMauSac)
    .length;
};

// Kiểm tra nếu dòng hiện tại là dòng đầu tiên trong nhóm có cùng idMauSac
const isFirstInGroup = (record) => {
  // const index = dataSource.value.findIndex((item) => item.id === record.id);
  // const firstInGroup = dataSource.value.findIndex(
  //   (item) => item.idMauSac === record.idMauSac
  // );
  // return index === firstInGroup;
  const group = dataSource.value.filter((item) => item.idMauSac === record.idMauSac);
  if (group.length === 1) {
    console.log(group);
  } else {
    return group[0].id === record.id;
  }
  // console.log(group.length);
  
};

// watch(
//   () => dataSource.value,
//   (newValue) => {
//     console.log(newValue);
//   }
// );

const handleDelete = (record: RenProductDetailResponse) => {
  emit("updateDataProductDetail", record);
};

const handleChangeKey = () => {
  current1.value = 1;
};

const { data, isLoading } = useGetProducts(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataSource: RenProductDetailResponse[] | any = computed(() => {
  return (
    props.dataProductDetail?.map((e: any, index) => ({
      // stt: index + 1,
      id: e.id || "",
      gia: e.gia || 0,
      soLuong: e.soLuong || 0,
      trangThai: 0,
      idChatLieu: e.idChatLieu || null,
      idCoAo: e.idCoAo || null,
      idHoaTiet: e.idHoaTiet || null,
      idMauSac: e.idMauSac || null,
      idKichCo: e.idKichCo || null,
      idKieuDang: e.idKieuDang || null,
      idTayAo: e.idTayAo || null,
      idThuongHieu: e.idThuongHieu || null,
      idTinhNang: e.idTinhNang || null,
      idSanPham: e.idSanPham || null,
    })) || []
  );
});

watch(
  () => dataSource.value,
  (newValue) => {
    if (newValue.length === 0) {
      selectedRowKeyNews.value = [];
    }
  }
);

watch(current1, () => {
  params.value.page = current1.value == 0 ? 1 : current1.value;
});

const selectedRowKeyNews = ref<(string | number)[]>([]);

const rowSelection: TableProps["rowSelection"] = {
  onChange: (selectedRowKeys: string | any) => {
    selectedRowKeyNews.value = selectedRowKeys;
  },
};

// watch (() => selectedRowKeyNews.value, (newValue) => console.log(newValue)
// )

// Điều hướng sau khi thêm success
const handleRedirectProductDetail = (id: string) => {
  router.push(
    `${ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.path}/${id}`
  );
};

// Thêm sản phẩm chi tiết toàn bộ list copitedData
const { mutate: create } = useCreateProductDetail();

const handleCreateProduct = async () => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn thêm?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      if (!dataSource.value || dataSource.value.length === 0) {
        return;
      }
      const promises = dataSource.value.map(async (item) => {
        try {
          if (fileLists && fileLists !== null) {
            if (fileLists.hasOwnProperty(item.idMauSac)) {
              item.listAnh = fileLists[item.idMauSac];
            } else {
              item.listAnh = [];
            }
          }
          create(item);
        } catch (error: any) {
          console.error("🚀 ~ handleCreate ~ error:", error);
          if (error?.response) {
            warningNotiSort(error?.response?.data?.message);
            return;
          } else if (error?.errorFields) {
            warningNotiSort("Vui lòng nhập đầy đủ các trường dữ liệu");
            return;
          }
        }
      });
      await Promise.all(promises);
      successNotiSort("Tất cả sản phẩm đã được tạo thành công!");
      handleRedirectProductDetail(dataSource.value[0].idSanPham);
    },
  });
};
</script>