import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_MATERIAL} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyMaterialParams {
    keyword?: string | null;
    [key: string]: any;
}

export interface FindMaterialRequest extends PropertyMaterialParams, PaginationParams {

}

export interface MaterialRequest {
    ten: string | null
}

export type MaterialResponse = ResponseList & {
    maChatLieu: string | null
    ten: string | null
};

export type ListMaterialResponse = {
    id: string | null,
    ten: string | null
};

export const getMaterials = async (params: Ref<FindMaterialRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_MATERIAL}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<MaterialResponse>>>
    >;

    return res.data;
};

export const getListMaterial = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_MATERIAL}/get-list-material`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ListMaterialResponse>>>
    >;

    return res.data;
};

export const createMaterial = async (data: MaterialRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_MATERIAL}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getMaterial = async (id: string | null) => {
    return await request({
        url: `${PREFIX_API_ADMIN_MATERIAL}/${id}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<MaterialResponse>>>
    >;
};

export const updateMaterial = async (id: string, data: MaterialRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_MATERIAL}/${id}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};