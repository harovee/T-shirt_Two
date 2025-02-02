<template>
  <div class="p-6 grid grid-cols-1 gap-6">
      <div class="flex items-center gap-2">
        <v-icon
          name="md-widgets-outlined"
          size="x-large"
          width="48"
          height="48"
        />
        <h3 class="text-2xl m-0">Thêm sản phẩm</h3>
    </div>
    <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
      <a-form
        layout="vertical"
        class="p-10 grid grid-cols-4 gap-4 md:grid-cols-1 lg:grid-cols-3"
      >
        <template v-for="field in formFields">
          <a-form-item
            :label="field.label"
            :name="field.name"
            v-bind="validateInfos[field.name]"
            v-if="field.name === 'idSanPham'"
            class="col-span-3 md:col-span-3 lg:col-span-3 label-bold"
          >
            <a-input
              v-if="field.component === 'a-input'"
              v-model:value="modelRef[field.name]"
            ></a-input>
            <div>
              <a-row gutter="{8}" align="middle">
                <!-- Select Product -->
                <a-col :span="23" class="pe-4">
                  <a-select
                    v-if="field.name === 'idSanPham'"
                    v-model:value="modelRef[field.name]"
                    show-search
                    placeholder="Chọn tên sản phẩm"
                    :options="dataProduct"
                    :filter-option="filterOption"
                    @change="generateProductDetails"
                    style="width: 100%"
                  ></a-select>
                </a-col>
                <!-- Add Product Button -->

                <a-col :span="1">
                  <a-button
                    class="bg-purple-100 flex justify-between items-center"
                    size="medium"
                    @click="handleOpenModalCreateProduct"
                  >
                    <v-icon name="md-addcircle" class="mx-2" />
                  </a-button>
                </a-col>
              </a-row>
            </div>
          </a-form-item>

          <a-form-item
            :label="field.label"
            :name="field.name"
            v-bind="validateInfos[field.name]"
            v-else-if="field.name === 'idMauSac'"
            class="col-span-3 md:col-span-3 lg:col-span-3 label-bold"
          >
            <a-select
              v-if="field.name === 'idMauSac'"
              v-model:value="colorItem"
              mode="multiple"
              style="width: 100%"
              placeholder="Chọn màu sắc"
              :options="listColor"
              @change="generateProductDetails"
              :show-search="true"
              :filter-option="filterOptionColor"
              :not-found-content="notFoundContentColor"
              @search="handleInputColor"
            >
              <template #option="{ label, maMauSac }">
                <span
                  :style="{
                    display: 'inline-block',
                    width: '20px',
                    height: '20px',
                    backgroundColor: maMauSac,
                    borderRadius: '50%',
                    marginRight: '10px',
                    verticalAlign: 'middle',
                  }"
                ></span>
                <span style="vertical-align: middle">{{ label }}</span>
              </template>
              <template
                #tagRender="{ label, option ,closable, onClose }"
              >
                <a-tag
                  :closable="closable"
                  @close="onClose"
                   style="vertical-align: middle"
                >
                  <span
                  :style="{
                    display: 'inline-block',
                    width: '15px',
                    height: '15px',
                    backgroundColor: option.maMauSac,
                    borderRadius: '50%',
                    marginRight: '10px',
                    verticalAlign: 'middle',
                  }"
                ></span>
                <span style="vertical-align: middle">{{ label }}</span>
                </a-tag>
              </template>
              <!-- <a-select-option
                v-for="(color, index) in listColor"
                :key="index"
                :value="color.value"
              >
                <span
                  :style="{
                    display: 'inline-block',
                    width: '20px',
                    height: '20px',
                    backgroundColor: color.maMauSac,
                    borderRadius: '50%',
                    marginRight: '10px',
                    verticalAlign: 'middle'
                  }"
                ></span>
                <span style="vertical-align: middle;">{{ color.label }}</span>
              </a-select-option> -->
            </a-select>
          </a-form-item>
          <a-form-item
            :label="field.label"
            :name="field.name"
            v-bind="validateInfos[field.name]"
            v-else-if="field.name === 'idKichCo'"
            class="col-span-3 md:col-span-3 lg:col-span-3 label-bold"
          >
            <a-select
              v-if="field.name === 'idKichCo'"
              v-model:value="sizeItem"
              mode="multiple"
              style="width: 100%"
              placeholder="Chọn kích cỡ"
              @change="generateProductDetails"
              :options="listSize"
              :show-search="true"
              :filter-option="filterOptionSize"
              :not-found-content="notFoundContentSize"
              @search="handleInputSize"
            />
          </a-form-item>

          <a-form-item
            class="label-bold"
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
                  :show-search="true"
                  :filter-option="filterOptionMaterial"
                  :not-found-content="notFoundContentMaterial"
                  @search="handleInputMaterial"
                ></a-select>
              </div>
              <div>
                <a-select
                  v-if="field.name === 'idCoAo'"
                  v-model:value="modelRef[field.name]"
                  placeholder="Chọn cổ áo"
                  :options="listCollar"
                  @change="generateProductDetails"
                  :show-search="true"
                  :filter-option="filterOptionCollar"
                  :not-found-content="notFoundContentCollar"
                  @search="handleInputCollar"
                ></a-select>
              </div>
              <div>
                <a-select
                  v-if="field.name === 'idTayAo'"
                  v-model:value="modelRef[field.name]"
                  placeholder="Chọn tay áo"
                  :options="listSleeve"
                  @change="generateProductDetails"
                  :show-search="true"
                  :filter-option="filterOptionSleeve"
                  :not-found-content="notFoundContentSleeve"
                  @search="handleInputSleeve"
                ></a-select>
              </div>
            </div>

            <a-select
              v-if="field.name === 'idHoaTiet'"
              v-model:value="modelRef[field.name]"
              placeholder="Chọn hoa tiết"
              :options="listPattern"
              @change="generateProductDetails"
              :show-search="true"
              :filter-option="filterOptionPattern"
              :not-found-content="notFoundContentPattern"
              @search="handleInputPattern"
              style="margin-right: 8px"
            ></a-select>
            <a-select
              v-if="field.name === 'idKieuDang'"
              v-model:value="modelRef[field.name]"
              placeholder="Chọn kiểu dáng"
              :options="listStyle"
              @change="generateProductDetails"
              :show-search="true"
              :filter-option="filterOptionStyle"
              :not-found-content="notFoundContentStyle"
              @search="handleInputStyle"
              style="margin-right: 8px"
            ></a-select>
            <a-select
              v-if="field.name === 'idThuongHieu'"
              v-model:value="modelRef[field.name]"
              placeholder="Chọn thương hiệu"
              :options="listTrademark"
              @change="generateProductDetails"
              :show-search="true"
              :filter-option="filterOptionTrademark"
              :not-found-content="notFoundContentTrademark"
              @search="handleInputTrademark"
              style="margin-right: 8px"
            ></a-select>
            <a-select
              v-if="field.name === 'idTinhNang'"
              v-model:value="modelRef[field.name]"
              placeholder="Chọn tính năng"
              :options="listFeature"
              @change="generateProductDetails"
              :show-search="true"
              :filter-option="filterOptionFeature"
              :not-found-content="notFoundContentFeature"
              @search="handleInputFeature"
              style="margin-right: 8px"
            ></a-select>
            <!-- <a-select
              v-if="field.name === 'idMauSac'"
              v-model:value="colorItem"
              mode="multiple"
              style="width: 100%"
              placeholder="Chọn màu sắc"
              @change="generateProductDetails"
              :options="listColor"
              :show-search="true"
              :filter-option="filterOptionColor"
              :not-found-content="notFoundContentColor"
              @search="handleInputColor"
            /> -->
            <!-- <a-select
              v-if="field.name === 'idKichCo'"
              v-model:value="sizeItem"
              mode="multiple"
              style="width: 100%"
              placeholder="Chọn kích cỡ"
              @change="generateProductDetails"
              :options="listSize"
              :show-search="true"
              :filter-option="filterOptionSize"
              :not-found-content="notFoundContentSize"
              @search="handleInputSize"
            /> -->
          </a-form-item>
        </template>
      </a-form>
      <modal-create-product
        :open="isOpenModalCreateProduct"
        @handleClose="handleCloseModalCreateProduct"
        @onCancel="isOpenModalCreateProduct = false"
      />
    </div>
    <div class="rounded-xl p-8 rounded-xl border-2">
      <div v-if="productDetails.length > 0" class="mb-5 label-bold">
        <h1>Bảng sản phẩm chi tiết</h1>
      </div>
      <!-- <product-detail-table
      :product="dataProduct"
          :material="listMaterial"
          :collar="listCollar"
          :trademark="listTrademark"
          :style="listStyle"
          :data-product-detail="productDetails" /> -->

      <product-table-detail
        :data-product-detail="productDetails"
        :products="dataProduct"
        :colors="listColor"
        :sizes="listSize"
        @updateDataProductDetail="handleUpdateDataProductDetail"
        @updatePrice="handleUpdatePrice"
        @updateQuantity="handleUpdateQuantity"
      />
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
  h,
  nextTick,
} from "vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { toast } from "vue3-toastify";
import { keepPreviousData } from "@tanstack/vue-query";
import { ProductRequest } from "@/infrastructure/services/api/admin/product.api";
import {
  ProductDetailRequest,
  RenProductDetailResponse,
} from "@/infrastructure/services/api/admin/product_detail.api";
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
  useGetListColor,
  useCreateColor,
} from "@/infrastructure/services/service/admin/color.action";
import {
  useGetListFeature,
  useCreateFeature,
} from "@/infrastructure/services/service/admin/feature.action";
import {
  useGetListPattern,
  useCreatePattern,
} from "@/infrastructure/services/service/admin/pattern.action";
import {
  useGetListSize,
  useCreateSize,
} from "@/infrastructure/services/service/admin/size.action";
import {
  useGetListSleeve,
  useCreateSleeve,
} from "@/infrastructure/services/service/admin/sleeve.action";
import {
  useGetListStyle,
  useCreateStyle,
} from "@/infrastructure/services/service/admin/style.action";
import {
  useGetListTrademark,
  useCreateTrademark,
} from "@/infrastructure/services/service/admin/trademark.action";
import ModalCreateProduct from "@/page/admin/product/product/ModalCreateProduct.vue";
import ProductDetailTable from "@/page/admin/product/product/ProductDetailTable.vue";
import ProductTableDetail from "@/page/admin/product/product/ProductTableDetail.vue";
import { Form, message, Modal, Upload } from "ant-design-vue";
import { PlusOutlined } from "@ant-design/icons-vue";
import { forEach } from "lodash";
import { errorNotiSort, successNotiSort } from "@/utils/notification.config";

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
  listAnh: [] || null,
});

