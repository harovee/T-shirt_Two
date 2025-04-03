import { DefaultResponse, ResponseList } from "@/infrastructure/types/api.common";
import request from "@/infrastructure/services/request.ts";
import { API_ADMIN_CHAT_HISTORY } from "@/infrastructure/constants/url";
import { AxiosResponse } from "axios";

export type ChatHistoryResponse = ResponseList & {
    sender: string | null;
    createdDate: number | null;
    type: string | null;
    content: string | null;
    roomId: string | null;
}

export const getChatHistories = async (roomId: string | null) => {
    const res = (await request({
        url: `${API_ADMIN_CHAT_HISTORY}/${roomId}`,
        method: 'GET',
    })) as AxiosResponse<DefaultResponse<Array<ChatHistoryResponse>>>;

    return res.data;
}

