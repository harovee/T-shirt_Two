<template>
  <div id="notifications">
    Đây là thông báo
    <p v-for="(message, index) in messages" :key="index">{{ message }}</p>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, onUnmounted, ref } from 'vue'
import SockJS from 'sockjs-client'
import type { Client, Message, Subscription } from 'stompjs'
import Stomp from 'stompjs'

const messages = ref<string[]>([])
let stompClient: Client | null = null
let subscription: Subscription | null = null

onMounted(() => {
  const socket = new SockJS('http://localhost:6868/ws')
  stompClient = Stomp.over(socket)
  
  stompClient.connect({}, () => {
    subscription = stompClient!.subscribe(
      '/topic/notification',
      (message: Message) => {
        messages.value.push(message.body)
      }
    )
  })
})

onUnmounted(() => {
  subscription?.unsubscribe()
  stompClient?.disconnect(() => {
    console.log('Disconnected from WebSocket')
  })
})
</script>
