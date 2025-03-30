<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from "vue";
import { useAuthStore } from "@/infrastructure/stores/auth";
import { Client } from "@stomp/stompjs";

const authStore = useAuthStore();

const messages = ref([]);
const message = ref("");
const stompClient = ref(null);
const chatVisible = ref(false);
const renderKey = ref(0);
const chatBodyRef = ref(null); // Thêm ref để truy cập phần hiển thị tin nhắn

const colors = [
  "#2196F3",
  "#32c787",
  "#00BCD4",
  "#ff5652",
  "#ffc107",
  "#ff85af",
  "#FF9800",
  "#39bbb0",
];

const getAvatarColor = (name) => {
  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = 31 * hash + name.charCodeAt(i);
  }
  return colors[Math.abs(hash % colors.length)];
};

const toggleChat = () => {
  chatVisible.value = !chatVisible.value;
  nextTick(scrollToBottom); // Cuộn xuống khi mở chat
};

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight;
    }
  });
};

const connectWebSocket = () => {
  stompClient.value = new Client({
    brokerURL: "ws://localhost:6868/ws",
    onConnect: (frame) => {
      console.log("Đã kết nối: " + frame);
      stompClient.value.subscribe("/topic/public", (payload) => {
        messages.value.push(JSON.parse(payload.body));
        scrollToBottom(); // Cuộn xuống khi có tin nhắn mới
      });
      stompClient.value.publish({
        destination: "/app/addUser",
        body: JSON.stringify({
          sender: authStore?.user ? authStore?.user?.userName : "Guest",
          type: "JOIN",
        }),
      });
    },
    onWebSocketError: (error) => {
      console.log("Lỗi kết nối WebSocket:", error);
      setTimeout(connectWebSocket, 5000);
    },
    onStompError: (frame) => {
      console.error("Lỗi từ broker: " + frame.headers["message"]);
      console.error("Chi tiết lỗi: " + frame.body);
    },
  });
  stompClient.value.activate();
};

const sendMessage = () => {
  if (message.value.trim() && stompClient.value) {
    const chatMessage = {
      sender: authStore?.user ? authStore?.user?.userName : "Guest",
      content: message.value,
      type: "CHAT",
    };

    stompClient.value.publish({
      destination: "/app/sendMessage",
      body: JSON.stringify(chatMessage),
    });

    message.value = "";
    scrollToBottom(); // Cuộn xuống khi gửi tin nhắn
  }
};

const disconnectWebSocket = () => {
  if (stompClient.value && stompClient.value.deactivate) {
    stompClient.value.deactivate();
    console.log("WebSocket đã ngắt kết nối");
  }
};

onUnmounted(() => {
  disconnectWebSocket();
});

onMounted(() => {
  if (authStore.isAuthenticated) {
    connectWebSocket();
    console.log("user đã đăng nhập", authStore.user);
  } else {
    console.log("chưa đăng nhập");
  }

  authStore.$onAction(({ name }) => {
    if (name === "logout") {
      disconnectWebSocket();
    }
  });
});

watch(
  () => authStore.isAuthenticated,
  (newVal, oldVal) => {
    if (newVal && !oldVal) {
      if (authStore.isAuthenticated) {
        connectWebSocket();
        renderKey.value++;
        console.log("user đã đăng nhập", authStore.user);
      } else {
        disconnectWebSocket();
        renderKey.value++;
      }
    }
  }
);
</script>

<template>
  <div class="chat-wrapper" :key="renderKey" v-show="authStore.isAuthenticated">
    <button class="chat-toggle" @click="toggleChat">Chat</button>

    <div v-if="chatVisible" class="chat-container">
      <div class="chat-header">
        <h2>Chat</h2>
        <button class="close-btn" @click="toggleChat">×</button>
      </div>

      <div ref="chatBodyRef" class="chat-body">
        <ul class="message-list">
          <li
            v-for="(msg, index) in messages"
            :key="index"
            class="message"
            :class="{
              'event-message': msg.type !== 'CHAT',
              'my-message':
                msg.sender ===
                (authStore?.user ? authStore?.user?.userName : 'Guest'),
            }"
          >
            <template v-if="msg.type === 'JOIN'">
              <span class="event">{{ msg.sender }} đã tham gia!</span>
            </template>
            <template v-else-if="msg.type === 'LEAVE'">
              <span class="event">{{ msg.sender }} đã rời khỏi!</span>
            </template>
            <template v-else>
              <div
                class="message-bubble"
                :style="{
                  backgroundColor:
                    msg.sender ===
                    (authStore?.user ? authStore?.user?.userName : 'Guest')
                      ? '#DCF8C6'
                      : '#FFFFFF',
                }"
              >
                <div
                  class="avatar"
                  :style="{ backgroundColor: getAvatarColor(msg.sender) }"
                >
                  {{ msg.sender[0] }}
                </div>
                <div class="message-content">
                  <span class="sender">{{ msg.sender }}:</span>
                  <span class="content">{{ msg.content }}</span>
                </div>
              </div>
            </template>
          </li>
        </ul>
      </div>

      <form @submit.prevent="sendMessage" class="message-form">
        <input
          v-model="message"
          placeholder="Nhập tin nhắn..."
          class="form-control"
        />
        <button type="submit" class="btn">Gửi</button>
      </form>
    </div>
  </div>
</template>

<style scoped>
/* Nút bật/tắt chat */
.chat-toggle {
  position: fixed;
  bottom: 200px;
  right: 10px;
  background: #007bff;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
}

.chat-container {
  position: fixed;
  bottom: 0;
  right: 70px;
  width: 350px;
  height: 350px;
  max-height: 500px;
  overflow-y: auto;
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  padding-bottom: 10px;
}

.chat-body {
  flex-grow: 1;
  overflow-y: auto;
}

.message-list {
  list-style-type: none;
  padding: 0;
}

.message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
}

.event-message {
  font-style: italic;
  color: #888;
  text-align: center;
}

.my-message {
  justify-content: flex-end;
}

.message-bubble {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 20px;
  max-width: 75%;
  margin-bottom: 5px;
  word-wrap: break-word;
}

.message-content {
  margin-left: 10px;
}

.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
  font-weight: bold;
  font-size: 14px;
  text-transform: uppercase;
}

.sender {
  font-weight: bold;
}

.content {
  margin-left: 5px;
  word-wrap: break-word;
}

.message-form {
  display: flex;
  padding-top: 10px;
}

.message-form input {
  flex-grow: 1;
  padding: 10px;
  border-radius: 20px;
  border: 1px solid #ccc;
}

.message-form button {
  padding: 10px;
  border-radius: 20px;
  background-color: #2196f3;
  color: white;
  border: none;
  margin-left: 10px;
  cursor: pointer;
}

.message-form button:hover {
  background-color: #0b79d0;
}
</style>
