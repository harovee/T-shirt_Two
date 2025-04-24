import React, { createContext, useContext, useState } from 'react';
import { Portal, Dialog, Button, Provider, Text } from 'react-native-paper';

const NotificationContext = createContext();

export const useNotification = () => useContext(NotificationContext);

export const NotificationProvider = ({ children }) => {
  const [visible, setVisible] = useState(false);
  const [message, setMessage] = useState('');
  const [confirmMode, setConfirmMode] = useState(false);
  const [onConfirm, setOnConfirm] = useState(() => () => {});
  const [onCancel, setOnCancel] = useState(() => () => {});

  // Thông báo thông thường
  const showNotification = (msg) => {
    setMessage(msg);
    setConfirmMode(false);
    setVisible(true);
  };

  // Hiển thị modal xác nhận
  const showConfirm = (msg, onOk, onCancelFn) => {
    setMessage(msg);
    setConfirmMode(true);
    setOnConfirm(() => onOk);
    setOnCancel(() => onCancelFn || (() => {}));
    setVisible(true);
  };

  const hideDialog = () => {
    setVisible(false);
  };

  const handleConfirm = () => {
    onConfirm();
    hideDialog();
  };

  const handleCancel = () => {
    onCancel();
    hideDialog();
  };

  return (
    <NotificationContext.Provider value={{ showNotification, showConfirm }}>
      <Provider>
        {children}
        <Portal>
          <Dialog visible={visible} onDismiss={hideDialog}>
            <Dialog.Title>{confirmMode ? 'Xác nhận' : 'Thông báo'}</Dialog.Title>
            <Dialog.Content>
              <Text>{message}</Text>
            </Dialog.Content>
            <Dialog.Actions>
              {confirmMode ? (
                <>
                  <Button onPress={handleCancel}>Hủy</Button>
                  <Button onPress={handleConfirm}>Đồng ý</Button>
                </>
              ) : (
                <Button onPress={hideDialog}>Đóng</Button>
              )}
            </Dialog.Actions>
          </Dialog>
        </Portal>
      </Provider>
    </NotificationContext.Provider>
  );
};
