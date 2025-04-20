
import { Client } from '@stomp/stompjs';
import { Modal } from "ant-design-vue";  // Import Modal từ ant-design-vue
import { createVNode } from "vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import {
  warningNotiSort,
  successNotiSort,
  errorNotiSort,
  notificationType,
  openNotification,
} from "@/utils/notification.config";
import { DOMAIN_BACKEND } from '../constants/url';

const stompClient = new Client({
  brokerURL: DOMAIN_BACKEND + "/ws",
  reconnectDelay: 5000,
  forceBinaryWSFrames: true,
  appendMissingNULLonIncoming: true,
  onConnect: () => {
    console.warn('🟢 Kết nối thành công!');
  },
  debug: (msg) => console.log('Debug: ', msg),
});
stompClient.activate();

let subscriptions: { [key: string]: any } = {}; // Lưu trữ các subscription cho mỗi invoiceId

// Kết nối WebSocket và subscribe cho nhiều hóa đơn
export const listenToInvoice = (invoiceId: string, onConnectConfirmed: () => void, onDisconnect: () => void) => {
  if ( !stompClient.connected ) {
    stompClient.onConnect = () => {
      console.log("Kết nối thành công!");
      subscribeInvoice(invoiceId, onConnectConfirmed, onDisconnect);
    };
  }else if ( !subscriptions[invoiceId] ) {
      subscribeInvoice(invoiceId, onConnectConfirmed, onDisconnect);
  }
};


export const subscribeInvoice = (invoiceId, onConnectConfirmed: () => void, onDisconnect: () => void) => {

     // Subscribe cho mỗi invoiceId khi kết nối thành công
     let subcr = stompClient.subscribe(`/topic/connect-confirm/${invoiceId}`, (message) => {
      let response = JSON.parse(message.body);

      console.log(`Cập nhật từ hóa đơn ${invoiceId}:`, response);

      Modal.confirm({
        title: "Xác nhận kết nối",
        content: `Có yêu cầu kết nối từ hóa đơn ${invoiceId}. Bạn có muốn tiếp tục?`,
        icon: createVNode(ExclamationCircleOutlined),
        centered: true,
        onOk() {
          console.log(`Đã đồng ý kết nối với hóa đơn ${invoiceId}`);
          onConnectConfirmed();
          response.connectConfirmed = true;
          response.connectConfirmedTime = Math.floor(Date.now() / 1000);
          sendConnectConfirm(response);


          subscribeDisconnectClient(invoiceId, onDisconnect);
          // Có thể gọi hàm để thực hiện kết nối thực tế hoặc cập nhật dữ liệu gì đó
        },
        onCancel() {
          console.log(`Đã từ chối kết nối với hóa đơn ${invoiceId}`);
          response.connectConfirmed = false;
          response.connectConfirmedTime = Math.floor(Date.now() / 1000);
          sendConnectConfirm(response);
        },
      });
    });

    // Lưu subscription vào object subscriptions để quản lý
    subscriptions[invoiceId] = subcr;

}

// Gửi thông báo kết nối thành công
export const sendConnectConfirm = (item) => {
  stompClient.publish({ destination:"/app/connect-confirm", body: JSON.stringify(item) });
};

// Gửi cập nhật giỏ hàng mb
export const sendCartUpdate = (invoiceId: string) => {
  stompClient.publish({ destination:"/app/update-cart-mb", body: invoiceId });
};

// Gửi xác nhận thanh toán
export const sendPaymentConfirm = (invoiceId: string, onPayment: () => boolean, onMbDisConnect: () => void) => {
  const subscription = subscriptions[invoiceId];
      if (subscription) {
        stompClient.publish({ destination:"/app/confirm-payment", body: invoiceId });
        subscribeConfirmPayment(invoiceId, onPayment, onMbDisConnect);
      }
};


// Gửi thông tin giao dịch
export const sendTransactionInfo = (invoiceId: string, transactionStatus: boolean) => {
  const subscription = subscriptions[invoiceId];
      if (subscription) {
        stompClient.publish({ 
          destination:"/app/transaction-info",
          body: JSON.stringify({
            invoiceId: invoiceId,
            transactionStatus: transactionStatus})
           });
      }
}

type UpdateQuantityInput = {
   invoiceId: string;
   productId: string;
   quantity: number;
   orderDetailId: string;
}

// Lắng nghe cập nhật giỏ hàng - quantity
export const subscribeUpdateProductQuantity = (invoiceId: string) => {
  var subcr = stompClient.subscribe(`/topic/update-cart/${invoiceId}`, (message) => {
    let response = JSON.parse(message.body);
    console.log(`Cập nhật từ hóa đơn ${invoiceId}:`, response);
  });
};

// Lắng nghe cập nhật gio hàng - xoa
export const subscribeDeleteProductInCart= (invoiceId: string, orderDetailId: string) => {
  var subcr = stompClient.subscribe(`/topic/delete-pd-cart/${invoiceId}`, (message) => {
    let response = JSON.parse(message.body);
    console.log(`Xóa pd trong ${orderDetailId}:`, response);
    subcr.unsubscribe();
  });
};

// Lắng nghe phản hồi xác nhận thanh toán
export const subscribeConfirmPayment= (invoiceId: string, onPayment: () => boolean, onMbDisConnect: () => void) => {
  var subcr = stompClient.subscribe(`/topic/confirm-payment/${invoiceId}`, (message) => {
    let response = JSON.parse(message.body) as Boolean;
    if (response) {
      const transactionStatus  = onPayment();
      onMbDisConnect();
      sendTransactionInfo(invoiceId, transactionStatus);

      subscriptions[invoiceId].unsubscribe();
      subcr.unsubscribe();

    }
  });
  
};

// Lắng nghe ngắt kết nối tử MB
export const subscribeDisconnectClient= (invoiceId: string, onMbDisConnect: () => void) => {
  var subcr = stompClient.subscribe(`/topic/disconnect-client/${invoiceId}`, (message) => {
    disconnectSocketByInvoiceId(invoiceId, onMbDisConnect);
    successNotiSort(`Đã ngắt kết nối cho hóa đơn ${invoiceId}`);
    subcr.unsubscribe();
  });
  
};

// Ngắt kết nối WebSocket
export const disconnectSocket = () => {
  if (stompClient && stompClient.connected) {
    stompClient.onDisconnect = () => {
      console.log("Đã ngắt kết nối WebSocket (Vue).");
      Object.keys(subscriptions).forEach(invoiceId => {
        subscriptions[invoiceId].unsubscribe();
      });
      subscriptions = {};
    };
  }
};


// Ngắt kết nối WebSocket cho một invoiceId cụ thể
  export const disconnectSocketByInvoiceId = (invoiceId: string | null, onMbDisConnect: () => void ) => {
    if (invoiceId && stompClient && stompClient.connected) {
      const subscription = subscriptions[invoiceId];
      if (subscription) {
        onMbDisConnect();
        stompClient.publish({ destination:"/app/disconnect-mobile", body: invoiceId });
        console.log(`Đã ngắt kết nối WebSocket cho hóa đơn ${invoiceId}`);
      } else {
        console.log(`Không tìm thấy kết nối WebSocket cho hóa đơn ${invoiceId}`);
      }
    }
  };