<template>
  <div>
    <a-steps :current="current" class="step-interface">
      <a-step
        v-for="item in selectedSteps"
        :key="item.title"
        :title="item.title"
        :icon="item.icon"
        :loading="loading"
      >
        <template #title>
          <div class="step-title">{{ item.title }}</div>
        </template>
        <template #description>
          <div class="step-time">{{ item.time || "Chưa có thông tin" }}</div>
        </template>
      </a-step>
    </a-steps>
    <div class="steps-action">
      <div class="left-buttons">
        <a-button
          v-if="
            current == 0 &&
            statusIndexStart !== 'Đã hủy' &&
            dataPaymentInfo.refund > 0
          "
          type="primary"
          @click="confirmBillRefund()"
        >
          Xác nhận đơn và hoàn lại tiền
        </a-button>
        <a-button
          v-else-if="current == 0 && statusIndexStart !== 'Đã hủy'"
          type="primary"
          @click="confirmBill()"
        >
          Xác nhận đơn
        </a-button>

        <a-button v-if="current == 1" type="primary" @click="confirmDelivery()">
          Xác nhận lấy hàng
        </a-button>

        <a-button v-if="current == 2" type="primary" @click="confirmArrived()">
          Xác nhận đã giao hàng
        </a-button>

        <a-button
          v-if="current == 4"
          type="primary"
          @click="confirmCompleted()"
        >
          Hoàn thành
        </a-button>

        <a-button
          v-if="
            statusIndexStart !== 'Đã hủy' &&
            statusIndexStart !== 'Chờ xác nhận' &&
            statusIndexStart !== 'Thành công'
          "
          style="margin-left: 8px"
          @click="rollBack(statusIndexStart)"
        >
          Quay lại trạng thái trước
        </a-button>

        <a-button
          v-if="
            current == 0 &&
            statusIndexStart !== 'Đã hủy' &&
            dataPaymentInfo.paid > 0
          "
          danger
          style="margin-left: 10px"
          @click="handleCancelBillPaid"
        >
          Hủy đơn và hoàn lại tiền
        </a-button>
        <a-button
          v-else-if="current == 0 && statusIndexStart !== 'Đã hủy'"
          danger
          style="margin-left: 10px"
          @click="handleCancelBill"
        >
          Hủy đơn
        </a-button>
      </div>

      <div class="right-buttons">
        <a-button
          class="border border-orange-500 bg-transparent text-orange-500 hover:border-orange-300"
          style="margin-right: 15px"
          @click="showDetailModal"
        >
          Chi tiết
        </a-button>
      </div>
    </div>

    <!-- Modal lịch sử -->
    <a-modal
      style="width: 1000px"
      v-model:open="isModalVisible"
      title="Chi tiết lịch sử"
      @cancel="handleCancel"
      @ok="handleCancel"
    >
      <a-table
        :columns="columns"
        :data-source="props.dataSource?.data"
        :loading="loading"
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'ngayTao'">
            <span v-if="record?.ngayTao">
              {{ convertDateFormat(record.ngayTao) }}
            </span>
          </template>
          <template v-if="column.key === 'hanhDongChiTiet'">
            {{ record.nguoiTao }} {{ record.hanhDong }}
          </template>
          <template v-if="column.key === 'trangThai'">
            <a-tag>{{ record.trangThai }}</a-tag>
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, h, onMounted, computed, nextTick } from "vue";
import { useAuthStore } from "@/infrastructure/stores/auth";
import {
  CarOutlined,
  CheckCircleOutlined,
  FileTextOutlined,
  IssuesCloseOutlined,
} from "@ant-design/icons-vue";
import { convertDateFormat, formatCurrencyVND } from "@/utils/common.helper";
import {
  useChangeBillStatus,
  useGetBillById,
} from "@/infrastructure/services/service/admin/bill.action";
import {
  errorNotiSort,
  successNotiSort,
  warningNotiSort,
} from "@/utils/notification.config";
import { Input, Modal } from "ant-design-vue";
import { keepPreviousData } from "@tanstack/vue-query";
import { useGetPayHistory } from "@/infrastructure/services/service/admin/payhistory.action";
import { FindPayHistoryRequest } from "@/infrastructure/services/api/admin/pay-history.api";
import {
  useCheckQuantityInStock,
  useCheckQuantityListProduct,
  usePlusQuantityListProduct,
} from "@/infrastructure/services/service/admin/productdetail.action";
import { sum } from "lodash";
import { PropType } from "vue";

