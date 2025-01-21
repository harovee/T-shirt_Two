<template>
  <a-form layout="vertical" class="pt-3">
    <div class="h-3 font-bold py-6">
      Địa chỉ
    </div>
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
      </a-form-item>
    </template>
  </a-form>
</template>

<script setup lang="ts">
import {computed, defineEmits, reactive} from "vue";
import {Form} from "ant-design-vue";
import {ClientRequest} from "@/infrastructure/services/api/admin/client.api.ts";

const emit = defineEmits(["handleClose"]);

const modelRef = reactive<ClientRequest>({
  name: null,
  email: null,
  password: null,
  birthday: null,
  gender: null,
  phoneNumber: null,
  picture: null
});

const rulesRef = reactive({
  name: [
    {
      required: true,
      validator: (_, value) => value !== null && value.trim() !== "" ? Promise.resolve() : Promise.reject("Tên không được để trống"),
      trigger: "blur"
    },
    {max: 50, message: "Tên không được dài quá 50 ký tự", trigger: "blur"},
  ],
  phoneNumber: [
    {required: true, message: "Vui lòng nhập số điện thoại", trigger: "blur"},
    {
      pattern: /^0[1-9]\d{8,9}$/,
      message: "Số điện thoại phải bắt đầu bằng số 0 và có 10-11 chữ số.",
      trigger: "blur"
    },
  ],
  detailAddress: [
    {required: true, message: "Vui lòng nhập số điện thoại", trigger: "blur"},
    {max: 200, message: "địa chỉ không được dài quá 200 ký tự", trigger: "blur"},
  ]
});

const {resetFields, validate, validateInfos} = Form.useForm(
    modelRef,
    rulesRef
);

const formFields = computed(() => [
  {
    label: "Tên khách hàng",
    name: "name",
    type: "string",
    component: "a-input",
    placeholder: "Nhâp tên khách hàng"
  },
  {
    label: "Số điện thoại",
    name: "phoneNumber",
    type: "number",
    component: "a-input",
    placeholder: "Nhâp số điện thoại"
  },
  {
    label: "Chi tiết địa chỉ",
    name: "detailAddress",
    type: "string",
    component: "a-input",
    placeholder: "Nhập chi tiết địa chỉ"
  },
]);

</script>