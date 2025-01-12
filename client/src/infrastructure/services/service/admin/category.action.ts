import {
    createCategory,
    getListCategory,
    CategoryRequest
} from "@/infrastructure/services/api/admin/category.api.ts";
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

export const useGetListCategory = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListCategory>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.category.categoryList],
        queryFn: () => getListCategory(),
        ...options,
    });
};

export const useCreateProduct = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: CategoryRequest) => createCategory(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.category.categoryList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.category.categoryList, "🚀 ~ categoryCreate ~ error:", error);
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