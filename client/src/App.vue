<script setup lang="ts">
import {VueQueryDevtools} from "@tanstack/vue-query-devtools";
import Chatbot from "./Chatbot.vue";
import LiveChat from "./websocket/view/LiveChat.vue";
</script>

<template>
  <vue-query-devtools/>
  <router-view/>
  <chatbot/>
  <live-chat/>
</template>

<style scoped>

</style>
