<template>
  <div class="p-4 grid grid-cols-1 gap-6">
    <!-- Header -->
    <div class="flex justify-between items-center bg-white rounded-md shadow p-4">
      <div class="flex items-center gap-4">
        <v-icon name="io-pricetag-outline" size="x-large" width="48" height="48" />
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
                  <a-radio :value="false">%</a-radio>
                  <a-radio :value="true">Tiền</a-radio>
                </a-radio-group>
              </template>
            </a-input>
          </a-form-item>
          <a-form-item class="mb-4" label="Số lượng" name="soLuong" required>
            <a-input-number v-model:value="formState.soLuong" min="0" step="10" placeholder="Nhập số lượng" :disabled="formState.kieu"/>
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
              :disabled-date="disabledDate"
              :disabled-date-time="disabledDateTime"
              format="DD/MM/YYYY HH:mm"
              v-model:value="formState.ngayBatDauVaKetThuc"
              :placeholder="['Ngày bắt đầu', 'Ngày kết thúc']"
              :presets="rangePresets"
            />
          </a-form-item>

          <a-form-item class="mb-4" label="Loại phiếu giảm giá" name="trangThai">
            <a-radio-group v-model:value="formState.kieu" option-type="default" button-style="solid">
              <a-radio :value="false">Công khai</a-radio>
              <a-radio :value="true">Cá nhân<nav></nav></a-radio>
            </a-radio-group>
          </a-form-item>

          <a-form-item class="mt-6">
            <div class="flex gap-4">
              <a-button type="primary" @click="onSubmit(formState.kieu ? 2 : 1)">Thêm</a-button>
              <a-button @click="resetForm">Xóa form</a-button>
            </div>
          </a-form-item>
        </a-form>
      </div>

      <!-- Khách Hàng Section -->

      <div  class="col-span-5 lg:col-span-3 bg-white rounded-md shadow-md p-6" v-if="formState.kieu">
        <h4 class="text-lg font-semibold mb-4">Danh sách khách hàng</h4>
        <div class="h-100 overflow-y-auto">
          <khach-hang-table
            :data="dataSource"
            :id-khach-hangs="idKhachHangs"
            @update:idKhachHangs="handleUpdateIdKhachHangs"
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
import { computed, watch, reactive, ref, createVNode } from "vue";
import type { UnwrapRef } from 'vue';
import {Modal} from "ant-design-vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import type { Rule } from 'ant-design-vue/es/form';
import { toast } from "vue3-toastify";
import { keepPreviousData } from "@tanstack/vue-query";
import dayjs from 'dayjs';

import {  FindKhachHangRequest, VoucherAndCustomerVoucherRequest, PhieuGiamGiaRequest } from "@/infrastructure/services/api/admin/voucher/voucher.api";
import { useCreateCustomerVoucher, useCreateVoucher, useGetListKhachHang} from "@/infrastructure/services/service/admin/voucher/voucher.action";
import KhachHangTable from "./KhachHangTable.vue";
import { defaultVoucherDatePickerRules, defaultVoucherRequest, FormState } from "./base/DefaultConfig";
import {  notificationType, openNotification,  warningNotiSort } from "@/utils/notification.config";
import { disabledDate, disabledDateTime } from "../sale/base/DefaultConfig";

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

const voucherRequest = ref<PhieuGiamGiaRequest>(defaultVoucherRequest)

const formRef = ref();

const formState: UnwrapRef<FormState> = reactive( {
    ten: "",
    loaiGiam: false,
    giaTriGiam: "0",
    giamToiDa: "",
    soLuong: 0,
    dieuKienGiam: "0",
    ngayBatDauVaKetThuc: [],
    kieu: false
});

const rangePresets = ref(defaultVoucherDatePickerRules);

