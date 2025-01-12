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
              :placeholder="field.placeholder"
          ></a-input>

          <a-radio-group
              v-if="field.component === 'a-radio-group'"
              v-for="option in field.options"
              v-model:value="modelRef[field.name]"
          >
            <a-radio :value="option.value">
              {{ option.name }}
            </a-radio>
          </a-radio-group>

          <a-date-picker
              class="w-full"
              v-else-if="field.component === 'a-date-picker'"
              v-model:value="modelRef[field.name]"
              format="YYYY-MM-DD HH:mm"
              show-time
              :placeholder="field.placeholder"
          ></a-date-picker>

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
  username: null,
  password: null,
  birthday: null,
  gender: null,
  phoneNumber: null
});

const rulesRef = reactive({
  name: [{required: true, message: "Vui lòng nhập tên khách hàng", trigger: "blur"}],
  email: [{required: true, message: "Vui lòng nhập tên khách hàng", trigger: "blur"}],
  username: [{required: true, message: "Vui lòng nhập tên tài khoản", trigger: "blur"}],
  password: [{required: true, message: "Vui lòng nhập mật khẩu", trigger: "blur"}],
  birthday: [{required: true, message: "Vui lòng nhập ngày sinh", trigger: "blur"}],
  gender: [{required: true, message: "Vui lòng chọn giới tính", trigger: "blur"}],
  phoneNumber: [{required: true, message: "Vui lòng nhập số điện thoại", trigger: "blur"}],
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
  {
    label: "Tên tài khoản",
    name: "username",
    component: "a-input",
    placeholder: "Nhâp tên tài khoản"
  },
  {
    label: "Mật khẩu",
    name: "password",
    component: "a-input",
    placeholder: "Nhâp mật khẩu"
  },
  {
    label: "Ngày sinh",
    name: "birthday",
    component: "a-date-picker",
    placeholder: "Nhâp ngày sinh"
  },
  {
    label: "Giới tính",
    name: "gender",
    component: "a-radio-group",
    options: [
      {
        name: "Nam",
        value: true,
      },
      {
        name: "Nữ",
        value: false,
      }
    ]
  },
  {
    label: "Số điện thoại",
    name: "phoneNumber",
    component: "a-input",
    placeholder: "Nhâp số điện thoại"
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