<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
      <v-icon name="bi-cart3" size="x-large" width="48" height="48" />
      <h3 class="text-2xl m-0">Bán hàng tại quầy</h3>
    </div>
    <div>
      <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
        <a-tabs
          v-model:activeKey="activeKey"
          type="editable-card"
          @edit="onEdit"
          class="m-5"
        >
          <a-tab-pane v-for="bill in dataSource" :key="bill.id" :tab="bill.ma">
            {{ bill.id }}
          </a-tab-pane>
        </a-tabs>
      </div>

      <div class="rounded-xl p-7 mt-6 rounded-xl border-2"></div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  useGetBillsWait,
  useCreateBillsWait,
} from "@/infrastructure/services/service/admin/bill.action";
import { BillCreateRequest } from "@/infrastructure/services/api/admin/bill.api";
import { warningNotiSort, successNotiSort } from "@/utils/notification.config";

const { data, isLoading, isFetching } = useGetBillsWait();

const dataSource = computed(() => data?.value?.data || []);

watch(
  () => dataSource.value,
  (newData) => {
    if (newData) {
        console.log(newData);
    }
  },
  { immediate: true }
);

const panes = ref<{ title: string; content: string; key: string }[]>([
  { title: "Tab 1", content: "Content of Tab 1", key: "1" },
  { title: "Tab 2", content: "Content of Tab 2", key: "2" },
  { title: "Tab 3", content: "Content of Tab 3", key: "3" },
]);

const newTabIndex = ref(0);

const { mutate: createBillWail } = useCreateBillsWait();

const add = async () => {
  const payload = {
    loaiHD: "Tại quầy",
    idKhachHang: null,
    idNhanVien: null,
    idPhieuGiamGia: null,
  };
  console.log(dataSource.value);

  if (dataSource.value.length <= 5) {
    await createBillWail(payload);
    successNotiSort("Tạo hóa đơn thành công");
  } else {
    warningNotiSort("Không được tạo quá 5 hóa đơn chờ!")
  }
};

// const remove = (targetKey: string) => {
//   let lastIndex = 0;
//   panes.value.forEach((pane, i) => {
//     if (pane.key === targetKey) {
//       lastIndex = i - 1;
//     }
//   });
//   panes.value = panes.value.filter((pane) => pane.key !== targetKey);
//   if (panes.value.length && activeKey.value === targetKey) {
//     if (lastIndex >= 0) {
//       activeKey.value = panes.value[lastIndex].key;
//     } else {
//       activeKey.value = panes.value[0].key;
//     }
//   }
// };

const onEdit = (targetKey: string | MouseEvent, action: string) => {
  if (action === "add") {
    add();
  } else {
    // remove(targetKey as string);
  }
};
</script>