interface DataSource {
  data: {
    trangThai: string;
    ngayTao: number;
    hanhDong: string;
    nguoiTao: string;
  }[];
}

interface Step {
  title: string;
  time: string;
  icon: ReturnType<typeof h>;
}

// Props
const props = defineProps({
  dataSource: {
    type: Object as PropType<DataSource>,
  },
  loading: Boolean,
  dataPaymentInfo: Object,
  dataProduct: {
    type: Array,
    required: true,
  },
  billData: Object,
});

const listProduct = ref(null);

watch(
  () => props?.dataProduct,
  (newData) => {
    listProduct.value = newData.map((item) => ({
      id: item.idSanPhamChiTiet,
      quantity: item.soLuong,
    }));
  }
);

const emit = defineEmits(["update:bill"]);

const selectedSteps = computed(() => {
  return props.dataSource?.data?.[0]?.trangThai === "Đã hủy"
    ? stepsCancel
    : steps;
});

// Reactive state
const current = ref<number>(0);
const isModalVisible = ref(false);

const { mutate: changeStatus } = useChangeBillStatus();

const { mutate: plusQuantityListProduct } = usePlusQuantityListProduct();

const { mutate: checkQuantity, data, error } = useCheckQuantityListProduct();

// Khai báo các bước khi hủy hóa đơn thành công
const stepsCancel: Step[] = [
  {
    title: "Đã hủy",
    time: "",
    icon: h(IssuesCloseOutlined),
  },
];

// Khai báo các bước
const steps: Step[] = [
  {
    title: "Chờ xác nhận",
    time: "",
    icon: h(IssuesCloseOutlined),
  },
  {
    title: "Chờ giao hàng",
    time: "",
    icon: h(IssuesCloseOutlined),
  },
  {
    title: "Đang vận chuyển",
    time: "",
    icon: h(CarOutlined),
  },
  {
    title: "Đã giao hàng",
    time: "",
    icon: h(CheckCircleOutlined),
  },
  {
    title: "Đã thanh toán",
    time: "",
    icon: h(FileTextOutlined),
  },
  {
    title: "Thành công",
    time: "",
    icon: h(CheckCircleOutlined),
  },
];

onMounted(() => {
  if (props.dataSource?.data?.length > 0) {
    updateCurrentStep(props.dataSource);
    updateStepTimes(props.dataSource);
  }
});

const statusIndexStart = ref("");

const updateCurrentStep = (dataSource: DataSource) => {
  const status = dataSource?.data?.[0]?.trangThai;
  statusIndexStart.value = dataSource?.data?.[0]?.trangThai;
  if (status === "Đã hủy") {
    const statusMap: Record<string, number> = {
      "Đã hủy": 0,
    };
    current.value = statusMap[status] || 0;
  } else {
    const statusMap: Record<string, number> = {
      "Chờ xác nhận": 0,
      "Chờ giao hàng": 1,
      "Đang vận chuyển": 2,
      "Đã giao hàng": 3,
      "Đã thanh toán": 4,
      "Thành công": 5,
    };
    current.value = statusMap[status] || 0;
  }
};

