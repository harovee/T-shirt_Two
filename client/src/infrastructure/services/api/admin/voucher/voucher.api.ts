import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_VOUCHER} from "@/infrastructure/constants/url.ts";
import {AxiosResponse} from "axios";

export interface PropertyVoucherParams {
    keyword?: string | null;
    startDate?: Date | null;
    endDate?: Date | null;
    loaiGiam? : boolean | null;
    trangThai? : string | null;
    
    [key: string]: any;
}

export interface FindVoucherRequest extends PropertyVoucherParams, PaginationParams {

}

export interface VoucherRequest {
    // name: string | null;
    // email: string | null;
    ten : string;
    soLuong: number;
    dieuKienGiam: string;
    giamToiDa: string;
    loaiGiam: boolean;
    giaTriGiam: string;
    ngayBatDau: Date | null;
    ngayKetThuc: Date | null;
}

export type VoucherResponse = ResponseList & {
    // name: string | null;
    // email: string | null;
    ten : string;
    soLuong: number;
    dieuKienGiam: string;
    giamToiDa: string;
    loaiGiam: boolean;
    giaTriGiam: string;
    ngayBatDau: Date;
    ngayKetThuc: Date;
    trangThai : string;
};

export type DetailVoucherResponse = {
    id : string;
    ten : string;
    soLuong: number;
    dieuKienGiam: string;
    giamToiDa: string;
    loaiGiam: boolean;
    giaTriGiam: string;
    ngayBatDau: Date;
    ngayKetThuc: Date;
    trangThai : string;
};

export const getListVoucher = async (params: Ref<FindVoucherRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_VOUCHER}`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<VoucherResponse>>>
    >;

    return res.data;
};

export const createVoucher = async (data: VoucherRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_VOUCHER}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const getVoucherById = async (VoucherId: Ref<string | null>) => {
    return await request({
        url: `${PREFIX_API_ADMIN_VOUCHER}/${VoucherId.value}`,
        method: "GET"
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<DetailVoucherResponse>>>
    >;
};

export const updateVoucher = async (VoucherId: string, data: VoucherRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_VOUCHER}/${VoucherId}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const deleteVoucher = async (VoucherId: string) => {
    return await request({
        url: `${PREFIX_API_ADMIN_VOUCHER}/${VoucherId}`,
        method: "DELETE",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};
