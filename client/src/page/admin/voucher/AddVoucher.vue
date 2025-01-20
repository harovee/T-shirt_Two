<template>
  <div class="p-4 grid grid-cols-1 gap-6">
    <!-- Header -->
    <div class="flex justify-between items-center bg-white rounded-md shadow p-4">
      <div class="flex items-center gap-4">
        <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48" />
        <h3 class="text-2xl font-semibold m-0">Thêm phiếu giảm giá</h3>
      </div>
      <div 
        class="flex items-center gap-2 cursor-pointer text-gray-500 transition hover:scale-105 hover:text-red-500" 
        @click="handleRedirectClient()">
        <v-icon name="gi-fast-backward-button" size="x-large" width="48" height="48" />
        <h3 class="text-lg m-0">Quay lại</h3>
      </div>
    </div>

    <!-- Main Content -->
    <div class="grid grid-cols-1 lg:grid-cols-5 gap-8">
      <!-- Form Section -->
      <div class="col-span-5 lg:col-span-2 bg-white rounded-md shadow-md p-6">
        <h4 class="text-lg font-semibold mb-4">Chi tiết phiếu giảm giá</h4>
        <a-form ref="formRef" :model="formState" :rules="rules" layout="vertical">
          <a-form-item class="mb-4" label="Tên" name="ten" required>
            <a-input v-model:value="formState.ten" placeholder="Nhập tên đợt giảm giá" />
          </a-form-item>

          <a-form-item class="mb-4" label="Giá trị giảm" name="giaTriGiam" required>
            <a-input v-model:value="formState.giaTriGiam" min="0">
              <template #addonAfter>
                <a-radio-group v-model:value="formState.loaiGiam" option-type="default" button-style="solid">
                  <a-radio value="false">%</a-radio>
                  <a-radio value="true">Tiền</a-radio>
                </a-radio-group>
              </template>
            </a-input>
          </a-form-item>

          <a-form-item v-if="formState.loaiGiam === true" class="mb-4" label="Giá trị giảm tối đa" name="giamToiDa" required>
            <a-input v-model:value="formState.giamToiDa" min="0" step="10" placeholder="Nhập giá trị giảm tối đa">
              <template #addonAfter>đ</template>
            </a-input>
          </a-form-item>

          <a-form-item class="mb-4" label="Số lượng" name="soLuong" required>
            <a-input-number v-model:value="formState.soLuong" min="0" step="10" placeholder="Nhập số lượng" />
          </a-form-item>

          <a-form-item class="mb-4" label="Đơn tối thiểu" name="dieuKienGiam" required>
            <a-input v-model:value="formState.dieuKienGiam" min="0" step="10" placeholder="Nhập đơn tối thiểu">
              <template #addonAfter>đ</template>
            </a-input>
          </a-form-item>

          <a-form-item class="mb-4" label="Thời gian" name="ngayBatDauVaKetThuc" required>
            <a-range-picker
              size="large"
              show-time
              format="DD/MM/YYYY HH:mm"
              v-model:value="formState.ngayBatDauVaKetThuc"
              :placeholder="['Ngày bắt đầu', 'Ngày kết thúc']"
              :presets="rangePresets"
            />
          </a-form-item>

          <a-form-item class="mb-4" label="Loại phiếu giảm giá" name="trangThai">
            <a-radio-group v-model:value="formState.kieu" option-type="default" button-style="solid">
              <a-radio value="true">Cá nhân</a-radio>
              <a-radio value="false">Công khai</a-radio>
            </a-radio-group>
          </a-form-item>

          <a-form-item class="mt-6">
            <div class="flex gap-4">
              <a-button type="primary" @click="onSubmit(1)">Thêm</a-button>
              <a-button @click="resetForm">Xóa form</a-button>
            </div>
          </a-form-item>
        </a-form>
      </div>

      <!-- Khách Hàng Section -->
      <div class="col-span-5 lg:col-span-3 bg-white rounded-md shadow-md p-6">
        <h4 class="text-lg font-semibold mb-4">Danh sách khách hàng</h4>
        <div class="h-96 overflow-y-auto">
          <khach-hang-table
            :data="dataSource"
            :id-khach-hangs="idKhachHangs"
            @update:idSanPhams="handleUpdateIdKhachHangs"
          />
        </div>
      </div>
    </div>
  </div>
</template>


<script lang="ts">
export default {
  name: 'admin add voucher',
};
</script>

<script lang="ts" setup>
import router from "@/infrastructure/routes/router.ts";
import { computed, onMounted, watch, reactive, ref, createVNode } from "vue";
import type { UnwrapRef } from 'vue';
import {Modal} from "ant-design-vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import type { Rule } from 'ant-design-vue/es/form';
import { toast } from "vue3-toastify";
import { keepPreviousData } from "@tanstack/vue-query";

import {  FindKhachHangRequest, VoucherRequest } from "@/infrastructure/services/api/admin/voucher/voucher.api";
import { useCreateVoucher, useGetListKhachHang} from "@/infrastructure/services/service/admin/voucher/voucher.action";
import KhachHangTable from "./KhachHangTable.vue";
import { defaultVoucherDatePickerRules, defaultVoucherRequest, FormState } from "./base/DefaultConfig";

const params = ref<FindKhachHangRequest>({
  page: 1,
  size: 5,
  keyword: null
});

const { data } = useGetListKhachHang(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});


const idKhachHangs = ref<string[]>([]);

const dataSource = computed(() => data?.value?.data|| []);

const vocherRequest = ref<VoucherRequest>(defaultVoucherRequest)

const formRef = ref();

