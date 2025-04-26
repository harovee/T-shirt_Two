"use client"

import { useState, useRef, useEffect } from "react"
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  ScrollView,
  Dimensions,
  FlatList,
  Animated,
  ActivityIndicator,
} from "react-native"
import { StatusBar } from "expo-status-bar"
import { Ionicons } from "@expo/vector-icons"
import { useTheme } from "../context/ThemeContext"
import { useAppContext } from "../context/AppContext"
import { useNavigation } from "@react-navigation/native"
import type { AppNavigationProp } from "../types/navigation"
import PaymentConfirmationModal from "../components/PaymentConfirmationModal"

const { width } = Dimensions.get("window")

// Dữ liệu mẫu cho banner
const BANNERS = [
  {
    id: "1",
    image: "https://placeholder.svg?height=200&width=400&query=store+interior",
    title: "Chào mừng đến với cửa hàng",
    description: "Nơi mua sắm tuyệt vời cho mọi nhu cầu của bạn",
  },
  {
    id: "2",
    image: "https://placeholder.svg?height=200&width=400&query=fashion+store",
    title: "Bộ sưu tập mới nhất",
    description: "Khám phá các xu hướng thời trang mùa này",
  },
  {
    id: "3",
    image: "https://placeholder.svg?height=200&width=400&query=sale+promotion",
    title: "Khuyến mãi đặc biệt",
    description: "Giảm giá lên đến 50% cho các sản phẩm chọn lọc",
  },
]

// Dữ liệu mẫu cho thông tin cửa hàng
const STORE_INFO = {
  name: "T-Shirt Two",
  address: "Trịnh Văn Bô, Phương Canh, Nam Từ Liêm, Hà Nội",
  phone: "0123 456 789",
  email: "contact@tshirttwo.com",
  website: "www.tshirttwo.com",
  openHours: "8:00 - 22:00 (Thứ 2 - Chủ nhật)",
  description:
    "T-Shirt Two là điểm đến lý tưởng cho những người yêu thời trang. Chúng tôi cung cấp các sản phẩm chất lượng cao với giá cả hợp lý, đảm bảo sự hài lòng cho mọi khách hàng.",
}

