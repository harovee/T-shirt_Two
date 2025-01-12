<template>
  <div class="shadow-md p-3 rounded-md m-3">
    <h2 class="p-4 d-flex align-items-center text-primary text-3xl font-semibold">
      <v-icon name="co-filter" scale="2"/>
      <span class="ml-2">Bộ lọc</span>
    </h2>
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
          placeholder="Nhập tên sản phẩm"
          allowClear
          @change="onChangeInput('keyword' , $event)"
      />
    </a-form-item>

    <a-form-item
        label="Trạng thái"
        class="col-span-3 md:col-span-3 lg:col-span-1"
    >
      <a-select
          v-model:value="params.status"
          @change="onChangeFilter('status' , $event)"
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
  </a-form>
  </div>
</template>

<script setup lang="ts">
import {debounce} from "lodash";
import { defineEmits, ref, watch} from "vue";
import {FindProductRequest, PropertyProductParams} from "@/infrastructure/services/api/admin/product.api";

const emit = defineEmits([
  "filter"
]);


const params = ref<PropertyProductParams>({
  page: 1,
  keyword: null,
  status: null,
})

const statusOptions = [
  {label: "Tất cả", value: null},
  {label: "Đang áp dụng", value: 0},
  {label: "Ngưng áp dụng", value: 1}
]

const debouncedEmit = debounce(() => {
  const payload = {...params.value};
  emit('filter', payload);
}, 100);

function onChangeFilter(key: keyof FindProductRequest, value: any) {
  params.value[key] = value;
  debouncedEmit();
}

function onChangeInput(key: keyof FindProductRequest, e: any) {
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