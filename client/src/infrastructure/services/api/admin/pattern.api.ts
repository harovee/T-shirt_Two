import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_PATTERN} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyPatternParams {
    keyword?: string | null;
    [key: string]: any;
}

export interface FindPatternRequest extends PropertyPatternParams, PaginationParams {

}

export interface PatternRequest {
    ten: string | null
}

export type PatternResponse = ResponseList & {
    maHoaTiet: string | null
    ten: string | null
};

export type ListPatternResponse = {
    id: string | null,
    ten: string | null
};

export const getPatterns = async (params: Ref<FindPatternRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PATTERN}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<PatternResponse>>>
    >;

    return res.data;
};

export const getListPattern = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PATTERN}/get-list-pattern`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ListPatternResponse>>>
    >;

    return res.data;
};

export const createPattern = async (data: PatternRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_PATTERN}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getPattern = async (id: string | null) => {
    return await request({
        url: `${PREFIX_API_ADMIN_PATTERN}/${id}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<PatternResponse>>>
    >;
};

export const updatePattern = async (id: string, data: PatternRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_PATTERN}/${id}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};