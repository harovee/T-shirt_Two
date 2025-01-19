import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_COLLAR} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyCollarParams {
    keyword?: string | null;
    [key: string]: any;
}

export interface FindCollarRequest extends PropertyCollarParams, PaginationParams {

}

export interface CollarRequest {
    ten: string | null
}

export type CollarResponse = ResponseList & {
    maCoAo: string | null
    ten: string | null
};

export type ListCollarResponse = {
    id: string | null,
    ten: string | null
};

export const getCollars = async (params: Ref<FindCollarRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_COLLAR}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<CollarResponse>>>
    >;

    return res.data;
};

export const getListCollar = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_COLLAR}/get-list-collar`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ListCollarResponse>>>
    >;

    return res.data;
};

export const createCollar = async (data: CollarRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_COLLAR}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getCollar = async (id: string | null) => {
    return await request({
        url: `${PREFIX_API_ADMIN_COLLAR}/${id}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<CollarResponse>>>
    >;
};

export const updateCollar = async (id: string, data: CollarRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_COLLAR}/${id}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};