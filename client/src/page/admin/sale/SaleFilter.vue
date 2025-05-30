<template>
  <a-form layout="vertical" class="grid grid-cols-4 gap-4 md:grid-cols-1 lg:grid-cols-3">
    <a-form-item label="Tìm kiếm" class="col-span-4 md:col-span-3 lg:col-span-1">
      <a-input v-model:value="params.keyword" placeholder="Nhập mã, tên đợt giảm giá" allowClear
        @change="onChangeInput('keyword', $event)" />
    </a-form-item>

    <a-form-item label="Loại giảm giá" class="col-span-4 md:col-span-3 lg:col-span-1">
      <a-select v-model:value="params.loai" @change="onChangeFilter('loai', $event)"
        placeholder="Chọn loại giảm giá" allowClear>
        <a-select-option v-for="option in loaiOptions" :key="option.value" :value="option.value">
          {{ option.label }}
        </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="Trạng thái" class="col-span-4 md:col-span-3 lg:col-span-1">
      <a-select v-model:value="params.trangThai" @change="onChangeFilter('trangThai', $event)"
        placeholder="Chọn trạng thái" allowClear>
        <a-select-option v-for="option in statusOptions" :key="option.value" :value="option.value">
          {{ option.label }}
        </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="Tìm trong khoảng thời gian" class="col-span-4 md:col-span-3 lg:col-span-1">
        <a-range-picker size="" style="" show-time format="HH:mm DD/MM/YYYY" :presets="rangePresets"
          :placeholder="['Ngày bắt đầu', 'Ngày kết thúc']"
          v-model:value="selectedDateRange"
          @change="onRangeChange" />
    </a-form-item>

  </a-form>

  <div class="flex justify-end">
      <a-tooltip title="Làm mới bộ lọc" trigger="hover">
          <a-button
            class="me-3 bg-purple-300 flex justify-between items-center gap-2"
            size="large"
            @click="removeSaleFiler()"
          >
            <v-icon name="ri-refresh-fill" style="font-size: 14px"></v-icon>
          </a-button>
        </a-tooltip>
  </div>
</template>

<script setup lang="ts">
import dayjs, { Dayjs } from 'dayjs';
import { debounce } from "lodash";
import { defineEmits, ref, watch } from "vue";
import { FindSaleRequest, PropertySaleParams } from "@/infrastructure/services/api/admin/sale.api.ts";
import { convertDateFormat } from "@/page/admin/sale/base/DefaultConfig";

const emit = defineEmits([
  "filter"
]);

const params = ref<PropertySaleParams>({
  page: 1,
  keyword: null,
  loai: null,
  trangThai: null,
  ngayBatDau: null,
  ngayKetThuc: null
})

type RangeValue = [Dayjs, Dayjs] | null;

const onRangeChange = (dates: RangeValue, dateStrings: string[]) => {
  if (dates) {
    selectedDateRange.value = dates;
    params.value['ngayBatDau'] = dayjs(convertDateFormat(dateStrings[0])).unix() * 1000;
    params.value['ngayKetThuc'] = dayjs(convertDateFormat(dateStrings[1])).unix() * 1000;
  } else {
    selectedDateRange.value = null;
    params.value['ngayBatDau'] = null;
    params.value['ngayKetThuc'] = null;
  }
  debouncedEmit();
};
const selectedDateRange = ref<RangeValue>(null);


const rangePresets = ref([
  {  label: 'Hôm qua', value: [dayjs().startOf('d').add(-1, 'd'), dayjs().endOf('d').add(-1, 'd')] },
  { label: '7 ngày trước', value: [dayjs().add(-7, 'd'), dayjs()] },
  { label: '14 ngày trước', value: [dayjs().add(-14, 'd'), dayjs()] },
  { label: '30 ngày trước', value: [dayjs().add(-30, 'd'), dayjs()] },
  { label: '90 ngày trước', value: [dayjs().add(-90, 'd'), dayjs()] },
]);


const statusOptions = [
  { label: "Tất cả", value: null },
  { label: "Đang diễn ra", value: 'IN_PROGRESS' },
  { label: "Chưa diễn ra", value: 'PENDING' },
  { label: "Đã kết thúc", value: 'FINISHED' },
  { label: "Bị vô hiệu hóa", value: 'INACTIVE' }
]

const loaiOptions = [
  { label: "Tất cả", value: null },
  { label: "Giảm theo phần trăm (%)", value: 'PERCENT' },
  { label: "Giảm theo số tiền (Vnd)", value: 'VND' },
]

const debouncedEmit = debounce(() => {
  const payload = { ...params.value };
  emit('filter', payload);
}, 1000);

function onChangeFilter(key: keyof FindSaleRequest, value: any) {
  params.value[key] = value;
  debouncedEmit();
}

function onChangeInput(key: keyof FindSaleRequest, e: any) {
  params.value[key] = e.target.value;
  debouncedEmit();
}

function removeSaleFiler() {
  params.value = {
    page: 1,
    keyword: null,
    loai: null,
    trangThai: null,
    ngayBatDau: null,
    ngayKetThuc: null
  }
  selectedDateRange.value = null;
}

watch(
  params,
  () => {
    debouncedEmit();
  },
  { deep: true }
);
</script>

<script lang="ts">
export default {
  name: "admin-sale-filter"
}
</script>

<style scoped></style>