import { defineStore } from 'pinia';

export const useCartStore = defineStore('cart', {
  state: () => ({
    checkoutData: []
  }),

  actions: {
    setCheckoutData(data: any) {
      this.checkoutData = data;
    },
    clearCheckoutData() {
      this.checkoutData = [];
    }
  }
});
