import AsyncStorage from '@react-native-async-storage/async-storage';

export const storage = {
  async set(key, value) {
    try {
      const jsonValue = JSON.stringify(value);
      await AsyncStorage.setItem(key, jsonValue);
    } catch (error) {
      console.error(`Lỗi khi lưu dữ liệu [${key}]:`, error);
    }
  },

  async get(key) {
    try {
      const jsonValue = await AsyncStorage.getItem(key);
      return jsonValue != null ? JSON.parse(jsonValue) : null;
    } catch (error) {
      console.error(`Lỗi khi lấy dữ liệu [${key}]:`, error);
      return null;
    }
  },

  async remove(key) {
    try {
      await AsyncStorage.removeItem(key);
    } catch (error) {
      console.error(`Lỗi khi xóa dữ liệu [${key}]:`, error);
    }
  },

  async clearAll() {
    try {
      await AsyncStorage.clear();
    } catch (error) {
      console.error(`Lỗi khi xóa toàn bộ dữ liệu:`, error);
    }
  },
};
