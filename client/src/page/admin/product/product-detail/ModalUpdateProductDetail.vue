<template>
  <div>
    <a-modal
      :open="props.open"
      title="Cập nhật sản phẩm chi tiết"
      @cancel="handleClose"
      @ok="handleUpdateProductDetail"
      ok-text="Cập nhật"
      cancel-text="Hủy"
      destroyOnClose
      centered
    >
      <a-form
        layout="vertical"
        class="mt-10 grid grid-cols-3 gap-4 md:grid-cols-1 lg:grid-cols-3"
      >
        <template v-for="field in formFields">
          <a-form-item
            class="col-span-1 md:col-span-1 lg:col-span-1"
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
      <!-- Canvas hiển thị mã QR -->
      <canvas
        ref="qrcodeCanvasRef"
        style="width: 200px; height: 200px"
      ></canvas>
      <a-button
        type="primary"
        class="mt-5"
        @click="downloadQr(ProductDetail.maSPCT)"
        >Tải mã QR</a-button
      >
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import {
  computed,
  onMounted,
  createVNode,
  defineEmits,
  defineProps,
  reactive,
  watch,
  inject,
  ref, nextTick
} from "vue";
import { Form, message, Modal, Upload } from "ant-design-vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { toast } from "vue3-toastify";
import { warningNotiSort, successNotiSort } from "@/utils/notification.config";
import { useUpdateProductDetail } from "@/infrastructure/services/service/admin/productdetail.action";
import { ProductDetailUpdateRequest } from "@/infrastructure/services/api/admin/product_detail.api";
import { useGetListCategory } from "@/infrastructure/services/service/admin/category.action";
import { keepPreviousData } from "@tanstack/vue-query";
import QRCode from "qrcode-generator";

const props = defineProps({
  open: Boolean,
  ProductDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
});

const listProduct = inject ('listProduct');
const listMaterial = inject('listMaterial');
const listCollar = inject('listCollar');
const listSleeve = inject('listSleeve');
const listTrademark = inject('listTrademark');
const listColor = inject('listColor');
const listFeature = inject('listFeature');
const listPattern = inject('listPattern');
const listSize = inject('listSize');
const listStyle = inject('listStyle');

watch(
  () => props.ProductDetail,
  (newData) => console.log(newData)
);

const emit = defineEmits(["handleClose"]);

const { mutate: updateProductDetail } = useUpdateProductDetail();

const modelRef = reactive<ProductDetailUpdateRequest>({
    gia: null,
    soLuong:null,
    idChatLieu: null,
    idCoAo: null,
    idHoaTiet: null,
    idMauSac: null,
    idKichCo: null,
    idKieuDang: null,
    idTayAo: null,
    idThuongHieu: null,
    idTinhNang: null,
    idSanPham: null,
    trangThai: null
});

const rulesRef = reactive({
  
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
        idChatLieu: newVal?.chatLieu?.id,
        idThuongHieu: newVal?.thuongHieu?.id,
        idCoAo: newVal?.coAo?.id,
        idTayAo: newVal?.tayAo?.id,
        idTinhNang: newVal?.tinhNang?.id,
        idHoaTiet: newVal?.hoaTiet?.id,
        idKieuDang: newVal?.kieuDang?.id,
        idMauSac: newVal?.mauSac?.id,
        idKichCo: newVal?.kichCo?.id,
        idSanPham: newVal?.sanPham?.id,
        gia: newVal?.gia,
        soLuong: newVal?.soLuong,
        trangThai: newVal?.trangThai === "ACTIVE" ? 0 : 1
      });
    } else {
      resetFields();
    }
  },
  { immediate: true }
);

