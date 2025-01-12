import {
    createSize,
    getListSize,
    sizeRequest
} from "@/infrastructure/services/api/admin/size.api";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


// export const useGetCAtegory = (
//     params: Ref<FindProductRequest>, options?: any
// ): UseQueryReturnType<Awaited<ReturnType<typeof getProducts>>, Error> => {
//     return useQuery({
//         queryKey: [queryKey.admin.product.productList, params],
//         queryFn: () => getProducts(params),
//         ...options,
//     });
// };

export const useGetListSize = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListSize>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.size.sizeList],
        queryFn: () => getListSize(),
        ...options,
    });
};

export const useCreateSize = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: sizeRequest) => createSize(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.size.sizeList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.size.sizeList, "🚀 ~ sizeCreate ~ error:", error);
        },
    });
};


// export const useGetEmployee = (
//     employeeId: Ref<string | null>, options?: any
// ): UseQueryReturnType<Awaited<ReturnType<typeof getEmployee>>, Error> => {
//     return useQuery({
//         queryKey: [queryKey.admin.employee.employeeDetail, employeeId,],
//         queryFn: () => getEmployee(employeeId),
//         ...options,
//     });
// };


// export const useUpdateProduct = () => {
//     const queryClient = useQueryClient();
//     return useMutation({
//         mutationFn: ({id, data,}: { id: string; data: ProductRequest; }) => updateProduct(id, data),
//         onSuccess: () => {
//             queryClient.invalidateQueries({queryKey: [queryKey.admin.product.productList],});
//         },
//         onError: (error: any) => {
//             console.log(queryKey.admin.product.productList + "🚀 ~ productUpdate ~ error:", error);
//         },
//     });
// };

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