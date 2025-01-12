import {
    deleteVoucher,
    createVoucher,
    FindVoucherRequest,
    getVoucherById,
    getListVoucher,
    VoucherRequest,
    updateVoucher
} from "@/infrastructure/services/api/admin/voucher/voucher.api";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetListVoucher = (
    params: Ref<FindVoucherRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListVoucher>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.voucher.voucherList, params],
        queryFn: () => getListVoucher(params),
        ...options,
    });
};

export const useCreateVoucher = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: VoucherRequest) => createVoucher(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.voucher.voucherList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.voucher.voucherList, "🚀 ~ voucherCreate ~ error:", error);
        },
    });
};


export const useGetVoucherById = (
    voucherId: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getVoucherById>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.voucher.voucherDetail, voucherId,],
        queryFn: () => getVoucherById(voucherId),
        ...options,
    });
};


export const useUpdateVoucher = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({voucherId, data,}: { employeeId: string; data: VoucherRequest; }) => updateVoucher(voucherId, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.voucher.voucherList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.voucher.voucherList + "🚀 ~ voucherUpdate ~ error:", error);
        },
    });
};

export const useDeleteVoucher = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (voucherId: string) => deleteVoucher(voucherId),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.voucher.voucherList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.voucher.voucherList + "🚀 ~ voucherDelete ~ error:", error);
        },
    });
};