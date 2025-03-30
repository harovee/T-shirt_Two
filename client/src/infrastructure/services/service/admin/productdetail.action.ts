import {
    ProductDetailRequest,
    createProductDetail,
    FindProductDetailRequest,
    getProductDetails,
    ProductDetailUpdateRequest,
    updateProductDetail,
    getProductDetail,
    getAllProductDetails,
    FindAllProductDetailRequest,
    getListProductDetail
} from "@/infrastructure/services/api/admin/product_detail.api";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetProductDetail = (
    params: Ref<FindProductDetailRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductDetails>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.productDetail.productDetailList, params],
        queryFn: () => getProductDetails(params),
        ...options,
    });
};

export const useGetListProductDetail = (
): UseQueryReturnType<Awaited<ReturnType<typeof getListProductDetail>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.productDetail.productDetailListNoPage],
        queryFn: () => getListProductDetail()
    });
};

export const useGetAllProductDetail = (
    paramsAll: Ref<FindAllProductDetailRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getAllProductDetails>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.productDetail.allProductDetails, paramsAll],
        queryFn: () => getAllProductDetails(paramsAll),
        ...options,
    });
};

export const useGetAllProductDetailOverZero = (
    paramsAll: Ref<FindAllProductDetailRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getAllProductDetails>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.productDetail.allProductDetailOverZero, paramsAll],
        queryFn: () => getAllProductDetails(paramsAll),
        ...options,
    });
};

export const useCreateProductDetail = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: ProductDetailRequest) => createProductDetail(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.productDetail.productDetailList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.productDetail.productDetailList, "🚀 ~ productCreate ~ error:", error);
        },
    });
};

export const useGetProductDetailById = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductDetail>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.productDetail.productDetailList, id,],
        queryFn: () => getProductDetail(id.value),
        ...options,
    });
};

export const useUpdateProductDetail = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, params,}: { id: string; params: ProductDetailUpdateRequest; }) => updateProductDetail(id, params),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.productDetail.productDetailList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.productDetail.productDetailList + "🚀 ~ productDetailUpdate ~ error:", error);
        },
    });
};