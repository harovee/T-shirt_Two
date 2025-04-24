"use client"

import { useState } from "react"
import { View, Text, StyleSheet, TouchableOpacity, Switch, ScrollView } from "react-native"
import { Ionicons } from "@expo/vector-icons"
import { useTheme } from "../context/ThemeContext"
import { StatusBar } from "expo-status-bar"
import { useNavigation } from "@react-navigation/native"
import type { AppNavigationProp } from "../types/navigation"

const SettingsScreen = () => {
  const { colors, isDark, toggleTheme } = useTheme()
  const navigation = useNavigation<AppNavigationProp>()

  // State cho các cài đặt
  const [notifications, setNotifications] = useState(true)
  const [sound, setSound] = useState(true)
  const [autoConnect, setAutoConnect] = useState(false)

  const handleBack = () => {
    navigation.goBack()
  }

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      <StatusBar style={isDark ? "light" : "dark"} />

      {/* Header */}
      <View style={[styles.header, { backgroundColor: colors.card }]}>
        <View style={styles.headerContent}>
          <TouchableOpacity onPress={handleBack} style={styles.backButton}>
            <Ionicons name="arrow-back" size={24} color={colors.text} />
          </TouchableOpacity>
          <Text style={[styles.headerTitle, { color: colors.text }]}>Cài đặt</Text>
          <View style={{ width: 24 }} />
        </View>
      </View>

      <ScrollView style={styles.content}>
        {/* Appearance */}
        <View style={styles.section}>
          <Text style={[styles.sectionTitle, { color: colors.text }]}>Giao diện</Text>
          <View style={[styles.settingCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <View style={styles.settingRow}>
              <View style={styles.settingInfo}>
                <Ionicons name="moon-outline" size={22} color={colors.primary} />
                <Text style={[styles.settingText, { color: colors.text }]}>Chế độ tối</Text>
              </View>
              <Switch
                value={isDark}
                onValueChange={toggleTheme}
                trackColor={{ false: "#767577", true: colors.primary + "70" }}
                thumbColor={isDark ? colors.primary : "#f4f3f4"}
              />
            </View>
          </View>
        </View>

        {/* Notifications */}
        <View style={styles.section}>
          <Text style={[styles.sectionTitle, { color: colors.text }]}>Thông báo</Text>
          <View style={[styles.settingCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <View style={styles.settingRow}>
              <View style={styles.settingInfo}>
                <Ionicons name="notifications-outline" size={22} color={colors.primary} />
                <Text style={[styles.settingText, { color: colors.text }]}>Thông báo</Text>
              </View>
              <Switch
                value={notifications}
                onValueChange={setNotifications}
                trackColor={{ false: "#767577", true: colors.primary + "70" }}
                thumbColor={notifications ? colors.primary : "#f4f3f4"}
              />
            </View>

            <View style={[styles.divider, { backgroundColor: colors.border }]} />

            <View style={styles.settingRow}>
              <View style={styles.settingInfo}>
                <Ionicons name="volume-medium-outline" size={22} color={colors.primary} />
                <Text style={[styles.settingText, { color: colors.text }]}>Âm thanh</Text>
              </View>
              <Switch
                value={sound}
                onValueChange={setSound}
                trackColor={{ false: "#767577", true: colors.primary + "70" }}
                thumbColor={sound ? colors.primary : "#f4f3f4"}
              />
            </View>
          </View>
        </View>

        {/* Connection */}
        <View style={styles.section}>
          <Text style={[styles.sectionTitle, { color: colors.text }]}>Kết nối</Text>
          <View style={[styles.settingCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <View style={styles.settingRow}>
              <View style={styles.settingInfo}>
                <Ionicons name="link-outline" size={22} color={colors.primary} />
                <Text style={[styles.settingText, { color: colors.text }]}>Tự động kết nối</Text>
              </View>
              <Switch
                value={autoConnect}
                onValueChange={setAutoConnect}
                trackColor={{ false: "#767577", true: colors.primary + "70" }}
                thumbColor={autoConnect ? colors.primary : "#f4f3f4"}
              />
            </View>
          </View>
        </View>

        {/* About */}
        <View style={styles.section}>
          <Text style={[styles.sectionTitle, { color: colors.text }]}>Thông tin</Text>
          <View style={[styles.settingCard, { backgroundColor: colors.card, borderColor: colors.border }]}>
            <TouchableOpacity style={styles.settingRow}>
              <View style={styles.settingInfo}>
                <Ionicons name="information-circle-outline" size={22} color={colors.primary} />
                <Text style={[styles.settingText, { color: colors.text }]}>Phiên bản</Text>
              </View>
              <View style={styles.settingAction}>
                <Text style={[styles.versionText, { color: colors.muted }]}>1.0.0</Text>
                <Ionicons name="chevron-forward" size={20} color={colors.muted} />
              </View>
            </TouchableOpacity>

            <View style={[styles.divider, { backgroundColor: colors.border }]} />

            <TouchableOpacity style={styles.settingRow}>
              <View style={styles.settingInfo}>
                <Ionicons name="help-circle-outline" size={22} color={colors.primary} />
                <Text style={[styles.settingText, { color: colors.text }]}>Trợ giúp</Text>
              </View>
              <Ionicons name="chevron-forward" size={20} color={colors.muted} />
            </TouchableOpacity>

            <View style={[styles.divider, { backgroundColor: colors.border }]} />

            <TouchableOpacity style={styles.settingRow}>
              <View style={styles.settingInfo}>
                <Ionicons name="document-text-outline" size={22} color={colors.primary} />
                <Text style={[styles.settingText, { color: colors.text }]}>Điều khoản sử dụng</Text>
              </View>
              <Ionicons name="chevron-forward" size={20} color={colors.muted} />
            </TouchableOpacity>
          </View>
        </View>
      </ScrollView>
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
  backButton: {
    padding: 8,
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: "bold",
  },
  content: {
    flex: 1,
    padding: 16,
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 12,
  },
  settingCard: {
    borderRadius: 12,
    borderWidth: 1,
    overflow: "hidden",
  },
  settingRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    padding: 16,
  },
  settingInfo: {
    flexDirection: "row",
    alignItems: "center",
  },
  settingText: {
    fontSize: 16,
    marginLeft: 12,
  },
  settingAction: {
    flexDirection: "row",
    alignItems: "center",
  },
  versionText: {
    fontSize: 14,
    marginRight: 8,
  },
  divider: {
    height: 1,
    width: "100%",
  },
})

export default SettingsScreen
