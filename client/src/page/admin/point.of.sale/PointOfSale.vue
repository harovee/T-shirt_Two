<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
      <v-icon name="bi-cart3" size="x-large" width="48" height="48" />
      <h3 class="text-2xl m-0">Bán hàng tại quầy</h3>
    </div>
    <div>
      <div class="p-2 rounded-xl border-2">
        <div class="flex justify-end gap-3">
          <a-tooltip title="Thêm sản phẩm vào giỏ" trigger="hover">
            <a-button
              class="bg-purple-300 flex justify-between items-center gap-2"
              size="large"
              @click="handleOpenProductsModel"
            >
              <v-icon name="md-addcircle" />
            </a-button>

            <a-modal
              v-model:open="openProductsModal"
              title="Danh sách sản phẩm có sẵn"
              width="80%"
              @ok="handleOk"
              @cancel="handleCancel"
            >
              <template #footer>
                <div class="text-center">
                  <a-button key="back" @click="handleCancel">Đóng</a-button>
                  <a-button
                    key="submit"
                    type="primary"
                    :loading="loadingSubmitProductTable"
                    @click="handleOk"
                    >Thêm</a-button
                  >
                </div>
              </template>
              <div>
                <POSProductTable
                  :attributes="listAttributes.data.value?.data"
                  :idSanPhamChiTiets="idSanPhamChiTiets"
                  @update:idSanPhamChiTiets="handleUpdateIdSanPhamChiTiets"
                  @update:refetch="setRefetch"
                />
              </div>
            </a-modal>
            <!-- <a-modal
              v-model:open="openQuantityModal"
              title="Chọn số lượng"
              width="500px"
              style="height='500px'"
              @ok="handleQuantityOk"
              @cancel="handleCancel"
              class="mt-10"
              >Nhập số lượng
              <a-input-number
                class="ms-5"
                min="0"
                v-model:value="quantityProduct"
              ></a-input-number
            ></a-modal> -->
          </a-tooltip>
          <a-tooltip title="Quét QR" trigger="hover">
            <a-button
              class="bg-purple-300 flex justify-between items-center gap-2"
              size="large"
              @click="openQRModal"
            >
              <v-icon name="bi-qr-code-scan" />
            </a-button>
          </a-tooltip>
          <scan-qr-code
            :openModal="isModalOpen"
            @update:open="isModalOpen = $event"
            @update:idSanPhamChitiet="handleUpdateIdSanPhamChiTietQr"
            @ok="handleQRScan"
          />
        </div>
        <a-tabs
          v-model:activeKey="activeKey"
          type="editable-card"
          @edit="onEdit"
          class="m-5"
        >
          <a-tab-pane
            v-for="bill in dataSource"
            :key="bill.id"
            :tab="`${bill.ma} (${dataSourcePro ? dataSourcePro.length : 0})`"
          >
            <div class="rounded-xl p-7 mt-6 rounded-xl border-2">
              <POSProducsInCart
                :idOrder="activeKey?.valueOf() || ''"
              ></POSProducsInCart>
            </div>

            <div class="rounded-xl p-7 mt-6 rounded-xl border-2">
              <div class="flex justify-between items-center mb-6">
                <h2 class="text-xl font-semibold">Thông tin khách hàng</h2>
                <div class="flex items-center space-x-3">
                  <a-tooltip title="Chọn khách hàng" trigger="hover">
                    <a-button
                      class="bg-purple-300 flex justify-between items-center gap-2"
                      @click="handleOpenKhachHang"
                      size="large"
                    >
                      <v-icon name="md-manageaccounts-round" />
                    </a-button>
                  </a-tooltip>
                  <a-tooltip
                    v-if="activeTabCustomers[bill.id] && activeTabPaymentInfo[bill.id].shippingOption === 'true'"
                    title="Chọn địa chỉ"
                    trigger="hover"
                  >
                    <a-button
                      class="bg-purple-300 flex justify-between items-center gap-2"
                      @click="handleOpenKhachHangAddress"
                      size="large"
                    >
                      <v-icon name="fa-address-book" />
                    </a-button>
                  </a-tooltip>
                </div>
                <khach-hang-payment-table
                  :open="open"
                  @handleClose="handleClose"
                  @cancel="open = false"
                  class="w-[600px] h-[400px]"
                  @handleOpenKhachHang="handleOpenKhachHang"
                  @selectCustomer="
                    (customer) => handleCustomerSelected(customer, bill)
                  "
                />
                <KhachHangAddressPaymentTable
                  :open="openCustomerAddress"
                  @handleClose="handleCloseCustomerAddress"
                  @cancel="openCustomerAddress = false"
                  :dataCustomerWithId="activeTabCustomers[bill.id]"
                  @selectCustomerAddress="
                    (customerAddress, dataCustomerAddress) =>
                      handleCustomerAddressSelected(
                        customerAddress,
                        dataCustomerAddress,
                        bill
                      )
                  "
                  class="w-[600px] h-[400px]"
                />
              </div>
              <div class="mb-6 h-100">
                <p>
                  <strong>Tên khách hàng:</strong>
                  {{
                    activeTabCustomers[bill.id]?.name
                      ? activeTabCustomers[bill.id].name
                      : "Khách lẻ"
                  }}
                </p>
                <template v-if="activeTabCustomers[bill.id]">
                  <p>
                    <strong>Số điện thoại:</strong>
                    {{ activeTabCustomers[bill.id]?.phoneNumber }}
                  </p>
                  <p>
                    <strong>Email:</strong>
                    {{ activeTabCustomers[bill.id]?.email }}
                  </p>
                </template>
              </div>

              <hr />
              <div>
                <payment-information
                  :dataSourceInfor="bill"
                  :selectedCustomerInfo="activeTabCustomers[bill.id]"
                  :selectedCustomerAddress="activeTabCustomerAddress[bill.id]"
                  
                  @handlePaymentInfo="
                    (paymentInfo) => handleChangePaymentInfo(paymentInfo, bill)
                  "
                />
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {
  ref,
  computed,
  watch,
  Ref,
  onMounted,
  createVNode,
  reactive,
  watchEffect,
} from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  useGetBillsWait,
  useCreateBillsWait,
  useRemoveBillById,
} from "@/infrastructure/services/service/admin/bill.action";
import POSProductTable from "./components/POSProductTable.vue";
import ScanQrCode from "./qr-code/ScanQrCode.vue";
import POSProducsInCart from "./components/POSProducsInCart.vue";
import { BillCreateRequest } from "@/infrastructure/services/api/admin/bill.api";
import {
  warningNotiSort,
  successNotiSort,
  errorNotiSort,
  notificationType,
  openNotification,
} from "@/utils/notification.config";
import { useGetAttributes } from "@/infrastructure/services/service/admin/sale.action";
import { Modal } from "ant-design-vue";
import { ExclamationCircleOutlined } from "@ant-design/icons-vue";
import {
  POSAddProductsToCartRequest,
  POSProductDetailResponse,
} from "@/infrastructure/services/api/admin/point-of-sale.api";
import {
  useCreateOrderDetails,
  useGetOrderDetails,
} from "@/infrastructure/services/service/admin/point-of-sale";
import { useAuthStore } from "@/infrastructure/stores/auth";
import KhachHangPaymentTable from "./KhachHangPaymentTable.vue";
import KhachHangAddressPaymentTable from "./KhachHangAddressPaymentTable.vue";
import PaymentInformation from "./PaymentInformation.vue";
import { ProductDetailListResponse } from "@/infrastructure/services/api/admin/product_detail.api";
import { useGetListProductDetail } from "@/infrastructure/services/service/admin/productdetail.action";
import {
  formatCurrencyVND,
  getDateFormat,
  getDateTimeMinutesFormat,
} from "@/utils/common.helper";

