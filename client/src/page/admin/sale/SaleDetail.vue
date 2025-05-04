<template>
    <div class="p-3 grid grid-cols-1 gap-6">
        <div class="flex justify-between items-center">
            <div class="flex items-center gap-2">
                <v-icon name="md-percent-round" size="x-large" width="48" height="48" />
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
        <div class="col-span-5 md:col-span-5 lg:col-span-2 w-full shadow-md flex justify-center h-fit">
            <div class="w-[30rem] p-5">
                <!-- FORM CHI TIẾT ĐỢT GIẢM GIÁ -->
                <h4 class="text-lg font-semibold mb-4">Chi tiết đợt giảm giá</h4>
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
                        <a-input-number v-model:value="formState.giaTri" min="0" style="width: 100%"
                        :formatter="formState.loai == 'VND' ? formatter : undefined"
                        ></a-input-number>
                    </a-form-item>
                    <!-- <a-form-item class="m-0 mt-2" v-if="formState.loai == 'VND'" ref="giaTriGiamToiDa" label="Giá trị giảm tối đa" name="giaTriGiamToiDa" required>
                        <a-input-number v-model:value="formState.giaTriGiamToiDa" min="0" step="10" style="width: 100%">
                            <template #addonAfter>đ</template>
                        </a-input-number>
                    </a-form-item> -->
                    <a-form-item class="m-0 mt-2" label="Thời gian" required name="ngayBatDauVaKetThuc">
                        <a-range-picker size="" style="width: 100%;" show-time format="DD/MM/YYYY HH:mm"
                            v-model:value="formState.ngayBatDauVaKetThuc"
                            :disabled-date="disabledDate"
                            :disabled-date-time="disabledDateTime"
                            :placeholder="['Ngày bắt đầu', 'Ngày kết thúc']" :presets="rangePresets" />
                        
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



                <a-typography-text type="danger" strong>
                    {{ currentStatus ? '':'Đã kết thúc |' }}
                    <a-popconfirm title="Nhân bản đợt giảm giá này?" placement="right" okText="OK" cancelText="Hủy" @confirm="handleCopySale()" >
                        <a-typography-text v-if="!currentStatus" type="danger" strong underline class="cursor-pointer">
                            <CopyOutlined /> Copy
                        </a-typography-text>
                    </a-popconfirm>
                </a-typography-text>



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
                            <h4 class="text-lg font-semibold mb-4">Danh sách sản phẩm</h4>
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
    :dgg-loai-giam="formState.loai"
    :dgg-gia-tri-giam="formState.giaTri"
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
    PlusCircleOutlined,
    CopyOutlined
} from '@ant-design/icons-vue';
import { Modal } from "ant-design-vue";
import { computed, onMounted, watch, reactive, ref, createVNode } from "vue";
import type { UnwrapRef } from 'vue';
import type { Rule } from 'ant-design-vue/es/form';
import { keepPreviousData } from "@tanstack/vue-query";
import { useAuthStore } from "@/infrastructure/stores/auth.ts";
import { SaleRequest, SaleAndSaleProductRequest } from "@/infrastructure/services/api/admin/sale.api.ts";
import { useGetSaleById, useUpdateSale, useGetAttributes, useUpdateSaleAndSaleProduct
    } from "@/infrastructure/services/service/admin/sale.action.ts";
import dayjs, { Dayjs } from 'dayjs';
import { getDateFormat } from "@/utils/common.helper";
import ProductTable from "./ProductTable.vue";
import ProductDetailTable from "./ProductDetailTableInAddSale.vue";
import ProductDetailTableInDetailSale from "./ProductDetailTableInDetailSale.vue";
import {
     defaultSaleRequest,
     FormState,
     disabledDate, disabledDateTime
     } from "./base/DefaultConfig";
import { openNotification, notificationType, warningNotiSort } from "@/utils/notification.config";
import { useSaleStore } from "./base/SaleStore";


