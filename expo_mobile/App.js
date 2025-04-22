import React from 'react';
import { enableScreens } from 'react-native-screens';
import { NotificationProvider } from './src/component/common/NotificationContext';
import { InvoiceProvider } from './src/core/invoice.connect/InvoiceContext';
import MainTabNavigator from './src/route/MainTabNavigator';
import { LogBox } from 'react-native';

export default function App() {

  enableScreens();
  // LogBox.ignoreAllLogs(); 
  LogBox.ignoreLogs([
    'VirtualizedLists should never be nested', // 👈 Dòng cảnh báo bạn muốn ẩn
  ]);

  return (
    <NotificationProvider>
        <InvoiceProvider>
            <MainTabNavigator />
        </InvoiceProvider>
    </NotificationProvider>
  );
}
