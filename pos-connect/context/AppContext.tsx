"use client"

import type React from "react"
import { useState, createContext, useContext, useEffect, useCallback, useRef } from "react"
import { Alert } from "react-native"
import SockJS from "sockjs-client"
import { Client } from "@stomp/stompjs"

// Define the types
interface Address {
  province: string
  district: string
  ward: string
  detail: string
}

interface CustomerInfo {
  name: string
  phone: string
  address: Address
  email: string
}

interface Shipping {
  method: string
  cost: number
  estimatedDelivery: string
}

interface Item {
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
  code: string
  discount: number
  type: "fixed" | "percent"
}

interface InvoiceData {
  id: string
  items: Item[]
  subtotal: number
  tax: number
  total: number
  customerInfo: CustomerInfo
  paymentMethod: string
  shipping?: Shipping
  vouchers?: Voucher[]
  guestMoney: number | 0
  transferMoney?: number | null
}

// Định nghĩa interface cho WebSocket messages
interface WebSocketMessage {
  type: string
  data?: any
}

interface AppContextType {
  invoiceData: InvoiceData | null
  setInvoiceData: React.Dispatch<React.SetStateAction<InvoiceData | null>>
  disconnectFromPOS: (navigation: any) => void
  connectionStatus: "disconnected" | "connecting" | "connected"
  updateShippingAddress: (address: Address) => void
  applyVoucher: (voucherCode: string) => void
  confirmPayment: () => void
  rejectPayment: () => void
  registerNavigation: (navigation: any) => void
  checkInvoiceStatus: () => boolean
  sendMessage: (message: WebSocketMessage) => void
  showPaymentConfirmation: boolean
  setShowPaymentConfirmation: React.Dispatch<React.SetStateAction<boolean>>
  reconnectToPOS: () => void
}

// Create the context
const AppContext = createContext<AppContextType | undefined>(undefined)

