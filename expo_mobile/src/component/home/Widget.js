import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, FlatList } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { useNotification } from '../common/NotificationContext';

const widgetData = [
  { id: '1', name: 'Giá tốt', icon: 'local-offer' },
  { id: '2', name: 'Free ship', icon: 'local-shipping' },
  { id: '3', name: 'Ưu đãi', icon: 'star' },
  { id: '4', name: 'SP mới', icon: 'new-releases' },
  { id: '5', name: 'Bán chạy', icon: 'whatshot' },
  { id: '6', name: 'Hot deal', icon: 'local-fire-department' },
  { id: '7', name: 'Khuyến mãi', icon: 'card-giftcard' },
  { id: '8', name: 'Giảm giá', icon: 'trending-down' },
  { id: '9', name: 'Combo', icon: 'widgets' },
  { id: '10', name: 'Độc quyền', icon: 'verified' },
];

const Widget = () => {
    const { showNotification } = useNotification();

  const renderItem = ({ item }) => (
    
    <TouchableOpacity style={styles.widgetButton}
        onPress={() => showNotification(`Chuyển màn hình: ${item.name}`)}
        >
      <MaterialIcons name={item.icon} size={24} color="thistle" />
      <Text style={styles.widgetText}>{item.name}</Text>
    </TouchableOpacity>
  );

  return (
    <FlatList
    data={widgetData}
    renderItem={renderItem}
    keyExtractor={(item) => item.id}
    numColumns={5} // Mỗi hàng 5 nút
    contentContainerStyle={styles.container}
    scrollEnabled={false} // Không cuộn được
  />
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: 'white',
    alignItems: 'center',
    // paddingVertical: 10,
  },
  widgetButton: {
    width: 70,
    height: 70,
    justifyContent: 'center',
    alignItems: 'center',
    // margin: 8,
    // borderRadius: 50,
    // shadowColor: '#000',
    // shadowOffset: { width: 0, height: 2 },
    // shadowOpacity: 0.3,
    // shadowRadius: 3,
    // elevation: 3,
  },
  widgetText: {
    marginTop: 5,
    fontSize: 12,
    // fontWeight: 'bold',
    color: '#333',
    textAlign: 'center',
  },
});

export default Widget;