const updateStepTimes = (dataSource: DataSource) => {
  if (!dataSource?.data || dataSource.data.length === 0) return;

  selectedSteps.value.forEach((step) => {
    const records = dataSource.data.filter(
      (item) => item.trangThai === step.title
    );

    if (records.length > 0) {
      records.sort((a, b) => b.ngayTao - a.ngayTao);
      step.time = convertDateFormat(records[0].ngayTao);
    } else {
      step.time = "Chưa có thông tin";
    }
  });
};

// Watch để cập nhật time khi dataSource thay đổi
watch(
  () => props.dataSource,
  (newValue) => {
    // console.log("📊 dataSource updated:", newValue);
    updateCurrentStep(newValue);
    updateStepTimes(newValue);
  },
  { deep: true, immediate: true }
);

// Các hàm điều hướng giữa các bước
// Hàm để lấy idBill từ URL
const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

const idBill = getIdHoaDonFromUrl();
const billId = ref<string | null>(null);

const params = ref<FindPayHistoryRequest>({
  idHoaDon: "",
});

onMounted(() => {
  params.value.idHoaDon = getIdHoaDonFromUrl();
  billId.value = getIdHoaDonFromUrl();
});

//dữ liệu lshd
const { data: PaymentData } = useGetPayHistory(params, {
  refetchOnWindowClose: false,
  placeholderData: keepPreviousData,
});

// console.log(PaymentData);

const { data: billData } = useGetBillById(billId, {
  refetchOnWindowFocus: false,
  enabled: () => !!billId.value,
});

watch(
  () => billData.value?.data?.data,
  (newData) => {
    // console.log(newData);
  },
  { immediate: true }
);

// hàm xác nhận không hoàn tiền
const confirmBill = async () => {
  const payload = {
    soDienThoai: props.billData.soDienThoai,
    diaChiNguoiNhan: props.billData.diaChiNguoiNhan,
    tenNguoiNhan: props.billData.tenNguoiNhan,
    ghiChu: props.billData.ghiChu,
    tinh: props.billData.tinh,
    huyen: props.billData.huyen,
    xa: props.billData.xa,
    idPhieuGiamGia: props.billData.idPhieuGiamGia,
    tienShip: props.dataProduct[0]?.tienShip || 0,
    tienGiam: props.dataProduct[0]?.tienGiamHD || 0,
    tongTien: props.dataProduct[0]?.tongTienHD || 0,
  };

  // Lấy trạng thái tiếp theo từ mảng steps
  const nextStep = steps[current.value + 1];
  const stepTitle = nextStep.title;

  // API tạo lịch sử hóa đơn
  const params = {
    status: stepTitle, // Trạng thái mới từ bước tiếp theo
    trangThai: "Chờ giao hàng",
    email: props.billData.emailNguoiNhan || null,
    idHoaDon: idBill,
    nhanVien: useAuthStore().user?.email || null,
    ghiChu: "Xác nhận trạng thái đơn hàng -> Chờ giao hàng",
  };

  const check = ref(null);

  if (props.billData.loaiHD === "Online") {
    checkQuantity(listProduct.value, {
      onSuccess: (result) => {
        if (result.data !== undefined) {
          check.value = result.data;
          if (check.value === true) {
            warningNotiSort(
              "Số lượng sản phẩm trong kho không đủ, vui lòng kiểm tra lại!"
            );
            return;
          }
          // modal confirm có thay đổi trạng thái k
          Modal.confirm({
            title: "Xác nhận thay đổi trạng thái",
            content: `Bạn muốn thay đổi trạng thái của đơn hàng này sang "${stepTitle}"?`,
            onOk: async () => {
              try {
                if (props.dataProduct.length === 0) {
                  warningNotiSort(
                    "Đơn hàng đang trống, xin vui lòng thêm sản phẩm vào giỏ hoặc hủy đơn hàng!"
                  );
                  return;
                }
                // Gọi API để thay đổi trạng thái đơn hàng
                changeStatus({ idBill, params });
                emit("update:bill");
                successNotiSort("Cập nhật trạng thái thành công!");

                // Sau khi cập nhật trạng thái thành công, di chuyển đến bước tiếp theo
                current.value++;
              } catch (error) {
                console.error("Cập nhật trạng thái thất bại:", error);
                errorNotiSort(
                  "Cập nhật trạng thái thất bại. Vui lòng thử lại."
                );
              }
            },
            onCancel: () => {
              console.log("Thao tác đã bị hủy.");
            },
          });
        }
      },
      onError: (error: any) => {
        errorNotiSort(error?.response?.data?.message);
      },
    });
  } else {
    Modal.confirm({
      title: "Xác nhận thay đổi trạng thái",
      content: `Bạn muốn thay đổi trạng thái của đơn hàng này sang "${stepTitle}"?`,
      onOk: async () => {
        try {
          if (props.dataProduct.length === 0) {
            warningNotiSort(
              "Đơn hàng đang trống, xin vui lòng thêm sản phẩm vào giỏ hoặc hủy đơn hàng!"
            );
            return;
          }
          // Gọi API để thay đổi trạng thái đơn hàng
          changeStatus({ idBill, params });
          emit("update:bill");
          successNotiSort("Cập nhật trạng thái thành công!");

          // Sau khi cập nhật trạng thái thành công, di chuyển đến bước tiếp theo
          current.value++;
        } catch (error) {
          console.error("Cập nhật trạng thái thất bại:", error);
          errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
        }
      },
      onCancel: () => {
        console.log("Thao tác đã bị hủy.");
      },
    });
  }
};

