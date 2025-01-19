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
import {
  SizeRequest,
  SizeResponse,
} from "@/infrastructure/services/api/admin/size.api";
import {
  useGetListSize,
  useCreateSize,
  useUpdateSize,
} from "@/infrastructure/services/service/admin/size.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { create } from "domain";

const props = defineProps({
  open: Boolean,
  SizeDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allSizeData: Array<SizeResponse>,
});

watch(
  () => props.SizeDetail,
  (newData) => console.log(newData)
);

const emit = defineEmits(["handleClose"]);

const { mutate: createSize } = useCreateSize();
const { mutate: updateSize } = useUpdateSize();

const modelRef = reactive<SizeRequest>({
  ten: "",
  chieuCaoMin: 0,
  chieuCaoMax: 0,
  canNangMin: 0,
  canNangMax: 0,
});

const regString = /^-?\d+(\.\d+)?$/;

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên kích cỡ", trigger: "blur" },
    {
      min: 1,
      max: 255,
      message: "Tên kích cỡ phải từ 1 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        const allSizeDatas = Array.isArray(props.allSizeData)
          ? props.allSizeData
          : [];
        if (
          props.SizeDetail &&
          props.SizeDetail.ten.toLowerCase() === value.toLowerCase()
        ) {
          return Promise.resolve();
        }
        const isNameExists = allSizeDatas.some(
          (cate) => cate.ten.trim().toLowerCase() === value.trim().toLowerCase()
        );
        if (isNameExists) {
          return Promise.reject("Tên kích cỡ đã tồn tại");
        }
        return Promise.resolve();
      },
      trigger: "blur",
    },
  ],
  chieuCaoMin: [
    {
      required: true,
      message: "Vui lòng nhập chiều cao tối thiểu",
      trigger: "change",
    },
    {
      validator: (rule, value) => {
        if (value != null && value <= 0) {
          return Promise.reject("Chiều cao tối thiểu phải lớn hơn 0");
        }
        return Promise.resolve();
      },
      trigger: "change",
    },
  ],
  chieuCaoMax: [
    {
      required: true,
      message: "Vui lòng nhập chiều cao tối đa",
      trigger: "change",
    },
    {
      validator: (rule, value) => {
        if (value != null && value <= 0) {
          return Promise.reject("Chiều cao tối đa phải lớn hơn 0");
        }
        if (value != null && modelRef.chieuCaoMin != null && value < modelRef.chieuCaoMin) {
        return Promise.reject("Chiều cao tối đa không được nhỏ hơn chiều cao tối thiểu");
      }
        return Promise.resolve();
      },
      trigger: "change",
    },
  ],
  canNangMax: [
    {
      required: true,
      message: "Vui lòng nhập cân nặng tối đa",
      trigger: "change",
    },
    {
      validator: (rule, value) => {
        if (value != null && value <= 0) {
          return Promise.reject("Cân nặng tối đa phải lớn hơn 0");
        }
        if (value != null && modelRef.canNangMin != null && value < modelRef.canNangMin) {
        return Promise.reject("Cân nặng tối đa không được nhỏ hơn cân nặng tối thiểu");
      }
        return Promise.resolve();
      },
      trigger: "change",
    },
  ],
  canNangMin: [
    {
      required: true,
      message: "Vui lòng nhập cân nặng tối thiểu",
      trigger: "change",
    },
    {
      validator: (rule, value) => {
        if (value != null && value <= 0) {
          return Promise.reject("Cân nặng tối thiểu phải lớn hơn 0");
        }
        return Promise.resolve();
      },
      trigger: "change",
    },
  ],
});

const { resetFields, validate, validateInfos } = Form.useForm(
  modelRef,
  rulesRef
);

const modalTitle = computed(() =>
  props.SizeDetail ? "Cập nhật kích cỡ" : "Thêm kích cỡ"
);

const okText = computed(() =>
  props.SizeDetail ? "Cập nhật kích cỡ" : "Thêm kích cỡ"
);

watch(
  () => props.SizeDetail,
  (newVal) => {
    if (newVal) {
      Object.assign(modelRef, {
        ten: newVal.ten,
        chieuCaoMin: newVal.chieuCaoMin,
        chieuCaoMax: newVal.chieuCaoMax,
        canNangMin: newVal.canNangMin,
        canNangMax: newVal.canNangMax,
      });
      console.log(modelRef);
    } else {
      resetFields();
    }
  },
  { immediate: true }
);

// lấy danh sách kích cỡ
const { data: categories } = useGetListSize({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listSize = computed(() => {
  return (
    categories?.value?.data?.map((size) => ({
      value: size.id,
      label: size.ten,
    })) || []
  );
});

// console.log(listSize.value);

const formFields = computed(() => [
  {
    label: "Tên kích cỡ",
    name: "ten",
    component: "a-input",
    props: {
      placeholder: "Nhập tên kích cỡ",
    },
  },
  {
    label: "Chiều cao tối thiểu",
    name: "chieuCaoMin",
    component: "a-input-number",
    props: {
      placeholder: "Nhập cân nặng",
      step: 0.1,
      min: 0,
      precision: 1,
      style: {
        width: "100%",
      },
    },
  },
  {
    label: "Chiều cao tối đa",
    name: "chieuCaoMax",
    component: "a-input-number",
    props: {
      placeholder: "Nhập chiều cao",
      step: 0.1,
      min: 0,
      precision: 1,
      style: {
        width: "100%",
      },
    },
  },
  {
    label: "Cân nặng tối thiểu",
    name: "canNangMin",
    component: "a-input-number",
    props: {
      placeholder: "Nhập cân nặng",
      step: 0.1,
      min: 0,
      precision: 1,
      style: {
        width: "100%",
      },
    },
  },
  {
    label: "Cân nặng tối đa",
    name: "canNangMax",
    component: "a-input-number",
    props: {
      placeholder: "Nhập cân nặng",
      step: 0.1,
      min: 0,
      precision: 1,
      style: {
        width: "100%",
      },
    },
  },
]);

const handleAddOrUpdate = async () => {
  const payload = {
    ten: modelRef.ten,
    chieuCaoMin: modelRef.chieuCaoMin,
    chieuCaoMax: modelRef.chieuCaoMax,
    canNangMin: modelRef.canNangMin,
    canNangMax: modelRef.canNangMax,
  };

  try {
    await validate();
    if (props.SizeDetail) {
      await updateSize({
        id: props.SizeDetail.id,
        data: payload,
      });
    } else {
      await createSize(payload);
      resetFields();
    }

    toast.success(
      props.SizeDetail
        ? "Cập nhật kích cỡ thành công"
        : "Thêm kích cỡ thành công"
    );

    emit("handleClose");
  } catch (error: any) {
    console.error("🚀 ~ handleAddOrUpdate ~ error:", error);
    toast.warning(error?.response?.data?.message);
  }
};

const handleClose = () => {
  emit("handleClose");
  resetFields();
};
</script>