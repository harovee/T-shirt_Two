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
import {
  CategoryRequest,
  CategoryResponse,
} from "@/infrastructure/services/api/admin/category.api";
import {
  useGetListCategory,
  useCreateCategory,
  useUpdateCategory,
} from "@/infrastructure/services/service/admin/category.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { create } from "domain";

const props = defineProps({
  open: Boolean,
  CategoryDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allCategoryData: Array<CategoryResponse>,
});

watch(
  () => props.CategoryDetail,
  (newData) => console.log(newData)
);

const emit = defineEmits(["handleClose"]);

const { mutate: createCategory } = useCreateCategory();
const { mutate: updateCategory } = useUpdateCategory();

const modelRef = reactive<CategoryRequest>({
  ten: "",
});

const regString = /^-?\d+(\.\d+)?$/;

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên danh mục", trigger: "blur" },
    {
      min: 1,
      max: 255,
      message: "Tên danh mục phải từ 1 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        const allCategoryDatas = Array.isArray(props.allCategoryData)
          ? props.allCategoryData
          : [];
        if (
          props.CategoryDetail &&
          props.CategoryDetail.ten.toLowerCase() === value.toLowerCase()
        ) {
          return Promise.resolve();
        }
        const isNameExists = allCategoryDatas.some(
          (cate) => cate.ten.trim().toLowerCase() === value.trim().toLowerCase()
        );
        if (isNameExists) {
          return Promise.reject("Tên danh mục đã tồn tại");
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
  props.CategoryDetail ? "Cập nhật danh mục" : "Thêm danh mục"
);

const okText = computed(() =>
  props.CategoryDetail ? "Cập nhật danh mục" : "Thêm danh mục"
);

watch(
  () => props.CategoryDetail,
  (newVal) => {
    if (newVal) {
      Object.assign(modelRef, {
        ten: newVal.ten,
      });
      console.log(modelRef);
    } else {
      resetFields();
    }
  },
  { immediate: true }
);

// lấy danh sách danh mục
const { data: categories } = useGetListCategory({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listCategory = computed(() => {
  return (
    categories?.value?.data?.map((category) => ({
      value: category.id,
      label: category.ten,
    })) || []
  );
});

// console.log(listCategory.value);

const formFields = computed(() => [
  {
    label: "Tên danh mục",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên danh mục",
  },
]);

const handleAddOrUpdate = async () => {
  const payload = {
    ten: modelRef.ten,
  };
  Modal.confirm({
    content: props.CategoryDetail
            ? "Bạn chắc chắn muốn cập nhật?"
            : "Bạn chắc chắn muốn thêm mới?" ,
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,

    async onOk() {
      try {
        await validate();
        if (props.CategoryDetail) {
          await updateCategory({
            id: props.CategoryDetail.id,
            data: payload,
          });
        } else {
          await createCategory(payload);
          resetFields();
        }

        successNotiSort(
          props.CategoryDetail
            ? "Cập nhật danh mục thành công"
            : "Thêm danh mục thành công"
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