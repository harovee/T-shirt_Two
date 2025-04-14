<script setup>
import {
  ref,
  onMounted,
  onUnmounted,
  watch,
  nextTick,
  computed,
  watchEffect,
} from "vue";
import { useAuthStore } from "@/infrastructure/stores/auth";
import { Client } from "@stomp/stompjs";
import { useGetClientChatList } from "./infrastructure/services/service/admin/client.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { useGetChatHistory } from "./infrastructure/services/service/admin/chathistory.action";
import { dateFormatChatBox } from "./utils/common.helper";
import { WechatOutlined } from "@ant-design/icons-vue";
import { useChatToggleStore } from "./infrastructure/stores/chatToggle";

const authStore = useAuthStore();

const messages = ref([]);
const message = ref("");
const stompClient = ref(null);
const chatVisible = computed(() => chatToggleStore.activeChat === "livechat");
const renderKey = ref(0);
const chatBodyRef = ref(null);
const previousMessages = ref({});
const newMessagesCount = ref(0);

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

// Bỏ phần đếm tin nhắn mới
watch(messages, (newMessages) => {
  if (newMessages.length > 0 && !chatVisible.value) {
    // Khi có tin nhắn mới và cửa sổ chat chưa mở
    newMessagesCount.value = 1;  // Đánh dấu là có tin nhắn mới
  }
});

const chatToggleStore = useChatToggleStore();

const toggleChat = () => {
  chatToggleStore.toggleChat("livechat");
  if (chatVisible.value) {
    newMessagesCount.value = 0;  // Reset đếm khi mở chat
  }
  nextTick(scrollToBottom);
};

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight;
    }
  });
};

// Xác định phòng chat hiện tại
const chatRooms = ref([{ id: "public", name: "Phòng Chung" }]);
const selectedRoom = ref("public");
const roomId = computed(() => selectedRoom.value);

// dữ liệu tin nhắn
const {
  data: chatHistory,
  isHistoryLoading,
  isHistoryFetching,
} = useGetChatHistory(roomId, {
  refetchOnWindowClose: false,
  placeholderData: keepPreviousData,
});

// list phòng chat cho admin theo danh sách người dùng còn hoạt động (chưa bị deleted)
const { data, isLoading, isFetching } = useGetClientChatList({
  refetchOnWindowClose: false,
  placeholderData: keepPreviousData,
});

watch(chatHistory, (newHistory) => {
  if (newHistory?.data) {
    messages.value = newHistory.data;
    previousMessages.value[selectedRoom.value] = newHistory.data;
    scrollToBottom();
  }
});

watch(
  () => [authStore.user, data.value],
  ([newUser, newData]) => {
    if (newData && Array.isArray(newData.data)) {
      if (newUser?.roleName === "ADMIN") {
        newData.data.forEach((client) => {
          const roomExists = chatRooms.value.some(
            (room) => room.id === `private-${client.id}`
          );
          if (!roomExists) {
            chatRooms.value.push({
              id: `private-${client.id}`,
              name: `Chat với ${client.name}`,
            });
          }
        });
      }
    }

    if (newUser?.roleName === "CLIENT") {
      const privateRoomId = `private-${newUser.id}`;
      const roomExists = chatRooms.value.some(
        (room) => room.id === privateRoomId
      );
      if (!roomExists) {
        chatRooms.value.push({ id: privateRoomId, name: `Chat với Admin` });
      }
    }
  },
  { immediate: true }
);

