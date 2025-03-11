
const CART_STORAGE_KEY = "cartItems";

// 🛒 Lấy danh sách sản phẩm từ LocalStorage
export const getCartFromLocalStorage = () => {
  const cartData = localStorage.getItem(CART_STORAGE_KEY);
  return cartData ? JSON.parse(cartData) : [];
};

// 🛒 Lưu danh sách sản phẩm vào LocalStorage
export const saveCartToLocalStorage = (cart: any[]) => {
  localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart));
};

// 🛒 Thêm sản phẩm vào giỏ hàng
export const addToCart = (product: any) => {
  const cart = getCartFromLocalStorage();
  const existingProduct = cart.find((item: any) => item.id === product.id);

  if (existingProduct) {
    existingProduct.quantity += product.quantity;
  } else {
    cart.push(product);

  }

  saveCartToLocalStorage(cart);
  return cart;
};

// 🛒 Xóa sản phẩm khỏi giỏ hàng
export const removeFromCart = (productId: string) => {
  let cart = getCartFromLocalStorage();
  cart = cart.filter((item: any) => item.id !== productId);
  saveCartToLocalStorage(cart);
  return cart;
};

// 🛒 Cập nhật số lượng sản phẩm
export const updateQuantity = (productId: string, quantity: number) => {
  const cart = getCartFromLocalStorage();
  const product = cart.find((item: any) => item.id === productId);

  if (product) {
    product.quantity = quantity;
  }

  saveCartToLocalStorage(cart);
  return cart;
};

// 🛒 Tính tổng tiền
export const calculateTotalAmount = () => {
  const cart = getCartFromLocalStorage();
  return cart.reduce((total: number, item: any) => total + item.price * item.quantity, 0);
};
