import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const TermsScreen = () => {
  return (
    <View style={styles.container}>
      <Text>Đây là màn hình thông tin điều khoản</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default TermsScreen;
