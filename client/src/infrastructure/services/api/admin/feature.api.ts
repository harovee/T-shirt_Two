import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_FEATURE} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyFeatureParams {
    keyword?: string | null;
    [key: string]: any;
}

export interface FindFeatureRequest extends PropertyFeatureParams, PaginationParams {

}

export interface FeatureRequest {
    ten: string | null
}

export type FeatureResponse = ResponseList & {
    maTinhNang: string | null
    ten: string | null
};

export type ListFeatureResponse = {
    id: string | null,
    ten: string | null
};

export const getFeatures = async (params: Ref<FindFeatureRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_FEATURE}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<FeatureResponse>>>
    >;

    return res.data;
};

export const getListFeature = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_FEATURE}/get-list-feature`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ListFeatureResponse>>>
    >;

    return res.data;
};

export const createFeature = async (data: FeatureRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_FEATURE}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getFeature = async (id: string | null) => {
    return await request({
        url: `${PREFIX_API_ADMIN_FEATURE}/${id}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<FeatureResponse>>>
    >;
};

export const updateFeature = async (id: string, data: FeatureRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_FEATURE}/${id}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};