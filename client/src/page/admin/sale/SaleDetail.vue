<template>
    <div class="p-3 grid grid-cols-1 gap-6">
        <div class="flex justify-between items-center">
            <div class="flex items-center gap-2">
                <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48" />
                <h3 class="text-xl m-0">Thông tin đợt giảm giá </h3>
            </div>
            <div class="flex items-center gap-2 scale-75 transition-all cursor-pointer
                    hover:scale-90 hover:text-red-500" @click="handleRedirectClient()">
                <v-icon name="gi-fast-backward-button" size="x-large" width="48" height="48" />
                <h3 class="text-xl m-0">Quay lại</h3>
            </div>
        </div>
    </div>
    <div class="p-2 grid grid-cols-5 gap-6">
        <div class="col-span-5 md:col-span-5 lg:col-span-2 w-full shadow-md flex justify-center">
            <div class="w-[30rem] p-5">
                <!-- FORM CHI TIẾT ĐỢT GIẢM GIÁ -->

                <a-form ref="formRef" :model="formState" :rules="rules" layout="vertical" :disabled="!currentStatus">
                    <div class="flex justify-start" style="width: 100%;">
                        <a-alert banner :showIcon="false" :message="'Ngày tạo: ' + getDateFormat(formState.createdDate)"
                            type="success" class="p-0" style="font-size: 12px;" />
                        <a-alert banner :showIcon="false" :message="'Ngày sửa gần đây: ' + getDateFormat(formState.lastModifiedDate)"
                            type="warning" class="p-0 ms-2" style="font-size: 12px;" />
                    </div>
                    <a-form-item class="m-0 mt-2" ref="ma" label="Mã" name="ma">
                        <a-input disabled="" v-model:value="formState.ma" />
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" ref="loai" label="Loại" name="loai" required>
                            <a-radio-group v-model:value="formState.loai">
                              <a-radio value="PERCENT">%</a-radio>
                              <a-radio value="VND">vnđ</a-radio>
                            </a-radio-group>
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" ref="giaTri" label="Giá trị" name="giaTri" required >
                        <a-input-number v-model:value="formState.giaTri" min="0" style="width: 100%"></a-input-number>
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" v-if="formState.loai == 'VND'" ref="giaTriGiamToiDa" label="Giá trị giảm tối đa" name="giaTriGiamToiDa" required>
                        <a-input-number v-model:value="formState.giaTriGiamToiDa" min="0" step="10" style="width: 100%">
                            <template #addonAfter>đ</template>
                        </a-input-number>
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" label="Thời gian" required name="ngayBatDauVaKetThuc">
                        <a-range-picker size="large" style="" show-time format="DD/MM/YYYY HH:mm"
                            v-model:value="formState.ngayBatDauVaKetThuc"
                            :disabled-date="disabledDate"
                            :disabled-date-time="disabledDateTime"
                            :placeholder="['Ngày bắt đầu', 'Ngày kết thúc']" :presets="rangePresets" />
                        <div class="text-right text-sm text-red-500">{{ currentStatus ? '':'Đã kết thúc' }}</div>
                    </a-form-item>

                    <a-form-item class="m-0 mt-2" label="" name="trangThai" v-if="currentStatus">
                        <a-checkbox v-model:checked="formState.trangThai">Hoạt động</a-checkbox>
                    </a-form-item>


                    <a-form-item class="m-0 mt-3"
                     v-if="currentStatus"
                    >
                        <a-button
                         type="primary" @click="onSubmit(1)">Cập nhật</a-button>
                        <a-button style="margin-left: 10px" @click="resetForm">Clear form</a-button>
                    </a-form-item>
                </a-form>

            </div>
        </div>

        <div class="col-span-3 md:col-span-5 lg:col-span-3 h-100 p-3  rounded-sm shadow-md"  >
            <a-tabs v-model:activeKey="activeTabKey" v-if="currentStatus">
                    <a-tab-pane key="1">
                    <template #tab>
                        <span><FileDoneOutlined />Sản phẩm áp dụng trong đợt</span>
                    </template>
                        <div>
                            <product-detail-table-in-detail-sale 
                            :id-dot-giam-gia="saleId || ''"
                            :current-status="currentStatus"
                            ></product-detail-table-in-detail-sale>
                        </div>
                    </a-tab-pane>
                    <a-tab-pane key="2">
                        <template #tab>
                        <span><TagsOutlined />Thêm sản phẩm vào đợt</span>
                    </template>
                        <div>
                            <product-table
                            :categories="listAttributes.data.value?.data.categories"
                            :id-san-phams="idSanPhams"
                            @update:idSanPhams="handleUpdateIdSanPhams"  
                            />
                        </div>
                        
                    </a-tab-pane>
                </a-tabs>
                <div class="p-3 rounded-sm shadow-md"  v-if="!currentStatus">
                    <product-detail-table-in-detail-sale 
                            :id-dot-giam-gia="saleId || ''"
                            :current-status="currentStatus"
                            ></product-detail-table-in-detail-sale>
                </div>
        </div>
        
    </div>

    <template v-if="idSanPhams.length > 0">
    <div class="p-2 grid grid-cols-1 gap-6">
      <div class="flex justify-between items-center">
          <div class="flex items-center gap-2">
              <v-icon name="md-switchaccount-round" size="x-large" width="48" height="48" />
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
    name: 'admin client detail',
};
</script>

