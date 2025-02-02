<template>
  <a-form
      layout="vertical"
  >
    <a-form-item
        label="Tìm kiếm"
    >
      <a-input
          v-model:value="params.keyword"
          placeholder="Nhập tên chất liệu"
          allowClear
          @change="onChangeInput('keyword' , $event)"
      />
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import {debounce} from "lodash";
import { defineEmits, ref, watch} from "vue";
import {FindMaterialRequest, PropertyMaterialParams} from "@/infrastructure/services/api/admin/material.api";

const emit = defineEmits([
  "filter"
]);


const params = ref<PropertyMaterialParams>({
  page: 1,
  keyword: null
})

const debouncedEmit = debounce(() => {
  const payload = {...params.value};
  emit('filter', payload);
}, 100);

function onChangeFilter(key: keyof FindMaterialRequest, value: any) {
  params.value[key] = value;
  debouncedEmit();
}

function onChangeInput(key: keyof FindMaterialRequest, e: any) {
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