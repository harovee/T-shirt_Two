<template>
  <div class="p-6 grid grid-cols-1 gap-6">
    <div class="flex justify-between items-center">
      <div class="flex items-center gap-2">
        <v-icon name="la-truck-solid" size="x-large" width="48" height="48" />
        <h3 class="text-2xl m-0">
          Trả hàng /
          <span class="text-gray-500 opacity-75">{{ billCode }}</span>
        </h3>
      </div>
      <div
        class="flex items-center gap-2 scale-75 transition-all cursor-pointer hover:scale-100 hover:text-red-500"
        @click="handleRedirectSearchBillToReturn()"
      >
        <v-icon
          name="gi-fast-backward-button"
          size="x-large"
          width="48"
          height="48"
        />
        <h3 class="text-2xl m-0">Quay lại</h3>
      </div>
    </div>

    <div class="p-6 shadow rounded-l">
      <h3 class="text-xl font-semibold text-gray-800">Danh sách sản phẩm</h3>
      <a-table
        :columns="columnsBill"
        :data-source="billDetailsData"
        :loading="isLoadingDetails"
        row-key="id"
        bordered
        :pagination="false"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'select'">
            <a-checkbox
              :checked="selectedProducts.some((p) => p.id === record.id)"
              @change="() => handleSelectProduct(record)"
            />
          </template>
          <template v-if="column.key === 'catalog'">
            {{ record.catalog }}
          </template>
          <template v-if="column.key === 'sanPham'">
            {{ record.tenSanPham }} - {{ record.tenMau }} -
            <a-tag>{{ record.tenKC }}</a-tag>
          </template>
          <template v-else-if="column.key === 'anhSanPhamChiTiet'">
            <Image
              :src="record.anhSanPhamChiTiet"
              width="40"
              height="40"
              alt="img"
            />
          </template>
          <template v-if="column.key === 'soLuong'">
            <div class="flex items-center justify-center gap-2">
              <a-button
                type="text"
                size="small"
                :disabled="
                  soLuongMap[record.id] <= 1 ||
                  !selectedProducts.some((p) => p.id === record.id)
                "
                @click="decreaseQuantity(record)"
              >
                -
              </a-button>

              <div
                class="shadow rounded-xl border border-gray-300 px-2 py-1 flex items-center justify-center gap-1"
              >
                <a-input
                  class="mb-1"
                  type="number"
                  v-model:value="soLuongMap[record.id]"
                  @change="handleInputQuantity(record)"
                  :min="1"
                  :max="record.soLuong"
                  :style="{
                    width: '20px',
                    textAlign: 'center',
                    padding: '0',
                    border: 'none',
                    outline: 'none',
                    boxShadow: 'none',
                    appearance: 'textfield',
                    fontSize: '15px' /* Điều chỉnh cỡ chữ */,
                  }"
                />
                <span class="text-gray-600 text-[14px]"
                  >/
                  {{
                    billDetailsData.find((p) => p.id === record.id)?.soLuong ||
                    0
                  }}</span
                >
              </div>

              <a-button
                type="text"
                size="small"
                :disabled="
                  soLuongMap[record.id] >=
                    (billDetailsData.find((p) => p.id === record.id)?.soLuong ||
                      0) || !selectedProducts.some((p) => p.id === record.id)
                "
                @click="increaseQuantity(record)"
              >
                +
              </a-button>
            </div>
          </template>

          <template v-if="column.key === 'gia'">
            {{ record.gia ? formatCurrencyVND(record.gia) : "0đ" }}
          </template>
        </template>
      </a-table>
    </div>

    <div class="grid grid-cols-3 gap-6 mt-2">
      <div class="col-span-2 p-4 border rounded shadow-md bg-white">
        <h3 class="text-lg font-semibold text-gray-800">
          <v-icon name="fa-clipboard-list" size="18" class="mr-2" />
          Danh sách sản phẩm trả
        </h3>

        <a-table
          :columns="columnsRefund"
          :data-source="selectedProducts"
          row-key="id"
          bordered
          :pagination="false"
          class="mt-3"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'sanPham'">
              <div class="flex items-center gap-3">
                <Image
                  :src="record.anhSanPhamChiTiet"
                  width="40"
                  height="40"
                  alt="img"
                />
                <div class="ms-8">
                  <div class="font-semibold text-center">
                    {{ record.tenSanPham }}
                  </div>
                  <div class="text-gray-500 text-sm">
                    {{ record.tenMau }} - <a-tag>{{ record.tenKC }}</a-tag>
                  </div>
                </div>
              </div>
            </template>

            <template v-if="column.key === 'soLuong'">
              <div class="text-center">
                <a-tag>{{ record.soLuong }}</a-tag>
              </div>
            </template>

            <template v-if="column.key === 'gia'">
              <div class="text-center">{{ formatCurrencyVND(record.gia) }}</div>
            </template>

            <template v-if="column.key === 'tong'">
              <div class="text-center text-red-500 font-semibold">
                {{ formatCurrencyVND(record.gia * record.soLuong) }}
              </div>
            </template>

            <template v-if="column.key === 'ghiChu'">
              <a-textarea
                v-model:value="record.ghiChu"
                placeholder="Nhập ghi chú..."
                :auto-size="{ minRows: 1, maxRows: 4 }"
              />
            </template>
          </template>
        </a-table>
      </div>

      <div class="col-span-1 p-4 border rounded shadow-md">
        <h3 class="text-xl font-semibold text-gray-800 text-center">
          Thông tin hoàn trả
        </h3>
        <div class="bg-gray-100 p-3 rounded-lg mb-4">
          <p class="flex items-center gap-2">
            <v-icon name="fa-user" size="18" />
            <strong>Khách hàng:</strong>
            {{ billByMaHD?.data?.tenKhachHang || "Khách lẻ" }}
          </p>
          <p class="flex items-center gap-2">
            <v-icon name="fa-map-marker-alt" size="18" />
            <strong>Người nhận:</strong>
            {{ billByMaHD?.data?.tenNguoiNhan || "Chưa có thông tin" }}
          </p>
          <p class="flex gap-2">
            <v-icon name="fa-home" size="18" />
            <strong style="width: 60px">Địa chỉ:</strong>
            {{ billByMaHD?.data?.diaChiNguoiNhan || "Chưa có thông tin" }}
          </p>
        </div>

        <div class="">
          <p class="flex justify-between text-lg">
            <span>Tổng tiền</span>
            <span class="text-red-500 font-bold">{{
              billByMaHD?.data?.tongTien
                ? formatCurrencyVND(billByMaHD?.data?.tongTien)
                : "0 đ"
            }}</span>
          </p>
          <p class="flex justify-between text-lg">
            <span>Giảm giá</span>
            <span class="text-red-500 font-bold">{{
              billByMaHD?.data?.tienGiam
                ? formatCurrencyVND(billByMaHD?.data?.tienGiam)
                : "0 đ"
            }}</span>
          </p>
          <p class="flex justify-between text-lg">
            <span>Số tiền hoàn trả</span>
            <span class="text-red-500 font-bold">{{
              billByMaHD?.data ? formatCurrencyVND(thanhTienHoanTra) : "0 đ"
            }}</span>
          </p>
        </div>

        <a-button
          class="border border-orange-500 bg-transparent text-orange-500 hover:border-orange-300"
          style="width: 100%"
        >
          Trả hàng
        </a-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path";
