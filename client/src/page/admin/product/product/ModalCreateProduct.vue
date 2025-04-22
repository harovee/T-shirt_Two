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
                @search="handleInput"
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
import { useCreateProduct, useGetListProduct } from "@/infrastructure/services/service/admin/product.action";
import { ProductAddRequest } from "@/infrastructure/services/api/admin/product.api";
import { useGetListCategory, useCreateCategory } from "@/infrastructure/services/service/admin/category.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { CategoryRequest, CategoryResponse } from "@/infrastructure/services/api/admin/category.api";

const props = defineProps({
  open: Boolean,
});

const emit = defineEmits(["handleClose"]);

const { mutate: create } = useCreateProduct();

const { data: products } = useGetListProduct({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const allProducts = computed(() => {
  return products?.value?.data || [];
});

const modelRef = reactive<ProductAddRequest>({
  ten: null,
  moTa: null,
  idDanhMuc: "",
});

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên sản phẩm", trigger: "blur" },
    {
      min: 1,
      max: 255,
      message: "Tên sản phẩm phải từ 1 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
          if (!value) {
            return Promise.resolve();
          }
          const specialCharRegex = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/;
          if (specialCharRegex.test(value)) {
            return Promise.reject("Tên sản phẩm không được chứa ký tự đặc biệt");
          }

          const existingProducts = allProducts.value;
          const isNameExists = existingProducts.some(
            (product) => product.ten && value && 
            product.ten.trim().toLowerCase() === value.trim().toLowerCase()
          );
          
          if (isNameExists) {
            return Promise.reject("Tên sản phẩm đã tồn tại");
          }
          return Promise.resolve();
        },
      },
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

const allCategories = computed(() => {
  return categories?.value?.data || [];
});

const filterOption = (input: string, option: any) => {
  return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const tempValue = ref("");

const handleInput = (value: string) => {
  tempValue.value = value;
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

const validateCategoryName = (categoryName: string): { isValid: boolean, message?: string } => {
  if (!categoryName || categoryName.trim() === '') {
    return { isValid: false, message: "Tên danh mục không được để trống" };
  }
  if (categoryName.length < 1 || categoryName.length > 255) {
    return { isValid: false, message: "Tên danh mục phải từ 1 đến 255 ký tự" };
  }
  
  const specialCharRegex = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/;
  if (specialCharRegex.test(categoryName)) {
    return { isValid: false, message: "Tên danh mục không được chứa ký tự đặc biệt" };
  }
  
  const existingCategories = allCategories.value;
  const isNameExists = existingCategories.some(
    (category) => category.ten.trim().toLowerCase() === categoryName.trim().toLowerCase()
  );
  
  if (isNameExists) {
    return { isValid: false, message: "Tên danh mục đã tồn tại" };
  }
  
  return { isValid: true };
};

const handleAddNewCategory = async () => {
  const validationResult = validateCategoryName(tempValue.value);
  
  if (!validationResult.isValid) {
    errorNotiSort(validationResult.message || "Dữ liệu danh mục không hợp lệ");
    return;
  }

  const payload: CategoryRequest = {
    ten: tempValue.value
  };

  try {
    await createCategory(payload, {
      onSuccess: (result) => {
        successNotiSort(result?.message || "Thêm danh mục thành công!");
      },
      onError: (error: any) => {
        warningNotiSort(error?.response?.data?.message || "Có lỗi xảy ra khi thêm danh mục!");
      },
    });
  } catch (error) {
    console.error(error);
    errorNotiSort("Có lỗi xảy ra khi thêm danh mục!");
  }
};

watch(listCategory, (newList) => {
  if (newList && newList.length > 0) {
    modelRef.idDanhMuc = newList[0].value;
  }
});

const formFields = computed(() => [
  {
    label: "Tên sản phẩm",
    name: "ten",
    component: "a-input",
    props: {
      placeholder: "Nhập tên sản phẩm",
    },
  },
  {
    label: "Mô tả",
    name: "moTa",
    component: "a-textarea",
    props: {
      placeholder: "Nhập mô tả",
    },
  },
  {
    label: "Danh mục",
    name: "idDanhMuc",
    component: "a-select",
    props: {
      placeholder: "Chọn danh mục",
      options: listCategory.value,
    },
  },
]);

const handleCreateProduct = async () => {
  try {
    await validate();
    Modal.confirm({
      content: "Bạn chắc chắn muốn thêm?",
      icon: createVNode(ExclamationCircleOutlined),
      centered: true,
      async onOk() {
        try {
          create(modelRef, {
            onSuccess: (result) => {
              successNotiSort(result?.message || "Thêm sản phẩm thành công");
              handleClose();
            },
            onError: (error: any) => {
              errorNotiSort(error?.response?.data?.message || "Có lỗi xảy ra khi thêm sản phẩm");
            },
          });
        } catch (error: any) {
          console.error("🚀 ~ handleCreate ~ error:", error);
          if (error?.response) {
            warningNotiSort(error?.response?.data?.message || "Có lỗi xảy ra");
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
  } catch (error) {
    console.error("Validation error:", error);
  }
};

const handleClose = () => {
  emit("handleClose");
  resetFields();
};
</script>