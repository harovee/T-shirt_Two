import { API_ADMIN_BILL_HISTORY } from "@/infrastructure/constants/url";
import { DefaultResponse, PaginationParams, PaginationResponse, ResponseList } from "@/infrastructure/types/api.common";
import { AxiosResponse } from "axios";
import request from "@/infrastructure/services/request.ts";
import { Ref } from "vue";

export interface BillHistoryParams {
    idHoaDon?: string | null;
}

export interface FindBillHistoryRequest extends BillHistoryParams, PaginationParams {

}

export interface createUpdateBillHistoryRequest {
    idHoaDon: string | null,
    hanhDong: string | null,
    moTa: string | null,
    trangThai: string | null,
    nguoiTao: string | null
}

export type BillHistoryResponse = ResponseList & {
    maHoaDon: string | null,
    hanhDong: string | null;
    trangThai: string | null;
    nguoiTao: string | null;
    nguoiSua: string | null;
    moTa: string | null;
    ngayTao: number | null;
    ngaySua: number | null;
}

export const getBillHistory = async (params: Ref<FindBillHistoryRequest>) => {
    const res = (await request ({
        url: `${API_ADMIN_BILL_HISTORY}`,
        method: 'GET',
        params: params.value
    })) as AxiosResponse<
        DefaultResponse<Array<PaginationResponse<BillHistoryResponse>>>
    >;
    return res.data
}

export const createBillHistory = async (data: createUpdateBillHistoryRequest) => {
    const res = (await request({
      url: `${API_ADMIN_BILL_HISTORY}`,
      method: "POST",
      data: data,
    })) as AxiosResponse<DefaultResponse<DefaultResponse<null>>>;
  
    return res.data;
  };