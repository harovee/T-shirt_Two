<template>
  <div>
    <h1 class="text-base font-bold">Bạn đã chọn</h1>
    <div class="flex flex-wrap gap-2 mb-4">
      <!-- Hiển thị các filter đã chọn -->
      <template v-for="(filters, category) in selectedFilters" :key="category">
        <a-tag 
          v-for="filter in filters" 
          :key="filter"
          closable 
          @close="removeFilter(category, filter)"
        >
          {{ filter }}
        </a-tag>
      </template>
    </div>
    <hr />
    <!-- Panel cho Danh mục -->
    <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
      <a-collapse-panel key="danhmuc" header="Danh mục">
        <a-checkbox-group>
          <a-checkbox
            v-for="dm in listDanhMuc"
            :key="dm.id"
            :value="dm.ten"
            :checked="selectedFiltersState.tenDanhMuc.includes(dm.ten)"
            @change="onChangeFilter('tenDanhMuc', $event, dm.ten)"
          >
            {{ dm.ten }}
          </a-checkbox>
        </a-checkbox-group>
      </a-collapse-panel>
    </a-collapse>

    <!-- Panel cho Kiểu dáng -->
    <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
      <a-collapse-panel key="kieudang" header="Kiểu dáng">
        <a-checkbox-group>
          <a-checkbox
            v-for="kd in listKieuDang"
            :key="kd.id"
            :value="kd.ten"
            :checked="selectedFiltersState.tenKieuDang.includes(kd.ten)"
            @change="onChangeFilter('tenKieuDang', $event, kd.ten)"
          >
            {{ kd.ten }}
          </a-checkbox>
        </a-checkbox-group>
      </a-collapse-panel>
    </a-collapse>

    <!-- Panel cho Chất liệu -->
    <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
      <a-collapse-panel key="chatlieu" header="Chất liệu">
        <a-checkbox-group>
          <a-checkbox
            v-for="cl in listChatLieu"
            :key="cl.id"
            :value="cl.ten"
            :checked="selectedFiltersState.tenChatLieu.includes(cl.ten)"
            @change="onChangeFilter('tenChatLieu', $event, cl.ten)"
          >
            {{ cl.ten }}
          </a-checkbox>
        </a-checkbox-group>
      </a-collapse-panel>
    </a-collapse>

    <!-- Panel cho Thương hiệu -->
    <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
      <a-collapse-panel key="thuonghieu" header="Thương hiệu">
        <a-checkbox-group>
          <a-checkbox
            v-for="th in listThuongHieu"
            :key="th.id"
            :value="th.ten"
            :checked="selectedFiltersState.tenThuongHieu.includes(th.ten)"
            @change="onChangeFilter('tenThuongHieu', $event, th.ten)"
          >
            {{ th.ten }}
          </a-checkbox>
        </a-checkbox-group>
      </a-collapse-panel>
    </a-collapse>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, reactive, watch } from "vue";
import { 
  useGetChatLieu, 
  useGetColor, 
  useGetDanhMuc, 
  useGetKieuDang, 
  useGetThuongHieu 
} from "@/infrastructure/services/service/client/productclient.action";
import { keepPreviousData } from "@tanstack/vue-query";
import { debounce } from "lodash";
import { FindProductClientRequest } from "@/infrastructure/services/api/client/clientproduct.api";

const emit = defineEmits([
  "filter"
]);

const selectedFiltersState = reactive({
  tenDanhMuc: [],
  tenKieuDang: [],
  tenChatLieu: [],
  tenThuongHieu: []
});


const params= ref<FindProductClientRequest>({
    page: 1,
    size: 20,
    tenSanPham: "",
    tenDanhMuc: "",
    tenChatLieu: "",
    tenTayAo: "",
    tenTinhNang: "",
    tenKieuDang: "",
    tenCoAo: "",
    tenThuongHieu: "",
    tenHoaTiet: ""
});


const { data: chatLieu } = useGetChatLieu({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});
const { data: danhMuc } = useGetDanhMuc({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});
const { data: thuongHieu } = useGetThuongHieu({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});
const { data: kieuDang } = useGetKieuDang({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});
const { data: color } = useGetColor({
  refetchOnWindowFocus: false,
  placeholderData: keepPreviousData,
});

const listChatLieu = computed(() => chatLieu?.value?.data || []);
const listDanhMuc = computed(() => danhMuc?.value?.data || []);
const listThuongHieu = computed(() => thuongHieu?.value?.data || []);
const listKieuDang = computed(() => kieuDang?.value?.data || []);
const listColor = computed(() => color?.value?.data || []);

const selectedFilters = computed(() => {
  return Object.fromEntries(
    Object.entries(selectedFiltersState).filter(([_, values]) => values.length > 0)
  );
});

watch(() => selectedFiltersState, () => {
  const filterMapping = {
    tenDanhMuc: 'tenDanhMuc',
    tenKieuDang: 'tenKieuDang',
    tenChatLieu: 'tenChatLieu',
    tenThuongHieu: 'tenThuongHieu'
  };

  Object.keys(filterMapping).forEach((stateKey) => {
    const paramKey = filterMapping[stateKey];
    if (selectedFiltersState[stateKey].length === 0) {
      params.value[paramKey] = "";
    } else {
      params.value[paramKey] = selectedFiltersState[stateKey].join(',');
    }
  });
  emit('filter', params.value);
}, { deep: true });

function onChangeFilter(key: keyof FindProductClientRequest, e: any, value: string) {
  const isChecked = e.target.checked;
  
  if (isChecked) {
    if (!selectedFiltersState[key].includes(value)) {
      selectedFiltersState[key].push(value);
    }
  } else {
    const index = selectedFiltersState[key].indexOf(value);
    if (index > -1) {
      selectedFiltersState[key].splice(index, 1);
    }
    
  }
  
};

function removeFilter(category: string, value: string) {
  const index = selectedFiltersState[category].indexOf(value);
  if (index > -1) {
    selectedFiltersState[category].splice(index, 1);
  }
}

const activeKey = ref(["color"]);

// const emitFilter = debounce(() => {
//   // const payload: FindProductClientRequest = { ...params.value };
//   const payload = params.value;
//   Object.keys(payload).forEach(key => {
//     if (payload[key] === '') {
//       delete payload[key];
//     }
//   });

//   emit('filter', payload);
// }, 300);

</script>
