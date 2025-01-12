import {
    createProduct,
    FindProductRequest,
    getProducts,
    ProductRequest,
    updateProduct,
    getListProducts,
    getProduct
} from "@/infrastructure/services/api/admin/product.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetProduct = (
    params: Ref<FindProductRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProducts>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.product.productList, params],
        queryFn: () => getProducts(params),
        ...options,
    });
};

export const useGetListProduct = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListProducts>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.product.productList],
        queryFn: () => getListProducts(),
        ...options,
    });
};

export const useCreateProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: ProductRequest) => createProduct(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.product.productList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.product.productList, "🚀 ~ productCreate ~ error:", error);
        },
    });
};


export const useGetProductById = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getProduct>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.product.productList, id,],
        queryFn: () => getProduct(id.value),
        ...options,
    });
};


export const useUpdateProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, params,}: { id: string; params: ProductRequest; }) => updateProduct(id, params),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.product.productList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.product.productList + "🚀 ~ productUpdate ~ error:", error);
        },
    });
};

// export const useChangeStatusEmployee = () => {
//     const queryClient = useQueryClient();
//     return useMutation({
//         mutationFn: (employeeId: string) => changeStatusEmployee(employeeId),
//         onSuccess: () => {
//             queryClient.invalidateQueries({queryKey: [queryKey.admin.employee.employeeList],});
//         },
//         onError: (error: any) => {
//             console.log(queryKey.admin.employee.employeeList + "🚀 ~ employeeDelete ~ error:", error);
//         },
//     });
// };