import {DefaultResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {API_ADMIN_ORDER_NOTIFICATION} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";


export type NotificationResponse = ResponseList & {
    id: string | null
    orderId: string | null
    createdDate: Number | null
    content: string | null
    isRead: boolean | null
};

export const getListNotification = async () => {
    const res = (await request({
        url: API_ADMIN_ORDER_NOTIFICATION,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<Array<NotificationResponse>>
    >;

    return res.data;
};

export const deleteNotification = async (id: string) => {
    return await request({
        url: `${API_ADMIN_ORDER_NOTIFICATION}/${id}`,
        method: "DELETE",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const deleteAllNotification = async () => {
    return await request({
        url: `${API_ADMIN_ORDER_NOTIFICATION}/all-notification`,
        method: "DELETE",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};
