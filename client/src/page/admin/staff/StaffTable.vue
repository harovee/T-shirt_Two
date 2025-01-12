<template>
  <div class="p-4 rounded-xl border-2 shadow-purple-950 shadow-xl">
    <div class="flex justify-between items-center">
      <div>
        <h3 class="text-xl font-semibold text-gray-800">Danh sách nhân viên</h3>
        <p class="text-sm text-gray-500">
          Hiển thị danh sách nhân viên T-Shirts Two
        </p>
      </div>
      <div class="p-2.5">
        <a-tooltip
            title="Thêm nhân viên"
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
    <table-t-shirt
        wrapperClassName="min-h-[410px]"
        :columns="columnsStaff"
        :data-source="props.dataSource?.data"
        :loading="loading"
        :pagination-params="paginationParams || {}"
        :total-pages="props.dataSource?.totalPages || 1"
        @update:pagination-params="$emit('update:paginationParams', $event)"
    >
      <template #bodyCell="{ column, record }">
        <div v-if="column.key === 'another'" class="text-center">
        </div>
        <div v-else-if="column.key === 'code'">
          NV{{record.code}}
        </div>
        <div v-else-if="column.key === 'status'" class="text-center">
          <a-tag v-if="record.status === 'false'" color="success">Hoạt động</a-tag>
          <a-tag v-else-if="record.status === 'true'" color="warning">Vô hiệu hóa</a-tag>
          <a-tag v-else color="violet">Không xác định</a-tag>
        </div>
        <div v-else-if="column.key === 'action'" class="flex items-center justify-center space-x-2">
          <a-popconfirm
              title="Bạn có chắc chắn muốn chuyển đổi trạng thái không?"
              ok-text="Có"
              cancel-text="Hủy"
              @confirm="handleChangeStatusStaff(record.id)"
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

          <a-tooltip
              title="Chi tiết nhân viên"
              trigger="hover"
          >
            <a-button
                class="bg-purple-100"
                size="middle"
                shape="round"
                @Click="handleRedirectStaffDetail(record.id)"
            >
              <v-icon name="fa-eye"/>
            </a-button>
          </a-tooltip>
        </div>
      </template>
    </table-t-shirt>
  </div>
</template>

<script setup lang="ts">
import TableTShirt from "@/components/ui/Table.vue";
import {ColumnType} from "ant-design-vue/es/table";
import {toast} from "vue3-toastify";
import {defineEmits, watch} from "vue";
import {useChangeStatusStaff} from "@/infrastructure/services/service/admin/staff.action.ts";
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import router from "@/infrastructure/routes/router.ts";

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

const {mutate: changeStatusStaff} = useChangeStatusStaff();

const handleChangeStatusStaff = (id: string) => {
  try {
    changeStatusStaff(id, {
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

const handleRedirectStaffDetail = (id: string) => {
  const staffDetailPath =
      ROUTES_CONSTANTS.ADMIN.children.STAFF_DETAIL.path.replace(':id', id);
  router.push(staffDetailPath);
}

const columnsStaff: ColumnType[] = [
  {
    title: "#",
    dataIndex: "catalog",
    key: "catalog",
    ellipsis: true,
    width: 50,
    align: "center"
  },
  {
    title: "Mã nhân viên",
    dataIndex: "code",
    key: "code",
    ellipsis: true,
    width: 200,
    resizable: true
  },
  {
    title: "Tên nhân viên",
    dataIndex: "name",
    key: "name",
    ellipsis: true,
    width: 200,
    resizable: true
  },
  {
    title: "Email nhân viên",
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
watch(
    () => props.dataSource,
    (newData) => {
      console.log(newData);
    }
)
</script>