// hàm xác nhận và hoàn lại tiền
const confirmBillRefund = () => {
  // Lấy trạng thái tiếp theo từ mảng steps
  const nextStep = steps[current.value + 1];
  const stepTitle = nextStep.title;
  const description = ref("");
  // API tạo lịch sử hóa đơn

  Modal.confirm({
    title: "Xác nhận hoàn tiền",
    content: () => {
      return h("div", [
        h(
          "p",
          `Số tiền cần trả lại: ${formatCurrencyVND(
            props.dataPaymentInfo.refund
          )}`
        ),
        h(Input, {
          placeholder: "Nhập mã giao dịch ...",
          autoSize: { minRows: 2, maxRows: 4 },
          onInput: (e) => (description.value = e.target.value),
        }),
      ]);
    },
    onOk: async () => {
      const params = {
        status: stepTitle,
        trangThai: "Chờ giao hàng",
        moTa: `Hoàn trả: ${formatCurrencyVND(
          props.dataPaymentInfo.refund
        )} - Mã giao dịch: ${description.value}`,
        email: props.billData.emailNguoiNhan || null,
        idHoaDon: idBill,
        nhanVien: useAuthStore().user?.email || null,
        ghiChu: `Xác nhận trạng thái đơn hàng -> Chờ giao hàng và hoàn trả ${formatCurrencyVND(
          props.dataPaymentInfo.refund
        )}`,
      };
      try {
        // Gọi API để thay đổi trạng thái đơn hàng
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");
        emit("update:bill");
        // Sau khi cập nhật trạng thái thành công, di chuyển đến bước tiếp theo
        current.value++;
      } catch (error) {
        console.error("Cập nhật trạng thái thất bại:", error);
        errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
      }
    },
    onCancel: () => {
      console.log("Thao tác đã bị hủy.");
    },
  });
};

