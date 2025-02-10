<template>
  <a-modal
      :open="props.open"
      title="Import nhân viên từ Excel"
      @cancel="handleClose"
      @ok="handleImportStaffs"
      ok-text="Thêm"
      cancel-text="Hủy"
      destroyOnClose
      centered
  >
    <a-form layout="vertical">
      <a-form-item label="Chọn file Excel" v-bind="validateInfos.file">
        <a-upload-dragger
            :before-upload="beforeUpload"
            :show-upload-list="false"
        >
          <p class="ant-upload-drag-icon">
            <inbox-outlined/>
          </p>
          <p class="ant-upload-text">Kéo hoặc chọn file Excel để tải lên</p>
          <p v-if="modelRef.file" class="text-green-500">
            {{ modelRef.file.name }}
          </p>
        </a-upload-dragger>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import {defineEmits, defineProps, reactive} from "vue";
import {Form, Modal, notification} from "ant-design-vue";
import {InboxOutlined} from "@ant-design/icons-vue";
import {useImportStaffs} from "@/infrastructure/services/service/admin/staff.action.ts";

const props = defineProps({
  open: Boolean,
});

const emit = defineEmits(["handleClose"]);
const {mutate: importStaffs} = useImportStaffs();

const modelRef = reactive({
  file: null as File | null,
});

const rulesRef = reactive({
  file: [{required: true, message: "Vui lòng chọn file Excel để import!"}],
});

const {validate, validateInfos, resetFields} = Form.useForm(modelRef, rulesRef);

const beforeUpload = (file: File) => {
  const isExcel =
      file.type === "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ||
      file.type === "application/vnd.ms-excel";

  if (!isExcel) {
    notification.error({
      message: "Lỗi",
      description: "Vui lòng chọn file Excel hợp lệ (.xlsx hoặc .xls)",
      duration: 4,
    });
    return false;
  }

  modelRef.file = file;
  return false;
};

const handleImportStaffs = async () => {
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
  modelRef.file = null;
  resetFields();
  emit("handleClose");
};
</script>
