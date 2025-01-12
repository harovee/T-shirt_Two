<template>
  <div class="grid justify-center">
    <div>
      <h3
        class="flex justify-center items-center text-1000 text-3xl font-semibold"
      >
        Thêm sản phẩm
      </h3>
    </div>
    <div class="w-[80rem]">
      <a-form
        layout="vertical"
        class="p-5 grid grid-cols-4 gap-4 md:grid-cols-1 lg:grid-cols-3"
      >
        <template v-for="field in formFields">
          <a-form-item
            :label="field.label"
            :name="field.name"
            v-bind="validateInfos[field.name]"
            v-if="field.name === 'idSanPham'"
            class="col-span-3 md:col-span-3 lg:col-span-3"
          >
            <a-input
              v-if="field.component === 'a-input'"
              v-model:value="modelRef[field.name]"
            ></a-input>

            <div v-if="field.name === 'idSanPham'">
              <a-select
                v-model:value="modelRef[field.name]"
                show-search
                placeholder="Chọn tên sản phẩm"
                :options="options"
                :filter-option="filterOption"
                @change="generateProductDetails"
                style="margin-right: 8px"
              ></a-select>
              <!-- Nút thêm sản phẩm -->
              <a
                class="mt-2"
                @click="handleOpenModalCreateProduct"
                style="
                  color: #1890ff;
                  cursor: pointer;
                  display: flex;
                  align-items: center;
                "
              >
                Thêm sản phẩm
              </a>
            </div>
          </a-form-item>
          <a-form-item
            :label="field.label"
            :name="field.name"
            v-bind="validateInfos[field.name]"
            v-else
          >
            <!-- Thuộc tính -->

            <div>
              <div>
                <a-select
                  v-if="field.name === 'idChatLieu'"
                  v-model:value="modelRef[field.name]"
                  placeholder="Chọn chất liệu"
                  :options="listMaterial"
                  @change="generateProductDetails"
                ></a-select>
              </div>
              <div>
                <a-select
                  v-if="field.name === 'idCoAo'"
                  v-model:value="modelRef[field.name]"
                  placeholder="Chọn cổ áo"
                  :options="listCollar"
                  @change="generateProductDetails"
                ></a-select>
              </div>
              <div>
                <a-select
                  v-if="field.name === 'idTayAo'"
                  v-model:value="modelRef[field.name]"
                  placeholder="Chọn tay áo"
                  :options="listSleeve"
                  @change="generateProductDetails"
                ></a-select>
              </div>
            </div>

            <a-select
              v-if="field.name === 'idHoaTiet'"
              v-model:value="modelRef[field.name]"
              placeholder="Chọn hoa tiết"
              :options="listPattern"
              @change="generateProductDetails"
              style="margin-right: 8px"
            ></a-select>
            <a-select
              v-if="field.name === 'idKieuDang'"
              v-model:value="modelRef[field.name]"
              placeholder="Chọn kiểu dáng"
              :options="listStyle"
              @change="generateProductDetails"
              style="margin-right: 8px"
            ></a-select>
            <a-select
              v-if="field.name === 'idThuongHieu'"
              v-model:value="modelRef[field.name]"
              placeholder="Chọn thương hiệu"
              :options="listTrademark"
              @change="generateProductDetails"
              style="margin-right: 8px"
            ></a-select>
            <a-select
              v-if="field.name === 'idTinhNang'"
              v-model:value="modelRef[field.name]"
              placeholder="Chọn tính năng"
              :options="listFeature"
              @change="generateProductDetails"
              style="margin-right: 8px"
            ></a-select>
            <a-select
              v-if="field.name === 'idMauSac'"
              v-model:value="colorItem"
              mode="multiple"
              style="width: 100%"
              placeholder="Chọn màu sắc"
              @change="generateProductDetails"
              :options="listColor"
            />
            <a-select
              v-if="field.name === 'idKichCo'"
              v-model:value="sizeItem"
              mode="multiple"
              style="width: 100%"
              placeholder="Chọn kích cỡ"
              @change="generateProductDetails"
              :options="listSize"
            />
          </a-form-item>
        </template>
      </a-form>
      <modal-create-product
        :open="isOpenModalCreateProduct"
        @handleClose="handleCloseModalCreateProduct"
        @onCancel="isOpenModalCreateProduct = false"
      />
    </div>
    <div>
      <div class="mb-5">
        Bảng sản phẩm chi tiết
      </div>
      <product-detail-table :data-product-detail="productDetails" />
    </div>
  </div>
