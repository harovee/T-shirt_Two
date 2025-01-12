<template>
  <div class="flex-1" ref="tableWrapper">
    <a-table
        expand-fixed
        class="overflow-hidden min-h-[35rem]"
        bordered
        :class="className"
        :title="title"
        :columns="columns"
        :data-source="dataSource || []"
        :loading="loading"
        :table-layout="tableLayout || 'auto'"
        :scroll="computedScroll"
        :sticky="true"
        :show-sorter-tooltip="false"
        @change="handleTableChange"
        :pagination="false"
        v-bind="$attrs"
        size="middle"
    >
      <template #headerCell="{ column }">
        <span>
          <slot :name="`header-${column.key}`" :column="column">
            {{ column.title }}
          </slot>
        </span>
      </template>
      <template #bodyCell="{ text, record, column }">
        <slot name="bodyCell" :column="column" :record="record">
          {{ text }}
        </slot>
      </template>
      <template #emptyText>
        <div class="flex justify-center items-center h-full">
          <a-empty description="Không có dữ liệu"/>
        </div>
      </template>
    </a-table>
  </div>
  <div
      v-if="isPagination && dataSource?.length > 0"
      id="pagination-spotify"
      class="mt-3 flex w-full justify-end"
  >
    <a-pagination
        :current="paginationParams.page"
        :total="isNaN(totalPages * paginationParams.size)? 0 : totalPages * paginationParams.size"
        :show-size-changer="!isNaN(totalPages * paginationParams.size)"
        :page-size-options="isNaN(totalPages * paginationParams.size) ? [] : ['10', '20', '40', '80']"
        :default-page-size="paginationParams.size"
        :show-quick-jumper="showSizeChanger"
        :locale="{
          jump_to: 'Đến',
          page: 'Trang',
          items_per_page: ' bản ghi / trang',
        }"
        :show-total="showTotal ? totalFormatter : undefined"
        responsive
        @change="(page: number, pageSize: number) => onPaginationChange(page, pageSize)"
    />
  </div>
</template>

<script setup lang="ts">
import {useTableHeight} from "@/infrastructure/composable/useTableHeight";
import {computed, defineEmits, defineProps, ref} from "vue";

const props = defineProps({
  title: [String, Function],
  columns: {
    type: Array,
    required: true,
  },
  dataSource: {
    type: Array,
    default: () => [],
  },
  paginationParams: {
    type: Object,
    required: true,
  },
  totalPages: {
    type: Number,
    required: true,
  },
  tableLayout: {
    type: String,
    default: "auto",
  },
  className: String,
  size: {
    type: String,
    default: "middle",
  },
  loading: Boolean,
  showSizeChanger: {
    type: Boolean,
    default: true,
  },
  showTotal: {
    type: Boolean,
    default: true,
  },
  isPagination: {
    type: Boolean,
    default: true,
  },
  scroll: {
    type: Object,
    default: () => ({
      x: "max-content",
    }),
  },
  wrapperClassName: {
    type: String,
    default: "min-h-[350px]",
  },
});

const emit = defineEmits(["update:paginationParams", "tableChange"]);

const handleTableChange = (pagination: any, filters: any, sorter: any) => {
  emit("tableChange", pagination, filters, sorter);
};

const onPaginationChange = (page: number, pageSize: number) => {
  emit("update:paginationParams", {
    ...props.paginationParams,
    page,
    size: pageSize,
  });
};

const totalFormatter = (total: number, range: [number, number]) => {
  return props.showTotal
      ? `Hiển thị ${range[0]}-${range[1]} trong tổng số ${total} bản ghi`
      : "";
};

const tableWrapper = ref<HTMLElement | null>(null);
const tableHeight = useTableHeight(tableWrapper, 400);

const computedScroll = computed(() => {
  const y = tableHeight.value || 0;
  const isScrollable =
      props.dataSource.length > 1 &&
      tableWrapper.value &&
      tableWrapper.value.scrollHeight > y;

  return isScrollable
      ? {y: props.scroll?.y || y, x: props.scroll?.x || "none"}
      : {x: props.scroll?.x || "none"};
});
</script>