// PromptModal.js
import React, { useState } from 'react';
import { Portal, Dialog, Button, Text, TextInput } from 'react-native-paper';
import { useNotification } from './NotificationContext';

export const PromptModal = () => {
  const [visible, setVisible] = useState(false);
  const [message, setMessage] = useState('');
  const [inputValue, setInputValue] = useState('');
  const [onSubmit, setOnSubmit] = useState(() => () => {});

  const { showPrompt } = useNotification();

  const show = (msg, callback) => {
    setMessage(msg);
    setOnSubmit(() => callback);
    setInputValue('');
    setVisible(true);
  };

  showPrompt(show);

  const handleSubmit = () => {
    onSubmit(inputValue);
    setVisible(false);
  };

  return (
    <Portal>
      <Dialog visible={visible} onDismiss={() => setVisible(false)}>
        <Dialog.Title>Nhập thông tin</Dialog.Title>
        <Dialog.Content>
          <Text>{message}</Text>
          <TextInput
            value={inputValue}
            onChangeText={setInputValue}
            style={{ marginTop: 10 }}
          />
        </Dialog.Content>
        <Dialog.Actions>
          <Button onPress={() => setVisible(false)}>Hủy</Button>
          <Button onPress={handleSubmit}>Xác nhận</Button>
        </Dialog.Actions>
      </Dialog>
    </Portal>
  );
};
