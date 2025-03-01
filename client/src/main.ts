import {createApp} from 'vue'
import './style.css'
import App from './App.vue'
import router from '@/infrastructure/routes/router.ts';
import Antd from "ant-design-vue";
import * as AllIcons from "oh-vue-icons/icons";
import {addIcons, OhVueIcon} from "oh-vue-icons";
import "ant-design-vue/dist/reset.css";
import Vue3Toastify, {type ToastContainerOptions} from "vue3-toastify";
import "vue3-toastify/dist/index.css";
import { VueQueryPlugin } from "@tanstack/vue-query";
import { createPinia } from "pinia";
import { notification } from 'ant-design-vue';
// import Vue3ColorPicker from "vue3-colorpicker";
// import "vue3-colorpicker/style.css";

const AllIcon = Object.values({...AllIcons});
addIcons(...AllIcon);

const app = createApp(App);

app.use(router);
app.use(Antd);
app.use(Vue3Toastify, {
    autoClose: 3000,
} as ToastContainerOptions);
app.use(VueQueryPlugin);
app.use(createPinia());
app.component("v-icon", OhVueIcon);
app.config.globalProperties.$notify = notification;
// app.use(Vue3ColorPicker)

app.mount('#app');

