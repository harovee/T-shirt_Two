import { useMutation, useQuery, useQueryClient, UseQueryReturnType } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey.ts";
import { Ref } from "vue";
import { createOrderDetails, deleteCartById, FindPOSProductDetailRequest, getOrderDetails, getProductDetailsInPOS, POSAddProductsToCartRequest, POSUpdateCartRequest, updateQuantityOrderDetails } from "../../api/admin/point-of-sale.api";


export const useGetProductDetailsInPOS= (
    params: Ref<FindPOSProductDetailRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductDetailsInPOS>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.pointOfSale.products, params],
        queryFn: () => getProductDetailsInPOS(params),
        ...options,
    });
};


export const useGetOrderDetails= (
    idOrder: string, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getOrderDetails>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.pointOfSale.orderDetails, idOrder,],
        queryFn: () => getOrderDetails(idOrder),
        ...options,
    });
};

export const useCreateOrderDetails = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: POSAddProductsToCartRequest) => createOrderDetails(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.pointOfSale.orderDetails],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.pointOfSale.orderDetails, "🚀 ~ order detail ~ error:", error);
        },
    });
};

export const useDeleteCartById = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (saleId: string) => deleteCartById(saleId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [queryKey.admin.pointOfSale.orderDetails], });
        },
        onError: (error: any) => {
            console.log(queryKey.admin.pointOfSale.orderDetails + "🚀 ~ order detail ~ error:", error);
        },
    });
};

export const useUpdateQuantityOrderDetails =  () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: POSUpdateCartRequest) => updateQuantityOrderDetails(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [queryKey.admin.pointOfSale.orderDetails], });
        },
        onError: (error: any) => {
            console.log(queryKey.admin.pointOfSale.orderDetails + "🚀 ~ orderdetail Update ~ error:", error);
        },
    });
};


