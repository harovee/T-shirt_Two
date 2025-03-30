<template>
  <a-modal
    :open="open"
    @ok="handleClose"
    key=""
    :width="'500px'"
    :okText="'Xác nhận'"
    :ok-button-props="{
      style: {
        backgroundColor: '#b91c1c',
        borderColor: '#b91c1c',
        color: 'white',
      },
    }"
    :cancelText="'Hủy bỏ'"
  >
    <h1 class="text-xl">Chọn phiếu giảm giá</h1>
    <div class="w-400px">
      <a-space class="flex justify-start items-center">
        Tìm kiếm
        <a-input-search
          class="m-2"
          v-model:value="params.keyword"
          placeholder="Tìm kiếm phiếu giảm giá"
          style="width: 200px"
          @change:value="handleSearch"
        />
      </a-space>
    </div>
    <div class="space-y-4 mt-5 max-h-96 overflow-auto">
      <div
        v-for="(voucher, index) in dataListVoucher"
        :key="index"
        class="relative flex items-center border shadow-lg text-white voucher-card"
      >
        <div
          v-if="index < 3"
          class="absolute top-2 -right-2 bg-yellow-500 text-white px-3 py-1 text-xs font-bold rounded-full shadow-lg"
        >
          TOP {{ index + 1 }}
        </div>
        <!-- Biểu tượng danh mục -->
        <div
          class="w-24 bg-red-700 flex items-center justify-center rounded-l-xl p-4"
        >
          <img
            src="@/assets/image/images/voucher1.png"
            alt="Voucher Icon"
            class="w-15 h-15"
          />
        </div>

        <!-- Nội dung -->
        <div class="flex items-center justify-between px-4 py-3">
          <div class="flex-1">
            <h3 class="text-sm font-semibold text-black">
              Giảm
              {{
                voucher.loaiGiam
                  ? formatCurrencyVND(voucher.giaTri)
                  : voucher.giaTri + "%"
              }}
              - Giảm tối đa {{ formatCurrencyVND(voucher.giamToiDa) }}
            </h3>
            <p class="text-xs text-black">
              Đơn tối thiểu {{ formatCurrencyVND(voucher.dieuKienGiam) }}
            </p>
            <p class="text-xs text-black">Còn lại {{ voucher.soLuong }}</p>
          </div>

          <a-button
            class="ml-5 w-24"
            :style="{
              backgroundColor: '#b91c1c',
              borderColor: '#b91c1c',
              color: 'white',
              height: '40px',
            }"
            @click="handleSelectVoucher(voucher)"
            >Chọn</a-button
          >

          <!-- Cắt mép hai bên -->
          <div
            class="absolute -left-3 top-1/2 transform -translate-y-1/2 w-6 h-6 bg-white rounded-full shadow-md"
          ></div>
          <div
            class="absolute -right-3 top-1/2 transform -translate-y-1/2 w-6 h-6 bg-white rounded-full shadow-md"
          ></div>
        </div>
      </div>
    </div>
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

const pageSize = ref(5);
const current1 = ref(1);

const props = defineProps({
  open: {
    type: Boolean,
    required: true,
  },
  totalAmount: {
    type: Number,
    required: true,
  },
  dataCustomer: {
    type: Object,
    required: false,
    default: () => ({}),
  },
});

const emit = defineEmits(["handleClose", "selectVoucher"]);

const handleClose = () => emit("handleClose");

const params = ref<FindVoucherRequest>({
  page: 1,
  size: 5,
  keyword: "",
  idKhachHang: props.dataCustomer ? props.dataCustomer.id : null,
  tongTien: 0,
});

watch(
  () => props.totalAmount,
  (newData) => {
    params.value.tongTien = newData;
  },
  { immediate: true }
);

watch(
  () => props.dataCustomer,
  (newData) => {
    params.value.idKhachHang = newData ? newData.key : null;
  },
  { immediate: true }
);

const { data } = useGetListVoucher(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataListVoucher = computed(() => data?.value?.data?.data || []);

const handleSearch = (newValue: string) => {
  params.value.keyword = newValue;
  params.value.page = 1;
};

watch(current1, () => {
  params.value.page = current1.value === 0 ? 1 : current1.value;
});

const handleSelectVoucher = (voucher: VoucherResponse) => {
  emit("selectVoucher", voucher);
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
    dataIndex: "giaTriGiam",
    key: "giaTriGiam",
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
    title: "Hành động",
    key: "actions",
    align: "center",
    width: 150,
    fixed: "right",
  },
];
</script>
  
<style scoped>
.voucher-card {
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}
</style>