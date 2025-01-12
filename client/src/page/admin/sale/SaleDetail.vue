<template>
    <div class="p-2 grid grid-cols-1 gap-6">
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
    <div class="p-3 grid grid-cols-5 gap-6">
        <div class="col-span-5 md:col-span-5 lg:col-span-2 w-full h-screen shadow-md flex justify-center">
            <div class="w-[30rem] h-[20rem] p-6">
                <!-- FORM CHI TIẾT ĐỢT GIẢM GIÁ -->

                <a-form ref="formRef" :model="formState" :rules="rules" layout="vertical">
                    <div class="flex justify-end" style="width: 100%;">
                        <a-alert banner :showIcon="false" :message="'Ngày tạo: ' + getDateFormat(formState.createdDate)"
                            type="success" class="p-0" />
                    </div>
                    <a-form-item class="m-0 mt-2" ref="ma" label="Mã" name="ma">
                        <a-input disabled="" v-model:value="formState.ma" />
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" ref="ten" label="Tên" name="ten" required>
                        <a-input v-model:value="formState.ten" />
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" ref="giaTri" label="Giá trị" name="giaTri" required>
                        <a-input-number v-model:value="formState.giaTri" min="0">
                            <template #addonAfter>
                                <a-select v-model:value="formState.loai" style="width: 60px">
                                    <a-select-option value="PERCENT">%</a-select-option>
                                    <a-select-option value="VND">đ</a-select-option>
                                </a-select>
                            </template>
                        </a-input-number>
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" v-if="formState.loai == 'VND'" ref="giaTriGiamToiDa" label="Giá trị giảm tối đa" name="giaTriGiamToiDa" required>
                        <a-input-number v-model:value="formState.giaTriGiamToiDa" min="0" step="10">
                            <template #addonAfter>đ</template>
                        </a-input-number>
                    </a-form-item>
                    <a-form-item class="m-0 mt-2" label="Thời gian" required name="ngayBatDauVaKetThuc">
                        <a-range-picker size="large" style="" show-time format="DD/MM/YYYY HH:mm"
                            v-model:value="formState.ngayBatDauVaKetThuc"
                            :placeholder="['Ngày bắt đầu', 'Ngày kết thúc']" :presets="rangePresets" />
                    </a-form-item>

                    <a-form-item class="m-0 mt-2" label="" name="trangThai">
                        <a-checkbox v-model:checked="formState.trangThai">Hoạt động</a-checkbox>
                    </a-form-item>


                    <a-form-item class="m-0 mt-3">
                        <a-button type="primary" @click="onSubmit">Cập nhật</a-button>
                        <a-button style="margin-left: 10px" @click="resetForm">Clear form</a-button>
                    </a-form-item>
                </a-form>

            </div>
        </div>

        <!-- <div class="col-span-3 md:col-span-5 lg:col-span-3 h-screen">
            <div v-if="isLoading">Đang tải dữ liệu...</div>
            <div v-else-if="error">Có lỗi xảy ra: {{ error.message }}</div>
            <div v-else>
                <div class="p-3 rounded-sm shadow-md">
                    <product-table
                        :categories="listAttributes.data.value?.data.categories"
                        :id-san-phams="idSanPhams"  
                    />
                </div>
                
            </div>
        </div> -->
    </div>
</template>

<script lang="ts">
export default {
    name: 'admin client detail',
};
</script>

<script lang="ts" setup>
import { ROUTES_CONSTANTS } from "@/infrastructure/constants/path.ts";
import router from "@/infrastructure/routes/router.ts";
import { useRoute } from 'vue-router';
import { computed, onMounted, watch, reactive, ref } from "vue";
import type { UnwrapRef } from 'vue';
import type { Rule } from 'ant-design-vue/es/form';
import { toast } from "vue3-toastify";
import { keepPreviousData } from "@tanstack/vue-query";

import { useAuthStore } from "@/infrastructure/stores/auth.ts";
import { SaleRequest } from "@/infrastructure/services/api/admin/sale.api.ts";
import { useGetSaleById, useUpdateSale, useGetAttributes } from "@/infrastructure/services/service/admin/sale.action.ts";
import dayjs from 'dayjs';
import { getDateFormat } from "@/utils/common.helper";
import ProductTable from "./ProductTable.vue";
import {
     defaultSaleDatePickerRules,
     defaultSaleRequest,
     FormState
     } from "./base/DefaultConfig";

const auth = useAuthStore();
const userInfo = computed(() => auth.user);
const saleId = ref<string | null>(null);
onMounted(() => {
    saleId.value = useRoute().params.id as string;
});
const { data } = useGetSaleById(saleId, {
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
});
const listAttributes = useGetAttributes({
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
});
const idSanPhams = ref<string[]>([]);

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
                return Promise.resolve();
            },
            trigger: 'change',
        },
    ],
    ngayBatDauVaKetThuc: [{ required: true, message: 'Vui lòng chọn ngày bắt đầu và kết thúc cho đợt giảm giá', trigger: 'change', type: 'array' }],
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

const onSubmit = () => {
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
            handleUpdateSale(saleId.value, saleRequest.value)

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


const handleRedirectClient = () => {
    router.push({ name: 'admin-sale' });
}

</script>