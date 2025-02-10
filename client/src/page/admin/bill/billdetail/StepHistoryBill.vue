<template>
  <div>
    <a-steps :current="current">
      <a-step
        v-for="item in steps"
        :key="item.title"
        :title="item.title"
        :icon="item.icon"
      />
    </a-steps>
    <div class="steps-content">
      {{ steps[current]?.content }} <br />
      {{ steps[current]?.time }}
    </div>
    <div class="steps-action">
      <a-button v-if="current == 0" type="primary" @click="next">
        Xác nhận đơn
      </a-button>

      <a-button v-if="current == 1" type="primary" @click="next">
        Xác nhận giao hàng
      </a-button>

      <a-button v-if="current == 2" type="primary" @click="next">
        Xác nhận lấy hàng
      </a-button>

      <a-button v-if="current == 4" type="primary" @click="next">
        Hoàn thành
      </a-button>

      <a-button v-if="current > 0 && current < 2" style="margin-left: 8px" @click="prev">
        Quay lại trạng thái trước
      </a-button>

      <a-button danger style="margin-left: 10px"> Hủy đơn </a-button>

      <a-button
        type="primary"
        class="right-button bg-orange-500 hover:bg-orange-600 text-white"
        style="margin-right: 15px"
        @click="showDetailModal"
      >
        Chi tiết
      </a-button>
    </div>

    <!-- Modal lịch sử -->
    <a-modal
      style="width: 1000px"
      v-model:open="isModalVisible"
      title="Chi tiết lịch sử"
      @cancel="handleCancel"
    >
      <a-table
        :columns="columns"
        :data-source="dataSource?.data"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'ngayTao'">
            <span v-if="record?.ngayTao">
              {{ convertDateFormat(record.ngayTao) }}
            </span>
          </template>
          <template v-if="column.key === 'hanhDongChiTiet'">
            {{ record.nguoiTao }} {{ record.hanhDong }}
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
import { ref, watch, h } from "vue";
import { CarOutlined, CheckCircleOutlined, FileTextOutlined, IssuesCloseOutlined } from "@ant-design/icons-vue";
import { convertDateFormat } from "@/utils/common.helper";

interface DataSource {
  data: {
    data: Array<{
      trangThai: string;
      ngayTao: string;
      hanhDong: string;
      nguoiTao: string;
    }>;
  };
}

interface Step {
  title: string;
  content: string;
  time: string;
  icon: ReturnType<typeof h>;
}

// Props
const props = defineProps<{
  dataSource: DataSource;
}>();

// Reactive state
const current = ref<number>(0);
const isModalVisible = ref(false);

// Khai báo các bước
const steps: Step[] = [
  {
    title: "Chờ xác nhận",
    content: "Đơn hàng đang chờ xác nhận",
    time: "",
    icon: h(IssuesCloseOutlined),
  },
  {
    title: "Chờ giao hàng",
    content: "Đơn hàng đã chuẩn bị xong, chờ giao hàng",
    time: "",
    icon: h(IssuesCloseOutlined),
  },
  {
    title: "Đang vận chuyển",
    content: "Đơn hàng đang được vận chuyển đến khách hàng",
    time: "",
    icon: h(CarOutlined),
  },
  {
    title: "Đã giao hàng",
    content: "Đơn hàng đã giao thành công",
    time: "",
    icon: h(CheckCircleOutlined),
  },
  {
    title: "Đã thanh toán",
    content: "Đơn hàng đã được thanh toán thành công",
    time: "",
    icon: h(FileTextOutlined),
  },
  {
    title: "Thành công",
    content: "Đơn hàng đã hoàn thành",
    time: "",
    icon: h(CheckCircleOutlined),
  },
];

// Watch để theo dõi khi dataSource thay đổi
watch(
  () => props.dataSource,
  (newValue) => {
    updateCurrentStep(newValue);
  },
  { deep: true }
);

// Cập nhật current step dựa trên dataSource
const updateCurrentStep = (dataSource: DataSource) => {
  const status = dataSource?.data?.data?.[0].trangThai;
  const statusMap: Record<string, number> = {
    "Chờ xác nhận": 0,
    "Chờ giao hàng": 1,
    "Đang vận chuyển": 2,
    "Đã giao hàng": 3,
    "Đã thanh toán": 4,
    "Thành công": 5,
  };
  current.value = statusMap[status] || 0;
};

// Các hàm điều hướng giữa các bước
const next = () => {
  if (current.value < steps.length - 1) current.value++;
};

const prev = () => {
  if (current.value > 0) current.value--;
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

// Convert date helper function
const convertDateFormat = (date: string) => {
  return new Date(date).toLocaleString(); // Chỉnh sửa theo nhu cầu của bạn
};
</script>

<style scoped>
.steps-content {
  margin-top: 16px;
  border: 1px dashed #e9e9e9;
  border-radius: 6px;
  background-color: #fafafa;
  min-height: 100px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.steps-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
}
.right-button {
  margin-left: auto;
}
.ant-modal-body {
  max-height: 350px;
  overflow-y: auto;
}
</style>
