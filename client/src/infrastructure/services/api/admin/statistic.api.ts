import { DefaultResponse, PaginationParams, PaginationResponse, ResponseList } from "@/infrastructure/types/api.common";
import { Ref } from "vue";
import request from "@/infrastructure/services/request.ts";
import { PREFIX_API_ADMIN_STATISTIC } from "@/infrastructure/constants/url.ts";
import { AxiosResponse } from "axios";


export interface PropertyOutStockParams {
    key?: string | null;
    [key: string]: any;
}
export interface FindOutStockProductRequest extends PropertyOutStockParams, PaginationParams {

}
export type OutStockProductResponse = ResponseList & {
    maSanPham: string | null;
    tenSanPham: string | null;
    tenDanhMuc: string | null;
    soLuong: number | null;
    [key: string]: any;
};
export const getOutStockProducts = async (params: Ref<FindOutStockProductRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_STATISTIC}/out-stock-products`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<OutStockProductResponse>>>
    >;

    return res.data;
};



export interface PropertyRevenueParams {
    statisticType: string | null;
    timeUnit: string | null;
    startTime: number | null;
    endTime: number | null;
    [key: string]: any;
}
export interface RevenueRequest extends PropertyRevenueParams, PaginationParams {

}
export type RevenueResponse = {
    timeDisplay?: string | null;
    objectValue?: string | null;
    totalRevenue: number | null;
    numberOrder: number | null;
    numberProductSold: number | null;
    [key: string]: any;
}
export const getRevenuePage = async (params: Ref<RevenueRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_STATISTIC}/revenue-page`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<PaginationResponse<Array<RevenueResponse>>>
    >;

    return res.data;
};



export type NumberOrderByStatusResponse = {
    numberSuccessOrder: number;
    numberPaymentOrder: number;
    numberDeliveryOrder: number;
    numberShippingOrder: number;
    numberDeliveryWaitingOrder: number;
    numberCancelOrder: number;
    numberConfirmWaitingOrder: number;
    numberWaitingOrder: number;
    [key: string]: any;
}


export type StatisticDataResponse = {
    revenuesType: DefaultResponse<Array<RevenueResponse>> | null;
    revenues: DefaultResponse<Array<RevenueResponse>> | null;
    numberOrderByStatus: DefaultResponse<NumberOrderByStatusResponse> | null;
    numberNewCustomers: number | null;
    top10ProductBestSaleByRangeTime: DefaultResponse<Array<RevenueResponse>> | null;
    [key: string]: any;
}
export const getStatisticData = async (params: Ref<RevenueRequest>) => {
    const res = (await request({
        url: `${PREFIX_API_ADMIN_STATISTIC}/statistic-data`,
        method: "GET",
        params: params.value,
    })) as AxiosResponse<
        DefaultResponse<StatisticDataResponse>
    >;

    return res.data;
};

