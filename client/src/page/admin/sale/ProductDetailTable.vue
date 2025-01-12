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
          <a-button size="small" type="primary">Bỏ lọc</a-button>
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
          <a-select style="width: 100px;" v-model:value="params.gioiTinh" size="small">
            <a-select-option key="-1" :value="null">Tất cả</a-select-option>
            <a-select-option key="0" :value="true">Nam</a-select-option>
            <a-select-option key="1" :value="false">Nữ</a-select-option>
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
    :row-selection="rowSelection"
    :columns="columns"
    :data-source="dataSource"
    :pagination="false"
    :scroll="{ x: 1500, y: 500 }"
  >
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
    title: "Mã",
    dataIndex: "maSanPhamChiTiet",
    fixed: true,
    width: 100,
  },
  {
    title: "Tên",
    dataIndex: "ten",
    width: 200,
  },
  {
    title: "Sản phẩm",
    dataIndex: "tenSanPham",
    width: 200,
  },
  {
    title: "Thương hiệu",
    dataIndex: "tenThuongHieu",
    width: 150,
  },
  {
    title: "Giới tính",
    dataIndex: "gioiTinh",
    width: 70,

  },
  {
    title: "Kích cỡ",
    dataIndex: "kichCo",
    width: 100,

  },
  {
    title: "Chi tiết",
    dataIndex: "phongCach",
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

const { data } = useGetProductDetails(params, {
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const dataSource: DataType[] | any = computed(() => {
  return (
    data?.value?.data?.data?.map((e: any) => ({
      key: e.id || "",
      maSanPhamChiTiet: e.maSanPhamChiTiet || null,
      ten: e.ten || "",
      tenSanPham: e.tenSanPham || "",
      tenThuongHieu: e.tenThuongHieu || "",
      gioiTinh: e.gioiTinh ? "Nam": e.gioiTinh == false ? "Nữ" : "Không xác định",
      kichCo: e.kichCo || "",
      phongCach: e.phongCach || ""
    })) || []
  );
});

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
    console.log(selectedRowKeys);
  emit('update:idSanPhamChiTiets', selectedRowKeys);
  },
};

</script>
