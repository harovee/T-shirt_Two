import { BASE_URL_SERVER } from '@env';

import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, ScrollView, Image, StyleSheet, Dimensions, ActivityIndicator, TouchableOpacity } from 'react-native';
import { MaterialIcons } from "@expo/vector-icons";

import axios from 'axios';
import { isEnabled } from 'react-native/Libraries/Performance/Systrace';

const { width } = Dimensions.get('window');
const CARD_WIDTH = (width - 32 - 8) / 2; // Padding 16*2 + spacing 8

const ProductList = ({navigation}) => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  const getProducts = async () => {
    try {
    //   console.warn(BASE_URL_SERVER +'/api/v1/admin/point-of-sale/products?page=1');
      const response = await axios.get( BASE_URL_SERVER +'/api/v1/admin/point-of-sale/products?page=1');
      setProducts(response.data.data.data);
    } catch (error) {
      console.error('Lỗi khi fetch sản phẩm:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getProducts();
  }, []);

  const handleAddToCart = (item) => {
    console.log('Thêm vào giỏ:', item);
    // gọi API thêm giỏ hoặc cập nhật Redux
  };
  
  const handleViewDetail = (item) => {
    console.log('Xem chi tiết:', item);
    navigation.navigate("ProductDetail", { productId: item.id });
  };

  const renderItem = ({ item }) => (
    <TouchableOpacity style={styles.card}>
      <Image
        source={{ uri: `https://scontent.fhan18-1.fna.fbcdn.net/v/t39.30808-6/487817253_640500218828602_435358394423283593_n.jpg?stp=cp6_dst-jpg_tt6&_nc_cat=101&ccb=1-7&_nc_sid=127cfc&_nc_ohc=aMVLUddwtLgQ7kNvwGJgUeq&_nc_oc=AdlPDGx86pJbiRzc_lwx0oDylJTToz-rOtzXoZAfUNsQ-6h0xpc4BHqOEr9v3zTmtMM&_nc_zt=23&_nc_ht=scontent.fhan18-1.fna&_nc_gid=v5DD902HDkUHMs0jRvqIhw&oh=00_AYGMvcMJORTYLjKIsToz4hJ7ljj5d0Gu8C0FY4KuskQ2Gw&oe=67F6FB5A` }} // hoặc dùng URL mặc định
        style={styles.image}
        resizeMode="cover"
      />
      <Text style={styles.name} numberOfLines={2}>{item.maSanPhamChiTiet} - {item.tenSanPham || 'Tên sản phẩm'}</Text>
      <Text style={styles.detail_content}>{item.tenMauSac} - {item.kichCo}</Text>
        {/* Giá + Icon thẳng hàng */}
  <View style={styles.rowBetween}>
    <Text style={styles.price}>
      {item.giaHienTai == null
        ? item.gia.toLocaleString('vi-VN')
        : item.giaHienTai.toLocaleString('vi-VN')} đ
    </Text>

    <View style={styles.iconGroup}>
      <TouchableOpacity onPress={() => handleAddToCart(item)}>
        <MaterialIcons name="add-shopping-cart" size={24} color="plum" />
      </TouchableOpacity>
      <TouchableOpacity onPress={() => handleViewDetail(item)} style={{ marginInline: 7 }}>
        <MaterialIcons name="info" size={24} color="plum" />
      </TouchableOpacity>
    </View>
  </View>
    </TouchableOpacity>
  );

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#000" />
      </View>
    );
  }

  return (
    <View>

    <View style={styles.componetName}>
        <MaterialIcons name="whatshot" size={24} />
        <Text style={styles.contentName}>Tất cả sản phẩm</Text>
      </View>


        <FlatList
            data={products}
            keyExtractor={(item) => item.id}
            numColumns={2}
            renderItem={renderItem}
            columnWrapperStyle={styles.row}
            contentContainerStyle={styles.container}
              />   

    </View>
   
  );
};

const styles = StyleSheet.create({
    componetName: {
        flexDirection: "row",
        alignItems: "center",
        paddingInline: 10,
      },
      contentName: {
        fontWeight: "600",
        fontSize: 18,
      },
  container: {
    padding: 6,
  },
  row: {
    justifyContent: 'space-around',
    marginBottom: 12,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    width: CARD_WIDTH,
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 6,
    elevation: 3,
  },
  image: {
    width: '100%',
    height: 200,
    backgroundColor: '#f2f2f2',
  },
  name: {
    fontSize: 13,
    fontWeight: '500',
    paddingHorizontal: 8,
    marginTop: 8,
    color: '#333',
  },
  detail_content: {
    fontSize: 12,
    fontWeight: '500',
    paddingHorizontal: 8,
    marginTop: 8,
    color: '#333',
  },
  price: {
    fontSize: 15,
    fontWeight: 'bold',
    padding: 8,
    color: '#e91e63',
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  rowBetween: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 8,
  },
  iconGroup: {
    flexDirection: 'row',
    alignItems: 'center',
  },
});

export default ProductList;
