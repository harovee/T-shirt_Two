<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
      <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48"/>
      <h3 class="text-2xl m-0">Quản lý nhân viên</h3>
    </div>
    <div class="p-4 rounded-xl border-2  flex flex-col gap-6">
      <div class="flex items-center gap-2">
        <v-icon name="si-iconfinder" size="x-large" width="24" height="24"/>
        <h4 class="text-xl m-0">Bộ lọc</h4>
      </div>
      <staff-filter
          @filter="handleFilter"
      />
    </div>
    <div class="rounded-xl">
      <staff-table
          :data-source="dataSource"
          :loading="isLoading || isFetching"
          @handleOpenModalCreate="handleOpenModalCreateStaff"
          @handleCloseModalCreate="handleCloseModalCreateStaff"
          :pagination-params="params"
          @update:pagination-params="handlePaginationChange"
      />
    </div>
  </div>
  <staff-modal-c
      :open="isOpenModalCreateStaff"
      @handleClose="handleCloseModalCreateStaff"
      @onCancel="isOpenModalCreateStaff = false"
  />
</template>

<script lang="ts" setup>

import {computed, ref, watch} from "vue";
import {FindStaffRequest} from "@/infrastructure/services/api/admin/staff.api.ts";
import {useGetStaffs} from "@/infrastructure/services/service/admin/staff.action.ts";
import {keepPreviousData} from "@tanstack/vue-query";
import StaffFilter from "@/page/admin/staff/StaffFilter.vue";
import StaffTable from "@/page/admin/staff/StaffTable.vue";
import StaffModalC from "@/page/admin/staff/StaffModalC.vue";

/*** Table - Pagination - Filter  ***/

const params = ref<FindStaffRequest>({
  page: 1,
  size: 10
});

const {data, isLoading, isFetching} = useGetStaffs(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const handleFilter = (newParams: FindStaffRequest) => {
  params.value = {...params.value, ...newParams};
};

const dataSource = computed(() => data?.value?.data || []);

const handlePaginationChange = (newParams: FindStaffRequest) => {
  params.value = {...params.value, ...newParams};
};

/*** Create Staff ***/
const isOpenModalCreateStaff = ref(false);

const handleOpenModalCreateStaff = () => {
  isOpenModalCreateStaff.value = true;
};

const handleCloseModalCreateStaff = () => {
  isOpenModalCreateStaff.value = false;
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
  name: 'admin staff',
};
</script>