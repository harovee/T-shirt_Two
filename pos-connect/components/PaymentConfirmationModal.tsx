"use client"

import { useState, useEffect, useRef } from "react"
import { View, Text, StyleSheet, TouchableOpacity, Animated, Easing, Modal, ScrollView } from "react-native"
import { Ionicons } from "@expo/vector-icons"
import { useAppContext } from "../context/AppContext"
import { useTheme } from "../context/ThemeContext"

const PaymentConfirmationModal = () => {
  const { confirmPayment, rejectPayment, invoiceData, showPaymentConfirmation, setShowPaymentConfirmation } =
    useAppContext()
  const { colors, isDark } = useTheme()
  const [countdown, setCountdown] = useState(5)

  // Animation values
  const scaleAnim = useRef(new Animated.Value(0.8)).current
  const opacityAnim = useRef(new Animated.Value(0)).current
  const progressAnim = useRef(new Animated.Value(0)).current

  // Format currency
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount)
  }

  // Reset countdown when modal opens
  useEffect(() => {
    if (showPaymentConfirmation) {
      setCountdown(5)

      // Start animations
      Animated.parallel([
        Animated.timing(scaleAnim, {
          toValue: 1,
          duration: 300,
          useNativeDriver: true,
          easing: Easing.out(Easing.back(1.5)),
        }),
        Animated.timing(opacityAnim, {
          toValue: 1,
          duration: 300,
          useNativeDriver: true,
        }),
        Animated.timing(progressAnim, {
          toValue: 1,
          duration: 5000,
          useNativeDriver: false,
          easing: Easing.linear,
        }),
      ]).start()
    } else {
      // Reset animations when modal closes
      scaleAnim.setValue(0.8)
      opacityAnim.setValue(0)
      progressAnim.setValue(0)
    }
  }, [showPaymentConfirmation])

  // Countdown timer
  useEffect(() => {
    if (!showPaymentConfirmation) return

    if (countdown <= 0) {
      // Auto confirm after countdown
      handleConfirm()
      return
    }

    const timer = setTimeout(() => {
      setCountdown(countdown - 1)
    }, 1000)

    return () => clearTimeout(timer)
  }, [countdown, showPaymentConfirmation])

  // Tính tổng tiền giảm giá từ voucher
  const calculateDiscount = () => {
    if (!invoiceData?.vouchers || invoiceData.vouchers.length === 0) return 0

    const voucher = invoiceData.vouchers[0]
    if (voucher.type === "percent") {
      return (invoiceData.subtotal * voucher.discount) / 100
    }
    return voucher.discount
  }

  const handleConfirm = () => {
    confirmPayment()
    setShowPaymentConfirmation(false)
  }

  const handleReject = () => {
    rejectPayment()
    setShowPaymentConfirmation(false)
  }

  const discount = calculateDiscount()

  if (!invoiceData) return null

  return (
    <Modal
      visible={showPaymentConfirmation}
      transparent={true}
      animationType="fade"
      onRequestClose={() => setShowPaymentConfirmation(false)}
    >
      <View style={styles.modalOverlay}>
        <Animated.View
          style={[
            styles.card,
            {
              backgroundColor: colors.card,
              borderColor: colors.border,
              transform: [{ scale: scaleAnim }],
              opacity: opacityAnim,
            },
          ]}
        >
          <View style={styles.iconContainer}>
            <Ionicons name="checkmark-circle" size={64} color={colors.success} />
          </View>

          <Text style={[styles.title, { color: colors.text }]}>Xác nhận thanh toán</Text>

          <Text style={[styles.message, { color: colors.muted }]}>Vui lòng xác nhận thanh toán cho hóa đơn</Text>

          {/* <ScrollView style={styles.summaryContainer}>
            <View style={[styles.amountContainer, { backgroundColor: colors.background }]}>
              <Text style={[styles.amountLabel, { color: colors.muted }]}>Tổng tiền:</Text>
              <Text style={[styles.amount, { color: colors.primary }]}>{formatCurrency(invoiceData.subtotal + (invoiceData.shipping?.cost ?? 0) - discount)}</Text>
            </View> */}

            {/* Thông tin tóm tắt */}
            {/* <View style={styles.summaryDetails}>
              <View style={styles.summaryRow}>
                <Text style={[styles.summaryLabel, { color: colors.muted }]}>Số lượng sản phẩm:</Text>
                <Text style={[styles.summaryValue, { color: colors.text }]}>{invoiceData.items.length}</Text>
              </View>

              <View style={styles.summaryRow}>
                <Text style={[styles.summaryLabel, { color: colors.muted }]}>Tạm tính:</Text>
                <Text style={[styles.summaryValue, { color: colors.text }]}>
                  {formatCurrency(invoiceData.subtotal)}
                </Text>
              </View>

              {invoiceData.shipping && (
                <View style={styles.summaryRow}>
                  <Text style={[styles.summaryLabel, { color: colors.muted }]}>Phí vận chuyển:</Text>
                  <Text style={[styles.summaryValue, { color: colors.text }]}>
                    {formatCurrency(invoiceData.shipping.cost)}
                  </Text>
                </View>
              )}

              {discount > 0 && (
                <View style={styles.summaryRow}>
                  <Text style={[styles.summaryLabel, { color: colors.success }]}>Giảm giá:</Text>
                  <Text style={[styles.summaryValue, { color: colors.success }]}>-{formatCurrency(discount)}</Text>
                </View>
              )} */}

              {/* <View style={styles.summaryRow}>
                <Text style={[styles.summaryLabel, { color: colors.muted }]}>Thuế:</Text>
                <Text style={[styles.summaryValue, { color: colors.text }]}>{formatCurrency(invoiceData.tax)}</Text>
              </View> */}
            {/* </View> */}

            {/* Phương thức thanh toán */}
            {/* {invoiceData.paymentMethod && (
              <View style={[styles.paymentMethod, { borderColor: colors.border }]}>
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
          </ScrollView> */}

          {/* <View style={styles.timerContainer}>
            <Animated.View
              style={[
                styles.progressBar,
                {
                  backgroundColor: colors.muted,
                  width: progressAnim.interpolate({
                    inputRange: [0, 1],
                    outputRange: ["0%", "100%"],
                  }),
                },
              ]}
            />
            <Text style={[styles.timer, { color: colors.text }]}>Tự động xác nhận sau {countdown}s</Text>
          </View> */}

          <View style={styles.buttonContainer}>
            <TouchableOpacity
              style={[styles.button, styles.rejectButton, { borderColor: colors.danger }]}
              onPress={handleReject}
            >
              <Ionicons name="close-circle-outline" size={20} color={colors.danger} />
              <Text style={[styles.buttonText, { color: colors.danger }]}>Hủy</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={[styles.button, styles.confirmButton, { backgroundColor: colors.success }]}
              onPress={handleConfirm}
            >
              <Ionicons name="checkmark-circle-outline" size={20} color="#FFFFFF" />
              <Text style={[styles.buttonText, { color: "#FFFFFF" }]}>Xác nhận</Text>
            </TouchableOpacity>
          </View>
        </Animated.View>
      </View>
    </Modal>
  )
}