const formState: UnwrapRef<FormState> = reactive( {
    ten: "",
    loaiGiam: false,
    giaTriGiam: "",
    giamToiDa: "",
    soLuong: 0,
    dieuKienGiam: "",
    ngayBatDauVaKetThuc: [],
    kieu: false
});

const rangePresets = ref(defaultVoucherDatePickerRules);

const rules: Record<string, Rule[]> = {
  ten: [
      { required: true, message: 'Vui lòng nhập tên đợt giảm giá', trigger: 'change' },
      { min: 3, max: 50, message: 'Tên phải từ 3 dến 50 ký tự', trigger: 'blur' },
  ],
  giaTriGiam: [
      { required: true, message: 'Vui lòng nhập giá trị giảm', trigger: 'change' },
      {
          validator: (rule, value) => {
              if (formState.loaiGiam === true && value != null && value <= 0) {
                  return Promise.reject('Giá trị giảm phải lớn hơn 0');
              }
              if (formState.loaiGiam === false && value > 100) {
                  return Promise.reject('Giá trị giảm chỉ bé hơn hoặc bằng 100%');
              }
              return Promise.resolve();
          },
          trigger: 'change',
      },
  ],
  giamToiDa: [
      { required: true, message: 'Vui lòng nhập giá trị giảm tối đa', trigger: 'change' },
      {
          validator: (rule, value) => {
              if (formState.loaiGiam === true && value != null && value <= 0 ) {
                  return Promise.reject('Giá trị giảm tối đa phải lớn hơn 0');
              }
              return Promise.resolve();
          },
          trigger: 'change',
      },
  ],
  dieuKienGiam: [
    { 
      required: true, 
      message: "Vui lòng nhập đơn tối thiểu", 
      trigger: "blur" 
    }
  ],
  soLuong: [
      { required: true, message: 'Vui lòng nhập số lượng', 
      trigger: 'change' },
      { 
        validator: (_, value) => 
          value > 0 
            ? Promise.resolve() 
            : Promise.reject("Số lượng phải lớn hơn 0"),
        trigger: "blur"
      }
  ],
  ngayBatDauVaKetThuc: [{ 
      required: true, message: 'Vui lòng chọn ngày bắt đầu và kết thúc cho đợt giảm giá', 
      trigger: 'change', type: 'array' }],

  loaiGiam: 
      [{ required: true, message: 'Vui lòng chọn loại đợt giảm giá', 
      trigger: 'change' }],
};

const { mutate: createVoucher } = useCreateVoucher();
// const { mutate: createSaleAndSaleProduct } = useCreateSaleAndSaleProduct();


const handleCreateVoucher = (dataRequest: VoucherRequest) => {
    Modal.confirm({
    content: "Bạn chắc chắn muốn thêm?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        createVoucher(dataRequest, {
          onSuccess: (result) => {
            toast.success(result?.message);
            handleRedirectClient();
          },
          onError: (error: any) => {
            toast.error(
                error?.response?.data?.message
            );
          },
        });
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          toast.warning(
              error?.response?.data?.message
          );
        } else if (error?.errorFields) {
          toast.warning("Vui lòng nhập đầy đủ các trường dữ liệu");
        }
      }
    },
    cancelText: "Huỷ",
    onCancel() {
        Modal.destroyAll();
    },
  });
}

// const handleAddSaleAndSaleProduct = (dataRequest: SaleAndSaleProductRequest) => {
//     Modal.confirm({
//     content: "Bạn chắc chắn muốn gán đợt mới cho các sản phẩm chi tiết đã chọn?",
//     icon: createVNode(ExclamationCircleOutlined),
//     centered: true,
//     async onOk() {
//       try {
//         createSaleAndSaleProduct(dataRequest, {
//           onSuccess: (result) => {
//             toast.success(result?.message);
//             handleRedirectClient();
//           },
//           onError: (error: any) => {
//             toast.error(
//                 error?.response?.data?.message
//             );
//           },
//         });
//       } catch (error: any) {
//         console.error("🚀 ~ handleCreate ~ error:", error);
//         if (error?.response) {
//           toast.warning(
//               error?.response?.data?.message
//           );
//         } else if (error?.errorFields) {
//           toast.warning("Vui lòng nhập đầy đủ các trường dữ liệu");
//         }
//       }
//     },
//     cancelText: "Huỷ",
//     onCancel() {
//         Modal.destroyAll();
//     },
//   });
// }

const onSubmit = (x: number) => {
  formRef.value
      .validate()
      .then(() => {
          vocherRequest.value.ten = formState.ten;
          vocherRequest.value.loaiGiam = formState.loaiGiam;
          vocherRequest.value.giaTriGiam = formState.giaTriGiam;
          vocherRequest.value.giamToiDa = formState.giamToiDa;
          vocherRequest.value.dieuKienGiam = formState.dieuKienGiam;
          vocherRequest.value.soLuong = formState.soLuong;
          vocherRequest.value.ngayBatDau = (formState.ngayBatDauVaKetThuc[0]?.valueOf()) || null;
          vocherRequest.value.ngayKetThuc = formState.ngayBatDauVaKetThuc[1]?.valueOf() || null;
          // x == 1 ? handleCreateSale(vocherRequest.value) : handleAddSaleAndSaleProduct(
          //   {
          //     vocherRequest: vocherRequest.value,
          //       saleProductRequest: {idSanPhamChiTiets: idSanPhamChiTiets.value.join(',')}
          //   }
          // );

      });
};
const resetForm = () => {
  formRef.value.resetFields();
};

const handleUpdateIdKhachHangs = (newIdKhachHangs: string[]) => {
    idKhachHangs.value = newIdKhachHangs;
};


const handleRedirectClient = () => {
    router.push({ name: 'admin-voucher' });
}

</script>