const HomeScreen = () => {
  const { colors, isDark } = useTheme()
  const { invoiceData, disconnectFromPOS, connectionStatus, reconnectToPOS } = useAppContext()
  const navigation = useNavigation<AppNavigationProp>()

  // State cho banner slider
  const [currentBannerIndex, setCurrentBannerIndex] = useState(0)
  const bannerScrollRef = useRef<FlatList>(null)
  const fadeAnim = useRef(new Animated.Value(0)).current

  // Lấy thông tin server từ biến môi trường
  const SERVER_IP = process.env.SERVER_IP || "192.168.1.100"
  const SERVER_PORT = process.env.SERVER_PORT || "8080"

  // Auto scroll banner
  useEffect(() => {
    const timer = setInterval(() => {
      const nextIndex = (currentBannerIndex + 1) % BANNERS.length
      bannerScrollRef.current?.scrollToIndex({
        index: nextIndex,
        animated: true,
      })
      setCurrentBannerIndex(nextIndex)
    }, 5000)

    return () => clearInterval(timer)
  }, [currentBannerIndex])

  // Animation
  useEffect(() => {
    Animated.timing(fadeAnim, {
      toValue: 1,
      duration: 500,
      useNativeDriver: true,
    }).start()
  }, [])

  // Kiểm tra nếu có hóa đơn mới, tự động chuyển đến màn hình Invoice
  useEffect(() => {
    if (invoiceData && connectionStatus === "connected") {
      // Nếu có hóa đơn mới và đã kết nối, chuyển đến màn hình Invoice
      navigation.navigate("Invoice")
    }
  }, [invoiceData, connectionStatus, navigation])

  const handleSettings = () => {
    navigation.navigate("Settings")
  }

  const handleDisconnect = () => {
    disconnectFromPOS(navigation)
  }

  const handleBannerScroll = (event: any) => {
    const contentOffset = event.nativeEvent.contentOffset.x
    const index = Math.round(contentOffset / width)
    setCurrentBannerIndex(index)
  }

  const handleReconnect = () => {
    reconnectToPOS()
  }

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      <StatusBar style={isDark ? "light" : "dark"} />

      {/* Header */}
      <View style={[styles.header, { backgroundColor: colors.card }]}>
        <View style={styles.headerContent}>
          <Text style={[styles.headerTitle, { color: colors.text }]}>{STORE_INFO.name}</Text>
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

      <ScrollView style={styles.scrollContainer} showsVerticalScrollIndicator={false}>
        <Animated.View style={{ opacity: fadeAnim }}>
          {/* Banner Slider */}
          <View style={styles.bannerContainer}>
            <FlatList
              ref={bannerScrollRef}
              data={BANNERS}
              horizontal
              pagingEnabled
              showsHorizontalScrollIndicator={false}
              onMomentumScrollEnd={handleBannerScroll}
              keyExtractor={(item) => item.id}
              renderItem={({ item }) => (
                <View style={styles.bannerSlide}>
                  <Image source={{ uri: item.image }} style={styles.bannerImage} />
                  <View style={[styles.bannerOverlay, { backgroundColor: colors.card + "CC" }]}>
                    <Text style={[styles.bannerTitle, { color: colors.text }]}>{item.title}</Text>
                    <Text style={[styles.bannerDescription, { color: colors.muted }]}>{item.description}</Text>
                  </View>
                </View>
              )}
            />

            {/* Dots indicator */}
            <View style={styles.dotsContainer}>
              {BANNERS.map((_, index) => (
                <View
                  key={index}
                  style={[
                    styles.dot,
                    {
                      backgroundColor: index === currentBannerIndex ? colors.primary : colors.muted + "80",
                    },
                  ]}
                />
              ))}
            </View>
          </View>

          {/* Connection Info */}
          <View style={[styles.connectionCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <Text style={[styles.sectionTitle, { color: colors.text }]}>Thông tin kết nối</Text>
            <View style={styles.connectionInfo}>
              <View style={styles.connectionRow}>
                <Ionicons name="server-outline" size={20} color={colors.primary} />
                <Text style={[styles.connectionText, { color: colors.text }]}>
                  Máy chủ: {SERVER_IP}:{SERVER_PORT}
                </Text>
              </View>
              <View style={styles.connectionRow}>
                <Ionicons
                  name={connectionStatus === "connected" ? "checkmark-circle-outline" : "alert-circle-outline"}
                  size={20}
                  color={connectionStatus === "connected" ? colors.success : colors.danger}
                />
                <Text
                  style={[
                    styles.connectionText,
                    {
                      color:
                        connectionStatus === "connected"
                          ? colors.success
                          : connectionStatus === "connecting"
                            ? colors.primary
                            : colors.danger,
                    },
                  ]}
                >
                  Trạng thái:{" "}
                  {connectionStatus === "connected"
                    ? "Đã kết nối"
                    : connectionStatus === "connecting"
                      ? "Đang kết nối..."
                      : "Chưa kết nối"}
                </Text>
              </View>
              {connectionStatus === "connecting" && (
                <ActivityIndicator size="small" color={colors.primary} style={styles.connectionLoader} />
              )}
            </View>
          </View>

          {/* Store Information */}
          <View style={[styles.infoCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <Text style={[styles.sectionTitle, { color: colors.text }]}>Thông tin cửa hàng</Text>

            <View style={styles.infoRow}>
              <Ionicons name="location-outline" size={20} color={colors.primary} />
              <Text style={[styles.infoText, { color: colors.text }]}>{STORE_INFO.address}</Text>
            </View>

            <View style={styles.infoRow}>
              <Ionicons name="call-outline" size={20} color={colors.primary} />
              <Text style={[styles.infoText, { color: colors.text }]}>{STORE_INFO.phone}</Text>
            </View>

            <View style={styles.infoRow}>
              <Ionicons name="mail-outline" size={20} color={colors.primary} />
              <Text style={[styles.infoText, { color: colors.text }]}>{STORE_INFO.email}</Text>
            </View>

            <View style={styles.infoRow}>
              <Ionicons name="globe-outline" size={20} color={colors.primary} />
              <Text style={[styles.infoText, { color: colors.text }]}>{STORE_INFO.website}</Text>
            </View>

            <View style={styles.infoRow}>
              <Ionicons name="time-outline" size={20} color={colors.primary} />
              <Text style={[styles.infoText, { color: colors.text }]}>{STORE_INFO.openHours}</Text>
            </View>
          </View>

          {/* Store Description */}
          <View style={[styles.descriptionCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <Text style={[styles.sectionTitle, { color: colors.text }]}>Giới thiệu</Text>
            <Text style={[styles.descriptionText, { color: colors.text }]}>{STORE_INFO.description}</Text>
          </View>

          {/* Status Message */}
          <View
            style={[
              styles.statusCard,
              {
                backgroundColor: connectionStatus === "connected" ? colors.success + "20" : colors.danger + "20",
                borderColor: connectionStatus === "connected" ? colors.success : colors.danger,
              },
            ]}
          >
            <Ionicons
              name={connectionStatus === "connected" ? "checkmark-circle-outline" : "alert-circle-outline"}
              size={24}
              color={connectionStatus === "connected" ? colors.success : colors.danger}
            />
            <Text
              style={[styles.statusText, { color: connectionStatus === "connected" ? colors.success : colors.danger }]}
            >
              {connectionStatus === "connected"
                ? "Đã kết nối với máy quầy. Đang chờ hóa đơn mới..."
                : connectionStatus === "connecting"
                  ? "Đang kết nối với máy quầy..."
                  : "Chưa kết nối với máy quầy. Vui lòng kiểm tra kết nối."}
            </Text>

            {/* Thêm nút kết nối lại khi trạng thái là disconnected */}
            {connectionStatus === "disconnected" && (
              <TouchableOpacity
                style={[styles.reconnectButton, { backgroundColor: colors.primary }]}
                onPress={handleReconnect}
              >
                <Ionicons name="refresh-outline" size={16} color="#FFFFFF" />
                <Text style={styles.reconnectText}>Kết nối lại</Text>
              </TouchableOpacity>
            )}
          </View>
        </Animated.View>
      </ScrollView>

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
  bannerContainer: {
    height: 200,
    marginBottom: 16,
  },
  bannerSlide: {
    width: width,
    height: 200,
    position: "relative",
  },
  bannerImage: {
    width: width,
    height: 200,
    resizeMode: "cover",
  },
  bannerOverlay: {
    position: "absolute",
    bottom: 0,
    left: 0,
    right: 0,
    padding: 16,
  },
  bannerTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 4,
  },
  bannerDescription: {
    fontSize: 14,
  },
  dotsContainer: {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center",
    position: "absolute",
    bottom: 10,
    left: 0,
    right: 0,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    marginHorizontal: 4,
  },
  connectionCard: {
    margin: 16,
    padding: 16,
    borderRadius: 12,
    borderWidth: 1,
    marginBottom: 8,
  },
  connectionInfo: {
    marginTop: 8,
  },
  connectionRow: {
    flexDirection: "row",
    alignItems: "center",
    marginBottom: 8,
  },
  connectionText: {
    fontSize: 14,
    marginLeft: 12,
  },
  connectionLoader: {
    marginTop: 8,
    alignSelf: "center",
  },
  infoCard: {
    margin: 16,
    padding: 16,
    borderRadius: 12,
    borderWidth: 1,
    marginTop: 8,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 16,
  },
  infoRow: {
    flexDirection: "row",
    alignItems: "flex-start",
    marginBottom: 12,
  },
  infoText: {
    fontSize: 14,
    marginLeft: 12,
    flex: 1,
  },
  descriptionCard: {
    marginHorizontal: 16,
    marginBottom: 16,
    padding: 16,
    borderRadius: 12,
    borderWidth: 1,
  },
  descriptionText: {
    fontSize: 14,
    lineHeight: 20,
  },
  statusCard: {
    flexDirection: "row",
    alignItems: "center",
    marginHorizontal: 16,
    marginBottom: 24,
    padding: 16,
    borderRadius: 12,
    borderWidth: 1,
  },
  statusText: {
    fontSize: 14,
    marginLeft: 12,
    flex: 1,
  },
  reconnectButton: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    paddingVertical: 6,
    paddingHorizontal: 12,
    borderRadius: 16,
    marginLeft: 8,
  },
  reconnectText: {
    color: "#FFFFFF",
    fontSize: 12,
    fontWeight: "500",
    marginLeft: 4,
  },
})

export default HomeScreen
