import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useSearchStore = defineStore('search', () => {
  // State
  const searchKey = ref('');

  // Actions
  function setSearchKey(key: string) {
    searchKey.value = key;
  }

  function clearSearchKey() {
    searchKey.value = '';
  }

  return {
    searchKey,
    setSearchKey,
    clearSearchKey
  };
});