const confirmDelivery = () => {
  // Lấy trạng thái tiếp theo từ mảng steps
  const nextStep = steps[current.value + 1];
  const stepTitle = nextStep.title;

  // Chuẩn bị tham số cho API
  const params = {
    status: stepTitle, // Trạng thái mới từ bước tiếp theo
    trangThai: "Đang vận chuyển",
    email: props.billData.emailNguoiNhan || null,
    idHoaDon: idBill,
    nhanVien: useAuthStore().user?.email || null,
    ghiChu: "Xác nhận trạng thái đơn hàng -> Đang vận chuyển",
  };

  Modal.confirm({
    title: "Xác nhận thay đổi trạng thái",
    content: `Bạn muốn xác nhận giao hàng cho đơn này"?`,
    onOk: async () => {
      try {
        // Gọi API để thay đổi trạng thái đơn hàng
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");

        // Sau khi cập nhật trạng thái thành công, di chuyển đến bước tiếp theo
        current.value++;
      } catch (error) {
        console.error("Cập nhật trạng thái thất bại:", error);
        errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
      }
    },
    onCancel: () => {
      console.log("Thao tác đã bị hủy.");
    },
  });
};

const confirmArrived = () => {
  const tienKhachDua =
    PaymentData.value?.data?.reduce(
      (sum, payment) => sum + payment.tienKhachDua,
      0
    ) || 0;
  // console.log(tienKhachDua);

  // const tongTienHD =
  // console.log(tongTienHD);
  if (props.dataPaymentInfo.amountPayable == 0) {
    // Nếu khách đã thanh toán đủ -> Chuyển trực tiếp sang trạng thái "Đã thanh toán"
    const nextStep = steps[4]; // "Đã thanh toán"
    const stepTitle = nextStep.title;

    const params = {
      status: stepTitle,
      trangThai: "Đã thanh toán",
      email: props.billData.emailNguoiNhan || null,
      idHoaDon: idBill,
      nhanVien: useAuthStore().user?.email || null,
      ghiChu: "Xác nhận trạng thái đơn hàng -> Đã thanh toán",
    };

    Modal.confirm({
      title: "Xác nhận đơn hàng đã thanh toán",
      content: `Khách đã thanh toán đủ ${tienKhachDua.toLocaleString()} VND. Chuyển trạng thái sang "${stepTitle}"?`,
      onOk: async () => {
        try {
          changeStatus({ idBill, params });
          successNotiSort(
            "Đơn hàng đã chuyển sang trạng thái 'Đã thanh toán'!"
          );
          current.value = 4; // Cập nhật trạng thái về 'Đã thanh toán'
        } catch (error) {
          console.error("Cập nhật trạng thái thất bại:", error);
          errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
        }
      },
    });
  } else {
    warningNotiSort(
      ` Cần thanh toán ${formatCurrencyVND(
        props.dataPaymentInfo.amountPayable
      )} để chuyển trạng thái hóa đơn!`
    );
    return;
  }

  // Nếu chưa đủ tiền, chỉ chuyển sang trạng thái tiếp theo bình thường
  const nextStep = steps[current.value + 1];
  const stepTitle = nextStep.title;

  const params = {
    status: stepTitle,
    trangThai: "Đã giao hàng",
    email: props.billData.emailNguoiNhan || null,
    idHoaDon: idBill,
    nhanVien: useAuthStore().user?.email || null,
    ghiChu: "Xác nhận trạng thái đơn hàng -> Đã giao hàng",
  };

  Modal.confirm({
    title: "Xác nhận giao hàng",
    content: `Bạn có muốn xác nhận giao hàng cho đơn này không?`,
    onOk: async () => {
      try {
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");
        current.value++;
      } catch (error) {
        console.error("Cập nhật trạng thái thất bại:", error);
        errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
      }
    },
  });
};