const colorItem = ref([]);
const sizeItem = ref([]);

//Lấy list id và tên product fill lên select option
const { data: products } = useGetListProduct({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataProduct = computed(() => {
  return (
    products?.value?.data.map((product: any) => ({
      value: product.id,
      label: product.ten,
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
      maMauSac: color.maMauSac,
    })) || []
  );
});

//Tìm tên màu theo id
const getColorNameById = (id: string) => {
  // Tìm màu theo id
  const color = listColor.value.find((item) => item.value === id);
  return color ? color.label : "Màu không tìm thấy";
};

// lấy danh sách tính năng
const { data: features } = useGetListFeature({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listFeature = computed(() => {
  return (
    features?.value?.data.map((feature) => ({
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
    patterns?.value?.data.map((pattern) => ({
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
    sizes?.value?.data.map((size) => ({
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
    sleeves?.value?.data.map((sleeve) => ({
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
    styles?.value?.data.map((style) => ({
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
    trademarks?.value?.data.map((tra) => ({
      value: tra.id,
      label: tra.ten,
    })) || []
  );
});

// Thêm nhanh chất liệu
const filterOptionMaterial = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const tempValueMaterial = ref("");

const handleInputMaterial = (value: string) => {
  tempValueMaterial.value = value;
};

const notFoundContentMaterial = computed(() => {
  return h(
    "a",
    {
      style: "color: #1890ff; cursor: pointer",
      onClick: handleAddNewMaterial,
    },
    "Thêm chất liệu mới"
  );
});

const { mutate: createMaterial } = useCreateMaterial();

const handleAddNewMaterial = async () => {
  const payload = {
    ten: tempValueMaterial.value,
  };

  try {
    await createMaterial(payload);

    successNotiSort("Thêm chất liệu thành công!");
  } catch (error) {
    console.error(error);
    errorNotiSort("Có lỗi xảy ra khi thêm chất liệu!");
  }
};

// Thêm nhanh cổ áo
const filterOptionCollar = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const tempValueCollar = ref("");

const handleInputCollar = (value: string) => {
  tempValueCollar.value = value;
};

const notFoundContentCollar = computed(() => {
  return h(
    "a",
    {
      style: "color: #1890ff; cursor: pointer",
      onClick: handleAddNewCollar,
    },
    "Thêm cổ áo mới"
  );
});

const { mutate: createCollar } = useCreateCollar();

const handleAddNewCollar = async () => {
  const payload = {
    ten: tempValueCollar.value,
  };

  try {
    await createCollar(payload);

    successNotiSort("Thêm cổ áo thành công!");
  } catch (error) {
    console.error(error);
    errorNotiSort("Có lỗi xảy ra khi thêm cổ áo!");
  }
};

// Thêm nhanh tay áo
const filterOptionSleeve = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const tempValueSleeve = ref("");

const handleInputSleeve = (value: string) => {
  tempValueSleeve.value = value;
};

const notFoundContentSleeve = computed(() => {
  return h(
    "a",
    {
      style: "color: #1890ff; cursor: pointer",
      onClick: handleAddNewSleeve,
    },
    "Thêm tay áo mới"
  );
});

const { mutate: createSleeve } = useCreateSleeve();

const handleAddNewSleeve = async () => {
  const payload = {
    ten: tempValueSleeve.value,
  };

  try {
    await createSleeve(payload);

    successNotiSort("Thêm tay áo thành công!");
  } catch (error) {
    console.error(error);
    errorNotiSort("Có lỗi xảy ra khi thêm tay áo!");
  }
};

// Thêm nhanh thương hiệu
const filterOptionTrademark = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const tempValueTrademark = ref("");

const handleInputTrademark = (value: string) => {
  tempValueTrademark.value = value;
};

const notFoundContentTrademark = computed(() => {
  return h(
    "a",
    {
      style: "color: #1890ff; cursor: pointer",
      onClick: handleAddNewTrademark,
    },
    "Thêm thương hiệu mới"
  );
});

const { mutate: createTrademark } = useCreateTrademark();

const handleAddNewTrademark = async () => {
  const payload = {
    ten: tempValueTrademark.value,
  };

  try {
    await createTrademark(payload);

    successNotiSort("Thêm thương hiệu thành công!");
  } catch (error) {
    console.error(error);
    errorNotiSort("Có lỗi xảy ra khi thêm thương hiệu!");
  }
};

// Thêm nhanh họa tiết
const filterOptionPattern = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const tempValuePattern = ref("");

const handleInputPattern = (value: string) => {
  tempValuePattern.value = value;
};

const notFoundContentPattern = computed(() => {
  return h(
    "a",
    {
      style: "color: #1890ff; cursor: pointer",
      onClick: handleAddNewPattern,
    },
    "Thêm họa tiết mới"
  );
});

const { mutate: createPattern } = useCreatePattern();

const handleAddNewPattern = async () => {
  const payload = {
    ten: tempValuePattern.value,
  };

  try {
    await createPattern(payload);

    successNotiSort("Thêm họa tiết thành công!");
  } catch (error) {
    console.error(error);
    errorNotiSort("Có lỗi xảy ra khi thêm họa tiết!");
  }
};

// Thêm nhanh kiểu dáng
const filterOptionStyle = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const tempValueStyle = ref("");

const handleInputStyle = (value: string) => {
  tempValueStyle.value = value;
};

const notFoundContentStyle = computed(() => {
  return h(
    "a",
    {
      style: "color: #1890ff; cursor: pointer",
      onClick: handleAddNewStyle,
    },
    "Thêm kiểu dáng mới"
  );
});

const { mutate: createStyle } = useCreateStyle();

const handleAddNewStyle = async () => {
  const payload = {
    ten: tempValueStyle.value,
  };

  try {
    await createStyle(payload);

    successNotiSort("Thêm kiểu dáng thành công!");
  } catch (error) {
    console.error(error);
    errorNotiSort("Có lỗi xảy ra khi thêm kiểu dáng!");
  }
};

// Thêm nhanh tính năng
const filterOptionFeature = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const tempValueFeature = ref("");

const handleInputFeature = (value: string) => {
  tempValueFeature.value = value;
};

const notFoundContentFeature = computed(() => {
  return h(
    "a",
    {
      style: "color: #1890ff; cursor: pointer",
      onClick: handleAddNewFeature,
    },
    "Thêm tính năng mới"
  );
});

const { mutate: createFeature } = useCreateFeature();

const handleAddNewFeature = async () => {
  const payload = {
    ten: tempValueFeature.value,
  };

  try {
    await createFeature(payload);

    successNotiSort("Thêm tính năng thành công!");
  } catch (error) {
    console.error(error);
    errorNotiSort("Có lỗi xảy ra khi thêm tính năng!");
  }
};

// Thêm nhanh màu sắc
const filterOptionColor = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const tempValueColor = ref("");

const handleInputColor = (value: string) => {
  tempValueColor.value = value;
};

const notFoundContentColor = computed(() => {
  return h(
    "a",
    {
      style: "color: #1890ff; cursor: pointer",
      onClick: handleAddNewColor,
    },
    "Thêm màu sắc mới"
  );
});

const { mutate: createColor } = useCreateColor();

const handleAddNewColor = async () => {
  const payload = {
    ten: tempValueColor.value,
  };

  try {
    await createColor(payload);

    successNotiSort("Thêm màu sắc thành công!");
  } catch (error) {
    console.error(error);
    errorNotiSort("Có lỗi xảy ra khi thêm màu sắc!");
  }
};

// Thêm nhanh kích cỡ
const filterOptionSize = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const tempValueSize = ref("");

const handleInputSize = (value: string) => {
  tempValueSize.value = value;
};

const notFoundContentSize = computed(() => {
  return h(
    "a",
    {
      style: "color: #1890ff; cursor: pointer",
      onClick: handleAddNewSize,
    },
    "Thêm kích cỡ mới"
  );
});

const { mutate: createSize } = useCreateSize();

const handleAddNewSize = async () => {
  const payload = {
    ten: tempValueSize.value,
    chieuCaoMin: 0,
    chieuCaoMax: 0,
    canNangMin: 0,
    canNangMax: 0,
  };

  try {
    await createSize(payload);
    successNotiSort("Thêm kích cỡ thành công!");
  } catch (error) {
    console.error(error);
    errorNotiSort("Có lỗi xảy ra khi thêm kích cỡ!");
  }
};

// Khai báo emit
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

// // Theo dõi dữ liệu đổ vào select
// const options = computed(() => {
//   return dataProduct?.map((product: any) => ({
//     value: product.id,
//     label: product.ten,
//   }));
// });

watch(dataProduct, (newOptions) => {
  if (newOptions && newOptions.length > 0) {
    modelRef.idSanPham = newOptions[0].value;
  }
});

watch(listMaterial, (newList) => {
  if (newList && newList.length > 0) {
    modelRef.idChatLieu = newList[0].value;
  }
});

watch(listCollar, (newList) => {
  if (newList && newList.length > 0) {
    modelRef.idCoAo = newList[0].value;
  }
});

watch(listFeature, (newList) => {
  if (newList && newList.length > 0) {
    modelRef.idTinhNang = newList[0].value;
  }
});

watch(listPattern, (newList) => {
  if (newList && newList.length > 0) {
    modelRef.idHoaTiet = newList[0].value;
  }
});

watch(listSleeve, (newList) => {
  if (newList && newList.length > 0) {
    modelRef.idTayAo = newList[0].value;
  }
});

watch(listStyle, (newList) => {
  if (newList && newList.length > 0) {
    modelRef.idKieuDang = newList[0].value;
  }
});

watch(listTrademark, (newList) => {
  if (newList && newList.length > 0) {
    modelRef.idThuongHieu = newList[0].value;
  }
});

// const handleChange = (value: string) => {
//   console.log(modelRef);
// };

// const handleChangeColor = (value: string) => {
//   console.log(colorItem);
// };

// const handleChangeSize = (value: string) => {
//   console.log(sizeItem);
// };

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

// Ren ra các biến thể theo màu sắc. Mỗi màu sắc có nhiều biến thể kích thước khác nhau
const productDetails = ref<RenProductDetailResponse[]>([]);

const generateProductDetails = () => {
  let idCounter = 1;
  const generatedDetails: RenProductDetailResponse[] = [];
  colorItem.value.forEach((color) => {
    sizeItem.value.forEach((size) => {
      generatedDetails.push({
        id: `product-${idCounter++}`,
        trangThai: modelRef.trangThai,
        gia: modelRef.gia,
        soLuong: modelRef.soLuong,
        idChatLieu: modelRef.idChatLieu,
        idCoAo: modelRef.idCoAo,
        idHoaTiet: modelRef.idHoaTiet,
        idMauSac: color,
        idKichCo: size,
        idKieuDang: modelRef.idKieuDang,
        idTayAo: modelRef.idTayAo,
        idThuongHieu: modelRef.idThuongHieu,
        idTinhNang: modelRef.idTinhNang,
        idSanPham: modelRef.idSanPham,
      });
    });
  });
  productDetails.value = generatedDetails;
};

// Xóa phần tử trong dataProductDetail để cập nhật lại bảng khi xóa 1 biến thể

const handleUpdateDataProductDetail = (recordToDelete) => {
  productDetails.value = productDetails.value.filter(
    (item) => item.id !== recordToDelete.id
  );
};

const handleUpdatePrice = (dataSource) => {
  dataSource.forEach((updateItem) => {
    const product = productDetails.value.find(
      (item) => item.id === updateItem.id
    );
    if (product) {
      product.gia = updateItem.gia;
      // console.log(updateItem.gia);
    }
  });
};

const handleUpdateQuantity = (dataSource) => {
  dataSource.forEach((updateItem) => {
    const product = productDetails.value.find(
      (item) => item.id === updateItem.id
    );
    if (product) {
      product.soLuong = updateItem.soLuong;
      // console.log(updateItem.soLuong);
    }
  });
};

// watch (() => productDetails.value, (newValue) => console.log(newValue)
// )

// ---------------------------------------

const filterOption = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const filteredProductDetails = (color: string) => {
  return productDetails.value.filter((product) => product.idMauSac === color);
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

.label-bold {
  font-weight: bold;
}
</style>