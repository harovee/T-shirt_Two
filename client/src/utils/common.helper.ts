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

export const convertDateFormat = (inputDate: number | null): string => {
    const parsedDate = dayjs(inputDate);
    return parsedDate.format('HH:mm:ss DD/MM/YYYY'); // Định dạng theo yêu cầu
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

 export const convertStringToTimeStampSecond = (date: string): number | null => {
  const dateObj = convertStringToDate(date);
  return convertDateToTimeStampSecond(dateObj);
}

export const convertTimeStampSecondToStringTimeZone = (timeStampSecond: number): string | null => {
  const dateObj = convertTimeStampSecondToDate(timeStampSecond);
  return convertDateToString(dateObj);
}

export const convertStringToDate = (date: string): Date | null => {
  if (!date || date.trim() === '') {
      return null;
  }
  return new Date(date);
}

export const convertDateToTimeStampSecond = (date: Date | null): number | null => {
  if (date) {
      return Math.floor(date.getTime() / 1000);
  }
  return null;
}

export const convertTimeStampSecondToDate = (timeStampSecond: number): Date  => {
  return new Date(timeStampSecond * 1000);
}

export const convertDateToString = (date: Date): string | null => {
  if (date) {
      return date.toISOString();
  }
  return null;
}

export const addMinutes = (date: Date, minutes: number): Date => {
  const newDate = new Date(date);
  newDate.setMinutes(newDate.getMinutes() + minutes);
  return newDate;
}

export const getCurrentTimeStampSecond = (): number => {
  return Math.floor(Date.now() / 1000);
}

export const defaultProductImageSaleUrl =
  'https://static.vecteezy.com/system/resources/previews/015/079/128/large_2x/orange-empty-stage-product-show-3d-render-png.png';

 export const convertStringToTimeStampSecond = (date: string): number | null => {
  const dateObj = convertStringToDate(date);
  return convertDateToTimeStampSecond(dateObj);
}

export const convertTimeStampSecondToStringTimeZone = (timeStampSecond: number): string | null => {
  const dateObj = convertTimeStampSecondToDate(timeStampSecond);
  return convertDateToString(dateObj);
}

export const convertStringToDate = (date: string): Date | null => {
    if (!date || date.trim() === '') {
        return null;
    }
    return new Date(date);
}

export const convertDateToTimeStampSecond = (date: Date | null): number | null => {
  if (date) {
      return Math.floor(date.getTime() / 1000);
  }
  return null;
}

export const convertTimeStampSecondToDate = (timeStampSecond: number): Date  => {
  return new Date(timeStampSecond * 1000);
}

export const convertDateToString = (date: Date): string | null => {
    if (date) {
        return date.toISOString();
    }
    return null;
}

export const addMinutes = (date: Date, minutes: number): Date => {
    const newDate = new Date(date);
    newDate.setMinutes(newDate.getMinutes() + minutes);
    return newDate;
}

export const getCurrentTimeStampSecond = (): number => {
    return Math.floor(Date.now() / 1000);
}

export const convertToAntdDatePicker = (timestamp: string | number | null): dayjs.Dayjs | null => {
    if (!timestamp) return null;
    const date = dayjs(Number(timestamp));
    return date.isValid() ? date : null;
};

/**
 * @param input - 'Phạm Thị Lan Anh'
 * @returns resuilt - 'anhptl'
 */
export function convertTextCode(input: string | null): string {
    if (!input) return "";
    const words = input.split(" "); // Tách chuỗi thành các từ
    const lastName = removeAccents(words[words.length - 1].toLowerCase());
    const initials = words
        .slice(0, words.length - 1)
        .map(word => removeAccents(word[0].toLowerCase()))
        .join("");

    return `${lastName}${initials}`;
}

const removeAccents = (str: string): string =>
    str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").replace(/đ/g, "d").replace(/Đ/g, "D");

export const defaultProductImageSaleUrl =
  'https://static.vecteezy.com/system/resources/previews/015/079/128/large_2x/orange-empty-stage-product-show-3d-render-png.png';