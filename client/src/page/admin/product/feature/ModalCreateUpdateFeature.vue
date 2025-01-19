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
import { FeatureRequest, FeatureResponse } from "@/infrastructure/services/api/admin/feature.api";
import { useGetListFeature, useCreateFeature, useUpdateFeature } from "@/infrastructure/services/service/admin/feature.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { create } from "domain";

const props = defineProps({
  open: Boolean,
  FeatureDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allFeatureData: Array<FeatureResponse>,
});

watch(
  () => props.FeatureDetail,
  (newData) => console.log(newData)
);


const emit = defineEmits(["handleClose"]);

const { mutate: createFeature } = useCreateFeature();
const { mutate: updateFeature } = useUpdateFeature();

const modelRef = reactive<FeatureRequest>({
  ten: ""
});

const regString = /^-?\d+(\.\d+)?$/;

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên tính năng", trigger: "blur" },
    {
      min: 1,
      max: 255,
      message: "Tên tính năng phải từ 1 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        
        const allFeatureDatas = Array.isArray(props.allFeatureData) ? props.allFeatureData : [];
        if (props.FeatureDetail && props.FeatureDetail.ten.toLowerCase() === value.toLowerCase()) {
            return Promise.resolve();
        }
        const isNameExists = allFeatureDatas.some(
          (cate) => cate.ten.trim().toLowerCase() === value.trim().toLowerCase()
        );
        if (isNameExists) {
          return Promise.reject("Tên tính năng đã tồn tại");
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
  props.FeatureDetail
    ? "Cập nhật tính năng"
    : "Thêm tính năng"
);

const okText = computed(() =>
  props.FeatureDetail
    ? "Cập nhật tính năng"
    : "Thêm tính năng"
);

watch(
  () => props.FeatureDetail,
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

// lấy danh sách tính năng
const { data: categories } = useGetListFeature({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listFeature = computed(() => {
  return (
    categories?.value?.data?.map((feature) => ({
      value: feature.id,
      label: feature.ten,
    })) || []
  );
});

// console.log(listFeature.value);

const formFields = computed(() => [
  {
    label: "Tên tính năng",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên tính năng",
  }
]);

const handleAddOrUpdate = async () => {
  const payload = {
    ten: modelRef.ten
  };

  try {
    await validate();
    if (props.FeatureDetail) {
      await updateFeature({
        id: props.FeatureDetail.id,
        data: payload,
      });
      
    } else {
      await createFeature(payload);
      resetFields();
    }
    
    toast.success(
      props.FeatureDetail
        ? "Cập nhật tính năng thành công"
        : "Thêm tính năng thành công"
    );
    
    emit("handleClose");
  } catch (error: any) {
    console.error("🚀 ~ handleAddOrUpdate ~ error:", error);
    toast.warning(
      error?.response?.data?.message
    );
  }
};

const handleClose = () => {
  emit("handleClose");
  resetFields();
};
</script>