const confirmCompleted = () => {
  // Lấy trạng thái tiếp theo từ mảng steps
  const nextStep = steps[current.value + 1];
  const stepTitle = nextStep.title;

  // API tạo lịch sử hóa đơn
  const params = {
    status: stepTitle, // Trạng thái mới từ bước tiếp theo
    trangThai: "Thành công",
    email: props.billData.emailNguoiNhan || null,
    idHoaDon: idBill,
    nhanVien: useAuthStore().user?.email || null,
    ghiChu: "Xác nhận trạng thái đơn hàng -> Thành công. Hoàn tất đơn hàng.",
  };

  Modal.confirm({
    title: "Xác nhận thay đổi trạng thái",
    content: `Bạn muốn thay đổi trạng thái của đơn hàng này sang "${stepTitle}"?`,
    onOk: async () => {
      try {
        // Gọi API để thay đổi trạng thái đơn hàng
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");

        // Sau khi cập nhật trạng thái thành công, di chuyển đến bước tiếp theo
        current.value++;
      } catch (error) {
        console.error("Cập nhật trạng thái thất bại:", error);
        errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
      }
    },
    onCancel: () => {
      console.log("Thao tác đã bị hủy.");
    },
  });
};

// Hàm hủy đơn chưa thanh toán
const handleCancelBill = () => {
  const nextStep = stepsCancel[current.value];
  const stepTitle = nextStep.title;
  const description = ref("");

  Modal.confirm({
    title: "Xác nhận hủy đơn",
    content: () => {
      return h("div", [
        h("p", `Bạn có chắc chắn muốn hủy đơn hàng này không?`),
        h(Input.TextArea, {
          placeholder: "Nhập lý do ...",
          autoSize: { minRows: 2, maxRows: 4 },
          onInput: (e) => (description.value = e.target.value),
        }),
      ]);
    },
    onOk: async () => {
      if (!description.value || !description.value.trim()) {
        errorNotiSort("Vui lòng nhập lý do quay lại");
        return Promise.reject();
      }

      const params = {
        status: stepTitle,
        trangThai: "Đã hủy",
        moTa: description.value,
        email: props.billData.emailNguoiNhan || null,
        idHoaDon: idBill,
        nhanVien: useAuthStore().user?.email || null,
        ghiChu: "Đơn hàng đã bị hủy!",
      };

      try {
        await changeStatus({ idBill, params });
        successNotiSort("Đã hủy đơn thành công!");
        current.value++;
      } catch (error) {
        console.error("Cập nhật trạng thái thất bại:", error);
        errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
      }
    },
    onCancel: () => console.log("Thao tác rollback bị hủy."),
  });
};

// Hàm hủy đơn đã thanh toán
const handleCancelBillPaid = () => {
  const nextStep = stepsCancel[current.value];
  const stepTitle = nextStep.title;
  const description = ref("");

  Modal.confirm({
    title: "Xác nhận hoàn tiền",
    content: () => {
      return h("div", [
        h(
          "p",
          `Số tiền cần trả lại: ${formatCurrencyVND(
            props.dataPaymentInfo.paid
          )}`
        ),
        h(Input, {
          placeholder: "Nhập mã giao dịch ...",
          autoSize: { minRows: 2, maxRows: 4 },
          onInput: (e) => (description.value = e.target.value),
        }),
      ]);
    },
    onOk: async () => {
      if (!description.value || !description.value.trim()) {
        errorNotiSort("Vui lòng nhập mã giao dịch!");
        return Promise.reject();
      }

      const params = {
        status: stepTitle,
        trangThai: "Đã hủy",
        moTa: `Hoàn trả: ${formatCurrencyVND(
          props.dataPaymentInfo.paid
        )} - Mã giao dịch: ${description.value}`,
        email: props.billData.emailNguoiNhan || null,
        idHoaDon: idBill,
        nhanVien: useAuthStore().user?.email || null,
        ghiChu: `Đơn hàng đã bị hủy và hoàn trả ${formatCurrencyVND(
          props.dataPaymentInfo.paid
        )}`,
      };

      try {
        await changeStatus({ idBill, params });
        successNotiSort("Đã hủy đơn thành công!");

        // const stepIndex = steps.findIndex((step) => step.title === stepTitle);
        // if (stepIndex !== -1) {
        //   steps[stepIndex].time = new Date().toLocaleString("vi-VN", {
        //     hour12: false,
        //   });
        // }

        // // Quay lại trạng thái trước
        // current.value--;
        current.value++;
      } catch (error) {
        console.error("Cập nhật trạng thái thất bại:", error);
        errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
      }
    },
    onCancel: () => console.log("Thao tác rollback bị hủy."),
  });
};

