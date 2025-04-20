"use client"

import type React from "react"
import { useState, createContext, useContext, useEffect, useCallback, useRef } from "react"

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
}

// Thêm interface cho WebSocket messages
interface WebSocketMessage {
  type:
    | "invoice_update"
    | "payment_request"
    | "voucher_applied"
    | "connection_status"
    | "invoice_clear"
    | "payment_confirmation"
  data?: any
}

// Thêm state để kiểm soát hiển thị modal xác nhận thanh toán
// Cập  -> This hook is being called conditionally, but all hooks must be called in the exact same order in every component render.
// const [showPaymentConfirmation, setShowPaymentConfirmation] = useState(false)

// Cập nhật interface AppContextType
interface AppContextType {
  invoiceData: InvoiceData | null
  setInvoiceData: React.Dispatch<React.SetStateAction<InvoiceData | null>>
  connectToPOS: (connectionId: string) => void
  disconnectFromPOS: (navigation: any) => void
  connectionStatus: "disconnected" | "connecting" | "connected"
  lastConnectionId: string
  saveConnectionId: (connectionId: string) => void
  updateShippingAddress: (address: Address) => void
  applyVoucher: (voucherCode: string) => void
  confirmPayment: () => void
  rejectPayment: () => void
  registerNavigation: (navigation: any) => void
  checkInvoiceStatus: () => boolean
  // Thêm các hàm liên quan đến WebSocket
  sendMessage: (message: WebSocketMessage) => void
  // Thêm các thuộc tính liên quan đến modal xác nhận thanh toán
  showPaymentConfirmation: boolean
  setShowPaymentConfirmation: React.Dispatch<React.SetStateAction<boolean>>
}

// Create the context
const AppContext = createContext<AppContextType | undefined>(undefined)