//kết nối ws
const connectWebSocket = () => {
  stompClient.value = new Client({
    brokerURL: "ws://localhost:6868/ws",
    onConnect: (frame) => {
      console.log("Đã kết nối: " + frame);
      subscribeToRoom(selectedRoom.value);

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

const subscribedRooms = ref(new Set());

// Hủy đăng ký phòng trước khi đăng ký lại
const unsubscribeFromRoom = (roomId) => {
  if (!stompClient.value) return;

  stompClient.value.unsubscribe(
    `/topic/${roomId === "public" ? "public" : "private/" + roomId}`
  );
  subscribedRooms.value.delete(roomId);
};

const roomsWithNewMessages = ref(new Set());

//chuyển phòng
const subscribeToRoom = (roomId) => {
  if (!stompClient.value) return;

  stompClient.value.subscribe(
    `/topic/${roomId === "public" ? "public" : "private/" + roomId}`,
    (payload) => {
      const newMessage = JSON.parse(payload.body);

      const exists = messages.value.some(
        (msg) => msg.timestamp === newMessage.timestamp
      );

      if (!exists) {
        messages.value.push(newMessage);
      }

      // Nếu đang không ở phòng đó thì đánh dấu có tin mới
      if (!chatVisible.value || selectedRoom.value !== roomId) {
        roomsWithNewMessages.value.add(roomId);
      }

      scrollToBottom();
    }
  );
};



//xử lý gửi tin nhắn
const sendMessage = () => {
  if (message.value.trim() && stompClient.value) {
    const timestamp = new Date().toISOString();
    const chatMessage = {
      sender: authStore?.user ? authStore?.user?.userName : "Guest",
      content: message.value,
      type: "CHAT",
      timestamp: timestamp,
    };

    // Kiểm tra nếu tin nhắn đã có trong messages
    const existingMessageIndex = messages.value.findIndex(
      (msg) => msg.timestamp === chatMessage.timestamp
    );

    if (existingMessageIndex === -1) {
      stompClient.value.publish({
        destination: `/app/sendMessage/${selectedRoom.value}`,
        body: JSON.stringify(chatMessage),
      });

      messages.value.push(chatMessage);
      scrollToBottom(); 
    }

    message.value = "";
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

const handleRoomChange = () => {
  if (previousMessages.value[selectedRoom.value]) {
    messages.value = previousMessages.value[selectedRoom.value];
  }
  // messages.value = [];
  subscribeToRoom(selectedRoom.value);
};

watchEffect(async () => {
  if (chatHistory.value) {
    messages.value = [...chatHistory.value]; // Gán lịch sử tin nhắn vào messages
    scrollToBottom();
  }
});
</script>

<template>
  <div class="chat-wrapper" :key="renderKey" v-show="authStore.isAuthenticated">
    <a-tooltip title="Cộng đồng">
      <button class="chat-toggle" @click="toggleChat">
        <WechatOutlined class="icon-chat" />
        <span v-if="newMessagesCount > 0" class="message-badge">Mới</span>
      </button>
    </a-tooltip>

    <div v-if="chatVisible" class="chat-container">
      <div class="chat-header">
        CHAT
        <span>
          <select
            v-model="selectedRoom"
            @change="handleRoomChange"
            class="room-select"
          >
            <option v-for="room in chatRooms" :key="room.id" :value="room.id">
              {{ room.name }}
            </option>
          </select>
        </span>
        <button class="close-chat-btn me-2" @click="toggleChat">x</button>
      </div>

      <div ref="chatBodyRef" class="chat-body">
        <ul class="message-list">
          <li v-if="isHistoryLoading" class="loading-message">
            🔄 Đang tải tin nhắn...
          </li>

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
                  <span class="timestamp">{{
                    dateFormatChatBox(msg.timestamp)
                  }}</span>
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
  bottom: 145px;
  right: 10px;
  background: #6c757d;
  color: white;
  border: none;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 6px 12px rgba(251, 247, 247, 0.2);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  z-index: 9999;
}

.chat-toggle:hover {
  transform: scale(1.1);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
}

.icon-chat {
  font-size: 24px; /* Điều chỉnh kích thước icon nếu cần */
  color: white; /* Thay đổi màu sắc nếu cần */
}

.message-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #ff0000;  /* Màu nền của badge */
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 50%;
  z-index: 9999; /* Đảm bảo badge hiển thị trên icon */
}

/* Căn chỉnh chat box */
.chat-container {
  position: fixed;
  bottom: 0;
  right: 70px;
  width: 350px;
  height: 450px;
  max-height: 500px;
  overflow-y: auto;
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  z-index: 9999;
}

/* Căn chỉnh header */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  padding-bottom: 10px;
  border-bottom: 1px solid #ccc;
}

.chat-header > h2 {
  margin-right: auto; /* Đẩy tiêu đề sang trái */
}

.room-select {
  margin-left: 10px;
  margin-right: 10px;
  padding: 5px;
  border-radius: 4px;
  border: 1px solid #ccc;
  font-size: 12px;
  background: white;
}

.close-chat-btn {
  padding: 2px 5px;
  background-color: #f44336;
  background-color: #434249;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 12px;
}

.close-chat-btn:hover {
  background-color: #81969c;
}

.chat-body {
  flex-grow: 1;
  overflow-x: hidden;
  word-break: break-word;
  overflow-x: auto;
}

.message-list {
  list-style-type: none;
  padding: 0;
}

.message {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 0.9rem;
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
  padding: 6px 10px;
  border-radius: 18px;
  max-width: 75%;
  margin-bottom: 4px;
  word-wrap: break-word;
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
  font-size: 12px;
  text-transform: uppercase;
  margin-right: 10px;
}

.sender {
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: black;
}

.message-content {
  max-width: calc(100% - 40px);
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: normal;
}

.content {
  margin-left: 5px;
  word-wrap: break-word;
  color: black;
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

.timestamp {
  display: block;
  font-size: 0.6rem;
  color: #888;
  margin-top: 3px;
}
</style>
