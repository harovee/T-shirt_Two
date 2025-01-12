<template>
    <div>
        <a-modal :open="props.open" title="Thêm sản phẩm" @cancel="handleClose" @ok="handleCreateProduct" ok-text="Thêm" cancel-text="Hủy"
            destroyOnClose centered>
                <a-form layout="vertical">
                    <template v-for="field in formFields">
                        <a-form-item :label="field.label" :name="field.name" v-bind="validateInfos[field.name]">
                            <component :is="field.component" v-bind="field.props" v-model:value="modelRef[field.name]">
                            </component>
                        </a-form-item>
                    </template>
                </a-form>
        </a-modal>
    </div>
</template>

<script setup lang="ts">
import {computed, createVNode, defineEmits, defineProps, reactive} from "vue";
import {Form, message, Modal, Upload} from "ant-design-vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import {toast} from "vue3-toastify";
import {useCreateProduct} from "@/infrastructure/services/service/admin/product.action";
import {ProductRequest} from "@/infrastructure/services/api/admin/product.api";
import {useGetListCategory} from "@/infrastructure/services/service/admin/category.action";
import {keepPreviousData} from "@tanstack/vue-query";

const props = defineProps({
  open: Boolean,
});

const emit = defineEmits(["handleClose"]);

const {mutate: create} = useCreateProduct();

const modelRef = reactive<ProductRequest>({
  ten: null,
  moTa: null,
  trangThai: null,
  idDanhMuc:null
});

const rulesRef = reactive({
  ten: [{required: true, message: "Vui lòng nhập tên sản phẩm", trigger: "blur"}]
});

const {resetFields, validate, validateInfos} = Form.useForm(
    modelRef,
    rulesRef
);

// lấy danh sách danh mục
const {data: categories} = useGetListCategory({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listCategory = computed(() => {
  return categories?.value?.data?.map(category => ({
    value: category.id,
    label: category.ten,
  })) || [];
});

// console.log(listCategory.value);



const formFields = computed(() => [
  {
    label: "Tên sản phẩm",
    name: "ten",
    component: "a-input",
    placeholder: "Nhâp tên sản phẩm"
  },
  {
    label: "Mô tả",
    name: "moTa",
    component: "a-textarea",
    placeholder: "Nhâp mô tả"
  },
  {
    label: "Trạng thái",
    name: "trangThai",
    component: "a-radio-group",
    props: {
      options: [
        { label: "Đang áp dụng", value: 0},
        { label: "Ngưng áp dụng", value: 1},
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
            toast.success(result?.message);
            handleClose();
          },
          onError: (error: any) => {
            toast.error(
                error?.response?.data?.message
            );
          },
        });
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          toast.warning(
              error?.response?.data?.message
          );
        } else if (error?.errorFields) {
          toast.warning("Vui lòng nhập đầy đủ các trường dữ liệu");
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