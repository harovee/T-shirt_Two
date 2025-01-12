import {
    createMaterial,
    getListMaterial,
    materialRequest
} from "@/infrastructure/services/api/admin/material.api";
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

export const useGetListMaterial = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListMaterial>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.material.materialList],
        queryFn: () => getListMaterial(),
        ...options,
    });
};

export const useCreateMaterial = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (data: materialRequest) => createMaterial(data),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: [queryKey.admin.material.materialList],
            })
        },
        onError: (error: any) => {
            console.log(queryKey.admin.material.materialList, "🚀 ~ materialCreate ~ error:", error);
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