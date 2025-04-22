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
      width="900px"
     
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

      <div class="flex flex-wrap gap-6 mt-6">
        <!-- QR Code Section -->
        <div class="qr-section flex-1">
          <h3 class="mb-2 font-medium">Mã QR</h3>
          <div class="flex flex-col items-center">
            <canvas
              ref="qrcodeCanvasRef"
              style="width: 200px; height: 200px"
            ></canvas>
            <a-button
              type="primary"
              class="mt-2"
              @click="downloadQr(ProductDetail.maSPCT)"
            >Tải mã QR</a-button>
          </div>
        </div>

        <!-- Images Section -->
        <div class="images-section flex-1">
          <h3 class="mb-2 font-medium">Hình ảnh sản phẩm</h3>
          <div v-if="isLoadingImages">
            <a-spin />
          </div>
          <div v-else class="flex flex-wrap gap-2">
            <!-- Existing Images -->
            <div
              v-for="(image, index) in productImages"
              :key="index"
              class="relative"
            >
              <img
                :src="image.url"
                alt="Product image"
                class="w-24 h-24 object-cover rounded border border-gray-200"
              />
              <div class="absolute top-0 right-0 flex">
                <a-button
                  type="primary"
                  danger
                  size="small"
                  class="flex items-center justify-center w-6 h-6 p-0"
                  @click="handleDeleteImage(image.id, index)"
                >
                  <v-icon name="fa-trash" style="font-size: 12px"></v-icon>
                </a-button>
              </div>
            </div>

            <!-- Upload New Image Button -->
            <div class="w-24 h-24">
              <a-button
                @click="openCloudinaryWidget"
                class="w-full h-full flex items-center justify-center border border-dashed border-gray-300"
                v-if="productImages.length <4"
              >
                <v-icon name="co-plus" style="font-size: 16px"></v-icon>
                <span class="ml-1">Thêm</span>
              </a-button>
            </div>
          </div>
        </div>
      </div>
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
  ref, 
  nextTick
} from "vue";
import { Form, message, Modal, Upload } from "ant-design-vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { toast } from "vue3-toastify";
import { warningNotiSort, successNotiSort, errorNotiSort } from "@/utils/notification.config";
import { useUpdateProductDetail } from "@/infrastructure/services/service/admin/productdetail.action";
import { ProductDetailUpdateRequest, anh } from "@/infrastructure/services/api/admin/product_detail.api";
import { useGetListCategory } from "@/infrastructure/services/service/admin/category.action";
import { keepPreviousData } from "@tanstack/vue-query";
import QRCode from "qrcode-generator";
import {
  CLOUDINARY_CLOUD_NAME,
  CLOUDINARY_UPLOAD_PRESET,
} from "@/infrastructure/constants/cloudinary";
import axios from "axios";

const props = defineProps({
  open: Boolean,
  ProductDetail: Object as () => any | null,
  isLoadingDetail: Boolean,
});

const listProduct = inject('listProduct');
const listMaterial = inject('listMaterial');
const listCollar = inject('listCollar');
const listSleeve = inject('listSleeve');
const listTrademark = inject('listTrademark');
const listColor = inject('listColor');
const listFeature = inject('listFeature');
const listPattern = inject('listPattern');
const listSize = inject('listSize');
const listStyle = inject('listStyle');

const emit = defineEmits(["handleClose", "refreshData"]);

const { mutate: updateProductDetail } = useUpdateProductDetail();
const productImages = ref<anh[]>([]);
const isLoadingImages = ref(false);

watch(()=> props.ProductDetail, (newValue) =>{
  console.log(newValue);
  
} )

// Initialize Cloudinary widget
let cloudinaryWidget;

onMounted(() => {
  cloudinaryWidget = window.cloudinary.createUploadWidget(
    {
      cloudName: CLOUDINARY_CLOUD_NAME,
      uploadPreset: CLOUDINARY_UPLOAD_PRESET,
    },
    (error, result) => {
      if (!error && result && result.event === "success") {
        const imageUrl = result.info.url;
        const imageName = result.info.original_filename;
        
        // Add the new image to productImages
        productImages.value.push({
          url: imageUrl,
          name: imageName
        });
        updateModelRefImages();
      }
    }
  );
});

const updateModelRefImages = () => {
  modelRef.listAnh = [...productImages.value];
};

const openCloudinaryWidget = () => {
  if (cloudinaryWidget) {
    cloudinaryWidget.open();
  }
};

const fetchProductImages = async () => {
  if (!props.ProductDetail || !props.ProductDetail.id) return;
  
  isLoadingImages.value = true;
  try {
    // Get images by product detail ID
    const response = await axios.get(`http://localhost:6868/api/v1/admin/image`, {
      params: {
        idSanPhamChiTiet: props.ProductDetail.id
      }
    });
    productImages.value = response.data.data || [];
    updateModelRefImages();
  } catch (error) {
    console.error("Error fetching product images:", error);
    errorNotiSort("Không thể tải hình ảnh sản phẩm");
  } finally {
    isLoadingImages.value = false;
  }
};

const handleDeleteImage = async (imageId, index) => {
  try {
    if (imageId) {
      // If the image has an ID, it's stored in the database
      await axios.delete(`http://localhost:6868/api/v1/admin/image/${imageId}`);
    }
    
    // Remove from local array
    productImages.value.splice(index, 1);
    updateModelRefImages();
    successNotiSort("Xóa ảnh thành công");
  } catch (error) {
    console.error("Error deleting image:", error);
    errorNotiSort("Không thể xóa ảnh");
  }
};

const modelRef = reactive<ProductDetailUpdateRequest>({
    gia: null,
    soLuong: null,
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
    trangThai: null,
    listAnh: [],
    gioiTinh: null
});

const rulesRef = reactive({
  // You can add validation rules here if needed
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
        trangThai: newVal?.trangThai === "ACTIVE" ? 0 : 1,
        gioiTinh: newVal?.gioiTinh
      });
      
      // Fetch images when product detail changes
      fetchProductImages();
      nextTick(() => {
        generateQr(newVal.maSPCT);
      });
    } else {
      resetFields();
      productImages.value = [];
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
        { label: "Đang bán", value: 0 },
        { label: "Ngưng bán", value: 1 },
      ],
      value: 0,
    },
  },
  {
    label: "Giới tính",
    name: "gioiTinh",
    component: "a-radio-group",
    props: {
      options: [
        { label: "Nam", value: "Nam" },
        { label: "Nữ", value: "Nữ" },
        { label: "Nam và nữ ", value: "Nam và Nữ" },
      ],
    },
  },
]);

const handleUpdateProductDetail = () => {
  // Update the listAnh in modelRef with current images
  updateModelRefImages();
  
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
    trangThai: modelRef.trangThai,
    listAnh: modelRef.listAnh,
    gioiTinh: modelRef.gioiTinh
  };
  console.log(payload);

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
        emit("refreshData");
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
  productImages.value = [];
};

const qrcodeCanvasRef = ref(null);

// Tạo Qr hiển thị lên
const generateQr = (maSPCT) => {
  if (!qrcodeCanvasRef.value || !maSPCT) return;

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
</script>

<style scoped>
.custom-modal .ant-modal-header {
  display: flex;
  justify-content: center;
  align-items: center;
}

.qr-section, .images-section {
  min-width: 300px;
}
</style>