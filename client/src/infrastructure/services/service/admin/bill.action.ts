import { Ref } from "vue";
import { FindBillRequest, getBillById, getBills } from "../../api/admin/bill.api";
import { useQuery, UseQueryReturnType } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey";

export const useGetBills = (
    params: Ref<FindBillRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getBills>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.bill.billList, params],
        queryFn: () => getBills(params),
        ...options,
    });
};