const styles = StyleSheet.create({
  modalOverlay: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(0,0,0,0.5)",
    padding: 20,
  },
  card: {
    width: "100%",
    maxWidth: 400,
    padding: 24,
    borderRadius: 16,
    alignItems: "center",
    borderWidth: 1,
  },
  iconContainer: {
    marginBottom: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 8,
    textAlign: "center",
  },
  message: {
    fontSize: 16,
    textAlign: "center",
    marginBottom: 24,
  },
  summaryContainer: {
    width: "100%",
    maxHeight: 250,
  },
  amountContainer: {
    width: "100%",
    padding: 16,
    borderRadius: 8,
    alignItems: "center",
    marginBottom: 16,
  },
  amountLabel: {
    fontSize: 14,
    marginBottom: 4,
  },
  amount: {
    fontSize: 28,
    fontWeight: "bold",
  },
  summaryDetails: {
    marginBottom: 16,
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
    fontWeight: "500",
  },
  paymentMethod: {
    flexDirection: "row",
    alignItems: "center",
    paddingVertical: 12,
    borderTopWidth: 1,
    marginBottom: 16,
  },
  paymentText: {
    fontSize: 14,
    marginLeft: 8,
  },
  timerContainer: {
    width: "100%",
    height: 4,
    backgroundColor: "rgba(0,0,0,0.1)",
    borderRadius: 2,
    marginBottom: 8,
    overflow: "hidden",
  },
  progressBar: {
    height: "100%",
    position: "absolute",
    left: 0,
    top: 0,
  },
  timer: {
    fontSize: 14,
    textAlign: "center",
    marginBottom: 24,
  },
  buttonContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    width: "100%",
  },
  button: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    padding: 12,
    borderRadius: 8,
    flex: 1,
  },
  rejectButton: {
    marginRight: 8,
    borderWidth: 1,
  },
  confirmButton: {
    marginLeft: 8,
  },
  buttonText: {
    fontSize: 16,
    fontWeight: "bold",
    marginLeft: 8,
  },
})

export default PaymentConfirmationModal
