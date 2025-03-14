import { AxiosResponse } from 'axios';
import { API_CLIENT_MY_ORDER } from '@/infrastructure/constants/url';
import { DefaultResponse, PaginationParams, PaginationResponse, ResponseList } from '@/infrastructure/types/api.common';
import { Ref } from 'vue';
import request from "@/infrastructure/services/request.ts";

export interface ClientBillParams extends PaginationParams{
    keyword?: string | null;
    trangThai? :string | null;
    idKhachHang?: string | null
}

export type ClientBillResponse = ResponseList & {
    phiShip: number | null,
    daTra: number | null,
    loaiDon: string | null,
    tienPhaiTra: number | null,
    id: string | null,
    trangThai: string | null,
    tienGiam: number | null,
    tongTien: number | null,
    ma: string | null,
    giaTriGiam: number | null,
    catalog: number | null,
    phuongThucNhan: string | null
}

export const getClientBills = async (params: Ref<ClientBillParams>) => {
    const res = (await request ({
        url: `${API_CLIENT_MY_ORDER}`,
        method: 'GET',
        params: params.value
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<ClientBillResponse>>>
    >;

    return res.data;
}