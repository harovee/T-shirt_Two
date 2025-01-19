<template>
  <div class="history">
    <h3>Lịch sử hóa đơn</h3>
    <!-- Sử dụng StepsContainer component -->
    <StepContainer :currentStepIndex="currentStepIndex" :steps="steps" />

    <div style="margin-top: 40px">
      <a-button type="primary" @click="prevStep" class="right-button">
        Quay lại trạng thái trước
      </a-button>
    </div>
    <div style="margin-top: 40px; display: flex">
      <a-button type="default" @click="nextStep" style="margin-right: 8px">
        Xác nhận đơn
      </a-button>
      <a-button danger> Hủy đơn </a-button>
      <a-button
        type="primary"
        class="right-button bg-orange-500 hover:bg-orange-600 text-white"
        @click="showDetailModal"
      >
        Chi tiết
      </a-button>
    </div>

    <!-- Modal -->
    <a-modal
      v-model:visible="isDetailModalVisible"
      title="Lịch sử chi tiết"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
    >
      <ul>
        <li v-for="(item, index) in dataSource?.data?.data" :key="index">
          {{ item.moTa }} - {{ item.ngayTao }}
        </li>
      </ul>
    </a-modal>
  </div>
</template>

<script lang="ts">
import { defineComponent, h, watch } from "vue";
import {
  CheckCircleOutlined,
  FileTextOutlined,
  CarOutlined,
  ReloadOutlined,
  CloseOutlined,
} from "@ant-design/icons-vue";
import StepContainer from "./StepContainer.vue";

interface Step {
  title: string;
  time: string;
  status: "wait" | "process" | "finish";
  icon: any;
}

export default defineComponent({
  name: "DynamicSteps",
  components: {
    StepContainer,
    CheckCircleOutlined,
    FileTextOutlined,
    CarOutlined,
    ReloadOutlined,
    CloseOutlined,
  },

  props: {
    dataSource: Array,
    loading: {
      type: Boolean,
      required: true,
    },
  },

  data() {
    return {
      currentStepIndex: 1,
      isDetailModalVisible: false, // Trạng thái hiển thị modal
      allSteps: [
        { title: "Tạo đơn hàng", time: "", icon: h(CheckCircleOutlined) },
        { title: "Chờ xác nhận", time: "", icon: h(FileTextOutlined) },
        { title: "Chờ giao hàng", time: "", icon: h(CarOutlined) },
        { title: "Đang giao hàng", time: "", icon: h(ReloadOutlined) },
        { title: "Đã giao hàng", time: "", icon: h(CheckCircleOutlined) },
        { title: "Thành công", time: "", icon: h(CheckCircleOutlined) },
        { title: "Trả hàng", time: "", icon: h(CloseOutlined) },
      ],
      steps: [
        { title: "Tạo đơn hàng", time: "", status: "finish", icon: h(CheckCircleOutlined) },
        { title: "Chờ xác nhận", time: "", status: "process", icon: h(FileTextOutlined) },
      ] as Step[],
    };
  },

  methods: {
    updateStepsBasedOnStatus() {
      // Lấy trạng thái hiện tại từ dataSource
      const trangThai = this.dataSource?.data?.data || "Tạo đơn hàng";
      // console.log(this.dataSource?.data?.data?.[0].trangThai);

      // Tìm chỉ số bước hiện tại
      const currentStepIndex = this.allSteps.findIndex((step) => step.title === trangThai);

      if (currentStepIndex !== -1) {
        // Cập nhật steps dựa trên trạng thái
        this.steps = this.allSteps.slice(0, currentStepIndex + 1).map((step, idx) => ({
          ...step,
          status: idx < currentStepIndex ? "finish" : "process",
        }));
        this.currentStepIndex = currentStepIndex;
      }
    },
    nextStep() {
      if (this.currentStepIndex < this.allSteps.length - 1) {
        this.currentStepIndex++;
        const nextStep = {
          ...this.allSteps[this.currentStepIndex],
          status: "process",
        };

        this.steps[this.steps.length - 1].status = "finish";
        this.steps.push(nextStep);
      }
    },
    prevStep() {
      if (this.currentStepIndex > 1) {
        this.steps.pop(); // Xóa bước cuối
        this.currentStepIndex--;
        this.steps[this.steps.length - 1].status = "process"; // Đặt bước trước đó thành "process"
      }
    },
    showDetailModal() {
      console.log(this.dataSource);
      this.isDetailModalVisible = true; // Hiển thị modal
    },
    handleModalOk() {
      this.isDetailModalVisible = false; // Đóng modal khi nhấn OK
    },
    handleModalCancel() {
      this.isDetailModalVisible = false; // Đóng modal khi nhấn Cancel
    },
  },

  watch: {
    // Watch để cập nhật steps khi dataSource thay đổi
    dataSource: {
      handler(newValue) {
        if (newValue) {
          this.updateStepsBasedOnStatus();
        }
      },
      immediate: true, // Gọi ngay lập tức khi component được khởi tạo
      deep: true, // Theo dõi các thay đổi sâu bên trong object
    },
  },
});
</script>


<style scoped>
.history {
  margin-bottom: 20px;
}

.right-button {
  margin-left: auto;
}
</style>