const { data, isLoading, isFetching } = useGetBillsWait();

const { data: listProductDetail } = useGetListProductDetail();

const dataSource = computed(() => data?.value?.data || []);
const activeKey = ref<string | null>(null);

const dataListProductDetail = computed(
  () => listProductDetail?.value?.data || []
);

// Hiển thị tab đầu tiên khi load trang
onMounted(() => {
  const storedActiveKey = localStorage.getItem("activeKey");
  if (storedActiveKey) {
    activeKey.value = storedActiveKey;
  }
});

watch(
  dataSource,
  (newDataSource) => {
    if (newDataSource.length > 0 && !activeKey.value) {
      activeKey.value = newDataSource[0].id;
    }
  },
  { immediate: true }
);

// hiển thị số lượng sản phẩm đang lỗi
let idHoaDon: any;

watch(
  activeKey,
  (newActiveKey) => {
    if (newActiveKey) {
      localStorage.setItem("activeKey", newActiveKey);
      idHoaDon = newActiveKey;
      // refetchPro();
    }
  },
  { immediate: true }
);

interface DataType extends POSProductDetailResponse {
  key: string;
  thanhTien: number;
}

const {
  data: dataPro,
  error,
  isFetching: proFetching,
  refetch: refetchPro,
} = useGetOrderDetails(idHoaDon, {
  refetchOnWindowFocus: false,
  placeholderData: [],
  enabled: false,
});

