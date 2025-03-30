import React, { useState } from "react";
import {
  View,
  Text,
  Image,
  TouchableOpacity,
  FlatList,
  StyleSheet,
} from "react-native";
import { MaterialIcons } from "@expo/vector-icons";

const productData = [
  {
    id: "1",
    ma: "SP001",
    name: "Đồng hồ Thời trang",
    image:
      "https://th.bing.com/th?id=OIF.b4XLN5117Ng%2bea4mEjN73g&rs=1&pid=ImgDetMain",
    hasVoucher: true,
    promotionValue: "10%",
    isFreeShip: true,
    originalPrice: 2000000,
    minPrice: 1500000,
    maxPrice: 1800000,
  },
  {
    id: "2",
    ma: "SP002",
    name: "Đồng hồ Cao cấp",
    image: "https://via.placeholder.com/100",
    hasVoucher: false,
    promotionValue: "0",
    isFreeShip: false,
    originalPrice: 5000000,
    minPrice: 4200000,
    maxPrice: 4800000,
  },
  {
    id: "3",
    ma: "SP003",
    name: "Đồng hồ Thể thao",
    image: "https://via.placeholder.com/100",
    hasVoucher: true,
    promotionValue: "20%",
    isFreeShip: true,
    originalPrice: 3000000,
    minPrice: 2400000,
    maxPrice: 2800000,
  },
  {
    id: "4",
    ma: "SP004",
    name: "Đồng hồ Classic",
    image: "https://via.placeholder.com/100",
    hasVoucher: false,
    promotionValue: "5%",
    isFreeShip: true,
    originalPrice: 1000000,
    minPrice: 900000,
    maxPrice: 950000,
  },
  {
    id: "5",
    ma: "SP005",
    name: "Đồng hồ Thông minh",
    image: "https://via.placeholder.com/100",
    hasVoucher: true,
    promotionValue: "15%",
    isFreeShip: false,
    originalPrice: 4000000,
    minPrice: 3400000,
    maxPrice: 3800000,
  },
  {
    id: "6",
    ma: "SP006",
    name: "Đồng hồ Trẻ em",
    image: "https://via.placeholder.com/100",
    hasVoucher: true,
    promotionValue: "10%",
    isFreeShip: true,
    originalPrice: 1200000,
    minPrice: 1000000,
    maxPrice: 1100000,
  },
];

const ProductItem = ({ item, navigation }) => (
  <View style={styles.productContainer}>
    <TouchableOpacity
      onPress={() => {
        navigation.navigate("ProductDetail", { productId: item.id });
      }}
    >
      <View style={styles.imageContainer}>
        <Image source={{ uri: item.image }} style={styles.productImage} />
      </View>
      <View style={styles.contentContainer}>
        <Text>
          {item.promotionValue && item.promotionValue !== "0" && (
            <Text style={styles.productPrice}>{item.promotionValue} </Text>
          )}
          <Text>{item.minPrice.toLocaleString()} đ</Text>
        </Text>
        {item.promotionValue && item.promotionValue !== "0" && (
          <Text style={styles.producOriginaltPrice}>
            {item.originalPrice} đ
          </Text>
        )}
        <View style={styles.badgeContainer}>
          <Text style={styles.productMa}>{item.ma}</Text>
          {item.hasVoucher && (
            <Text>
              <MaterialIcons name="local-offer" color={"#4caf50"} size={13} />
            </Text>
          )}
          {item.isFreeShip && (
            <Text>
              <MaterialIcons
                name="local-shipping"
                color={"#4caf50"}
                size={13}
              />
            </Text>
          )}
        </View>
        <Text style={styles.productName}>{item.name}</Text>
      </View>
    </TouchableOpacity>
  </View>
);

const BestSellerListInHome = ({ navigation }) => {
  const [visibleItems, setVisibleItems] = useState(productData.slice(0, 5));

  const loadMore = () => {
    setVisibleItems(productData);
  };

  return (
    <View>
      <View style={styles.componetName}>
        <MaterialIcons name="whatshot" size={24} />
        <Text style={styles.contentName}>Sản phẩm đang hot</Text>
      </View>
      <View style={styles.container}>
        <FlatList
          data={visibleItems}
          renderItem={({ item }) => (
            <ProductItem item={item} navigation={navigation} />
          )}
          keyExtractor={(item) => item.id}
          horizontal
          showsHorizontalScrollIndicator={false}
        />
        {visibleItems.length < productData.length && (
          <TouchableOpacity style={styles.loadMoreButton} onPress={loadMore}>
            <MaterialIcons name="arrow-forward" size={24} color="#000" />
          </TouchableOpacity>
        )}
      </View>
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
    marginVertical: 10,
    flexDirection: "row",
    alignItems: "center",
  },
  productContainer: {
    width: 150,
    marginHorizontal: 8,
    backgroundColor: "#fff",
    borderRadius: 4,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.3,
    shadowRadius: 4,
    elevation: 4,
    alignItems: "flex-start",
  },
  imageContainer: {
    width: 150,
  },
  productImage: {
    width: "100%",
    height: 150,
    marginBottom: 3,
    borderRadius: 5,
    borderBottomLeftRadius: 0,
    borderBottomRightRadius: 0,
  },
  contentContainer: {
    paddingInline: 10,
  },

  productMa: {
    fontSize: 11,
    marginEnd: 5,
  },

  productName: {
    fontSize: 11,
    marginBottom: 3,
  },
  productPrice: {
    fontSize: 13,
    color: "#ff5722",
    fontWeight: "bold",
  },
  producOriginaltPrice: {
    opacity: 0.7,
    color: "#cccccc",
    textDecorationLine: "line-through",
    fontSize: 13,
  },
  badgeContainer: {
    flexDirection: "row",
    marginTop: 5,
  },
  badge: {
    backgroundColor: "#4caf50",
    color: "#fff",
    fontSize: 9,
    paddingVertical: 2,
    paddingHorizontal: 6,
    margin: 2,
    borderRadius: 4,
  },
  loadMoreButton: {
    marginLeft: 5,
    backgroundColor: "#f4f4f4",
    padding: 10,
    borderRadius: 20,
    justifyContent: "center",
    alignItems: "center",
  },
});

export default BestSellerListInHome;
