import { StatusBar } from "expo-status-bar"
import { NavigationContainer } from "@react-navigation/native"
import { createNativeStackNavigator } from "@react-navigation/native-stack"
import { AppProvider } from "./context/AppContext"
import { ThemeProvider } from "./context/ThemeContext"
import InvoiceScreen from "./screens/InvoiceScreen"
import SettingsScreen from "./screens/SettingsScreen"
import PaymentScreen from "./screens/PaymentScreen"
import HomeScreen from "./screens/HomeScreen"

// Định nghĩa kiểu cho Stack Navigator
export type RootStackParamList = {
  Invoice: undefined
  Settings: undefined
  Payment: undefined
  Home: undefined
}

const Stack = createNativeStackNavigator<RootStackParamList>()

export default function App() {
  return (
    <NavigationContainer>
      <ThemeProvider>
        <AppProvider>
          <StatusBar style="auto" />
          <Stack.Navigator
            initialRouteName="Home"
            screenOptions={{
              headerShown: false,
              animation: "slide_from_right",
            }}
          >
            <Stack.Screen name="Invoice" component={InvoiceScreen} />
            <Stack.Screen name="Settings" component={SettingsScreen} />
            <Stack.Screen name="Payment" component={PaymentScreen} />
            <Stack.Screen name="Home" component={HomeScreen} />
          </Stack.Navigator>
        </AppProvider>
      </ThemeProvider>
    </NavigationContainer>
  )
}
