"use client"

import { useRef, useEffect, useState } from "react"
import { View, Text, StyleSheet, TouchableOpacity, Animated } from "react-native"
import { Ionicons } from "@expo/vector-icons"
import { useAppContext } from "../context/AppContext"
import { useTheme } from "../context/ThemeContext"
import { StatusBar } from "expo-status-bar"
import { useNavigation } from "@react-navigation/native"
import type { AppNavigationProp } from "../types/navigation"

const PaymentScreen = () => {
  const { invoiceData, confirmPayment, rejectPayment, checkInvoiceStatus } = useAppContext()
  const { colors, isDark } = useTheme()
  const navigation = useNavigation<AppNavigationProp>()

  // Animation refs
  const fadeAnim = useRef(new Animated.Value(0)).current
  const scaleAnim = useRef(new Animated.Value(0.9)).current

  // Countdown state
  const [countdown, setCountdown] = useState(30)
  // Thêm state để theo dõi khi nào cần tự động xác nhận
  const [shouldAutoConfirm, setShouldAutoConfirm] = useState(false)

  useEffect(() => {
    // Start animations
    Animated.parallel([
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: 300,
        useNativeDriver: true,
      }),
      Animated.timing(scaleAnim, {
        toValue: 1,
        duration: 300,
        useNativeDriver: true,
      }),
    ]).start()

    // Start countdown
    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timer)
          // Đặt flag để xác nhận tự động thay vì gọi trực tiếp
          setShouldAutoConfirm(true)
          return 0
        }
        return prev - 1
      })
    }, 1000)

    return () => clearInterval(timer)
  }, [])

  // Thêm useEffect riêng để xử lý xác nhận tự động
  useEffect(() => {
    if (shouldAutoConfirm) {
      handleConfirm()
    }
  }, [shouldAutoConfirm])

  const handleConfirm = () => {
    // Xác nhận thanh toán qua WebSocket
    confirmPayment()
    // Không cần chuyển hướng ở đây vì sẽ được xử lý bởi WebSocket message handler
  }

  const handleReject = () => {
    // Từ chối thanh toán qua WebSocket
    rejectPayment()
    // Không cần chuyển hướng ở đây vì sẽ được xử lý bởi WebSocket message handler
  }

  // Format currency
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount)
  }

  if (!invoiceData) {
    return (
      <View style={[styles.container, { backgroundColor: colors.background }]}>
        <StatusBar style={isDark ? "light" : "dark"} />
        <Text style={[styles.emptyText, { color: colors.text }]}>Không có dữ liệu thanh toán</Text>
      </View>
    )
  }

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      <StatusBar style={isDark ? "light" : "dark"} />

      <Animated.View
        style={[
          styles.content,
          {
            opacity: fadeAnim,
            transform: [{ scale: scaleAnim }],
          },
        ]}
      >
        <View style={[styles.card, { backgroundColor: colors.card, borderColor: colors.border }]}>
          <View style={styles.cardHeader}>
            <Ionicons name="card-outline" size={32} color={colors.primary} />
            <Text style={[styles.cardTitle, { color: colors.text }]}>Xác nhận thanh toán</Text>
          </View>

          <View style={[styles.divider, { backgroundColor: colors.border }]} />

          <View style={styles.paymentInfo}>
            <View style={styles.infoRow}>
              <Text style={[styles.infoLabel, { color: colors.muted }]}>Mã hóa đơn:</Text>
              <Text style={[styles.infoValue, { color: colors.text }]}>{invoiceData.id}</Text>
            </View>

            <View style={styles.infoRow}>
              <Text style={[styles.infoLabel, { color: colors.muted }]}>Phương thức:</Text>
              <Text style={[styles.infoValue, { color: colors.text }]}>{invoiceData.paymentMethod}</Text>
            </View>

            <View style={styles.infoRow}>
              <Text style={[styles.infoLabel, { color: colors.muted }]}>Số tiền:</Text>
              <Text style={[styles.infoValue, styles.amount, { color: colors.primary }]}>
                {formatCurrency(invoiceData.total)}
              </Text>
            </View>
          </View>

          <View style={[styles.divider, { backgroundColor: colors.border }]} />

          <View style={styles.countdownContainer}>
            <Text style={[styles.confirmText, { color: colors.text }]}>
              Vui lòng xác nhận thanh toán cho hóa đơn này
            </Text>
            <Text style={[styles.countdownText, { color: colors.muted }]}>Tự động xác nhận sau {countdown} giây</Text>
            <View style={styles.progressContainer}>
              <View
                style={[
                  styles.progressBar,
                  {
                    backgroundColor: colors.primary,
                    width: `${(countdown / 30) * 100}%`,
                  },
                ]}
              />
            </View>
          </View>

          <View style={styles.buttonContainer}>
            <TouchableOpacity
              style={[styles.button, styles.rejectButton, { backgroundColor: colors.danger + "20" }]}
              onPress={handleReject}
            >
              <Ionicons name="close-circle" size={24} color={colors.danger} />
              <Text style={[styles.buttonText, { color: colors.danger }]}>Từ chối</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={[styles.button, styles.confirmButton, { backgroundColor: colors.primary }]}
              onPress={handleConfirm}
            >
              <Ionicons name="checkmark-circle" size={24} color="#FFFFFF" />
              <Text style={styles.buttonText}>Xác nhận</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Animated.View>
    </View>
  )
}

// Thêm styles cho countdown
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: 20,
  },
  content: {
    width: "100%",
    maxWidth: 400,
  },
  card: {
    borderRadius: 16,
    borderWidth: 1,
    padding: 24,
  },
  cardHeader: {
    alignItems: "center",
    marginBottom: 20,
  },
  cardTitle: {
    fontSize: 24,
    fontWeight: "bold",
    marginTop: 12,
  },
  divider: {
    height: 1,
    width: "100%",
    marginVertical: 20,
  },
  paymentInfo: {
    marginBottom: 20,
  },
  infoRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 12,
  },
  infoLabel: {
    fontSize: 16,
  },
  infoValue: {
    fontSize: 16,
    fontWeight: "500",
  },
  amount: {
    fontSize: 20,
    fontWeight: "bold",
  },
  countdownContainer: {
    alignItems: "center",
    marginBottom: 24,
  },
  confirmText: {
    fontSize: 16,
    textAlign: "center",
    marginBottom: 12,
  },
  countdownText: {
    fontSize: 14,
    marginBottom: 8,
  },
  progressContainer: {
    width: "100%",
    height: 4,
    backgroundColor: "rgba(0,0,0,0.1)",
    borderRadius: 2,
    overflow: "hidden",
  },
  progressBar: {
    height: "100%",
    borderRadius: 2,
  },
  buttonContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
  },
  button: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    height: 56,
    borderRadius: 12,
    paddingHorizontal: 20,
  },
  rejectButton: {
    flex: 1,
    marginRight: 8,
  },
  confirmButton: {
    flex: 1,
    marginLeft: 8,
  },
  buttonText: {
    color: "#FFFFFF",
    fontSize: 16,
    fontWeight: "bold",
    marginLeft: 8,
  },
  emptyText: {
    fontSize: 18,
    fontWeight: "bold",
  },
})

export default PaymentScreen
