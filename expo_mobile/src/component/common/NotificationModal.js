// NotificationModal.js
import React, { useState } from 'react';
import { Portal, Dialog, Button, Text } from 'react-native-paper';
import { useNotification } from './NotificationContext';

export const NotificationModal = () => {
  const [visible, setVisible] = useState(false);
  const [message, setMessage] = useState('');

  const { showNotification } = useNotification();

  const show = (msg) => {
    setMessage(msg);
    setVisible(true);
  };

  showNotification(show);

  return (
    <Portal>
      <Dialog visible={visible} onDismiss={() => setVisible(false)}>
        <Dialog.Title>Thông báo</Dialog.Title>
        <Dialog.Content>
          <Text>{message}</Text>
        </Dialog.Content>
        <Dialog.Actions>
          <Button onPress={() => setVisible(false)}>Đóng</Button>
        </Dialog.Actions>
      </Dialog>
    </Portal>
  );
};
