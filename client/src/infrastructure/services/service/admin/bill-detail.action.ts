import { Ref } from "vue";
import { createBillDetail, CreateBillDetailRequest, FindBillDetailRequest, getBillDetailsByIdHoaDon, getBillDetailsByMaHoaDon, updateBillDetail, UpdateBillDetailRequest } from "../../api/admin/bill-detail.api";
import { useMutation, useQuery, useQueryClient, UseQueryReturnType } from "@tanstack/vue-query";
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

export const useGetBillDetailRefundByMaHD = (
    billCode: string, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getBillDetailsByMaHoaDon>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.billdetail.billDetailByMaHD, billCode],
        queryFn: () => getBillDetailsByMaHoaDon(billCode),
        ...options,
    });
};

export const useUpdateBillDetail = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({idBillDetail, data}: { idBillDetail: string; data: UpdateBillDetailRequest }) => updateBillDetail(idBillDetail, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.billdetail.billDetailById]});
            queryClient.invalidateQueries({queryKey: [queryKey.admin.billdetail.detailList]});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.billdetail.billDetailById + "🚀 ~ billDetailUpdate ~ error:", error);
        },
    });
};

export const useCreateBillDetail = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: CreateBillDetailRequest) => createBillDetail(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.billdetail.detailList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.billdetail.detailList, "🚀 ~ billDetailCreat ~ error:", error);
        },
    });
};