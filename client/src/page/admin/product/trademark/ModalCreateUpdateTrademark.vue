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
import { TrademarkRequest, TrademarkResponse } from "@/infrastructure/services/api/admin/trademark.api";
import { useGetListTrademark, useCreateTrademark, useUpdateTrademark } from "@/infrastructure/services/service/admin/trademark.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { create } from "domain";

const props = defineProps({
  open: Boolean,
  TrademarkDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allTrademarkData: Array<TrademarkResponse>,
});

watch(
  () => props.TrademarkDetail,
  (newData) => console.log(newData)
);


const emit = defineEmits(["handleClose"]);

const { mutate: createTrademark } = useCreateTrademark();
const { mutate: updateTrademark } = useUpdateTrademark();

const modelRef = reactive<TrademarkRequest>({
  ten: ""
});

const regString = /^-?\d+(\.\d+)?$/;

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên thương hiệu", trigger: "blur" },
    {
      min: 1,
      max: 255,
      message: "Tên thương hiệu phải từ 1 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        
        const allTrademarkDatas = Array.isArray(props.allTrademarkData) ? props.allTrademarkData : [];
        if (props.TrademarkDetail && props.TrademarkDetail.ten.toLowerCase() === value.toLowerCase()) {
            return Promise.resolve();
        }
        const isNameExists = allTrademarkDatas.some(
          (cate) => cate.ten.trim().toLowerCase() === value.trim().toLowerCase()
        );
        if (isNameExists) {
          return Promise.reject("Tên thương hiệu đã tồn tại");
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
  props.TrademarkDetail
    ? "Cập nhật thương hiệu"
    : "Thêm thương hiệu"
);

const okText = computed(() =>
  props.TrademarkDetail
    ? "Cập nhật thương hiệu"
    : "Thêm thương hiệu"
);

watch(
  () => props.TrademarkDetail,
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

// lấy danh sách thương hiệu
const { data: categories } = useGetListTrademark({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listTrademark = computed(() => {
  return (
    categories?.value?.data?.map((trademark) => ({
      value: trademark.id,
      label: trademark.ten,
    })) || []
  );
});

// console.log(listTrademark.value);

const formFields = computed(() => [
  {
    label: "Tên thương hiệu",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên thương hiệu",
  }
]);

const handleAddOrUpdate = async () => {
  await validate();
  const payload = {
    ten: modelRef.ten
  };

  Modal.confirm({
    content: props.TrademarkDetail
            ? "Bạn chắc chắn muốn cập nhật?"
            : "Bạn chắc chắn muốn thêm mới?" ,
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,

    async onOk() {

  try {
    
    if (props.TrademarkDetail) {
      await updateTrademark({
        id: props.TrademarkDetail.id,
        data: payload,
      });
      
    } else {
      await createTrademark(payload);
      resetFields();
    }
    
    successNotiSort(
      props.TrademarkDetail
        ? "Cập nhật thương hiệu thành công"
        : "Thêm thương hiệu thành công"
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