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

export interface BillDetailRequest {

}

export type BillDetailResponse = ResponseList & {
    catalog: number | null;
    id: string | null;
    maHoaDon: string | null;
    tenSanPhamChiTiet: string | null;
    soLuong: number | null;
    gia: number | null;
    thanhTien: number | null;
    tienGiamHD: number | null;
    tienShip: number | null;
    tongTienHD: number | null;
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