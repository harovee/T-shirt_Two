import { Ref } from 'vue';
import { DefaultResponse, PaginationParams, PaginationResponse, ResponseList } from "@/infrastructure/types/api.common";
import request from "@/infrastructure/services/request.ts";
import { API_ADMIN_BILL_DETAIL } from '@/infrastructure/constants/url';
import {  AxiosResponse } from 'axios';

export interface BillDetailPropsParams {
    idHoaDon?: string | null;
    keyword?: string | null;

    [key: string]: any; 
}

export interface FindBillDetailRequest extends BillDetailPropsParams, PaginationParams {

}

export interface UpdateBillDetailRequest {
    soLuong: number | null;
}

export interface CreateBillDetailRequest {
    idHoaDon: string | null,
    idSanPhamChiTiet: string | null,
    soLuong: number | null,
}

export type BillDetailResponse = ResponseList & {
    catalog: number | null;
    id: string | null;
    maHoaDon: string | null;
    tenSanPhamChiTiet: string | null;
    anhSanPhamChiTiet: string | null;
    soLuong: number | 0;
    gia: number | null;
    thanhTien: number;
    tienGiamHD: number | 0;
    tienShip: number | 0;
    tongTienHD: number | null;
    loaiGiam: boolean | null;
    giamToiDa: string | null;
    giaTriGiam: number;
    dieuKienGiam: string | null;
    tenPhieuGiam: string | null;
}

export const getBillDetailsByIdHoaDon = async (params: Ref<FindBillDetailRequest>) => {
    const res = (await request ({
        url: `${API_ADMIN_BILL_DETAIL}`,
        method: 'GET',
        params: params.value
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<BillDetailResponse>>>
    >;

    return res.data;
}

export const createBillDetail = async (data: CreateBillDetailRequest) => {
    const res = (await request({
        url: `${API_ADMIN_BILL_DETAIL}`,
        method: "POST",
        data: data
    })) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;

    return res.data;
};

export const updateBillDetail = async (idBillDetail: string, data: UpdateBillDetailRequest) => {
    return await request({
        url: `${API_ADMIN_BILL_DETAIL}/${idBillDetail}`,
        method: "PUT",
        data: data
    }) as AxiosResponse<
        DefaultResponse<DefaultResponse<null>>
    >;
};