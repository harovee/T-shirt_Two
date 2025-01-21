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
import { SleeveRequest, SleeveResponse } from "@/infrastructure/services/api/admin/sleeve.api";
import { useGetListSleeve, useCreateSleeve, useUpdateSleeve } from "@/infrastructure/services/service/admin/sleeve.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { create } from "domain";

const props = defineProps({
  open: Boolean,
  SleeveDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allSleeveData: Array<SleeveResponse>,
});

watch(
  () => props.SleeveDetail,
  (newData) => console.log(newData)
);


const emit = defineEmits(["handleClose"]);

const { mutate: createSleeve } = useCreateSleeve();
const { mutate: updateSleeve } = useUpdateSleeve();

const modelRef = reactive<SleeveRequest>({
  ten: ""
});

const regString = /^-?\d+(\.\d+)?$/;

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên tay áo", trigger: "blur" },
    {
      min: 1,
      max: 255,
      message: "Tên tay áo phải từ 1 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        
        const allSleeveDatas = Array.isArray(props.allSleeveData) ? props.allSleeveData : [];
        if (props.SleeveDetail && props.SleeveDetail.ten.toLowerCase() === value.toLowerCase()) {
            return Promise.resolve();
        }
        const isNameExists = allSleeveDatas.some(
          (cate) => cate.ten.trim().toLowerCase() === value.trim().toLowerCase()
        );
        if (isNameExists) {
          return Promise.reject("Tên tay áo đã tồn tại");
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
  props.SleeveDetail
    ? "Cập nhật tay áo"
    : "Thêm tay áo"
);

const okText = computed(() =>
  props.SleeveDetail
    ? "Cập nhật tay áo"
    : "Thêm tay áo"
);

watch(
  () => props.SleeveDetail,
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

// lấy danh sách tay áo
const { data: categories } = useGetListSleeve({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listsleeve = computed(() => {
  return (
    categories?.value?.data?.map((sleeve) => ({
      value: sleeve.id,
      label: sleeve.ten,
    })) || []
  );
});

// console.log(listSleeve.value);

const formFields = computed(() => [
  {
    label: "Tên tay áo",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên tay áo",
  }
]);

const handleAddOrUpdate = async () => {
  const payload = {
    ten: modelRef.ten
  };

  Modal.confirm({
    content: props.SleeveDetail
            ? "Bạn chắc chắn muốn cập nhật?"
            : "Bạn chắc chắn muốn thêm mới?" ,
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,

    async onOk() {

  try {
    await validate();
    if (props.SleeveDetail) {
      await updateSleeve({
        id: props.SleeveDetail.id,
        data: payload,
      });
      
    } else {
      await createSleeve(payload);
      resetFields();
    }
    
    successNotiSort(
      props.SleeveDetail
        ? "Cập nhật tay áo thành công"
        : "Thêm tay áo thành công"
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