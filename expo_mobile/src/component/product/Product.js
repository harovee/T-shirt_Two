import React from 'react';
import { View, Text, Image, FlatList, StyleSheet } from 'react-native';

const productData = [
  {
    id: '1',
    name: 'Đồng hồ Thời trang',
    image: 'https://th.bing.com/th?id=OIF.b4XLN5117Ng%2bea4mEjN73g&rs=1&pid=ImgDetMain',
    hasVoucher: true,
    promotionValue: '10%',
    isFreeShip: true,
    originalPrice: 2000000,
    minPrice: 1500000,
    maxPrice: 1800000,
  },
  {
    id: '2',
    name: 'Đồng hồ Cao cấp',
    image: 'https://via.placeholder.com/100',
    hasVoucher: false,
    promotionValue: '0%',
    isFreeShip: false,
    originalPrice: 5000000,
    minPrice: 4200000,
    maxPrice: 4800000,
  },
  {
    id: '3',
    name: 'Đồng hồ Thể thao',
    image: 'https://via.placeholder.com/100',
    hasVoucher: true,
    promotionValue: '20%',
    isFreeShip: true,
    originalPrice: 3000000,
    minPrice: 2400000,
    maxPrice: 2800000,
  },
  {
    id: '4',
    name: 'Đồng hồ Classic',
    image: 'https://via.placeholder.com/100',
    hasVoucher: false,
    promotionValue: '5%',
    isFreeShip: true,
    originalPrice: 1000000,
    minPrice: 900000,
    maxPrice: 950000,
  },
  {
    id: '5',
    name: 'Đồng hồ Thông minh',
    image: 'https://via.placeholder.com/100',
    hasVoucher: true,
    promotionValue: '15%',
    isFreeShip: false,
    originalPrice: 4000000,
    minPrice: 3400000,
    maxPrice: 3800000,
  },
];

const ProductItem = ({ item }) => (
  <View style={styles.productContainer}>
    <Image source={{ uri: item.image }} style={styles.productImage} />
    <Text style={styles.productName}>{item.name}</Text>
    <Text style={styles.productPrice}>
      {item.minPrice.toLocaleString()} đ - {item.maxPrice.toLocaleString()} đ
    </Text>
    <View style={styles.badgeContainer}>
      {item.hasVoucher && <Text style={styles.badge}>Voucher {item.promotionValue}</Text>}
      {item.isFreeShip && <Text style={styles.badge}>Free Ship</Text>}
    </View>
  </View>
);

const Product = () => {
  return (
    <FlatList
      data={productData}
      renderItem={({ item }) => <ProductItem item={item} />}
      keyExtractor={(item) => item.id}
      numColumns={2} // Hiển thị tối đa 2 cột
      columnWrapperStyle={styles.row}
      contentContainerStyle={styles.container}
    />
  );
};

const styles = StyleSheet.create({
  container: {
    padding: 10,
    backgroundColor: '#f5f5f5',
  },
  row: {
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  productContainer: {
    width: '48%',
    backgroundColor: '#fff',
    borderRadius: 10,
    padding: 10,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    elevation: 3,
  },
  productImage: {
    width: 80,
    height: 80,
    marginBottom: 8,
    borderRadius: 8,
  },
  productName: {
    fontSize: 14,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: 4,
  },
  productPrice: {
    fontSize: 13,
    color: '#ff5722',
    marginBottom: 6,
    fontWeight: 'bold',
  },
  badgeContainer: {
    flexDirection: 'row',
    marginTop: 4,
  },
  badge: {
    backgroundColor: '#4caf50',
    color: '#fff',
    fontSize: 10,
    paddingVertical: 2,
    paddingHorizontal: 5,
    marginHorizontal: 2,
    borderRadius: 4,
  },
});

export default Product;
