import { Client } from '@stomp/stompjs';
import { SOCKET_TARGET } from '@env';

let stompClient;

export const connectToInvoice = (invoiceId, staffId) => {
  stompClient = new Client({
    brokerURL: SOCKET_TARGET,
    reconnectDelay: 5000,
    forceBinaryWSFrames: true,
    appendMissingNULLonIncoming: true,
    debug: (msg) => console.log('[STOMP]', msg),
  });

  stompClient.onConnect = () => {
    console.log("✅ WebSocket connected");

    // 1. Gửi yêu cầu kết nối hóa đơn
    stompClient.publish({
      destination: "/app/connect-invoice",
      body: JSON.stringify({
        invoiceId,
        staffId,
        invoiceStatus: "PENDING",
        confirmedTime: null,
      }),
    });

    // 2. Lắng nghe các cập nhật hóa đơn
    stompClient.subscribe(`/topic/invoice/${invoiceId}`, (message) => {
      const invoice = JSON.parse(message.body);
      console.log("📦 Hóa đơn cập nhật:", invoice);
    });

    // 3. Lắng nghe xác nhận thanh toán
    stompClient.subscribe(`/topic/payment/${invoiceId}`, (message) => {
      const invoice = JSON.parse(message.body);
      if (invoice.invoiceStatus === 'PAID') {
        alert("✅ Đã thanh toán");
      } else {
        alert("⚠️ Chưa thanh toán hoặc thất bại");
      }
    });
  };

  stompClient.activate();
};


export const sendCartUpdate = (invoiceId, staffId) => {
  stompClient.publish({
    destination: "/app/update-cart",
    body: JSON.stringify({
      invoiceId,
      staffId,
      invoiceStatus: "CART_UPDATED",
      confirmedTime: null,
    }),
  });
};


export const sendPaymentConfirmation = (invoiceId, staffId) => {
  stompClient.publish({
    destination: "/app/confirm-payment",
    body: JSON.stringify({
      invoiceId,
      staffId,
      invoiceStatus: "PAID",
      confirmedTime: Date.now(),
    }),
  });
};


export const disconnectSocket = () => {
  if (stompClient && stompClient.connected) {
    stompClient.deactivate().then(() => {
      console.log("🔌 Đã ngắt WebSocket");
    });
  }
};
