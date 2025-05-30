<template>
  <div class=" flex justify-between">
    <a-space class="text-lg font-semibold mb-4" v-if="currentStatus">Các sản phẩm chi tiết đang được áp dụng</a-space>
    <a-space class="text-lg font-semibold mb-4" v-if="!currentStatus">Các sản phẩm chi tiết đã được áp dụng</a-space>
    <a-statistic title="Số lượng" :value="data?.data.totalElements" style="margin-right: 50px" />
  </div>
  <div>
    <a-space class="flex justify-start items-end flex-wrap">
        <a-space direction="vertical" class="m-1 ms-0">
            <div>Tìm kiếm</div>
            <a-input-search
              v-model:value="params.keyword"
              placeholder="Mã, tên, sản phẩm, danh mục"
              size="small"
              style="width: 350px"
              @change="handleChangeKey"
            />
        </a-space>
    </a-space>
  </div> 
  <div>
    <a-table
    :loading="isLoading"
    :columns="columns"
    :data-source="data?.data.data"
    :pagination="false"
    :scroll="{ x: 500, y: 600 }"
  >
      <template #bodyCell="{ record, column }">
        <a-image-preview-group>
            <div v-if="column.dataIndex === 'linkAnh'" class="text-center">
                  <a-badge-ribbon :text="formatCurrency((record.giaSauGiam - record.gia), 'VND', 'vi-VN')"  color="red">
                        <a-image :width="140"
                         :alt="record.linkAnh ? record.ten : 'T-shirt Two'"
                         :src="record.linkAnh != 'default-product-detail-image-url.jpg'
                        ? record.linkAnh : defaultProductImageSaleUrl " />
                  </a-badge-ribbon>
            </div>
        <div v-if="column.dataIndex === 'thongTin'">
            <a-space direction="vertical" class="text-left">
                <a-space>{{ record.maSanPhamChiTiet }} - {{ record.ten }}</a-space>
                <a-space>Số lượng: {{ record.soLuong }}</a-space>
                <a-space>Giá gốc: <a-tag color="#108ee9"> {{ formatCurrency(record.gia, 'VND', 'vi-VN') }}</a-tag></a-space>
                <a-space>Giá sau giảm:  <a-tag color="#f50">{{  formatCurrency(record.giaSauGiam, 'VND', 'vi-VN') }}</a-tag></a-space>
                <a-popover placement="bottom">
                    <template #content>
                      <!-- <EventDetail /> -->
                       <p>Giá trung bình trên số đợt đang diễn ra</p>
                    </template>
                    <template #title>
                      <!-- <span>Giá trung bình trên các đợt đang diễn ra</span> -->
                    </template>
                   <a-typography-text type="danger" strong underline class="cursor-pointer">
                        Giá bán hiện tại: {{ formatCurrency(record.giaHienTai ? record.giaHienTai : record.gia, 'VND', 'vi-VN')  }}
                    </a-typography-text>
                </a-popover>
            </a-space>
        </div>
        <div v-else-if="column.dataIndex === 'action'">
          <a-popconfirm
              :title="'Bạn chắc chắn muốn loại khỏi danh sách?'"
              ok-text="Có"
              cancel-text="Hủy"
              @confirm="handleDelete(record.idSanPhamGiamGia)"
              :disabled="!currentStatus"
          >
            <a-tooltip
                placement="left"
                :title="currentStatus ? 'Bỏ sản phẩm chi tiết khỏi đợt giảm giá' : 'Không thể xóa sản phẩm chi tiết khỏi đợt giảm giá đã kết thúc'"
                trigger="hover"
            >
              <a-button
                  class="bg-purple-100"
                  size="middle"
                  shape="round"
                  :disabled="!currentStatus"
                  
              >
              <v-icon name="fa-trash-alt"/>

              </a-button>
            </a-tooltip>
          </a-popconfirm>
        </div>
      </a-image-preview-group>
      </template>
  </a-table>
  <a-pagination
      class="m-2"
      v-model:current="current1"
      v-model:pageSize="pageSize"
      :total="data?.data.totalElements"
    />
  </div>
  
</template>
<script lang="ts" setup>
import {defineProps, ref, watch } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import { defaultProductImageSaleUrl, formatCurrency } from "@/utils/common.helper";
import {
  FindSaleProductDetailRequest,
} from "@/infrastructure/services/api/admin/sale.api.ts";
import { useGetSaleProductDetails, useDeleteSaleProduct } from "@/infrastructure/services/service/admin/sale.action.ts";
import { errorNotiSort, successNotiSort } from "@/utils/notification.config";
import EventDetail from "./EventDetail.vue";


const props = defineProps<{
  idDotGiamGia: string | '',
  currentStatus: boolean | null
}>();


const columns = [
  {
    title: "Ảnh",
    dataIndex: "linkAnh",
    fixed: true,
    width: 100,
    align: 'center'
  },
  {
    title: "Thông tin",
    dataIndex: "thongTin",
    width: 200,
    align: 'center'
  },
  {
    title: "Hành động",
    dataIndex: "action",
    width: 80,
    align: 'center'
  },

];


const pageSize = ref(5);
const current1 = ref(1);
const params = ref<FindSaleProductDetailRequest>({
  page: 1,
  idDotGiamGia: props.idDotGiamGia,
  size: 5,
});

const { data, isLoading } = useGetSaleProductDetails(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const handleChangeKey = () =>{
  params.value.page = 1;
}
watch(current1, () => {
  params.value.page = current1.value == 0 ? 1 : current1.value;
});

watch(
      () => props.idDotGiamGia,
      (newValue, oldValue) => {
        params.value.idDotGiamGia = newValue || oldValue || '' ;
      });

const { mutate } = useDeleteSaleProduct();

const handleDelete = (id : any) => {
  mutate(id, {
    onSuccess() {
      successNotiSort('Xóa thành công');
    },
    onError(error) {
      errorNotiSort(error?.response?.data?.message);
    },
  });
};


</script>
