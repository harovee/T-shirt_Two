import { onMounted, onUnmounted, ref } from 'vue';
import SockJS from 'sockjs-client';
import Stomp, { Client, Message, Subscription } from 'stompjs';

const messages = ref<string[]>([]);
let stompClient: Client | null = null;
let subscription: Subscription | null = null;

export function useNotificationSocket() {
  onMounted(() => {
    const socket = new SockJS("http://localhost:6868/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
      console.log("✅ WebSocket connected");
      subscription = stompClient!.subscribe("/topic/notification", (message: Message) => {
        console.log("🔔 Received:", message.body);
        messages.value.push(message.body);
      });
    });
  });

  onUnmounted(() => {
    subscription?.unsubscribe();
    stompClient?.disconnect(() => {
      console.log("🔌 WebSocket disconnected");
    });
  });

  return { messages };
}