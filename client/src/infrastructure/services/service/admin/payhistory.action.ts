import { Ref } from "vue";
import { FindPayHistoryRequest, getPayHistory, getPaymentMethod} from "../../api/admin/pay-history.api";
import { useQuery, UseQueryReturnType } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey";

export const useGetPayHistory = (
    params: Ref<FindPayHistoryRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getPayHistory>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.payHistory, params],
        queryFn: () => getPayHistory(params),
        ...options,
    });
};

export const useGetPaymentMethod = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getPaymentMethod>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.paymentMethod],
        queryFn: () => getPaymentMethod(),
        ...options,
    });
};