// Create a provider component
export const AppProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [invoiceData, setInvoiceData] = useState<InvoiceData | null>(null)
  const [connectionStatus, setConnectionStatus] = useState<"disconnected" | "connecting" | "connected">("disconnected")
  const [navigation, setNavigation] = useState<any>(null)
  const [showPaymentConfirmation, setShowPaymentConfirmation] = useState(false)

  // Tham chiếu đến STOMP client
  const stompClientRef = useRef<Client | null>(null)
  const subscriptionRef = useRef<any>(null)

  // Lấy địa chỉ IP từ biến môi trường
  const SERVER_IP = process.env.SERVER_IP || "192.168.1.100"
  const SERVER_PORT = process.env.SERVER_PORT || "8080"

  // Sửa lại URL để sử dụng http thay vì ws
  const SERVER_URL = `ws://${SERVER_IP}:${SERVER_PORT}/ws`

  // Gửi tin nhắn qua STOMP
  const sendMessage = (message: WebSocketMessage) => {
    if (connectionStatus !== "connected" || !stompClientRef.current) {
      console.log("Cannot send message: Not connected to POS")
      return
    }

    console.log("Sending message to POS:", message)

    // Gửi tin nhắn qua STOMP
    const destination = `/app/${message.type}`
    stompClientRef.current.publish({
      destination,
      body: JSON.stringify(message.data || {}),
    })
  }

  // Xử lý tin nhắn nhận được từ STOMP
  const handleWebSocketMessage = (message: any) => {
    try {
      const parsedMessage = typeof message === "string" ? JSON.parse(message) : message
      console.log("Received message from POS:", parsedMessage)

      switch (parsedMessage.type) {
        case "invoice_update":
          // Cập nhật dữ liệu hóa đơn
          console.log("Invoice update received:", parsedMessage.data)
          setInvoiceData(parsedMessage.data)
          break

        case "payment_request":
          // Hiển thị màn hình thanh toán
          if (navigation) {
            navigation.navigate("Payment")
          }
          break

        case "payment_confirmation":
          // Hiển thị modal xác nhận thanh toán với đếm ngược 5 giây
          setShowPaymentConfirmation(true)
          break

        case "voucher_applied":
          // Áp dụng voucher
          if (parsedMessage.data && parsedMessage.data.code) {
            applyVoucher(parsedMessage.data.code)
          }
          break

        case "connection_status":
          // Cập nhật trạng thái kết nối
          if (parsedMessage.data && parsedMessage.data.status) {
            setConnectionStatus(parsedMessage.data.status)
          }
          break

        case "invoice_clear":
          // Xóa dữ liệu hóa đơn hiện tại
          setInvoiceData(null)
          // Chuyển về màn hình Home
          if (navigation) {
            navigation.navigate("Home")
          }
          break
      }
    } catch (error) {
      console.error("Error handling WebSocket message:", error)
    }
  }

  // Thiết lập kết nối STOMP
  const setupStompConnection = () => {
    try {
      console.log(`Setting up STOMP connection to: ${SERVER_URL}`)
      setConnectionStatus("connecting")

      const client = new Client({
          brokerURL: SERVER_URL,
          reconnectDelay: 5000,
          forceBinaryWSFrames: true,
          appendMissingNULLonIncoming: true,
          debug: (msg) => console.log('[STOMP]', msg),
        });

      // Thêm timeout để kiểm tra kết nối
      let connectionTimeout: NodeJS.Timeout | null = null

      // Xử lý khi kết nối thành công
      client.onConnect = (frame) => {
        console.info("Connected to STOMP:", frame)
        setConnectionStatus("connected")

        // Xóa timeout nếu kết nối thành công
        if (connectionTimeout) {
          clearTimeout(connectionTimeout)
          connectionTimeout = null
        }

        // Đăng ký nhận tin nhắn từ topic chung
        const subscription = client.subscribe(`/topic/pos`, (message) => {
          try {
            const parsedBody = JSON.parse(message.body)
            handleWebSocketMessage(parsedBody)
          } catch (error) {
            console.error("Error parsing message:", error)
          }
          
        })

        // Lưu subscription để có thể hủy đăng ký sau này
        subscriptionRef.current = subscription

        // Gửi tin nhắn kết nối
        client.publish({
          destination: `/app/connect`,
          body: JSON.stringify({}),
        })
      }

      // Xử lý khi mất kết nối
      client.onDisconnect = () => {
        console.log("Disconnected from STOMP")
        setConnectionStatus("disconnected")
      }

      // Xử lý lỗi
      client.onStompError = (frame) => {
        console.error("STOMP error:", frame)
        setConnectionStatus("disconnected")
        Alert.alert("Lỗi kết nối", "Không thể kết nối đến máy quầy POS")
      }

      // Thêm xử lý lỗi WebSocket
      client.onWebSocketError = (event) => {
        console.error("WebSocket error:", event)
      }

      // Thêm xử lý đóng WebSocket
      client.onWebSocketClose = (event) => {
        console.log("WebSocket closed:", event)
      }

      // Kích hoạt kết nối
      client.activate()

      // Thiết lập timeout để kiểm tra kết nối
      connectionTimeout = setTimeout(() => {
        if (connectionStatus !== "connected") {
          console.log("Connection timeout, deactivating client")
          client.deactivate()
          setConnectionStatus("disconnected")
          Alert.alert(
            "Lỗi kết nối",
            "Không thể kết nối đến máy quầy POS. Vui lòng kiểm tra địa chỉ IP và cổng kết nối.",
          )
        }
      }, 10000) // 10 giây timeout

      // Lưu client để sử dụng sau này
      stompClientRef.current = client
    } catch (error) {
      console.error("Error setting up STOMP connection:", error)
      setConnectionStatus("disconnected")
      Alert.alert("Lỗi kết nối", "Không thể thiết lập kết nối STOMP")
    }
  }

  // Ngắt kết nối STOMP
  const disconnectFromPOS = (navigation: any) => {
    // Đóng kết nối STOMP
    if (stompClientRef.current) {
      // Hủy đăng ký subscription
      if (subscriptionRef.current) {
        subscriptionRef.current.unsubscribe()
        subscriptionRef.current = null
      }

      // Ngắt kết nối client
      stompClientRef.current.deactivate()
      stompClientRef.current = null
    }

    setConnectionStatus("disconnected")
    setInvoiceData(null)
    console.log("Disconnected from POS")
    navigation.navigate("Home")
  }

  // Thêm hàm reconnectToPOS vào AppProvider
  // Thêm hàm này sau hàm disconnectFromPOS
  const reconnectToPOS = () => {
    // Nếu đã kết nối, không làm gì cả
    if (connectionStatus === "connected" || connectionStatus === "connecting") {
      return
    }

    // Thiết lập lại kết nối STOMP
    setupStompConnection()
    console.log("Reconnecting to POS...")
  }

  // Cập nhật địa chỉ giao hàng
  const updateShippingAddress = (address: Address) => {
    setInvoiceData((prev) => {
      if (!prev) return prev

      return {
        ...prev,
        customerInfo: {
          ...prev.customerInfo,
          address: address,
        },
      }
    })

    // Gửi cập nhật địa chỉ qua STOMP
    if (stompClientRef.current && invoiceData) {
      stompClientRef.current.publish({
        destination: `/app/update-address`,
        body: JSON.stringify({
          invoiceId: invoiceData.id,
          address: address,
        }),
      })
    }
  }

  // Áp dụng voucher
  const applyVoucher = (voucherCode: string) => {
    // Gửi yêu cầu áp dụng voucher qua STOMP
    if (stompClientRef.current && invoiceData) {
      stompClientRef.current.publish({
        destination: `/app/apply-voucher`,
        body: JSON.stringify({
          invoiceId: invoiceData.id,
          voucherCode: voucherCode,
        }),
      })
    }
  }

  const getAmount = (items: Item[]) => {
    
    return items.reduce((sum, item) => sum + item.price * item.quantity, 0) 

  }


  // Xác nhận thanh toán
  const confirmPayment = () => {
    console.log("Payment confirmed")

    // Gửi xác nhận thanh toán qua STOMP
    if (stompClientRef.current && invoiceData) {
      stompClientRef.current.publish({
        destination: `/app/payment`,
        body: JSON.stringify({
          invoiceId: invoiceData.id,
          status: "confirmed",
        }),
      })
    }
  }

  // Từ chối thanh toán
  const rejectPayment = () => {
    console.log("Payment rejected")

    // Gửi từ chối thanh toán qua STOMP
    if (stompClientRef.current && invoiceData) {
      stompClientRef.current.publish({
        destination: `/app/payment`,
        body: JSON.stringify({
          invoiceId: invoiceData.id,
          status: "rejected",
        }),
      })
    }
  }

  const registerNavigation = (nav: any) => {
    setNavigation(nav)
  }

  const checkInvoiceStatus = () => {
    return invoiceData !== null
  }

  // Kết nối khi component mount
  useEffect(() => {
    setupStompConnection()

    // Cleanup khi component unmount
    return () => {
      // Đóng kết nối STOMP khi unmount
      if (stompClientRef.current) {
        if (subscriptionRef.current) {
          subscriptionRef.current.unsubscribe()
        }
        stompClientRef.current.deactivate()
      }
    }
  }, [])

  const memoizedDisconnectFromPOS = useCallback(disconnectFromPOS, [])

  const value: AppContextType = {
    invoiceData,
    setInvoiceData,
    disconnectFromPOS: memoizedDisconnectFromPOS,
    connectionStatus,
    updateShippingAddress,
    applyVoucher,
    confirmPayment,
    rejectPayment,
    registerNavigation,
    checkInvoiceStatus,
    sendMessage,
    showPaymentConfirmation,
    setShowPaymentConfirmation,
    reconnectToPOS,
  }

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>
}

// Create a custom hook to use the context
export const useAppContext = () => {
  const context = useContext(AppContext)
  if (context === undefined) {
    throw new Error("useAppContext must be used within an AppProvider")
  }
  return context
}

export type { Address, WebSocketMessage }
