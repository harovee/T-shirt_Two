
import { useMutation, useQueryClient } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey.ts";
import { clientPaymentRequest, createInvoiceOnline, getVnPayLink, vnPayLinkResponse, vnPayRequest, createInvoiceOnlineWithVnPay, createInvoiceOnlineWithMoMo, vietQrRequest, getVietQrCode, createInvoiceOnlineWithVietQr, createUrlVnPay, createUrlVnPayCallBack, createUrlMomo, momoRequest, createUrlMomoCallBack } from "../../api/client/clientpayment.api";

export const useCreateInvoiceOnline = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: clientPaymentRequest) => createInvoiceOnline(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.client.payment.invoiceOnline],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.client.payment.invoiceOnline, "🚀 ~ createInvoiceOnline ~ error:", error);
        },
    });
};

export const useCreateInvoiceOnlineWithVnPay = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: clientPaymentRequest) => createInvoiceOnlineWithVnPay(data),
        onSuccess: (response) => {
            console.log(response);
            queryClient.invalidateQueries({
                queryKey: [queryKey.client.payment.invoiceOnlineVNPay],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.client.payment.invoiceOnline, "🚀 ~ createInvoiceOnline ~ error:", error);
        },
    });
};

export const useCreateInvoiceOnlineWithMomo = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: clientPaymentRequest) => createInvoiceOnlineWithMoMo(data),
        onSuccess: (response) => {
            console.log(response);
            queryClient.invalidateQueries({
                queryKey: [queryKey.client.payment.invoiceOnlineMomo],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.client.payment.invoiceOnlineMomo, "🚀 ~ createInvoiceOnline ~ error:", error);
        },
    });
};

export const useGetVietQrCode = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: vietQrRequest) => getVietQrCode(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.client.payment.invoiceInlineVietQr],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.client.payment.invoiceInlineVietQr, "🚀 ~ getVietQrCode ~ error:", error);
        },
    });
};

export const useCreateInvoiceOnlineWithVietQR = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: clientPaymentRequest) => createInvoiceOnlineWithVietQr(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.client.payment.invoiceInlineVietQr],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.client.payment.invoiceInlineVietQr, "🚀 ~ createInvoiceOnlineWithVietQR ~ error:", error);
        },
    });
};

export const useCreateUrlVnPay = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (params: vnPayRequest) => createUrlVnPay(params),
        onSuccess: (response) => {
            console.log(response);
            queryClient.invalidateQueries({
                queryKey: [queryKey.client.payment.invoiceOnlineVNPay],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.client.payment.invoiceOnline, "🚀 ~ createInvoiceOnline ~ error:", error);
        },
    });
};
export const useCreateUrlVnPayCallBack = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: clientPaymentRequest) => createUrlVnPayCallBack(data),
        onSuccess: (response) => {
            console.log(response);
            queryClient.invalidateQueries({
                queryKey: [queryKey.client.payment.invoiceOnlineVNPay],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.client.payment.invoiceOnline, "🚀 ~ createInvoiceOnline ~ error:", error);
        },
    });
};

export const useCreateUrlMomo = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: momoRequest) => createUrlMomo(data),
        onSuccess: (response) => {
            console.log(response);
            queryClient.invalidateQueries({
                queryKey: [queryKey.client.payment.invoiceOnlineMomo],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.client.payment.invoiceOnlineMomo, "🚀 ~ createInvoiceOnline ~ error:", error);
        },
    });
};

export const useCreateUrlMomoCallBack = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: clientPaymentRequest) => createUrlMomoCallBack(data),
        onSuccess: (response) => {
            console.log(response);
            queryClient.invalidateQueries({
                queryKey: [queryKey.client.payment.invoiceOnlineMomo],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.client.payment.invoiceOnlineMomo, "🚀 ~ createInvoiceOnline ~ error:", error);
        },
    });
};
