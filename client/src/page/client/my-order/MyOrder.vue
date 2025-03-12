<template>
  <div class="p-10">
    <h1 class="flex justify-center items-center mx-auto text-center text-2xl">ĐƠN MUA CỦA TÔI</h1>
    <div class="w-full mx-auto p-6">
      <div class="flex space-x-4 border-b pb-2">
        <button
        v-for="tab in orderTabs"
        :key="tab.status"
        @click="changeTab(tab.status)"
        class="py-2 px-4 text-sm font-medium border-b-2 transition"
        :class="activeTab === tab.status ? 'border-blue-500 text-blue-500' : 'border-transparent text-gray-500 hover:text-gray-700'"
      >
        {{ tab.label }}
      </button>
      </div>
      <a-form layout="vertical" class="mt-5">
        <a-form-item label="Tìm kiếm" class="w-full">
          <a-input
            v-model:value="params.keyword"
            placeholder="Nhập mã đơn hàng"
            @change="onChangeInput('keyword', $event)"
          />
        </a-form-item>
      </a-form>

      <div class="rounded-xl">
        <my-order-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, computed } from "vue";
import {
  BillPropsParams,
  FindBillRequest,
} from "@/infrastructure/services/api/admin/bill.api";
import MyOrderTable from "./MyOrderTable.vue"
import { ClientBillParams } from "@/infrastructure/services/api/client/clientmyorder.api";
import { useGetClientBills } from "@/infrastructure/services/service/client/clientbill.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { useAuthStore } from "@/infrastructure/stores/auth";

const params = ref<ClientBillParams>({
  page: 1,
  size: 10,
  trangThai: null,
  idKhachHang: useAuthStore().user?.id || null
});

const { data, isLoading, isFetching } = useGetClientBills(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataSource = computed(() => data?.value?.data || []);

const handlePaginationChange = (newParams: ClientBillParams) => {
  params.value = { ...params.value, ...newParams };
};

const orderTabs = ref([
  { label: "Tất cả", status: "all" },
  { label: "Chờ xác nhận", status: "Chờ xác nhận" },
  { label: "Chờ vận chuyển", status: "Chờ giao hàng" },
  { label: "Đang vận chuyển", status: "Đang vận chuyển" },
  { label: "Hoàn thành", status: "Thành công" },
  { label: "Đã hủy", status: "Đã hủy" },
]);

const activeTab = ref("all");

const changeTab = (status) => {
  activeTab.value = status;
  params.value = {
    ...params.value,
    trangThai: status === "all" ? null : status,
    page: 1,
  };
};

watch(
  () => data.value,
  (newData) => {
    if (newData) {
        console.log(newData);   
    }
  },
  { immediate: true }
);
</script>

<style scoped>
button {
  outline: none;
}
</style>