// -------------------------------------------

/*  THAO - ADD PRODUCT TO CART (PENDING ORDER)   */
const loadingSubmitProductTable = ref<boolean>(false);
const idSanPhamChiTiets = ref<string[]>([]);
const openProductsModal = ref<boolean>(false);
const openQuantityModal = ref<boolean>(false);
const quantityProduct = ref<number>(1);

type RefetchFunction = () => void;
const refetchProducts = ref<RefetchFunction | null>(null);

// console.log(props.selectedCustomerInfo);

const dataSourcePro: DataType[] | any = computed(() => {
  return (
    dataPro?.value?.data?.map((e: any) => ({
      key: e.id || "",
      maSanPhamChiTiet: e.maSanPhamChiTiet || "",
      ten: e.ten || "",
      soLuong: e.soLuong || "",
      gia: e.gia || 0,
      giaHienTai: e.giaHienTai || 0,
      tenSanPham: e.tenSanPham || "",
      tenThuongHieu: e.tenThuongHieu || "",
      gioiTinh: e.gioiTinh
        ? "Nam"
        : e.gioiTinh == false
        ? "Nữ"
        : "Không xác định",
      kichCo: e.kichCo || "",
      phongCach: e.phongCach || "",
      maMauSac: e.maMauSac || "",
      tenMauSac: e.tenMauSac || "",
      linkAnh: e.linkAnh || "",
      thanhTien: e.soLuong * e.gia,
    })) || []
  );
});

