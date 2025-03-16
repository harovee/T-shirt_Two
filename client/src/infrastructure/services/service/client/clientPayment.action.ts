
import { useMutation, useQueryClient } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey.ts";
import { clientPaymentRequest, createInvoiceOnline, getVnPayLink, vnPayLinkResponse, vnPayRequest, createInvoiceOnlineWithVnPay } from "../../api/client/clientpayment.api";

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
