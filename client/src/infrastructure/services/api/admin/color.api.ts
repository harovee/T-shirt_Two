import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_COLOR} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyColorParams {
    keyword?: string | null;
    [key: string]: any;
}

export interface FindColorRequest extends PropertyColorParams, PaginationParams {

}

export interface ColorRequest {
    maMauSac: string | null
    ten: string | null
}

export type ColorResponse = ResponseList & {
    maMauSac: string | null
    ten: string | null
};

export type ListColorResponse = {
    id: string | null,
    ten: string | null,
    maMauSac: string | null
};

export const getColors = async (params: Ref<FindColorRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_COLOR}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ColorResponse>>>
    >;

    return res.data;
};

export const getListColor = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_COLOR}/get-list-color`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ListColorResponse>>>
    >;

    return res.data;
};

export const createColor = async (data: ColorRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_COLOR}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getColor = async (id: string | null) => {
    return await request({
        url: `${PREFIX_API_ADMIN_COLOR}/${id}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ColorResponse>>>
    >;
};

export const updateColor = async (id: string, data: ColorRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_COLOR}/${id}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};