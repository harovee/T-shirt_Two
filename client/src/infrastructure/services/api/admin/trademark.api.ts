import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_TRADEMARK} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyTrademarkParams {
    keyword?: string | null;
    [key: string]: any;
}

export interface FindTrademarkRequest extends PropertyTrademarkParams, PaginationParams {

}

export interface TrademarkRequest {
    ten: string | null
}

export type TrademarkResponse = ResponseList & {
    maThuongHieu: string | null
    ten: string | null
};

export type ListTrademarkResponse = {
    id: string | null,
    ten: string | null
};

export const getTrademarks = async (params: Ref<FindTrademarkRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_TRADEMARK}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<TrademarkResponse>>>
    >;

    return res.data;
};

export const getListTrademark = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_TRADEMARK}/get-list-trademark`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ListTrademarkResponse>>>
    >;

    return res.data;
};

export const createTrademark = async (data: TrademarkRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_TRADEMARK}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getTrademark = async (id: string | null) => {
    return await request({
        url: `${PREFIX_API_ADMIN_TRADEMARK}/${id}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<TrademarkResponse>>>
    >;
};

export const updateTrademark = async (id: string, data: TrademarkRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_TRADEMARK}/${id}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};