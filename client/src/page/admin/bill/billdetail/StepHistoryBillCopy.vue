<template v-if="isDataReady">
  <div>
    <div style="overflow-x: auto; white-space: nowrap">
      <a-steps
        :current="uniqueStepsTmp.length"
        class="step-interface"
        direction="horizontal"
        style="min-width: max-content"
      >
        <a-step
          v-for="(item, index) in uniqueStepsTmp"
          :key="index"
          :title="item?.title"
          :icon="item.icon"
        >
          <template #title>
            <div class="step-title">{{ item.title }}</div>
          </template>
          <template #description>
            <div class="step-time">{{ item.time || "Chưa có thông tin" }}</div>
          </template>
        </a-step>
      </a-steps>
    </div>

    <div class="steps-action">
      <div class="left-buttons">
        <a-button
          v-if="
            stepsTmp.length > 0 &&
            stepsTmp[stepsTmp.length - 1]?.title === 'Chờ xác nhận' &&
            dataPaymentInfo.refund > 0
          "
          type="primary"
          @click="confirmBillRefund()"
        >
          Xác nhận đơn và hoàn lại tiền
        </a-button>
        <a-button
          v-if="
            stepsTmp.length > 0 &&
            stepsTmp[stepsTmp.length - 1]?.title === 'Chờ xác nhận'
          "
          type="primary"
          @click="confirmBill()"
        >
          Xác nhận đơn
        </a-button>

        <a-button
          v-if="stepsTmp[stepsTmp.length - 1]?.title === 'Chờ giao hàng'"
          type="primary"
          @click="confirmDelivery()"
        >
          Xác nhận lấy hàng
        </a-button>

        <a-button
          v-if="stepsTmp[stepsTmp.length - 1]?.title === 'Đang vận chuyển'"
          type="primary"
          @click="confirmArrived()"
        >
          Xác nhận đã giao hàng
        </a-button>

        <a-button
          v-if="stepsTmp[stepsTmp.length - 1]?.title === 'Đã thanh toán'"
          type="primary"
          @click="confirmCompleted()"
        >
          Hoàn thành
        </a-button>

        <!-- Quay lại trạng thái -->
        <a-button
          v-if="
            stepsTmp[stepsTmp.length - 1]?.title !== 'Đã hủy' &&
            stepsTmp[stepsTmp.length - 1]?.title !== 'Chờ xác nhận' &&
            stepsTmp[stepsTmp.length - 1]?.title !== 'Thành công'
          "
          style="margin-left: 8px"
          @click="rollBack(stepsTmp[stepsTmp.length - 1]?.title)"
        >
          Quay lại trạng thái trước
        </a-button>

        <a-button
          v-if="
            stepsTmp[stepsTmp.length - 1]?.title === 'Chờ xác nhận' &&
            dataPaymentInfo.paid > 0
          "
          danger
          style="margin-left: 10px"
          @click="handleCancelBillPaid"
        >
          Hủy đơn và hoàn lại tiền
        </a-button>
        <a-button
          v-else-if="stepsTmp[stepsTmp.length - 1]?.title === 'Chờ xác nhận'"
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
        <a-button
          v-if="stepsTmp[stepsTmp.length - 1]?.title === 'Thành công'"
          class="border border-orange-500 bg-transparent text-orange-500 hover:border-orange-300"
          style="margin-right: 15px"
          @click="createInvoicePdf"
        >
          Xuất hóa đơn
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
      :scroll="{ x: 'max-content', y: 600 }"
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
            {{ record.hanhDong }}
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
import {
  ref,
  watch,
  h,
  onMounted,
  computed,
  nextTick,
  createVNode,
  watchEffect,
} from "vue";
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
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import { createInvoicePdfWithId } from "@/infrastructure/services/api/admin/payment.api";

interface DataSource {
  data: {
    trangThai: string;
    ngayTao: number;
    hanhDong: string;
    nguoiTao: string;
  }[];
}

interface Product {
  catalog: number | null;
  tenMau: string | null;
  tenKichCo: string | null;
  tenSanPham: string | null;
  gia: number | null;
  soLuong: number | null;
}

interface ProductTemp {
  idSanPhamChiTiet: string | null,
  soLuong: number | null
}

// Props
const props = defineProps({
  dataSource: {
    type: Object as PropType<DataSource>,
  },
  loading: { type: Boolean, default: true },
  dataPaymentInfo: Object,
  dataProduct: {
    type: Array,
    required: true,
  },
  billData: Object,
});

const isLoading = ref(true);

