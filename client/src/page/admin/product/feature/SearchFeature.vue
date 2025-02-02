<template>
  <a-form
      layout="vertical"
  >
    <a-form-item
        label="Tìm kiếm"
    >
      <a-input
          v-model:value="params.keyword"
          placeholder="Nhập tên tính năng"
          allowClear
          @change="onChangeInput('keyword' , $event)"
      />
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import {debounce} from "lodash";
import { defineEmits, ref, watch} from "vue";
import {FindFeatureRequest, PropertyFeatureParams} from "@/infrastructure/services/api/admin/feature.api";

const emit = defineEmits([
  "filter"
]);


const params = ref<PropertyFeatureParams>({
  page: 1,
  keyword: null
})

const debouncedEmit = debounce(() => {
  const payload = {...params.value};
  emit('filter', payload);
}, 100);

function onChangeFilter(key: keyof FindFeatureRequest, value: any) {
  params.value[key] = value;
  debouncedEmit();
}

function onChangeInput(key: keyof FindFeatureRequest, e: any) {
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