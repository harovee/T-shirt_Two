import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_SLEEVE} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertySleeveParams {
    keyword?: string | null;
    [key: string]: any;
}

export interface FindSleeveRequest extends PropertySleeveParams, PaginationParams {

}

export interface SleeveRequest {
    ten: string | null
}

export type SleeveResponse = ResponseList & {
    maTayAo: string | null
    ten: string | null
};

export type ListSleeveResponse = {
    id: string | null,
    ten: string | null
};

export const getSleeves = async (params: Ref<FindSleeveRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SLEEVE}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<SleeveResponse>>>
    >;

    return res.data;
};

export const getListSleeve = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SLEEVE}/get-list-sleeve`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ListSleeveResponse>>>
    >;

    return res.data;
};

export const createSleeve = async (data: SleeveRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_SLEEVE}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getSleeve = async (id: string | null) => {
    return await request({
        url: `${PREFIX_API_ADMIN_SLEEVE}/${id}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<SleeveResponse>>>
    >;
};

export const updateSleeve = async (id: string, data: SleeveRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_SLEEVE}/${id}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};