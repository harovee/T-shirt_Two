<template>
  <a-modal
    :open="open"
    @ok="handleClose"
    key=""
    :width="'1400px'"
    :okText="'Xác nhận'"
    :cancelText="'Hủy bỏ'"
  >
    <h1 class="text-xl">Danh sách phiếu giảm giá</h1>
    <div class="w-400px">
      <a-space class="flex justify-start items-center">
        <a-input-search
          class="m-2"
          v-model:value="params.keyword"
          placeholder="Tìm kiếm phiếu giảm giá"
          style="width: 200px"
          @change:value="handleSearch"
        />
      </a-space>
    </div>
    <a-table
      :columns="columns"
      :data-source="dataVoucher"
      :pagination="false"
      :scroll="{ x: 300, y: 300 }"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'giaTri'" class="text-left">
          <span>
             {{ record.loaiGiam ? formatCurrencyVND(record.giaTri) : (record.giaTri + "%") }}
             
              <!-- {{ formatCurrencyVND(record.giaTriGiam) }} -->
          </span> 
        </div>
        <div v-if="column.key === 'giamToiDa'" class="text-left">
          <span>
             
              {{ formatCurrencyVND(record.giaTriGiam) }}
          </span> 
        </div>
        <div v-if="column.key === 'dieuKienGiam'" class="text-left">
          {{ formatCurrencyVND(record.dieuKienGiam) }}
        </div>
        <div v-else-if="column.key === 'kieu'">
          <a-tag
            v-if="record.kieu === true || record.kieu === 'true'"
            color="blue"
            >Cá nhân</a-tag
          >
          <a-tag
            v-else-if="record.kieu === false || record.kieu === 'false'"
            color="green"
            >Công khai</a-tag
          >
          <span v-else color="secondary">Không xác định</span>
        </div>
        <template v-if="column.key === 'actions'">
          <a-button color="primary" @click="handleSelectVoucher(record)"
            >Chọn</a-button
          >
        </template>
      </template>
    </a-table>
    <a-pagination
      class="m-2"
      v-model:current="params.page"
      v-model:pageSize="params.size"
      :total="data?.data.totalElements"
    />
  </a-modal>
</template>
<script lang="ts" setup>
import type { TableProps, TableColumnType } from "ant-design-vue";
import {
  formatCurrencyVND,
  getDateFormat,
  getDateTimeMinutesFormat,
} from "@/utils/common.helper";
import { defineProps, computed, defineEmits, ref, watch, onMounted } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import {
  VoucherResponse,
  FindVoucherRequest,
} from "@/infrastructure/services/api/admin/payment.api";
import { useGetListVoucher } from "@/infrastructure/services/service/admin/payment.action";
import { convertDateFormatTime } from "@/utils/common.helper";
import { useRoute } from "vue-router";
import { currentInvoice, invoices, sendCartInfo } from "@/infrastructure/mobile.connect/InvoiceConnect2";

const pageSize = ref(5);
const current1 = ref(1);

const props = defineProps({
  open: {
    type: Boolean,
    required: true,
  },
  dataCustomer: {
    type: Object,
    required: true,
    default: () => ({})
  },
  totalAmount: {
    type: Number,
    required: true,
  },
  dataVoucher: {
    type: Object,
    required: true,
    default: () => ({})
  }
});

const emit = defineEmits(["handleClose", "selectVoucher"]);

const handleClose = () => emit("handleClose");

const params = ref<FindVoucherRequest>({
  page: 1,
  size: 5,
  keyword: "",
  idKhachHang: props.dataCustomer ? props.dataCustomer.id : null,
  tongTien: 0
});

watch(
  () => props.totalAmount,
  (newData) => {
    params.value.tongTien = newData
  },
  { immediate: true }
);

watch(
  () => props.dataCustomer,
  (newData) => {
    params.value.idKhachHang = newData ? newData.key : null
  },
  { immediate: true }
);

const { data } = useGetListVoucher(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

console.log(data);

const handleSearch = (newValue: string) => {
  params.value.keyword = newValue;
  params.value.page = 1;
};

watch(current1, () => {
  params.value.page = current1.value === 0 ? 1 : current1.value;
});

const handleSelectVoucher = (voucher: VoucherResponse) => {
  emit("selectVoucher", voucher);

  // Thảo
  currentInvoice.value.vouchers = [
    {
      id: voucher.id,
      code: voucher.ma,
      name: voucher.ten,
      discount: Number(voucher.loaiGiam ? voucher.giaTriGiam : voucher.giaTri),
      type: voucher.loaiGiam ?  "fixed" : "percent",
      maxDiscount: Number(voucher.giamToiDa),
    },
  ]
  invoices.value.forEach((item) => {
    if (item.id === currentInvoice.value.id) {
      item.vouchers = currentInvoice.value.vouchers;
      sendCartInfo(currentInvoice.value);
    }
  });
  // console.log(voucher);
  
  handleClose();
};

const columns: TableColumnType<VoucherResponse>[] = [
  {
    title: "Mã",
    dataIndex: "ma",
    key: "ma",
    ellipsis: true,
    width: 70,
    resizable: true,
  },
  {
    title: "Tên voucher",
    dataIndex: "ten",
    key: "ten",
    ellipsis: true,
    width: 100,
    resizable: true,
  },
  {
    title: "Số lượng",
    dataIndex: "soLuong",
    key: "soLuong",
    ellipsis: true,
    width: 55,
    resizable: true,
  },
  {
    title: "Loại giảm",
    dataIndex: "kieu",
    key: "kieu",
    ellipsis: true,
    width: 100,
    resizable: true,
  },
  {
    title: "Giá trị giảm",
    dataIndex: "giaTri",
    key: "giaTri",
    ellipsis: true,
    width: 100,
    resizable: true,
  },
  {
    title: "Đơn tối thiểu",
    dataIndex: "dieuKienGiam",
    key: "dieuKienGiam",
    ellipsis: true,
    width: 100,
    resizable: true,
  },
  {
    title: "Giảm tối đa",
    dataIndex: "giamToiDa",
    key: "giamToiDa",
    ellipsis: true,
    width: 100,
    resizable: true,
  },
  {
    title: "Hành động",
    key: "actions",
    align: "center",
    width: 150,
    fixed: "right",
  },
];
</script>
  