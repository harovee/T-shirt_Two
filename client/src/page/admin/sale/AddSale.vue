<template>
  <div class="p-2 grid grid-cols-1 gap-6">
      <div class="flex justify-between items-center">
          <div class="flex items-center gap-2">
              <v-icon name="md-percent-round" size="x-large" width="48" height="48" />
              <h3 class="text-xl m-0">Thêm đợt giảm giá </h3>
          </div>
          <div class="flex items-center gap-2 scale-75 transition-all cursor-pointer
                  hover:scale-90 hover:text-red-500" @click="handleRedirectClient()">
              <v-icon name="gi-fast-backward-button" size="x-large" width="48" height="48" />
              <h3 class="text-xl m-0">Quay lại</h3>
          </div>
      </div>
  </div>
  <div class="p-3 grid grid-cols-5 gap-6">
      <div class="col-span-5 md:col-span-5 lg:col-span-2 w-full h-fit shadow-md flex justify-center">
          <div class="w-[30rem] p-5">
              <h4 class="text-lg font-semibold mb-4">Chi tiết đợt giảm giá</h4>
              <!-- FORM CHI TIẾT ĐỢT GIẢM GIÁ -->
              <a-form ref="formRef" :model="formState" :rules="rules" layout="vertical">
                    <a-form-item class="m-0 mt-2" ref="ten" label="Tên" name="ten" required>
                        <a-input v-model:value="formState.ten" />
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" ref="loai" label="Loại" name="loai" required>
                            <a-radio-group v-model:value="formState.loai">
                              <a-radio value="PERCENT">%</a-radio>
                              <a-radio value="VND">Tiền</a-radio>
                            </a-radio-group>
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" ref="giaTri" label="Giá trị" name="giaTri" required >
                        <a-input-number v-model:value="formState.giaTri" min="1" style="width: 100%"></a-input-number>
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" v-if="formState.loai == 'VND'" ref="giaTriGiamToiDa" label="Giá trị giảm tối đa" name="giaTriGiamToiDa" required>
                        <a-input-number v-model:value="formState.giaTriGiamToiDa" min="0" step="10" style="width: 100%">
                            <template #addonAfter>đ</template>
                        </a-input-number>
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" label="Thời gian" required name="ngayBatDauVaKetThuc">
                        <a-range-picker size="" style="width: 100%;" show-time format="DD/MM/YYYY HH:mm"
                            :disabled-date="disabledDate"
                            :disabled-date-time="disabledDateTime"
                            v-model:value="formState.ngayBatDauVaKetThuc"
                            :placeholder="['Ngày bắt đầu', 'Ngày kết thúc']" :presets="rangePresets" />
                    </a-form-item>

                    <!-- <a-form-item class="m-0 mt-2 hidden" label="" name="trangThai">
                        <a-checkbox checked="true">Hoạt động</a-checkbox>
                    </a-form-item> -->


                    <a-form-item class="m-0 mt-3">
                        <a-button type="primary" @click="onSubmit(1)">Thêm</a-button>
                        <a-button style="margin-left: 10px" @click="resetForm">Clear form</a-button>
                    </a-form-item>
                </a-form>

          </div>
      </div>

      <div class="col-span-3 md:col-span-5 lg:col-span-3">
              <div class="p-3 rounded-sm shadow-md h-96">
                  <h4 class="text-lg font-semibold mb-4">Danh sách sản phẩm</h4>
                  <product-table
                      :categories="listAttributes.data.value?.data.categories"
                      :id-san-phams="idSanPhams"
                      @update:idSanPhams="handleUpdateIdSanPhams"  
                  />
              </div>
              
      </div>
  </div>

  <template v-if="idSanPhams.length > 0">
    <div class="p-2 grid grid-cols-1 gap-6">
      <div class="flex justify-between items-center">
          <div class="flex items-center gap-2">
              <AppstoreAddOutlined style="font-size: 35px;" />
              <h3 class="text-xl m-0">Các sản phẩm chi tiết cần thêm vào đợt giảm giá</h3>
          </div>
          <div v-if="idSanPhamChiTiets.length > 0" class="flex items-center gap-2 scale-75 cursor-pointer"
           @click="onSubmit(2)">
                <PlusCircleOutlined two-tone-color="black" style="font-size: 35px;"  />
              <h3 class="text-xl m-0">Áp dụng</h3>
          </div>
      </div>
  </div>
  <div class="p-1">
    <ProductDetailTable 
    :attributes="listAttributes.data.value?.data" 
    :id-san-phams="idSanPhams"
    :id-san-pham-chi-tiets="idSanPhamChiTiets"
    @update:idSanPhamChiTiets="handleUpdateIdSanPhamChiTiets"  
     />
  </div>
  </template>
  


