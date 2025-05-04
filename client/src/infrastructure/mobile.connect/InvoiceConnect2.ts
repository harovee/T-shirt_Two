
import { Client } from '@stomp/stompjs';
import { createVNode, ref } from "vue";
import {
  warningNotiSort,
  successNotiSort,
  errorNotiSort,
  notificationType,
  openNotification,
} from "@/utils/notification.config";
import { DOMAIN_BACKEND } from '../constants/url';
import { ROUTES_CONSTANTS } from '../constants/path';
import router from '../routes/router';
import { updateBillWait } from '../services/api/admin/bill.api';

interface Address {
  province: string
  district: string
  ward: string
  detail: string
  receiver?: string
  phone: string
  note?: string
}

interface CustomerInfo {
  name: string
  phone: string
  address: Address | null
  email: string
}

interface Shipping {
  method: string
  cost: number
  estimatedDelivery: string
}

export interface Item {
  id: string
  name: string
  quantity: number
  price: number
  total: number
  image: string
  size: string
  color: string
  sku: string
}

interface Voucher {
  id: string
  code: string
  name: string
  discount: number
  type: "fixed" | "percent"
  maxDiscount: number | null
}

export interface InvoiceData {
  id: string
  items: Item[]
  subtotal: number
  tax: number
  total: number
  customerInfo: CustomerInfo
  paymentMethod: string
  shipping?: Shipping | null
  vouchers?: Voucher[]
  guestMoney: number | null
  transferMoney?: number | null
}

export const invoices = ref<InvoiceData[]>([])
export const mbConnectStatus = ref<boolean>(false)
export const currentInvoiceUUID = ref<string>("")
export const currentPayloadPaymentInfo = ref<any>(null)

export const currentInvoice = ref({
  id: "#####",
  items: [
    {
      id: "SPCT001",
      name: "Áo một",
      quantity: 2,
      price: 850000,
      total: 1700000,
      image: "https://example.com/images/casio-mtp-v002l.jpg",
      size: "M",
      color: "Đen",
      sku: "CASIO-MTP-V002L-1BUDF"
    },
    {
      id: "SPCT002",
      name: "Áo hai",
      quantity: 1,
      price: 2300000,
      total: 2300000,
      image: "https://example.com/images/seiko5-snk809.jpg",
      size: "S",
      color: "Đen",
      sku: "SEIKO-SNK809K2"
    },
    // {
    //   id: "SPCT003",
    //   name: "Áo ba",
    //   quantity: 1,
    //   price: 1500000,
    //   total: 1500000,
    //   image: "https://example.com/images/seiko5-snk809.jpg",
    //   size: "XL",
    //   color: "Đen",
    //   sku: "SEIKO-SNK809K2"
    // }
  ],
  subtotal: 0,
  tax: 0,
  total: 0,
  customerInfo: {
    name: "Khách lẻ",
    phone: "#####",
    email: "#####",
    address: null
  },
  paymentMethod: "Chuyển khoản",
  // shipping: {
  //   method: "GHN",
  //   cost: 30000,
  //   estimatedDelivery: "2025-04-24"
  // },
  shipping: null,
  vouchers: [],
  guestMoney: 0,
  transferMoney: 0
} as InvoiceData);



const stompClient = new Client({
  brokerURL: DOMAIN_BACKEND + "/ws",
  reconnectDelay: 5000,
  forceBinaryWSFrames: true,
  appendMissingNULLonIncoming: true,
  onConnect: () => {
    console.warn('🟢 Kết nối thành công!');
    subscribeInvoice2()
  },
  debug: (msg) => console.log('Debug: ', msg),
});
stompClient.activate();


///////////////
// Exist Invoice
export const isExistInvoice = (invoiceId: string) => {
  return invoices.value.some(invoice => invoice.id === invoiceId);
};

export const getInvoiceById = (invoiceId: string) => {
  return invoices.value.find(invoice => invoice.id === invoiceId) || null;  
}

export const addInvoice = (id: string): boolean => {
  if (!isExistInvoice(id) && id !== ""){
    let newInvoice = createEmptyInvoice(id);
    invoices.value.push(newInvoice);
    currentInvoice.value = newInvoice; 
    console.log("Thêm hóa đơn mới:", newInvoice);
    return true;
  } else {
    console.warn(`Hóa đơn với ID ${id} đã tồn tại.`);
    return false; 
  }
};

