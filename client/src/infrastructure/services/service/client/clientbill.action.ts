import { Ref } from "vue";
import { ClientBillParams, ClientBillResponse, getClientBills } from "../../api/client/clientmyorder.api";
import { useMutation, useQuery, useQueryClient, UseQueryReturnType } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey";

export const useGetClientBills = (
    params: Ref<ClientBillParams>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getClientBills>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.myOrder.clientBill, params],
        queryFn: () => getClientBills(params),
        ...options,
    });
};