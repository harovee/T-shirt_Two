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
      <a-button danger="true"> Hủy đơn </a-button>
      <a-button type="primary"  class="right-button bg-orange-500 hover:bg-orange-600 text-white">
        Chi tiết
      </a-button>
    </div>
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
    StepContainer, // Register component
    CheckCircleOutlined,
    FileTextOutlined,
    CarOutlined,
    ReloadOutlined,
    CloseOutlined,
  },
  data() {
    return {
      currentStepIndex: 1,
      allSteps: [
        {
          title: "Tạo đơn hàng",
          time: "23-12-2023 13:48:17",
          icon: h(CheckCircleOutlined),
        },
        {
          title: "Chờ xác nhận",
          time: "21-12-2023 13:52:38",
          icon: h(FileTextOutlined),
        },
        {
          title: "Chờ giao hàng",
          time: "21-12-2023 13:53:23",
          icon: h(CarOutlined),
        },
        {
          title: "Đang giao hàng",
          time: "21-12-2023 13:54:52",
          icon: h(ReloadOutlined),
        },
        {
          title: "Đã giao hàng",
          time: "21-12-2023 13:54:52",
          icon: h(CheckCircleOutlined),
        },
        {
          title: "Thành công",
          time: "23-12-2023 13:48:17",
          icon: h(CheckCircleOutlined),
        },
        {
          title: "Trả hàng",
          time: "21-12-2023 14:00:00",
          icon: h(CloseOutlined),
        },
      ],
      steps: [
        {
          title: "Tạo đơn hàng",
          time: "23-12-2023 13:48:17",
          status: "finish",
          icon: h(CheckCircleOutlined),
        },
        {
          title: "Chờ xác nhận",
          time: "21-12-2023 13:52:38",
          status: "process",
          icon: h(FileTextOutlined),
        },
      ] as Step[],
    };
  },
  methods: {
    nextStep(): void {
      if (this.currentStepIndex < this.allSteps.length - 2) {
        this.currentStepIndex++;
        const nextStep = {
          ...this.allSteps[this.currentStepIndex],
          status: "process",
        };

        this.steps[this.steps.length - 1].status = "finish";
        this.steps.push(nextStep);
      }
    },
    prevStep(): void {
      if (this.currentStepIndex > 1 && this.currentStepIndex) {
        this.currentStepIndex--;
        const prevStep = {
          ...this.allSteps[this.currentStepIndex],
          status: "process",
        };
        this.steps[this.steps.length - 1].status = "finish";
        this.steps.push(prevStep);
      }
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
