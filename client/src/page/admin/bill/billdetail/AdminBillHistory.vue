<template>
  <step-history-bill 
  :data-source="dataHistory || {}"
  :loading="isHistoryLoading || isHistoryFetching"
   />
  
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from "vue";
import StepHistoryBill from "./StepHistoryBill.vue";
import { FindBillHistoryRequest } from "@/infrastructure/services/api/admin/billhistory.api";
import { useGetBillHistory } from "@/infrastructure/services/service/admin/billhistory.action";
import { keepPreviousData } from "@tanstack/vue-query";

const paramsHistory = ref<FindBillHistoryRequest>({
  page: 1,
  size: 10,
  idHoaDon: "",
});

const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

onMounted(() => {
  paramsHistory.value.idHoaDon = getIdHoaDonFromUrl();
});

const {
  data: historyData,
  isLoading: isHistoryLoading,
  isFetching: isHistoryFetching,
} = useGetBillHistory(paramsHistory, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataHistory = computed(() => historyData?.value)
// console.log(dataHistory);


// watch(
//   () => props.dataSource?.data?.data,
//   (newvalue) => console.log(newvalue)
// );
</script>
