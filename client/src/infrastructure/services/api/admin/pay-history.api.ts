import { API_ADMIN_PAY_HISTORY } from '@/infrastructure/constants/url';
import request from '@/infrastructure/services/request.ts';
import { DefaultResponse, ResponseList } from "@/infrastructure/types/api.common";
import { AxiosResponse } from 'axios';
import { Ref } from "vue";

export interface PayHistoryParams {
    idHoaDon?: string | null;
}

export interface FindPayHistoryRequest extends PayHistoryParams {

}

export type PayHistoryResponse = ResponseList & {
    maGiaoDich: string | null;
    tenPhuongThuc: string | null;
    ghiChu  : string | null;
    tongTienHD: number | null;
    ngayTao: number | null;
    nguoiTao: string | null;
}

export const getPayHistory = async (params: Ref<FindPayHistoryRequest>) => {
    const res = (await request ({
        url: `${API_ADMIN_PAY_HISTORY}`,
        method: 'GET',
        params: params.value
    })) as AxiosResponse<
        DefaultResponse<Array<PayHistoryResponse>>
    >;
    return res.data
}