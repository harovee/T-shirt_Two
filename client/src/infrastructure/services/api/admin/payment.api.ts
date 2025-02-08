import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {API_ADMIN_PAYMENT} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyVoucherParams {
    keyword?: string | null;
    startDate?: number | null;
    endDate?: number | null;
    loaiGiam? : boolean | null;
    trangThai? : string | null;
    
    [key: string]: any;
}

export interface FindVoucherRequest extends PropertyVoucherParams, PaginationParams {

}


export type VoucherResponse = ResponseList & {
    ten : string;
    soLuong: number;
    dieuKienGiam: string;
    giamToiDa: string;
    loaiGiam: boolean;
    kieu: boolean;
    giaTriGiam: string;
    
};

export const getListVoucher = async (params: Ref<FindVoucherRequest>) => {
    const res = (await request({
        url: `${API_ADMIN_PAYMENT}/voucher`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<VoucherResponse>>>
    >;

    return res.data;
};


export const getVoucherById = async (VoucherId: Ref<string | null>) => {
    return await request({
        url: `${API_ADMIN_PAYMENT}/voucher/${VoucherId.value}`,
        method: "GET",
    }) as AxiosResponse<
        DefaultResponse<VoucherResponse>
    >;
};

export type CustomerResponse = ResponseList & {
    profilePicture: string;
    name: string;
    phoneNumber: string;
    email: string;
    tinh: string;
    huyen: string;
    xa: string;
    soNha: string;
}

export interface FindCustomerRequest extends PaginationParams{
    keyword : string | null;
}

export const getListCustomer = async (params: Ref<FindCustomerRequest>) => {
    const res = (await request({
        url: `${API_ADMIN_PAYMENT}/khach-hang`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<CustomerResponse>>>
    >;

    return res.data;
};

export const getCustomerById = async (id: Ref<string | null>) => {
    return await request({
        url: `${API_ADMIN_PAYMENT}/khach-hang/${id.value}`,
        method: "GET",
    }) as AxiosResponse<
        DefaultResponse<CustomerResponse>
    >;

};



