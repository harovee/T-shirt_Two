<template>
  <a-modal
    v-bind:open="openModal"
    title="Quét Mã QR"
    @cancel="handleCancel"
    :width="'25%'"
    @update:open="handleUpdateOpen"
  >
    <qrcode-stream @decode="onDecode" @init="onInit" />
    <p v-if="qrResult">Mã QR: {{ qrResult }}</p>
  </a-modal>
</template>

<script>
import { ref } from "vue";
import { QrcodeStream } from "vue3-qrcode-reader";

export default {
  name: "QRScanner",
  components: {
    QrcodeStream
  },
  props: {
    openModal: Boolean,
  },
  emits: ["update:open", "cancel"],
  setup(props, { emit }) {
    const qrResult = ref("");
    
    const onDecode = (result) => {
      qrResult.value = result;
      console.log("Quét thành công:", result);
      // handleCancel();
    };

    // Hàm xử lý khi khởi tạo stream
    const onInit = (stream) => {
      console.log("Stream initialized:", stream);
    };

    const handleCancel = () => {
      emit("update:open", false);
      qrResult.value = "";
    };

    const handleUpdateOpen = (value) => {
      emit("update:open", value);
    };

    return {
      qrResult,
      handleCancel,
      handleUpdateOpen,
      onDecode,
      onInit,
    };
  },
};
</script>

<style scoped>
</style>
