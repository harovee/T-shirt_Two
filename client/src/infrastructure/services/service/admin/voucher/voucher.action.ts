import {
    deleteVoucher,
    createVoucher,
    FindVoucherRequest,
    getVoucherById,
    getListVoucher,
    PhieuGiamGiaRequest,
    updateVoucher,
    FindKhachHangRequest,
    getListKhachHang,
    VoucherAndCustomerVoucherRequest,
    createCustomerVoucher,
    getKhachHangInPhieuGiamGia,
    updateCustomerVoucher,
    changeStatusVoucher
} from "@/infrastructure/services/api/admin/voucher/voucher.api";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";
import { log } from "console";


export const useGetListVoucher = (
    params: Ref<FindVoucherRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListVoucher>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.voucher.voucherList, params],
        queryFn: () => getListVoucher(params),
        ...options,
    });
};

export const useGetListKhachHang = (
    params: Ref<FindKhachHangRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListKhachHang>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.voucher.khachHangList, params],
        queryFn: () => getListKhachHang(params),
        ...options,
    });
};
export const useCreateVoucher = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: PhieuGiamGiaRequest) => createVoucher(data),
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
        queryKey: [queryKey.admin.voucher.voucherDetail,voucherId,],
        queryFn: () => getVoucherById(voucherId),
        ...options,
    });
};


export const useUpdateVoucher = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({voucherId, data,}: { voucherId: string; data: PhieuGiamGiaRequest; }) => updateVoucher(voucherId, data),
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

export const useCreateCustomerVoucher = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: VoucherAndCustomerVoucherRequest) => createCustomerVoucher(data),
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

export const useGetCusTomerByIdPhieuGiamGia = (
    voucherId: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getKhachHangInPhieuGiamGia>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.voucher.khachHangList,voucherId,],
        queryFn: () => getKhachHangInPhieuGiamGia(voucherId),
        ...options,
    });
};

export const useUpdateCustomerVoucher = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({voucherId, data,}: { voucherId: string; data: VoucherAndCustomerVoucherRequest; }) => updateCustomerVoucher(voucherId, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.voucher.voucherList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.voucher.voucherList + "🚀 ~ voucherUpdate ~ error:", error);
        },
    });
};
export const useChangeStatusVoucher = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ voucherId, trangThai, }: { voucherId: string; trangThai: string; }) => changeStatusVoucher(voucherId, trangThai),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [queryKey.admin.voucher.voucherList], });
        },
        onError: (error: any) => {
            console.log(queryKey.admin.voucher.voucherList + "🚀 ~ change status voucher ~ error:", error);
        },
    });
};