import router from "@/infrastructure/routes/router";
import { useGetBillRefundByMaHD } from "@/infrastructure/services/service/admin/bill.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { computed, reactive, ref, watch } from "vue";
import { ColumnType } from "ant-design-vue/es/table";
import { useGetBillDetailRefundByMaHD } from "@/infrastructure/services/service/admin/bill-detail.action";
import { Checkbox, notification } from "ant-design-vue";
import { formatCurrencyVND } from "@/utils/common.helper";
import { Image } from "ant-design-vue";
import { h } from "vue";
import { warningNotiSort } from "@/utils/notification.config";

// Lấy thông tin từ URL
const billCode = computed(() => {
  const currentUrl = window.location.href;
  const match = currentUrl.match(/\/admin\/refund\/([^\/]+)/); // Chấp nhận tất cả ký tự sau `/refund/`

  if (match) {
    return match[1];
  } else {
    router.push({ name: ROUTES_CONSTANTS.NOT_FOUND.name });
    notification.warning({
      message: "Thông báo",
      description: "Không tìm thấy đơn hàng trên",
      duration: 4,
    });
    return "idNotFound";
  }
});

// console.log(billCode.value);

const thanhTienHoanTra = ref(0); // Tổng tiền hoàn trả

// Danh sách sản phẩm được chọn
const selectedProducts = ref<any[]>([]);