const rules: Record<string, Rule[]> = {
  ten: [
      { required: true, message: 'Vui lòng nhập tên phiếu giảm giá', trigger: 'change' },
      { min: 3, max: 50, message: 'Tên phải từ 3 dến 50 ký tự', trigger: 'blur' },
  ],

  loaiGiam:
      [{ required: true, message: 'Vui lòng chọn loại giảm giá',
      trigger: 'change'
     }],

  giaTriGiam: [
      { required: true, message: 'Vui lòng nhập giá trị giảm', trigger: 'change' },
      {
          validator: (rule, value) => {
              if (formState.loaiGiam === true && value != null && value <= 0) {
                  return Promise.reject('Giá trị giảm phải lớn hơn 0');
              }
              if (formState.loaiGiam === false && value > 100 || formState.loaiGiam === false && value <= 0) {
                  return Promise.reject('Giá trị giảm chỉ bé hơn hoặc bằng 100%');
              }
              return Promise.resolve();
          },
          trigger: 'change',
      },
  ],
  dieuKienGiam: [
  {
    required: true,
    message: 'Vui lòng nhập đơn tối thiểu',
    trigger: 'change'},
    {
      validator: (rule, value) => {
        // Chuyển đổi value thành số để so sánh
        const minOrderValue = parseFloat(value);
        const discountValue = parseFloat(formState.giaTriGiam);

        // Kiểm tra nếu giá trị không phải số hợp lệ
        if (isNaN(minOrderValue)) {
          return Promise.reject('Đơn tối thiểu phải là số');
        }

        // Kiểm tra giá trị âm
        if (minOrderValue <= 0) {
          return Promise.reject('Đơn tối thiểu phải lớn hơn 0');
        }

        // Nếu là giảm theo tiền (loaiGiam = true)
        if (formState.loaiGiam === true) {
          if (minOrderValue <= discountValue) {
            return Promise.reject('Đơn tối thiểu phải lớn hơn giá trị giảm');
          }
        }
        return Promise.resolve();
      },
      trigger: 'change'
    }
  ],
  soLuong: [
  {
    required: true,
    message: 'Vui lòng nhập số lượng',
    trigger: 'change',
    validator: (_, value) => {
      if (formState.kieu) {
        return Promise.resolve(); // Bỏ qua kiểm tra nếu là "Cá nhân"
      }
      return value > 0
        ? Promise.resolve()
        : Promise.reject("Số lượng phải lớn hơn 0");
    }
  }
],
  ngayBatDauVaKetThuc: [{ 
      required: true, message: 'Vui lòng chọn ngày bắt đầu và kết thúc cho phiếu giảm giá',
      trigger: 'change', type: 'array'
    },
    {
          validator: (rule, value) => {
          const [ngayBatDau, ngayKetThuc] = value.map((date: any) =>
          dayjs(date).valueOf()
          );
          const now = dayjs().valueOf();
          if (ngayBatDau < now) {
            return Promise.reject('Ngày bắt đầu không được nhỏ hơn thời điểm hiện tại');
          }
          if (ngayKetThuc < ngayBatDau) {
            return Promise.reject('Ngày kết thúc không được nhỏ hơn ngày bắt đầu');
          }
          return Promise.resolve();
          },
          trigger: 'change',
    }
  ],

};

const { mutate: createVoucher } = useCreateVoucher();
const { mutate: createCustomerVoucher } = useCreateCustomerVoucher();


const handleCreateVoucher = (dataRequest: PhieuGiamGiaRequest) => {

    Modal.confirm({
    content: "Bạn chắc chắn muốn thêm?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        createVoucher(dataRequest, {
          onSuccess: (result) => {
            openNotification(notificationType.success, result?.message, '');
            handleRedirectClient();
          },
          onError: (error: any) => {
            toast.error(
              openNotification(notificationType.error, error?.response?.data?.message, '')
            );
          },
        });
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          toast.warning(
            warningNotiSort(error?.response?.data?.message)
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

const handleAddVoucherAndCustomerVoucher = (dataRequest: VoucherAndCustomerVoucherRequest) => {
  console.log(dataRequest);
    Modal.confirm({
    content: "Bạn chắc chắn có muốn thêm phiếu giảm giá cho khách hàng không?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        createCustomerVoucher(dataRequest, {
          onSuccess: (result) => {
            openNotification(notificationType.success, result?.message, '');
            handleRedirectClient();
          },
          onError: (error: any) => {
            openNotification(notificationType.error, error?.response?.data?.message, '');
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

const onSubmit = (x: number) => {
  formRef.value
      .validate()
      .then(() => {
          if (formState.kieu && idKhachHangs.value.length === 0) {
              toast.warning("Vui lòng chọn ít nhất một khách hàng!");
              return; // Dừng quá trình submit
          }
          voucherRequest.value.ten = formState.ten;
          voucherRequest.value.loaiGiam = formState.loaiGiam;
          voucherRequest.value.giaTriGiam = formState.giaTriGiam;
          voucherRequest.value.giamToiDa = formState.giamToiDa;
          voucherRequest.value.dieuKienGiam = formState.dieuKienGiam;
          voucherRequest.value.kieu = formState.kieu;
          voucherRequest.value.soLuong = formState.kieu ? idKhachHangs.value.length : formState.soLuong;
          voucherRequest.value.ngayBatDau = formState.ngayBatDauVaKetThuc[0]?.valueOf() || null;
          voucherRequest.value.ngayKetThuc = formState.ngayBatDauVaKetThuc[1]?.valueOf() || null;
          x === 1 ?
             handleCreateVoucher(voucherRequest.value)
                    :
                handleAddVoucherAndCustomerVoucher({
                  phieuGiamGiaRequest: voucherRequest.value,
                  voucherKhachHangRequest : { idKhachHangs: idKhachHangs.value},
              });

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

watch(
  () => formState.kieu,
  (newValue) => {
    if (newValue) {
      // Nếu đổi sang "Cá nhân"
      //console.log("Đã chuyển sang phiếu giảm giá Cá nhân");
      idKhachHangs.value = []; // Reset danh sách khách hàng được chọn
    } else {
      // Nếu đổi về "Công khai"
     // console.log("Đã chuyển sang phiếu giảm giá Công khai");
      idKhachHangs.value = []; // Reset danh sách khách hàng
    }
  }
);

</script>