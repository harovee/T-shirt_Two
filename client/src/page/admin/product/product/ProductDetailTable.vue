<template v-if="copiedData.length > 0">
  <div
    v-if="selectedRows.length > 0"
    class="mb-2"
    style="display: flex; justify-content: flex-end"
  >
    <div class="me-10">
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
    <div>
      <a-input
        class="me-1"
        v-model:value="newPrice"
        type="number"
        min="0"
        style="width: 100px"
      />
      <a-button type="primary" @click="updateValuesGia">Sửa giá</a-button>
    </div>
  </div>
  <table
    v-if="copiedData.length > 0"
    class="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400 rounded-xl border-2 mb-10"
  >
    <thead
      class="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400"
    >
      <tr>
        <th>
          <input
            class="ms-6"
            type="checkbox"
            :checked="isAllSelected.value"
            @change="toggleAllSelection"
          />
        </th>
        <th scope="col" class="px-6 py-3">STT</th>
        <th scope="col" class="px-6 py-3">Tên sản phẩm</th>
        <th scope="col" class="px-6 py-3">Số lượng</th>
        <th scope="col" class="px-6 py-3">Giá</th>
        <th scope="col" class="px-6 py-3">Hành động</th>
      </tr>
    </thead>
    <tbody>
      <tr
        class="bg-white border-b dark:bg-gray-800 dark:border-gray-700"
        v-for="(item, index) in copiedData"
        :key="index"
      >
        <td class="px-6 py-4">
          <input
            type="checkbox"
            v-model="selectedRows"
            :value="item.idKichCo"
            @change="updateSelection"
          />
        </td>
        <td class="px-6 py-4">{{ index + 1 }}</td>
        <td class="px-6 py-4">
          {{
            findSanPham(item.idSanPham) +
            " [ " +
            findMau(item.idMauSac) +
            " - " +
            findKichCo(item.idKichCo) +
            " ]"
          }}
        </td>
        <td class="px-6 py-4">
          <a-input
            v-model:value="item.soLuong"
            type="number"
            style="width: 100px"
            :min="0"
            @blur="handleInputChangeSoLuong(item, index)"
          />
        </td>
        <td class="px-6 py-4">
          <a-input
            v-model:value="item.gia"
            type="number"
            style="width: 100px"
            :min="0"
            @blur="handleInputChangeGia(item, index)"
          />
        </td>
        <td class="px-6 py-4">
          <a-button type="primary" danger @click="handleDelete(item)"
            >Xóa</a-button
          >
        </td>
      </tr>
    </tbody>
  </table>
  <div v-if="copiedData.length > 0">
    <a-button type="primary" class="w-full" @click="handleCreateProduct()">
      Hoàn thành
    </a-button>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, inject, computed, defineExpose, createVNode } from "vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { Form, message, Modal, Upload } from "ant-design-vue";
import { useRouter } from "vue-router";
import { ProductDetailRequest } from "@/infrastructure/services/api/admin/product_detail.api";
import { ListProductResponse } from "@/infrastructure/services/api/admin/product.api";
import { ListMaterialResponse } from "@/infrastructure/services/api/admin/material.api";
import { ListCollarResponse } from "@/infrastructure/services/api/admin/collar.api";
import { ListTrademarkResponse } from "@/infrastructure/services/api/admin/trademark.api";
import { ListStyleResponse } from "@/infrastructure/services/api/admin/style.api";
import { forEach } from "lodash";
import { keepPreviousData } from "@tanstack/vue-query";
import { useGetListColor } from "@/infrastructure/services/service/admin/color.action";
import { useGetListSize } from "@/infrastructure/services/service/admin/size.action";
import { toast } from "vue3-toastify";
import { warningNotiSort, successNotiSort } from "@/utils/notification.config";
import { useCreateProductDetail } from "@/infrastructure/services/service/admin/productdetail.action";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import { useGetListProduct } from "@/infrastructure/services/service/admin/product.action";
import {
  useGetListMaterial,
  useCreateMaterial,
} from "@/infrastructure/services/service/admin/material.action";
import {
  useGetListCollar,
  useCreateCollar,
} from "@/infrastructure/services/service/admin/collar.action";
import {
  useGetListStyle,
  useCreateStyle,
} from "@/infrastructure/services/service/admin/style.action";
import {
  useGetListTrademark,
  useCreateTrademark,
} from "@/infrastructure/services/service/admin/trademark.action";

