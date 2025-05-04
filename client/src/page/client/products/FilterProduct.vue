<template>
  <div>
    <h1 class="text-base font-bold">Bạn đã chọn</h1>
    <div class="flex flex-wrap gap-2 mb-4 max-w-full">
      <!-- Hiển thị các filter đã chọn với độ dài giới hạn -->
      <template
        v-for="(value, category) in selectedFiltersState"
        :key="category"
      >
        <a-tag
          v-if="value && category !== 'khoangGia'"
          closable
          @close="removeFilter(category)"
          class="truncate-tag"
          :title="value"
        >
          {{ truncateText(value) }}
        </a-tag>
      </template>
      <!-- Hiển thị khoảng giá đã chọn -->
      <a-tag
        v-if="priceRange[0] > 0 || priceRange[1] < maxPrice"
        closable
        @close="resetPriceRange"
        class="truncate-tag"
      >
        {{ formatCurrency(priceRange[0]) }} -
        {{ formatCurrency(priceRange[1]) }}
      </a-tag>
    </div>
    <hr />

    <!-- Khoảng giá luôn hiển thị -->
    <div class="mt-4 mb-4">
      <h2 class="text-base font-medium mb-2">Khoảng giá</h2>
      <div class="price-range-container">
        <div class="price-range-values">
          <span>{{ formatCurrency(priceRange[0]) }}</span>
          <span>{{ formatCurrency(priceRange[1]) }}</span>
        </div>
        <a-slider
          v-model:value="priceRange"
          range
          :min="0"
          :max="maxPrice"
          :step="10000"
          @change="handlePriceChange"
        />
      </div>
    </div>
    <hr />

    <!-- Panel cho Danh mục -->
    <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
      <a-collapse-panel key="danhmuc" header="Danh mục">
        <div class="checkbox-group">
          <!-- <a-checkbox
            v-for="dm in listDanhMuc"
            :key="dm.id"
            :checked="selectedFiltersState.tenDanhMuc === dm.ten"
            @change="(e) => handleCheckboxChange('tenDanhMuc', e, dm.ten)"
          >
            <span :title="dm.ten" class="truncate-text">
              {{ truncateText(dm.ten) }}
            </span>
          </a-checkbox> -->
          <a-radio-group v-model:value="selectedFiltersState.tenDanhMuc">
            <a-radio
              v-for="dm in listDanhMuc"
              :key="dm.id"
              :value="dm.ten"
              class="custom-radio radio-item"
            >
              <span :title="dm.ten" class="truncate-text">
                {{ truncateText(dm.ten) }}
              </span>
            </a-radio>
          </a-radio-group>
        </div>
      </a-collapse-panel>
    </a-collapse>

    <!-- Panel cho Kiểu dáng -->
    <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
      <a-collapse-panel key="kieudang" header="Kiểu dáng">
        <div class="checkbox-group">
          <!-- <a-checkbox
            v-for="kd in listKieuDang"
            :key="kd.id"
            :checked="selectedFiltersState.tenKieuDang === kd.ten"
            @change="(e) => handleCheckboxChange('tenKieuDang', e, kd.ten)"
          >
            <span :title="kd.ten" class="truncate-text">
              {{ truncateText(kd.ten) }}
            </span>
          </a-checkbox> -->
          <a-radio-group v-model:value="selectedFiltersState.tenKieuDang">
            <a-radio
              v-for="kd in listKieuDang"
              :key="kd.id"
              :value="kd.ten"
              class="custom-radio radio-item"
            >
              <span :title="kd.ten" class="truncate-text">
                {{ truncateText(kd.ten) }}
              </span>
            </a-radio>
          </a-radio-group>
        </div>
      </a-collapse-panel>
    </a-collapse>

    <!-- Panel cho Chất liệu -->
    <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
      <a-collapse-panel key="chatlieu" header="Chất liệu">
        <div class="checkbox-group">
          <!-- <a-checkbox
            v-for="cl in listChatLieu"
            :key="cl.id"
            :checked="selectedFiltersState.tenChatLieu === cl.ten"
            @change="(e) => handleCheckboxChange('tenChatLieu', e, cl.ten)"
          >
            <span :title="cl.ten" class="truncate-text">
              {{ truncateText(cl.ten) }}
            </span>
          </a-checkbox> -->
          <a-radio-group v-model:value="selectedFiltersState.tenChatLieu">
            <a-radio
              v-for="cl in listChatLieu"
              :key="cl.id"
              :value="cl.ten"
              class="custom-radio radio-item"
            >
              <span :title="cl.ten" class="truncate-text">
                {{ truncateText(cl.ten) }}
              </span>
            </a-radio>
          </a-radio-group>
        </div>
      </a-collapse-panel>
    </a-collapse>

    <!-- Panel cho Thương hiệu -->
    <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
      <a-collapse-panel key="thuonghieu" header="Thương hiệu">
        <div class="checkbox-group">
          <!-- <a-checkbox
            v-for="th in listThuongHieu"
            :key="th.id"
            :checked="selectedFiltersState.tenThuongHieu === th.ten"
            @change="(e) => handleCheckboxChange('tenThuongHieu', e, th.ten)"
          >
            <span :title="th.ten" class="truncate-text">
              {{ truncateText(th.ten) }}
            </span>
          </a-checkbox> -->
          <a-radio-group v-model:value="selectedFiltersState.tenThuongHieu">
            <a-radio
              v-for="th in listThuongHieu"
              :key="th.id"
              :value="th.ten"
              class="custom-radio radio-item"
            >
              <span :title="th.ten" class="truncate-text">
                {{ truncateText(th.ten) }}
              </span>
            </a-radio>
          </a-radio-group>
        </div>
      </a-collapse-panel>
    </a-collapse>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, reactive, watch } from "vue";
