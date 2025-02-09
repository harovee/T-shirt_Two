<template>
  <a-modal
      :open="props.open"
      title="Quét Mã QR Code"
      @cancel="handleClose"
      @ok="handleQRScanStaffs"
      ok-text="Quét"
      cancel-text="Hủy"
      destroyOnClose
      centered
  >
    <a-form layout="vertical">
      <a-form-item label="Chọn file Excel" v-bind="validateInfos.file">
        <qrcode-stream
            @decode="onDecode"
        ></qrcode-stream>
        <p v-if="qrResult">Dữ liệu quét được: {{ qrResult }}</p>
      </a-form-item>
    </a-form>
  </a-modal>
</template>


<script setup lang="ts">
import {defineEmits, defineProps} from "vue";
import {Modal, notification} from "ant-design-vue";
import {QrcodeStream} from "vue-qrcode-reader";

const props = defineProps({
  open: Boolean,
});

const emit = defineEmits(["handleClose"]);


// Biến lưu dữ liệu quét được
const qrResult = ref<string | null>(null);

// Hàm xử lý khi quét được mã QR
const onDecode = async (result: string) => {
  qrResult.value = result;

  // await sendToBackend(result);
};

const sendToBackend = async (qrData: string) => {
  try {
    const response = await fetch("http://localhost:8080/api/scan-qr", {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({qrData}),
    });
    const data = await response.json();
    console.log("Thông tin CCCD:", data);
  } catch (error) {
    console.error("Lỗi gửi dữ liệu:", error);
  }
};

// const onInit = (promise: Promise<void>) => {
//   promise.catch((error) => console.error("Lỗi camera:", error));
// };

// const {mutate: importStaffs} = useImportStaffs();

// const modelRef = reactive({
//   file: null as File | null,
// });
//
// const rulesRef = reactive({
//   file: [{required: true, message: "Vui lòng chọn file Excel để import!"}],
// });
//
// const {validate, validateInfos, resetFields} = Form.useForm(modelRef, rulesRef);
//
// const beforeUpload = (file: File) => {
//   const isExcel =
//       file.type === "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ||
//       file.type === "application/vnd.ms-excel";
//
//   if (!isExcel) {
//     notification.error({
//       message: "Lỗi",
//       description: "Vui lòng chọn file Excel hợp lệ (.xlsx hoặc .xls)",
//       duration: 4,
//     });
//     return false;
//   }
//
//   modelRef.file = file;
//   return false;
// };

const handleQRScanStaffs = async () => {
  try {
    await validate();

    Modal.confirm({
      content: "Bạn chắc chắn muốn import file Excel?",
      centered: true,
      async onOk() {
        try {
          const formData = new FormData();
          formData.append("file", modelRef.file as File);

          importStaffs(formData, {
            onSuccess: (res) => {
              console.log(res);
              notification.success({
                message: "Thành công",
                description: res?.message || "Import thành công!",
                duration: 4,
              });
              handleClose();
            },
            onError: (error: any) => {
              notification.warning({
                message: "Cảnh báo",
                description: error?.response?.data?.message || "Import thất bại, file không đúng form template!",
                duration: 4,
              });
            },
          });
        } catch (error) {
          console.error("Lỗi khi import:", error);
          notification.error({
            message: "Lỗi hệ thống",
            description: "Có lỗi xảy ra khi xử lý file.",
            duration: 4,
          });
        }
      },
      cancelText: "Hủy",
    });
  } catch (error) {
    console.error("Lỗi validate:", error);
  }
};

const handleClose = () => {
  emit("handleClose");
};
</script>
