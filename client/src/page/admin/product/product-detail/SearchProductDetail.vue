<template>
  <div>
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
    <div class="flex justify-end">
      <a-tooltip title="Làm mới bộ lọc" trigger="hover">
          <a-button
            class="me-3 bg-purple-300 flex justify-between items-center gap-2"
            size="large"
            @click="resetFilter"
          >
            <v-icon name="ri-refresh-fill" style="font-size: 14px"></v-icon>
          </a-button>
        </a-tooltip>
      <a-tooltip :title="changeProductDetail ? 'Sản phẩm đơn lẻ' : 'Toàn bộ sản phẩm'" trigger="hover">
          <a-button
            class="me-3 bg-purple-300 flex justify-between items-center gap-2"
            size="large"
            @click="fetchAllProductDetail"
          >
            <v-icon name="ri-exchange-box-fill" style="font-size: 14px"></v-icon>
          </a-button>
        </a-tooltip>
        <a-tooltip
            title="Quét QR sản phẩm"
            trigger="hover"
        >
          <a-button
              class="me-3 bg-purple-300 flex justify-between items-center gap-2"
              size="large"
              @click="openQRModal"
          >
            <v-icon name="bi-qr-code-scan"/>
          </a-button>
        </a-tooltip>
        <scan-qr-code
            :openModal="isModalOpen"
            @update:open="isModalOpen = $event"
            @update:idSanPhamChitiet="handleUpdateIdSanPhamChiTietQr"
            @ok="handleQRScan"
          />
      <a-tooltip title="Xuất Excel" trigger="hover">
          <a-button
            class="bg-purple-300 flex justify-between items-center gap-2"
            size="large"
            @click="exportToExcel"
          >
            <v-icon name="bi-file-earmark-excel-fill" style="font-size: 14px"></v-icon>
          </a-button>
        </a-tooltip>
      <!-- <a-button @click="resetFilter" class="me-3 bg-purple-300 flex justify-between items-center gap-2"> Làm mới </a-button>
      <a-button @click="fetchAllProductDetail" class="me-3  bg-purple-300 flex justify-between items-center gap-2">
        {{changeProductDetail ? "Đơn lẻ" : "Toàn bộ"}}
      </a-button>
      <a-button @click="exportToExcel" class="bg-purple-300 flex justify-between items-center gap-2">Xuất Excel</a-button> -->
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
import { FilePdfOutlined, FileExcelOutlined } from "@ant-design/icons-vue";
import ScanQrCode from "@/page/admin/point.of.sale/qr-code/ScanQrCode.vue";

const listMaterial = inject("listMaterial");
const listCollar = inject("listCollar");
const listSleeve = inject("listSleeve");
const listTrademark = inject("listTrademark");
const listColor = inject("listColor");
const listFeature = inject("listFeature");
const listPattern = inject("listPattern");
const listSize = inject("listSize");
const listStyle = inject("listStyle");

const emit = defineEmits(["filter", "fetch-all-product-detail", "export-to-excel"]);

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
  idTinhNang: null,
  keyword: null
});

const isModalOpen = ref(false);

// Mở modal quét mã QR khi nhấn nút
const openQRModal = () => {
  isModalOpen.value = true;
};

// Xử lý mã QR khi quét thành công
const handleQRScan = (qrCode: string) => {
  console.log("Mã QR quét được:", qrCode);
};

const handleUpdateIdSanPhamChiTietQr = (newId: string) => {
  console.log(newId);
  params.value.keyword = newId;
};

const changeProductDetail = ref(false);

const statusOptions = [
  { label: "Tất cả", value: null },
  { label: "Đang bán", value: 0 },
  { label: "Ngừng kinh doanh", value: 1 },
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

function fetchAllProductDetail() {
  changeProductDetail.value = !changeProductDetail.value;
  emit("fetch-all-product-detail", changeProductDetail.value);
}

function exportToExcel() {
  emit("export-to-excel");
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
  (params.value.trangThai = null),
    (params.value.idChatLieu = null),
    (params.value.idCoAo = null),
    (params.value.idHoaTiet = null),
    (params.value.idKichCo = null),
    (params.value.idKieuDang = null),
    (params.value.idMauSac = null),
    (params.value.idTayAo = null),
    (params.value.idThuongHieu = null),
    (params.value.idTinhNang = null),
    (params.value.gia = null);
    (params.value.keyword = null);
};

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