// Hàm xử lý chọn/bỏ chọn sản phẩm
const handleSelectProduct = (product) => {
  const index = selectedProducts.value.findIndex((p) => p.id === product.id);

  if (index === -1) {
    selectedProducts.value.push({
      ...product,
      maxSoLuong: product.soLuong || 0,
      soLuong: soLuongMap[product.id] || product.soLuong, // Đồng bộ số lượng
    });
  } else {
    selectedProducts.value.splice(index, 1);
  }
  updateTotalRefund();
};

watch(
  selectedProducts,
  (newVal) => {
    console.log(
      "Danh sách sản phẩm trả:",
      newVal.map((p) => ({
        id: p.id,
        tenSanPham: p.tenSanPham,
        soLuong: p.soLuong,
      }))
    );
  },
  { deep: true }
);

//quay lại tìm kiếm
const handleRedirectSearchBillToReturn = () => {
  router.push({
    name: ROUTES_CONSTANTS.ADMIN.children.BILL.children.BILL_REFUND.name,
  });
};

//dữ liệu hóa đơn
const { data: billByMaHD } = useGetBillRefundByMaHD(billCode.value, {
  refetchOnWindowClose: false,
  placeholderData: keepPreviousData,
});

//dữ liệu hóa đơn chi tiết
const { data: billDetailsByMaHD, isLoading: isLoadingDetails } =
  useGetBillDetailRefundByMaHD(billCode.value, {
    refetchOnWindowClose: false,
    placeholderData: keepPreviousData,
  });

const billDetailsData = computed(() => {
  // console.log("Dữ liệu API:", billDetailsByMaHD?.value);
  return Array.isArray(billDetailsByMaHD?.value?.data)
    ? billDetailsByMaHD.value.data
    : [];
});

const soLuongMap = reactive(
  Object.fromEntries(selectedProducts.value.map((p) => [p.id, p.soLuong]))
);

// Hàm cập nhật số lượng khi nhập vào input
const handleInputQuantity = (record) => {
  let value = parseInt(soLuongMap[record.id], 10);

  if (isNaN(value) || value < 1) {
    warningNotiSort("Số lượng phải lớn hơn 0!");
    value = 1; // Giới hạn nhỏ nhất là 1
  } else if (value > record.soLuong) {
    warningNotiSort(`Số lượng tối đa là ${record.soLuong}`);
    value = record.soLuong; // Giới hạn tối đa
  }

  soLuongMap[record.id] = value; // Cập nhật số lượng hợp lệ
  // Cập nhật tổng tiền hoàn trả
  updateTotalRefund();
};

const updateTotalRefund = () => {
  let total = 0;
  selectedProducts.value.forEach((item) => {
    const soLuong = soLuongMap[item.id] || 0;
    const gia = item.gia || 0;
    const tienGiam = item.tienGiam || 0; // Đảm bảo không bị `undefined`

    total += soLuong * gia - tienGiam;
  });

  thanhTienHoanTra.value = total;
};

