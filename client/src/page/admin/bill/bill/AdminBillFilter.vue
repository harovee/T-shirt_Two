<template>
  <a-form
    layout="vertical"
    class="grid grid-cols-4 gap-4 md:grid-cols-1 lg:grid-cols-3"
  >
    <a-form-item
      label="Tìm kiếm"
      class="col-span-3 md:col-span-3 lg:col-span-2"
    >
      <a-input
        v-model:value="params.keyword"
        placeholder="Nhập mã, loại hóa đơn, hoặc bất kỳ thông tin khách hàng"
        allowClear
        @change="onChangeInput('keyword', $event)"
      />
    </a-form-item>

    <a-form-item
      label="Trạng thái"
      class="col-span-3 md:col-span-3 lg:col-span-1"
    >
      <a-select
        v-model:value="params.trangThai"
        @change="onChangeFilter('trangThai', $event)"
        placeholder="Chọn trạng thái"
        allowClear
      >
        <a-select-option
          v-for="option in statusOptions"
          :key="option.value"
          :value="option.value"
        >
          <span class="flex justify-between items-center w-full">
            <span>{{ option.label }}</span>
            <a-badge
              v-if="option.value !== 'all' && getStatusCount(option.value) > 0"
              :count="getStatusCount(option.value)"
              :offset="[10, 0]"
              :show-zero="true"
              color="blue"
              class="custom-badge"
            />
          </span>
        </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="Từ ngày" class="col-span-2 md:col-span-1 lg:col-span-1">
      <a-date-picker
        v-model:value="ngayBatDau"
        placeholder="Chọn ngày bắt đầu"
        format="DD-MM-YYYY"
        style="width: 100%"
        allowClear
        @change="onChangeFilter('ngayBatDau', $event)"
      />
    </a-form-item>

    <a-form-item
      label="Đến ngày"
      class="col-span-2 md:col-span-1 lg:col-span-1"
    >
      <a-date-picker
        placeholder="Chọn ngày kết thúc"
        format="DD-MM-YYYY"
        style="width: 100%"
        allowClear
        @change="onChangeFilter('ngayKetThuc', $event)"
      />
    </a-form-item>

    <!-- Loại hóa đơn -->
    <a-form-item
      label="Loại hóa đơn"
      class="col-span-4 md:col-span-2 lg:col-span-1"
    >
      <a-radio-group
        v-model:value="params.loaiHD"
        @change="onChangeFilter('loaiHD', $event.target.value)"
      >
        <a-radio
          v-for="typeHD in typeOptions"
          :key="typeHD.value"
          :value="typeHD.value"
        >
          {{ typeHD.label }}</a-radio
        >
      </a-radio-group>
    </a-form-item>
  </a-form>
</template>

<script setup lang="ts">
import { debounce } from "lodash";
import { defineEmits, onMounted, ref, watch } from "vue";
import {
  BillPropsParams,
  FindBillRequest,
} from "@/infrastructure/services/api/admin/bill.api";
import dayjs from "dayjs";
import { getBillStatusCount } from "@/infrastructure/services/api/admin/bill.api";

const emit = defineEmits(["filter"]);

const ngayBatDau = ref(dayjs().startOf("day"));

const params = ref<BillPropsParams>({
  page: 1,
  keyword: null,
  trangThai: null,
  ngayBatDau: ngayBatDau.value.toDate().getTime(),
  ngayKetThuc: null,
  loaiHD: null,
});

const statusCounts = ref<{ [key: string]: number }>({});

const fetchStatusCounts = async () => {
  try {
    const response = await getBillStatusCount();
    // console.log("✅ Dữ liệu trả về từ API:", response);

    statusCounts.value = response.data || {};
  } catch (error) {
    // console.error("❌ Lỗi khi lấy số lượng trạng thái:", error);
  }
};

onMounted(fetchStatusCounts);

const statusOptions = ref([
  { label: "Tất cả", value: "all" },
  { label: "Thành công", value: "Thành công" },
  { label: "Chờ xác nhận", value: "Chờ xác nhận" },
  { label: "Chờ giao hàng", value: "Chờ giao hàng" },
  { label: "Đang vận chuyển", value: "Đang vận chuyển" },
  { label: "Đã giao hàng", value: "Đã giao hàng" },
  { label: "Đã thanh toán", value: "Đã thanh toán" },
  { label: "Trả hàng", value: "Trả hàng" },
]);

// Hàm lấy số lượng đơn hàng theo trạng thái
const getStatusCount = (status: string) => {
  return statusCounts.value[status] || 0;
};

const typeOptions = [
  { label: "Tất cả", value: null },
  { label: "Tại quầy", value: "Tại quầy" },
  { label: "Online", value: "Online" },
];

const debouncedEmit = debounce(() => {
  const payload = { ...params.value };
  emit("filter", payload);
}, 1000);

function onChangeFilter(key: keyof FindBillRequest, value: any) {
  if (key === "ngayBatDau") {
    if (value === null) {
      params.value[key] = null;
    } else {
      if (dayjs.isDayjs(value)) {
        params.value[key] = value.toDate().getTime();
      } else {
        params.value[key] = null;
      }
    }
  } else if (value && typeof value === "object") {
    if (dayjs.isDayjs(value)) {
      params.value[key] = value.toDate().getTime();
    } else {
      params.value[key] = new Date(value).getTime();
    }
  } else if (value) {
    params.value[key] = value;
  } else {
    params.value[key] = null;
  }

  if (key === "trangThai" && value === "all") {
    params.value[key] = null; // Chuyển "all" thành null khi gửi API
  } else {
    params.value[key] = value;
  }
  debouncedEmit();
}

// Đồng bộ giá trị ngayBatDau với params khi ref thay đổi
watch(ngayBatDau, (newValue) => {
  if (newValue === null) {
    params.value.ngayBatDau = null;
  } else {
    params.value.ngayBatDau = newValue.toDate().getTime();
  }
  debouncedEmit();
});

// Gọi debouncedEmit ngay sau khi component được mounted
onMounted(() => {
  debouncedEmit();
});

function onChangeInput(key: keyof FindBillRequest, e: any) {
  params.value[key] = e.target.value;
  debouncedEmit();
}

watch(
  params,
  () => {
    debouncedEmit();
  },
  { deep: true }
);
</script>

<style scoped>
.custom-badge {
  margin-right: 10px;
}
</style>

