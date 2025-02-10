<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
      <v-icon
        name="bi-cart3"
        size="x-large"
        width="48"
        height="48"
        style="color: aqua"
      />
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
            <a-modal
                  v-model:open="openQuantityModal"
                  title="Chọn số lượng"
                  width="500px"
                  style="height='500px'"
                  @ok="handleQuantityOk"
                  class="mt-10"
                >Nhập số lượng <a-input-number class="ms-5" min="0" v-model:value="quantityProduct"></a-input-number></a-modal>
          </a-tooltip>
          <a-tooltip title="Quét QR" trigger="hover">
            <a-button
              class="bg-purple-300 flex justify-between items-center gap-2"
              size="large"
              @click="redirectToCreateProduct"
            >
              <v-icon name="bi-qr-code-scan" />
            </a-button>
          </a-tooltip>
        </div>
        <a-tabs
          v-model:activeKey="activeKey"
          type="editable-card"
          @edit="onEdit"
          class="m-5"
        >
          <a-tab-pane v-for="bill in dataSource" :key="bill.id" :tab="bill.ma">
            <div>
              <POSProducsInCart
                :idOrder="activeKey?.valueOf() || ''"
              ></POSProducsInCart>
            </div>

            <div class="rounded-xl p-7 mt-6 rounded-xl border-2">
              <div class="flex justify-between items-center mb-6">
                <h2 class="text-xl font-semibold">Tài khoản</h2>
                <a-button class="text-blue-500" @click="handleOpenKhachHang"
                  >Chọn tài khoản</a-button
                >
                <khach-hang-payment-table
                  :open="open"
                  @handleClose="handleClose"
                  @cancel="open = false"
                  class="w-[600px] h-[400px]"
                  @handleOpenKhachHang="handleOpenKhachHang"
                  @selectCustomer="
                    (customer, dataCustomer) =>
                      handleCustomerSelected(customer, dataCustomer, bill)
                  "
                />
              </div>
              <div class="mb-6 h-100">
                <p>
                  <strong>Tên khách hàng:</strong>
                  {{
                    bill.idKhachHang
                      ? getNameCustomer(bill.idKhachHang)
                      : "Khách lẻ"
                  }}
                </p>
                <template v-if="bill && bill.idKhachHang">
                  <p>
                    <strong>Số điện thoại:</strong>
                    {{ getPhoneNumberCustomer(bill.idKhachHang) }}
                  </p>
                  <p>
                    <strong>Email:</strong>
                    {{ getEmailCustomer(bill.idKhachHang) }}
                  </p>
                </template>
              </div>

              <div class="flex justify-between items-center mb-6">
                <h2 class="text-xl font-semibold">Khách hàng</h2>
                <a-button class="text-blue-500">Chọn địa chỉ</a-button>
              </div>
              <hr />
              <div class="p-4 grid grid-cols-1 lg:grid-cols-2 gap-6">
                <payment-information />
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch, Ref, onMounted, createVNode } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  useGetBillsWait,
  useCreateBillsWait,
  useRemoveBillById,
} from "@/infrastructure/services/service/admin/bill.action";
import POSProductTable from "./components/POSProductTable.vue";
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
import { POSAddProductsToCartRequest } from "@/infrastructure/services/api/admin/point-of-sale.api";
import { useCreateOrderDetails } from "@/infrastructure/services/service/admin/point-of-sale";
import { useAuthStore } from "@/infrastructure/stores/auth";
import KhachHangPaymentTable from "./KhachHangPaymentTable.vue";
import PaymentInformation from "./PaymentInformation.vue";

const { data, isLoading, isFetching } = useGetBillsWait();

const dataSource = computed(() => data?.value?.data || []);
const activeKey = ref<string | null>(dataSource.value[0]?.id || null);
onMounted(() => {
  if (dataSource.value.length > 0) {
    activeKey.value = dataSource.value[0].id;
  }
});

/*  THAO - ADD PRODUCT TO CART (PENDING ORDER)   */
const loadingSubmitProductTable = ref<boolean>(false);
const idSanPhamChiTiets = ref<string[]>([]);
const openProductsModal = ref<boolean>(false);
const openQuantityModal = ref<boolean>(false);
const quantityProduct = ref<number>(1);