const stepsTmp = ref([
  { title: "Chờ xác nhận", time: "2025-04-27 10:00" },
  { title: "Đang giao", time: "2025-04-27 15:00" },
]);

const currentCopy = ref(stepsTmp.value.length);

const listProduct = ref(null);

watch(
  () => props?.dataProduct,
  (newData) => {
    listProduct.value = newData.map((item: ProductTemp) => ({
      id: item.idSanPhamChiTiet,
      quantity: item.soLuong,
    }));
  }
);

const emit = defineEmits(["update:bill"]);

const isModalVisible = ref(false);

const { mutate: changeStatus } = useChangeBillStatus();

const { mutate: plusQuantityListProduct } = usePlusQuantityListProduct();

const { mutate: checkQuantity, data, error } = useCheckQuantityListProduct();

const isDataReady = ref(false);

onMounted(async () => {
  params.value.idHoaDon = getIdHoaDonFromUrl();
  billId.value = getIdHoaDonFromUrl();

  // đợi Vue cập nhật reactive xong
  await nextTick();
});

watch(
  () => [props.loading, props.dataSource],
  ([loading, dataSource]) => {
    if (
      !loading &&
      typeof dataSource === "object" &&
      Array.isArray(dataSource.data) &&
      dataSource.data.length > 0
    ) {
      stepsTmp.value = dataSource.data.map((item) => ({
        title: item.trangThai,
        time: convertDateFormat(item.ngayTao),
      }));
      isDataReady.value = true;
      // console.log("StepsTmp đã có dữ liệu:", stepsTmp.value);
    } else {
      console.warn("Không có dữ liệu stepsTmp:", { loading, dataSource });
    }
  },
  { immediate: true }
);

