<template>
  <a-modal :open="open" @ok="handleClose" key="" :width="'1400px'" :okText="'Xác nhận'"
    :cancelText="'Hủy bỏ'">
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
        <div v-if="column.key === 'giaTriGiam'" class="text-left">
            <span v-if="record.loaiGiam === true || record.loaiGiam === 'true'">
                {{ formatCurrencyVND(record.giaTriGiam) }}
            </span>
            <span v-else-if="record.loaiGiam === false || record.loaiGiam === 'false'">
                {{ record.giaTriGiam }} %
          </span>
            <span v-else>
                Không xác định
          </span>
        </div>
        <div v-if="column.key === 'dieuKienGiam'" class="text-left">
                {{ formatCurrencyVND(record.dieuKienGiam) }}
        </div>
        <div v-else-if="column.key === 'kieu'" >
            <a-tag v-if="record.kieu === true || record.kieu === 'true'" color="blue">Cá nhân</a-tag>
            <a-tag v-else-if="record.kieu === false || record.kieu === 'false'" color="green">Công khai</a-tag>
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
import { formatCurrencyVND, getDateFormat, getDateTimeMinutesFormat } from "@/utils/common.helper";
import { defineProps, computed, defineEmits, ref, watch, onMounted } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import { VoucherResponse, FindVoucherRequest } from "@/infrastructure/services/api/admin/voucher/voucher.api";
import { useGetListVoucher } from "@/infrastructure/services/service/admin/voucher/voucher.action";
import { convertDateFormatTime } from "@/utils/common.helper";
import { useRoute } from "vue-router";

const pageSize = ref(5);
const current1 = ref(1);

const props = defineProps<{ open: boolean }>();

const emit = defineEmits(["handleClose", "selectVoucher"]);

const handleClose = () => emit("handleClose");

// interface DataType {
//     id: string;
//     ten : string;
//     soLuong: number;
//     dieuKienGiam: string;
//     giamToiDa: string;
//     loaiGiam: boolean;
//     kieu: boolean;
//     giaTriGiam: string;
//     ngayBatDau: number;
//     ngayKetThuc: number;
//     trangThai : string;
// }



const params = ref<FindVoucherRequest>({
  page: 1,
  size: 5,
  keyword: "",
});

const { data } = useGetListVoucher(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

// const dataVoucher: VoucherResponse[] | any = computed(() => {
//   return (
//     data?.value?.data?.data.map((e: any) => ({
//       key: e.id || "",
//       name: e.name || "",
//       phoneNumber: e.phoneNumber || "",
//       email: e.email || "",
//       profilePicture: e.profilePicture || "",
//     })) || []
//   );
// });

const dataVoucher = computed(() => data?.value?.data?.data || []);

const handleSearch = (newValue: string) => {
  params.value.keyword = newValue;
  params.value.page = 1;
};

watch(current1, () => {
  params.value.page = current1.value === 0 ? 1 : current1.value;
});

watch(current1, () => {
  params.value.page = current1.value === 0 ? 1 : current1.value;
});

const handleSelectVoucher = (voucher: VoucherResponse) => {
  emit("selectVoucher", voucher, dataVoucher.value);
  handleClose();
  console.log(voucher);
};

const columns: TableColumnType<VoucherResponse>[] = [
  {
    title: "#",
    dataIndex: "catalog",
    key: "catalog",
    ellipsis: true,
    width: 50,
    align: "center"
  },
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
  