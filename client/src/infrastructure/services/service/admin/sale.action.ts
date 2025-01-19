import {
    FindSaleRequest,
    SaleRequest,
    getSales,
    getSaleById,
    createSale,
    changeStatusSale,
    updateSale,
    deleteSaleById,

    getAttributesOfProductDetail,

    FindProductRequest,
    getProductSaleModule,

    FindProductDetailRequest,
    getProductDetailSaleModule,

    SaleAndSaleProductRequest,
    saveSaleAndSaleProduct,
    deleteSaleProductById,
    updateSaleAndSaleProduct,

    FindSaleProductDetailRequest,
    getSaleProductDetails
} from "../../api/admin/sale.api";

import { useMutation, useQuery, useQueryClient, UseQueryReturnType } from "@tanstack/vue-query";
import { queryKey } from "@/infrastructure/constants/queryKey.ts";
import { Ref } from "vue";


export const useGetSales = (
    params: Ref<FindSaleRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getSales>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.sale.saleList, params],
        queryFn: () => getSales(params),
        ...options,
    });
};

export const useGetProducts = (
    params: Ref<FindProductRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductSaleModule>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.sale.productList, params],
        queryFn: () => getProductSaleModule(params),
        ...options,
    });
};

export const useGetProductDetails= (
    params: Ref<FindProductDetailRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductDetailSaleModule>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.sale.productDetailList, params],
        queryFn: () => getProductDetailSaleModule(params),
        ...options,
    });
};

export const useGetAttributes  = (
options?: any): UseQueryReturnType<Awaited<ReturnType<typeof getAttributesOfProductDetail>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.sale.attributeList,],
        queryFn: () => getAttributesOfProductDetail(),
        ...options,
    });
};

export const useCreateSale = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: SaleRequest) => createSale(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.sale.saleList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.sale.saleList, "🚀 ~ saleCreate ~ error:", error);
        },
    });
};


export const useCreateSaleAndSaleProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: SaleAndSaleProductRequest) => saveSaleAndSaleProduct(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.sale.saleList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.sale.saleList, "🚀 ~ saleCreate ~ error:", error);
        },
    });
};

export const useUpdateSaleAndSaleProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ saleId, data, }: { saleId: string; data: SaleAndSaleProductRequest; })=> updateSaleAndSaleProduct(data, saleId),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.sale.saleProductList],
            })
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.sale.productDetailList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.sale.saleProductList, "🚀 ~ saleProductCreate ~ error:", error);
        },
    });
};


export const useGetSaleById = (
    saleId: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getSaleById>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.client.clientDetail, saleId,],
        queryFn: () => getSaleById(saleId),
        ...options,
    });
};


export const useUpdateSale = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ saleId, data, }: { saleId: string; data: SaleRequest; }) => updateSale(saleId, data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [queryKey.admin.sale.saleList], });
            queryClient.invalidateQueries({ queryKey: [queryKey.admin.sale.saleProductList], });
        },
        onError: (error: any) => {
            console.log(queryKey.admin.sale.saleList + "🚀 ~ saleUpdate ~ error:", error);
        },
    });
};

export const useChangeStatusSale = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({ saleId, trangThai, }: { saleId: string; trangThai: string; }) => changeStatusSale(saleId, trangThai),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [queryKey.admin.sale.saleList], });
        },
        onError: (error: any) => {
            console.log(queryKey.admin.sale.saleList + "🚀 ~ change status sale ~ error:", error);
        },
    });
};


export const useDeleteSale = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (saleId: string) => deleteSaleById(saleId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [queryKey.admin.sale.saleList], });
        },
        onError: (error: any) => {
            console.log(queryKey.admin.sale.saleList + "🚀 ~ saleDelete ~ error:", error);
        },
    });
};



export const useGetSaleProductDetails = (
    params: Ref<FindSaleProductDetailRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getSaleProductDetails>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.sale.saleProductList, params],
        queryFn: () => getSaleProductDetails(params),
        ...options,
    });
};

export const useDeleteSaleProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (saleId: string) => deleteSaleProductById(saleId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: [queryKey.admin.sale.saleProductList], });
            queryClient.invalidateQueries({ queryKey: [queryKey.admin.sale.productDetailList] });
        },
        onError: (error: any) => {
            console.log(queryKey.admin.sale.saleProductList + "🚀 ~ saleProductDelete ~ error:", error);
        },
    });
};