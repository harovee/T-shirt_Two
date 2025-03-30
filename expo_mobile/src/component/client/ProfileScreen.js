// MyHome.js
import React, { useState } from 'react';
import { View, Text, Button, Image, ScrollView } from 'react-native';
import { Dialog, Portal, TextInput } from 'react-native-paper';

const MyHome = () => {
  const [user, setUser] = useState({
    name: 'Người dùng',
    savedVouchers: 5,
    orders: 12,
    cancelledOrders: 2,
    viewedItems: [
      { id: 1, code: 'DH001', name: 'Đồng hồ A' },
      { id: 2, code: 'DH002', name: 'Đồng hồ B' },
      { id: 3, code: 'DH003', name: 'Đồng hồ C' }
    ]
  });

  const [visible, setVisible] = useState(false);
  const [newName, setNewName] = useState('');

  const showModal = () => setVisible(true);
  const hideModal = () => setVisible(false);

  const handleChangeName = () => {
    setUser((prevUser) => ({ ...prevUser, name: newName }));
    hideModal();
  };

  return (
    <ScrollView style={{ padding: 20 }}>
      {/* Avatar và Tên */}
      <View style={{ flexDirection: 'row', alignItems: 'center' }}>
        <Image source={{ uri: 'https://via.placeholder.com/80' }} style={{ width: 80, height: 80, borderRadius: 40 }} />
        <Text style={{ marginLeft: 20, fontSize: 20 }}>{user.name}</Text>
        <Button title="Đổi tên" onPress={showModal} />
      </View>

      {/* Modal đổi tên */}
      <Portal>
        <Dialog visible={visible} onDismiss={hideModal}>
          <Dialog.Title>Đổi tên</Dialog.Title>
          <Dialog.Content>
            <TextInput label="Tên mới" value={newName} onChangeText={setNewName} />
          </Dialog.Content>
          <Dialog.Actions>
            <Button title="Hủy" onPress={hideModal} />
            <Button title="Lưu" onPress={handleChangeName} />
          </Dialog.Actions>
        </Dialog>
      </Portal>

      {/* Voucher */}
      <View style={{ marginTop: 30, alignItems: 'center' }}>
        <Text style={{ fontSize: 18 }}>Voucher</Text>
        <Text>{user.savedVouchers} ưu đãi đã lưu</Text>
      </View>

      {/* Số đơn hàng và Số đơn đã hủy */}
      <View style={{ flexDirection: 'row', justifyContent: 'space-between', marginTop: 30 }}>
        <Text>Số đơn hàng đã mua: {user.orders}</Text>
        <Text>Số đơn đã hủy: {user.cancelledOrders}</Text>
      </View>

      {/* Item Sản phẩm đã xem */}
      <View style={{ flexDirection: 'row', justifyContent: 'space-between', marginTop: 30, alignItems: 'center' }}>
        <Text>Item Sản phẩm đã xem</Text>
        <Button title=">" onPress={() => console.log('Xem toàn bộ sản phẩm đã xem')} />
      </View>

      {/* Các card item sản phẩm đã xem */}
      <ScrollView horizontal style={{ marginTop: 20 }}>
        {user.viewedItems.map((item) => (
          <View key={item.id} style={{ padding: 10, marginRight: 10, borderWidth: 1, borderRadius: 10 }}>
            <Text>{item.name}</Text>
            <Text>Mã: {item.code}</Text>
          </View>
        ))}
      </ScrollView>
    </ScrollView>
  );
};

export default MyHome;
