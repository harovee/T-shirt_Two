<template>
  <a-form
      layout="vertical"
      class="grid grid-cols-4 gap-4 md:grid-cols-1 lg:grid-cols-3"
  >
    <a-form-item
        label="Tìm kiếm"
        class="col-span-3 md:col-span-3 lg:col-span-2"
    >
      <a-input
          v-model:value="params.keyword"
          placeholder="Nhập mã, tên, email, hoặc bất kỳ thông tin nhân viên"
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
</template>

<script setup lang="ts">
import {debounce} from "lodash";
import { defineEmits, ref, watch} from "vue";
import {FindSongRequest, PropertySongParams} from "@/infrastructure/services/api/admin/song.api.ts";

const emit = defineEmits([
  "filter"
]);


const params = ref<PropertySongParams>({
  page: 1,
  keyword: null,
  status: null,
  genre: undefined,
})

const statusOptions = [
  {label: "Tất cả", value: null},
  {label: "Hoạt động", value: 0},
  {label: "Vô hiệu hóa", value: 1}
]

const debouncedEmit = debounce(() => {
  const payload = {...params.value};
  if (Array.isArray(payload.genre)) {
    payload.genre = payload.genre.join(',');
  }
  emit('filter', payload);
}, 1000);

function onChangeFilter(key: keyof FindSongRequest, value: any) {
  params.value[key] = value;
  debouncedEmit();
}

function onChangeInput(key: keyof FindSongRequest, e: any) {
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