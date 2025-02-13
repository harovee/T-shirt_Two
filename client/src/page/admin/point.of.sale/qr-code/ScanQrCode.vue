<template>
  <a-modal
    v-bind:open="openModal"
    title="Quét Mã QR"
    @cancel="handleCancel"
    :width="'25%'"
    @update:open="handleUpdateOpen"
    :okText="'Xác nhận'"
    :cancelText="'Hủy bỏ'"
    :footer="null"
  >
    <qrcode-stream @decode="onDecode" @init="onInit" />
    <p v-if="qrResult">Mã QR: {{ qrResult }}</p>
  </a-modal>
</template>

<script lang="ts">
import { ref } from "vue";
import { QrcodeStream } from "vue3-qrcode-reader";

export default {
  name: "QRScanner",
  components: {
    QrcodeStream,
  },
  props: {
    openModal: Boolean,
  },
  emits: ["update:open", "cancel", "update:idSanPhamChitiet"],
  setup(props, { emit }) {
    const qrResult = ref("");

    const onDecode = (result:any) => {
      qrResult.value = result;
      console.log("Quét thành công:", result);
      handleCancel(result);
    };

    const onInit = (stream) => {
      // console.log("Stream initialized:", stream);
    };

    const handleCancel = (result: any) => {
      emit("update:open", false);
      emit("update:idSanPhamChitiet", result)
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
