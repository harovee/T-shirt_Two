import { defineStore } from "pinia";
import { ref } from "vue";

export const useChatToggleStore = defineStore("chatToggle", () => {
  const activeChat = ref<"chatbot" | "livechat" | null>(null);

  const toggleChat = (type: "chatbot" | "livechat") => {
    activeChat.value = activeChat.value === type ? null : type;
  };

  return { activeChat, toggleChat };
});