const listAttributes = useGetAttributes({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const isModalOpen = ref(false);

// Mở modal quét mã QR khi nhấn nút
const openQRModal = () => {
  isModalOpen.value = true;
};

// Xử lý mã QR khi quét thành công
const handleQRScan = (qrCode: string) => {
  console.log("Mã QR quét được:", qrCode);
};

// refetchProducts.value = listAttributes.refetch;

function handleOpenProductsModel() {
  openProductsModal.value = true;
  if (refetchProducts.value) {
    refetchProducts.value();
  }
}

function handleOpenQuantityModel() {
  openQuantityModal.value = true;
}

const handleUpdateIdSanPhamChiTiets = (newIdSanPhamChiTiets: string[]) => {
  idSanPhamChiTiets.value = newIdSanPhamChiTiets;
};

const getProductCountByBillId = (billId: string) => {
  return computed(() => {
    return dataSourcePro.value
      ? dataSourcePro.value.filter((item: any) => item.idHoaDon === billId)
          .length
      : 0;
  });
};

// Hàm tìm id theo mã SPCT
const findIdByMaSPCT = (maSPCT: string) => {
  const product = dataListProductDetail.value.find(
    (item) => item.maSanPhamChiTiet === maSPCT
  );
  return product ? product.id : null;
};

const handleUpdateIdSanPhamChiTietQr = (newId: string) => {
  idSanPhamChiTiets.value = [];
  const idSPCT = findIdByMaSPCT(newId);
  idSanPhamChiTiets.value.push(idSPCT);
  console.log(newId);
  
  if (newId) {
    handleCreateQrOrderDetails({
    idSanPhamChiTiets: idSanPhamChiTiets.value,
    idHoaDonCho: activeKey.value,
    userEmail: useAuthStore().user?.email || null,
    soLuong: 1,
  });
  }
  // handleCreateQrOrderDetails({
  //   idSanPhamChiTiets: idSanPhamChiTiets.value,
  //   idHoaDonCho: activeKey.value,
  //   userEmail: useAuthStore().user?.email || null,
  //   soLuong: 1,
  // });
};

const handleCancel = () => {
  openProductsModal.value = false;
  quantityProduct.value = 1;
};

const { mutate: createOrderDetails } = useCreateOrderDetails();
const handleOk = (e: MouseEvent) => {
  // openQuantityModal.value = true;
  handleCreateOrderDetails({
    idSanPhamChiTiets: idSanPhamChiTiets.value,
    idHoaDonCho: activeKey.value,
    userEmail: useAuthStore().user?.email || null,
    soLuong: 1,
  });
};

// modal thêm số lượgn
const handleQuantityOk = () => {
  // console.log(quantityProduct.value);
  // handleCreateOrderDetails({
  //   idSanPhamChiTiets: idSanPhamChiTiets.value,
  //   idHoaDonCho: activeKey.value,
  //   userEmail: useAuthStore().user?.email || null,
  //   soLuong: 1,
  // });
};

const handleCreateQrOrderDetails = (data: POSAddProductsToCartRequest) => {
  try {
    createOrderDetails(data, {
      onSuccess: (result) => {
        openNotification(notificationType.success, result?.message, "");
      },
      onError: (error: any) => {
        openNotification(
          notificationType.error,
          error?.response?.data?.message,
          ""
        );
      },
    });
  } catch (error: any) {
    if (error?.response) {
      openNotification(
        notificationType.error,
        error?.response?.data?.message,
        ""
      );
    } else if (error?.errorFields) {
      openNotification(notificationType.warning, "", "");
    }
  }
};

const handleCreateOrderDetails = (data: POSAddProductsToCartRequest) => {
  Modal.confirm({
    title: "Bạn chắc chắn muốn thêm các sản phẩm dã chọn vào giỏ hàng?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      if (data.idSanPhamChiTiets.length === 0) {
        warningNotiSort("Vui lòng chọn sản phẩm!");
        return;
      }
      try {
        createOrderDetails(data, {
          onSuccess: (result) => {
            openNotification(notificationType.success, result?.message, "");
            openQuantityModal.value = false;
            openProductsModal.value = false;
          },
          onError: (error: any) => {
            openNotification(
              notificationType.error,
              error?.response?.data?.message,
              ""
            );
          },
        });
      } catch (error: any) {
        if (error?.response) {
          openNotification(
            notificationType.error,
            error?.response?.data?.message,
            ""
          );
        } else if (error?.errorFields) {
          openNotification(notificationType.warning, "", "");
        }
      }
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
    },
  });
};

const setRefetch = (refetch) => {
  refetchProducts.value = refetch;
};

const { mutate: createBillWail } = useCreateBillsWait();
const { mutate: removeBillWait } = useRemoveBillById();

const dataCustomers = ref(null);

const dataSources = ref(null);

const open = ref(false);

const openCustomerAddress = ref(false);

const activeTabCustomers = reactive({});

const activeTabPaymentInfo = reactive({});

const activeTabCustomerAddress = reactive({});

const isRefresh = ref(false);

watch(
  dataSource,
  (newDataSource) => {
    newDataSource.forEach((bill) => {
      activeTabPaymentInfo[bill.id] = {
        method: "cash",
        bankAccount: formatCurrencyVND(""),
        voucherCode: "",
        voucherId: null,
        shippingOption: "false",
        shippingFee: 0,
        discount: 0,
        total: 0,
        totalProductPrice: 0,
      };
    });
  },
  { immediate: true }
);