// Create a provider component
export const AppProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [invoiceData, setInvoiceData] = useState<InvoiceData | null>(null)
  const [connectionStatus, setConnectionStatus] = useState<"disconnected" | "connecting" | "connected">("disconnected")
  const [lastConnectionId, setLastConnectionId] = useState<string>("")
  const [navigation, setNavigation] = useState<any>(null)
  const [paymentProcessed, setPaymentProcessed] = useState(false)
  const [showPaymentConfirmation, setShowPaymentConfirmation] = useState(false)

  // Tham chiếu đến "WebSocket" giả lập
  const webSocketRef = useRef<any>(null)

  // Hàm gửi tin nhắn qua "WebSocket" giả lập
  const sendMessage = (message: WebSocketMessage) => {
    if (connectionStatus !== "connected") {
      console.log("Cannot send message: Not connected to POS")
      return
    }

    console.log("Sending message to POS:", message)

    // Trong môi trường thực tế, đây sẽ là webSocketRef.current.send(JSON.stringify(message))
    // Nhưng ở đây chúng ta sẽ mô phỏng phản hồi từ server

    // Mô phỏng phản hồi từ server dựa trên loại tin nhắn
    switch (message.type) {
      case "payment_request":
        // Mô phỏng phản hồi xác nhận thanh toán
        setTimeout(() => {
          handleWebSocketMessage({
            type: "invoice_clear",
            data: { success: true },
          })
        }, 500)
        break

      // Xử lý các loại tin nhắn khác nếu cần
    }
  }

  // Hàm xử lý tin nhắn nhận được từ "WebSocket" giả lập
  const handleWebSocketMessage = (message: WebSocketMessage) => {
    console.log("Received message from POS:", message)

    switch (message.type) {
      case "invoice_update":
        // Cập nhật dữ liệu hóa đơn
        setInvoiceData(message.data)
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
        if (message.data && message.data.code) {
          applyVoucher(message.data.code)
        }
        break

      case "connection_status":
        // Cập nhật trạng thái kết nối
        if (message.data && message.data.status) {
          setConnectionStatus(message.data.status)
        }
        break

      case "invoice_clear":
        // Xóa dữ liệu hóa đơn hiện tại
        setInvoiceData(null)
        // Chuyển về màn hình Invoice trước (sẽ hiển thị trạng thái chờ)
        if (navigation) {
          navigation.navigate("Invoice")

          // Sau 3 giây, kiểm tra xem có hóa đơn mới không
          setTimeout(() => {
            // 50% khả năng có hóa đơn mới
            if (Math.random() > 0.5) {
              simulateInvoiceData()
              console.log("New invoice received from POS")
            } else {
              console.log("No new invoice from POS")
              // Nếu không có hóa đơn mới, chuyển về màn hình Home
              navigation.navigate("Home")
            }
          }, 3000)
        }
        break
    }
  }

  // Mô phỏng thiết lập kết nối WebSocket
  const setupWebSocketConnection = (connectionId: string) => {
    // Trong môi trường thực tế, đây sẽ là:
    // webSocketRef.current = new WebSocket(`wss://your-pos-server.com/ws/${connectionId}`)
    // webSocketRef.current.onopen = () => { ... }
    // webSocketRef.current.onmessage = (event) => { ... }
    // webSocketRef.current.onclose = () => { ... }

    console.log(`Setting up WebSocket connection with ID: ${connectionId}`)

    // Mô phỏng kết nối thành công
    setTimeout(() => {
      handleWebSocketMessage({
        type: "connection_status",
        data: { status: "connected" },
      })

      // Mô phỏng nhận dữ liệu hóa đơn ban đầu
      simulateInvoiceData()

      // Mô phỏng nhận yêu cầu xác nhận thanh toán sau một khoảng thời gian
      setTimeout(() => {
        handleWebSocketMessage({
          type: "payment_confirmation",
          data: { invoiceId: invoiceData?.id },
        })
      }, 10000)
    }, 2000)

    // Mô phỏng nhận tin nhắn định kỳ từ server (trong thực tế sẽ là sự kiện onmessage)
    const mockWebSocketInterval = setInterval(() => {
      // Chỉ gửi tin nhắn nếu đã kết nối
      if (connectionStatus === "connected") {
        // Đôi khi gửi cập nhật hóa đơn ngẫu nhiên
        if (!invoiceData && Math.random() > 0.7) {
          simulateInvoiceData()
        }
        // Hoặc cập nhật hóa đơn hiện tại
        else if (invoiceData && Math.random() > 0.8) {
          updateExistingInvoice()
        }
      }
    }, 5000)

    // Lưu tham chiếu để có thể xóa interval khi ngắt kết nối
    webSocketRef.current = {
      mockInterval: mockWebSocketInterval,
      send: (data: string) => {
        // Mô phỏng gửi dữ liệu qua WebSocket
        console.log("WebSocket send:", data)
      },
      close: () => {
        // Mô phỏng đóng kết nối WebSocket
        clearInterval(mockWebSocketInterval)
        console.log("WebSocket connection closed")
      },
    }
  }

  // Cập nhật hàm connectToPOS để sử dụng WebSocket
  const connectToPOS = (connectionId: string) => {
    setConnectionStatus("connecting")
    console.log(`Connecting to POS with ID: ${connectionId}`)

    // Thiết lập kết nối WebSocket
    setupWebSocketConnection(connectionId)

    // Lưu ID kết nối
    saveConnectionId(connectionId)
  }

  // Cập nhật hàm disconnectFromPOS để đóng WebSocket
  const disconnectFromPOS = (navigation: any) => {
    // Đóng kết nối WebSocket nếu có
    if (webSocketRef.current) {
      webSocketRef.current.close()
      webSocketRef.current = null
    }

    setConnectionStatus("disconnected")
    setInvoiceData(null)
    setPaymentProcessed(false)
    console.log("Disconnected from POS")
    navigation.navigate("Connection")
  }

  // Hàm cập nhật hóa đơn hiện tại
  const updateExistingInvoice = () => {
    if (!invoiceData) return

    setInvoiceData((prev) => {
      if (!prev) return prev

      // Thêm một sản phẩm mới hoặc cập nhật số lượng sản phẩm hiện có
      const updatedItems = [...prev.items]

      if (Math.random() > 0.5 && updatedItems.length > 0) {
        // Cập nhật số lượng sản phẩm ngẫu nhiên
        const randomIndex = Math.floor(Math.random() * updatedItems.length)
        const updatedItem = { ...updatedItems[randomIndex] }
        updatedItem.quantity += 1
        updatedItem.total = updatedItem.price * updatedItem.quantity
        updatedItems[randomIndex] = updatedItem
      } else {
        // Thêm sản phẩm mới
        updatedItems.push({
          id: "item-" + (prev.items.length + 1),
          name: "Sản phẩm mới " + Math.floor(Math.random() * 100),
          quantity: 1,
          price: Math.floor(Math.random() * 500000) + 100000,
          total: 0, // Sẽ được tính toán bên dưới
          image: "https://placeholder.svg?height=80&width=80&query=product",
          size: ["S", "M", "L", "XL"][Math.floor(Math.random() * 4)],
          color: ["Đen", "Trắng", "Xanh", "Đỏ"][Math.floor(Math.random() * 4)],
          sku: "SKU-" + Math.floor(Math.random() * 1000),
        })

        // Cập nhật tổng tiền cho sản phẩm mới
        const newItem = updatedItems[updatedItems.length - 1]
        newItem.total = newItem.price * newItem.quantity
      }

      // Tính lại tổng tiền
      const newSubtotal = updatedItems.reduce((sum, item) => sum + item.total, 0)
      const newTax = newSubtotal * 0.1
      const shippingCost = prev.shipping?.cost || 0

      return {
        ...prev,
        items: updatedItems,
        subtotal: newSubtotal,
        tax: newTax,
        total: newSubtotal + newTax + shippingCost,
      }
    })
  }

  const saveConnectionId = (connectionId: string) => {
    setLastConnectionId(connectionId)
  }

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
  }

  const applyVoucher = (voucherCode: string) => {
    // Simulate voucher application
    const voucher: Voucher = {
      code: voucherCode,
      discount: 10,
      type: "percent",
    }

    setInvoiceData((prev) => {
      if (!prev) return prev

      return {
        ...prev,
        vouchers: [voucher],
        total: prev.total - (prev.subtotal * voucher.discount) / 100,
      }
    })
  }

  // Cập nhật hàm confirmPayment để sử dụng WebSocket
  const confirmPayment = () => {
    console.log("Payment confirmed")
    setPaymentProcessed(true)

    // Gửi xác nhận thanh toán qua WebSocket
    sendMessage({
      type: "payment_request",
      data: {
        invoiceId: invoiceData?.id,
        status: "confirmed",
      },
    })
  }

  const rejectPayment = () => {
    console.log("Payment rejected")

    // Gửi từ chối thanh toán qua WebSocket
    sendMessage({
      type: "payment_request",
      data: {
        invoiceId: invoiceData?.id,
        status: "rejected",
      },
    })
  }

  // Hàm mô phỏng nhận dữ liệu hóa đơn từ WebSocket
  const simulateInvoiceData = () => {
    // Initial data
    const initialData: InvoiceData = {
      id: "INV-" + Math.floor(Math.random() * 10000),
      items: [
        {
          id: "item-1",
          name: "Áo thun nam cổ tròn",
          quantity: 2,
          price: 150000,
          total: 300000,
          image: "https://placeholder.svg?height=80&width=80&query=tshirt",
          size: "L",
          color: "Đen",
          sku: "AT-001-L-BLK",
        },
        {
          id: "item-2",
          name: "Quần jean nữ ống rộng",
          quantity: 1,
          price: 450000,
          total: 450000,
          image: "https://placeholder.svg?height=80&width=80&query=jeans",
          size: "M",
          color: "Xanh nhạt",
          sku: "QJ-002-M-LBL",
        },
      ],
      subtotal: 750000,
      tax: 75000,
      total: 825000,
      customerInfo: {
        name: "Nguyễn Văn A",
        phone: "0912345678",
        address: {
          province: "TP. Hồ Chí Minh",
          district: "Quận 1",
          ward: "Phường Bến Nghé",
          detail: "123 Đường Lê Lợi",
        },
        email: "nguyenvana@example.com",
      },
      paymentMethod: "Tiền mặt",
      shipping: {
        method: "Standard Delivery",
        cost: 30000,
        estimatedDelivery: "2-3 ngày",
      },
    }

    // Mô phỏng nhận dữ liệu hóa đơn qua WebSocket
    handleWebSocketMessage({
      type: "invoice_update",
      data: initialData,
    })

    // Mô phỏng cập nhật hóa đơn sau một khoảng thời gian
    setTimeout(() => {
      // Thêm sản phẩm mới
      const updatedData = {
        ...initialData,
        items: [
          ...initialData.items,
          {
            id: "item-3",
            name: "Giày thể thao nam",
            quantity: 1,
            price: 850000,
            total: 850000,
            image: "https://placeholder.svg?height=80&width=80&query=sneakers",
            size: "42",
            color: "Trắng",
            sku: "GT-003-42-WHT",
          },
        ],
      }

      // Tính lại tổng tiền
      const newSubtotal = updatedData.items.reduce((sum, item) => sum + item.total, 0)
      const newTax = newSubtotal * 0.1
      const shippingCost = updatedData.shipping?.cost || 0

      updatedData.subtotal = newSubtotal
      updatedData.tax = newTax
      updatedData.total = newSubtotal + newTax + shippingCost

      // Gửi cập nhật qua WebSocket
      handleWebSocketMessage({
        type: "invoice_update",
        data: updatedData,
      })

      // Mô phỏng nhận voucher từ POS
      setTimeout(() => {
        const voucherCode = "SALE" + Math.floor(Math.random() * 1000)
        handleWebSocketMessage({
          type: "voucher_applied",
          data: { code: voucherCode },
        })
      }, 2000)
    }, 5000)
  }

  const registerNavigation = (nav: any) => {
    setNavigation(nav)
  }

  const checkInvoiceStatus = () => {
    return invoiceData !== null
  }

  // Cleanup khi component unmount
  useEffect(() => {
    return () => {
      // Đóng kết nối WebSocket khi unmount
      if (webSocketRef.current) {
        webSocketRef.current.close()
      }
    }
  }, [])

  const memoizedDisconnectFromPOS = useCallback(disconnectFromPOS, [])

  const value: AppContextType = {
    invoiceData,
    setInvoiceData,
    connectToPOS,
    disconnectFromPOS: memoizedDisconnectFromPOS,
    connectionStatus,
    lastConnectionId,
    saveConnectionId,
    updateShippingAddress,
    applyVoucher,
    confirmPayment,
    rejectPayment,
    registerNavigation,
    checkInvoiceStatus,
    sendMessage,
    showPaymentConfirmation,
    setShowPaymentConfirmation,
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