const formFields = computed(() => [
  {
    label: "Chất liệu",
    name: "idChatLieu",
    component: "a-select",
    props: {
      placeholder: "Chọn chất liệu",
      options: listMaterial,
    },
  },
  {
    label: "Thương hiệu",
    name: "idThuongHieu",
    component: "a-select",
    props: {
      placeholder: "Chọn thương hiệu",
      options: listTrademark,
    },
  },
  {
    label: "Cổ áo",
    name: "idCoAo",
    component: "a-select",
    props: {
      placeholder: "Chọn cổ áo",
      options: listCollar,
    },
  },
  {
    label: "Tay áo",
    name: "idTayAo",
    component: "a-select",
    props: {
      placeholder: "Chọn tay áo",
      options: listSleeve,
    },
  },
  {
    label: "Kiểu dáng",
    name: "idKieuDang",
    component: "a-select",
    props: {
      placeholder: "Chọn kiểu dáng",
      options: listStyle,
    },
  },
  {
    label: "Họa tiết",
    name: "idHoaTiet",
    component: "a-select",
    props: {
      placeholder: "Chọn họa tiết",
      options: listPattern,
    },
  },
  {
    label: "Tính năng",
    name: "idTinhNang",
    component: "a-select",
    props: {
      placeholder: "Chọn tính năng",
      options: listFeature,
    },
  },
  {
    label: "Màu sắc",
    name: "idMauSac",
    component: "a-select",
    props: {
      placeholder: "Chọn màu sắc",
      options: listColor,
    },
  },
  {
    label: "Kích cỡ",
    name: "idKichCo",
    component: "a-select",
    props: {
      placeholder: "Chọn kích cỡ",
      options: listSize,
    },
  },
  {
    label: "Giá",
    name: "gia",
    component: "a-input-number",
    props: {
      placeholder: "Nhập giá",
      class: "w-full",
      min: 0
    },
  },
  {
    label: "Số lượng",
    name: "soLuong",
    component: "a-input-number",
    props: {
      placeholder: "Nhập số lượng",
      class: "w-full",
      min: 0
    },
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
  
]);

const handleUpdateProductDetail = () => {
  const payload = {
    gia: modelRef.gia,
    soLuong: modelRef.soLuong,
    idChatLieu: modelRef.idChatLieu,
    idCoAo: modelRef.idCoAo,
    idHoaTiet: modelRef.idHoaTiet,
    idMauSac: modelRef.idMauSac,
    idKichCo: modelRef.idKichCo,
    idKieuDang: modelRef.idKieuDang,
    idTayAo: modelRef.idTayAo,
    idThuongHieu: modelRef.idThuongHieu,
    idTinhNang: modelRef.idTinhNang,
    idSanPham: modelRef.idSanPham,
    trangThai: modelRef.trangThai
  };

  Modal.confirm({
    content: "Bạn chắc chắn muốn cập nhật?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,

    async onOk() {
      try {
        await validate();
        updateProductDetail({
          id: props.ProductDetail.id,
          params: payload,
        });
        successNotiSort("Cập nhật sản phẩm chi tiết thành công");
        handleClose();
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          warningNotiSort(error?.response?.data?.message);
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

const qrcodeCanvasRef = ref(null);

// Tạo Qr hiển thị lên
const generateQr = (maSPCT) => {
  if (!qrcodeCanvasRef.value) return;

  const qr = QRCode(0, "H");
  qr.addData(maSPCT);
  qr.make();

  const canvas = qrcodeCanvasRef.value;
  const ctx = canvas.getContext("2d");

  const size = 200;
  canvas.width = size;
  canvas.height = size;

  ctx.clearRect(0, 0, size, size);
  ctx.fillStyle = "#fff";
  ctx.fillRect(0, 0, size, size);

  const cellSize = size / qr.getModuleCount();
  for (let r = 0; r < qr.getModuleCount(); r++) {
    for (let c = 0; c < qr.getModuleCount(); c++) {
      ctx.fillStyle = qr.isDark(r, c) ? "#000" : "#fff";
      ctx.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
    }
  }
};

const downloadQr = (maSPCT) => {
  if (!maSPCT) {
    console.error("Mã sản phẩm trống!");
    return;
  }

  const qr = QRCode(0, "H");
  qr.addData(maSPCT);
  qr.make();

  const size = 1024;
  const padding = 50;
  const qrSize = size - 2 * padding;

  const canvas = document.createElement("canvas");
  const ctx = canvas.getContext("2d");

  canvas.width = size;
  canvas.height = size;

  ctx.fillStyle = "#fff";
  ctx.fillRect(0, 0, size, size);

  const cellSize = qrSize / qr.getModuleCount();
  for (let r = 0; r < qr.getModuleCount(); r++) {
    for (let c = 0; c < qr.getModuleCount(); c++) {
      ctx.fillStyle = qr.isDark(r, c) ? "#000" : "#fff";
      ctx.fillRect(padding + c * cellSize, padding + r * cellSize, cellSize, cellSize);
    }
  }

  // Tạo link tải ảnh PNG
  const link = document.createElement("a");
  link.href = canvas.toDataURL("image/png");
  link.download = `${maSPCT}.png`;
  link.click();
};

onMounted(() => {
  if (props.ProductDetail) {
    generateQr(props.ProductDetail.maSPCT)
  }
})
watch(
  () => props.ProductDetail, 
  (newVal) => {
    if (newVal) {
      generateQr(newVal.maSPCT);
    }
  }
);

  // -----------------------------------------------
</script>

<style scoped>
.custom-modal .ant-modal-header {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>