export const createEmptyInvoice = (id: string): InvoiceData => {
  return {
    id,
    items: [],
    subtotal: 0,
    tax: 0,
    total: 0,
    customerInfo: {
      name: "Khách lẻ",
      phone: "#####",
      email: "#####",
      address: null
    },
    paymentMethod: "Tiền mặt",
    shipping: null,
    vouchers: [],
    guestMoney: 0,
    transferMoney: 0
  };
};

const getAmount = (items: Item[]) => {
  return items.reduce((sum, item) => sum + item.price * item.quantity, 0) 
}

// Gửi cập nhật màn app
export const sendCartInfo = (cartInfo: InvoiceData) => {
  cartInfo.subtotal = getAmount(cartInfo.items);
  // console.log("Gửi thông tin giỏ hàng đến app:", cartInfo);
  stompClient.publish({ destination:"/app/cart-info", body: JSON.stringify(cartInfo) });
};

// Gửi xác nhận thanh toán
export const sendPaymentConfirm = (invoiceId: string) => {
  stompClient.publish({ destination:"/app/payment-confirm", body: JSON.stringify({invoiceId: invoiceId, status: null}) });
};

// Hàm lấy đối tượng địa chỉ START
export const convertAddressForApp = (response: any) => {
  const parts = response.split(',').map(s => s.trim()).filter(s => s !== '');

  let ward = '';
  let district = '';
  let province = '';
  let detail = '';
  let receiver = '';
  let phone = '';
  let note = '';

  if (parts.length > 0) {
    province = parts[parts.length - 1];
  }
  if (parts.length > 1 && !parts[parts.length - 2].includes('-- Chọn')) {
    district = parts[parts.length - 2];
  }
  if (parts.length > 2 && !parts[parts.length - 3].includes('-- Chọn')) {
    ward = parts[parts.length - 3];
  }
  detail = parts.slice(0, parts.length - 3).filter(p => !p.includes('-- Chọn')).join(', ');

  return {
    province,
    district,
    ward,
    detail,
    receiver,
    phone,
    note
  };
}

export const subscribeInvoice2 = () => {
  // Lắng nghe thông báo từ server
  let subcr = stompClient.subscribe(`/topic/pos`, (message) => {
    const parsedBody = JSON.parse(message.body);
    handleWebSocketMessage(parsedBody);
 });

}

const handleWebSocketMessage = (message: any) => {
  try {
    const parsedMessage = typeof message === "string" ? JSON.parse(message) : message
    console.log("Received message from POS:", parsedMessage)

    switch (parsedMessage.type) {
      case "connection_status":
        if (parsedMessage.data && parsedMessage.data.status) {
            if (parsedMessage.data.status === "connected") {
              mbConnectStatus.value = true;
              // successNotiSort("Kết nối thành công với MB!");
            }else {
              mbConnectStatus.value = false;
              // errorNotiSort("Không có kết nối từ APP POS!");
            }
          }
        break
        case "payment_confirmed":
          if (parsedMessage.data && parsedMessage.data.status) {
              if (parsedMessage.data.status === "YES_CONFIRM") {
                console.log("Xác nhận thanh toán thành công từ MB!");
                // Gọi hoàn thành thanh toán ở đây
                completePayment(currentPayloadPaymentInfo.value, currentInvoiceUUID.value);
                successNotiSort("Xác nhận thanh toán thành công từ MB!");
              }else {
                console.log("Xác nhận thanh toán thành công từ MB!");
                errorNotiSort("Khách từ chối thanh toán!");
              }
            }
          break
    }
  }catch (error) {
    console.error("Error parsing message:", error)
  }
}

export const completePayment = async (payload: any, invoiceUUID: string) => {
  try {
    await updateBillWait(
      invoiceUUID,
      payload
    );
    successNotiSort("Thanh toán thành công!");
    router.push(
      ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_MANAGEMENT.path
    );
  } catch (error: any) {
    console.error("🚀 ~ handleCreate ~ error:", error);
    if (error?.response) {
      errorNotiSort(error?.response?.data?.message);
    }
  }
}

///////////////
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
     let subcr = stompClient.subscribe(`/topic/pos`, (message) => {
      let response = JSON.parse(message.body);
      console.log(`Cập nhật từ hóa đơn ${invoiceId}:`, response);
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
// export const sendPaymentConfirm = (invoiceId: string, onPayment: () => boolean, onMbDisConnect: () => void) => {
//   const subscription = subscriptions[invoiceId];
//       if (subscription) {
//         stompClient.publish({ destination:"/app/confirm-payment", body: invoiceId });
//         subscribeConfirmPayment(invoiceId, onPayment, onMbDisConnect);
//       }
// };


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