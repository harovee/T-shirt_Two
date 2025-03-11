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

export type ClientAddressRequest = {
    name: string,
    phoneNumber: string,
    line: string,
    ward: string,
    district: string,
    province: string,
    isDefault: string,
    clientId: string,
}

export type ClientAddressPaymentRequest = {
    name: string,
    email:string,
    phoneNumber: string,
    line: string,
    ward: string,
    district: string,
    province: string,
    isDefault: string,
    clientId: string,
    ghiChu: string
}

export type ClientAddressRequestCreate = {
    name: String,
    phoneNumber: String,
    email: String,
    birthday: String,
    gender: String,
    picture: String,
    line: String,
    ward: String,
    district: String,
    province: String,
}

export type ClientAddressCommonOptionsResponse = {
    id: string,
    name: string,
}

export type ClientAddressResponse = {
    id: string,
    name: string,
    phoneNumber: string,
    line: string,
    ward: string,
    district: string,
    province: string,
    isDefault: string,
}

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

export const createClientAddressMo = async (data: ClientAddressRequestCreate) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/mo`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getClientById = async (clientId: string) => {
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

export const getProvinces = async () => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/province`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<Array<ClientAddressCommonOptionsResponse>>
    >;

    return res.data;
};

export const getDistrictsByProvinceId = async (provinceId: number) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/district/${provinceId}`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<Array<ClientAddressCommonOptionsResponse>>
    >;

    return res.data;
};

export const getWardsByDistrictId = async (districtId: number) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/ward/${districtId}`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<Array<ClientAddressCommonOptionsResponse>>
    >;

    return res.data;
};

export const createClientAddress = async (data: ClientAddressRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/address`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const updateClientAddress = async (addressId: string, data: ClientAddressRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/address/${addressId}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
}

export const changeClientAddressDefault = async (addressId: string) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/address/default/${addressId}`,
        method: "PUT",
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
}

export const getClientAddressesResponseByClientId = async (clientId: string) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_CLIENT}/address/${clientId}`,
        method: "GET",
    })) as AxiosResponse<
        DefaultResponse<Array<ClientAddressResponse>>
    >;

    return res.data;
}