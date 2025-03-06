import {
    getCustomerById,
   getListCustomer,
    getListVoucher,
    getVoucherById,
    FindCustomerRequest,
    FindVoucherRequest,
    getListCustomerAddress,
    FindCustomerAddressRequest,
    getPriceNextVoucher,
    nextVoucherRequest,
    ShippingFeeRequest,
    calculateShippingFee,
    getListPaymentMethodDetail,
    paymentMethodDetailRequest,
    createPaymentMethodDetail,
    getCustomerByPhoneNumber, 
    ServiceIdRequest,
    getServiceId, voucherRequest,
    getVoucherByCode
} from "@/infrastructure/services/api/admin/payment.api";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";
import { log } from "console";


export const useGetListVoucher = (
    params: Ref<FindVoucherRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListVoucher>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.voucherPayList, params],
        queryFn: () => getListVoucher(params),
        ...options,
    });
};

export const useGetVoucherByCode = (
    params: Ref<voucherRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getVoucherByCode>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.voucherByCode, params],
        queryFn: () => getVoucherByCode(params),
        ...options,
    });
};

export const useGetListPaymentMethodDetail = (
    params: Ref<paymentMethodDetailRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListPaymentMethodDetail>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.paymentMethodDetail, params],
        queryFn: () => getListPaymentMethodDetail(params),
        ...options,
    });
};

export const useCreatePaymentMethodDetail = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: paymentMethodDetailRequest) => createPaymentMethodDetail(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.payment.paymentMethodDetail],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.payment.paymentMethodDetail, "🚀 ~ paymentMethodDetailCreate ~ error:", error);
        },
    });
};

export const useGetPriceNextVoucher = (
    params: Ref<nextVoucherRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getPriceNextVoucher>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.nextPriceVoucher, params],
        queryFn: () => getPriceNextVoucher(params),
        ...options,
    });
};


export const useGetListCustomerAddress = (
    params: Ref<FindCustomerAddressRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListCustomerAddress>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.customerAddress, params],
        queryFn: () => getListCustomerAddress(params),
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

export const useGetCustomerByPhoneNumber = (
    phoneNumber: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getCustomerByPhoneNumber>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.customerDetail,phoneNumber,],
        queryFn: () => getCustomerByPhoneNumber(phoneNumber),
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

export const useGetServiceId = (
    params: Ref<ServiceIdRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getServiceId>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.payment.serviceId, params],
        queryFn: () => getServiceId(params),
        ...options,
    });
};

