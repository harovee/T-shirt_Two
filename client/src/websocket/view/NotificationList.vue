<template>
  <div>
    <p
      v-for="(message, index) in messages"
      :key="index"
      class="p-2 text-sm text-gray-800 border-b border-gray-200 hover:bg-gray-50"
    >
      {{ message }}
    </p>
    <p v-if="!messages.length" class="p-2 text-sm text-gray-400 text-center">
      Không có thông báo
    </p>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';

const props = defineProps<{
  messages: string[];
}>();

const messages = computed(() => props.messages);
</script>
