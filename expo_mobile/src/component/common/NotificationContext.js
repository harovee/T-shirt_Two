// NotificationContext.js
import React, { createContext, useContext, useState } from 'react';
import { Portal, Dialog, Button, Provider, Text } from 'react-native-paper';

const NotificationContext = createContext();

export const useNotification = () => useContext(NotificationContext);

export const NotificationProvider = ({ children }) => {
  const [visible, setVisible] = useState(false);
  const [message, setMessage] = useState('');

  const showNotification = (msg) => {
    setMessage(msg);
    setVisible(true);
  };

  const hideNotification = () => setVisible(false);

  return (
    <NotificationContext.Provider value={{ showNotification }}>
      <Provider>
        {children}
        <Portal>
          <Dialog visible={visible} onDismiss={hideNotification}>
            <Dialog.Title>Thông báo</Dialog.Title>
            <Dialog.Content>
              <Text>{message}</Text>
            </Dialog.Content>
            <Dialog.Actions>
              <Button onPress={hideNotification}>Đóng</Button>
            </Dialog.Actions>
          </Dialog>
        </Portal>
      </Provider>
    </NotificationContext.Provider>
  );
};
