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

export const getDateFormat = (unix: number | any, showTime: boolean = false) => {
  return dayjs(unix).format(showTime ? "DD/MM/YYYY HH:mm:ss" : "DD/MM/YYYY");
};

export const getDateTimeMinutesFormat = (unix: number, showTime: boolean = false) => {
  return dayjs(unix).format(showTime ? "YYYY/MM/DD HH:mm" : "YYYY/MM/DD");
};

export const convertDateFormat = (inputDate: string): string => {
  const parsedDate = dayjs(inputDate, 'DD/MM/YYYY HH:mm');
  if (!parsedDate.isValid()) {
    throw new Error('Ngày tháng không hợp lệ');
  }
  return parsedDate.format('YYYY/MM/DD HH:mm:ss');
};

export const formatCurrency = (amount: number, currency: string, locale: string = "en-US"): string => {
  return new Intl.NumberFormat(locale, {
    style: "currency",
    currency,
  }).format(amount);
}
// // Ví dụ sử dụng:
// console.log(formatCurrency(123456.78, "USD", "en-US")); // $123,456.78
// console.log(formatCurrency(123456.78, "GBP", "en-GB")); // £123,456.78
// console.log(formatCurrency(123456.78, "VND", "vi-VN")); // 123.456,78 ₫
// console.log(formatCurrency(123456.78, "JPY", "ja-JP")); // ￥123,457

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

export const defaultProductImageSaleUrl =
  'https://static.vecteezy.com/system/resources/previews/015/079/128/large_2x/orange-empty-stage-product-show-3d-render-png.png';