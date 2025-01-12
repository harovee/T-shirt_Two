<template>
  <table class="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400 rounded-xl border-2">
    <thead class="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
      <tr>
        <th scope="col" class="px-6 py-3">Màu sắc</th>
        <th scope="col" class="px-6 py-3">Kích cỡ</th>
        <th scope="col" class="px-6 py-3">Giá</th>
        <th scope="col" class="px-6 py-3">Số lượng</th>
        <th scope="col" class="px-6 py-3">Hành động</th>
      </tr>
    </thead>
    <tbody>
      <tr class="bg-white border-b dark:bg-gray-800 dark:border-gray-700" v-for="(item, index) in copiedData" :key="index">
        <td class="px-6 py-4">{{ findMau(item.idMauSac) }}</td>
        <td class="px-6 py-4">{{ findKichCo(item.idKichCo) }}</td>
        <td class="px-6 py-4">
          <a-input
            v-model:value="item.gia"
            type="number"
            style="width: 100px"
            :min="0"
            @blur="handleInputChange('gia', item, index)"
          />
        </td>
        <td class="px-6 py-4">
          <a-input
            v-model:value="item.soLuong"
            type="number"
            style="width: 100px"
            :min="0"
            @blur="handleInputChange('soLuong', item, index)"
          />
        </td>
        <td class="px-6 py-4">
          <a-button type="primary" danger @click="handleDelete(item)">Xóa</a-button>
        </td>
      </tr>
    </tbody>
  </table>

  <div >
    <a-button type="primary" class="m-4 w-[80rem]" @click="handleCreateProduct">
      Hoàn thành
    </a-button>
  </div>

</template>

<script lang="ts" setup>
import { ref, watch, inject, computed, } from 'vue';
import { useRouter } from 'vue-router';
import { ProductDetailRequest } from "@/infrastructure/services/api/admin/product_detail.api";
import { forEach } from 'lodash';
import { keepPreviousData } from "@tanstack/vue-query";
import { useGetListColor } from "@/infrastructure/services/service/admin/color.action";
import { useGetListSize } from "@/infrastructure/services/service/admin/size.action";
import {toast} from "vue3-toastify";
import {useCreateProductDetail} from "@/infrastructure/services/service/admin/productdetail.action";
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path";

const props = defineProps<{
  dataProductDetail: ProductDetailRequest[];
}>();

 const router = useRouter();

const copiedData = ref<ProductDetailRequest[]>([]);

watch(() => props.dataProductDetail, (newData) => {
  copiedData.value = [...newData];
});

// lấy danh sách kich cỡ
const { data: sizes } = useGetListSize({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listSize = computed(() => {
  return (
    sizes?.value?.map((size) => ({
      value: size.id,
      label: size.ten,
    })) || []
  );
});

// lấy danh sách màu sắc
const { data: colors } = useGetListColor({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listColor = computed(() => {
  return (
    colors?.value?.map((color) => ({
      value: color.id,
      label: color.ten,
    })) || []
  );
});

const findMau = (id) => {
  const mau = listColor.value.find(mau => mau.value === id);
  return mau ? mau.label : null;
};

const findKichCo = (id) => {
  const kt = listSize.value.find(size => size.value === id);
  return kt ? kt.label : null;
};

// Khi cần sao chép dữ liệu từ props
copiedData.value = [...props.dataProductDetail];

console.log(copiedData.value);

// Xử lý các kí tự khi truyền vào giá và số lượng
const handleInputChange = (field: string, item: ProductDetailRequest, index: number) => {
  let value = item[field].toString();
  value = value.replace(/[^0-9.]/g, '');
  if (value.split('.').length > 2) {
    value = value.substring(0, value.lastIndexOf('.'));
  }
  if (value === '') {
    value = '0';
  }
  item[field] = parseFloat(value);
  copiedData.value[index] = { ...copiedData.value[index], ...item };
};

const handleDelete = (index: number) => {
  // Xóa phần tử trong copiedData bằng index
  copiedData.value.splice(index, 1);
};

const {mutate: create} = useCreateProductDetail();

const handleCreateProduct = async () => {
  if (!copiedData.value || copiedData.value.length === 0) {
    return;
  }
  const promises = copiedData.value.map(async (item) => {
      try {
        await
        create(item);
      } catch (error: any) {
        console.error("🚀 ~ handleCreate ~ error:", error);
        if (error?.response) {
          toast.warning(
              error?.response?.data?.message
          );
          return;
        } else if (error?.errorFields) {
          toast.warning("Vui lòng nhập đầy đủ các trường dữ liệu");
          return;
        }
      }
    });
    await Promise.all(promises);
    toast.success("Tất cả sản phẩm đã được tạo thành công!");
    setTimeout(() => {
    router.push(ROUTES_CONSTANTS.ADMIN.children.PRODUCTS.children.PRODUCT.path);
  }, 3000);
};

// Cột của bảng
const columns = [
  {
    title: 'Màu sắc',
    dataIndex: 'idMauSac',
    width: '25%',
  },
  {
    title: 'Kích cỡ',
    dataIndex: 'idKichCo',
    width: '25%',
  },
  {
    title: 'Giá',
    dataIndex: 'gia',
    width: '15%',
  },
  {
    title: 'Số lượng',
    dataIndex: 'soLuong',
    width: '15%',
  },
  {
    title: 'Hành động',
    dataIndex: 'operation',
    width: '10%',
  },
];

// Dữ liệu mẫu cho bảng
</script>

<style scoped>
.editable-row-operations a {
  margin-right: 8px;
  cursor: pointer;
}
</style>
<style scoped>
/* table {
  width: 100%;
  border-collapse: collapse;
}

td,
th {
  padding: 8px;
  text-align: center;
}

input[type="number"] {
  text-align: center;
} */
</style>