// Hàm chỉ hiển thị 1 step khi có nhiều step giống nhau
const uniqueStepsTmp = computed(() => {
  const seen = new Set();
  return stepsTmp.value.filter(item => {
    const key = item.title; // Hoặc item.trangThai nếu dùng trường đó
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
});

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

//dữ liệu lshd
const { data: PaymentData } = useGetPayHistory(params, {
  refetchOnWindowClose: false,
  placeholderData: keepPreviousData,
});

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
  // Lấy trạng thái tiếp theo từ mảng steps
  const nextStep = stepsTmp.value[stepsTmp.value.length - 1];
  const stepTitle = nextStep.title;

  // API tạo lịch sử hóa đơn
  const params = {
    status: stepTitle, // Trạng thái mới từ bước tiếp theo
    moTa: `Nhân viên ${
      useAuthStore().user?.email || "Không xác định"
    } chuyển trạng thái hóa đơn -> 'Chờ giao hàng'`,
    trangThai: "Chờ giao hàng",
    email: props.billData.emailNguoiNhan || null,
    idHoaDon: idBill,
    nhanVien: useAuthStore().user?.id || null,
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
            content: `Bạn muốn thay đổi trạng thái của đơn hàng này sang "${params.trangThai}"?`,
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
      content: `Bạn muốn thay đổi trạng thái của đơn hàng này sang "${params.trangThai}"?`,
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
  const nextStep = stepsTmp.value[stepsTmp.value.length - 1];
  const stepTitle = nextStep.title;
  const description = ref("");
  // API tạo lịch sử hóa đơn

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
                moTa: `Nhân viên ${
                  useAuthStore().user?.email || "Không xác định"
                } thay đổi trạng thái -> "Chờ giao hàng" và hoàn trả: ${formatCurrencyVND(
                  props.dataPaymentInfo.refund
                )} - Mã giao dịch: ${description.value}`,
                email: props.billData.emailNguoiNhan || null,
                idHoaDon: idBill,
                nhanVien: useAuthStore().user?.id || null,
                ghiChu: `Xác nhận trạng thái đơn hàng -> Chờ giao hàng và hoàn trả ${formatCurrencyVND(
                  props.dataPaymentInfo.refund
                )}`,
              };
              try {
                // Gọi API để thay đổi trạng thái đơn hàng
                changeStatus({ idBill, params });
                successNotiSort("Cập nhật trạng thái thành công!");
                emit("update:bill");
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
          moTa: `Nhân viên ${
            useAuthStore().user?.email || "Không xác định"
          } thay đổi trạng thái -> "Chờ giao hàng" và hoàn trả: ${formatCurrencyVND(
            props.dataPaymentInfo.refund
          )} - Mã giao dịch: ${description.value}`,
          email: props.billData.emailNguoiNhan || null,
          idHoaDon: idBill,
          nhanVien: useAuthStore().user?.id || null,
          ghiChu: `Xác nhận trạng thái đơn hàng -> Chờ giao hàng và hoàn trả ${formatCurrencyVND(
            props.dataPaymentInfo.refund
          )}`,
        };
        try {
          // Gọi API để thay đổi trạng thái đơn hàng
          changeStatus({ idBill, params });
          successNotiSort("Cập nhật trạng thái thành công!");
          emit("update:bill");
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

const confirmDelivery = () => {
  // Lấy trạng thái tiếp theo từ mảng steps
  const nextStep = stepsTmp.value[stepsTmp.value.length - 1];
  const stepTitle = nextStep.title;

  // Chuẩn bị tham số cho API
  const params = {
    status: stepTitle, // Trạng thái mới từ bước tiếp theo
    trangThai: "Đang vận chuyển",
    moTa: `Nhân viên ${
      useAuthStore().user?.email || "Không xác định"
    } chuyển trạng thái hóa đơn -> "Đang vận chuyển"`,
    email: props.billData.emailNguoiNhan || null,
    idHoaDon: idBill,
    nhanVien: useAuthStore().user?.id || null,
    ghiChu: "Xác nhận trạng thái đơn hàng -> Đang vận chuyển",
  };

  Modal.confirm({
    title: "Xác nhận thay đổi trạng thái",
    content: `Bạn muốn xác nhận giao hàng cho đơn này?`,
    onOk: async () => {
      try {
        // Gọi API để thay đổi trạng thái đơn hàng
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");
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

  if (props.dataPaymentInfo.amountPayable == 0) {
    // Nếu khách đã thanh toán đủ -> Chuyển trực tiếp sang trạng thái "Đã thanh toán"
    const nextStep = "Đã thanh toán";

    const params = {
      status: nextStep,
      trangThai: nextStep,
      moTa: `Nhân viên ${
        useAuthStore().user?.email || "Không xác định"
      } chuyển trạng thái hóa đơn -> "Đã thanh toán"`,
      email: props.billData.emailNguoiNhan || null,
      idHoaDon: idBill,
      nhanVien: useAuthStore().user?.id || null,
      ghiChu: "Xác nhận trạng thái đơn hàng -> Đã thanh toán",
    };

    Modal.confirm({
      title: "Xác nhận đơn hàng đã thanh toán",
      content: `Khách đã thanh toán đủ ${tienKhachDua.toLocaleString()} VND. Chuyển trạng thái sang "${nextStep}"?`,
      onOk: async () => {
        try {
          changeStatus({ idBill, params });
          successNotiSort(
            "Đơn hàng đã chuyển sang trạng thái 'Đã thanh toán'!"
          );
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
  const nextStep = stepsTmp.value[stepsTmp.value.length];
  const stepTitle = nextStep.title;

  const params = {
    status: stepTitle,
    moTa: `Nhân viên ${
      useAuthStore().user?.email || "Không xác định"
    } chuyển trạng thái hóa đơn -> "Đã giao hàng"`,
    trangThai: "Đã giao hàng",
    email: props.billData.emailNguoiNhan || null,
    idHoaDon: idBill,
    nhanVien: useAuthStore().user?.id || null,
    ghiChu: "Xác nhận trạng thái đơn hàng -> Đã giao hàng",
  };

  Modal.confirm({
    title: "Xác nhận giao hàng",
    content: `Bạn có muốn xác nhận giao hàng cho đơn này không?`,
    onOk: async () => {
      try {
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");
      } catch (error) {
        console.error("Cập nhật trạng thái thất bại:", error);
        errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
      }
    },
  });
};

const confirmCompleted = () => {
  // Lấy trạng thái tiếp theo từ mảng steps
  const nextStep = stepsTmp.value[stepsTmp.value.length - 1];
  const stepTitle = nextStep.title;

  // API tạo lịch sử hóa đơn
  const params = {
    status: stepTitle, // Trạng thái mới từ bước tiếp theo
    moTa: `Nhân viên ${
      useAuthStore().user?.email || "Không xác định"
    } chuyển trạng thái hóa đơn -> "Thành công"`,
    trangThai: "Thành công",
    email: props.billData.emailNguoiNhan || null,
    idHoaDon: idBill,
    nhanVien: useAuthStore().user?.id || null,
    ghiChu: "Xác nhận trạng thái đơn hàng -> Thành công. Hoàn tất đơn hàng.",
  };

  Modal.confirm({
    title: "Xác nhận thay đổi trạng thái",
    content: `Bạn muốn thay đổi trạng thái của đơn hàng này sang "${params.trangThai}"?`,
    onOk: async () => {
      try {
        // Gọi API để thay đổi trạng thái đơn hàng
        changeStatus({ idBill, params });
        successNotiSort("Cập nhật trạng thái thành công!");
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
  const stepTitle = stepsTmp.value[0].title;
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
        errorNotiSort("Vui lòng nhập lý do hủy");
        return Promise.reject();
      }

      const params = {
        status: stepTitle,
        trangThai: "Đã hủy",
        moTa: `Nhân viên ${
          useAuthStore().user?.email || "Không xác định"
        } đã hủy đơn hàng!`,
        email: props.billData.emailNguoiNhan || null,
        idHoaDon: idBill,
        nhanVien: useAuthStore().user?.id || null,
        ghiChu: "Đơn hàng đã bị hủy!",
      };

      try {
        await changeStatus({ idBill, params });
        successNotiSort("Đã hủy đơn thành công!");
        if (
            props.billData.loaiHD === "Online"
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
  const nextStep = stepsTmp.value[stepsTmp.value.length - 1];
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
        moTa: `Nhân viên ${
          useAuthStore().user?.email || "Không xác định"
        } đã hủy và hoàn trả: ${formatCurrencyVND(
          props.dataPaymentInfo.paid
        )} - Mã giao dịch: ${description.value}`,
        email: props.billData.emailNguoiNhan || null,
        idHoaDon: idBill,
        nhanVien: useAuthStore().user?.id || null,
        ghiChu: `Đơn hàng đã bị hủy và hoàn trả ${formatCurrencyVND(
          props.dataPaymentInfo.paid
        )}`,
      };

      try {
        await changeStatus({ idBill, params });
        successNotiSort("Đã hủy đơn thành công!");
        if (
            props.billData.loaiHD === "Online"
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
      } catch (error) {
        console.error("Cập nhật trạng thái thất bại:", error);
        errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
      }
    },
    onCancel: () => console.log("Thao tác rollback bị hủy."),
  });
};

const rollBack = (stepStatus: string) => {
  let prevStep = "";
  const stepTitle = prevStep;
  const description = ref();
  if (stepStatus) {
    switch (stepStatus) {
      case "Chờ giao hàng":
        prevStep = "Chờ xác nhận";
        break;

      case "Đang vận chuyển":
        prevStep = "Chờ giao hàng";
        break;
      case "Đã thanh toán":
        prevStep = "Đang vận chuyển";
        break;
      default:
        console.log("Bước không xác định hoặc không có title");
        break;
    }

    Modal.confirm({
      title: "Xác nhận quay lại trạng thái trước",
      content: () => {
        return h("div", [
          h(
            "p",
            `Bạn có chắc chắn muốn quay lại trạng thái "${prevStep}" không?`
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
          status: prevStep,
          trangThai: prevStep,
          moTa: `Nhân viên ${
            useAuthStore().user?.email || "Không xác định"
          } đã quay lại trạng thái ${prevStep}`,
          email: props.billData.emailNguoiNhan || null,
          idHoaDon: idBill,
          nhanVien: useAuthStore().user?.id || null,
          ghiChu: "Quay lai trạng thái",
        };

        try {
          await changeStatus({ idBill, params });
          successNotiSort(`Trạng thái đã quay lại: ${prevStep}`);
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
        } catch (error) {
          console.error("Cập nhật trạng thái thất bại:", error);
          errorNotiSort("Cập nhật trạng thái thất bại. Vui lòng thử lại.");
        }
      },
      onCancel: () => console.log("Thao tác rollback bị hủy."),
    });
  }
};



// Hàm in hóa đơn
const createInvoicePdf = async () => {
  const listProducts = computed(() => {
    return props.dataProduct.map((item: Product) => ({
      catalog: item.catalog,
      tenMauSac: item.tenMau,
      kichCo: item.tenKichCo,
      tenSanPham: item.tenSanPham,
      giaHienTai: item.gia,
      SoLuong: item.soLuong,
    }));
  });

  const pdfParams = {
    idKhachHang: null,

    idNhanVien: useAuthStore().user?.id || null,

    idHoaDon: billId.value,

    products: listProducts.value,

    tongTien: null,

    phiVanChuyen: null,

    giamGia: null,
  };

  Modal.confirm({
    content: "Bạn chắc chắn muốn in hóa đơn?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await createInvoicePdfWithId(billId.value, pdfParams);
        console.log(props.dataProduct);

        console.log(billId.value);
        console.log(pdfParams);
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
      }
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
    },
  });
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
