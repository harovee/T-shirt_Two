import {
    getCustomerById,
   getListCustomer,
    getListVoucher,
    getVoucherById,
    FindCustomerRequest,
    FindVoucherRequest,
    calculateShippingFee,
    ShippingFeeRequest
} from "@/infrastructure/services/api/admin/payment.api";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetListVoucher = (
    params: Ref<FindVoucherRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListVoucher>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.voucherPayList, params],
        queryFn: () => getListVoucher(params),
        ...options,
    });
};

export const useGetListCustomer = (
    params: Ref<FindCustomerRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListCustomer>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.customerList, params],
        queryFn: () => getListCustomer(params),
        ...options,
    });
};


export const useGetVoucherById = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getVoucherById>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.voucherPayDetail,id,],
        queryFn: () => getVoucherById(id),
        ...options,
    });
};


export const useGetCustomerById = (
    khachHangId: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getCustomerById>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.customerDetail,khachHangId,],
        queryFn: () => getCustomerById(khachHangId),
        ...options,
    });
};

export const useGetShippingFee = (
    params: Ref<ShippingFeeRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof calculateShippingFee>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.shippingFee, params],
        queryFn: () => calculateShippingFee(params),
        ...options,
    });
};

