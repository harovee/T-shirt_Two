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
import { warningNotiSort, successNotiSort, errorNotiSort } from "@/utils/notification.config";
import { Form, message, Modal, Upload } from "ant-design-vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { toast } from "vue3-toastify";
import { ColorRequest, ColorResponse } from "@/infrastructure/services/api/admin/color.api";
import { useGetListColor, useCreateColor, useUpdateColor } from "@/infrastructure/services/service/admin/color.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { create } from "domain";

const props = defineProps({
  open: Boolean,
  ColorDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allColorData: Array<ColorResponse>,
});

watch(
  () => props.ColorDetail,
  (newData) => console.log(newData)
);


const emit = defineEmits(["handleClose"]);

const { mutate: createColor } = useCreateColor();
const { mutate: updateColor } = useUpdateColor();

const modelRef = reactive<ColorRequest>({
  ten: ""
});

const regString = /^-?\d+(\.\d+)?$/;

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên màu sắc", trigger: "blur" },
    {
      min: 1,
      max: 255,
      message: "Tên màu sắc phải từ 1 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        
        const allColorDatas = Array.isArray(props.allColorData) ? props.allColorData : [];
        if (props.ColorDetail && props.ColorDetail.ten.toLowerCase() === value.toLowerCase()) {
            return Promise.resolve();
        }
        const isNameExists = allColorDatas.some(
          (cate) => cate.ten.trim().toLowerCase() === value.trim().toLowerCase()
        );
        if (isNameExists) {
          return Promise.reject("Tên màu sắc đã tồn tại");
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
  props.ColorDetail
    ? "Cập nhật màu sắc"
    : "Thêm màu sắc"
);

const okText = computed(() =>
  props.ColorDetail
    ? "Cập nhật màu sắc"
    : "Thêm màu sắc"
);

watch(
  () => props.ColorDetail,
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

// lấy danh sách màu sắc
const { data: categories } = useGetListColor({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listColor = computed(() => {
  return (
    categories?.value?.data?.map((color) => ({
      value: color.id,
      label: color.ten,
    })) || []
  );
});

// console.log(listColor.value);

const formFields = computed(() => [
  {
    label: "Tên màu sắc",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên màu sắc",
  }
]);

const handleAddOrUpdate = async () => {
  const payload = {
    ten: modelRef.ten
  };

  Modal.confirm({
    content: props.ColorDetail
            ? "Bạn chắc chắn muốn cập nhật?"
            : "Bạn chắc chắn muốn thêm mới?" ,
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,

    async onOk() {

  try {
    await validate();
    if (props.ColorDetail) {
      await updateColor({
        id: props.ColorDetail.id,
        data: payload,
      });
      
    } else {
      await createColor(payload);
      resetFields();
    }
    
    successNotiSort(
      props.ColorDetail
        ? "Cập nhật màu sắc thành công"
        : "Thêm màu sắc thành công"
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