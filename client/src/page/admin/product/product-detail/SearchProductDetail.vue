<template>
  <div class="shadow-md p-3 rounded-md m-3">
    <div class="d-flex justify-content-between align-items-center p-4">
    <h2 class="d-flex align-items-center text-primary text-3xl font-semibold">
      <v-icon name="co-filter" scale="2" />
      <span class="ml-2">Bộ lọc</span>
    </h2>
    
  </div>
    <a-form
      layout="vertical"
      class="grid grid-cols-5 gap-4 md:grid-cols-1 lg:grid-cols-5"
    >
      <!-- Select option trạng thái -->
      <a-form-item
        label="Trạng thái"
        class="col-span-1 md:col-span-1 lg:col-span-1"
      >
        <a-select
          v-model:value="params.trangThai"
          @change="onChangeFilter('status', $event)"
          placeholder="Chọn trạng thái"
          allowClear
        >
          <a-select-option
            v-for="option in statusOptions"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <!-- Select option chất liệu -->
      <a-form-item
        label="Chất liệu"
        class="col-span-1 md:col-span-1 lg:col-span-1"
      >
        <a-select
          v-model:value="params.idChatLieu"
          @change="onChangeFilter('idChatLieu', $event)"
          placeholder="Chọn chất liệu"
          allowClear
        >
          <a-select-option :value="null"> Tất cả </a-select-option>
          <a-select-option
            v-for="option in listMaterial"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <!-- Select option thương hiệu -->
      <a-form-item
        label="Thương hiệu"
        class="col-span-1 md:col-span-1 lg:col-span-1"
      >
        <a-select
          v-model:value="params.idThuongHieu"
          @change="onChangeFilter('idThuongHieu', $event)"
          placeholder="Chọn thương hiệu"
          allowClear
        >
          <a-select-option :value="null"> Tất cả </a-select-option>
          <a-select-option
            v-for="option in listTrademark"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <!-- Select option cổ áo -->
      <a-form-item label="Cổ áo" class="col-span-1 md:col-span-1 lg:col-span-1">
        <a-select
          v-model:value="params.idCoAo"
          @change="onChangeFilter('idCoAo', $event)"
          placeholder="Chọn cổ áo"
          allowClear
        >
          <a-select-option :value="null"> Tất cả </a-select-option>
          <a-select-option
            v-for="option in listCollar"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <!-- Select option tay áo -->
      <a-form-item
        label="Tay áo"
        class="col-span-1 md:col-span-1 lg:col-span-1"
      >
        <a-select
          v-model:value="params.idTayAo"
          @change="onChangeFilter('idTayAo', $event)"
          placeholder="Chọn tay áo"
          allowClear
        >
          <a-select-option :value="null"> Tất cả </a-select-option>
          <a-select-option
            v-for="option in listSleeve"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <!-- Select option kiểu dáng -->
      <a-form-item
        label="Kiểu dáng"
        class="col-span-1 md:col-span-1 lg:col-span-1"
      >
        <a-select
          v-model:value="params.idKieuDang"
          @change="onChangeFilter('idKieuDang', $event)"
          placeholder="Chọn kiểu dáng"
          allowClear
        >
          <a-select-option :value="null"> Tất cả </a-select-option>
          <a-select-option
            v-for="option in listStyle"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <!-- Select option họa tiết -->
      <a-form-item
        label="Họa tiết"
        class="col-span-1 md:col-span-1 lg:col-span-1"
      >
        <a-select
          v-model:value="params.idHoaTiet"
          @change="onChangeFilter('idHoaTiet', $event)"
          placeholder="Chọn họa tiết"
          allowClear
        >
          <a-select-option :value="null"> Tất cả </a-select-option>
          <a-select-option
            v-for="option in listPattern"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <!-- Select option tính năng -->
      <a-form-item
        label="Tính năng"
        class="col-span-1 md:col-span-1 lg:col-span-1"
      >
        <a-select
          v-model:value="params.idTinhNang"
          @change="onChangeFilter('idTinhNang', $event)"
          placeholder="Chọn tính năng"
          allowClear
        >
          <a-select-option :value="null"> Tất cả </a-select-option>
          <a-select-option
            v-for="option in listFeature"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <!-- Select option màu sắc -->
      <a-form-item
        label="Màu sắc"
        class="col-span-1 md:col-span-1 lg:col-span-1"
      >
        <a-select
          v-model:value="params.idMauSac"
          @change="onChangeFilter('idMauSac', $event)"
          placeholder="Chọn màu sắc"
          allowClear
        >
          <a-select-option :value="null"> Tất cả </a-select-option>
          <a-select-option
            v-for="option in listColor"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <!-- Select option kích cỡ -->
      <a-form-item
        label="Kích cỡ"
        class="col-span-1 md:col-span-1 lg:col-span-1"
      >
        <a-select
          v-model:value="params.idKichCo"
          @change="onChangeFilter('idKichCo', $event)"
          placeholder="Chọn kích cỡ"
          allowClear
        >
          <a-select-option :value="null"> Tất cả </a-select-option>
          <a-select-option
            v-for="option in listSize"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="Giá" class="col-span-1">
        <a-slider
          v-model:value="params.gia"
          @change="onChangeFilter('gia', $event)"
          :min="null"
          :max="10000000"
        />
        <span v-if="params.gia !== undefined || params.gia === '0'">{{
          formatCurrency(params.gia)
        }}</span>
      </a-form-item>
    </a-form>
    <div>
    <a-button @click="resetFilter" type="primary">
      Làm mới
    </a-button>
  </div>
  </div>
