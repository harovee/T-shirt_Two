import * as React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import { StyleSheet, Text, View, Button, TextInput, Pressable, ScrollView, FlatList, SafeAreaView } from 'react-native';
import {  MaterialIcons } from '@expo/vector-icons';


import {createNativeStackNavigator} from '@react-navigation/native-stack';
import Home from '../component/HomeScreen';
import Menu from '../component/menu/MenuScreen';
import Cart from '../component/cart/CartScreen';
import Profile from '../component/client/ProfileScreen';
import VoucherList from '../component/voucher/VoucherList';
import ProductDetail from '../component/product/ProductDetail';

const Stack = createNativeStackNavigator();

const MainTabNavigator = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen
          name="Home"
          component={Home}
          options={{
            title: null,
            headerSearchBarOptions: {
              placeholder: 'Tên, danh mục',
              onChangeText: (e) => {
                console.log(e.nativeEvent.text)
              }
            },
          }}
        />
        <Stack.Screen name="Menu" component={Menu}
          options={{
            title: 'Trang chủ' ,
            presentation: 'fullScreenModal',
            animation: 'fade_from_bottom', 
            animationDuration: 10,
          }}
        />
        <Stack.Screen name="Profile" component={Profile}
          options={{
            title: 'Tài khoản' ,
            presentation: 'fullScreenModal',
            animation: 'fade_from_bottom', 
            animationDuration: 10,
          }}
        />
        <Stack.Screen name="Cart" component={Cart}
           options={{
            title: 'Giỏ hàng' ,
            headerTitleAlign:  'center',
            presentation: 'fullScreenModal',
            animation: 'fade_from_bottom', 
            animationDuration: 10,
          }}
        />

      <Stack.Screen name="VoucherList" component={VoucherList}
           options={{
            title: 'Ưu đãi' ,
            headerTitleAlign:  'center',
            presentation: 'fullScreenModal',
            animation: 'fade_from_bottom', 
            animationDuration: 10,
          }}
        />

      <Stack.Screen name="ProductDetail" component={ProductDetail}
          options={({ navigation }) => ({
            title: 'Chi tiết sản phẩm',
            presentation: 'fullScreenModal',
            animation: 'fade_from_bottom', 
            animationDuration: 10,
          })}
        />


        <Stack.Screen name="example_screen" component={example_screen}
           options={{
            title: 'Example' ,
            headerTitleAlign:  'center',
            presentation: 'fullScreenModal',
            animation: 'fade_from_bottom', 
            animationDuration: 10,
          
          }}
        />

      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default MainTabNavigator;

const example_screen = ({navigation}) => {
  return <Text>example</Text>;
};



const styles = StyleSheet.create({
  
});