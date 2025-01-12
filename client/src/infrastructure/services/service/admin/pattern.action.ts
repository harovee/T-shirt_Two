import {
    createPattern,
    getListPattern,
    patternRequest
} from "@/infrastructure/services/api/admin/pattern.api";
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

export const useGetListPattern = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListPattern>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.pattern.patternList],
        queryFn: () => getListPattern(),
        ...options,
    });
};

export const useCreatePattern = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: patternRequest) => createPattern(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.pattern.patternList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.pattern.patternList, "🚀 ~ patternCreate ~ error:", error);
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