import {PREFIX_API_LOGIN, PREFIX_API_REGISTER} from "@/infrastructure/constants/url";
import request from "@/infrastructure/services/request";
import {DefaultResponse,} from "@/infrastructure/types/api.common";
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

export const login = async (params: LoginForm) => {
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

export const register = async (params: RegisterForm) => {
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