const props = defineProps<{
  dataProductDetail: ProductDetailRequest[];
  product: Array<ListProductResponse>;
  material: Array<ListMaterialResponse>;
  collar: Array<ListCollarResponse>;
  style: Array<ListStyleResponse>;
  trademark: Array<ListTrademarkResponse>;
}>();

const emit = defineEmits(["update-data"]);

const router = useRouter();

const copiedData = ref<ProductDetailRequest[]>([]);

const selectedRows = ref<string[]>([]);

// Computed property để kiểm tra xem tất cả hàng đã được chọn chưa
const isAllSelected = computed(
  () => selectedRows.value.length === copiedData.value.length
);

// Hàm thay đổi trạng thái checkbox "Chọn tất cả"
const toggleAllSelection = (event) => {
  if (event.target.checked) {
    selectedRows.value = copiedData.value.map((item) => item.idKichCo);
  } else {
    selectedRows.value = [];
  }
};

const newQuantity = ref(0);
const newPrice = ref(0);

// Cập nhật giá trị trong copiedData cho tất cả các dòng được chọn
const updateValuesGia = (field: string) => {
  if (selectedRows.value.length > 0) {
    // Lặp qua tất cả các chỉ số trong selectedRows
    selectedRows.value.forEach((selectedItem) => {
      // Kiểm tra idKichCo của mục này
      copiedData.value.forEach((item) => {
        // Nếu idKichCo của mục hiện tại trùng với idKichCo của selectedItem
        if (item.idKichCo === selectedItem) {
          if (newPrice.value !== null) {
            item.gia = parseFloat(newPrice.value + ""); // Cập nhật giá
          }
        }
      });
    });
  }
  newPrice.value = 0;
};

const updateValuesSoLuong = (field: string) => {
  if (selectedRows.value.length > 0) {
    // Lặp qua tất cả các chỉ số trong selectedRows
    selectedRows.value.forEach((selectedItem) => {
      // Kiểm tra idKichCo của mục này
      copiedData.value.forEach((item) => {
        // Nếu idKichCo của mục hiện tại trùng với idKichCo của selectedItem
        if (item.idKichCo === selectedItem) {
          if (newQuantity.value !== null) {
            item.soLuong = parseFloat(newQuantity.value + ""); // Cập nhật giá
          }
        }
      });
    });
  }
  newQuantity.value = 0;
};

// Cập nhật lựa chọn khi một checkbox thay đổi
const updateSelection = () => {
  if (selectedRows.value.length === copiedData.value.length) {
    selectedRows.value = copiedData.value.map((item) => item.idKichCo);
  } else {
    selectedRows.value = selectedRows.value.filter((idKichThuoc) =>
      copiedData.value.some((item) => item.idKichCo === idKichThuoc)
    );
  }
};

watch(
  () => props.dataProductDetail,
  (newData) => {
    copiedData.value = [...newData];
  }
);

