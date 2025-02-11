import {DefaultResponse, PaginationParams, PaginationResponse, ResponseList} from "@/infrastructure/types/api.common";
import {Ref} from "vue";
import request from "@/infrastructure/services/request.ts";
import {PREFIX_API_ADMIN_VOUCHER} from "@/infrastructure/constants/url.ts";
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

export interface PhieuGiamGiaRequest {
    // name: string | null;
    // email: string | null;
    ten : string;
    soLuong: number;
    dieuKienGiam: string;
    giamToiDa: string;
    loaiGiam: boolean;
    kieu: boolean;
    giaTriGiam: string;
    ngayBatDau: number | null;
    ngayKetThuc: number | null;
}

export type VoucherResponse = ResponseList & {
    ten : string;
    soLuong: number;
    dieuKienGiam: string;
    giamToiDa: string;
    loaiGiam: boolean;
    kieu: boolean;
    giaTriGiam: string;
    ngayBatDau: number;
    ngayKetThuc: number;
    trangThai : string;
};

export type DetailVoucherResponse = {
    id : string;
    ten : string;
    soLuong: number;
    dieuKienGiam: string;
    giamToiDa: string;
    loaiGiam: boolean;
    kieu: boolean;
    giaTriGiam: string;
    ngayBatDau: number;
    ngayKetThuc: number;
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

export const createVoucher = async (data: PhieuGiamGiaRequest) => {
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
        method: "GET",
    }) as AxiosResponse<
        DefaultResponse<DetailVoucherResponse>
    >;

};

export const updateVoucher = async (VoucherId: string, data: PhieuGiamGiaRequest) => {
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
export interface FindKhachHangRequest extends PaginationParams{
    keyword : string | null;
}

export type KhachHangResponse = ResponseList & {
    name: string;
    phone: string;
    email: string;
    ngaySinh: number;
}

export const getListKhachHang = async (params: Ref<FindKhachHangRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_VOUCHER}/khach-hang`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<KhachHangResponse>>>
    >;

    return res.data;
};

export const getKhachHangInPhieuGiamGia = async (VoucherId: Ref<string | null>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_VOUCHER}/khach-hang/${VoucherId.value}`,
        method: "GET",
    })) as AxiosResponse<
    DefaultResponse<Array<KhachHangResponse>>
    >;
    return res.data;
};

export interface VoucherKhachHangRequest {
    idKhachHangs : string[] | null;
    idPhieuGiamGia?: string | null;
    nhanVien?: string | null;
}
export interface VoucherAndCustomerVoucherRequest{
    phieuGiamGiaRequest : PhieuGiamGiaRequest | null;
    voucherKhachHangRequest : VoucherKhachHangRequest | null;
}

export const createCustomerVoucher = async (data: VoucherAndCustomerVoucherRequest) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_VOUCHER}/save-voucher-khach-hang`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const updateCustomerVoucher = async (VoucherId: string, data: VoucherAndCustomerVoucherRequest) => {
    return await request({
        url: `${PREFIX_API_ADMIN_VOUCHER}/save-voucher-khach-hang/${VoucherId}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const changeStatusVoucher = async (VoucherId: string, trangThai: string) => {
    return await request({
        url: `${PREFIX_API_ADMIN_VOUCHER}/change-status/${VoucherId}/${trangThai}`,
        method: "PUT",
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