</template>

<script setup lang="ts">
import { debounce } from "lodash";
import { defineEmits, ref, watch, computed, inject } from "vue";
import {
  FindProductDetailRequest,
  PropertyProductDetailParams,
} from "@/infrastructure/services/api/admin/product_detail.api";
import { keepPreviousData } from "@tanstack/vue-query";

const listMaterial = inject('listMaterial');
const listCollar = inject('listCollar');
const listSleeve = inject('listSleeve');
const listTrademark = inject('listTrademark');
const listColor = inject('listColor');
const listFeature = inject('listFeature');
const listPattern = inject('listPattern');
const listSize = inject('listSize');
const listStyle = inject('listStyle');

const emit = defineEmits(["filter"]);

const params = ref<PropertyProductDetailParams>({
  page: 1,
  trangThai: null,
  idChatLieu: null,
  idCoAo: null,
  idHoaTiet: null,
  idKichCo: null,
  idKieuDang: null,
  idMauSac: null,
  idTayAo: null,
  idThuongHieu: null,
  idTinhNang: null
});

const statusOptions = [
  { label: "Tất cả", value: null },
  { label: "Đang áp dụng", value: 0 },
  { label: "Ngưng áp dụng", value: 1 },
];

const debouncedEmit = debounce(() => {
  const payload = { ...params.value };
  emit("filter", payload);
}, 100);

function onChangeFilter(key: keyof FindProductDetailRequest, value: any) {
  params.value[key] = value;
  debouncedEmit();
}

function onChangeInput(key: keyof FindProductDetailRequest, e: any) {
  params.value[key] = e.target.value;
  debouncedEmit();
}

const formatCurrency = (number) => {
  // Kiểm tra nếu số là một giá trị hợp lệ
  if (isNaN(number) || number == null) {
    return "";
  }

  // Chuyển số thành chuỗi và thêm dấu phân cách hàng nghìn
  return number.toLocaleString() + " VNĐ";
};

const resetFilter = () => {
  params.value.trangThai = null,
  params.value.idChatLieu = null,
  params.value.idCoAo = null,
  params.value.idHoaTiet = null,
  params.value.idKichCo = null,
  params.value.idKieuDang = null,
  params.value.idMauSac = null,
  params.value.idTayAo = null,
  params.value.idThuongHieu = null,
  params.value.idTinhNang = null,
  params.value.gia = null
}

watch(
  params,
  () => {
    debouncedEmit();
  },
  { deep: true }
);

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