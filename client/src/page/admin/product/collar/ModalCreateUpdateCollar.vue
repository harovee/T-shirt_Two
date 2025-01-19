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
import { CollarRequest, CollarResponse } from "@/infrastructure/services/api/admin/collar.api";
import { useGetListCollar, useCreateCollar, useUpdateCollar } from "@/infrastructure/services/service/admin/collar.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { create } from "domain";

const props = defineProps({
  open: Boolean,
  CollarDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allCollarData: Array<CollarResponse>,
});

watch(
  () => props.CollarDetail,
  (newData) => console.log(newData)
);


const emit = defineEmits(["handleClose"]);

const { mutate: createCollar } = useCreateCollar();
const { mutate: updateCollar } = useUpdateCollar();

const modelRef = reactive<CollarRequest>({
  ten: ""
});

const regString = /^-?\d+(\.\d+)?$/;

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên cổ áo", trigger: "blur" },
    {
      min: 1,
      max: 255,
      message: "Tên cổ áo phải từ 1 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        
        const allCollarDatas = Array.isArray(props.allCollarData) ? props.allCollarData : [];
        if (props.CollarDetail && props.CollarDetail.ten.toLowerCase() === value.toLowerCase()) {
            return Promise.resolve();
        }
        const isNameExists = allCollarDatas.some(
          (cate) => cate.ten.trim().toLowerCase() === value.trim().toLowerCase()
        );
        if (isNameExists) {
          return Promise.reject("Tên cổ áo đã tồn tại");
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
  props.CollarDetail
    ? "Cập nhật cổ áo"
    : "Thêm cổ áo"
);

const okText = computed(() =>
  props.CollarDetail
    ? "Cập nhật cổ áo"
    : "Thêm cổ áo"
);

watch(
  () => props.CollarDetail,
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

// lấy danh sách cổ áo
const { data: categories } = useGetListCollar({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listCollar = computed(() => {
  return (
    categories?.value?.data?.map((Collar) => ({
      value: Collar.id,
      label: Collar.ten,
    })) || []
  );
});

// console.log(listCollar.value);

const formFields = computed(() => [
  {
    label: "Tên cổ áo",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên cổ áo",
  }
]);

const handleAddOrUpdate = async () => {
  const payload = {
    ten: modelRef.ten
  };

  try {
    await validate();
    if (props.CollarDetail) {
      await updateCollar({
        id: props.CollarDetail.id,
        data: payload,
      });
      
    } else {
      await createCollar(payload);
      resetFields();
    }
    
    toast.success(
      props.CollarDetail
        ? "Cập nhật cổ áo thành công"
        : "Thêm cổ áo thành công"
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