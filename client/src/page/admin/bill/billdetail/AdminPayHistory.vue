<template>
  <div class="table-container">
    <template v-if="data?.data && data?.data?.length > 0">
      <a-table
        :dataSource="data?.data || []"
        :columns="columnsBill"
        :pagination="false"
        :scroll="{ x: 'max-content' }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'tienKhachDua'">
            <span v-if="record?.tongTienHD">{{
              formatCurrencyVND(record.tienKhachDua)
            }}</span>
          </template>
          <template v-else-if="column.key === 'ngayTao'">
            <span v-if="record?.ngayTao">{{
              convertDateFormat(record.ngayTao)
            }}</span>
          </template>
        </template>
      </a-table>
    </template>

    <template v-else>
      <div class="flex justify-center items-center h-full">
        <a-empty description="Không có dữ liệu" />
      </div>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { FindPayHistoryRequest } from "@/infrastructure/services/api/admin/pay-history.api";
import { useGetPayHistory } from "@/infrastructure/services/service/admin/payhistory.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { onMounted, ref, watch, watchEffect } from "vue";
import { formatCurrencyVND, convertDateFormat } from "@/utils/common.helper";
import { useRoute } from "vue-router";

const props = defineProps({
  isPaymented: Boolean,
  billId: String,
});

const emit = defineEmits(["get:total-amount"]);

interface ColumnType {
  title: string;
  dataIndex: string | number;
  key: string | number;
  ellipsis?: boolean;
  width?: string;
  align?: string;
}

const params = ref<FindPayHistoryRequest>({
  idHoaDon: props.billId || "",
});

const { data, isLoading, isFetching, refetch } = useGetPayHistory(params, {
  refetchOnWindowClose: false,
  placeholderData: keepPreviousData,
  keepPreviousData: false,
});

watch(
  () => props.billId,
  (newId) => {
    params.value.idHoaDon = newId || "";
    refetch().then(() => {
      emit("get:total-amount", totalAmountPaid(data?.value?.data));
    });
  }
);

const totalAmountPaid = (listPay: any) => {
  return listPay.reduce((total, item) => total + (item.tienKhachDua || 0), 0);
};

watch(
  () => props.isPaymented,
  (newData) => {
    refetch();
  }
);

const columnsBill: ColumnType[] = [
  {
    title: "Số tiền",
    dataIndex: "tienKhachDua",
    key: "tienKhachDua",
    ellipsis: true,
    align: "center",
  },
  {
    title: "Thời gian",
    dataIndex: "ngayTao",
    key: "ngayTao",
    ellipsis: true,
    align: "center",
    width: "170px",
  },
  {
    title: "Mã giao dịch",
    dataIndex: "maGiaoDich",
    key: "maGiaoDich",
    ellipsis: true,
    align: "center",
  },
  {
    title: "PT thanh toán",
    dataIndex: "tenPhuongThuc",
    key: "tenPhuongThuc",
    ellipsis: true,
    align: "center",
  },
  {
    title: "Ghi chú",
    dataIndex: "ghiChu",
    key: "ghiChu",
    ellipsis: true,
    align: "center",
  },
  {
    title: "Nhân viên xác nhận",
    dataIndex: "nguoiTao",
    key: "nguoiTao",
    ellipsis: true,
    align: "center",
    width: "170px",
  },
];
</script>
