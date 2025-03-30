import React, { useState, useEffect, useRef } from 'react';
import { View, Text, Image, StyleSheet, Dimensions, TouchableOpacity, FlatList, Animated } from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';

const screenWidth = Dimensions.get('window').width;

const Slide = () => {
  const slide_data = [
    {
      id: '1',
      title: 'Slide 1',
      description: 'This is the first slide',
      image: 'https://th.bing.com/th?id=OIF.b4XLN5117Ng%2bea4mEjN73g&rs=1&pid=ImgDetMain',
    },
    {
      id: '2',
      title: 'Slide 2',
      description: 'This is the second slide',
      image: 'https://via.placeholder.com/400x200/7FB3FF/333333?text=Slide+2',
    },
    {
      id: '3',
      title: 'Slide 3',
      description: 'This is the third slide',
      image: 'https://via.placeholder.com/400x200/7FFF7F/333333?text=Slide+3',
    },
  ];

  const [currentIndex, setCurrentIndex] = useState(0);
  const flatListRef = useRef(null);

  // Hàm tự động chuyển slide
  useEffect(() => {
    const interval = setInterval(() => {
      handleNext();
    }, 3000); // Tự động sau 3 giây

    return () => clearInterval(interval);
  }, [currentIndex]);

  const handleNext = () => {
    const nextIndex = currentIndex === slide_data.length - 1 ? 0 : currentIndex + 1;
    setCurrentIndex(nextIndex);
    flatListRef.current.scrollToIndex({ index: nextIndex, animated: true });
  };

  const handlePrev = () => {
    const prevIndex = currentIndex === 0 ? slide_data.length - 1 : currentIndex - 1;
    setCurrentIndex(prevIndex);
    flatListRef.current.scrollToIndex({ index: prevIndex, animated: true });
  };

  const handleScroll = (event) => {
    const slideIndex = Math.round(event.nativeEvent.contentOffset.x / screenWidth);
    setCurrentIndex(slideIndex);
  };

  return (
    <View style={styles.container}>
      {/* Nút mũi tên trái */}
      <TouchableOpacity style={styles.arrowLeft} onPress={handlePrev}>
        <MaterialIcons name="arrow-back-ios" size={24} color="black" />
      </TouchableOpacity>

      {/* Slider */}
      <FlatList
        ref={flatListRef}
        data={slide_data}
        horizontal
        pagingEnabled
        showsHorizontalScrollIndicator={false}
        onScroll={handleScroll}
        renderItem={({ item }) => (
          <View style={styles.slide}>
            <Image source={{ uri: item.image }} style={styles.image} />
            {/* <Text style={styles.title}>{item.title}</Text> */}
            {/* <Text style={styles.description}>{item.description}</Text> */}
          </View>
        )}
        keyExtractor={(item) => item.id}
      />

      {/* Nút mũi tên phải */}
      <TouchableOpacity style={styles.arrowRight} onPress={handleNext}>
        <MaterialIcons name="arrow-forward-ios" size={24} color="black" />
      </TouchableOpacity>

      {/* Chấm trạng thái */}
      <View style={styles.indicators}>
        {slide_data.map((_, index) => (
          <View
            key={index}
            style={[
              styles.indicator,
              currentIndex === index ? styles.activeIndicator : {},
            ]}
          />
        ))}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    width: screenWidth,
    height: 180,
    justifyContent: 'center',
    alignItems: 'center',
    position: 'relative',
    marginVertical: 5,
  },
  slide: {
    width: screenWidth,
    alignItems: 'center',
  },
  image: {
    width: screenWidth,
    height: 150,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginTop: 8,
  },
  description: {
    fontSize: 14,
    color: '#666',
    marginTop: 4,
  },
  arrowLeft: {
    position: 'absolute',
    left: 10,
    top: '40%',
    zIndex: 1,
  },
  arrowRight: {
    position: 'absolute',
    right: 10,
    top: '40%',
    zIndex: 1,
  },
  indicators: {
    flexDirection: 'row',
    position: 'absolute',
    bottom: 5,
    alignSelf: 'center',
  },
  indicator: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#ccc',
    margin: 4,
  },
  activeIndicator: {
    backgroundColor: '#000',
  },
});

export default Slide;