</template>
<script setup lang="ts">
import {
  computed,
  createVNode,
  defineEmits,
  defineProps,
  reactive,
  ref,
  watch,
  inject,
  provide,
} from "vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { toast } from "vue3-toastify";
import { keepPreviousData } from "@tanstack/vue-query";
import { ProductRequest } from "@/infrastructure/services/api/admin/product.api";
import { ProductDetailRequest } from "@/infrastructure/services/api/admin/product_detail.api";
import { useGetListProduct } from "@/infrastructure/services/service/admin/product.action";
import { useGetListMaterial } from "@/infrastructure/services/service/admin/material.action";
import { useGetListCollar } from "@/infrastructure/services/service/admin/collar.action";
import { useGetListColor } from "@/infrastructure/services/service/admin/color.action";
import { useGetListFeature } from "@/infrastructure/services/service/admin/feature.action";
import { useGetListPattern } from "@/infrastructure/services/service/admin/pattern.action";
import { useGetListSize } from "@/infrastructure/services/service/admin/size.action";
import { useGetListSleeve } from "@/infrastructure/services/service/admin/sleeve.action";
import { useGetListStyle } from "@/infrastructure/services/service/admin/style.action";
import { useGetListTrademark } from "@/infrastructure/services/service/admin/trademark.action";
import ModalCreateProduct from "@/page/admin/product/product/ModalCreateProduct.vue";
import ProductDetailTable from "@/page/admin/product/product/ProductDetailTable.vue";
import { Form, message, Modal, Upload } from "ant-design-vue";
import { PlusOutlined } from "@ant-design/icons-vue";

const props = defineProps({
  open: Boolean,
});

// tạo request create product detail
const modelRef = reactive<ProductDetailRequest>({
  trangThai: 0,
  gia: 0,
  soLuong: 0,
  idChatLieu: null,
  idCoAo: null,
  idHoaTiet: null,
  idMauSac: null,
  idKichCo: null,
  idKieuDang: null,
  idTayAo: null,
  idThuongHieu: null,
  idTinhNang: null,
  idSanPham: null,
});

const colorItem = ref([]);

const sizeItem = ref([]);

