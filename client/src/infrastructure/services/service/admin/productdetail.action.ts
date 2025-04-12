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
    getListProductDetail,
    checkQuantityRequest,
    checkQuantityInStock,
    checkQuantityInStockByIdProductDetail,
    checkQuantityListProduct,
    deleteQuantityListProduct
} from "@/infrastructure/services/api/admin/product_detail.api";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetProductDetail = (
    params: Ref<FindProductDetailRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProductDetails>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.productDetail.checkQuantity, params],
        queryFn: () => getProductDetails(params),
        ...options,
    });
};

export const useCheckQuantityInStock = (
    params: Ref<checkQuantityRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof checkQuantityInStock>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.productDetail.productDetailList, params],
        queryFn: () => checkQuantityInStock(params),
        ...options,
    });
};

export const useCheckQuantityInStockByProductDetail = (
    params: Ref<checkQuantityRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof checkQuantityInStockByIdProductDetail>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.productDetail.productDetailList, params],
        queryFn: () => checkQuantityInStockByIdProductDetail(params),
        ...options,
    });
};

// Check số lượng truyền vào 1 list nếu 1 cái k đủ thì trả true <> false
// export const useCheckQuantityListProduct = (
//     params: Ref<Array<checkQuantityRequest>>, options?: any
// ): UseQueryReturnType<Awaited<ReturnType<typeof checkQuantityListProduct>>, Error> => {
//     return useQuery({
//         queryKey: [queryKey.admin.productDetail.productDetailList, params],
//         queryFn: () => checkQuantityListProduct(params),
//         ...options,
//     });
// };
// export const useCheckQuantityListProduct = (options?: any) => {
//     return useMutation({
//       mutationFn: (params: Ref<Array<checkQuantityRequest>>) => checkQuantityListProduct(params),
//       onSuccess: (data) => {
//         // Xử lý data trả về thành công
//         console.log("Data từ API:", data);
//       },
//       onError: (error) => {
//         // Xử lý khi có lỗi
//         console.error("Có lỗi xảy ra:", error);
//       },
//       ...options, // Các option bạn muốn thêm vào
//     });
//   };

  export const useCheckQuantityListProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: Array<checkQuantityRequest>) => checkQuantityListProduct(data),
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

// Trừ số lượng sản phẩm trong kho, truyền vào 1 list
export const useDeleteQuantityListProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({params}: { params: Array<checkQuantityRequest> }) => deleteQuantityListProduct(params),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.productDetail.productDetailList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.productDetail.productDetailList + "🚀 ~ productDetailUpdate ~ error:", error);
        },
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