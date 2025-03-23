import React from 'react';
import { enableScreens } from 'react-native-screens';
import { NotificationProvider } from './src/component/common/NotificationContext';
import MainTabNavigator from './src/route/MainTabNavigator';

export default function App() {

  enableScreens();

  return (
    <NotificationProvider>
         <MainTabNavigator />
    </NotificationProvider>
  );
}