//Lấy list id và tên product fill lên select option
const { data } = useGetListProduct({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataProduct = computed(() => data?.value?.data || []);

// lấy danh sách chất liệu
const { data: materials } = useGetListMaterial({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listMaterial = computed(() => {
  return (
    materials?.value?.map((mate) => ({
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
    collars?.value?.map((collar) => ({
      value: collar.id,
      label: collar.ten,
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
    colors?.value?.map((color) => ({
      value: color.id,
      label: color.ten,
    })) || []
  );
});

// lấy danh sách tính năng
const { data: features } = useGetListFeature({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listFeature = computed(() => {
  return (
    features?.value?.map((feature) => ({
      value: feature.id,
      label: feature.ten,
    })) || []
  );
});

// lấy danh sách họa tiết
const { data: patterns } = useGetListPattern({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listPattern = computed(() => {
  return (
    patterns?.value?.map((pattern) => ({
      value: pattern.id,
      label: pattern.ten,
    })) || []
  );
});

// lấy danh sách kich cỡ
const { data: sizes } = useGetListSize({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listSize = computed(() => {
  return (
    sizes?.value?.map((size) => ({
      value: size.id,
      label: size.ten,
    })) || []
  );
});

// lấy danh sách tay áo
const { data: sleeves } = useGetListSleeve({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listSleeve = computed(() => {
  return (
    sleeves?.value?.map((sleeve) => ({
      value: sleeve.id,
      label: sleeve.ten,
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
    styles?.value?.map((style) => ({
      value: style.id,
      label: style.ten,
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
    trademarks?.value?.map((tra) => ({
      value: tra.id,
      label: tra.ten,
    })) || []
  );
});

const emit = defineEmits(["handleClose"]);

const rulesRef = reactive({
  name: [
    {
      required: true,
      message: "Vui lòng nhập tên sản phẩm",
      trigger: "blur",
    },
  ],
});

// Select
const options = computed(() => {
  return dataProduct?.value?.map((product) => ({
    value: product.id,
    label: product.ten,
  }));
});
const handleChange = (value: string) => {
  console.log(modelRef);
};

const handleChangeColor = (value: string) => {
  console.log(colorItem);
};

const handleChangeSize = (value: string) => {
  console.log(sizeItem);
};

const filterOption = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const value = ref<string | undefined>(undefined);

// Đóng mở modal
const isOpenModalCreateProduct = ref(false);

const handleOpenModalCreateProduct = () => {
  value.value = undefined;
  isOpenModalCreateProduct.value = true;
};

const handleCloseModalCreateProduct = () => {
  isOpenModalCreateProduct.value = false;
};

const productDetails = ref<ProductDetailRequest[]>([]);

// Hàm tạo các chi tiết sản phẩm với tất cả kết hợp giữa màu sắc và kích thước
const generateProductDetails = () => {
  const generatedDetails: ProductDetailRequest[] = [];

  // Lặp qua mỗi màu sắc và kích thước để tạo ra các kết hợp
  colorItem.value.forEach((color) => {
    sizeItem.value.forEach((size) => {
      // Thêm chi tiết sản phẩm với sự kết hợp của màu sắc và kích thước
      generatedDetails.push({
        trangThai: modelRef.trangThai,
        gia: modelRef.gia,
        soLuong: modelRef.soLuong,
        idChatLieu: modelRef.idChatLieu,
        idCoAo: modelRef.idCoAo,
        idHoaTiet: modelRef.idHoaTiet,
        idMauSac: color, // Màu sắc động
        idKichCo: size, // Kích thước động
        idKieuDang: modelRef.idKieuDang,
        idTayAo: modelRef.idTayAo,
        idThuongHieu: modelRef.idThuongHieu,
        idTinhNang: modelRef.idTinhNang,
        idSanPham: modelRef.idSanPham,
      });
    });
  });

  // Cập nhật lại giá trị của productDetails
  productDetails.value = generatedDetails;
  console.log(generatedDetails);
};

const { resetFields, validate, validateInfos } = Form.useForm(rulesRef);

const formFields = computed(() => [
  {
    label: "Tên sản phẩm",
    name: "idSanPham",
    component: "a-select",
    placeholder: "Nhâp tên sản phẩm",
  },
  {
    label: "Chất liệu",
    name: "idChatLieu",
    component: "a-select",
    placeholder: "Chọn chất liệu",
  },
  {
    label: "Cổ áo",
    name: "idCoAo",
    component: "a-select",
    placeholder: "Chọn cổ áo",
  },
  {
    label: "Tay áo",
    name: "idTayAo",
    component: "a-select",
    placeholder: "Chọn tay áo",
  },
  {
    label: "Họa tiết",
    name: "idHoaTiet",
    component: "a-select",
    placeholder: "Chọn họa tiết",
  },
  {
    label: "Tính năng",
    name: "idTinhNang",
    component: "a-select",
    placeholder: "Chọn tính năng",
  },
  {
    label: "Thương hiệu",
    name: "idThuongHieu",
    component: "a-select",
    placeholder: "Chọn thương hiệu",
  },
  {
    label: "Kiểu dáng",
    name: "idKieuDang",
    component: "a-select",
    placeholder: "Chọn kiểu dáng",
  },
  {
    label: "Màu sắc",
    name: "idMauSac",
    component: "a-select",
    placeholder: "Chọn màu sắc",
  },
  {
    label: "Kích cỡ",
    name: "idKichCo",
    component: "a-select",
    placeholder: "Chọn kích cỡ",
  },
]);
</script>

<style scoped>
.select-container {
  display: flex;
  gap: 8px; /* Khoảng cách giữa các phần tử */
  justify-content: flex-start;
  flex-wrap: wrap;
}

.product-container {
  display: flex;
  flex-direction: column; /* Sản phẩm và nút Thêm sản phẩm sẽ nằm dọc */
  margin-bottom: 16px; /* Khoảng cách giữa sản phẩm và phần tử bên dưới */
}
</style>