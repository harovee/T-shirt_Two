import { ref, computed } from "vue";
import {
  getCartFromLocalStorage,
  saveCartToLocalStorage,
  addToCart,
  removeFromCart,
  updateQuantity,
  calculateTotalAmount,
} from "@/page/client/products/business.logic/CartLocalStorageBL";

export const useCartStorageBL = () => {
  const cart = ref(getCartFromLocalStorage());

  const totalAmount = computed(() => calculateTotalAmount());

  const addProduct = (product: any) => {
    cart.value = addToCart(product);
  };

  const removeProduct = (productId: string) => {
    cart.value = removeFromCart(productId);
  };

  const updateProductQuantity = (productId: string, quantity: number) => {
    cart.value = updateQuantity(productId, quantity);
  };

  return {
    cart,
    totalAmount,
    addProduct,
    removeProduct,
    updateProductQuantity,
  };
};
