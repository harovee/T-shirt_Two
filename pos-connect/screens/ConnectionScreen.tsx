"use client"

import { useState, useEffect, useRef } from "react"
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  FlatList,
  Animated,
  Keyboard,
  Alert,
  ActivityIndicator,
} from "react-native"
import { StatusBar } from "expo-status-bar"
import { Ionicons } from "@expo/vector-icons"
import { useAppContext } from "../context/AppContext"
import { useTheme } from "../context/ThemeContext"
import { useNavigation } from "@react-navigation/native"
import type { AppNavigationProp } from "../types/navigation"

// Danh sách kết nối gần đây mẫu
const RECENT_CONNECTIONS = [
  { id: "1", name: "POS Cửa hàng chính", connectionId: "POS-MAIN-01" },
  { id: "2", name: "POS Quầy thu ngân 1", connectionId: "POS-CHECKOUT-01" },
  { id: "3", name: "POS Quầy thu ngân 2", connectionId: "POS-CHECKOUT-02" },
]

const ConnectionScreen = () => {
  const { connectToPOS, connectionStatus, lastConnectionId, saveConnectionId } = useAppContext()
  const { colors, isDark } = useTheme()
  const navigation = useNavigation<AppNavigationProp>()
  const [connectionId, setConnectionId] = useState("")
  const [recentConnections, setRecentConnections] = useState(RECENT_CONNECTIONS)
  const [isKeyboardVisible, setKeyboardVisible] = useState(false)
  // Thêm vào phần đầu của component ConnectionScreen
  // State để theo dõi trạng thái kết nối
  const [reconnecting, setReconnecting] = useState(false)

  // Animation refs
  const fadeAnim = useRef(new Animated.Value(0)).current
  const slideAnim = useRef(new Animated.Value(50)).current

  useEffect(() => {
    // Khôi phục ID kết nối gần đây nhất
    if (lastConnectionId) {
      setConnectionId(lastConnectionId)
    }

    // Bắt đầu animation khi component mount
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

    // Theo dõi sự kiện hiển thị/ẩn bàn phím
    const keyboardDidShowListener = Keyboard.addListener("keyboardDidShow", () => {
      setKeyboardVisible(true)
    })
    const keyboardDidHideListener = Keyboard.addListener("keyboardDidHide", () => {
      setKeyboardVisible(false)
    })

    // Cleanup listeners
    return () => {
      keyboardDidShowListener.remove()
      keyboardDidHideListener.remove()
    }
  }, [])

  // Cập nhật useEffect để theo dõi trạng thái kết nối
  useEffect(() => {
    if (connectionStatus === "connected") {
      // Lưu ID kết nối
      saveConnectionId(connectionId)

      // Thêm vào danh sách kết nối gần đây nếu chưa có
      const exists = recentConnections.some((conn) => conn.connectionId === connectionId)
      if (!exists && connectionId) {
        const newConnection = {
          id: (recentConnections.length + 1).toString(),
          name: `POS ${connectionId}`,
          connectionId: connectionId,
        }
        setRecentConnections((prev) => [newConnection, ...prev])
      }

      // Đặt lại trạng thái reconnecting
      setReconnecting(false)

      // Chuyển đến màn hình hóa đơn
      navigation.navigate("Invoice")
    }
  }, [connectionStatus])

  // Cập nhật phần xử lý kết nối để phản ánh WebSocket
  // Tìm hàm handleConnect và cập nhật như sau:

  const handleConnect = () => {
    if (!connectionId.trim()) {
      Alert.alert("Lỗi", "Vui lòng nhập ID kết nối")
      return
    }

    // Hiển thị trạng thái đang kết nối
    setReconnecting(true)

    // Kết nối qua WebSocket
    connectToPOS(connectionId)
  }

  // Thêm useEffect để theo dõi trạng thái kết nối
  useEffect(() => {
    if (connectionStatus === "connected") {
      // Khi kết nối thành công, tắt trạng thái reconnecting
      setReconnecting(false)

      // Chuyển đến màn hình Invoice
      navigation.navigate("Invoice")
    } else if (connectionStatus === "disconnected") {
      // Khi ngắt kết nối, tắt trạng thái reconnecting
      setReconnecting(false)
    }
  }, [connectionStatus, navigation])

  const handleSelectRecent = (item: (typeof RECENT_CONNECTIONS)[0]) => {
    setConnectionId(item.connectionId)
  }

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      <StatusBar style={isDark ? "light" : "dark"} />

      {reconnecting && (
        <View style={styles.reconnectingBanner}>
          <ActivityIndicator size="small" color="#FFFFFF" />
          <Text style={styles.reconnectingText}>Đang kết nối lại với máy quầy...</Text>
        </View>
      )}

      <Animated.View
        style={[
          styles.content,
          {
            opacity: fadeAnim,
            transform: [{ translateY: slideAnim }],
          },
        ]}
      >
        <View style={styles.logoContainer}>
          <Ionicons name="phone-portrait-outline" size={64} color={colors.primary} />
          <Text style={[styles.title, { color: colors.text }]}>POS Customer Display</Text>
          <Text style={[styles.subtitle, { color: colors.muted }]}>Kết nối với hệ thống POS</Text>
        </View>

        <View style={styles.formContainer}>
          <View style={[styles.inputContainer, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <Ionicons name="link-outline" size={24} color={colors.muted} />
            <TextInput
              style={[styles.input, { color: colors.text }]}
              placeholder="Nhập ID kết nối"
              placeholderTextColor={colors.muted}
              value={connectionId}
              onChangeText={setConnectionId}
              autoCapitalize="none"
            />
            {connectionId.length > 0 && (
              <TouchableOpacity onPress={() => setConnectionId("")}>
                <Ionicons name="close-circle" size={24} color={colors.muted} />
              </TouchableOpacity>
            )}
          </View>

          <TouchableOpacity
            style={[styles.connectButton, { backgroundColor: colors.primary }]}
            onPress={handleConnect}
            disabled={connectionStatus === "connecting"}
          >
            {connectionStatus === "connecting" ? (
              <ActivityIndicator color="#FFFFFF" />
            ) : (
              <>
                <Ionicons name="arrow-forward" size={24} color="#FFFFFF" />
                <Text style={styles.connectButtonText}>Kết nối</Text>
              </>
            )}
          </TouchableOpacity>
        </View>

        {!isKeyboardVisible && (
          <View style={styles.recentContainer}>
            <Text style={[styles.recentTitle, { color: colors.text }]}>Kết nối gần đây</Text>
            <FlatList
              data={recentConnections}
              keyExtractor={(item) => item.id}
              renderItem={({ item }) => (
                <TouchableOpacity
                  style={[styles.recentItem, { backgroundColor: colors.card, borderColor: colors.border }]}
                  onPress={() => handleSelectRecent(item)}
                >
                  <View style={styles.recentItemContent}>
                    <Ionicons name="time-outline" size={24} color={colors.primary} />
                    <View style={styles.recentItemText}>
                      <Text style={[styles.recentItemName, { color: colors.text }]}>{item.name}</Text>
                      <Text style={[styles.recentItemId, { color: colors.muted }]}>{item.connectionId}</Text>
                    </View>
                  </View>
                  <Ionicons name="chevron-forward" size={20} color={colors.muted} />
                </TouchableOpacity>
              )}
              contentContainerStyle={styles.recentList}
            />
          </View>
        )}
      </Animated.View>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flex: 1,
    padding: 20,
    justifyContent: "center",
  },
  logoContainer: {
    alignItems: "center",
    marginBottom: 40,
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    marginTop: 16,
  },
  subtitle: {
    fontSize: 16,
    marginTop: 8,
  },
  formContainer: {
    marginBottom: 30,
  },
  inputContainer: {
    flexDirection: "row",
    alignItems: "center",
    borderWidth: 1,
    borderRadius: 12,
    paddingHorizontal: 16,
    height: 56,
    marginBottom: 16,
  },
  input: {
    flex: 1,
    height: 56,
    marginLeft: 12,
    fontSize: 16,
  },
  connectButton: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    height: 56,
    borderRadius: 12,
  },
  connectButtonText: {
    color: "#FFFFFF",
    fontSize: 18,
    fontWeight: "bold",
    marginLeft: 8,
  },
  recentContainer: {
    flex: 1,
    maxHeight: 300,
  },
  recentTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 12,
  },
  recentList: {
    paddingBottom: 20,
  },
  recentItem: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    padding: 16,
    borderRadius: 12,
    marginBottom: 8,
    borderWidth: 1,
  },
  recentItemContent: {
    flexDirection: "row",
    alignItems: "center",
  },
  recentItemText: {
    marginLeft: 12,
  },
  recentItemName: {
    fontSize: 16,
    fontWeight: "500",
  },
  recentItemId: {
    fontSize: 14,
    marginTop: 4,
  },
  reconnectingBanner: {
    backgroundColor: "rgba(0,0,0,0.7)",
    paddingVertical: 8,
    paddingHorizontal: 16,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    position: "absolute",
    top: 40,
    left: 0,
    right: 0,
    zIndex: 100,
  },
  reconnectingText: {
    color: "#FFFFFF",
    marginLeft: 8,
    fontSize: 14,
  },
})

export default ConnectionScreen
