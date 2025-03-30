import React from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, Image } from 'react-native';
import { MaterialIcons, FontAwesome5 } from '@expo/vector-icons';

// Fake data
const vouchers = [
  {
    id: '1',
    name: 'Free Ship Toàn Quốc',
    description: 'Miễn phí vận chuyển cho đơn từ 200k',
    note: 'Áp dụng toàn quốc',
    startTime: '10.03.2023',
    endTime: '12.03.2023',
    type: 'freeship',
    isSaved: false, // Phân biệt loại
  },
  {
    id: '2',
    name: 'Giảm 50k Cho Đơn Từ 500k',
    description: 'Giảm ngay 50.000đ cho đơn hàng từ 500k',
    note: 'Áp dụng 1 lần/ngày',
    startTime: '10.03.2023',
    endTime: '11.03.2023',
    type: 'discount',
    isSaved: false,
  },
  {
    id: '3',
    name: 'Giảm 10% Đơn Hàng',
    description: 'Giảm 10% tối đa 100k',
    note: 'Áp dụng toàn quốc',
    startTime: '09.03.2023',
    endTime: '10.03.2023',
    type: 'discount',
    isSaved: false,
  },
  {
    id: '4',
    name: 'Giảm 10% Đơn Hàng',
    description: 'Giảm 10% tối đa 100k',
    note: 'Áp dụng toàn quốc',
    startTime: '09.03.2023',
    endTime: '10.03.2023',
    type: 'discount',
    isSaved: true,
  },
];

// Component hiển thị từng voucher
const VoucherItem = ({ item }) => {
  const isFreeShip = item.type === 'freeship';

  return (
    <View style={styles.card}>
      <View style={styles.iconContainer}>
        {isFreeShip ? (
          <FontAwesome5 name="shipping-fast" size={32} color="#4CAF50" />
        ) : (
          <MaterialIcons name="local-offer" size={32} color="#FF6347" />
        )}
      </View>
      <View style={styles.infoContainer}>
        <Text style={styles.voucherName}>{item.name}</Text>
        <Text style={styles.description}>{item.description}</Text>
        <Text style={styles.note}>{item.note}</Text>
        <Text style={styles.time}>
          ⏳ {item.startTime} - {item.endTime}
        </Text>
      </View>
      <TouchableOpacity style={styles.saveButton} onPress={() => onSaveToggle(item.id)}>
        {item.isSaved ? (
          <>
            <MaterialIcons name='check-circle' size={28} color="#888" />
            <Text style={styles.savedText}>Đã lưu</Text>
          </>
        ) : (
          <>
            <MaterialIcons name="download" size={28} color="#1E90FF" />
            <Text style={styles.availableText}>Khả dụng</Text>
          </>
        )}
      </TouchableOpacity>
    </View>
  );
};

// Màn hình danh sách voucher
const VoucherList = () => {
  return (
    <View style={styles.container}>
      {/* <Text style={styles.header}>🎟️ Danh Sách Voucher</Text> */}
      <FlatList
        data={vouchers}
        renderItem={({ item }) => <VoucherItem item={item} />}
        keyExtractor={(item) => item.id}
      />
    </View>
  );
};

export default VoucherList;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f9f9f9',
    padding: 10,
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 10,
    textAlign: 'center',
    color: '#333',
  },
  card: {
    flexDirection: 'row',
    backgroundColor: '#fff',
    borderRadius: 7,
    paddingVertical: 7,
    marginBottom: 8,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowOffset: { width: 0, height: 2 },
    elevation: 3,
  },
  iconContainer: {
    width: 60, flexDirection: 'row', justifyContent: 'center'
  },
  infoContainer: {
    flex: 1,
  },
  voucherName: {
    fontSize: 15,
    fontWeight: 'bold',
    color: '#333',
  },
  description: {
    fontSize: 11,
    color: '#555',
    marginVertical: 4,
  },
  note: {
    fontSize: 11,
    color: '#888',
    marginBottom: 4,
  },
  time: {
    fontSize: 12,
    color: '#666',
  },
  saveButton: {
    flexDirection: 'column',
    alignItems: 'center',
    paddingVertical: 8,
    paddingHorizontal: 16,
    borderRadius: 20,
    width: 100,
  },
  saveButtonText: {
    color: '#fff',
    fontWeight: 'bold',
  },
});
