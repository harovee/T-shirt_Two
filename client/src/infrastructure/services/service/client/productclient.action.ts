import {
    getAllProducts,
    FindProductClientRequest,
    getProductDetailById,
    getTop8ProductsMoiNhat,
    ClientProductDetailRequest,
    getProductById,
    ClientProductRequest,
    getDanhMuc,
    getChatLieu,
    getThuongHieu,
    getKieuDang,
    getColor,
    getProductsBestSale,
    getSaleProducts,
    getProductDetailByIdWithColor,
    getProductDetailByIdWithSize
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
    params: Ref<ClientProductRequest>,
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductById>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.productDetailList, sanPhamId,params],
        queryFn: () => getProductById(sanPhamId,params),
        ...options,
    });
};


export const useGetDanhMuc = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getDanhMuc>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.danhMucList, ],
        queryFn: () => getDanhMuc(),
        ...options,
    });
};

export const useGetChatLieu = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getChatLieu>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.chatLieuList, ],
        queryFn: () => getChatLieu(),
        ...options,
    });
};

export const useGetThuongHieu = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getThuongHieu>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.thuongHieuList, ],
        queryFn: () => getThuongHieu(),
        ...options,
    });
};

export const useGetKieuDang = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getKieuDang>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.kieuDangList, ],
        queryFn: () => getKieuDang(),
        ...options,
    });
};

export const useGetColor = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getColor>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.colorList, ],
        queryFn: () => getColor(),
        ...options,
    });
};

export const useGetProductBestSale = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductsBestSale>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.productBestSaleList],
        queryFn: () => getProductsBestSale(),
        ...options,
    });
};

export const useGetSaleProduct = (
    params: Ref<FindProductClientRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getSaleProducts>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.productSaleList, params],
        queryFn: () => getSaleProducts(params),
        ...options,
    });
};

export const useGetProductDetailByIdWithColor = (
    sanPhamId: Ref<string | null>,
    params: Ref<ClientProductDetailRequest>,
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductDetailByIdWithColor>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.productDetailWithColor, sanPhamId,params],
        queryFn: () => getProductDetailByIdWithColor(sanPhamId,params),
        ...options,
    });
};

export const useGetProductDetailByIdWithSize = (
    sanPhamId: Ref<string | null>,
    params: Ref<ClientProductDetailRequest>,
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductDetailByIdWithSize>>, Error> => {
    return useQuery({
        queryKey: [queryKey.client.product.productDetailWithSize, sanPhamId,params],
        queryFn: () => getProductDetailByIdWithSize(sanPhamId,params),
        ...options,
    });
};