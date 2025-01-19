import {
    createTrademark,
    getListTrademark,
    TrademarkRequest,
    getTrademark,
    FindTrademarkRequest,
    getTrademarks,
    updateTrademark
} from "@/infrastructure/services/api/admin/trademark.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetTrademarks = (
    params: Ref<FindTrademarkRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getTrademarks>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.trademark.trademarkList, params],
        queryFn: () => getTrademarks(params),
        ...options,
    });
};

export const useGetListTrademark = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListTrademark>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.trademark.trademarkList],
        queryFn: () => getListTrademark(),
        ...options,
    });
};

export const useCreateTrademark = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: TrademarkRequest) => createTrademark(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.trademark.trademarkList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.trademark.trademarkList, "🚀 ~ TrademarkCreate ~ error:", error);
        },
    });
};


export const useGetTrademark = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getTrademark>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.trademark.trademarkList, id,],
        queryFn: () => getTrademark(id.value),
        ...options,
    });
};


export const useUpdateTrademark = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, data,}: { id: string; data: TrademarkRequest; }) => updateTrademark(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.trademark.trademarkList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.trademark.trademarkList + "🚀 ~ TrademarkUpdate ~ error:", error);
        },
    });
};