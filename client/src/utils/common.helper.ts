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

export const convertDateFormat = (inputDate: number): string => {
  const parsedDate = dayjs(inputDate);
  if (!parsedDate.isValid()) {
    throw new Error('Ngày tháng không hợp lệ');
  }
  return parsedDate.format('DD/MM/YYYY HH:mm:ss'); // Định dạng theo yêu cầu
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