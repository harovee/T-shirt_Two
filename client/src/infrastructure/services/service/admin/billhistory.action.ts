import { FindBillHistoryRequest, getBillHistory, createUpdateBillHistoryRequest, createBillHistory } from './../../api/admin/billhistory.api';
import { Ref } from "vue";
import { useMutation, useQuery, useQueryClient, UseQueryReturnType } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey";

export const useGetBillHistory = (
    params: Ref<FindBillHistoryRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getBillHistory>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.bill.billHistory, params],
        queryFn: () => getBillHistory(params),
        ...options,
    });
};

export const useCreateBillHistory = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: createUpdateBillHistoryRequest) => createBillHistory(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.bill.billHistory],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.bill.billHistory, "🚀 ~ BillHistoryWaitCreate ~ error:", error);
        },
    })
};
