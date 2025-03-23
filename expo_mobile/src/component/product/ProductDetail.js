// ProductDetail.js
import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  Image,
  ScrollView,
  Button,
  Pressable,
  Dimensions,
  StyleSheet,
  TouchableOpacity,
} from "react-native";
import { useRoute } from "@react-navigation/native";
import { MaterialIcons } from "@expo/vector-icons";

const ProductDetail = ({ navigation }) => {
  const route = useRoute();
  const { productId } = route.params;

  console.log(productId);

  const [product, setProduct] = useState(null);
  const { width, height } = Dimensions.get("window");

  useEffect(() => {
    // Giả lập dữ liệu sản phẩm từ API
    const fetchProduct = async () => {
      const mockProduct = {
        id: productId,
        name: "Đồng hồ cơ cao cấp",
        shortDescription: "Thiết kế tinh tế, máy cơ tự động.",
        price: 2500000,
        originalPrice: 3000000,
        vouchers: [
          { id: 1, discount: "30k", condition: "Đơn từ 100k" },
          { id: 2, discount: "50k", condition: "Đơn từ 500k" },
          { id: 3, discount: "50k", condition: "Đơn từ 500k" },
          { id: 4, discount: "50k", condition: "Đơn từ 500k" },
          { id: 5, discount: "50k", condition: "Đơn từ 500k" },
          { id: 6, discount: "50k", condition: "Đơn từ 500k" },
        ],
        shipping: {
          type: "Tiết kiệm | Đảm bảo",
          fee: "25,000đ",
          deliveryDate: "20-10-2024",
          //   preparationTime: '2 ngày',
        },
        description:
          "Chi tiết về đồng hồ với máy cơ tự động, chống nước 5ATM, bảo hành 12 tháng.",
        images: [
          "https://th.bing.com/th?id=OIF.b4XLN5117Ng%2bea4mEjN73g&rs=1&pid=ImgDetMain",
          "https://th.bing.com/th?id=OIF.b4XLN5117Ng%2bea4mEjN73g&rs=1&pid=ImgDetMain",
          "https://th.bing.com/th?id=OIF.b4XLN5117Ng%2bea4mEjN73g&rs=1&pid=ImgDetMain",
        ],
      };
      setProduct(mockProduct);
    };

    fetchProduct();
  }, [productId]);

  if (!product) return <Text>Đang tải...</Text>;

  return (
    <View style={styles.tcontainer}>
      <ScrollView style={{ padding: 20, backgroundColor: "white" }}>
        {/* Header */}
        <View style={{ flexDirection: "row", justifyContent: "flex-end" }}>
          <Pressable onPress={() => navigation.navigate("Cart")}>
            <MaterialIcons name="shopping-cart" size={24} color={"plum"} />
          </Pressable>
        </View>

        {/* Slide Ảnh Lớn */}
        <ScrollView horizontal pagingEnabled width={"100%"}>
          {product.images.map((img, index) => (
            <Image
              key={index}
              source={{ uri: img }}
              style={{ width: width, height: 330, resizeMode: "contain" }}
            />
          ))}
        </ScrollView>

        {/* Tên & Giá */}
        <Text style={{ fontSize: 22, fontWeight: "bold" }}>{product.name}</Text>
        <Text>{product.shortDescription}</Text>
        <Text>
          {product.price.toLocaleString("vi-VN")}đ{" "}
          {product.price !== product.originalPrice && (
            <Text
              style={{ textDecorationLine: "line-through", marginLeft: 10 }}
            >
              {product.originalPrice.toLocaleString("vi-VN")}đ
            </Text>
          )}
        </Text>

        {/* Vouchers */}
        <ScrollView horizontal style={{ marginVertical: 20 }}>
          {product.vouchers.map((voucher) => (
            <View key={voucher.id} style={{ marginRight: 15 }}>
              <Text>Giảm {voucher.discount}</Text>
              <Text>{voucher.condition}</Text>
              <Button
                title="Lưu"
                onPress={() => console.log("Lưu voucher", voucher.id)}
              />
            </View>
          ))}
        </ScrollView>

        {/* Thông tin giao hàng */}
        <View>
          <Text>
            Giao hàng: {product.shipping.type} - {product.shipping.fee}
          </Text>
          <Text>Dự kiến nhận hàng: {product.shipping.deliveryDate}</Text>
          {product.shipping.preparationTime && (
            <Text>Thời gian chuẩn bị: {product.shipping.preparationTime}</Text>
          )}
        </View>

        {/* Mô tả chi tiết sản phẩm */}
        <View style={{ marginTop: 20 }}>
          <Text>{product.description}</Text>
        </View>

        {/* Ảnh chi tiết sản phẩm */}
        <View style={{ marginTop: 20 }}>
          {product.images.map((img, index) => (
            <Image
              key={index}
              source={{ uri: img }}
              style={{ width: "100%", height: 300, marginBottom: 10 }}
            />
          ))}
        </View>
      </ScrollView>

      <View style={styles.container}>
        <TouchableOpacity
          style={[styles.button, styles.addToCart]}
          onPress={() => alert("Thêm vào giỏ!")}
        >
          <Text style={styles.buttonText}>+ Thêm vô giỏ</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.button, styles.buyNow]}
          onPress={() => alert("Mua ngay!")}
        >
          <Text style={styles.buttonText}>Mua ngay</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  tcontainer: {
    flex: 1,
    backgroundColor: "#fff",
  },
  container: {
    position: "",
    bottom: 0, 
    width: "100%", 
    flexDirection: "row", 
    borderTopWidth: 1,
    borderTopColor: "#ddd",
    backgroundColor: "#fff",
    zIndex: 999, 
    elevation: 10, 
    padding: 10,
  },
  button: {
    paddingVertical: 10,
    alignItems: "center",
    justifyContent: "center",
  },
  addToCart: {
    flex: 2,
    backgroundColor: "#f5f5f5", 
  },
  buyNow: {
    flex: 3,
    backgroundColor: "thistle",
    marginLeft: 10
  },
  buttonText: {
    color: "#000",
    fontWeight: "600",
    fontSize: 16,
  },
});

export default ProductDetail;
