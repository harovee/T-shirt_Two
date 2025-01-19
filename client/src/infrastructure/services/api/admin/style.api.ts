import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_STYLE} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyStyleParams {
    keyword?: string | null;
    [key: string]: any;
}

export interface FindStyleRequest extends PropertyStyleParams, PaginationParams {

}

export interface StyleRequest {
    ten: string | null
}

export type StyleResponse = ResponseList & {
    maKieuDang: string | null
    ten: string | null
};

export type ListStyleResponse = {
    id: string | null,
    ten: string | null
};

export const getStyles = async (params: Ref<FindStyleRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_STYLE}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<StyleResponse>>>
    >;

    return res.data;
};

export const getListStyle = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_STYLE}/get-list-style`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ListStyleResponse>>>
    >;

    return res.data;
};

export const createStyle = async (data: StyleRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_STYLE}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getStyle = async (id: string | null) => {
    return await request({
        url: `${PREFIX_API_ADMIN_STYLE}/${id}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<StyleResponse>>>
    >;
};

export const updateStyle = async (id: string, data: StyleRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_STYLE}/${id}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};