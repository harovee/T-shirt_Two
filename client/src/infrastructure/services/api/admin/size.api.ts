import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_SIZE} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertySizeParams {
    keyword?: string | null;
    [key: string]: any;
}

export interface FindSizeRequest extends PropertySizeParams, PaginationParams {

}

export interface SizeRequest {
    ten: string | null,
    chieuCaoMin: number | null,
    chieuCaoMax: number | null,
    canNangMin: number | null,
    canNangMax: number | null
}

export type SizeResponse = ResponseList & {
    maKichCo: string | null
    ten: string | null
    chieuCaoMin: number | null,
    chieuCaoMax: number | null,
    canNangMin: number | null,
    canNangMax: number | null
};

export type ListSizeResponse = {
    id: string | null,
    ten: string | null
};

export const getSizes = async (params: Ref<FindSizeRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SIZE}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<SizeResponse>>>
    >;

    return res.data;
};

export const getListSize = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SIZE}/get-list-size`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ListSizeResponse>>>
    >;

    return res.data;
};

export const createSize = async (data: SizeRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SIZE}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getSize = async (id: string | null) => {
    return await request({
        url: `${PREFIX_API_ADMIN_SIZE}/${id}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<SizeResponse>>>
    >;
};

export const updateSize = async (id: string, data: SizeRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_SIZE}/${id}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};