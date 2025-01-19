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
import { MaterialRequest, MaterialResponse } from "@/infrastructure/services/api/admin/material.api";
import { useGetListMaterial, useCreateMaterial, useUpdateMaterial } from "@/infrastructure/services/service/admin/material.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { create } from "domain";

const props = defineProps({
  open: Boolean,
  MaterialDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allMaterialData: Array<MaterialResponse>,
});

const emit = defineEmits(["handleClose"]);

const { mutate: createMaterial } = useCreateMaterial();
const { mutate: updateMaterial } = useUpdateMaterial();

const modelRef = reactive<MaterialRequest>({
  ten: ""
});

const regString = /^-?\d+(\.\d+)?$/;

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên chất liệu", trigger: "blur" },
    {
      min: 1,
      max: 255,
      message: "Tên chất liệu phải từ 1 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        
        const allMaterialDatas = Array.isArray(props.allMaterialData) ? props.allMaterialData : [];
        if (props.MaterialDetail && props.MaterialDetail.ten.toLowerCase() === value.toLowerCase()) {
            return Promise.resolve();
        }
        const isNameExists = allMaterialDatas.some(
          (cate) => cate.ten.trim().toLowerCase() === value.trim().toLowerCase()
        );
        if (isNameExists) {
          return Promise.reject("Tên chất liệu đã tồn tại");
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
  props.MaterialDetail
    ? "Cập nhật chất liệu"
    : "Thêm chất liệu"
);

const okText = computed(() =>
  props.MaterialDetail
    ? "Cập nhật chất liệu"
    : "Thêm chất liệu"
);

watch(
  () => props.MaterialDetail,
  (newVal) => {
    if (newVal) {
      Object.assign(modelRef, {
        ten: newVal.ten
      });
      console.log(modelRef);
      console.log(newVal)
    } else {
      resetFields();
    }
  },
  { immediate: true }
);


// lấy danh sách chất liệu
const { data: categories } = useGetListMaterial({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listMaterial = computed(() => {
  return (
    categories?.value?.data?.map((material) => ({
      value: material.id,
      label: material.ten,
    })) || []
  );
});

// console.log(listMaterial.value);

const formFields = computed(() => [
  {
    label: "Tên chất liệu",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên chất liệu",
  }
]);

const handleAddOrUpdate = async () => {
  const payload = {
    ten: modelRef.ten
  };

  try {
    await validate();
    if (props.MaterialDetail) {
      updateMaterial({
        id: props.MaterialDetail.id,
        data: payload,
      });
      
    } else {
      createMaterial(payload);
      resetFields();
    }
    
    toast.success(
      props.MaterialDetail
        ? "Cập nhật chất liệu thành công"
        : "Thêm chất liệu thành công"
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