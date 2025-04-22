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
      <!-- <component
              :is="field.component"
              v-bind="field.props"
              v-model:value="modelRef[field.name]"
            >
            </component> -->
      <a-form layout="vertical">
        <template v-for="field in formFields">
          <a-form-item
            v-if="field.name === 'maMauSac'"
            :label="field.label"
            :name="field.name"
            v-bind="validateInfos[field.name]"
          >
            <input type="color" 
              v-bind="field.props"
              v-model="modelRef[field.name]">{{modelRef[field.name]}}
          </a-form-item>
          <a-form-item
            :label="field.label"
            :name="field.name"
            v-bind="validateInfos[field.name]"
            v-else
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
import {
  warningNotiSort,
  successNotiSort,
  errorNotiSort,
} from "@/utils/notification.config";
import { Form, message, Modal, Upload } from "ant-design-vue";
import Antd from "ant-design-vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { toast } from "vue3-toastify";
import {
  ColorRequest,
  ColorResponse,
} from "@/infrastructure/services/api/admin/color.api";
import {
  useGetListColor,
  useCreateColor,
  useUpdateColor,
} from "@/infrastructure/services/service/admin/color.action";
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
  maMauSac: null,
  ten: "",
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
        const specialCharRegex = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/;
        if (specialCharRegex.test(value)) {
          return Promise.reject("Tên màu sắc không được chứa ký tự đặc biệt");
        }
        const allColorDatas = Array.isArray(props.allColorData)
          ? props.allColorData
          : [];
        if (
          props.ColorDetail &&
          props.ColorDetail.ten.toLowerCase() === value.toLowerCase()
        ) {
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
  props.ColorDetail ? "Cập nhật màu sắc" : "Thêm màu sắc"
);

const okText = computed(() =>
  props.ColorDetail ? "Cập nhật màu sắc" : "Thêm màu sắc"
);

watch(
  () => props.ColorDetail,
  (newVal) => {
    if (newVal) {
      Object.assign(modelRef, {
        maMauSac: newVal.maMauSac,
        ten: newVal.ten,
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
    label: "Mã màu sắc",
    name: "maMauSac",
    component: "a-color-picker",
    placeholder: "Nhâp mã màu sắc",
  },
  {
    label: "Tên màu sắc",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên màu sắc",
  },
]);

const handleAddOrUpdate = async () => {
  await validate();
  const payload = {
    maMauSac: modelRef.maMauSac,
    ten: modelRef.ten,
  };

  Modal.confirm({
    content: props.ColorDetail
      ? "Bạn chắc chắn muốn cập nhật?"
      : "Bạn chắc chắn muốn thêm mới?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,

    async onOk() {
      try {
        
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
        warningNotiSort(error?.response?.data?.message);
      }
    },
  });
};

const handleClose = () => {
  emit("handleClose");
  resetFields();
};
</script>

<style scoped>
input[type="color"] {
  width: 100%; /* Chiếm full chiều rộng của a-form-item */
  height: 40px; /* Chiều cao của input */
  border: 1px solid #d9d9d9; /* Đường viền nhẹ */
  border-radius: 4px; /* Góc bo tròn */
  padding: 0; /* Bỏ padding mặc định */
  outline: none; /* Xóa viền focus mặc định */
  cursor: pointer; /* Thêm hiệu ứng chuột khi hover */
  transition: all 0.3s ease; /* Hiệu ứng chuyển động mượt mà */
}

/* Thêm hiệu ứng khi hover vào input[type=color] */
input[type="color"]:hover {
  border-color: #40a9ff; /* Thay đổi màu viền khi hover */
}

/* Thêm hiệu ứng khi input[type=color] được focus */
input[type="color"]:focus {
  border-color: #1890ff; /* Màu viền khi focus */
  box-shadow: 0 0 5px rgba(24, 144, 255, 0.2); /* Hiệu ứng shadow khi focus */
}
</style>