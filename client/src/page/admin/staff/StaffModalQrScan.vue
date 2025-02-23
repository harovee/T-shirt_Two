<template>
  <a-modal
      :open="props.open"
      title="Scan QR Code"
      @cancel="handleClose"
      @ok="handleQRScanStaffs"
      ok-text="Kiểm tra"
      cancel-text="Hủy"
      destroyOnClose
      centered
  >
    <a-form layout="vertical">
      <a-form-item>
        <qrcode-stream
            :constraints="selectedConstraints"
            :track="trackFunctionSelected.value"
            :formats="selectedBarcodeFormats"
            @error="onError"
            @detect="onDetect"
            @camera-on="onCameraReady"
        />
      </a-form-item>
      <transition
          enter-active-class="transition-opacity transition-transform duration-900 ease-out"
          enter-from-class="opacity-0 scale-95"
          enter-to-class="opacity-100 scale-100"
          leave-active-class="transition-opacity transition-transform duration-900 ease-in"
          leave-from-class="opacity-100 scale-100"
          leave-to-class="opacity-0 scale-95"
      >
        <div
            v-if="personData"
            class="p-3 m-0 bg-gray-300 rounded-xl shadow-lg"
        >
          <h3 class="text-center text-xl font-semibold text-gray-700">Thông tin QR Code</h3>
          <p>Tên: {{ personData.name }}</p>
          <p>Mã định danh cá nhân: {{ personData.identity }}</p>
          <p>Ngày Sinh: {{ personData.birthDay }}</p>
          <p>Giới Tính: {{ personData.gender }}</p>
        </div>
      </transition>
    </a-form>
  </a-modal>
</template>


<script setup lang="ts">
import {computed, createVNode, defineEmits, defineProps, ref} from "vue";
import {QrcodeStream} from "vue-qrcode-reader";
import {Modal, notification} from "ant-design-vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import {StaffQRRequest} from "@/infrastructure/services/api/admin/staff.api.ts";
import {useCreateStaffByQRCode} from "@/infrastructure/services/service/admin/staff.action.ts";

const props = defineProps({
  open: Boolean,
});

const emit = defineEmits(["handleClose"]);

const result = ref('')

const parseQRData = (qrString: string): StaffQRRequest | null => {
  try {
    const rawData = qrString.split("|");
    console.log(rawData);
    if (rawData.length < 7) {
      console.error("⚠️ Lỗi: Thiếu dữ liệu cần thiết");
      notification.warning({
        message: "⚠️ Cảnh Báo",
        description: "Vui lòng quét mã QR code thẻ căn cước công dân trong VNID",
        duration: 4,
      });
      return null;
    }

    let identity = rawData[0].trim();
    let name = rawData[2].trim();
    let birthDay = rawData[3].trim();
    const gender = rawData[4].trim();
    const address = rawData[5].trim();

    birthDay = formatDate(birthDay);

    const {line, ward, district, province} = parseAddress(address);

    return {name, birthDay, gender, identity, line, ward, district, province};
  } catch (error) {
    console.error("❌ Lỗi khi phân tích dữ liệu QR:", error);
    return null;
  }
};

const parseAddress = (address: string) => {
  const parts = address.split(",");

  if (parts.length < 3) {
    console.error("⚠️ Địa chỉ không đủ dữ liệu:", address);
    notification.warning({
      message: "⚠️ Cảnh Báo",
      description: "Vui lòng quét mã QR code thẻ căn cước công dân trong VNID",
      duration: 4,
    });
    return {line: "", ward: "", district: "", province: ""};
  }

  const line = parts[0].trim();
  const ward = parts[1].trim();
  const district = parts[2].trim();
  const province = parts.slice(3).join(",").trim();

  return {line, ward, district, province};
};

const formatDate = (dateStr: string): string => {
  if (dateStr.includes("/")) {
    return dateStr; // Giữ nguyên nếu đã có định dạng
  }
  return dateStr.replace(/(\d{2})(\d{2})(\d{4})/, "$1/$2/$3");
};

const personData = ref<StaffQRRequest | null>(null);

function onDetect(detectedCodes) {
  console.log("🚀 QR detected:", detectedCodes);
  if (!detectedCodes.length) return;
  const qrString = detectedCodes[0].rawValue;

  if (!qrString || typeof qrString !== "string") {
    console.error("⚠️ Lỗi: QR không chứa dữ liệu hợp lệ!");
    return;
  }

  personData.value = parseQRData(qrString);
  result.value = qrString;

  console.log("✅ result.value:", result.value);
  console.log("✅ personData.value:", personData.value);
}

/*** select camera ***/

const selectedConstraints = ref({facingMode: 'environment'})

const defaultConstraintOptions = [
  {label: 'rear camera', constraints: {facingMode: 'environment'}},
  {label: 'front camera', constraints: {facingMode: 'user'}}
]
const constraintOptions = ref(defaultConstraintOptions)

async function onCameraReady() {
  const devices = await navigator.mediaDevices.enumerateDevices()
  const videoDevices = devices.filter(({kind}) => kind === 'videoinput')

  constraintOptions.value = [
    ...defaultConstraintOptions,
    ...videoDevices.map(({deviceId, label}) => ({
      label: `${label} (ID: ${deviceId})`,
      constraints: {deviceId}
    }))
  ]

  error.value = ''
}

