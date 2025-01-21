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
import { PatternRequest, PatternResponse } from "@/infrastructure/services/api/admin/pattern.api";
import { useGetListPattern, useCreatePattern, useUpdatePattern } from "@/infrastructure/services/service/admin/pattern.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { create } from "domain";

const props = defineProps({
  open: Boolean,
  PatternDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allPatternData: Array<PatternResponse>,
});

watch(
  () => props.PatternDetail,
  (newData) => console.log(newData)
);


const emit = defineEmits(["handleClose"]);

const { mutate: createPattern } = useCreatePattern();
const { mutate: updatePattern } = useUpdatePattern();

const modelRef = reactive<PatternRequest>({
  ten: ""
});

const regString = /^-?\d+(\.\d+)?$/;

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên họa tiết", trigger: "blur" },
    {
      min: 1,
      max: 255,
      message: "Tên họa tiết phải từ 1 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        
        const allPatternDatas = Array.isArray(props.allPatternData) ? props.allPatternData : [];
        if (props.PatternDetail && props.PatternDetail.ten.toLowerCase() === value.toLowerCase()) {
            return Promise.resolve();
        }
        const isNameExists = allPatternDatas.some(
          (cate) => cate.ten.trim().toLowerCase() === value.trim().toLowerCase()
        );
        if (isNameExists) {
          return Promise.reject("Tên họa tiết đã tồn tại");
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
  props.PatternDetail
    ? "Cập nhật họa tiết"
    : "Thêm họa tiết"
);

const okText = computed(() =>
  props.PatternDetail
    ? "Cập nhật họa tiết"
    : "Thêm họa tiết"
);

watch(
  () => props.PatternDetail,
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

// lấy danh sách họa tiết
const { data: categories } = useGetListPattern({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listPattern = computed(() => {
  return (
    categories?.value?.data?.map((pattern) => ({
      value: pattern.id,
      label: pattern.ten,
    })) || []
  );
});

// console.log(listPattern.value);

const formFields = computed(() => [
  {
    label: "Tên họa tiết",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên họa tiết",
  }
]);

const handleAddOrUpdate = async () => {
  const payload = {
    ten: modelRef.ten
  };

  Modal.confirm({
    content: props.PatternDetail
            ? "Bạn chắc chắn muốn cập nhật?"
            : "Bạn chắc chắn muốn thêm mới?" ,
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,

    async onOk() {

  try {
    await validate();
    if (props.PatternDetail) {
      await updatePattern({
        id: props.PatternDetail.id,
        data: payload,
      });
      
    } else {
      await createPattern(payload);
      resetFields();
    }
    
    successNotiSort(
      props.PatternDetail
        ? "Cập nhật họa tiết thành công"
        : "Thêm họa tiết thành công"
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