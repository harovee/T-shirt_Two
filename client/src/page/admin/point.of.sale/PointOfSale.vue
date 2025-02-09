<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex items-center gap-2">
      <v-icon name="bi-cart3" size="x-large" width="48" height="48" />
      <h3 class="text-2xl m-0">Bán hàng tại quầy</h3>
    </div>
    <div>
      <div class="p-4 rounded-xl border-2 flex flex-col gap-6">
        <a-tabs
          v-model:activeKey="activeKey"
          type="editable-card"
          @edit="onEdit"
          class="m-5"
        >
          <a-tab-pane v-for="bill in dataSource" :key="bill.id" :tab="bill.ma">
            {{ bill.id }}

            <!-- Tài khoản + khách hàng, form thanh toán -->
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
                  @selectCustomer="(customer, dataCustomer) => handleCustomerSelected(customer, dataCustomer, bill)"
                />
              </div>
              <div class="mb-6 h-100">
                <p>
                  <strong>Tên khách hàng:</strong>
                  {{ bill.idKhachHang ? getNameCustomer(bill.idKhachHang) : "Khách lẻ" }}
                </p>
                <template v-if="bill && bill.idKhachHang">
                  <p>
                    <strong>Số điện thoại:</strong>
                    {{ getPhoneNumberCustomer(bill.idKhachHang) }}
                  </p>
                  <p><strong>Email:</strong> {{ getEmailCustomer(bill.idKhachHang) }}</p>
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
import { ref, computed, watch } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  useGetBillsWait,
  useCreateBillsWait,
} from "@/infrastructure/services/service/admin/bill.action";
import { BillCreateRequest } from "@/infrastructure/services/api/admin/bill.api";

import KhachHangPaymentTable from "./KhachHangPaymentTable.vue";
import PaymentInformation from "./PaymentInformation.vue";
import { warningNotiSort, successNotiSort } from "@/utils/notification.config";

const { data, isLoading, isFetching } = useGetBillsWait();

const dataSource = computed(() => data?.value?.data || []);

const dataCustomers = ref(null)

const dataSources = ref(null)

const open = ref(false);

const handleOpenKhachHang = () => {
  open.value = true;
};
watch(
  () => dataSource.value,
  (newData) => {
    if (newData) {
      dataSources.value = JSON.parse(JSON.stringify(dataSource.value))
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


const handleCustomerSelected = (customer: any, dataCustomer: any, bill: any) => {
  const billWait = dataSources.value.find((data:any) => data.id === bill.id);
  billWait.idKhachHang = customer.key
  dataCustomers.value = dataCustomer
};

const getNameCustomer = (id: string) => {
  if (id !== null && id !== "") {
    const customer = dataCustomers.value.find(customer => customer.key === id);
    if (customer) {
      return customer.name;
    } else {
      return null;
    }
  }
}

const getEmailCustomer = (id: string) => {
  if (id !== null && id !== "") {
    const customer = dataCustomers.value.find(customer => customer.key === id);
    if (customer) {
      return customer.email;
    } else {
      return "";
    }
  }
}

const getPhoneNumberCustomer = (id: string) => {
  if (id !== null && id !== "") {
    const customer = dataCustomers.value.find(customer => customer.key === id);
    if (customer) {
      return customer.phoneNumber;
    } else {
      return "";
    }
  }
}

const panes = ref<{ title: string; content: string; key: string }[]>([
  { title: "Tab 1", content: "Content of Tab 1", key: "1" },
  { title: "Tab 2", content: "Content of Tab 2", key: "2" },
  { title: "Tab 3", content: "Content of Tab 3", key: "3" },
]);

const activeKey = ref<string | null>(null);

const newTabIndex = ref(0);

const { mutate: createBillWail } = useCreateBillsWait();

const add = async () => {
  const payload = {
    loaiHD: "Tại quầy",
    idKhachHang: null,
    idNhanVien: null,
    idPhieuGiamGia: null,
  };
  console.log(dataSource.value);

  if (dataSource.value.length <= 5) {
    await createBillWail(payload);
    successNotiSort("Tạo hóa đơn thành công");
  } else {
    warningNotiSort("Không được tạo quá 5 hóa đơn chờ!");
  }
};

// const remove = (targetKey: string) => {
//   let lastIndex = 0;
//   panes.value.forEach((pane, i) => {
//     if (pane.key === targetKey) {
//       lastIndex = i - 1;
//     }
//   });
//   panes.value = panes.value.filter((pane) => pane.key !== targetKey);
//   if (panes.value.length && activeKey.value === targetKey) {
//     if (lastIndex >= 0) {
//       activeKey.value = panes.value[lastIndex].key;
//     } else {
//       activeKey.value = panes.value[0].key;
//     }
//   }
// };

const onEdit = (targetKey: string | MouseEvent, action: string) => {
  if (action === "add") {
    add();
  } else {
    // remove(targetKey as string);
  }
};
</script>