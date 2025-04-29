<template>
  <div class="w-full m-5">
    <div style="overflow-x: auto; white-space: nowrap">
      <a-steps
        :current="uniqueStepsTmp.length"
        class="step-interface"
        direction="horizontal"
        style="min-width: max-content"
      >
        <a-step
          v-for="(item, index) in uniqueStepsTmp"
          :key="index"
          :title="item?.title"
          :icon="item.icon"
        >
          <template #title>
            <div class="step-title">{{ item.title }}</div>
          </template>
          <template #description>
            <div class="step-time">{{ item.time || "Chưa có thông tin" }}</div>
          </template>
        </a-step>
      </a-steps>
    </div>
    <div v-if="stepsTmp[stepsTmp.length - 1]?.title === 'Chờ xác nhận'" class="mt-5">
      <a-radio-group v-model:value="reason">
        <a-radio :style="radioStyle" value="Tôi thay đổi ý"
          >Tôi thay đổi ý</a-radio
        >
        <a-radio :style="radioStyle" value="Tôi muốn mua sản phẩm khác"
          >Tôi muốn mua sản phẩm khác</a-radio
        >
        <a-radio :style="radioStyle" value="Tôi muốn hủy đơn"
          >Tôi muốn hủy đơn</a-radio
        >
        <a-radio :style="radioStyle" value="Khác"> Khác </a-radio>
        <a-input
          v-if="reason === 'Khác'"
          v-model:value="reasonInput"
          style="width: 500px"
        />
      </a-radio-group>
    </div>
    <div class="steps-action">
      <div class="left-buttons" v-if="stepsTmp[stepsTmp.length - 1]?.title === 'Chờ xác nhận'">
        <a-button danger style="margin-left: 10px" @click="handleCancelBill">
          Hủy đơn
        </a-button>
      </div>

      <div class="right-buttons">
        <a-button
          class="border border-orange-500 bg-transparent text-orange-500 hover:border-orange-300"
          style="margin-right: 15px"
          @click="showDetailModal"
        >
          Chi tiết
        </a-button>
      </div>
    </div>

    <!-- Modal lịch sử -->
    <a-modal
      style="width: 1000px"
      v-model:open="isModalVisible"
      title="Chi tiết lịch sử"
      @cancel="handleCancel"
      @ok="handleCancel"
    >
      <a-table
        :columns="columns"
        :data-source="props.dataSource?.data"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 'max-content', y: 600 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'ngayTao'">
            <span v-if="record?.ngayTao">
              {{ convertDateFormat(record.ngayTao) }}
            </span>
          </template>
          <template v-if="column.key === 'hanhDongChiTiet'">
            {{ record.hanhDong }}
          </template>
          <template v-if="column.key === 'trangThai'">
            <a-tag>{{ record.trangThai }}</a-tag>
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, h, onMounted, reactive, computed } from "vue";
import {
  CarOutlined,
  CheckCircleOutlined,
  FileTextOutlined,
  IssuesCloseOutlined,
} from "@ant-design/icons-vue";
import { useAuthStore } from "@/infrastructure/stores/auth";
import { convertDateFormat } from "@/utils/common.helper";
import { useChangeBillStatus } from "@/infrastructure/services/service/admin/bill.action";
import { errorNotiSort, successNotiSort } from "@/utils/notification.config";
import { Input, Modal } from "ant-design-vue";

interface DataSource {
  data: {
    trangThai: string;
    ngayTao: number;
    hanhDong: string;
    nguoiTao: string;
  }[];
}

interface Step {
  title: string;
  time: string;
  icon: ReturnType<typeof h>;
}

// Props
const props = defineProps<{
  dataSource: DataSource;
  loading: boolean;
}>();

const radioStyle = reactive({
  display: "flex",
  height: "30px",
  lineHeight: "30px",
});

const stepsTmp = ref([
  { title: "Chờ xác nhận", time: "2025-04-27 10:00" },
  { title: "Đang giao", time: "2025-04-27 15:00" },
]);

const currentCopy = ref(stepsTmp.value.length);

const reasonInput = ref(null);

// Reactive state
const current = ref<number>(0);
const isModalVisible = ref(false);

