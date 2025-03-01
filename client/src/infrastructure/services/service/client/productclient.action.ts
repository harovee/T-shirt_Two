import {
    getAllProducts,
    FindProductClientRequest,
    getProductDetailById,
    getTop8ProductsMoiNhat,
    ClientProductDetailRequest,
    getProductById
} from "@/infrastructure/services/api/client/clientproduct.api";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetAllProducts = (
    params: Ref<FindProductClientRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getAllProducts>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.productList, params],
        queryFn: () => getAllProducts(params),
        ...options,
    });
};

export const useGetTop8ProductsMoiNhat = (
    params: Ref<FindProductClientRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getTop8ProductsMoiNhat>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.productList, params],
        queryFn: () => getTop8ProductsMoiNhat(params),
        ...options,
    });
};

export const useGetProductDetailById = (
    sanPhamId: Ref<string | null>,
    params: Ref<ClientProductDetailRequest>,
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductDetailById>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.productDetailList, sanPhamId,params],
        queryFn: () => getProductDetailById(sanPhamId,params),
        ...options,
    });
};

export const useGetProductById = (
    sanPhamId: Ref<string | null>,
    params: Ref<ClientProductDetailRequest>,
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductById>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.productDetailList, sanPhamId,params],
        queryFn: () => getProductById(sanPhamId,params),
        ...options,
    });
};
