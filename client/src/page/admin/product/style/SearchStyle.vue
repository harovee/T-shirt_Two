<template>
  <div class="shadow-md p-3 rounded-md m-3">
    <h2 class="p-4 d-flex align-items-center text-primary text-3xl font-semibold">
      <v-icon name="co-filter" scale="2"/>
      <span class="ml-2">Bộ lọc</span>
    </h2>
    <a-form
      layout="vertical"
  >
    <a-form-item
        label="Tìm kiếm"
    >
      <a-input
          v-model:value="params.keyword"
          placeholder="Nhập tên kiểu dáng"
          allowClear
          @change="onChangeInput('keyword' , $event)"
      />
    </a-form-item>
  </a-form>
  </div>
</template>

<script setup lang="ts">
import {debounce} from "lodash";
import { defineEmits, ref, watch} from "vue";
import {FindStyleRequest, PropertyStyleParams} from "@/infrastructure/services/api/admin/style.api";

const emit = defineEmits([
  "filter"
]);


const params = ref<PropertyStyleParams>({
  page: 1,
  keyword: null
})

const debouncedEmit = debounce(() => {
  const payload = {...params.value};
  emit('filter', payload);
}, 100);

function onChangeFilter(key: keyof FindStyleRequest, value: any) {
  params.value[key] = value;
  debouncedEmit();
}

function onChangeInput(key: keyof FindStyleRequest, e: any) {
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