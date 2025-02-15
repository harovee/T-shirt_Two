import { queryKey } from '@/infrastructure/constants/queryKey';
import { createDeliveryPayment, CreateDeliveryPaymentRequest } from '../../api/admin/deliverypayment.api';
import { useMutation, useQueryClient } from "@tanstack/vue-query"

export const useCreateDeliveryPayment = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: CreateDeliveryPaymentRequest) => createDeliveryPayment(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.payment.deliveryPayment],
            });
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.payment.payHistory],
            });
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.bill.billById],
            });
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.bill.billHistory],
            });
        },
        onError: (error: any) => {
            console.log(queryKey.admin.payment.deliveryPayment + "🚀 ~ createPayment ~ error:", error);  
        }
    })
}