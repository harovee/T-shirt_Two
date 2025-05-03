<template>
  <div>
    <a-space class="flex justify-start items-end flex-wrap">
        <a-space direction="vertical" class="m-1 ms-0">
            <div>Tìm kiếm</div>
            <a-input-search
              v-model:value="params.keyword"
              placeholder="Mã, tên, sản phẩm, danh mục, màu sắc"
              size="small"
              style="width: 350px"
              @change="handleChangeKey"
            />
        </a-space>
        <a-space class="m-1 ms-0">
          <a-button size="small" type="primary" @click="handleRemoveFilter">Bỏ lọc</a-button>
        </a-space>
        
    </a-space>
   
    <a-space class="flex justify-start items-center flex-wrap">
      <a-space direction="vertical" class="m-1 ms-0">
          <div>Thương hiệu</div>
          <a-select style="width: 150px;" v-model:value="params.idThuongHieu" size="small">
            <a-select-option key="-1" :value="null">Tất cả</a-select-option>
            <a-select-option v-for="(item, index) in attributes?.brands" :key="index" :value="item.id">{{ item.ten }}</a-select-option>
          </a-select>
        </a-space>
        <a-space  direction="vertical" class="m-1 ms-0">
          <div>Kích cỡ</div>
          <a-select style="width: 100px;" v-model:value="params.idKichCo" size="small">
            <a-select-option key="-1" :value="null">Tất cả</a-select-option>
            <a-select-option v-for="(item, index) in attributes?.sizes" :key="index" :value="item.id">{{ item.ten }}</a-select-option>
          </a-select>
        </a-space>
        <a-space  direction="vertical" class="m-1 ms-0">
          <div>Giới tính</div>
          <a-select style="width: 110px;" v-model:value="params.gioiTinh" size="small">
            <a-select-option key="-1" :value="null">Tất cả</a-select-option>
            <a-select-option key="Nam" :value="'Nam'">Nam</a-select-option>
            <a-select-option key="Nữ" :value="'Nữ'">Nữ</a-select-option>
            <a-select-option key="Nam và Nữ" :value="'Nam và Nữ'">Nam và Nữ</a-select-option>
          </a-select>
        </a-space>
       
        <a-space direction="vertical" class="m-1 ms-0">
          <div>Chất liệu</div>
          <a-select style="width: 150px;" v-model:value="params.idChatLieu" size="small">
            <a-select-option key="-1" :value="null">Tất cả</a-select-option>
            <a-select-option v-for="(item, index) in attributes?.materials" :key="index" :value="item.id">{{ item.ten }}</a-select-option>
          </a-select>
        </a-space>
        <a-space  direction="vertical" class="m-1 ms-0">
          <div>Kiểu dáng</div>
          <a-select style="width: 150px;" v-model:value="params.idKieuDang" size="small">
            <a-select-option key="-1" :value="null">Tất cả</a-select-option>
            <a-select-option v-for="(item, index) in attributes?.styles" :key="index" :value="item.id">{{ item.ten }}</a-select-option>
          </a-select>
        </a-space>
        <a-space direction="vertical" class="m-1 ms-0">
          <div>Họa tiết</div>
          <a-select style="width: 150px;" v-model:value="params.idHoaTiet" size="small">
            <a-select-option key="-1" :value="null">Tất cả</a-select-option>
            <a-select-option v-for="(item, index) in attributes?.vignettes" :key="index" :value="item.id">{{ item.ten }}</a-select-option>
          </a-select>
        </a-space>
        <a-space direction="vertical" class="m-1 ms-0">
          <div>Cổ áo</div>
          <a-select style="width: 100px;" v-model:value="params.idCoAo" size="small">
            <a-select-option key="-1" :value="null">Tất cả</a-select-option>
            <a-select-option v-for="(item, index) in attributes?.collars" :key="index" :value="item.id">{{ item.ten }}</a-select-option>
          </a-select>
        </a-space>
        <a-space direction="vertical" class="m-1 ms-0">
          <div>Tay áo</div>
          <a-select style="width: 100px;" v-model:value="params.idTayAo" size="small">
            <a-select-option key="-1" :value="null">Tất cả</a-select-option>
            <a-select-option v-for="(item, index) in attributes?.sleeves" :key="index" :value="item.id">{{ item.ten }}</a-select-option>
          </a-select>
        </a-space>
        
      
    </a-space>
   
  </div> 
  <div>
    <a-table
    :loading="isLoading"
    :row-selection="rowSelection"
    :columns="columns"
    :data-source="dataSource"
    :pagination="false"
    :scroll="{ x: 1000, y: 500 }"
  >
  <template #bodyCell="{ record, column }">
        <a-image-preview-group>
            <div v-if="column.dataIndex === 'linkAnh'" class="text-center">
                <a-image :width="140"
                  :alt="record.linkAnh ? record.ten : 'T-shirt Two'"
                  :src="record.linkAnh != 'default-product-detail-image-url.jpg'
                ? record.linkAnh : defaultProductImageSaleUrl " />
            </div>
            <div v-if="column.dataIndex === 'thongTinChung'" class="text-left">
              <a-space direction="vertical">
                <a-space>{{ record.maSanPhamChiTiet }} - {{ record.ten }}</a-space>
                <a-space>Số lượng: {{ record.soLuong }}</a-space>
                <a-space>Giá gốc: <a-tag color="#108ee9"> {{ formatCurrency(record.gia, 'VND', 'vi-VN') }}</a-tag></a-space>
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
            <div v-if="column.dataIndex === 'phongCach'" class="text-left">
              <a-space direction="vertical">
                <a-space>Sản phẩm: {{ record.tenSanPham }}</a-space>
                <a-space>Giới tính: {{ record.gioiTinh }}</a-space>
                <a-space>Kích cỡ: {{ record.kichCo }}</a-space>
                <a-space>Khác: {{ record.phongCach }}</a-space>
                
              </a-space>
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
import type { TableProps, TableColumnType } from "ant-design-vue";
import {defineProps, computed, defineEmits, ref, watch } from "vue";
import { keepPreviousData } from "@tanstack/vue-query";
import { defaultProductImageSaleUrl, formatCurrency } from "@/utils/common.helper";
import {
  AttributesResponse,
  FindProductDetailRequest,
  ProductDetailResponse
} from "@/infrastructure/services/api/admin/sale.api.ts";
import { useGetProductDetails } from "@/infrastructure/services/service/admin/sale.action.ts";