const rollBack = (stepStatus: string) => {
  if (current.value > 0) {
    const prevStep = steps[current.value - 1];
    const stepTitle = prevStep.title;
    const description = ref();

    Modal.confirm({
      title: "Xác nhận quay lại trạng thái trước",
      content: () => {
        return h("div", [
          h(
            "p",
            `Bạn có chắc chắn muốn quay lại trạng thái "${stepTitle}" không?`
          ),
          h(Input.TextArea, {
            placeholder: "Nhập lý do quay lại...",
            autoSize: { minRows: 2, maxRows: 4 },
            onChange: (e) => (description.value = e.target.value),
          }),
        ]);
      },
      onOk: async () => {
        if (!description.value || !description.value.trim()) {
          errorNotiSort("Vui lòng nhập lý do quay lại");
          return Promise.reject();
        }

        const params = {
          status: stepTitle,
          trangThai: stepTitle,
          moTa: description.value,
        };

        try {
          await changeStatus({ idBill, params });
          successNotiSort(`Trạng thái đã quay lại: ${stepTitle}`);
          // Hoàn lại số lượng
          if (
            props.billData.loaiHD === "Online" &&
            stepStatus === "Chờ giao hàng"
          ) {
            plusQuantityListProduct(
              { params: listProduct.value },
              {
                onSuccess: (result) => {
                  console.log("Đã hoàn lại số lượng của hóa đơn onl");
                },
                onError: (error: any) => {
                  console.log("Lỗi khi hoàn số lượng");
                },
              }
            );
          }

          // 🔄 Cập nhật lại thời gian của trạng thái rollback
          const stepIndex = steps.findIndex((step) => step.title === stepTitle);
          if (stepIndex !== -1) {
            steps[stepIndex].time = new Date().toLocaleString("vi-VN", {
              hour12: false,
            });
          }

          // Quay lại trạng thái trước
          current.value--;
        } catch (error) {
          console.error("Cập nhật trạng thái thất bại:", error);
          errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
        }
      },
      onCancel: () => console.log("Thao tác rollback bị hủy."),
    });
  }
};

const showDetailModal = () => {
  isModalVisible.value = true;
};

const handleCancel = () => {
  isModalVisible.value = false;
};

// Cột của bảng Modal
const columns = [
  {
    title: "Trạng thái",
    dataIndex: "trangThai",
    key: "trangThai",
    align: "center",
  },
  {
    title: "Thời gian",
    dataIndex: "ngayTao",
    key: "ngayTao",
    align: "center",
  },
  {
    title: "Hành động",
    dataIndex: "hanhDongChiTiet",
    key: "hanhDongChiTiet",
    align: "center",
  },
  {
    title: "Mô tả",
    dataIndex: "moTa",
    key: "moTa",
    align: "center",
  },
];
</script>

<style scoped>
.steps-action {
  display: flex;
  justify-content: space-between; /* Căn hai nhóm nút ra hai bên */
  align-items: center;
  padding-top: 20px;
  margin-top: 20px;
}

.left-buttons {
  display: flex;
  gap: 10px; /* Tạo khoảng cách giữa các nút */
}

.right-buttons {
  display: flex;
}

.ant-modal-body {
  max-height: 350px;
  overflow-y: auto;
}
.step-interface {
  border: 1px solid #cbd0d4; /* Border màu xanh dương (có thể thay đổi theo ý thích) */
  border-radius: 5px; /* Bo góc để các góc mềm mại */
  padding: 20px; /* Thêm khoảng cách trong thẻ */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
.step-title {
  font-weight: bold;
  margin-bottom: 8px;
}
.step-time {
  font-size: 12px;
  color: #666;
}
</style>
