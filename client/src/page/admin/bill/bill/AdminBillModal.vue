<template>
    <a-modal
        :open="props.open"
        title="Thông tin hóa đơn"
        @cancel="hanldeClose"
        destroyOnClose
        centered
    >
        <a-form>
            <template v-for="field in formFields">
                <a-form-item
                    :lable="field.label"
                    :name="field.name"
                    v-bind="validateInfos[field.name]"
                >
                    <a-input
                        v-if="field.component === 'a-input'"
                        v-model:value="modelRef[field.name]"
                    >
                    </a-input>
                </a-form-item>
            </template>
        </a-form>
    </a-modal>
</template>
<script lang="ts">
import { Form, Modal } from 'ant-design-vue';
import {computed, createVNode, defineEmits, defineProps, reactive} from "vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import { BillRequest } from '@/infrastructure/services/api/admin/bill.api';

const props = defineProps ({
    open: Boolean
})

const emit = defineEmits(["handleClose"]);

const modelRef = reactive<BillRequest>({
  name: null,
  email: null,
});

const formFields = computed(() => [
    {
    label: "Tên khách hàng",
    name: "name",
    component: "a-input",
    placeholder: "Nhâp tên khách hàng"
  },
  {
    label: "Tên khách hàng",
    name: "name",
    component: "a-input",
    placeholder: "Nhâp tên khách hàng"
  },

])
</script>
