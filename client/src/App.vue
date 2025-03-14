<script setup lang="ts">
import {VueQueryDevtools} from "@tanstack/vue-query-devtools";
import Chatbot from "./Chatbot.vue";
</script>

<template>
  <vue-query-devtools/>
  <router-view/>
  <chatbot/>
</template>

<style scoped>

</style>
