<template>
  <step-history-bill 
  :data-source="dataSource" />
  
</template>

<script lang="ts" setup>
import { watch } from "vue";
import StepHistoryBill from "./StepHistoryBill.vue";

const props = defineProps({
  dataSource: Array,
});

// watch(
//   () => props.dataSource?.data?.data,
//   (newvalue) => console.log(newvalue)
// );
</script>
