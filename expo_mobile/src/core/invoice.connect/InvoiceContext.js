import React, { createContext, useState, useRef, useContext } from 'react';
import { Alert } from "react-native";
import { Client } from '@stomp/stompjs';
import { SOCKET_TARGET, BASE_URL_SERVER } from '@env';
import { useNotification } from '../../component/common/NotificationContext';
import axios from 'axios';

export const InvoiceContext = createContext();

export const InvoiceProvider = ({ children }) => {
  const [invoiceId, setInvoiceId] = useState(null);
  const [staffId, setStaffId] = useState(null);
  const [cartItems, setCartItems] = useState([]);
  const [isConnected, setIsConnected] = useState(false);

  const stompClientRef = useRef(null);
  const { showNotification, showConfirm } = useNotification();

  const initStompClient = () => {
    if (!stompClientRef.current) {
      console.log("JcbHvs");
      const client = new Client({
        brokerURL: SOCKET_TARGET,
        reconnectDelay: 5000,
        forceBinaryWSFrames: true,
        appendMissingNULLonIncoming: true,
        debug: (msg) => console.log(msg),
      });

      stompClientRef.current = client;
    }
  };

  const connectToInvoice = (id) => {
    initStompClient();
    const stompClient = stompClientRef.current;

    setInvoiceId(id);

    const request = {
      invoiceId: id,
      staffId: staffId,
      invoiceStatus: "PENDING",
      confirmedTime: null,
    };

    stompClient.onConnect = () => {
      stompClient.publish({
        destination: "/app/connect-invoice",
        body: JSON.stringify(request),
      });

      var subc = stompClient.subscribe(`/topic/invoice/${id}`, (message) => {
        try {
          const parsed = JSON.parse(message.body);
          if (parsed.connectConfirmed) {
            showNotification(`Kết nối thành công: ${parsed.invoiceId}`);
            setIsConnected(true);
            rerender(parsed.invoiceId);
            listen(parsed.invoiceId);
          } else {
            showNotification(`Từ chối kết nối hóa đơn: ${parsed.invoiceId}`);
            setIsConnected(false);
          }
        } catch (err) {
          console.error('Lỗi khi parse message:', err);
          setIsConnected(false);
          subc.unSubscribe();
        }
      });
    };

    stompClient.onStompError = (frame) => {
      console.warn('🔴 Lỗi STOMP:', frame);
    };

    stompClient.activate();
  };

  const disconnect = async () => {
    const stompClient = stompClientRef.current;
    if (stompClient && stompClient.active) {
      await stompClient.deactivate();
      console.log('❌ Đã ngắt kết nối');
    }

    setInvoiceId(null);
    setStaffId(null);
    setCartItems([]);
    setIsConnected(false);
  };

  const listen = (id) => {
    const stompClient = stompClientRef.current;

    stompClient.subscribe(`/topic/disconnect-mobile/${id}`, () => {
      disconnect();
      showNotification('Đã hủy kết nối');
    });

    stompClient.subscribe(`/topic/update-cart-mb/${id}`, () => {
      rerender(id);
    });

    stompClient.subscribe(`/topic/payment/${id}`, () => {
      confirmPayment(id);
    });

    stompClient.subscribe(`/topic/transaction-info/${id}`, (message) => {
      const transactionStatus = JSON.parse(message.body);
          if (transactionStatus) {
            disconnect();
            showNotification(`Thanh toán thành công`);
          } else {
            showNotification(`Hóa đơn ${id} chưa hoàn thành`);
          }
    });
  };
  

  const confirmPayment = (invoiceId) => {
    const stompClient = stompClientRef.current;
  
    showConfirmCountdown(
      invoiceId,
      () => {
        stompClient.publish({
          destination: "/app/confirm-payment-mb",
          body: JSON.stringify({
            invoiceId,
            transactionStatus: true,
          }),
        });
      },
      () => {
        stompClient.publish({
          destination: "/app/confirm-payment-mb",
          body: JSON.stringify({
            invoiceId,
            transactionStatus: false,
          }),
        });
      }
    );
  };

  
  const rerender = async (invoiceId) => {
    try {
      const response = await axios.get(`${BASE_URL_SERVER}/api/v1/admin/point-of-sale/products-in-order/${invoiceId}`);
      setCartItems(response.data.data);
    } catch (error) {
      console.error('Lỗi khi lấy dữ liệu giỏ hàng:', error);
    }
  };

  const sendUpdateQuantity = (updateQuantityInput) => {
    const stompClient = stompClientRef.current;
    if (stompClient?.connected) {
      stompClient.publish({
        destination: "/app/update-cart",
        body: JSON.stringify(updateQuantityInput),
      });
    }
  };

  const sendDeleteProductInCart = (invoiceId, invoiceDetailId) => {
    const stompClient = stompClientRef.current;
    if (stompClient?.connected) {
      stompClient.publish({
        destination: "/app/delete-pd-cart",
        body: invoiceId + ',' + invoiceDetailId,
      });
    }
  };

  const sendMessWhenDisconnect = () => {
    const stompClient = stompClientRef.current;
    if (stompClient?.connected) {
      stompClient.publish({
        destination: "/app/disconnect-client",
        body: invoiceId,
      });
    } else {
      console.warn("❌ Không có kết nối STOMP để gửi disconnect.");
    }
  };



 const showConfirmCountdown = (invoiceId, onOk, onCancel) => {
    let secondsLeft = 5;
    let resolved = false;
  
    const timer = setInterval(() => {
      secondsLeft--;
      if (secondsLeft === 0 && !resolved) {
        clearInterval(timer);
        resolved = true;
        onCancel();
      }
    }, 1000);
  
    const askConfirm = () => {
      Alert.alert(
        "Xác nhận thanh toán",
        `Vui lòng xác nhận thanh toán! (${secondsLeft}s)`,
        [
          {
            text: "Hủy",
            onPress: () => {
              if (!resolved) {
                resolved = true;
                clearInterval(timer);
                onCancel();
              }
            },
            style: "cancel",
          },
          {
            text: "Xác nhận",
            onPress: () => {
              if (!resolved) {
                resolved = true;
                clearInterval(timer);
                onOk();
              }
            },
          },
        ],
        { cancelable: false }
      );
    };
  
    askConfirm();
  };

  return (
    <InvoiceContext.Provider
      value={{
        invoiceId,
        cartItems,
        setCartItems,
        isConnected,
        connectToInvoice,
        disconnect,
        rerender,
        sendMessWhenDisconnect,
        sendDeleteProductInCart,
      }}
    >
      {children}
    </InvoiceContext.Provider>
  );
};
