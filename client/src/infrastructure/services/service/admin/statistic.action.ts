import {FindOutStockProductRequest, getOutStockProducts, getRevenuePage, getStatisticData, RevenueRequest} from "../../api/admin/statistic.api";

import { useMutation, useQuery, useQueryClient, UseQueryReturnType } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey.ts";
import { Ref } from "vue";


export const useGetOutStockProducts = (
    params: Ref<FindOutStockProductRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getOutStockProducts>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.statistic.outStockProductList, params],
        queryFn: () => getOutStockProducts(params),
        ...options,
    });
};


export const useGetStatisticData = (
    params: Ref<RevenueRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getStatisticData>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.statistic.detailData, params],
        queryFn: () => getStatisticData(params),
        ...options,
    });
};


export const useGetRevenuePage = (
    params: Ref<RevenueRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getRevenuePage>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.statistic.commonRevenueList, params],
        queryFn: () => getRevenuePage(params),
        ...options,
    });
};