<script lang="ts" setup>
import router from "@/infrastructure/routes/router.ts";
import { useRoute } from 'vue-router';
import {
    TagsOutlined, FileDoneOutlined,
    ExclamationCircleOutlined,
    PlusCircleOutlined
} from '@ant-design/icons-vue';
import { Modal, notification } from "ant-design-vue";
import { computed, onMounted, watch, reactive, ref, createVNode } from "vue";
import type { UnwrapRef } from 'vue';
import type { Rule } from 'ant-design-vue/es/form';
import { toast } from "vue3-toastify";
import { keepPreviousData } from "@tanstack/vue-query";
import { useAuthStore } from "@/infrastructure/stores/auth.ts";
import { SaleRequest, SaleAndSaleProductRequest } from "@/infrastructure/services/api/admin/sale.api.ts";
import { useGetSaleById, useUpdateSale, useGetAttributes, useUpdateSaleAndSaleProduct 
    } from "@/infrastructure/services/service/admin/sale.action.ts";
import dayjs from 'dayjs';
import { getDateFormat } from "@/utils/common.helper";
import ProductTable from "./ProductTable.vue";
import ProductDetailTable from "./ProductDetailTableInAddSale.vue";
import ProductDetailTableInDetailSale from "./ProductDetailTableInDetailSale.vue";
import {
     defaultSaleDatePickerRules,
     defaultSaleRequest,
     FormState,
     disabledDate, disabledDateTime 
     } from "./base/DefaultConfig";


const auth = useAuthStore();
const userInfo = computed(() => auth.user);
const saleId = ref<string | null>('');
const currentStatus = ref<boolean | null>(true);
const activeTabKey = ref('1');

