import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_STAFF} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyStaffParams {
    keyword?: string | null;
    status?: string | null;

    [key: string]: any;
}

export interface FindStaffRequest extends PropertyStaffParams, PaginationParams {

}

export interface StaffRequest {
    name: String | null;
    email: String | null;
    username: String | null,
    password: String | null,
    birthday: String | null,
    gender: Boolean | null,
    phoneNumber: String | null,
    identity: String | null,
}

export type StaffResponse = ResponseList & {
    id: string | null;
    name: string | null;
    email: string | null;
    code: string | null;
    status: string | null;
};

export type DetailStaffResponse = {
    name: string | null;
    email: string | null;
};

export const getStaffs = async (params: Ref<FindStaffRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_STAFF}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<StaffResponse>>>
    >;

    return res.data;
};

export const createStaff = async (data: StaffRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_STAFF}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getStaffById = async (staffId: Ref<string | null>) => {
    return await request({
        url: `${PREFIX_API_ADMIN_STAFF}/${staffId}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<StaffResponse>>>
    >;
};

export const updateStaff = async (staffId: string, data: StaffRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_STAFF}/${staffId}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const changeStatusStaff = async (staffId: string) => {
    return await request({
        url: `${PREFIX_API_ADMIN_STAFF}/${staffId}`,
        method: "DELETE",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};
