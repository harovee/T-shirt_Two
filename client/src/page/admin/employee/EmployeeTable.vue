<template>
  <div class="p-4 rounded-xl border-2 shadow-purple-950 shadow-xl">
    <div class="flex justify-between items-center">
      <div>
        <h3 class="text-xl font-semibold text-gray-800">Danh sách khách hàng</h3>
        <p class="text-sm text-gray-500">
          Hiển thị danh sách khách hàng T-Shirts Two
        </p>
      </div>
      <div class="p-2.5">
        <a-tooltip
            title="Thêm bài hát"
            trigger="hover"
        >
          <a-button
              class="bg-purple-300 flex justify-between items-center gap-2"
              size="large"
              @click="$emit('handleOpenModalCreate', $event)"
          >
            <v-icon name="md-addcircle"/>
          </a-button>
        </a-tooltip>
      </div>
    </div>
    <table-spotify
        wrapperClassName="min-h-[410px]"
        :columns="columnsEmployee"
        :data-source="props.dataSource?.data"
        :loading="loading"
        :pagination-params="paginationParams || {}"
        :total-pages="props.dataSource?.totalPages || 1"
        @update:pagination-params="$emit('update:paginationParams', $event)"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'another'" class="text-center">
        </div>
        <div v-else-if="column.key === 'status'" class="text-center">
          <a-tag v-if="record.status === 'false'" color="success">Hoạt động</a-tag>
          <a-tag v-else-if="record.status === 'true'" color="warning">Vô hiệu hóa</a-tag>
          <a-tag v-else color="secondary">Không xác định</a-tag>
        </div>
        <div v-else-if="column.key === 'action'" class="flex items-center justify-center space-x-2">
          <a-popconfirm
              title="Bạn có chắc chắn muốn chuyển đổi trạng thái không?"
              ok-text="Có"
              cancel-text="Hủy"
              @confirm="handleChangeStatusEmployee(record.id)"
          >
            <a-tooltip
                title="Cập nhật trạng thái"
                trigger="hover"
            >
              <a-button
                  class="bg-purple-100"
                  size="middle"
                  shape="round"
              >
                <v-icon name="fa-exchange-alt"/>
              </a-button>
            </a-tooltip>
          </a-popconfirm>
        </div>
      </template>
    </table-spotify>
  </div>
</template>

<script setup lang="ts">
import TableSpotify from "@/components/ui/Table.vue";
import {ColumnType} from "ant-design-vue/es/table";
import {toast} from "vue3-toastify";
import {defineEmits} from "vue";
import {useChangeStatusEmployee} from "@/infrastructure/services/service/admin/employee.action.ts";

const emit = defineEmits([
  "update:paginationParams",
  "handleOpenModalCreate",
  "handleCloseModalCreate"
]);

const props = defineProps({
  dataSource: Object,
  loading: Boolean,
  paginationParams: Object,
});

const {mutate: changeStatusEmployee} = useChangeStatusEmployee();

const handleChangeStatusEmployee = (id: string) => {
  try {
    changeStatusEmployee(id, {
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
    console.error("🚀 ~ handleChangeStatus ~ error:", error);
    toast.error(
        error?.response?.data?.message
    );
  }
}

const columnsEmployee: ColumnType[] = [
  {
    title: "#",
    dataIndex: "catalog",
    key: "catalog",
    ellipsis: true,
    width: 50,
    align: "center"
  },
  {
    title: "Tên khách hàng",
    dataIndex: "name",
    key: "name",
    ellipsis: true,
    width: 200,
    resizable: true
  },
  {
    title: "Email khách hàng",
    dataIndex: "email",
    key: "email",
    ellipsis: true,
    width: 200,
    resizable: true
  },
  {
    title: "Trạng thái",
    dataIndex: "status",
    key: "status",
    ellipsis: true,
    width: 150,
    align: "center"
  },
  {
    title: "Hành động",
    key: "action",
    align: "center",
    width: 300,
    fixed: "right"
  },
];
</script>