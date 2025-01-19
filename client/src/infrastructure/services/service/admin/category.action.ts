import {
    createCategory,
    getListCategory,
    CategoryRequest,
    getCategory,
    FindCategoryRequest,
    getCategorys,
    updateCategory
} from "@/infrastructure/services/api/admin/category.api.ts";
import {useMutation, useQuery, useQueryClient, UseQueryReturnType} from "@tanstack/vue-query";
import {queryKey} from "@/infrastructure/constants/queryKey.ts";
import {Ref} from "vue";


export const useGetCategorys = (
    params: Ref<FindCategoryRequest>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getCategorys>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.category.categoryList, params],
        queryFn: () => getCategorys(params),
        ...options,
    });
};

export const useGetListCategory = (
    options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getListCategory>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.category.categoryList],
        queryFn: () => getListCategory(),
        ...options,
    });
};

export const useCreateCategory = () => {
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


export const useGetCategory = (
    id: Ref<string | null>, options?: any
): UseQueryReturnType<Awaited<ReturnType<typeof getCategory>>, Error> => {
    return useQuery({
        queryKey: [queryKey.admin.category.categoryList, id,],
        queryFn: () => getCategory(id.value),
        ...options,
    });
};


export const useUpdateCategory = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, data,}: { id: string; data: CategoryRequest; }) => updateCategory(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [queryKey.admin.category.categoryList],});
        },
        onError: (error: any) => {
            console.log(queryKey.admin.category.categoryList + "🚀 ~ CategoryUpdate ~ error:", error);
        },
    });
};