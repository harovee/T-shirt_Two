<template>
  <step-history-bill-copy 
  :data-source="dataHistory || {}"
  :loading="isHistoryLoading || isHistoryFetching"
  :data-payment-info="dataPaymentInfo"
  :data-product="dataProduct"
  :bill-data="billData"
  @update:bill="handleUpdateBill"
   />
</template>

<script lang="ts" setup>
import { computed, onMounted, watch, ref } from "vue";
import  StepHistoryBill from "./StepHistoryBill.vue";
import  StepHistoryBillCopy from "./StepHistoryBillCopy.vue";
import { FindBillHistoryRequest } from "@/infrastructure/services/api/admin/billhistory.api";
import { useGetBillHistory } from "@/infrastructure/services/service/admin/billhistory.action";
import { keepPreviousData } from "@tanstack/vue-query";

const props = defineProps ({
  dataPaymentInfo: {
    type: Object,
    required: true
  },
  dataProduct: {
    type: Array,
    required: true
  },
  billData: {
    type: Object
  }
})

const emit = defineEmits(["update:bill"])

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

const handleUpdateBill = () => {
  emit("update:bill");
}
// console.log(dataHistory);


// watch(
//   () => props.dataSource?.data?.data,
//   (newvalue) => console.log(newvalue)
// );
</script>
