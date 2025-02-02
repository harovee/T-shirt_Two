import { Ref } from "vue";
import { FindBillDetailRequest, getBillDetailsByIdHoaDon } from "../../api/admin/bill-detail.api";
import { useMutation, useQuery, useQueryClient, UseQueryReturnType } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey";
import { BillRequest, updateBill } from "../../api/admin/bill.api";


export const useGetBillDetails = (
    params: Ref<FindBillDetailRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getBillDetailsByIdHoaDon>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.billdetail.detailList, params],
        queryFn: () => getBillDetailsByIdHoaDon(params),
        ...options,
    });
}

export const useUpdateBillDetails = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({idBill, params}: { idBill: string; params: BillRequest; }) => updateBill(idBill, params),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.bill.billById]});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.bill.billList + "🚀 ~ billUpdate ~ error:", error);
        },
    });
};