type RefetchFunction = () => void;
const refetchProducts = ref<RefetchFunction | null>(null);

const listAttributes = useGetAttributes({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

function handleOpenProductsModel() {
  openProductsModal.value = true;
  if (refetchProducts.value) {
    refetchProducts.value();
  }
}

function handleOpenQuantityModel() {
  openQuantityModal.value = true;
  // if (refetchProducts.value) {
  //   refetchProducts.value();
  // }
}

const handleUpdateIdSanPhamChiTiets = (newIdSanPhamChiTiets: string[]) => {
  idSanPhamChiTiets.value = newIdSanPhamChiTiets;
  console.log(idSanPhamChiTiets.value);
};
const handleCancel = () => {
  openProductsModal.value = false;
};

const { mutate: createOrderDetails } = useCreateOrderDetails();
const handleOk = (e: MouseEvent) => {
  // alert("id hóa đơn: " + activeKey.value + "\n" + " list id sản phẩm chi tiết: " + idSanPhamChiTiets.value)
  // handleCreateOrderDetails({
  //   idSanPhamChiTiets: idSanPhamChiTiets.value,
  //   idHoaDonCho: activeKey.value,
  //   userEmail: useAuthStore().user?.email || null,
  // });
  openQuantityModal.value = true;
  // openProductsModal.value = false;
};

const handleQuantityOk = () => {
  console.log(quantityProduct.value);
  handleCreateOrderDetails({
    idSanPhamChiTiets: idSanPhamChiTiets.value,
    idHoaDonCho: activeKey.value,
    userEmail: useAuthStore().user?.email || null,
    soLuong: quantityProduct.value
  });
}

const handleCreateOrderDetails = (data: POSAddProductsToCartRequest) => {
  Modal.confirm({
    title: "Bạn chắc chắn muốn thêm các sản phẩm dã chọn vào giỏ hàng?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        createOrderDetails(data, {
          onSuccess: (result) => {
            openNotification(notificationType.success, result?.message, "");
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
  refetchProducts.value = refetch; // Lưu hàm refetch
};

// // Copy dataSource để xóa
// const dataSources = ref([]);

// // Theo dõi khi props.dataSource?.data thay đổi, đợi cho đến khi có dữ liệu hiển thị được lên bảng ...
// watch(
//   () => dataSource.value,
//   (newData) => {
//     if (newData) {
//       dataSources.value = JSON.parse(JSON.stringify(newData));
//     }
//   },
//   { immediate: true }
// );

// watch(
//   () => dataSource.value,
//   (newData) => {
//     if (newData) {
//         console.log(newData);
//     }
//   },
//   { immediate: true }
// );

// const panes = ref<{ title: string; content: string; key: string }[]>([
//   { title: "Tab 1", content: "Content of Tab 1", key: "1" },
//   { title: "Tab 2", content: "Content of Tab 2", key: "2" },
//   { title: "Tab 3", content: "Content of Tab 3", key: "3" },
// ]);

// const newTabIndex = ref(0);

const { mutate: createBillWail } = useCreateBillsWait();
const { mutate: removeBillWait } = useRemoveBillById();

const dataCustomers = ref(null);

const dataSources = ref(null);

const open = ref(false);

const handleOpenKhachHang = () => {
  open.value = true;
};
watch(
  () => dataSource.value,
  (newData) => {
    if (newData) {
      dataSources.value = JSON.parse(JSON.stringify(dataSource.value));
      console.log(dataSources.value);
    }
  },
  { immediate: true }
);

const handleClose = () => {
  open.value = false;
};

const selectedCustomer = ref<{
  id: string;
  name: string;
  phoneNumber: string;
  email: string;
} | null>(null);

const handleCustomerSelected = (
  customer: any,
  dataCustomer: any,
  bill: any
) => {
  const billWait = dataSources.value.find((data: any) => data.id === bill.id);
  billWait.idKhachHang = customer.key;
  dataCustomers.value = dataCustomer;
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
  try {
    await removeBillWait(targetKey);
    successNotiSort("Xóa hóa đơn thành công");
  } catch (error) {
    errorNotiSort("Xóa hóa đơn thất bại");
  }
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
