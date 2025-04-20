"use client"

import type React from "react"

import { createContext, useContext, useState, useEffect } from "react"
import { useColorScheme } from "react-native"

// Định nghĩa kiểu cho theme
interface ThemeColors {
  primary: string
  background: string
  card: string
  text: string
  border: string
  muted: string
  success: string
  danger: string
}

interface ThemeContextType {
  isDark: boolean
  toggleTheme: () => void
  colors: ThemeColors
}

// Tạo context
const ThemeContext = createContext<ThemeContextType | undefined>(undefined)

// Định nghĩa các màu sắc cho theme
const lightColors: ThemeColors = {
  primary: "#007AFF",
  background: "#F2F2F7",
  card: "#FFFFFF",
  text: "#1C1C1E",
  border: "#E5E5EA",
  muted: "#8E8E93",
  success: "#34C759",
  danger: "#FF3B30",
}

const darkColors: ThemeColors = {
  primary: "#0A84FF",
  background: "#1C1C1E",
  card: "#2C2C2E",
  text: "#FFFFFF",
  border: "#38383A",
  muted: "#8E8E93",
  success: "#30D158",
  danger: "#FF453A",
}

// Provider component
export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Lấy theme từ hệ thống
  const colorScheme = useColorScheme()
  const [isDark, setIsDark] = useState(colorScheme === "dark")

  // Cập nhật theme khi theme hệ thống thay đổi
  useEffect(() => {
    setIsDark(colorScheme === "dark")
  }, [colorScheme])

  // Hàm toggle theme
  const toggleTheme = () => {
    setIsDark((prev) => !prev)
  }

  // Chọn bảng màu dựa trên theme
  const colors = isDark ? darkColors : lightColors

  return <ThemeContext.Provider value={{ isDark, toggleTheme, colors }}>{children}</ThemeContext.Provider>
}

// Custom hook để sử dụng theme
export const useTheme = () => {
  const context = useContext(ThemeContext)
  if (context === undefined) {
    throw new Error("useTheme must be used within a ThemeProvider")
  }
  return context
}
