import React, { useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Modal,
  Button,
  Pressable,
  Image
} from "react-native";
import { MaterialIcons } from "@expo/vector-icons";
import { Picker } from "@react-native-picker/picker";

// Dữ liệu giỏ hàng với size riêng cho từng sản phẩm
const initialCart = [
  {
    id: "101",
    img: 'https://th.bing.com/th?id=OIF.b4XLN5117Ng%2bea4mEjN73g&rs=1&pid=ImgDetMain',
    name: "Đồng hồ Rolex Submariner",
    note: "Phiên bản giới hạn",
    sizes: [
      {label: "40mm", value: 's1'},
      {label: "45mm", value: 's3'},
      {label: "50mm", value: 's2'},
    ],
    size: "s2",
    quantity: 1,
    price: 500000,
    selected: true,
  },
  {
    id: "102",
    img: 'https://th.bing.com/th?id=OIF.b4XLN5117Ng%2bea4mEjN73g&rs=1&pid=ImgDetMain',
    name: "Đồng hồ Omega Seamaster",
    note: "Chống nước 300m",
    sizes: [
      {label: "40mm", value: 's1'},
      {label: "45mm", value: 's3'},
      {label: "50mm", value: 's2'},
    ],
    size: "s2",
    quantity: 2,
    price: 500000,
    selected: true,
  },
  {
    id: "103",
    img: 'https://th.bing.com/th?id=OIF.b4XLN5117Ng%2bea4mEjN73g&rs=1&pid=ImgDetMain',
    name: "Đồng hồ Casio G-Shock",
    note: "Chống sốc cao cấp",
    sizes:[
      {label: "M", value: 's1'},
      {label: "L", value: 's3'},
      {label: "XL", value: 's2'},
    ],
    size: "s3",
    quantity: 1,
    price: 500000,
    selected: false,
  },
  {
    id: "104",
    img: 'https://th.bing.com/th?id=OIF.b4XLN5117Ng%2bea4mEjN73g&rs=1&pid=ImgDetMain',
    name: "Đồng hồ Tissot PRX",
    note: "Dây thép không gỉ",
    sizes: [
      {label: "40mm", value: 's1'},
      {label: "45mm", value: 's3'},
      {label: "50mm", value: 's2'},
    ],
    size: "s1",
    quantity: 1,
    price: 500000,
    selected: true,
  },
];