const { mutate: changeStatus } = useChangeBillStatus();

const reason = ref("Khác");

const isDataReady = ref(false);

onMounted(() => {
  if (props.dataSource?.data?.length > 0) {
  }
});

const cucurent = ref(null);

// Hàm chỉ hiển thị 1 step khi có nhiều step giống nhau
const uniqueStepsTmp = computed(() => {
  const seen = new Set();
  return stepsTmp.value.filter(item => {
    const key = item.title; // Hoặc item.trangThai nếu dùng trường đó
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
});

watch(
  () => props.dataSource,
  (newData) => {
    if (newData) {
      cucurent.value = newData?.data?.[0].trangThai;
    }
  }
);

watch(
  () => [props.loading, props.dataSource],
  ([loading, dataSource]) => {
    if (
      !loading &&
      typeof dataSource === "object" &&
      Array.isArray(dataSource.data) &&
      dataSource.data.length > 0
    ) {
      stepsTmp.value = dataSource.data.map((item) => ({
        title: item.trangThai,
        time: convertDateFormat(item.ngayTao),
      }));
      isDataReady.value = true;
      console.log("StepsTmp đã có dữ liệu:", stepsTmp.value);
    } else {
      console.warn("Không có dữ liệu stepsTmp:", { loading, dataSource });
    }
  },
  { immediate: true }
);


// Watch để cập nhật time khi dataSource thay đổi
watch(
  () => props.dataSource,
  (newValue) => {
  },
  { deep: true, immediate: true }
);

// Các hàm điều hướng giữa các bước
// Hàm để lấy idBill từ URL
const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

const idBill = getIdHoaDonFromUrl();

const handleCancelBill = () => {
  const stepTitle = stepsTmp.value[0].title;

  // Chuẩn bị tham số cho API
  const params = {
        status: stepTitle,
        trangThai: "Đã hủy",
        moTa: "Khách hàng đã hủy đơn hàng",
        email: null,
        idHoaDon: idBill,
        nhanVien: useAuthStore().user?.email || null,
        ghiChu: "Khách hàng đã hủy đơn hàng",
  };

  Modal.confirm({
    title: "Xác nhận hủy đơn",
    content: `Bạn muốn hủy đơn hàng này?`,
    onOk: async () => {
      try {
        // Gọi API để thay đổi trạng thái đơn hàng
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");
      } catch (error) {
        console.error("Cập nhật trạng thái thất bại:", error);
        errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
      }
    },
    onCancel: () => {
      console.log("Thao tác đã bị hủy.");
    },
  });
};

const showDetailModal = () => {
  isModalVisible.value = true;
};

const handleCancel = () => {
  isModalVisible.value = false;
};

// Cột của bảng Modal
const columns = [
  {
    title: "Trạng thái",
    dataIndex: "trangThai",
    key: "trangThai",
    align: "center",
  },
  {
    title: "Thời gian",
    dataIndex: "ngayTao",
    key: "ngayTao",
    align: "center",
  },
  {
    title: "Hành động",
    dataIndex: "hanhDongChiTiet",
    key: "hanhDongChiTiet",
    align: "center",
  },
  {
    title: "Mô tả",
    dataIndex: "moTa",
    key: "moTa",
    align: "center",
  },
];
</script>

<style scoped>
.steps-action {
  display: flex;
  justify-content: space-between; /* Căn hai nhóm nút ra hai bên */
  align-items: center;
  padding-top: 20px;
  margin-top: 20px;
}

.left-buttons {
  display: flex;
  gap: 10px; /* Tạo khoảng cách giữa các nút */
}

.right-buttons {
  display: flex;
}

.ant-modal-body {
  max-height: 350px;
  overflow-y: auto;
}
.step-interface {
  border: 1px solid #cbd0d4; /* Border màu xanh dương (có thể thay đổi theo ý thích) */
  border-radius: 5px; /* Bo góc để các góc mềm mại */
  padding: 20px; /* Thêm khoảng cách trong thẻ */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
.step-title {
  font-weight: bold;
  margin-bottom: 8px;
}
.step-time {
  font-size: 12px;
  color: #666;
}
</style>