</template>

<script lang="ts">
export default {
  name: 'admin add sale',
};
</script>

<script lang="ts" setup>
import router from "@/infrastructure/routes/router.ts";
import { reactive, ref, createVNode, onMounted, watch, computed, onUnmounted } from "vue";
import type { UnwrapRef } from 'vue';
import {Modal} from "ant-design-vue";
import {ExclamationCircleOutlined, PlusCircleOutlined, AppstoreAddOutlined} from "@ant-design/icons-vue";
import type { Rule } from 'ant-design-vue/es/form';
import { keepPreviousData } from "@tanstack/vue-query";
import dayjs from 'dayjs';

import { SaleAndSaleProductRequest, SaleRequest } from "@/infrastructure/services/api/admin/sale.api.ts";
import { useCreateSale, useCreateSaleAndSaleProduct, useGetAttributes } from "@/infrastructure/services/service/admin/sale.action.ts";
import ProductTable from "./ProductTable.vue";
import ProductDetailTable from "./ProductDetailTableInAddSale.vue";
import { defaultSaleRequest, FormState, disabledDate, disabledDateTime, defaultSaleDatePickerRules} from "./base/DefaultConfig";
import { notificationType, openNotification } from "@/utils/notification.config";
import { useSaleStore } from "./base/SaleStore";

const saleStore = useSaleStore();

const listAttributes = useGetAttributes({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});
const idSanPhams = ref<string[]>([]);
const idSanPhamChiTiets = ref<string[]>([]);

const saleRequest = ref<SaleRequest>(defaultSaleRequest)
const formRef = ref();
let formState: UnwrapRef<FormState> = reactive( {
    ma: '',
    ten: '',
    loai: 'PERCENT',
    giaTri: 1,
    giaTriGiamToiDa: null,
    ngayBatDauVaKetThuc: [],
    nguoiSua: undefined,
    trangThai: true,
    createdDate: null,
    lastModifiedDate: null,
});
const rangePresets = ref(defaultSaleDatePickerRules);
const rules: Record<string, Rule[]> = {
  ten: [
      { required: true, message: 'Vui lòng nhập tên đợt giảm giá', trigger: 'change' },
      { min: 3, max: 50, message: 'Tên phải từ 3 dến 50 ký tự', trigger: 'blur' },
  ],
  giaTri: [
      { required: true, message: 'Vui lòng nhập giá trị giảm', trigger: 'change' },
      {
          validator: (rule, value) => {
              if (formState.loai === 'PERCENT' && value != null && value < 1) {
                  return Promise.reject('Giá trị giảm phải từ 1');
              }
              if (formState.loai === 'PERCENT' && value > 100) {
                  return Promise.reject('Giá trị giảm không lớn hơn 100%');
              }
              if (formState.loai !== 'PERCENT' && value > 100000000) {
                  return Promise.reject('Giá trị giảm không lớn hơn 100.000.000 vnđ');
              }
              return Promise.resolve();
          },
          trigger: 'change',
      },
  ],
  giaTriGiamToiDa: [
      { required: true, message: 'Vui lòng nhập giá trị giảm tối đa', trigger: 'change' },
      {
          validator: (rule, value) => {
              if (formState.loai === 'VND' && value != null && value <= 0 ) {
                  return Promise.reject('Giá trị giảm tối đa phải lớn hơn 0');
              }
              if (formState.loai === 'VND' && value > formState.giaTri && value != null ) {
                    return Promise.reject('Giá trị giảm tối đa không được lớn hơn giá trị giảm');
              }
              return Promise.resolve();
          },
          trigger: 'change',
      },
  ],
  ngayBatDauVaKetThuc: [{ required: true, message: 'Vui lòng chọn ngày bắt đầu và kết thúc cho đợt giảm giá', trigger: 'change', type: 'array'},
      {   
        validator: (rule, value) => {
          
          if (!value || value.length !== 2) return Promise.resolve();
            const startStr = dayjs(value[0]).format('YYYY-MM-DD HH:mm');
            const endStr = dayjs(value[1]).format('YYYY-MM-DD HH:mm');
            
            if (startStr === endStr) {
              return Promise.reject('Ngày kết thúc không được trùng ngày bắt đầu');
            }
            
            const ngayBatDau = value[0].valueOf();
            const ngayKetThuc = value[1].valueOf();
            const now = dayjs().valueOf();
            
            if (ngayBatDau < now) {
              return Promise.reject('Ngày bắt đầu không được nhỏ hơn thời điểm hiện tại');
            }
            if (ngayKetThuc < ngayBatDau) {
              return Promise.reject('Ngày kết thúc không được nhỏ hơn ngày bắt đầu');
            }
            return Promise.resolve();
          }
      },
  ],
  loai: [{ required: true, message: 'Vui lòng chọn loại đợt giảm giá', trigger: 'change' }],
};