/*** track functons ***/

function paintOutline(detectedCodes, ctx) {
  for (const detectedCode of detectedCodes) {
    const [firstPoint, ...otherPoints] = detectedCode.cornerPoints

    ctx.strokeStyle = 'red'

    ctx.beginPath()
    ctx.moveTo(firstPoint.x, firstPoint.y)
    for (const {x, y} of otherPoints) {
      ctx.lineTo(x, y)
    }
    ctx.lineTo(firstPoint.x, firstPoint.y)
    ctx.closePath()
    ctx.stroke()
  }
}

function paintBoundingBox(detectedCodes, ctx) {
  for (const detectedCode of detectedCodes) {
    const {
      boundingBox: {x, y, width, height}
    } = detectedCode

    ctx.lineWidth = 2
    ctx.strokeStyle = '#007bff'
    ctx.strokeRect(x, y, width, height)
  }
}

function paintCenterText(detectedCodes, ctx) {
  for (const detectedCode of detectedCodes) {
    const {boundingBox, rawValue} = detectedCode

    const centerX = boundingBox.x + boundingBox.width / 2
    const centerY = boundingBox.y + boundingBox.height / 2

    const fontSize = Math.max(12, (50 * boundingBox.width) / ctx.canvas.width)

    ctx.font = `bold ${fontSize}px sans-serif`
    ctx.textAlign = 'center'

    ctx.lineWidth = 3
    ctx.strokeStyle = '#35495e'
    ctx.strokeText(detectedCode.rawValue, centerX, centerY)

    ctx.fillStyle = '#5cb984'
    ctx.fillText(rawValue, centerX, centerY)
  }
}

const trackFunctionOptions = [
  {text: 'nothing (default)', value: undefined},
  {text: 'outline', value: paintOutline},
  {text: 'centered text', value: paintCenterText},
  {text: 'bounding box', value: paintBoundingBox}
]
const trackFunctionSelected = ref(trackFunctionOptions[1])

/*** barcode formats ***/

const barcodeFormats = ref({
  aztec: false,
  code_128: false,
  code_39: false,
  code_93: false,
  codabar: false,
  databar: false,
  databar_expanded: false,
  data_matrix: false,
  dx_film_edge: false,
  ean_13: false,
  ean_8: false,
  itf: false,
  maxi_code: false,
  micro_qr_code: false,
  pdf417: false,
  qr_code: true,
  rm_qr_code: false,
  upc_a: false,
  upc_e: false,
  linear_codes: false,
  matrix_codes: false
})
const selectedBarcodeFormats = computed(() => {
  return Object.keys(barcodeFormats.value).filter((format) => barcodeFormats.value[format])
})

/*** error handling ***/

const error = ref('')

function onError(err) {
  error.value = `[${err.name}]: `

  if (err.name === 'NotAllowedError') {
    error.value += 'you need to grant camera access permission'
  } else if (err.name === 'NotFoundError') {
    error.value += 'no camera on this device'
  } else if (err.name === 'NotSupportedError') {
    error.value += 'secure context required (HTTPS, localhost)'
  } else if (err.name === 'NotReadableError') {
    error.value += 'is the camera already in use?'
  } else if (err.name === 'OverconstrainedError') {
    error.value += 'installed cameras are not suitable'
  } else if (err.name === 'StreamApiNotSupportedError') {
    error.value += 'Stream API is not supported in this browser'
  } else if (err.name === 'InsecureContextError') {
    error.value +=
        'Camera access is only permitted in secure context. Use HTTPS or localhost rather than HTTP.'
  } else {
    error.value += err.message
  }
}

const {mutate: scan} = useCreateStaffByQRCode();

const handleQRScanStaffs = () => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn kiểm tra thông tin trên? Hệ thống sẽ thêm mới thông tin của nhân viên có mã định danh trên với trạng thái ngừng hoạt động!!!",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        if (personData.value === null) {
          notification.warning({
            message: "⚠️ Cảnh Báo",
            description: "Bạn chưa quét thành công mã! Vui lòng quét mã QR của hệ thống VNID",
            duration: 4,
          });
          return;
        }
        scan(personData.value, {
          onSuccess: (res) => {
            if (res.status === "OK") {
              notification.warning({
                message: 'Thông báo',
                description: res?.message,
                duration: 4,
              });
            } else if (res.status === "CREATED") {
              notification.success({
                message: 'Thông báo',
                description: res?.message,
                duration: 4,
              });
            }
            handleClose();
          },
          onError: (error: any) => {
            notification.error({
              message: 'Thông báo',
              description: error?.response?.data?.message,
              duration: 4,
            });
          },
        });
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          notification.warning({
            message: 'Thông báo',
            description: error?.response?.data?.message,
            duration: 4,
          });
        } else if (error?.errorFields) {
          notification.warning({
            message: 'Thông báo',
            description: 'Vui lòng nhập đúng đủ các trường dữ liệu',
            duration: 4,
          });
        }
      }
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
    },
  });
}

const handleClose = () => {
  personData.value = null;
  emit("handleClose");
};
</script>