onMounted(() => {
    saleId.value = useRoute().params.id as string;
});
const { data, isLoading, isFetching } = useGetSaleById(saleId, {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
});
const listAttributes = useGetAttributes({
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
});
const idSanPhams = ref<string[]>([]);
const idSanPhamChiTiets = ref<string[]>([]);
const saleRequest = ref<SaleRequest>(defaultSaleRequest)
const formRef = ref();
const formState: UnwrapRef<FormState> = reactive( {
    ma: '',
    ten: '',
    loai: 'PERCENT',
    giaTri: 0,
    giaTriGiamToiDa: null,
    ngayBatDauVaKetThuc: [],
    nguoiSua: undefined,
    trangThai: false,
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
                if (formState.loai === 'PERCENT' && value != null && value <= 0) {
                  return Promise.reject('Giá trị giảm phải lớn hơn 0');
                }
                if (formState.loai === 'PERCENT' && value > 100) {
                    return Promise.reject('Giá trị giảm chỉ bé hơn hoặc bằng 100%');
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
                if (formState.loai === 'VND' && value <= 0 && value != null ) {
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
    ngayBatDauVaKetThuc: [{ required: true, message: 'Vui lòng chọn ngày bắt đầu và kết thúc cho đợt giảm giá', trigger: 'change', type: 'array' },
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
    loai: [{ required: true, message: 'Vui lòng chọn loại đợt giảm giá', trigger: 'change' }],
};


watch(() => data.value?.data.data, (saleData) => {
    if (saleData) {
        Object.assign(formState, {
            ma: saleData.maDotGiamGia || '',
            ten: saleData.ten || '',
            loai: saleData.loai || '',
            giaTri: saleData.giaTri || 0,
            giaTriGiamToiDa: saleData.giaTriGiamToiDa || null,
            ngayBatDauVaKetThuc: [
                saleData.ngayBatDau ? dayjs(saleData.ngayBatDau) : null,
                saleData.ngayKetThuc ? dayjs(saleData.ngayKetThuc) : null,
            ],
            nguoiSua: userInfo.value?.email || '',
            trangThai: saleData.trangThai === 'ACTIVE',
            createdDate: saleData.createdDate,
            lastModifiedDate: saleData.lastModifiedDate,
        });
            currentStatus.value = saleData.ngayKetThuc ? (saleData.ngayKetThuc > Date.now() ? true : false) : null;
            // console.log(saleData.ngayKetThuc, Date.now());
    }
});
const { mutate: updateSale } = useUpdateSale();
const handleUpdateSale = (id: string | any, dataRequest: SaleRequest) => {
    try {
        updateSale(
            { saleId: id, data: dataRequest },
            {
                onSuccess: (res: any) => {
                    toast.success(res.data.message);
                    activeTabKey.value =  '1';
                },
                onError: (error: any) => {
                    toast.error(
                        error?.response?.data?.message
                    )
                },
            })
    } catch (error: any) {
        console.error("🚀 ~ handleUpdateSale ~ error:", error);
        toast.error(
            error?.response?.data?.message
        );
    }
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
            saleRequest.value.nguoiSua = userInfo.value?.email || null;
            saleRequest.value.trangThai = formState.trangThai ? 'ACTIVE' : 'INACTIVE';
            if ( x == 1 ) {
                 handleUpdateSale(saleId.value, saleRequest.value)
            }else{
                handleUpdateSaleProduct(saleId.value || '',{
                saleRequest: saleRequest.value,
                saleProductRequest: {idSanPhamChiTiets: idSanPhamChiTiets.value}
            });
            }
        });
};
const resetForm = () => {
    const ma = formState.ma;
    formRef.value.resetFields();
    formState.ma = ma;
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

const { mutate: updateSaleProduct } = useUpdateSaleAndSaleProduct();
const handleUpdateSaleProduct = (saleId: string | '', data: SaleAndSaleProductRequest) => {
    Modal.confirm({
    content: "Bạn chắc chắn muốn áp dụng đợt giảm giá cho các sản phẩm đã chọn?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        updateSaleProduct({saleId, data}, {
          onSuccess: (res: any) => {
            toast.success(res?.message);
            activeTabKey.value =  '1';
        },
          onError: (error: any) => {
            toast.error(error?.response?.data?.message);
          },
        });
      } catch (error: any) {
        if (error?.response) {
            toast.error(error?.response?.data?.message);
        } else if (error?.errorFields) {
            toast.error('Lưu thất bại');
        }
      }
    },
    cancelText: "Huỷ",
    onCancel() {
        Modal.destroyAll();
    },
  });


}

</script>