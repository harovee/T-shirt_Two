import React, { useState, useContext } from 'react';
import {
  View,
  Text,
  TextInput,
  Button,
  StyleSheet,
  FlatList,
  Image,
  TouchableOpacity,
} from 'react-native';
import { InvoiceContext } from '../invoice.connect/InvoiceContext';
import { MaterialIcons } from '@expo/vector-icons';

const ConnectScreen = () => {
  const [inputId, setInputId] = useState('');
  const {
    connectToInvoice,
    disconnect,
    isConnected,
    cartItems,
    setCartItems,
    invoiceId,
    rerender,
    sendMessWhenDisconnect
  } = useContext(InvoiceContext);

  const handleConnect = () => {
    if (inputId.trim()) {
      connectToInvoice(inputId.trim());
    }
  };

  const handleDisconnect = () => {
    sendMessWhenDisconnect();

  };

  const updateQuantity = (id, delta) => {
    setCartItems((prev) =>
      prev.map((item) =>
        item.id === id
          ? {
              ...item,
              soLuong: Math.max(1, parseInt(item.soLuong) + delta),
            }
          : item
      )
    );
  };

  const removeItem = (id) => {
    setCartItems((prev) => prev.filter((item) => item.id !== id));
    
  };

  const total = cartItems.reduce(
    (sum, item) => sum + item.giaHienTai * parseInt(item.soLuong),
    0
  );

  const renderItem = ({ item }) => (
    <View style={styles.card}>
      <Image
        source={{ uri: item.linkAnh }}
        style={styles.image}
        resizeMode="cover"
      />
      <View style={{ flex: 1, marginLeft: 10 }}>
        <Text style={styles.name} numberOfLines={2}>
          [{item.maSanPhamChiTiet}] {item.tenSanPham}
        </Text>
        <Text style={styles.detail}>
          {item.tenMauSac} - {item.kichCo}
        </Text>
        <View style={styles.row}>
          <Text style={styles.price}>
            {item.giaHienTai.toLocaleString('vi-VN')} đ
          </Text>
          <View style={styles.qtyControl}>
            <TouchableOpacity onPress={() => updateQuantity(item.id, -1)}>
              <MaterialIcons name="remove-circle-outline" size={24} color="black" />
            </TouchableOpacity>
            <Text style={styles.qty}>{item.soLuong}</Text>
            <TouchableOpacity onPress={() => updateQuantity(item.id, 1)}>
              <MaterialIcons name="add-circle-outline" size={24} color="black" />
            </TouchableOpacity>
          </View>
        </View>
        <TouchableOpacity onPress={() => removeItem(item.id)} style={{ marginTop: 5 }}>
          <Text style={{ color: 'red' }}>Xóa</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      {!isConnected ? (
        <>
            <Text style={styles.title}>Kết nối hóa đơn</Text>
          <TextInput
            placeholder="Nhập mã hóa đơn"
            value={inputId}
            onChangeText={setInputId}
            style={styles.input}
          />
          <Button title="Kết nối" onPress={handleConnect} />
        </>
      ) : (
        <>
          <Text style={styles.connected}>Đã kết nối: {invoiceId}</Text>

          <FlatList
            data={cartItems}
            keyExtractor={(item) => item.id}
            renderItem={renderItem}
            ListFooterComponent={
              <Text style={styles.total}>
                Tổng cộng: {total.toLocaleString('vi-VN')} đ
              </Text>
            }
          />

          <View style={{ marginTop: 20 }}>
            <Button title="Ngắt kết nối" color="#cc0000" onPress={handleDisconnect} />
          </View>
        </>
      )}
    </View>
  );
};

export default ConnectScreen;

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: 'white' },
  title: { fontSize: 20, fontWeight: 'bold', marginBottom: 10 },
  input: {
    borderWidth: 1,
    borderColor: '#999',
    padding: 10,
    borderRadius: 6,
    marginBottom: 15,
  },
  connected: { fontSize: 16, marginBottom: 10, color: 'green' },
  card: {
    flexDirection: 'row',
    borderWidth: 1,
    borderColor: '#ddd',
    padding: 10,
    marginBottom: 10,
    borderRadius: 6,
  },
  image: { width: 80, height: 80, borderRadius: 6 },
  name: { fontWeight: 'bold', fontSize: 16 },
  detail: { color: '#666' },
  price: { fontWeight: 'bold', fontSize: 16 },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 6,
  },
  qtyControl: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    marginRight: 10,
  },
  qty: {
    fontSize: 16,
    paddingHorizontal: 8,
  },
  total: {
    textAlign: 'right',
    fontSize: 16,
    fontWeight: 'bold',
    marginVertical: 15,
  },
});
