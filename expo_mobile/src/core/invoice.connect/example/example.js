import { SOCKET_TARGET } from '@env';
import React, { useState, useEffect } from 'react';
import { View, TextInput, Button, StyleSheet, Alert } from 'react-native';
import { Client } from '@stomp/stompjs';
import { Logs } from 'expo';

const Example = () => {
  // Logs.enableExpoCliLogging();
  const brokerURL = SOCKET_TARGET;
  const [name, setName] = useState('');
  const [client, setClient] = useState(null);
  
  useEffect(() => {
    const stompClient = new Client({
      brokerURL: brokerURL, 
      reconnectDelay: 5000,
      forceBinaryWSFrames: true,
      appendMissingNULLonIncoming: true,
      debug: (msg) => console.log(msg),
    });

    stompClient.onConnect = () => console.warn('🟢 Kết nối thành công!');
    stompClient.onStompError = (frame) => {console.warn('🔴 Lỗi STOMP:', frame)};

    stompClient.onWebSocketError = (e) => {
      console.warn("onWebSocketError: ", e)
    }
    stompClient.onWebSocketClose = (e) => {
      console.warn("onWebSocketError: ", e)
    }

    stompClient.activate();
    console.warn('stompClient: ', stompClient.connected);
    setClient(stompClient);

    // return () => stompClient.deactivate();
  }, []);

  const sendMessage = () => {
    console.warn(client.connected, name);
    if (client && client.connected && name.trim()) {
      

      client.publish({ destination: '/app/send', body: name });
      Alert.alert('✅ Gửi thành công!', `Nội dung: ${name}`);
      setName('');
    } else {
      Alert.alert('❌ Lỗi', 'Chưa kết nối hoặc chưa nhập nội dung');
    }
  };

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.input}
        placeholder="Nhập tên..."
        value={name}
        onChangeText={setName}
      />
      <Button title="Gửi" onPress={sendMessage} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', padding: 20 },
  input: { borderBottomWidth: 1, marginBottom: 20 },
});

export default Example;
