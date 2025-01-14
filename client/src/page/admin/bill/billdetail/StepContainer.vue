<template>
  <div class="steps-wrapper">
    <div class="steps-container">
      <a-steps :current="currentStepIndex" size="small" direction="horizontal">
        <a-step
          v-for="(step, index) in steps"
          :key="index"
          :title="step.title"
          :description="step.time"
          :status="step.status"
          :icon="step.icon"
          class="custom-step"
        />
      </a-steps>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, h } from "vue";
interface Step {
  title: string;
  time: string;
  status: "wait" | "process" | "finish";
  icon: any;
}

export default defineComponent({
  name: "StepContainer",
  props: {
    currentStepIndex: {
      type: Number,
      required: true,
    },
    steps: {
      type: Array as () => Step[],
      required: true,
    },
  },
});
</script>

<style scoped>
.steps-wrapper {
  overflow-x: auto;
  width: 100%;
  max-width: 100vw;
  margin-bottom: 16px;
}

.steps-container {
  display: flex;
  flex-wrap: nowrap;
  min-width: max-content;
}

.custom-step {
  flex: 0 0 auto;
  text-align: center;
}
</style>
