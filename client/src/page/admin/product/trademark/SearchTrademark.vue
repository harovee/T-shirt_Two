<template>
  <a-form
      layout="vertical"
  >
    <a-form-item
        label="Tìm kiếm"
    >
      <a-input
          v-model:value="params.keyword"
          placeholder="Nhập tên thương hiệu"
          allowClear
          @change="onChangeInput('keyword' , $event)"
      />
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import {debounce} from "lodash";
import { defineEmits, ref, watch} from "vue";
import {FindTrademarkRequest, PropertyTrademarkParams} from "@/infrastructure/services/api/admin/trademark.api";

const emit = defineEmits([
  "filter"
]);


const params = ref<PropertyTrademarkParams>({
  page: 1,
  keyword: null
})

const debouncedEmit = debounce(() => {
  const payload = {...params.value};
  emit('filter', payload);
}, 100);

function onChangeFilter(key: keyof FindTrademarkRequest, value: any) {
  params.value[key] = value;
  debouncedEmit();
}

function onChangeInput(key: keyof FindTrademarkRequest, e: any) {
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