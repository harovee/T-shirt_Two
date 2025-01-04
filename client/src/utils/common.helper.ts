import dayjs from "dayjs";
import {Modal} from "ant-design-vue";
import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
import {createVNode} from "vue";

export function sortObjectKeys(obj: Record<string, any>) {
  if (!obj) return obj;

  return sortAlphaText(Object.keys(obj)).reduce((acc, key) => {
    acc[key] = obj[key];

    return acc;
  }, {});
}

export function sortAlphaText(arr: string[], type?: "asc" | "desc") {
  if (!arr) return arr;

  return arr.sort((a, b) => {
    return a.localeCompare(b) * (type === "asc" ? 1 : -1);
  });
}

export const getDateFormat = (unix: number, showTime: boolean = false) => {
  return dayjs(unix).format(showTime ? "DD/MM/YYYY HH:mm:ss" : "DD/MM/YYYY");
};

export const getDateTimeMinutesFormat = (unix: number, showTime: boolean = false) => {
  return dayjs(unix).format(showTime ? "YYYY/MM/DD HH:mm" : "YYYY/MM/DD");
};

export const confirmModal = (message, onConfirm) => {
  Modal.confirm({
    content: message,
    icon: createVNode(ExclamationCircleOutlined),
    centered: true,
    onOk() {
      onConfirm();
    },
    cancelText: 'Huỷ',
    onCancel() {
      Modal.destroyAll();
    },
  });
};