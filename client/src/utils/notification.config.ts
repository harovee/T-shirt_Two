import { notification } from "ant-design-vue";

export const notificationType = {
  success: 'success',
  info: 'info',
  warning: 'warning',
  error: 'error',
}

export const openNotification = (type: string, message: string, description: string) => {
    notification[type]({
      message: message,
      description: description,
      duration: 4
    });
  };

export const successNotiSort = (message: string) => {
  openNotification(notificationType.success, message, '');
}
export const errorNotiSort = (message: string) => {
  openNotification(notificationType.error, message, '');
}
export const warningNotiSort = (message: string) => {
  openNotification(notificationType.warning, message, '');
}
export const infoNotiSort = (message: string) => {
  openNotification(notificationType.info, message, '');
}
