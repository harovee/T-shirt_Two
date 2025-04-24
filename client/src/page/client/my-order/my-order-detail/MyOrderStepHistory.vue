<template>
  <div class="w-full m-5">
    <a-steps :current="current" class="step-interface">
      <a-step
        v-for="item in selectedSteps"
        :key="item.title"
        :title="item.title"
        :icon="item.icon"
        :loading="loading"
      >
        <template #title>
          <div class="step-title">{{ item.title }}</div>
        </template>
        <template #description>
          <div class="step-time">{{ item.time || "Chưa có thông tin" }}</div>
        </template>
      </a-step>
    </a-steps>
    <div v-if="cucurent === 'Chờ xác nhận'" class="mt-5">
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
      <div class="left-buttons" v-if="cucurent === 'Chờ xác nhận'">
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
import { ref, watch, h, onMounted, reactive, computed } from "vue";
import {
  CarOutlined,
  CheckCircleOutlined,
  FileTextOutlined,
  IssuesCloseOutlined,
} from "@ant-design/icons-vue";
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
  loading: Boolean;
}>();

const radioStyle = reactive({
  display: "flex",
  height: "30px",
  lineHeight: "30px",
});

const selectedSteps = computed(() => {
  return props.dataSource?.data?.[0]?.trangThai === "Đã hủy"
    ? stepsCancel
    : steps;
});

const reasonInput = ref(null);

// Reactive state
const current = ref<number>(0);
const isModalVisible = ref(false);

const { mutate: changeStatus } = useChangeBillStatus();

const reason = ref("Khác");

// Khai báo các bước
const stepsCancel: Step[] = [
  {
    title: "Chờ xác nhận",
    time: "",
    icon: h(IssuesCloseOutlined),
  },
  {
    title: "Đã hủy",
    time: "",
    icon: h(IssuesCloseOutlined),
  },
];

const steps: Step[] = [
  {
    title: "Chờ xác nhận",
    time: "",
    icon: h(IssuesCloseOutlined),
  },
  {
    title: "Chờ giao hàng",
    time: "",
    icon: h(IssuesCloseOutlined),
  },
  {
    title: "Đang vận chuyển",
    time: "",
    icon: h(CarOutlined),
  },
  {
    title: "Đã giao hàng",
    time: "",
    icon: h(CheckCircleOutlined),
  },
  {
    title: "Đã thanh toán",
    time: "",
    icon: h(FileTextOutlined),
  },
  {
    title: "Thành công",
    time: "",
    icon: h(CheckCircleOutlined),
  },
];
onMounted(() => {
  if (props.dataSource?.data?.length > 0) {
    updateCurrentStep(props.dataSource);
    updateStepTimes(props.dataSource);
  }
});

const cucurent = ref(null);

watch(
  () => props.dataSource,
  (newData) => {
    if (newData) {
      cucurent.value = newData?.data?.[0].trangThai
    }
  }
);

// Cập nhật current step dựa trên dataSource
const updateCurrentStep = (dataSource: DataSource) => {
  const status = dataSource?.data?.[0]?.trangThai;
  if (status === "Đã hủy") {
    const statusMap: Record<string, number> = {
      "Chờ xác nhận": 0,
      "Đã hủy": 1,
    };
    current.value = statusMap[status] || 0;
  } else {
    const statusMap: Record<string, number> = {
      "Chờ xác nhận": 0,
      "Chờ giao hàng": 1,
      "Đang vận chuyển": 2,
      "Đã giao hàng": 3,
      "Đã thanh toán": 4,
      "Thành công": 5,
    };
    current.value = statusMap[status] || 0;
  }
};

const updateStepTimes = (dataSource: DataSource) => {
  if (!dataSource?.data || dataSource.data.length === 0) return;

  selectedSteps.value.forEach((step) => {
    const records = dataSource.data.filter(
      (item) => item.trangThai === step.title
    );

    if (records.length > 0) {
      records.sort((a, b) => b.ngayTao - a.ngayTao);
      step.time = convertDateFormat(records[0].ngayTao);
    } else {
      step.time = "Chưa có thông tin";
    }
  });
};

