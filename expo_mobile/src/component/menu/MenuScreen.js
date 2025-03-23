import * as React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

// Dữ liệu menu
const menuItems = [
  {
    title: 'Hệ thống',
    data: [
      { name: 'Trang cá nhân', icon: 'person', screen: 'example_screen' },
      { name: 'Cài đặt tài khoản', icon: 'settings', screen: 'example_screen' },
      { name: 'Thông báo', icon: 'notifications', screen: 'example_screen' },
    ],
  },
  {
    title: 'Mua sắm',
    data: [
      { name: 'Phương thức thanh toán', icon: 'payment', screen: 'example_screen' },
      { name: 'Địa chỉ đã lưu', icon: 'location-on', screen: 'example_screen' },
      { name: 'Ưu đãi', icon: 'local-offer', screen: 'VoucherList' },
    ],
  },
  {
    title: 'Hỗ trợ',
    data: [
      { name: 'Đóng góp ý kiến', icon: 'feedback', screen: 'example_screen' },
      { name: 'Điều khoản hệ thống', icon: 'description', screen: 'example_screen' },
      { name: 'Chính sách bảo mật', icon: 'lock', screen: 'example_screen' },
    ],
  },
];

const Menu = ({ navigation }) => {
  const handleNavigate = (screen) => {
    navigation.navigate(screen);
  };

  return (
    <View style={styles.container}>
      {menuItems.map((group, groupIndex) => (
        <View key={groupIndex}>
          {/* Tiêu đề nhóm */}
          <Text style={styles.groupTitle}>{group.title}</Text>

          {/* Các mục trong nhóm */}
          {group.data.map((item, index) => (
            <TouchableOpacity key={index} style={styles.menuItem} onPress={() => handleNavigate(item.screen)}>
              <View style={styles.itemContent}>
                <MaterialIcons name={item.icon} size={23} color="plum" />
                <Text style={styles.menuText}>{item.name}</Text>
              </View>
              <MaterialIcons name="chevron-right" size={24} color="#aaa" />
            </TouchableOpacity>
          ))}

          {/* Dòng kẻ chia cách giữa các nhóm */}
          {groupIndex < menuItems.length - 1 && <View style={styles.divider} />}
        </View>
      ))}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    paddingHorizontal: 16,
    paddingTop: 20,
  },
  groupTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 8,
    color: '#555',
  },
  menuItem: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 5,
  },
  itemContent: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  menuText: {
    fontSize: 16,
    marginLeft: 12,
    color: '#333',
  },
  divider: {
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
    marginVertical: 12,
  },
});

export default Menu;