const auth = useAuthStore();
const userInfo = computed(() => auth.user);
const saleId = ref<string | null>('');
const currentStatus = ref<boolean | null>(true);
const activeTabKey = ref('1');
const isCopyMode = ref(false); // Thêm biến theo dõi chế độ sao chép
const isInitialLoad = ref(true);

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
const validStartDate = ref<number | null> (dayjs().valueOf());
const rangePresets = ref([
    { label: 'Bây giờ', value: [dayjs().add(1, 'minute'), dayjs().add(16, 'minute')] },
    { label: 'Ngày mai', value: [dayjs().startOf('d').add(1, 'd'), dayjs().endOf('d').add(1, 'd')] },
    { label: '7 ngày tiếp theo', value: [dayjs(), dayjs().add(7, 'd')] },
    { label: '15 ngày tiếp theo', value: [dayjs(), dayjs().add(15, 'd')] },
    { label: '30 ngày tiếp theo', value: [dayjs(), dayjs().add(30, 'd')] },
    {
        label: 'Tuần sau',
        value: [
            dayjs().startOf('week').add(1, 'week').add(1, 'd'),
            dayjs().endOf('week').add(1, 'week').add(1, 'd')
        ]
    },
    {
        label: 'Tháng sau',
        value: [
            dayjs().startOf('month').add(1, 'month'),
            dayjs().endOf('month').add(1, 'month'),
        ]
    },

]);
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
    ngayBatDauVaKetThuc: [
        { required: true, message: 'Vui lòng chọn ngày bắt đầu và kết thúc cho đợt giảm giá', trigger: 'change', type: 'array' },
        {   
            validator: (rule, value) => {
                const [ngayBatDau, ngayKetThuc] = value.map((date: any) =>
                    dayjs(date).valueOf()
                );
                const now = dayjs().valueOf();
                
                if (currentStatus.value && ngayBatDau < now) {
                    return Promise.reject('Ngày bắt đầu không được nhỏ hơn thời điểm hiện tại');
                }

                if (!value || value.length !== 2) return Promise.resolve();
                    const startStr = dayjs(value[0]).format('YYYY-MM-DD HH:mm');
                    const endStr = dayjs(value[1]).format('YYYY-MM-DD HH:mm');
                
                if (startStr === endStr) {
                return Promise.reject('Ngày kết thúc không được trùng ngày bắt đầu');
                }
                    if (ngayKetThuc < ngayBatDau) {
                        return Promise.reject('Ngày kết thúc không được nhỏ hơn ngày bắt đầu');
                    }
                return Promise.resolve();
            },
            trigger: 'change',
        },
    ],
    loai: [{ required: true, message: 'Vui lòng chọn loại đợt giảm giá', trigger: 'change' }],
};

// Hàm format tiền sang VNĐ
const formatter = (value: any) => {
  if (!value) return "";
  return `${value} ₫`.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

watch(() => data.value?.data.data, (saleData) => {
    if (saleData) {
        isInitialLoad.value = true;

        Object.assign(formState, {
            ma: saleData.maDotGiamGia || '',
            ten: saleData.ten || '',
            loai: saleData.loai || '',
            giaTri: saleData.giaTri || 0,
            giaTriGiamToiDa: null,
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
            validStartDate.value = saleData.ngayBatDau ;
            // console.log(saleData.ngayKetThuc, Date.now());
            setTimeout(() => {
                isInitialLoad.value = false;
            }, 100);
    }
});

// THAO - 04/05/2025
watch(() => formState.loai, (newValue, oldValue) => {
  if (!isInitialLoad.value && newValue !== oldValue) {
    formState.giaTri = null;
    if (formState.giaTriGiamToiDa !== null) {
      formState.giaTriGiamToiDa = null;
    }
  }
});
// THAO - 04/05/2025

const { mutate: updateSale } = useUpdateSale();
const handleUpdateSale = (id: string | any, dataRequest: SaleRequest) => {
    Modal.confirm({
    title: "Bạn chắc chắn cập nhật đợt giảm giá này?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        updateSale({ saleId: id, data: dataRequest }, {
          onSuccess: (result) => {
            openNotification(notificationType.success, result?.data.message, '');
            activeTabKey.value =  '1';
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
            saleRequest.value.giaTriGiamToiDa = null;
            saleRequest.value.ngayBatDau = formState.ngayBatDauVaKetThuc[0]?.valueOf() || null;
            saleRequest.value.ngayKetThuc = formState.ngayBatDauVaKetThuc[1]?.valueOf() || null;
            saleRequest.value.nguoiSua = userInfo.value?.email || null;
            saleRequest.value.trangThai = formState.trangThai ? 'ACTIVE' : 'INACTIVE';
            
            if (x == 1) {
                handleUpdateSale(saleId.value, saleRequest.value)
            } else if (x == 2) {
                handleUpdateSaleProduct(saleId.value || '', {
                    saleRequest: saleRequest.value,
                    saleProductRequest: {idSanPhamChiTiets: idSanPhamChiTiets.value}
                });
            } else {
                
                if (!currentStatus.value) {
                    const saleStore = useSaleStore();
                    saleRequest.value.ngayBatDau = null;
                    saleRequest.value.ngayKetThuc = null;
                    saleRequest.value.trangThai = 'ACTIVE';

                    saleStore.setSaleData(saleRequest.value);
                    router.push({name: 'admin-sale-add'})
                    // .then(() => {
                    //      warningNotiSort("Vui lòng kiểm tra và điều chỉnh thời gian cho đợt giảm giá.");
                    // });
                }
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
    title: "Bạn chắc chắn muốn áp dụng đợt giảm giá cho các sản phẩm đã chọn?",
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    async onOk() {
      try {
        updateSaleProduct({saleId, data}, {
            onSuccess: (result) => {
                openNotification(notificationType.success, result?.message, '');
                activeTabKey.value =  '1';
                // handleRedirectClient();
          },
          onError: (error: any) => {
                openNotification(notificationType.error, error?.response?.data?.message, '');
          },
        });
      } catch (error: any) {
        if (error?.response) {
          openNotification(notificationType.error, error?.response?.data?.message, '');
        } else if (error?.errorFields) {
          openNotification(notificationType.warning, "Vui lòng nhập đúng các trường dữ liệu", '');
        }
      }
    },
    cancelText: "Huỷ",
    onCancel() {
        Modal.destroyAll();
    },
  });


}

const handleCopySale = () => {
    onSubmit(3);
}

</script>