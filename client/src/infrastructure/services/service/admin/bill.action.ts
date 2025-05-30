import { Ref } from "vue";
import { BillRequest, FindBillRequest, BillConfirmRequest, updateBillConfirm, getBillById, getBills, updateBill, getBillsWait, BillCreateRequest, createBillsWait,BillWaitRequest, BillWaitResponse,removeBillWait, updateBillWait, ChangeStatusBillRequest, changeBillStatus, getBillRefundByMaHD } from "../../api/admin/bill.api";
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

export const useGetBillsWait = (): UseQueryReturnType<Awaited<ReturnType<typeof getBillsWait>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.bill.billsWait],
        queryFn: () => getBillsWait()
    });
};

export const useGetBillById = (
    billId: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getBillById>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.bill.billById, billId,],
        queryFn: () => getBillById(billId.value),
        ...options,
    });
};

export const useGetBillRefundByMaHD = (
    billCode: string, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getBillRefundByMaHD>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.bill.billRefund, billCode,],
        queryFn: () => getBillRefundByMaHD(billCode),
        ...options,
    });
};

// export const useRemoveBillById = (
//     billId: Ref<string | null>
// ): UseQueryReturnType<Awaited<ReturnType<typeof removeBillWait>>, Error> => {
//     return useQuery({
//         queryKey: [queryKey.admin.bill.billsWait, billId],
//         queryFn: () => removeBillWait(billId.value)
//     });
// };

export const useRemoveBillById = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (billId: string) => removeBillWait(billId),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.bill.billsWait],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.bill.billsWait, "🚀 ~ BillWaitRemove ~ error:", error);
        },
    });
};

export const useCreateBillsWait = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: BillCreateRequest) => createBillsWait(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.bill.billsWait],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.bill.billsWait, "🚀 ~ BillWaitCreate ~ error:", error);
        },
    });
};

export const useUpdateBill = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({idBill, params}: { idBill: string; params: BillRequest; }) => updateBill(idBill, params),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.bill.billById],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.bill.billList + "🚀 ~ billUpdate ~ error:", error);
        },
    });
};

export const useUpdateBillConfirm = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({idBill, params}: { idBill: string; params: BillConfirmRequest; }) => updateBillConfirm(idBill, params),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.bill.billById],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.bill.billList + "🚀 ~ billUpdate ~ error:", error);
        },
    });
};

export const useChangeBillStatus= () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({idBill, params}: { idBill: string; params: ChangeStatusBillRequest }) => changeBillStatus(idBill, params),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.bill.billHistory],});
            queryClient.invalidateQueries({queryKey: [queryKey.admin.bill.billById],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.bill.billList + "🚀 ~ billUpdate ~ error:", error);

        }
    })
};

export const useUpdateBillWait = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({idBill, params}: { idBill: string; params: BillWaitRequest; }) => updateBillWait(idBill, params),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.bill.billsWait],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.bill.billsWait + "🚀 ~ billUpdate ~ error:", error);
        },
    });
};