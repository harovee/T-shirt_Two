<template>
  <div>
    <h1>Vue WebSocket</h1>
    <div v-if="message">
      <p>📩 Tin nhắn mới: {{ message }}</p>
      <button @click="confirm(true)">✅ OK</button>
      <button @click="confirm(false)">❌ Cancel</button>
    </div>
  </div>
</template>

<script>
import { Client } from '@stomp/stompjs';

export default {
  data() {
    return {
      message: '',
      client: null,
    };
  },
  mounted() {
    this.connectWebSocket();
  },
  methods: {
    connectWebSocket() {
      this.client = new Client({
        brokerURL: 'http://localhost:6868/ws',
        reconnectDelay: 5000,
        debug: (msg) => console.log('Debug: ', msg),
      });

      this.client.onConnect = () => {
        console.log('🟢 Đã kết nối WebSocket');
        this.client.subscribe('/topic/confirm', (msg) => {
          this.message = msg.body;
        });
      };

      this.client.activate();
    },
    confirm(isConfirmed) {
      alert(isConfirmed ? '✅ Đã xác nhận!' : '❌ Đã hủy!');
      this.message = '';
    },
  },
};
</script>

<style scoped>
button {
  margin: 5px;
  padding: 8px 16px;
  cursor: pointer;
}
</style>
