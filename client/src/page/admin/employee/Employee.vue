<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
      <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48"/>
      <h3 class="text-2xl m-0">Quản lý khách hàng</h3>
    </div>
    <div class="p-4 rounded-xl border-2 shadow-purple-950 shadow-xl flex flex-col gap-6">
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24"/>
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <employee-filter
          @filter="handleFilter"
      />
    </div>
    <div class="rounded-xl">
      <employee-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          @handleOpenModalCreate="handleOpenModalCreateEmployee"
          @handleCloseModalCreate="handleCloseModalCreateEmployee"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
      />
    </div>
  </div>
  <employee-modal-c
      :open="isOpenModalCreateEmployee"
      @handleClose="handleCloseModalCreateEmployee"
      @onCancel="isOpenModalCreateEmployee = false"
  />
</template>

<script lang="ts" setup>

import {computed, ref, watch} from "vue";
import {FindEmployeeRequest} from "@/infrastructure/services/api/admin/employee.api.ts";
import {useGetEmployees} from "@/infrastructure/services/service/admin/employee.action.ts";
import {keepPreviousData} from "@tanstack/vue-query";
import EmployeeFilter from "@/page/admin/employee/EmployeeFilter.vue";
import EmployeeTable from "@/page/admin/employee/EmployeeTable.vue";
import EmployeeModalC from "@/page/admin/employee/EmployeeModalC.vue";

/*** Table - Pagination - Filter  ***/

const params = ref<FindEmployeeRequest>({
  page: 1,
  size: 10
});

const {data, isLoading, isFetching} = useGetEmployees(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const handleFilter = (newParams: FindEmployeeRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => data?.value?.data || []);

const handlePaginationChange = (newParams: FindEmployeeRequest) => {
  params.value = {...params.value, ...newParams};
};

/*** Create Employee ***/
const isOpenModalCreateEmployee = ref(false);

const handleOpenModalCreateEmployee = () => {
  isOpenModalCreateEmployee.value = true;
};

const handleCloseModalCreateEmployee = () => {
  isOpenModalCreateEmployee.value = false;
};

watch(
    () => data.value,
    (newData) => {
      if (newData) {

      }
    },
    {immediate: true}
);
</script>

<script lang="ts">
export default {
  name: 'admin employee',
};
</script>