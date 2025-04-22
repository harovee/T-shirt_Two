import * as React from 'react';
import { useState, useContext } from 'react';
import {NavigationContainer} from '@react-navigation/native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { StyleSheet, Text, View, Button, TextInput, Pressable, ScrollView, FlatList, SafeAreaView } from 'react-native';
import {  MaterialIcons } from '@expo/vector-icons';


import {createNativeStackNavigator} from '@react-navigation/native-stack';
import { InvoiceContext } from '../core/invoice.connect/InvoiceContext';

import Slide from '../component/home/Slide';
import Widget from '../component/home/Widget';
import BestSellerListInHome from '../component/home/BestSellerListInHome';
import ProductList from './home/ProductList';

export default Home = ({navigation}) => {
  const insets = useSafeAreaInsets();
    const {
      isConnected,
    } = useContext(InvoiceContext);

  const list_screens = [
    {
      key: '1',
      name: 'slide',
      value: Slide,
      style: styles.slide_container
    },
    {
      key: '2',
      name: 'widget',
      value: Widget,
      style: {}
    },
    {
      key: '3',
      name: 'best-seller',
      value: BestSellerListInHome,
      style: {}
    },
    {
      key: '4',
      name: 'product-list',
      value: ProductList,
      style: {}
    },

  ];
  return (
    <View>

      <View  style={styles.header}>
          <Pressable
          onPress={() => navigation.navigate('Menu')}
          style={({ pressed }) => [
            pressed && styles.pressedButton,
          ]}
          >
             <MaterialIcons name="menu" size={24} style={styles.header_button}/>
          </Pressable>
          
            <View style={styles.right_header}>
                <Pressable
                onPress={() => navigation.navigate('Profile', { name: 'Thảo' })}
                style={({ pressed }) => [
                  pressed && styles.pressedButton,
                ]}
                >
                  <MaterialIcons name="person" size={24} style={styles.header_button}/>
                </Pressable>
                
                <Pressable
                onPress={() => navigation.navigate('Cart')}
                style={({ pressed }) => [
                  styles.ms,
                  pressed && styles.pressedButton,
                ]}
                >
                  <MaterialIcons name="shopping-cart" size={24} style={styles.header_button}/>
                </Pressable>

                <Pressable
                onPress={() => navigation.navigate('InvoiceConnect')}
                style={({ pressed }) => [
                  styles.ms,
                  pressed && styles.pressedButton,
                  { backgroundColor: isConnected ? 'red':'white'}
                ]}
                >
                  <MaterialIcons name="link" size={26} style={styles.header_button}/>
                </Pressable>
                
            </View>
      </View>

      <View style={styles.bg}>
        <FlatList
            data={list_screens}
            keyExtractor={(item) => item.key}
                contentContainerStyle={{
                  paddingBottom: insets.bottom + 150, 
                }}
            renderItem={({ item }) => {
              const ScreenComponent = item.value;
              return (
                <SafeAreaView style={[item.style, styles.bg]}>
                  <ScreenComponent navigation={navigation} />
                </SafeAreaView>
              );
            }}
        />

      </View>
      
    </View>
    
  );
};



const styles = StyleSheet.create({
  header: {
    position: 'sticky',
    height: 40,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 15,
    boxShadow: '1 0 1',
    backgroundColor: 'white'
  },
  right_header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  title: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
  ms:{
    marginLeft: '8'
  },
  bg: {
    backgroundColor: 'white',
  },
  header_button: {
    color: 'plum',
    cursor: 'pointer',
  },
  pressedButton: {
    backgroundColor: '#ddd',
    opacity: 0.8,
  },

  main: {
    height: '700',
  },

  slide_container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
    justifyContent: 'center',
    alignItems: 'center',
  },

  widget_container: {
    elevation: 3,
    width: 'fit-content'

  }

});