const handleOpenKhachHang = () => {
  open.value = true;
};

const handleOpenKhachHangAddress = () => {
  openCustomerAddress.value = true;
};
watch(
  () => dataSource.value,
  (newData) => {
    if (newData) {
      dataSources.value = JSON.parse(JSON.stringify(dataSource.value));
      // console.log(dataSources.value);
    }
  },
  { immediate: true }
);

const handleClose = () => {
  open.value = false;
};

const handleCloseCustomerAddress = () => {
  openCustomerAddress.value = false;
};

const selectedCustomer = ref<{
  id: string;
  name: string;
  phoneNumber: string;
  email: string;
} | null>(null);

// const handleCustomerSelected = (
//   customer: any,
//   dataCustomer: any,
//   bill: any
// ) => {
//   const billWait = dataSources.value.find((data: any) => data.id === bill.id);
//   billWait.idKhachHang = customer.key;
//   dataCustomers.value = dataCustomer;
// };

const handleCustomerSelected = (customer: any, bill: any) => {
  if (!activeTabCustomers[bill.id]) {
    activeTabCustomers[bill.id] = {};
  }
  activeTabCustomers[bill.id] = { ...customer };
};

const handleChangePaymentInfo = (paymentInfo: any, bill: any) => {
  activeTabPaymentInfo[bill.id] = { ...paymentInfo };
};

const handleCustomerAddressSelected = (
  customerAddress: any,
  dataCustomerAddress: any,
  bill: any
) => {
  activeTabCustomerAddress[bill.id] = { ...customerAddress };
  isRefresh.value = !isRefresh.value
  // console.log(activeTabCustomerAddress[bill.id]);
};

const getNameCustomer = (id: string) => {
  if (id !== null && id !== "") {
    const customer = dataCustomers.value.find(
      (customer) => customer.key === id
    );
    if (customer) {
      return customer.name;
    } else {
      return null;
    }
  }
};

const getEmailCustomer = (id: string) => {
  if (id !== null && id !== "") {
    const customer = dataCustomers.value.find(
      (customer) => customer.key === id
    );
    if (customer) {
      return customer.email;
    } else {
      return "";
    }
  }
};

const getPhoneNumberCustomer = (id: string) => {
  if (id !== null && id !== "") {
    const customer = dataCustomers.value.find(
      (customer) => customer.key === id
    );
    if (customer) {
      return customer.phoneNumber;
    } else {
      return "";
    }
  }
};

const add = async () => {
  const payload = {
    loaiHD: "Tại quầy",
    idKhachHang: null,
    idNhanVien: null,
    idPhieuGiamGia: null,
  };

  if (dataSource.value.length < 5) {
    await createBillWail(payload);
    successNotiSort("Tạo hóa đơn thành công");
  } else {
    warningNotiSort("Không được tạo quá 5 hóa đơn chờ!");
  }
};

const remove = async (targetKey: string) => {
  Modal.confirm({
    content: "Bạn chắc chắn muốn hủy hóa đơn này?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        await removeBillWait(targetKey);
        // console.log(targetKey);

        successNotiSort("Hủy hóa đơn thành công");
      } catch (error) {
        errorNotiSort("Hủy hóa đơn thất bại");
      }
    },
    cancelText: "Huỷ",
    onCancel() {
      Modal.destroyAll();
    },
  });
};

const onEdit = (targetKey: string | MouseEvent, action: string) => {
  if (action === "add") {
    add();
  } else {
    remove(targetKey as string);
  }
};
</script>

<style>
.full-modal .ant-modal {
  max-width: 100%;
  top: 0;
  padding-bottom: 0;
  margin: 0;
}
.full-modal .ant-modal-content {
  display: flex;
  flex-direction: column;
  height: calc(100vh);
}
.full-modal .ant-modal-body {
  flex: 1;
}
</style>
