import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_CLIENT} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyClientParams {
    keyword?: string | null;
    status?: string | null;

    [key: string]: any;
}

export interface FindClientRequest extends PropertyClientParams, PaginationParams {

}

export interface ClientRequest {
    name: String | null;
    email: String | null;
    password: String | null;
    birthday: String | null;
    gender: String | null;
    phoneNumber: String | null;
    picture: String | null;
}

export type ClientResponse = ResponseList & {
    id: string | null;
    name: string | null;
    email: string | null;
    code: string | null;
    status: string | null;
};

export type DetailClientResponse = {
    id: string;
    code: string | null;
    fullName: string | null;
    birthday: string | null;
    gender: string | null;
    phoneNumber: string | null;
    email: string | null;
    password: string | null;
    status: Boolean | null;
    picture: string | null;
    createdBy: string | null;
    lastModifiedBy: string | null;
    createdDate: number | null;
    lastModifiedDate: number | null;
};

export const getClients = async (params: Ref<FindClientRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CLIENT}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ClientResponse>>>
    >;

    return res.data;
};

export const createClient = async (data: ClientRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CLIENT}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getClientById = async (clientId: Ref<string | null>) => {
    return await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/${clientId}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<DetailClientResponse>
    >;
};

export const updateClient = async (clientId: string, data: ClientRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/${clientId}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const changeStatusClient = async (clientId: string) => {
    return await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/${clientId}`,
        method: "DELETE",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const updateAvatarClient = async (clientId: string, data: ClientRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/avatar/${clientId}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
}
