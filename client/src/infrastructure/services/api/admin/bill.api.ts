import { AxiosResponse } from 'axios';
import { API_ADMIN_BILL } from '@/infrastructure/constants/url';
import { DefaultResponse, PaginationParams, PaginationResponse, ResponseList } from '@/infrastructure/types/api.common';
import { Ref } from 'vue';
import request from "@/infrastructure/services/request.ts";

export interface BillPropsParams {
    keyword?: string | null;

    [key: string]: any;
}

export interface FindBillRequest extends BillPropsParams, PaginationParams {

}

export interface BillRequest {
    
}

export type BillResponse = ResponseList & {
    catalog: number | null;
    ma: string | null;
    loaiHD: string | null;
    tongTien: number | null;
    trangThai: string | null;
    tenKhachHang: string | null;

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

export const getBillById = async (billId: Ref<string | null>) => {
    return await request ({
        url: `${API_ADMIN_BILL}/${billId}`,
        method: 'GET'
    }) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<BillResponse>>>
    >;
}