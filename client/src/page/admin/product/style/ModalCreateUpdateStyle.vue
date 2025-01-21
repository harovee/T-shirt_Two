<template>
  <div>
    <a-modal
      :open="props.open"
      :title="modalTitle"
      @cancel="handleClose"
      @ok="handleAddOrUpdate"
      :ok-text="okText"
      destroyOnClose
      centered
    >
      <a-form layout="vertical">
        <template v-for="field in formFields">
          <a-form-item
            :label="field.label"
            :name="field.name"
            v-bind="validateInfos[field.name]"
          >
            <component
              :is="field.component"
              v-bind="field.props"
              v-model:value="modelRef[field.name]"
            >
            </component>
          </a-form-item>
        </template>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import {
  computed,
  createVNode,
  defineEmits,
  defineProps,
  reactive,
  watch,
} from "vue";
import { Form, message, Modal, Upload } from "ant-design-vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { toast } from "vue3-toastify";
import { StyleRequest, StyleResponse } from "@/infrastructure/services/api/admin/style.api";
import { useGetListStyle, useCreateStyle, useUpdateStyle } from "@/infrastructure/services/service/admin/style.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { create } from "domain";
import { successNotiSort, warningNotiSort } from "@/utils/notification.config";

const props = defineProps({
  open: Boolean,
  StyleDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allStyleData: Array<StyleResponse>,
});

watch(
  () => props.StyleDetail,
  (newData) => console.log(newData)
);


const emit = defineEmits(["handleClose"]);

const { mutate: createStyle } = useCreateStyle();
const { mutate: updateStyle } = useUpdateStyle();

const modelRef = reactive<StyleRequest>({
  ten: ""
});

const regString = /^-?\d+(\.\d+)?$/;

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên kiểu dáng", trigger: "blur" },
    {
      min: 1,
      max: 255,
      message: "Tên kiểu dáng phải từ 1 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        
        const allStyleDatas = Array.isArray(props.allStyleData) ? props.allStyleData : [];
        if (props.StyleDetail && props.StyleDetail.ten.toLowerCase() === value.toLowerCase()) {
            return Promise.resolve();
        }
        const isNameExists = allStyleDatas.some(
          (cate) => cate.ten.trim().toLowerCase() === value.trim().toLowerCase()
        );
        if (isNameExists) {
          return Promise.reject("Tên kiểu dáng đã tồn tại");
        }
        return Promise.resolve();
        },
        trigger: "blur",
      },
  ],
});

const { resetFields, validate, validateInfos } = Form.useForm(
  modelRef,
  rulesRef
);

const modalTitle = computed(() =>
  props.StyleDetail
    ? "Cập nhật kiểu dáng"
    : "Thêm kiểu dáng"
);

const okText = computed(() =>
  props.StyleDetail
    ? "Cập nhật kiểu dáng"
    : "Thêm kiểu dáng"
);

watch(
  () => props.StyleDetail,
  (newVal) => {
    if (newVal) {
      Object.assign(modelRef, {
        ten: newVal.ten
      });
      console.log(modelRef);
      
    } else {
      resetFields();
    }
  },
  { immediate: true }
);

// lấy danh sách kiểu dáng
const { data: categories } = useGetListStyle({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listStyle = computed(() => {
  return (
    categories?.value?.data?.map((style) => ({
      value: style.id,
      label: style.ten,
    })) || []
  );
});

// console.log(listStyle.value);

const formFields = computed(() => [
  {
    label: "Tên kiểu dáng",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên kiểu dáng",
  }
]);

const handleAddOrUpdate = async () => {
  const payload = {
    ten: modelRef.ten
  };

  Modal.confirm({
    content: props.StyleDetail
            ? "Bạn chắc chắn muốn cập nhật?"
            : "Bạn chắc chắn muốn thêm mới?" ,
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,

    async onOk() {

  try {
    await validate();
    if (props.StyleDetail) {
      await updateStyle({
        id: props.StyleDetail.id,
        data: payload,
      });
      
    } else {
      await createStyle(payload);
      resetFields();
    }
    
    successNotiSort(
      props.StyleDetail
        ? "Cập nhật kiểu dáng thành công"
        : "Thêm kiểu dáng thành công"
    );
    
    emit("handleClose");
  } catch (error: any) {
    console.error("🚀 ~ handleAddOrUpdate ~ error:", error);
    warningNotiSort(
      error?.response?.data?.message
    );
  }}})
};

const handleClose = () => {
  emit("handleClose");
  resetFields();
};
</script>