// lấy danh sách kich cỡ
const { data: sizes } = useGetListSize({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listSize = computed(() => {
  return (
    sizes?.value?.data.map((size) => ({
      value: size.id,
      label: size.ten,
    })) || []
  );
});

// lấy danh sách màu sắc
const { data: colors } = useGetListColor({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listColor = computed(() => {
  return (
    colors?.value?.data.map((color) => ({
      value: color.id,
      label: color.ten,
    })) || []
  );
});

// lấy danh sách thương hiệu
const { data: trademarks } = useGetListTrademark({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listTrademark = computed(() => {
  return (
    trademarks?.value?.data.map((tra) => ({
      value: tra.id,
      label: tra.ten,
    })) || []
  );
});

// lấy danh sách chất liệu
const { data: materials } = useGetListMaterial({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listMaterial = computed(() => {
  return (
    materials?.value?.data.map((mate) => ({
      value: mate.id,
      label: mate.ten,
    })) || []
  );
});

// lấy danh sách cổ áo
const { data: collars } = useGetListCollar({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listCollar = computed(() => {
  return (
    collars?.value?.data.map((collar) => ({
      value: collar.id,
      label: collar.ten,
    })) || []
  );
});

// lấy danh sách kiểu dáng
const { data: styles } = useGetListStyle({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listStyle = computed(() => {
  return (
    styles?.value?.data.map((style) => ({
      value: style.id,
      label: style.ten,
    })) || []
  );
});

//Lấy danh sách sản phẩm
const { data: products } = useGetListProduct({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listProduct = computed(() => {
  return (
    products?.value?.data.map((product) => ({
      value: product.id,
      label: product.ten,
    })) || []
  );
});

const findMau = (id: string) => {
  const mau = listColor.value.find((mau) => mau.value === id);
  return mau ? mau.label : null;
};

const findKichCo = (id: string) => {
  const kt = listSize.value.find((size) => size.value === id);
  return kt ? kt.label : null;
};

const findSanPham = (id: string) => {
  const sp = listProduct.value.find((sp1) => sp1.value === id);
  return sp ? sp.label : null;
};

const findChatLieu = (id: string) => {
  const cl = listMaterial.value.find((cl1) => cl1.value === id);
  return cl ? cl.label : null;
};

const findCoAo = (id: string) => {
  const ca = listCollar.value.find((ca1) => ca1.value === id);
  return ca ? ca.label : null;
};

const findThuongHieu = (id: string) => {
  const th = listTrademark.value.find((th1) => th1.value === id);
  return th ? th.label : null;
};

const findKieuDang = (id: string) => {
  const kd = listStyle.value.find((kd1) => kd1.value === id);
  return kd ? kd.label : null;
};

// Khi cần sao chép dữ liệu từ propsdataProductDetail
copiedData.value = [...props.dataProductDetail];

// console.log(copiedData.value);

// Hàm Xử lý các kí tự khi truyền vào giá và số lượng phải là số
const handleInputChangeSoLuong = (
  item: ProductDetailRequest,
  index: number
) => {
  let value = item["soLuong"].toString();
  value = value.replace(/[^0-9.]/g, "");
  if (value.split(".").length > 2) {
    value = value.substring(0, value.lastIndexOf("."));
  }
  if (value === "") {
    value = "0";
  }
  item["soLuong"] = parseFloat(value);
  copiedData.value[index] = { ...copiedData.value[index], ...item };
  console.log(copiedData);
  console.log(props.dataProductDetail);
};

const handleInputChangeGia = (item: ProductDetailRequest, index: number) => {
  let value = item["gia"].toString();
  value = value.replace(/[^0-9.]/g, "");
  if (value.split(".").length > 2) {
    value = value.substring(0, value.lastIndexOf("."));
  }
  if (value === "") {
    value = "0";
  }
  item["gia"] = parseFloat(value);
  copiedData.value[index] = { ...copiedData.value[index], ...item };
  console.log(copiedData);
  console.log(props.dataProductDetail);
};

const handleDelete = (index: number) => {
  copiedData.value.splice(index, 1);
  if (copiedData.value.length === 0) {
    emit("update-data", true);
  }
};

// Thêm sản phẩm chi tiết toàn bộ list copitedData
const { mutate: create } = useCreateProductDetail();

const handleCreateProduct = async () => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn thêm?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      if (!copiedData.value || copiedData.value.length === 0) {
        return;
      }
      const promises = copiedData.value.map(async (item) => {
        try {
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
    },
  });
};
</script>

<style scoped>
.editable-row-operations a {
  margin-right: 8px;
  cursor: pointer;
}
</style>