const pageSize = ref(5);
const current1 = ref(1);

const props = defineProps<{
  attributes: AttributesResponse| undefined,
  idSanPhams: string[] | null,
  idSanPhamChiTiets: string[] | null
}>();

const emit = defineEmits(['update:idSanPhamChiTiets']);

interface DataType extends ProductDetailResponse{
  key: string;
}

const columns: TableColumnType<DataType>[] = [
  {
    title: "Ảnh",
    dataIndex: "linkAnh",
    fixed: true,
    width: 180,
    align: 'center'
  },
  {
    title: "Thông tin chung",
    dataIndex: "thongTinChung",
    width: 400,
    align: 'center'
  },
  {
    title: "Chi tiết",
    dataIndex: "phongCach",
    align: 'center'

  }
];

const params = ref<FindProductDetailRequest>({
  page: 1,
  idSanPhams: props.idSanPhams?.join(',') || '',
  key: null,
  gioiTinh: null,
  idThuongHieu: null,
  idKichCo: null,
  idCoAo: null,
  idTayAo: null,
  idHoaTiet: null,
  idChatLieu: null,
  idKieuDang: null,
  idTinhNang: null,
});

const { data, isLoading } = useGetProductDetails(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataSource: DataType[] | any = computed(() => {
  return (
    data?.value?.data?.data?.map((e: any) => ({
      key: e.id || "",
      maSanPhamChiTiet: e.maSanPhamChiTiet || null,
      ten: e.ten || "",
      gia: e.gia || 0,
      giaHienTai: e.giaHienTai || 0,
      tenSanPham: e.tenSanPham || "",
      tenThuongHieu: e.tenThuongHieu || "",
      gioiTinh: e.gioiTinh,
      kichCo: e.kichCo || "",
      phongCach: e.phongCach || "",
      soLuong: e.soLuong || "",
      linkAnh: e.linkAnh || ""
    })) || []
  );
});

const handleRemoveFilter = () => {
  params.value =  {
  page: 1,
  idSanPhams: props.idSanPhams?.join(',') || '',
  key: null,
  gioiTinh: null,
  idThuongHieu: null,
  idKichCo: null,
  idCoAo: null,
  idTayAo: null,
  idHoaTiet: null,
  idChatLieu: null,
  idKieuDang: null,
  idTinhNang: null,
};
}

const handleChangeKey = () =>{
  current1.value = 1;
}
watch(current1, () => {
  params.value.page = current1.value == 0 ? 1 : current1.value;
});
watch(
      () => props.idSanPhams,
      (newValue) => {
        params.value.idSanPhams = newValue?.join(',') || '';
      });
const rowSelection: TableProps["rowSelection"] = {
  onChange: (selectedRowKeys: string[] | any) => {
  emit('update:idSanPhamChiTiets', selectedRowKeys);
  },
};



</script>
