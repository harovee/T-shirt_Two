<template>
  <div>
  <div
    v-for="(noti, index) in dataListNotification"
    :key="index"
    class="relative p-2 text-sm text-gray-800 border-b border-gray-200 hover:bg-gray-50"
  >
    <p
    @click="goToNotification(noti.orderId)"
    class="cursor-pointer hover:underline"
  >{{ noti.content }}</p>
    <button
      @click="handleDeleteNotification(noti.id)"
      class="absolute top-1 right-1 text-gray-400 hover:text-red-500 text-xs"
      title="Xóa"
    >
      ✕
    </button>
  </div>

  <p v-if="!dataListNotification.length" class="p-2 text-sm text-gray-400 text-center">
    Không có thông báo
  </p>
</div>
</template>

<script lang="ts" setup>
import { orderBy } from "lodash";
import { computed, ref, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useDeleteNotification, useGetListNotification } from "../services/notification.action";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";

const props = defineProps<{
  messages: string[];
}>();

const router = useRouter();

const notifications = ref([]);

const { data: notificationData, refetch: refetchNotification } = useGetListNotification();

const { mutate: deleteNotification } = useDeleteNotification();

const dataListNotification = computed(
  () => notificationData?.value?.data || []
);

const handleDeleteNotification = async (id) => {
  try {
    await deleteNotification(id);
  } catch (err) {
    console.error("Lỗi xoá thông báo:", err);
  }
};

const goToNotification = (idHoaDon) => {
  const path = {
    path:
      ROUTES_CONSTANTS.ADMIN.path +
      "/" +
      ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_DETAIL.path,
    query: { idHoaDon },
  };
  router.push(path);
}

const route = useRoute();

// watch(() => router.query.orderId, (newVal, oldVal) => {
//   if (newVal !== oldVal) {
//     fetchData();
//   }
// });
const messages = computed(() => props.messages);
</script>
