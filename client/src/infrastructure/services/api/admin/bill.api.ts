import { AxiosResponse } from 'axios';
import { API_ADMIN_BILL, API_ADMIN_BILL_REFUND, API_ADMIN_COUNT_BILL } from '@/infrastructure/constants/url';
import { DefaultResponse, PaginationParams, PaginationResponse, ResponseList } from '@/infrastructure/types/api.common';
import { Ref } from 'vue';
import request from "@/infrastructure/services/request.ts";


export interface BillPropsParams {
    keyword?: string | null;
    trangThai? :string | null;
    loaiHD?: string | null;
    ngayBatDau?: number | null;
    ngayKetThuc?: number | null;
    [key: string]: any;
}

export interface FindBillRequest extends BillPropsParams, PaginationParams {

}

export interface BillRequest {
    soDienThoai: String | null;
    diaChiNguoiNhan: String | null;
    // idKhachHang: String | null;
    tenNguoiNhan: String | null;
    ghiChu: String | null;
}

export interface BillWaitRequest {
    trangThai: String | null;
    idKhachHang: String | null;
    idPhieuGiamGia: String | null;
    idNhanVien: String | null;
    diaChiNguoiNhan: String | null;
    tenNguoiNhan: String | null;
    soDienThoai: String | null;
    ngayShip: number | null;
    ghiChu: String | null;
     tienGiam: number | null;
     tienShip: number | null;
     tongTien: number | null;
}

export interface ChangeStatusBillRequest {
    trangThai: String | null;
}

export interface ChangeStatusBillRequest {
    trangThai: String | null;
}

export interface BillCreateRequest {
    loaiHD: string | null,
    idNhanVien: string | null,
    idKhachHang: string | null,
    idPhieuGiamGia: string | null
}

export type BillResponse = ResponseList & {
    catalog: number | null;
    ma: string | null;
    maNhanVien: string | null;
    loaiHD: string | null;
    tongTien: number;
    tienShip: number | null;
    tienGiam: number | null;
    trangThai: string | null;
    tenKhachHang: string | null;
    soDienThoai: string | null;
    diaChiNguoiNhan: string | null;
    tenNguoiNhan: string | null;
    ghiChu: string | null;
}

export type BillRefundResponse = ResponseList & {
    ma: string | null;
    tongTien: number | null;
    tienGiam: number | null;
    tenKhachHang: string | null;
    soDienThoai: string | null;
    diaChiNguoiNhan: string | null;
    tenNguoiNhan: string | null;
}

export type BillWaitResponse = ResponseList & {
    catalog: number | null;
    ma: string | null;
    maNhanVien: string | null;
    loaiHD: string | null;
    tongTien: number | null;
    tienShip: number | null;
    tienGiam: number | null;
    trangThai: string | null;
    tenKhachHang: string | null;
    soDienThoai: string | null;
    diaChiNguoiNhan: string | null;
    tenNguoiNhan: string | null;
    ghiChu: string | null;
    idNhanVien: string | null;
    idKhachHang: string | null;
    idPhieuGiamGia: string | null
}

export interface CountBillByStatusResponse {
    [key: string]: number;
  }


export const getBills = async (params: Ref<FindBillRequest>) => {
    const res = (await request ({
        url: `${API_ADMIN_BILL}`,
        method: 'GET',
        params: params.value
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<BillResponse>>>
    >;

    return res.data;
}

export const getBillsWait = async () => {
    const res = (await request ({
        url: `${API_ADMIN_BILL}/bill-wait`,
        method: 'GET',
    })) as AxiosResponse<
        DefaultResponse<Array<BillWaitResponse>>
    >;

    return res.data;
}

export const getBillById = async (billId: string | null) => {
    return await request ({
        url: `${API_ADMIN_BILL}/${billId}`,
        method: 'GET'
    }) as AxiosResponse<
        DefaultResponse<BillResponse>
    >;
}

export const getBillRefundByMaHD = async (billCode: string) => {
    const res = (await request ({
        url: `${API_ADMIN_BILL_REFUND}/${billCode}`,
        method: 'GET',
    })) as AxiosResponse<
        DefaultResponse<BillRefundResponse>
    >;
    return res.data
}

export const createBillsWait = async (data: BillCreateRequest) => {
    const res = (await request({
        url: `${API_ADMIN_BILL}/create-bill`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const updateBill = async (idBill: string, params: BillRequest) => {
    return await request({
        url: `${API_ADMIN_BILL}/${idBill}`,
        method: "PUT",
        data: params
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const updateBillWait = async (idBill: string, params: BillWaitRequest) => {
    return await request({
        url: `${API_ADMIN_BILL}/bill-wait/${idBill}`,
        method: "PUT",
        data: params
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const removeBillWait = async (idBill: string | null) => {
    return await request({
        url: `${API_ADMIN_BILL}/${idBill}`,
        method: "DELETE"
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

export const getBillStatusCount  = async () => {
    return await request({
        url: `${API_ADMIN_COUNT_BILL}`,
        method: 'GET'
    }) as AxiosResponse<CountBillByStatusResponse>;
}

export const changeBillStatus = async (idBill: string, params: ChangeStatusBillRequest) => {
    return await request({
        url: `${API_ADMIN_BILL}/status-bill/${idBill}`,
        method: "PUT",
        data: params
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};