const Cart = () => {
  const [cart, setCart] = useState(initialCart);
  const [isSelectAll, setIsSelectAll] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);

  // Cập nhật số lượng
  const updateQuantity = (id, amount) => {
    setCart((prev) =>
      prev.map((item) =>
        item.id === id ? { ...item, quantity: Math.max(1, item.quantity + amount) } : item
      )
    );
  };

  // Chọn/bỏ chọn từng sản phẩm
  const toggleSelect = (id) => {
    setCart((prev) =>
      prev.map((item) => (item.id === id ? { ...item, selected: !item.selected } : item))
    );
  };

  // Chọn/bỏ chọn tất cả
  const toggleSelectAll = () => {
    setIsSelectAll(!isSelectAll);
    setCart((prev) => prev.map((item) => ({ ...item, selected: !isSelectAll })));
  };

  // Xóa mục đã chọn
  const deleteSelectedItems = () => {
    setCart((prev) => prev.filter((item) => !item.selected));
  };

  // Tính tổng tiền (giả định mỗi sản phẩm 1 triệu)
  const calculateTotal = () => {
    return cart
      .filter((item) => item.selected)
      .reduce((total, item) => total + item.quantity * item.price, 0);
  };

  // Hiển thị mỗi item sản phẩm
  const renderItem = ({ item }) => (
    <View style={styles.itemContainer}>
      <TouchableOpacity onPress={() => toggleSelect(item.id)}>
        <MaterialIcons
          name={item.selected ? "radio-button-checked" : "radio-button-unchecked"}
          size={20}
          color="black"
        />
      </TouchableOpacity>

        <TouchableOpacity style={{marginLeft: 7}} onPress={() => {  }}>
           <Image source={{ uri: item.img }} style={styles.image} />
        </TouchableOpacity>

      <View style={styles.itemContent}>
        <View style={styles.itemInfo}>
          <View>
            <Text style={styles.productName}>{item.name}</Text>
            <Text>{item.note}</Text>
          </View>
          <View> <Text style={{fontSize: 17, color: 'red'}}>{item.price}</Text> </View>
        </View>
       
       

        <View style={styles.valueContainer}>
                {/* Picker size theo sản phẩm */}
                    <Picker
                      selectedValue={item.size.value}
                      onValueChange={(value) =>
                        setCart((prev) =>
                          prev.map((el) => (el.id === item.id ? { ...el, size: value } : el))
                        )
                      }
                      style={styles.picker}
                    > 
                      {item.sizes.map((size) => (
                        <Picker.Item key={size.value} label={size.label} value={size} />
                      ))}
                    </Picker>   
                    
                {/* Bộ đếm số lượng */}
                <View style={styles.quantityContainer}>
                    <TouchableOpacity
                      style={styles.circleButton}
                      onPress={() => updateQuantity(item.id, -1)}
                    >
                      <Text>-</Text>
                    </TouchableOpacity>
                        <Text>　{item.quantity}　</Text>
                    <TouchableOpacity
                      style={styles.circleButton}
                      onPress={() => updateQuantity(item.id, 1)}
                    >
                      <Text>+</Text>
                    </TouchableOpacity>
                </View>
        </View>

        
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity onPress={toggleSelectAll}>
          <Text>{isSelectAll ? "Bỏ chọn tất cả" : "Chọn tất cả"}</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={deleteSelectedItems}>
          <Text>Xóa mục đã chọn</Text>
        </TouchableOpacity>
      </View>

      {/* Danh sách sản phẩm */}
      <FlatList
        data={cart}
        keyExtractor={(item) => item.id}
        renderItem={renderItem}
      />

      {/* Tóm tắt đơn hàng */}
      <TouchableOpacity
        style={styles.summaryButton}
        onPress={() => setModalVisible(true)}
      >
        <Text>Tóm tắt đơn hàng: {calculateTotal().toLocaleString()} đ</Text>
      </TouchableOpacity>

      {/* Modal hiển thị chi tiết thanh toán */}
      <Modal visible={modalVisible} animationType="slide" transparent={true}>
        <View style={styles.modalView}>
          <Text>Tổng tiền hàng: {calculateTotal().toLocaleString()} đ</Text>
          <Text>Phí vận chuyển: 30.000 đ</Text>
          <Text>Mã giảm giá: 0 đ</Text>
          <Button title="Đóng" onPress={() => setModalVisible(false)} />
        </View>
      </Modal>

      {/* Nút thanh toán */}
      <TouchableOpacity style={styles.payButton}>
        <Text style={styles.payButtonText}>THANH TOÁN</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#fff" },
  header: { flexDirection: "row", justifyContent: "space-between", padding: 16 },
  itemContainer: { flexDirection: "row", padding: 12, alignItems: "center" },
  valueContainer: { flexDirection: "row", justifyContent: "space-between", },
  itemContent: { marginLeft: 7, flex: 1 },
  itemInfo: { flexDirection: "row", justifyContent: "space-between", alignItems: "center"},
  image: { width: 80, height: 80 },
  productName: { fontWeight: "bold", fontSize: 14 },
  picker: { height: 50, width: 130},
  quantityContainer: { flexDirection: "row", alignItems: "center", marginTop: 8 },
  circleButton: {
    width: 23,                  
    height: 23,                 
    borderRadius: 18,           
    borderWidth: 1,             
    borderColor: "#D3D3D3",     
    alignItems: "center",       
    justifyContent: "center",   
    marginHorizontal: 4,        
  },
  
  summaryButton: {
    padding: 16,
    backgroundColor: "#f0f0f0",
    alignItems: "center",
  },
  modalView: {
    marginTop: "50%",
    backgroundColor: "white",
    padding: 20,
    borderRadius: 10,
    alignItems: "center",
  },
  payButton: {
    backgroundColor: "#4CAF50",
    padding: 16,
    alignItems: "center",
    width: "100%",
  },
  payButtonText: { color: "white", fontWeight: "bold", fontSize: 18 },
});

export default Cart;
