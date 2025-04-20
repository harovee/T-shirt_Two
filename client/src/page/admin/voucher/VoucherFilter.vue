<template>
  <a-form
      layout="vertical"
      class="grid grid-cols-4 gap-4 md:grid-cols-1 lg:grid-cols-3"
  >
    <a-form-item
        label="Tìm kiếm"
        class="col-span-3 md:col-span-3 lg:col-span-1"
    >
      <a-input
          v-model:value="params.keyword"
          placeholder="Tìm kiếm theo mã hoặc tên"
          allowClear
          @change="onChangeInput('keyword', $event)"
      />
    </a-form-item>

    <a-form-item
        label="Loại giảm"
        class="col-span-3 md:col-span-3 lg:col-span-1"
    >
      <a-select
          v-model:value="params.loaiGiam"
          @change="onChangeFilter('loaiGiam', $event)"
          placeholder="Chọn loại giảm"
          allowClear
      >
        <a-select-option
            v-for="option in loaiGiamOptions"
            :key="option.value"
            :value="option.value"
        >
          {{ option.label }}
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item
        label="Trạng thái"
        class="col-span-3 md:col-span-3 lg:col-span-1"
    >
      <a-select
          v-model:value="params.trangThai"
          @change="onChangeFilter('trangThai', $event)"
          placeholder="Chọn trạng thái"
          allowClear
      >
        <a-select-option
            v-for="option in trangThaiOptions"
            :key="option.value"
            :value="option.value"
        >
          {{ option.label }}
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item
        label="Khoảng thời gian"
        class="col-span-3 md:col-span-3 lg:col-span-1"
    >
      <a-range-picker
          v-model:value="dateRange"
          format="hh:ss DD-MM-YYYY"
          @change="onChangeDateRange"
      />
    </a-form-item>
  </a-form>
  <div class="flex justify-end">
      <a-tooltip title="Làm mới bộ lọc" trigger="hover">
          <a-button
            class="me-3 bg-purple-300 flex justify-between items-center gap-2"
            size="large"
            @click="refreshFilters()"
          >
            <v-icon name="ri-refresh-fill" style="font-size: 14px"></v-icon>
          </a-button>
        </a-tooltip>
  </div>
</template>

<script setup lang="ts">
import {debounce} from "lodash";
import { defineEmits, ref, watch} from "vue";
import {FindVoucherRequest, PropertyVoucherParams} from "@/infrastructure/services/api/admin/voucher/voucher.api";
import dayjs from 'dayjs';
import { ReloadOutlined } from '@ant-design/icons-vue';

const emit = defineEmits([
  "filter"
]);

const dateRange = ref<[string | null, string | null] | null>(null);

function onChangeDateRange(dates: [dayjs.Dayjs | null, dayjs.Dayjs | null] | null) {
  if (dates && dates[0] && dates[1]) {
    params.value.startDate = dates[0].valueOf();
    params.value.endDate = dates[1].valueOf();
  } else {
    params.value.startDate = null;
    params.value.endDate = null;
  }
  debouncedEmit();
}

const defaultParams: PropertyVoucherParams = {
  page: 1,
  keyword: null,
  startDate: null,
  endDate: null,
  loaiGiam: null,
  trangThai: null,
};

const params = ref<PropertyVoucherParams>({...defaultParams});

const loaiGiamOptions = [
  {label: "Tất cả", value: null},
  {label: "%", value: 0},
  {label: "Tiền", value: 1}
];

const trangThaiOptions = [
  {label: "Tất cả", value: null},
  {label: "Đang áp dụng", value: "IN_PROGRESS"},
  {label: "Sắp diễn ra", value: "NOT_STARTED"},
  {label: "Hết hạn", value: "EXPIRED"}
];

const debouncedEmit = debounce(() => {
  const payload = {...params.value};
  if (Array.isArray(payload.genre)) {
    payload.genre = payload.genre.join(',');
  }
  emit('filter', payload);
}, 1000);

function onChangeFilter(key: keyof FindVoucherRequest, value: any) {
  if (key === 'startDate' || key === 'endDate') {
    params.value[key] = value ? value.valueOf() : null;
  } else {
    params.value[key] = value;
  }
  debouncedEmit();
}

function onChangeInput(key: keyof FindVoucherRequest, e: any) {
  params.value[key] = e.target.value;
  debouncedEmit();
}

function refreshFilters() {
  params.value = {...defaultParams};
  dateRange.value = null;
  debouncedEmit();
}

watch(
    params,
    () => {
      debouncedEmit();
    },
    {deep: true}
);
</script>

<style scoped>
</style>