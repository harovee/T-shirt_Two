"use client"

import { useRef, useEffect, useState } from "react"
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Animated,
  FlatList,
  Dimensions,
  Image,
  TextInput,
  ScrollView,
  Modal,
  Alert,
  ActivityIndicator,
} from "react-native"
import { Ionicons } from "@expo/vector-icons"
import { useAppContext, type Address } from "../context/AppContext"
import { useTheme } from "../context/ThemeContext"
import { StatusBar } from "expo-status-bar"
import { useNavigation } from "@react-navigation/native"
import type { AppNavigationProp } from "../types/navigation"

// Thêm import PaymentConfirmationModal
import PaymentConfirmationModal from "../components/PaymentConfirmationModal"

const { width } = Dimensions.get("window")

const InvoiceScreen = () => {
  // Đã gọi useAppContext() ở đầu component
  const { invoiceData, disconnectFromPOS, updateShippingAddress, applyVoucher, registerNavigation, connectionStatus } =
    useAppContext()
  const { colors, isDark } = useTheme()
  const navigation = useNavigation<AppNavigationProp>()

  // State cho địa chỉ giao hàng
  const [showAddressModal, setShowAddressModal] = useState(false)
  const [address, setAddress] = useState<Address>({
    province: "",
    district: "",
    ward: "",
    detail: "",
    receiver: "",
    phone: "",
    note: "",
  })

  // State cho voucher
  const [voucherCode, setVoucherCode] = useState("")
  const [showVoucherInput, setShowVoucherInput] = useState(false)

  // State để theo dõi trạng thái loading
  const [isLoading, setIsLoading] = useState(false)

  // Animation refs
  const fadeAnim = useRef(new Animated.Value(0)).current
  const slideAnim = useRef(new Animated.Value(50)).current

  // Format currency
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount)
  }



  useEffect(() => {
    // Start animations
    Animated.parallel([
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: 500,
        useNativeDriver: true,
      }),
      Animated.timing(slideAnim, {
        toValue: 0,
        duration: 500,
        useNativeDriver: true,
      }),
    ]).start()

    // Khởi tạo địa chỉ từ invoiceData nếu có
    if (invoiceData?.customerInfo?.address) {
      setAddress(invoiceData.customerInfo.address)
    }
  }, [])

  // Animation for new items
  const itemFadeAnim = useRef(new Animated.Value(0)).current

  useEffect(() => {
    if (invoiceData) {
      setIsLoading(false)
      Animated.sequence([
        Animated.timing(itemFadeAnim, {
          toValue: 0,
          duration: 0,
          useNativeDriver: true,
        }),
        Animated.timing(itemFadeAnim, {
          toValue: 1,
          duration: 500,
          useNativeDriver: true,
        }),
      ]).start()
    } else {
      // Nếu không có dữ liệu hóa đơn, hiển thị trạng thái loading
      setIsLoading(true)
    }
  }, [invoiceData])

  // Thêm useEffect để tự động chuyển đến Home khi không có hóa đơn
  useEffect(() => {
    // Nếu không có hóa đơn và không phải đang loading và đã kết nối
    if (!invoiceData && !isLoading && connectionStatus === "connected") {
      // Đợi một chút để hiển thị thông báo "không có hóa đơn" trước khi chuyển
      const timer = setTimeout(() => {
        navigation.navigate("Home")
      }, 2000)

      return () => clearTimeout(timer)
    }
  }, [invoiceData, isLoading, connectionStatus, navigation])

  const handleDisconnect = () => {
    disconnectFromPOS(navigation)
  }

  const handleSettings = () => {
    navigation.navigate("Settings")
  }

  const handleSaveAddress = () => {
    // Kiểm tra địa chỉ hợp lệ
    if (!address.province || !address.district || !address.ward || !address.detail) {
      Alert.alert("Lỗi", "Vui lòng nhập đầy đủ thông tin địa chỉ")
      return
    }

    // Cập nhật địa chỉ
    updateShippingAddress(address)
    setShowAddressModal(false)
  }

  const handleApplyVoucher = () => {
    if (!voucherCode.trim()) {
      Alert.alert("Lỗi", "Vui lòng nhập mã voucher")
      return
    }

    applyVoucher(voucherCode.trim())
    setShowVoucherInput(false)
  }

  // Danh sách tỉnh/thành phố mẫu
  const provinces = ["TP. Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Cần Thơ", "Hải Phòng"]

  // Danh sách quận/huyện mẫu
  const getDistricts = (province: string) => {
    if (province === "TP. Hồ Chí Minh") {
      return ["Quận 1", "Quận 2", "Quận 3", "Quận 7", "Thủ Đức"]
    } else if (province === "Hà Nội") {
      return ["Ba Đình", "Hoàn Kiếm", "Đống Đa", "Cầu Giấy", "Hai Bà Trưng"]
    }
    return ["Quận/Huyện 1", "Quận/Huyện 2", "Quận/Huyện 3"]
  }

  // Danh sách phường/xã mẫu
  const getWards = (district: string) => {
    if (district === "Quận 1") {
      return ["Phường Bến Nghé", "Phường Bến Thành", "Phường Đa Kao"]
    } else if (district === "Thủ Đức") {
      return ["Phường Linh Trung", "Phường Hiệp Bình Phước", "Phường Tam Bình"]
    }
    return ["Phường/Xã 1", "Phường/Xã 2", "Phường/Xã 3"]
  }

  // Thêm useEffect để đăng ký navigation với AppContext
  useEffect(() => {
    // Đăng ký navigation với AppContext để có thể điều hướng từ context
    registerNavigation(navigation)
  }, [navigation, registerNavigation])

  // Hiển thị màn hình loading hoặc không có hóa đơn
  if (isLoading) {
    return (
      <View style={[styles.container, { backgroundColor: colors.background }]}>
        <StatusBar style={isDark ? "light" : "dark"} />
        <View style={styles.emptyContainer}>
          <ActivityIndicator size="large" color={colors.primary} style={styles.loadingIndicator} />
          <Text style={[styles.emptyText, { color: colors.text }]}>Đang chờ dữ liệu từ máy quầy...</Text>
          <Text style={[styles.emptySubtext, { color: colors.muted }]}>
            Vui lòng đợi hoặc kiểm tra kết nối với máy quầy
          </Text>
        </View>
      </View>
    )
  }

  if (!invoiceData) {
    return (
      <View style={[styles.container, { backgroundColor: colors.background }]}>
        <StatusBar style={isDark ? "light" : "dark"} />
        <View style={styles.emptyContainer}>
          <Ionicons name="receipt-outline" size={64} color={colors.muted} />
          <Text style={[styles.emptyText, { color: colors.text }]}>Hiện tại không có hóa đơn nào</Text>
          <Text style={[styles.emptySubtext, { color: colors.muted }]}>
            Đang chờ máy quầy gửi thông tin hóa đơn mới
          </Text>
        </View>
      </View>
    )
  }

  // Tính tổng tiền giảm giá từ voucher
  const calculateDiscount = () => {
    if (!invoiceData.vouchers || invoiceData.vouchers.length === 0) return 0
    const voucher = invoiceData.vouchers[0]
    if (voucher.type === "percent") {
      return (invoiceData.subtotal * voucher.discount) / 100
    }
    return voucher.discount
  }
  const discount = calculateDiscount();

  // Tính phí vận chuyển thực tế
  const realShippingCost = () => {
    if (invoiceData &&
       invoiceData.customerInfo?.address &&
       invoiceData.customerInfo?.address?.ward &&
       invoiceData.customerInfo?.address?.ward != '' &&
       invoiceData.shipping &&
       invoiceData.shipping.cost) {
      const tienHang = invoiceData.subtotal;
      if (tienHang > 2000000) {
        return 0;
      } else {
        return invoiceData.shipping.cost;
      }
    }
    return 0; // Default value if conditions are not met
  }

  // Tính tiền thừa hoặc thiếu của khách hàng
  const getCurrentChanges = () => {
    if (invoiceData.paymentMethod.includes("Tiền mặt")) {
    return invoiceData.guestMoney - (invoiceData.subtotal + (realShippingCost()) - discount);
    }else if (invoiceData.paymentMethod.includes("Chuyển khoản")) {
      return 0;
    }else if (invoiceData.paymentMethod.includes("Cả hai")) {
      return invoiceData.guestMoney - (-(invoiceData.transferMoney ?? 0) + invoiceData.subtotal + (realShippingCost()) - discount);
    }
    return 0;
  }

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      <StatusBar style={isDark ? "light" : "dark"} />

      {/* Header */}
      <View style={[styles.header, { backgroundColor: colors.card }]}>
        <View style={styles.headerContent}>
          <Text style={[styles.headerTitle, { color: colors.text }]}>
            {invoiceData.id ? `Hóa đơn: ${invoiceData.id}` : "Chưa có hóa đơn"}
          </Text>
          <View style={styles.headerButtons}>
            <TouchableOpacity style={styles.headerButton} onPress={handleSettings}>
              <Ionicons name="settings-outline" size={24} color={colors.text} />
            </TouchableOpacity>
            <TouchableOpacity style={styles.headerButton} onPress={handleDisconnect}>
              <Ionicons name="log-out-outline" size={24} color={colors.danger} />
            </TouchableOpacity>
          </View>
        </View>
      </View>

      {invoiceData.items.length === 0 ? (
        <View style={styles.emptyContainer}>
          <Ionicons name="cart-outline" size={64} color={colors.muted} />
          <Text style={[styles.emptyText, { color: colors.text }]}>Chưa có sản phẩm nào</Text>
          <Text style={[styles.emptySubtext, { color: colors.muted }]}>
            Các sản phẩm sẽ hiển thị khi được thêm vào giỏ hàng
          </Text>
        </View>
      ) : (
        <ScrollView
          style={styles.scrollContainer}
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
        >
          <Animated.View
            style={[
              styles.content,
              {
                opacity: fadeAnim,
                transform: [{ translateY: slideAnim }],
              },
            ]}
          >
            {/* Items list */}
            <View style={styles.section}>
              <Text style={[styles.sectionTitle, { color: colors.text }]}>Sản phẩm</Text>
              <FlatList
                data={invoiceData.items}
                keyExtractor={(item, index) => `${item.id}-${index}`} // Sửa lỗi key trùng lặp
                renderItem={({ item, index }) => (
                  <Animated.View
                    style={[
                      styles.itemCard,
                      {
                        backgroundColor: colors.card,
                        borderColor: colors.border,
                        opacity: index === invoiceData.items.length - 1 ? itemFadeAnim : 1,
                      },
                    ]}
                  >
                    <Image source={{ uri: item.image }} style={styles.itemImage} />
                    <View style={styles.itemInfo}>
                      <Text style={[styles.itemName, { color: colors.text }]}>{item.id} - {item.name}</Text>
                      <View style={styles.itemDetails}>
                        {item.size && (
                          <Text style={[styles.itemDetail, { color: colors.muted }]}>Size: {item.size}</Text>
                        )}
                        {item.color && (
                          <Text style={[styles.itemDetail, { color: colors.muted }]}>Màu: {item.color}</Text>
                        )}
                      </View>
                      <Text style={[styles.itemPrice, { color: colors.muted }]}>
                        {formatCurrency(item.price)} x {item.quantity}
                      </Text>
                    </View>
                    <Text style={[styles.itemTotal, { color: colors.text }]}>{formatCurrency(item.total)}</Text>
                  </Animated.View>
                )}
                scrollEnabled={false}
                contentContainerStyle={styles.itemsList}
              />
            </View>

            {/* Voucher */}
            {(invoiceData.vouchers ?? []).length > 0 && (
                <View style={[styles.voucherCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
                {showVoucherInput ? (
                  <View style={styles.voucherInputContainer}>
                    <TextInput
                      style={[styles.voucherInput, { backgroundColor: colors.background, color: colors.text }]}
                      placeholder="Nhập mã voucher"
                      placeholderTextColor={colors.muted}
                      value={voucherCode}
                      onChangeText={setVoucherCode}
                      autoCapitalize="characters"
                    />
                    <View style={styles.voucherButtons}>
                      <TouchableOpacity
                        style={[styles.voucherButton, { backgroundColor: colors.muted }]}
                        onPress={() => setShowVoucherInput(false)}
                      >
                        <Text style={styles.voucherButtonText}>Hủy</Text>
                      </TouchableOpacity>
                      <TouchableOpacity
                        style={[styles.voucherButton, { backgroundColor: colors.primary }]}
                        onPress={handleApplyVoucher}
                      >
                        <Text style={styles.voucherButtonText}>Áp dụng</Text>
                      </TouchableOpacity>
                    </View>
                  </View>
                ) : (
                  // <TouchableOpacity
                  //   style={styles.voucherRow}
                  //   onPress={() => setShowVoucherInput(true)}
                  //   activeOpacity={0.7}
                  // >
                  //   <View style={styles.voucherLeft}>
                  //     <Ionicons name="ticket-outline" size={20} color={colors.primary} />
                  //     <Text style={[styles.voucherText, { color: colors.text }]}>
                  //       {invoiceData.vouchers && invoiceData.vouchers.length > 0
                  //         ? `Voucher: ${invoiceData.vouchers[0].code}`
                  //         : "Thêm Voucher"}
                  //     </Text>
                  //   </View>
                  //   <Ionicons name="chevron-forward" size={20} color={colors.muted} />
                  // </TouchableOpacity>
  
                  <View style={styles.voucherRow}>
                    <View style={styles.voucherLeft}>
                      <Ionicons name="ticket-outline" size={20} color={colors.primary} />
                      <Text style={[styles.voucherText, { color: colors.text }]}>
                        {invoiceData.vouchers && invoiceData.vouchers.length > 0
                          ? `Voucher: ${invoiceData.vouchers[0].code}`
                          : "Thêm Voucher"}
                      </Text>
                    </View>
                    <Text>
                      ― {invoiceData.vouchers && invoiceData.vouchers.length > 0
                          ? `${invoiceData.vouchers[0].discount} %`
                          : formatCurrency((invoiceData.vouchers?.[0]?.discount ?? 0))}
                    </Text>
                  </View>
                )}
              </View>
            )}
            

            {/* Customer info */}
            {invoiceData.customerInfo && (
              <View style={[styles.customerCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
                <View style={styles.cardHeader}>
                  <Text style={[styles.sectionTitle, { color: colors.text }]}>Thông tin khách hàng</Text>
                </View>
                <View style={styles.customerInfo}>
                  <View style={styles.customerRow}>
                    <Ionicons name="person-outline" size={20} color={colors.muted} />
                    <Text style={[styles.customerText, { color: colors.text }]}>{invoiceData.customerInfo.name}</Text>
                  </View>
                  <View style={styles.customerRow}>
                    <Ionicons name="call-outline" size={20} color={colors.muted} />
                    <Text style={[styles.customerText, { color: colors.text }]}>{invoiceData.customerInfo.phone}</Text>
                  </View>
                  {invoiceData.customerInfo.email && (
                    <View style={styles.customerRow}>
                      <Ionicons name="mail-outline" size={20} color={colors.muted} />
                      <Text style={[styles.customerText, { color: colors.text }]}>
                        {invoiceData.customerInfo.email}
                      </Text>
                    </View>
                  )}
                </View>
              </View>
            )}

            {/* Shipping Address */}
            {invoiceData.customerInfo?.address && 
              <>
                  <View style={[styles.addressCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
                <View style={styles.cardHeader}>
                  <Text style={[styles.sectionTitle, { color: colors.text }]}>Địa chỉ giao hàng</Text>
                  {/* <TouchableOpacity
                    style={[styles.editButton, { backgroundColor: colors.primary + "20" }]}
                    onPress={() => setShowAddressModal(true)}
                  >
                    <Text style={[styles.editButtonText, { color: colors.primary }]}>Sửa</Text>
                  </TouchableOpacity> */}
                </View>

                {invoiceData.customerInfo?.address ? (
                  <View style={styles.addressInfo}>
                     <View style={styles.addressRow}>
                        <Ionicons name="person-outline" size={20} color={colors.muted} />
                        <Text style={[styles.addressText, { color: colors.text }]}>{invoiceData.customerInfo.address.receiver}</Text>
                     </View>
                    <View style={styles.addressRow}>
                        <Ionicons name="call-outline" size={20} color={colors.muted} />
                        <Text style={[styles.addressText, { color: colors.text }]}>{invoiceData.customerInfo.address.phone}</Text>
                    </View>
                    <View style={styles.addressRow}>
                      <Ionicons name="location-outline" size={20} color={colors.muted} />
                      <Text style={[styles.addressText, { color: colors.text }]}>
                        {invoiceData.customerInfo.address.detail}, {invoiceData.customerInfo.address.ward},{" "}
                        {invoiceData.customerInfo.address.district}, {invoiceData.customerInfo.address.province}
                      </Text>
                    </View>
                    {invoiceData.customerInfo?.address?.ward && invoiceData.shipping && (
                      <View style={styles.shippingInfo}>
                        <View style={styles.shippingRow}>
                          <Ionicons name="car-outline" size={20} color={colors.muted} />
                          <Text style={[styles.shippingText, { color: colors.text }]}>
                            {invoiceData.shipping.method} ({invoiceData.shipping.estimatedDelivery})
                          </Text>
                        </View>
                        <Text style={[styles.shippingCost, { color: colors.primary }]}>
                          {formatCurrency(invoiceData.shipping.cost)}
                        </Text>
                      </View>
                    )}
                  </View>
                ) : (
                  <></>
                  // <TouchableOpacity
                  //   style={styles.addAddressButton}
                  //   onPress={() => setShowAddressModal(true)}
                  //   activeOpacity={0.7}
                  // >
                  //   <Ionicons name="add-circle-outline" size={20} color={colors.primary} />
                  //   <Text style={[styles.addAddressText, { color: colors.primary }]}>Thêm địa chỉ giao hàng</Text>
                  // </TouchableOpacity>
                )}
              </View>
              
              </>
            }
            

            {/* Payment method */}
            {invoiceData.paymentMethod && (
              <View style={[styles.paymentCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
                <Text style={[styles.sectionTitle, { color: colors.text }]}>Phương thức thanh toán</Text>
                <View style={styles.paymentMethod}>
                  {invoiceData.paymentMethod.includes("Cả hai") ? (
                    <>
                      <Ionicons name="cash-outline" size={24} color={colors.primary} />
                      <Ionicons name="card-outline" size={24} color={colors.primary} />
                    </>
                  ) : (
                    <Ionicons
                      name={invoiceData.paymentMethod.includes("Tiền mặt") ? "cash-outline" : "card-outline"}
                      size={24}
                      color={colors.primary}
                    />
                  )}
                  <Text style={[styles.paymentText, { color: colors.text }]}>{invoiceData.paymentMethod}</Text>
                </View>
              </View>
            )}

            {/* Summary */}
            <View style={[styles.summaryCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
              <View style={styles.summaryRow}>
                <Text style={[styles.summaryLabel, { color: colors.muted }]}>Tạm tính:</Text>
                <Text style={[styles.summaryValue, { color: colors.text }]}>
                  {formatCurrency(invoiceData.subtotal)}
                </Text>
              </View>
              {/* 

              <View style={styles.summaryRow}>
                <Text style={[styles.summaryLabel, { color: colors.muted }]}>Thuế:</Text>
                <Text style={[styles.summaryValue, { color: colors.text }]}>{formatCurrency(invoiceData.tax)}</Text>
              </View>
              
              */}
              
              {invoiceData.customerInfo?.address && invoiceData.shipping && (
                <View style={styles.summaryRow}>
                  <Text style={[styles.summaryLabel, { color: colors.muted }]}>Phí vận chuyển:</Text>
                  <Text style={[styles.summaryValue, { color: colors.text }]}>
                    {formatCurrency(realShippingCost())}
                  </Text>
                </View>
              )}
              {discount > 0 && (
                <View style={styles.summaryRow}>
                  <Text style={[styles.summaryLabel, { color: colors.success }]}>Giảm giá:</Text>
                  <Text style={[styles.summaryValue, { color: colors.success }]}>-{formatCurrency(discount)}</Text>
                </View>
              )}
              <View style={[styles.summaryRow, styles.totalRow]}>
                <Text style={[styles.totalLabel, { color: colors.text }]}>Tổng cộng:</Text>
                <Text style={[styles.totalValue, { color: colors.primary }]}>{formatCurrency(Math.floor(invoiceData.subtotal + (realShippingCost()) - discount))}</Text>
              </View>
              {invoiceData.paymentMethod.includes("Tiền mặt") && (
                <>
                  <View style={[styles.summaryRow, styles.totalRow]}>
                    <Text style={[styles.totalLabel, { color: colors.text }]}>Tiền khách đưa:</Text>
                    <Text style={[styles.totalValue, { color: colors.primary }]}>{formatCurrency(invoiceData.guestMoney)}</Text>
                  </View>
                  <View style={[styles.summaryRow, styles.totalRow]}>
                    { getCurrentChanges() < 0 ? (
                      <>
                        <Text style={[styles.totalLabel, { color: colors.text }]}>Tiền còn thiếu:</Text>
                        <Text style={[styles.totalValue, { color: colors.danger }]}>{formatCurrency( -(getCurrentChanges()))}</Text>
                      </>
                    ) : (
                      <>
                        <Text style={[styles.totalLabel, { color: colors.text }]}>Tiền thừa:</Text>
                        <Text style={[styles.totalValue, { color: colors.primary }]}>{formatCurrency(getCurrentChanges())}</Text>
                      </>
                    ) }
                    
                  </View>
                </>
                
              )}

              {/* {invoiceData.paymentMethod.includes("Chuyển khoản") && (
                <>
                  <View style={[styles.summaryRow, styles.totalRow]}>
                    <Text style={[styles.totalLabel, { color: colors.text }]}>Chuyển khoản</Text>
                    <Text style={[styles.totalValue, { color: colors.primary }]}> {formatCurrency(invoiceData.transferMoney ?? 0)}</Text>
                  </View>
                </>
                
              )} */}

            {invoiceData.paymentMethod.includes("Cả hai") && (
                <>
                  <View style={[styles.summaryRow, styles.totalRow]}>
                    <Text style={[styles.totalLabel, { color: colors.text }]}>Chuyển khoản</Text>
                    <Text style={[styles.totalValue, { color: colors.primary }]}> {formatCurrency(invoiceData.transferMoney ?? 0)}</Text>
                  </View>
                  <View style={[styles.summaryRow, styles.totalRow]}>
                    <Text style={[styles.totalLabel, { color: colors.text }]}>Tiền khách đưa:</Text>
                    <Text style={[styles.totalValue, { color: colors.primary }]}>{formatCurrency(invoiceData.guestMoney)}</Text>
                  </View>
                  <View style={[styles.summaryRow, styles.totalRow]}>
                    
                    <Text style={[styles.totalLabel, { color: colors.text }]}>Tiền thừa:</Text>
                    <Text style={[styles.totalValue, { color: colors.danger }]}> {formatCurrency(getCurrentChanges())} </Text>
                  </View>
                </>
                
              )}
              
            </View>
          </Animated.View>
        </ScrollView>
      )}

      {/* Modal chọn địa chỉ */}
      <Modal visible={showAddressModal} animationType="slide" transparent={true}>
        <View style={styles.modalContainer}>
          <View style={[styles.modalContent, { backgroundColor: colors.background }]}>
            <View style={[styles.modalHeader, { borderBottomColor: colors.border }]}>
              <Text style={[styles.modalTitle, { color: colors.text }]}>Địa chỉ giao hàng</Text>
              <TouchableOpacity onPress={() => setShowAddressModal(false)}>
                <Ionicons name="close" size={24} color={colors.text} />
              </TouchableOpacity>
            </View>

            <ScrollView style={styles.modalBody}>
              <View style={styles.formGroup}>
                <Text style={[styles.formLabel, { color: colors.text }]}>Tỉnh/Thành phố</Text>
                <View style={[styles.formSelect, { backgroundColor: colors.card, borderColor: colors.border }]}>
                  <FlatList
                    data={provinces}
                    horizontal
                    showsHorizontalScrollIndicator={false}
                    keyExtractor={(item) => item}
                    renderItem={({ item }) => (
                      <TouchableOpacity
                        style={[
                          styles.selectItem,
                          {
                            backgroundColor: address.province === item ? colors.primary : colors.card,
                          },
                        ]}
                        onPress={() => setAddress({ ...address, province: item, district: "", ward: "" })}
                      >
                        <Text
                          style={[
                            styles.selectItemText,
                            { color: address.province === item ? "#FFFFFF" : colors.text },
                          ]}
                        >
                          {item}
                        </Text>
                      </TouchableOpacity>
                    )}
                  />
                </View>
              </View>

              {address.province && (
                <View style={styles.formGroup}>
                  <Text style={[styles.formLabel, { color: colors.text }]}>Quận/Huyện</Text>
                  <View style={[styles.formSelect, { backgroundColor: colors.card, borderColor: colors.border }]}>
                    <FlatList
                      data={getDistricts(address.province)}
                      horizontal
                      showsHorizontalScrollIndicator={false}
                      keyExtractor={(item) => item}
                      renderItem={({ item }) => (
                        <TouchableOpacity
                          style={[
                            styles.selectItem,
                            {
                              backgroundColor: address.district === item ? colors.primary : colors.card,
                            },
                          ]}
                          onPress={() => setAddress({ ...address, district: item, ward: "" })}
                        >
                          <Text
                            style={[
                              styles.selectItemText,
                              { color: address.district === item ? "#FFFFFF" : colors.text },
                            ]}
                          >
                            {item}
                          </Text>
                        </TouchableOpacity>
                      )}
                    />
                  </View>
                </View>
              )}

              {address.district && (
                <View style={styles.formGroup}>
                  <Text style={[styles.formLabel, { color: colors.text }]}>Phường/Xã</Text>
                  <View style={[styles.formSelect, { backgroundColor: colors.card, borderColor: colors.border }]}>
                    <FlatList
                      data={getWards(address.district)}
                      horizontal
                      showsHorizontalScrollIndicator={false}
                      keyExtractor={(item) => item}
                      renderItem={({ item }) => (
                        <TouchableOpacity
                          style={[
                            styles.selectItem,
                            {
                              backgroundColor: address.ward === item ? colors.primary : colors.card,
                            },
                          ]}
                          onPress={() => setAddress({ ...address, ward: item })}
                        >
                          <Text
                            style={[styles.selectItemText, { color: address.ward === item ? "#FFFFFF" : colors.text }]}
                          >
                            {item}
                          </Text>
                        </TouchableOpacity>
                      )}
                    />
                  </View>
                </View>
              )}

              <View style={styles.formGroup}>
                <Text style={[styles.formLabel, { color: colors.text }]}>Địa chỉ cụ thể</Text>
                <TextInput
                  style={[
                    styles.formInput,
                    { backgroundColor: colors.card, color: colors.text, borderColor: colors.border },
                  ]}
                  placeholder="Số nhà, tên đường..."
                  placeholderTextColor={colors.muted}
                  value={address.detail}
                  onChangeText={(text) => setAddress({ ...address, detail: text })}
                />
              </View>
            </ScrollView>

            <View style={styles.modalFooter}>
              <TouchableOpacity
                style={[styles.modalButton, { backgroundColor: colors.muted }]}
                onPress={() => setShowAddressModal(false)}
              >
                <Text style={styles.modalButtonText}>Hủy</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.modalButton, { backgroundColor: colors.primary }]}
                onPress={handleSaveAddress}
              >
                <Text style={styles.modalButtonText}>Lưu địa chỉ</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {/* Thêm modal xác nhận thanh toán */}
      <PaymentConfirmationModal />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  header: {
    paddingTop: 40,
    paddingBottom: 10,
    paddingHorizontal: 20,
    borderBottomWidth: 1,
    borderBottomColor: "rgba(0,0,0,0.1)",
  },
  headerContent: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: "bold",
  },
  headerButtons: {
    flexDirection: "row",
  },
  headerButton: {
    padding: 8,
    marginLeft: 8,
  },
  scrollContainer: {
    flex: 1,
  },
  scrollContent: {
    paddingBottom: 30,
  },
  content: {
    padding: 16,
  },
  section: {
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 12,
  },
  itemsList: {
    paddingBottom: 8,
  },
  itemCard: {
    flexDirection: "row",
    alignItems: "center",
    padding: 16,
    borderRadius: 12,
    marginBottom: 8,
    borderWidth: 1,
  },
  itemImage: {
    width: 60,
    height: 60,
    borderRadius: 8,
    marginRight: 12,
  },
  itemInfo: {
    flex: 1,
  },
  itemName: {
    fontSize: 16,
    fontWeight: "500",
    marginBottom: 4,
  },
  itemDetails: {
    flexDirection: "row",
    marginBottom: 4,
  },
  itemDetail: {
    fontSize: 12,
    marginRight: 8,
  },
  itemPrice: {
    fontSize: 14,
  },
  itemTotal: {
    fontSize: 16,
    fontWeight: "bold",
    marginLeft: 8,
  },
  voucherCard: {
    padding: 16,
    borderRadius: 12,
    marginBottom: 16,
    borderWidth: 1,
  },
  voucherRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  voucherLeft: {
    flexDirection: "row",
    alignItems: "center",
  },
  voucherText: {
    fontSize: 16,
    marginLeft: 8,
  },
  voucherInputContainer: {
    width: "100%",
  },
  voucherInput: {
    height: 40,
    borderRadius: 8,
    paddingHorizontal: 12,
    marginBottom: 8,
  },
  voucherButtons: {
    flexDirection: "row",
    justifyContent: "flex-end",
  },
  voucherButton: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 8,
    marginLeft: 8,
  },
  voucherButtonText: {
    color: "#FFFFFF",
    fontWeight: "500",
  },
  customerCard: {
    padding: 16,
    borderRadius: 12,
    marginBottom: 16,
    borderWidth: 1,
  },
  cardHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 12,
  },
  editButton: {
    paddingVertical: 4,
    paddingHorizontal: 12,
    borderRadius: 16,
  },
  editButtonText: {
    fontSize: 12,
    fontWeight: "500",
  },
  customerInfo: {
    marginTop: 8,
  },
  customerRow: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 8,
  },
  customerText: {
    fontSize: 14,
    marginLeft: 8,
  },
  addressCard: {
    padding: 16,
    borderRadius: 12,
    marginBottom: 16,
    borderWidth: 1,
  },
  addressInfo: {
    marginTop: 8,
  },
  addressRow: {
    flexDirection: "row",
    alignItems: "flex-start",
    marginBottom: 12,
  },
  addressText: {
    fontSize: 14,
    marginLeft: 8,
    flex: 1,
    lineHeight: 20,
  },
  addAddressButton: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    paddingVertical: 12,
  },
  addAddressText: {
    fontSize: 16,
    fontWeight: "500",
    marginLeft: 8,
  },
  shippingInfo: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingTop: 8,
    borderTopWidth: 1,
    borderTopColor: "rgba(0,0,0,0.05)",
  },
  shippingRow: {
    flexDirection: "row",
    alignItems: "center",
  },
  shippingText: {
    fontSize: 14,
    marginLeft: 8,
  },
  shippingCost: {
    fontSize: 16,
    fontWeight: "bold",
  },
  paymentCard: {
    padding: 16,
    borderRadius: 12,
    marginBottom: 16,
    borderWidth: 1,
  },
  paymentMethod: {
    flexDirection: "row",
    alignItems: "center",
    marginTop: 8,
  },
  paymentText: {
    fontSize: 16,
    marginLeft: 8,
  },
  summaryCard: {
    padding: 16,
    borderRadius: 12,
    marginBottom: 16,
    borderWidth: 1,
  },
  summaryRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 8,
  },
  summaryLabel: {
    fontSize: 14,
  },
  summaryValue: {
    fontSize: 14,
  },
  totalRow: {
    marginTop: 8,
    paddingTop: 8,
    borderTopWidth: 1,
    borderTopColor: "rgba(0,0,0,0.1)",
  },
  totalLabel: {
    fontSize: 16,
    fontWeight: "bold",
  },
  totalValue: {
    fontSize: 18,
    fontWeight: "bold",
  },
  modalContainer: {
    flex: 1,
    justifyContent: "flex-end",
    backgroundColor: "rgba(0,0,0,0.5)",
  },
  modalContent: {
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    maxHeight: "80%",
  },
  modalHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 16,
    borderBottomWidth: 1,
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: "bold",
  },
  modalBody: {
    padding: 16,
    maxHeight: 400,
  },
  formGroup: {
    marginBottom: 16,
  },
  formLabel: {
    fontSize: 14,
    fontWeight: "500",
    marginBottom: 8,
  },
  formSelect: {
    borderRadius: 8,
    borderWidth: 1,
    padding: 8,
  },
  selectItem: {
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 16,
    marginRight: 8,
  },
  selectItemText: {
    fontSize: 14,
  },
  formInput: {
    height: 40,
    borderRadius: 8,
    borderWidth: 1,
    paddingHorizontal: 12,
  },
  modalFooter: {
    flexDirection: "row",
    justifyContent: "space-between",
    padding: 16,
    borderTopWidth: 1,
    borderTopColor: "rgba(0,0,0,0.1)",
  },
  modalButton: {
    flex: 1,
    height: 44,
    borderRadius: 8,
    justifyContent: "center",
    alignItems: "center",
    marginHorizontal: 8,
  },
  modalButtonText: {
    color: "#FFFFFF",
    fontWeight: "bold",
  },
  emptyContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 20,
  },
  emptyText: {
    fontSize: 18,
    fontWeight: "bold",
    marginTop: 16,
  },
  emptySubtext: {
    fontSize: 14,
    textAlign: "center",
    marginTop: 8,
  },
  redText: {
    color: "red",
  },
  loadingIndicator: {
    marginBottom: 16,
  },
})

export default InvoiceScreen
