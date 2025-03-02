import {PREFIX_API_AUTH_BUSINESS_TYPE, PREFIX_API_LOGIN, PREFIX_API_REGISTER} from "@/constants/url";
import request from "@/services/request";
import {DefaultResponse,} from "@/utils/types/api.common";
import {AxiosResponse} from "axios";

export interface LoginForm {
    email: string;
    password: string;
}

export interface RegisterForm {
    fullName: string,
    email: string,
    businessName: string,
    businessType: null,
    password: string,
    rePassword: string,
}

export interface BusinessType {
    id: string,
    name: string,
}

export const login = async (
    params: LoginForm
) => {
    try {
        const res = (await request({
            url: `${PREFIX_API_LOGIN}`,
            method: "POST",
            data: params,
        })) as AxiosResponse<DefaultResponse<String>>;

        return res.data;
    } catch (error) {
        return error as AxiosResponse<DefaultResponse<any>>;
    }
};

export const getBusinessType = async () => {
    try {
        const res = (await request({
            url: `${PREFIX_API_AUTH_BUSINESS_TYPE}`,
            method: "GET",
        })) as AxiosResponse<DefaultResponse<BusinessType>>;

        return res.data;
    } catch (error) {
        return error as AxiosResponse<DefaultResponse<any>>;
    }
};

export const register = async (
    params: RegisterForm
) => {
    try {
        const res = (await request({
            url: `${PREFIX_API_REGISTER}`,
            method: "POST",
            data: params,
        })) as AxiosResponse<DefaultResponse<String>>;

        return res.data;
    } catch (error) {
        return error as AxiosResponse<DefaultResponse<any>>;
    }
};