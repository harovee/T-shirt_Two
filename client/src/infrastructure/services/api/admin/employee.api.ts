import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_EMPLOYEE} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyEmployeeParams {
    keyword?: string | null;
    status?: string | null;

    [key: string]: any;
}

export interface FindEmployeeRequest extends PropertyEmployeeParams, PaginationParams {

}

export interface EmployeeRequest {
    name: string | null;
    email: string | null;
}

export type EmployeeResponse = ResponseList & {
    name: string | null;
    email: string | null;
};

export type DetailEmployeeResponse = {
    name: string | null;
    email: string | null;
};

export const getEmployees = async (params: Ref<FindEmployeeRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_EMPLOYEE}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<EmployeeResponse>>>
    >;

    return res.data;
};

export const createEmployee = async (data: EmployeeRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_EMPLOYEE}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getEmployee = async (EmployeeId: Ref<string | null>) => {
    return await request({
        url: `${PREFIX_API_ADMIN_EMPLOYEE}/${EmployeeId}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<EmployeeResponse>>>
    >;
};

export const updateEmployee = async (EmployeeId: string, data: EmployeeRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_EMPLOYEE}/${EmployeeId}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const changeStatusEmployee = async (EmployeeId: string) => {
    return await request({
        url: `${PREFIX_API_ADMIN_EMPLOYEE}/${EmployeeId}`,
        method: "DELETE",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};
