// ConfirmModal.js
import React, { useState } from 'react';
import { Portal, Dialog, Button, Text } from 'react-native-paper';
import { useNotification } from './NotificationContext';

export const ConfirmModal = () => {
  const [visible, setVisible] = useState(false);
  const [message, setMessage] = useState('');
  const [onConfirm, setOnConfirm] = useState(() => () => {});

  const { showConfirm } = useNotification();

  const show = (msg, callback) => {
    setMessage(msg);
    setOnConfirm(() => callback);
    setVisible(true);
  };

  showConfirm(show);

  const handleConfirm = () => {
    onConfirm();
    setVisible(false);
  };

  return (
    <Portal>
      <Dialog visible={visible} onDismiss={() => setVisible(false)}>
        <Dialog.Title>Xác nhận</Dialog.Title>
        <Dialog.Content>
          <Text>{message}</Text>
        </Dialog.Content>
        <Dialog.Actions>
          <Button onPress={() => setVisible(false)}>Hủy</Button>
          <Button onPress={handleConfirm}>Đồng ý</Button>
        </Dialog.Actions>
      </Dialog>
    </Portal>
  );
};
