import { Ref } from "vue";
import { BillRequest, FindBillRequest, getBillById, getBills, updateBill } from "../../api/admin/bill.api";
import { useMutation, useQuery, useQueryClient, UseQueryReturnType } from "@tanstack/vue-query";
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


export const useGetBillById = (
    billId: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getBillById>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.bill.billById, billId,],
        queryFn: () => getBillById(billId),
        ...options,
    });
};

export const useUpdateBill = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({billId, data,}: { billId: string; data: BillRequest; }) => updateBill(billId, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.bill.billList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.bill.billList + "🚀 ~ billUpdate ~ error:", error);
        },
    });
};