// Watch để cập nhật time khi dataSource thay đổi
watch(
  () => props.dataSource,
  (newValue) => {
    updateCurrentStep(newValue);
    updateStepTimes(newValue);
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
  const nextStep = stepsCancel[current.value + 1];
  const stepTitle = nextStep.title;

  // Chuẩn bị tham số cho API
  const params = {
    status: stepTitle,
    trangThai: "Đã hủy",
  };

  Modal.confirm({
    title: "Xác nhận hủy đơn",
    content: `Bạn muốn hủy đơn hàng này?`,
    onOk: async () => {
      try {
        // Gọi API để thay đổi trạng thái đơn hàng
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");

        // Sau khi cập nhật trạng thái thành công, di chuyển đến bước tiếp theo
        current.value++;
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

const confirmBill = () => {
  // Lấy trạng thái tiếp theo từ mảng steps
  const nextStep = steps[current.value + 1];
  const stepTitle = nextStep.title;

  // API tạo lịch sử hóa đơn
  const params = {
    status: stepTitle, // Trạng thái mới từ bước tiếp theo
    trangThai: "Chờ giao hàng",
  };

  Modal.confirm({
    title: "Xác nhận thay đổi trạng thái",
    content: `Bạn muốn thay đổi trạng thái của đơn hàng này sang "${stepTitle}"?`,
    onOk: async () => {
      try {
        // Gọi API để thay đổi trạng thái đơn hàng
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");

        // Sau khi cập nhật trạng thái thành công, di chuyển đến bước tiếp theo
        current.value++;
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

const confirmDelivery = () => {
  // Lấy trạng thái tiếp theo từ mảng steps
  const nextStep = steps[current.value + 1];
  const stepTitle = nextStep.title;

  // Chuẩn bị tham số cho API
  const params = {
    status: stepTitle, // Trạng thái mới từ bước tiếp theo
    trangThai: "Đang vận chuyển",
  };

  Modal.confirm({
    title: "Xác nhận thay đổi trạng thái",
    content: `Bạn muốn xác nhận giao hàng cho đơn này"?`,
    onOk: async () => {
      try {
        // Gọi API để thay đổi trạng thái đơn hàng
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");

        // Sau khi cập nhật trạng thái thành công, di chuyển đến bước tiếp theo
        current.value++;
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

const confirmArrived = () => {
  const nextStep = steps[current.value + 1];
  const stepTitle = nextStep.title;

  const params = {
    status: stepTitle,
    trangThai: "Đã giao hàng",
  };

  Modal.confirm({
    title: "Xác nhận thay đổi trạng thái",
    content: `Bạn muốn xác nhận giao hàng cho đơn này"?`,
    onOk: async () => {
      try {
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");

        current.value++;
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

const confirmCompleted = () => {
  // Lấy trạng thái tiếp theo từ mảng steps
  const nextStep = steps[current.value + 1];
  const stepTitle = nextStep.title;

  // API tạo lịch sử hóa đơn
  const params = {
    status: stepTitle, // Trạng thái mới từ bước tiếp theo
    trangThai: "Thành công",
  };

  Modal.confirm({
    title: "Xác nhận thay đổi trạng thái",
    content: `Bạn muốn thay đổi trạng thái của đơn hàng này sang "${stepTitle}"?`,
    onOk: async () => {
      try {
        // Gọi API để thay đổi trạng thái đơn hàng
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");

        // Sau khi cập nhật trạng thái thành công, di chuyển đến bước tiếp theo
        current.value++;
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

const rollBack = () => {
  if (current.value > 0) {
    const prevStep = steps[current.value - 1];
    const stepTitle = prevStep.title;
    const description = ref();

    Modal.confirm({
      title: "Xác nhận quay lại trạng thái trước",
      content: () => {
        return h("div", [
          h(
            "p",
            `Bạn có chắc chắn muốn quay lại trạng thái "${stepTitle}" không?`
          ),
          h(Input.TextArea, {
            placeholder: "Nhập lý do quay lại...",
            autoSize: { minRows: 2, maxRows: 4 },
            onChange: (e) => (description.value = e.target.value),
          }),
        ]);
      },
      onOk: async () => {
        if (!description.value || !description.value.trim()) {
          errorNotiSort("Vui lòng nhập lý do quay lại");
          return Promise.reject();
        }

        const params = {
          status: stepTitle,
          trangThai: stepTitle,
          moTa: description.value, // Gửi mô tả rollback
        };

        try {
          await changeStatus({ idBill, params });
          successNotiSort(`Trạng thái đã quay lại: ${stepTitle}`);

          // 🔄 Cập nhật lại thời gian của trạng thái rollback
          const stepIndex = steps.findIndex((step) => step.title === stepTitle);
          if (stepIndex !== -1) {
            steps[stepIndex].time = new Date().toLocaleString("vi-VN", {
              hour12: false,
            });
          }

          // Quay lại trạng thái trước
          current.value--;
        } catch (error) {
          console.error("Cập nhật trạng thái thất bại:", error);
          errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
        }
      },
      onCancel: () => console.log("Thao tác rollback bị hủy."),
    });
  }
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
