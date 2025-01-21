<template>
  <div class="p-4 rounded-xl border-2 ">
    <div class="flex justify-between items-center">
      <div>
        <h3 class="text-xl font-semibold text-gray-800">Danh sách khách hàng</h3>
        <p class="text-sm text-gray-500">
          Hiển thị danh sách khách hàng T-Shirts Two
        </p>
      </div>
      <div class="p-2.5">
        <a-tooltip
            title="Thêm khách hàng"
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
        :columns="columnsClient"
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
          KH{{ record.code }}
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
              @confirm="handleChangeStatusClient(record.id)"
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
              title="Chi tiết khách hàng"
              trigger="hover"
          >
            <a-button
                class="bg-purple-100"
                size="middle"
                shape="round"
                @Click="handleRedirectClientDetail(record.id)"
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
import {defineEmits} from "vue";
import {useChangeStatusClient} from "@/infrastructure/services/service/admin/client.action.ts";
import {ROUTES_CONSTANTS} from "@/infrastructure/constants/path.ts";
import router from "@/infrastructure/routes/router.ts";
import {notification} from "ant-design-vue";

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

const {mutate: changeStatusClient} = useChangeStatusClient();

const handleChangeStatusClient = (id: string) => {
  try {
    changeStatusClient(id, {
      onSuccess: (res: any) => {
        notification.success({
          message: 'Thông báo',
          description: res?.data?.message,
          duration: 4,
        });
      },
      onError: (error: any) => {
        notification.error({
          message: 'Thông báo',
          description: error?.response?.data?.message,
          duration: 4,
        });
      },
    })
  } catch (error: any) {
    console.error("🚀 ~ handleChangeStatus ~ error:", error);
    notification.warning({
      message: 'Thông báo',
      description: error?.response?.data?.message,
      duration: 4,
    });
  }
}

const handleRedirectClientDetail = (id: string) => {
  const clientDetailPath =
      ROUTES_CONSTANTS.ADMIN.children.CLIENT_DETAIL.path.replace(':id', id);
  router.push(clientDetailPath);
}

const columnsClient: ColumnType[] = [
  {
    title: "#",
    dataIndex: "catalog",
    key: "catalog",
    ellipsis: true,
    width: 50,
    align: "center"
  },
  {
    title: "Mã khách hàng",
    dataIndex: "code",
    key: "code",
    ellipsis: true,
    width: 200,
    resizable: true
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
    title: "Số điện thoại",
    dataIndex: "phoneNumber",
    key: "phoneNumber",
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