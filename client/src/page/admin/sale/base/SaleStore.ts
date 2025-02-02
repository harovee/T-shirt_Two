// saleStore.ts
import { defineStore } from 'pinia';

export const useSaleStore = defineStore('sale', {
  state: () => ({
    saleData: null as any,
  }),
  actions: {
    setSaleData(data: any) {
      this.saleData = data;
    },
  },
});
