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
          <template v-if="column.key === 'tongTienHD'">
            <span v-if="record?.tongTienHD">{{ formatCurrencyVND(record.tongTienHD) }}</span>
          </template>
          <template v-else-if="column.key === 'ngayTao'">
            <span v-if="record?.ngayTao">{{ convertDateFormat(record.ngayTao) }}</span>
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
import { onMounted, ref, watch } from "vue";
import { formatCurrencyVND } from "@/utils/common.helper";
import { convertDateFormat } from "@/utils/common.helper";

const props= defineProps ({
  isPaymented: Boolean
})

interface ColumnType {
  title: string;
  dataIndex: string | number;
  key: string | number;
  ellipsis?: boolean;
  width?: string;
  align?: string;
}

const params = ref<FindPayHistoryRequest>({
  idHoaDon: "",
});

const getIdHoaDonFromUrl = () => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("idHoaDon") || "";
};

onMounted(() => {
  params.value.idHoaDon = getIdHoaDonFromUrl();
});

const { data, isLoading, isFetching, refetch } = useGetPayHistory(params, {
  refetchOnWindowClose: false,
  placeholderData: keepPreviousData,
});

watch(
  () => data.value,
  (newData) => {
    if (newData) {
      // console.log(newData);
    }
  },
  { immediate: true }
);

watch(
  () => props.isPaymented,
  (newData) => {
    refetch();
  }
);

const columnsBill: ColumnType[] = [
  {
    title: "Số tiền",
    dataIndex: "tongTienHD",
    key: "tongTienHD",
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