const updateRangePresets = () => {
  rangePresets[0] = { label: 'Bây giờ', value: [dayjs().add(1, 'minute').startOf('minute'), dayjs().add(16, 'minute').startOf('minute')] };
}

function loadDataFromStore() {
  const savedSaleData = saleStore.saleData;
  if (savedSaleData) {
        Object.assign(formState, {
            ma: savedSaleData.maDotGiamGia || '',
            ten: savedSaleData.ten || '',
            loai: savedSaleData.loai || '',
            giaTri: savedSaleData.giaTri || 0,
            giaTriGiamToiDa: savedSaleData.giaTriGiamToiDa || null,
            ngayBatDauVaKetThuc: [
                savedSaleData.ngayBatDau ? dayjs(savedSaleData.ngayBatDau) : null,
                savedSaleData.ngayKetThuc ? dayjs(savedSaleData.ngayKetThuc) : null,
            ],
            nguoiSua: savedSaleData.nguoiSua || '',
            trangThai: savedSaleData.trangThai === 'ACTIVE',
            createdDate: savedSaleData.createdDate,
            lastModifiedDate: savedSaleData.lastModifiedDate,
        });
        saleStore.setSaleData(null);
    }
}

onMounted(() => {
  loadDataFromStore();
  const interval = setInterval(updateRangePresets, 60000);
  onUnmounted(() => clearInterval(interval));
});

const { mutate: createSale } = useCreateSale();
const { mutate: createSaleAndSaleProduct } = useCreateSaleAndSaleProduct();


const handleCreateSale = (dataRequest: SaleRequest) => {
    Modal.confirm({
    title: "Bạn chắc chắn muốn thêm mới mà không gán cho sản phẩm chi tiết?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        createSale(dataRequest, {
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
          openNotification(notificationType.error, error?.response?.data?.message, '');
        } else if (error?.errorFields) {
          openNotification(notificationType.warning, "Vui lòng nhập đầy đủ các trường dữ liệu", '');
        }
      }
    },
    cancelText: "Huỷ",
    onCancel() {
        Modal.destroyAll();
    },
  });
}

const handleAddSaleAndSaleProduct = (dataRequest: SaleAndSaleProductRequest) => {
    Modal.confirm({
    title: "Bạn chắc chắn muốn gán đợt mới cho các sản phẩm chi tiết đã chọn?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        createSaleAndSaleProduct(dataRequest, {
          onSuccess: (result) => {
            openNotification(notificationType.success, result?.message, '');
            handleRedirectClient();
          },
          onError: (error: any) => {
            openNotification(notificationType.error, error?.response?.data?.message, '');
          },
        });
      } catch (error: any) {
        if (error?.response) {
          openNotification(notificationType.error, error?.response?.data?.message, '');
        } else if (error?.errorFields) {
          openNotification(notificationType.warning, "Vui lòng nhập đầy đủ các trường dữ liệu", '');
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
          saleRequest.value.ma = formState.ma;
          saleRequest.value.ten = formState.ten;
          saleRequest.value.loai = formState.loai;
          saleRequest.value.giaTri = formState.giaTri;
          saleRequest.value.giaTriGiamToiDa = formState.giaTriGiamToiDa;
          saleRequest.value.ngayBatDau = formState.ngayBatDauVaKetThuc[0]?.valueOf() || null;
          saleRequest.value.ngayKetThuc = formState.ngayBatDauVaKetThuc[1]?.valueOf() || null;
          saleRequest.value.trangThai = formState.trangThai ? 'ACTIVE' : 'INACTIVE';
          
          x == 1 ? handleCreateSale(saleRequest.value) : handleAddSaleAndSaleProduct(
            {
                saleRequest: saleRequest.value,
                saleProductRequest: {idSanPhamChiTiets: idSanPhamChiTiets.value}
            }
          );
      }).catch(() => {
        openNotification(notificationType.warning, "Vui lòng nhập đúng các trường dữ liệu", '');
      });
};
const resetForm = () => {
  formRef.value.resetFields();
};

const handleUpdateIdSanPhams = (newIdSanPhams: string[]) => {
    idSanPhams.value = newIdSanPhams;
};
const handleUpdateIdSanPhamChiTiets = (newIdSanPhamChiTiets: string[]) => {
    idSanPhamChiTiets.value = newIdSanPhamChiTiets;
};


const handleRedirectClient = () => {
    router.push({ name: 'admin-sale' });
}

</script>