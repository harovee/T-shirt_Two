import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_CATEGORY} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyCategoryParams {
    keyword?: string | null;
    [key: string]: any;
}

export interface FindCategoryRequest extends PropertyCategoryParams, PaginationParams {

}

export interface CategoryRequest {
    ten: string | null
}

export type CategoryResponse = ResponseList & {
    maDanhMuc: string | null
    ten: string | null
};

export type ListCategoryResponse = {
    id: string | null,
    ten: string | null
};

export const getCategorys = async (params: Ref<FindCategoryRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CATEGORY}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<CategoryResponse>>>
    >;

    return res.data;
};

export const getListCategory = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CATEGORY}/get-list-category`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ListCategoryResponse>>>
    >;

    return res.data;
};

export const createCategory = async (data: CategoryRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CATEGORY}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getCategory = async (id: string | null) => {
    return await request({
        url: `${PREFIX_API_ADMIN_CATEGORY}/${id}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<CategoryResponse>>>
    >;
};

export const updateCategory = async (id: string, data: CategoryRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_CATEGORY}/${id}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};