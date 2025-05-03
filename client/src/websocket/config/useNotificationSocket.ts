import { onMounted, onUnmounted, ref } from 'vue';
import SockJS from 'sockjs-client';
import Stomp, { Client, Message, Subscription } from 'stompjs';
import { infoNotiSort, successNotiSort } from '@/utils/notification.config';

interface OrderNotification {
  orderId: number;
  content: string;
}
const messages = ref<OrderNotification[]>([]);
let stompClient: Client | null = null;
let subscription: Subscription | null = null;

export function useNotificationSocket() {
  onMounted(() => {
    const socket = new SockJS("http://localhost:6868/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
      console.log("✅ WebSocket connected");
      subscription = stompClient!.subscribe("/topic/notification", (message: Message) => {
      const data = JSON.parse(message.body) as OrderNotification;
        console.log("🔔 Received:", data);
        infoNotiSort(`Có đơn hàng mới cần xác nhận`);
        messages.value.push(data);
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