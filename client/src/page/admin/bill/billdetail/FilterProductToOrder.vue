<template>
  <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 10px;">
    <a-input
      v-model="params.keyword"
      @change="onChangeInput('keyword', $event)"
      placeholder="Nhập mã sản phẩm để tìm"
      style="margin-bottom: 5px"
    ></a-input>
    <a-tooltip title="Làm mới bộ lọc" trigger="hover">
      <a-button
        class="me-3 bg-purple-300 flex justify-between items-center gap-2"
        size="large"
        @click="resetFilter(); clearInputText()"
      >
        <v-icon name="ri-refresh-fill" style="font-size: 14px"></v-icon>
      </a-button>
    </a-tooltip>
  </div>
  <div>
    <a-space
      wrap
      style="width: 100%; display: flex; flex-wrap: wrap; gap: 8.3px"
    >
      <!-- Flexbox for wrapping and spacing -->
      <a-select
        v-for="filter in filters"
        :key="filter.key"
        v-model:value="params[filter.key]"
        :placeholder="filter.label"
        :options="filter.list"
        allowClear
        @change="onChangeFilter(filter.key, $event)"
        style="min-width: 118.5px; width: 100%"
      />
    </a-space>
  </div>
</template>

<script lang="ts" setup>
import { FindProductDetailRequest } from "@/infrastructure/services/api/admin/product_detail.api";
import { debounce } from "lodash";
import { inject, ref, watch } from "vue";

interface SelectOption {
  value: string | number | null;
  label: string;
}

const filters = ref([
  {
    key: "idChatLieu",
    label: "Chất liệu",
    list: inject<SelectOption[]>("listMaterial", []),
  },
  {
    key: "idCoAo",
    label: "Cổ áo",
    list: inject<SelectOption[]>("listCollar", []),
  },
  {
    key: "idTayAo",
    label: "Tay áo",
    list: inject<SelectOption[]>("listSleeve", []),
  },
  {
    key: "idThuongHieu",
    label: "Thương hiệu",
    list: inject<SelectOption[]>("listTrademark", []),
  },
  {
    key: "idMauSac",
    label: "Màu sắc",
    list: inject<SelectOption[]>("listColor", []),
  },
  {
    key: "idTinhNang",
    label: "Tính năng",
    list: inject<SelectOption[]>("listFeature", []),
  },
  {
    key: "idHoaTiet",
    label: "Họa tiết",
    list: inject<SelectOption[]>("listPattern", []),
  },
  {
    key: "idKichCo",
    label: "Kích cỡ",
    list: inject<SelectOption[]>("listSize", []),
  },
  {
    key: "idKieuDang",
    label: "Kiểu dáng",
    list: inject<SelectOption[]>("listStyle", []),
  },
]);

const emit = defineEmits(["filter"]);

const params = ref<Record<string, any>>({
  keyword: "",
});

const resetFilter = () => {
    (params.value.keyword = ""),
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
};

const clearInputText = () => {
  params.value.keyword = "";  // Clear the text in the input field
};


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

watch(
  params,
  () => {
    debouncedEmit();
  },
  { deep: true }
);
</script>