/// Khi dữ liệu sản phẩm thay đổi, cập nhật số lượng ban đầu
watch(
  billDetailsData,
  (newData) => {
    newData.forEach((record) => {
      if (!(record.id in soLuongMap)) {
        soLuongMap[record.id] = record.soLuong; // Gán số lượng ban đầu
      }
    });
  },
  { immediate: true }
);

// Theo dõi sự thay đổi của soLuongMap để cập nhật vào danh sách sản phẩm trả
watch(soLuongMap, () => {
  selectedProducts.value = selectedProducts.value.map((product) => ({
    ...product,
    soLuong: soLuongMap[product.id] ?? product.soLuong,
  }));
});
// Hàm tăng số lượng
const increaseQuantity = (record) => {
  const maxSoLuong =
    billDetailsData.value.find((p) => p.id === record.id)?.soLuong || 0;
  if (soLuongMap[record.id] < maxSoLuong) {
    soLuongMap[record.id]++;

    // Cập nhật số lượng trong selectedProducts nếu sản phẩm đã được chọn
    const selectedProduct = selectedProducts.value.find(
      (p) => p.id === record.id
    );
    if (selectedProduct) {
      selectedProduct.soLuong = soLuongMap[record.id];
    }
  }
  updateTotalRefund();
};

const decreaseQuantity = (record) => {
  if (soLuongMap[record.id] > 1) {
    soLuongMap[record.id]--;

    // Cập nhật số lượng trong selectedProducts nếu sản phẩm đã được chọn
    const selectedProduct = selectedProducts.value.find(
      (p) => p.id === record.id
    );
    if (selectedProduct) {
      selectedProduct.soLuong = soLuongMap[record.id];
    }
  }
  updateTotalRefund();
};

const isAllSelected = computed(() => {
  return (
    billDetailsData.value.length > 0 &&
    billDetailsData.value.every((item) =>
      selectedProducts.value.some((p) => p.id === item.id)
    )
  );
});

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedProducts.value = [];
  } else {
    selectedProducts.value = [...billDetailsData.value];
  }
  // console.log("Danh sách sản phẩm đã chọn:", selectedProducts.value);
  updateTotalRefund();
};

const columnsBill: ColumnType[] = [
  {
    key: "select",
    align: "center",
    fixed: "left",
    width: 60,
    title: () =>
      h(Checkbox, {
        checked: isAllSelected.value,
        onChange: toggleSelectAll,
      }),
  },
  {
    title: "#",
    dataIndex: "catalog",
    key: "catalog",
    ellipsis: true,
    width: 50,
    align: "center",
  },
  {
    title: "Ảnh",
    dataIndex: "anhSanPhamChiTiet",
    key: "anhSanPhamChiTiet",
    ellipsis: true,
    width: 150,
    align: "center",
  },
  {
    title: "Sản phẩm",
    dataIndex: "sanPham",
    key: "sanPham",
    ellipsis: true,
    width: 200,
    align: "center",
  },
  {
    title: "Số lượng",
    dataIndex: "soLuong",
    key: "soLuong",
    ellipsis: true,
    width: 200,
    align: "center",
  },
  {
    title: "Đơn giá",
    dataIndex: "gia",
    key: "gia",
    ellipsis: true,
    width: 200,
    align: "center",
  },
];

const columnsRefund: ColumnType[] = [
  {
    title: "Sản phẩm",
    dataIndex: "sanPham",
    key: "sanPham",
    width: 250,
    align: "center",
  },
  {
    title: "Số lượng",
    dataIndex: "soLuong",
    key: "soLuong",
    align: "center",
    width: 80,
  },
  {
    title: "Đơn giá",
    dataIndex: "gia",
    key: "gia",
    align: "center",
    width: 100,
  },
  {
    title: "Tổng",
    dataIndex: "tong",
    key: "tong",
    align: "center",
    width: 100,
  },
  {
    title: "Ghi chú",
    dataIndex: "ghiChu",
    key: "ghiChu",
    width: 200,
  },
];
</script>

<style scoped>
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type="number"] {
  -moz-appearance: textfield;
}
</style>
