<template>
  <a-modal
      :open="props.open"
      title="Thông tin khách hàng"
      @cancel="handleClose"
      @ok="handleCreateClient"
      ok-text="Thêm"
      cancel-text="Hủy"
      destroyOnClose
      centered
  >
    <a-form layout="vertical" class="pt-3">
      <template v-for="field in formFields">
        <a-form-item
            :label="field.label"
            :name="field.name"
            v-bind="validateInfos[field.name]"
        >
          <a-input
              v-if="field.component === 'a-input'"
              v-model:value="modelRef[field.name]"
          ></a-input>

          <!--          <a-select-->
          <!--              v-else-if="field.component === 'a-select'"-->
          <!--              :max-tag-count="field.maxTagCount"-->
          <!--              :placeholder="field.placeholder"-->
          <!--              :show-search="field.showSearch"-->
          <!--              :filter-option="field.filterOption"-->
          <!--              :allow-clear="field.allowClear"-->
          <!--              :mode="field.mode"-->
          <!--              :options="field.options"-->
          <!--              v-model:value="modelRef[field.name]"-->
          <!--          ></a-select>-->

          <!--          <a-date-picker-->
          <!--              class="w-full"-->
          <!--              v-else-if="field.component === 'a-date-picker'"-->
          <!--              v-model:value="modelRef[field.name]"-->
          <!--              format="YYYY-MM-DD HH:mm"-->
          <!--              show-time-->
          <!--              :placeholder="field.placeholder"-->
          <!--          ></a-date-picker>-->

          <!--          <a-upload-->
          <!--              v-else-if="field.component === 'a-upload'"-->
          <!--              v-bind="field.customProps || {}"-->
          <!--              :max-count="1"-->
          <!--              v-model:value="modelRef[field.name]"-->
          <!--          >-->
          <!--            <a-button class="flex justify-between items-center gap-1">-->
          <!--              <upload-outlined></upload-outlined>-->
          <!--              Tải tệp âm thanh-->
          <!--            </a-button>-->
          <!--          </a-upload>-->

        </a-form-item>
      </template>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import {computed, createVNode, defineEmits, defineProps, reactive} from "vue";
import {Form, Modal} from "ant-design-vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import {toast} from "vue3-toastify";
import {useCreateClient} from "@/infrastructure/services/service/admin/client.action.ts";
import {ClientRequest} from "@/infrastructure/services/api/admin/client.api.ts";

const props = defineProps({
  open: Boolean,
});

const emit = defineEmits(["handleClose"]);

const {mutate: create} = useCreateClient();

const modelRef = reactive<ClientRequest>({
  name: null,
  email: null,
});

const rulesRef = reactive({
  name: [{required: true, message: "Vui lòng nhập tên khách hàng", trigger: "blur"}],
  email: [{required: true, message: "Vui lòng nhập tên khách hàng", trigger: "blur"}],
});

const {resetFields, validate, validateInfos} = Form.useForm(
    modelRef,
    rulesRef
);

const formFields = computed(() => [
  {
    label: "Tên khách hàng",
    name: "name",
    component: "a-input",
    placeholder: "Nhâp tên khách hàng"
  },
  {
    label: "Email",
    name: "email",
    component: "a-input",
    placeholder: "Nhâp email"
  },
]);

const handleCreateClient = () => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn thêm?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await validate();
        create(modelRef, {
          onSuccess: (result) => {
            toast.success(result?.message);
            handleClose();
          },
          onError: (error: any) => {
            toast.error(
                error?.response?.data?.message
            );
          },
        });
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          toast.warning(
              error?.response?.data?.message
          );
        } else if (error?.errorFields) {
          toast.warning("Vui lòng nhập đầy đủ các trường dữ liệu");
        }
      }
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
      resetFields();
    },
  });
};

const handleClose = () => {
  emit("handleClose");
  resetFields();
};
</script>