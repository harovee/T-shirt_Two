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
          @change="onChangeInput('keyword' , $event)"
      />
    </a-form-item>

    <a-form-item
        label="Loại giảm"
        class="col-span-3 md:col-span-3 lg:col-span-1"
    >
      <a-select
          v-model:value="params.loaiGiam"
          @change="onChangeFilter('loaiGiam' , $event)"
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
          @change="onChangeFilter('trangThai' , $event)"
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
          format="DD-MM-YYYY"
          @change="onChangeDateRange"
      />
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import {debounce} from "lodash";
import { defineEmits, ref, watch} from "vue";
import {FindVoucherRequest, PropertyVoucherParams} from "@/infrastructure/services/api/admin/voucher/voucher.api";

const emit = defineEmits([
  "filter"
]);

const dateRange = ref<[string | null, string | null] | null>(null);

function onChangeDateRange(dates: [Date | null, Date | null] | null) {
  if (dates) {
    params.value.startDate = dates[0] ? dates[0].valueOf() : null;
    params.value.endDate = dates[1] ? dates[1].valueOf() : null;
  } else {
    // Nếu không có ngày nào được chọn
    params.value.startDate = null;
    params.value.endDate = null;
  }
  debouncedEmit(); // Gửi sự kiện để cập nhật dữ liệu
}


const params = ref<PropertyVoucherParams>({
  page: 1,
  keyword: null,
  startDate: null,
  endDate: null,
  loaiGiam: null,
  trangThai: null,
})

const loaiGiamOptions = [
  {label: "Tất cả", value: null},
  {label: "%", value: 0},
  {label: "Tiền", value: 1}
]

const trangThaiOptions = [
  {label: "Tất cả", value: null},
  {label: "Đang áp dụng", value: "IN_PROGRESS"},
  {label: "Sắp diễn ra", value: "NOT_STARTED"},
  {label: "Hết hạn", value: "EXPIRED"}
]

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