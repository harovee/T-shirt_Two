<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
      <v-icon name="la-truck-solid" size="x-large" width="48" height="48" />
      <h3 class="text-2xl m-0">Trả hàng</h3>
    </div>
    <div
      class="p-6 flex flex-col items-center mb-12 p-4 rounded-xl border-2 shadow"
    >
      <div
        class="flex flex-col items-center justify-center mt-8 w-full max-w-lg"
      >
        <div class="search-container flex items-center gap-4 w-full">
          <a-input
            v-bind:value="searchQuery"
            @update:value="searchQuery = $event"
            placeholder="Nhập mã đơn hàng..."
            class="search-input flex-1"
            style="height: 40px; width: auto; min-width: 500px"
          />

          <div>
            <a-tooltip title="Tìm kiếm đơn hàng" trigger="hover">
              <a-button
                class="bg-purple-300 flex justify-between items-center gap-2"
                size="large"
                @click="handleSearch()"
              >
                <FileSearchOutlined />
              </a-button>
            </a-tooltip>
          </div>

          <div>
            <a-tooltip title="Quét mã đơn hàng" trigger="hover">
              <a-button
                class="bg-purple-300 flex justify-between items-center gap-2"
                size="large"
              >
                <QrcodeOutlined />
              </a-button>
            </a-tooltip>
          </div>
        </div>

        <div class="image-container mt-12">
          <img
            src="@/assets/image/background/return-bg.jpg"
            alt="Return Background"
            class="small-image"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch } from "vue";
import { FileSearchOutlined, QrcodeOutlined } from "@ant-design/icons-vue";
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import router from "@/infrastructure/routes/router";
import { successNotiSort, warningNotiSort } from "@/utils/notification.config";
import { getBillRefundByMaHD } from "@/infrastructure/services/api/admin/bill.api";

// Tạo searchQuery và function handleSearch trong setup()
const searchQuery = ref("");

const handleSearch = async () => {
  // console.log("Giá trị của searchQuery:", searchQuery.value);

  if (!searchQuery.value.trim()) {
    warningNotiSort("Vui lòng nhập mã đơn hàng");
    return;
  }
  const res = await getBillRefundByMaHD(searchQuery.value);
  // console.log(res.data);

  if (!res.data) {
    warningNotiSort("Không tồn tại đơn hàng");
    return;
  }

  try {
    const detailBillPath = {
      path: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_REFUND_DETAIL.path.replace(
        ":maHoaDon",
        searchQuery.value
      ),
    };
    router.push(detailBillPath);
    successNotiSort("Tìm đơn hàng thành công");
  } catch (error) {
    warningNotiSort("Lỗi khi lấy dữ liệu từ API");
    console.error(error);
  }
};
</script>

<style scoped>
.small-image {
  max-width: 100%;
  height: auto;
}
</style>
