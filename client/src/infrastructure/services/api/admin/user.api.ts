import {PREFIX_API_ADMIN_USER} from "@/infrastructure/constants/url";
import request from "@/infrastructure/services/request";
import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {AxiosResponse} from "axios";
import {Ref} from "vue";

export interface PropertyUserParams {
    keyword?: string | null;
    status?: number | null;
    role?: number | null;
    subscriptionType?: string | null;

    [key: string]: any;
}

export interface FindUserRequest extends PaginationParams, PropertyUserParams {

}

export interface UserRequest {
    name: string;
    email: string;
    password: string;
    subscriptionType: subscriptionType;
    profilePicture: string;
    role: number;
    status: number;
}

export enum subscriptionType {
    google = "google",
    github = "github",
    facebook = "facebook",
    admin = "admin",
}

export type UserResponse = ResponseList & {
    name: string;
    email: string;
    password: string;
    subscriptionType: string;
    profilePicture: string;
    role: number;
    status: number;
};


export const getUsers = async (params: Ref<FindUserRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_USER}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<UserResponse>>>
    >;

    return res.data;
};

export const createUser = async (data: UserRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_USER}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getUser = async (userId: Ref<string | null>) => {
    return await request({
        url: `${PREFIX_API_ADMIN_USER}/${userId}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<UserResponse>>>
    >;
};

export const updateUser = async (userId: string, data: UserRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_USER}/${userId}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const changeStatusUser = async (userId: string) => {
    return await request({
        url: `${PREFIX_API_ADMIN_USER}/${userId}`,
        method: "DELETE",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};
