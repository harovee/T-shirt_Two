import { Ref } from "vue";
import { FindBillDetailRequest, getBillDetailsByIdHoaDon } from "../../api/admin/bill-detail.api";
import { useQuery, UseQueryReturnType } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey";


export const useGetBillDetails = (
    params: Ref<FindBillDetailRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getBillDetailsByIdHoaDon>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.billdetail.detailList, params],
        queryFn: () => getBillDetailsByIdHoaDon(params),
        ...options,
    });
}