import {
  useGetChatLieu,
  useGetColor,
  useGetDanhMuc,
  useGetKieuDang,
  useGetThuongHieu,
} from "@/infrastructure/services/service/client/productclient.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { FindProductClientRequest } from "@/infrastructure/services/api/client/clientproduct.api";

const emit = defineEmits(["filter"]);

const selectedFiltersState = reactive({
  tenDanhMuc: "",
  tenKieuDang: "",
  tenChatLieu: "",
  tenThuongHieu: "",
});

const maxPrice = 20000000;
const priceRange = ref([0, maxPrice]);

const params = ref<FindProductClientRequest>({
  page: 1,
  size: 20,
  tenSanPham: "",
  tenDanhMuc: "",
  tenChatLieu: "",
  tenKieuDang: "",
  tenThuongHieu: "",
  min: 0,
  max: null,
});

const { data: chatLieu } = useGetChatLieu({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});
const { data: danhMuc } = useGetDanhMuc({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});
const { data: thuongHieu } = useGetThuongHieu({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});
const { data: kieuDang } = useGetKieuDang({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});
const { data: color } = useGetColor({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listChatLieu = computed(() => chatLieu?.value?.data || []);
const listDanhMuc = computed(() => danhMuc?.value?.data || []);
const listThuongHieu = computed(() => thuongHieu?.value?.data || []);
const listKieuDang = computed(() => kieuDang?.value?.data || []);
const listColor = computed(() => color?.value?.data || []);

// Hàm để cắt bớt text khi dài hơn 15 ký tự
function truncateText(text: string): string {
  return text && text.length > 15 ? text.substring(0, 15) + "..." : text;
}

// Hàm định dạng tiền tệ
function formatCurrency(value: number): string {
  return new Intl.NumberFormat("vi-VN").format(value);
}

// Xử lý khi thay đổi khoảng giá
function handlePriceChange(value: [number, number]) {
  params.value.min = value[0];
  params.value.max = value[1] === maxPrice ? null : value[1];

  emit("filter", params.value);
}

// Reset khoảng giá về mặc định
function resetPriceRange() {
  priceRange.value = [0, maxPrice];
  params.value.min = 0;
  params.value.max = null;
  emit("filter", params.value);
}

// Xử lý checkbox chỉ chọn một giá trị
function handleCheckboxChange(category: string, e: any, value: string) {
  if (e.target.checked) {
    selectedFiltersState[category] = value;
  } else {
    selectedFiltersState[category] = "";
  }
}

watch(
  selectedFiltersState,
  () => {
    const filterMapping = {
      tenDanhMuc: "tenDanhMuc",
      tenKieuDang: "tenKieuDang",
      tenChatLieu: "tenChatLieu",
      tenThuongHieu: "tenThuongHieu",
    };

    Object.keys(filterMapping).forEach((stateKey) => {
      const paramKey = filterMapping[stateKey];
      params.value[paramKey] = selectedFiltersState[stateKey] || "";
    });

    emit("filter", params.value);
  },
  { deep: true }
);

function removeFilter(category: string) {
  selectedFiltersState[category] = "";
}

const activeKey = ref(["danhmuc"]); // Changed default active key to danhmuc since khoanggia is now always visible
</script>

<style scoped>
.truncate-tag {
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.truncate-text {
  display: inline-block;
  max-width: 150px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.price-range-container {
  padding: 0 8px;
}

.price-range-values {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.radio-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px; /* khoảng cách giữa các radio-item */
}

.radio-item:last-child {
  margin-bottom: 0;
}

.radio-item .ant-radio {
  margin-right: 10px; /* đảm bảo có khoảng cách giữa nút radio và text */
}

.truncate-text {
  max-width: 150px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

:deep(.ant-slider-track) {
  background-color: #f5222d;
}

:deep(.ant-slider-handle) {
  border-color: #f5222d;
}

:deep(.ant-slider-handle:focus) {
  box-shadow: 0 0 0 5px rgba(245, 34, 45, 0.12);
}

:deep(.ant-slider:hover .ant-slider-track) {
  background-color: #ff4d4f;
}

:deep(.ant-slider:hover .ant-slider-handle) {
  border-color: #ff4d4f;
}
</style>