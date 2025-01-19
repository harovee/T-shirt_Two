import { FindBillHistoryRequest, getBillHistory } from './../../api/admin/billhistory.api';
import { Ref } from "vue";
import { useQuery, UseQueryReturnType } from "@tanstack/vue-query";
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
