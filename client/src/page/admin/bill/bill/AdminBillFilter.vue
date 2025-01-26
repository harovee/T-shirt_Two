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
          {{ option.label }}
        </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="Từ ngày" class="col-span-2 md:col-span-1 lg:col-span-1">
      <a-date-picker
        placeholder="Chọn ngày bắt đầu"
        format="YYYY-MM-DD"
        style="width: 100%"
        @change="onChangeFilter('ngayBatDau', $event)"
      />
    </a-form-item>

    <a-form-item
      label="Đến ngày"
      class="col-span-2 md:col-span-1 lg:col-span-1"
    >
      <a-date-picker
        placeholder="Chọn ngày kết thúc"
        format="YYYY-MM-DD"
        style="width: 100%"
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
import { defineEmits, ref, watch } from "vue";
import {
  BillPropsParams,
  FindBillRequest,
} from "@/infrastructure/services/api/admin/bill.api";

const emit = defineEmits(["filter"]);

const params = ref<BillPropsParams>({
  page: 1,
  keyword: null,
  trangThai: null,
  ngayBatDau: null,
  ngayKetThuc: null,
  loaiHD: null,
});

const statusOptions = [
  { label: "Tất cả", value: null },
  { label: "Thành công", value: "Thành công" },
  { label: "Chờ xác nhận", value: "Chờ xác nhận" },
  { label: "Chờ giao hàng", value: "Chờ giao hàng" },
  { label: "Đang vận chuyển", value: "Đang vận chuyển" },
  { label: "Đã giao hàng", value: "Đã giao hàng" },
  { label: "Đã thanh toán", value: "Đã thanh toán" },
  { label: "Trả hàng", value: "Trả hàng" },
];

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
  if (value && typeof value === "object") {
    // Nếu value là một đối tượng Date, chuyển thành timestamp
    params.value[key] = value instanceof Date ? value.getTime() : new Date(value).getTime();  
  } else if (value) {
    params.value[key] = value;
  } else {
    params.value[key] = null;
  }

  debouncedEmit();
}

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

<style scoped></style>
