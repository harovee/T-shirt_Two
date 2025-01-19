<template>
  <div>
    <a-modal
      :open="props.open"
      title="Cập nhật sản phẩm"
      @cancel="handleClose"
      @ok="handleUpdateProduct"
      ok-text="Cập nhật"
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
import { useUpdateProduct } from "@/infrastructure/services/service/admin/product.action";
import { ProductRequest, ProductResponse } from "@/infrastructure/services/api/admin/product.api";
import { useGetListCategory } from "@/infrastructure/services/service/admin/category.action";
import { keepPreviousData } from "@tanstack/vue-query";

const props = defineProps({
  open: Boolean,
  ProductDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
  allProductData: Array<ProductResponse>,
});

// watch(
//   () => props.ProductDetail,
//   (newData) => console.log(newData)
// );

const emit = defineEmits(["handleClose"]);

const { mutate: updateProduct } = useUpdateProduct();

const modelRef = reactive<ProductRequest>({
  ten: null,
  moTa: null,
  trangThai: null,
  idDanhMuc: null,
});

const rulesRef = reactive({
  ten: [
    { required: true, message: "Vui lòng nhập tên sản phẩm", trigger: "blur" },
    {
      min: 5,
      max: 255,
      message: "Tên sản phẩm phải từ 5 đến 255 ký tự",
      trigger: "blur",
    },
    {
      validator: (_, value) => {
        if (props.ProductDetail && props.ProductDetail.ten.toLowerCase() === value.toLowerCase()) {
            return Promise.resolve();
        }
        const isNameExists = props.allProductData?.some(
          (pro) => pro.ten.toLowerCase() === value.toLowerCase()
        );
        if (isNameExists) {
          return Promise.reject("Tên sản phẩm đã tồn tại");
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

watch(
  () => props.ProductDetail,
  (newVal) => {
    if (newVal) {
      Object.assign(modelRef, {
        ten: newVal.ten,
        moTa: newVal.moTa,
        trangThai: newVal?.trangThai === "ACTIVE" ? 0 : 1,
        idDanhMuc: newVal?.danhMuc?.id,
      });
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
    label: "Trạng thái",
    name: "trangThai",
    component: "a-radio-group",
    props: {
      options: [
        { label: "Đang áp dụng", value: 0 },
        { label: "Ngưng áp dụng", value: 1 },
      ],
      value: 0,
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

const handleUpdateProduct = () => {
  const payload = {
    ten: modelRef.ten,
    moTa: modelRef.moTa,
    trangThai: modelRef.trangThai,
    idDanhMuc: modelRef.idDanhMuc,
  };

  Modal.confirm({
    content: "Bạn chắc chắn muốn sửa?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,

    async onOk() {
      try {
        await validate();
        updateProduct({
          id: props.ProductDetail.id,
          params: payload,
        });
        toast.success("Cập nhật sản phẩm thành công");
        handleClose();
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          toast.warning(error?.response?.data?.message);
        }
      }
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
      resetFields();
    },
  });
};

const handleClose = () => {
  emit("handleClose");
  resetFields();
};
</script>