<template>
  <div>
    <a-modal
      :open="props.open"
      title="Thêm sản phẩm"
      @cancel="handleClose"
      @ok="handleCreateProduct"
      ok-text="Thêm"
      cancel-text="Hủy"
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
            <div
              v-if="field.component === 'a-select'"
              style="display: flex; align-items: center"
            >
              <component
                :is="field.component"
                v-bind="field.props"
                v-model:value="modelRef[field.name]"
                :show-search="true"
                :filter-option="filterOption"
                :not-found-content="notFoundContent"
                @search="handleInput "
                style="flex: 1"
              />
            </div>
            <component
              :is="field.component"
              v-bind="field.props"
              v-model:value="modelRef[field.name]"
              v-else
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
  ref,
  h,
  watch,
  nextTick
} from "vue";
import { Form, message, Modal, Upload } from "ant-design-vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { toast } from "vue3-toastify";
import { warningNotiSort, successNotiSort, errorNotiSort } from "@/utils/notification.config";
import { useCreateProduct } from "@/infrastructure/services/service/admin/product.action";
import { ProductAddRequest } from "@/infrastructure/services/api/admin/product.api";
import { useGetListCategory, useCreateCategory } from "@/infrastructure/services/service/admin/category.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { CategoryRequest, CategoryResponse } from "@/infrastructure/services/api/admin/category.api";

const props = defineProps({
  open: Boolean,
});

const emit = defineEmits(["handleClose"]);

const { mutate: create } = useCreateProduct();

const modelRef = reactive<ProductAddRequest>({
  ten: null,
  moTa: null,
  idDanhMuc: "",
});

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên sản phẩm", trigger: "blur" },
  ],
});

const { resetFields, validate, validateInfos } = Form.useForm(
  modelRef,
  rulesRef
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

const filterOption = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const tempValue = ref("");

const handleInput = (value: string) => {
  tempValue.value = value
};

const notFoundContent = computed(() => {
  return h(
    'a',
    {
      style: 'color: #1890ff; cursor: pointer',
      onClick: handleAddNewCategory,
    },
    'Thêm danh mục mới'
  );
});


const { mutate: createCategory } = useCreateCategory();

const handleAddNewCategory = async () => {

  const payload = {
    ten: tempValue.value
  };

  try {

    await createCategory(payload);

    toast.success("Thêm danh mục thành công!");
  } catch (error) {
    console.error(error);
    toast.error("Có lỗi xảy ra khi thêm danh mục!");
  }
};

watch(listCategory, (newList) => {
    if (newList && newList.length > 0) {
      modelRef.idDanhMuc = newList[0].value;
    }
  // console.log(modelRef.idDanhMuc);
});

const formFields = computed(() => [
  {
    label: "Tên sản phẩm",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên sản phẩm",
  },
  {
    label: "Mô tả",
    name: "moTa",
    component: "a-textarea",
    placeholder: "Nhâp mô tả",
  },
  {
    label: "Danh mục",
    name: "idDanhMuc",
    component: "a-select",
    props: {
      placeholder: "Chọn danh mục",
      // đang làm đến đây mai làm lấy api danh mục
      options: listCategory.value,
    },
  },
]);

const handleCreateProduct = () => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn thêm?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await validate();
        create(modelRef, {
          onSuccess: (result) => {
            successNotiSort(result?.message);
            handleClose();
          },
          onError: (error: any) => {
            errorNotiSort(error?.response?.data?.message);
          },
        });
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          warningNotiSort(error?.response?.data?.message);
        } else if (error?.errorFields) {
          warningNotiSort("Vui lòng nhập đầy đủ các trường dữ liệu");
        }
      }
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
    },
  });
};

const handleClose = () => {
  emit("handleClose");
  resetFields();
};
</script>